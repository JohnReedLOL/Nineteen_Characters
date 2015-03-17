package src;

import java.io.ObjectInputStream;
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

/**
 * This class is used to connect to and send stuff over the Internet.
 * @author JohnReedLOL
 */
public final class Internet {

    private static DatagramSocket udp_socket_for_outgoing_signals = null;
    private static InetAddress address = null;
    private static Socket tcp_socket_for_incoming_signals = null;
    private static final Random rand = new Random();
    private static final int unique_id = rand.nextInt();
    private static final String unique_id_string = Integer.toString(unique_id, 10);
    private static ObjectInputStream object_input_stream = null;

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
    public static IO_Bundle sendStuffToTheMap(String avatar_name, Enum key_command, int width, int height, String optional_text) {
        try {
            final String to_send = unique_id_string + " " + avatar_name + " "
                    + key_command.name() + " " + width + " " + height + " " + optional_text;
            final byte[] buf = to_send.getBytes();
            final DatagramPacket packet = new DatagramPacket(buf, buf.length, Internet.address, Map.UDP_PORT_NUMBER);
            // send command to map over UDP connection
            Internet.udp_socket_for_outgoing_signals.send(packet);
            // recieve IO_Bundle from map over UTCP connection
            IO_Bundle to_recieve = (IO_Bundle) object_input_stream.readObject();
            // Decompression the IO_Bundle if characters are compressed.
            if (to_recieve.view_for_display_ == null && to_recieve.compressed_characters_ != null) {
                to_recieve.view_for_display_ = IO_Bundle.runLengthDecodeView(width, height,
                        to_recieve.compressed_characters_, to_recieve.character_frequencies_);
                to_recieve.color_for_display_ = IO_Bundle.runLengthDecodeColor(width, height,
                        to_recieve.compressed_colors_, to_recieve.color_frequencies_);
            }
            return to_recieve;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Allows the controller to connects itself to an internet connection and
     * use Internet.sendStuffToTheMap(String, Enum, int, int, "")
     *
     * @param ip_address - use "localhost" to connect to local machine, ex.
     * "192.***.***.***".
     * @return 0 if connection successful, -1 if connection not successful
     */
    public static int makeConnectionUsingIP_Address(String ip_address) {
        try {
            if (udp_socket_for_outgoing_signals != null) {
                udp_socket_for_outgoing_signals.close();
                Internet.address = null;
            }
            udp_socket_for_outgoing_signals = new DatagramSocket();
            Internet.address = InetAddress.getByName(ip_address);
            if (tcp_socket_for_incoming_signals != null) {
                if (tcp_socket_for_incoming_signals.isConnected()) {
                    tcp_socket_for_incoming_signals.close();
                }
                tcp_socket_for_incoming_signals = null;
            }
            tcp_socket_for_incoming_signals = new Socket();
            tcp_socket_for_incoming_signals.setTcpNoDelay(true); // no latency
            tcp_socket_for_incoming_signals.setReuseAddress(true); // allow client to reconnect
            tcp_socket_for_incoming_signals.connect(new InetSocketAddress(ip_address, Map.TCP_PORT_NUMBER));
            tcp_socket_for_incoming_signals.setTcpNoDelay(true);
            ObjectOutputStream oos = new ObjectOutputStream(tcp_socket_for_incoming_signals.getOutputStream());
            oos.flush();
            oos.writeObject(Integer.toString(Internet.unique_id, 10));
            oos.flush();
            oos = null;
            object_input_stream = new ObjectInputStream(tcp_socket_for_incoming_signals.getInputStream());
            return 0;
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }

    private static String getMacAddress() throws Exception {

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
    }

}
