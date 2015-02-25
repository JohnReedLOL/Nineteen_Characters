/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package src.io.view;
import java.util.*;

import src.model.map.constructs.Avatar;
import src.model.map.constructs.Item;

import java.lang.Character;
/**
 * Players see the StatsView when they are checking their stats
 * @author Matthew B, Jessan, Jack C,JohnReedLOL
 */
public final class StatsView extends Viewport
{
    // Converts the class name into a base 35 number
    private static final long serialVersionUID = Long.parseLong("StatsView", 35);
	
    private char[][] view_contents_;
    private ArrayList< ArrayList<Character>> render;
    private final Avatar avatar_reference_;

    private ArrayList<String> template_;    
    private boolean display_index = false;
    
    /**
     * Generates a new StatsView using the avatar_reference.
     */
    public StatsView(Avatar my_avatar) {
    	super();
    	avatar_reference_ = my_avatar;

    	view_contents_= new char[width_][height_];
		template_ = getAsciiArtFromFile("ASCIIART/statsview.txt");
		renderArray();
	}
    
	@Override
	public void renderToDisplay() {
		renderArray();
	}
	/*
	 * Helper method to handle bulk of rendering, keeps renderToDisplay pure
	 */
    private void renderArray() {
    	for (int i = 0; i < template_.size(); i++) {
    		writeStringToContents(0, 0+i, template_.get(i));
    	}
    	renderStats();
    	renderInventory();
    }
/*
 * helps renderToDisplay
 */
    private void renderStats() {
    	if (avatar_reference_.getOccupation() == null)
    		return;
    		
    	writeStringToContents(5, 6, avatar_reference_.name_ + ",");
    	
    	int level = avatar_reference_.getStatsPack().cached_current_level_;
    	if (level == 1)
    		writeStringToContents(5, 8, level + "st Level " + avatar_reference_.getOccupation().toString());
    	else if (level == 2)
    		writeStringToContents(5, 8, level + "nd Level " + avatar_reference_.getOccupation().toString());
    	else if (level == 3)
    		writeStringToContents(5, 8, level + "rd Level " + avatar_reference_.getOccupation().toString());
    	else
    		writeStringToContents(5, 8, level + "th Level " + avatar_reference_.getOccupation().toString());

    	writeStringToContents(18, 10, rightAlign(3, "" + avatar_reference_.getStatsPack().strength_level_));
    	writeStringToContents(18, 11, rightAlign(3, "" + avatar_reference_.getStatsPack().agility_level_));
    	writeStringToContents(18, 12, rightAlign(3, "" + avatar_reference_.getStatsPack().intellect_level_));
    	writeStringToContents(18, 13, rightAlign(3, "" + avatar_reference_.getStatsPack().hardiness_level_));
    	
    	StringBuilder hearts = new StringBuilder();
    	for (int i = 0; i < (avatar_reference_.getStatsPack().current_life_/avatar_reference_.getStatsPack().max_life_)*10; i++)
    		hearts.append("♥");
    	writeStringToContents(38, 6, rightAlign(10, hearts.toString()));
    	writeStringToContents(40, 7, rightAlign(3, "" + avatar_reference_.getStatsPack().current_life_));
    	writeStringToContents(44, 7, rightAlign(3, "" + avatar_reference_.getStatsPack().max_life_));

    	StringBuilder diamonds = new StringBuilder();
    	for (int i = 0; i < avatar_reference_.getStatsPack().current_mana_/avatar_reference_.getStatsPack().max_mana_*10; i++)
    		diamonds.append("♦");
    	writeStringToContents(38, 9, rightAlign(10, diamonds.toString()));
    	writeStringToContents(40, 10, rightAlign(3, "" + avatar_reference_.getStatsPack().current_mana_));
    	writeStringToContents(44, 10, rightAlign(3, "" + avatar_reference_.getStatsPack().max_mana_));

    	StringBuilder spades = new StringBuilder();
    	for (int i = 0; i < (avatar_reference_.getStatsPack().quantity_of_experience_
    			- ((avatar_reference_.getStatsPack().cached_current_level_-1)*100))/10; i++)
    		spades.append("♠");
    	writeStringToContents(40, 12, rightAlign(10, spades.toString()));
    	writeStringToContents(40, 13, rightAlign(3, "" + avatar_reference_.getStatsPack().quantity_of_experience_));

    	writeStringToContents(68, 6, rightAlign(3, "" + avatar_reference_.getStatsPack().lives_left_));
    	writeStringToContents(68, 8, rightAlign(3, "" + avatar_reference_.getStatsPack().moves_left_in_turn_));
    	writeStringToContents(72, 8, rightAlign(3, "" + avatar_reference_.getStatsPack().movement_level_));
    	writeStringToContents(72, 11, rightAlign(3, "" + avatar_reference_.getStatsPack().offensive_rating_));
    	writeStringToContents(72, 12, rightAlign(3, "" + avatar_reference_.getStatsPack().current_defensive_rating_));
    	writeStringToContents(72, 13, rightAlign(3, "" + avatar_reference_.getStatsPack().current_armor_rating_));
    }
/*
 * Helps renderToDisplay
 */
    private void renderInventory() {
    	ArrayList<Item> inventory = avatar_reference_.getInventory();
    	for (int i = 0; i < inventory.size(); i++) {
    		String item_name = inventory.get(i).name_;
    		if (item_name.length() > 22)
    			item_name = item_name.substring(0, 21);
    		if (i < 10) {
    			if (display_index)
    				writeStringToContents(19+i, 2, "" + (char)(48+i));
    			System.out.println("yes it works");
    			writeStringToContents(4, 19+i, item_name);
    		} else if (i < 12) {
    			if (display_index)
    				writeStringToContents(19+i, 2, "" + (char)(97+i));
    			writeStringToContents(4, 19+i, item_name);
    		} else if (i < 24) {
    			if (display_index)
    				writeStringToContents(19+i, 2, "" + (char)(97+i));
    			writeStringToContents(30, 19+i, item_name);
    		} else if (i < 36) {
    			if (display_index)
    				writeStringToContents(19+i, 2, "" + (char)(97+i));
    			writeStringToContents(56, 19+i, item_name);
    		}
    	}
    }

	@Override
	public boolean getInput(char c) {
		if (c == 'i') {
			avatar_reference_.switchToMapView();
			return true;
		}
		return false;
	}
		
}


