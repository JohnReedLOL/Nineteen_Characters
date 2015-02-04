/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package src.view;

import src.model.MapDisplayInterface;

/**
 * Players see the MapView while they are interacting with the map
 *
 * @author JohnReedLOL
 */

final class MapView extends Viewport {

    // Converts the class name into a base 35 number
    private static final long serialVersionUID = Long.parseLong("MapView", 35);
    
    // map_relationship_ is used in place of a map_referance_
    private final MapDisplayInterface map_relationship_;
    private final int xPos_;
    private final int yPos_;
    /*
     * Generates a new MapView from the map using coordinates x and y.
     */
    public MapView(MapDisplayInterface map_relationship, int x, int y) {
        map_relationship_ = map_relationship;
        xPos_ = x;
        yPos_ = y;
    }
}
