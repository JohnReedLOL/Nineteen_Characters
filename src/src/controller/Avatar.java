/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package src.controller;

import src.SaveData;
import src.SavedGame;
import src.model.MapAvatar_Relation;
import src.model.MapDrawableThing_Relation;
import src.model.MapMain_Relation;
import src.view.Display;
import src.view.AvatarCreationView;
import src.view.MapView;
import src.view.StatsView;
import src.view.Viewport;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.HashMap;
import java.util.LinkedList;

/**
 * Each avatar represents a player
 *
 *
 * @author JohnReedLOL
 */
public final class Avatar extends Entity{

    // map_relationship_ is used in place of a map_referance_
    private final MapAvatar_Relation map_relationship_;

    // holds the views
    private final Viewport current_viewport_;
    private final AvatarCreationView avatar_creation_view_;
    private final MapView map_view_;
    private final StatsView stats_view_;
    private char storedInput;

    /**
     * Accepts a key command from the map
     *
     * @param command
     * @return 0 on success, not zero if command cannot be accepted
     */
    public int acceptKeyCommand(char command) {
        return 0;
    }

    /**
     * Use this to call functions contained within the MapAvatar relationship
     *
     * @return map_relationship_
     * @author Reed, John
     */
    @Override
    public MapAvatar_Relation getMapRelation() {
        return map_relationship_;
    }

    public void setMapRelation(MapAvatar_Relation a) {
        map_relationship_ = a;
    }
/* Make sure to call set map after this!
 * 
 */
    public Avatar(String name, char representation, int x_respawn_point, int y_respawn_point) {
        super(name, representation, x_respawn_point, y_respawn_point);
        map_relationship_ = new MapAvatar_Relation(this, x_respawn_point, y_respawn_point);
        map_view_ = generateMapView();
        stats_view_ = generateStatsView();
        current_viewport_ = new AvatarCreationView(this);
    }

    public Viewport getMyView() {
        return this.current_viewport_;
    }

    public void switchToMapView() {
        current_viewport_ = map_view_;
    }

    public void switchToStatsView() {
        current_viewport_ = stats_view_;
    }

    private MapView generateMapView() {
        MapView map_view = new MapView();
        return map_view;
    }

    private StatsView generateStatsView() {
        return new StatsView(this);
    }
    public void setMap(MapMain_Relation map_main){
    	map_main.addViewToMap(map_view_);
    	map_main.addAvatar(this, 0, 0);
    }
    
	/* determine if input is not important
	 * or if we already did something
	 * then if true
	 * 
	 * storedInput = '~';
	 */
	public void giveInput( char current ) {
		if (current_viewport_ != map_view_)
	    	current_viewport_.getInput( current );
		else {
			if (storedInput == 'p') {
				switch (current) {
	    			case '1':
						map_relationship_.pickUpItemInDirection(0, -1);
						break;
					case '2':
						map_relationship_.pickUpItemInDirection(0, -1);
						break;
					case '3':
						map_relationship_.pickUpItemInDirection(1, -1);
						break;
					case '4':
						map_relationship_.pickUpItemInDirection(-1, 0);
						break;
					case '5':
						map_relationship_.pickUpItemInDirection(0, 0);
						break;
					case '6':
						map_relationship_.pickUpItemInDirection(1, 0);
						break;
					case '7':
						map_relationship_.pickUpItemInDirection(-1, 1);
						break;
					case '8':
						map_relationship_.pickUpItemInDirection(0, 1);
						break;
					case '9':
						map_relationship_.pickUpItemInDirection(1, 1);
						break;
				}
			} else if (storedInput == ' ') {
				switch (current) {
	    			case '1':
						map_relationship_.sendAttack(0, -1);
						break;
					case '2':
						map_relationship_.sendAttack(0, -1);
						break;
					case '3':
						map_relationship_.sendAttack(1, -1);
						break;
					case '4':
						map_relationship_.sendAttack(-1, 0);
						break;
					case '5':
						map_relationship_.sendAttack(0, 0);
						break;
					case '6':
						map_relationship_.sendAttack(1, 0);
						break;
					case '7':
						map_relationship_.sendAttack(-1, 1);
						break;
					case '8':
						map_relationship_.sendAttack(0, 1);
						break;
					case '9':
						map_relationship_.sendAttack(1, 1);
						break;
				}
			} else if (storedInput == '~') {
				switch (current) {
	    			case '1':
						map_relationship_.moveInDirection(0, -1);
						break;
					case '2':
						map_relationship_.moveInDirection(0, -1);
						break;
					case '3':
						map_relationship_.moveInDirection(1, -1);
						break;
					case '4':
						map_relationship_.moveInDirection(-1, 0);
						break;
					case '5':
						map_relationship_.moveInDirection(0, 0);
						break;
					case '6':
						map_relationship_.moveInDirection(1, 0);
						break;
					case '7':
						map_relationship_.moveInDirection(-1, 1);
						break;
					case '8':
						map_relationship_.moveInDirection(0, 1);
						break;
					case '9':
						map_relationship_.moveInDirection(1, 1);
						break;
				}
			}
			
		}
	}

