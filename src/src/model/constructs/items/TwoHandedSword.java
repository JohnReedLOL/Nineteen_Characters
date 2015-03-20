/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package src.model.constructs.items;

import java.io.Serializable;

import src.model.constructs.DrawableThingStatsPack;

/**
 *
 * @author JohnReedLOL
 */
public class TwoHandedSword extends TwoHandedWeapon implements PrimaryHandHoldable, SecondaryHandHoldable, Serializable {

    public TwoHandedSword(String name, char representation) {
        super(name, representation);
        this.getStatsPack().addOn(new DrawableThingStatsPack(10,5));
    }
}
