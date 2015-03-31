/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package src;

import java.awt.Color;
import java.io.Serializable;
import java.util.ArrayList;

import src.model.constructs.EntityStatsPack;
import src.model.constructs.Occupation;
import src.model.constructs.items.PickupableItem;
import src.model.constructs.items.PrimaryHandHoldable;
import src.model.constructs.items.SecondaryHandHoldable;

/**
 * Contains contents of data for IO to user.
 *
 * @author JohnReedLOL
 */
public class IO_Bundle implements Serializable {

    public IO_Bundle( String observation_string,
            char[][] v, int[][] c, ArrayList<PickupableItem> i,
            EntityStatsPack s, Occupation o, int n, int bi, int ba, int ob,
            PrimaryHandHoldable pri, SecondaryHandHoldable sec,
            ArrayList<String> sfc, int num_coins, boolean is_alive
    ) {
        observation_string_ = new String(observation_string);
        view_for_display_ = v;
        color_for_display_ = c;
        if (i != null) {
            inventory_ = new ArrayList<PickupableItem>(i);
            inventory_.trimToSize();
        } else {
            inventory_ = null;
        }
        if (s != null) {
            stats_for_display_ = s.makeCopyOfMyself();
        } else {
            stats_for_display_ = null;
        }
        if (o != null) {
            occupation_ = o.getACopyOfMyself();
        } else {
            occupation_ = null;
        }
        num_skillpoints_ = n;
        bind_wounds_ = bi;
        bargain_ = ba;
        observation_ = ob;
        // Assume that these guys NEVER get modified in-game [** Danger **]
        primary_ = pri;
        second_ = sec;

        if (sfc != null) {
            strings_for_communication_ = new ArrayList<String>(sfc);
            strings_for_communication_.trimToSize();
        } else {
            strings_for_communication_ = null;
        }
        num_coins_ = num_coins;
        is_alive_ = is_alive;
    }

    public static char[] convertArrayListOfCharToArray(ArrayList<Character> c) {
        char[] arr = new char[c.size()];
        for (int i = 0; i < c.size(); ++i) {
            arr[i] = c.get(i);
        }
        return arr;
    }

    public static short[] convertArrayListOfShortToArray(ArrayList<Short> c) {
        short[] arr = new short[c.size()];
        for (int i = 0; i < c.size(); ++i) {
            arr[i] = c.get(i);
        }
        return arr;
    }
    // stores text of observation command
    public final String observation_string_;
    public char[][] view_for_display_;
    public int[][] color_for_display_;
    public final PrimaryHandHoldable primary_;
    public final SecondaryHandHoldable second_;
    public final ArrayList<PickupableItem> inventory_;
    public final EntityStatsPack stats_for_display_;
    public final Occupation occupation_;
    public final int num_skillpoints_;
    public final int bind_wounds_;
    public final int bargain_;
    public final int observation_;
    public final ArrayList<String> strings_for_communication_;
    public final int num_coins_;
    public final boolean is_alive_;

    public EntityStatsPack getStatsPack() {
        return stats_for_display_;
    }

    public Occupation getOccupation() {
        return occupation_;
    }

    public ArrayList<PickupableItem> getInventory() {
        return inventory_;
    }

    public ArrayList<String> getSkillNames() {
        ArrayList<String> result = new ArrayList<String>();
        if (occupation_ != null) {
            result.add(occupation_.getSkillNameFromNumber(1));
            result.add(occupation_.getSkillNameFromNumber(2));
            result.add(occupation_.getSkillNameFromNumber(3));
            result.add(occupation_.getSkillNameFromNumber(4));
        } else {
            result.add("Special skill 1");
            result.add("Special skill 2");
            result.add("Special skill 3");
            result.add("Special skill 4");
        }
        result.add(0, "Bind Wounds");
        result.add(0, "Bargain");
        result.add(0, "Observation");
        return result;
    }

    public ArrayList<Integer> getSkillLevels() {
        ArrayList<Integer> result = new ArrayList<Integer>();
        if (occupation_ != null) {
            result.add(occupation_.getSkill_1_());
            result.add(occupation_.getSkill_2_());
            result.add(occupation_.getSkill_3_());
            result.add(occupation_.getSkill_4_());
        } else {
            result.add(0);
            result.add(0);
            result.add(0);
            result.add(0);
        }
        result.add(0, bind_wounds_);
        result.add(0, bargain_);
        result.add(0, observation_);
        return result;

    }
}
