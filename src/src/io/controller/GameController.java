/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package src.io.controller;

import java.net.DatagramPacket;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentLinkedQueue;

import javax.swing.SwingUtilities;

import java.util.Queue;
import javafx.application.Application;
import javafx.scene.media.AudioClip;
import javafx.stage.Stage;

import src.HardCodedStrings;
import src.IO_Bundle;
import src.Key_Commands;
import src.Music;
import src.QueueCommandInterface;
import src.RunGame;
import src.enumHandler;
import src.io.view.AvatarCreationView;
import src.io.view.ChatBoxViewPort;
import src.io.view.MapView;
import src.io.view.StatsView;
import src.io.view.Viewport;
import src.io.view.display.Display;
import src.model.Map;
import src.model.MapUser_Interface;

/**
 * Uses keyboard input to control the avatar Handles the main game mode
 *
 * @author JohnReedLOL/mbregg
 */
public class GameController extends Controller {

    //Queue of the strings from clicking on the command box inthe gui.
    private ConcurrentLinkedQueue<String> stringQueue_ = new ConcurrentLinkedQueue<String>();

    private final Music music = new Music();
    private final Thread musicThread = new Thread(music);

    @Override()
    public void run() {
        musicThread.start();
        super.run();
    }

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
            Key_Commands command = Key_Commands.GET_CONVERSATION_OPTIONS;
            if (foo.contains(HardCodedStrings.attack)) {
                command = Key_Commands.ATTACK;
                updateDisplay(sendCommandToMapWithText(command, foo));
                return null;
            }
            if (foo.contains(HardCodedStrings.getChatOptions)) {
                command = Key_Commands.GET_CONVERSATION_STARTERS;
                updateDisplay(sendCommandToMapWithText(command, foo));
                return null;
            }
            updateDisplay(sendCommandToMapWithText(command, foo));
            return null;
        }

        public void chatBoxHandleMapInputAndPrintNewContents(ArrayList<String> strings_for_communication, boolean is_alive) {
            //System.out.println("Calling GameController.chatBoxHandleMapInputAndPrintNewContents("
            //        + "ArrayList<String> strings_for_communication, boolean is_alive)");
            chatview_.renderToDisplay(strings_for_communication, is_alive);
            ArrayList<String> list = chatview_.getContents();
            for (String i : list) {
                Display.getDisplay().setMessage(i);
            }
        }
        //The queue for commands given into the input box.
        private ConcurrentLinkedQueue<String> commandQueue_ = new ConcurrentLinkedQueue<String>();
        //The queue for when you hit a character in the output box.
        private ConcurrentLinkedQueue<Character> commandChoiceQueue_ = new ConcurrentLinkedQueue<Character>();

        private class outputBoxFunction implements QueueCommandInterface<Character> {

            @Override
            public void enqueue(Character command) {
                commandChoiceQueue_.add(command);

            }

        }

        @Override
        public void enqueue(String command) {
            commandQueue_.add(command);
        }

        /**
         * Process the input that has built up in the two queues.
         *
         * @return true if there are commands on Queue false if there are none
         */
        public boolean processQueue() {
            if (commandQueue_.isEmpty() && commandChoiceQueue_.isEmpty()) {
                return false;
            }
            while (!commandQueue_.isEmpty()) {
                String foo = commandQueue_.remove();
                if (foo != null && foo.startsWith("/")) {
                    processCommandAndDisplayOutput(foo);
                }
                //IF it starts with a /, it's a command, so send it
                //To the command function, not the map.
                if (foo != null) {
                    sendTextCommandAndUpdate(foo);
                }
            }
            while (!commandChoiceQueue_.isEmpty()) {
                Character c = commandChoiceQueue_.remove();
                sendTextCommandAndUpdate(chatview_.getChoice(Character.getNumericValue(c)));
            }
            return true;
        }

    }

    private String getListOfCommands() {
        StringBuilder commands = new StringBuilder();
        ArrayList<String> commandsList = new ArrayList<String>(this.getRemap().entrySet().size());
        for (Entry<Character, Key_Commands> i : this.getRemap().entrySet()) {
            commandsList.add(i.getValue().toString() + "  :  " + i.getKey() + System.lineSeparator());
        }
        Collections.sort(commandsList);
        for (String i : commandsList) {
            commands.append(i);
        }

        return commands.toString();

    }

    @Override
    public void regenerateCommandsBox() {
        Display.getDisplay().setCommandList(getListOfCommands());
    }

    public void GameController_Constructor_Helper(MapUser_Interface mui, String uName) {
        MapUserAble_ = mui;
        Display.getDisplay().setCommandList(getListOfCommands());
        Display.getDisplay().addDoubleClickCommandEventReceiver(new QueueCommandInterface<String>() {

            @Override
            public void enqueue(String command) {
                if (command != null) {
                    stringQueue_.add(command);
                }

            }

        });
        views_.add(new MapView());
        views_.add(new StatsView(getUserName()));//Build our two views.
        takeTurnandPrintTurn('5');//For some reason need to take a empty turn for fonts to load...
    }

    public GameController(Map map, String uName, GameRemapper remap) {
        super(map, new AvatarCreationView(), remap, uName);
        Application.launch(); // launches the sound effects application
        GameController_Constructor_Helper(super.getMap(), uName);
    }

    public GameController(Map map, String uName) {
        super(map, new AvatarCreationView(), new GameRemapper(), uName);
        GameController_Constructor_Helper(super.getMap(), uName);
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
        //System.out.println("called function GameController.updateDisplay(IO_Bundle bundle)");
        // ** chatbox should not be getting the whole bundle ** //
        if (bundle != null) {
            chatbox_.chatBoxHandleMapInputAndPrintNewContents(
                    bundle.strings_for_communication_, bundle.is_alive_);
            super.updateDisplay(bundle);
        }

    }

    private void handleSoundEffect(Key_Commands command, IO_Bundle to_return) {
        if (command == Key_Commands.ATTACK) {
            music.playAttackSound();
        } else if (command == Key_Commands.BIND_WOUNDS) {
            music.playBindSound();
        } else if (command == Key_Commands.GET_INTERACTION_OPTIONS) {
            music.playTalkingSound();
        } else if (command == Key_Commands.USE_SKILL_1) {
            music.playSpellSound();
        } else if (command == Key_Commands.USE_SKILL_2) {
            music.playSpellSound();
        } else if (command == Key_Commands.USE_SKILL_3) {
            music.playSpellSound();
        } else if (command == Key_Commands.USE_SKILL_4) {
            music.playSpellSound();
        }
        // makes a cash register sound if your money goes down.

        if (last_num_coins > 0) {
            if (to_return.num_coins_ < last_num_coins) {
                // don't make the cash register sound if you lose a life
                if (to_return.stats_for_display_ != null && to_return.stats_for_display_.getLives_left_() >= last_lives_left) {
                    music.playBuyingSound();
                }
            }
        }
        last_num_coins = to_return.num_coins_;

        if (to_return.inventory_ != null) {
            if (last_inventory_size >= 0) {
                if (to_return.inventory_.size() < last_inventory_size) {
                    music.playDropItemSound();
                } else if (to_return.inventory_.size() > last_inventory_size) {
                    music.playPickupItemSound();
                }
            }
            last_inventory_size = to_return.inventory_.size();
        }

        last_num_coins = to_return.num_coins_;
        if (to_return.stats_for_display_ != null) {
            int lives = to_return.stats_for_display_.getLives_left_();
            if (last_lives_left > 0) {
                if (lives < last_lives_left) {
                    music.playDyingSound();
                }
            }
            last_lives_left = lives;
        }
    }
    private int last_inventory_size = -10;
    private int last_lives_left = -10;
    private int last_num_coins = -10;

    private IO_Bundle sendCommandToMapWithText(Key_Commands command, String input) {
        if (command == null) {
            return null;
        }
        final IO_Bundle to_return = super.getMessenger().sendCommandToMap(command, input);
        if (to_return == null) {
            System.out.println("IO_Bundle to return is " + to_return + ". " + RunGame.getStackTrace());
            return null;
        }
        // Sound effects!!!!
        this.handleSoundEffect(last_key, to_return);

        if (to_return != null && last_key == Key_Commands.OBSERVE) {
            if (to_return.observation_string_ == null) {
                System.err.println("The observation string is not allowed to be null");
                System.exit(4);
            }
            java.awt.EventQueue.invokeLater(new Runnable() {
                public void run() {
                    Display.getDisplay().setMessage(to_return.observation_string_);
                }
            });
        }

        if (to_return != null && to_return.strings_for_communication_ != null
                && !to_return.strings_for_communication_.isEmpty() && Key_Commands.GET_INTERACTION_OPTIONS.equals(last_key)) {
            java.awt.EventQueue.invokeLater(new Runnable() {
                public void run() {
                    Display.getDisplay().requestOutBoxFocus();
                }
            });
        }

        // Make the buttons says the right skill names.
        if (to_return != null && to_return.occupation_ != null && last_key == Key_Commands.BECOME_SMASHER || last_key == Key_Commands.BECOME_SUMMONER
                || last_key == Key_Commands.BECOME_SNEAK || last_key == Key_Commands.SWAP_SUB_OCCUPATION) {
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
        last_key = command;
        return to_return;
    }
    private Key_Commands last_key = Key_Commands.DO_ABSOLUTELY_NOTHING;
    private Queue<Viewport> views_ = new LinkedList<Viewport>();

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
        //System.out.println("Called GameController.updateViewsAndMap(Key_Commands input)");
        boolean taken = false;

        if (Key_Commands.BECOME_SNEAK.equals(input) || Key_Commands.BECOME_SMASHER.equals(input)
                || Key_Commands.BECOME_SUMMONER.equals(input)) {
            setView(views_.element());
            System.gc();

        }
        if (Key_Commands.TOGGLE_VIEW.equals(input)) {
            views_.add(views_.remove());//Pop the last element and bring it front.
            setView(views_.element());
            System.gc();
            taken = true;
        }

        if (!taken) {
            return sendCommandToMapWithText(input, "");
        } else {
            return sendCommandToMapWithText(Key_Commands.DO_ABSOLUTELY_NOTHING, "");
        }

    }

    @Override
    protected void takeTurnandPrintTurn(Key_Commands input) {
        //System.out.println("calling GameController.takeTurnandPrintTurn(Key_Commands input)");
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

    /**
     * Do nothing if queues are empty.
     *
     * @return
     */
    @Override
    public boolean process() {
        boolean did_something_super = super.process();

        //System.out.println("Processing in GameController subclass");
        boolean did_something_chatbox = chatbox_.processQueue();
        if (!did_something_super && !did_something_chatbox && stringQueue_.isEmpty()) {
            takeTurnandPrintTurn(Key_Commands.DO_ABSOLUTELY_NOTHING);
            return false;
        }
        while (!stringQueue_.isEmpty()) {
            String foo = stringQueue_.remove();
            if (foo == null) {
                break;
            }
            Key_Commands command = enumHandler.stringCommandToKeyCommand(foo);
            if (command == null) {
                break;
            }
            takeTurnandPrintTurn(command);

        }
        return true;
    }
}
