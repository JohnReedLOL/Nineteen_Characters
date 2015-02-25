/*
 * Implementor: Alex Stewart
 * Last Update: 15-02-13
 */
package src;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;

import src.model.map.constructs.AreaEffectItem;
import src.model.map.constructs.Avatar;
import src.model.map.constructs.Item;
import src.model.map.Map;
import src.model.map.MapTile;


/**
 * This class manages a saved game object. A saved game has a file path and 
 * methods to interact with that file (loading and saving data).
 * 
 * @author Alex Stewart
 */
public class SavedGame {
    private String file_path_ = null;
    private ArrayList<String> array_list;

    public static final SimpleDateFormat SAVE_DATE_FORMAT = new SimpleDateFormat("yyMMdd");
    /**
     * SAVE_DATA_VERSION is the data format version number. It should be 
     * incremented any time that the serialization or deserialization sequence 
     * is modified. The version number 0 is reserved. This value has no 
     * relation to the Java native Serialization object ID.
     */
    public static final long SAVE_DATA_VERSION = 1;
    public static final String SAVE_EXT = ".sav";
    public static final char SAVE_ITERATOR_FLAG = '_';
    private static final String SAVE_EOF_STRING = "///END OF FILE///";
    // SAVE FILE FORMAT: yyMMdd_<number>.sav
    
    public SavedGame(String filePath) {
        file_path_ = filePath;
    }

