package src;

import java.awt.Color;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.text.SimpleDateFormat;
import java.util.Date;

import src.Not_part_of_iteration_2_requirements.BONUS.MapEditor.MapAddable;
import src.Not_part_of_iteration_2_requirements.BONUS.MapEditor.MapAddableFactory;
import src.io.controller.Controller;
import src.io.controller.GameController;
import src.io.controller.MapEditorController;
import src.model.Map;
import src.model.constructs.Avatar;
import src.model.constructs.DrawableThingStatsPack;
import src.model.constructs.Merchant;
import src.model.constructs.Monster;
import src.model.constructs.Terrain;
import src.model.constructs.Villager;
import src.model.constructs.items.Bow;
import src.model.constructs.items.Item;
import src.model.constructs.items.KnightsSerum;
import src.model.constructs.items.InvisibilitySerum;
import src.model.constructs.items.ObstacleRemovingItem;
import src.model.constructs.items.OneHandedSword;
import src.model.constructs.items.OneShotAreaEffectItem;
import src.model.constructs.items.OneWayTeleportItem;
import src.model.constructs.items.PermanentObstacleItem;
import src.model.constructs.items.Shield;
import src.model.constructs.items.SpreadingCircleAreaEffectItem;
import src.model.constructs.items.SpreadingConeAreaEffectItem;
import src.model.constructs.items.SpreadingLineAreaEffectItem;
import src.model.constructs.items.Staff;
import src.model.constructs.items.TemporaryObstacleItem;
import src.model.constructs.items.Trap;
import src.model.constructs.items.TwoHandedSword;

/**
 * Initializes, opens the program.
 *
 * @author JohnReedLOL, Alex Stewart
 */
public class RunGame {

    private static ProgramOpts pOpts_ = null;
    private static Avatar avatar_;
    private static final String avatar_name = "avatar";
    private static Map map_;
    private static Controller uc_;
    private static int mapHeight_ = 20;
    private static int mapWidth_ = 35;
    private static boolean map_editor_mode_ = false;
    private static StringBuilder newUserName_ = new StringBuilder();

    /**
     * Get the current file name, method name, and line number.
     *
     * @return int - Current line number.
     */
    public static String getLineNumber() {
        StackTraceElement stack_trace = Thread.currentThread().getStackTrace()[2];
        return "\n" + stack_trace.getFileName().replace(".java", "") + " " 
                + "" + stack_trace.getMethodName() + "() " +
                stack_trace.getLineNumber() + "\n";
    }

    /**
     * Takes in a new [not yet started] map thread. Kills the old one and
     * replaces it.
     *
     * @param map new replacement map
     */
    public static void setNewMap(Map map) {
        grusomelyKillTheMap();
        try {
            Thread.sleep(10);
        } catch (InterruptedException e) {
            System.err.println("This interrupted exception should never ever happen");
            System.exit(77);
        }
        map_ = map;
        map_.start();
        map_ = map;
    }

    public static void grusomelyKillTheMap() {
        if (RunGame.map_ != null) {
            //map_.grusomelyKillTheMapThread();
            map_.interrupt();
            System.out.println("Killed the map thread");
        } else {
            System.out.println("The map thread is null");
        }
    }

    public static void grusomelyKillTheController() {
        if (RunGame.uc_ != null) {
            uc_.grusomelyKillTheControllerThread();
            System.out.println("Killed the controller thread");
        } else {
            System.out.println("The controller thread is null");
        }
    }

    public static void closeGame() {
        grusomelyKillTheMap();
        grusomelyKillTheController();
    }

    public static String getAvatarName() {
        return avatar_.name_;
    }

    public static void main(String[] args) {
        parseArgs(args); // Parse command line arguments
        handleArgs(args);
        if (!pOpts_.lsg_flag) {
            if (map_editor_mode_) {
                startMapEditor();
            }

            if (map_ == null) {
                initialize();
                populateMap();
            }
        }
        startGame();
    }

    private static int startNewGame() {

        initialize(); // Initialize any data we need to before loading
        populateMap();//Add stuff into the map

        startGame(); // Begin the avatarcontroller loop
        //try {
        //    Thread.sleep(2000);
        //} catch (InterruptedException e) {
        //    e.printStackTrace();
        //}

        return 0;
    }

    private static int startMapEditor() {
        initialize(); // Initialize any data we need to before loading
        coverMapInGrass(map_);
        uc_ = new MapEditorController(map_);
        (new Thread(uc_)).start();
        return 0;
    }

    private static void coverMapInGrass(Map map_2) {
        MapAddableFactory factory = new MapAddableFactory();
        for (int x = 0; x < map_2.width_; ++x) {
            for (int y = 0; y < map_2.height_; ++y) {
                MapAddable addable = factory.getAddable(AddableThingEnum.GRASS_TERRAIN);
                addable.addToMap(map_2, x, y);

            }
        }
    }

    public static void loadGame(String file_path) {
        /*
         Map newMap = SavedGame.loadGame(file_path);
         if (newMap == null) {
         RunGame.errOut("Failed to load the map from: " + file_path);
         return;
         }*/
        //map_ = newMap;
    }

