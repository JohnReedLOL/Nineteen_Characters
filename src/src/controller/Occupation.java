/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package src.controller;

import src.SaveData;

/**
 * Represents the eight directions plus one direction for staying in place
 * @author JohnReedLOL
 */
public interface Occupation extends SaveData {
    public void change_stats(EntityStatsPack current_stats);
}
