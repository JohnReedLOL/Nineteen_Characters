/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package src.model.map.constructs;

import java.util.ArrayList;
import src.FacingDirection;

import src.model.map.MapEntity_Relation;
import src.io.view.display.Display;
import src.FacingDirection;
import src.model.map.constructs.PrimaryHandHoldable;
import src.model.map.constructs.SecondaryHandHoldable;

/**
 * Entity inherits from DrawableThing. Entity is a DrawableThing that can move
 * on the map.
 */
abstract public class Entity extends DrawableThing {

    private PrimaryHandHoldable primary_hand_ = null;
    private SecondaryHandHoldable secondary_hand_ = null;
    private FacingDirection direction_ = FacingDirection.UP;
    protected ArrayList<PickupableItem> inventory_;
    private EntityStatsPack stats_pack_ = new EntityStatsPack(this);

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

    public FacingDirection getFacingDirection() {
        return direction_;
    }

    public void setFacingDirection(FacingDirection dir) {
        direction_ = dir;
    }

    public int getExperienceBetweenLevels() {
        if (stats_pack_ != null) {
            return stats_pack_.NUMBER_OF_EXPERIENCE_POINT_PER_LEVEL;
        } else {
            return -1;
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
     * Entities should check their health after they are damaged.
     */
    public void checkHealth() {
        if (stats_pack_.getCurrent_life_() <= 0) {
            commitSuicide();
        }
    }

    /**
     * Entity dies, Game Over.
     */
    public void commitSuicide() {
        int health_left = stats_pack_.getCurrent_life_();
        stats_pack_.deductCurrentLifeBy(health_left);
        getMapRelation().respawn();
        if (stats_pack_.getLives_left_() < 0) {
            gameOver();
        }
    }

    public void gameOver() {
        System.out.println("game over");
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
     * @param sheild
     * @return
     */
    public int equipSheild(Sheild sheild) {
        if (sheild != null) {
            // In the case of a 2H sword
            if ((primary_hand_ == secondary_hand_) && (secondary_hand_ != null)) {
                inventory_.add((PickupableItem) primary_hand_);
                stats_pack_.reduceBy(secondary_hand_.getStatsPack());
                primary_hand_ = null;
                secondary_hand_ = null;
            } // In the case of a sheild
            else if ((primary_hand_ != secondary_hand_) && (secondary_hand_ != null)) {
                inventory_.add((PickupableItem) primary_hand_);
                stats_pack_.reduceBy(secondary_hand_.getStatsPack());
                secondary_hand_ = null;
            }

            secondary_hand_ = sheild;
            stats_pack_.addOn(secondary_hand_.getStatsPack());
            boolean successful_removal = inventory_.remove((PickupableItem) sheild);
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

        if (occupation_ == null) {
            return 0;
        } else {
            return occupation_.unEquipEverything();
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
     * Gets the Inventory of Entity.
     *
     * @return ArrayList of Items that are in the Entities Inventory
     */
    public ArrayList<PickupableItem> getInventory() {
        return inventory_;
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
     * Uses first item in inventory Does not destroy the item
     *
     * @return 0 on success, -1 on fail (no item to use)
     */
    public int useLastInventoryItem() {
        Item i = getLastItemInInventory();
        if (i == null) {
            Display.getDisplay().setMessage("You have no items to use.");
            return -1;
        } else {
            i.use(this);
            return 0;
        }
    }

    /**
     *
     * @param amount
     * @return number of level ups;
     */
    public int gainExperiencePoints(int amount) {
        int num_level_ups = stats_pack_.increaseQuantityOfExperienceBy(amount);
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
        occupation_ = new Smasher(this);
        return 0;
    }

    /**
     * Sets Entities Occupation to smasher. Resets stats
     */
    public int becomeSummoner() {
        occupation_ = new Summoner(this);
        return 0;
    }

    /**
     * Sets Entities Occupation to smasher. Resets stats
     */
    public int becomeSneak() {
        occupation_ = new Sneak(this);
        return 0;
    }

    /**
     * Specify null if the attacker is not an entity that can be attacked.
     *
     * @param damage - damage received
     * @param attacker - who the attack is coming from
     */
    public void receiveAttack(int damage, Entity attacker) {
        int did_I_run_out_of_health = stats_pack_.deductCurrentLifeBy(damage - getStatsPack().getDefensive_rating_() - getStatsPack().getArmor_rating_());
        if (did_I_run_out_of_health != 0) {
            getMapRelation().respawn();
            if (stats_pack_.getLives_left_() < 0) {
                gameOver();
            }
        } else {
            if (attacker != null) {
                this.replyToAttackFrom(attacker);
            }
        }
    }

    public void receiveHeal(int strength) {
        this.stats_pack_.increaseCurrentLifeBy(strength);
    }

    // reply(greeting, this);
    public String reply(String recieved_text, Entity speaker) {
        String reply = "";
        if (recieved_text == "hello") {
            reply = "goodbye";
            return speaker.reply(reply, this);
        } else if (recieved_text == "goodbye") {
            reply = "";
            return speaker.reply(reply, this);
        } else if (recieved_text == "") {
            return "";
        } else {
            return "";
        }
    }

    /**
     * Called by an entity that was attacked by another entity. Override for
     * monster/villager.
     *
     * @author John-Michael Reed
     * @param attacker
     * @return 0 if reply succeeded, non-zero otherwise [ex. if entity is null
     * or off the map]
     */
    public abstract int replyToAttackFrom(Entity attacker);

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
