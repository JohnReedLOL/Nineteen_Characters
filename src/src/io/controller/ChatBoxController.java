package src.io.controller;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import src.Function;
import src.io.view.display.Display;
/**
 * 
 * @author Mbregg
 * Handles the input from the chatbox controller
 * Implements Function<Void,String> to enable it to be passed into the chatbox via the method in display
 * The apply method in this class is called by the chatbox whenever enter is hit, with the string that was in the chatbox.
 */
public final class ChatBoxController implements Function<Void, String> {
	public ChatBoxController(){
	Display.getDisplay().addChatBoxFunctionEvent(this);
	}
	



	/**
	 * The function that is called by the chat box when enter is hit. Receives contents of input box. 
	 */
	@Override
	public Void apply(String foo) {
		// React to text from chatbox here
		System.out.println("Chatboooox was "+foo);
		return null;
	}

}
