/*
 * Implementor: Alex Stewart
 * Last Update: 15-02-13
 */
package src;

import java.awt.*;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.*;
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
import src.model.constructs.Entity;
import src.model.constructs.items.*;

/**
 * This class manages a saved game object. A saved game has a file path and 
 * methods to interact with that file (loading and saving data).
 * 
 * @author Alex Stewart
 */
public class SavedGame {

    public static final SimpleDateFormat SAVE_DATE_FORMAT = new SimpleDateFormat("yyMMdd");
    /**
     * SAVE_DATA_VERSION is the data format version number. It should be 
     * incremented any time that the serialization or deserialization sequence 
     * is modified. The version number 0 is reserved. This value has no 
     * relation to the Java native Serialization object ID.
     */
    public static final long SAVE_DATA_VERSION = 10;
    public static final String SAVE_EXT = ".xml";
    public static final String KEY_EXT = ".key";
    public static final char SAVE_ITERATOR_FLAG = '_';
    // SAVE FILE FORMAT: yyMMdd_<number>.sav

    // XML node names
    private static final String XML_ROOT = "save_game";
    private static final String XML_SAVEVERSION = "version";
    private static final String XML_MAP = "map";
    private static final String XML_KEYMAP = "keymap";
    private static final String XML_KEY = "key";
    private static final String XML_ROOT_CONTROLLER = "remap";
    public static final String XML_MAP_MAPGRID = "map_grid";
    public static final String XML_MAP_MAPGRID_WIDTH = "width";
    public static final String XML_MAP_MAPGRID_HEIGHT = "height";
    public static final String XML_MAP_TIME = "time";

    //<editor-fold desc="PUBLIC METHODS" defaultstate="collapsed">

