package src.model;

import java.awt.Color;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.PatternSyntaxException;
import src.IO_Bundle;
import src.ControllerInternet;
import src.Key_Commands;
import src.RunGame;
import src.model.constructs.Entity;

/**
 * Used for sending and receiving data from a Map [not part of Iteration 2]
 * @author John-Michael Reed
 */
public class MapInternet extends Thread {

    //<editor-fold desc="Static fields" defaultstate="collapsed">
    public final static int TCP_PORT_NUMBER = 14456;
    public final static int UDP_PORT_NUMBER_FOR_MAP_SENDING_AND_CLIENT_RECIEVING = 14455;
    public final static int UDP_PORT_NUMBER_FOR_MAP_RECIEVING_AND_CLIENT_SENDING = 14454;
    //</editor-fold>
    //<editor-fold desc="Non-static fields" defaultstate="collapsed">
    private final DatagramSocket recieving_socket;
    private TCP_Connection_Maker tcp_thread;
    private ConcurrentHashMap<String, Packet_Sender> users = new ConcurrentHashMap<>();
    private final TCP_Connection_Maker connection_initiator = new TCP_Connection_Maker();
    private final Map my_owner_;
    //</editor-fold>
    //<editor-fold desc="Constructors" defaultstate="collapsed">

    private MapInternet() throws SocketException {
        recieving_socket = null;
        my_owner_ = null;
        System.exit(-43);
    }

    public MapInternet(Map owner) throws SocketException {
        super("MapInternet");
        recieving_socket = new DatagramSocket(MapInternet.UDP_PORT_NUMBER_FOR_MAP_RECIEVING_AND_CLIENT_SENDING);
        connection_initiator.start();
        my_owner_ = owner;
    }

