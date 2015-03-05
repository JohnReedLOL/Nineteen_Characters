/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package src.model.map.constructs;

/**
 *
 * @author JohnReedLOL
 */
public class OneHandedWeapon extends Weapon implements PrimaryHandHoldable {

    public OneHandedWeapon(String name, char representation) {
        super(name, representation);
    }

    @Override
    public int equipMyselfTo(Entity to_equip) {
        return to_equip.equip1hWeapon(this);
    }
}
