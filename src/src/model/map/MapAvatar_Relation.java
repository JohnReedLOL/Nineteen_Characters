/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package src.model.map;

import java.util.LinkedList;
import src.model.map.constructs.Avatar;
import src.model.map.constructs.Entity;
import src.model.map.constructs.Item;

/**
 * Handles interactions between map + avatar that neither should otherwise be
 * othered from.
 *
 * @author JohnMichaelReed
 */
public class MapAvatar_Relation extends MapEntity_Relation {

    private final Avatar avatar_;

    /**
     * Gets Avatar associated with the relation.
     *
     * @return avatar
     */
    public Avatar getAvatar() {
        return avatar_;
    }

    /**
     * MapAvatar_Relation constructor. Inherits from MapEntity_Relation.
     *
     * @param avatar
     * @param x_respawn_point
     * @param y_respawn_point
     */
    public MapAvatar_Relation(Map m, Avatar avatar,
            int x_respawn_point, int y_respawn_point) {
        super(m, avatar, x_respawn_point, y_respawn_point);
        avatar_ = avatar;
    }

    /**
     * This function will be called from observe() to get info for a tile at
     * (x,y).
     * @author Reid Olsen
     * @param x coordinate of tile relative to avatar.
     * @param y coordinate of tile relative to avatar.
     * @return String of info on tile (x,y).
     */
    public String getTileInfo(int relative_x, int relative_y) {
        final int x = relative_x + getMyXCoordinate();
        final int y = relative_y + getMyYCoordinate();
        String s = "";
        if (this.getMap().getTile(x, y).isPassable()) {
            s += "This tile is passable.";
        } else {
            s += "This tile is not passable.";
        }
        LinkedList<Item> items = this.getMap().getTile(x, y)
                .getItemList();
        if (!items.isEmpty()) {
            s += " Items on this tile:";
            for (int j = 0; j < items.size(); j++) {
                s += " " + items.get(j).name_;
                if (j + 1 == items.size()) {
                    s += ".";
                } else {
                    s += ",";
                }
            }
        }
        Entity e = this.getMap().getTile(x, y).getEntity();
        if (e != null) {
            if (avatar_.getObservation_() < 3) {
                s += " Entity: " + e.name_;
            } else if (avatar_.getObservation_() >= 3 && avatar_.getObservation_() < 6) {
                s += " Entity: " + e.name_ + " with "
                        + e.getStatsPack().getOffensive_rating_() + " offense.";
            } else {
                s += " Entity: " + e.name_ + " with "
                        + e.getStatsPack().getOffensive_rating_()
                        + " offense and "
                        + e.getStatsPack().getDefensive_rating_() + " defense.";
            }
        }

        return s;
    }
}
