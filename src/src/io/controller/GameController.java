/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package src.io.controller;

import java.net.DatagramPacket;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentLinkedQueue;

import javax.swing.SwingUtilities;

import src.HardCodedStrings;
import src.IO_Bundle;
import src.Internet;
import src.Key_Commands;
import src.QueueCommandInterface;
import src.RunGame;
import src.enumHandler;
import src.io.view.AvatarCreationView;
import src.io.view.ChatBoxViewPort;
import src.io.view.MapView;
import src.io.view.StatsView;
import src.io.view.display.Display;
import src.model.MapUser_Interface;

/**
 * Uses keyboard input to control the avatar Handles the main game mode
 *
 * @author JohnReedLOL/mbregg
 */
public class GameController extends Controller {

    private String stringQueue_ = null;

    private final class ChatBoxMiniController implements QueueCommandInterface<String> {

        private CommandMiniController commandController_ = new CommandMiniController(getRemapper(), GameController.this);
        private ChatBoxViewPort chatview_ = new ChatBoxViewPort();

        public ChatBoxMiniController() {
            Display.getDisplay().addInputBoxTextEnteredFunction(this);
            Display.getDisplay().addOutputBoxCharacterFunction(new outputBoxFunction());
        }

        /**
         * Processes a command entered into the chatbox. Commands begin with a /
         * Prints the result of running that command to the chatbox.
         *
         * @param foo
         */
        private void processCommandAndDisplayOutput(String foo) {
            Display.getDisplay().setMessage(commandController_.processCommand(foo));
        }

        private Void sendTextCommandAndUpdate(String foo) {
            Key_Commands command = Key_Commands.GET_CONVERSATION_CONTINUATION_OPTIONS;
            if (foo.contains(HardCodedStrings.attack)) {
                command = Key_Commands.ATTACK;
                updateDisplay(sendCommandToMapWithText(command, ""));
                return null;
            }
            if (foo.contains(HardCodedStrings.getChatOptions)) {
                command = Key_Commands.GET_CONVERSATION_STARTERS;
                updateDisplay(sendCommandToMapWithText(command, ""));
                return null;
            }
            updateDisplay(sendCommandToMapWithText(command, foo));
            return null;
        }

        public void chatBoxHandleMapInputAndPrintNewContents(IO_Bundle bundle) {
            chatview_.renderToDisplay(bundle);
            ArrayList<String> list = chatview_.getContents();
            for (String i : list) {
                Display.getDisplay().setMessage(i);
            }
        }
        //private ConcurrentLinkedQueue<String> commandQueue_ = new ConcurrentLinkedQueue<String>();
       // private ConcurrentLinkedQueue<Character> commandChoiceQueue_ = new ConcurrentLinkedQueue<Character>();
        private Character commandChoiceQueue_ = null;
        private String commandQueue_ = null;

        private class outputBoxFunction implements QueueCommandInterface<Character> {

            @Override
            public void enqueue(Character command) {
                commandChoiceQueue_ =(command);

            }

            @Override
            public void sendInterrupt() {
                GameController.this.sendInterrupt();

            }

        }


        @Override
        public void enqueue(String command) {
            commandQueue_ =(command);
        }

        @Override
        public void sendInterrupt() {
            GameController.this.sendInterrupt();

        }

        /**
         * Process the input that has built up in the two queues.
         */
        public void processQueue() {
            while (!commandQueue_.isEmpty()) {
                String foo = commandQueue_;
                if (foo!= null && foo.startsWith("/")) {
                    processCommandAndDisplayOutput(foo);
                }
				//IF it starts with a /, it's a command, so send it
                //To the command function, not the map.
                if(foo!=null){
                	sendTextCommandAndUpdate(foo);
                }
            }
            	Character c = commandChoiceQueue_;
                sendTextCommandAndUpdate(chatview_.getChoice(Character.getNumericValue(c)));
                commandChoiceQueue_ = null;
        }

    }

    public GameController(MapUser_Interface mui, String uName) {
        super(new AvatarCreationView(), new GameRemapper(), uName);
        MapUserAble_ = mui;
        Display.getDisplay().setCommandList(HardCodedStrings.gameCommands);
        Display.getDisplay().addDoubleClickCommandEventReceiver(new QueueCommandInterface<String>() {

            @Override
            public void enqueue(String command) {
                stringQueue_ =(command);

            }

            @Override
            public void sendInterrupt() {
                GameController.this.sendInterrupt();
            }

        });
        takeTurnandPrintTurn('5');//For some reason need to take a empty turn for fonts to load...
        sleepLoop();
    }

    private MapUser_Interface MapUserAble_;

    private ChatBoxMiniController chatbox_ = new ChatBoxMiniController();

    /**
     * Takes in a bundle, and updates and then prints the dispaly with it.
     *
     * @param bundle
     */
    @Override
    public void updateDisplay(IO_Bundle bundle) {
        chatbox_.chatBoxHandleMapInputAndPrintNewContents(bundle);
        super.updateDisplay(bundle);
    }

    private static DatagramPacket packet = null;

