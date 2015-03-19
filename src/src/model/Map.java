package src.model;

import java.awt.Color;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.PatternSyntaxException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import src.IO_Bundle;
import src.Key_Commands;
import src.RunGame;
import src.model.constructs.Avatar;
import src.model.constructs.DrawableThingStatsPack;
import src.model.constructs.Entity;
import src.model.constructs.EntityStatsPack;
import src.model.constructs.Terrain;
import src.model.constructs.items.Item;
import src.model.constructs.items.PickupableItem;

/**
 *
 * @author John-Michael Reed
 */
public class Map implements MapMapEditor_Interface, MapUser_Interface {

    public static final int MAX_NUMBER_OF_WORLDS = 1;
    private static int number_of_worlds_generated_ = 0;

    public final static int TCP_PORT_NUMBER = 14456;
    public final static int UDP_PORT_NUMBER = 14455;

    //<editor-fold desc="Fields and Accessors" defaultstate="collapsed">
    // The map has a clock
    private int time_measured_in_turns;
    // MAP MUST BE SQUARE
    //TODO:if Map has to be square, why have two different variables that will always be equivalent?
    public int height_;
    public int width_;

    // String is the entity's name. The entity name must be unqiue or else bugs will occur.
    private LinkedHashMap<String, Entity> entity_list_;
    private LinkedList<Item> items_list_;
    // 2d array of tiles.
    private transient MapTile map_grid_[][];
    private GetMapInputFromUsers udp_thread;
    private TCP_Connection_Maker tcp_thread;
    private ConcurrentHashMap<String, Single_User_TCP_Thread> users = new ConcurrentHashMap<>();

    public void grusomelyKillTheMapThread() {
        if (tcp_thread != null && tcp_thread.isAlive()) {
            tcp_thread.stop();
            tcp_thread = null;
        }
        if (udp_thread != null && udp_thread.isAlive()) {
            udp_thread.stop();
            udp_thread = null;
        }
        for (ConcurrentHashMap.Entry<String, Single_User_TCP_Thread> entry : this.users.entrySet()) {
            if (entry.getValue() != null) {
                entry.getValue().closeAndNullifyConnection();
                entry.getValue().stop();
            }
        }
    }

    /**
     *
     * @param name - name of Entity
     * @return Entity with the name of of input.
     */
    public Entity getEntityByName(String name) {
        return this.entity_list_.get(name);
    }

    public LinkedList<Item> getItemsList() {
        return items_list_;
    }

    public MapTile[][] getMapGrid() {
        return map_grid_;
    }

    /**
     * Gets mapTile at (x,y).
     *
     * @param x_pos - x position of tile
     * @param y_pos - y position of tile
     * @return MapTile at (x,y), null if tile is outside the map.
     */
    public MapTile getTile(int x_pos, int y_pos) {
        if (x_pos < 0 || y_pos < 0 || x_pos >= map_grid_[0].length || y_pos >= map_grid_.length) {
            return null;
        }
        return map_grid_[y_pos][x_pos];
    }

    /**
     * Gets the character representation of a tile
     *
     * @author John-Michael Reed
     * @param x
     * @param y
     * @return error code: returns empty space if the tile is off the map
     */
    public char getTileRepresentation(int x, int y) {
        MapTile tile_at_x_y = this.getTile(x, y);
        if (tile_at_x_y == null) {
            return ' ';
        } else {
            return tile_at_x_y.getTopCharacter();
        }
    }

    public Color getColorRepresentation(int x, int y) {
        MapTile tile_at_x_y = this.getTile(x, y);
        if (tile_at_x_y == null) {
            return Color.WHITE;//Off the map should be white.
        } else {
            return tile_at_x_y.getTopColor();
        }
    }

    //</editor-fold>
    //<editor-fold desc="Constructors" defaultstate="collapsed">
    // This should never get called
    @SuppressWarnings("unused")
    private Map() {//throws Exception {
        height_ = 0;
        width_ = 0;
        System.exit(-777);
        /*
         Exception e = new Exception("Do not use this constructor");
         throw e;*/

    }

    /**
     * Map Constructor, creates new x by y Map.
     *
     * @param x - Length of Map
     * @param y - Height of Map
     */
    public Map(int x, int y) {
        if (number_of_worlds_generated_ < MAX_NUMBER_OF_WORLDS) {
            ++number_of_worlds_generated_;

            height_ = y;
            width_ = x;

            map_grid_ = new MapTile[height_][width_];
            for (int i = 0; i < height_; ++i) {
                for (int j = 0; j < width_; ++j) {
                    map_grid_[i][j] = new MapTile(j, i); //switch rows and columns
                }
            }
            entity_list_ = new LinkedHashMap<String, Entity>();
            items_list_ = new LinkedList<Item>();
            time_measured_in_turns = 0;

            try {
                udp_thread = new GetMapInputFromUsers();
            } catch (IOException e) {
                // No clue what causes this
                e.printStackTrace();
                System.exit(-6);
                return;
            }
            tcp_thread = new TCP_Connection_Maker();

            udp_thread.start();
            tcp_thread.start();
        } else {
            System.err.println("Number of world allowed: "
                    + MAX_NUMBER_OF_WORLDS);
            System.err.println("Number of worlds already in existence: "
                    + number_of_worlds_generated_);
            System.err.println("Please don't make more than "
                    + MAX_NUMBER_OF_WORLDS + " worlds.");
            System.exit(-4);
        }
    }

