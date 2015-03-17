/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package src.model;

import src.model.constructs.Terrain;

/**
 *
 * @author JohnMichaelReed
 */
public class MapTerrain_Relation extends MapDrawableThing_Relation {
	
    public MapTerrain_Relation(Map m, Terrain terrain) {
        super(m);
        terrain_ = terrain;
    }
    
    private final Terrain terrain_;
}
