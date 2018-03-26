package com.craig;

import javax.sound.sampled.*;
import java.io.IOException;
import java.net.*;

public class MicPlayer {


    private static final String IP_TO_STREAM_TO = "127.0.0.1";
//    private static final String IP_TO_STREAM_TO = "3.39.48.168";
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
            DataLine.Info dataLineInfo = new DataLine.Info(TargetDataLine.class, DefaultFormat.getAudioFormat());
            TargetDataLine targetDataLine = null;
            try {
                targetDataLine = (TargetDataLine) AudioSystem.getLine(dataLineInfo);
                targetDataLine.open(DefaultFormat.getAudioFormat());
            }
            catch (LineUnavailableException e) { handleCriticalError(e); }
            targetDataLine.start();
            try { packet = getPacket(tempBuffer); }
            catch (UnknownHostException e)  { handleCriticalError(e); }
            while (true) {
                targetDataLine.read(tempBuffer, 0, tempBuffer.length);
                if(! sendThruUDP(tempBuffer)) {
                    System.err.println("Could not send packet via UDP.");
                    break;
                }
            }
        }

        sock.close();
    }

    private static void handleCriticalError(Throwable e) {
        System.err.println("Critical error " + e.getMessage());
        e.printStackTrace();
        System.exit(1);
    }

    public static boolean sendThruUDP(byte[] soundpacket) {
        packet.setData(soundpacket);
        packet.setLength(soundpacket.length);
        try { sock.send(packet); }
        catch (IOException e) {
            System.err.println("IOException: " + e);
            e.printStackTrace();
            return false;
        }
        return true;
    }

    private static DatagramPacket getPacket(byte[] soundpacket) throws UnknownHostException {
        return new DatagramPacket(soundpacket, soundpacket.length, InetAddress.getByName(IP_TO_STREAM_TO), PORT_TO_STREAM_TO);
    }
}

