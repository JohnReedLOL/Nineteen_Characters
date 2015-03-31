package src.Not_part_of_iteration_2_requirements.BONUS.MapEditor;

import src.model.MapMapAddable_Interface;
import src.model.constructs.Avatar;

public class KnightAvatarAdder implements MapAddable {
	private Avatar Avatar_;
	public KnightAvatarAdder(Avatar ent) {
		Avatar_ = ent;
	}
	@Override
	public int addToMap(MapMapAddable_Interface mapp_, int x, int y) {
		if(isEmpty()){return 2;}
		if(!mapp_.isWithinMap(x, y)){return 1;}
		int result = mapp_.addAsKnight(Avatar_, x, y);
		Avatar_ = null;
		return result;
	}

	@Override
	public boolean isEmpty() {
			return(Avatar_==null);
	}

}
