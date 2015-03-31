package src.model.constructs;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Stats Pack for Drawable Things.
 *
 * @author John-Michael Reed
 */
public class DrawableThingStatsPack implements Serializable {

    /* SHOULD ALL BE PRIVATE!!! */
    private int offensive_rating_;
    private int armor_rating_;

    /**
     * Sets the stats pack back to 0
     */
    public void reset() {
        armor_rating_ = 0;
        offensive_rating_ = 0;
    }

    public int getOffensive_rating_() {
        return offensive_rating_;
    }

    public int getArmor_rating_() {
        return armor_rating_;
    }

    protected void setOffensive_rating_(int i) {
        offensive_rating_ = i;
    }

    protected void setArmor_rating_(int i) {
        armor_rating_ = i;
    }

    public int incrementOffensive_rating_() {
        return ++offensive_rating_;
    }

    public int incrementtArmor_rating_() {
        return ++armor_rating_;
    }

    /**
     * Constructor: sets stats to 0.
     */
    public DrawableThingStatsPack() {
        offensive_rating_ = 0;
        armor_rating_ = 0;
    }

    /**
     * Copy constructor substitute
     */
    public DrawableThingStatsPack makeCopyOfMyself() {
        DrawableThingStatsPack copy = new DrawableThingStatsPack();
        copy.offensive_rating_ = this.offensive_rating_;
        copy.armor_rating_ = this.armor_rating_;
        return copy;
    }

    /**
     * Constructor: contains several parameters to set stats.
     *
     * @param o - offensive rating
     * @param a - armor rating
     */
    public DrawableThingStatsPack(int o, int a) {
        offensive_rating_ = o;
        armor_rating_ = a;
    }

    /**
     * Adds on to the Drawable Thing stats pack.
     *
     * @param other
     */
    public void addOn(final DrawableThingStatsPack other) {
        offensive_rating_ += other.offensive_rating_;
        armor_rating_ += other.armor_rating_;
    }

    /**
     * reduces Drawable Thing's stat pack.
     *
     * @param other
     */
    public void reduceBy(final DrawableThingStatsPack other) {
        offensive_rating_ -= other.offensive_rating_;
        armor_rating_ -= other.armor_rating_;
    }

    @Override
    public String toString() {
        return "Offense: " + offensive_rating_
                + "Armor: " + armor_rating_;
    }

    public ArrayList<byte[]> makeByteArray() {
        ArrayList<byte[]> arrayList = new ArrayList<byte[]>();
        arrayList.add(Integer.toString(offensive_rating_).getBytes());
        arrayList.add(Integer.toString(armor_rating_).getBytes());
        return arrayList;
    }
}
