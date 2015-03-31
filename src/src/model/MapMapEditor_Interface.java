package src.model;

import src.IO_Bundle;

public interface MapMapEditor_Interface extends MapMapAddable_Interface {
	public IO_Bundle getMapAt(int x, int y,int width, int height);
	
    /**
     * Takes in name so save to, defaults to date
     * @param foo
     */
    public int saveGame(String foo);
    /**
     * Takes in name to load. 
     * @param foo
     */
    public int loadGame(String foo);

}