    // <editor-fold desc="GAME METHODS" defaultstate="collapsed">
    private static void exitGame() {

    }

    private static void initialize() {
        map_ = new Map(mapWidth_, mapHeight_);
        map_.start();
    }

    private static void populateMap() {
        avatar_ = new Avatar(avatar_name, '☃');
        // map_.addAsAvatar(avatar_, 0, 0);
        map_.addAsAvatar(avatar_, mapWidth_ / 2 - 1, mapHeight_ / 2 - 1);

        Avatar buddy = new Avatar("buddy", '☺');
        // map_.addAsAvatar(buddy, 3, 0);
        map_.addAsKnight(buddy, 3, 0); // buddy can jump over entities!

        Villager villager1 = new Villager("villager1", '♙');
        villager1.getStatsPack().increaseQuantityOfExperienceBy(200);
        map_.addAsEntity(villager1, 3, 13);

        Monster strong = new Monster("monster1", '♟');
        strong.getStatsPack().increaseQuantityOfExperienceBy(400);
        strong.getStatsPack().increaseDefenseLevelByOne();
        strong.getStatsPack().increaseHardinessLevelByOne();
        map_.addAsEntity(strong, 13, 3);

        Merchant merchant = new Merchant("merchant1", '♖');
        merchant.getStatsPack().increaseQuantityOfExperienceBy(1000);
        map_.addAsEntity(merchant, 1, 1);
        Item teleport1 = new OneWayTeleportItem("tele1", 'T', 6, 6);
        Item teleport2 = new OneWayTeleportItem("tele2", 'T', 12, 12);
        map_.addItem(teleport2, 5, 6);
        map_.addItem(teleport1, 11, 12);

        //Add The one handed swords
        Item onehandedsword = new OneHandedSword("Excalibur", '†');
        onehandedsword.getStatsPack().addOn(new DrawableThingStatsPack(50, 0));
        Item SteelSword = new OneHandedSword("Steel Sword", '†');//Base sword, no need to boost stats. 
        Item BlackSword = new OneHandedSword("Black Sword", '†');
        BlackSword.getStatsPack().addOn(new DrawableThingStatsPack(3, 0));

        map_.addItem(SteelSword, 5, 5);
        map_.addItem(BlackSword, 6, 5);
        map_.addItem(onehandedsword, 7, 5);
        //Two handed weapon.
        Item twohandedsword = new TwoHandedSword("Two hander", '|');//Base sword, no need to raise stats. 
        Item LightningSword = new TwoHandedSword("Lightning Sword", '⚡');
        LightningSword.getStatsPack().addOn(new DrawableThingStatsPack(15, 0));//Heavy damage
        Item BoatAnchor = new TwoHandedSword("Boat Anchor", '⚓');
        BoatAnchor.getStatsPack().addOn(new DrawableThingStatsPack(20, 0));//Heavy Heavy damange
        map_.addItem(twohandedsword, 25, 1);
        map_.addItem(LightningSword, 25, 2);
        map_.addItem(BoatAnchor, 25, 3);

        //UnArmed Weps.
        Item spiked_gauntlet = new Shield("Spiked Gauntlet", '♕');
        spiked_gauntlet.getStatsPack().addOn(new DrawableThingStatsPack(10, 0));
        Item radiationEmittingGloves = new Shield("Radiation Gloves", '☣');
        radiationEmittingGloves.getStatsPack().addOn(new DrawableThingStatsPack(20, 1));
        Item atomicFists = new Shield("Atomic Fists", '⚛');
        atomicFists.getStatsPack().addOn(new DrawableThingStatsPack(30, 2));
        map_.addItem(spiked_gauntlet, 28, 1);
        map_.addItem(radiationEmittingGloves, 28, 2);
        map_.addItem(atomicFists, 28, 3);

        Item shield = new Shield("Shieldy", 'O');
        shield.getStatsPack().addOn(new DrawableThingStatsPack(0, 10));
        OneShotAreaEffectItem heal = new OneShotAreaEffectItem("healer", 'h', Effect.HEAL, 10);
        OneShotAreaEffectItem hurt = new OneShotAreaEffectItem("hurter", 'u', Effect.HURT, 2);
        OneShotAreaEffectItem kill = new OneShotAreaEffectItem("killer", 'k', Effect.KILL, 10);
        OneShotAreaEffectItem level = new OneShotAreaEffectItem("leveler", 'l', Effect.LEVEL, 10);

        KnightsSerum knight_serum = new KnightsSerum("Knight serum", 'N');
        map_.addItem(knight_serum, 18, 10);

        InvisibilitySerum flying_serum = new InvisibilitySerum("Invisibility Serum", 'I');
        map_.addItem(flying_serum, 14, 10);

        ObstacleRemovingItem key = new ObstacleRemovingItem("Key", 'K');
        TemporaryObstacleItem door = new TemporaryObstacleItem("Door", 'D', key);
        map_.addItem(key, 11, 0);
        map_.addItem(door, 13, 0);

        map_.addItem(heal, 3, 2);
        map_.addItem(hurt, 6, 2);
        map_.addItem(kill, 9, 2);
        map_.addItem(level, 12, 2);

        //Add some traps
        Trap trap1 = new Trap("trap1", 'b', Effect.HURT, 5);
        map_.addItem(trap1, 1, 0);

        Trap trap2 = new Trap("trap2", 'b', Effect.HURT, 5);
        map_.addItem(trap2, 2, 0);

        map_.addItem(shield, 10, 7);
        //Three Weapons : Ranged

        Bow bow = new Bow("Bow", 'B');//Base ranged, no need to change.
        Bow ThrowStar = new Bow("Throwing Star", '✪');
        ThrowStar.getStatsPack().addOn(new DrawableThingStatsPack(2, 0));
        Bow Coffee_SquirtGun = new Bow("Coffee Squirt Gun", '☕');
        Coffee_SquirtGun.getStatsPack().addOn(new DrawableThingStatsPack(1 - Coffee_SquirtGun.getStatsPack().getOffensive_rating_(), 1));
        map_.addItem(Coffee_SquirtGun, 34, 17);
        map_.addItem(ThrowStar, 33, 17);
        map_.addItem(bow, 32, 17);

        Staff staff = new Staff("Staff", '⚚');
        bow.getStatsPack().incrementOffensive_rating_();
        staff.getStatsPack().incrementOffensive_rating_();

        map_.addItem(staff, 20, 6);

        for (int y = 0; y < mapHeight_; ++y) {
            for (int x = 0; x < mapWidth_; ++x) {
                Terrain land = new Terrain("land", '▨', false, false);
                if (y == 4) {
                    if (x == 2) {
                        land.addDecal('☠', Color.black);
                    } else if (x == 6) {
                        land.addDecal('★', Color.yellow);
                    } else if (x == 9) {
                        land.addDecal('✚', Color.red);
                    }
                }
                map_.addTerrain(land, x, y);
            }
        }

        for (int x = 0; x < mapWidth_; ++x) {
            Terrain river = new Terrain("blue_river", '~', true, false);
            map_.addTerrain(river, x, 18);
        }
        // this should be gray
        Terrain mountain = new Terrain("gray_mountain", '\u25B2', false, true);
        map_.addTerrain(mountain, 20, 14);
        map_.addTerrain(mountain, 21, 13);
        map_.addTerrain(mountain, 22, 12);
        // this should be cyan
        Terrain cyan_mountain = new Terrain("cyan_mountain", '\u25B2', true, true);
        map_.addTerrain(cyan_mountain, 23, 11);

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
        if (map_ == null) {
            RunGame.errOut("startGame(): invalid (null) map");
            return;
        }
        /*
         if (avatar_ == null) {
         RunGame.errOut("startGame(): invalid (null) avatar");
         return;
         }*/
        uc_ = new GameController(map_, avatar_name);
        (new Thread(uc_)).start();
    }

