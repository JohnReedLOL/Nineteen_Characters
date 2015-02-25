/**
 * Implementor: Alex Stewart
 * Last Update: 15-02-13
 */
package src;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.text.SimpleDateFormat;
import java.util.Date;

import src.model.map.constructs.AreaEffectItem;
import src.model.map.constructs.Avatar;
import src.io.controller.AvatarController;
import src.model.map.constructs.Item;
import src.model.map.constructs.Smasher;
import src.model.map.constructs.Sneak;
import src.model.map.constructs.Summoner;
import src.model.map.constructs.Terrain;
import src.model.map.MapTile;
import src.io.view.Display;
import src.io.view.Viewport;

/**
 * Initializes, opens the program.
 * @author JohnReedLOL, Alex Stewart
 */
public class RunGame
{
    
    private static ProgramOpts pOpts_ = null;
    private static SavedGame saveGame_;
    private static Avatar avatar_;

    


    public static void main(String[] args) {
        //parseArgs(args); // Parse command line arguments
    		if (args.length != 0) {
    			loadGame("save.dave");
    		}
        initialize(); // Initialize any data we need to before loading
        populateMap();//Add stuff into the map
        startGame(); // Begin the avatarcontroller loop
        //handleArgs(args);
        

        // testing
        //saveGameToDisk();

        //exitGame();
        //initializeEverything();
    }

