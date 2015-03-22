/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package src.model;

import java.awt.Color;
import java.io.Serializable;
import java.util.LinkedList;

import src.Effect;
import src.FacingDirection;
import src.io.view.display.Display;
import src.model.constructs.Avatar;
import src.model.constructs.DrawableThing;
import src.model.constructs.Entity;
import src.model.constructs.Merchant;
import src.model.constructs.Monster;
import src.model.constructs.Villager;
import src.model.constructs.items.Item;
import src.model.constructs.items.PickupableItem;
import src.model.constructs.items.Trap;

/**
 * One line description
 *
 * @author JohnReedLOL
 */
public class MapEntity_Relation extends MapDrawableThing_Relation {

    //<editor-fold desc="Non-static fields" defaultstate="collapsed">
    private transient final Entity entity_;
    private final int x_respawn_point_;
    private final int y_respawn_point_;

    //</editor-fold>
    //<editor-fold desc="Constructors" defaultstate="collapsed">
    public MapEntity_Relation(Map m, Entity entity, int x_respawn_point,
            int y_respawn_point) {
        super(m);
        entity_ = entity;
        x_respawn_point_ = x_respawn_point;
        y_respawn_point_ = y_respawn_point;
        if (entity.getMapRelation() != null && entity.getMapRelation() != this) {
            this.setMapTile(entity.getMapRelation().getMapTile());
        }
    }

    //</editor-fold>
    //<editor-fold desc="Accessors" defaultstate="collapsed">
    protected Entity getEntity() {
        return entity_;
    }

    public int getXrespawnPoint() {
        return x_respawn_point_;
    }

    public int getYrespawnPoint() {
        return y_respawn_point_;
    }

    //</editor-fold> 
    //<editor-fold desc="MapEntity Methods" defaultstate="collapsed">
    /**
     * @author John-Michael Reed
     * @return -1 if no item can be dropped (inventory empty)
     */
    public int dropItem() {
        Item itemToBeDropped = entity_.pullLastItemOutOfInventory();
        if (itemToBeDropped != null) {
            super.getMap().addItem(itemToBeDropped,
                    this.getMapTile().x_, this.getMapTile().y_);
            Display.getDisplay().setMessage(
                    "Dropped item: " + itemToBeDropped.name_);
            return 0;
        } else {
            Display.getDisplay().setMessage("You have no items to drop.");
            return -1;
        }
    }

    /**
     * Turns an entity's MapEntityRelation into a MapKnight_Relation
     */
    public void becomeKnightRelation() {
        entity_.setMapRelation(new MapKnight_Relation(super.getMap(),
                this.entity_, this.x_respawn_point_, this.y_respawn_point_));
    }

    public void becomeEntityRelation() {
        entity_.setMapRelation(new MapEntity_Relation(super.getMap(),
                this.entity_, this.x_respawn_point_, this.y_respawn_point_));
    }

    /**
     * Gets positive distance between entity_ [owner of this relation] and a
     * DrawableThing if that DrawableThing has a valid map relation.
     *
     * @param entity - Drawable that my entity_ wants to measure its distance
     * from.
     * @return -1 on failure [entity has no map relation], 0 or greater on
 success.
     */
    public double measureDistanceTowardEntity(Entity entity) {
        if (entity == null || entity.getMapRelation() == null || !entity.hasLivesLeft()) {
            return -1; // This thing cannot have its position ascertained without a map relation.
        }
        final int drawables_x = entity.getMapRelation().getMyXCoordinate();
        final int drawables_y = entity.getMapRelation().getMyYCoordinate();
        final int my_x = this.getMyXCoordinate();
        final int my_y = this.getMyYCoordinate();
        final double x_distance = Math.abs(drawables_x - my_x);
        final double y_distance = Math.abs(drawables_y - my_y);
        final double pythagoras_distance = Math.sqrt(Math.pow(x_distance, 2) + Math.pow(y_distance, 2));
        return pythagoras_distance;
    }