    //</editor-fold>
    //<editor-fold desc="TCP TO User Thread (optional use)" defaultstate="collapsed">
    private class TCP_Connection_Maker extends Thread {

        public IO_Bundle bundle_to_send_ = null; // ** bullshit **

        final int portNumber = Map.TCP_PORT_NUMBER;

        public void run() {
            try (ServerSocket serverSocket = new ServerSocket(portNumber)) {
                serverSocket.setPerformancePreferences(0, 1, 0);
                while (true) {
                    Socket to_accept = serverSocket.accept();
                    to_accept.setTcpNoDelay(true);
                    to_accept.setReuseAddress(true); // allow for re-connections
                    ObjectInputStream object_input_stream_ = new ObjectInputStream(to_accept.getInputStream());
                    ObjectOutputStream object_output_stream = new ObjectOutputStream(to_accept.getOutputStream());
                    object_output_stream.flush();
                    String unique_id = (String) object_input_stream_.readObject();
                    System.out.println("String was accepted. Unique id is: " + unique_id);
                    Map.Single_User_TCP_Thread new_thread = new Map.Single_User_TCP_Thread(to_accept, unique_id, object_output_stream);
                    new_thread.start();
                    object_output_stream = null;
                }
            } catch (Exception e) {
                e.printStackTrace();
                System.err.println("Could not listen on port " + portNumber);
                System.exit(-1);
            }
        }
    }

    private class Single_User_TCP_Thread extends Thread {

        public final String unique_id_;
        private final Socket socket_;
        private final ObjectOutputStream object_output_stream_;
        private IO_Bundle bundle_to_send_ = null;

        public void closeAndNullifyConnection() {
            if (socket_ != null) {
                if (socket_.isConnected()) {
                    try {
                        object_output_stream_.close();
                        socket_.close();
                    } catch (Exception e) {// socket already closed}
                    }
                }
            }
        }

        public boolean isConnectionAlive() {
            try {
                object_output_stream_.writeObject(null);
                return true;
            } catch (Exception e) {
                return false;
            }
        }

        public synchronized void setAndTransmitBundle(IO_Bundle to_set) {
            bundle_to_send_ = to_set;
            this.interrupt();
        }

        public Single_User_TCP_Thread(Socket socket, String unique_id, ObjectOutputStream object_output_stream) {
            super("Single_User_TCP_Thread");
            this.socket_ = socket;
            this.unique_id_ = unique_id;
            object_output_stream_ = object_output_stream;
        }

        public void run() {
            try {
                object_output_stream_.flush();
            } catch (Exception e) {
                e.printStackTrace();
            }
            // remove and replace on re-connection
            if (users.containsKey(unique_id_)) {
                Single_User_TCP_Thread to_kill = users.get(unique_id_);
                if (to_kill.isAlive() && to_kill.isConnectionAlive()) {
                    to_kill.closeAndNullifyConnection();
                    to_kill.stop();
                } else {
                    //to_kill.closeAndNullifyConnection();
                }
                System.out.println("replacing connection");
            }
            users.put(unique_id_, this);
            System.out.println("New connection made");
            while (true) {

                // end of resource statement beginning of execution
                if (bundle_to_send_ == null) {
                    System.out.println("bundle_to_send_ in ServerThread is null");
                } else {
                    System.out.println("bundle_to_send_ in ServerThread not null");
                }
                try {
                    Thread.sleep(Long.MAX_VALUE);
                } catch (InterruptedException e) {
                    try {
                        //do {
                            object_output_stream_.writeObject(bundle_to_send_);
                            object_output_stream_.flush();
                        //} while (Thread.currentThread().isInterrupted());
                    } catch (Exception e2) {
                        e2.printStackTrace();
                        System.out.println("connection disconnected in Map.ServerThread.run");
                        this.closeAndNullifyConnection();
                        this.stop();
                        break;
                    }
                }

            }
        }
    }

    //</editor-fold>
//<editor-fold desc="User Input Thread (optional use)" defaultstate="collapsed">
    private class GetMapInputFromUsers extends Thread {

        public GetMapInputFromUsers() throws IOException {
            this("GetMapInput");
        }

        public GetMapInputFromUsers(String name) throws IOException {
            super(name);

        }

