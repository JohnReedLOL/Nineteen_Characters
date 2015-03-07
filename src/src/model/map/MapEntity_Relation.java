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
import src.io.view.display.Display;

import java.awt.Color;
import java.util.regex.*;
import src.model.map.constructs.PickupableItem;

/**
 * One line description
 *
 * @author JohnReedLOL
 */
public class MapEntity_Relation extends MapDrawableThing_Relation {

    public class AreaEffect extends MapDrawableThing_Relation.AreaEffect {

        /**
         * For damage coming from entities
         *
         * @param x_pos - x coordinate of effect
         * @param y_pos - y coordinate of effect
         * @param strength - how much effect
         * @param effect - which effect
         */
        @Override
        public void repeat(int x_pos, int y_pos, int strength, Effect effect) {
            MapTile infliction = current_map_reference_.getTile(x_pos, y_pos);
            if (infliction != null) {
                // If there is no decal, fuck shit up
                if (infliction.getTerrain() != null && !infliction.getTerrain().hasDecal()) {
                    if (effect == Effect.HURT) {
                        infliction.getTerrain().addDecal('♨',Color.magenta);
                    } else if (effect == Effect.HEAL) {
                        infliction.getTerrain().addDecal('♥',Color.red);
                    } else if (effect == Effect.LEVEL) {
                        infliction.getTerrain().addDecal('↑',Color.black);
                    } else if (effect == Effect.KILL) {
                        infliction.getTerrain().addDecal('☣',Color.orange);
                    }
                }
                Entity to_effect = infliction.getEntity();
                if (to_effect != null) {
                    if (effect == Effect.HURT) {
                        to_effect.receiveAttack(strength, entity_); // kills avatar if health is negative
                    } else if (effect == Effect.HEAL) {
                        to_effect.receiveHeal(strength);
                    } else if (effect == Effect.LEVEL) {
                        to_effect.commitSuicide();
                    } else if (effect == Effect.KILL) {
                        to_effect.gainEnoughExperienceTolevelUp();
                    }
                }
            }
        }

