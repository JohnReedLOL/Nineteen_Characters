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
public final class Smasher implements Occupation
{
    // Converts the class name into a base 35 number
    private static final long serialVersionUID = Long.parseLong("Smasher", 35);
    public StatsPack change_stats(StatsPack current_stats){
        //for smasher
        StatsPack level_up_stats = new StatsPack(0,2,1,1,2,0,1,0,1);
        return current_stats.add(level_up_stats);
    }
}