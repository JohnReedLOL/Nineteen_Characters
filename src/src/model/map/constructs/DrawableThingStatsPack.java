package src.model.map.constructs;

/**
 * Stats Pack for Drawable Things.
 * @author John-Michael Reed
 */
public class DrawableThingStatsPack {

	protected int max_life_ = 0;

	protected int max_mana_ = 0;

	protected int offensive_rating_;

	protected int defensive_rating_ = 0;

	protected int armor_rating_;

    public int getMax_life_() {
        return max_life_;
    }

    public int getMax_mana_() {
        return max_mana_;
    }

    public int getOffensive_rating_() {
        return offensive_rating_;
    }

    public int getDefensive_rating_() {
        return defensive_rating_;
    }

    public int getArmor_rating_() {
        return armor_rating_;
    }

	/**
	 * Constructor: sets stats to 0.
	 */
	public DrawableThingStatsPack() {
		//life_ = 0;
		//mana_ = 0;
		offensive_rating_ = 0;
		//defensive_rating_ = 0;
		armor_rating_ = 0;
	}
        
       /**
	 * Constructor: contains several parameters to set stats.
	 * @param o - offensive rating
	 * @param a - armor rating
	 */
	public DrawableThingStatsPack(int o, int a) {
		//life_ = l;
		//mana_ = m;
		offensive_rating_ = o;
		//defensive_rating_ = d;
		armor_rating_ = a;
	}

	/**
	 * LEGACY CODE
	 * @param l - life
	 * @param m - mana
	 * @param o - offensive rating
	 * @param d - defensive rating
	 * @param a - armor rating
	 */
	public DrawableThingStatsPack(int l, int m, int o, int d, int a) {
		//life_ = l;
		//mana_ = m;
		offensive_rating_ = o;
		//defensive_rating_ = d;
		armor_rating_ = a;
	}
	/*
public DrawableThingStatsPack add(final DrawableThingStatsPack other) {
return new DrawableThingStatsPack(
max_life_ + other.max_life_,
max_mana_ + other.max_mana_,
offensive_rating_ + other.offensive_rating_,
defensive_rating_ + other.defensive_rating_,
armor_rating_ + other.armor_rating_
); 
}

public DrawableThingStatsPack subtract(final DrawableThingStatsPack other) {
return new DrawableThingStatsPack(
max_life_ - other.max_life_,
max_mana_ - other.max_mana_,
offensive_rating_ - other.offensive_rating_,
defensive_rating_ - other.defensive_rating_,
armor_rating_ - other.armor_rating_
); 
}
	 */
	/**
	 * Adds on to the Drawable Thing stats pack.
	 * @param other
	 */
	public void addOn(final DrawableThingStatsPack other) {
		//life_ += other.max_life_;
		//mana_ += other.max_mana_;
		offensive_rating_ += other.offensive_rating_;
		//defensive_rating_ += other.defensive_rating_;
		armor_rating_ += other.armor_rating_;
	}

	/**
	 * reduces Drawable Thing's stat pack.
	 * @param other
	 */
	public void reduceBy(final DrawableThingStatsPack other) {
		//life_ -= other.max_life_;
		//mana_ -= other.max_mana_;
		offensive_rating_ -= other.offensive_rating_;
		//defensive_rating_ -= other.defensive_rating_;
		armor_rating_ -= other.armor_rating_;
	}

	@Override
	public String toString() {
		return //"Life: " + max_life_ + "\n" + 
				//"Mana: " + max_mana_ + "\n" +
				"Offense: " + offensive_rating_ + "\n" +
				//"Defense: " + defensive_rating_ + "\n" +
				"Armor: " + armor_rating_ + "\n";
	}
}
