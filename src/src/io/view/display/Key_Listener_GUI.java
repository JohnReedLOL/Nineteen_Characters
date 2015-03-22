package src.io.view.display;

import java.awt.EventQueue;
import java.awt.Font;
import java.awt.event.KeyEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.event.WindowStateListener;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentLinkedQueue;

import javax.swing.JComponent;
import javax.swing.text.StyledDocument;

import src.Key_Commands;
import src.QueueCommandInterface;
import src.RunGame;

/**
 * Computer generated code made with Netbeans GUI builder using instruction
 * from: https://netbeans.org/kb/docs/java/gui-functionality.html
 *
 * @author Matthew B [human-written code]
 */
class Key_Listener_GUI extends javax.swing.JFrame implements WindowListener {


    public javax.swing.JTextArea getIncomingText() {
        return incoming_text_jTextArea;
    }

    /**
     * Returns the skill button of index i. Should i not be a valid skill
     * button, returns null.
     *
     * @param i
     * @return
     */
    public javax.swing.JButton getSkillButton(int i) {
        switch (i) {
            case 1:
                return occupation_skill_1_jButton;
            case 2:
                return occupation_skill_2_jButton;
            case 3:
                return occupation_skill_3_jButton;
            case 4:
                return occupation_skill_4_jButton;
            default:
                return null;
        }
    }

    /**
     * The number of skill buttons.
     *
     * @return
     */
    public int getSkillButtonCount() {
        return 4;
    }

    /**
     *
     */
    private static final long serialVersionUID = 1L;
    //These concurrent linked queues holds the things that called when an event happens.
    private ConcurrentLinkedQueue<QueueCommandInterface<Character>> game_inputHandlers_ = new ConcurrentLinkedQueue<QueueCommandInterface<Character>>();
    private ConcurrentLinkedQueue<QueueCommandInterface<Character>> outputbox_inputHandlers_ = new ConcurrentLinkedQueue<QueueCommandInterface<Character>>();
    private ConcurrentLinkedQueue<QueueCommandInterface<String>> inputchatbox_Handlers_ = new ConcurrentLinkedQueue<QueueCommandInterface<String>>();
    private ConcurrentLinkedQueue<QueueCommandInterface<Key_Commands>> direct_command_receivers_ = new ConcurrentLinkedQueue<QueueCommandInterface<Key_Commands>>();
    private ConcurrentLinkedQueue<QueueCommandInterface<String>> command_area_double_clicked_ = new ConcurrentLinkedQueue<QueueCommandInterface<String>>();

    /**
     *
     * @param in What to write to the equipped box
     */
    public void takeInEquipped(String in) {
        equipment_jTextArea.setText(in);
    }

    /**
     * Adds something for the buttons to call with a direct command when
     * pressed.
     *
     * @param receiver
     */
    public void addDirectCommandReceiver(QueueCommandInterface<Key_Commands> receiver) {
        direct_command_receivers_.add(receiver);
    }

    /**
     * Adds a event to be triggered when chars typed in out box.
     *
     * @param handler_
     */
    public void addoutputBoxReceiver(QueueCommandInterface<Character> handler_) {
        outputbox_inputHandlers_.add(handler_);

    }

    /**
     * Adds an handler for when the input box receives a string.
     *
     * @param handler_
     */
    public void addInputBoxReceiver(QueueCommandInterface<String> handler_) {
        inputchatbox_Handlers_.add(handler_);

    }

    public void addCommandBoxReceiver(QueueCommandInterface<String> handler_) {
        command_area_double_clicked_.add(handler_);
    }

    /**
     *
     * @param in What to write to the inventory box.
     */
    public void takeInInventory(String in) {
        inventory_jTextArea.setText(in);
    }

    /**
     * Puts a string in the output box
     *
     * @param message The string to display in a new line
     */
    public void addMessage(String message) {
        incoming_text_jTextArea.append(System.lineSeparator() + message);
        updateScroll();
    }

    public void setCommands(String commands) {
        commands_jTextArea.setText(commands);
    }

    /**
     * Sends the scroll bar to the buttom, used when new text is added.
     */
    private void updateScroll() {
        incoming_text_jTextArea.setCaretPosition(incoming_text_jTextArea.getText().length());
    }

