package src;

import java.awt.Color;
import java.io.FileNotFoundException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

import src.io.controller.Controller;
import src.io.controller.GameController;
import src.io.controller.MapEditorController;
import src.model.map.Map;
import src.model.map.constructs.Avatar;
import src.model.map.constructs.Item;
import src.model.map.constructs.KnightsSerum;
import src.model.map.constructs.Merchant;
import src.model.map.constructs.Monster;
import src.model.map.constructs.ObstacleRemovingItem;
import src.model.map.constructs.OneHandedSword;
import src.model.map.constructs.OneShotAreaEffectItem;
import src.model.map.constructs.OneWayTeleportItem;
import src.model.map.constructs.PermanentObstacleItem;
import src.model.map.constructs.Shield;
import src.model.map.constructs.TemporaryObstacleItem;
import src.model.map.constructs.Terrain;
import src.model.map.constructs.Trap;
import src.model.map.constructs.TwoHandedSword;
import src.model.map.constructs.Villager;

/**
 * Initializes, opens the program.
 *
 * @author JohnReedLOL, Alex Stewart
 */
public class RunGame {

    private static ProgramOpts pOpts_ = null;
    private static SavedGame saveGame_;
    private static Avatar avatar_;
    private static Map map_;
    private static Controller uc_;
    private static int mapHeight_ = 40;
    private static int mapWidth_ = 40;
    private static boolean map_editor_mode_ = false;
    private static DatagramSocket udp_socket_for_outgoing_signals = null;
    private static InetAddress address = null;
    private final static String ip_address = "localhost";
    private static Socket tcp_socket_for_incoming_signals = null;
    private static Random rand = new Random();
    private static final int unique_id = rand.nextInt();
    private static final String unique_id_string = Integer.toString(unique_id, 10);
    private static ObjectInputStream object_input_stream = null;

