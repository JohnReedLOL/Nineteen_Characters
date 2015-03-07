/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package src.model.map.constructs;

import java.util.ArrayList;

/**
 *
 * @author JohnReedLOL
 */
public class Monster extends Entity {

    public Monster(String name, char representation) {
        super(name, representation);
    }

    public ArrayList<String> getInteractionOptionStrings() {
        ArrayList<String> options = new ArrayList<String>();
        options.add("Attack me. [ Attack ]");
        return options;
    }

    public ArrayList<String> getConversationStarterStrings() {
        ArrayList<String> options = new ArrayList<String>();
        return options;
    }

    public ArrayList<String> getConversationContinuationStrings(String what_you_just_said_to_me) {
        ArrayList<String> options = new ArrayList<String>();
        return options;
    }

    public ArrayList<String> getListOfItemsYouCanUseOnMe() {
        ArrayList<String> options = new ArrayList<String>();
        return options;
    }

    /**
     * Monsters automatically attack back when attacked
     *
     * @author John-Michael Reed
     * @param attacker
     * @return 0 if reply succeeded, non-zero otherwise [ex. if entity is null
     * or off the map]
     */
    @Override
    public int replyToAttackFrom(Entity attacker) {
        if (attacker == null) {
            return -1;
        }
        this.getMapRelation().sendAttack(attacker);
        return 0;
    }
}
