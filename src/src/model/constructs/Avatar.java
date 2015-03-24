/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package src.model.constructs;

import java.awt.Color;
import java.util.ArrayList;

import src.HardCodedStrings;

/**
 * Each avatar represents a player
 *
 * @author JohnReedLOL
 */
public final class Avatar extends Entity {

    private static final long serialVersionUID = 16L;
    @Override
    public int getID() { return (int)serialVersionUID; }

    public Avatar(String name, char representation) {
        super(name, representation);
        setNumGoldCoinsWhenSpawned(0); // Avatars re-spawn with no cold coins.
    }

    /**
     * Same as superclass.
     * @param amount
     * @return number of level ups;
     */
    @Override
    public int gainExperiencePoints(int amount) {
        final int num_level_ups = super.gainExperiencePoints(amount);
        return num_level_ups;
    }

    public ArrayList<String> getInteractionOptionStrings() {
        ArrayList<String> options = new ArrayList<String>();
        options.add("Attack me. " + HardCodedStrings.attack);
        options.add("Start a conversation with me. " + HardCodedStrings.getChatOptions);
        return options;
    }

    public ArrayList<String> getConversationStarterStrings() {
        ArrayList<String> options = new ArrayList<String>();
        options.add("Hello");
        return options;
    }

    /**
     * If/else loop not optimized.
     * 
     * - Jack
     */
    public ArrayList<String> getConversationContinuationStrings(String what_you_just_said_to_me, Entity who_is_talking_to_me) {
        if(what_you_just_said_to_me == null ) return endConversation();
        ArrayList<String> options = new ArrayList<String>();
        if (what_you_just_said_to_me.equals("Hello")) {
            options.add("Goodbye");
            return options;
        } else {
            return endConversation();
        }
    }

    public ArrayList<String> getListOfItemsYouCanUseOnMe() {
        ArrayList<String> options = new ArrayList<String>();
        return options;
    }

    @Override
    public void gameOver() {
        super.gameOver();
    }

    /**
     * Avatars don't do anything when attacked.
     *
     * @author John-Michael Reed
     * @param damage - see super.receiveAttack()
     * @param attacker - see super.receiveAttack()
     * @return - see super.receiveAttack()
     */
    @Override
    public boolean receiveAttack(int damage, Entity attacker) {
        boolean isAlive = super.receiveAttack(damage, attacker);
        if (isAlive) {
            if (attacker != null) {
                System.out.println(name_ + " got attacked.");
            }
        }
        return isAlive;
    }
    
    @Override
    public void takeTurn() {
    	try{
    		getOccupation().takeTurn();
    	}catch(NullPointerException e){
    		
    	}
    }

    @Override
    public String toString() {
        String s = "Avatar name: " + name_;

        s += "\n Inventory " + "(" + getInventory().size() + ")" + ":";
        for (int i = 0; i < getInventory().size(); ++i) {
            s += " " + getInventory().get(i).name_;
        }

        s += "\n";

        s += " map_relationship_: ";
        if (super.getMapRelation() != null) {
            s += "Not null";
        } else {
            s += "null";
        }

        s += "\n associated with map:"
                + super.getMapRelation().isAssociatedWithMap();

        return s;
    }
    @Override
    public Color getColor(){return Color.RED;}
}
