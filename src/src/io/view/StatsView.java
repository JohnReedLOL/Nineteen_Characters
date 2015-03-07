/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package src.io.view;
import java.util.*;

import src.IO_Bundle;
import src.model.map.constructs.Avatar;
import src.model.map.constructs.Item;

import java.lang.Character;
import src.model.map.constructs.PickupableItem;
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

    private ArrayList<String> template_;    
    private boolean display_index = false;
    private String userName_;
    private final String tab = "  ";
    
    /**
     * Generates a new StatsView using the avatar_reference.
     */
    public StatsView(String _uName) {
    	super();
    	userName_ = _uName;
    	view_contents_= new char[this.getWidth()][this.getHeight()];
		template_ = getAsciiArtFromFile("ASCIIART/statsview.txt");
	}
    
	@Override
	public void renderToDisplay(IO_Bundle bundle) {
		renderArray(bundle);
		populateEquipped(bundle);
		populateItems(bundle);
	}
	/*
	 * Helper method to handle bulk of rendering, keeps renderToDisplay pure
	 */
    private void renderArray(IO_Bundle bundle) {
    	for (int i = 0; i < template_.size(); i++) {
    		writeStringToContents(0, 0+i, template_.get(i));
    	}
    	renderStats(bundle);
    	renderInventory(bundle);
    }
/*
 * helps renderToDisplay
 */
    private void renderStats(IO_Bundle bundle_) {
    	if (bundle_.getOccupation() == null)
    		return;
    		
    	writeStringToContents(5, 6, userName_ + ",");
    	
    	int level = bundle_.getStatsPack().cached_current_level_;
    	if (level == 1)
    		writeStringToContents(5, 8, level + "st Level " + bundle_.getOccupation().toString());
    	else if (level == 2)
    		writeStringToContents(5, 8, level + "nd Level " + bundle_.getOccupation().toString());
    	else if (level == 3)
    		writeStringToContents(5, 8, level + "rd Level " + bundle_.getOccupation().toString());
    	else
    		writeStringToContents(5, 8, level + "th Level " + bundle_.getOccupation().toString());

    	writeStringToContents(18, 10, rightAlign(3, "" + bundle_.getStatsPack().getStrength_level_()));
    	writeStringToContents(18, 11, rightAlign(3, "" + bundle_.getStatsPack().getAgility_level_()));
    	writeStringToContents(18, 12, rightAlign(3, "" + bundle_.getStatsPack().getIntellect_level_()));
    	writeStringToContents(18, 13, rightAlign(3, "" + bundle_.getStatsPack().getHardiness_level_()));
    	
    	StringBuilder hearts = new StringBuilder();
    	for (int i = 0; i < (bundle_.getStatsPack().current_life_/bundle_.getStatsPack().getMax_life_())*10; i++)
    		hearts.append("♥");
    	writeStringToContents(38, 6, rightAlign(10, hearts.toString()));
    	writeStringToContents(40, 7, rightAlign(3, "" + bundle_.getStatsPack().getCurrent_life_()));
    	writeStringToContents(44, 7, rightAlign(3, "" + bundle_.getStatsPack().getMax_life_()));

    	StringBuilder diamonds = new StringBuilder();
    	for (int i = 0; i < bundle_.getStatsPack().getCurrent_mana_()/bundle_.getStatsPack().getMax_mana_()*10; i++)
    		diamonds.append("♦");
    	writeStringToContents(38, 9, rightAlign(10, diamonds.toString()));
    	writeStringToContents(40, 10, rightAlign(3, "" + bundle_.getStatsPack().getCurrent_mana_()));
    	writeStringToContents(44, 10, rightAlign(3, "" + bundle_.getStatsPack().getMax_mana_()));

    	StringBuilder spades = new StringBuilder();
    	for (int i = 0; i < (bundle_.getStatsPack().getQuantity_of_experience_()
    			- ((bundle_.getStatsPack().getCached_current_level_()-1)*100))/10; i++)
    		spades.append("♠");
    	writeStringToContents(40, 12, rightAlign(10, spades.toString()));
    	writeStringToContents(40, 13, rightAlign(3, "" + bundle_.getStatsPack().getQuantity_of_experience_()));

    	writeStringToContents(68, 6, rightAlign(3, "" + bundle_.getStatsPack().getLives_left_()));
    	writeStringToContents(68, 8, rightAlign(3, "" + bundle_.getStatsPack().getMoves_left_in_turn_()));
    	writeStringToContents(72, 8, rightAlign(3, "" + bundle_.getStatsPack().getMovement_level_()));
    	writeStringToContents(72, 11, rightAlign(3, "" + bundle_.getStatsPack().getOffensive_rating_()));
        // Replacing current_defensive_rating with defensive_rating
    	writeStringToContents(72, 12, rightAlign(3, "" + bundle_.getStatsPack().getDefensive_rating_()));
    	writeStringToContents(72, 13, rightAlign(3, "" + bundle_.getStatsPack().getArmor_rating_()));
    }
    private void printItemName(String item_name, int row){
    	if (item_name.length() > 22)
			item_name = item_name.substring(0, 21);
		if (row < 10) {
			if (display_index)
				writeStringToContents(19+row, 2, "" + (char)(48+row));
			writeStringToContents(4, 19+row, item_name);
		} else if (row < 12) {
			if (display_index)
				writeStringToContents(19+row, 2, "" + (char)(97+row));
			writeStringToContents(4, 19+row, item_name);
		} else if (row < 24) {
			if (display_index)
				writeStringToContents(19+row, 2, "" + (char)(97+row));
			writeStringToContents(30, 19+row, item_name);
		} else if (row < 36) {
			if (display_index)
				writeStringToContents(19+row, 2, "" + (char)(97+row));
			writeStringToContents(56, 19+row, item_name);
		}
    }
/*
 * Helps renderToDisplay
 */
    private void renderInventory(IO_Bundle bundle_) {
    	ArrayList<PickupableItem> inventory = bundle_.getInventory();
    	int i = 0;
    	for (i = 0; i < inventory.size(); i++) {
    		String item_name = inventory.get(i).name_;
    		printItemName(item_name, i);	
    	}
    	printItemName("Equipped:",++i);
    	if(bundle_.primary_!=null){
    		printItemName(tab+"Primary:",++i);
    		printItemName(tab+tab+bundle_.primary_.getName(),++i);
    	}
    	if(bundle_.second_!=null){
    		printItemName(tab+"Secondary:",++i);
    		printItemName(tab+tab+bundle_.second_.getName(), ++i);
    	}
    }

	@Override
	public boolean getInput(char c) {
		return false;
	}
		
}


