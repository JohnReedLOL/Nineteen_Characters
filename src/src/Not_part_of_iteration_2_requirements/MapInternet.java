package src.Not_part_of_iteration_2_requirements;

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
import src.Key_Commands;
import src.RunGame;
import src.model.Map;
import src.model.constructs.Entity;

/**
 * Used for sending and receiving data from a Map [not part of Iteration 2]
 *
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
    private ConcurrentHashMap<String, Packet_Sender> users = new ConcurrentHashMap<>();
    //private final TCP_Connection_Maker connection_initiator = new TCP_Connection_Maker();
    private final Map my_owner_;
    private int frame_number = 0;
    private boolean is_using_compression = true;

    public void enableFrameCompression() {
        is_using_compression = true;
    }

    public void disableFrameCompression() {
        is_using_compression = false;
    }
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
        //connection_initiator.start();
        my_owner_ = owner;
    }

    //</editor-fold>
    //<editor-fold desc="MapInternet Methods" defaultstate="collapsed">
    @Override
    public synchronized void run() {
        while (!this.isInterrupted()) {
            this.getInputForMap();
        }
        //cleanup
        //connection_initiator.interrupt();
        for (ConcurrentHashMap.Entry<String, Packet_Sender> entry : this.users.entrySet()) {
            if (entry.getValue() != null) {
                entry.getValue().interrupt();
                System.out.println("Killed a packet_sender thread");
            }
        }
    }

    public void interruptAllMyUserThreads() {
        for (ConcurrentHashMap.Entry<String, Packet_Sender> entry : this.users.entrySet()) {
            if (entry.getValue() != null) {
                entry.getValue().interrupt();
                System.out.println("Killed a packet_sender thread");
            }
        }
    }

    private void getInputForMap() {
        try {
            byte[] buf = new byte[256];

            // receive request
            DatagramPacket packet = new DatagramPacket(buf, buf.length);

            recieving_socket.receive(packet);
            System.out.println("Map received a packet");
                //RunGame.dbgOut("The map recieved a packet in Map.GetMapInputFromUsers.run() from address: " + packet.getAddress().toString(), 6);

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
                // RunGame.dbgOut("Optional text: " + optional_text, 6);
                optional_text = optional_text.trim();
            } else {
                // RunGame.dbgOut("Error. splitArray.length == " + splitArray.length, 6);
                return;
            }
            if (!users.containsKey(unique_id)) {
                Packet_Sender new_users_packet_sender = new Packet_Sender(unique_id, packet.getAddress());
                new_users_packet_sender.start();
                users.put(unique_id, new_users_packet_sender);
            }
            if (!Key_Commands.DO_ABSOLUTELY_NOTHING.equals(command)) {
                my_owner_.makeTakeTurns();
                // entities dies and is removed from the map here
            }
            Packet_Sender sender = users.get(unique_id);
            sendToClient(username, command, width_from_center, height_from_center, optional_text, sender);
        } catch (IOException e) {
            e.printStackTrace();
            RunGame.errOut("Connection is closed");
        }
    }

    private void sendToClient(String username, Key_Commands command,
            int width_from_center, int height_from_center, String text, Packet_Sender sender) {
        Entity to_recieve_command = null;
        if (my_owner_.getEntityList().containsKey(username)) {
            to_recieve_command = my_owner_.getEntityList().get(username);
        } else {
            to_recieve_command = null;
            System.err.println("The avatar of entity you are trying to reach does not exist.");
        }
        ArrayList<String> strings_for_IO_Bundle = null;
        if (to_recieve_command != null) {
            if (to_recieve_command.getMapRelation() == null) {
                System.err.println(to_recieve_command.name_ + " has a null relation with this map. ");
                IO_Bundle to_return = null;
                sender.setBundleAvatarAndNotify(to_recieve_command, to_return);
                return;
            }
            if (command != null) {
                if (command == Key_Commands.STANDING_STILL) {
                    strings_for_IO_Bundle = null;
                } else if (to_recieve_command.hasLivesLeft() == true) {
                    strings_for_IO_Bundle = to_recieve_command.acceptKeyCommand(command, text);
                } else {
                    strings_for_IO_Bundle = null;
                }
                if (to_recieve_command.hasLivesLeft() == true) {
                    char[][] view = my_owner_.makeView(to_recieve_command.getMapRelation().getMyXCoordinate(),
                            to_recieve_command.getMapRelation().getMyYCoordinate(),
                            width_from_center, height_from_center);
                    Color[][] colors = my_owner_.makeColors(to_recieve_command.getMapRelation().getMyXCoordinate(),
                            to_recieve_command.getMapRelation().getMyYCoordinate(),
                            width_from_center, height_from_center);
                    if (!Key_Commands.DO_ABSOLUTELY_NOTHING.equals(command)) {
                        my_owner_.makeTakeTurns();//Make all the maptiles take a turn.
                    }
                    IO_Bundle return_package = new IO_Bundle(
                            null, null, null, null,
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
                            to_recieve_command.hasLivesLeft()
                    );
                    sender.setBundleAvatarAndNotify(to_recieve_command, return_package);
                    System.out.println("Map sent back a packet with a view and stats");
                    if(sender.isAlive() ) {
                        System.out.println("Sender is alive");
                    }
                    return;
                } else {
                    char[][] view = null;
                    Color[][] colors = null;
                    IO_Bundle return_package = new IO_Bundle(
                            null, null, null, null,
                            view,
                            colors,
                            null,
                            // Don't for get left and right hand items
                            null,
                            null,
                            -1,
                            -1,
                            -1,
                            -1,
                            null,
                            null,
                            null,
                            -1,
                            to_recieve_command.hasLivesLeft()
                    );
                    sender.setBundleAvatarAndNotify(to_recieve_command, return_package);
                    System.out.println("Map sent back a packet with just an indication of game over.");
                    return;
                }
            } else if (command == null) {
                IO_Bundle return_package = new IO_Bundle(null, null, null, null, null, null, to_recieve_command.getInventory(),
                        // Don't for get left and right hand items
                        to_recieve_command.getStatsPack(), to_recieve_command.getOccupation(),
                        to_recieve_command.getNum_skillpoints_(), to_recieve_command.getBind_wounds_(),
                        to_recieve_command.getBargain_(), to_recieve_command.getObservation_(),
                        to_recieve_command.getPrimaryEquipped(),
                        to_recieve_command.getSecondaryEquipped(),
                        strings_for_IO_Bundle,
                        to_recieve_command.getNumGoldCoins(),
                        to_recieve_command.hasLivesLeft()
                );
                sender.setBundleAvatarAndNotify(to_recieve_command, return_package);
                System.out.println("Map sent back a packet with just the stats");
                return;
            } else {
                System.err.println("avatar + " + username + " is invalid. \n"
                        + "Please check username and make sure he is on the map.");
                sender.setBundleAvatarAndNotify(to_recieve_command, null);
                System.out.println("Map sent back a null packet");
                return;
            }
        } else {
            System.out.println(username + " cannot be found on this map.");
            sender.setBundleAvatarAndNotify(to_recieve_command, null);
            System.out.println("Map sent back a null packet");
            return;
        }
    }

    private class Packet_Sender extends Thread {

        public final String unique_id_;
        private DatagramSocket udp_output_socket_;
        private final InetAddress address_;
        private IO_Bundle bundle_to_send_ = null;
        private byte[] my_bytes_ = null;
        private Entity last_controlled = null;
        volatile boolean is_notified = false;

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
            if (udp_output_socket_ != null) {
                udp_output_socket_.close();
            }
        }

        //private volatile boolean is_notified = false;
        public synchronized void setBundleAvatarAndNotify(Entity e, IO_Bundle to_set) {
            bundle_to_send_ = to_set;
            last_controlled = e;
            is_notified = true;
            this.notify();
        }

        public Packet_Sender(String unique_id, InetAddress address) {
            super("Single_User_TCP_Thread");
            this.unique_id_ = unique_id;
            address_ = address;
        }

        public synchronized void run() {
            try {
                udp_output_socket_ = new DatagramSocket();
            } catch (Exception e) {
                e.printStackTrace();
            }
            while (!isInterrupted()) {
                try {
                    if(! is_notified) {
                    wait();
                    } else {
                        // keep going
                    }
                    is_notified = false;
                } catch (InterruptedException e) {
                    return;
                }
                byte[] to_send = ControllerInternet.bundleToBytes(bundle_to_send_);
                if (frame_number % 256 == 0) {
                    if (is_using_compression) {
                        System.out.print("With compression, ");
                    } else {
                        System.out.print("Without compression, ");
                    }
                    System.out.println("number of bytes sent = " + to_send.length);
                }
                ++frame_number;
                DatagramPacket packet_to_send = new DatagramPacket(
                        to_send, to_send.length, address_, UDP_PORT_NUMBER_FOR_MAP_SENDING_AND_CLIENT_RECIEVING);
                try {
                    udp_output_socket_.send(packet_to_send);
                } catch (IOException udp_send_exception) {
                    udp_send_exception.printStackTrace();
                }
            }
            return; // only return if interrupted.
        }
    }
    //</editor-fold>
}
