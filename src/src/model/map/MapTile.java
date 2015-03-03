package src.model.map;

import java.util.LinkedList;
import java.util.ListIterator;

import src.model.map.constructs.Entity;
import src.model.map.constructs.Item;
import src.model.map.constructs.Terrain;

/**
 * This class represents a single unit of logical area on the Map. It relates
 * the Terrain, Entities, and Items who share that space.
 */
public final class MapTile {

    private Entity entity_;     // the single Entity occupying this space

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

    public Entity getEntity() {
        return this.entity_;
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
     * Checks the tile to gets its character representation Returns empty space
     * when tile is empty
     *
     * @return the character that will represent this tile on the map
     * @author Reed, John
     */
    public char getTopCharacter() {
        if (entity_ != null && entity_.isVisible()) {
            return entity_.getRepresentation();
        } else if (hasItemRepresentation() == true) {
            char ret = 0;
            for (int i = 0; i < items_.size(); ++i) {
                if (items_.get(i).isVisible()) {
                    ret = items_.get(i).getRepresentation();
                }
            }
            if (ret != 0) {
                return ret;
            } else {
                System.err.println("Impossible error occured in MapTile");
                System.exit(-97);
                return ret;
            }
        } else if (terrain_ != null && terrain_.isVisible()) {
            return terrain_.getRepresentation();
        } else {
            return '▩';
        }
    }

    private boolean hasItemRepresentation() {
        for (int i = 0; i < items_.size(); ++i) {
            if (items_.get(i).isVisible()) {
                return true;
            }
        }
        return false;
    }

    /**
     * Checks the tile for obstacles
     *
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

    private LinkedList<Item> items_;    // the collection of Items in this space

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

    /**
     * Removes top item of tile.
     *
     * @author John-Michael Reed
     * @return -2 if there are no items, -1 if item cannot be found
     */
    public int removeSpecificItem(Item i) {
        if (!this.items_.isEmpty()) {
            boolean found = this.items_.remove(i);
            if (!found) {
                return -2;
            } else {
                return 0;
            }
        } else {
            return -1;
        }
    }

    /**
     * Removes top item of tile.
     *
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
     * Peeks at (does not remove) top item on tile.
     *
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
     * Create a new MapTile object
     * <p>
     * This method does not link this MapTile to any Terrain, Entities, or
     * Items</p>
     *
     * @param x The x position of the MapTile on the Map
     * @param y The y position of the MapTile on the Map
     */
    MapTile(int x, int y) {
        x_ = x;
        y_ = y;
        terrain_ = null;
        entity_ = null;
        items_ = new LinkedList<Item>();
    }

    private Terrain terrain_;   // the Terrain at this space

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

    public Terrain getTerrain() {
        return this.terrain_;
    }

    public final int x_;    // the x coordinate of this area unit
    public final int y_;    // the y coordinate of this area unit
}