        /**
         * Casts a 90 degree wide area effect
         *
         * @author Reed, John-Michael
         */
        public void effectAreaWithinArc(int length, int strength, Effect effect) {
            if (length < 0 || strength < 0) {
                System.exit(-1);
            }
            FacingDirection attack_direction = entity_.getFacingDirection();
            final int x_start = entity_.getMapRelation().getMyXCoordinate();
            final int y_start = entity_.getMapRelation().getMyYCoordinate();
            for (int i = 1; i <= length; ++i) {
                int reduction = 0;
                if (effect == Effect.HEAL || effect == Effect.HURT) {
                    reduction = i - 1;
                }
                for (int width = -i + 1; width <= i - 1; ++width) {
                    switch (attack_direction) {
                        case UP:
                            repeat(x_start + width, y_start + i, strength - reduction, effect);
                            break;
                        case DOWN:
                            repeat(x_start + width, y_start - i, strength - reduction, effect);
                            break;
                        case RIGHT:
                            repeat(x_start + i, y_start + width, strength - reduction, effect);
                            break;
                        case LEFT:
                            repeat(x_start - i, y_start + width, strength - reduction, effect);
                            break;
                        case UP_RIGHT:
                            repeat(x_start + width + i, y_start - width + i, strength - reduction, effect);
                            break;
                        case UP_LEFT:
                            repeat(x_start - width - i, y_start - width + i, strength - reduction, effect);
                            break;
                        case DOWN_RIGHT:
                            repeat(x_start + width + i, y_start + width - i, strength - reduction, effect);
                            break;
                        case DOWN_LEFT:
                            repeat(x_start - width - i, y_start + width - i, strength - reduction, effect);

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
                    reduction = i - 1;
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
     * This object is actually a function used to call area effects
     *
     * @author John-Michael Reed
     */
    public final AreaEffect areaEffectFunctor = new MapEntity_Relation.AreaEffect();

    /**
     * @author John-Michael Reed
     * @return -1 if no item can be dropped (inventory empty)
     */
    public int dropItem() {
        Item itemToBeDropped = entity_.pullLastItemOutOfInventory();
        if (itemToBeDropped != null) {
            current_map_reference_.addItem(itemToBeDropped, this.getMapTile().x_, this.getMapTile().y_);
            Display.getDisplay().setMessage("Dropped item: " + itemToBeDropped.name_);
            return 0;
        } else {
            Display.getDisplay().setMessage("You have no items to drop.");
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
        } else if (x < 0 && y < 0) {
            entity_.setFacingDirection(FacingDirection.DOWN_LEFT);
        } else {
            System.exit(-1); // Impossible
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
     * @return error_code: return -2 if item is not pickupable
     */
    public int pickUpItemInDirection(int x, int y) {
        int error_code = -1;

        Item itemToBePickedUp = current_map_reference_.removeTopItem(x + getMyXCoordinate(), y + getMyYCoordinate());
        if (itemToBePickedUp != null) {
            try {
                entity_.addItemToInventory((PickupableItem) itemToBePickedUp);
            } catch (ClassCastException c) {
                return -2;
            }
            Display.getDisplay().setMessage("Picked up item: " + itemToBePickedUp.name_);
            error_code = 0;
        } else {
            Display.getDisplay().setMessage("There is nothing here to pick up.");
        }

        return error_code;
    }

    /**
     * Causes an entity to tele-port to the place where it was spawned
     *
     * @param toSpawn
     * @return -1 if respawn point is occupied
     */
    public int respawn() {
        //super.pushEntityInDirection(toSpawn, x_respawn_point_, y_respawn_point_);
        int error_code = this.teleportTo(x_respawn_point_, x_respawn_point_);
        if (error_code != 0) {
            error_code = this.teleportTo(x_respawn_point_ + 1, x_respawn_point_);
            if (error_code != 0) {
                return this.teleportTo(x_respawn_point_, x_respawn_point_ + 1);
            }
        }
        return 0;
    }

    /**
     * Sends an attack over x and up y.
     *
     * @author John-Michael Reed
     * @param x - x position of attack relative to sender
     * @param y - y position of attack relative to sender
     * @return -1 if tile is off the map, -2 if entity does not exist
     */
    public int sendAttack(int x, int y) {
        MapTile target_tile = this.current_map_reference_.getTile(x, y);
        if (target_tile == null) {
            return -1;
        } else {
            Entity target_entity = target_tile.getEntity();
            if (target_entity == null) {
                return -2;
            } else {
                target_entity.receiveAttack(3 + entity_.getStatsPack().getOffensive_rating_(), entity_);
                return 0;
            }
        }
    }

    /**
     * Sends an attack to an entity.
     *
     * @author John-Michael Reed
     * @param target - entity to hit
     * @return -1 if target is null, 0 if success
     */
    public int sendAttack(Entity target_entity) {
        if (target_entity == null) {
            return -1;
        } else {
            target_entity.receiveAttack(3 + entity_.getStatsPack().getOffensive_rating_(), entity_);
            return 0;
        }
    }

    /**
     * Sends an attack in the direction the entity is facing.
     *
     * @author John-Michael Reed
     * @return -1 if tile is off the map, -2 if entity does not exist
     */
    public int sendAttack() {
        int error_code = 0;
        FacingDirection f = entity_.getFacingDirection();
        switch (f) {
            case UP:
                error_code = sendAttack(0, 1);
                break;
            case DOWN:
                error_code = sendAttack(0, -1);
                break;
            case LEFT:
                error_code = sendAttack(-1, 0);
                break;
            case RIGHT:
                error_code = sendAttack(1, 0);
                break;
            case UP_RIGHT:
                error_code = sendAttack(1, 1);
                break;
            case UP_LEFT:
                error_code = sendAttack(-1, 1);
                break;
            case DOWN_RIGHT:
                error_code = sendAttack(1, -1);
                break;
            case DOWN_LEFT:
                error_code = sendAttack(-1, -1);
                break;
        }
        return error_code;
    }

    /**
     * Sends a greeting to an entity.
     *
     * @author John-Michael Reed
     * @return reply string
     */
    public String sendGreeting(Entity target) {
        String greeting = "hello";
        String reply = "";
        if (target != null) {
            reply = target.reply(greeting, this.entity_);
        }
        return reply;
    }

    /**
     * Sends a greeting in the direction the entity is facing.
     *
     * @author John-Michael Reed
     * @return reply string
     */
    public String sendGreeting() {
        int error_code = 0;
        String greeting = "hello";
        String reply = "";
        FacingDirection f = entity_.getFacingDirection();
        final int x = this.getMyXCoordinate();
        final int y = this.getMyYCoordinate();
        MapTile target_tile = null;
        switch (f) {
            case UP:
                target_tile = current_map_reference_.getTile(x, y + 1);
                if (target_tile != null) {
                    Entity target = target_tile.getEntity();
                    reply = sendGreeting(target);
                }
                break;
            case DOWN:
                target_tile = current_map_reference_.getTile(x, y - 1);
                if (target_tile != null) {
                    Entity target = target_tile.getEntity();
                    reply = sendGreeting(target);
                }
                break;
            case RIGHT:
                target_tile = current_map_reference_.getTile(x + 1, y);
                if (target_tile != null) {
                    Entity target = target_tile.getEntity();
                    reply = sendGreeting(target);
                }
                break;
            case LEFT:
                target_tile = current_map_reference_.getTile(x - 1, y);
                if (target_tile != null) {
                    Entity target = target_tile.getEntity();
                    reply = sendGreeting(target);
                }
                break;
            case UP_RIGHT:
                target_tile = current_map_reference_.getTile(x + 1, y + 1);
                if (target_tile != null) {
                    Entity target = target_tile.getEntity();
                    reply = sendGreeting(target);
                }
                break;
            case UP_LEFT:
                target_tile = current_map_reference_.getTile(x - 1, y + 1);
                if (target_tile != null) {
                    Entity target = target_tile.getEntity();
                    reply = sendGreeting(target);
                }
                break;
            case DOWN_RIGHT:
                target_tile = current_map_reference_.getTile(x + 1, y - 1);
                if (target_tile != null) {
                    Entity target = target_tile.getEntity();
                    reply = sendGreeting(target);
                }
                break;
            case DOWN_LEFT:
                target_tile = current_map_reference_.getTile(x - 1, y - 1);
                if (target_tile != null) {
                    Entity target = target_tile.getEntity();
                    reply = sendGreeting(target);
                }
                break;
        }
        return reply;
    }

    /**
     * @author John-Michael Reed
     *
     * @param x - x coordinate of tele-port
     * @param y - y coordinate of tele-port
     * @return -1 if an entity is already there, -2 if tele-port location is
     * invalid, -4 if destination is impassable
     */
    public int teleportTo(int new_x, int new_y) {
        MapTile destination = current_map_reference_.getTile(new_x, new_y);
        if (destination == null) {
            return -2;
        } else {
            int old_x = this.getMyXCoordinate();
            int old_y = this.getMyYCoordinate();
            current_map_reference_.getTile(old_x, old_y).removeEntity();
            if (destination.isPassable() == false) { // put the entity back in its place
                current_map_reference_.getTile(old_x, old_y).addEntity(entity_);
                return -4;
            } else { // move the entity
                int error_code = destination.addEntity(entity_);
                Item landed_on_item = destination.viewTopItem();
                if (landed_on_item != null) { // make the item walked on do stuff
                    landed_on_item.onWalkOver();
                }
                return error_code;
            }
        }
    }

    private final int x_respawn_point_;
    private final int y_respawn_point_;
}
