package src.model;

import src.Not_part_of_iteration_2_requirements.MapInternet;
import java.awt.Color;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map.Entry;
import java.util.Random;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import src.IO_Bundle;
import src.Key_Commands;
import src.RunGame;
import src.SavedGame;
import src.model.constructs.Avatar;
import src.model.constructs.DrawableThingStatsPack;
import src.model.constructs.Entity;
import src.model.constructs.EntityStatsPack;
import src.model.constructs.Terrain;
import src.model.constructs.items.Item;

/**
 * An aggregation of MapTiles with methods to add and remove map constructs.
 * @author John-Michael Reed
 */
public class Map extends Thread implements MapMapEditor_Interface, MapUser_Interface {

    //<editor-fold desc="Static fields" defaultstate="collapsed">
    //</editor-fold>
    //<editor-fold desc="Non-static fields" defaultstate="collapsed">
    // The map has a clock
    private int time_measured_in_turns;
    public int height_;
    public int width_;

    // String is the entity's name. The entity name must be unqiue or else bugs will occur.
    private LinkedHashMap<String, Entity> entity_list_;
    private LinkedList<Item> items_list_;
    // 2d array of tiles.
    private MapTile map_grid_[][];
    private MapInternet my_internet_;

    //</editor-fold>
    //<editor-fold desc="Constructors" defaultstate="collapsed">
    // This should never get called
    @SuppressWarnings("unused")
    private Map() {
        height_ = 0;
        width_ = 0;
        System.exit(-777);
    }

