package src.map.editor;

import src.model.MapMapEditor_Interface;
/**
 * Interface which mapaddables implement.
 * Allows something to take any *Addable, and treat it the same. 
 * @author mbregg
 *
 */
public interface MapAddable {

	/**
	 * Adds thing to map.
	 * May need to be called multiple times to get all things on the map added.
	 */
	public int addToMap(MapMapEditor_Interface mapp_, int x, int y);
	/**
	 * True if this adder has nothing left to add
	 */
	public boolean isEmpty();
}
