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

    public IO_Bundle(ArrayList<Character> unchanged_characters, ArrayList<Short> character_frequencies,
            ArrayList<Color> compressed_colors, ArrayList<Short> color_frequencies,
            char[][] v, Color[][] c, ArrayList<PickupableItem> i,
            EntityStatsPack s, Occupation o, int n, int bi, int ba, int ob,
            PrimaryHandHoldable pri, SecondaryHandHoldable sec,
            ArrayList<String> sfc, int num_coins, boolean is_alive
    ) {
        if (unchanged_characters != null) {
            compressed_characters_ = convertArrayListOfCharToArray(unchanged_characters);
        } else {
            compressed_characters_ = null;
        }
        if (unchanged_characters != null) {
            character_frequencies_ = convertArrayListOfShortToArray(character_frequencies);
        } else {
            character_frequencies_ = null;
        }
        if (unchanged_characters != null) {
            compressed_colors_ = new Color[compressed_colors.size()];
            compressed_colors_ = compressed_colors.toArray(compressed_colors_);
        } else {
            compressed_colors_ = null;
        }
        if (unchanged_characters != null) {
            color_frequencies_ = convertArrayListOfShortToArray(color_frequencies);
        } else {
            color_frequencies_ = null;
        }
        view_for_display_ = v;
        color_for_display_ = c;
        inventory_ = i;
        stats_for_display_ = s;
        occupation_ = o;
        num_skillpoints_ = n;
        bind_wounds_ = bi;
        bargain_ = ba;
        observation_ = ob;
        primary_ = pri;
        second_ = sec;
        strings_for_communication_ = sfc;
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

    /**
     * Uses run length decoding with repeatable characters "char[]
     * unchanged_characters" and frequencies "int[] unchanged_indexes."
     *
     * @param x_center
     * @param y_center
     * @param width_from_center
     * @param height_from_center
     * @param unchanged_characters - array of repeatable characters
     * @param frequencies - array of frequencies
     * @return
     */
    public static char[][] runLengthDecodeView(final int width_from_center,
            final int height_from_center, char[] unchanged_characters, short[] frequencies) {

        if (unchanged_characters != null && frequencies != null && (unchanged_characters.length == frequencies.length)) {
            char[][] view = new char[1 + 2 * height_from_center][1 + 2 * width_from_center];
            int unchanged_indexes_index = 0;
            int character_length_counter = frequencies[unchanged_indexes_index];
            int y_index = 0;
            for (int y = -height_from_center; y <= +height_from_center; ++y) {
                int x_index = 0;
                for (int x = 0 - width_from_center; x <= 0 + width_from_center; ++x) {
                    view[y_index][x_index] = unchanged_characters[unchanged_indexes_index];
                    --character_length_counter;
                    if (character_length_counter == 0) {
                        ++unchanged_indexes_index;
                        if (unchanged_indexes_index == frequencies.length) {
                            return view;
                        }
                        character_length_counter = frequencies[unchanged_indexes_index];
                    }
                    if (character_length_counter < 0) {
                        System.err.println("Impossible error in runLengthDecodeView");
                        System.exit(14);
                    }
                    ++x_index;
                }
                ++y_index;
            }
        } else {
            System.err.println("Precondition violated in runLengthDecodeView");
            System.exit(-18);
            return null;
        }
        System.err.println("You shouldn't get this far in runLengthDecodeView");
        System.exit(-4);
        return null;
    }

    public static Color[][] runLengthDecodeColor(final int width_from_center,
            final int height_from_center, Color[] unchanged_characters, short[] frequencies) {

        if (unchanged_characters != null && frequencies != null && unchanged_characters.length == frequencies.length) {
            Color[][] view = new Color[1 + 2 * height_from_center][1 + 2 * width_from_center];
            int unchanged_indexes_index = 0;
            int character_length_counter = frequencies[unchanged_indexes_index];
            int y_index = 0;
            for (int y = 0 - height_from_center; y <= 0 + height_from_center; ++y) {
                int x_index = 0;
                for (int x = 0 - width_from_center; x <= 0 + width_from_center; ++x) {
                    view[y_index][x_index] = unchanged_characters[unchanged_indexes_index];
                    --character_length_counter;
                    if (character_length_counter == 0) {
                        ++unchanged_indexes_index;
                        if (unchanged_indexes_index == frequencies.length) {
                            return view;
                        }
                        character_length_counter = frequencies[unchanged_indexes_index];
                    } else if (character_length_counter > 0) {
                        // keep going
                    } else {
                        System.err.println("Impossible error in runLengthDecodeView");
                        System.exit(14);
                    }
                    ++x_index;
                }
                ++y_index;
            }
        } else {
            System.err.println("Precondition violated in runLengthDecodeView");
            System.exit(-18);
            return null;
        }
        System.err.println("You shouldn't get this far in runLengthDecodeView");
        System.exit(-4);
        return null;
    }

    public final char[] compressed_characters_;
    public final short[] character_frequencies_;
    public Color[] compressed_colors_;
    public final short[] color_frequencies_;
    public char[][] view_for_display_;
    public Color[][] color_for_display_;
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
