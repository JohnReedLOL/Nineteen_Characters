package src.model;

import src.model.constructs.Entity;
import src.model.constructs.Terrain;
import src.model.constructs.items.Item;

public interface MapMapAddable_Interface {
	/**
	 * Once a tile has terrain, that terrain is constant.
	 *
	 * @param t - Terrain
	 * @param x - x position for tile
	 * @param y - y position for tile
	 * @return error code, 0 for success
	 */
	public int addTerrain(Terrain t, int x, int y);
	/**
	 * Adds an entity to the map.
	 *
	 * @param e - Entity to be added
	 * @param x - x position of where you want to add entity
	 * @param y - y posiition of where you want to add entity
	 * @return -1 on fail, 0 on success
	 */
	public int addAsEntity(Entity e, int x, int y);
	/**
	 * Adds an item to the map.
	 *
	 * @param i - Item to be added
	 * @param x - x position of where you want to add item
	 * @param y - y position of where you want to add item
	 * @return -1 on fail, 0 on success
	 */
	public int addItem(Item i, int x, int y);

	public boolean isWithinMap(int x, int y);
    /**
     * Adds an knight avatar to the map.
     *
     * @param a - Avatar to be added
     * @param x - x position of where you want to add Avatar
     * @param y - y posiition of where you want to add Avatar
     * @return -1 on fail, 0 on success
     **/
	public int addAsKnight(Entity e, int x, int y);
}
