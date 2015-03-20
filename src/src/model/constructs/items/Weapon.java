/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package src.model.constructs.items;

import java.io.Serializable;

/**
 *
 * @author JohnReedLOL
 */
public abstract class Weapon extends EquipableItem implements PrimaryHandHoldable, Serializable {

    public Weapon(String name, char representation) {
        super(name, representation);
    }
}