    /**
     * This function will be called from observe() to get info for a tile at
     * (x,y).
     *
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
        LinkedList<Item> items = this.getMap().getTile(x, y).getItemList();
        if (!items.isEmpty()) {
            s += " Items on this tile:";
            for (int j = 0; j < items.size(); j++) {
                s += " " + items.get(j).name_;
                if (j + 1 != items.size()) {
                    s += ",";
                } else {
                    s += ".";
                }
            }
        }
        Entity e = this.getMap().getTile(x, y).getEntity();
        if (e != null) {
            if (entity_.getObservation_() < 3) {
                if (e instanceof Monster) {
                    s += " Monster: " + e.name_;
                } else if (e instanceof Villager) {
                    s += " Villager: " + e.name_;
                } else if (e instanceof Merchant) {
                    s += " Merchant: " + e.name_;
                } else if (e instanceof Avatar) {
                    s += " Avatar: " + e.name_;
                } else {
                    s += " Entity: " + e.name_;
                }
            } else if (entity_.getObservation_() >= 3
                    && entity_.getObservation_() < 6) {
                if (e instanceof Monster) {
                    s += " Monster: " + e.name_ + " with "
                            + e.getStatsPack().getOffensive_rating_()
                            + " offense.";
                } else if (e instanceof Villager) {
                    s += " Villager: " + e.name_ + " with "
                            + e.getStatsPack().getOffensive_rating_()
                            + " offense.";
                } else if (e instanceof Merchant) {
                    s += " Merchant: " + e.name_ + " with "
                            + e.getStatsPack().getOffensive_rating_()
                            + " offense.";
                } else if (e instanceof Avatar) {
                    s += " Avatar: " + e.name_ + " with "
                            + e.getStatsPack().getOffensive_rating_()
                            + " offense.";
                } else {
                    s += " Entity: " + e.name_ + " with "
                            + e.getStatsPack().getOffensive_rating_()
                            + " offense.";
                }
            } else {
                if (e instanceof Monster) {
                    s += " Monster: " + e.name_ + " with "
                            + e.getStatsPack().getOffensive_rating_()
                            + " offense and "
                            + e.getStatsPack().getDefensive_rating_()
                            + " defense.";
                } else if (e instanceof Villager) {
                    s += " Villager: " + e.name_ + " with "
                            + e.getStatsPack().getOffensive_rating_()
                            + " offense and "
                            + e.getStatsPack().getDefensive_rating_()
                            + " defense.";
                } else if (e instanceof Merchant) {
                    s += " Merchant: " + e.name_ + " with "
                            + e.getStatsPack().getOffensive_rating_()
                            + " offense and "
                            + e.getStatsPack().getDefensive_rating_()
                            + " defense.";
                } else if (e instanceof Avatar) {
                    s += " Avatar: " + e.name_ + " with "
                            + e.getStatsPack().getOffensive_rating_()
                            + " offense and "
                            + e.getStatsPack().getDefensive_rating_()
                            + " defense.";
                } else {
                    s += " Entity: " + e.name_ + " with "
                            + e.getStatsPack().getOffensive_rating_()
                            + " offense and "
                            + e.getStatsPack().getDefensive_rating_()
                            + " defense.";
                }
            }
        }

        return s;
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
            // nothing
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
            System.err.print("An impossible error occured in MapEntity_Relation.moveInDirection()");
            System.exit(-1); // Impossible
        }
        return super.pushEntityInDirection(entity_, x, y);
    }

    /**
     * Moves entity_ [owner of this relation] towards a DrawableThing if it has
     * a valid map relation.
     *
     * @param entity - Thing that entity wants to move toward.
     * @return -1 on failure [entity has no map relation or path is blocked],
 0 on success [move occured]
     */
    public int moveTowardEntity(Entity entity) {
        if (entity == null || entity.getMapRelation() == null || ! entity.hasLivesLeft()) {
            return -1; // This thing cannot have its position ascertained without a map relation.
        }
        final int drawables_x = entity.getMapRelation().getMyXCoordinate();
        final int drawables_y = entity.getMapRelation().getMyYCoordinate();
        final int my_x = this.getMyXCoordinate();
        final int my_y = this.getMyYCoordinate();
        final int delta_x = drawables_x - my_x;
        final int delta_y = drawables_y - my_y;
        final int amount_to_move_in_x_direction;
        if (delta_x > 0) {
            amount_to_move_in_x_direction = 1;
        } else if ((delta_x < 0)) {
            amount_to_move_in_x_direction = -1;
        } else {
            amount_to_move_in_x_direction = 0;
        }
        final int amount_to_move_in_y_direction;
        if (delta_y > 0) {
            amount_to_move_in_y_direction = 1;
        } else if ((delta_y < 0)) {
            amount_to_move_in_y_direction = -1;
        } else {
            amount_to_move_in_y_direction = 0;
        }
        return this.moveInDirection(amount_to_move_in_x_direction, amount_to_move_in_y_direction);
    }
    