    private static void loadGame(String file_path) {
        saveGame_ = null;
        mmr_ = new MapMain_Relation();
        mmr_.bindToNewMapOfSize(Viewport.width_ / 2, Viewport.height_); //Can change these later if we so desire. 
    	
    	BufferedReader br = null;
    	try {
    		br = new BufferedReader( new FileReader( file_path ));
    		
    		String line;
    		
    		String name = br.readLine();
    		int x = Integer.parseInt(br.readLine());
    		int y = Integer.parseInt(br.readLine());
            Avatar avatar = new Avatar(name, '☃', 0, 0);
            avatar.setMap(mmr_);
            avatar_ = avatar;
            avatar.switchToMapView();
            avatar.getMapRelation().moveInDirection(x, y);
            avatar.sendInput('5');
    		
    		String occupation = br.readLine();
			if (occupation.equals("Smasher"))
    			avatar_.setOccupation(new Smasher());
			if (occupation.equals("Summoner"))
				avatar_.setOccupation(new Summoner());
			if (occupation.equals("Sneak"))
				avatar_.setOccupation(new Sneak());
			avatar_.setRepresentation(br.readLine().charAt(0));
			
			avatar.getStatsPack().max_life_ = Integer.parseInt(br.readLine());
			avatar.getStatsPack().max_mana_ = Integer.parseInt(br.readLine());
			avatar.getStatsPack().offensive_rating_ = Integer.parseInt(br.readLine());
			avatar.getStatsPack().defensive_rating_ = Integer.parseInt(br.readLine());
			avatar.getStatsPack().armor_rating_ = Integer.parseInt(br.readLine());
			avatar.getStatsPack().lives_left_ = Integer.parseInt(br.readLine());
			avatar.getStatsPack().strength_level_ = Integer.parseInt(br.readLine());
			avatar.getStatsPack().agility_level_ = Integer.parseInt(br.readLine());
			avatar.getStatsPack().intellect_level_ = Integer.parseInt(br.readLine());
			avatar.getStatsPack().hardiness_level_ = Integer.parseInt(br.readLine());
			avatar.getStatsPack().quantity_of_experience_ = Integer.parseInt(br.readLine());
			avatar.getStatsPack().movement_level_ = Integer.parseInt(br.readLine());
			avatar.getStatsPack().moves_left_in_turn_ = Integer.parseInt(br.readLine());
			avatar.getStatsPack().cached_current_level_ = Integer.parseInt(br.readLine());
			avatar.getStatsPack().current_life_ = Integer.parseInt(br.readLine());
			avatar.getStatsPack().current_mana_ = Integer.parseInt(br.readLine());
			avatar.getStatsPack().current_offensive_rating_ = Integer.parseInt(br.readLine());
			avatar.getStatsPack().current_defensive_rating_ = Integer.parseInt(br.readLine());
			avatar.getStatsPack().current_armor_rating_ = Integer.parseInt(br.readLine());
			
			String hasEquipped = br.readLine();
			if (hasEquipped.equals("true")) {
				String equip_name = br.readLine();
				char equip_rep = br.readLine().charAt(0);
				boolean equip_passable = (br.readLine().equals("true") ? true : false);
				Item equipped = new Item(equip_name, equip_rep, equip_passable, true, false);
				equipped.getStatsPack().max_life_ = Integer.parseInt(br.readLine());
				equipped.getStatsPack().max_mana_ = Integer.parseInt(br.readLine());
				equipped.getStatsPack().offensive_rating_ = Integer.parseInt(br.readLine());
				equipped.getStatsPack().defensive_rating_ = Integer.parseInt(br.readLine());
				equipped.getStatsPack().armor_rating_ = Integer.parseInt(br.readLine());
				avatar_.equipInventoryItem(equipped);
			} else {
				br.readLine();
				br.readLine();
				br.readLine();
				br.readLine();
				br.readLine();
				br.readLine();
				br.readLine();
				br.readLine();
			}
			
			String next_name;
			while (!(next_name = br.readLine()).equals("map")) {
				char item_rep = br.readLine().charAt(0);
				boolean item_passable = (br.readLine() == "true" ? true : false);
				Item item = new Item(next_name, item_rep, item_passable, true, false);
				item.getStatsPack().max_life_ = Integer.parseInt(br.readLine());
				item.getStatsPack().max_mana_ = Integer.parseInt(br.readLine());
				item.getStatsPack().offensive_rating_ = Integer.parseInt(br.readLine());
				item.getStatsPack().defensive_rating_ = Integer.parseInt(br.readLine());
				item.getStatsPack().armor_rating_ = Integer.parseInt(br.readLine());
				avatar.addItemToInventory(item);
			}
			
			MapTile[][] map_grid = mmr_.getMyMap().getMapGrid();
			for (int i = 0; i < map_grid.length; i++)
				for (int j = 0; j < map_grid[i].length; j++) {
					int tile_x = Integer.parseInt(br.readLine());
					int tile_y = Integer.parseInt(br.readLine());
					String tile_name = br.readLine();
					char tile_rep = br.readLine().charAt(0);
					String tile_decal_string;
					char tile_decal = ' ';
					boolean has_decal = false;
					if (!(tile_decal_string = br.readLine()).equals("null")) {
						tile_decal = tile_decal_string.charAt(0);
						has_decal = true;
					}
					boolean has_water = (br.readLine()).equals("true") ? true : false;
					boolean has_mountain = (br.readLine()).equals("true") ? true : false;
					if (has_decal) {
						mmr_.addTerrain( new Terrain(tile_name, tile_rep, has_mountain, has_water, tile_decal), tile_x, tile_y );
					} else {
						mmr_.addTerrain( new Terrain(tile_name, tile_rep, has_mountain, has_water), tile_x, tile_y);						
					}
				}
			
			String item_x_string;
			while ((item_x_string = br.readLine()) != null) {
				int item_x = Integer.parseInt(item_x_string);
				int item_y = Integer.parseInt(br.readLine());
				boolean item_isoneshot = br.readLine().equals("true") ? true : false;
				boolean is_aoe_item = false;
				String aoe_item_effect = "null";
				int aoe_power = 0;
				boolean aoe_activated = false;
				if (br.readLine().equals("true")) {
					is_aoe_item = true;
					aoe_item_effect = br.readLine();
					aoe_power = Integer.parseInt(br.readLine());
					aoe_activated = br.readLine().equals("true") ? true : false;
				} else {
					br.readLine();
					br.readLine();
					br.readLine();
				}
				String item_name = br.readLine();
				char item_rep = br.readLine().charAt(0);
				boolean item_viewable = (br.readLine().equals("true") ? true : false);
				boolean item_passable = (br.readLine().equals("true") ? true : false);
				boolean item_pickupable = (br.readLine().equals("true") ? true : false);
				if (is_aoe_item) {
					AreaEffectItem aoe_item = null;
					switch (aoe_item_effect) {
						case "HURT":
							aoe_item = new AreaEffectItem(item_name, item_rep, item_passable, item_pickupable, true, AreaEffectItem.Effect.HURT, aoe_power);
							mmr_.addItem(aoe_item, item_x, item_y);
							break;
						case "HEAL":
							aoe_item = new AreaEffectItem(item_name, item_rep, item_passable, item_pickupable, true, AreaEffectItem.Effect.HEAL, aoe_power);
							mmr_.addItem(aoe_item, item_x, item_y);
							break;
						case "LEVEL":
							aoe_item = new AreaEffectItem(item_name, item_rep, item_passable, item_pickupable, true, AreaEffectItem.Effect.LEVEL, aoe_power);
							mmr_.addItem(aoe_item, item_x, item_y);
							break;
						case "KILL":
							aoe_item = new AreaEffectItem(item_name, item_rep, item_passable, item_pickupable, true, AreaEffectItem.Effect.KILL, aoe_power);
							mmr_.addItem(aoe_item, item_x, item_y);
							break;
						default:
							break;
					}
					if (aoe_activated)
						aoe_item.onWalkOver();
					br.readLine();
					br.readLine();
					br.readLine();
					br.readLine();
					br.readLine();
				} else {
					Item item = new Item(item_name, item_rep, item_passable, item_pickupable, item_isoneshot);
					item.getStatsPack().max_life_ = Integer.parseInt(br.readLine());
					item.getStatsPack().max_mana_ = Integer.parseInt(br.readLine());
					item.getStatsPack().offensive_rating_ = Integer.parseInt(br.readLine());
					item.getStatsPack().defensive_rating_ = Integer.parseInt(br.readLine());
					item.getStatsPack().armor_rating_ = Integer.parseInt(br.readLine());
					mmr_.addItem(item, item_x, item_y);
				}
			}
			
    	} catch (IOException e) {
    		e.printStackTrace();
    	}

        Display _display = new Display(avatar_.getMyView());
        _display.printView();
        startGame();
    }

