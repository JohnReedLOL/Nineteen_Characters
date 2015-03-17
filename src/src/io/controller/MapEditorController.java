package src.io.controller;

import java.util.ArrayList;
import java.util.concurrent.ConcurrentLinkedQueue;

import src.Key_Commands;
import src.QueueCommandInterface;
import src.enumHandler;
import src.io.view.MapEditorView;
import src.io.view.display.Display;
import src.map.editor.MapAddable;
import src.map.editor.MapAddableFactory;
import src.model.MapMapEditor_Interface;
/**
 * The controller subclass for the mapeditor game mode
 * @author mbregg
 *
 */
public class MapEditorController extends Controller {
	private MapMapEditor_Interface map_;
	private MapEditorView mappy_viewy_ = new MapEditorView();
	private ArrayList<String> spawnables_ = new ArrayList<String>(1);
	private String setToSpawn_ = "";
	private MapAddableFactory factory_= new MapAddableFactory();
	private MapAddable addable = null;
	private ConcurrentLinkedQueue<String> setToSpawnQueue_ = new ConcurrentLinkedQueue<String>();
	private ConcurrentLinkedQueue<String> commandQueue_ = new ConcurrentLinkedQueue<String>();
	CommandMiniController cont_ = new CommandMiniController(MapEditorController.this.getRemapper(), MapEditorController.this);
	public MapEditorController(MapMapEditor_Interface map) {
		super(new MapEditorView(),new MapEditRemapper(), "Temporary Name Map User");
		super.setView(mappy_viewy_);
		map_ = map;


		spawnables_.add(enumHandler.getAllAddables());
		mappy_viewy_.setSpawnableList(spawnables_);
		this.takeTurnandPrintTurn('5');
		Display.getDisplay().setMessage("SWITCH TO THE INVENTORY TAB!!!");
		Display.getDisplay().setMessage("TO USE: Hit space to spawn something. Select what to spawn by " +
				"clicking on it in the item box. Move around as usual. Hitting space with nothing selected spawns\n" +
				"The last thing spawned.");
		Display.getDisplay().addDoubleClickCommandEventReceiver(new QueueCommandInterface<String>() {


			@Override
			public void enqueue(final String command) {
				if(command!=null){
					setToSpawnQueue_.add(command);
				}
			}

			@Override
			public void sendInterrupt() {
				MapEditorController.this.sendInterrupt();
				
			}
		});
		
		Display.getDisplay().addInputBoxTextEnteredFunction(new QueueCommandInterface<String>() {

			@Override
			public void enqueue(String command) {
				commandQueue_.add(command);
				
			}

			@Override
			public void sendInterrupt() {
				MapEditorController.this.sendInterrupt();
				
			}
			
		});
		this.sleepLoop();
	}

	@Override
	protected void process(){
		while(!setToSpawnQueue_.isEmpty()){
			String foo = setToSpawnQueue_.remove();
			if(foo == null){return;}
			setToSpawn_ = foo;
			setLastSpawned(setToSpawn_);
			updateDisplay();
		}
		while(!commandQueue_.isEmpty()){
			String foo = commandQueue_.remove();
			if(foo.startsWith("/")){Display.getDisplay().setMessage(cont_.processCommand(foo));}
		}
		super.process();
	}
	int x = 0;
	int y = 0;

	@Override
	protected void takeTurnandPrintTurn(Key_Commands foo) {
		switch(foo){
		case MOVE_UP: ++y; break;
		case MOVE_DOWN: --y; break;
		case MOVE_LEFT: --x; break;
		case MOVE_RIGHT: ++x; break;
		case MAP_INSERT: mapInsert(Display.getDisplay().getHighlightedItem()); break;
		case MAP_CENTER: x = 0; y = 0; break;
		case SAVE_GAME: 
		default: break;

		}
		updateDisplay();
		printSpawnablesToDisplay();


	}

	private void printSpawnablesToDisplay(){
		mappy_viewy_.setSpawnableList(spawnables_);
		Display.getDisplay().setCommandList(mappy_viewy_.getItemList());
	}
	private void updateDisplay(){
		updateDisplay(map_.getMapAt(x, y, getView().getWidth()/2, getView().getHeight()/2));
	}

	private void mapInsert(String spawnName) {
		if(spawnName == null){spawnName = setToSpawn_;}
		if(addable == null){
			addable = factory_.getAddable(spawnName);
		}
		if(addable == null){Display.getDisplay().setMessage("Invalid spawnable!"); return;}
		if(addable.addToMap(map_, x,y) == 0){
			setLastSpawned(spawnName);
		}
		if(addable.isEmpty()){addable=null;}
	}
	private  void setLastSpawned(String spawnName){
		mappy_viewy_.setLastSpawned(spawnName);
		setToSpawn_ = spawnName;
	}
	@Override
	public String getUserName() {
		String foo = factory_.mostRecentAvatar();
		System.out.println("foo");
		if(foo == null){return "null";}
		else{return foo;}
		
		
	}

	@Override
	public void saveGame(String foo) {
		map_.saveGame(foo);
		
	}


	@Override
	public void loadGame(String foo) {
		map_.loadGame(foo);
		
	}

}
