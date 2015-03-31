package src.model.constructs.items;

import src.model.MapEntity_Relation;
import src.model.constructs.Entity;

public class KnightsSerum extends PickupableItem {

    public int getID() { return 4; }

    public KnightsSerum(String name, char representation) {
        super(name, representation);
    }

    private boolean activated = false;

    public boolean getActivated() { return activated; }

    public void setActivated(boolean value) { activated = value; }

    @Override
    public void use(Entity target) {
        if (!activated) {
            target.getMapRelation().becomeKnightRelation();
            activated = true;
        } else {
            target.getMapRelation().becomeEntityRelation();
            activated = false;
        }
    }
}
