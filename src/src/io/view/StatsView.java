/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package src.io.view;
import java.awt.Color;
import java.util.ArrayList;

import src.IO_Bundle;
import src.model.constructs.items.PickupableItem;
/**
 * Players see the StatsView when they are checking their stats
 * @author Matthew B, Jessan, Jack C
 */
public final class StatsView extends Viewport
{
    // Converts the class name into a base 35 numbers
	

    private ArrayList<String> template_;    
    private boolean display_index = false;
    private String userName_;
    private final String tab = "  ";
    private Viewport minimap_ = new MapView(this.getHeight(),this.getWidth());
    
    /**
     * Generates a new StatsView using the avatar_reference.
     */
    public StatsView(String _uName) {
    	super(40, 100);
    	userName_ = _uName;
		template_ = getAsciiArtFromFile("ASCIIART/statsview.txt");
	}
    
	@Override
	public void renderToDisplayInternally(IO_Bundle bundle) {
		minimap_.renderToDisplay(bundle);
		renderArray(bundle);
		populateEquipped(bundle);
		populateItems(bundle);
		char[][] minimapcontents_ = minimap_.getCharContents();
		Color[][] minimapcolorcontents_ = minimap_.getColorContents();
		for(int x = 81, xx = minimap_.getWidth()/2 - (this.getWidth()-80)/2; x< this.getWidth()&& xx < minimapcontents_.length;++x,++xx){
			for(int y =this.getHeight()/2 , yy = minimap_.getHeight()/2-this.getHeight()/4; y< this.getHeight()&& yy < minimapcontents_[xx].length; ++y,++yy){
				this.getCharContents()[x][y] = minimapcontents_[xx][yy];
				this.getColorContents()[x][y] = minimapcolorcontents_[xx][yy];
			}
		}
	}
	/*
	 * Helper method to handle bulk of rendering, keeps renderToDisplay pure
	 */
    private void renderArray(IO_Bundle bundle) {
    	for (int i = 0; i < template_.size(); i++) {
    		writeStringToContents(0, 0+i, template_.get(i));
    	}
    	renderStats(bundle);
    	renderInventoryAndSkill(bundle);//These must be rendered in the correct order...
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

    	writeStringToContents(5, 9, "Coins: " + bundle_.num_coins_); //QUICK AND DIRTY FOR DEMONSTRATION PURPOSES
    	
    	writeStringToContents(18, 10, rightAlign(3, "" + bundle_.getStatsPack().getStrength_level_()));
    	writeStringToContents(18, 11, rightAlign(3, "" + bundle_.getStatsPack().getAgility_level_()));
    	writeStringToContents(18, 12, rightAlign(3, "" + bundle_.getStatsPack().getIntellect_level_()));
    	writeStringToContents(18, 13, rightAlign(3, "" + bundle_.getStatsPack().getHardiness_level_()));
    	writeStringToContents(18,14,rightAlign(3,""+bundle_.num_coins_));
    	
    	StringBuilder hearts = new StringBuilder();
    	for (int i = 0; i < (bundle_.getStatsPack().current_life_/bundle_.getStatsPack().getMax_life_())*10; i++)
    		hearts.append("♥");
    	writeStringToContents(38, 6, rightAlign(10, hearts.toString()));
    	writeStringToContents(40, 7, rightAlign(3, "" + bundle_.getStatsPack().getCurrent_life_()));
    	writeStringToContents(44, 7, rightAlign(3, "" + bundle_.getStatsPack().getMax_life_()));

    	StringBuilder diamonds = new StringBuilder();
    	for (int i = 0; i < bundle_.getStatsPack().getCurrent_mana_()/bundle_.getStatsPack().getMax_mana_()*10 &&  i< 10000; i++)
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
    private void printName(String item_name, int row){
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
			writeStringToContents(4, 19+row, item_name);//4 used to be 30
		} else if (row < 36) {
			if (display_index)
				writeStringToContents(19+row, 2, "" + (char)(97+row));
			writeStringToContents(4, 19+row, item_name);//4 used to be 56
		}
    }
/*
 * Helps renderToDisplay
 */
    private void renderInventoryAndSkill(IO_Bundle bundle_) {
    	int i = 0;
    	i = renderInventory(bundle_, i);
    	i = renderSkills(bundle_,i);
    }
    private int renderSkills(IO_Bundle bundle_, int i){
    	printName("Skills (Points: " + String.valueOf(bundle_.num_skillpoints_)+"):", i++);
    	ArrayList<String> skills = bundle_.getSkillNames();
    	ArrayList<Integer> levels = bundle_.getSkillLevels();
    	for(int j = 0; j< skills.size(); ++j,++i){
    		printName(tab+skills.get(j)+": "+String.valueOf(levels.get(j)), i);
    	}
    	return i;
    }
    private int renderInventory(IO_Bundle bundle_, int i){
    	printName("Items:", i++);
    	ArrayList<PickupableItem> inventory = bundle_.getInventory();
    	for (int j = 0; j < inventory.size(); j++,i++) {
    		String item_name = inventory.get(j).name_;
    		printName(tab+item_name, i);	
    	}
    	printName("Equipped:",++i);
    	if(bundle_.primary_!=null){
    		printName(tab+"Primary:",++i);
    		printName(tab+tab+bundle_.primary_.getName(),++i);
    	}
    	if(bundle_.second_!=null){
    		printName(tab+"Secondary:",++i);
    		printName(tab+tab+bundle_.second_.getName(), ++i);
    	}
    	++i;
    	return i;
    }

	@Override
	public boolean getInput(char c) {
		return false;
	}
		
}


