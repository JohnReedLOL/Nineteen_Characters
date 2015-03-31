package src.io.controller;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.NetworkInterface;
import java.net.Socket;
import java.util.Enumeration;
import java.util.Random;
import src.IO_Bundle;
import src.io.controller.Controller;
import src.io.controller.GameController;

import src.model.Map;
import src.model.MapInternet;
import src.RunGame;
import src.model.MapMessage;

/**
 * Used for sending and receiving data from a Controller [not part of Iteration
 * 2]
 *
 * @author John-Michael Reed
 */
public final class ControllerInternet_NEW {

    private static InetAddress address = null;

    private static DatagramSocket udp_socket_for_outgoing_signals = null;
    private static final Random rand = new Random();
    private static final String unique_id_string = getMacAddress();
    //private final String monitor_For_UDP_Sender = "";
    private final UDP_Receiver_Thread receiver_thread;
    private final Controller who_I_am_providing_internet_to_;
    private boolean is_internet_connected = false;
    // keeps returning the current value of this IO_Bundle
    private volatile IO_Bundle last_thing_received = null;

    public ControllerInternet_NEW(Controller who_I_am_providing_internet_to) {
        receiver_thread = new UDP_Receiver_Thread();
        receiver_thread.start();
        try {
            udp_socket_for_outgoing_signals = new DatagramSocket();
        } catch (Exception e) {
            e.printStackTrace();
        }
        who_I_am_providing_internet_to_ = who_I_am_providing_internet_to;
    }

    private class UDP_Receiver_Thread extends Thread {

        private DatagramSocket udp_socket_for_incoming_signals;
        private DatagramPacket packet_to_receive = null;
        private volatile boolean is_notified = false;

