package src.io.controller;

import java.util.HashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

import src.IO_Bundle;
import src.Key_Commands;
import src.QueueCommandInterface;
import src.io.view.Viewport;
import src.io.view.display.Display;

/**
 * Abstract controller class
 *
 * @author mbregg
 *
 */
public abstract class Controller implements QueueCommandInterface<Character> {

    private KeyRemapper remap_;
    private Viewport currentView_;
    private String userName_;
    private ConcurrentLinkedQueue<Key_Commands> keyCommandQueue_ = new ConcurrentLinkedQueue<Key_Commands>();
    private ConcurrentLinkedQueue<Character> characterQueue_ = new ConcurrentLinkedQueue<Character>();
    private Thread controllerThread_ = Thread.currentThread();

    public void grusomelyKillTheControllerThread() {
        if (controllerThread_ == null) {
            System.err.println("Controller thread is null too soon");
            return;
        }
        if (controllerThread_.isAlive()) {
            controllerThread_.stop();
        }
    }

    public void setControlling(String in) {
        userName_ = in;
    }

    public Controller(Viewport view, KeyRemapper remap, String uName) {
        remap_ = remap;
        currentView_ = view;
        userName_ = uName;
        Display.getDisplay().addDirectCommandReceiver(new QueueCommandInterface<Key_Commands>() {

            @Override
            public void enqueue(Key_Commands command) {
                keyCommandQueue_.add(command);
            }

            @Override
            public void sendInterrupt() {
                System.out.println("Controller.sendInterrupt() @Override QueueCommandInterface<Key_Commands>() was called.");
                Controller.this.sendInterrupt();
            }
        });
        Display.getDisplay().addGameInputerHandler(this);
        Display.getDisplay().setView(currentView_);
        Display.getDisplay().printView();
    }

    protected void sleepLoop() {

        while (true) {
            if (controllerThread_ == null) {
                System.err.println("Controller thread null in sleep loop!");
            }
            //System.out.println("Entetered sleep loop");
            try {
                //if(!controllerThread_.interrupted()){//If we are interuppted, don't bother sleeping again.
                Thread.sleep(100L);
                process();
                //}
            } catch (InterruptedException e) {
                System.out.println("InterruptedInnerLoop");
                System.exit(-65);
                process();
                System.out.println("InterruptedInnerLoopEnd");
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
        System.out.println("called function Controller.updateDisplay(IO_Bundle bundle)");
        getView().renderToDisplay(bundle);
        Display.getDisplay().setView(getView());
        Display.getDisplay().printView();
    }

    @Override
    public void enqueue(Character c) {
        characterQueue_.add(c);
    }
    int count = 0;

    @Override
    public void sendInterrupt() {
        ++count;
        try {
            System.out.println("Interuppting!" + count);
            // controllerThread_.interrupt();
        } catch (Exception e) {
            System.err.println("Failed to interupt thread for input...Controller");
            e.printStackTrace();
        }
        System.out.println("Ent interrupt");

    }

    /**
     * Should be overridden to save the file with the name given, if no name
     * given, save with date.
     *
     * @param foo
     */
    public abstract void saveGame(String foo);

    /**
     * Should be overrridden to load given save file.
     *
     * @param foo
     */
    public abstract void loadGame(String foo);

}
