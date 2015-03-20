package src.io.controller;

import java.util.Scanner;
import javax.print.attribute.standard.RequestingUserName;

import src.HardCodedStrings;
import src.RunGame;
import src.enumHandler;

/**
 * Processes /commands given in the chatbox
 *
 * @author mbregg
 *
 */
class CommandMiniController {

    private KeyRemapper remap_ = null;
    private Controller cont_ = null;

    public CommandMiniController(KeyRemapper remap, Controller cont) {
        remap_ = remap;
        cont_ = cont;
    }
    private static final String man = "man";
    private static final String pwd = "pwd";
    private static final String help = "help";
    private static final String controls = "controls";
    private static final String loadControls = "load-controls";
    private static final String saveControls = "save-controls";
    private static final String save = "save";
    private static final String load = "load";
    private static final String rebind = "rebind";
    private static final String bindings = "bindings";
    private static final String setIP = "set-ip";
    private static final String setControl = "set-control";
    private static final String setTCP = "set-tcp";

    private static final String commandKey = "/";

    public String processCommand(String foo) {
    	//Firstly ensure we are dealing with a command.

        if(foo == null){System.err.println("Tried to give the commandminicontroller processCommand a null string?");}
         String command = "";
         String[] inputs = new String[0];
         try {
        	 inputs = foo.split(" ");
             command = inputs[0];
             if (!command.startsWith(commandKey)) {
                 System.err.println("This isn't a command!");
                 return "Error in the CommandMini";
             }
             command = command.substring(1);//Strip the command key away now, we already checked that it exists.
         }catch(Exception e){}
         //invalid command send apparently, so nothing to read....
         

        if (command.equals(rebind)) {
            return this.processRebind(inputs);
        }
        if (command.equals(saveControls)) {
            return this.processSaveControls(inputs);
        }
        if (command.equals(loadControls)) {
            return this.processLoadControls(inputs);
        }
        if (command.equals(save)) {
            return this.processSave(inputs);
        }
        if (command.equals(load)) {
            return this.processLoad(inputs);
        }
        if (command.equals(controls)) {
            return this.processCommands();
        }
        if (command.equals(help)) {
            return this.processHelp();
        }
        if (command.equals(pwd)) {
            return System.getProperty("user.dir");
        }
        if (command.equals("cat")) {
            return "meow";
        }
        if (command.equals("tiger")) {
            return "ROAR!";
        }
        if (command.equals(man)) {
            return this.processManCommand(inputs);
        }
        if (command.equals(bindings)) {
            return this.remap_.getBindingList();
        }
        if (command.equals(setIP)) {
            int error_code = RunGame.internet.makeConnectionUsingIP_Address(foo.split(" ")[foo.split(" ").length - 1]);
            if (error_code == 0) {
                return "Successfully connected to ip address: " + foo;
            } else {
                int error_code_2 = RunGame.internet.makeConnectionUsingIP_Address("localhost");
                if (error_code_2 == 0) {
                    return "Connection failed. Reconnecting to localhost.";
                } else {
                    RunGame.setUseInternet(false);
                    return "Something is seriously wrong with the program. Cannot connect to the internet.";
                }
            }
        }
        if (command.equals(setControl)) {
            return this.setControl(inputs);
        }
        if (command.equals(setTCP)) {
            if (foo.toLowerCase().contains("n")) {
                RunGame.setUseTCP(false);
                return "TCP turned off because you said no";
            } else {
                RunGame.setUseTCP(true);
                return "TCP turned on because you didn't say no.";
            }
        }

        return "No valid command given!";
    }

    private String setControl(String[] foo) {
    	if(foo.length < 2){return HardCodedStrings.setControl_error;}


    	cont_.setControlling(foo[1]);

    	return HardCodedStrings.setControlSuccess;
    }

    private String processManCommand(String[] foo) {
        if(foo.length < 2){return HardCodedStrings.command_error;}
        String in = foo[1];
            if (in.equals(rebind)) {
                return HardCodedStrings.rebindHelp;
            }
            if (in.equals(save)) {
                return HardCodedStrings.saveHelp;
            }
            if (in.equals(load)) {
                return HardCodedStrings.loadHelp;
            }
            if (in.equals(saveControls)) {
                return HardCodedStrings.saveControlsHelp;
            }
            if (in.equals(loadControls)) {
                return HardCodedStrings.loadControlsHelp;
            }
            if (in.equals(controls)) {
                return HardCodedStrings.controlsHelp;
            }
            if (in.equals(help)) {
                return HardCodedStrings.helpHelp;
            }
            if (in.equals(pwd)) {
                return HardCodedStrings.pwdHelp;
            }
            if (in.equals(man)) {
                return HardCodedStrings.manHelp;
            }
            if (in.equals(bindings)) {
                return HardCodedStrings.bindingsHelp;
            }
            if (in.equals(setIP)) {
                return HardCodedStrings.setIPHelp;
            }
            if (in.equals(setControl)) {
                return HardCodedStrings.setControlHelp;
            }
            if (in.equals("fontsize")) {
                return HardCodedStrings.fontsizeHelp;
            }
        return HardCodedStrings.command_error;
    }

    private String processLoad(String[] foo) {
    	if(foo.length < 2){return HardCodedStrings.loadHelp;}
        cont_.loadGame(foo[1]);
        return "Loaded " + foo;
    }

    private String processLoadControls(String[] words) {
        RunGame.dbgOut("FUNC: processLoadControls()", 3);

        if (words.length >= 2) {
            cont_.loadKeys(words[1]);
            return "Load Controls: " + words[1];
        } else {
            return "You must specify a key file to load.";
        }
    }

    private String processSaveControls(String[] words) {
        RunGame.dbgOut("FUNC: processSaveControls()", 3);

        if (words.length >= 2) {
            cont_.saveKeys(words[1]);
            return "Save Controls: " + words[1];
        } else {
            cont_.saveKeys(null);
            return "Save Controls: autogenerated";
        }
    }

    private String processCommands() {
        return enumHandler.getAllCommands();
		//The output box seems a bit bugged here....
        //Scrolling horizontally horrifically garbles the text. This should be avoided.
    }

    private String processHelp() {
        return HardCodedStrings.help;
    }

    private String processRebind(String[] foo) {
        String error = HardCodedStrings.command_error + System.lineSeparator() + HardCodedStrings.rebindHelp;
        if(foo.length < 3){return error;}
        if(foo[2].length() < 1){return error;}
        if(remap_.bind(foo[2].charAt(0), enumHandler.stringCommandToKeyCommand(foo[1])) != 0){return error;}
        return "Success, Rebound : " + foo[1] + " To " + String.valueOf(foo[2].charAt(0));
    }

    private String processSave(String[] foo) {
       String saveName = "";
    	if(foo.length >= 2){saveName = foo[1];}
        cont_.saveGame(saveName);
        if (saveName != "") {
            return "Saved to " + saveName;
        }
        return "Saved to  default";
    }

}