        public void run() {

            System.out.println("UDP thread is running");

            while (true) {
                try {
                    byte[] buf = new byte[256];

                    // receive request
                    DatagramPacket packet = new DatagramPacket(buf, buf.length);

                    DatagramSocket socket = new DatagramSocket(Map.UDP_PORT_NUMBER);
                    socket.receive(packet);
                    System.out.println("The map recieved a packet.");
                    socket.close();
                    socket = null;

                    // "udp packet recieved in GetMapInputFromUsers
                    String decoded_string_with_trailing_zeros = new String(buf, "UTF-8");

                    String decoded_string = decoded_string_with_trailing_zeros.trim();

                    String[] splitArray;
                    try {
                        // split whenever at least one whitespace is encountered
                        splitArray = decoded_string.split("\\s+");
                    } catch (PatternSyntaxException ex) {
                        ex.printStackTrace();
                        System.exit(-16);
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

                    Single_User_TCP_Thread sender = users.get(unique_id);

                    while (sender == null) {
                        sender = users.get(unique_id);
                    }

                    // start the actual function
                    Entity to_recieve_command;
                    if (entity_list_.containsKey(username)) {
                        to_recieve_command = entity_list_.get(username);
                    } else {
                        to_recieve_command = null;
                    }
                    System.out.println("Entity: " + to_recieve_command.name_
                            + " xpos: " + to_recieve_command.getMapRelation().getMyXCoordinate() + "\nypos: " + to_recieve_command.getMapRelation().getMyYCoordinate());
                    ArrayList<String> strings_for_IO_Bundle = null;
                    if (to_recieve_command != null) {
                        if (to_recieve_command.getMapRelation() == null) {
                            System.err.println(to_recieve_command.name_ + " has a null relation with this map. ");
                            continue;
                        }
                        if (command != null) {
                            if (command == Key_Commands.STANDING_STILL) {
                                strings_for_IO_Bundle = null;
                            } else if (to_recieve_command.isAlive() == true) {
                                strings_for_IO_Bundle = to_recieve_command.acceptKeyCommand(command, optional_text);
                            } else {
                                strings_for_IO_Bundle = null;
                            }
                            if (to_recieve_command.isAlive() == true) {

                                ArrayList<Character> compressed_characters = new ArrayList<Character>();
                                ArrayList<Short> frequencies = new ArrayList<Short>();
                                char[][] view = null;

                                ArrayList<Color> compressed_colors = new ArrayList<Color>();
                                ArrayList<Short> color_frequencies = new ArrayList<Short>();
                                /*Color[][] colors = makeColors(to_recieve_command.getMapRelation().getMyXCoordinate(),
                                 to_recieve_command.getMapRelation().getMyYCoordinate(),
                                 width_from_center, height_from_center);*/
                                Color[][] colors = null;
                                runLengthEncodeColors(to_recieve_command.getMapRelation().getMyXCoordinate(),
                                        to_recieve_command.getMapRelation().getMyYCoordinate(),
                                        width_from_center, height_from_center, compressed_colors, color_frequencies);

                                // compressed_characters and frequencies are pass by referance outputs
                                runLengthEncodeView(to_recieve_command.getMapRelation().getMyXCoordinate(),
                                        to_recieve_command.getMapRelation().getMyYCoordinate(),
                                        width_from_center, height_from_center, compressed_characters, frequencies);

                                if (compressed_characters == null || frequencies == null || compressed_characters.isEmpty()) {
                                    System.out.println("Bad - compression produced no encodings");
                                    System.exit(-4);
                                }

                                IO_Bundle return_package = new IO_Bundle(
                                        compressed_characters,
                                        frequencies,
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
                                // return return_package;
                                sender.setAndTransmitBundle(return_package);
                                continue;
                            } else {
                                ArrayList<Character> compressed_characters = null;
                                ArrayList<Short> frequencies = null;
                                char[][] view = null;
                                Color[][] colors = null;
                                ArrayList<Color> compressed_colors = null;
                                ArrayList<Short> color_frequencies = null;
                                IO_Bundle return_package = new IO_Bundle(
                                        compressed_characters,
                                        frequencies,
                                        compressed_colors,
                                        color_frequencies,
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
                                        to_recieve_command.isAlive()
                                );
                                // return return_package;
                                //tcp_thread.bundle_to_send_ = return_package;
                                //tcp_thread.interrupt();
                                sender.setAndTransmitBundle(return_package);
                                continue;
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
                                    to_recieve_command.isAlive()
                            );
                            // return return_package;
                            // tcp_thread.bundle_to_send_ = return_package;
                            // tcp_thread.interrupt();
                            sender.setAndTransmitBundle(return_package);
                            continue;
                        } else {
                            System.err.println("avatar + " + username + " is invalid. \n"
                                    + "Please check username and make sure he is on the map.");
                            //tcp_thread.bundle_to_send_ = null;
                            //tcp_thread.interrupt();
                            sender.setAndTransmitBundle(null);
                            continue;
                        }
                    } else {
                        System.out.println(username + " cannot be found on this map.");
                        // return null;
                        //tcp_thread.bundle_to_send_ = null;
                        //tcp_thread.interrupt();
                        sender.setAndTransmitBundle(null);
                        continue;
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                    System.out.println("Connection is closed");
                    //tcp_thread.bundle_to_send_ = null;
                    //tcp_thread.interrupt();
                    continue;
                }
            }
        }
    }
    //</editor-fold>

//<editor-fold desc="Map Methods" defaultstate="collapsed">
    /**
     * Adds an entity to the map and provides it with a MapEntity_Relation.
     *
     * @param e - Entity to be added
     * @param x - x position of where you want to add entity
     * @param y - y posiition of where you want to add entity
     * @return -1 on fail, 0 on success
     */
    public int addAsEntity(Entity e, int x, int y) {
        e.setMapRelation(new MapEntity_Relation(this, e, x, y));
        System.out.println(e.name_);
        int error_code = this.map_grid_[y][x].addEntity(e);
        System.out.println(e.name_ + "2");
        if (error_code == 0) {
            this.entity_list_.put(e.name_, e);
        } else {
            e.setMapRelation(null);
            System.err.println("Error in entity list");
        }
        return error_code;
    }

    /**
     * Adds an entity to the map and provides it with a MapKnight_Relation.
     *
     * @param e - Entity to be added
     * @param x - x position of where you want to add entity
     * @param y - y posiition of where you want to add entity
     * @return -1 on fail, 0 on success
     */
    public int addAsKnight(Entity e, int x, int y) {
        e.setMapRelation(new MapKnight_Relation(this, e, x, y));
        System.out.println(e.name_);
        int error_code = this.map_grid_[y][x].addEntity(e);
        System.out.println(e.name_ + "2");
        if (error_code == 0) {
            this.entity_list_.put(e.name_, e);
        } else {
            e.setMapRelation(null);
            System.err.println("Error in entity list");
        }
        return error_code;
    }

    /**
     * Adds an avatar to the map and provides it with a MapAvatar_Relation.
     *
     * @param a - Avatar to be added
     * @param x - x position of where you want to add Avatar
     * @param y - y posiition of where you want to add Avatar
     * @return -1 on fail, 0 on success
     */
    public int addAsAvatar(Avatar a, int x, int y) {
        System.out.println("Adding avatar: " + a.name_ + " to the map");
        a.setMapRelation(new MapAvatar_Relation(this, a, x, y));
        int error_code = this.map_grid_[y][x].addEntity(a);
        if (error_code == 0) {
            this.entity_list_.put(a.name_, a);
            Entity aa = this.entity_list_.get(a.name_);
            if (aa == null) {
                System.err.println("Something is seriously wrong with the avatar list");
                System.exit(-5);
            }
        } else {
            a.setMapRelation(null);
            System.err.println("Error in avatar list");
        }
        return error_code;
    }

    /**
     * Once a tile has terrain, that terrain is constant.
     *
     * @param t - Terrain
     * @param x - x position for tile
     * @param y - y position for tile
     * @return error code, 0 for success
     */
    public int addTerrain(Terrain t, int x, int y) {
        t.setMapRelation(new MapTerrain_Relation(this, t));
        int error_code = this.map_grid_[y][x].addTerrain(t);
        if (error_code == 0) {
            t.getMapRelation().setMapTile(this.map_grid_[y][x]);
        } else {
            t.setMapRelation(null);
        }
        return error_code;
    }

    /**
     * Returns true if the given coord is within the map
     *
     * @param x
     * @param y
     * @return
     */
    public boolean withinMap(int x, int y) {
        return ((x >= 0 && x < this.width_) && (y >= 0 && y < this.height_));
    }

    /**
     * Adds an avatar to the map.
     *
     * @param a - Avatar to be added
     * @param x - x position of where you want to add Avatar
     * @param y - y posiition of where you want to add Avatar
     * @return -1 on fail, 0 on success
     */
    // public int addAsAvatar(Avatar a, int x, int y) {
    //    return addAsEntity(a, x, y);
    //}
    /**
     * Adds an avatar to the map.
     *
     * @param a - Avatar to be added
     * @param x - x position of where you want to add Avatar
     * @param y - y posiition of where you want to add Avatar
     * @return -1 on fail, 0 on success
     */
    /*
     public int addAsAvatar(Avatar a, int x, int y) {
     System.out.println("Adding avatar: " + a.name_ + " to the map");
     a.setMapRelation(new MapAvatar_Relation(this, a, x, y));
     int error_code = this.map_grid_[y][x].addAsEntity(a);
     if (error_code == 0) {
     this.avatar_list_.put(a.name_, a);
     Avatar aa = this.avatar_list_.get(a.name_);
     if (aa == null) {
     System.err.println("Something is seriously wrong with the avatar list");
     System.exit(-5);
     }
     } else {
     a.setMapRelation(null);
     }
     return error_code;
     }
     */
    @Override
    public IO_Bundle getMapAt(int x, int y, int width, int height) {
        char[][] view = makeView(x, y, width, height);
        Color[][] colors = makeColors(x, y, width, height);
        return new IO_Bundle(null, null, null, null, view, colors, null, null, null, 0, 0, 0, 0, null, null, null, 0, true);
        //Mapeditor has no game over condition, you are always alive. 
    }

    /**
     * Makes a rectangular view with y coordinates in first [] of 2D array
     *
     * @param x_center
     * @param y_center
     * @param width_from_center - how much offset from the left side and the
     * right side the view has
     * @param height_from_center- how much horizontal offset from the center
     * point the view has
     * @return
     */
    public char[][] makeView(int x_center, int y_center, int width_from_center, int height_from_center) {
        char[][] view = new char[1 + 2 * height_from_center][1 + 2 * width_from_center];
        int y_index = 0;
        for (int y = y_center - height_from_center; y <= y_center + height_from_center; ++y) {
            int x_index = 0;
            for (int x = x_center - width_from_center; x <= x_center + width_from_center; ++x) {
                view[y_index][x_index] = this.getTileRepresentation(x, y);
                ++x_index;
            }
            ++y_index;
        }
        return view;
    }

    /**
     * Uses run length encoding with characters "char[] unchanged_characters"
     * and frequencies "int[] unchanged_indexes."
     *
     * @param x_center
     * @param y_center
     * @param width_from_center
     * @param height_from_center
     * @param unchanged_characters - empty arraylist of characters - outputs as
     * a list of repeated encoded characters
     * @param frequencies - empty arraylist of encoded character frequencies -
     * outputs as a corresponding list of frequencies
     */
    public void runLengthEncodeView(final int x_center, final int y_center, final int width_from_center,
            final int height_from_center, ArrayList<Character> unchanged_characters, ArrayList<Short> frequencies) {
        if (unchanged_characters.isEmpty() && frequencies.isEmpty()) {
            short length = 1;
            int array_index = 0;
            final int x_start = x_center - width_from_center;
            final int y_start = y_center - height_from_center;
            char first = this.getTileRepresentation(x_start, y_start);
            for (int y = y_start; y <= y_center + height_from_center; ++y) {
                for (int x = x_start; x <= x_center + width_from_center; ++x) {
                    if (this.getTileRepresentation(x, y) != first) {
                        unchanged_characters.add(first); // java.lang.ArrayIndexOutOfBoundsException
                        frequencies.add(length);
                        length = 1;
                        first = this.getTileRepresentation(x, y);
                    } else {
                        // flag stays true
                        ++length;
                    }
                }
            }
        } else {
            System.out.println("Precondition for Map.runLengthEncodeView not met - output parameters are not empty");
            System.exit(-16);
        }
    }

    /**
     * Use this when the command the map is receiving requires a string
     * parameter
     *
     * @param username
     * @param command
     * @param width_from_center
     * @param height_from_center
     * @param text - empty string preffered when not in use.
     * @return Bundle of stuff used by the display.
     */
    public IO_Bundle sendCommandToMapWithOptionalText(String username, Key_Commands command, int width_from_center, int height_from_center, String text) {
        // Avatar to_recieve_command = this.avatar_list_.get(username);
        Entity to_recieve_command;
        if (this.entity_list_.containsKey(username)) {
            to_recieve_command = this.entity_list_.get(username);
        } else {
            to_recieve_command = null;
            System.err.println("The avatar of entity you are trying to reach does not exist.");
        }
        ArrayList<String> strings_for_IO_Bundle = null;
        if (to_recieve_command != null) {
            if (to_recieve_command.getMapRelation() == null) {
                System.err.println(to_recieve_command.name_ + " has a null relation with this map. ");
                return null;
            }
            if (command != null) {
                if (command == Key_Commands.STANDING_STILL) {
                    strings_for_IO_Bundle = null;
                } else if (to_recieve_command.isAlive() == true) {
                    strings_for_IO_Bundle = to_recieve_command.acceptKeyCommand(command, text);
                } else {
                    strings_for_IO_Bundle = null;
                }
                if (to_recieve_command.isAlive() == true) {
                    char[][] view = makeView(to_recieve_command.getMapRelation().getMyXCoordinate(),
                            to_recieve_command.getMapRelation().getMyYCoordinate(),
                            width_from_center, height_from_center);
                    Color[][] colors = makeColors(to_recieve_command.getMapRelation().getMyXCoordinate(),
                            to_recieve_command.getMapRelation().getMyYCoordinate(),
                            width_from_center, height_from_center);
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
                            to_recieve_command.isAlive()
                    );
                    return return_package;
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
                            to_recieve_command.isAlive()
                    );
                    return return_package;
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
                        to_recieve_command.isAlive()
                );
                return return_package;
            } else {
                System.err.println("avatar + " + username + " is invalid. \n"
                        + "Please check username and make sure he is on the map.");
                return null;
            }
        } else {
            System.out.println(username + " cannot be found on this map.");
            return null;
        }
    }

