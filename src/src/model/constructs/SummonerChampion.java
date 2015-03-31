package src.model.constructs;

import java.util.Random;

import src.Effect;
import src.HardCodedStrings;
import src.io.view.display.Display;
import src.model.constructs.items.SpreadingCircleAreaEffectItem;
import src.model.constructs.items.SpreadingConeAreaEffectItem;
import src.model.constructs.items.SpreadingLineAreaEffectItem;

public class SummonerChampion extends Summoner {

    public int getID() { return 25; }

    public SummonerChampion(Entity e) {
        super(e);
    }

    public SummonerChampion(Occupation o) {
        super(o);
    }

    public SummonerChampion getACopyOfMyself() {
        return new SummonerChampion(this);
    }

    @Override
    public String getSkillNameFromNumber(int skill_number) {
        super.getSkillNameFromNumber(skill_number); // checks input
        switch (skill_number) {
            case 1:
                return "Sleep";
            case 2:
                return "Concentrated Heal";
            case 3:
                return "Flammenwerfer";
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
        final int cost = 2;
        System.out.println("Starting skill 2: DEBUG");
        int has_run_out_of_mana = getEntity().getStatsPack().deductCurrentManaBy(cost);
        Entity target = super.getEntity().getMapRelation().getEntityInFacingDirection(5+getEntity().getStatsPack().getIntellect_level_());
        if (has_run_out_of_mana == 0) {
            if (number == 1) {
                // enchantment, puts target to sleep (stops them from chasing until more damage is taken)
                Random randomGenerator = new Random();
                double failed = randomGenerator.nextDouble();
                if (failed + getSkill_1_() * .1 > .6) {
                    if (target != null) {
                        try {
                            ((Monster) target).stopFollowing();
                            Display.getDisplay().setMessage("Suceeded in casting!");
                        } catch (Exception e) {
                        	Display.getDisplay().setMessage("No Target isn't a monster!");
                        }
                    } else {
                        // get your mana back
                        getEntity().getStatsPack().increaseCurrentManaBy(cost);
                    	Display.getDisplay().setMessage("No valid Target! Got your mana back!");
                    }
                } else {
                	if(target!=null){
                		target.receiveAttack(0, super.getEntity());
                		Display.getDisplay().setMessage(HardCodedStrings.failed + getSkillNameFromNumber(1) + ".");
                	}else{
                		Display.getDisplay().setMessage("No valid Target!");
                	}
                }
            } else if (number == 2) {
                // boon - magic that heals
                Random randomGenerator = new Random();
                double failed = randomGenerator.nextDouble();
                if (failed + getSkill_2_() * .1 > .4) {
                	SpreadingCircleAreaEffectItem healer = new SpreadingCircleAreaEffectItem(1, 4 + getSkill_2_() * 4, Effect.HEAL);
                    super.getEntity().getMapRelation().addItem(healer, getEntity().getMapRelation().getMyXCoordinate(), getEntity().getMapRelation().getMyYCoordinate());
                } else {
                    Display.getDisplay().setMessage(HardCodedStrings.failed + getSkillNameFromNumber(2) + ".");
                }
            } else if (number == 3) {
                // bane - magic that does damage or harm.
                System.out.println("About to call Bane");
                Random randomGenerator = new Random();
                double failed = randomGenerator.nextDouble();
                if (failed + getSkill_3_() * .1 > .5) {
                    SpreadingLineAreaEffectItem hurter = new SpreadingLineAreaEffectItem(getSkill_3_() + 4, 2 + 4 * getSkill_3_(), Effect.HURT, super.getEntity().getFacingDirection());
                    super.getEntity().getMapRelation().addItem(hurter, getEntity().getMapRelation().getMyXCoordinate(), getEntity().getMapRelation().getMyYCoordinate());
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
                if (staff_ != null && target != null) {
                	target.receiveAttack(getSkill_4_() * 2, super.getEntity());
                    Display.getDisplay().setMessage("You whacked it with your staff.");
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
        return new SummonerUltimate(this);
    }

}
