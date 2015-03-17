package src.map.editor;

import src.model.MapMapEditor_Interface;
import src.model.constructs.Terrain;
/**
 * Class to add the given thing to the map when add is called.
 * Note that it may contain several things, so add may be suitable to be called several times, check with isEmpty.
 * @author mbregg
 *
 */
class TerrainAdder implements MapAddable {
	private Terrain terrain_;
	public TerrainAdder(Terrain terra) {
		terrain_ = terra;
	}

	@Override
	public int addToMap(MapMapEditor_Interface mapp_, int x, int y) {
		if(isEmpty()){return 2;}
		if(!mapp_.withinMap(x, y)){return 1;}
		int result = mapp_.addTerrain(terrain_, x, y);
		terrain_ = null;
		return result;
	}

	@Override
	public boolean isEmpty() {
		return (terrain_ == null);
	}

}
