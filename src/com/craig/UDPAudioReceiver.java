package com.craig;

import javax.sound.sampled.*;
import java.net.*;

public class UDPAudioReceiver extends Thread {

    private static final String IP_TO_STREAM_TO   = "localhost" ;
    private static final int PORT_TO_STREAM_TO     = 8888 ;
    private static DatagramSocket sock;
    private static final byte[] soundpacket = new byte[1000];
    private static DatagramPacket datagram;
    private static DataLine.Info dataLineInfo;
    private static SourceDataLine sourceDataLine;

    /** Creates a new instance of UDPAudioReceiver */
    public UDPAudioReceiver() {
    }

    public void run()
    {
        try {
            while( true )
            {
                toSpeaker(receiveThruUDP()) ;
            }
        } finally {
            sock.close();
        }
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {

        dataLineInfo = new DataLine.Info( SourceDataLine.class , getAudioFormat() );
        try {
            datagram = getDatagramPacket();
        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
        }
        try {
            sock = new DatagramSocket(PORT_TO_STREAM_TO);
        } catch (SocketException e) {
            throw new RuntimeException(e);
        }
        try {
            sourceDataLine = (SourceDataLine) AudioSystem.getLine(dataLineInfo);
            sourceDataLine.open( getAudioFormat() ) ;
        } catch (LineUnavailableException e) {
            throw new RuntimeException(e);
        }
        sourceDataLine.start();
        UDPAudioReceiver r = new UDPAudioReceiver() ;
        r.start() ;
        try {
            AudioSystem.getLine(dataLineInfo).close() ;
        } catch (LineUnavailableException e) {
            throw new RuntimeException(e);
        }

    }


    public static byte[] receiveThruUDP()
    {
        try
        {
            sock.receive(datagram) ;
            return datagram.getData() ; // soundpacket ;
        }
        catch( Exception e )
        {
            System.out.println(" Unable to send soundpacket using UDP " ) ;
            return null ;
        }

    }

    private static DatagramPacket getDatagramPacket() throws UnknownHostException {
        return new DatagramPacket(soundpacket, soundpacket.length , InetAddress.getByName( IP_TO_STREAM_TO ) , PORT_TO_STREAM_TO );
    }


    public static void toSpeaker( byte soundbytes[] )
    {

        try{
            int cnt = 0;
            sourceDataLine.write( soundbytes , 0, soundbytes.length );
            sourceDataLine.drain() ;
        }
        catch(Exception e )
        {
            System.out.println("not working in speakers " ) ;
        }

    }


    public static AudioFormat getAudioFormat()
    {
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
        return new AudioFormat( sampleRate, sampleSizeInBits, channels, signed, bigEndian );
    }


}