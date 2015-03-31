package src;

/**
 * Th is is decent now, bit too much duplication with Key_commands and AddableThingEnum...
 * @author Matthew Breggs
 */
public class enumHandler {

	public static Key_Commands stringCommandToKeyCommand(String in){
		in = in.toUpperCase();
		try{
			Key_Commands e = Key_Commands.valueOf(in);
			return e;
		}catch(IllegalArgumentException e){}

		return null;//Didn't find any matching enums
	}
	public static String getAllCommands(){
		String enums = "";
		for(Key_Commands direction : Key_Commands.values()){
			enums+=direction.toString()+System.lineSeparator();
		}
		return enums;
	}
	public static AddableThingEnum stringCommandToAddable(String in){
		in = in.toUpperCase();
		try{
			AddableThingEnum e = AddableThingEnum.valueOf(in);
			return e;
		}catch(IllegalArgumentException e){}

		return null;//Didn't find any matching enums
	}
	public static String getAllAddables(){
		String enums = "";
		for(AddableThingEnum direction : AddableThingEnum.values()){
			enums+=direction.toString()+System.lineSeparator();
		}
		return enums;
	}
}
