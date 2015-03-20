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
public abstract class ObstacleItem extends Item {

    public ObstacleItem(String name, char representation) {
        super(name, representation, false, false, false);
    }

}
