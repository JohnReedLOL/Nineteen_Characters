/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package src.model.constructs;

import java.util.ArrayList;
import java.util.Random;

import src.Effect;
import src.FacingDirection;
import src.Key_Commands;
import src.SkillEnum;
import src.io.view.display.Display;
import src.model.MapEntity_Relation;
import src.model.constructs.items.EquipableItem;
import src.model.constructs.items.Item;
import src.model.constructs.items.OneHandedWeapon;
import src.model.constructs.items.PickupableItem;
import src.model.constructs.items.PrimaryHandHoldable;
import src.model.constructs.items.SecondaryHandHoldable;
import src.model.constructs.items.Shield;
import src.model.constructs.items.TwoHandedWeapon;

/**
 * Entity inherits from DrawableThing. Entity is a DrawableThing that can move
 * on the map.
 */
abstract public class Entity extends DrawableThing {

    private PrimaryHandHoldable primary_hand_ = null;
    private SecondaryHandHoldable secondary_hand_ = null;
    private FacingDirection direction_ = FacingDirection.UP;
    private ArrayList<PickupableItem> inventory_;
    private EntityStatsPack stats_pack_ = new EntityStatsPack();
    private int num_gold_coins_when_spawned_ = 10;
    private int num_gold_coins_possessed_ = num_gold_coins_when_spawned_;

    private boolean has_lives_left_ = true;

    /**
     *
     * @return true if you have lives left, false if you don't.
     */
    public boolean isAlive() {
        return has_lives_left_;
    }

    /**
     * Entity Constructor
     *
     * @param name
     * @param representation - What will represent the Entity on the screen.
     */
    public Entity(String name, char representation) {
        super(name, representation);
        inventory_ = new ArrayList<PickupableItem>();
    }

    public int getNumGoldCoins() {
        return num_gold_coins_possessed_;
    }

    protected void setNumGoldCoinsWhenSpawned(int amount) {
        num_gold_coins_when_spawned_ = amount;
    }

    /**
     * Reset the num_gold_coins to the original amount. Used after re-spawning.
     *
     * @author John-Michael Reed
     */
    public void reinstateNumGoldCoins() {
        num_gold_coins_possessed_ = num_gold_coins_when_spawned_;
    }

    public int decrementNumGoldCoinsBy(int amount) {
        num_gold_coins_possessed_ -= amount;
        if (num_gold_coins_possessed_ >= 0) {
            return num_gold_coins_possessed_;
        } else {
            System.err.println("Number of coins going negative in Entity.decrementNumGoldCoinsBy(int amount)");
            num_gold_coins_possessed_ = 0;
            return num_gold_coins_possessed_;
        }
    }

    public int incrementNumGoldCoinsBy(int amount) {
        return (num_gold_coins_possessed_ += amount);
    }

    public abstract ArrayList<String> getInteractionOptionStrings();

    public abstract ArrayList<String> getConversationStarterStrings();

    /**
     * This function returns a list of appropriate responses to the string that
     * you recieved last.
     *
     * @author John-Michael Reed
     * @param what_you_just_said_to_me - same as "what you last said to me"
     * @return conversation options
     */
    public abstract ArrayList<String> getConversationContinuationStrings(String what_you_just_said_to_me, Entity who_is_talking_to_me);

    public abstract ArrayList<String> getListOfItemsYouCanUseOnMe();

    public int getExperienceBetweenLevels() {
        if (stats_pack_ != null) {
            return stats_pack_.NUMBER_OF_EXPERIENCE_POINT_PER_LEVEL;
        } else {
            return -1;
        }
    }

    public FacingDirection getFacingDirection() {
        return direction_;
    }

    public void setFacingDirection(FacingDirection dir) {
        direction_ = dir;
    }

    public ArrayList<PickupableItem> getInventory() {
        return this.inventory_;
    }

    private ArrayList<String> said_to_me_ = new ArrayList<String>();

    public void sayStuffToMe(ArrayList<String> in) {
        for (String i : in) {
            said_to_me_.add(i);
        }

    }

    // map_relationship_ is used in place of a map_referance_
    private int num_skillpoints_ = 1;

    public int getNum_skillpoints_() {
        return num_skillpoints_;
    }