    public static src.model.Map loadGame(String filepath, StringBuilder out_pc) {//UserController controller) {
        try {
            if (out_pc == null) {
                RunGame.errOut("Invalid (null) PC passed to load game.");
                throw new Exception();
            }

            File saveFile = validateFile(filepath, SAVE_EXT);
            if (!saveFile.exists()) {
                RunGame.errOut("LOAD ERROR: File does not exist\nString was: " + filepath);
                return null;
            }

            // Initialize DOM document
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            dbf.setNamespaceAware(false);
            DocumentBuilder docBuilder = dbf.newDocumentBuilder();
            Document load = docBuilder.parse(saveFile);
            RunGame.dbgOut("XML file loaded from: " + saveFile.getPath(), 3);

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
                return null;
            }

            // Load map
            ns_result = root.getElementsByTagName(XML_MAP);
            if (ns_result.getLength() == 0) {
                RunGame.errOut("XML ERR: no map found. Cannot load save file.");
                return null;
            } else if (ns_result.getLength() > 1) {
                RunGame.errOut("XML WARN: save file contains more than [" + ns_result.getLength() + "] map nodes. Loading the first one.");
            }

            out_pc.append(xml_getNodeByString(ns_result.item(0), "pc").getTextContent());
            src.model.Map mm = xml_readMap(load, (Element) ns_result.item(0));

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

    public static HashMap<Character, Key_Commands> loadKeymap(String filepath) {
        try{
            File loadFile = validateFile(filepath, KEY_EXT);
            if (loadFile == null)
                throw new Exception("Could not load file");

            if (!loadFile.exists()) throw new Exception("Load file does not exist");

            HashMap<Character, Key_Commands> ret_kMap = new HashMap<>();

            // Initialize DOM document
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            dbf.setNamespaceAware(false);
            DocumentBuilder docBuilder = dbf.newDocumentBuilder();
            Document load = docBuilder.parse(loadFile.getAbsolutePath());
            RunGame.dbgOut("XML Key file loaded from: " + loadFile.getPath(), 3);

            // Capture root element
            Element root = load.getDocumentElement();
            root.normalize();
            RunGame.dbgOut("XML Captured document root as: " + root.getNodeName(), 4);

            if (!validateDataVersion(root))
                throw new Exception("Invalid data version");

            // Load the user controller
            Node n_keymap = xml_getNodeByString(root, XML_KEYMAP);
            LinkedList<Node> bindings = xml_getAllNodesByString(n_keymap, "remap");

            int numkeys = 0;
            Node d_data;
            Character t_key;
            Key_Commands t_cmd;
            RunGame.dbgOut("Found " + bindings.size() + " entries.");
            for (Node b : bindings) {
                d_data = b.getAttributes().getNamedItem("key");
                if (d_data == null) { throw new Exception("Malformed key @ " + numkeys); }
                t_key = d_data.getTextContent().charAt(0);


                t_cmd = Key_Commands.valueOf(b.getTextContent());
                if (t_cmd == null) { throw new Exception("Malformed key @ " + numkeys); }

                ret_kMap.put(t_key, t_cmd);
                RunGame.dbgOut("Loaded mapping: " + t_key + " -> " + t_cmd.name(), 5);
                numkeys++;
            }

            RunGame.dbgOut("Processed " + numkeys + " key remaps.", 3);

            return ret_kMap;
        } catch (Exception e) {
            RunGame.errOut(e, true);
            return null;
        }
    }

    public static int saveGame(String filepath, src.model.Map map, String pc) {
        try {
            File saveFile = validateFile(filepath, SAVE_EXT);
            if (saveFile == null)
                throw new Exception("Could not save file");

            saveFile.createNewFile(); // create the save file if it doesn't already exist

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

            // MAP
            Element e_map = save.createElement(XML_MAP);

            Element e_pc = save.createElement("pc");
            e_pc.appendChild(save.createTextNode(pc));
            e_map.appendChild(e_pc);
            xml_writeMap(save, e_map, map);

            root.appendChild(e_map);

            // write the content into xml file
            Transformer transformer = TransformerFactory.newInstance().newTransformer();
            DOMSource source = new DOMSource(save);
            StreamResult result = new StreamResult(saveFile);
            transformer.transform(source, result); // actually write the XML to the file

        } catch (Exception e) {
            RunGame.errOut(e, true);
        }

        return 1;
    }

    /**
     * Saves the given key remapping to the provided file (or a new autogenerated file)
     * @param filepath The file to save to, the directory to save to, or null to autogenerate
     * @param remap The key remapping to save
     * @return 0 if the keymap was not saved, 1 otherwise
     */
    public static int saveKeymap(String filepath, HashMap<Character, Key_Commands> remap) {
        RunGame.dbgOut("FUNC: SavedGame.saveKeymap()", 2);
        try {
            if (remap == null) {
                throw new Exception("Invalid remapping");
            }
            if (remap.size() == 0) {
                RunGame.dbgOut("Keymap Save: remap size is 0", 3);
                return 0;
            }

            File saveFile = validateFile(filepath, KEY_EXT);
            if (saveFile == null)
                throw new Exception("Invalid keymap save filepath");

            saveFile.createNewFile(); // create the save file if it doesn't already exist

            // open or create the save file
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            dbf.setNamespaceAware(false);
            DocumentBuilder docBuilder = dbf.newDocumentBuilder();

            Document save = docBuilder.newDocument();
            Element root = save.createElement("remapping");
            save.appendChild(root);

            Element e_version = save.createElement("version");
            e_version.appendChild(save.createTextNode(Long.toString(SAVE_DATA_VERSION)));
            root.appendChild(e_version);

            Element eKeymap = save.createElement(XML_KEYMAP);
            Element tmp_eKey;
            for (java.util.Map.Entry<Character, Key_Commands> e : remap.entrySet()) {
                tmp_eKey = save.createElement(XML_ROOT_CONTROLLER);
                tmp_eKey.setAttribute(XML_KEY, e.getKey().toString());
                tmp_eKey.appendChild(save.createTextNode(e.getValue().name()));
                eKeymap.appendChild(tmp_eKey);
                RunGame.dbgOut("Keymap adding: <" + e.getKey().toString() + " -> " + e.getValue().name() + ">", 5);
            }
            root.appendChild(eKeymap);
            RunGame.dbgOut("Keymap object added to DOM", 3);

            // write the content into xml file
            Transformer transformer = TransformerFactory.newInstance().newTransformer();
            DOMSource source = new DOMSource(save);
            StreamResult result = new StreamResult(saveFile);
            transformer.transform(source, result); // actually write the XML to the file

            RunGame.dbgOut("Keymap successfully saved.", 2);
            return 1; // Return SUCCESS
        } catch (Exception e) {
            RunGame.errOut(e, true);
        }
        return 0; // Return FAILURE
    }

    //</editor-fold>

    //<editor-fold desc="PRIVATE METHODS" defaultstate="collapsed">

    /**
     * Searches the given directory (or current dir, if the param is null) for the current files and returns a string
     * that will be unique to the files in that directory.
     * @param directory The directory to search for files in or NULL - to search the current directory
     * @param extension The file extension to filter for
     * @return A unique file name for the given directory or NULL, if an invalid directory was given
     */
    private static String autoGen (String directory, String extension) {
        if (directory == null || directory.length() == 0)
            directory = System.getProperty("user.dir"); // ask the JVM for the current dir

        RunGame.dbgOut("New file requested for dir: " + directory, 3);
        File dir = new File(directory);
        if (!dir.isDirectory() || !dir.exists()) {
            RunGame.errOut("Requested directory path is invalid or does not exist");
            return null;
        }

        String date = SAVE_DATE_FORMAT.format(new Date()); // get the current date string

        // Search the current directory for existing files and keep an iterator to append the file name with a unique
        // ID for this day.
        int iterator = 1;
        try {
            File[] files = dir.listFiles();
            String buf_string;

            int buf_iter;
            for (File f : files) { // Search files in directory
                if (f.getName().endsWith(extension)) {
                    buf_string = f.getName(); // temporarily store the filename
                    if(!buf_string.startsWith(date))
                        continue; // if the file isn't from this date, ignore it
                    buf_string = buf_string.substring(buf_string.lastIndexOf('_') + 1, buf_string.lastIndexOf(".")); // otherwise, get the ID
                    buf_iter = Integer.parseInt(buf_string);
                    if (buf_iter >= iterator) iterator = buf_iter + 1; // ensure that the iterator is always ahead by 1
                }
            }
        } catch (Exception e) {
            RunGame.errOut(e, true);
        }
        // iterator is now the correct unique ID
        // ready to construct path
        String ff = date + SAVE_ITERATOR_FLAG + iterator + extension;
        RunGame.dbgOut("Returning autogen: " + ff, 4);
        return ff;
    }

    private static boolean validateDataVersion(Element root) {
        // Validate save data version
        NodeList ns_result = root.getElementsByTagName("version"); // ns_result used for node search results

        // If there is no "version" tag, FAIL
        if (ns_result.getLength() == 0) {
            RunGame.errOut("XML Error: no save version found - cannot load");
            return false;
        }
        // If there are multiple "version" tags, notify
        else if (ns_result.getLength() > 1) {
            RunGame.errOut("XML WARN: save file has [" + ns_result.getLength() + "] versions. Ignoring all but first.");
        }

        // Parse version number from first version tag
        Integer v = Integer.parseInt(ns_result.item(0).getTextContent());
        RunGame.dbgOut("XML DBG: Checking save versions. Expecting [" + SAVE_DATA_VERSION + "], got: [" + v + "].", 3);
        // If the versions do not match, FAIL
        if (SAVE_DATA_VERSION != v) {
            RunGame.errOut("XML ERR: save data version mismatch. Save game cannot be loaded.");
            return false;
        }

        return true; // Otherwise, return SUCCESS
    }

    /**
     * Validates a given filepath and returns a File object representing a valid file
     * <p>NOTE: This function DOES NOT create a the file it validates. It does verify that the file that it returns
     * can be read-from and written-to.</p>
     * @param filepath Either a file path, a directory path for an autogenerate file, or null to auto-generate in the
     *                 current directory.
     * @param fileExt The file extension to look for or create with
     * @return Either a valid File object or null, if there was a problem validating the file
     */
    private static File validateFile(String filepath, String fileExt) {
        RunGame.dbgOut("FUNC: validateFile()", 3);
        RunGame.dbgOut("validateFile(): arg0 = " + filepath + "; arg1 = " + fileExt, 4);

        File ret_file;
        // if the filepath is null or 0-length, autogenerate the file in the current dir
        if (filepath == null || filepath.length() == 0) {
            ret_file = new File(autoGen(null, fileExt));

            RunGame.dbgOut("Validate File: returning autogen file: " + ret_file.getPath());
            return ret_file; // return the file
        }

        // otherwise, create a new file object
        ret_file = new File(filepath);

        // if the filepath is a directory, autogenerate a new file in that directory
        if (ret_file.isDirectory()) {
            ret_file = new File(autoGen(ret_file.getPath(), fileExt));
        }
        // otherwise, the file is a local file. Append with the appropriate extension
        else if (!ret_file.getAbsolutePath().endsWith(fileExt))
            ret_file = new File(ret_file.getAbsolutePath() + fileExt);

        // Check that the file can be read and written to
        if (ret_file.exists() && (!ret_file.canRead() || !ret_file.canWrite()))
            return null;

        RunGame.dbgOut("Validate File: returning found file: " + ret_file.getPath(), 4);
        return ret_file;
    }

    //<editor-fold desc="XML OPERATIONS" defaultstate="collapsed">

    private static LinkedList<Node> xml_getAllNodesByString(Node parent, String tagName) {
        NodeList ns = parent.getChildNodes();
        LinkedList<Node> ret_list = new LinkedList<Node>();

        for (int i = 0; i < ns.getLength(); i++) {
            if (ns.item(i).getNodeName().compareTo(tagName) == 0)
                ret_list.push(ns.item(i));
        }
        return ret_list;
    }

    private static Node xml_getNodeByString(Node parent, String tagName) {
        NodeList ns = parent.getChildNodes();

        for (int i = 0; i < ns.getLength(); i++) {
            if (ns.item(i).getNodeName().compareTo(tagName) == 0)
                return ns.item(i);
        }
        return null;
    }

    private static OneShotAreaEffectItem xml_readAOE_One (Document doc, Node n_aoe) {
        if (doc == null || n_aoe == null) {
            RunGame.errOut("xml_readAOE_One: invalid (null) argument");
            return null;
        }

        try {
            // collect:
            String d_name;
            char d_representation = '\u0000';
            Effect d_effect;
            int d_power;

            Node n_data = xml_getNodeByString(n_aoe, "name");
            if (n_data == null) {
                throw new Exception();
            }
            d_name = n_data.getTextContent();

            n_data = xml_getNodeByString(n_aoe, "power");
            if (n_data == null) {
                throw new Exception();
            }
            d_power = Integer.parseInt(n_data.getTextContent());

            n_data = xml_getNodeByString(n_aoe, "effect");
            if (n_data == null) { throw new Exception(); }
            d_effect = Effect.valueOf(n_data.getTextContent());
            if (d_effect == null) { throw new Exception(); }

            n_data = xml_getNodeByString(n_aoe, "char");
            if (n_data != null) { d_representation = n_data.getTextContent().charAt(0); }

            OneShotAreaEffectItem ret_item = new OneShotAreaEffectItem(d_name, d_representation, d_effect, d_power);

            n_data = xml_getNodeByString(n_aoe, "b_invisible");
            if (n_data != null) { ret_item.setViewable(false); }

            return ret_item;

        } catch (Exception e) {
            RunGame.errOut("xml_readAOE_One: failed to parse item");
            return null;
        }
    }

    // DO NOT CALL THIS METHOD!
    private static void xml_readDrawable(Document doc, Node n_draw, DrawableThing dt) throws Exception{
        if (doc == null || n_draw == null || dt == null) {
            RunGame.errOut("xml_readDrawable: invalid (null) argument");
            throw new Exception();
        }

        String d_name;
        char d_char = '\u0000';

        Node n_data = xml_getNodeByString(n_draw, "name");
        if (n_data == null ) { throw new Exception(); }
        d_name = n_data.getTextContent();

        n_data = xml_getNodeByString(n_draw, "char");
        if (n_data != null) { d_char = n_data.getTextContent().charAt(0); }

        n_data = xml_getNodeByString(n_draw, "b_invisible");
        if (n_data != null) { dt.setViewable(false); }

        return; // explicit
    }

    private static DrawableThingStatsPack xml_readDrawableStats (Document doc, Node n_draw) throws Exception {

        int d_armor = 0, d_off = 0;

        Node n_data = xml_getNodeByString(n_draw, "armor_rating");
        d_armor = Integer.parseInt(n_data.getTextContent());

        n_data = xml_getNodeByString(n_draw, "off_rating");
        d_off = Integer.parseInt(n_data.getTextContent());

        return new DrawableThingStatsPack(d_off, d_armor);
    }

    private static Entity xml_readEntity(Document doc, Node n_entity, HashMap<TemporaryObstacleItem, String> keyLinks) {
        try {
            Entity ret_entity;

            // TODO GET STATSPACK

            // Collect:
            String d_name;
            char d_rep = '\u0000';

            Node n_data = xml_getNodeByString(n_entity, "name");
            if (n_data == null) { throw new Exception(); }
            d_name = n_data.getTextContent();
            if (d_name == null) { throw new Exception(); }

            n_data = xml_getNodeByString(n_entity, "char");
            if (n_data != null) { d_rep = n_data.getTextContent().charAt(0); }

            int oid = Integer.parseInt(n_entity.getAttributes().getNamedItem("id").getTextContent());
            switch(oid) {
                case 15: // monster
                    // TODO FINISH
                    ret_entity = new Villager(d_name, d_rep); // TODO REMOVE
                    break;
                case 16: // avatar
                    ret_entity = new Avatar(d_name, d_rep);
                    break;
                case 17: // villager
                    ret_entity = new Villager(d_name, d_rep);
                    break;
                case 18: // merchant
                    ret_entity = new Merchant(d_name, d_rep);
                    break;
                default:
                    throw new Exception();
            }

            n_data = xml_getNodeByString(n_entity, "gold");
            if (n_data == null) { throw new Exception(); }
            int diff = Integer.parseInt(n_data.getTextContent());
            diff = diff - ret_entity.getNumGoldCoins();
            ret_entity.incrementNumGoldCoinsBy(diff);

            n_data = xml_getNodeByString(n_entity, "skill_points");
            if (n_data == null) { throw new Exception(); }


            return ret_entity;
        } catch (Exception e) {
            RunGame.errOut(e, true);
            RunGame.errOut("xml_readEntity: could not parse entity");
            return null;
        }
    }

    private static EntityStatsPack xml_readEntityStatsPack (Document doc, Node n_entity) throws Exception {
        EntityStatsPack ret_pack = new EntityStatsPack();
        return ret_pack;
    }

    private static Item xml_readItem(Document doc, Node n_item, HashMap<TemporaryObstacleItem, String> keyLinks) {
        try {
            Item ret_item;

            if (keyLinks == null) { keyLinks = new HashMap<>(); }

            String d_name;
            char d_rep = '\u0000';
            boolean d_oneshot = false, d_inventory = false, d_passable = false;
            Color d_color = null;

            Node n_data = xml_getNodeByString(n_item, "name");
            if (n_data == null) { throw new Exception(); }
            d_name = n_data.getTextContent();
            if (d_name == null) { throw new Exception(); }

            n_data = xml_getNodeByString(n_item, "char");
            if (n_data != null) { d_rep = n_data.getTextContent().charAt(0); }

            n_data = xml_getNodeByString(n_item, "color");
            if (n_data != null) { d_color = Color.getColor("", Integer.parseInt(n_data.getTextContent())); }

            int oid = Integer.parseInt(n_item.getAttributes().getNamedItem("id").getTextContent());
            switch (oid) {
                case 2: // bow
                    ret_item = new Bow(d_name, d_rep);
                    break;
                case 3: // invis. serum
                    ret_item = new InvisibilitySerum(d_name, d_rep);
                    break;
                case 4: // Kngihts serum
                    ret_item = new KnightsSerum(d_name, d_rep);
                    n_data = xml_getNodeByString(n_item, "b_activated");
                    if (n_data != null) { ((KnightsSerum)ret_item).setActivated(true); }
                    else
                        ((KnightsSerum)ret_item).setActivated(false);
                    break;
                case 5: // Two handed sword
                    ret_item = new TwoHandedSword(d_name, d_rep);
                    break;
                case 6: // trap
                    ret_item = xml_readAOE_One(doc, n_item);
                    if (ret_item == null)
                        throw new Exception();
                    break;
                case 7: // staff
                    ret_item = new Staff(d_name, d_rep);
                    break;
                case 8: // shield
                    ret_item = new Shield(d_name, d_rep);
                    break;
                case 9: // one handed sword
                    ret_item = new OneHandedSword(d_name, d_rep);
                    break;
                case 10: // 1way teleport
                    ret_item = xml_readTeleport_OneWay(doc, n_item);
                    break;
                case 11: // 1s AOE item
                    ret_item = xml_readAOE_One(doc, n_item);
                    if (ret_item == null) { throw new Exception(); }
                    break;
                case 12: // temp obstacle item
                    ret_item = xml_readTempObstacle(doc, n_item);
                    n_data = xml_getNodeByString(n_item, "key");
                    if (n_data == null) { throw new Exception(); }
                    keyLinks.put((TemporaryObstacleItem)ret_item, n_data.getTextContent());
                    break;
                case 13: // Obstacle removing item
                    ret_item = new ObstacleRemovingItem(d_name, d_rep);
                    break;
                case 14: // Permanent Item
                    ret_item = new PermanentObstacleItem(d_name, d_rep);
                    break;
                case 19: // AOE ITEM (NOT SAVED)
                    return null;
                case 20: // AOE ITEM (NOT SAVED)
                    return null;
                case 21: // AOE ITEM (NOT SAVED)
                    return null;
                default:
                    RunGame.errOut("Invalid item id found");
                    throw new Exception();
            }

            ret_item.getStatsPack().addOn(xml_readDrawableStats(doc, n_item));

            n_data = xml_getNodeByString(n_item, "b_invisible");
            if (n_data != null) { ret_item.setViewable(false); }

            return ret_item;

        } catch (Exception e) {
            RunGame.errOut("xml_readItem: could not parse terrain");
            return null;
        }
    }

    private static src.model.Map xml_readMap(Document doc, Element e_map) {
        if (doc == null || e_map == null) {
            RunGame.errOut("xml_readMap: invalid (null) argument");
            return null;
        }

        try {
            src.model.Map ret_map;

            Element e_mapgrid = (Element) e_map.getElementsByTagName(SavedGame.XML_MAP_MAPGRID).item(0);
            Integer map_x = Integer.parseInt(e_mapgrid.getAttributes().getNamedItem(SavedGame.XML_MAP_MAPGRID_WIDTH).getNodeValue());
            Integer map_y = Integer.parseInt(e_mapgrid.getAttributes().getNamedItem(SavedGame.XML_MAP_MAPGRID_HEIGHT).getNodeValue());
            RunGame.dbgOut("XML Parsed: map grid x = " + map_x, 4);
            RunGame.dbgOut("XML Parsed: map grid y = " + map_y, 4);

            ret_map = new src.model.Map(map_x, map_y);

            HashMap<TemporaryObstacleItem, String> keyLinks = new HashMap<>();

            NodeList ns_tiles = e_mapgrid.getElementsByTagName("map_tile");
            int x, y;
            Node tn_tile, tn_data;
            Terrain t_terr;
            for (int i = 0; i < ns_tiles.getLength(); i++) {
                tn_tile = ns_tiles.item(i);
                x = Integer.parseInt(tn_tile.getAttributes().getNamedItem("x").getTextContent());
                y = Integer.parseInt(tn_tile.getAttributes().getNamedItem("y").getTextContent());

                // parse Terrain
                tn_data = xml_getNodeByString(tn_tile, "terrain");
                if (tn_data == null) { throw new Exception(); }
                t_terr = xml_readTerrain(doc, tn_data);
                if (t_terr == null) { throw new Exception(); }
                ret_map.addTerrain(t_terr, x, y);

                // parse entities
                tn_data = xml_getNodeByString(tn_tile, "entity");
                if (tn_data != null) {
                    Entity ent = xml_readEntity(doc, tn_data, keyLinks);
                    ret_map.addAsEntity(ent, x, y);
                }

                // parse items
                LinkedList<Node> items = xml_getAllNodesByString(tn_tile, "item");
                Item t_item;
                for (Node item : items) {
                    t_item = xml_readItem(doc, item, keyLinks);
                    if (t_item != null)
                        ret_map.addItem(t_item, x, y);
                }
            }

            return ret_map;
        } catch (Exception e) {
            RunGame.errOut("xml_readMap: could not parse map");
            return null;
        }

    }

    private static OneWayTeleportItem xml_readTeleport_OneWay(Document doc, Node n_tele) throws Exception {
        OneWayTeleportItem ret_tele;

        // Collect:
        String d_name;
        char d_rep = '\u0000';
        int dx, dy;

        Node n_data = xml_getNodeByString(n_tele, "name");
        if (n_data == null) { throw new Exception(); }
        d_name = n_data.getTextContent();

        n_data = xml_getNodeByString(n_tele, "char");
        if (n_data != null) { d_rep = n_data.getTextContent().charAt(0); }

        n_data = xml_getNodeByString(n_tele, "dx");
        if (n_data == null) { throw new Exception(); }
        dx = Integer.parseInt(n_data.getTextContent());

        n_data = xml_getNodeByString(n_tele, "dy");
        if (n_data == null) { throw new Exception(); }
        dy = Integer.parseInt(n_data.getTextContent());

        ret_tele = new OneWayTeleportItem(d_name, d_rep, dx, dy);

        n_data = xml_getNodeByString(n_tele, "b_invisible");
        if (n_data != null) { ret_tele.setViewable(false); }

        return ret_tele;
    }

    private static TemporaryObstacleItem xml_readTempObstacle (Document doc, Node n_obst) throws Exception {
        TemporaryObstacleItem ret_obst;

        // collect:
        String d_name;
        char d_rep = '\u0000';

        Node n_data = xml_getNodeByString(n_obst, "name");
        if (n_data == null) { throw new Exception(); }
        d_name = n_data.getTextContent();

        n_data = xml_getNodeByString(n_obst, "char");
        if (n_data != null) { d_rep = n_data.getTextContent().charAt(0); }

        ret_obst = new TemporaryObstacleItem(d_name, d_rep, null);

        return ret_obst;
    }

    private static Terrain xml_readTerrain(Document doc, Node n_terr) {

        try {
            Terrain ret_terr;

            // collect:
            String d_name;
            char d_rep = '\u0000';
            boolean d_water = false, d_mountain = false;
            char d_decal = '\u0000';
            Color d_color = null;

            Node n_data = xml_getNodeByString(n_terr, "name");
            if (n_data == null) { throw new Exception(); }
            d_name = n_data.getTextContent();
            if (d_name == null) { throw new Exception(); }

            n_data = xml_getNodeByString(n_terr, "char");
            if (n_data != null) { d_rep = n_data.getTextContent().charAt(0); }

            n_data = xml_getNodeByString(n_terr, "b_water");
            if (n_data != null) { d_water = true; }

            n_data = xml_getNodeByString(n_terr, "b_mountain");
            if (n_data != null) { d_mountain = true; }

            n_data = xml_getNodeByString(n_terr, "decal");
            if (n_data != null) { d_decal = n_data.getTextContent().charAt(0); }

            n_data = xml_getNodeByString(n_terr, "color");
            if (n_data != null) { d_color = Color.getColor("", Integer.parseInt(n_data.getTextContent())); }


            ret_terr = new Terrain(d_name, d_rep, d_water, d_mountain, d_decal, d_color);

            n_data = xml_getNodeByString(n_terr, "b_invisible");
            if (n_data != null) { ret_terr.setViewable(false); }

            return ret_terr;

        } catch (Exception e) {
            RunGame.errOut("xml_readTerrain: could not parse terrain");
            return null;
        }
    }

    private static void xml_writeAOE_One(Document doc, Element parent, OneShotAreaEffectItem item) {
        if (item == null) {
            RunGame.errOut("xml_writeAOE_One: null item argument");
            return;
        }

        if (item.hasBeenActivated()) { return; }

        if (item.getPower() != 0) {
            Element e_pwr = doc.createElement("power");
            e_pwr.appendChild(doc.createTextNode(Integer.toString(item.getPower())));
            parent.appendChild(e_pwr);
        }

        Element e_effect = doc.createElement("effect");
        e_effect.appendChild(doc.createTextNode(item.getEffect()));
        parent.appendChild(e_effect);
    }

    /**
     * Writes this map to the given XML Element in the given XML document
     *
     * @param doc The XML Document to write to
     * @param e_map The XML Element to write to
     * @return 0 = success
     * @author Alex Stewart
     */
    private static int xml_writeMap(Document doc, Element e_map, src.model.Map map) {
        // MAP::TIME)
        Element e_time = doc.createElement(SavedGame.XML_MAP_TIME);
        e_time.appendChild(doc.createTextNode(Integer.toString(map.getTime())));
        e_map.appendChild(e_time);

        // MAP::MAP_GRID
        Element e_map_grid = doc.createElement(SavedGame.XML_MAP_MAPGRID);
        e_map_grid.setAttribute(SavedGame.XML_MAP_MAPGRID_WIDTH, Integer.toString(map.getWidth()));
        e_map_grid.setAttribute(SavedGame.XML_MAP_MAPGRID_HEIGHT, Integer.toString(map.getHeight()));
        RunGame.dbgOut("Writing map grid of size: " + map.getWidth() + "x" + map.getHeight(), 4);
        MapTile[][] grid = map.getMapGrid();

        // iterate through map tiles
        Element e_l;
        for (int j = 0; j < map.getHeight(); j++) {
            for (int i = 0; i < map.getWidth(); i++) {
                e_l = doc.createElement("map_tile");
                e_l.setAttribute("x", Integer.toString(i));
                e_l.setAttribute("y", Integer.toString(j));

                Terrain terr = grid[j][i].getTerrain();
                RunGame.dbgOut("Writing map tile [" + i + ", " + j + "]", 5);
                if (terr == null) {
                    RunGame.errOut("xml_writeMap: null terrain @ [" + i + ", " + j + "]");
                    return 1;
                }

                // Terrain
                Element e_terr = doc.createElement("terrain");
                e_terr.setAttribute("id", Integer.toString(terr.getID()));
                //xml_writeTerrain(doc, e_l, terr);
                xml_writeDrawable(doc, e_terr, terr);
                e_l.appendChild(e_terr);


                // Entity
                Entity ent = grid[j][i].getEntity();
                if (ent != null) {
                    Element e_ent = doc.createElement("entity");
                    e_ent.setAttribute("id", Integer.toString(ent.getID()));
                    //xml_writeEntity(doc, e_ent, ent);
                    xml_writeDrawable(doc, e_ent, ent);
                    e_l.appendChild(e_ent);
                }

                // Item list
                Element te_item;
                if (map.getMapGrid()[j][i].getItemList().size() != 0) {
                    Element e_itemlist = doc.createElement("item_list");
                    for (Item item : grid[j][i].getItemList()) {
                        te_item = doc.createElement("item");
                        te_item.setAttribute("id", Integer.toString(item.getID()));
                        xml_writeDrawable(doc, te_item, item);
                        e_itemlist.appendChild(te_item);
                    }
                    e_l.appendChild(e_itemlist);
                }

                e_map_grid.appendChild(e_l);
            }
        }

        // MAP - APPEND
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
    private static void xml_writeEntity(Document doc, Element parent, Entity entity) {

        if (entity == null) {
            RunGame.errOut("xml_writeEntity: null entity argument");
            return;
        }

        xml_writeEntityStatsPack(doc, parent, entity.getStatsPack());

        // Record primary and secondary (written in item-list step)
        PrimaryHandHoldable eq_primary = entity.getPrimaryEquipped();
        SecondaryHandHoldable eq_secondary = entity.getSecondaryEquipped();

        // gold
        Element e_data = doc.createElement("gold");
        e_data.appendChild(doc.createTextNode(Integer.toString(entity.getNumGoldCoins())));
        parent.appendChild(e_data);

        // skill points
        e_data = doc.createElement("skill_points");
        e_data.appendChild(doc.createTextNode(Integer.toString(entity.getNum_skillpoints_())));
        parent.appendChild(e_data);

        // bind wounds
        e_data = doc.createElement("bind_wounds");
        e_data.appendChild(doc.createTextNode(Integer.toString(entity.getBind_wounds_())));
        parent.appendChild(e_data);

        // observation
        e_data = doc.createElement("observation");
        e_data.appendChild(doc.createTextNode(Integer.toString(entity.getObservation_())));
        parent.appendChild(e_data);

        // bargain
        e_data = doc.createElement("bargain");
        e_data.appendChild(doc.createTextNode(Integer.toString(entity.getBargain_())));
        parent.appendChild(e_data);

        // occupation
        // TODO IMPLEMENT THIS

        // Direction
        Element e_dir = doc.createElement("direction");
        e_dir.appendChild(doc.createTextNode(entity.getFacingDirection().toString()));

        // Item List
        Element te_item;
        ArrayList<PickupableItem> inv = entity.getInventory();
        for (PickupableItem i : inv) {
            te_item = doc.createElement("item");

            if (i == eq_primary) {
                te_item.appendChild(doc.createElement("b_primary"));
            }
            if (i == eq_secondary) {
                te_item.appendChild(doc.createElement("b_secondary"));
            }

            xml_writeDrawable(doc, te_item, i);
            parent.appendChild(te_item);
        }

        // Occupation
        if (entity.getOccupation() == null) {
            RunGame.dbgOut("xml_writeEntity: entity has no occupation", 4);
            return;
        }

        Element e_occ = doc.createElement("occupation");
        xml_writeOccupation(doc, e_occ, entity.getOccupation());
        parent.appendChild(e_occ);
    }

    private static void xml_writeItem(Document doc, Element parent, Item item) {
        if (item == null) {
            RunGame.errOut("xml_writeItem: null item argument");
            return;
        }

        if (item.goesInInventory()) { parent.appendChild(doc.createElement("b_inventoryItem")); }

        if (item.isOneShot()) { parent.appendChild(doc.createElement("b_oneshot")); }

        if (item.isPassable()) { parent.appendChild(doc.createElement("b_passable")); }
    }

    private static void xml_writeEntityStatsPack(Document doc, Element parent, EntityStatsPack stats) {
        if (stats == null) {
            RunGame.errOut("xml_writeStatsEntity: null statspack");
            return;
        }

        Element tra_eStat;

        if (stats.getLives_left_() != 0) {
            tra_eStat = doc.createElement("lives");
            tra_eStat.appendChild(doc.createTextNode(Integer.toString(stats.getLives_left_())));
            parent.appendChild(tra_eStat);
        }
        if (stats.getStrength_level_() != 0) {
            tra_eStat = doc.createElement("strength");
            tra_eStat.appendChild(doc.createTextNode(Integer.toString(stats.getStrength_level_())));
            parent.appendChild(tra_eStat);
        }
        if (stats.getAgility_level_() != 0) {
            tra_eStat = doc.createElement("agility");
            tra_eStat.appendChild(doc.createTextNode(Integer.toString(stats.getAgility_level_())));
            parent.appendChild(tra_eStat);
        }
        if (stats.getIntellect_level_() != 0) {
            tra_eStat = doc.createElement("intellect");
            tra_eStat.appendChild(doc.createTextNode(Integer.toString(stats.getIntellect_level_())));
            parent.appendChild(tra_eStat);
        }
        if (stats.getHardiness_level_() != 0) {
            tra_eStat = doc.createElement("hardness");
            tra_eStat.appendChild(doc.createTextNode(Integer.toString(stats.getHardiness_level_())));
            parent.appendChild(tra_eStat);
        }
        if (stats.getQuantity_of_experience_() != 0) {
            tra_eStat = doc.createElement("XP");
            tra_eStat.appendChild(doc.createTextNode(Integer.toString(stats.getQuantity_of_experience_())));
            parent.appendChild(tra_eStat);
        }
        if (stats.getMovement_level_() != 0) {
            tra_eStat = doc.createElement("movement");
            tra_eStat.appendChild(doc.createTextNode(Integer.toString(stats.getMovement_level_())));
            parent.appendChild(tra_eStat);
        }
        if (stats.getMax_life_() != 0) {
            tra_eStat = doc.createElement("max_life");
            tra_eStat.appendChild(doc.createTextNode(Integer.toString(stats.getMax_life_())));
            parent.appendChild(tra_eStat);
        }
        if (stats.getMax_mana_() != 0) {
            tra_eStat = doc.createElement("max_mana");
            tra_eStat.appendChild(doc.createTextNode(Integer.toString(stats.getMax_mana_())));
            parent.appendChild(tra_eStat);
        }
        if (stats.getMoves_left_in_turn_() != 0) {
            tra_eStat = doc.createElement("moves_remaining");
            tra_eStat.appendChild(doc.createTextNode(Integer.toString(stats.getMoves_left_in_turn_())));
            parent.appendChild(tra_eStat);
        }
        if (stats.getCached_current_level_() != 0) {
            tra_eStat = doc.createElement("level");
            tra_eStat.appendChild(doc.createTextNode(Integer.toString(stats.getCached_current_level_())));
            parent.appendChild(tra_eStat);
        }
        if (stats.getCurrent_life_() != 0) {
            tra_eStat = doc.createElement("life");
            tra_eStat.appendChild(doc.createTextNode(Integer.toString(stats.getCurrent_life_())));
            parent.appendChild(tra_eStat);
        }
        if (stats.getCurrent_mana_() != 0) {
            tra_eStat = doc.createElement("mana");
            tra_eStat.appendChild(doc.createTextNode(Integer.toString(stats.getCurrent_mana_())));
            parent.appendChild(tra_eStat);
        }
        if (stats.getDefensive_rating_() != 0) {
            tra_eStat = doc.createElement("defensive_rating");
            tra_eStat.appendChild(doc.createTextNode(Integer.toString(stats.getDefensive_rating_())));
            parent.appendChild(tra_eStat);
        }
    }

    private static void xml_writeMonster(Document doc, Element parent, Monster monst) {
        if (monst == null) {
            RunGame.errOut("xml_writeMonster: null monster argument");
            return;
        }

        Element e_turns = doc.createElement("turns");
        e_turns.appendChild(doc.createTextNode(Integer.toString(monst.getFollowTurns())));
        parent.appendChild(e_turns);

        if (monst.getFolloweeName() != null) {
            Element e_followee = doc.createElement("followee");
            e_followee.appendChild(doc.createTextNode(monst.getFolloweeName()));
            parent.appendChild(e_followee);
        }
    }

    private static void xml_writeOccupation(Document doc, Element parent, Occupation occ) {
        if (occ == null) {
            RunGame.errOut("xml_writeOccupation: null occupation argument");
            return;
        }

        int id = occ.getID();
        parent.setAttribute("id", Integer.toString(id));

        Element e_data = doc.createElement("char");
        e_data.appendChild(doc.createTextNode(Character.toString(occ.getOccupationRepresentation())));
        parent.appendChild(e_data);

        e_data = doc.createElement("sk1");
        e_data.appendChild(doc.createTextNode(Integer.toString(occ.getSkill_1_())));
        parent.appendChild(e_data);

        e_data = doc.createElement("sk2");
        e_data.appendChild(doc.createTextNode(Integer.toString(occ.getSkill_2_())));
        parent.appendChild(e_data);

        e_data = doc.createElement("sk3");
        e_data.appendChild(doc.createTextNode(Integer.toString(occ.getSkill_3_())));
        parent.appendChild(e_data);

        e_data = doc.createElement("sk4");
        e_data.appendChild(doc.createTextNode(Integer.toString(occ.getSkill_4_())));
        parent.appendChild(e_data);

        switch(id) {
            case 22: // smasher
                break;
            case 23: // sneak
                e_data = doc.createElement("cloak_timer");
                e_data.appendChild(doc.createTextNode(Integer.toString(((Sneak)occ).getCloakTimer())));
                parent.appendChild(e_data);
                break;
            case 24: // summoner - novice
            case 25: // summoner - champion
            case 26: // summoner - ultimate
                e_data = doc.createElement("boon_timer");
                e_data.appendChild(doc.createTextNode(Integer.toString(((Summoner)occ).getBoonTimer())));
                parent.appendChild(e_data);
                break;
        }
    }

    private static void xml_writeStatsDrawable(Document doc, Element parent, DrawableThingStatsPack stats) {
        if (stats == null) {
            RunGame.errOut("xml_writeStatsDrawable: null statspack");
            return;
        }

        Element trans_eStat;

        if (stats.getArmor_rating_() != 0) {
            trans_eStat = doc.createElement("armor_rating");
            trans_eStat.appendChild(doc.createTextNode(Integer.toString(stats.getArmor_rating_())));
            parent.appendChild(trans_eStat);
        }
        if (stats.getOffensive_rating_() != 0) {
            trans_eStat = doc.createElement("off_rating");
            trans_eStat.appendChild(doc.createTextNode(Integer.toString(stats.getOffensive_rating_())));
            parent.appendChild(trans_eStat);
        }
    }

    private static void xml_writeTeleporter_OneWay(Document doc, Element parent, OneWayTeleportItem item) {
        if (item == null) {
            RunGame.errOut("xml_writeTeleporter_OneWay: null item argument");
            return;
        }

        Element e_data = doc.createElement("dx");
        e_data.appendChild(doc.createTextNode(Integer.toString(item.getDestX())));
        parent.appendChild(e_data);

        e_data = doc.createElement("dy");
        e_data.appendChild(doc.createTextNode(Integer.toString(item.getDestY())));
        parent.appendChild(e_data);
    }

    private static void xml_writeTempObstacle(Document doc, Element parent, TemporaryObstacleItem item) {
        if (item == null) {
            RunGame.errOut("xml_writeTempObstacle: null item argument");
            return;
        }

        String s_key = item.getKeyName(); // get the name of the key
        if (s_key == null || s_key.length() == 0) { s_key = "NULL"; } // if invalid key, write NULL

        // write the name of the key to be rebuilt later
        Element e_key = doc.createElement("key");
        e_key.appendChild(doc.createTextNode(s_key));
        parent.appendChild(e_key);
    }

    private static void xml_writeTerrain(Document doc, Element parent, Terrain terr) {
        if (terr.getName() == null) {
            RunGame.errOut("xml_writeTerrain: null Terrain name");
            return;
        }

        // BOOLEANS:
        if (terr.isMountain()) {
            parent.appendChild(doc.createElement("b_mountain"));
        }

        if (terr.isWater()) {
            parent.appendChild(doc.createElement("b_water"));
        }

        // Terrain::Decal - only write if non-null
        if (terr.getDecal() != '\u0000') {
            Element e_decal = doc.createElement("decal");
            e_decal.appendChild(doc.createTextNode(Character.toString(terr.getDecal())));
            parent.appendChild(e_decal);
        }
    }

    private static void xml_writeDrawable(Document doc, Element parent, DrawableThing dt) {
        if (dt == null) {
            RunGame.errOut("Invalid drawable thing reference");
            return;
        }

        Element te_data;
        te_data = doc.createElement("name");
        te_data.appendChild(doc.createTextNode(dt.getName()));
        parent.appendChild(te_data);

        // PROP: b_invisible iff the object is invisible
        if (!dt.isVisible()) { parent.appendChild(doc.createElement("b_invisible")); }

        // PROP: drawable character iff not null-char
        if (dt.getRepresentation() != '\u0000') {
            te_data = doc.createElement("char");
            te_data.appendChild(doc.createTextNode(Character.toString(dt.getDrawableCharacter())));
            parent.appendChild(te_data);
        }

        // PROP: drawable character color iff not null-color
        if (dt.getColor() != null) {
            te_data = doc.createElement("color");
            te_data.appendChild(doc.createTextNode(Integer.toString(dt.getColor().getRGB())));
            parent.appendChild(te_data);
        }

        xml_writeStatsDrawable(doc, parent, dt.getStatsPack());

        switch (dt.getID()) {
            case 0:
                RunGame.errOut("Attempted to write invalid DrawableThing ID (0)");
                break;
            case 1:
                xml_writeTerrain(doc, parent, (Terrain)dt);
                break;
            case 2:
                xml_writeItem(doc, parent, (Item) dt);
                break;
            case 3:
                xml_writeItem(doc, parent, (Item) dt);
                break;
            case 4:
                xml_writeItem(doc, parent, (Item) dt);
                if(((KnightsSerum)dt).getActivated()) { parent.appendChild(doc.createElement("b_activated")); }
                break;
            case 5:
                xml_writeItem(doc, parent, (Item) dt);
                break;
            case 6:
                xml_writeAOE_One(doc, parent, (OneShotAreaEffectItem)dt);
                break;
            case 7:
                xml_writeItem(doc, parent, (Item)dt);
                break;
            case 8:
                xml_writeItem(doc, parent, (Item) dt);
                break;
            case 9:
                xml_writeItem(doc, parent, (Item)dt);
                break;
            case 10:
                xml_writeTeleporter_OneWay(doc, parent, (OneWayTeleportItem)dt);
                break;
            case 11:
                xml_writeAOE_One(doc, parent, (OneShotAreaEffectItem)dt);
                break;
            case 12:
                xml_writeTempObstacle(doc, parent, (TemporaryObstacleItem)dt);
                break;
            case 13:
                xml_writeItem(doc, parent, (Item)dt);
                break;
            case 14:
                xml_writeItem(doc, parent, (Item)dt);
                break;
            case 15:
                xml_writeEntity(doc, parent, (Entity)dt);
                xml_writeMonster(doc, parent, (Monster) dt);
                break;
            case 16:
                xml_writeEntity(doc, parent, (Entity)dt);
                break;
            case 17:
                xml_writeEntity(doc, parent, (Entity)dt);
                break;
            case 18:
                xml_writeEntity(doc, parent, (Entity)dt);
                break;
            case 19:
                break;
            case 20:
                break;
            case 21:
                break;
            default:
                RunGame.errOut("Attempted to write invalid DrawableThing ID (default)");
        };
    }
    /**
     * MAGIC NUMBERS:
     * 0    Reserved
     * 1    Terrain
     * 2    Bow
     * 3    Invisibility Serum
     * 4    Knights Serum
     * 5    Two Handed Sword
     * 6    Trap
     * 7    Staff
     * 8    Shield
     * 9    One Handed Sword
     * 10   One Way Teleport Item
     * 11   One Shot Area Effect Item
     * 12   Temporary Obstacle Item
     * 13   Obstacle Removing Item
     * 14   Permanent Obstacle Item
     * 15   Monster
     * 16   Avatar
     * 17   Villager
     * 18   Merchant
     * 19   Spreading Line Area Effect Item
     * 20   Spreading Circle Area Effect Item
     * 21   Spreading Cone Area Effect Item
     * 22   Occ: Smasher
     * 23   Occ: Sneak
     * 24   Occ: Summoner - Novice
     * 25   Occ: Summoner - Champion
     * 26   Occ: Summoner - Ultimate
     */

    //</editor-fold>
    //</editor-fold>
}
