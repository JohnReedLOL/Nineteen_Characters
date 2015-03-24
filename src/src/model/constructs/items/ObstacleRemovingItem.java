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

    private static final long serialVersionUID = 13L;
    @Override
    public int getID() { return (int)serialVersionUID; }

    public ObstacleRemovingItem(String name, char representation) {
        super(name, representation);
    }
}
