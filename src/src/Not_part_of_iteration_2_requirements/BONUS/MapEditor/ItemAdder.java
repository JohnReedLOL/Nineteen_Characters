package src.Not_part_of_iteration_2_requirements.BONUS.MapEditor;

import src.model.MapMapAddable_Interface;
import src.model.constructs.items.Item;
/**
 * Class to add the given thing to the map when add is called.
 * Note that it may contain several things, so add may be suitable to be called several times, check with isEmpty.
 * @author mbregg
 *
 */
class ItemAdder implements MapAddable {
	private Item item_;
	public ItemAdder(Item item) {
		item_ = item;
	}

	@Override
	public int addToMap(MapMapAddable_Interface mapp_, int x, int y) {
		if(isEmpty()){return 2;}
		if(!mapp_.isWithinMap(x, y)){return 1;}
		int result = mapp_.addItem(item_,x,y);
		item_ = null;
		return result;
	}

	@Override
	public boolean isEmpty() {
		return(item_ == null);
	}

}
