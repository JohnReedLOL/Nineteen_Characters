/*
 * Implementor: Alex Stewart
 * Last Update: 15-02-13
 */
package src;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

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
    public static final long SAVE_DATA_VERSION = 2;
    public static final String SAVE_EXT = ".xml";
    public static final char SAVE_ITERATOR_FLAG = '_';
    private static final String SAVE_EOF_STRING = "///END OF FILE///";
    // SAVE FILE FORMAT: yyMMdd_<number>.sav
    
    public SavedGame(String filePath) {
        file_path_ = filePath;
    }

    public int saveGame(src.model.Map map, src.io.controller.Controller controller,String foo) {
        try {
            // open or create the save file
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            dbf.setNamespaceAware(false);
            DocumentBuilder docBuilder = dbf.newDocumentBuilder();

            Document save = docBuilder.newDocument();
            Element root = save.createElement("save_game");
            save.appendChild(root);

            Element e_version = save.createElement("version");
            e_version.appendChild(save.createTextNode(Long.toString(SAVE_DATA_VERSION)));
            root.appendChild(e_version);

            // CONTROLLER KEYMAP
            Element e_keymap = save.createElement("keymap");
            e_keymap.setAttribute("username", controller.getUserName());

            Element e_key;
            for (Map.Entry<Character, Key_Commands> e : controller.getRemap().entrySet()) {
                e_key = save.createElement("remap");
                e_key.setAttribute("key", e.getKey().toString());
                e_key.appendChild(save.createTextNode(e.getValue().toString()));
                e_keymap.appendChild(e_key);
            }

            // MAP
            Element e_map = save.createElement("map");

            map.xml_writeMap(save, e_map);

            // ROOT - APPEND
            root.appendChild(e_keymap);
            root.appendChild(e_map);

            // write the content into xml file
            Transformer transformer = TransformerFactory.newInstance().newTransformer();
            DOMSource source = new DOMSource(save);
            StreamResult result = new StreamResult(new File(file_path_));
            transformer.transform(source, result); // actually write the XML to the file

        } catch (Exception e) {
            RunGame.errOut(e, true);
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
                    s_buff = f.getName(); // temporarily store the filename
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
