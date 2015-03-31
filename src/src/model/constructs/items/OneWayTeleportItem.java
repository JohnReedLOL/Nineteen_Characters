/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package src.model.constructs.items;

import src.model.constructs.Entity;

/**
 *
 * @author JohnReedLOL
 */
public class OneWayTeleportItem extends Item {

    public int getID() { return 10; }
    
    int x_destination_;
    int y_destination_;

    public int getDestX() { return x_destination_; }

    public int getDestY() { return y_destination_; }

    public OneWayTeleportItem(String name, char representation, int x_destination, int y_destination) {
        super(name, representation, false, true, false);
        x_destination_ = x_destination;
        y_destination_ = y_destination;
    }

    /**
     * Teleports the walker to his destination
     */
    @Override
    public void onWalkOver() {
        Entity to_be_teleported = this.getMapRelation().getTheEntityOnTopOfMe();
        to_be_teleported.getMapRelation().teleportTo(x_destination_, x_destination_);
    }
    /**
     * Set where the teleporter teleports to.
     * @param x
     * @param y
     */
    public void setDestination(int x, int y){
    	x_destination_ = x;
    	y_destination_ = y;
    }

}