    public int moveAwayFromEntity(Entity entity) {
        if (entity == null || entity.getMapRelation() == null || ! entity.hasLivesLeft()) {
            return -1; // This thing cannot have its position ascertained without a map relation.
        }
        final int drawables_x = entity.getMapRelation().getMyXCoordinate();
        final int drawables_y = entity.getMapRelation().getMyYCoordinate();
        final int my_x = this.getMyXCoordinate();
        final int my_y = this.getMyYCoordinate();
        final int delta_x = drawables_x - my_x;
        final int delta_y = drawables_y - my_y;
        final int amount_to_move_in_x_direction;
        if (delta_x > 0) {
            amount_to_move_in_x_direction = -1;
        } else if ((delta_x < 0)) {
            amount_to_move_in_x_direction = 1;
        } else {
            amount_to_move_in_x_direction = 0;
        }
        final int amount_to_move_in_y_direction;
        if (delta_y > 0) {
            amount_to_move_in_y_direction = -1;
        } else if ((delta_y < 0)) {
            amount_to_move_in_y_direction = 1;
        } else {
            amount_to_move_in_y_direction = 0;
        }
        return this.moveInDirection(amount_to_move_in_x_direction, amount_to_move_in_y_direction);
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
        if (entity_.hasLivesLeft()) {
            Item itemToBePickedUp = super.getMap().removeTopItem(x
                    + getMyXCoordinate(), y + getMyYCoordinate());
            if (itemToBePickedUp != null) {
                try {
                    entity_.addItemToInventory((PickupableItem) itemToBePickedUp);
                } catch (ClassCastException c) {
                    return -2;
                }
                Display.getDisplay().setMessage(
                        itemToBePickedUp.name_ + " was picked up off the map!");
                error_code = 0;
            }
        } else {
            // Dead men cannot pick up items.
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
        System.out.println("Entity [or subclass] is respawning");
        int error_code = this.teleportTo(x_respawn_point_, y_respawn_point_);
        if (error_code != 0) {
            error_code = this
                    .teleportTo(x_respawn_point_ + 1, y_respawn_point_);
            if (error_code != 0) {
                return this.teleportTo(x_respawn_point_, y_respawn_point_ + 1);
            }
        }
        // set health and manna to max on respawn
        entity_.getStatsPack().increaseCurrentLifeBy(Integer.MAX_VALUE);
        entity_.getStatsPack().increaseCurrentManaBy(Integer.MAX_VALUE);
        // reset money on respawn
        entity_.reinstateNumGoldCoins();
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
    public int sendAttackToRelativePosition(int x, int y) {
        MapTile target_tile = super.getMap().getTile(
                getMyXCoordinate() + x, getMyYCoordinate() + y);
        if (target_tile != null) {
            Entity target_entity = target_tile.getEntity();
            if (target_entity == null) {
                return -2;
            } else {
                System.out.println("You attacking an entity");
                target_entity.receiveAttack(3 + entity_.getStatsPack()
                        .getOffensive_rating_(), entity_);
                return 0;
            }
        } else {
            return -1;
        }
    }

    public void removeMyselfFromTheMapCompletely() {
        super.getMap().removeEntity(entity_);
    }

    /**
     * Sends an attack to absolute position (x,y)
     *
     * @author John-Michael Reed
     * @param x - x position of attack
     * @param y - y position of attack
     * @return -1 if tile is off the map, -2 if entity does not exist
     */
    public int sendAttackToAbsolutePosition(int x, int y) {
        MapTile target_tile = super.getMap().getTile(x, y);
        if (target_tile == null) {
            Entity target_entity = target_tile.getEntity();
            if (target_entity == null) {
                return -2;
            } else {
                target_entity.receiveAttack(3 + entity_.getStatsPack()
                        .getOffensive_rating_(), entity_);
                return 0;
            }
        } else {
            return -1;
        }
    }

    /**
     * Sends an attack in the direction the entity is facing.
     *
     * @author John-Michael Reed
     * @return -1 if tile is off the map, -2 if entity does not exist
     */
    public int sendAttackInFacingDirection() {
        int error_code = 0;
        FacingDirection f = entity_.getFacingDirection();
        switch (f) {
            case UP:
                error_code = sendAttackToRelativePosition(0, 1);
                break;
            case DOWN:
                error_code = sendAttackToRelativePosition(0, -1);
                break;
            case LEFT:
                error_code = sendAttackToRelativePosition(-1, 0);
                break;
            case RIGHT:
                error_code = sendAttackToRelativePosition(1, 0);
                break;
            case UP_RIGHT:
                error_code = sendAttackToRelativePosition(1, 1);
                break;
            case UP_LEFT:
                error_code = sendAttackToRelativePosition(-1, 1);
                break;
            case DOWN_RIGHT:
                error_code = sendAttackToRelativePosition(1, -1);
                break;
            case DOWN_LEFT:
                error_code = sendAttackToRelativePosition(-1, -1);
                break;
        }
        return error_code;
    }

    /**
     * Gets the Entity you are facing
     *
     * @author John-Michael Reed
     * @return null if no entity is there.
     */
    public Entity getEntityInFacingDirection() {
        MapTile target_tile = null;
        final int x = this.getMyXCoordinate();
        final int y = this.getMyYCoordinate();
        FacingDirection f = entity_.getFacingDirection();
        switch (f) {
            case UP:
                target_tile = super.getMap().getTile(x, y + 1);
                if (target_tile != null) {
                    return target_tile.getEntity();
                }
                break;
            case DOWN:
                target_tile = super.getMap().getTile(x, y - 1);
                if (target_tile != null) {
                    return target_tile.getEntity();
                }
                break;
            case RIGHT:
                target_tile = super.getMap().getTile(x + 1, y);
                if (target_tile != null) {
                    return target_tile.getEntity();
                }
                break;
            case LEFT:
                target_tile = super.getMap().getTile(x - 1, y);
                if (target_tile != null) {
                    return target_tile.getEntity();
                }
                break;
            case UP_RIGHT:
                target_tile = super.getMap().getTile(x + 1, y + 1);
                if (target_tile != null) {
                    return target_tile.getEntity();
                }
                break;
            case UP_LEFT:
                target_tile = super.getMap().getTile(x - 1, y + 1);
                if (target_tile != null) {
                    return target_tile.getEntity();
                }
                break;
            case DOWN_RIGHT:
                target_tile = super.getMap().getTile(x + 1, y - 1);
                if (target_tile != null) {
                    return target_tile.getEntity();
                }
                break;
            case DOWN_LEFT:
                target_tile = super.getMap().getTile(x - 1, y - 1);
                if (target_tile != null) {
                    return target_tile.getEntity();
                }
                break;
            default:
                System.err
                        .println("Impossible error in getEntityInFacingDirection");
                System.exit(-44);
                break;
        }
        return null;
    }
    

    /**
     * Gets the Entity you are facing
     *
     * @author John-Michael Reed
     * @return null if no entity is there.
     */
    public Entity getEntityInFacingDirection(int range) {
        MapTile target_tile = null;
        int x = this.getMyXCoordinate();
        int y = this.getMyYCoordinate();
        FacingDirection f = entity_.getFacingDirection();
        switch (f) {
            case UP:
            	for (int i = 0; i < range; i++) {
            		y++;
	                target_tile = super.getMap().getTile(x, y);
	                if (target_tile != null) {
	                    Entity entity = target_tile.getEntity();
	                    if (entity != null) {
	                    	return entity;
	                    }
	                }
            	}
                break;
            case DOWN:
            	for (int i = 0; i < range; i++) {
            		y--;
	                target_tile = super.getMap().getTile(x, y);
	                if (target_tile != null) {
	                    Entity entity = target_tile.getEntity();
	                    if (entity != null) {
	                    	return entity;
	                    }
                    }
                }
                break;
            case RIGHT:
            	for (int i = 0; i < range; i++) {
            		x++;
	                target_tile = super.getMap().getTile(x, y);
	                if (target_tile != null) {
	                    Entity entity = target_tile.getEntity();
	                    if (entity != null) {
	                    	return entity;
	                    }
                    }
                }
                break;
            case LEFT:
            	for (int i = 0; i < range; i++) {
            		x--;
	                target_tile = super.getMap().getTile(x, y);
	                if (target_tile != null) {
	                    Entity entity = target_tile.getEntity();
	                    if (entity != null) {
	                    	return entity;
	                    }
                    }
                }
                break;
            case UP_RIGHT:
            	for (int i = 0; i < range; i++) {
            		x++;
            		y++;
	                target_tile = super.getMap().getTile(x, y);
	                if (target_tile != null) {
	                    Entity entity = target_tile.getEntity();
	                    if (entity != null) {
	                    	return entity;
	                    }
	                }
	            }
                break;
            case UP_LEFT:
            	for (int i = 0; i < range; i++) {
            		x--;
            		y++;
	                target_tile = super.getMap().getTile(x, y);
	                if (target_tile != null) {
	                    Entity entity = target_tile.getEntity();
	                    if (entity != null) {
	                    	return entity;
	                    }
                    }
                }
                break;
            case DOWN_RIGHT:
            	for (int i = 0; i < range; i++) {
            		x++;
            		y--;
	                target_tile = super.getMap().getTile(x, y);
	                if (target_tile != null) {
	                    Entity entity = target_tile.getEntity();
	                    if (entity != null) {
	                    	return entity;
	                    }
                    }
                }
                break;
            case DOWN_LEFT:
            	for (int i = 0; i < range; i++) {
            		x--;
            		y--;
	                target_tile = super.getMap().getTile(x, y);
	                if (target_tile != null) {
	                    Entity entity = target_tile.getEntity();
	                    if (entity != null) {
	                    	return entity;
	                    }
                    }
                }
                break;
            default:
                System.err
                        .println("Impossible error in getEntityInFacingDirection");
                System.exit(-44);
                break;
        }
        return null;
    }

    /**
     * Gets the Item you are facing
     *
     * @author John-Michael Reed
     * @return null if no item is there.
     */
    public Item getTopmostItemInFacingDirection() {
        MapTile target_tile = null;
        final int x = this.getMyXCoordinate();
        final int y = this.getMyYCoordinate();
        FacingDirection f = entity_.getFacingDirection();
        switch (f) {
            case UP:
                target_tile = super.getMap().getTile(x, y + 1);
                if (target_tile != null) {
                    return target_tile.viewTopItem();
                }
                break;
            case DOWN:
                target_tile = super.getMap().getTile(x, y - 1);
                if (target_tile != null) {
                    return target_tile.viewTopItem();
                }
                break;
            case RIGHT:
                target_tile = super.getMap().getTile(x + 1, y);
                if (target_tile != null) {
                    return target_tile.viewTopItem();
                }
                break;
            case LEFT:
                target_tile = super.getMap().getTile(x - 1, y);
                if (target_tile != null) {
                    return target_tile.viewTopItem();
                }
                break;
            case UP_RIGHT:
                target_tile = super.getMap().getTile(x + 1, y + 1);
                if (target_tile != null) {
                    return target_tile.viewTopItem();
                }
                break;
            case UP_LEFT:
                target_tile = super.getMap().getTile(x - 1, y + 1);
                if (target_tile != null) {
                    return target_tile.viewTopItem();
                }
                break;
            case DOWN_RIGHT:
                target_tile = super.getMap().getTile(x + 1, y - 1);
                if (target_tile != null) {
                    return target_tile.viewTopItem();
                }
                break;
            case DOWN_LEFT:
                target_tile = super.getMap().getTile(x - 1, y - 1);
                if (target_tile != null) {
                    return target_tile.viewTopItem();
                }
                break;
            default:
                System.err.println("Impossible");
                System.exit(-44);
                break;
        }
        return null;
    }

    /**
     * Checks for trap on (x,y)
     *
     * @author Reid Olsen
     * @param x
     * @param y
     * @return
     */
    public Trap checkForTrap(int x, int y) {
        Trap trap = null;
        try {
            if (super.getMap().getTile(x, y).viewTopItem() instanceof Trap) {
                trap = (Trap) super.getMap().getTile(x, y)
                        .viewTopItem();
            }
        } catch (NullPointerException e) {

        }

        return trap;
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
        MapTile destination = super.getMap().getTile(new_x, new_y);
        if (destination != null) {
            int old_x = this.getMyXCoordinate();
            int old_y = this.getMyYCoordinate();
            super.getMap().getTile(old_x, old_y).removeEntity();
            if (destination.isPassable() == false) { // put the entity back in
                // its place
                super.getMap().getTile(old_x, old_y).addEntity(entity_);
                return -4;
            } else { // move the entity
                int error_code = destination.addEntity(entity_);
                Item landed_on_item = destination.viewTopItem();
                if (landed_on_item != null) { // make the item walked on do
                    // stuff
                    landed_on_item.onWalkOver();
                }
                return error_code;
            }
        } else {
            return -2;
        }
    }
    //</editor-fold> 
    //<editor-fold desc="Area Effect" defaultstate="collapsed">
    /**
     * This object is actually a functor used to call area effects
     *
     * @author John-Michael Reed
     */
    public final transient AreaEffect areaEffectFunctor = new MapEntity_Relation.AreaEffect();

    public class AreaEffect extends MapDrawableThing_Relation.AreaEffect {

        private int areaEffectDamageTimer_ = 20;

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
            MapTile infliction = getMap().getTile(x_pos, y_pos);
            if (infliction != null) {
                // If there is no decal, fuck shit up
                if (infliction.getTerrain() != null
                        && !infliction.getTerrain().hasDecal()) {
                    if (effect == Effect.HURT) {
                        infliction.getTerrain().addTempDecal('♨', Color.magenta, areaEffectDamageTimer_);
                    } else if (effect == Effect.HEAL) {
                        infliction.getTerrain().addTempDecal('♥', Color.red, areaEffectDamageTimer_);
                    } else if (effect == Effect.LEVEL) {
                        infliction.getTerrain().addTempDecal('↑', Color.black, areaEffectDamageTimer_);
                    } else if (effect == Effect.KILL) {
                        infliction.getTerrain().addTempDecal('☣', Color.orange, areaEffectDamageTimer_);
                    }
                }
                Entity to_effect = infliction.getEntity();
                if (to_effect != null) {
                    System.out.println("to_effect not null in MapEntity_Relation.repeat");
                    if (effect == Effect.HURT) {
                        System.out.println("Effect is hurt in MapEntity_Relation.repeat");
                        to_effect.receiveAttack(strength, entity_); // kills
                        System.out.println("Recieve attack completed in. MapEntity_Relation.repeat");
                        // avatar if
                        // health is
                        // negative
                    } else if (effect == Effect.HEAL) {
                        to_effect.receiveHeal(strength);
                    } else if (effect == Effect.LEVEL) {
                        to_effect.gainEnoughExperienceTolevelUp();
                    } else if (effect == Effect.KILL) {
                        to_effect.commitSuicide();
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
                            repeat(x_start + width, y_start + i, strength
                                    - reduction, effect);
                            break;
                        case DOWN:
                            repeat(x_start + width, y_start - i, strength
                                    - reduction, effect);
                            break;
                        case RIGHT:
                            repeat(x_start + i, y_start + width, strength
                                    - reduction, effect);
                            break;
                        case LEFT:
                            repeat(x_start - i, y_start + width, strength
                                    - reduction, effect);
                            break;
                        case UP_RIGHT:
                            repeat(x_start + width + i, y_start - width + i,
                                    strength - reduction, effect);
                            break;
                        case UP_LEFT:
                            repeat(x_start - width - i, y_start - width + i,
                                    strength - reduction, effect);
                            break;
                        case DOWN_RIGHT:
                            repeat(x_start + width + i, y_start + width - i,
                                    strength - reduction, effect);
                            break;
                        case DOWN_LEFT:
                            repeat(x_start - width - i, y_start + width - i,
                                    strength - reduction, effect);

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
                        repeat(x_start + i, y_start + i, strength - reduction,
                                effect);
                        break;
                    case UP_LEFT:
                        repeat(x_start - i, y_start + i, strength - reduction,
                                effect);
                        break;
                    case DOWN_RIGHT:
                        repeat(x_start + i, y_start - i, strength - reduction,
                                effect);
                        break;
                    case DOWN_LEFT:
                        repeat(x_start - i, y_start - i, strength - reduction,
                                effect);
                        break;
                }
            }
        }
    };
    //</editor-fold>
}
