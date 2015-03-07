package src.model.map.constructs;

import java.util.ArrayList;

/**
 * Merchant is a type of Villager that holds items that are for sale.
 *
 */
public class Merchant extends Villager {

	private ArrayList<PickupableItem> itemsForSale;

	public Merchant(String name, char representation) {
		super(name, representation);
		itemsForSale = new ArrayList<PickupableItem>();
	}

	/**
	 * Add an item to be sold in the merchants shop.
	 *
	 * @param item
	 */
	public void addItemToBeSold(PickupableItem item) {
		itemsForSale.add(item);
	}

	/**
	 * Avatar will buy an Item from the Merchant.
	 * 
	 * @param itemSlot
	 *            The number position of Item wanted in the shop.
	 * @return Item at the the position number of 'itemSlot'.
	 */
	public Item buyItem(int itemSlot) {
		if (!itemsForSale.isEmpty())
			return itemsForSale.remove(itemSlot);
		else
			return null;
	}
}
