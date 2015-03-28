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
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.PatternSyntaxException;
import src.IO_Bundle;
import src.Key_Commands;
import src.io.controller.ControllerInternet_NEW;
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
                System.out.println("Killed a packet_sender thread1");
            }
        }
    }

    public void interruptAllMyUserThreads() {
        for (ConcurrentHashMap.Entry<String, Packet_Sender> entry : this.users.entrySet()) {
            if (entry.getValue() != null) {
                entry.getValue().interrupt();
                System.out.println("Killed a packet_sender thread2");
            }
        }
        this.stop();
    }

    // Generates fake packet loss to mimic real packet loss in long distance internet connection
    final static Random random_packet_loss_generator = new Random();

    private final int current_buffer_size_ = 1024;

    private void getInputForMap() {
        byte[] buf = new byte[current_buffer_size_];
        try {
            // receive request
            DatagramPacket receivePacket = new DatagramPacket(buf, buf.length);

            recieving_socket.receive(receivePacket);

            // Generates fake packet loss to mimic real packet loss in long distance internet connection
            int chance_of_dropped_packet1 = random_packet_loss_generator.nextInt(100);
            if (chance_of_dropped_packet1 < 5) {
                //System.out.println("lost a packet [mock packet loss level 1]");
                return;
            }
            MapMessage message_from_controller = null;
            try {
                message_from_controller = ControllerInternet_NEW.bytesToObject(receivePacket.getData());
            } catch (IOException eof) {
                System.out.println("Map internet buffer size is too small. "
                        + RunGame.getStackTrace());
                System.exit(76);
            }

            /*String decoded_string_with_trailing_zeros = new String(receivePacket.getData(),
             receivePacket.getOffset(), receivePacket.getLength(), "UTF-8");*/
            // Generates fake packet loss to mimic real packet loss in long distance internet connection
            int chance_of_dropped_packet2 = random_packet_loss_generator.nextInt(100);
            if (chance_of_dropped_packet2 < 5) {
                //System.out.println("lost a packet [mock packet loss level 2]");
                return;
            }

            //System.out.println("Map received a receivePacket");
            //RunGame.dbgOut("The map recieved a receivePacket in Map.GetMapInputFromUsers.run() from address: " + receivePacket.getAddress().toString(), 6);
            // "udp receivePacket recieved in GetMapInputFromUsers
            String unique_id = message_from_controller.unique_id_;
            String username = message_from_controller.username_;
            Key_Commands command = message_from_controller.command_;
            int width_from_center = message_from_controller.width_from_center_;
            int height_from_center = message_from_controller.height_from_center_;
            String optional_text;
            if (message_from_controller.optional_text_ == null
                    || message_from_controller.optional_text_.equals("")) {
                optional_text = null;
            } else {
                optional_text = message_from_controller.optional_text_;
                // RunGame.dbgOut("Optional text: " + optional_text, 6);
                optional_text = optional_text.trim();
            }
            if (!users.containsKey(unique_id)) {
                Packet_Sender new_users_packet_sender = new Packet_Sender(unique_id, receivePacket.getAddress());
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
            System.err.println("Connection is closed");
            System.exit(-4);
        }
    }

    private IO_Bundle make_dead_packet() {
        IO_Bundle return_package = new IO_Bundle(
                "",
                null,
                null,
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
                false
        );
        return return_package;
    }

    private void sendToClient(String username, Key_Commands command,
            int width_from_center, int height_from_center, String text, Packet_Sender sender) {
        Entity to_recieve_command = null;
        if (my_owner_.getEntityList().containsKey(username)) {
            to_recieve_command = my_owner_.getEntityList().get(username);
        } else {
            to_recieve_command = null;
            System.err.println("The avatar of entity you are trying to reach does not exist.");
            IO_Bundle return_package = make_dead_packet();
            sender.setBundleAvatarAndNotify(to_recieve_command, return_package);
            return;
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
                    int[][] colors = my_owner_.makeColors(to_recieve_command.getMapRelation().getMyXCoordinate(),
                            to_recieve_command.getMapRelation().getMyYCoordinate(),
                            width_from_center, height_from_center);
                    if (!Key_Commands.DO_ABSOLUTELY_NOTHING.equals(command)) {
                        my_owner_.makeTakeTurns();//Make all the maptiles take a turn.
                    }
                    IO_Bundle return_package = new IO_Bundle(
                            to_recieve_command.getObservationString(),
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
                    //System.out.println("Map sent back a receivePacket with a view and stats");
                    return;
                } else {
                    char[][] view = null;
                    int[][] colors = null;

                    IO_Bundle return_package = make_dead_packet();
                    sender.setBundleAvatarAndNotify(to_recieve_command, return_package);
                    System.out.println("Map sent back a packet with just an indication of game over.");
                    return;
                }
            } else if (command == null) {
                IO_Bundle return_package = new IO_Bundle(to_recieve_command.getObservationString(), null, null, to_recieve_command.getInventory(),
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
                    if (!is_notified) {
                        wait();
                    } else {
                        // keep going
                    }
                    is_notified = false;
                } catch (InterruptedException e) {
                    return;
                }
                byte[] to_send = ControllerInternet_NEW.objectToBytes(bundle_to_send_);
                if (frame_number % 256 == 0) {
                    System.out.println("Number of bytes sent = " + to_send.length + "." + RunGame.getStackTrace());
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
