/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package src.model.map;

import src.Effect;
import src.FacingDirection;
import src.model.map.constructs.Entity;
import src.model.map.constructs.Item;
import src.io.view.Display;

/**
 * One line description
 *
 * @author JohnReedLOL
 */
public class MapEntity_Relation extends MapDrawableThing_Relation {

    protected class EntityAreaEffect extends MapDrawableAreaEffect {

        /**
         * Casts a 90 degree wide area effect
         *
         * @author Reed, John-Michael
         */
        public void effectAreaWithinArc(int length, int strength, Effect effect) {
            FacingDirection attack_direction = entity_.getFacingDirection();
            final int x_start = entity_.getMapRelation().getMyXCoordinate();
            final int y_start = entity_.getMapRelation().getMyYCoordinate();
            for (int len = 1; len <= length; ++len) {
                int reduction = 0;
                if (effect == Effect.HEAL || effect == Effect.HURT) {
                    reduction = len-1;
                }
                for (int width = -len; width >= len; ++width) {
                    switch (attack_direction) {
                        case UP:
                            repeat(x_start + width, y_start + length, strength - reduction, effect);
                            break;
                        case DOWN:
                            repeat(x_start + width, y_start - length, strength - reduction, effect);
                            break;
                        case RIGHT:
                            repeat(x_start + length, y_start + width, strength - reduction, effect);
                            break;
                        case LEFT:
                            repeat(x_start - length, y_start + width, strength - reduction, effect);
                            break;
                        case UP_RIGHT:
                            repeat(x_start + width + length, y_start + width + length, strength - reduction, effect);
                            break;
                        case UP_LEFT:
                            repeat(x_start + width - length, y_start + width + length, strength - reduction, effect);
                            break;
                        case DOWN_RIGHT:
                            repeat(x_start + width + length, y_start + width - length, strength - reduction, effect);
                            break;
                        case DOWN_LEFT:
                            repeat(x_start + width - length, y_start + width - length, strength - reduction, effect);
                            break;
                    }

                }
            }
        }

        /**
         * Does area damage in a line
         *
         * @author Reed, John-Michael
         */
        public void effectAreaWithinLine(int length, int strength, Effect effect) {
            FacingDirection attack_direction = entity_.getFacingDirection();
            final int x_start = entity_.getMapRelation().getMyXCoordinate();
            final int y_start = entity_.getMapRelation().getMyYCoordinate();
            for (int i = 1; i <= length; ++i) {
                int reduction = 0;
                if (effect == Effect.HEAL || effect == Effect.HURT) {
                    reduction = i-1;
                }
                switch (attack_direction) {
                    case UP:
                        repeat(x_start, y_start + i, strength - reduction, effect);
                        break;
                    case DOWN:
                        repeat(x_start, y_start - i, strength - reduction, effect);
                        break;
                    case RIGHT:
                        repeat(x_start + i, y_start, strength - reduction, effect);
                        break;
                    case LEFT:
                        repeat(x_start - i, y_start, strength - reduction, effect);
                        break;
                    case UP_RIGHT:
                        repeat(x_start + i, y_start + i, strength - reduction, effect);
                        break;
                    case UP_LEFT:
                        repeat(x_start - i, y_start + i, strength - reduction, effect);
                        break;
                    case DOWN_RIGHT:
                        repeat(x_start + i, y_start - i, strength - reduction, effect);
                        break;
                    case DOWN_LEFT:
                        repeat(x_start - i, y_start - i, strength - reduction, effect);
                        break;
                }
            }
        }
    };

    /**
     * @author John-Michael Reed
     * @return -1 if no item can be dropped (inventory empty)
     */
    public int dropItem() {
        Item itemToBeDropped = entity_.pullFirstItemOutOfInventory();
        if (itemToBeDropped != null) {
            current_map_reference_.addItem(itemToBeDropped, this.getMapTile().x_, this.getMapTile().y_,
                    itemToBeDropped.getMapRelation().isPassable(), itemToBeDropped.getMapRelation().isOneShot());
            Display.setMessage("Dropped item: " + itemToBeDropped.name_, 3);
            return 0;
        } else {
            Display.setMessage("You have no items to drop.", 3);
            return -1;
        }
    }

    private final Entity entity_;

    public MapEntity_Relation(Map m, Entity entity,
            int x_respawn_point, int y_respawn_point) {
        super(m);
        entity_ = entity;
        x_respawn_point_ = x_respawn_point;
        y_respawn_point_ = y_respawn_point;
    }

    /**
     * Moves the entity that this relation refers to over x and up y
     *
     * @param x x displacement
     * @param y y displacement
     * @return error codes: see function pushEntityInDirection() in
     * MapDrawableThing_Relation
     * @author John-Michael Reed
     */
    public int moveInDirection(int x, int y) {
        if (x == 0 && y == 0) {
            //nothing
        } else if (x == 0 && y > 0) {
            entity_.setFacingDirection(FacingDirection.UP);
        } else if (x == 0 && y < 0) {
            entity_.setFacingDirection(FacingDirection.DOWN);
        } else if (x > 0 && y == 0) {
            entity_.setFacingDirection(FacingDirection.RIGHT);
        } else if (x < 0 && y == 0) {
            entity_.setFacingDirection(FacingDirection.LEFT);
        } else if (x > 0 && y > 0) {
            entity_.setFacingDirection(FacingDirection.UP_RIGHT);
        } else if (x > 0 && y < 0) {
            entity_.setFacingDirection(FacingDirection.DOWN_RIGHT);
        } else if (x < 0 && y > 0) {
            entity_.setFacingDirection(FacingDirection.UP_LEFT);
        } else if (x < 0 && y > 0) {
            entity_.setFacingDirection(FacingDirection.DOWN_LEFT);
        }
        return super.pushEntityInDirection(entity_, x, y);
    }

    /**
     * An item underneath you can be picked up using the parameters 0,0. 0 if
     * item is picked up successfully, -1 if no item is on the specified tile.
     *
     * @author John-Michael Reed
     * @param x
     * @param y
     * @return error_code
     */
    public int pickUpItemInDirection(int x, int y) {
        int error_code = -1;

        Item itemToBePickedUp = current_map_reference_.removeTopItem(x + getMyXCoordinate(), y + getMyYCoordinate());
        if (itemToBePickedUp != null) {
            entity_.addItemToInventory(itemToBePickedUp);
            Display.setMessage("Picked up item: " + itemToBePickedUp.name_, 3);
            error_code = 0;
        } else {
            Display.setMessage("There is nothing here to pick up.", 3);
        }

        return error_code;
    }

    public void sendAttack(int x, int y) {

    }

    public void spawn(Entity toSpawn, int time_until_spawn) {
        //super.pushEntityInDirection(toSpawn, x_respawn_point_, y_respawn_point_);
    }

    private final int x_respawn_point_;
    private final int y_respawn_point_;
}
