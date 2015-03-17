/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package src.model.constructs.items;

/**
 *
 * @author JohnReedLOL
 */
public class PermanentObstacleItem extends ObstacleItem {

    public PermanentObstacleItem(String name, char representation) {
        super(name, representation);
    }

    /**
     * You can't make a permanent Obstacle Item passable
     * @param is_passable
     * @return 
     */
    @Override
    public int setPassable(boolean is_passable) {
        return -1;
    }

    /**
     * Permanent obstacles are always impassable
     * @return 
     */
    @Override
    public boolean isPassable() {
        return false;
    }
}
