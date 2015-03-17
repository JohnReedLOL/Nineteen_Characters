package src.model.map.constructs;

import src.Internet;

import java.io.Serializable;
import java.util.ArrayList;


/**
 * Stats Pack for an Entity. Inherits from DrawableThingStatsPack.
 *
 * @author John-Michael Reed
 */
public final class EntityStatsPack extends DrawableThingStatsPack implements Serializable {

    public static final int NUMBER_OF_EXPERIENCE_POINT_PER_LEVEL = 100;

// Primary stats - SHOULD ALL BE PRIVATE!!!!!!!!!!!
    private int lives_left_ = 1; // this can change without leveling up
    private int strength_level_ = 1;
    private int agility_level_ = 1;
    private int intellect_level_ = 1;
    private int hardiness_level_ = 1;
    private int quantity_of_experience_ = 1;
    private int movement_level_ = 1;
    private int max_life_ = 1;
    private int max_mana_ = 1;
    private int defensive_rating_ = 1;

    public int getMax_life_() {
        return max_life_;
    }

    public int getMax_mana_() {
        return max_mana_;
    }

// Gets decremented every time an entity moves
    public int moves_left_in_turn_ = 1;

// Constant Secondary Stats
    public int cached_current_level_ = 1;

// Modifiable Secondary Stats
// These secondary stats can be modified without leveling up
    public int current_life_ = 1;
    public int current_mana_ = 1;

    public int getLives_left_() {
        return lives_left_;
    }

    public int getStrength_level_() {
        return strength_level_;
    }

    public int getAgility_level_() {
        return agility_level_;
    }

    public int getIntellect_level_() {
        return intellect_level_;
    }

    public int getHardiness_level_() {
        return hardiness_level_;
    }

    public int getQuantity_of_experience_() {
        return quantity_of_experience_;
    }

    public int getMovement_level_() {
        return movement_level_;
    }

    public int getMoves_left_in_turn_() {
        return moves_left_in_turn_;
    }

    public int getCached_current_level_() {
        return cached_current_level_;
    }

    public int getCurrent_life_() {
        return current_life_;
    }

    public int getCurrent_mana_() {
        return current_mana_;
    }
    public int getDefensive_rating_(){
    	return defensive_rating_;
    }

    /**
     * Constructor: sets values to 1.
     */
    public EntityStatsPack() {
        super(1, 1);
    }
    /**
     * Resets the stat pack back to it's natural, pristine state.
     */
    public void reset() {
    	super.reset();
    	lives_left_ = 1; // this can change without leveling up
    	strength_level_ = 1;
    	agility_level_ = 1;
    	intellect_level_ = 1;
    	hardiness_level_ = 1;
    	quantity_of_experience_ = 1;
    	movement_level_ = 1;
    	max_life_ = 1;
    	max_mana_ = 1;
    	defensive_rating_ = 1;
    }
    /** 
     * Copy constructor
     * @param in : Stats pack to copy
     */
    public EntityStatsPack(EntityStatsPack in){
    	super(in);
    	lives_left_ = in.lives_left_; // this can change without leveling up
    	strength_level_ = in.strength_level_;
    	agility_level_ = in.agility_level_;
    	intellect_level_ = in.intellect_level_;
    	hardiness_level_ = in.hardiness_level_;
    	quantity_of_experience_ = in.quantity_of_experience_;
    	movement_level_ = in.movement_level_;
    	max_life_ = in.max_life_;
    	max_mana_ = in.max_mana_;
    	defensive_rating_ = in.defensive_rating_;
    }

    public void increaseCurrentLevelByOne() {
        ++cached_current_level_;
        ++max_life_;
        ++current_life_;
        ++max_mana_;
        ++current_mana_;
        
        super.incrementOffensive_rating_();
       
        increaseDefenseLevelByOne();
        increaseHardinessLevelByOne();
        increaseMovementLevelByOne();
        increaseLivesLeftByOne();
        increaseStrengthLevelByOne();
        increaseAgilityLevelByOne();
        increaseIntellectLevelByOne();
    }

    public void decreaseLivesLeftByOne() {
        --lives_left_;
    }

    public void increaseLivesLeftByOne() {
        ++lives_left_;
    }

    public void increaseStrengthLevelByOne() {
        ++strength_level_;
        super.incrementOffensive_rating_();
    }

    public void increaseAgilityLevelByOne() {
        ++agility_level_;
        increaseDefenseLevelByOne();
    }


	public void increaseIntellectLevelByOne() {
        ++intellect_level_;
        ++max_mana_;
        ++current_mana_;
    }

    public void increaseHardinessLevelByOne() {
        ++hardiness_level_;
        ++max_life_;
        ++current_life_;
        super.incrementtArmor_rating_();
    }
    public void increaseDefenseLevelByOne(){
    	++defensive_rating_;
    }

    /**
     *
     * @param increase
     * @return 0 if leveled up zero times, 1 if leveled up 1 time, etc.
     */
    public int increaseQuantityOfExperienceBy(int increase) {
        if (increase < 0) {
            System.exit(1);
        }
        final int old_experience = quantity_of_experience_;
        quantity_of_experience_ += increase;
        final int old_div_100 = old_experience / NUMBER_OF_EXPERIENCE_POINT_PER_LEVEL;
        
        final int new_div_100 = quantity_of_experience_ / NUMBER_OF_EXPERIENCE_POINT_PER_LEVEL;
        int num_level_ups_counter = (Math.abs(new_div_100 - old_div_100));
        final int num_level_ups = num_level_ups_counter;
        while (num_level_ups_counter > 0) {
            increaseCurrentLevelByOne();
            --num_level_ups_counter;
        }
        return num_level_ups;
    }

