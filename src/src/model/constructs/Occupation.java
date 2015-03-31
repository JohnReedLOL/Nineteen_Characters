/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package src.model.constructs;

import java.io.Serializable;
import java.util.ArrayList;

import src.SkillEnum;
import src.model.constructs.items.OneHandedWeapon;
import src.model.constructs.items.TwoHandedWeapon;

/**
 * Interface for Occupations (Smasher, Sneak, Summoner). Different Occupations
 * have different advantages.
 *
 * @author JohnReedLOL
 */
public abstract class Occupation implements Serializable {

    public abstract int getID();

	/**
	 * The character that represents this occupation.
	 * @return
	 */
	public abstract char getOccupationRepresentation();
    private final transient Entity occupation_holder_;

    protected Entity getEntity() {
        return occupation_holder_;
    }
    public abstract Occupation getACopyOfMyself();

    private Occupation() {
        occupation_holder_ = null;
    }

    public Occupation(Entity occupation_holder) {
        occupation_holder_ = occupation_holder;
    }

    public Occupation(Occupation old) {
        occupation_holder_ = old.occupation_holder_;
        skill_1_level_ = old.getSkill_1_();
        skill_2_level_ = old.getSkill_2_();
        skill_3_level_ = old.getSkill_3_();
        skill_4_level_ = old.getSkill_4_();
    }
    /**
     * Switch to the next sub occupation.
     * For summoner, does moves to novice, champion, ultimate
     * The other occupations do not have subclasses atm. 
     * @return
     */
    public abstract Occupation switchToNextSubOccupation();
    //private int[] skills_levels_ = {1, 1, 1, 1};
    private int skill_1_level_ = 1;
    private int skill_2_level_ = 1;
    private int skill_3_level_ = 1;
    private int skill_4_level_ = 1;
    //This should really use the enum....

    /**
     * Goes from one to four
     *
     * @param number
     * @return
     */
    abstract public int performOccupationSkill(int number);

    public int getSkill_1_() {
        return skill_1_level_;
    }

    public int getSkill_2_() {
        return skill_2_level_;
    }

    public int getSkill_3_() {
        return skill_3_level_;
    }

    public int getSkill_4_() {
        return skill_4_level_;
    }

    public int incrementSkill_1_() {
        return ++skill_1_level_;
    }

    public int incrementSkill_2_() {
        return ++skill_2_level_;
    }

    public int incrementSkill_3_() {
        return ++skill_3_level_;
    }

    public int incrementSkill_4_() {
        return ++skill_4_level_;
    }

    /**
     * @author John-Michael Reed
     * @param weapon weapon to be equipped
     * @return 0 on success, -1 on failure
     */
    public abstract int equipOneHandWeapon(OneHandedWeapon weapon);

    public abstract int equipTwoHandWeapon(TwoHandedWeapon weapon);

    public abstract int unEquipEverything();

    /**
     * Used to level-up an entity's stats in an occupation specific way.
     * @param current_stats 
     */
    public abstract void changeStats(EntityStatsPack current_stats);

    public abstract int incrementSkill(SkillEnum skill);

    /**
     * Must be called before subclass getSkillNameFromNumber is called.
     * @param skill_number
     * @return 
     */
    public String getSkillNameFromNumber(int skill_number) {
        if (skill_number <= 0 || skill_number > 4) {
            System.err.println("Error in Occupation.getSkillsName(int skill_number)");
            System.exit(-108);
        }
        return "";
    }
    public ArrayList<byte[]> makeByteArray(){
        ArrayList<byte[]> arrayList = new ArrayList<byte[]>();
        arrayList.add(Integer.toString(skill_1_level_).getBytes());
        arrayList.add(Integer.toString(skill_2_level_).getBytes());
        arrayList.add(Integer.toString(skill_3_level_).getBytes());
        arrayList.add(Integer.toString(skill_4_level_).getBytes());
        return arrayList;
    }
    
    public abstract void takeTurn();
}