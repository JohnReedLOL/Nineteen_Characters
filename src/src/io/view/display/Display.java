/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package src.io.view.display;

import java.awt.Color;
import java.awt.EventQueue;

import javax.swing.text.DefaultStyledDocument;
import javax.swing.text.MutableAttributeSet;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

import src.Function;
import src.Key_Commands;
import src.io.view.Viewport;

/**
 * Represents a single player's display. Has a static game wide message.
 *
 * @author Matthew B
 */
public class Display {
	/**
	 * The chatbox.
	 * It is it's own listener, and on the enter keypress, it flushes the input box into the output. 
	 * Has methods to add messages, and can take in anything that implements the Function interface
	 * Things passed in that implement Function interface are called with a String containing the contents of input, 
	 * before it is cleared when input is hit. 
	 * @author Mbregg
	 *
	 */

    // Converts the class name into a base 35 number
	static private Display display_ = null;
	/**
	 * Sends text to the command text pane.
	 * @param in
	 */
	public void setCommandList(String in){
		EventQueue.invokeLater(new commandRunnable(in));
	}
	/**
	 * The runnable to handle sending message to the GUI
	 * @author mbregg
	 *
	 */
	private class commandRunnable implements Runnable{
		String message_;
		public commandRunnable(String m) {
			message_ = m;
		}
		@Override
		public void run() {
			Key_Listener_GUI.getGUI().setCommands(message_);
		}
	}

	/**
	 * Puts the given message in the chatboxes output box
	 * @param m The string to output
	 */
	public void setMessage(String m){
		EventQueue.invokeLater(new messageRunnable(m));
	}
	/**
	 * The runnable to handle sending message to the GUI
	 * @author mbregg
	 *
	 */
	private class messageRunnable implements Runnable{
		String message_;
		public messageRunnable(String m) {
			message_ = m;
		}
		@Override
		public void run() {
			Key_Listener_GUI.getGUI().addMessage(message_);
		}
	}

    /**
     * Create a display from a Viewport
     * @author Matthew B
     * @param Viewport
     * @return Display
     */
    private Display(){
    	java.awt.EventQueue.invokeLater(new Runnable() {
			public void run() { Key_Listener_GUI.getGUI().setVisible(true);}});

    }
    /**
     * Gets the display
     * @return Returns a reference to the Display
     */
    static public Display getDisplay(){
    	if (display_ == null){
    		display_ = new Display();
    	}
    	return display_;
    }
    /**
     * Does the same as above, but also sets the Displays view to be the given view
     * @param _view
     * @return
     */
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
     * The runnable to send the current styled doc to the GUI
     * @author mbregg
     *
     */
    private class setDocumentRunnable implements Runnable{
    	private StyledDocument doc_;
    	public setDocumentRunnable(StyledDocument d){
    		doc_ = d;
    	}
    	@Override
    	public void run() {
    		Key_Listener_GUI.getGUI().setGameContent(doc_);
    	}
    }
    /**
     * Print the currently held view
     * 
     */
    public void printView() {
    	if(guard()){return;}
        char[][] in = current_view_.getCharContents();
        Color[][] colors = current_view_.getColorContents();
        StringBuilder out = new StringBuilder();
    		// Use this to print a 2D array
    		for(int j = 0; j!=current_view_.getHeight();++j){
    			for(int i = 0; i!=current_view_.getWidth();++i){
    				if(!(Color.white.equals(colors[i][j]))){
    					out.append(in[i][j]);
    				}
    				else {out.append(' ');}//Append a space rather than coloring white. 

    			}
    			out.append(System.lineSeparator());
    		}
    		StyledDocument doc = new DefaultStyledDocument();
    		try{
    		doc.insertString(0,out.toString(),null);
    		}
    		catch(Exception e){System.err.println(e.toString());}
    		
    		for(int j = 0; j!=current_view_.getHeight();++j){
    			Color currColor = null;
    			int currColorCount = 0;
    			int oldIndex = 0;
    			/* 
    			 * What's going on here: Also: Yes, I profiled and tested, it makes a big difference.
    			 * Color assigning for a single char is inefficient, so to avoid that, since colors often appear lots in a row, we count that row up, and then render it
    			 * in one big block when the color changes. So, when currColor no longer equals the colors[i][j], it is rendered, with the oldIndex and count, 
    			 * which are then reset. 
    			 *   black is the default, no need to do that. That optimization is done in the colorChar method. 
    			 *   Right now white is ignored and replaced with a space. , since it'd just make stuff invisible.
    			 */
    			for(int i = 0; i!=current_view_.getWidth();++i){
    				if(colors[i][j] != null && colors[i][j].equals(currColor)){currColorCount++;}
    				else if(colors[i][j] == null && currColor == null){currColorCount++;}
    				else{
    					if(currColor != null){
    						colorChar(doc,oldIndex,j,currColor,currColorCount);
    					}
						oldIndex = i;
						currColorCount = 1;
						currColor = colors[i][j];
    						
    				}
    			}
			if(currColor != null){
				colorChar(doc,oldIndex,j,currColor,currColorCount);
			}
		}
    		EventQueue.invokeLater(new setDocumentRunnable(doc));
    		//And lastly, take care of the inventory and equipped
    		final String inventory = current_view_.getItemList();
    		final String equipped = current_view_.getEquippedList();
    		EventQueue.invokeLater(new Runnable() {
				@Override
				public void run() {
					Key_Listener_GUI.getGUI().takeInEquipped(equipped);
					Key_Listener_GUI.getGUI().takeInInventory(inventory);
				}
			});
    }
   
