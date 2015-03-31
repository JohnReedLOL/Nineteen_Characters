/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package src.model.constructs;


import java.awt.Color;

import src.model.MapTerrain_Relation;

/**
 * Terrain for MapTile.
 */
public class Terrain extends DrawableThing {

    public int getID() { return 1; }

    void activate() {

    }

    void applyTerrainEffect(Entity entity) {

    }


    private boolean contains_water_;
    private boolean contains_mountain_;

    private char decal_ = '\u0000'; // null character
    private Character tempDecal_ = null;
    private Color tempColor_ = null;
    private int tempTurnCount_ = -1;
    /**
     * Sets Terrain's decal. and color.
     * @param decal, Color col_
     */
    public void addDecal(char decal,Color col_) {
        this.setColor(col_);
        this.addDecal(decal);
    }
    public void addTempDecal(char decal, Color col_, int turns){
    	tempTurnCount_ =  turns;
    	tempColor_ = col_;
    	tempDecal_ = decal;
    	
    }
    /**
     * Sets Terrain's decal
     * @param decal
     */
    public void addDecal(char decal) {
        decal_ = decal;
    }
    public char getDecal() {
    	if(tempDecal_ == null){
    		return decal_;
    	}else{return tempDecal_;}
    }
    
    /**
     * Checks if terrain has decal.
     * @return true if terrain has decal. False if not.
     */
    public boolean hasDecal() {
    	if(tempDecal_ != null){return true;}
        if (decal_ == '\u0000' || decal_ == ' ') {
            return false;
        } else {
            return true;
        }
    }

    public boolean isMountain() { return contains_mountain_; }

    public boolean isWater() { return contains_water_; }

    public void removeDecal(char decal) {
        decal_ = ' ';
        tempDecal_= null;
        tempColor_ = null;
        tempTurnCount_ = -1;
    }

    //potential duplicate of isPassable
    boolean determineIfCanPass(Entity entity) {
        if (!contains_water_ && !contains_mountain_) {
            return true;
        } else {
            return false;
        }
    }
    
    /**
     * Gets char that represents terrain on map.
     */
    @Override
    public char getRepresentation() {
    	if(!this.hasDecal()) {
    		return super.getRepresentation();
    	} else {
    		if(tempDecal_ == null){
    			return decal_;
    		}else{return tempDecal_;}

    	}
    }
    /**
     * Returns the tempColor if there is one, else call super.
     */
    @Override
    public Color getColor(){
    	if(tempColor_ == null){return super.getColor();}
    	else{return tempColor_;}
    }
    /**
     * Update anything that must be updated on a per turn basis. 
     * ATM that is the tempDecal and it's associates.
     */
    @Override
    public void takeTurn(){
    	--tempTurnCount_;
    	if(tempTurnCount_ < 0){
    		tempTurnCount_ = -1;
    		tempColor_ = null;
    		tempDecal_ = null;
    	}
    }
    @Override
    public boolean isPassable() {
        if (contains_water_ || contains_mountain_) {
            return false;
        }
        return true;
    }

    // map_relationship_ is used in place of a map_referance_
    private MapTerrain_Relation map_relationship_;

    /**
     * Use this to call functions contained within the MapTerrain relationship
     *
     * @return map_relationship_
     * @author Reed, John
     */
    public MapTerrain_Relation getMapRelation() {
        return map_relationship_;
    }

    public void setMapRelation(MapTerrain_Relation t) {
        map_relationship_ = t;
    }

    /**
     * Contains extra parameter, decal. 
     * @param name
     * @param representation
     * @param contains_mountain
     * @param contains_water
     * @param decal - Character that will represent terrain on map.
     */
    public Terrain(String name, char representation, boolean contains_water,
            boolean contains_mountain, char decal) {
        super(name, representation);
        contains_water_ = contains_water;
        contains_mountain_ = contains_mountain;
        if(!contains_mountain_ && contains_water_){this.setColor(Color.blue);}
        else if(contains_mountain_ && !contains_water_){this.setColor(Color.gray);}
        else if(contains_mountain_ && contains_water_){this.setColor(Color.cyan.darker());}
        else if(!contains_mountain_ && !contains_water_){this.setColor(Color.green.darker());}//Set grass to be green.
        decal_ = decal;
    }
    public Terrain(String name, char representation, boolean contains_water,
            boolean contains_mountain, char decal, Color col_) {
        super(name, representation);
        contains_water_ = contains_water;
        contains_mountain_ = contains_mountain;
        decal_ = decal;
        this.setColor(col_);
    }

    /**
     * Constructor for Terrain. Decal set to null.
     * @param name
     * @param representation
     * @param contains_water
     * @param contains_mountain
     */
    public Terrain(String name, char representation, boolean contains_water,
            boolean contains_mountain) {
        super(name, representation);

        contains_water_ = contains_water;
        contains_mountain_ = contains_mountain;
        if(!contains_mountain_ && contains_water_){this.setColor(Color.blue);}
        else if(contains_mountain_ && !contains_water_){this.setColor(Color.gray);}
        else if(contains_mountain_ && contains_water_){this.setColor(Color.cyan.darker());}
        else if(!contains_mountain_ && !contains_water_){this.setColor(Color.green.darker());}//Set grass to be green.
    }
}
