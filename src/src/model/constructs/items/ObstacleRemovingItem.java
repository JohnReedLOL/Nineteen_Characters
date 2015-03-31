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
public class ObstacleRemovingItem extends PickupableItem {

    public int getID() { return 13; }

    public ObstacleRemovingItem(String name, char representation) {
        super(name, representation);
    }
}
