package Golem.api.utils;

import discord4j.voice.AudioProvider;
import io.github.jaredmdobson.concentus.OpusApplication;
import io.github.jaredmdobson.concentus.OpusEncoder;
import io.github.jaredmdobson.concentus.OpusException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

public class FFmpegAudioProvider extends AudioProvider {

  private final InputStream pcmInputStream;
  private static final byte[] SILENCE_FRAME = new byte[] {(byte) 0xF8, (byte) 0xFF, (byte) 0xFE};

  private final OpusEncoder encoder;
  private final byte[] pcmBuffer =
      new byte[960 * 2 * 2]; // 20ms stereo s16le => 1920 samples * 2 bytes/sample * 2 channels
  private final byte[] opusBuffer = new byte[4096];

  public FFmpegAudioProvider(InputStream pcmInputStream) {
    super(ByteBuffer.allocate(DEFAULT_BUFFER_SIZE));
    this.pcmInputStream = pcmInputStream;
    try {
      encoder = new OpusEncoder(48000, 2, OpusApplication.OPUS_APPLICATION_AUDIO);

      encoder.setBitrate(64000);
    } catch (Exception e) {
      throw new RuntimeException("Impossible d'initialiser OpusEncoder", e);
    }
  }

  @Override
  public boolean provide() {
    try {
      int bytesRead = pcmInputStream.read(pcmBuffer);

      if (bytesRead == -1) {
        // Fin du flux ? -> envoyer un paquet Opus "silence"
        getBuffer().clear();
        getBuffer().put(SILENCE_FRAME);
        getBuffer().flip();
        return true; // continuer à fournir pour garder le websocket actif
      }

      // Convertir PCM en short[]
      short[] pcmShorts = new short[bytesRead / 2];
      for (int i = 0; i < pcmShorts.length; i++) {
        int low = pcmBuffer[i * 2] & 0xFF;
        int high = pcmBuffer[i * 2 + 1];
        pcmShorts[i] = (short) ((high << 8) | low);
      }

      int encoded;
      try {
        encoded =
            encoder.encode(pcmShorts, 0, pcmShorts.length / 2, opusBuffer, 0, opusBuffer.length);
      } catch (OpusException e) {
        e.printStackTrace();
        return false; // gros problème → stop
      }

      getBuffer().clear();
      getBuffer().put(opusBuffer, 0, encoded);
      getBuffer().flip();

      return true;

    } catch (IOException e) {
      e.printStackTrace();
      return false; // gros problème IO → stop
    }
  }
}