    public int saveGame(Avatar my_avatar) {
    	BufferedWriter bw = null;
    	
    	try {
    		bw = new BufferedWriter( new FileWriter( file_path_ ) );
    		StringBuilder sb = new StringBuilder();
    		
    		sb.append(my_avatar.name_ + "\n");
    		sb.append(my_avatar.getMapRelation().getMyXCoordinate() + "\n");
    		sb.append(my_avatar.getMapRelation().getMyYCoordinate() + "\n");
    		sb.append(my_avatar.getOccupation().toString() + "\n");
    		sb.append(my_avatar.getRepresentation() + "\n");
    		sb.append(my_avatar.getStatsPack().max_life_ + "\n");
    		sb.append(my_avatar.getStatsPack().max_mana_ + "\n");
    		sb.append(my_avatar.getStatsPack().offensive_rating_ + "\n");
    		sb.append(my_avatar.getStatsPack().defensive_rating_ + "\n");
    		sb.append(my_avatar.getStatsPack().armor_rating_ + "\n");
    		sb.append(my_avatar.getStatsPack().lives_left_ + "\n");
    		sb.append(my_avatar.getStatsPack().strength_level_ + "\n");
    		sb.append(my_avatar.getStatsPack().agility_level_ + "\n");
    		sb.append(my_avatar.getStatsPack().intellect_level_ + "\n");
    		sb.append(my_avatar.getStatsPack().hardiness_level_ + "\n");
    		sb.append(my_avatar.getStatsPack().quantity_of_experience_ + "\n");
    		sb.append(my_avatar.getStatsPack().movement_level_ + "\n");
    		sb.append(my_avatar.getStatsPack().moves_left_in_turn_ + "\n");
    		sb.append(my_avatar.getStatsPack().cached_current_level_ + "\n");
    		sb.append(my_avatar.getStatsPack().current_life_ + "\n");
    		sb.append(my_avatar.getStatsPack().current_mana_ + "\n");
    		sb.append(my_avatar.getStatsPack().current_offensive_rating_ + "\n");
    		sb.append(my_avatar.getStatsPack().current_defensive_rating_ + "\n");
    		sb.append(my_avatar.getStatsPack().current_armor_rating_ + "\n");
    		Item equipped = my_avatar.getEquipped();
    		if (equipped != null) {
    			sb.append("true\n");
    			sb.append(equipped.name_ + "\n");
    			sb.append(equipped.getRepresentation() + "\n");
    			sb.append(equipped.isPassable() ? "true\n" : "false\n");
    			sb.append(equipped.getStatsPack().max_life_ + "\n");
    			sb.append(equipped.getStatsPack().max_mana_  + "\n");
    			sb.append(equipped.getStatsPack().offensive_rating_ + "\n");
    			sb.append(equipped.getStatsPack().defensive_rating_ + "\n");
    			sb.append(equipped.getStatsPack().armor_rating_ + "\n");
    		} else
    			sb.append("false\nnull\nnull\nnull\nnull\nnull\nnull\nnull\nnull\n");
    		ArrayList<Item> inventory = my_avatar.getInventory();
    		for (Item item : inventory) {
    			sb.append(item.name_ + "\n");
    			sb.append(item.getRepresentation() + "\n");
    			sb.append(item.isPassable() ? "true\n" : "false\n");
    			sb.append(item.getStatsPack().max_life_ + "\n");
    			sb.append(item.getStatsPack().max_mana_  + "\n");
    			sb.append(item.getStatsPack().offensive_rating_ + "\n");
    			sb.append(item.getStatsPack().defensive_rating_ + "\n");
    			sb.append(item.getStatsPack().armor_rating_ + "\n");
    		}
    		sb.append("map\n");
    		Map map_reference = my_avatar.getMapRelation().getMap();
    		MapTile[][] map_grid = map_reference.getMapGrid();
    		for (int i = 0; i < map_grid.length; i++) {
    			for (int j = 0; j < map_grid[i].length; j++) {
    				MapTile tile = map_grid[i][j];
    				sb.append(tile.x_ + "\n");
    				sb.append(tile.y_ + "\n");
    				sb.append(tile.getTerrain().name_ + "\n");
    				sb.append(tile.getTerrain().getRepresentation() + "\n");
    				sb.append(tile.getTerrain().hasDecal() ? tile.getTerrain().getDecal()+"\n" : "null\n");
    				sb.append(tile.getTerrain().hasWater() + "\n");
    				sb.append(tile.getTerrain().hasMountain() + "\n");
    			}
    		}
    		LinkedList<Item> items_list = map_reference.getItemsList();
    		for (Item item : items_list) {
    			sb.append(item.getMapRelation().getMyXCoordinate() + "\n");
    			sb.append(item.getMapRelation().getMyYCoordinate() + "\n");
    			sb.append(item.isOneShot() ? "true\n" : "false\n");
    			if (item instanceof AreaEffectItem) {
    				sb.append("true\n");
    				sb.append(((AreaEffectItem)item).getEffect() + "\n");
    				sb.append(((AreaEffectItem)item).getPower() + "\n");
    				sb.append(((AreaEffectItem)item).hasBeenActivated() ? "true\n" : "false\n");
    			} else
    				sb.append("false\n0\n0\n0\n");
    			sb.append(item.name_ + "\n");
    			sb.append(item.getRepresentation() + "\n");
				sb.append(item.getViewable() ? "true\n" : "false\n");
				sb.append(item.isPassable() ? "true\n" : "false\n");
    			sb.append(item.goesInInventory() ? "true\n" : "false\n");
    			sb.append(item.getStatsPack().max_life_ + "\n");
    			sb.append(item.getStatsPack().max_mana_  + "\n");
    			sb.append(item.getStatsPack().offensive_rating_ + "\n");
    			sb.append(item.getStatsPack().defensive_rating_ + "\n");
    			sb.append(item.getStatsPack().armor_rating_ + "\n");
    		}
    		bw.write(sb.toString());
    		bw.close();
    	} catch (Exception e) {
    		e.printStackTrace();
    	} finally {
    		try {
				bw.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
    	}
    	
        return 0;
    }



    /**
     * Generates a new SavedGame object with the file path set to the next available save game file path in the current
     * working directory.
     * @return The new SavedGame object
     */
    public static SavedGame newSavedGame() {
        String pwd = System.getProperty("user.dir"); // get the current working directory
        return newSavedGame(pwd);
    }

    public static SavedGame newSavedGame(String directory) {
        RunGame.dbgOut("New save game requested for dir: " + directory);
        String date = SAVE_DATE_FORMAT.format(new Date()); // get the current date string

        // Search the current directory for existing saves and keep an iterator to append the save name with a unique
        // ID for this day.
        int iterator = 1;
        try {
            File dir = new File(System.getProperty("user.dir"));
            File[] files = dir.listFiles();
            String s_buff;

            int i_buff;
            for (File f : files) { // Search files in directory
                if (f.getName().endsWith(SavedGame.SAVE_EXT)) { // for save files...
                    s_buff = f.getName(); // temprorarily store the filename
                    if(!s_buff.startsWith(date))
                        continue; // if the save isn't from this date, ignore it
                    s_buff = s_buff.substring(s_buff.lastIndexOf('_') + 1, s_buff.lastIndexOf(".")); // otherwise, get the ID
                    i_buff = Integer.parseInt(s_buff);
                    if (i_buff >= iterator) iterator = i_buff + 1; // ensure that the iterator is always ahead by 1
                }
            }
        } catch (Exception e) {
            RunGame.errOut(e);
        }
        // iterator is now the correct unique ID
        // ready to construct path
        String path = date + SAVE_ITERATOR_FLAG + iterator + SAVE_EXT;
        return new SavedGame(path);
    }
}
