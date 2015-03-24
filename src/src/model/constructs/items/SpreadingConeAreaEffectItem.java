package src.model.constructs.items;

import java.util.UUID;

import src.Effect;
import src.FacingDirection;

public class SpreadingConeAreaEffectItem extends Item {

    private static final long serialVersionUID = 21L;
    @Override
    public int getID() { return (int)serialVersionUID; }
	
	int current_size_ = 0;
	int max_size_;
	int strength_;
	FacingDirection direction_ = null;
	Effect effect_ = null;
	boolean wait_turn_;
	
    public SpreadingConeAreaEffectItem(int max_size, int strength, Effect effect, FacingDirection direction) {
    	super(UUID.randomUUID().toString(), '?', false, true, false);
    	super.setViewable(false); // invisible
    	max_size_ = max_size;
    	strength_ = strength;
    	effect_ = effect;
    	direction_ = direction;
    }
    
	@Override
    public void takeTurn() {
		wait_turn_ = !wait_turn_;
		if (wait_turn_) {
			for (int i = 0; i < 2; i++) {
				this.getMapRelation().areaEffectFunctor.effectArc(current_size_, strength_, effect_, direction_);
				++current_size_;
				if(current_size_ > max_size_) {
					this.getMapRelation().removeMyselfFromTheMapCompletely();
					break;
				}
			}
		}
    }
}
