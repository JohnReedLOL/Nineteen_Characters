/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package src.controller;

import java.util.ArrayList;
import src.model.MapEntity_Relation;

import java.io.Serializable;

/**
 *
 * @author JohnReedLOL
 */
abstract public class Entity extends DrawableThing {

    // map_relationship_ is used in place of a map_referance_
    private MapEntity_Relation map_relationship_;

    /**
     * Use this to call functions contained within the MapEntity relationship
     *
     * @return map_relationship_
     * @author Reed, John
     */
    public MapEntity_Relation getMapRelation() {
        return map_relationship_;
    }

    public void setMapRelation(MapEntity_Relation e) {
        map_relationship_ = e;
    }

    @Override
    public boolean isPassable() {
        return false;
    }

    public Entity(String name, char representation,
            int x_respawn_point, int y_respawn_point) {
        super(name, representation);
        map_relationship_ = new MapEntity_Relation(this, x_respawn_point, y_respawn_point);
        inventory_ = new ArrayList<Item>();
    }

    private Occupation occupation_ = null;

    protected final ArrayList<Item> inventory_;
    
    /**
     * 
     * @return Null if list is empty 
     */
    public Item pullFirstItemOutOfInventory() {
        if(! inventory_.isEmpty()) {
        return inventory_.remove(0);
        } else {
            return null;
        }
    }

    // Only 1 equipped item in iteration 1
    protected Item equipped_item_;

    /**
     * @author John-Michael Reed
     * @return error codes: -2, inventory has no item; -1, cannot equip another
     * item
     */
    public int equipInventoryItem() {
        if (!inventory_.isEmpty()) {
            if (equipped_item_ == null) {
                DrawableThingStatsPack to_add = inventory_.get(0).getStatsPack();
                this.stats_pack_.addOn(to_add);
                equipped_item_ = inventory_.get(0);
                inventory_.remove(0); // Very inefficient for large number of items
                return 0;
            } else {
                return -1;
            }
        } else {
            return -2;
        }
    }

    /**
     * @author John-Michael Reed
     * @return error codes: -1 inventory is too full for item
     * [not yet availible]
     */
    public int unEquipInventoryItem() {
        if (true /* Inventory has room */) {
                DrawableThingStatsPack to_remove = equipped_item_.getStatsPack();
                this.stats_pack_.reduceBy(to_remove);
                inventory_.add(equipped_item_);
                equipped_item_ = null;
                return 0;
            } 
        else {
            return -1;
        }
    }

    //private final int max_level_;
    private EntityStatsPack stats_pack_ = new EntityStatsPack();

    public EntityStatsPack getStatsPack() {
        return stats_pack_;
    }

    /**
     * Adds default stats to item stats and updates my_stats_after_powerups
     *
     * @author Jessan
     */
    private void recalculateStats() {
        //my_stats_after_powerups_.equals(my_stats_after_powerups_.add(equipped_item_.get_stats_pack_()));

    }

    public void addItemStatsToMyStats(Item item) {
        stats_pack_.addOn(item.getStatsPack());
    }

    public void subtractItemStatsFromMyStats(Item item) {
        stats_pack_.reduceBy(item.getStatsPack());
    }

    private static final int experience_between_levels = 100;

    /**
     * this function levels up an entity Modified to make it "gain enough
     * experience to level up"
     *
     * @author John
     */
    public void gainEnoughExperienceTolevelUp() {
        // Increases experience up to the next multiple of 100
        stats_pack_.quantity_of_experience_
                = ((stats_pack_.quantity_of_experience_ / 100) * 100) + 100;
        this.levelUp();
    }

    /**
     * this function levels up an entity Stubbed to work with new
     * EntityStatsPack class
     *
     * @author Jessan, John
     */
    public void levelUp() {
        stats_pack_.cached_current_level_ += 1;
        if (occupation_ == null) {
            //levelup normally
            //EntityStatsPack new_stats = new EntityStatsPack();
            //set_default_stats_pack(get_default_stats_pack_().add(new_stats));
            stats_pack_.hardiness_level_ += 1;
        } //if occupation is not null/have an occupation
        else {
            occupation_.change_stats(stats_pack_);
        }
        recalculateStats();
    }

    /**
     * Entities should check their health after they are damaged.
     */
    public void checkHealth() {
        if (stats_pack_.current_life_ < 1) {
            commitSuicide();
        }
    }

    public void commitSuicide() {
        --stats_pack_.lives_left_;
        if (stats_pack_.lives_left_ < 0) {
            System.out.println("game over");
        }
    }

    public void setOccupation(Occupation occupation) {
        occupation_ = occupation;
    }

    public Occupation getOccupation() {
        return occupation_;
    }

    public void addItemToInventory(Item item) {
        inventory_.add(item);
    }

    public String toString() {
        String s = "Entity name: " + name_;

        if (!(equipped_item_ == null)) {
            s += "\n equppied item: " + equipped_item_.name_;
        } else {
            s += "\n equppied item: null";
        }

        s += "\n Inventory " + "(" + inventory_.size() + ")" + ":";
        for (int i = 0; i < inventory_.size(); ++i) {
            s += " " + inventory_.get(i).name_;
        }

        s += "\n";

        s += " map_relationship_: ";
        if (map_relationship_ == null) {
            s += "null";
        } else {
            s += "Not null";
        }

        s += "\n associated with map:" + map_relationship_.isAssociatedWithMap();

        return s;
    }

    // <editor-fold desc="SERIALIZATION" defaultstate="collapsed">
    /*
    protected Entity (String name, char drawableThingChar) {
        super(name, drawableThingChar);
        inventory_ = new ArrayList<Item>();
    }

    protected void ser_linkMap(MapEntity_Relation rel) { map_relationship_ = rel) }

    protected void ser_linkStats(EntityStatsPack pack) {
        stats_pack_ = pack;
    }*/
    // </editor-fold>
}
