/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package src.model.constructs.items;

import java.util.ArrayList;

import src.HardCodedStrings;
import src.model.constructs.Entity;

/**
 *
 * @author JohnReedLOL
 */
public class TemporaryObstacleItem extends ObstacleItem {

    private final ObstacleRemovingItem keyItem_;

    public TemporaryObstacleItem(String name, char representation, ObstacleRemovingItem keyItem) {
        super(name, representation);
        keyItem_ = keyItem;
    }

    public ArrayList<String> getInteractionOptionStrings() {
        ArrayList<String> options = new ArrayList<String>();
        options.add("Give me a list of items that I can use on you. " + HardCodedStrings.getItemList);
        return options;
    }

    public ArrayList<String> getConversationContinuationStrings(String what_you_just_said_to_me, Entity who_is_talking_to_me) {
        ArrayList<String> options = new ArrayList<String>();
        if(what_you_just_said_to_me.equals("Give me a list of items that I can use on you. " + HardCodedStrings.getItemList)) {
            options.add(keyItem_.name_ + HardCodedStrings.useItem);
        } else if(what_you_just_said_to_me.equals(keyItem_.name_ + HardCodedStrings.useItem)) {
            this.use(who_is_talking_to_me);
        }
        return options;
    }

    /**
     * Returns a reference to the keyItem needed to open this door.
     *
     * @return
     */
    public ObstacleRemovingItem checkKey() {
        return this.keyItem_;
    }

    /**
     * The use function allows an item to exert its effect on an entity.
     *
     * @param target - The entity that the item will be used on.
     */
    @Override
    public void use(Entity target) {
        if (target.getInventory().contains(keyItem_)) {
            System.out.println("You have the key");
            this.setPassable(true);
        } else {
            System.out.println("You don't have the key");
        }
    }

    /**
     * The use function also allows an item to exert an effect on another item.
     *
     * @param target - The item that this item will be used upon.
     */
    @Override
    public void use(Item target) {
        if (target == keyItem_) {
            this.setPassable(true);
        }
    }
}
