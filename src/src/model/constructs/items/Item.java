/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package src.model.constructs.items;

import java.io.Serializable;

import src.model.MapItem_Relation;
import src.model.constructs.DrawableThing;
import src.model.constructs.Entity;

/**
 * Class item represents a stackable entity that cannot move itself.
 *
 * @author JohnReedLOL
 */
public abstract class Item extends DrawableThing implements Serializable {

    public Item(String name, char representation,
            boolean goes_in_inventory, boolean is_passable, boolean is_one_shot) {
        super(name, representation);
        goes_in_inventory_ = goes_in_inventory;
        is_passable_ = is_passable;
        is_one_shot_ = is_one_shot;
    }

    private final boolean is_one_shot_;

    private boolean is_passable_;

    public int setPassable(boolean is_passable) {
        is_passable_ = is_passable;
        return 0;
    }

    private boolean goes_in_inventory_;

    /**
     * Checks if item can go in Inventory.
     *
     * @return true if item can be put into inventory, false if not.
     */
    public boolean goesInInventory() {
        return this.goes_in_inventory_;
    }

    /**
     * Returns false because Entities are not passable.
     */
    @Override
    public boolean isPassable() {
        return this.is_passable_;
    }

    /**
     * Checks if item is one shot.
     *
     * @return true if item is one shot, false if not.
     */
    public boolean isOneShot() {
        return this.is_one_shot_;
    }

    // map_relationship_ is used in place of a map_referance_
    private transient MapItem_Relation map_relationship_;

    /**
     * Use this to call functions contained within the MapItem relationship
     *
     * @return map_relationship_
     * @author Reed, John
     */
    public MapItem_Relation getMapRelation() {
        return map_relationship_;
    }

    public void setMapRelation(MapItem_Relation i) {
        map_relationship_ = i;
    }

    /**
     *
     */
    public void onWalkOver() {
        //System.out.println("Item: " + this.toString() + " is being walked on.");
        if (this.isOneShot() && !this.goesInInventory()) {
            this.getMapRelation().getMapTile().removeTopItem();
        }
        // Display.setMessage("Walked on Item: " + this.toString(), 3);
        // this.getMapRelation().hurtWithinRadius(10, 2);
    }

    public String toString() {
        String s = "Item name: " + name_;

        s += "\n map_relationship_: ";
        if (map_relationship_ != null) {
            s += "Not null";
        } else {
            s += "null";
        }

        return s;
    }

    public String getName() {
        return name_;
    }

    /**
     * The use function allows an item to exert its effect on an entity.
     *
     * @param target - The entity that the item will be used on.
     */
    public void use(Entity target) {
        //Display.setMessage("Used Item: " + this.toString() 
        // + " Health: " 
        //+ target.getStatsPack().current_life_
        //+ " Level: " + target.getStatsPack().cached_current_level_, 3)
        //;
    }

    /**
     * The use function also allows an item to exert an effect on another item.
     *
     * @param target - The item that this item will be used upon.
     */
    public void use(Item target) {

    }

    @Override
    public void takeTurn() {

    }
}
