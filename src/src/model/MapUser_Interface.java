/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package src.model;

import src.IO_Bundle;
import src.Key_Commands;

/**
 *
 * @author JohnReedLOL
 */
public interface MapUser_Interface {
    /**
     * Note that the IO_Bundle contains a 2D array of characters in which the 
     * y coordinates are stored in the first [] and the x_cordinates are in the second.
     * @param username
     * @param command
     * @param width_from_center
     * @param height_from_center
     * @return 
     */
    public IO_Bundle sendCommandToMapWithOptionalText(String username, Key_Commands command, int width_from_center, int height_from_center, String text);
    /**
     * Takes in name so save to, defaults to date
     * @param foo
     */
    public int saveGame(String foo);
    /**
     * Takes in name to load. 
     * @param foo
     */
    public int loadGame(String foo);
    
}