    /**
     * Map Constructor, creates new x by y Map.
     *
     * @param x - Length of Map
     * @param y - Height of Map
     */
    public Map(int x, int y) {

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
            my_internet_ = new MapInternet(this);
        } catch (Exception e) {
            // No clue what causes this
            e.printStackTrace();
            System.exit(-6);
            return;
        }
    }

    @Override
    public void run() {
        try {
            my_internet_.start();
            Thread.sleep(Long.MAX_VALUE);
        } catch (InterruptedException e) {
            System.err.println("This error is supposed to appear on closing:");
            e.printStackTrace();
            this.grusomelyKillTheMapThread();
            return;
        }
    }

    //</editor-fold>
    //<editor-fold desc="Accessors" defaultstate="collapsed">
    /**
     * Gets an entity by name
     *
     * @param name - name of Entity
     * @return Entity with the name provided in parenthesis.
     */
    public Entity getEntityByName(String name) {
        return this.entity_list_.get(name);
    }

    public int getHeight() {
        return height_;
    }

    public LinkedList<Item> getItemsList() {
        return items_list_;
    }

    public LinkedHashMap<String, Entity> getEntityList() {
        return this.entity_list_;
    }

    @Override
    public IO_Bundle getMapAt(int x, int y, int width, int height) {
        char[][] view = makeView(x, y, width, height);
        int[][] colors = makeColors(x, y, width, height);
        return new IO_Bundle(null, view, colors, null, null, null, 0, 0, 0, 0, null, null, null, 0, true);
        //Mapeditor has no game over condition, you are always alive.
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

    public int getTime() {
        return time_measured_in_turns;
    }

    public int getWidth() {
        return width_;
    }

    public Color getColorRepresentation(int x, int y) {
        MapTile tile_at_x_y = this.getTile(x, y);
        if (tile_at_x_y == null) {
            return Color.WHITE;//Off the map should be white.
        } else {
            return tile_at_x_y.getTopColor();
        }
    }

    public boolean hasEntity(String name) {
        return entity_list_.containsKey(name);
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
     * Adds an avatar to the map and provides it with a MapAvatar_Relation. Can
     * only be used on Avatars.
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
     * @return error code, 0 for success, -1 if terrain is null.
     */
    public int addTerrain(Terrain t, int x, int y) {
        if (t != null) {
            t.setMapRelation(new MapTerrain_Relation(this, t));
            this.map_grid_[y][x].addTerrain(t);
            t.getMapRelation().setMapTile(this.map_grid_[y][x]);
            return 0;
        } else {
            return -1;
        }
    }

    public void grusomelyKillTheMapThread() {
        my_internet_.interrupt();
        my_internet_.interruptAllMyUserThreads();
        my_internet_.stop();
    }

    /**
     * Returns true if the given coord is within the map
     *
     * @author Matthew Breggs?
     * @param x
     * @param y
     * @return
     */
    public boolean isWithinMap(int x, int y) {
        return ((x >= 0 && x < this.width_) && (y >= 0 && y < this.height_));
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

    public void makeTakeTurns() {
        for (int y = 0; y < height_; ++y) {
            for (int x = 0; x < width_; ++x) {
                makeMapTileTakeTurn(x, y);
            }
        }
        return;
    }

    /**
     * Makes the tile specified run it's take turn function if it exists.
     *
     * @param x
     * @param y
     * @return
     */
    private boolean makeMapTileTakeTurn(int x, int y) {
        MapTile tile_at_x_y = this.getTile(x, y);
        if (tile_at_x_y == null) {
            return false;
        }
        tile_at_x_y.takeTurn();
        return true;
    }

    /**
     * Uses run length encoding with characters "char[] unchanged_characters"
     * and character_frequencies "int[] unchanged_indexes."
     *
     * @param x_center
     * @param y_center
     * @param width_from_center
     * @param height_from_center
     * @param unchanged_characters - empty arraylist of characters - outputs as
     * a list of repeated encoded characters
     * @param frequencies - empty arraylist of encoded character
     * character_frequencies - outputs as a corresponding list of
     * character_frequencies
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
                    if (x == x_start && y == y_start) {
                        ++x; // you have to be one after the first tile.
                    }
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
            System.exit(-7);
        }
    }

    private IO_Bundle make_dead_packet() {
        IO_Bundle return_package = new IO_Bundle(
                "",
                null,
                null,
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
                false
        );
        return return_package;
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
        //System.out.println("Calling Map.sendCommandToMapWithOptionalText - No internet being used");
        // Avatar to_recieve_command = this.avatar_list_.get(username);

        Entity to_recieve_command;
        if (this.entity_list_.containsKey(username)) {
            to_recieve_command = this.entity_list_.get(username);
        } else {
            to_recieve_command = null;
            System.err.println("The avatar of entity you are trying to reach does not exist.");
            return make_dead_packet();
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
                } else if (to_recieve_command.hasLivesLeft() == true) {
                    strings_for_IO_Bundle = to_recieve_command.acceptKeyCommand(command, text);
                } else {
                    strings_for_IO_Bundle = null;
                }
                if (to_recieve_command.hasLivesLeft() == true) {
                    char[][] view = makeView(to_recieve_command.getMapRelation().getMyXCoordinate(),
                            to_recieve_command.getMapRelation().getMyYCoordinate(),
                            width_from_center, height_from_center);
                    int[][] colors = makeColors(to_recieve_command.getMapRelation().getMyXCoordinate(),
                            to_recieve_command.getMapRelation().getMyYCoordinate(),
                            width_from_center, height_from_center);
                    if (!Key_Commands.DO_ABSOLUTELY_NOTHING.equals(command)) {
                        System.out.println(items_list_);
                        makeTakeTurns();//Make all the maptiles take a turn.
                    }
                    IO_Bundle return_package = new IO_Bundle(
                            to_recieve_command.getObservationString(),
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
                            to_recieve_command.hasLivesLeft()
                    );
                    return return_package;
                } else {
                    char[][] view = null;
                    int[][] colors = null;
                    return make_dead_packet();
                }
            } else if (command == null) {
                IO_Bundle return_package = new IO_Bundle(to_recieve_command.getObservationString(),
                        null, null, to_recieve_command.getInventory(),
                        // Don't for get left and right hand items
                        to_recieve_command.getStatsPack(), to_recieve_command.getOccupation(),
                        to_recieve_command.getNum_skillpoints_(), to_recieve_command.getBind_wounds_(),
                        to_recieve_command.getBargain_(), to_recieve_command.getObservation_(),
                        to_recieve_command.getPrimaryEquipped(),
                        to_recieve_command.getSecondaryEquipped(),
                        strings_for_IO_Bundle,
                        to_recieve_command.getNumGoldCoins(),
                        to_recieve_command.hasLivesLeft()
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
                    if (x == x_start && y == y_start) {
                        ++x; // you have to be one after the first tile.
                    }
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
            System.exit(-27);
        }
    }

    /**
     * Returns RGB color valued ints instead of Java objects to conserve network
     * bandwidth
     *
     * @param x_center
     * @param y_center
     * @param width_from_center
     * @param height_from_center
     * @return
     */
    public int[][] makeColors(int x_center, int y_center, int width_from_center, int height_from_center) {
        int[][] colors = new int[1 + 2 * height_from_center][1 + 2 * width_from_center];
        int y_index = 0;
        for (int y = y_center - height_from_center; y <= y_center + height_from_center; ++y) {
            int x_index = 0;
            for (int x = x_center - width_from_center; x <= x_center + width_from_center; ++x) {
                //colors[y_index][x_index] = new Color(this.getColorRepresentation(x, y).getRGB());
                colors[y_index][x_index] = this.getColorRepresentation(x, y).getRGB();
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
            //e.setMapRelation(null);
            //System.gc();
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

    public Item removeExactItem(Item item) {
        int x_position = item.getMapRelation().getMyXCoordinate();
        int y_position = item.getMapRelation().getMyYCoordinate();

        int error_code = this.map_grid_[y_position][x_position].removeSpecificItem(item);
        if (error_code != 0) {
            System.err.println("Item not found in Map.removeExactItem");
            System.exit(-6);
        }
        items_list_.remove(item);
        return item;
    }

    //</editor-fold>
    //<editor-fold desc="Save/Load" defaultstate="collapsed">
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
        //</editor-fold>
}