    /**
     * Sets the given styled doc to be displayed in the main view.
     * This must be called in a Runnable to be executed by the GUI thread
     * @param doc
     */
    public void setGameContent(StyledDocument doc) {
        game_jTextPane.setStyledDocument(doc);
    }
    private static Key_Listener_GUI gui_ = null;//Singleton variable.

    /**
     * Singleton constructor
     */
    private Key_Listener_GUI() {
        initComponents();
        setFont();
        bargain_barter_jButton.setText("Talk / Bargain");
        occupation_skill_1_jButton.setText("Reassign Me");
        occupation_skill_1_jButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {

                if (EventQueue.isDispatchThread()) {
                    System.out.println("IN KEY_LISTENER: YOU ARE EXECUTING SHIT ON THE EVENT DISPATCH THREADDD!!!!!!!!!!!!!");
                } else {
                    System.out.println("IN KEY_LISTENER: YOU ARE NOT EXECUTING SHIT ON THE EVENT DISPATCH THREADDD!!!!!!!!!!!!!");
                }

                occupation_skill_1_jButtonMouseClicked(evt);
            }
        });
        occupation_skill_2_jButton.setText("Reassign Me");
        occupation_skill_2_jButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                occupation_skill_2_jButtonMouseClicked(evt);
            }
        });
        occupation_skill_3_jButton.setText("Reassign Me");
        occupation_skill_3_jButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                occupation_skill_3_jButtonMouseClicked(evt);
            }
        });
        occupation_skill_4_jButton.setText("Reassign Me");
        occupation_skill_4_jButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                occupation_skill_4_jButtonMouseClicked(evt);
            }
        });
        addWindowListener(this);
    }

    @Override
    public void windowClosing(WindowEvent e) {
        // close all threads before closing.
        RunGame.grusomelyKillTheMapAndTheController();
    }

    @Override
    public void windowClosed(WindowEvent e) {
        //This will only be seen on standard output.
        // close all threads before closing.
        RunGame.grusomelyKillTheMapAndTheController();
    }

    @Override
    public void windowOpened(WindowEvent e) {
    }

    @Override
    public void windowIconified(WindowEvent e) {
    }

    @Override
    public void windowDeiconified(WindowEvent e) {
    }

    @Override
    public void windowActivated(WindowEvent e) {
    }

    @Override
    public void windowDeactivated(WindowEvent e) {
    }

    /**
     * Sets the font of the 3 main components. Uses the fontSize_ variable.
     */
    private void setFont() {
        setFont(game_jTextPane);
        setFont(incoming_text_jTextArea);
        setFont(outgoing_chat_text_area_jScrollPane);
        setFont(incoming_text_jTextArea);
        setFont(equip_text_area_jScrollPane);
        setFont(inventory_jTextArea);
    }

    public String getHighlightedItem() {
        String highlighted = inventory_jTextArea.getSelectedText();
        if (highlighted != null) {
            return highlighted;
        }
        highlighted = equipment_jTextArea.getSelectedText();
        if (highlighted != null) {
            return highlighted;
        }
        highlighted = commands_jTextArea.getSelectedText();
        return highlighted;
    }

    /**
     * Returns the singleton instance.
     *
     * @return The singleton Key_Listener_GUI
     */
    public static Key_Listener_GUI getGUI() {
        if (gui_ == null) {
            gui_ = new Key_Listener_GUI();
        }
        return gui_;

    }

    /**
     * Adds a class to be called via the function interface whenever a character
     * is typed in the main gameview.
     *
     * @param foo : The class to call
     */
    public void addGameInputerHandler(QueueCommandInterface<Character> foo) {
        game_inputHandlers_.add((foo));
    }

    /**
     * Adds a class to be called via the function interface whenever a new line
     * is typed in the input box.
     *
     * @param foo : the class to call
     */
    public void addChatboxInputerHandler(QueueCommandInterface<String> foo) {
        inputchatbox_Handlers_.add((foo));
    }
    private float fontSize_ = 14f;//The font size

    /**
     * Loads the font from file, if it can find it.
     *
     * @return
     */
    private Font loadFont() {
        InputStream in = this.getClass().getResourceAsStream("Font/DejaVuSansMono.ttf");
        try {
            return Font.createFont(Font.TRUETYPE_FONT, in);
        } catch (Exception e) {
            System.err.println(e.toString());
            return null;
        }
    }

    /**
     * Taking into account fontSize_, sets the font of the given component.
     *
     * @param object
     */
    private void setFont(JComponent object) {
        Font font = loadFont();
        if (font == null) {
            return;
        }//If we failed to load the font, do nothing
        Font resized = font.deriveFont(fontSize_);//This line sets the size of the game, not sure how to make it dynamic atm
        object.setFont(resized);
        return;
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        controls_inventory_and_equipment_jTabbedPane = new javax.swing.JTabbedPane();
        jScrollPane2 = new javax.swing.JScrollPane();
        commands_jTextArea = new javax.swing.JTextArea();
        inventory_text_area_jScrollPane = new javax.swing.JScrollPane();
        inventory_jTextArea = new javax.swing.JTextArea();
        equip_text_area_jScrollPane = new javax.swing.JScrollPane();
        equipment_jTextArea = new javax.swing.JTextArea();
        outgoing_text_jTextField = new javax.swing.JTextField();
        outgoing_chat_text_area_jScrollPane = new javax.swing.JScrollPane();
        incoming_text_jTextArea = new javax.swing.JTextArea();
        regular_and_special_skills_jTabbedPane = new javax.swing.JTabbedPane();
        regular_skills_jPanel = new javax.swing.JPanel();
        bind_wounds_jButton = new javax.swing.JButton();
        observe_jButton = new javax.swing.JButton();
        bargain_barter_jButton = new javax.swing.JButton();
        special_skills_jPanel = new javax.swing.JPanel();
        occupation_skill_2_jButton = new javax.swing.JButton();
        occupation_skill_1_jButton = new javax.swing.JButton();
        occupation_skill_3_jButton = new javax.swing.JButton();
        occupation_skill_4_jButton = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        game_jTextPane = new javax.swing.JTextPane();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        commands_jTextArea.setColumns(20);
        commands_jTextArea.setRows(5);
        jScrollPane2.setViewportView(commands_jTextArea);

        controls_inventory_and_equipment_jTabbedPane.addTab("Controls", jScrollPane2);

        inventory_jTextArea.setEditable(false);
        inventory_jTextArea.setColumns(20);
        inventory_jTextArea.setRows(5);
        inventory_text_area_jScrollPane.setViewportView(inventory_jTextArea);

        controls_inventory_and_equipment_jTabbedPane.addTab("Inventory", inventory_text_area_jScrollPane);

        equipment_jTextArea.setEditable(false);
        equipment_jTextArea.setColumns(20);
        equipment_jTextArea.setRows(5);
        equip_text_area_jScrollPane.setViewportView(equipment_jTextArea);

        controls_inventory_and_equipment_jTabbedPane.addTab("Equip.", equip_text_area_jScrollPane);

        outgoing_text_jTextField.setText("Outgoing text.");
        outgoing_text_jTextField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                outgoing_text_jTextFieldKeyPressed(evt);
            }
        });

        incoming_text_jTextArea.setEditable(false);
        incoming_text_jTextArea.setColumns(20);
        incoming_text_jTextArea.setRows(5);
        incoming_text_jTextArea.setText("Incoming text.\n");
        incoming_text_jTextArea.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                incoming_text_jTextAreaKeyTyped(evt);
            }
        });
        outgoing_chat_text_area_jScrollPane.setViewportView(incoming_text_jTextArea);

        bind_wounds_jButton.setText("Bind Wounds");
        bind_wounds_jButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                bind_wounds_jButtonMouseClicked(evt);
            }
        });

        observe_jButton.setText("Observe");
        observe_jButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                observe_jButtonMouseClicked(evt);
            }
        });

        bargain_barter_jButton.setText("Talk / Barter");
        bargain_barter_jButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                bargain_barter_jButtonMouseClicked(evt);
            }
        });

        javax.swing.GroupLayout regular_skills_jPanelLayout = new javax.swing.GroupLayout(regular_skills_jPanel);
        regular_skills_jPanel.setLayout(regular_skills_jPanelLayout);
        regular_skills_jPanelLayout.setHorizontalGroup(
            regular_skills_jPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(regular_skills_jPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(regular_skills_jPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(bind_wounds_jButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(observe_jButton, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 123, Short.MAX_VALUE)
                    .addComponent(bargain_barter_jButton, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 123, Short.MAX_VALUE))
                .addContainerGap())
        );
        regular_skills_jPanelLayout.setVerticalGroup(
            regular_skills_jPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(regular_skills_jPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(bind_wounds_jButton, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(bargain_barter_jButton, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(observe_jButton, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(54, Short.MAX_VALUE))
        );

        regular_and_special_skills_jTabbedPane.addTab("Regular", regular_skills_jPanel);

        occupation_skill_2_jButton.setText("Special Skill 2");

        occupation_skill_1_jButton.setText("Special Skill 1");

        occupation_skill_3_jButton.setText("Special Skill 3");

        occupation_skill_4_jButton.setText("Special Skill 4");

        javax.swing.GroupLayout special_skills_jPanelLayout = new javax.swing.GroupLayout(special_skills_jPanel);
        special_skills_jPanel.setLayout(special_skills_jPanelLayout);
        special_skills_jPanelLayout.setHorizontalGroup(
            special_skills_jPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(special_skills_jPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(special_skills_jPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(occupation_skill_2_jButton, javax.swing.GroupLayout.DEFAULT_SIZE, 123, Short.MAX_VALUE)
                    .addComponent(occupation_skill_3_jButton, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(occupation_skill_4_jButton, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(occupation_skill_1_jButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        special_skills_jPanelLayout.setVerticalGroup(
            special_skills_jPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(special_skills_jPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(occupation_skill_1_jButton, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(occupation_skill_2_jButton, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(occupation_skill_3_jButton, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(occupation_skill_4_jButton, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(12, Short.MAX_VALUE))
        );

        regular_and_special_skills_jTabbedPane.addTab("Special", special_skills_jPanel);

        jScrollPane1.setViewportView(game_jTextPane);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(regular_and_special_skills_jTabbedPane, javax.swing.GroupLayout.PREFERRED_SIZE, 148, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(outgoing_text_jTextField)
                    .addComponent(outgoing_chat_text_area_jScrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 612, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(controls_inventory_and_equipment_jTabbedPane, javax.swing.GroupLayout.DEFAULT_SIZE, 229, Short.MAX_VALUE))
            .addComponent(jScrollPane1)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 557, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(regular_and_special_skills_jTabbedPane, javax.swing.GroupLayout.PREFERRED_SIZE, 205, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                        .addComponent(controls_inventory_and_equipment_jTabbedPane, javax.swing.GroupLayout.PREFERRED_SIZE, 205, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGroup(layout.createSequentialGroup()
                            .addComponent(outgoing_chat_text_area_jScrollPane, javax.swing.GroupLayout.PREFERRED_SIZE, 169, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(outgoing_text_jTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)))))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents
     private void sendKeyCommand(Key_Commands command) {

        for (QueueCommandInterface<Key_Commands> foo : direct_command_receivers_) {
            foo.enqueue(command);
        }
    }
    
    private void incoming_text_jTextAreaKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_incoming_text_jTextAreaKeyTyped
        for (QueueCommandInterface<Character> foo : outputbox_inputHandlers_) {
            foo.enqueue(evt.getKeyChar());
        }
    }//GEN-LAST:event_incoming_text_jTextAreaKeyTyped
    private void outgoing_text_jTextFieldKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_outgoing_text_jTextFieldKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            String S = outgoing_text_jTextField.getText();

            incoming_text_jTextArea.append(System.lineSeparator() + outgoing_text_jTextField.getText());
            if (!outgoing_text_jTextField.getText().startsWith("/fontsize")) {
                for (QueueCommandInterface<String> functor : inputchatbox_Handlers_) {
                    functor.enqueue(S);//Loop through and apply, but ONLY if we haven't already eaten /fontsize.
                }
            } else {
                try {
                    String temp = outgoing_text_jTextField.getText();
                    temp = temp.replaceAll("[^0-9 | .]", "");//Regex, to select anything not 0-9 or .
                    System.out.println("Test " + temp);
                    fontSize_ = Float.parseFloat(temp);
                    setFont();
                    this.addMessage("Set font to " + fontSize_);
                } catch (Exception e) {
                    this.addMessage("Invalid Font size! Current size is " + Float.toString(fontSize_));
                }
            }
            outgoing_text_jTextField.setText("");//Upon enter, clear the input box, and move it's text to output
            updateScroll();
        }
    }//GEN-LAST:event_outgoing_text_jTextFieldKeyPressed

    private void occupation_skill_1_jButtonMouseClicked(java.awt.event.MouseEvent evt) {
        sendKeyCommand(Key_Commands.USE_SKILL_1);
    }

    private void occupation_skill_2_jButtonMouseClicked(java.awt.event.MouseEvent evt) {
        sendKeyCommand(Key_Commands.USE_SKILL_2);
    }

    private void occupation_skill_3_jButtonMouseClicked(java.awt.event.MouseEvent evt) {
        sendKeyCommand(Key_Commands.USE_SKILL_3);
    }

    private void occupation_skill_4_jButtonMouseClicked(java.awt.event.MouseEvent evt) {
        sendKeyCommand(Key_Commands.USE_SKILL_4);
    }

    private void bind_wounds_jButtonMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_bind_wounds_jButtonMouseClicked
        sendKeyCommand(Key_Commands.BIND_WOUNDS);
    }//GEN-LAST:event_bind_wounds_jButtonMouseClicked

    private void bargain_barter_jButtonMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_bargain_barter_jButtonMouseClicked
        sendKeyCommand(Key_Commands.BARGAIN_AND_BARTER);
        incoming_text_jTextArea.requestFocusInWindow();
    }//GEN-LAST:event_bargain_barter_jButtonMouseClicked

    private void observe_jButtonMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_observe_jButtonMouseClicked
        sendKeyCommand(Key_Commands.OBSERVE);
    }//GEN-LAST:event_observe_jButtonMouseClicked
    private void game_jTextPaneKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_game_jTextPaneKeyTyped
    	//System.out.println("Was runA");
        for (QueueCommandInterface<Character> foo : game_inputHandlers_) {
            foo.enqueue(evt.getKeyChar());
        }
    	//System.out.println("Was runB");
    }//GEN-LAST:event_game_jTextPaneKeyTyped
    private void command_jButtonMouseClicked(java.awt.event.MouseEvent evt) {
        if (evt.getClickCount() >= 2) {
            String selected = commands_jTextArea.getSelectedText();
            for (QueueCommandInterface<String> foo : command_area_double_clicked_) {
                foo.enqueue(selected);
            }
        }
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton bargain_barter_jButton;
    private javax.swing.JButton bind_wounds_jButton;
    private javax.swing.JTextArea commands_jTextArea;
    private javax.swing.JTabbedPane controls_inventory_and_equipment_jTabbedPane;
    private javax.swing.JScrollPane equip_text_area_jScrollPane;
    private javax.swing.JTextArea equipment_jTextArea;
    private javax.swing.JTextPane game_jTextPane;
    private javax.swing.JTextArea incoming_text_jTextArea;
    private javax.swing.JTextArea inventory_jTextArea;
    private javax.swing.JScrollPane inventory_text_area_jScrollPane;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JButton observe_jButton;
    private javax.swing.JButton occupation_skill_1_jButton;
    private javax.swing.JButton occupation_skill_2_jButton;
    private javax.swing.JButton occupation_skill_3_jButton;
    private javax.swing.JButton occupation_skill_4_jButton;
    private javax.swing.JScrollPane outgoing_chat_text_area_jScrollPane;
    private javax.swing.JTextField outgoing_text_jTextField;
    private javax.swing.JTabbedPane regular_and_special_skills_jTabbedPane;
    private javax.swing.JPanel regular_skills_jPanel;
    private javax.swing.JPanel special_skills_jPanel;
    // End of variables declaration//GEN-END:variables

}
