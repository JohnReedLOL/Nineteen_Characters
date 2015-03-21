/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package src.model;

import java.awt.Color;

import src.Effect;
import src.model.constructs.Entity;
import src.model.constructs.items.Item;

/**
 * This should be abstract because you can't make drawable things
 *
 * @author JohnMichaelReed
 */
public class MapDrawableThing_Relation {

    private final Map current_map_reference_;

    /**
     * Get map reference
     */
    protected Map getMap() {
        return current_map_reference_;
    }

    public class AreaEffect {

        /**
         * casts an area effect. Set symbol to empty space for no effect on map
         * decals
         *
         * @param x_center - center of area effect
         * @param y_center - center of area effect
         * @param radius - diameter/2 of area effect
         * @author Reed, John-Michael
         */
        public void effectAreaWithinRadius(int radius, int strength, Effect effect) {
            int left_edge = getMyXCoordinate() - radius;
            int right_edge = getMyXCoordinate() + radius;
            int top = getMyYCoordinate() + radius;
            int bottom = getMyYCoordinate() - radius;
            for (int i = top; i >= bottom; --i) {
                for (int j = left_edge; j <= right_edge; ++j) {

                    int reduction = 0;
                    if (effect == Effect.HEAL || effect == Effect.HURT) {
                        int damage_reduction_x = Math.abs(getMyXCoordinate() - j);
                        int damage_reduction_y = Math.abs(getMyYCoordinate() - i);
                        reduction = damage_reduction_x + damage_reduction_y;
                    }

                    repeat(j, i, strength - reduction, effect);
                }
            }
        }
        private int effectDecalDuration_ = 20;

        /**
         * For damage coming from non-entities
         *
         * @param x_pos - x coordinate of effect
         * @param y_pos - y coordinate of effect
         * @param strength - how much effect
         * @param effect - which effect
         */
        public void repeat(int x_pos, int y_pos, int strength, Effect effect) {
            MapTile infliction = current_map_reference_.getTile(x_pos, y_pos);
            if (infliction != null) {
                // If there is no decal, fuck shit up
                if (infliction.getTerrain() != null && !infliction.getTerrain().hasDecal()) {
                    if (effect == Effect.HURT) {
                        infliction.getTerrain().addTempDecal('♨', Color.magenta, effectDecalDuration_);
                    } else if (effect == Effect.HEAL) {
                        infliction.getTerrain().addTempDecal('♥', Color.red, effectDecalDuration_);
                    } else if (effect == Effect.LEVEL) {
                        infliction.getTerrain().addTempDecal('↑', Color.black, effectDecalDuration_);
                    } else if (effect == Effect.KILL) {
                        infliction.getTerrain().addTempDecal('☣', Color.orange, effectDecalDuration_);
                    }
                }
                Entity to_effect = infliction.getEntity();
                if (to_effect != null) {
                    if (effect == Effect.HURT) {
                        to_effect.receiveAttack(strength, null); // kills avatar if health is negative
                    } else if (effect == Effect.HEAL) {
                        to_effect.receiveHeal(strength);
                        to_effect.receiveMana(strength);
                    } else if (effect == Effect.LEVEL) {
                        to_effect.gainEnoughExperienceTolevelUp();
                    } else if (effect == Effect.KILL) {
                        to_effect.commitSuicide();
                    }
                }
            }
        }
    };

    /**
     * This object is actually a function used to call area effects
     *
     * @author John-Michael Reed
     */
    public final AreaEffect areaEffectFunctor = new MapDrawableThing_Relation.AreaEffect();

    @Override
    protected Object clone() throws CloneNotSupportedException {
        return super.clone(); //To change body of generated methods, choose Tools | Templates.
    }

    public MapDrawableThing_Relation(Map m) {
        current_map_reference_ = m;
    }

    private void initguardMap() {
        if (current_map_reference_ == null) {
            System.err.println("Empty map reference, "
                    + "and attempted to access map. Perhaps avatar was never passed a map, or mapview was never passed a map");
        }
    }

    private void initguardTile() {
        if (my_tile_ == null) {
            System.err.println("Empty tile reference, "
                    + "and attempted to access map. Perhaps avatar was never passed a map, or mapview was never passed a map");
        }
    }

    public boolean isAssociatedWithMap() {
        if (current_map_reference_ != null) {
            return true;
        } else {
            return false;
        }
    }

    private MapTile my_tile_ = null;

    /**
     *
     * @return MapTile associated with drawable thing (avatar/entity/item/etc.).
     */
    public MapTile getMapTile() {
        return my_tile_;
    }

    /**
     * Set MapTile that drawable thing (avatar/entity/item/etc.) is on.
     *
     * @param new_tile
     */
    public void setMapTile(MapTile new_tile) {
        my_tile_ = new_tile;
    }

    /**
     *
     * @return x coordinate of tile drawable thing (avatar/entity/item/etc.) is
     * on.
     */
    public int getMyXCoordinate() {
        initguardTile();
        return my_tile_.x_;
    }

    /**
     *
     * @return y coordinate of tile drawable thing (avatar/entity/item/etc.) is
     * on.
     */
    public int getMyYCoordinate() {
        initguardTile();
        return my_tile_.y_;
    }

    /**
     * Moves an entity without removing it from the list of entities
     *
     * @param: entity The entity to be moved
     * @param: x - distance to push in the x direction
     * @param: y - distance to push in the y direction
     * @return error codes: -1 if tile is taken, -2 if entity is null, -3 if
     * entity cannot be found, -4 if tile is off the map
     * @author John-Michael Reed
     */
    public int pushEntityInDirection(Entity e, int delta_x, int delta_y) {
        if (e == null) {
            return -2;
        }
        int old_x = e.getMapRelation().getMyXCoordinate(); //Current directions say attempted
        int old_y = e.getMapRelation().getMyYCoordinate();
        Entity toMove = current_map_reference_.getTile(old_x, old_y).getEntity();
        if (toMove == e) {
            current_map_reference_.getTile(old_x, old_y).removeEntity();
            MapTile move_tile = current_map_reference_.getTile(old_x + delta_x, old_y + delta_y);
            if (!(move_tile == null || move_tile.isPassable() == false)) { // move the entity
                int error_code = move_tile.addEntity(e);
                Item walked_on_item = move_tile.viewTopItem();
                if (walked_on_item != null) { // make the item walked on do stuff
                    walked_on_item.onWalkOver();
                }
                return error_code;
            } else { // put the entity back in its place
                current_map_reference_.getTile(old_x, old_y).addEntity(e);
                return -4;
            }
        } else {
            return -3;
        }
    }
}
