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
public class Staff extends OneHandedWeapon implements PrimaryHandHoldable {

    public Staff(String name, char representation) {
        super(name, representation,"Summoner");
    }
}
