package src.io.controller;

import java.util.HashMap;
import java.util.Map.Entry;

import src.Key_Commands;
/**
 * Abstract class to handle mapping keys to commands for various game modes. 
 * @author mbregg
 *
 */
abstract class  KeyRemapper {

    private HashMap<Character, Key_Commands> remap_ = new HashMap<Character, Key_Commands>();
    Key_Commands NULL_COMMAND;
    public KeyRemapper() {
        initBindings();
        NULL_COMMAND = Key_Commands.DO_ABSOLUTELY_NOTHING;
    }

    protected abstract void initBindings();
       

    public void setMap(HashMap<Character, Key_Commands> remap) {
        remap_ = remap;
    }
    public String getBindingList(){
    	StringBuilder result = new StringBuilder();
    	for(Entry<Character, Key_Commands> entry : remap_.entrySet()){
    		result.append(entry.getValue().toString()+": " + entry.getKey()+System.lineSeparator());
    	}
    	return result.toString();
    }

    public HashMap<Character, Key_Commands> getMap() {
        return remap_;
    }

    public Key_Commands
    mapInput(char input) {
        Key_Commands command = remap_.get(input);
        if (command == null) {
            return NULL_COMMAND;
        }
        return command;
    }
/**
 * Puts the given command in the map with the char key. 
 * If the command is null, it is not put into the map, and -1 is returned to indicate this. 
 * @param input
 * @param command
 * @return
 */
    public int bind(char input, Key_Commands command) {
    	if(command == null){return -1;}
        remap_.put(input, command);
        return 0;
    }
}