    //</editor-fold>
    //<editor-fold desc="MapInternet Methods" defaultstate="collapsed">
    @Override
    public void run() {
        while ( !this.isInterrupted() ) {
            this.getInputForMap();
        }
        //cleanup
        connection_initiator.interrupt();
        for (ConcurrentHashMap.Entry<String, Packet_Sender> entry : this.users.entrySet()) {
            if (entry.getValue() != null) {
                entry.getValue().interrupt();
            }
        }
    }
    private void getInputForMap() {

        System.out.println("incomind UDP thread is running in Map.GetMapInputFromUsers.run()");

        while (true) {
            try {
                byte[] buf = new byte[256];

                // receive request
                DatagramPacket packet = new DatagramPacket(buf, buf.length);

                recieving_socket.receive(packet);
                System.out.println("The map recieved a packet in Map.GetMapInputFromUsers.run() from address: " + packet.getAddress().toString());

                // "udp packet recieved in GetMapInputFromUsers
                String decoded_string_with_trailing_zeros = new String(buf, "UTF-8");

                String decoded_string = decoded_string_with_trailing_zeros.trim();

                String[] splitArray;
                try {
                    // split whenever at least one whitespace is encountered
                    splitArray = decoded_string.split("\\s+");
                } catch (PatternSyntaxException ex) {
                    ex.printStackTrace();
                    System.exit(-15);
                    return;
                }

                System.out.print("Recieved array: ");
                for (int i = 0; i < splitArray.length; ++i) {
                    System.out.print(splitArray[i] + " ");
                }
                System.out.println();

                String unique_id = splitArray[0];
                String username = splitArray[0 + 1];
                String command_enum_as_a_string = splitArray[1 + 1];
                Key_Commands command = Key_Commands.valueOf(command_enum_as_a_string);
                int width_from_center = Integer.parseInt(splitArray[2 + 1], 10);
                int height_from_center = Integer.parseInt(splitArray[3 + 1], 10);
                String optional_text;
                if (splitArray.length == 4 + 1) {
                    optional_text = null;
                } else if (splitArray.length >= 5 + 1) {
                    optional_text = "";
                    for (int i = 4 + 1; i < splitArray.length; ++i) {
                        optional_text = optional_text + " " + splitArray[i];
                    }
                    System.out.println("Optional text: " + optional_text);
                    optional_text = optional_text.trim();
                } else {
                    System.out.println("Error. splitArray.length == " + splitArray.length);
                    return;
                }

                // add to list of addresses for mass udp.
                // addresses_for_udp.put(unique_id, packet.getAddress());
                // Sender must recieve either TCP or UDP.
                Packet_Sender sender = null;
                InetAddress sender_address = null;
                while (sender == null) {
                    sender = users.get(unique_id);
                }

                // start the actual function
                Entity to_recieve_command;
                if ( my_owner_.hasEntity(username) ) {
                    to_recieve_command = my_owner_.getEntityByName(username);
                } else {
                    to_recieve_command = null;
                }
                sendToClient(to_recieve_command, command, width_from_center, height_from_center, optional_text, sender);
                // tell each and every player to refresh their screens.
                    /*
                 for (ConcurrentHashMap.Entry<String, Packet_Sender> entry : users.entrySet()) {
                 if (entry.getValue() != null) {
                 // for every thread except the one that just went
                            
                 if(! entry.getKey().equals(unique_id)) {
                 sendToClient(entry.getValue().last_controlled, Key_Commands.DO_ABSOLUTELY_NOTHING, 
                 width_from_center, height_from_center, "", entry.getValue());
                 }
                 }
                 }*/
            } catch (IOException e) {
                e.printStackTrace();
                System.out.println("Connection is closed");
                //tcp_thread.bundle_to_send_ = null;
                //tcp_thread.interrupt();
                continue;
            }
        }
    }
    private void sendToClient(Entity to_recieve_command, Key_Commands command,
            int width_from_center, int height_from_center, String optional_text, Packet_Sender sender) {
        ArrayList<String> strings_for_IO_Bundle = null;
        if (to_recieve_command != null) {
            if (to_recieve_command.getMapRelation() == null) {
                System.err.println(to_recieve_command.name_ + " has a null relation with this map. ");
                return;
            }
            if (command == Key_Commands.STANDING_STILL) {
                strings_for_IO_Bundle = null;
            } else if (to_recieve_command.isAlive() == true && command != null) {
                strings_for_IO_Bundle = to_recieve_command.acceptKeyCommand(command, optional_text);
            } else {
                strings_for_IO_Bundle = null;
            }
            ArrayList<Character> compressed_characters = null;
            ArrayList<Short> character_frequencies = null;
            char[][] view = null;
            ArrayList<Color> compressed_colors = null;
            ArrayList<Short> color_frequencies = null;
            /*Color[][] colors = makeColors(to_recieve_command.getMapRelation().getMyXCoordinate(),
             to_recieve_command.getMapRelation().getMyYCoordinate(),
             width_from_center, height_from_center);*/
            Color[][] colors = null;
            if (to_recieve_command.isAlive() && command != null) {
                compressed_characters = new ArrayList<>();
                character_frequencies = new ArrayList<>();
                compressed_colors = new ArrayList<>();
                color_frequencies = new ArrayList<>();
                my_owner_.runLengthEncodeColors(to_recieve_command.getMapRelation().getMyXCoordinate(),
                        to_recieve_command.getMapRelation().getMyYCoordinate(),
                        width_from_center, height_from_center, compressed_colors, color_frequencies);

                // compressed_characters and character_frequencies are pass by referance outputs
                my_owner_.runLengthEncodeView(to_recieve_command.getMapRelation().getMyXCoordinate(),
                        to_recieve_command.getMapRelation().getMyYCoordinate(),
                        width_from_center, height_from_center, compressed_characters, character_frequencies);

                if (compressed_characters == null || character_frequencies == null || compressed_characters.isEmpty()) {
                    System.out.println("Bad - compression produced no encodings");
                    System.exit(-4);
                }
            }

            IO_Bundle return_package = new IO_Bundle(
                    compressed_characters,
                    character_frequencies,
                    compressed_colors,
                    color_frequencies,
                    view,
                    colors,
                    to_recieve_command.getInventory(),
                    // Don't for get left and right hand items
                    to_recieve_command.getStatsPack(), to_recieve_command.getOccupation(),
                    to_recieve_command.getNum_skillpoints_(), to_recieve_command.getBind_wounds_(),
                    to_recieve_command.getBargain_(), to_recieve_command.getObservation_(),
                    to_recieve_command.getPrimaryEquipped(),
                    to_recieve_command.getSecondaryEquipped(),
                    strings_for_IO_Bundle,
                    to_recieve_command.getNumGoldCoins(),
                    to_recieve_command.isAlive()
            );
            sender.setBundleAvatarAndInterrupt(to_recieve_command, return_package);
            return;
        }
        // Silently ignore it if the avatar name is wrong.

    }
    //</editor-fold>
    //<editor-fold desc="TCP_Connection_Maker and Packet_Sender" defaultstate="collapsed">
    public class TCP_Connection_Maker extends Thread {

