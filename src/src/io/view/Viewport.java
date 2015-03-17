package src.io.view;
import java.awt.Color;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import src.IO_Bundle;
import src.model.Vector2;
import src.model.constructs.items.Item;

/**
 * Abstract view class that the views inherit from.
 * Contains some basic drawing functions, and a map relation.
 * @author Matthew B
 */
public abstract class Viewport {

    private final int height_;
    private final int width_;
	private transient char[][] view_contents_;
	private transient Color[][] color_contents_;
	private transient String item_list_ = "";
	private transient String equipped_list_ = "";
	public int getHeight(){return height_;}
	public int getWidth(){return width_;}
    private static ArrayList<String> game_over_ = null;
	public abstract boolean getInput(char c);

	public Viewport(){
		height_ = 40;
		width_ = 80;
		view_contents_ = new char[width_][height_];
		color_contents_ = new Color[width_][height_];
		if(game_over_ == null){
			game_over_ = getAsciiArtFromFile("ASCIIART/gameover.txt");
		}

	}
	public Viewport(int height,int width){
		height_ = height;
		width_ = width;
		view_contents_ = new char[width_][height_];
		color_contents_ = new Color[width_][height_];
	}
	    
	/**
	 * Tells the view to update it's array contents. 
	 */
	public final void renderToDisplay(IO_Bundle bundle){
		if(bundle == null){return;}
		if(!bundle.is_alive_){
			clear();
			for(int i = 0; i!=game_over_.size();++i){writeStringToContents(0, i, game_over_.get(i));}
			return;
		}
		renderToDisplayInternally(bundle);
	}
	protected abstract void renderToDisplayInternally(IO_Bundle bundle);
	/**
	 * returns the contents of a view as a 2D array
	 * @return the 2D array of characters that represents the view
	 */
	public char[][] getCharContents(){
	initGuard();
	return view_contents_;	
	}
	public Color[][] getColorContents(){
		initGuard();
		return color_contents_;
	}
	/**
	 * Populate the items string
	 * @param bundle
	 */
	protected void populateItems(IO_Bundle bundle){
		item_list_ = "";
		if(bundle.getInventory()!=null){
			for(Item i : bundle.getInventory()){
				item_list_+=i.getName()+System.lineSeparator();
			}
		}
	}
	/**
	 * Populate the Equipped string
	 * @param bundle
	 */
	protected void populateEquipped(IO_Bundle bundle){
		equipped_list_ = "";
		if(bundle.primary_!= null){
			equipped_list_ += "Primary: "+System.lineSeparator()+"   "+bundle.primary_.getName();
		}
		if(bundle.second_ != null){
			equipped_list_ += "Secondary : "+System.lineSeparator()+"   "+bundle.second_.getName();
		}
	}
	/**
	 * 
	 * @return Returns a newline separated inventory list.
	 */
	public String getItemList(){
		return item_list_;
	}
	/**
	 * 
	 * @return Returns a newline separated equipped list.
	 */
	public String getEquippedList(){
		return equipped_list_;
	}
	/**
	 * Load in ascii art from file
	 * @return Array list of the strings of the ascci art
	 * @param file path
	 * @example getAsciiArtFromFile("src/view/ASCIIART/stats.txt");
	 * @exception Prints to error line and returns empty ArrayList in event of failure.
	 */
	public ArrayList<String> getAsciiArtFromFile(String input){
		ArrayList<String> art = new ArrayList<String>();
		try {
            InputStream in =this.getClass().getResourceAsStream(input);
            if(in == null){throw new java.io.FileNotFoundException(input);}
		    BufferedReader reader = new BufferedReader(new InputStreamReader(in));
		    String line = null;
		    while ((line = reader.readLine()) != null) {
		        art.add(line);
		    }
		} catch (IOException x) {
		    System.err.println(x); //Sure, that works. 
		}
		
		return art;

	
	}
	/*
	 * Clear the current array
	 * Helper method for rendering and such
	 */
	protected void clear(){
		if(view_contents_==null){return;}//Avoid doing this on null array.
		for(int j = 0; j!=height_;++j){
			for(int i = 0; i!=width_;++i){
				{
					view_contents_[i][j]=' ';
					color_contents_[i][j] = null;
					}
			}	
		}
	}
	/* 
	 * If someone tries to touch the view_contents_ prior to it being set, fix that.
	 */
	private void initGuard(){
		if(view_contents_ == null){
			view_contents_=new char[width_][height_];
			color_contents_ = new Color[width_][height_];
			clear();
		}
	}
	
	/**
	 * Right justifies a string
	 * @author Jack
	 */
	protected String rightAlign(int length, String text) {
		if (length < text.length())
			return text;
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < length-text.length(); i++)
			sb.append(' ');
		return sb.toString() + text;
	}
	
	/**
	 * Writes the string given into view from the given starting coords. 
	 * @returns false if not enough room/invalidposition
	 * @param int x, int y, String in. Starting coords, and string to write.
	 * 
	*/
	protected boolean writeStringToContents(int x, int y, String in) {
		initGuard();

		if(x+(in.length()-1) >= width_){return false;}
		if(y>=height_) {return false;}
		if(x<0 || y < 0){return false;}
		for(int i = 0; i < in.length();i++){
                    view_contents_[x+i][y] = in.charAt(i);
                    color_contents_[x+i][y] = Color.black;
                }
		return true;
	}
	/**
	 * Writes the string given into view from the given starting coords. 
	 * @returns false if not enough room/invalidposition
	 * @param Vector2 coord, String in. Starting coords, and string to write.
	 * 
	*/
	protected boolean writeStringToContents(Vector2 coord, String in){
		return writeStringToContents(coord.x(),coord.y(),in);
	}
	  /**
     * @returns false if not enough room /invalid position
     * @param int x, int y, int length, int width. Starting position + size.
     *
     */
    protected boolean makeSquare(int x, int y, int length, int width){
    	initGuard();
    	//Bounds checking
    	if(x+length >= width_) {return false;}
    	if(y+width >= height_){return false;}
    	if(x<0 || y < 0){return false;}
    	//Begin filling the square
    	for(int i = x+1; i<x+length;++i){//offset by one to handle corners
    		view_contents_[i][y] = '═';
    		view_contents_[i][y+width] = '═';
    	}
    	for(int i = y+1; i<y+width;++i){//see above
    		view_contents_[x][i] = '║';
    		view_contents_[x+length][i] = '║';
    	}
    	//Corner time
    	view_contents_[x+length][y+width] = '╝';
    	view_contents_[x+length][y] = '╗';
    	view_contents_[x][y+width] = '╚';
    	view_contents_[x][y] = '╔';
    	return true;
    	
    }
	  /**
     * @returns false if not enough room /invalid position
     * @param Vector2 coord, int length, int width. Starting position + size.
     *
     */
  protected boolean makeSquare(Vector2 coord, Vector2 size){
	  return makeSquare(coord.x(),coord.y(),size.x(),size.y());
  }

    // TODO: add fold

}