    private IO_Bundle sendCommandToMapWithText(Key_Commands command, String in) {
        if (SwingUtilities.isEventDispatchThread()) {
            System.err.println("GameController is running on the Swing Dispatch Thread [Bad]");
        } else {
            System.out.println("GameController is not running on the Swing Dispatch Thread [Good]");
        }
        if (command == Key_Commands.GET_INTERACTION_OPTIONS) {
            java.awt.EventQueue.invokeLater(new Runnable() {
                public void run() {
                    Display.getDisplay().requestOutBoxFocus();
                }
            });
        }
        final IO_Bundle to_return;
        if (RunGame.getUseInternet()) {
            to_return = Internet.sendStuffToMap(getUserName(),
                    command, getView().getWidth() / 2, getView().getHeight() / 2, in);
            System.out.println("Using internet");
        } else {
            to_return = MapUserAble_.sendCommandToMapWithOptionalText(getUserName(), command, getView().getWidth() / 2, getView().getHeight() / 2, "");
            System.out.println("Not using internet");
        }
        // Make the buttons says the right skill names.
        if (to_return != null && to_return.occupation_ != null && command == Key_Commands.BECOME_SMASHER || command == Key_Commands.BECOME_SUMMONER
                || command == Key_Commands.BECOME_SNEAK) {
            java.awt.EventQueue.invokeLater(new Runnable() {
                public void run() {
                    Display.getDisplay().getSkillButton(1).
                            setText(to_return.occupation_.getSkillNameFromNumber(1));
                    Display.getDisplay().getSkillButton(2).
                            setText(to_return.occupation_.getSkillNameFromNumber(2));
                    Display.getDisplay().getSkillButton(3).
                            setText(to_return.occupation_.getSkillNameFromNumber(3));
                    Display.getDisplay().getSkillButton(4).
                            setText(to_return.occupation_.getSkillNameFromNumber(4));
                }
            });
        }
        // Auto focus on chatbox
        if ((to_return != null && to_return.strings_for_communication_ != null && !to_return.strings_for_communication_.isEmpty())
                && (command == Key_Commands.MOVE_DOWN || command == Key_Commands.MOVE_DOWNLEFT
                || command == Key_Commands.MOVE_DOWNRIGHT || command == Key_Commands.MOVE_LEFT
                || command == Key_Commands.MOVE_RIGHT || command == Key_Commands.MOVE_UP
                || command == Key_Commands.MOVE_UPLEFT || command == Key_Commands.MOVE_UPRIGHT
                || command == Key_Commands.GET_INTERACTION_OPTIONS)) {
            java.awt.EventQueue.invokeLater(new Runnable() {
                public void run() {
                    Display.getDisplay().requestOutBoxFocus();
                }
            });
        }
        return to_return;
    }

    /**
     * Sends the command and string to the map.
     *
     * @param command
     * @param in
     * @return
     */
    //Handles the view switching, uses the  instance of operator in a slightly evil way, 
    //ideally we should look into refactoring this to nots
    protected IO_Bundle updateViewsAndMap(Key_Commands input) {
        boolean taken = false;
        if (getView() instanceof AvatarCreationView) {
            if (Key_Commands.BECOME_SNEAK.equals(input) || Key_Commands.BECOME_SMASHER.equals(input)
                    || Key_Commands.BECOME_SUMMONER.equals(input)) {
                setView(new MapView());
                System.gc();
            }
        }
        if (getView() instanceof MapView) {
            if (Key_Commands.TOGGLE_VIEW.equals(input)) {
                setView(new StatsView(getUserName()));
                System.gc();
                taken = true;
            }
        } else if (getView() instanceof StatsView) {
            if (Key_Commands.TOGGLE_VIEW.equals(input)) {
                setView(new MapView());
                System.gc();
                taken = true;
            }
        }
        if (!taken) {
            return sendCommandToMapWithText(input, "");
        } else {
            return sendCommandToMapWithText(Key_Commands.DO_ABSOLUTELY_NOTHING, "");
        }

    }

    @Override
    protected void takeTurnandPrintTurn(Key_Commands input) {
        IO_Bundle bundle = updateViewsAndMap(input);
        updateDisplay(bundle);
    }

    @Override
    public void saveGame(String foo) {
        MapUserAble_.saveGame(foo);

    }

    @Override
    public void loadGame(String foo) {
        MapUserAble_.loadGame(foo);

    }

    @Override
    public void process() {
        //System.out.println("Processing");
        super.process();
        chatbox_.processQueue();
        while (!stringQueue_.isEmpty()) {
            String foo = stringQueue_;
            stringQueue_ = null;
            if (foo == null) {
                return;
            }
            Key_Commands command = enumHandler.stringCommandToKeyCommand(foo);
            if (command == null) {
                return;
            }
            takeTurnandPrintTurn(command);

        }
    }
    // FIELD ACCESSORS
    /**
     * Gets this UserController's user name value
     * <p>
     * Used for saving. Loading is done through the constructor</p>
     *
     * @return A String object with this UserController's user name
     * @author Alex Stewart
     */
}
