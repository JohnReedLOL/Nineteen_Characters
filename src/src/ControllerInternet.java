package src;

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

import src.model.Map;
import src.model.MapInternet;

/**
 * Used for sending and receiving data from a Controller [not part of Iteration
 * 2]
 *
 * @author John-Michael Reed
 */
public final class ControllerInternet {

    private static InetAddress address = null;

    private static DatagramSocket udp_socket_for_incoming_signals = null;
    private static Socket tcp_socket_for_incoming_signals = null;
    private static ObjectInputStream object_input_stream = null;
    private static final Random rand = new Random();
    private static final String unique_id_string = ControllerInternet.getMacAddress();
    private static boolean isConnected = false;
    //private final String monitor_For_UDP_Sender = "";
    private final UDP_Sender_Thread sender_thread;
    private int recieved_buffer_size = 30000;

    public ControllerInternet() {
        sender_thread = new UDP_Sender_Thread();
        sender_thread.start();
        try {
            udp_socket_for_incoming_signals = new DatagramSocket(MapInternet.UDP_PORT_NUMBER_FOR_MAP_SENDING_AND_CLIENT_RECIEVING);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private class UDP_Sender_Thread extends Thread {

        private DatagramSocket udp_socket_for_outgoing_signals;
        private DatagramPacket packet_to_send = null;

        public synchronized void setPacketAndNotify(DatagramPacket s) {
            packet_to_send = s;
            notify();
        }

        public UDP_Sender_Thread() {
            try {
                udp_socket_for_outgoing_signals = new DatagramSocket();
                udp_socket_for_outgoing_signals.setReuseAddress(true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        public synchronized void run() {
            while (true) {
                try {
                    this.wait();
                    udp_socket_for_outgoing_signals.send(packet_to_send);
                } catch (InterruptedException i) {
                    i.printStackTrace();
                    return; // safely kill the thread
                } catch (IOException io) {
                    io.printStackTrace();
                }
            }
        }
    }

    public void closeAndNullifyConnection() {
        try {
            if (tcp_socket_for_incoming_signals != null) {
                if (tcp_socket_for_incoming_signals.isConnected()) {
                    tcp_socket_for_incoming_signals.close();
                }
                tcp_socket_for_incoming_signals = null;
            }
            sender_thread.interrupt(); // make the udp_sender thread commit suicide.
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
    public IO_Bundle sendStuffToMap(String avatar_name, Enum key_command, int width, int height, String optional_text) {
        if (!isConnected) {
            final int error_code = makeConnectionUsingIP_Address("localhost");
        }
        try {
            final String to_send = unique_id_string + " " + avatar_name + " "
                    + key_command.name() + " " + width + " " + height + " " + optional_text;
            final byte[] buf = to_send.getBytes();
            final DatagramPacket packet = new DatagramPacket(buf, buf.length, ControllerInternet.address, MapInternet.UDP_PORT_NUMBER_FOR_MAP_RECIEVING_AND_CLIENT_SENDING);
            if (tcp_socket_for_incoming_signals != null
                    && object_input_stream != null && ControllerInternet.udp_socket_for_incoming_signals != null) {
                sender_thread.setPacketAndNotify(packet);
            } else {
                System.out.println("UDP or TCP or input stream is null in " + "Internet.sendStuffToMap(" + avatar_name + ", " + key_command.name() + ",...)");
                System.out.println("Impossible error in " + "Internet.sendStuffToMap(" + avatar_name + ", " + key_command.name() + ",...)");
                System.exit(-23);
            }

            /*if (RunGame.getUseTCP()) {
             //System.out.println("Calling Internet.sendStuffToMap over TCP");
             // recieve IO_Bundle from map over TCP connection
             Object temp = object_input_stream.readObject();
             IO_Bundle to_recieve = null;
             if (temp != null) {
             to_recieve = (IO_Bundle) temp;
             // Decompression the IO_Bundle if characters are compressed.
             if (to_recieve.view_for_display_ == null && to_recieve.compressed_characters_ != null) {
             to_recieve.view_for_display_ = IO_Bundle.runLengthDecodeView(width, height,
             to_recieve.compressed_characters_, to_recieve.character_frequencies_);
             to_recieve.color_for_display_ = IO_Bundle.runLengthDecodeColor(width, height,
             to_recieve.compressed_colors_, to_recieve.color_frequencies_);
             }
             }
             return to_recieve;
             } else {*/
            // recieve IO_Bundle from map over UDP connection
            IO_Bundle to_recieve = getBundleFromBufferOfSize(40000);
            // Decompression the IO_Bundle if characters are compressed.
            if (to_recieve.view_for_display_ == null && to_recieve.compressed_characters_ != null) {
                to_recieve.view_for_display_ = IO_Bundle.runLengthDecodeView(width, height,
                        to_recieve.compressed_characters_, to_recieve.character_frequencies_);
                to_recieve.color_for_display_ = IO_Bundle.runLengthDecodeColor(width, height,
                        to_recieve.compressed_colors_, to_recieve.color_frequencies_);
            }
            return to_recieve;
            //}

        } catch (Exception e) {
            System.err.println("Exception in " + "Internet.sendStuffToMap(" + avatar_name + ", " + key_command.name() + ",...)" + " named: " + e.toString());
            e.printStackTrace();
            return null;
        }
    }

    private IO_Bundle getBundleFromBufferOfSize(int buffer_size) {
        boolean is_too_small = true;
        IO_Bundle to_return = null;

        while (is_too_small) {
            byte[] recieved = new byte[buffer_size];
            DatagramPacket recvPacket = new DatagramPacket(recieved, recieved.length);
            try {
                udp_socket_for_incoming_signals.receive(recvPacket);
            } catch (IOException ioe) {
                ioe.printStackTrace();
            }
            try {
                to_return = ControllerInternet.bytesToBundle(recieved);
                is_too_small = false;
            } catch (IOException eof) {
                System.err.println("The map is too big to fit in the internet buffer.");
                // if the buffer is too small.
                buffer_size = buffer_size * 2;
            }
        }
        return to_return;
    }

    /**
     * Allows the controller to connects itself to an internet connection and
     * use ControllerInternet.sendStuffToMap(String, Enum, int, int, "")
     *
     * @param ip_address - use "localhost" to connect to local machine, ex.
     * "192.***.***.***".
     * @return 0 if connection successful, -1 if connection not successful
     */
    public int makeConnectionUsingIP_Address(String ip_address) {
        ip_address = ip_address.trim().toLowerCase();
        System.out.println("Starting Internet.makeConnectionUsingIP_Address(" + ip_address + ")");
        if (!ip_address.equals("localhost") && !ip_address.matches(".*[0-9].*")) {
            RunGame.setUseInternet(false);
            isConnected = false;
            //System.out.println("Not using internet in Internet.makeConnectionUsingIP_Address(String ip_address)");
            return 0;
        } else {
            RunGame.setUseInternet(true);
            //System.out.println("Using internet in Internet.makeConnectionUsingIP_Address(String ip_address)");
        }
        ObjectOutputStream oos = null;
        try {
            if (object_input_stream != null) {
                object_input_stream.close();
                object_input_stream = null;
            }
            ControllerInternet.address = InetAddress.getByName(ip_address);
            tcp_socket_for_incoming_signals = new Socket();
            tcp_socket_for_incoming_signals.setTcpNoDelay(true); // no latency
            tcp_socket_for_incoming_signals.setReuseAddress(true); // allow client to reconnect
            tcp_socket_for_incoming_signals.connect(new InetSocketAddress(ip_address, MapInternet.TCP_PORT_NUMBER));
            tcp_socket_for_incoming_signals.setTcpNoDelay(true);
            tcp_socket_for_incoming_signals.setReuseAddress(true);
            oos = new ObjectOutputStream(tcp_socket_for_incoming_signals.getOutputStream());
            oos.flush();
            oos.writeObject(unique_id_string);
            System.out.println("You MAC address / unique identifier in Internet.makeConnection is: " + unique_id_string);
            oos.flush();
            oos = null;
            object_input_stream = new ObjectInputStream(tcp_socket_for_incoming_signals.getInputStream());
            //last_ip_connected = ip_address;
            isConnected = true;
            return 0;
        } catch (Exception e) {
            RunGame.setUseInternet(false);
            System.err.println("Exception in Internet.makeConnectionUsingIP_Address(" + ip_address + "). Not using internet.");
            e.printStackTrace();
            isConnected = false;
            return -1;
        } finally {
            try {
                if (oos != null) {
                    oos.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
                System.err.println("Exception in closing ObjectOutputStream in makeConnectionUsingIP_Address(String ip_address)");
            }
        }
    }

    public static byte[] bundleToBytes(IO_Bundle io_bundle) {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream(2048);
            ObjectOutputStream oos = new ObjectOutputStream(baos);
            oos.flush();
            oos.writeObject(io_bundle);
            oos.flush();
            oos.close();
            // get the byte array of the object
            byte[] obj = baos.toByteArray();
            baos.flush();
            baos.close();
            return obj;
        } catch (Exception e) {
            System.err.println("Exception in Internet.bundleToBytes(IO_Bundle io_bundle) named: " + e.toString());
            e.printStackTrace();
            System.exit(-78);
            return null;
        }
    }

    /**
     * THROWS AN IO EXCEPTION [EOF EXCEPTION] IF THE BYTE ARRAY SO TOO SMALL TO
     * FIT THE OBJECT
     *
     * @param data
     * @return
     * @throws IOException
     */
    public static IO_Bundle bytesToBundle(byte[] data) throws IOException {
        ByteArrayInputStream bais = new ByteArrayInputStream(data);
        ObjectInputStream ois = new ObjectInputStream(bais);
        IO_Bundle to_return = null;
        try {
            Object object = ois.readObject();
            to_return = (IO_Bundle) object;
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