    public void runLengthEncodeColors(final int x_center, final int y_center, final int width_from_center,
            final int height_from_center, ArrayList<Color> unchanged_colors, ArrayList<Short> frequencies) {
        if (unchanged_colors.isEmpty() && frequencies.isEmpty()) {
            short length = 1;
            int array_index = 0;
            final int x_start = x_center - width_from_center;
            final int y_start = y_center - height_from_center;
            Color first = this.getColorRepresentation(x_start, y_start);
            for (int y = y_start; y <= y_center + height_from_center; ++y) {
                for (int x = x_start; x <= x_center + width_from_center; ++x) {
                    if (this.getColorRepresentation(x, y) != first) {
                        unchanged_colors.add(first); // java.lang.ArrayIndexOutOfBoundsException
                        frequencies.add(length);
                        length = 1;
                        first = this.getColorRepresentation(x, y);
                    } else {
                        // flag stays true
                        ++length;
                    }
                }
            }
        } else {
            System.out.println("Precondition for Map.runLengthEncodeView not met - output parameters are not empty");
            System.exit(-16);
        }
    }

    public Color[][] makeColors(int x_center, int y_center, int width_from_center, int height_from_center) {
        Color[][] colors = new Color[1 + 2 * height_from_center][1 + 2 * width_from_center];
        int y_index = 0;
        for (int y = y_center - height_from_center; y <= y_center + height_from_center; ++y) {
            int x_index = 0;
            for (int x = x_center - width_from_center; x <= x_center + width_from_center; ++x) {
                colors[y_index][x_index] = this.getColorRepresentation(x, y);
                ++x_index;
            }
            ++y_index;
        }
        return colors;
    }

