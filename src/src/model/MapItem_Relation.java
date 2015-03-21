/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package src.model;

import src.model.constructs.Entity;
import src.model.constructs.items.Item;

/**
 *
 * @author JohnReedLOL
 */
public class MapItem_Relation extends MapDrawableThing_Relation {

    private final Item item_;

    public MapItem_Relation(Map m, Item item) {
        super(m);
        item_ = item;
    }

    public Entity getTheEntityOnTopOfMe() {
        return this.getMapTile().getEntity();
    }

    /**
     * @author John-Michael Reed
     * @param x - x coordinate of tele-port
     * @param y - y coordinate of tele-port
     * @return -1 if an entity is already there, -2 if tele-port location is
     * invalid, -4 if destination is impassable
     */
    public int teleportTo(int new_x, int new_y) {
        MapTile destination = super.getMap().getTile(new_x, new_y);
        if (destination != null) {
            int old_x = this.getMyXCoordinate();
            int old_y = this.getMyYCoordinate();
            super.getMap().getTile(old_x, old_y).removeSpecificItem(item_);
            if (destination.isPassable() != false) { // move the entity
                int error_code = destination.addItem(item_);
                Item landed_on_item = destination.viewTopItem();
                if (landed_on_item != null) { // make the item walked on do stuff
                    landed_on_item.onWalkOver();
                }
                return error_code;
            } else { // put the entity back in its place
                super.getMap().getTile(old_x, old_y).addItem(item_);
                return -4;
            }
        } else {
            return -2;
        }
    }
}