        public IO_Bundle bundle_to_send_ = null; // ** bullshit **

        final int portNumber = MapInternet.TCP_PORT_NUMBER;

        public void run() {
            try (ServerSocket serverSocket = new ServerSocket(portNumber)) {
                serverSocket.setPerformancePreferences(0, 1, 0);
                while (true) {
                    Socket to_accept = serverSocket.accept();
                    to_accept.setTcpNoDelay(true);
                    to_accept.setReuseAddress(true); // allow for re-connections
                    ObjectInputStream object_input_stream_ = new ObjectInputStream(to_accept.getInputStream());
                    String unique_id = (String) object_input_stream_.readObject();
                    System.out.println("String was accepted in Map.TCP_Connection_Maker.run() . Unique id is: " + unique_id);
                    // remove and replace on re-connection
                    if (users.containsKey(unique_id)) {
                        Packet_Sender to_kill = users.get(unique_id);
                        // to_kill.closeAndNullifyConnection();
                        to_kill.closeAndNullifyObjectOutputStream();
                        users.remove(unique_id);
                        System.out.println("Replacing already made connection in Map.TCP_Connection_Maker.run()");
                    }
                    ObjectOutputStream object_output_stream = new ObjectOutputStream(to_accept.getOutputStream());
                    Packet_Sender new_thread = new Packet_Sender(to_accept,
                            unique_id, object_output_stream, to_accept.getInetAddress());
                    users.put(unique_id, new_thread);
                    new_thread.start();
                    object_output_stream = null;
                }
            } catch (Exception e) {
                System.err.println("Exception in Map.TCP_Connection_Maker.run() named: " + e.toString());
                e.printStackTrace();
                System.err.println("Could not listen on port " + portNumber);
                System.exit(-1);
            }
        }
    }
    private class Packet_Sender extends Thread {

        public final String unique_id_;
        private final Socket tcp_output_socket_;
        private DatagramSocket udp_output_socket_;
        private final ObjectOutputStream object_output_stream_;
        private final InetAddress address_;
        private boolean was_oos_closed = false;
        private IO_Bundle bundle_to_send_ = null;
        private byte[] my_bytes_ = null;
        private Entity last_controlled = null;

        public Entity seeLastControlled() {
            return last_controlled;
        }

        public String seeLastControlledName() {
            if (last_controlled != null) {
                return last_controlled.name_;
            } else {
                return null;
            }
        }

        public void closeAndNullifyConnection() {
            if (tcp_output_socket_ != null) {
                if (tcp_output_socket_.isConnected()) {
                    try {
                        tcp_output_socket_.close();
                    } catch (Exception e) {// recieving_socket already closed}
                    }
                }
            }
            if (udp_output_socket_ != null) {
                udp_output_socket_.close();
            }
        }

