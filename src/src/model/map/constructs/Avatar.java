/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package src.model.map.constructs;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Random;

import src.CompassEnum;
import src.CharacterCreationEnum;
import src.FacingDirection;
import src.InteractEnum;
import src.RunGame;
import src.SavedGame;
import src.SkillEnum;
import src.model.map.MapAvatar_Relation;
import src.io.view.AvatarCreationView;
import src.io.view.MapView;
import src.io.view.StatsView;
import src.io.view.Viewport;
import src.io.view.display.Display;

/**
 * Each avatar represents a player
 *
 * @author JohnReedLOL
 */
public final class Avatar extends Entity {

    // map_relationship_ is used in place of a map_referance_
    private MapView map_view_;
    private StatsView stats_view_;

    private int num_skillpoints_ = 1;

    public int getNum_skillpoints_() {
        return num_skillpoints_;
    }

    /**
     * Same as superclass except increases skillpoints
     *
     * @param amount
     * @return number of level ups;
     */
    @Override
    public int gainExperiencePoints(int amount) {
        final int num_level_ups = super.gainExperiencePoints(amount);
        num_skillpoints_ += num_level_ups;
        return num_level_ups;
    }

    // Non-occupation specific skills
    private int bind_wounds_ = 1;

    public int getBind_wounds_() {
        return bind_wounds_;
    }

    public int bindWounds() {
        return 0;
    }

    private int bargain_ = 1;

    public int getBargain_() {
        return bargain_;
    }

    private int observation_ = 1;

    public int getObservation_() {
        return observation_;
    }

    /**
     * Gets information based on observation level. If the entity is facing up,
     * observation will work in the up direction.
     *
     * @author Reid Olsen
     * @return
     */
    public int observe() {
        Random rn = new Random();

        String s = "";

        // Get random number between 0 and 10.
        int chanceForSuccessfulObserve = rn.nextInt(11);
        // Checks if observe is succuessful, takes observation level into
        // account. If observation level is 11 or higher, success rate is %100.
        if (chanceForSuccessfulObserve >= (11 - observation_)) {
            Display.getDisplay().setMessage(
                    "Looking in direction: " + getFacingDirection());

            if (getFacingDirection() == FacingDirection.UP) {
                for (int i = 0; i < observation_; ++i) {
                    s += " Tile " + (i + 1) + ": ";
                    try {
                        s += map_relationship_.getTileInfo(0, (i + 1));
                        s += "\n";
                    } catch (NullPointerException e) {
                        s += "No tile here.\n";
                    }
                }
                Display.getDisplay().setMessage(s);
            } else if (getFacingDirection() == FacingDirection.UP_RIGHT) {
                for (int i = 0; i < observation_; ++i) {
                    s += " Tile " + (i + 1) + ": ";
                    try {
                        s += map_relationship_.getTileInfo((i + 1), (i + 1));
                        s += "\n";
                    } catch (NullPointerException e) {
                        s += "No tile here.\n";
                    }
                }
                Display.getDisplay().setMessage(s);
            } else if (getFacingDirection() == FacingDirection.RIGHT) {
                for (int i = 0; i < observation_; ++i) {
                    s += " Tile " + (i + 1) + ": ";
                    try {
                        s += map_relationship_.getTileInfo((i + 1), 0);
                        s += "\n";
                    } catch (NullPointerException e) {
                        s += "No tile here.\n";
                    }
                }
                Display.getDisplay().setMessage(s);
            } else if (getFacingDirection() == FacingDirection.DOWN_RIGHT) {
                for (int i = 0; i < observation_; ++i) {
                    s += " Tile " + (i + 1) + ": ";
                    try {
                        s += map_relationship_.getTileInfo((i + 1), (i + 1));
                        s += "\n";
                    } catch (NullPointerException e) {
                        s += "No tile here.\n";
                    }
                }
                Display.getDisplay().setMessage(s);
            } else if (getFacingDirection() == FacingDirection.DOWN) {
                for (int i = 0; i < observation_; ++i) {
                    s += " Tile " + (i + 1) + ": ";
                    try {
                        s += map_relationship_.getTileInfo(0, (i + 1));
                        s += "\n";
                    } catch (NullPointerException e) {
                        s += "No tile here.\n";
                    }
                }
                Display.getDisplay().setMessage(s);
            } else if (getFacingDirection() == FacingDirection.DOWN_LEFT) {
                for (int i = 0; i < observation_; ++i) {
                    s += " Tile " + (i + 1) + ": ";
                    try {
                        s += map_relationship_.getTileInfo((i + 1), (i + 1));
                        s += "\n";
                    } catch (NullPointerException e) {
                        s += "No tile here.\n";
                    }
                }
                Display.getDisplay().setMessage(s);
            } else if (getFacingDirection() == FacingDirection.LEFT) {
                for (int i = 0; i < observation_; ++i) {
                    s += " Tile " + (i + 1) + ": ";
                    try {
                        s += map_relationship_.getTileInfo((i + 1), 0);
                        s += "\n";
                    } catch (NullPointerException e) {
                        s += "No tile here.\n";
                    }
                }
                Display.getDisplay().setMessage(s);
            } else if (getFacingDirection() == FacingDirection.UP_LEFT) {
                for (int i = 0; i < observation_; ++i) {
                    s += " Tile " + (i + 1) + ": ";
                    try {
                        s += map_relationship_.getTileInfo((i + 1), (i + 1));
                        s += "\n";
                    } catch (NullPointerException e) {
                        s += "No tile here.\n";
                    }
                }
                Display.getDisplay().setMessage(s);
            }
            return 0;
        } else {
            Display.getDisplay().setMessage(
                    "Failed to look in direction: " + getFacingDirection());
            return -1;
        }
    }

