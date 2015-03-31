package src.model.constructs.items;

import src.model.constructs.Entity;

public class InvisibilitySerum extends PickupableItem {

    public int getID() { return 3; }

    public InvisibilitySerum(String name, char representation) {
        super(name, representation);
    }


    /**
     * Modified to allow for invisibility
     * @param target 
     */
    @Override
    public void use(Entity target) {
        // target.getMapRelation().becomeFlyingRelation();
            target.setViewable(! target.isVisible());
    }
        @Override
    public void takeTurn() {
        
    }
}
