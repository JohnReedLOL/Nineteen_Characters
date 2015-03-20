/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package src;

/**
 *
 * @author JohnReedLOL
 */
public class HardCodedStrings {
	public final static String getChatOptions = "[ Greet ]"; // lists all greetings
	public final static String getReplyOptions = "[ Continue ]"; // lists all replies
	public final static String attack = "[ Attack ]"; // causes the entity to engage in combat with you
	public final static String getAllSkills = "[ Skill ]";
	public final static String bind = "[ Bind ]"; // uses your bind ability
	public final static String barter = "[ Barter ]"; // lists items and prices
	public final static String observe = "[ Observe ]";
	public final static String skill_1 = "[ Skill_1 ]";
	public final static String skill_2 = "[ Skill_2 ]";
	public final static String skill_3 = "[ Skill_3 ]";
	public final static String skill_4 = "[ Skill_4 ]";
	public final static String useItem = "[ Use_Item ]"; // causes the entity to check your inventory for a usable item
	public final static String getItemList = "[ Item ]"; // lists items that this entity will accept
	public final static String trade = "[ Trade ]"; // actually puts the item into your inventory

	//Command list
	public final static String gameCommands = src.enumHandler.getAllCommands();
	//String from the command mini module
	public final static String fontsizeHelp = "Format /fontsize [number] "+System.lineSeparator()+
			"Sets the fontsize in the main game screen, chatboxes, and list areas to the given number";
	public final static String command_error = "Invalid Command";
	public final static String help = "Type /controls to list all controls" +System.lineSeparator()+
			"Type /save filename to save" + System.lineSeparator() +
			"TYpe /save-controls filename to save the current control layout" + System.lineSeparator()+
			"Note that both save games will save with a date format if no filename is given" + System.lineSeparator()+
			"Type /rebind command char to rebind a key" + System.lineSeparator()+
			"Type /load filename to load a file" + System.lineSeparator()+
			"Type /load-controls filename to load the current controls" + System.lineSeparator()+
			"Type /help for this dialog"+System.lineSeparator()+
			"Type /pwd to print the directory the game will look in" + System.lineSeparator()+
			"Double click a command in the command box to use it!" + System.lineSeparator()+
			"Type /bindings to see the key bindings"+
			"Type in /set-control [entityname] to switch to controlling entitiy." +
                        "Type in /set-ip [ip-address] to switch to controlling a different ip." +
                        "Type in /set-tcp [on/off] to switch to using TCP instead of UDP." +
			System.lineSeparator() +
			"If no such entity exists, you will be controlling nothing."+
			System.lineSeparator()+
			"Note that attempting to control a non existent entity results in game over"+System.lineSeparator()+
			fontsizeHelp;
	public final static String rebindHelp = 
			"Format is : /rebind CONTROL char" + System.lineSeparator() +
			"Example : /rebind MOVE_UP u" + System.lineSeparator()+
			"For commands available for rebinding, type /commands , or type /help for help.";
	public final static String saveHelp = "Type in /save '[filename]'. The file will then be saved to disk.";
	public final static String loadHelp= "Type in '/load [filename]'. The file will be loaded from disk";
	public final static String saveControlsHelp = "Type in '/save-controls [filename]'. The controls will be saved to disk";
	public final static String loadControlsHelp = "Type in '/load-controls [filename]'. The controls will be loaded from disk";
	public final static String controlsHelp = "Type in '/controls'. Will list all viable controls that can be rebound via /rebind";
	public final static String helpHelp = "Type in '/help'. Will list all commands and summaries of them.";
	public final static String pwdHelp= "Type in '/pwd'. Will print the current directory.";
	public final static String manHelp= "Type in '/man [commands]'. For information about a command.";
	public final static String bindingsHelp = "Type in /bindings to get a list of controls bound to keys, in the form "+
			System.lineSeparator()+
			"/[control]: [key]";
        public final static String setIPHelp = "Type in /set-ip [ip-address] to switch to controlling a different ip." +
			System.lineSeparator() +
			"If no such valid ip exists, you will be controlling localhost.";
        public final static String setTCP = "Type in /set-tcp [yes/no] to switch to TCP or UDP." +
			System.lineSeparator() +
			"If no such valid ip exists, you will be controlling localhost.";
	public final static String setControlHelp = "Type in /set-control [entityname] to switch to controlling a different ip." +
			System.lineSeparator() +
			"If no such ip exists, you will be controlling nothing."+
			System.lineSeparator()+
			"Note that attempting to control a non existent ip results in controlling localhost";
	public final static String setControlSuccess = "Succesfully set control!";
	public final static String setControl_error = "Error, didn't give who to control!";

}