    /**
     * Designates a skill point towards a skill.
     *
     * @author John-Michael Reed
     * @param skill
     * @return -2 if no skill points, -1 if skill cannot be spent [invalid
     * occupation]
     */
    public int spendSkillpointOn(SkillEnum skill) {
        if (num_skillpoints_ <= 0) {
            return -2;
        }
        Occupation occupation = this.getOccupation();
        switch (skill) {
            case BIND_WOUNDS:
                ++bind_wounds_;
                --num_skillpoints_;
                return 0;
            case BARGAIN:
                ++bargain_;
                --num_skillpoints_;
                return 0;
            case OBSERVATION:
                ++observation_;
                --num_skillpoints_;
                return 0;
            case OCCUPATION_SKILL_1:
                if (occupation == null) {
                    return -1;
                }
                int error_code = occupation.incrementSkill(skill);
                if (error_code == 0) {
                    --num_skillpoints_;
                }
                return error_code;
            case OCCUPATION_SKILL_2:
                if (occupation == null) {
                    return -1;
                }
                int error_code2 = occupation.incrementSkill(skill);
                if (error_code2 == 0) {
                    --num_skillpoints_;
                }
                return error_code2;
            case OCCUPATION_SKILL_3:
                if (occupation == null) {
                    return -1;
                }
                int error_code3 = occupation.incrementSkill(skill);
                if (error_code3 == 0) {
                    --num_skillpoints_;
                }
                return error_code3;
            case OCCUPATION_SKILL_4:
                if (occupation == null) {
                    return -1;
                }
                int error_code4 = occupation.incrementSkill(skill);
                if (error_code4 == 0) {
                    --num_skillpoints_;
                }
                return error_code4;
            default:
                System.exit(-1); // should never happen
                return -3;
        }
    }

