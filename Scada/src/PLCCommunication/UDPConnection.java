/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package PLCCommunication;

import java.io.IOException;
import java.io.Serializable;
import java.net.*;

/**
 * Connect and communicate with ABB PLC via UDP
 * PLC default address is: 192.168.0.100 og PC
 * PC must have a fixes address at same subnet ex. 192.168.0.100
 *
 * @author Steffen Skov
 */
public class UDPConnection extends PLCConnection implements IMessage, Serializable {
    private InetAddress adr = null;
    private int port;
    transient private DatagramPacket answerDP = null;
    //private Message mess;

    /**
     * Create UDP connection
     *
     * @param cmd  Command number - See ICommands
     * @param data Data to communicate
     * @param port Destination port
     * @param adr  Internet desination
     */
    public UDPConnection(byte cmd, byte[] data, int port, String adr) {
        mess = new Message(cmd, data);
        this.port = port;
        try {
            setInetAdr(adr);
        } catch (UnknownHostException e) {
            System.out.println("Unknown host address");
        }
        answerDP = new DatagramPacket(new byte[110], 110);


    }

    /**
     * Create UDP connection
     *
     * @param port Destination port
     * @param adr  Internet destination
     */
    public UDPConnection(int port, String adr) {
        mess = null;
        this.port = port;
        try {
            setInetAdr(adr);
        } catch (UnknownHostException e) {
            System.out.println("Unknown host address");
        }
        answerDP = new DatagramPacket(new byte[110], 110);


    }

    /**
     * Create connection
     * Destination port = 1025
     * Internet destination = "localhost"
     */
    public UDPConnection() {
        mess = null;
        this.port = 1025;
        try {
            setInetAdr("localhost");
        } catch (UnknownHostException e) {
            System.out.println("Unknown host address");
        }
        answerDP = new DatagramPacket(new byte[110], 110);
    }


    private void setInetAdr(String adr) throws UnknownHostException {
        this.setAdr(InetAddress.getByName(adr));

    }

    /**
     * Send the message and wait 5 sec for answer
     *
     * @return true if communition succeed
     */
    public boolean send() {
        byte[] p;
        DatagramSocket socket = null;
        try {
            p = mess.packMessage();
            DatagramPacket packet = new DatagramPacket(p, 0, p.length, adr, port);

            //socket = new DatagramSocket(port, adr);// to test with local host
            socket = new DatagramSocket();

            //System.out.println("Packet content:" + packet);
            socket.send(packet);
            socket.setSoTimeout(5000); //wait for answar max. 1 sec.
            socket.receive(answerDP);
            byte[] a = answerDP.getData();
            mess.answer = a;
            if (a[DIRECTION] == FROMPLC) // Dicard own message
            {
                System.out.println("Data received:" + answerDP.getLength() + " byte " + a[0] + "," + a[1] + "," + a[2] + "," + a[3] + "," + a[10] + ".....");
                if (!mess.answerIsValid()) {
                    mess.answer = answerDP.getData();
                    return false;
                }
                return true;
            }
            return false;
        } catch (UnknownHostException e) {
            System.out.println("Unknown host");
            return false;
        } catch (SocketException e) {
            System.out.println("Socket exception");
            System.out.println(e);
            return false;
        } catch (SocketTimeoutException e) {
            System.out.println(e);
            return false;
        } catch (IOException e) {
            System.out.println("IOException");
            return false;
        } catch (Exception e) {
            System.out.println(e);
            return false;
        } finally {
            if (socket != null)
                socket.close();
        }

    }

    /**
     * @return the port
     */
    public int getPort() {
        return port;
    }

    public String getAdr() {
        return adr.getHostAddress();
    }

    /**
     * @param port the port to set
     */
    public void setPort(int port) {
        this.port = port;
    }

    /**
     * @param adr the adr to set
     */
    private void setAdr(InetAddress adr) {
        this.adr = adr;
    }


}
