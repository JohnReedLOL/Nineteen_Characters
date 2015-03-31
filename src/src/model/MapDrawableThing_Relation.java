/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package src.model;

import java.awt.Color;

import src.Effect;
import src.FacingDirection;
import src.model.constructs.Entity;
import src.model.constructs.items.Item;

/**
 * This should be abstract because you can't make drawable things
 *
 * @author JohnMichaelReed
 */
public class MapDrawableThing_Relation {

    private final Map current_map_reference_;

    /**
     * Get map reference
     */
    protected Map getMap() {
        return current_map_reference_;
    }

    public class AreaEffect {

        /**
         * casts an area effect. Set symbol to empty space for no effect on map
         * decals
         *
         * @param x_center - center of area effect
         * @param y_center - center of area effect
         * @param radius - diameter/2 of area effect
         * @author Reed, John-Michael
         */
        public void effectAreaWithinRadius(int radius, int strength, Effect effect) {
            int left_edge = getMyXCoordinate() - radius;
            int right_edge = getMyXCoordinate() + radius;
            int top = getMyYCoordinate() + radius;
            int bottom = getMyYCoordinate() - radius;
            for (int i = top; i >= bottom; --i) {
                for (int j = left_edge; j <= right_edge; ++j) {
                	if (!(i == top && j == left_edge || i == top && j == right_edge ||
                			i == bottom && j == left_edge || i == bottom && j == right_edge)) {
	                    int reduction = 0;
	                    if (effect == Effect.HEAL || effect == Effect.HURT) {
	                        int damage_reduction_x = Math.abs(getMyXCoordinate() - j);
	                        int damage_reduction_y = Math.abs(getMyYCoordinate() - i);
	                        reduction = damage_reduction_x + damage_reduction_y;
	                    }
	
	                    repeat(j, i, strength - reduction, effect);
                	}
                }
            }
        }
        
        public void effectPerimeter(int radius, int strength, Effect effect) {
        	if (effect != Effect.HURT)
	        	if (radius == 0)
	        		repeat(getMyXCoordinate(), getMyYCoordinate(), strength, effect);
        	if (radius == 1) {
        		for (int i = 0; i < 3; i++)
        			for (int j = 0; j < 3; j++)
        				if (!(j == 1 && i == 1))
        					repeat(getMyXCoordinate()-1+i, getMyYCoordinate()-1+j, strength, effect);
        	}
        	if (radius > 1) {
                final int left_edge = getMyXCoordinate() - radius;
                final int right_edge = getMyXCoordinate() + radius;
                final int top = getMyYCoordinate() + radius;
                final int bottom = getMyYCoordinate() - radius;
                if (radius != 2) {
                	repeat(left_edge+1, top-1, strength, effect);
                	repeat(left_edge+1, bottom+1, strength, effect);
                	repeat(right_edge-1, top-1, strength, effect);
                	repeat(right_edge-1, bottom+1, strength, effect);
                }
        		for (int i = top; i >= bottom; i--) {
        			for (int j = left_edge; j <= right_edge; j++) {
        				if (i == top || j == left_edge || i == bottom || j == right_edge) {
	                    	if (!(i == top && j == left_edge || i == top && j == right_edge ||
	            				i == bottom && j == left_edge || i == bottom && j == right_edge)) {
	                    		int reduction = 0;
	    	                    if (effect == Effect.HEAL || effect == Effect.HURT)
	    	                        reduction = radius;
	
	    	                    repeat(j, i, strength - reduction, effect);
	                    	}
        				}
        			}
        		}
        	}
        }
        
