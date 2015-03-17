package src.io.controller;

import java.util.HashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

import src.IO_Bundle;
import src.Key_Commands;
import src.QueueCommandInterface;
import src.io.view.Viewport;
import src.io.view.display.Display;
/**
 * Abstract controller class
 * @author mbregg
 *
 */
public abstract class Controller implements QueueCommandInterface<Character> {
	private KeyRemapper remap_;
	private Viewport currentView_;
	private String userName_;
	private ConcurrentLinkedQueue<Key_Commands> keyCommandQueue_ = new ConcurrentLinkedQueue<Key_Commands>();
	private ConcurrentLinkedQueue<Character> characterQueue_ = new ConcurrentLinkedQueue<Character>();
	private Thread controllerThread_ = Thread.currentThread();

	public void setControlling(String in){
		userName_ = in;
	}
	public Controller(Viewport view,KeyRemapper remap,String uName) {
		remap_ = remap;
		currentView_ = view;
		userName_ = uName;
		Display.getDisplay().addDirectCommandReceiver(new QueueCommandInterface<Key_Commands>() {

			@Override
			public void enqueue(Key_Commands command) {
				keyCommandQueue_.add(command);
				
			}

			@Override
			public void sendInterrupt() {
				Controller.this.sendInterrupt();
				
			}

						//takeTurnandPrintTurn(foo);


		});
		Display.getDisplay().addGameInputerHandler(this);
		Display.getDisplay().setView(currentView_);
		Display.getDisplay().printView();
	}
	
	protected void sleepLoop(){
		while(true){
		 try {
			 	System.out.println("Sleeping");
		        Thread.sleep(Long.MAX_VALUE);
		    } catch (InterruptedException e) {
		       process();
		    }
		}
	}
	
	protected void process(){
		System.out.println("Processing!");
		while(!keyCommandQueue_.isEmpty()){
			takeTurnandPrintTurn(keyCommandQueue_.remove());
		}
		while(!characterQueue_.isEmpty()){
			takeTurnandPrintTurn(characterQueue_.remove());
		}
		
	}
	protected Viewport getView(){return currentView_;}
	protected void setView(Viewport view){currentView_=view;}

	protected void takeTurnandPrintTurn(char foo) {
		Key_Commands input = getRemapper().mapInput(foo);
		takeTurnandPrintTurn(input);
	}
	protected abstract void takeTurnandPrintTurn(Key_Commands foo);

	public String getUserName() {
		return userName_;
	}

	protected KeyRemapper getRemapper(){return remap_;}
	/**
	 * Gets the underlying key remapping values
	 *
	 * @return A HashMap with the remapped key values in it
	 * @author Alex Stewart
	 */
	public HashMap<Character, Key_Commands> getRemap() {
		if (remap_ == null) {
			return null;
		}
		return remap_.getMap();
	}

	/**
	 * Sets the underlying key remapping
	 *
	 * @param remap The new key remapping to be applied
	 * @author Alex Stewart
	 */
	public void setRemap(HashMap<Character, Key_Commands> remap) {
		if (remap_ == null) {
			remap_ = new GameRemapper();
		}
		remap_.setMap(remap);
	}


	/**
	 * Takes the given iobundle and updates display with it's content
	 * @param bundle
	 */
	public void updateDisplay(IO_Bundle bundle){
		getView().renderToDisplay(bundle);
		Display.getDisplay().setView(getView());
		Display.getDisplay().printView();
	}
	@Override
	public void enqueue(Character c){
		characterQueue_.add(c);
	}
	@Override
	public void sendInterrupt(){
		try{
			System.out.println("Interuppting!");
		controllerThread_.interrupt();
		}catch(Exception e){
			System.err.println("Failed to interupt thread for input...Controller");
			e.printStackTrace();
		}
	}
		



	/**
	 * Should be overridden to save the file with the name given, if no name given, save with date.
	 * @param foo
	 */
	public abstract void saveGame(String foo);
	/**
	 * Should be overrridden to load given save file. 
	 * @param foo
	 */
	public abstract void loadGame(String foo);



}
