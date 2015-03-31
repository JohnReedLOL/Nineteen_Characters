/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package src.model.constructs;

import java.util.Random;

import src.Effect;
import src.SkillEnum;
import src.model.constructs.items.OneHandedWeapon;
import src.model.constructs.items.Staff;
import src.model.constructs.items.TwoHandedWeapon;

/**
 * Summoner Occupation, intellect +1.
 */
public abstract class Summoner extends Occupation {

	protected int boon_timer_ = 0; //Timer for temporary boon skill.
    protected EntityStatsPack boon_stats_ = null;
    
    public Summoner(Entity e) {
        super(e);
    }

    public Summoner(Occupation o) {
        super(o);
    }
    
    protected Staff staff_ = null;

    @Override
    public void changeStats(EntityStatsPack current_stats) {
        //for sneak
        current_stats.increaseIntellectLevelByOne();
    }

    @Override
    public int equipOneHandWeapon(OneHandedWeapon weapon) {
        try {
            staff_ = (Staff) weapon;
            return 0;
        } catch (ClassCastException e) {
            return -1;
        }
    }

    public int getBoonTimer() { return boon_timer_; }

    @Override
    public int unEquipEverything() {
        staff_ = null;
        return 0;
    }

    @Override
    public int equipTwoHandWeapon(TwoHandedWeapon weapon) {
        return -1;
    }

    /**
     * Increments an occupation specific skill. Caller should decrement number
     * of skillpoints.
     *
     * @author John-Michael Reed
     * @param skill Which skill to increment
     * @return 0 on success, -1 if this occupation cannot increment this skill.
     */
    @Override
    public int incrementSkill(SkillEnum skill) {
        if (skill == SkillEnum.OCCUPATION_SKILL_1) {
            super.incrementSkill_1_();
            return 0;
        } else if (skill == SkillEnum.OCCUPATION_SKILL_2) {
            super.incrementSkill_2_();
            return 0;
        } else if (skill == SkillEnum.OCCUPATION_SKILL_3) {
            super.incrementSkill_3_();
            return 0;
        } else if (skill == SkillEnum.OCCUPATION_SKILL_4) {
            super.incrementSkill_4_();
            return 0;
        } else {
            System.exit(-1);
            return -99999; // F***ing impossible
        }
    }

    @Override
    public String toString() {
        return "Summoner";
    }
    
    
    private boolean boonActivated = false;
    public void deActivateBoon(){
    	boonActivated = false;
    }
    public void activateBoon(){
    	boonActivated = true;
    }
    public boolean isBoonActivated(){
    	return boonActivated;
    }
    @Override
    public void takeTurn(){
    	if (boon_stats_ != null) {
    		if (boon_timer_ > 0) {
    			boon_timer_--;
    		} else {
    			deActivateBoon();
   				super.getEntity().getStatsPack().reduceBy(boon_stats_);
   				boon_stats_ = null;
   			}
    	}
    	
    }

	@Override
	public abstract Summoner switchToNextSubOccupation();

	@Override
	public char getOccupationRepresentation() {
		return '☃';
	}
}
