/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package src.io.view;

import java.awt.Font;
import java.awt.GraphicsConfiguration;
import java.io.InputStream;
import java.io.Serializable;

import javax.swing.JFrame;
import javax.swing.JMenuBar;
import javax.swing.JTextPane;
import javax.swing.text.StyledDocument;

/**
 * Represents a single player's display. Has a static game wide message.
 *
 * @author Matthew B, JohnReedLOL
 */
public class Display {

    // Converts the class name into a base 35 number
	private static String message_ = "";
	private static int counter_ = 0;
	static private Display display_ = null;
	private JTextPane pane_ = null;
	private JFrame frame_ = null;
	/* 
	 * Static method, sets to what is being output the given string, for counter frames
	 * Note that is handles multiline strings, but pushes the view up for each line.
	 * Don't abuse please
	 * @params String m : The message string, int counter : The frames to display it for
	 */
	public static void setMessage(String m, int counter){
		message_ = m;
		counter_ = counter;
	}
    private static final long serialVersionUID = Long.parseLong("Display", 35);
    private Font getFont(){
    	InputStream in = this.getClass().getResourceAsStream("Font/DejaVuSansMono.ttf");
    	try{
    		return Font.createFont(Font.TRUETYPE_FONT, in);
    	}
    	catch(Exception e){
    		System.err.println(e.toString());
    		return null;
    	}
    }
    private void setFont(){
    	Font font = getFont();
    	if(font == null){return;}//If we failed to load the font, do nothing
    	Font  resized = font.deriveFont(20f);//This line sets the size of the game, not sure how to make it dynamic atm
    	pane_.setFont(resized);
    	return;
    }
    /**
     * Create a display from a Viewport
     * @author Matthew B
     * @param Viewport
     * @return Display
     */
    private Display(){
    	 frame_ = new JFrame("NineTeen Characters");
    	frame_.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    	frame_.setJMenuBar(new JMenuBar());
    	pane_ = new JTextPane();
    	setFont();
    	pane_.setEditable(false);
    	StyledDocument doc = pane_.getStyledDocument();
    	try{
    	doc.insertString(0, "test", null);
    	}
    	catch(Exception x){
    		
    	}
    	frame_.add(pane_);
    	frame_.setExtendedState(JFrame.MAXIMIZED_BOTH); 
    	frame_.setVisible(true);

    }
    static public Display getDisplay(){
    	if (display_ == null){
    		display_ = new Display();
    	}
    	return display_;
    }
    static public Display getDisplay(Viewport _view){
    	Display _display = getDisplay();
    	_display.setView(_view);
    	return _display;
    }
    private boolean guard(){
    	if (current_view_ == null){ System.err.println("DISPLAY VIEW NULL"); return true;}
    	return false;
    }
    private Viewport current_view_;
    /**
     * Print the currently held view
     * 
     */
    public void printView() {
    	if(guard()){return;}
    	current_view_.renderToDisplay();
    	this.clearScreen();
        char[][] in = current_view_.getContents();
        StringBuilder out = new StringBuilder();
    		// Use this to print a 2D array
    		for(int j = 0; j!=current_view_.height_;++j){
    			for(int i = 0; i!=current_view_.width_;++i){
    				System.out.print(in[i][j]);
    				out.append(in[i][j]);
    			}
    			System.out.print(System.lineSeparator());
    			out.append(System.lineSeparator());
    		}
    		StyledDocument doc = pane_.getStyledDocument();
    		try{
    		doc.insertString(0,out.toString(),null);
    		}
    		catch(Exception e){System.err.println(e.toString());}

		
		if(counter_ > 0){System.out.println(message_);--counter_;}
    }
    /* 
     * Helper method to handle 'clearing' the screen
     */
    private void clearScreen(){
    	//Create the illusion of clearing the screen.
    	pane_.setText("");
    }
    /**
     * Change the viewport held by the display
     * @author Matthew B
     * @param Viewport
     * @return Display
     */
    public void setView(Viewport _view){
    	current_view_ = _view;
    }
    /*
     * Does nothing atm, int incase we want to return error codes later.
     */
    public int open(){
    	return 1;
    }
    /*
     * Does nothing atm, int incase we want to return error codes later.
     */
    public int close(){
    	return 0;
    }
}