    public static void saveGameToDisk(String filepath) {
        SavedGame.saveGame(filepath, map_, uc_.getUserName()); // save game to file "foo"
    }

    // </editor-fold>
    // <editor-fold desc="UTILITIES" defaultstate="collapsed">
    // Error date format for the errOut(Exception) write
    private static SimpleDateFormat errDateFormat_ = new SimpleDateFormat("yyyy-MM-dd HH:mm:ssZ");

    /* DEBUG LEVEL GUIDELINES
     * 1 - Basic program functionality (application functions and important checkpoints)
     * 2 - Sublevel game and application methods (such as save/load)
     * 3 - Sublevel key details (those which are infrequent and important)
     * 4 - Method details which are not important except for debugging and are otherwise extraneous
     * 5 - Iterator variables and other such information which is likely to fill up a log file if enabled
     * 6 - Special debug level for UDP packet output
     */
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

        String[] editor_match = {"-e", "--editor"};
        boolean editor_flag = false;

        // Load Saved Game
        String[] lsg_match = {"-l", "--load"}; // option flag match string
        boolean lsg_flag = false; // whether or not to load the game
        int lsg_path = -1; // the index in args to get the path from

        // Redirect STDERR
        String[] err_match = {"--error-out"};
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
     * <strong>DEPRECIATED! DO NOT USE!</strong>
     *
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
                dbgOut("ARGS: error out set piped to: " + args[pOpts_.err_path], 2);
            } catch (FileNotFoundException e) {
                errOut(e);
            }
        }
        if (pOpts_.dbg_flag) {
            dbgOut("ARGS: debug mode enabled at level: " + pOpts_.dbg_level, 2);
        }
        if (pOpts_.lsg_flag) {
            Map tmp_map = SavedGame.loadGame(args[pOpts_.lsg_path], newUserName_); // attempt to load the saved game
            if (tmp_map == null) // if the load has failed, log that
            {
                RunGame.errOut("MAIN: Could not load map from: " + args[pOpts_.lsg_path]);
            } else {
                RunGame.dbgOut("Game loaded from arguments", 3);
                setNewMap(tmp_map); // otherwise, apply the loaded map
            }
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

