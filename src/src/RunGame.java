package src;

import java.awt.Color;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.text.SimpleDateFormat;
import java.util.Date;

import src.model.map.constructs.Avatar;
import src.io.controller.ChatBoxController;
import src.io.controller.UserController;
import src.model.map.constructs.Bow;
import src.model.map.constructs.Item;
import src.model.map.constructs.Merchant;
import src.model.map.constructs.OneHandedSword;
import src.model.map.constructs.PickupableItem;
import src.model.map.constructs.Sheild;
import src.model.map.constructs.Terrain;
import src.model.map.constructs.Villager;
import src.model.map.Map;
import src.io.view.Viewport;
import src.model.map.MapUser_Interface;
import src.model.map.constructs.OneShotAreaEffectItem;
import src.model.map.constructs.OneWayTeleportItem;
import src.model.map.constructs.TwoHandedSword;

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
    private static UserController uc_;
    private static ChatBoxController cc_;
    private static int mapHeight_ = 40;
    private static int mapWidth_ = 40;

    public static void main(String[] args) {
        parseArgs(args); // Parse command line arguments
        handleArgs(args);

        initialize(); // Initialize any data we need to before loading
        populateMap();//Add stuff into the map
        startGame(); // Begin the avatarcontroller loop

        // testing
        //saveGameToDisk();
        //exitGame();
        //initializeEverything();
    }

    private static void loadGame(String file_path) {

    }

    // <editor-fold desc="GAME METHODS" defaultstate="collapsed">
    private static void exitGame() {

    }

    private static void initialize() {
        if (saveGame_ == null)
            saveGame_ = SavedGame.newSavedGame();
        map_ = new Map(mapWidth_,mapHeight_);
        avatar_ = new Avatar("avatar", '☃');
        map_.addAvatar(avatar_, 0, 0);


    }

    private static void populateMap() {
        Item teleport = new OneWayTeleportItem("tele", 'T', 0, 0);
        Item onehandedsword = new OneHandedSword("Excalibur", '|');
        Item twohandedsword = new TwoHandedSword("Two_hander", '|');
        Item sheild = new Sheild("Sheildy",'O');
        OneShotAreaEffectItem heal = new OneShotAreaEffectItem("healer", 'h', Effect.HEAL, 10);
        OneShotAreaEffectItem hurt = new OneShotAreaEffectItem("hurter", 'u', Effect.HURT, 10);
        OneShotAreaEffectItem kill = new OneShotAreaEffectItem("killer", 'k', Effect.KILL, 10);
        OneShotAreaEffectItem level = new OneShotAreaEffectItem("leveler", 'l', Effect.LEVEL, 10);
        
        map_.addItem(heal, 3, 2);
        map_.addItem(hurt, 6, 2);
        map_.addItem(kill, 9, 2);
        map_.addItem(level, 12, 2);
        
        //These Items will be added to the Merchants shop.
        Item longSword = new OneHandedSword("Long Sword", '|');
        Item shield = new Sheild("Shield", 'O');
        Item bow = new Bow("bow",')');
        //Making Merchant and adding items to his shop.
        Merchant merchant = new Merchant("Merchant", 'M'); 
        merchant.addItemToBeSold((PickupableItem)longSword);
        merchant.addItemToBeSold((PickupableItem)shield);
        merchant.addItemToBeSold((PickupableItem)bow);
        map_.addEntity(merchant,25, 3);
        
        
        //Adding a Villager.
        Villager villager =new Villager("Tom", 'V');
        map_.addEntity(villager,0,5);
        
        //seven.getStatsPack().offensive_rating_ = 17; //Can no longer do this.
        map_.addItem(teleport, 2, 4);
        map_.addItem(twohandedsword, 1, 1);
        map_.addItem(sheild,10,10);
        map_.addItem(onehandedsword, 5,5);
        for (int y = 0; y < mapHeight_; ++y) {
            for (int x = 0; x < mapWidth_; ++x) {
                Terrain obstacle = new Terrain("land", '▨', false, false);
                if (y == 4) {
                    if (x == 2) {
                        obstacle.addDecal('☠',Color.black);
                    } else if (x == 6) {
                        obstacle.addDecal('★',Color.yellow);
                    } else if (x == 9) {
                        obstacle.addDecal('✚',Color.red);
                    }
                }
                map_.addTerrain(obstacle, x, y);
            }
        }

        Terrain obstacle = new Terrain("boulder", '■', true, false);
        map_.addTerrain(obstacle, 2, 2);

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
            ((MapUser_Interface) map_).sendCommandToMap("avatar", 'x', 10, 20);
        }

    }

    private static void startGame() {
        uc_ = new UserController(map_,avatar_.name_);
        cc_ = new ChatBoxController();
        
    }

    public static void saveGameToDisk() {
        if (saveGame_ == null) {
            saveGame_ = SavedGame.newSavedGame();
        }
        saveGame_.saveGame(map_, uc_);
    }

    // TODO: complete
    private static int startNewGame() {
        return 0;
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
        boolean dbg_flag = false;
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
        }
    }
    // </editor-fold>
} // end of Main
