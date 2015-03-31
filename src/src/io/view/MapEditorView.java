package src.io.view;

import java.awt.Color;
import java.util.ArrayList;

import src.IO_Bundle;
/**
 * View for the mapeditor game mode
 * @author mbregg
 *
 */
public class MapEditorView extends MapView {
	private ArrayList<String> items_ = new ArrayList<String>();
	public MapEditorView() {
		// TODO Auto-generated constructor stub
	}
	private String lastSpawned = "";
	protected void renderToDisplayInternally(IO_Bundle bundle) {
		super.renderToDisplayInternally(bundle);
		drawCross();
		this.writeStringToContents(1, 1, "Set to spawn a: " + lastSpawned);
	}
	public void setSpawnableList(ArrayList<String> in){
		items_ = in;
	}
	public void setLastSpawned(String in){
		if(in == "" || in == null || in.equals(System.lineSeparator())){return;}//Avoid setting lastSpawned to invalid. 
		lastSpawned = in;
	}
	@Override
	public String getItemList() {
		StringBuilder result = new StringBuilder();
		for(String i : items_){result.append(i); result.append(System.lineSeparator());}
		return result.toString();
	}
	protected void drawCross(){
		int mid_x = this.getWidth()/2+1;
		int mid_y = this.getHeight()/2+1;
		for(int i = 0; i!=this.getHeight(); ++i){
			if(i!=mid_y){
				this.getCharContents()[mid_x][i] = '|';
				this.getColorContents()[mid_x][i] = Color.black;
			}
		}
		for(int i = 0; i!=this.getWidth(); ++i){
			if(i!=mid_x){
				this.getCharContents()[i][mid_y] = '-';
				this.getColorContents()[i][mid_y] = Color.black;
			}
		}

	}

}
