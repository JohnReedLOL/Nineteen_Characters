/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package src.model.map.constructs;

import java.util.LinkedList;
import java.util.Random;

import src.FacingDirection;
import src.RunGame;
import src.SavedGame;
import src.SkillEnum;
import src.model.map.MapAvatar_Relation;
import src.io.view.AvatarCreationView;
import src.io.view.Display;
import src.io.view.MapView;
import src.io.view.StatsView;
import src.io.view.Viewport;

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
    
    /**
     * Bargains with Merchant. 
     */
    public void Bargain(){
    	((Merchant)(map_relationship_.getMap().getTile(25,3).getEntity())).displayShop();
    }
    
    private int observation_ = 1;

    public int getObservation_() {
        return observation_;
    }

    /**
     * Gets information based on observation level. If the entity is facing up,
     * observation will work in the up direction.
     *
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
                        s += getTileInfo(map_relationship_.getMyXCoordinate(),
                                map_relationship_.getMyYCoordinate() + (i + 1));
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
                        s += getTileInfo(map_relationship_.getMyXCoordinate() + (i + 1),
                                map_relationship_.getMyYCoordinate() + (i + 1));
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
                        s += getTileInfo(map_relationship_.getMyXCoordinate() + (i + 1),
                                map_relationship_.getMyYCoordinate());
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
                        s += getTileInfo(map_relationship_.getMyXCoordinate() + (i + 1),
                                map_relationship_.getMyYCoordinate() - (i + 1));
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
                        s += getTileInfo(map_relationship_.getMyXCoordinate(),
                                map_relationship_.getMyYCoordinate() - (i + 1));
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
                        s += getTileInfo(map_relationship_.getMyXCoordinate() - (i + 1),
                                map_relationship_.getMyYCoordinate() - (i + 1));
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
                        s += getTileInfo(map_relationship_.getMyXCoordinate() - (i + 1),
                                map_relationship_.getMyYCoordinate());
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
                        s += getTileInfo(map_relationship_.getMyXCoordinate() - (i + 1),
                                map_relationship_.getMyYCoordinate() + (i + 1));
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
     * This function will be called from observe() to get info for a tile at
     * (x,y).
     *
     * @param x coordinate of tile.
     * @param y coordinate of tile.
     * @return String of info on tile (x,y).
     */
    private String getTileInfo(int x, int y) {
        String s = "";
        if (map_relationship_.getMap().getTile(x, y).isPassable()) {
            s += "This tile is passable.";
        } else {
            s += "This tile is not passable.";
        }
        LinkedList<Item> items = map_relationship_.getMap().getTile(x, y)
                .getItemList();
        if (!items.isEmpty()) {
            s += " Items on this tile:";
            for (int j = 0; j < items.size(); j++) {
                s += " " + items.get(j).name_;
                if (j + 1 == items.size()) {
                    s += ".";
                } else {
                    s += ",";
                }
            }
        }
        Entity e = map_relationship_.getMap().getTile(x, y).getEntity();
        if (e != null) {
            if (observation_ < 3) {
                s += " Entity: " + e.name_;
            } else if (observation_ >= 3 && observation_ < 6) {
                s += " Entity: " + e.name_ + " with "
                        + e.getStatsPack().getOffensive_rating_() + " offense.";
            } else {
                s += " Entity: " + e.name_ + " with "
                        + e.getStatsPack().getOffensive_rating_()
                        + " offense and "
                        + e.getStatsPack().getDefensive_rating_() + " defense.";
            }
        }

        return s;
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
     * @return 0 on success, not zero if command cannot be accepted
     */
    public int acceptKeyCommand(char command) {
        MapAvatar_Relation mar = this.getMapRelation();
        if (mar == null) {
            System.out
                    .println("Avatar cannot be controlled without a MapAvatar_Relation");
            System.exit(-8);
        }
        switch (command) {
            case '1':// Move SW
                mar.moveInDirection(-1, -1);
                break;
            case '2':// Move S
                mar.moveInDirection(0, -1);
                break;
            case '3':// Move SE
                mar.moveInDirection(1, -1);
                break;
            case '4': // Move W
                mar.moveInDirection(-1, 0);
                break;
            case '6':// Move E
                mar.moveInDirection(1, 0);
                break;
            case '7':// Move NW
                mar.moveInDirection(-1, 1);
                break;
            case '8':// Move N
                mar.moveInDirection(0, 1);
                break;
            case '9': // Move NE
                mar.moveInDirection(1, 1);
                break;
            case 'S': // Save game
                RunGame.saveGameToDisk(); // TODO: this is for testing, remove for
                // deployment
                break;
		// case 'v': //Open stats
            // break;
            // case 'i': //Use item in direction
            // switchToStatsView();
            // break;
            case 'u': // Use item in inventory
                int error_code_u = this.useLastInventoryItem();
                return error_code_u;
            case 'q':// move NW
                mar.moveInDirection(-1, 1);
                break;
            case 'w': // move N
                mar.moveInDirection(0, 1);
                break;
            case 'e':// move NE
                mar.moveInDirection(1, 1);
                break;
            case 'a': // move W
                mar.moveInDirection(-1, 0);
                break;
            case 's':// Move stationary?
                mar.moveInDirection(0, 0);
                break;
            case 'd':// Move E
                mar.moveInDirection(1, 0);
                break;
            case 'z':// Move SW
                mar.moveInDirection(-1, -1);
                break;
            case 'x':// move s
                mar.moveInDirection(0, -1);
                break;
            case 'c':// move SE
                mar.moveInDirection(1, -1);
                break;
            case 'E': // equipMyselfTo
                try {
                    EquipableItem item = (EquipableItem) this.getLastItemInInventory();
                    if (item != null) {
                        Display.getDisplay().setMessage("Attempted to Equip " + item.toString());
                    } else {
                        Display.getDisplay().setMessage("No item(s) to equip");
                    }
                    if (item != null) {
                        return item.equipMyselfTo(this);
                    }
                } catch (ClassCastException e) {
                    // ignore it
                    Display.getDisplay().setMessage("Cannot Equip From Inventory");
                }
                return -1;
            case 'U': // unEquip
                this.unEquipEverything();
                Display.getDisplay().setMessage("Unequipped Everything");
                break;
            case 'D': // drop item
                int error_code_D = mar.dropItem();
                return error_code_D;
            case 'p':// pickup item
                int error_code_p = mar.pickUpItemInDirection(0, 0);
                return error_code_p;
            case 'Z': // switch to Smasher
                this.setRepresentation('⚔');
                return this.becomeSmasher();
            case 'X': // switch to Summoner
                this.setRepresentation('☃');
                return this.becomeSummoner();
            case 'C': // switch to Sneaker
                this.setRepresentation('☭');
                return this.becomeSneak();
            case 'V': // switch to Smasher
                this.setOccupation(null);
                return 0;
            case 'l':
                this.observe();
                break;
            case 'B':
            	this.Bargain();
            	break;
            default: // no valid input
                System.out.println("Invalid input in Avatar.acceptKeyCommand() ");
                break;
        }
        return 0;
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