    /**
     * Removes entity from map.
     *
     * @param e - entity to be removed
     * @return -1 if the entity to be removed does not exist.
     */
    public int removeEntity(Entity e) {
        Entity removed = this.entity_list_.remove(e.name_);
        if (removed != null) {
            System.out.println(removed.name_ + " has been removed from the map");
        } else {
            System.err.println("The entity to be removed does not exist in the list of entities");
        }
        if (this.map_grid_[e.getMapRelation().getMyYCoordinate()][e.getMapRelation().getMyXCoordinate()].getEntity() == e) {
            this.map_grid_[e.getMapRelation().getMyYCoordinate()][e.getMapRelation().getMyXCoordinate()].removeEntity();
            e.setMapRelation(null);
            System.gc();
            return 0;
        } else {
            System.err.println("The avatar to be removed cannot be found on the map.");
            System.exit(-88);
            return -1;
        }
    }

    public int removeAvatar(Avatar avatar) {
        return removeEntity(avatar);
    }

    /**
     * Adds an item to the map.
     *
     * @param i - Item to be added
     * @param x - x position of where you want to add item
     * @param y - y posiition of where you want to add item
     * @return -1 on fail, 0 on success
     */
    public int addItem(Item i, int x, int y) {
        i.setMapRelation(new MapItem_Relation(this, i));
        int error_code = this.map_grid_[y][x].addItem(i);
        if (error_code == 0) {
            items_list_.add(i);
        } else {
            i.setMapRelation(null);
        }
        return error_code;
    }