        public void effectArc(int distance, int strength, Effect effect, FacingDirection direction) {
            if (distance < 0 || strength < 0) {
                System.exit(-1);
            }
            final int x_start = getMyXCoordinate();
            final int y_start = getMyYCoordinate();
            switch (direction) {
            	case UP:
            		for (int i = 1; i < distance*2; i++)
            			repeat(x_start-distance+i, y_start+distance, strength - distance/2, effect);
            		break;
            	case DOWN:
            		for (int i = 1; i < distance*2; i++)
            			repeat(x_start-distance+i, y_start-distance, strength - distance/2, effect);
            		break;
            	case LEFT:
            		for (int i = 1; i < distance*2; i++)
            			repeat(x_start-distance, y_start-distance+i, strength - distance/2, effect);
            		break;
            	case RIGHT:
            		for (int i = 1; i < distance*2; i++)
            			repeat(x_start+distance, y_start-distance+i, strength - distance/2, effect);
            		break;
            	case UP_LEFT:
            		for (int i = 0; i < distance; i++)
            			repeat(x_start-distance+i, y_start+1+i, strength - distance/2, effect);
            		break;
            	case UP_RIGHT:
            		for (int i = 0; i < distance; i++)
            			repeat(x_start+1+i, y_start+distance-i, strength - distance/2, effect);
            		break;
            	case DOWN_LEFT:
            		for (int i = 0; i < distance; i++)
            			repeat(x_start-distance+i, y_start-1-i, strength - distance/2, effect);
            		break;
            	case DOWN_RIGHT:
            		for (int i = 0; i < distance; i++)
            			repeat(x_start+1+i, y_start-distance+i, strength - distance/2, effect);
            		break;
            }
        }
        
        public void effectLine(int distance, int strength, Effect effect, FacingDirection direction) {
            if (distance < 0 || strength < 0) {
                System.exit(-1);
            }
            final int x_start = getMyXCoordinate();
            final int y_start = getMyYCoordinate();
        	int new_strength;
            switch (direction) {
            	case UP:
            		new_strength = strength * (1 - distance/10);
            		repeat(x_start, y_start+distance, (new_strength >= 0) ? new_strength : 0, effect);
            		break;
            	case DOWN:
            		new_strength = strength * (1 - distance/10);
            		repeat(x_start, y_start-distance, (new_strength >= 0) ? new_strength : 0, effect);
            		break;
            	case LEFT:
            		new_strength = strength * (1 - distance/10);
            		repeat(x_start-distance, y_start, (new_strength >= 0) ? new_strength : 0, effect);
            		break;
            	case RIGHT:
            		new_strength = strength * (1 - distance/10);
            		repeat(x_start+distance, y_start, (new_strength >= 0) ? new_strength : 0, effect);
            		break;
            	case UP_LEFT:
            		new_strength = strength * (1 - distance/10);
            		repeat(x_start-distance, y_start+distance, (new_strength >= 0) ? new_strength : 0, effect);
            		break;
            	case UP_RIGHT:
            		new_strength = strength * (1 - distance/10);
            		repeat(x_start+distance, y_start+distance, (new_strength >= 0) ? new_strength : 0, effect);
            		break;
            	case DOWN_LEFT:
            		new_strength = strength * (1 - distance/10);
            		repeat(x_start-distance, y_start-distance, (new_strength >= 0) ? new_strength : 0, effect);
            		break;
            	case DOWN_RIGHT:
            		new_strength = strength * (1 - distance/10);
            		repeat(x_start+distance, y_start-distance, (new_strength >= 0) ? new_strength : 0, effect);
            		break;
            }
        }
        
        private int effectDecalDuration_ = 12;

        /**
         * For damage coming from non-entities
         *
         * @param x_pos - x coordinate of effect
         * @param y_pos - y coordinate of effect
         * @param strength - how much effect
         * @param effect - which effect
         */
        public void repeat(int x_pos, int y_pos, int strength, Effect effect) {
            MapTile infliction = current_map_reference_.getTile(x_pos, y_pos);
            if (infliction != null) {
                // If there is no decal, fuck shit up
                if (infliction.getTerrain() != null && !infliction.getTerrain().hasDecal()) {
                    if (effect == Effect.HURT) {
                        infliction.getTerrain().addTempDecal('♨', Color.magenta, effectDecalDuration_);
                    } else if (effect == Effect.HEAL) {
                        infliction.getTerrain().addTempDecal('♥', Color.red, effectDecalDuration_);
                    } else if (effect == Effect.LEVEL) {
                        infliction.getTerrain().addTempDecal('↑', Color.black, effectDecalDuration_);
                    } else if (effect == Effect.KILL) {
                        infliction.getTerrain().addTempDecal('☣', Color.orange, effectDecalDuration_);
                    }
                }
                Entity to_effect = infliction.getEntity();
                if (to_effect != null) {
                    if (effect == Effect.HURT) {
                        to_effect.receiveAttack(strength, null); // kills avatar if health is negative
                    } else if (effect == Effect.HEAL) {
                        to_effect.receiveHeal(strength);
                        to_effect.receiveMana(strength);
                    } else if (effect == Effect.LEVEL) {
                        to_effect.gainEnoughExperienceTolevelUp();
                    } else if (effect == Effect.KILL) {
                        to_effect.commitSuicide();
                    }
                }
            }
        }
    };