    public void increaseMovementLevelByOne() {
        ++movement_level_;
        ++moves_left_in_turn_;
    }

    /**
     *
     * @return -1 if moves left is less than or equal to zero
     */
    public int decreaseMovesLeftByOne() {
        if (moves_left_in_turn_ > 0) {
            --moves_left_in_turn_;
            return 0;
        } else {
            return -1;
        }
    }

    public boolean hasMovesLeft() {
        if (moves_left_in_turn_ > 0) {
            return true;
        } else {
            return false;
        }
    }

    /**
     *
     * @param amount
     * @return -1 if your health ran out, 0 if you did not
     */
    public int deductCurrentLifeBy(int amount) {
        if (amount < 0) {
            System.err.println("Error in EntityStatsPack.deductCurrentLifeBy(int amount).");
            System.err.println("You are not allowed to do negative damage.");
            System.exit(-1);
        }
        if (current_life_ - amount > 0) {
            current_life_ -= amount;
            return 0;
        } else {
            current_life_ = 0;
            return -1;
        }
    }

    /**
     * Sets health to max if increase produces more than max health
     * @param amount
     * @return -1 if your health exceeded its max, 0 if you did not
     */
    public int increaseCurrentLifeBy(int amount) {
        if (amount < 0) {
            System.err.println("Warning! Current life increasing function cannot increase life by negative amount: " + amount);
            amount = 0;
        }
        if (current_life_ + amount <= max_life_) {
            current_life_ += amount;
            return 0;
        } else {
            current_life_ = max_life_;
            return -1;
        }
    }

    /**
     * Sets mana to zero if decrease produces less than max mana
     * @param amount
     * @return -1 if your mana became negative, 0 if it did not
     */
    public int deductCurrentManaBy(int amount) {
        if (amount < 0) {
            System.err.println("Warning! Current mana reducing function cannot reduce mana by negative amount: " + amount);
            amount = 0;
        }
        if (current_mana_ - amount >= 0) {
            current_mana_ -= amount;
            return 0;
        } else {
            current_mana_ = 0;
            return -1;
        }
    }

    /**
     * Sets mana to maximum if increase produces more than max mana
     * @param amount
     * @return -1 if your mana exceeded its max, 0 if it did not
     */
    public int increaseCurrentManaBy(int amount) {
        if (amount < 0) {
            System.err.println("Warning! Current mana increasing function cannot increase mana by negative amount: " + amount);
            amount = 0;
        }
        if (current_mana_ + amount <= max_mana_) {
            current_mana_ += amount;
            return 0;
        } else {
            current_mana_ = max_mana_;
            return -1;
        }
    }

    @Override
    public String toString() {
        return super.toString()
                + "lives_left_: " + lives_left_ + "\n"
                + "strength_level_: " + strength_level_ + "\n"
                + "agility_level_: " + agility_level_ + "\n"
                + "intellect_level_: " + intellect_level_ + "\n"
                + "hardiness_level_: " + hardiness_level_ + "\n"
                + "quantity_of_experience_: " + quantity_of_experience_ + "\n"
                + "movement_level_: " + movement_level_ + "\n"
                + "Mana: " + max_mana_ + "\n"
                + "Offense: " + super.getOffensive_rating_() + "\n"
                + "Defense: " + getDefensive_rating_() + "\n"
                + "Armor: " + super.getArmor_rating_() + "\n"
                + "moves_left_in_turn_: " + moves_left_in_turn_ + "\n"
                + "cached_current_level_: " + cached_current_level_ + "\n"
                + "current_life_: " + current_life_ + "\n"
                + "current_mana_: " + current_mana_ + "\n" //+ "current_offensive_rating_: " + current_offensive_rating_ + "\n"
                //+ "current_defensive_rating_: " + current_defensive_rating_ + "\n"
                //+ "current_armor_rating_: " + current_armor_rating_ + "\n"
                ;
    }

    public ArrayList<byte[]> makeByteArray(){
        ArrayList<byte[]> arrayList = new ArrayList<byte[]>();
        arrayList.addAll(super.makeByteArray());

       // arrayList.add(Integer.toString(NUMBER_OF_EXPERIENCE_POINT_PER_LEVEL).getBytes());
        arrayList.add(Integer.toString(lives_left_).getBytes());
        arrayList.add(Integer.toString(strength_level_).getBytes());
        arrayList.add(Integer.toString(agility_level_).getBytes());
        arrayList.add(Integer.toString(intellect_level_).getBytes());
        arrayList.add(Integer.toString(hardiness_level_).getBytes());
        arrayList.add(Integer.toString(quantity_of_experience_).getBytes());
        arrayList.add(Integer.toString(movement_level_).getBytes());
        arrayList.add(Integer.toString(max_life_).getBytes());
        arrayList.add(Integer.toString(max_mana_).getBytes());
        arrayList.add(Integer.toString(defensive_rating_).getBytes());
        arrayList.add(Integer.toString(moves_left_in_turn_).getBytes());
        arrayList.add(Integer.toString(cached_current_level_).getBytes());
        arrayList.add(Integer.toString(current_life_).getBytes());
        arrayList.add(Integer.toString(current_mana_).getBytes());

        return arrayList;
    }

    /*public EntityStatsPack makeObjectFromBytes(ArrayList<byte[]> arrayList){
        EntityStatsPack es = new EntityStatsPack();
        es.lives_left_ = Integer.parseInt(arrayList.get(0).toString());
        
    }*/
}

