/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package src.view;

import src.model.MapDisplay_Relation;

/**
 * Players see the MapView while they are interacting with the map
 *
 * @author Matthew B, Jessan, JohnReedLOL
 */

final class MapView extends Viewport {

    // Converts the class name into a base 35 number
    private static final long serialVersionUID = Long.parseLong("MapView", 35);
    
    // map_relationship_ is used in place of a map_referance_
    private final MapDisplay_Relation map_relationship_;
    private final int xPos_;
    private char[][] view_contents_;
    private final int yPos_;
    /*
     * Generates a new MapView from the map using coordinates x and y.
     */
    public MapView(MapDisplay_Relation map_relationship, int x, int y) {
        map_relationship_ = map_relationship;
        xPos_ = x;
        yPos_ = y;
    	view_contents_=new char[length_][width_];
    }
}
