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
public class Bow extends TwoHandedWeapon implements Serializable {
    private static final long serialVersionUID = 2L;
    @Override
    public int getID() { return (int)serialVersionUID; }

    public Bow(String name, char representation) {
        super(name, representation);
    }
}
