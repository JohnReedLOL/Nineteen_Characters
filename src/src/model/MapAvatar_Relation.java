/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package src.model;

import src.io.view.display.Display;
import src.model.constructs.Avatar;
import src.model.constructs.Entity;

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
     * override movement to attempt to pick up an item. 
     */
    @Override
    public int moveInDirection(int x, int y){
    	int x1 = super.moveInDirection(x, y);
        this.pickUpItemInDirection(0, 0);
        if(x1 !=0){
        	Entity bumped_into = getEntityInFacingDirection();
        	if(bumped_into != null){this.getAvatar().sayStuffToMe(bumped_into.getInteractionOptionStrings());}
        }
        return x1;
    }
    
    @Override
    public int pickUpItemInDirection(int x, int y){
    	int error_code = super.pickUpItemInDirection(x, y);
    	if(error_code == 0){Display.getDisplay().setMessage(this.getAvatar().getName()+" picked up an item!");}
    	return error_code;
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
     * Causes an entity to tele-port to the place where it was spawned
     *
     * @param toSpawn
     * @return -1 if respawn point is occupied
     */
    public int respawn() {
        return super.respawn();
    }

    @Override
    public void removeMyselfFromTheMapCompletely() {
        super.getMap().removeAvatar(avatar_);
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
    @Override
    public String getTileInfo(int relative_x, int relative_y) {
        return super.getTileInfo(relative_x, relative_y);
    }
}