    /**
     * Removes top item from tile in position (x,y).
     *
     * @param x - x position of tile
     * @param y - y position of tile
     * @return Top item from tile (x,y)
     */
    public Item removeTopItem(int x, int y) {
        Item item = this.map_grid_[y][x].removeTopItem();
        items_list_.remove(item);
        return item;
    }

    //</editor-fold>
    //<editor-fold desc="XML Saving/Loading" defaultstate="collapsed">
    /**
     * Writes this map to the given XML Element in the given XML document
     *
     * @param doc The XML Document to write to
     * @param e_map The XML Element to write to
     * @return 0 = success
     * @author Alex Stewart
     */
    public int xml_writeMap(Document doc, Element e_map) {
        // MAP::TIME)
        Element e_time = doc.createElement("time");
        e_map.appendChild(doc.createTextNode(Integer.toString(this.time_measured_in_turns)));

        // MAP::MAP_GRID
        Element e_map_grid = doc.createElement("map_grid");
        e_map_grid.setAttribute("width", Integer.toString(this.width_));
        e_map_grid.setAttribute("height", Integer.toString(this.height_));

        Element e_l;
        for (int j = 0; j < this.height_; j++) {
            for (int i = 0; i < this.width_; i++) {
                e_l = doc.createElement("map_tile");
                e_l.setAttribute("x", Integer.toString(i));
                e_l.setAttribute("y", Integer.toString(j));

                // Terrain
                Terrain terr = this.map_grid_[i][j].getTerrain();
                if (terr == null) {
                    RunGame.errOut("xml_writeMap: null terrain @ [" + i + ", " + j + "]");
                    return 1;
                }
                xml_writeTerrain(doc, e_l, terr);

                // Entity
                Entity ent = this.map_grid_[i][j].getEntity();
                if (ent != null) {
                    xml_writeEntity(doc, e_l, ent);
                }

                // Item list
                if (map_grid_[i][j].getItemList().size() != 0) {
                    Element e_itemlist = doc.createElement("item_list");
                    for (Item item : map_grid_[i][j].getItemList()) {
                        xml_writeItem(doc, e_itemlist, item);
                    }
                    e_l.appendChild(e_itemlist);
                }

                e_map_grid.appendChild(e_l);
            }
        }

        // MAP - APPEND
        e_map.appendChild(e_time);
        e_map.appendChild(e_map_grid);

        return 0; // Return success
    }

