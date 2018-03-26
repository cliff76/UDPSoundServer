package com.craig;

import javax.sound.sampled.*;
import java.net.*;

public class MicPlayer {


    private static final String IP_TO_STREAM_TO = "3.39.48.168";
    private static final int PORT_TO_STREAM_TO = 8888;
    private static DatagramSocket sock;
    private static final byte[] tempBuffer = new byte[1000];
    private static DatagramPacket packet;

    /**
     * Creates a new instance of MicPlayer
     */
    public MicPlayer() {

    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        try {
            sock = new DatagramSocket();
        } catch (SocketException e) {
            e.printStackTrace();
        }
        Mixer.Info minfo[] = AudioSystem.getMixerInfo();
        for (int i = 0; i < minfo.length; i++) {
            System.out.println(minfo[i]);
        }


        if (AudioSystem.isLineSupported(Port.Info.MICROPHONE)) {
            try {


                DataLine.Info dataLineInfo = new DataLine.Info(TargetDataLine.class, getAudioFormat());
                TargetDataLine targetDataLine = (TargetDataLine) AudioSystem.getLine(dataLineInfo);
                targetDataLine.open(getAudioFormat());
                targetDataLine.start();
                int cnt = 0;
                packet = getPacket(tempBuffer);
                while (true) {
                    targetDataLine.read(tempBuffer, 0, tempBuffer.length);
                    sendThruUDP(tempBuffer);
                }

            } catch (Exception e) {
                System.out.println(" not correct ");
                System.exit(0);
            }
        }

        sock.close();
    }

    public static AudioFormat getAudioFormat() {
        float sampleRate = 8000.0F;
        //8000,11025,16000,22050,44100
        int sampleSizeInBits = 16;
        //8,16
        int channels = 1;
        //1,2
        boolean signed = true;
        //true,false
        boolean bigEndian = false;
        //true,false
        return new AudioFormat(sampleRate, sampleSizeInBits, channels, signed, bigEndian);
    }

    public static void sendThruUDP(byte soundpacket[]) {
        try {
            packet.setData(soundpacket);
            packet.setLength(soundpacket.length);
            sock.send(packet);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println(" Unable to send soundpacket using UDP ");
        }

    }

    private static DatagramPacket getPacket(byte[] soundpacket) throws UnknownHostException {
        return new DatagramPacket(soundpacket, soundpacket.length, InetAddress.getByName(IP_TO_STREAM_TO), PORT_TO_STREAM_TO);
    }
}

