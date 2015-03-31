/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package src.io.view;

import java.util.ArrayList;

import src.IO_Bundle;

/**
 * Players see the AvatarCreationView when they chose their occupation.
 *Generates the view, loads the ascii art, and then gives it to display to print
 * @author Matthew B, Jessan
 */
public final class AvatarCreationView extends Viewport
{
    // Converts the class name into a base 35 number
    private ArrayList<String> title_;
    private ArrayList<String> smasherArt_;
    private ArrayList<String> sneakerArt_;
    private ArrayList<String> summonerArt_;
    private static char summonSelect_ = 'X';
    private static char smashSelect_ = 'Z';
    private static char sneakSelect_ = 'C';
    private static String sneakerString_ = String.valueOf(sneakSelect_)+" default to Select a Sneaker:";
    private static String summonerString_ = String.valueOf(summonSelect_)+" default to Select a Summoner:";
    private static String smasherString_ = String.valueOf(smashSelect_)+" default to Select a Smasher:";
    public static void setChoosableChars(char sneak, char smash, char summon){
    	summonSelect_ = summon;
    	sneakSelect_ = sneak;
    	smashSelect_ = smash;
    	sneakerString_ = String.valueOf(sneakSelect_)+" to Select a Sneaker:";
        summonerString_ = String.valueOf(summonSelect_)+" to Select a Summoner:";
        smasherString_ = String.valueOf(smashSelect_)+" to Select a Smasher:";
    }
    /* 
     * Internal method to assit with the rendering, does most of the bulk work, separated out so
     * that renderToDisplay remains pure
     */
    private void renderArray(){
    	makeSquare(0,0,this.getWidth()-1,this.getHeight()-1);
    	for(int i = 0; i!=title_.size();++i){
    		writeStringToContents(5,1+i,title_.get(i));
    	}

    	int heightFromBottom = this.getHeight()-sneakerArt_.size()-1;
    	for(int i = 0; i!=sneakerArt_.size();++i){
    		int offCenteredLength = this.getWidth()/2-sneakerArt_.get(0).length()/2;
    		writeStringToContents(offCenteredLength,heightFromBottom+i,sneakerArt_.get(i));
    		//Above is a bit long, but I don't really think it's worth moving to a function, doing so would be just as cluttering. 
    	}
    	
    	writeStringToContents(1,heightFromBottom+1,sneakerString_);
    	heightFromBottom-=smasherArt_.size();
    	for(int i = 0; i!=smasherArt_.size();++i){
    	 writeStringToContents(1,heightFromBottom+i,smasherArt_.get(i));
    	}
    	heightFromBottom--;
    	writeStringToContents(1,heightFromBottom,smasherString_);
    	
    	//Summoner positioning a bit weird
    	heightFromBottom+=summonerArt_.size()-3;//3 is the magic constant. Really, it makes it looks properly spaced.
    	int midpoint = this.getWidth()/2;
    	writeStringToContents(midpoint,heightFromBottom,summonerString_);
    	++heightFromBottom;
    	for(int i = 0; i!=summonerArt_.size();++i){
    		writeStringToContents(midpoint,heightFromBottom+i,summonerArt_.get(i));
    	}


    }

    /*
     * Generates a new ChaAvatarCreationViewses avatar_reference_ to modify the avatar.
     */
    public AvatarCreationView() {
    	super(40, 80);
		title_ = getAsciiArtFromFile("ASCIIART/class.txt");
		smasherArt_ = getAsciiArtFromFile("ASCIIART/smasher.txt");
		sneakerArt_ = getAsciiArtFromFile("ASCIIART/sneaker.txt");
		summonerArt_ = getAsciiArtFromFile("ASCIIART/summoner.txt");
		renderArray();
    	
    }

    /* 
     * 
     * @see src.view.Viewport#renderToDisplay()
     */
	@Override
	public void renderToDisplayInternally(IO_Bundle bundle) {
		clear();
		renderArray();
		populateEquipped(bundle);
		populateItems(bundle);
		
		
	}

	@Override
	public boolean getInput(char c) {
		// No input to grab
		return false;
	}

}
	