    /**
     * This object is actually a function used to call area effects
     *
     * @author John-Michael Reed
     */
    public final AreaEffect areaEffectFunctor = new MapDrawableThing_Relation.AreaEffect();

    @Override
    protected Object clone() throws CloneNotSupportedException {
        return super.clone(); //To change body of generated methods, choose Tools | Templates.
    }

    public MapDrawableThing_Relation(Map m) {
        current_map_reference_ = m;
    }

    private void initguardMap() {
        if (current_map_reference_ == null) {
            System.err.println("Empty map reference, "
                    + "and attempted to access map. Perhaps avatar was never passed a map, or mapview was never passed a map");
        }
    }

    private void initguardTile() {
        if (my_tile_ == null) {
            System.err.println("Empty tile reference, "
                    + "and attempted to access map. Perhaps avatar was never passed a map, or mapview was never passed a map");
        }
    }

    public boolean isAssociatedWithMap() {
        if (current_map_reference_ != null) {
            return true;
        } else {
            return false;
        }
    }

    private MapTile my_tile_ = null;

    /**
     *
     * @return MapTile associated with drawable thing (avatar/entity/item/etc.).
     */
    public MapTile getMapTile() {
        return my_tile_;
    }

    /**
     * Set MapTile that drawable thing (avatar/entity/item/etc.) is on.
     *
     * @param new_tile
     */
    public void setMapTile(MapTile new_tile) {
        my_tile_ = new_tile;
    }

    /**
     *
     * @return x coordinate of tile drawable thing (avatar/entity/item/etc.) is
     * on. OR -1 IF MY TILE IS NULL
     */
    public int getMyXCoordinate() {
        initguardTile();
        if(my_tile_ != null) {
        return my_tile_.x_;
        } else {
            return -1;
        }
    }

    /**
     *
     * @return y coordinate of tile drawable thing (avatar/entity/item/etc.) is
     * on. OR -1 IF MY TILE IS NULL
     */
    public int getMyYCoordinate() {
        initguardTile();
        if(my_tile_ != null) {
        return my_tile_.y_;
        } else {
            return -1;
        }
    }

    /**
     * Moves an entity without removing it from the list of entities
     *
     * @param: entity The entity to be moved
     * @param: x - distance to push in the x direction
     * @param: y - distance to push in the y direction
     * @return error codes: -1 if tile is taken, -2 if entity is null, -3 if
     * entity cannot be found, -4 if tile is off the map
     * @author John-Michael Reed
     */
    public int pushEntityInDirection(Entity e, int delta_x, int delta_y) {
        if (e == null || e.getMapRelation() == null || !e.hasLivesLeft()) {
            return -2;
        }
        int old_x = e.getMapRelation().getMyXCoordinate(); //Current directions say attempted
        int old_y = e.getMapRelation().getMyYCoordinate();
        Entity toMove = current_map_reference_.getTile(old_x, old_y).getEntity();
        if (toMove == e) {
            current_map_reference_.getTile(old_x, old_y).removeEntity();
            MapTile move_tile = current_map_reference_.getTile(old_x + delta_x, old_y + delta_y);
            if (!(move_tile == null || move_tile.isPassable() == false)) { // move the entity
                int error_code = move_tile.addEntity(e);
                Item walked_on_item = move_tile.viewTopItem();
                if (walked_on_item != null) { // make the item walked on do stuff
                    walked_on_item.onWalkOver();
                }
                return error_code;
            } else { // put the entity back in its place
                current_map_reference_.getTile(old_x, old_y).addEntity(e);
                return -4;
            }
        } else {
            return -3;
        }
    }
}
