package src.model;

import java.util.ArrayDeque;
import java.util.HashMap;
import java.util.LinkedList;
import java.io.Serializable;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.IOException;
import java.util.ListIterator;

import src.SaveData;
import src.SavedGame;
import src.controller.Entity;
import src.controller.Item;
import src.controller.Terrain;

/**
 *
 * @author JohnReedLOL
 */
final public class MapTile implements SaveData {

    public int x_;
    public int y_;

    private Terrain terrain_;
    private Entity entity_;
    private transient LinkedList<Item> items_;

    MapTile(int x, int y) {
        x_ = x;
        y_ = y;
        terrain_ = null;
        entity_ = null;
        items_ = new LinkedList<Item>();
    }

    /**
     * Returns 0 on success, returns -1 if terrain is already set.
     *
     * @param terrain - terrain to be added to the tile
     */
    public int addTerrain(Terrain terrain) {
        if (terrain != null) {
            this.terrain_ = terrain;
            return 0;
        } else {
            return -1;
        }
    }

    /**
     * Checks the tile for obstacles
     * @author Reed, John
     * @return whether or not this tile is passable
     */
    public boolean isPassable() {
        if (terrain_ != null && !terrain_.isPassable()) {
            return false;
        } 
        if (entity_ != null && !entity_.isPassable()) {
            return false;
        } 
        if (items_ != null && items_.peekLast() != null) {
            for (int i = 0; i < items_.size(); ++i) {
                if (!items_.get(i).isPassable()) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * Only works if there in no entity there already.
     *
     * @param entity - entity to be added to the tile
     * @return error codes: -1 if an entity is already there.
     */
    public int addEntity(Entity entity) {
        if (this.entity_ == null && entity != null) {
            entity.getMapRelation().setMapTile(this);
            this.entity_ = entity;
            return 0;
        } else {
            return -1;
        }
    }

    /**
     * Will return -1 if entity already equals null
     *
     * @return 0 on success, non-zero on error
     */
    public int removeEntity() {
        if (this.entity_ == null) {
            return -1;
        } else {
            this.entity_.getMapRelation().setMapTile(null);
            this.entity_ = null;
            return 0;
        }
    }

    /**
     * Returns 0 on success, -1 when blocking item is already there, -2 when
     * item is null
     *
     * @param item - item to be added to the tile
     * @return -
     */
    public int addItem(Item item) {
        if (item == null) {
            return -2;
        }
        // Make sure there are no impassible items on this tile
        ListIterator<Item> listIterator = items_.listIterator();
        while (listIterator.hasNext()) {
            if (!listIterator.next().isPassable()) {
                return -1;
            }
        }
        // Add the item.
        item.getMapRelation().setMapTile(this);
        this.items_.add(item);
        return 0;
    }

    public Terrain getTerrain() {
        return this.terrain_;
    }

    public Entity getEntity() {
        return this.entity_;
    }

    /**
     * Peeks at (does not remove) top item on tile.
     * @return Item on top of the tile. Does not remove item from tile.
     */
    public Item viewTopItem() {
        if (!this.items_.isEmpty()) {
            return this.items_.peekLast();
        } else {
            return null;
        }
    }

    /**
     * Removes top item of tile.
     * @return Item on top of tile. Removes it from tile.
     */
    public Item removeTopItem() {
        if (!this.items_.isEmpty()) {
            return this.items_.removeLast();
        } else {
            return null;
        }
    }

    /**
     * Checks the tile to gets its character representation Returns empty space
     * when tile is empty
     *
     * @return the character that will represent this tile on the map
     * @author Reed, John
     */
    public char getTopCharacter() {
    	if (entity_ != null) {
            return entity_.getRepresentation();
    	}
         else if (!items_.isEmpty()) {
             return items_.peekLast().getRepresentation();
         }
         else if (terrain_ != null) {
            return terrain_.getRepresentation();
        } else {
            return '▩';
        }
    }


    // <editor-fold desc="SERIALIZATION" defaultstate="collapsed">
    private int s_itemcount;

    protected MapTile() {}

    @Override
    public String getSerTag() {
        return "MapTile";
    }

    protected void linkOther (ArrayDeque<SaveData> refs) {
        items_ = new LinkedList<Item>();
        for (int i = 0; i < s_itemcount; i++) {
            items_.add((Item)refs.pop());
        }
    }

    protected void readOther (ObjectInputStream ois, ArrayDeque<Integer> out_rels) throws IOException, ClassNotFoundException {
        s_itemcount = ois.readInt();
        for (int i = 0; i < s_itemcount; i++) {
            out_rels.addLast(ois.readInt());
        }
    }

    protected void writeOther (ObjectOutputStream oos, HashMap<SaveData, Boolean> saveMap) throws IOException {
        oos.writeInt(items_.size());
        for (int i = 0; i < items_.size(); i++) {
            oos.writeInt(SavedGame.getHash(items_.get(i)));
            saveMap.putIfAbsent(items_.get(i), false);
        }
    }
    // </editor-fold>
}
