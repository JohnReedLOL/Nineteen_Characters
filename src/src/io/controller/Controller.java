package src.io.controller;

import src.Not_part_of_iteration_2_requirements.ControllerInternet;
import java.util.HashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

import src.*;
import src.io.view.Viewport;
import src.io.view.display.Display;
import src.model.Map;

import java.net.URL;
import javafx.application.Application;
import javafx.scene.media.AudioClip;
import javafx.stage.Stage;

/**
 * Abstract controller class
 *
 * @author mbregg
 *
 */
public abstract class Controller implements QueueCommandInterface<Character>, Runnable {

    public void regenerateCommandsBox() {
    }//Do nothing in super.
    private KeyRemapper remap_;
    private Viewport currentView_;
    private String userName_;
    //The queue for keyCommand given from the buttons. 
    private ConcurrentLinkedQueue<Key_Commands> keyCommandQueue_ = new ConcurrentLinkedQueue<Key_Commands>();
    //The queue of keyboard input in the main game. 
    private ConcurrentLinkedQueue<Character> characterQueue_ = new ConcurrentLinkedQueue<Character>();
    // private Thread controllerThread_ = Thread.currentThread(); Bad because constructor could be called by another thread.
    private final ControllerInternet internet = new ControllerInternet(this);

    private Map map_;

    protected Map getMap() {
        return map_;
    }

    public void grusomelyKillTheControllerThread() {
        if (Thread.currentThread() == null) {
            System.err.println("Controller thread is null too soon");
            return;
        }
        Thread.currentThread().interrupt();
    }

    public void setControlling(String in) {
        userName_ = in;
    }

    public Controller(Map map, Viewport view, KeyRemapper remap, String uName) {
        map_ = map;
        remap_ = remap;
        currentView_ = view;
        userName_ = uName;
        Display.getDisplay().addDirectCommandReceiver(new QueueCommandInterface<Key_Commands>() {

            @Override
            public void enqueue(Key_Commands command) {
                keyCommandQueue_.add(command);
            }

        });
        Display.getDisplay().addGameInputerHandler(this);
        Display.getDisplay().setView(currentView_);
        Display.getDisplay().printView();
    }

    @Override
    public void run() {
        sleepLoop();
    }

    protected void sleepLoop() {

        while (!Thread.currentThread().isInterrupted()) {
            //System.out.println("Entetered sleep loop");
            try {
                //if(!controllerThread_.interrupted()){//If we are interuppted, don't bother sleeping again.
                Thread.sleep(100L);
                process();
                //}
            } catch (InterruptedException e) {
                break;
            }
            //System.out.println("Exited sleep loop"); //Exited
        }
    }

    /**
     *
     * @return true if something was done and false if nothing was done.
     */
    protected boolean process() {
        //System.out.println("Processing in Controller Superclass");
        if (keyCommandQueue_.isEmpty() && characterQueue_.isEmpty()) {
            return false;
        }
        while (!keyCommandQueue_.isEmpty()) {
            Key_Commands c = keyCommandQueue_.remove();
            if (c != null) {
                takeTurnandPrintTurn(c);
            }
        }
        while (!characterQueue_.isEmpty()) {
            Character c = characterQueue_.remove();
            if (c != null) {
                takeTurnandPrintTurn(c);
            }
        }
        return true;
    }

    protected Viewport getView() {
        return currentView_;
    }

    protected void setView(Viewport view) {
        currentView_ = view;
    }

    protected void takeTurnandPrintTurn(char foo) {
        Key_Commands input = getRemapper().mapInput(foo);
        takeTurnandPrintTurn(input);
    }

    protected abstract void takeTurnandPrintTurn(Key_Commands foo);

    public String getUserName() {
        return userName_;
    }

    protected KeyRemapper getRemapper() {
        return remap_;
    }

    /**
     * Gets the underlying key remapping values
     *
     * @return A HashMap with the remapped key values in it
     * @author Alex Stewart
     */
    public HashMap<Character, Key_Commands> getRemap() {
        if (remap_ == null) {
            return null;
        }
        return remap_.getMap();
    }

    /**
     * Sets the underlying key remapping
     *
     * @param remap The new key remapping to be applied
     * @author Alex Stewart
     */
    public void setRemap(HashMap<Character, Key_Commands> remap) {
        if (remap_ == null) {
            remap_ = new GameRemapper();
        }
        remap_.setMap(remap);
    }

