package src.io.controller;
import src.Key_Commands;
/**
 * The remapper for the mapeditor game mode
 * @author mbregg
 *
 */
class MapEditRemapper extends KeyRemapper {

	public MapEditRemapper() {

	}

	@Override
	protected void initBindings() {
		 	bind('1', Key_Commands.MOVE_DOWNLEFT);
	        bind('2', Key_Commands.MOVE_DOWN);
	        bind('3', Key_Commands.MOVE_DOWNRIGHT);
	        bind('4', Key_Commands.MOVE_LEFT);
	        bind('5', Key_Commands.STANDING_STILL);
	        bind('6', Key_Commands.MOVE_RIGHT);
	        bind('7', Key_Commands.MOVE_UPLEFT);
	        bind('8', Key_Commands.MOVE_UP);
	        bind('9', Key_Commands.MOVE_UPRIGHT);
	        bind(' ',Key_Commands.MAP_INSERT);
	        bind('C',Key_Commands.MAP_CENTER);
	}

}
