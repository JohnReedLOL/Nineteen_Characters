package src.map.editor;

import src.model.MapMapAddable_Interface;
import src.model.constructs.items.OneWayTeleportItem;

public class TeleportAdder implements MapAddable {

	OneWayTeleportItem tele_ = null;
	public TeleportAdder(OneWayTeleportItem item) {
		tele_ = item;
	}
	boolean locationSet_ = false;
	@Override
	public int addToMap(MapMapAddable_Interface mapp_, int x, int y) {
		if(isEmpty()){return 2;}
		
		if(!locationSet_){
			if(mapp_.isWithinMap(x, y))
			{
				tele_.setDestination(x, y);
				locationSet_ = true;
				return 0;
			}
			return 1;
		}else{
			if(!mapp_.isWithinMap(x, y)){return 1;}
			int result = mapp_.addItem(tele_,x,y);
			tele_ = null;
			return result;
		}
	}

	@Override
	public boolean isEmpty() {
		return (tele_==null);
	}

}
