/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package src.model;

import src.controller.Terrain;

/**
 *
 * @author JohnMichaelReed
 */
public class MapTerrain_Relation extends MapDrawableThing_Relation {

    private final Terrain terrain_;

    public MapTerrain_Relation(Terrain terrain) {
        super(terrain);
        terrain_ = terrain;
    }
}
