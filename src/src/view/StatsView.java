/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package src.view;
import java.util.*;
import src.controller.Avatar;
import src.controller.Entity;
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
    private void renderStats(){
    	//code to get stats from avatar
    }
    /**
     * Generates a new StatsView using the avatar_reference.
     */
    public StatsView(Avatar my_avatar) {
    	super();
    	avatar_reference_ = my_avatar;
    	view_contents_=getContents();
	    }
	@Override
	public void renderToDisplay() {
		render = new ArrayList< ArrayList<Character>>(width_);
		for(ArrayList<Character> i : render){
			i = new ArrayList<Character>(height_);
		}
	}
	@Override
	public boolean getInput(char c) {
		// TODO Auto-generated method stub
		return false;
	}


		
}