    // <editor-fold desc="GAME METHODS" defaultstate="collapsed">
    private static void exitGame() {

    }

    private static void initialize() {
        saveGame_ = null;
        mmr_ = new MapMain_Relation();
        mmr_.bindToNewMapOfSize(Viewport.width_ / 2, Viewport.height_); //Can change these later if we so desire. 
        Avatar avatar = new Avatar("avatar", '☃', 0, 0);
        avatar.setMap(mmr_);
        avatar_ = avatar;
        Display _display = new Display(avatar.getMyView());
        _display.printView();


    }
    private static void populateMap(){
        Item equipable = new Item("☂", '☂', true, true, false);
        equipable.getStatsPack().offensive_rating_ += 17;
        
        mmr_.addItem(equipable, 5, 5); // ▨
        for (int y = 0; y < Viewport.height_; ++y) {
            for (int x = 0; x < Viewport.width_ / 2; ++x) {
                Terrain obstacle = new Terrain("land", '▨', false, false);
                if (y == 4) {
                    if (x == 2) {
                        obstacle.addDecal('☠');
                    } else if (x == 6) {
                        obstacle.addDecal('★');
                    } else if (x == 9) {
                        obstacle.addDecal('✚');
                    }
                }
                mmr_.addTerrain(obstacle, x, y);
            }
        }
            // String name, char representation, boolean is_passable,
        // boolean goes_in_inventory, boolean is_one_shot, Effect effect, int power
        AreaEffectItem inflict_pain = new AreaEffectItem("inflict_pain", '♨', true, false,
                true, AreaEffectItem.Effect.HURT, 10);
        mmr_.addItem(inflict_pain, 16, 7);
        
        AreaEffectItem area_heal = new AreaEffectItem("area_heal", '♥', true, false,
                false, AreaEffectItem.Effect.HEAL, 10);
        mmr_.addItem(area_heal, 12, 12);
        
        AreaEffectItem area_kill = new AreaEffectItem("area_kill", '☣', true, false,
                true, AreaEffectItem.Effect.KILL, 10);
        mmr_.addItem(area_kill, 3, 11);
        
        AreaEffectItem area_level = new AreaEffectItem("area_level", '↑', true, false,
                true, AreaEffectItem.Effect.LEVEL, 10);
        mmr_.addItem(area_level, 11, 5);
        

        Terrain mountain = new Terrain("boulder", '▲', true, false);
        mmr_.addTerrain(mountain, 2, 2);
        
        Terrain water = new Terrain("water", '~', false, true);
        mmr_.addTerrain(water, 5, 2);
    }
    private static void startGame(){
    	AvatarController AC = new AvatarController(avatar_);
    	AC.runTheGame();
    }
    private static void saveGameToDisk() {
        if (saveGame_ == null) {
            saveGame_ = SavedGame.newSavedGame();
        }
        Exception e = null;

        if (e != null)
            errOut(e);
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
     * be triggered via command line arguments. Reference {@link #parseArgs}
     * for parsing implementation.
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
     * Writes the provided String to the errOut stream with the prefix: (DEBUG|0).
     * @param s The String to write.
     */
    public static void dbgOut(String s) {
        if (s == null) s = "NULL";
        if (pOpts_.dbg_flag)
            errOut("(DEBUG|0) " + s);
    }

    /**
     * Writes the provided String to the errOut stream with the prefix: (DEBUG|X) where X is the debug level of the
     * output. If the provided debug level is greater (larger number) than the debug level option set in
     * {@link ProgramOpts}, the debug string will not be output.
     * @param s The debug string to write
     * @param dLevel The debug level of the output
     */
    public static void dbgOut(String s, int dLevel) {
        if (dLevel > pOpts_.dbg_level)
            return;
        if (s == null) s = "NULL";
        if (pOpts_.dbg_flag)
            errOut("(DEBUG|" + dLevel + ") " + s);
    }

    /**
     * Writes the provided Exception to the errOut stream with the prefix: "ERROR:" and WITHOUT a stack trace called.
     * If you wish to print the stack tace, call {@link #errOut(Exception, boolean)} with printTrace set to TRUE.
     * @param e The Exception to write
     */
    public static void errOut(Exception e) {
        errOut(e, false);
    }

    /**
     * Writes the provided Exception to the errOut stream with the prefix: "ERROR:"
     * @param e The Exception object to write
     * @param printTrace whether or not to print the Exception's stack trace below the error output
     */
    public static void errOut(Exception e, boolean printTrace) {
        if (e == null) {
            errOut("errOut called with null Exception");
        }
        errOut("ERROR: " + e.toString());
        if (!printTrace)
            return;
        for (StackTraceElement elem : e.getStackTrace()) {
            errOut("TRACE: " + elem.toString());
        }
    }

    /**
     * Writes the provided String to the errOut stream.
     * @param s The message to write out.
     */
    public static void errOut(String s) {
        if (s == null) s = "NULL";
        System.err.println("[" + errDateFormat_.format(new Date()) + "] " + s);
    }

    /**
     * Commits the changes specified by the current {@link ProgramOpts}.
     * @param args The command line arguments that were given to this program (not used in this method)
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
     * Parses an array of String objects for program options and sets their appropriate values in {@link ProgramOpts}.
     * @param args The command line arguments that were given to this program
     */
    protected static void parseArgs(String[] args) {
        pOpts_ = new ProgramOpts();

        for (int a = 0; a < args.length; a++) {
            // DEBUG
            for (String m : pOpts_.dbg_match) {
                if (m.equals(args[a])) {
                    if (args.length > a + 1) {
                        int temp = Integer.parseInt(args[a+1]);
                        if (temp > 0)
                            pOpts_.dbg_level = temp;
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
        }
    }
    // </editor-fold>
} // end of Main
