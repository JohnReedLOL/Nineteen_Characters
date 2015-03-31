package src.model.constructs;

import java.util.Random;

import src.Effect;
import src.HardCodedStrings;
import src.io.view.display.Display;
import src.model.constructs.items.SpreadingCircleAreaEffectItem;
import src.model.constructs.items.SpreadingConeAreaEffectItem;
import src.model.constructs.items.SpreadingLineAreaEffectItem;

public class SummonerRookie extends Summoner {

    public int getID() { return 24; }

    public SummonerRookie(Entity e) {
        super(e);
    }

    public SummonerRookie(Occupation o) {
        super(o);
    }
    public SummonerRookie getACopyOfMyself() {
        return new SummonerRookie(this);
    }
	
    @Override
    public String getSkillNameFromNumber(int skill_number) {
        super.getSkillNameFromNumber(skill_number); // checks input
        switch (skill_number) {
            case 1:
                return "Confuse";
            case 2:
                return "Heal";
            case 3:
                return "Burn";
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
        final int cost = 1;
        System.out.println("Starting skill 2: DEBUG");
        int has_run_out_of_mana = getEntity().getStatsPack().getCurrent_mana_();
        if (number != 4)
        	has_run_out_of_mana = getEntity().getStatsPack().deductCurrentManaBy(cost);
        Entity target = super.getEntity().getMapRelation().getEntityInFacingDirection(5+getEntity().getStatsPack().getIntellect_level_());
        if (has_run_out_of_mana == 0) {
            if (number == 1) {
                // enchantment, target hurts itself [damage caster on fail]
                Random randomGenerator = new Random();
                double failed = randomGenerator.nextDouble();
                if (failed + getSkill_1_()*.1 < .5) {
                    super.getEntity().receiveAttack(getSkill_1_(), null); // hurt myself by skill1
                    Display.getDisplay().setMessage(HardCodedStrings.hurtYourself);
                } else {
                    if (target != null) {
                        target.receiveAttack(target.getStatsPack().getOffensive_rating_(), super.getEntity());
                        Display.getDisplay().setMessage("You casted " + getSkillNameFromNumber(1) + ".");
                    } else {
                        // get your mana back
                        getEntity().getStatsPack().increaseCurrentManaBy(cost);
                        Display.getDisplay().setMessage("No Valid Target! Got your mana back!");
                    }
                }
            } else if (number == 2) {
                // boon - magic that heals
                Random randomGenerator = new Random();
                double failed = randomGenerator.nextDouble();
                if (failed + getSkill_2_()*.1 > .3) {
                	SpreadingCircleAreaEffectItem healer = new SpreadingCircleAreaEffectItem(2 + getSkill_2_(), 4 + getSkill_4_()*2, Effect.HEAL);
                    super.getEntity().getMapRelation().addItem(healer, getEntity().getMapRelation().getMyXCoordinate(), getEntity().getMapRelation().getMyYCoordinate());
                    Display.getDisplay().setMessage("You casted " + getSkillNameFromNumber(2) + ".");
                } else {
                    Display.getDisplay().setMessage(HardCodedStrings.failed + getSkillNameFromNumber(2) + ".");
                }
            } else if (number == 3) {
                // bane - magic that does damage or harm.
                System.out.println("About to call Bane");
                Random randomGenerator = new Random();
                double failed = randomGenerator.nextDouble();
                if (failed + getSkill_3_()*.1 > .3) {
                	SpreadingConeAreaEffectItem hurter = new SpreadingConeAreaEffectItem(getSkill_3_() + 3, 2 + 2 * getSkill_3_(), Effect.HURT, super.getEntity().getFacingDirection());
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
		return new SummonerChampion(this);
	}
	
}
