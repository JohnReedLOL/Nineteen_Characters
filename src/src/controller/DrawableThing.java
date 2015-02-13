package src.controller;

import java.io.Serializable;
import src.model.MapDrawableThing_Relation;
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author JohnReedLOL
 */
abstract public class DrawableThing implements Serializable {

    // Converts the class name into a base 35 number. "DrawableThing" too long
    private static final long serialVersionUID = Long.parseLong("Drawable", 35);

    // names of items and terrain should be non-unique.
    // names of entities should be unique to fit in a hashmap.
    public final String name_;

    // For things that take up only  1 tile or need to appear on a minimap
    private final char single_character_representation_;
    
    private final MapDrawableThing_Relation map_relationship_;
    
    /**
     * Use this to call functions contained within the MapDrawable relationship
     * @return map_relationship_
     * @author Reed, John
     */
    public MapDrawableThing_Relation getMapRelation() {
        return map_relationship_;
    }
    
    protected DrawableThing(String name, char representation) {
        name_ = name;
        single_character_representation_ = representation;
        is_viewable_ = true;
        map_relationship_ = new MapDrawableThing_Relation(this);
    }
    
    private boolean is_viewable_;
    
    private StatsPack stats_pack_ = new StatsPack();
    
    public StatsPack get_stats_pack_() {
        return this.stats_pack_;
    }
    
    public void onTurn() {
        
    }

    //representation changes for terrain with/without decal
    public char getRepresentation() {
        return this.single_character_representation_;
    }
    
    public void setViewable(boolean is_viewable) {
        is_viewable_ = is_viewable;
    }
    
    public boolean getViewable() {
        return this.is_viewable_;
    }
    
}