        public void closeAndNullifyObjectOutputStream() {
            try {
                if (object_output_stream_ != null && was_oos_closed == false) {
                    object_output_stream_.close();
                    was_oos_closed = true;
                }
            } catch (Exception e) {
                System.err.println("object_output_stream_ was already closed in Map.ServerThread.run()");
                e.printStackTrace();
            }
        }

        public synchronized void setBundleAvatarAndInterrupt(Entity e, IO_Bundle to_set) {
            bundle_to_send_ = to_set;
            last_controlled = e;
            this.interrupt();
        }

        public Packet_Sender(Socket socket, String unique_id, ObjectOutputStream object_output_stream, InetAddress address) {
            super("Single_User_TCP_Thread");
            this.tcp_output_socket_ = socket;
            this.unique_id_ = unique_id;
            object_output_stream_ = object_output_stream;
            address_ = address;
        }

        public void run() {
            try {
                udp_output_socket_ = new DatagramSocket();
                //this.tcp_output_socket_.setKeepAlive(true); // hopefully will cause an exception to be thrown if used disconnected for 2+ hours
                object_output_stream_.flush();
            } catch (Exception e) {
                e.printStackTrace();
            }
            while (true) {
                // end of resource statement beginning of execution
                if (bundle_to_send_ == null) {
                    System.out.println("bundle_to_send_ in Map.ServerThread.run() is null");
                } else {
                    System.out.println("bundle_to_send_ in Map.ServerThread.run() not null");
                }
                try {
                    Thread.sleep(Integer.MAX_VALUE);
                } catch (InterruptedException e) {
                    if (RunGame.getUseTCP()) {
                        try {
                            // do {
                            object_output_stream_.writeObject(bundle_to_send_);
                            object_output_stream_.flush();
                            // } while (Thread.currentThread().isInterrupted()); // ignore signal if theyinterrupt while I write.
                        } catch (NullPointerException null_ptr_exception) {
                            System.err.print("Err: Caught the NullPointerException. ObjectOutputStream has already been nullified by another thread in Map.ServerThread.run()");
                            System.out.print("Out: Caught the NullPointerException. ObjectOutputStream has already been nullified by another thread in Map.ServerThread.run()");
                            null_ptr_exception.printStackTrace();
                            return;
                        } catch (Exception e2) {
                            System.err.println("Err: object_output_stream_ experienced an exception in Map.ServerThread.run() named: " + e2.toString());
                            System.out.println("Out: object_output_stream_ experienced an exception in Map.ServerThread.run() named: " + e2.toString());
                            e2.printStackTrace();
                            return;
                        }
                    } else {
                        if (bundle_to_send_ == null) {
                            System.err.println("Return package is null in MAP!!!!!!!!!!!");
                        } else {
                            System.err.println("Return package is NOT null in MAP!!!!!!!!!!!");
                        }
                        byte[] to_send = ControllerInternet.bundleToBytes(bundle_to_send_);
                        if (to_send[0] == 0 && to_send[0] == 0 && to_send[0] == 0 && to_send[0] == 0) {
                            System.err.println("To send is zeros in MAP!!!!!!!!!!!");
                        } else {
                            System.err.println("To send is NOT zeros in MAP!!!!!!!!!!!");
                        }
                        System.out.println("Length of array sent over UDP in Map.GetMapInputFromUsers.run() : " + to_send.length);
                        if (to_send.length > 1400) {
                            System.out.println("Map cannot fit the whole byte array in one packet");
                        } else {
                            System.out.println("Map fit the whole byte array on one packet");
                        }
                        DatagramPacket packet_to_send = new DatagramPacket(
                                to_send, to_send.length, address_, UDP_PORT_NUMBER_FOR_MAP_SENDING_AND_CLIENT_RECIEVING);
                        try {
                            udp_output_socket_.send(packet_to_send);
                        } catch (IOException udp_send_exception) {
                            udp_send_exception.printStackTrace();
                        }
                    }
                }
            }
        }
    }
    //</editor-fold>
}