    // Non-occupation specific skills
    private int bind_wounds_ = 1;

    public int getBind_wounds_() {
        return bind_wounds_;
    }

    public int bindWounds() {
        this.getMapRelation().areaEffectFunctor.effectAreaWithinRadius(0, getBind_wounds_(), Effect.HEAL);
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
                        s += map_relationship_.getTileInfo((i + 1), -(i + 1));
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
                        s += map_relationship_.getTileInfo(0, -(i + 1));
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
                        s += map_relationship_.getTileInfo(-(i + 1), -(i + 1));
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
                        s += map_relationship_.getTileInfo(-(i + 1), 0);
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
                        s += map_relationship_.getTileInfo(-(i + 1), (i + 1));
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
     * @param optional_text - either the last thing that was said to you or the
     * thing you are about to say.
     * @return ArrayList of strings for IO_Bundle or null if nothing to display
     */
    public ArrayList<String> acceptKeyCommand(Key_Commands command, String optional_text) {
        MapEntity_Relation mar = this.getMapRelation();
        if (mar == null) {
            System.out
                    .println(this.name_ + " cannot be controlled without a MapAvatar_Relation");
            System.exit(-8);
        }
        Entity target = this.getMapRelation().getEntityInFacingDirection();
        Item target_item_ = this.getMapRelation().getTopmostItemInFacingDirection();
        switch (command) {
            case MOVE_DOWNLEFT:// Move SW
                mar.moveInDirection(-1, -1);
                break;
            case MOVE_DOWN:// Move S
                mar.moveInDirection(0, -1);
                break;
            case MOVE_DOWNRIGHT:// Move SE
                mar.moveInDirection(1, -1);
                break;
            case MOVE_LEFT: // Move W
                mar.moveInDirection(-1, 0);
                break;
            case MOVE_RIGHT:// Move E
                mar.moveInDirection(1, 0);
                break;
            case MOVE_UPLEFT:// Move NW
                mar.moveInDirection(-1, 1);
                break;
            case MOVE_UP:// Move N
                mar.moveInDirection(0, 1);
                break;
            case MOVE_UPRIGHT: // Move NE
                mar.moveInDirection(1, 1);
                break;
            case SAVE_GAME: // Save Game
                // RunGame.saveGameToDisk(); // TODO: this is for testing, remove for
                // deployment
                break;
            case USE_LAST_ITEM: // Use item in inventory
                int error_code = this.useItemInFacingDirectionOnMyself();
                //if(error_code != 0) {
                PickupableItem last = this.getLastItemInInventory();
                if (last == null) {
                    System.out.println("last inventory item is null");
                } else {
                    System.out.println("last inventory item is not null");
                    if (error_code != 0) {
                        last.use(this);
                        System.out.println("using item!");
                    }
                }
                break;
            case EQUIP_LAST_ITEM: // equipMyselfTo
                try {
                    EquipableItem item = (EquipableItem) this.getLastItemInInventory();
                    if (item != null) {
                        Display.getDisplay().setMessage("Attempted to Equip " + item.toString());
                    } else {
                        Display.getDisplay().setMessage("No item(s) to equip");
                    }
                    if (item != null) {
                        item.equipMyselfTo(this);
                        break;
                    }
                } catch (ClassCastException e) {
                    // ignore it
                    Display.getDisplay().setMessage("Cannot Equip From Inventory");
                }
                break;
            case UNEQUIP_EVERYTHING: // unEquip
                this.unEquipEverything();
                Display.getDisplay().setMessage("Unequipped Everything");
                break;
            case DROP_LAST_ITEM: // drop item
                mar.dropItem();
                break;
            case BECOME_SMASHER: // switch to Smasher
                this.setRepresentation('⚔');
                this.becomeSmasher();
                break;
            case BECOME_SUMMONER: // switch to Summoner
                this.setRepresentation('☃');
                this.becomeSummoner();
                break;
            case BECOME_SNEAK: // switch to Sneaker
                this.setRepresentation('☭');
                this.becomeSneak();
                break;
            case BIND_WOUNDS:
                this.bindWounds();
                break;
            case BARGAIN_AND_BARTER:
                if (target != null) {
                    sayStuffToMe(target.getInteractionOptionStrings());
                    break;
                } else {
                    break;
                }
            case OBSERVE:
                this.observe();
                break;
            case USE_SKILL_1:
                this.getOccupation().performOccupationSkill(1);
                break;
            case USE_SKILL_2:
                System.out.println("Performing Skill 2");
                this.getOccupation().performOccupationSkill(2);
                System.out.println("Already performed Skill 2");
                break;
            case USE_SKILL_3:
                this.getOccupation().performOccupationSkill(3);
                break;
            case USE_SKILL_4:
                this.getOccupation().performOccupationSkill(4);
                break;
            case SPEND_SKILLPOINT_ON_BIND:
                this.spendSkillpointOn(SkillEnum.BIND_WOUNDS);
                break;
            case SPEND_SKILLPOINT_ON_BARGAIN:
                this.spendSkillpointOn(SkillEnum.BARGAIN);
                break;
            case SPEND_SKILLPOINT_ON_OBSERVE:
                this.spendSkillpointOn(SkillEnum.OBSERVATION);
                break;
            case SPEND_SKILLPOINT_ON_SKILL_1:
                this.spendSkillpointOn(SkillEnum.OCCUPATION_SKILL_1);
                break;
            case SPEND_SKILLPOINT_ON_SKILL_2:
                this.spendSkillpointOn(SkillEnum.OCCUPATION_SKILL_2);
                break;
            case SPEND_SKILLPOINT_ON_SKILL_3:
                this.spendSkillpointOn(SkillEnum.OCCUPATION_SKILL_3);
                break;
            case SPEND_SKILLPOINT_ON_SKILL_4:
                this.spendSkillpointOn(SkillEnum.OCCUPATION_SKILL_4);
                break;
            case GET_INTERACTION_OPTIONS:
                if (target != null) {
                    sayStuffToMe(target.getInteractionOptionStrings());
                    break;
                }
                if (target_item_ != null) {
                    sayStuffToMe(target_item_.getInteractionOptionStrings());
                    break;
                }
                return null;

            case GET_CONVERSATION_STARTERS:
                if (target != null) {
                    return target.getConversationStarterStrings();
                }
                break;
            case GET_CONVERSATION_CONTINUATION_OPTIONS:
                if (target != null) {
                    sayStuffToMe(target.getConversationContinuationStrings(optional_text, this));
                    break;
                } else if (target_item_ != null) {
                    System.out.println("optional text in Item GET_CONVERSATION_CONTINUATION_OPTIONS: \n" + optional_text);
                    sayStuffToMe(target_item_.getConversationContinuationStrings(optional_text, this));
                    break;
                } else {
                    break;
                }
            case ATTACK:
                getMapRelation().sendAttackInFacingDirection();
                break;
            case DO_ABSOLUTELY_NOTHING:
                break;
            default:
                System.out.println("Invalid command sent to avatar");
                break;
        }
        if (said_to_me_.size() != 0) {
            ArrayList<String> result = said_to_me_;
            said_to_me_ = new ArrayList<String>();
            return result;
        } else {
            return null;
        }
    }

    /**
     * Include stat increase from item i.e., item with stat increase is equipped
     *
     * @param item
     */
    private void addItemStatsToMyStats(Item item) {
        stats_pack_.addOn(item.getStatsPack());
    }

    /**
     * Entities must check their health after they are damaged.
     *
     * @return true if alive false is dead
     */
    public boolean checkHealthAndCommitSuicideIfDead() {
        if (stats_pack_.getCurrent_life_() > 0) {
            return true;
        } else {
            commitSuicide();
            return false;
        }
    }

    /**
     * Entity dies, Game Over.
     */
    public void commitSuicide() {
        int health_left = stats_pack_.getCurrent_life_();
        stats_pack_.deductCurrentLifeBy(health_left);
        stats_pack_.decreaseLivesLeftByOne();
        getMapRelation().respawn();
        if (stats_pack_.getLives_left_() < 0) {
            gameOver();
        }
    }

    public void gameOver() {
        System.out.println("Entity " + this.name_ + " has run out of lives and is gone forever.");
        this.has_lives_left_ = false;
        getMapRelation().removeMyselfFromTheMapCompletely();
    }

    public PrimaryHandHoldable getPrimaryEquipped() {
        return primary_hand_;
    }

    public SecondaryHandHoldable getSecondaryEquipped() {
        return secondary_hand_;
    }

    public void setPrimaryEquipped(PrimaryHandHoldable primary_hard) {
        primary_hand_ = primary_hard;
    }

    public void setSecondaryEquipped(SecondaryHandHoldable secondary_hard) {
        secondary_hand_ = secondary_hard;
    }

    /**
     * @author John-Michael Reed
     * @return
     */
    public int equip(EquipableItem item) {
        int temp = item.equipMyselfTo(this);
        return temp;

    }

    /**
     * @author John-Michael Reed
     * @param shield
     * @return
     */
    public int equipShield(Shield shield) {
        if (shield != null) {
            // In the case of a 2H sword
            if ((primary_hand_ == secondary_hand_) && (secondary_hand_ != null)) {
                inventory_.add((PickupableItem) primary_hand_);
                stats_pack_.reduceBy(secondary_hand_.getStatsPack());
                primary_hand_ = null;
                secondary_hand_ = null;
            } // In the case of a shield
            else if ((primary_hand_ != secondary_hand_) && (secondary_hand_ != null)) {
                inventory_.add((PickupableItem) primary_hand_);
                stats_pack_.reduceBy(secondary_hand_.getStatsPack());
                secondary_hand_ = null;
            }

            secondary_hand_ = shield;
            stats_pack_.addOn(secondary_hand_.getStatsPack());
            boolean successful_removal = inventory_.remove((PickupableItem) shield);
            if (successful_removal != true) {
                System.exit(-66);
            }
            return 0;
        } else {
            return -5;
        }
    }

    /**
     * @author John-Michael Reed Equips the weapon and modifies stats
     * @param one_hand_weapon
     * @return
     */
    public int equip1hWeapon(final OneHandedWeapon one_hand_weapon) {
        if (one_hand_weapon != null) {
            // In the case of a 2H sword
            if ((primary_hand_ == secondary_hand_) && (primary_hand_ != null)) {
                inventory_.add((PickupableItem) primary_hand_);
                stats_pack_.reduceBy(primary_hand_.getStatsPack());
                primary_hand_ = null;
                secondary_hand_ = null;
            } // In the case of a 1h sword
            else if ((primary_hand_ != secondary_hand_) && (primary_hand_ != null)) {
                inventory_.add((PickupableItem) primary_hand_);
                stats_pack_.reduceBy(primary_hand_.getStatsPack());
                primary_hand_ = null;
            }

            int error_code = -1; // by default null occupation cannot equipMyselfTo any weapon
            if (occupation_ != null) {
                error_code = occupation_.equipOneHandWeapon(one_hand_weapon);
            }
            if (error_code == 0) {
                primary_hand_ = one_hand_weapon;
                stats_pack_.addOn(primary_hand_.getStatsPack());
                boolean successful_removal = inventory_.remove((PickupableItem) one_hand_weapon);
                if (successful_removal != true) {
                    System.exit(-77);
                }
                return 0;
            } else {
                return error_code;
            }
        } else {
            return -5;
        }
    }

    /**
     * @author John-Michael Reed
     * @param two_hand_weapon
     * @return
     */
    public int equip2hWeapon(TwoHandedWeapon two_hand_weapon) {
        if (two_hand_weapon != null) {
            // case of 2H sword
            if ((primary_hand_ == secondary_hand_) && (primary_hand_ != null)) {
                inventory_.add((PickupableItem) primary_hand_);
                stats_pack_.reduceBy(primary_hand_.getStatsPack());
                primary_hand_ = null;
                secondary_hand_ = null;
            } else if ((primary_hand_ != secondary_hand_)) {
                // case of 1h SWORD
                if (primary_hand_ != null) {
                    inventory_.add((PickupableItem) primary_hand_);
                    stats_pack_.reduceBy(primary_hand_.getStatsPack());
                    primary_hand_ = null;
                }
                // case of sheld
                if (secondary_hand_ != null) {
                    inventory_.add((PickupableItem) secondary_hand_);
                    stats_pack_.reduceBy(secondary_hand_.getStatsPack());
                    secondary_hand_ = null;
                }
            }
            primary_hand_ = null;
            secondary_hand_ = null;

            int error_code = -1; // by default null occupation cannot equipMyselfTo any weapon
            if (occupation_ != null) {
                error_code = occupation_.equipTwoHandWeapon(two_hand_weapon);
            }
            if (error_code == 0) {
                primary_hand_ = two_hand_weapon;
                secondary_hand_ = two_hand_weapon;
                stats_pack_.addOn(primary_hand_.getStatsPack());
                boolean successful_removal = inventory_.remove((PickupableItem) two_hand_weapon);
                if (successful_removal != true) {
                    System.exit(-88);
                }
                return 0;
            } else {
                return error_code;
            }
        } else {
            return -5;
        }
    }

    public int unEquipEverything() {
        if (primary_hand_ == secondary_hand_ && primary_hand_ != null) {
            inventory_.add((PickupableItem) primary_hand_);
            stats_pack_.reduceBy(primary_hand_.getStatsPack());
        } else if (primary_hand_ != secondary_hand_) {
            if (primary_hand_ != null) {
                inventory_.add((PickupableItem) primary_hand_);
                stats_pack_.reduceBy(primary_hand_.getStatsPack());
            }
            if (secondary_hand_ != null) {
                inventory_.add((PickupableItem) secondary_hand_);
                stats_pack_.reduceBy(secondary_hand_.getStatsPack());
            }
        }
        primary_hand_ = null;
        secondary_hand_ = null;

        if (occupation_ != null) {
            return occupation_.unEquipEverything();
        } else {
            return 0;
        }
    }

    /**
     * this function levels up an entity Modified to make it "gain enough
     * experience to level up"
     *
     * @author John
     */
    public void gainEnoughExperienceTolevelUp() {
        final int number_of_experience_points_per_level = getExperienceBetweenLevels();
        int exp_to_next = number_of_experience_points_per_level
                - (stats_pack_.getQuantity_of_experience_() % number_of_experience_points_per_level);
        int old_level = stats_pack_.getCached_current_level_();
        gainExperiencePoints(exp_to_next);
        int new_level = stats_pack_.getCached_current_level_();
        if (new_level - old_level != 1) {
            System.err.println("Error in Entity.gainEnoughExperienceTolevelUp()");
            System.exit(-45);
        }

    }

    public boolean hasEquippedPrimaryHand() {
        if (primary_hand_ != null) {
            return true;
        }
        return false;
    }

    public boolean hasEquippedSecondaryHand() {
        if (secondary_hand_ != null) {
            return true;
        }
        return false;
    }

    /**
     * Returns false because Entities are not passable.
     */
    @Override
    public boolean isPassable() {
        return false;
    }

    /**
     * Add item to the inventory.
     *
     * @param item
     */
    public void addItemToInventory(PickupableItem item) {
        inventory_.add(item);
    }

    /**
     * Returns first Item object in Inventory
     *
     * @return Item
     */
    public PickupableItem getLastItemInInventory() {
        if (!inventory_.isEmpty()) {
            return inventory_.get(inventory_.size() - 1);
        } else {
            return null;
        }
    }

    /**
     * Gets last Item in Inventory. In the 0 position of the arrayList.
     *
     * @return Null if list is empty
     */
    public PickupableItem pullLastItemOutOfInventory() {
        if (!inventory_.isEmpty()) {
            return inventory_.remove(inventory_.size() - 1);
        } else {
            return null;
        }
    }

    /**
     * Sends an attack to an entity.
     *
     * @author John-Michael Reed
     * @param target - entity to hit
     * @return -1 if target is null, 0 if success
     */
    public int sendAttack(Entity target_entity) {
        if (target_entity != null) {
            target_entity.receiveAttack(3 + this.getStatsPack().getOffensive_rating_(), this);
            return 0;
        } else {
            return -1;
        }
    }

    /**
     * Uses the item I am facing on myself. If I have a key the door will unlock
     * itself.\
     *
     * @return 0 on success, -1 on fail (no item to use)
     */
    public int useItemInFacingDirectionOnMyself() {
        Item target = getMapRelation().getTopmostItemInFacingDirection();
        if (target != null) {
            target.use(this);
            return 0;
        }
        return -1;
    }

    /**
     * Call this function when an Entity gains experience points.
     *
     * @param amount - number of experience points
     * @return number of level ups - 0 for no level ups, 1 for 1 level up, 2 for
     * 2 levels up, etc.;
     */
    public int gainExperiencePoints(int amount) {
        int num_level_ups = stats_pack_.increaseQuantityOfExperienceBy(amount);
        num_skillpoints_ += num_level_ups;
        return num_level_ups;
    }

    public int checkLevel() {
        return stats_pack_.getCached_current_level_();
    }

    // map_relationship_ is used in place of a map_referance_
    private MapEntity_Relation map_relationship_;

    /**
     * Use this to call functions contained within the MapEntity relationship
     *
     * @return map_relationship_
     * @author Reed, John
     */
    public MapEntity_Relation getMapRelation() {
        return map_relationship_;
    }

    /**
     * Set MapEntity_Relation
     *
     * @param e - MapEntity_Relation
     */
    public void setMapRelation(MapEntity_Relation e) {
        map_relationship_ = e;
    }

    private Occupation occupation_ = null;

    /**
     * Get the Entities occupation.
     *
     * @return Occupation of Entity
     */
    public Occupation getOccupation() {
        return occupation_;
    }

    /**
     * Sets Entities Occupation.
     *
     * @param occupation
     */
    public void setOccupation(Occupation occupation) {
        occupation_ = occupation;
    }

    /**
     * Sets Entities Occupation to smasher. Resets stats
     */
    public int becomeSmasher() {
        this.unEquipEverything(); // You cannot change classes if you hold weapons of another class
        if (occupation_ != null) {
            occupation_ = new Smasher(this.occupation_);
        } else {
            occupation_ = new Smasher(this);
        }
        return 0;
    }

    /**
     * Sets Entities Occupation to smasher. Resets stats
     */
    public int becomeSummoner() {
        this.unEquipEverything(); // You cannot change classes if you hold weapons of another class
        if (occupation_ != null) {
            occupation_ = new Summoner(this.occupation_);
        } else {
            occupation_ = new Summoner(this);
        }
        return 0;
    }

    /**
     * Sets Entities Occupation to smasher. Resets stats
     */
    public int becomeSneak() {
        this.unEquipEverything(); // You cannot change classes if you hold weapons of another class
        if (occupation_ != null) {
            occupation_ = new Sneak(this.occupation_);
        } else {
            occupation_ = new Sneak(this);
        }
        return 0;
    }

    /**
     *
     * Override for villager/monster
     *
     * @author John-Michael Reed
     * @param damage - damage received
     * @param attacker - who the attack is coming from. Specify null if the
     * attacker is from an unreachable source i.e. the map
     * @return true if I am still alive, false if I am dead
     */
    public boolean receiveAttack(int damage, Entity attacker) {
        int amount_of_damage = damage - getStatsPack().getDefensive_rating_() - getStatsPack().getArmor_rating_();
        if (amount_of_damage < 0) {
            amount_of_damage = 0;
        }
        getStatsPack().deductCurrentLifeBy(amount_of_damage);
        if (stats_pack_.getCurrent_life_() <= 0 && attacker != null) {
            int money = this.num_gold_coins_possessed_;
            this.decrementNumGoldCoinsBy(money); // All money goes to my attacker.
            attacker.incrementNumGoldCoinsBy(money);
        }
        return checkHealthAndCommitSuicideIfDead(); // returns true if alive, false if dead
    }

    public void receiveHeal(int strength) {
        this.stats_pack_.increaseCurrentLifeBy(strength);
    }

    public void receiveMana(int strength) {
        this.stats_pack_.increaseCurrentManaBy(strength);
    }

    /**
     * Return the combined stats of the entity, includes armour stats.
     */
    public EntityStatsPack getStatsPack() {
        return stats_pack_;
    }

    public String toString() {
        String s = "Entity name: " + name_;

        /*if (!(equipped_item_ == null)) {
         s += "\n equppied item: " + equipped_item_.name_;
         } else {
         s += "\n equppied item: null";
         }*/
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

        s += "\n associated with map:" + map_relationship_.isAssociatedWithMap();

        return s;
    }
}