    /**
     * Writes an entity to an XML element
     *
     * @param doc The DOM document to write to
     * @param parent The parent element to write this entity in
     * @param entity The Entity object to write
     * @return The entity's DOM Element, or null - on failure.
     */
    private Element xml_writeEntity(Document doc, Element parent, Entity entity) {
        Element e_entity = doc.createElement("entity");

        // Name
        e_entity.setAttribute("name", entity.getName());

        if (this.entity_list_.containsValue(entity)) {
            e_entity.appendChild(doc.createElement("b_avatar"));
        }

        // Direction
        Element e_dir = doc.createElement("direction");
        e_dir.appendChild(doc.createTextNode(entity.getFacingDirection().toString()));

        // Item List
        Element e_itemList = doc.createElement("item_list");
        // write inventory items to xml
        Item equipped1 = (Item) entity.getPrimaryEquipped();
        Item equipped2 = (Item) entity.getSecondaryEquipped();
        /* Hey Alex I changed the item hierarchy and made entities dual weild */
        ArrayList<PickupableItem> tmp_inv = entity.getInventory();
        Element tmp_eInvItem; // temp inventory item
        for (int i = 0; i < entity.getInventory().size(); i++) {
            tmp_eInvItem = xml_writeItem(doc, e_itemList, tmp_inv.get(i));

            if (tmp_inv.get(i) == equipped1) {
                tmp_eInvItem.appendChild(doc.createElement("b_equipped"));
            }
            e_itemList.appendChild(tmp_eInvItem);
        }
        e_entity.appendChild(e_itemList);

        xml_writeStatsDrawable(doc, e_entity, (DrawableThingStatsPack) entity.getStatsPack());
        xml_writeStatsEntity(doc, e_entity, entity.getStatsPack());

        parent.appendChild(e_entity);

        return e_entity;
    }

    /**
     * Writes an Item to a DOM document
     *
     * @param doc The DOM Document to write to
     * @param parent The parent Element to insert the item in
     * @param item The Item to write
     * @return The item's DOM Element, or null - if there was an error
     */
    private Element xml_writeItem(Document doc, Element parent, Item item) {
        Element e_item = doc.createElement("item");

        // Name
        e_item.setAttribute("name", item.getName());
        // Is One Shot
        if (item.isOneShot()) {
            e_item.appendChild(doc.createElement("b_one_shot"));
        }
        // Is Passable
        if (item.isPassable()) {
            e_item.appendChild(doc.createElement("b_passable"));
        }
        // Goes in Inventory
        if (item.goesInInventory()) {
            e_item.appendChild(doc.createElement("b_inventory-able"));
        }

        xml_writeStatsDrawable(doc, e_item, item.getStatsPack());

        parent.appendChild(e_item);
        return e_item;
    }

    private Element xml_writeStatsDrawable(Document doc, Element parent, DrawableThingStatsPack stats) {
        if (stats == null) {
            RunGame.errOut("xml_writeStatsDrawable: null statspack");
            return null;
        }

        Element e_stats = doc.createElement("stats_drawable");
        Element trans_eStat;

        if (stats.getArmor_rating_() != 0) {
            trans_eStat = doc.createElement("armor_rating");
            trans_eStat.appendChild(doc.createTextNode(Integer.toString(stats.getArmor_rating_())));
            e_stats.appendChild(trans_eStat);
        }
        /*
         if (stats.getDefensive_rating_() != 0) {
         trans_eStat = doc.createElement("def_rating");
         trans_eStat.appendChild(doc.createTextNode(Integer.toString(stats.getDefensive_rating_())));
         e_stats.appendChild(trans_eStat);
         }
         */
        if (stats.getOffensive_rating_() != 0) {
            trans_eStat = doc.createElement("off_rating");
            trans_eStat.appendChild(doc.createTextNode(Integer.toString(stats.getOffensive_rating_())));
            e_stats.appendChild(trans_eStat);
        }

        parent.appendChild(e_stats);
        return e_stats;
    }

