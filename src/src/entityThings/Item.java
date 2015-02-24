/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package src.entityThings;


import src.model.MapItem_Relation;
import src.userIO.Display;

/**
 * Class item represents a stackable entity that
 * cannot move itself.
 *
 * @author JohnReedLOL
 */
public class Item extends DrawableThing {
	
	public boolean determineIfCanPass(Entity entity) {
        if (this.getMapRelation().isPassable() ) {
            return false;
        } else {
            return true;
        }
    }
	
	private boolean goes_in_inventory_;
    /**
     * Checks if item can go in Inventory.
     * @return true if item can be put into inventory, false if not.
     */
    public boolean goesInInventory() {
        return this.goes_in_inventory_;
    }
	    
    /**
     * Returns false because Entities are not passable.
     */
    @Override
    public boolean isPassable() {
        return this.getMapRelation().isPassable();
    }

    /**
     * Checks if item is one shot.
     * @return true if item is one shot, false if not.
     */
    public boolean isOneShot() {
        return this.getMapRelation().isOneShot();
    }

    public Item(String name, char representation,
            boolean goes_in_inventory) {
        super(name, representation);
        goes_in_inventory_ = goes_in_inventory;
    }

    // map_relationship_ is used in place of a map_referance_
    private MapItem_Relation map_relationship_;
    /**
     * Use this to call functions contained within the MapItem relationship
     *
     * @return map_relationship_
     * @author Reed, John
     */
    public MapItem_Relation getMapRelation() {
        return map_relationship_;
    }

    public void setMapRelation(MapItem_Relation i) {
        map_relationship_ = i;
    }

    /**
     * 
     */
    public void onWalkOver() {
        //System.out.println("Item: " + this.toString() + " is being walked on.");
        if (this.isOneShot() && !this.goesInInventory()) {
            this.getMapRelation().getMapTile().removeTopItem();
        }
        // Display.setMessage("Walked on Item: " + this.toString(), 3);
        // this.getMapRelation().hurtWithinRadius(10, 2);
    }

    public String toString() {
        String s = "Item name: " + name_;

        s += "\n map_relationship_: ";
        if (map_relationship_ == null) {
            s += "null";
        } else {
            s += "Not null";
        }

        s += "\n associated with map: " + map_relationship_.isAssociatedWithMap();

        return s;
    }

    /**
     * The use function allows an item to exert its effect on an entity.
     *
     * @param target - The entity that the item will be used on.
     */
    public void use(Entity target) {
        Display.setMessage("Used Item: " + this.toString() + " Health: " + target.getStatsPack().current_life_
                + " Level: " + target.getStatsPack().cached_current_level_, 3);
    }

    /**
     * The use function also allows an item to exert an effect on another item.
     *
     * @param target - The item that this item will be used upon.
     */
    public void use(Item target) {

    }

    // <editor-fold desc="SERIALIZATION" defaultstate="collapsed">
    @Override
    public String getSerTag() {
        return "ITEM";
    }

    protected void linkOther (ArrayDeque<SaveData> refs) {
        super.linkOther(refs);
    }

    protected void readOther (ObjectInputStream ois, ArrayDeque<Integer> out_rels) throws IOException, ClassNotFoundException {
        super.readOther(ois, out_rels);
    }

    protected void writeOther (ObjectOutputStream oos, HashMap<SaveData, Boolean> saveMap) throws IOException {
        super.writeOther(oos, saveMap);
    }
    // </editor-fold>
}
