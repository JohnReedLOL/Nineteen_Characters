/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package src.model.constructs;

import java.util.ArrayList;

import src.HardCodedStrings;
import src.model.constructs.items.Bow;
import src.model.constructs.items.OneHandedSword;
import src.model.constructs.items.Staff;

/**
 * Merchant runs away from attacks like a villager.
 *
 * @author JohnReedLOL
 */
public class Merchant extends Villager {

    public Merchant(String name, char representation) {
        super(name, representation);
    }

    public ArrayList<String> getInteractionOptionStrings() {
        ArrayList<String> options = new ArrayList<String>();
        options.add("Ask for a list of items that I am selling. " + HardCodedStrings.getChatOptions);
        return options;
    }

    public ArrayList<String> getConversationStarterStrings() {
        ArrayList<String> options = new ArrayList<String>();
        options.add("Super_Sword : 10 gold : " + HardCodedStrings.getReplyOptions);
        options.add("Super_Staff : 10 gold : " + HardCodedStrings.getReplyOptions);
        options.add("Super_Bow : 10 gold : " + HardCodedStrings.getReplyOptions);
        return options;
    }

    /**
     * This function returns a list of appropriate responses to the string that
     * you recieved last.
     *
     * @author John-Michael Reed
     * @param what_you_just_said_to_me - same as "what you last said to me"
     * @return conversation options
     */
    @Override
    public ArrayList<String> getConversationContinuationStrings(String what_you_just_said_to_me, Entity who_is_talking_to_me) {
        if(what_you_just_said_to_me == null ) return endConversation();
        ArrayList<String> options = new ArrayList<String>();
        if (what_you_just_said_to_me.equals("Super_Sword : 10 gold : " + HardCodedStrings.getReplyOptions)) {
            options.add("Buy Super_Sword! " + HardCodedStrings.trade);
            options.add("Nevermind.");
        } else if (what_you_just_said_to_me.equals("Super_Staff : 10 gold : " + HardCodedStrings.getReplyOptions)) {
            options.add("Buy Super_Staff! " + HardCodedStrings.trade);
            options.add("Nevermind.");
        } else if (what_you_just_said_to_me.equals("Super_Bow : 10 gold : " + HardCodedStrings.getReplyOptions)) {
            options.add("Buy Super_Bow! " + HardCodedStrings.trade);
            options.add("Nevermind.");
        } else if (what_you_just_said_to_me.equals("Nevermind.")) {
            options.add("Bye.");
        } else if (what_you_just_said_to_me.equals("Buy Super_Sword! " + HardCodedStrings.trade)) {
            return makePurchase("Super_Sword", who_is_talking_to_me);
        } else if (what_you_just_said_to_me.equals("Buy Super_Staff! " + HardCodedStrings.trade)) {
            return makePurchase("Super_Staff", who_is_talking_to_me);
        } else if (what_you_just_said_to_me.equals("Buy Super_Bow! " + HardCodedStrings.trade)) {
            return makePurchase("Super_Bow", who_is_talking_to_me);
        } else {
            return endConversation();
        }
        return options;
    }

    /**
     * @author John-Michael Reed
     * @param recieved_text - what was said to me
     * @param buyer - the person who I am talking to
     * @return - what I said back
     */
    public ArrayList<String> makePurchase(String weapon_name, Entity buyer) {
        ArrayList<String> reply = new ArrayList<>();
        if (buyer.getNumGoldCoins() < 10) {
            reply.add("Sorry. You are too poor to afford my wares.");
            reply.add(endConversation().get(0));
            return reply;
        } else {
            //reply.add("You will be amazed at what my weapons can do.");
            if (buyer.getBargain_() == 1) {
                reply.add("With your bargaining skills, I give you " + buyer.getBargain_() + " coin off.");
            } else {
                reply.add("With your bargaining skills, I give you " + buyer.getBargain_() + " coins off.");
            }
            reply.add(endConversation().get(0));
            buyer.decrementNumGoldCoinsBy(10 - buyer.getBargain_());
        }
        if (weapon_name.equals("Super_Sword")) {
            OneHandedSword super_sword = new OneHandedSword("Super_Sword", 'S');
            super_sword.getStatsPack().addOn(new DrawableThingStatsPack(100, 0));
            buyer.addItemToInventory(super_sword);
        } else if (weapon_name.equals("Super_Staff")) {
            Staff super_staff = new Staff("Super_Staff", 'S');
            super_staff.getStatsPack().addOn(new DrawableThingStatsPack(100, 0));
            buyer.addItemToInventory(super_staff);
        } else if (weapon_name.equals("Super_Bow")) {
            Bow super_bow = new Bow("Super_Bow", 'S');
            super_bow.getStatsPack().addOn(new DrawableThingStatsPack(100, 0));
            buyer.addItemToInventory(super_bow);
            return reply;
        } else {
            System.err.println("Undefined purchase option in Merchant.makePurchase");
            System.exit(-82);
        }
        return reply;
    }

    public ArrayList<String> getListOfItemsYouCanUseOnMe() {
        ArrayList<String> options = new ArrayList<String>();
        return options;
    }
}
