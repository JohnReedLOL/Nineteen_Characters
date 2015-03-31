package src.io.controller;
import src.Key_Commands;
/**
 * Handles the mappings the main game mode.
 * @author mbregg
 *
 */
class GameRemapper extends KeyRemapper {

	public GameRemapper() {
		// TODO Auto-generated constructor stub
	}

	@Override
	protected void initBindings() {
		 //Character Creation
        bind('Z', Key_Commands.BECOME_SMASHER);
        bind('X', Key_Commands.BECOME_SUMMONER);
        bind('C', Key_Commands.BECOME_SNEAK);
        //Directions NUMPAD
        bind('1', Key_Commands.MOVE_DOWNLEFT);
        bind('2', Key_Commands.MOVE_DOWN);
        bind('3', Key_Commands.MOVE_DOWNRIGHT);
        bind('4', Key_Commands.MOVE_LEFT);
        bind('5', Key_Commands.STANDING_STILL);
        bind('6', Key_Commands.MOVE_RIGHT);
        bind('7', Key_Commands.MOVE_UPLEFT);
        bind('8', Key_Commands.MOVE_UP);
        bind('9', Key_Commands.MOVE_UPRIGHT);
        //Directions Keyboard
        bind('z', Key_Commands.MOVE_DOWNLEFT);
        bind('x', Key_Commands.MOVE_DOWN);
        bind('c', Key_Commands.MOVE_DOWNRIGHT);
        bind('a', Key_Commands.MOVE_LEFT);
        bind('s', Key_Commands.STANDING_STILL);
        bind('d', Key_Commands.MOVE_RIGHT);
        bind('q', Key_Commands.MOVE_UPLEFT);
        bind('w', Key_Commands.MOVE_UP);
        bind('e', Key_Commands.MOVE_UPRIGHT);
        //Interact up bindings.
        bind('p', Key_Commands.PICK_UP_ITEM);
        bind('D', Key_Commands.DROP_LAST_ITEM);
        bind('A', Key_Commands.ATTACK);
        bind('R', Key_Commands.INCREMENT_BARGAIN);
        bind('E', Key_Commands.EQUIP_LAST_ITEM);
        bind('U', Key_Commands.UNEQUIP_EVERYTHING);
        bind('i', Key_Commands.TOGGLE_VIEW);
        bind('S', Key_Commands.SAVE_GAME);
        bind('u', Key_Commands.USE_LAST_ITEM);
        bind('T', Key_Commands.GET_INTERACTION_OPTIONS);
        //Spend skillpoints
        bind('r', Key_Commands.INCREMENT_BIND);
        bind('t', Key_Commands.INCREMENT_BARGAIN);
        bind('y', Key_Commands.INCREMENT_OBSERVE);
        bind('f', Key_Commands.INCREMENT_SKILL_1);
        bind('g', Key_Commands.INCREMENT_SKILL_2);
        bind('h', Key_Commands.INCREMENT_SKILL_3);
        bind('v', Key_Commands.INCREMENT_SKILL_4);
        //Use special abilities
        bind('!', Key_Commands.BIND_WOUNDS);
        bind('@', Key_Commands.BARGAIN_AND_BARTER);
        bind('#', Key_Commands.OBSERVE);
        bind('$', Key_Commands.USE_SKILL_1);
        bind('%', Key_Commands.USE_SKILL_2);
        bind('^', Key_Commands.USE_SKILL_3);
        bind('&', Key_Commands.USE_SKILL_4);
        
        //Occupation sub switching
        bind('S',Key_Commands.SWAP_SUB_OCCUPATION);
    }
		

}
