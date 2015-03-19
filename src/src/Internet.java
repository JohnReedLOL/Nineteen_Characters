package src;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
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

/**
 * This class is used to connect to and send stuff over the Internet.
 *
 * @author JohnReedLOL
 */
public final class Internet {

    private static InetAddress address = null;
    private static DatagramSocket udp_socket_for_outgoing_signals = null;
    private static DatagramSocket udp_socket_for_incoming_signals = null;
    private static Socket tcp_socket_for_incoming_signals = null;
    private static ObjectInputStream object_input_stream = null;
    private static final Random rand = new Random();
    private static final String unique_id_string = Internet.getMacAddress();
    //private static String last_ip_connected = null;
    private static boolean isConnected = false;
    private static boolean is_using_TCP = RunGame.use_TCP;

    public static void closeAndNullifyConnection() {
        try {
            if (udp_socket_for_outgoing_signals != null) {
                udp_socket_for_outgoing_signals.close();
                udp_socket_for_outgoing_signals = null;
                Internet.address = null;
            }
            udp_socket_for_outgoing_signals = new DatagramSocket();
            udp_socket_for_outgoing_signals.setReuseAddress(true);
            if (tcp_socket_for_incoming_signals != null) {
                if (tcp_socket_for_incoming_signals.isConnected()) {
                    tcp_socket_for_incoming_signals.close();
                }
                tcp_socket_for_incoming_signals = null;
            }
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
    public static IO_Bundle sendStuffToMap(String avatar_name, Enum key_command, int width, int height, String optional_text) {
        System.out.println("Starting Internet.sendStuffToMap(" + avatar_name + ", " + key_command.name() + ",...)");
        if (!isConnected) {
            final int error_code = makeConnectionUsingIP_Address("localhost");
            if (error_code != 0) {
                System.out.println("Failed to send setuff over internet in Internet.sendStuffToMap(" + avatar_name + ", " + key_command.name() + ",...)");
            } else {
                System.out.println("Made initial connection to map in Internet.sendStuffToMap(" + avatar_name + ", " + key_command.name() + ",...)");
            }
        }
        try {
            final String to_send = unique_id_string + " " + avatar_name + " "
                    + key_command.name() + " " + width + " " + height + " " + optional_text;
            final byte[] buf = to_send.getBytes();
            final DatagramPacket packet = new DatagramPacket(buf, buf.length, Internet.address, Map.UDP_PORT_NUMBER_FOR_MAP_RECIEVING_AND_CLIENT_SENDING);
            if (Internet.udp_socket_for_outgoing_signals != null && tcp_socket_for_incoming_signals != null
                    && object_input_stream != null && Internet.udp_socket_for_incoming_signals != null) {
                Internet.udp_socket_for_outgoing_signals.send(packet);
            } else {
                System.out.println("UDP or TCP or input stream is null in " + "Internet.sendStuffToMap(" + avatar_name + ", " + key_command.name() + ",...)");
                System.out.println("Impossible error in " + "Internet.sendStuffToMap(" + avatar_name + ", " + key_command.name() + ",...)");
                System.exit(-23);
            }

            if (Internet.is_using_TCP) {
                System.out.println("Calling Internet.sendStuffToMap over TCP");
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
            } else {
                System.out.println("Calling Internet.sendStuffToMap over UDP");
                // recieve IO_Bundle from map over UDP connection
                final byte[] recieved;
                DatagramPacket recvPacket = new DatagramPacket(recieved = new byte[2048], recieved.length);
                if (recieved[0] == 0 && recieved[1] == 0 && recieved[2] == 0 && recieved[3] == 0) {
                    System.err.println("To send is zeros in Internet [Controller]!!!!!!!!!!!");
                } else {
                    System.err.println("To send is NOT zeros in Internet [Controller]!!!!!!!!!!!");
                }
                final int numReceivedBytes = recvPacket.getLength(); // returns length of data to be sent or data recieved.
                System.out.println("Number of recieved bytes in Internet.sendStuffToMap(" + avatar_name + ", " + key_command.name() + ",...): "
                        + numReceivedBytes);
                IO_Bundle to_return = Internet.bytesToBundle(recieved);
                return to_return;
            }

        } catch (Exception e) {
            System.err.println("Exception in " + "Internet.sendStuffToMap(" + avatar_name + ", " + key_command.name() + ",...)" + " named: " + e.toString());
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Allows the controller to connects itself to an internet connection and
     * use Internet.sendStuffToMap(String, Enum, int, int, "")
     *
     * @param ip_address - use "localhost" to connect to local machine, ex.
     * "192.***.***.***".
     * @return 0 if connection successful, -1 if connection not successful
     */
    public static int makeConnectionUsingIP_Address(String ip_address) {
        ip_address = ip_address.trim().toLowerCase();
        System.out.println("Starting Internet.makeConnectionUsingIP_Address(" + ip_address + ")");
        if (!ip_address.equals("localhost") && !ip_address.matches(".*[0-9].*")) {
            RunGame.setUseInternet(false);
            isConnected = false;
            System.out.println("Not using internet in Internet.makeConnectionUsingIP_Address(String ip_address)");
            return 0;
        } else {
            RunGame.setUseInternet(true);
            System.out.println("Using internet in Internet.makeConnectionUsingIP_Address(String ip_address)");
        }
        ObjectOutputStream oos = null;
        try {
            if (object_input_stream != null) {
                object_input_stream.close();
                object_input_stream = null;
            }
            Internet.address = InetAddress.getByName(ip_address);
            udp_socket_for_outgoing_signals = new DatagramSocket();
            udp_socket_for_incoming_signals = new DatagramSocket(Map.UDP_PORT_NUMBER_FOR_MAP_SENDING_AND_CLIENT_RECIEVING);
            tcp_socket_for_incoming_signals = new Socket();
            tcp_socket_for_incoming_signals.setTcpNoDelay(true); // no latency
            tcp_socket_for_incoming_signals.setReuseAddress(true); // allow client to reconnect
            tcp_socket_for_incoming_signals.connect(new InetSocketAddress(ip_address, Map.TCP_PORT_NUMBER));
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
    /*
     public static byte[] bundleToBytes1(IO_Bundle io_bundle) {
     if (io_bundle == null) {
     System.err.println("IO_Bundle to be converted to array is null in Internet.bundleToBytes(IO_Bundle io_bundle)");
     } else {
     System.err.println("IO_Bundle to be converted to array is null in Internet.bundleToBytes(IO_Bundle io_bundle)");
     }
     ByteArrayOutputStream boas = new ByteArrayOutputStream();
     ObjectOutput out = null;
     byte[] bytes = null;
     try {
     out = new ObjectOutputStream(boas);
     out.writeObject(io_bundle);
     bytes = boas.toByteArray();
     } catch (Exception e) {
     System.err.println("Exception in Internet.bundleToBytes(IO_Bundle io_bundle) named: " + e.toString());
     e.printStackTrace();
     } finally {
     try {
     if (out != null) {
     out.close();
     }
     boas.close();
     } catch (Exception ex) {
     }
     }
     return bytes;
     }*/

    public static byte[] bundleToBytes(IO_Bundle io_bundle) {
        if (io_bundle == null) {
            System.err.println("IO_Bundle to be converted to array IS null in Internet.bundleToBytes(IO_Bundle io_bundle)");
        } else {
            System.err.println("IO_Bundle to be converted to array ISN'T null in Internet.bundleToBytes(IO_Bundle io_bundle)");
        }
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream(2048);
            ObjectOutputStream oos = new ObjectOutputStream(baos);
            oos.writeObject(io_bundle);
            oos.flush();
            oos.close();
            // get the byte array of the object
            byte[] obj = baos.toByteArray();
            baos.close();
            return obj;
        } catch (Exception e) {
            System.err.println("Exception in Internet.bundleToBytes(IO_Bundle io_bundle) named: " + e.toString());
            e.printStackTrace();
            return null;
        }
    }

    public static IO_Bundle bytesToBundle(byte[] data) {
        if (data[0] == 0 && data[1] == 0 && data[2] == 0 && data[3] == 0 && data[4] == 0) {
            System.err.println("Array to be converted to IO_Bundle DOES start with zeros in Internet.bytesToBundle(byte[] data)");
        } else {
            System.err.println("Array to be converted to IO_Bundle DOESN'T start with zeros in Internet.bytesToBundle(byte[] data)");
        }
        try {
            ByteArrayInputStream bais = new ByteArrayInputStream(data);
            ObjectInputStream ois = new ObjectInputStream(bais);
            IO_Bundle obj = (IO_Bundle) ois.readObject();
            ois.close();
            return obj;
        } catch (Exception e) {
            System.err.println("Exception in Internet.bytesToBundle(byte[] data) named: " + e.toString());
            e.printStackTrace();
            return null;
        }
    }

    /*
     public static IO_Bundle bytesToBundle(byte[] data) {
     // prevents invalid stream header
     if (data[0] == 0 && data[1] == 0 && data[2] == 0 && data[3] == 0 && data[4] == 0) {
     System.err.println("Array to be converted to IO_Bundle is null in Internet.bytesToBundle(byte[] data)");
     } else {
     System.err.println("Array to be converted to IO_Bundle is not null in Internet.bytesToBundle(byte[] data)");
     }
     ByteArrayInputStream bis = new ByteArrayInputStream(data);
     ObjectInput in = null;
     IO_Bundle io_bundle = null;
     try {
     in = new ObjectInputStream(bis);
     io_bundle = (IO_Bundle) in.readObject();

     } catch (Exception e) {
     System.err.println("Exception in Internet.bytesToBundle(byte[] data) named: " + e.toString());
     e.printStackTrace();
     } finally {
     try {
     bis.close();
     if (in != null) {
     in.close();
     }
     } catch (IOException ex) {
     }
     }
     return io_bundle;
     }*/
    /**
     * Gets your MAC address to be used as a unique_id on success. Produces a
     * random string if a valid MAC address could not be obtained.
     *
     * @return either MAC address or a random, unique identifier
     */
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
