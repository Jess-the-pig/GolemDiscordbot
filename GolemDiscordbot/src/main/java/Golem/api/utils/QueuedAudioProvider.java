package Golem.api.utils;

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

@Configuration
public class QueuedAudioProvider extends AudioProvider {

  private final BlockingQueue<InputStream> queue = new LinkedBlockingQueue<>();
  private InputStream currentStream;

  private final OpusEncoder encoder;
  private final byte[] pcmBuffer = new byte[960 * 2 * 2];
  private final byte[] opusBuffer = new byte[4096];
  private static final byte[] SILENCE_FRAME = new byte[] {(byte) 0xF8, (byte) 0xFF, (byte) 0xFE};

  // ✅ Nouveau champ pour l'état de connexion
  private volatile boolean connected = false;

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
