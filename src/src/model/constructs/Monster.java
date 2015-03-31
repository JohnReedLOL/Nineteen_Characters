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

    public int getID() { return 15; }

    public Monster(String name, char representation) {
        super(name, representation);
    }

    public ArrayList<String> getInteractionOptionStrings() {
        ArrayList<String> options = new ArrayList<String>();
        options.add("Attack me. " + HardCodedStrings.attack);
        options.add("Select a skill to use on me. " + HardCodedStrings.getAllSkills);
        return options;
    }
    boolean is_running_ = false;
    int turns_to_run_ = 0;
    int turns_to_follow_ = 0;
    Entity Entity_to_follow_ = null;
    Entity Entity_to_avoid_ = null;

    public int getFollowTurns() { return turns_to_follow_; }

    public String getFolloweeName() {
        if (Entity_to_follow_ != null)
            return Entity_to_follow_.getName();
        else
            return null;
    }

    /**
     * Follow the given entity for X turns
     *
     * @param followee
     * @param turns
     * @return 0
     */
    private int setFollowing(Entity followee, int turns) {
        turns_to_follow_ = turns;
        Entity_to_follow_ = followee;
        return 0;

    }

    @Override
    public void takeTurn() {
    	if (!is_running_) {
	        if (Entity_to_follow_ != null && Entity_to_follow_.getMapRelation() != null
	                && Entity_to_follow_.hasLivesLeft() && turns_to_follow_ > 0) {
	            //attack then follow.
	            attackIfNear(Entity_to_follow_);
	            follow(Entity_to_follow_);
	            --turns_to_follow_;
	            if (turns_to_follow_ == 0) {
	                stopFollowing();
	            }
	        }
    	}
    	else if (Entity_to_avoid_ != null && Entity_to_avoid_.getMapRelation() != null
                && Entity_to_avoid_.hasLivesLeft() && turns_to_run_ > 0) {
            run(Entity_to_avoid_);
            --turns_to_run_;
            if (turns_to_run_ == 0) {
                stopAvoiding();
            }
        }

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
                options.add("Name: " + this.getName());
                options.add("Health: " + this.getStatsPack().getCurrent_life_());
                options.add("Max health: " + this.getStatsPack().getMax_life_());
                options.add("Mana is: " + this.getStatsPack().getCurrent_mana_());
                options.add("Attack power: " + this.getStatsPack().getOffensive_rating_());
                options.add("Defense: " + this.getStatsPack().getDefensive_rating_());
                options.add("Armor: " + this.getStatsPack().getArmor_rating_());
                options.add("Lives left: " + this.getStatsPack().getLives_left_());
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
     * Follow the given entity, AKA, move a square towards it.
     *
     * @param followee
     * @return 0
     */
    private int follow(Entity followee) {
        if (followee == null || followee.getMapRelation() == null || !followee.hasLivesLeft()
                || !this.hasLivesLeft()) {
            System.out.println("Folowee is gone");
            // precondition violated
            return -1;
        }
        System.out.println("Attacker is " + followee.name_ + " Monster.receiveAttack");
        final int zero_if_I_moved = getMapRelation().moveTowardEntity(followee);
        System.out.println("Zero if I moved in Monster.receiveAttack: " + zero_if_I_moved);
        final double pythagorean_distance = getMapRelation().measureDistanceTowardEntity(followee);
        System.out.println("pythagorean_distance  in Monster.receiveAttack: " + pythagorean_distance);
        return 0;
    }
    
    /**
     * Follow the given entity, AKA, move a square towards it.
     *
     * @param followee
     * @return 0
     */
    private int run(Entity avoidee) {
        if (avoidee == null || avoidee.getMapRelation() == null || !avoidee.hasLivesLeft()) {
            System.out.println("Avoidee is gone");
            // precondition violated
            return -1;
        }
        System.out.println("Avoidee is " + avoidee.name_ + " Monster.receiveAttack");
        final int zero_if_I_moved = getMapRelation().moveAwayFromEntity(avoidee);
        return 0;
    }
    
    public int stopFollowing() {
    	turns_to_follow_ = 0;
        Entity_to_follow_ = null;
        return 0;
    }
    
    public int stopAvoiding() {
    	turns_to_run_ = 0;
        Entity_to_avoid_ = null;
        is_running_ = false;
        return 0;
    }

    /**
     * Attempts to attack the given entity if they are near.
     *
     * @param followee
     * @return
     */
    private boolean attackIfNear(Entity followee) {
        if (followee != null && followee.getMapRelation() != null && followee.hasLivesLeft()
                && this.hasLivesLeft()) {
            final double epsilon = .0001;
            final double pythagorean_distance = getMapRelation().measureDistanceTowardEntity(followee);
            if (pythagorean_distance >= 0 - epsilon && pythagorean_distance < 2) {
                this.sendAttack(followee);
                return true;
            } else {
                return false;
                // error pythagorean distance is negative or attacker is too far away.
            }
        }
        return false;
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
        if (this != null && this.getMapRelation() != null && this.hasLivesLeft()) {
            System.out.println("Monster's map relation is not null in Monster.receiveAttack and monster has lives left");
            // precondition met.
            boolean isAlive = super.receiveAttack(damage, attacker);
            if (isAlive) {
                if (attacker != null && attacker.getMapRelation() != null && attacker.hasLivesLeft()) {
                    setFollowing(attacker, 6);//Arbitrary value for time to follow the thing.
                    follow(attacker);
                    attackIfNear(attacker);
                }
            }
            return isAlive;
        } else {
            // precondition violated.
            System.out.println("Precondition violated in Monster.receiveAttack");
            System.exit(-76);
            return super.receiveAttack(damage, attacker);
        }
    }

	public void causeFear(Entity avoidee, int turns) {
		is_running_ = true;
		turns_to_run_ = turns;
		Entity_to_avoid_ = avoidee;
	}

    /**
     * Monsters don't respawn.
     */
    @Override
    public void commitSuicide() {
        int health_left = getStatsPack().getCurrent_life_();
        getStatsPack().deductCurrentLifeBy(health_left);
        getStatsPack().decreaseLivesLeftByOne();
        gameOver();
        
    }

}
