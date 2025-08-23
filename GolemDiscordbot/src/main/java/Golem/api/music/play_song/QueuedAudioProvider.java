package Golem.api.music.play_song;

import discord4j.voice.AudioProvider;
import io.github.jaredmdobson.concentus.OpusApplication;
import io.github.jaredmdobson.concentus.OpusEncoder;
import io.github.jaredmdobson.concentus.OpusException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import org.springframework.context.annotation.Configuration;

/**
 * Fournisseur audio pour Discord4J basé sur un système de queue de flux PCM.
 *
 * <p>Les pistes sont envoyées sous forme de {@link InputStream} PCM (16 bits, 48kHz, stéréo), qui
 * sont encodées en Opus avant d'être transmises à Discord.
 *
 * <p>Si la queue est vide, un frame de silence est envoyé (RFC 7845).
 */
@Configuration
public class QueuedAudioProvider extends AudioProvider {

  private final BlockingQueue<InputStream> queue = new LinkedBlockingQueue<>();
  private InputStream currentStream;

  private final OpusEncoder encoder;
  private final byte[] pcmBuffer = new byte[960 * 2 * 2];
  private final byte[] opusBuffer = new byte[4096];
  private static final byte[] SILENCE_FRAME = new byte[] {(byte) 0xF8, (byte) 0xFF, (byte) 0xFE};

  private volatile boolean connected = false;

  /**
   * Constructeur du fournisseur audio. Initialise l’encodeur Opus en mode {@link
   * OpusApplication#OPUS_APPLICATION_AUDIO}.
   */
  public QueuedAudioProvider() {
    super(ByteBuffer.allocate(DEFAULT_BUFFER_SIZE));
    try {
      encoder = new OpusEncoder(48000, 2, OpusApplication.OPUS_APPLICATION_AUDIO);
      encoder.setBitrate(64000);
    } catch (Exception e) {
      throw new RuntimeException("Impossible d'initialiser OpusEncoder", e);
    }
  }

  public boolean isConnected() {
    return this.connected;
  }

  public void setConnected(boolean connected) {
    this.connected = connected;
  }

  public void queueTrack(InputStream pcmStream) {
    queue.add(pcmStream);
  }

  /**
   * Fournit le prochain frame Opus à envoyer à Discord.
   *
   * <p>- Si aucun flux n’est en cours, essaie d’en récupérer un dans la queue. - Si la queue est
   * vide, envoie un frame de silence. - Si un flux est actif, encode le prochain paquet PCM en
   * Opus. - Si le flux est terminé, passe au suivant.
   *
   * @return true si un buffer valide a été préparé, false sinon
   */
  @Override
  public boolean provide() {
    try {
      if (currentStream == null) {
        currentStream = queue.poll();
        if (currentStream == null) {
          // Rien à jouer => silence
          getBuffer().clear();
          getBuffer().put(SILENCE_FRAME);
          getBuffer().flip();
          return true;
        }
      }

      int bytesRead = currentStream.read(pcmBuffer);
      if (bytesRead == -1) {
        currentStream.close();
        currentStream = null;
        return provide(); // Passe au suivant
      }

      short[] pcmShorts = new short[bytesRead / 2];
      for (int i = 0; i < pcmShorts.length; i++) {
        int low = pcmBuffer[i * 2] & 0xFF;
        int high = pcmBuffer[i * 2 + 1];
        pcmShorts[i] = (short) ((high << 8) | low);
      }

      int encoded =
          encoder.encode(pcmShorts, 0, pcmShorts.length / 2, opusBuffer, 0, opusBuffer.length);

      getBuffer().clear();
      getBuffer().put(opusBuffer, 0, encoded);
      getBuffer().flip();

      return true;

    } catch (IOException | OpusException e) {
      e.printStackTrace();
      currentStream = null;
      return false;
    }
  }
}
