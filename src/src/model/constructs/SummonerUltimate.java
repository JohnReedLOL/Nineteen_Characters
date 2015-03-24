package src.model.constructs;

import java.io.Serializable;
import java.util.Random;

import src.Effect;
import src.FacingDirection;
import src.HardCodedStrings;
import src.io.view.display.Display;
import src.model.constructs.items.SpreadingCircleAreaEffectItem;
import src.model.constructs.items.SpreadingLineAreaEffectItem;

public class SummonerUltimate extends Summoner implements Serializable {

    /**
	 * 
	 */
	//private static final long serialVersionUID = -306813571940934809L;

	private static final long serialVersionUID = 26L;
    @Override
    public int getID() { return (int)serialVersionUID; }

    public SummonerUltimate(Entity e) {
        super(e);
    }

    public SummonerUltimate(Occupation o) {
        super(o);
    }

    public SummonerUltimate getACopyOfMyself() {
        return new SummonerUltimate(this);
    }

    @Override
    public String getSkillNameFromNumber(int skill_number) {
        super.getSkillNameFromNumber(skill_number); // checks input
        switch (skill_number) {
            case 1:
                return "Fear";
            case 2:
                return "Tank";
            case 3:
                return "Explode";
            case 4:
                return "Staff";
            default:
                System.err.println("Error in Summoner");
                System.exit(-54);
                return "";
        }
    }

    @Override
    public int performOccupationSkill(int number) {
        if (number <= 0 || number > 4) {
            System.err.println("Error in Summoner.performOccupationSkill()");
            System.exit(-109);
        }
        final int cost = 3;
        System.out.println("Starting skill 2: DEBUG");
        int has_run_out_of_mana = getEntity().getStatsPack().deductCurrentManaBy(cost);
        Entity target = super.getEntity().getMapRelation().getEntityInFacingDirection(5+getEntity().getStatsPack().getIntellect_level_());
        if (has_run_out_of_mana == 0) {
            if (number == 1) {
                // enchantment, puts target to sleep (stops them from chasing until more damage is taken)
                Random randomGenerator = new Random();
                double failed = randomGenerator.nextDouble();
                if (failed + getSkill_1_() * .1 > .3) {
                    if (target != null) {
                        ((Monster) target).causeFear(super.getEntity(), (3 + getSkill_1_())*2);
                        Display.getDisplay().setMessage("Succesfully casted Fear!");
                    } else {
                        // get your mana back
                        getEntity().getStatsPack().increaseCurrentManaBy(cost);
                        Display.getDisplay().setMessage("Spell Succeeded, but not a monster! The entity was "
                        		+ "kind enough to return your mana to you");
                    }
                } else {
                	if(target ==null){
                		Display.getDisplay().setMessage("NO VALID TARGET");
                	}else{
                		Display.getDisplay().setMessage("SPELL FAILED");
                		target.receiveAttack(0, super.getEntity());
                		Display.getDisplay().setMessage(HardCodedStrings.failed + getSkillNameFromNumber(1) + ".");
                	}
                }
            } else if (number == 2) {
                // tank - magic that increases some stats
                Random randomGenerator = new Random();
                double failed = randomGenerator.nextDouble();
                if (failed + getSkill_2_() * .1 > .4) {
                	Display.getDisplay().setMessage("You casted Tank!");
                    EntityStatsPack boon_stats = new EntityStatsPack();
                    for (int i = 0; i < 4 + getSkill_2_() * 2; i++) {
                        boon_stats.increaseDefenseLevelByOne();
                    }
                    for (int i = 0; i < 4 + getSkill_2_() * 2; i++) {
                        boon_stats.increaseHardinessLevelByOne();
                    }
                    if (boon_stats_ == null)
                    	super.boon_stats_ = boon_stats;
                    else
                    	super.boon_stats_.addOn(boon_stats);
                    super.getEntity().getStatsPack().addOn(boon_stats_);
                    super.boon_timer_ = (3 + getSkill_2_())*2;
                    /*if(!isBoonActivated())
                    	super.getEntity().getStatsPack().addOn(boon_stats_);
                    activateBoon();*/

                } else {
                    Display.getDisplay().setMessage(HardCodedStrings.failed + getSkillNameFromNumber(2) + ".");
                }
            } else if (number == 3) {
                // bane - magic that does damage or harm.
                System.out.println("About to call Bane");
                Random randomGenerator = new Random();
                double failed = randomGenerator.nextDouble();
                if (failed + getSkill_3_() * .1 > .3) {
                    SpreadingCircleAreaEffectItem hurter = new SpreadingCircleAreaEffectItem(getSkill_3_() + 2, 6 + 6 * getSkill_3_(), Effect.HURT);
                    super.getEntity().getMapRelation().addItem(hurter, getEntity().getMapRelation().getMyXCoordinate(), getEntity().getMapRelation().getMyYCoordinate());
                    SpreadingLineAreaEffectItem hurter_down_left = new SpreadingLineAreaEffectItem(6, 4 + getSkill_3_(), Effect.HURT, FacingDirection.DOWN_LEFT);
                    super.getEntity().getMapRelation().addItem(hurter_down_left, getEntity().getMapRelation().getMyXCoordinate(), getEntity().getMapRelation().getMyYCoordinate());
                    SpreadingLineAreaEffectItem hurter_down_right = new SpreadingLineAreaEffectItem(6, 4 + getSkill_3_(), Effect.HURT, FacingDirection.DOWN_RIGHT);
                    super.getEntity().getMapRelation().addItem(hurter_down_right, getEntity().getMapRelation().getMyXCoordinate(), getEntity().getMapRelation().getMyYCoordinate());
                    SpreadingLineAreaEffectItem hurter_up_left = new SpreadingLineAreaEffectItem(6, 4 + getSkill_3_(), Effect.HURT, FacingDirection.UP_LEFT);
                    super.getEntity().getMapRelation().addItem(hurter_up_left, getEntity().getMapRelation().getMyXCoordinate(), getEntity().getMapRelation().getMyYCoordinate());
                    SpreadingLineAreaEffectItem hurter_up_right = new SpreadingLineAreaEffectItem(6, 4 + getSkill_3_(), Effect.HURT, FacingDirection.UP_RIGHT);
                    super.getEntity().getMapRelation().addItem(hurter_up_right, getEntity().getMapRelation().getMyXCoordinate(), getEntity().getMapRelation().getMyYCoordinate());
                } else {
                    Display.getDisplay().setMessage(HardCodedStrings.failed + getSkillNameFromNumber(3) + ".");
                }
                System.out.println("Finished calling Bane");
            }
        } else if (number == 4) {
            // Staff attack
            Random randomGenerator = new Random();
            double failed = randomGenerator.nextDouble();
            if (failed + getSkill_4_()*.1 > .4) {
                if (staff_ != null) {
                	if (target != null) {
	                	target.receiveAttack(getSkill_4_() * 2, super.getEntity());
	                    Display.getDisplay().setMessage("You whacked it with your staff.");
                	}
                } else {
                    Display.getDisplay().setMessage("You have no staff.");
                }
            } else {
                Display.getDisplay().setMessage(HardCodedStrings.failed + getSkillNameFromNumber(4) + ".");
            }
        } else {
            System.out.println("Out of mana");
        }
        return 0;
    }

    @Override
    public Summoner switchToNextSubOccupation() {
        return new SummonerRookie(this);
    }

}
