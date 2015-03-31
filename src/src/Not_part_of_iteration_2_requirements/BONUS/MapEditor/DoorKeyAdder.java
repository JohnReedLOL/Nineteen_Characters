package src.Not_part_of_iteration_2_requirements.BONUS.MapEditor;

import src.model.MapMapAddable_Interface;
import src.model.constructs.items.Item;
/**
 * Class to add the given thing to the map when add is called.
 * Note that it may contain several things, so add may be suitable to be called several times, check with isEmpty.
 * @author mbregg
 *
 */
class DoorKeyAdder implements MapAddable {
	private Item key_;
	private Item door_;
	public DoorKeyAdder(Item key, Item door) {
		key_ = key;
		door_ = door;
	}

	@Override
	public int addToMap(MapMapAddable_Interface mapp_, int x, int y) {
		if(isEmpty()){return 2;}
		if(!mapp_.isWithinMap(x, y)){return 1;}
		int result;
		if(key_ == null) {
			result = mapp_.addItem(door_, x, y);
			door_ = null;
		} else {
			System.out.println(key_);
			result = mapp_.addItem(key_,x,y);
			key_ = null;
		}

		return result;
	}

	@Override
	public boolean isEmpty() {
		return (door_ == null && key_ == null);

	}

}
