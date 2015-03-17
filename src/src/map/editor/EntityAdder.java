package src.map.editor;

import src.model.MapMapEditor_Interface;
import src.model.constructs.Entity;
/**
 * Class to add the given thing to the map when add is called.
 * Note that it may contain several things, so add may be suitable to be called several times, check with isEmpty.
 * @author mbregg
 *
 */
class EntityAdder implements MapAddable {
	private Entity entity_;
	public EntityAdder(Entity ent) {
		entity_ = ent;
	}

	@Override
	public int addToMap(MapMapEditor_Interface mapp_, int x, int y) {
		if(isEmpty()){return 2;}
		if(!mapp_.withinMap(x, y)){return 1;}
		int result = mapp_.addAsEntity(entity_, x, y);
		entity_ = null;
		return result;
	}

	@Override
	public boolean isEmpty() {
		return(entity_==null);
	}

}