    private Element xml_writeStatsEntity(Document doc, Element parent, EntityStatsPack stats) {
        if (stats == null) {
            RunGame.errOut("xml_writeStatsEntity: null statspack");
            return null;
        }

        Element e_stats = doc.createElement("stats_entity");
        Element tra_eStat;

        if (stats.getLives_left_() != 0) {
            tra_eStat = doc.createElement("lives");
            tra_eStat.appendChild(doc.createTextNode(Integer.toString(stats.getLives_left_())));
            e_stats.appendChild(tra_eStat);
        }
        if (stats.getStrength_level_() != 0) {
            tra_eStat = doc.createElement("strength");
            tra_eStat.appendChild(doc.createTextNode(Integer.toString(stats.getStrength_level_())));
            e_stats.appendChild(tra_eStat);
        }
        if (stats.getAgility_level_() != 0) {
            tra_eStat = doc.createElement("agility");
            tra_eStat.appendChild(doc.createTextNode(Integer.toString(stats.getAgility_level_())));
            e_stats.appendChild(tra_eStat);
        }
        if (stats.getIntellect_level_() != 0) {
            tra_eStat = doc.createElement("intellect");
            tra_eStat.appendChild(doc.createTextNode(Integer.toString(stats.getIntellect_level_())));
            e_stats.appendChild(tra_eStat);
        }
        if (stats.getHardiness_level_() != 0) {
            tra_eStat = doc.createElement("hardness");
            tra_eStat.appendChild(doc.createTextNode(Integer.toString(stats.getHardiness_level_())));
            e_stats.appendChild(tra_eStat);
        }
        if (stats.getQuantity_of_experience_() != 0) {
            tra_eStat = doc.createElement("XP");
            tra_eStat.appendChild(doc.createTextNode(Integer.toString(stats.getQuantity_of_experience_())));
            e_stats.appendChild(tra_eStat);
        }
        if (stats.getMovement_level_() != 0) {
            tra_eStat = doc.createElement("movement");
            tra_eStat.appendChild(doc.createTextNode(Integer.toString(stats.getMovement_level_())));
            e_stats.appendChild(tra_eStat);
        }
        if (stats.getMax_life_() != 0) {
            tra_eStat = doc.createElement("max_life");
            tra_eStat.appendChild(doc.createTextNode(Integer.toString(stats.getMax_life_())));
            e_stats.appendChild(tra_eStat);
        }
        if (stats.getMax_mana_() != 0) {
            tra_eStat = doc.createElement("max_mana");
            tra_eStat.appendChild(doc.createTextNode(Integer.toString(stats.getMax_mana_())));
            e_stats.appendChild(tra_eStat);
        }
        if (stats.getMoves_left_in_turn_() != 0) {
            tra_eStat = doc.createElement("moves_remaining");
            tra_eStat.appendChild(doc.createTextNode(Integer.toString(stats.getMoves_left_in_turn_())));
            e_stats.appendChild(tra_eStat);
        }
        if (stats.getCached_current_level_() != 0) {
            tra_eStat = doc.createElement("level");
            tra_eStat.appendChild(doc.createTextNode(Integer.toString(stats.getCached_current_level_())));
            e_stats.appendChild(tra_eStat);
        }
        if (stats.getCurrent_life_() != 0) {
            tra_eStat = doc.createElement("life");
            tra_eStat.appendChild(doc.createTextNode(Integer.toString(stats.getCurrent_life_())));
            e_stats.appendChild(tra_eStat);
        }
        if (stats.getCurrent_mana_() != 0) {
            tra_eStat = doc.createElement("mana");
            tra_eStat.appendChild(doc.createTextNode(Integer.toString(stats.getCurrent_mana_())));
            e_stats.appendChild(tra_eStat);
        }

        parent.appendChild(e_stats);
        return e_stats;
    }

    private Element xml_writeTerrain(Document doc, Element parent, Terrain terr) {
        Element e_Terrain = doc.createElement("terrain");

        if (terr.getName() == null) {
            RunGame.errOut("xml_writeTerrain: null Terrain name");
            return null;
        }
        e_Terrain.setAttribute("name", terr.getName());

        // BOOLEANS:
        if (terr.isMountain()) {
            e_Terrain.appendChild(doc.createElement("b_mountain"));
        }

        if (terr.isWater()) {
            e_Terrain.appendChild(doc.createElement("b_water"));
        }

        // Terrain::Decal - only write if non-null
        if (terr.getDecal() != '\u0000') {
            Element e_decal = doc.createElement("decal");
            e_decal.appendChild(doc.createTextNode(Character.toString(terr.getDecal())));
            e_Terrain.appendChild(e_decal);
        }

        // Terrain::Character
        Element e_dChar = doc.createElement("terr_char");
        e_dChar.appendChild(doc.createTextNode(Character.toString(terr.getRepresentation())));
        e_Terrain.appendChild(e_dChar);

        // Terrain::Color - only write if non-null
        /* What is this?
         if (terr.color_ != null) {
         Element e_color = doc.createElement("color");
         e_color.appendChild(doc.createTextNode(terr.color_.name()));
         e_Terrain.appendChild(e_color);
         }
         */
        parent.appendChild(e_Terrain);
        return e_Terrain;
    }

    //</editor-fold>
    /**
     * Takes in name so save to, defaults to date
     *
     * @param foo
     */
    @Override
    public int saveGame(String foo) {
        RunGame.saveGameToDisk(foo);
        return 0;
    }

    /**
     * Takes in name to load.
     *
     * @param foo
     */
    @Override
    public int loadGame(String foo) {
        RunGame.loadGame(foo);
        return 0;
    }

}
