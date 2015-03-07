/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package src.io.controller;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.util.HashMap;

import src.Function;
import src.model.map.MapUser_Interface;
import src.io.view.AvatarCreationView;
import src.io.view.MapView;
import src.io.view.StatsView;
import src.io.view.Viewport;
import src.io.view.display.Display;
/**
 * Uses keyboard input to control the avatar
 * @author JohnReedLOL
 */
public final class UserController implements Function<Void, Character>
{
	private class KeyRemapper{
		private char remapTrigger_ = '~';
		private HashMap<Character, Character> map_ = new HashMap<Character, Character>();
		private boolean rebindMode_ = false;
		private char rebindA_ = nullChar_;

        /**
         * Gets a copy of the key remapping
         * @return A HashMap copy of the key remapping
         * @author Alex Stewart
         */
        public HashMap<Character, Character> getMap() { return map_; }

        /**
         * Validates and sets the key remapping, if successful; overwrites the entire remapping
         * <p>ie. it does not mix the maps</p>
         * @param newMap A new key remapping to be applied
         * @auhtor Alex Stewart
         */
        public void setMap(HashMap<Character, Character> newMap) {
            // validate entries in the map
            if (newMap == null) return;
            // Add code here if we want to reject some characters
            map_ = newMap;
        }

		public char remapInput(char c){
			if(rebindMode_){
				if(rebindA_ == nullChar_){rebindA_ = c;}
				else{
					map_.put(c, rebindA_);//The value at A is now bound to C.
					//Also, it would be worth it to add a textoutput showing remappings. For now, println
					System.out.println(c +" was remapped to original value for " +rebindA_);
					rebindA_ = nullChar_;
					rebindMode_ = false; //Reset it all.
				}
			return stillChar_;//In this case, we want to stand still while the remapping occurs.
			}
			else{
				Character value = map_.get(c);
				if(value != null){c = value;}
			}
			if(c == remapTrigger_){rebindMode_ = true;}
			return c;
		}
	}

    public UserController(MapUser_Interface mui, String uName) {
        MapUserAble_ = mui;
        userName_ = uName;
        setView(nullChar_);
    	Display.getDisplay().addGameInputerHandler(this);
    	Display.getDisplay().setView(currentView_);
        Display.getDisplay().printView();
        
    }

    private MapUser_Interface MapUserAble_;
    private final String userName_;
    private Viewport currentView_ = new AvatarCreationView(); 
    private final char nullChar_ = (char)0;
    private final char stillChar_ = 'M';
    private KeyRemapper remap_ = new KeyRemapper();
    
    //Handles the view switching, uses the  instanceof operator in a slightly evil way, 
    //ideally we should look into refactoring this to not
    private void setView(char c){
    	boolean taken = false;
    	if(currentView_ instanceof AvatarCreationView){
    		if(c == 'Z' || c == 'C' || c == 'X' || c == 'V'){
    			currentView_ = new MapView(); 
    		}
    	}	
    	if(currentView_ instanceof MapView){
    		if(c == 'i'){currentView_ = new StatsView(userName_); taken = true;}
    	}
    	else if(currentView_ instanceof StatsView){
    		if(c == 'i'){currentView_ = new MapView(); taken = true;}
    	}
    	if(!taken){
    	currentView_.renderToDisplay(MapUserAble_.sendCommandToMap(userName_, c, currentView_.getWidth()/2,currentView_.getHeight()/2));
    	}
    	else{
    		currentView_.renderToDisplay(MapUserAble_.sendCommandToMap(userName_, stillChar_, currentView_.getWidth()/2,currentView_.getHeight()/2));
    		//I need to get this info without sending a command, sending ' ' is a hack for now.
    	}
    }
    private void takeTurn(char foo) {
    	char remapped = remap_.remapInput(foo);
    	setView(remapped);
    	//my_avatar_.getInput((char)input);
		//my_avatar_.getMapRelation().getSimpleAngle();//Example of simpleangle
		//my_avatar_.getMapRelation().getAngle();//Example of how to use getAngle
    	Display.getDisplay().setView(currentView_);
        Display.getDisplay().printView();
    	}

    // FIELD ACCESSORS
    /**
     * Gets this UserController's user name value
     * <p>Used for saving. Loading is done through the constructor</p>
     * @return A String object with this UserController's user name
     * @author Alex Stewart
     */
    public String getUserName() { return userName_; }

    /**
     * Gets the underlying key remapping values
     * @return A HashMap with the remapped key values in it
     * @author Alex Stewart
     */
    public HashMap<Character, Character> getRemap() {
        if (remap_ == null) return null;
        return remap_.getMap();
    }

    /**
     * Sets the underlying key remapping
     * @param remap The new key remapping to be applied
     * @author Alex Stewart
     */
    public void setRemap(HashMap<Character, Character> remap) {
        if (remap_ == null) remap_ = new KeyRemapper();
        remap_.setMap(remap);
    }



	@Override
	public Void apply(Character foo) {
		takeTurn(foo);
		return null;
	}
    
}