    /** 
     * Make the char at this point take on the given attributes.
     * Make sure the x and y are in range.
     * @param x
     * @param y
     * @param the styled doc to color.
     * 
     * @param attr
     */
    private void colorChar(StyledDocument doc, int x, int y, Color color, int length){
    	if(color.equals(Color.white)){return;}
    	if(color.equals(Color.black)){return;}//White is only used for space, so no need to render it. 
    	MutableAttributeSet attr = new SimpleAttributeSet();
    	StyleConstants.setForeground(attr, color);
    	doc.setCharacterAttributes(y*(current_view_.getWidth()+(System.lineSeparator().length()))+x,
    			length, attr, false);
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
    /**
     * Adds a class to be called via the function interface whenever a character is typed in the main gameview.
	 * @param foo : The class to call
     */
    public void addGameInputerHandler(Function<Void,Character> foo){
    	java.awt.EventQueue.invokeLater(new gameInputHandlerRunnable(foo));
	}
    /** 
     * The class handles giving the Function interface to the GUI
     * @author mbregg
     *
     */
    private class gameInputHandlerRunnable implements Runnable{
    	private Function<Void,Character> handler_;
    	public gameInputHandlerRunnable(Function<Void,Character> foo) {
			handler_ = foo;
		}
		@Override
		public void run() {
			Key_Listener_GUI.getGUI().addGameInputerHandler(handler_);	
		}
    	
    }
	/**
	 * Adds a Function<Void,String> object to the list of things called by chatbox on enter
	 * @param Function<Void,String> listen
	 */
	public void addInputBoxTextEnteredFunction(Function<Void,String> listen){
		EventQueue.invokeLater(new inputHandlerRunnable(listen));
	}
    private class inputHandlerRunnable implements Runnable{
    	private Function<Void,String> handler_;
    	public inputHandlerRunnable(Function<Void,String> foo) {
			handler_ = foo;
		}
		@Override
		public void run() {
			Key_Listener_GUI.getGUI().addInputBoxReceiver(handler_);	
		}
    	
    }
	/**
	 * Adds something to listen for characters from the output box. 
	 * @param receiver
	 */
	public void addOutputBoxCharacterFunction(Function<Void,Character> receiver){
		EventQueue.invokeLater(new outputBoxHandlerRunnable(receiver));
	}
	/**
	 * Handles the outbox box input fetching
	 * @author mbregg
	 *
	 */
    private class outputBoxHandlerRunnable implements Runnable{
    	private Function<Void,Character> handler_;
    	public outputBoxHandlerRunnable(Function<Void,Character> foo) {
			handler_ = foo;
		}
		@Override
		public void run() {
			Key_Listener_GUI.getGUI().addoutputBoxReceiver(handler_);	
		}
    	
    }
	/**
	 * Adds a function to be triggered by direct, unremappable things, like buttons. 
	 * @param receiver
	 */
	public void addDirectCommandReceiver(Function<Void,Key_Commands> receiver){
		java.awt.EventQueue.invokeLater(new directHandlerRunnable(receiver));
	}
	 /** 
     * The class handles giving the Function interface to the GUI
     * @author mbregg
     *
     */
    private class directHandlerRunnable implements Runnable{
    	private Function<Void,Key_Commands> handler_;
    	public directHandlerRunnable(Function<Void,Key_Commands> foo) {
			handler_ = foo;
		}
		@Override
		public void run() {
			Key_Listener_GUI.getGUI().addDirectCommandReceiver(handler_);	
		}
    	
    }
    /**
     * When command is double clicked on, the function passed in will receive the highlighted string.
     * @param receiver
     */
    public void addDoubleClickCommandEventReceiver(Function<Void,String> receiver){
    	java.awt.EventQueue.invokeLater(new commandHandlerRunnable(receiver));
    }
    
    private class commandHandlerRunnable implements Runnable{
    	private Function<Void,String> handler_;
    	public commandHandlerRunnable(Function<Void,String> foo) {
			handler_ = foo;
		}
		@Override
		public void run() {
			Key_Listener_GUI.getGUI().addCommandBoxReceiver(handler_);	
		}
    	
    }
    /**
     * Request focus in the outgoing message box.
     */
	public void requestOutBoxFocus() {
        src.io.view.display.Key_Listener_GUI.getGUI().getIncomingText().requestFocusInWindow();
	}
	/** 
	 * Returns  of how many skill buttons there are.
	 * @return Returns  of how many skill buttons there are.
	 */
	public int getSkillButtonCount(){
		return Key_Listener_GUI.getGUI().getSkillButtonCount();
	}
	/**
	 * Returns the skill button of index i. 
	 * Should i not be a valid skill button, returns null.
	 * @param i
	 * @return
	 */
	public javax.swing.JButton getSkillButton(int i) {
		return Key_Listener_GUI.getGUI().getSkillButton(i);
	}
	
	public String getHighlightedItem(){
		return Key_Listener_GUI.getGUI().getHighlightedItem();

	}

}
