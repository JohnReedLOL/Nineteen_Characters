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

import src.model.*;
import src.model.constructs.*;

import org.w3c.dom.*;

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
    public static final long SAVE_DATA_VERSION = 3;
    public static final String SAVE_EXT = ".xml";
    public static final char SAVE_ITERATOR_FLAG = '_';
    private static final String SAVE_EOF_STRING = "///END OF FILE///";
    // SAVE FILE FORMAT: yyMMdd_<number>.sav

    // XML node names
    private static final String XML_ROOT = "save_game";
    private static final String XML_SAVEVERSION = "version";
    private static final String XML_MAP = "map";
    private static final String XML_USERNAME = "username";
    private static final String XML_KEYMAP = "keymap";
    private static final String XML_KEY = "key";
    private static final String XML_REMAP = "remap";
    public static final String XML_MAP_MAPGRID = "map_grid";
    public static final String XML_MAP_MAPGRID_WIDTH = "width";
    public static final String XML_MAP_MAPGRID_HEIGHT = "height";
    public static final String XML_MAP_TIME = "time";
    
    public SavedGame(String filePath) {
        file_path_ = filePath;
    }

    public src.model.Map loadGame() {//UserController controller) {
        try {
            // Initialize DOM document
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            dbf.setNamespaceAware(false);
            DocumentBuilder docBuilder = dbf.newDocumentBuilder();
            Document load = docBuilder.parse(new File(file_path_));
            RunGame.dbgOut("XML file loaded from: " + file_path_, 3);

            Element root = load.getDocumentElement();
            root.normalize();
            RunGame.dbgOut("XML Captured document root as: " + root.getNodeName(), 4);

            // Validate save data version
            NodeList ns_result = root.getElementsByTagName("version"); // ns_result used for node search results
            if (ns_result.getLength() == 0) {
                RunGame.errOut("XML Error: no save version found - cannot load");
                return null;
            }
            else if (ns_result.getLength() > 1) {
                RunGame.errOut("XML WARN: save file has [" + ns_result.getLength() + "] versions. Ignoring all but first.");
            }
            Integer v = Integer.parseInt(ns_result.item(0).getTextContent());
            RunGame.dbgOut("XML DBG: Checking save versions. Expecting [" + SAVE_DATA_VERSION + "], got: [" + v + "].", 3);
            if (SAVE_DATA_VERSION != v) {
                RunGame.errOut("XML ERR: save data version mismatch. Save game cannot be loaded.");
                // TODO: uncomment line below
                //return null;
            }

            // TODO: load user controller
            ns_result = root.getElementsByTagName(XML_KEYMAP);

            // Load map
            ns_result = root.getElementsByTagName(XML_MAP);
            if (ns_result.getLength() == 0) {
                RunGame.errOut("XML ERR: no map found. Cannot load save file.");
                return null;
            } else if (ns_result.getLength() > 1) {
                RunGame.errOut("XML WARN: save file contains more than [" + ns_result.getLength() + "] map nodes. Loading the first one.");
            }

            src.model.Map mm = src.model.Map.xml_readMap(load, (Element)ns_result.item(0));

            if (mm == null) {
                RunGame.errOut("XML ERR: map load has failed.");
            } else {
                RunGame.dbgOut("XML INFO: load has completed successfully.");
            }
            return mm; // return with map (may be null, if map load failed)

        } catch (Exception e) {
            RunGame.errOut(e, true);
            return null;
        }
    }

    public int saveGame(src.model.Map map) {
        try {
            // open or create the save file
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            dbf.setNamespaceAware(false);
            DocumentBuilder docBuilder = dbf.newDocumentBuilder();

            Document save = docBuilder.newDocument();
            Element root = save.createElement(XML_ROOT);
            save.appendChild(root);

            Element e_version = save.createElement("version");
            e_version.appendChild(save.createTextNode(Long.toString(SAVE_DATA_VERSION)));
            root.appendChild(e_version);

            /*
            if (controller != null) {
                // CONTROLLER KEYMAP
                Element e_keymap = save.createElement(XML_KEYMAP);
                e_keymap.setAttribute(XML_USERNAME, controller.getUserName());

                Element e_key;
                for (Map.Entry<Character, Character> e : controller.getRemap().entrySet()) {
                    e_key = save.createElement(XML_REMAP);
                    e_key.setAttribute(XML_KEY, e.getKey().toString());
                    e_key.appendChild(save.createTextNode(e.getValue().toString()));
                    e_keymap.appendChild(e_key);
                }
                root.appendChild(e_keymap);
            } else {
                RunGame.dbgOut("XML INFO: no user controller provided for saving.", 3);
            */

            // MAP
            Element e_map = save.createElement(XML_MAP);

            map.xml_writeMap(save, e_map);

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
