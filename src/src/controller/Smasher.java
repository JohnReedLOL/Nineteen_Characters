/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package src.controller;

/**
 *
 * @author JohnReedLOL
 */
public final class Smasher implements Occupation {

    public void change_stats(EntityStatsPack current_stats) {
        //for smasher
        current_stats.strength_level_ += 1;

    }

    @Override
    public String toString() {
    	return "Smasher";
    }


    // <editor-fold desc="SERIALIZATION" defaultstate="collapsed">
    @Override
    public String getSerTag() {
        return "OCC_SMASHER";
    }
    // </editor-fold>
}
