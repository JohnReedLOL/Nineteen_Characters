package src.model.constructs.items;

import src.model.constructs.Entity;

public class FlyingSerum extends PickupableItem {

    public FlyingSerum(String name, char representation) {
        super(name, representation);
    }


    @Override
    public void use(Entity target) {
        target.getMapRelation().becomeFlyingRelation();
    }

}
