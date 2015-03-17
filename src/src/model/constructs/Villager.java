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
public class Villager extends Entity {

    public Villager(String name, char representation) {
        super(name, representation);
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
    @Override
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

    /**
     * Runs away if attacked
     *
     * @param damage - see super.receiveAttack()
     * @param attacker - see super.receiveAttack()
     * @return - see super.receiveAttack()
     */
    @Override
    public boolean receiveAttack(int damage, Entity attacker) {
        boolean isAlive = super.receiveAttack(damage, attacker);
        if (isAlive) {
            if (attacker != null) {
                replyToAttackFrom(attacker);
            }
        }
        return isAlive;
    }

    /**
     * Villagers automatically run away [in the opposite direction of attacker]
     * when attacked
     *
     * @author John-Michael Reed
     * @param attacker
     * @return 0 if reply succeeded, non-zero otherwise [failed to run away,
     * trapped]
     */
    private int replyToAttackFrom(Entity attacker) {
        if (attacker == null) {
            return -1;
        }
        final int attackerX = attacker.getMapRelation().getMyXCoordinate();
        final int attackerY = attacker.getMapRelation().getMyYCoordinate();

        final int myX = this.getMapRelation().getMyXCoordinate();
        final int myY = this.getMapRelation().getMyYCoordinate();
        System.out.println("You attacked a villager");
        if (myX == attackerX && myY == attackerY) {
            System.err.println("impossible error in Villager.replyToAttackFrom");
            System.exit(-6); // Impossible
            return -999;
        } else if (myX == attackerX && myY > attackerY) {
            return this.getMapRelation().moveInDirection(0, 1);
        } else if (myX == attackerX && myY < attackerY) {
            return this.getMapRelation().moveInDirection(0, -1);
        } else if (myX > attackerX && myY == attackerY) {
            return this.getMapRelation().moveInDirection(1, 0);
        } else if (myX < attackerX && myY == attackerY) {
            return this.getMapRelation().moveInDirection(-1, 0);
        } else if (myX > attackerX && myY > attackerY) {
            return this.getMapRelation().moveInDirection(1, 1);
        } else if (myX < attackerX && myY < attackerY) {
            return this.getMapRelation().moveInDirection(-1, -1);
        } else if (myX > attackerX && myY < attackerY) {
            return this.getMapRelation().moveInDirection(1, -1);
        } else if (myX < attackerX && myY > attackerY) {
            return this.getMapRelation().moveInDirection(-1, 1);
        } else {
            System.err.println("Impossible error in Villager.replyToAttackFrom");
            System.exit(-9); // Impossible
            return -999;
        }
    }
}
