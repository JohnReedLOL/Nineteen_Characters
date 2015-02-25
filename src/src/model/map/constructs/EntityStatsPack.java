package src.model.map.constructs;

/**
 * Stats Pack for an Entity. Inherits from DrawableThingStatsPack.
 * @author John-Michael Reed
 */
public final class EntityStatsPack extends DrawableThingStatsPack {

    public static final int NUMBER_OF_EXPERIENCE_POINT_PER_LEVEL = 100;

// Primary stats
    private int lives_left_ = 1; // this can change without leveling up
    private int strength_level_ = 1;
    private int agility_level_ = 1;
    private int intellect_level_ = 1;
    private int hardiness_level_ = 1;
    private int quantity_of_experience_ = 1;
    private int movement_level_ = 1;

// Gets decremented every time an entity moves
    private int moves_left_in_turn_ = 1;

// Constant Secondary Stats
    private int cached_current_level_ = 1;

// Modifiable Secondary Stats
// These secondary stats can be modified without leveling up
    private int current_life_ = 1;
    private int current_mana_ = 1;
    // public int current_offensive_rating_ = 1;
    // public int current_defensive_rating_ = 1;
    // public int current_armor_rating_ = 1;

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

    /**
     * Constructor: sets values to 1.
     */
    public EntityStatsPack() {
        super(1, 1, 1, 1, 1);
    }

    public void increaseCurrentLevelByOne() {
        ++cached_current_level_;
        ++max_life_;
        ++current_life_;
        ++max_mana_;
        ++current_mana_;
        ++offensive_rating_;
        ++defensive_rating_;
    }

    public void decreaseLivesLeftByOne() {
        --lives_left_;
    }

    public void increaseLivesLeftByOne() {
        ++lives_left_;
    }

    public void increaseStrengthLevelByOne() {
        ++strength_level_;
        ++offensive_rating_;
    }

    public void increaseAgilityLevelByOne() {
        ++agility_level_;
        ++defensive_rating_;
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
        ++armor_rating_;
    }

    /**
     * 
     * @param increase
     */
    public void increaseQuantityOfExperienceBy(int increase) {
        if (increase < 0) {
            System.exit(1);
        }
        int old_experience = quantity_of_experience_;
        quantity_of_experience_ += increase;
        int diff = quantity_of_experience_ - old_experience;
        while (diff >= NUMBER_OF_EXPERIENCE_POINT_PER_LEVEL) {
            increaseCurrentLevelByOne();
            diff -= NUMBER_OF_EXPERIENCE_POINT_PER_LEVEL;
        }
    }

    public void increaseQuantityOfExperienceToNextLevel() {
        int exp_to_next = NUMBER_OF_EXPERIENCE_POINT_PER_LEVEL
                - (quantity_of_experience_ % NUMBER_OF_EXPERIENCE_POINT_PER_LEVEL);
        increaseQuantityOfExperienceBy(exp_to_next);
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
        if(moves_left_in_turn_ <= 0) {
            return -1;
        } else {
        --moves_left_in_turn_;
        return 0;
        }
    }
    
    public boolean hasMovesLeft() {
        if (moves_left_in_turn_ <= 0) {
            return false;
        }
        else {
            return true;
        }
    }

    /**
     * 
     * @param amount
     * @return -1 if your health ran out, 0 if you did not 
     */
    public int deductCurrentLifeBy(int amount) {
        if (amount < 0) {
            System.exit(-1);
        }
        if (current_life_ - amount <= 0) {
            current_life_ = 0;
            this.decreaseLivesLeftByOne();
            current_life_ = max_life_;
            return -1;
        } else {
            current_life_ -= amount;
            return 0;
        }
    }

    /**
     * 
     * @param amount
     * @return -1 if your health maxed out, 0 if you did not
     */
    public int increaseCurrentLifeBy(int amount) {
        if (amount < 0) {
            System.exit(-1);
        }
        if (current_life_ + amount >= max_life_) {
            current_life_ = max_life_;
            return -1;
        } else {
            current_life_ += amount;
            return 0;
        }
    }

    /**
     * 
     * @param amount
     * @return -1 if your mana ran out, 0 if it did not 
     */
    public int deductCurrentManaBy(int amount) {
        if (amount < 0) {
            System.exit(-1);
        }
        if (current_mana_ - amount <= 0) {
            current_mana_ = 0;
            return -1;
        } else {
            current_mana_ -= amount;
            return 0;
        }
    }

    /**
     * 
     * @param amount
     * @return -1 if your mana maxed out, 0 if it did not
     */
    public int increaseCurrentManaBy(int amount) {
        if (amount < 0) {
            System.exit(-1);
        }
        if (current_mana_ + amount >= max_mana_) {
            current_mana_ = max_mana_;
            return -1;
        } else {
            current_mana_ += amount;
            return 0;
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
                + "Armor: " + armor_rating_ + "\n"
                + "Mana: " + max_mana_ + "\n"
                + "Offense: " + offensive_rating_ + "\n"
                + "Defense: " + defensive_rating_ + "\n"
                + "Armor: " + armor_rating_ + "\n"
                + "moves_left_in_turn_: " + moves_left_in_turn_ + "\n"
                + "cached_current_level_: " + cached_current_level_ + "\n"
                + "current_life_: " + current_life_ + "\n"
                + "current_mana_: " + current_mana_ + "\n" //+ "current_offensive_rating_: " + current_offensive_rating_ + "\n"
                //+ "current_defensive_rating_: " + current_defensive_rating_ + "\n"
                //+ "current_armor_rating_: " + current_armor_rating_ + "\n"
                ;
    }

}