    public static IO_Bundle sendStuffToMapOverTheInternet(String avatar_name, Enum key_command, int width, int height, String optional_text) {
        try {
            final String to_send = unique_id_string + " " + avatar_name + " "
                    + key_command.name() + " " + width + " " + height + " " + optional_text;
            final byte[] buf = to_send.getBytes();
            final DatagramPacket packet = new DatagramPacket(buf, buf.length, RunGame.address, Map.UDP_PORT_NUMBER);
            RunGame.udp_socket_for_outgoing_signals.send(packet);
            IO_Bundle to_recieve = (IO_Bundle) object_input_stream.readObject();
            // decompression
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

    public static String getAvatarName() {
        return avatar_.name_;
    }

    public static void main(String[] args) {
        parseArgs(args); // Parse command line arguments
        handleArgs(args);

        if (!map_editor_mode_) {
            startNewGame();
        } else {
            startMapEditor();
        }
        try { //InetAddress addr = InetAddress.getLocalHost();
            udp_socket_for_outgoing_signals = new DatagramSocket();
            address = InetAddress.getByName(ip_address);
            tcp_socket_for_incoming_signals = new Socket();
            tcp_socket_for_incoming_signals.setTcpNoDelay(true);
            tcp_socket_for_incoming_signals.connect(new InetSocketAddress(ip_address, Map.TCP_PORT_NUMBER));
            ObjectOutputStream oos = new ObjectOutputStream(tcp_socket_for_incoming_signals.getOutputStream());
            oos.flush();
            oos.writeObject(Integer.toString(RunGame.unique_id, 10));
            oos.flush();
            oos = null;
            object_input_stream = new ObjectInputStream(tcp_socket_for_incoming_signals.getInputStream());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    /*
     private static String getMacAddess() throws Exception
     {
        
     //Get MAC address
     String MAC_Username = "";
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
     */

    private static int startNewGame() {
        initialize(); // Initialize any data we need to before loading
        populateMap();//Add stuff into the map
        startGame(); // Begin the avatarcontroller loop
        return 0;
    }

    private static int startMapEditor() {
        initialize(); // Initialize any data we need to before loading
        new Thread(new Runnable() {

            @Override
            public void run() {
                uc_ = new MapEditorController(map_); // Begin the avatarcontroller loop
            }
        }).start();
        return 0;
    }

    public static void loadGame(String file_path) {

    }

    // <editor-fold desc="GAME METHODS" defaultstate="collapsed">
    private static void exitGame() {

    }

    private static void initialize() {
        if (saveGame_ == null) {
            saveGame_ = SavedGame.newSavedGame();
        }
        map_ = new Map(mapWidth_, mapHeight_);

    }

    private static void populateMap() {
        avatar_ = new Avatar("avatar", '☃');
        // map_.addAsAvatar(avatar_, 0, 0);
        map_.addAsAvatar(avatar_, 0, 0);

        Avatar buddy = new Avatar("buddy", '웃');
        // map_.addAsAvatar(buddy, 3, 0);
        map_.addAsKnight(buddy, 3, 0); // buddy can jump over entities!

        Villager villager1 = new Villager("villager1", '웃');
        villager1.getStatsPack().increaseQuantityOfExperienceBy(200);
        map_.addAsEntity(villager1, 3, 13);

        Monster monster = new Monster("monster1", '웃');
        monster.getStatsPack().increaseQuantityOfExperienceBy(300);
        map_.addAsEntity(monster, 13, 3);

        Merchant merchant = new Merchant("merchant1", '웃');
        merchant.getStatsPack().increaseQuantityOfExperienceBy(1000);
        map_.addAsEntity(merchant, 1, 1);
        Item teleport = new OneWayTeleportItem("tele", 'T', 0, 0);
        Item onehandedsword = new OneHandedSword("Excalibur", '|');
        Item twohandedsword = new TwoHandedSword("Two_hander", '|');
        Item shield = new Shield("Shieldy", 'O');
        OneShotAreaEffectItem heal = new OneShotAreaEffectItem("healer", 'h', Effect.HEAL, 10);
        OneShotAreaEffectItem hurt = new OneShotAreaEffectItem("hurter", 'u', Effect.HURT, 10);
        OneShotAreaEffectItem kill = new OneShotAreaEffectItem("killer", 'k', Effect.KILL, 10);
        OneShotAreaEffectItem level = new OneShotAreaEffectItem("leveler", 'l', Effect.LEVEL, 10);

        KnightsSerum knight_serum = new KnightsSerum("Knight serum", 'N');
        map_.addItem(knight_serum, 18, 12);

        ObstacleRemovingItem key = new ObstacleRemovingItem("Key", 'K');
        TemporaryObstacleItem door = new TemporaryObstacleItem("Door", 'D', key);
        map_.addItem(key, 11, 0);
        map_.addItem(door, 13, 0);

        map_.addItem(heal, 3, 2);
        map_.addItem(hurt, 6, 2);
        map_.addItem(kill, 9, 2);
        map_.addItem(level, 12, 2);

        Villager villager = new Villager("Tom", 'V');
        map_.addAsEntity(villager, 0, 5);

        //Add some traps
        Trap trap1 = new Trap("trap1", 'b', Effect.HURT, 2);
        map_.addItem(trap1, 1, 0);

        //seven.getStatsPack().offensive_rating_ = 17; //Can no longer do this.
        map_.addItem(teleport, 2, 4);
        map_.addItem(twohandedsword, 1, 1);
        map_.addItem(shield, 10, 10);
        map_.addItem(onehandedsword, 5, 5);
        for (int y = 0; y < mapHeight_; ++y) {
            for (int x = 0; x < mapWidth_; ++x) {
                Terrain obstacle = new Terrain("land", '▨', false, false);
                if (y == 4) {
                    if (x == 2) {
                        obstacle.addDecal('☠', Color.black);
                    } else if (x == 6) {
                        obstacle.addDecal('★', Color.yellow);
                    } else if (x == 9) {
                        obstacle.addDecal('✚', Color.red);
                    }
                }
                map_.addTerrain(obstacle, x, y);
            }
        }

        //Terrain obstacle = new Terrain("boulder", '■', true, false);
        //map_.addTerrain(obstacle, 2, 2);
        PermanentObstacleItem obstacle = new PermanentObstacleItem("boulder", '■');
        map_.addItem(obstacle, 2, 2);

        /*
         avatar_.getMapRelation().moveInDirection(18, 0);
         avatar_.getMapRelation().moveInDirection(0, 12);
         avatar_.getMapRelation().areaEffectFunctor.effectAreaWithinLine(5, 20, Effect.HEAL);
         avatar_.getMapRelation().moveInDirection(0, 1);
         avatar_.setFacingDirection(FacingDirection.LEFT);
         avatar_.getMapRelation().areaEffectFunctor.effectAreaWithinArc(3, 20, Effect.HURT);
         avatar_.setFacingDirection(FacingDirection.DOWN_RIGHT);
         avatar_.getMapRelation().areaEffectFunctor.effectAreaWithinArc(3, 1, Effect.KILL);
         System.out.println("x position of avatar: " + avatar_.getMapRelation().getMyXCoordinate());
         System.out.println("y position of avatar: " + avatar_.getMapRelation().getMyYCoordinate());

         for (int i = 0; i < 20; ++i) {
         ((MapUser_Interface) map_).sendCommandToMapWithOptionalText("avatar", Key_Commands.MOVE_DOWN, 10, 20, "");
         }
         */
    }

    private static void startGame() {
        new Thread(new Runnable() {

            @Override
            public void run() {
                uc_ = new GameController(map_, avatar_.name_);
            }
        }).start();

    }

    public static void saveGameToDisk(String foo) {
        if (saveGame_ == null) {
            saveGame_ = SavedGame.newSavedGame();
        }
        saveGame_.saveGame(map_, uc_, foo);
    }

    // </editor-fold>
    // <editor-fold desc="UTILITIES" defaultstate="collapsed">
    // Error date format for the errOut(Exception) write
    private static SimpleDateFormat errDateFormat_ = new SimpleDateFormat("yyyy-MM-dd HH:mm:ssZ");

    /**
     * This class holds information about optional program utilities which may
     * be triggered via command line arguments. Reference {@link #parseArgs} for
     * parsing implementation.
     */
    private static class ProgramOpts {

        // Debug Mode
        String[] dbg_match = {"-d", "--debug"};
        String[] editor_match = {"-e", "--editor"};
        boolean dbg_flag = false;
        boolean editor_flag = false;
        int dbg_level = 1;

        // Load Saved Game
        String[] lsg_match = {"-l", "--load"}; // option flag match string
        boolean lsg_flag = false; // whether or not to load the game
        int lsg_path = -1; // the index in args to get the path from

        // Redirect STDERR
        String[] err_match = {"-e", "-err-out"};
        boolean err_flag = false;
        int err_path = -1;
    }

    /**
     * Writes the provided String to the errOut stream with the prefix:
     * (DEBUG|0).
     *
     * @param s The String to write.
     */
    public static void dbgOut(String s) {
        if (s == null) {
            s = "NULL";
        }
        if (pOpts_.dbg_flag) {
            errOut("(DEBUG|0) " + s);
        }
    }

    /**
     * Writes the provided String to the errOut stream with the prefix:
     * (DEBUG|X) where X is the debug level of the output. If the provided debug
     * level is greater (larger number) than the debug level option set in
     * {@link ProgramOpts}, the debug string will not be output.
     *
     * @param s The debug string to write
     * @param dLevel The debug level of the output
     */
    public static void dbgOut(String s, int dLevel) {
        if (dLevel > pOpts_.dbg_level) {
            return;
        }
        if (s == null) {
            s = "NULL";
        }
        if (pOpts_.dbg_flag) {
            errOut("(DEBUG|" + dLevel + ") " + s);
        }
    }

    /**
     * Writes the provided Exception to the errOut stream with the prefix:
     * "ERROR:" and WITHOUT a stack trace called. If you wish to print the stack
     * tace, call {@link #errOut(Exception, boolean)} with printTrace set to
     * TRUE.
     *
     * @param e The Exception to write
     */
    public static void errOut(Exception e) {
        errOut(e, false);
    }

    /**
     * Writes the provided Exception to the errOut stream with the prefix:
     * "ERROR:"
     *
     * @param e The Exception object to write
     * @param printTrace whether or not to print the Exception's stack trace
     * below the error output
     */
    public static void errOut(Exception e, boolean printTrace) {
        if (e == null) {
            errOut("errOut called with null Exception");
        }
        errOut("ERROR: " + e.toString());
        if (!printTrace) {
            return;
        }
        for (StackTraceElement elem : e.getStackTrace()) {
            errOut("TRACE: " + elem.toString());
        }
    }

    /**
     * Writes the provided String to the errOut stream.
     *
     * @param s The message to write out.
     */
    public static void errOut(String s) {
        if (s == null) {
            s = "NULL";
        }
        System.err.println("[" + errDateFormat_.format(new Date()) + "] " + s);
    }

    /**
     * Commits the changes specified by the current {@link ProgramOpts}.
     *
     * @param args The command line arguments that were given to this program
     * (not used in this method)
     */
    protected static void handleArgs(String[] args) {
        if (pOpts_.err_flag) {
            try {
                System.setErr(new PrintStream(args[pOpts_.err_path]));
                dbgOut("ARGS: error out set piped to: " + pOpts_.err_path, 2);
            } catch (FileNotFoundException e) {
                errOut(e);
            }
        }
        if (pOpts_.dbg_flag) {
            dbgOut("ARGS: debug mode enabled at level: " + pOpts_.dbg_level, 2);
        }
        if (pOpts_.lsg_flag) {
            saveGame_ = new SavedGame(args[pOpts_.lsg_path]);
            Exception e = null;

            //int s = saveGame_.loadFile(mmr_, e);
            /*
             if (s == 0) { // the saved game load has failed
             errOut(e);  // print out error
             if (startNewGame() == 0) {
             errOut(e);
             exitGame();
             }
             }*/
        }
        if (pOpts_.editor_flag) {
            map_editor_mode_ = true;
        }
    }

    /**
     * Parses an array of String objects for program options and sets their
     * appropriate values in {@link ProgramOpts}.
     *
     * @param args The command line arguments that were given to this program
     */
    protected static void parseArgs(String[] args) {
        pOpts_ = new ProgramOpts();

        for (int a = 0; a < args.length; a++) {
            // DEBUG
            for (String m : pOpts_.dbg_match) {
                if (m.equals(args[a])) {
                    if (args.length > a + 1) {
                        int temp = Integer.parseInt(args[a + 1]);
                        if (temp > 0) {
                            pOpts_.dbg_level = temp;
                        }
                    }
                    pOpts_.dbg_flag = true;
                    break;
                }
            }

            // LOAD SAVED GAME
            for (String m : pOpts_.lsg_match) {
                if (m.equals(args[a]) && (args.length > a + 1)) {
                    pOpts_.lsg_path = a + 1;
                    // TODO: add line to load saveGame_
                    pOpts_.lsg_flag = true;
                    break;
                }
            }
            // REDIRECT STDERR
            for (String m : pOpts_.err_match) {
                if (m.equals(args[a]) && (args.length > a + 1)) {
                    pOpts_.err_path = a + 1;
                    pOpts_.err_flag = true;
                    break;
                }
            }
            // Map Editing
            for (String m : pOpts_.editor_match) {
                if (m.equals(args[a])) {
                    pOpts_.editor_flag = true;
                    break;
                }
            }
        }
    }
    // </editor-fold>
} // end of Main

