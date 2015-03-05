/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package src.io.view;

import java.awt.Color;

import src.IO_Bundle;


/**
 * Players see the MapView while they are interacting with the map
 *
 * @author Matthew B, Jessan, JohnReedLOL
 */

public final class MapView extends Viewport {
    
    // map_relationship_ is used in place of a map_referance_
    private transient char[][] view_contents_;
    private transient Color[][] color_contents_;


	@Override
	public void renderToDisplay(IO_Bundle bundle) {
		render(bundle);//All that's needed for now.
		
	}
	/*
	 * Constructor
	 */
	public MapView(){
		super();
		view_contents_ = getCharContents();	
		color_contents_ = getColorContents();
	}
	/*
	 * Helper method to keep renderToDisplay pure
	 */
	private void render(IO_Bundle bundle){
		clear();
		makeSquare(0, 0,this.getWidth()-1,this.getHeight()-1);
		if(bundle.view_for_display_ == null || bundle.view_for_display_.length == 0){return;}
		for(int i = 0;i<this.getWidth()-2 && i < bundle.view_for_display_[0].length;++i){
			for(int j = 0;j<this.getHeight()-2 && j< bundle.view_for_display_.length;++j){
				view_contents_[i+1][j+1] = bundle.view_for_display_[bundle.view_for_display_.length-j-1][i];
				color_contents_[i+1][j+1] = bundle.color_for_display_[bundle.view_for_display_.length-j-1][i];
			}

		}
	}
	@Override
	public boolean getInput(char c) {
		// TODO Auto-generated method stub
		return false;
	}
}