        public UDP_Receiver_Thread() {
            try {
                udp_socket_for_incoming_signals = new DatagramSocket(MapInternet.UDP_PORT_NUMBER_FOR_MAP_SENDING_AND_CLIENT_RECIEVING);
                udp_socket_for_incoming_signals.setReuseAddress(true);
                //udp_socket_for_incoming_signals.s
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        public synchronized void run() {
            while (!Thread.currentThread().isInterrupted()) {
                // recieve IO_Bundle from map over UDP connection
                try {
                    IO_Bundle to_set = getBundleFromBuffer();
                    last_thing_received = to_set;
                } catch (IOException e) {
                    
                }
            }

            System.out.println("Controller Internet receiving thread was interupted");
        }

        private int current_buffer_size_ = 100;

        /**
         * Kills the program if the buffer is not big enough [testing]
         *
         * @param buffer_size
         * @throws IOEXception if UDP buffer is too small
         * @return
         */
        private IO_Bundle getBundleFromBuffer() throws IOException {
            boolean is_too_small = true;
            IO_Bundle to_return = null;

            while (is_too_small) {
                byte[] recieved = new byte[current_buffer_size_];
                DatagramPacket recvPacket = new DatagramPacket(recieved, recieved.length);
                try {
                    udp_socket_for_incoming_signals.receive(recvPacket);
                    int received_bytes = recvPacket.getLength();
                    //System.out.println("Received length is " + received_bytes + RunGame.getLineNumber());
                } catch (IOException ioe) {
                    System.out.println("Failed to receieve data in getBundleFromBufferOfSize");
                    ioe.printStackTrace();
                    RunGame.closeGame();
                    System.exit(-4);
                }
                try {
                    to_return = bytesToObject(recieved);
                    is_too_small = false;
                } catch (IOException eof) {
                    current_buffer_size_ *= 2;
                    System.out.println("Internet buffer size increased to " + current_buffer_size_ + 
                            "." + RunGame.getLineNumber());
                    throw eof;
                    // if the buffer is too small.
                    //buffer_size = current_buffer_size_ * 2;
                    //RunGame.closeGame();
                    //System.exit(-4);
                }
            }
            return to_return;
        }
    }

    public void terminate() {
        try {
            receiver_thread.interrupt(); // make the udp_sender thread commit suicide.
            is_internet_connected = false;
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Error while closing and nullifying connection");
        }
    }

    /**
     * Use this function to send commands to the Map over TCP/UDP sockets
     *
     * @author John-Michael Reed
     * @param avatar_name - name of the avatar to control
     * @param key_command - command that the map will execute
     * @param width - width from center of map to rightmost or leftmost edge.
     * @param height - height from center of map to top or bottom edge.
     * @param optional_text - optional string parameter - must be either empty
     * string or non-empty.
     * @return IO_Bundle object containing all the data needed by the controller
     * to render the view.
     */
    public IO_Bundle sendStuffToMap(String avatar_name, src.Key_Commands key_command, int width, int height, String optional_text) {
        if (!who_I_am_providing_internet_to_.isUsingInternet()) {
            System.err.println("Impossible exception - Controller is using internet and not using internet");
            System.exit(-87);
        }
        if (!is_internet_connected) {
            final int error_code = makeConnectionUsingIP_Address("localhost");
            if (error_code == 0) {
            } else {
                who_I_am_providing_internet_to_.tellNotToUseNetwork();
                System.err.println("An impossible error occured in Redone_Controller_Internet.sendStuffToMap(). Could not connect to localhost");
                System.exit(-43);
                return null;
            }
        }
        try {
            /*final String to_send = unique_id_string + " " + avatar_name + " "
                    + key_command.name() + " " + width + " " + height + " " + optional_text;*/
            final MapMessage to_send = new MapMessage(unique_id_string, avatar_name, 
                    key_command, width, height, optional_text);
            final byte[] buf = ControllerInternet_NEW.objectToBytes(to_send);
            final DatagramPacket packet_to_send = new DatagramPacket(buf, buf.length, address, MapInternet.UDP_PORT_NUMBER_FOR_MAP_RECIEVING_AND_CLIENT_SENDING);
            if (udp_socket_for_outgoing_signals != null) {
                udp_socket_for_outgoing_signals.send(packet_to_send);
            } else {
                System.out.println("UDP or TCP or input stream is null in " + "Internet.sendStuffToMap(" + avatar_name + ", " + key_command.name() + ",...)");
                System.out.println("Impossible error in " + "Internet.sendStuffToMap(" + avatar_name + ", " + key_command.name() + ",...)");
                System.exit(-23);
            }
            // might be one frame behind
            return last_thing_received;

        } catch (Exception e) {
            System.err.println("Exception in " + "Internet.sendStuffToMap(" + avatar_name + ", " + key_command.name() + ",...)" + " named: " + e.toString());
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Allows the controller to connects itself to an internet connection and
     * use ControllerInternet_OLD.sendStuffToMap(String, Enum, int, int, "")
     *
     * @param ip_address - use "localhost" to connect to local machine, ex.
     * "192.***.***.***".
     * @return 0 if connection successful, -1 if connection not successful
     */
    public int makeConnectionUsingIP_Address(String ip_address) {
        try {
            address = InetAddress.getByName(ip_address);
        } catch (IOException e) {
            //e.printStackTrace();
            is_internet_connected = false;
            return -1;
        }
        is_internet_connected = true;
        return 0;
    }

    public static byte[] objectToBytes(Object object) {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream(2048);
            ObjectOutputStream oos = new ObjectOutputStream(baos);
            oos.flush();
            oos.writeObject(object);
            oos.flush();
            oos.close();
            // get the byte array of the object
            byte[] obj = baos.toByteArray();
            baos.flush();
            baos.close();
            return obj;
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(-78);
            return null;
        }
    }

    /**
     * THROWS AN IO EXCEPTION [EOF EXCEPTION] IF THE BYTE ARRAY IS TOO SMALL TO
     * FIT THE OBJECT
     *
     * @param data
     * @return
     * @throws IOException
     */
    public static <Type> Type bytesToObject(byte[] data) throws IOException {
        ByteArrayInputStream bais = new ByteArrayInputStream(data);
        ObjectInputStream ois = new ObjectInputStream(bais);
        Type to_return = null;
        try {
            Object object = ois.readObject();
            to_return = (Type) object;
        } catch (ClassNotFoundException cnfe) {
            System.err.println("Impossible error");
            cnfe.printStackTrace();
            System.exit(-76);
        }
        ois.close();
        return to_return;
    }

    private static String getMacAddress() {
        try {
            //Get MAC address
            String MAC_Address = "";
            InetAddress ip = InetAddress.getLocalHost();

            Enumeration e = NetworkInterface.getNetworkInterfaces();

            while (e.hasMoreElements()) {

                NetworkInterface n = (NetworkInterface) e.nextElement();
                Enumeration<InetAddress> ee = n.getInetAddresses();
                while (ee.hasMoreElements()) {
                    InetAddress i = (InetAddress) ee.nextElement();
                    if (!i.isLoopbackAddress() && !i.isLinkLocalAddress() && i.isSiteLocalAddress()) {
                        ip = i;
                    }
                }
            }

            NetworkInterface network = NetworkInterface.getByInetAddress(ip);
            byte[] mac_byte = network.getHardwareAddress();

            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < mac_byte.length; i++) {
                sb.append(String.format("%02X%s", mac_byte[i], (i < mac_byte.length - 1) ? "-" : ""));
            }
            return sb.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return Integer.toString(rand.nextInt(), 10);
        }
    }
}