    /**
     * Accepts a key command from the map
     *
     * @param command
     * @return ArrayList of strings for IO_Bundle or null if nothing to display
     */
    public ArrayList<String> acceptKeyCommand(Enum command) {
        MapAvatar_Relation mar = this.getMapRelation();
        if (mar == null) {
            System.out
                    .println("Avatar cannot be controlled without a MapAvatar_Relation");
            System.exit(-8);
        }
        for (CompassEnum direction : CompassEnum.values()) {
            if (direction.equals(command)) {
                switch (direction) {
                    case SOUTH_WEST:// Move SW
                        mar.moveInDirection(-1, -1);
                        return null;
                    case SOUTH:// Move S
                        mar.moveInDirection(0, -1);
                        return null;
                    case SOUTH_EAST:// Move SE
                        mar.moveInDirection(1, -1);
                        return null;
                    case WEST: // Move W
                        mar.moveInDirection(-1, 0);
                        return null;
                    case EAST:// Move E
                        mar.moveInDirection(1, 0);
                        return null;
                    case NORTH_WEST:// Move NW
                        mar.moveInDirection(-1, 1);
                        return null;
                    case NORTH:// Move N
                        mar.moveInDirection(0, 1);
                        return null;
                    case NORTH_EAST: // Move NE
                        mar.moveInDirection(1, 1);
                        return null;
                    default:
                        System.err.println("Error in the avatar direction enum switch");
                        return null;
                }
            }
        }
        for (InteractEnum interact : InteractEnum.values()) {
            if (interact.equals(command)) {
                switch (interact) {

                    case SAVE_GAME: // Save game
                        RunGame.saveGameToDisk(); // TODO: this is for testing, remove for
                        // deployment
                        return null;
                    case USE_ITEM: // Use item in inventory
                        this.useLastInventoryItem();
                        return null;
                    case EQUIP: // equipMyselfTo
                        try {
                            EquipableItem item = (EquipableItem) this.getLastItemInInventory();
                            if (item != null) {
                                Display.getDisplay().setMessage("Attempted to Equip " + item.toString());
                            } else {
                                Display.getDisplay().setMessage("No item(s) to equip");
                            }
                            if (item != null) {
                                item.equipMyselfTo(this);
                                return null;
                            }
                        } catch (ClassCastException e) {
                            // ignore it
                            Display.getDisplay().setMessage("Cannot Equip From Inventory");
                        }
                        return null;
                    case UNEQUIP: // unEquip
                        this.unEquipEverything();
                        Display.getDisplay().setMessage("Unequipped Everything");
                        return null;
                    case DROP: // drop item
                        int error_code_D = mar.dropItem();
                        return null;
                    case PICK_UP:// pickup item
                        int error_code_p = mar.pickUpItemInDirection(0, 0);
                        return null;
                    default:
                        System.err.println("Error in the avatar interact enum switch");
                        return null;
                }
            }
        }
        for (CharacterCreationEnum cc : CharacterCreationEnum.values()) {
            if (cc.equals(command)) {
                switch (cc) {
                    case SMASHER: // switch to Smasher
                        this.setRepresentation('⚔');
                        this.becomeSmasher();
                        return null;
                    case SUMMONER: // switch to Summoner
                        this.setRepresentation('☃');
                        this.becomeSummoner();
                        return null;
                    case SNEAKER: // switch to Sneaker
                        this.setRepresentation('☭');
                        this.becomeSneak();
                        return null;
                    default:
                        System.err.println("Error in the avatar creation enum switch");
                        return null;
                }
            }
        }
        for (SkillEnum skill : SkillEnum.values()) {
            if (skill.equals(command)) {
                switch (skill) {
                    case OBSERVATION:
                        this.observe();
                        return null;
                    default:
                        System.err.println("Error in the avatar skills enum switch");
                        return null;
                }
            }
        }
        return null;
    }

    public Avatar(String name, char representation) {
        super(name, representation);
    }

    // map_relationship_ is used in place of a map_reference_
    private MapAvatar_Relation map_relationship_;

    /**
     * Use this to call functions contained within the MapAvatar relationship
     *
     * @return map_relationship_
     * @author Reed, John
     */
    @Override
    public MapAvatar_Relation getMapRelation() {
        return map_relationship_;
    }

    /**
     * Sets MapAvatar_Relation
     *
     * @param a
     */
    public void setMapRelation(MapAvatar_Relation a) {
        map_relationship_ = a;
    }

    /*
     * Make sure to call set map after this!
     */
    /**
     * Avatars automatically do nothing when attacked
     *
     * @author John-Michael Reed
     * @param attacker
     * @return 0 if reply succeeded, non-zero otherwise [ex. if entity is null
     * or off the map]
     */
    @Override
    public int replyToAttackFrom(Entity attacker) {
        if (attacker == null) {
            return -1;
        }
        // return this.getMapRelation().sendAttack(attacker);
        return 0;
    }

    @Override
    public String toString() {
        String s = "Avatar name: " + name_;

        s += "\n Inventory " + "(" + inventory_.size() + ")" + ":";
        for (int i = 0; i < inventory_.size(); ++i) {
            s += " " + inventory_.get(i).name_;
        }

        s += "\n";

        s += " map_relationship_: ";
        if (map_relationship_ == null) {
            s += "null";
        } else {
            s += "Not null";
        }

        s += "\n associated with map:"
                + map_relationship_.isAssociatedWithMap();

        return s;
    }

}
