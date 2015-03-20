/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package src.model.constructs;

import java.util.ArrayList;

import src.HardCodedStrings;

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
        options.add("Attack me. " + HardCodedStrings.attack);
        options.add("Select a skill to use on me. " + HardCodedStrings.getAllSkills);
        return options;
    }

    public ArrayList<String> getConversationStarterStrings() {
        ArrayList<String> options = new ArrayList<String>();
        return options;
    }

    @Override
    public ArrayList<String> getConversationContinuationStrings(String what_you_just_said_to_me, Entity who_is_talking_to_me) {
        if (what_you_just_said_to_me == null) {
            return endConversation();
        }
        if (what_you_just_said_to_me.equals("Select a skill to use on me. " + HardCodedStrings.getAllSkills)) {
            ArrayList<String> options = new ArrayList<String>();
            options.add("Observe " + HardCodedStrings.observe);
            return options;
        } else if (what_you_just_said_to_me.equals("Observe " + HardCodedStrings.observe)) {
            ArrayList<String> options = new ArrayList<String>();
            int observation_level = who_is_talking_to_me.getObservation_();
            if (observation_level <= 1) {
                options.add("Your observation level is too low to observe me");
            } else {
                options.add("My health is: " + this.getStatsPack().getCurrent_life_());
                options.add("My mana is: " + this.getStatsPack().getCurrent_mana_());
                options.add("The number of respawns I have left is: " + this.getStatsPack().getLives_left_());
            }
            return options;
        } else {
            return endConversation();
        }
    }

    public ArrayList<String> getListOfItemsYouCanUseOnMe() {
        ArrayList<String> options = new ArrayList<String>();
        return options;
    }

    /**
     * Monsters will move toward attacker and attack back is distance is less
     * than 2. Precondition: Monsters must have a MapRelation to attack back if
     * enemy is in range.
     *
     * @param damage - amount of damage received from attacker
     * @param attacker - Entity that is attacking me
     * @return true if I did not die on attack, false if I did die
     */
    @Override
    public boolean receiveAttack(int damage, Entity attacker) {
        if (this.getMapRelation() != null) {
            System.out.println("Monster's map relation is not null in Monster.receiveAttack");
            // precondition met.
            boolean isAlive = super.receiveAttack(damage, attacker);
            if (isAlive) {
                if (attacker != null) {
                    System.out.println("Attacker is " + attacker.name_ + " Monster.receiveAttack");
                    final int zero_if_I_moved = getMapRelation().moveTowardDrawable(attacker);
                    System.out.println("Zero if I moved in Monster.receiveAttack: " + zero_if_I_moved);
                    final double pythagorean_distance = getMapRelation().measureDistanceTowardDrawable(attacker);
                    System.out.println("pythagorean_distance  in Monster.receiveAttack: " + pythagorean_distance);
                    final double epsilon = .0001;
                    if (pythagorean_distance >= 0 - epsilon && pythagorean_distance < 2) {
                        this.sendAttack(attacker);
                        System.out.println("Monster attacked back in Monster.receiveAttack");
                    } else {
                        // error pythagorean distance is negative or attacker is too far away.
                        System.out.println("error pythagorean distance is negative or attacker is too far away in Monster.receiveAttack");
                    }
                } else {
                    System.out.println("Attacker is null Monster.receiveAttack");
                }
            } else {
                // I am dead I cannot move or attack back.
            }
            return isAlive;
        } else {
            // precondition violated.
            System.out.println("Precondition violated in Monster.receiveAttack");
            System.exit(-76);
            return super.receiveAttack(damage, attacker);
        }
    }
}
