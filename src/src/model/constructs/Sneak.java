/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package src.model.constructs;

import java.util.Random;

import src.Effect;
import src.FacingDirection;
import src.SkillEnum;
import src.io.view.display.Display;
import src.model.constructs.items.Bow;
import src.model.constructs.items.OneHandedWeapon;
import src.model.constructs.items.SpreadingLineAreaEffectItem;
import src.model.constructs.items.Trap;
import src.model.constructs.items.TwoHandedWeapon;

/**
 * Sneak Occupation, agility +1.
 */
public final class Sneak extends Occupation {

    public int getID() { return 23; }

    public Sneak(Entity e) {
        super(e);
    }

    public Sneak(Occupation o) {
        super(o);
    }

    public Sneak getACopyOfMyself() {
        return new Sneak(this);
    }

    private Bow bow_ = null;

    private int cloak_timer = 0; //Timer for cloak skill.

    @Override
    public void changeStats(EntityStatsPack current_stats) {
        // for sneak
        current_stats.increaseAgilityLevelByOne();

    }

    public int getCloakTimer() { return cloak_timer; }

    @Override
    public int equipOneHandWeapon(OneHandedWeapon weapon) {
        return -1;
    }

    @Override
    public int unEquipEverything() {
        bow_ = null;
        return 0;
    }

    @Override
    public int equipTwoHandWeapon(TwoHandedWeapon weapon) {
        try {
            bow_ = (Bow) weapon;
            return 0;
        } catch (ClassCastException e) {
            return -1;
        }
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
    public String getSkillNameFromNumber(int skill_number) {
        super.getSkillNameFromNumber(skill_number); // checks input
        switch (skill_number) {
            case 1:
                return "Steal";
            case 2:
                return "Detect";
            case 3:
                return "Cloak";
            case 4:
                return "Bow";
            default:
                System.err.println("Error in Sneak");
                System.exit(-57);
                return "";
        }
    }

    @Override
    public int performOccupationSkill(int number) {
        if (number <= 0 || number > 4) {
            System.err.println("Error in Sneak.performOccupationSkill()");
            System.exit(-68);
        }
        final int cost = 1;
        int has_run_out_of_mana = getEntity().getStatsPack()
                .deductCurrentManaBy(cost);
        Random randomGenerator = new Random();
        if (has_run_out_of_mana == 0) {
            if (number == 1) {
                Entity target = getEntity().getMapRelation()
                        .getEntityInFacingDirection();
                final int chance_to_steal = randomGenerator.nextInt(10)
                        + super.getSkill_1_();
                if (chance_to_steal < 5) {
                    // fail to steal
                    System.out.println("Failed to steal.");
                } else {
                    if (target != null) {
                        int money = target.getNumGoldCoins();
                        getEntity().incrementNumGoldCoinsBy(money);
                        target.decrementNumGoldCoinsBy(money);
                        Display.getDisplay().setMessage(
                                "Successful pickpocket.");
                    } else {
                        Display.getDisplay().setMessage(
                                "No one to pick pocket!");
                    }
                }
            } else if (number == 2) {
                // detect & remove trap
                Trap trap;
                int numOfTraps = 0;
                int numOfTrapsDetected = 0;

                int chance_of_detection;

                // Double for loop to go through tiles around Entity with a radius of the detectLevel (skill_2).
                for (int i = getEntity().getMapRelation().getMyXCoordinate()
                        - super.getSkill_2_(); i <= getEntity().getMapRelation()
                        .getMyXCoordinate() + super.getSkill_2_(); ++i) {
                    for (int j = getEntity().getMapRelation()
                            .getMyYCoordinate() + super.getSkill_2_(); j >= getEntity()
                            .getMapRelation().getMyYCoordinate()
                            - super.getSkill_2_(); --j) {

                        //Check the tile of trap.
                        trap = getEntity().getMapRelation().checkForTrap(i, j);

                        if (trap != null) {
                            // If you get here, there is a trap on tile (i, j).

                            ++numOfTraps;
                            chance_of_detection = randomGenerator
                                    .nextInt(10) + super.getSkill_2_();
                            if (chance_of_detection < 5) {
                                // failed to detect the trap
                                System.out.println("Failed to detect trap.");
                            } else {
                                // detected the trap
                                ++numOfTrapsDetected;
                                System.out.println("Trap detected on: " + i
                                        + ", " + j + ".");
                                trap.onWalkOver();
                                //trap.removeMyselfFromMap();
                            }
                        }
                    }
                }
                Display.getDisplay().setMessage(
                        numOfTraps + " traps near you, " + numOfTrapsDetected + " removed.");
            } else if (number == 3) {
                // become invisible [or visible]
                cloak_timer = 0;
                boolean is_visible = getEntity().isVisible();
                getEntity().setViewable(!is_visible);
            } else if (number == 4) {
                // Bow attack
                Entity target = super.getEntity().getMapRelation().getEntityInFacingDirection();
                if (bow_ != null && target != null) {
                    target.receiveAttack(0, super.getEntity()); // hurt enemy [no attack-back]
                }

                SpreadingLineAreaEffectItem arrow = new SpreadingLineAreaEffectItem(12 + getSkill_4_()*4, 2 + getSkill_4_()*2, Effect.HURT, super.getEntity().getFacingDirection());
                super.getEntity().getMapRelation().addItem(arrow, getEntity().getMapRelation().getMyXCoordinate(), getEntity().getMapRelation().getMyYCoordinate());
            }
        } else {
            Display.getDisplay().setMessage("You are out of mana");
        }
        return 0;
    }

    @Override
    public String toString() {
        return "Sneak";
    }

    @Override
    public void takeTurn() {

        if (!getEntity().isVisible()) {
            ++cloak_timer;
        }
        if (cloak_timer > 5) {
            cloak_timer = 0;
            getEntity().setViewable(true);
        }
    }

    @Override
    public Sneak switchToNextSubOccupation() {
        //A sneak has no sub occupations atm.
        return this;
    }

    @Override
    public char getOccupationRepresentation() {
        return '☭';
    }
}
