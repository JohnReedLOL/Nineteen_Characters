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
	public final static String attack = "Hit the attack key to [ Attack ]"; // causes the entity to engage in combat with you
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

	//String from the command mini module
	public final static String fontsizeHelp = "/fontsize [number] sets the fontsize in the main game screen,"+System.lineSeparator()+
			"chatboxes, and list areas to the given number";
	public final static String command_error = "Invalid Command";
	public final static String help = "/controls - lists all controls" +System.lineSeparator()+
			"/save filename  - saves game to a file named filename" + System.lineSeparator() +
			"/save-controls filename - saves the current control layout" + System.lineSeparator()+
			"^ Save game commands will use a date/time if no filename is given" + System.lineSeparator()+
			"/rebind COMMAND_NAME key - rebinds a key to that command" + System.lineSeparator()+
			"/load filename - loads a file" + System.lineSeparator()+
			"/load-controls filename - loads the current controls" + System.lineSeparator()+
			"/pwd - prints the directory that this game will look in" + System.lineSeparator()+
			"/bindings - display the current key bindings"+System.lineSeparator()+
			"/set-control entityname - allows you to control another entitiy." +System.lineSeparator()+
                        "^ If entityname doesn't exist, you will be controlling nothing."+System.lineSeparator()+
			"^ Setting control to a non existent entity ends the game"+System.lineSeparator()+
                        "/set-ip ip-address - switches to a different ip address." +System.lineSeparator()+
			fontsizeHelp;
	public final static String rebindHelp = 
			"Format is : /rebind CONTROL_NAME key" + System.lineSeparator() +
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
        /*public final static String setTCP = "Type in /set-tcp [yes/no] to switch to TCP or UDP." +
			System.lineSeparator() +
			"If no such valid ip exists, you will be controlling localhost.";*/
	public final static String setControlHelp = "Type in /set-control [entityname] to switch to controlling a different ip." +
			System.lineSeparator() +
			"If no such ip exists, you will be controlling nothing."+
			System.lineSeparator()+
			"Note that attempting to control a non existent ip results in controlling localhost";
        public final static String setCompressionHelp = "Type in /set-compressuib [on/off] to switch to enable/disable frame compression.";
	public final static String setControlSuccess = "Succesfully set control!";
	public final static String setControl_error = "Error, didn't give who to control!";

	
	// For skills
	public final static String hurtYourself = "You hurt yourself instead!";
	public final static String failed = "You failed to cast ";
}