    @Override
    public String toString() {
        String s = "Avatar name: " + name_;

        if (!(equipped_item_ == null)) {
            s += "\n equppied item: " + equipped_item_.name_;
        } else {
            s += "\n equppied item: null";
        }

        s += "\n Inventory " + "(" + inventory_.size() + ")" + ":";
        for (int i = 0; i < inventory_.size(); ++i) {
            s += " " + inventory_.get(i).name_;
        }

        s += "\n";

        s += " map_relationship_: ";
        if (map_relationship_ == null) {
            s += "null";
        } else {
            s += "Not null";
        }

        s += "\n associated with map:" + map_relationship_.isAssociatedWithMap();

        return s;
    }
    /*
     * Handles avatar input
     */
    public void getInput(char c){
    	if (current_viewport_ == map_view_) {	//If we currently have our mapview equipped(check by reference)

    		MapAvatar_Relation map_avatar_relation = this.getMapRelation();
    		if (map_avatar_relation == null) 
    			return;						//If the avatar is not on the map, it can't really do anything.
    		
    		map_view_.setCenter(map_avatar_relation.getMyXCoordinate(),map_avatar_relation.getMyYCoordinate());
    		switch(c){
	    		case '1'://Move SW
	    			map_avatar_relation.moveInDirection(-1, -1);
	    			break;
	    		case '2'://Move S
	    			map_avatar_relation.moveInDirection(0, -1);
	    			break;
	    		case '3'://Move SE
	    			map_avatar_relation.moveInDirection(1, -1);
	    			break;
	    		case '4': // Move W
	    			map_avatar_relation.moveInDirection(-1,0);
	    			break;
	    		case '6'://Move E
	    			map_avatar_relation.moveInDirection(1,0);
	    			break;
	    		case '7'://Move NW
	    			map_avatar_relation.moveInDirection(-1, 1);
	    			break;
	    		case '8'://Move N
	    			map_avatar_relation.moveInDirection(0,1);
	    			break;
	    		case '9': //Move NE
	    			map_avatar_relation.moveInDirection(1,1);
	    			break;
	    		case 'S': //Save game
	    			break;
	    		case 'v': //Open stats
	    			break;
	    		case 'i': //Use item
	    			break;
	    		case 'q'://move NW
	    			break;
	    		case 'w': //move N
	    			map_avatar_relation.moveInDirection(0, 1);
	    			break;
	    		case 'e'://move NE
	    			map_avatar_relation.moveInDirection(1,1);
	    			break;
	    		case 'a': //move W
	    			map_avatar_relation.moveInDirection(-1,1);
	    			break;
	    		case 's'://Move stationary?
	    			break;
	    		case 'd'://Move E
	    			map_avatar_relation.moveInDirection(1,0);
	    			break;
	    		case 'z'://Move SW
	    			map_avatar_relation.moveInDirection(-1,-1);
	    			break;
	    		case 'x'://move s
	    			map_avatar_relation.moveInDirection(0,-1);
	    			break;
	    		case 'c'://move SE
	    			map_avatar_relation.moveInDirection(1,-1);
	    			break;
	    		case 'D': //drop item
	    			break;
	    		case 'p'://pickup item
	    			break;
	    		default: //no valid input
	    			break;
    		}
    		current_viewport_.renderToDisplay(); //See lower comment, maybe avatar should have a Display also to print it's views?
    	}
    	else{
    		current_viewport_.getInput(c);
    		current_viewport_.renderToDisplay();//Although printing with display already calls this, might just want to move the display into avatar or something, not really sure
    	}
    }

    // <editor-fold desc="SERIALIZATION" defaultstate="collapsed">

    // </editor-fold>
}
