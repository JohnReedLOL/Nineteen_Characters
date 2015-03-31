package src.model.constructs.items;

import src.Effect;

public class Trap extends OneShotAreaEffectItem {
    public int getID() { return 6; }

	/**
	 * 
	 * @param name
	 * @param representation
	 * @param effect
	 * @param power
	 */
	public Trap(String name, char representation, Effect effect, int power) {
		super(name, representation, effect, power);
		this.setViewable(false);
	}

	public void removeMyselfFromMap() {
		this.getMapRelation().getMapTile().removeTopItem();
	}

	/**
	 * On walk over, Trap should activate and hurt you. Trap will only hurt on
	 * one tile (radius of effect = 0) and always have Effect.HURT
	 * 
	 * @author Reid Olsen
	 */
	@Override
	public void onWalkOver() {

		activate();

		//System.out.println("Item: " + this.toString() +
		 //" is being walked on. HasBeenActivated = " + this.hasBeenActivated());
		if (this.isOneShot() && !this.goesInInventory()) {
			this.getMapRelation().getMapTile().removeTopItem();
		}
		this.getMapRelation().areaEffectFunctor.effectAreaWithinRadius(1,
				getPower(), Effect.HURT);
	}
}
