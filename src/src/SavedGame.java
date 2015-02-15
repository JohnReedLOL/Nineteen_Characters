/*
 * Implementor: Alex Stewart
 * Last Update: 15-02-13
 */
package src;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

import src.model.MapMain_Relation;

/**
 * This class manages a saved game object. A saved game has a file path and 
 * methods to interact with that file (loading and saving data).
 * 
 * @author Alex Stewart
 */
public class SavedGame {
    private String filePath_ = null;

    public static final SimpleDateFormat SAVE_DATE_FORMAT = new SimpleDateFormat("yyMMdd");
    /**
     * SAVE_DATA_VERSION is the data format version number. It should be 
     * incremented any time that the serialization or deserialization sequence 
     * is modified. The version number 0 is reserved. This value has no 
     * relation to the Java native Serialization object ID.
     */
    public static final int SAVE_DATA_VERSION = 1;
    public static final String SAVE_EXT = ".sav";
    public static final char SAVE_ITERATOR_FLAG = '_';
    
    /** DATA FORMAT VERSION: 1
     * =======================
     * GENERAL:
     * int              VERSION NUMBER
     * -----------------------
     * MAP_DATA:
     * int      m.w     Map Width
     * int      m.h     Map Height
     * MapTile          Map Tiles[m.w][m.h]
     * LinkedHashMap    Avatar List
     * LinkedHashMap    Entity List
     * LinkedList       Items List
     * -----------------------
     * EOF
     */
    
    public SavedGame(String filePath) {
        filePath_ = filePath;
    }
    
    public int loadFile(MapMain_Relation mapRel, Exception exep) {
        Main.dbgOut("Save game load requested from: " + filePath_);
        exep = null; // First, set the out Exception variable to null
        // Error checking
        if (filePath_ == null) {
            exep = new Exception("Provided filepath is NULL");
            return 0;
        }
        if (mapRel == null) {
            exep = new Exception("Invalid map relationship provided");
            return 0;
        }
        
        // Attempt to open the file and read the map object
        try
        {
            FileInputStream fis = new FileInputStream(filePath_);
            ObjectInputStream ois = new ObjectInputStream(fis);
            
            // Check the version number
            if (ois.readInt() != SAVE_DATA_VERSION)
                throw new IOException("Invalid save file version number");
            
            // Hand stream over to MapMain_Relation to deserialize Map
            mapRel = MapMain_Relation.deserializeMap(ois); // Populate the map object
            ois.close();
            fis.close();
        }
        catch (Exception e)
        {
            exep = e;
            return 0;
        }
        
        return 1;
    }
    
    public int saveFile(MapMain_Relation mapRel, Exception exep) {
        exep = null; // first, set the out Exception variable to null
        Main.dbgOut("Saving Game File to: " + filePath_);
        try {
            FileOutputStream fos = new FileOutputStream(filePath_, false);
            ObjectOutputStream oos = new ObjectOutputStream(fos);

            oos.writeInt(SAVE_DATA_VERSION); // write the save version
            mapRel.serializeMap(oos); // serialize the map

            oos.close();
            fos.close();
            return 1;
        } catch (Exception e) {
            Main.errOut(e, true);
            return 0;
        }
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
        Main.dbgOut("New save game requested for dir: " + directory);
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
                    s_buff.substring(s_buff.lastIndexOf('_') + 1, s_buff.lastIndexOf(".")); // otherwise, get the ID
                    i_buff = Integer.parseInt(s_buff);
                    if (i_buff >= iterator) iterator = i_buff + 1; // ensure that the iterator is always ahead by 1
                }
            }
        } catch (Exception e) {
            Main.errOut(e);
        }
        // iterator is now the correct unique ID
        // ready to construct path
        String path = date + SAVE_ITERATOR_FLAG + iterator + SAVE_EXT;
        return new SavedGame(path);
    }
}