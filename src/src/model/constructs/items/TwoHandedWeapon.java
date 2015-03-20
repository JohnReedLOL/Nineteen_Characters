/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package src.model.constructs.items;

import java.io.Serializable;

import src.model.constructs.Entity;

/**
 *
 * @author JohnReedLOL
 */
public class TwoHandedWeapon extends Weapon implements PrimaryHandHoldable, SecondaryHandHoldable, Serializable {

    public TwoHandedWeapon(String name, char representation) {
        super(name, representation);
    }

    public int equipMyselfTo(Entity to_equip) {
        return to_equip.equip2hWeapon(this);
    }
}
