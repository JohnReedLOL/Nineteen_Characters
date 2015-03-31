/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package src;

/**
 * The map is obligated to accept these 39 key commands.
 * @author JohnReedLOL
 */
public enum Key_Commands {
	MOVE_STATIONARY, MOVE_UP, MOVE_UPRIGHT, MOVE_RIGHT, MOVE_DOWNRIGHT, 
	MOVE_DOWN, MOVE_DOWNLEFT, MOVE_LEFT, MOVE_UPLEFT, STANDING_STILL,

	PICK_UP_ITEM, DROP_LAST_ITEM, USE_LAST_ITEM, TOGGLE_VIEW,

	EQUIP_LAST_ITEM, UNEQUIP_EVERYTHING,

	BECOME_SMASHER, BECOME_SUMMONER, BECOME_SNEAK, BECOME_UNEMPLOYED,

	GET_INTERACTION_OPTIONS, GET_CONVERSATION_STARTERS, GET_CONVERSATION_OPTIONS,
	// IO_Bundle will return 4 strings
			// Must provide additional string parameter for talk.

	BIND_WOUNDS, BARGAIN_AND_BARTER, OBSERVE, ATTACK,
	// BARGAIN_AND_BARTER returns a list of item names and prices as strings
	BUY_ITEM_BY_NAME, // also accepts a string

	USE_SKILL_1, USE_SKILL_2, USE_SKILL_3, USE_SKILL_4,

	INCREMENT_BIND, INCREMENT_BARGAIN, INCREMENT_OBSERVE,
	INCREMENT_SKILL_1, INCREMENT_SKILL_2, INCREMENT_SKILL_3, INCREMENT_SKILL_4,

	SAVE_GAME, LOAD_GAME, DO_ABSOLUTELY_NOTHING,
	//The save class itself is named SAVE_GAME, so I'd going with that over save_map

	//Any commands needed for map editing will go here
	MAP_INSERT, MAP_CENTER,
	
	//Swap sub occupation
	SWAP_SUB_OCCUPATION

}