    /**
     * Takes the given iobundle and updates display with it's content
     *
     * @param bundle
     */
    public void updateDisplay(IO_Bundle bundle) {
        //System.out.println("called function Controller.updateDisplay(IO_Bundle bundle)");
        getView().renderToDisplay(bundle);
        Display.getDisplay().setView(getView());
        Display.getDisplay().printView();
    }

    @Override
    public void enqueue(Character c) {
        characterQueue_.add(c);
    }

    /**
     * Should be overridden to save the file with the name given, if no name
     * given, save with date.
     *
     * @param foo
     */
    public abstract void saveGame(String foo);

    public void saveKeys(String filepath) {
        SavedGame.saveKeymap(filepath, remap_.getMap());
    }

    /**
     * Should be overrridden to load given save file.
     *
     * @param foo
     */
    public abstract void loadGame(String foo);

    /**
     * -1 if null hashmap received
     *
     * @param filepath
     * @return
     */
    public int loadKeys(String filepath) {
        HashMap<Character, Key_Commands> newmap = SavedGame.loadKeymap(filepath);
        if (newmap != null) {
            remap_.setMap(newmap);
            return 0;
        } else {
            return -1;
        }
    }

    /**
     * This class is used as a replacement for method references pre-Java-8
     */
    protected abstract class Functor {

        abstract public IO_Bundle sendCommandToMap(Key_Commands command, String input);
    }

    private final sendCommandToMapViaLocalReferance sendCommandViaLocalReferance_Functor_ = new sendCommandToMapViaLocalReferance();

    /**
     * Private functor [method reference] used to send commands to the map using
     * a local reference.
     *
     * @author John-Michael Reed
     */
    private class sendCommandToMapViaLocalReferance extends Functor {

        /**
         * @author John-Michael Reed
         * @param command - key_command that map interprets and executes.
         * @param input - Optional [if non-empty String] parameter used for
         * sending chat communication to the map.
         * @return reply from map
         */
        @Override
        public IO_Bundle sendCommandToMap(Key_Commands command, String input) {
            return map_.sendCommandToMapWithOptionalText(getUserName(), command,
                    getView().getWidth() / 2, getView().getHeight() / 2, input);
        }
    }
    private final sendCommandToMapViaNetwork sendCommandViaNetwork_Functor_ = new sendCommandToMapViaNetwork();

    private Functor message_deliverer_ = sendCommandViaNetwork_Functor_;
    //private Functor message_deliverer_ = sendCommandViaLocalReferance_Functor_;

    /**
     * Returns the functor responsible for relaying signals to maps.
     *
     * @return
     */
    protected Functor getMessenger() {
        return message_deliverer_;
    }

    /**
     *
     * @param ip
     * @return -1 if ip is invalid, 0 if ip is valid.
     */
    public int setNetworkIPTo(String ip) {
        int error_code = internet.makeConnectionUsingIP_Address(ip);
        return error_code;
    }

    public boolean isUsingInternet() {
        if (message_deliverer_ == sendCommandViaNetwork_Functor_) {
            return true;
        } else if (message_deliverer_ == sendCommandViaLocalReferance_Functor_) {
            return false;
        } else {
            System.err.println("Impossible error in Controller.is_using_internet()");
            System.exit(65);
            return false;
        }
    }

    public void tellToUseNetwork() {
        message_deliverer_ = sendCommandViaNetwork_Functor_;
    }

    public void tellNotToUseNetwork() {
        message_deliverer_ = sendCommandViaLocalReferance_Functor_;
    }

    /**
     * Private functor [method reference] used to send commands to the map using
     * UDP.
     *
     * @author John-Michael Reed
     */
    private class sendCommandToMapViaNetwork extends Functor {

        /**
         * @author John-Michael Reed
         * @param command - key_command that map interprets and executes.
         * @param input - Optional [if non-empty String] parameter used for
         * sending chat communication to the map.
         * @return reply from map
         */
        @Override
        public IO_Bundle sendCommandToMap(Key_Commands command, String input) {
            return internet.sendStuffToMap(getUserName(), command,
                    getView().getWidth() / 2, getView().getHeight() / 2, input);
        }
    }
}
