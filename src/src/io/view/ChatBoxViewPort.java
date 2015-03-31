package src.io.view;

import java.util.ArrayList;

import src.IO_Bundle;
/**
 * View for the chatbox. Separate from the other views.
 * @author mbregg
 *
 */
public class ChatBoxViewPort {
	private ArrayList<String> view_content_strings_ = new ArrayList<String>();
	private ArrayList<String> headings_ = new ArrayList<String>();
	private ArrayList<String> result_ = new ArrayList<String>();
	private int whiteSpaceCount_ = 1;
	public ChatBoxViewPort() {
	}
        
        // tells you whether or not your game is already over
        boolean did_game_over = false;
        
	public void renderToDisplay(ArrayList<String> strings_for_communication, boolean is_alive){
            //System.out.println("Calling ChatBoxViewPort.renderToDisplay(ArrayList<String> strings_for_communication, boolean is_alive)");
		result_.clear();
		if(is_alive == false && did_game_over == false){
                    result_.add("GAME OVER YOU ARE DEAD"); 
                    did_game_over = true;
                    return;
                } else if (is_alive == false && did_game_over == true) {
                    return;
                }
                did_game_over = false; // prevents repetition of "GAME OVER" messages
                //If null we are dead, so simply say that and do nothing else
		if(strings_for_communication == null){return;}
		clear();
		for(int i = 0; i != strings_for_communication.size(); ++i){
                    String temp = "";
                    if(i == 0) {
                        temp = temp.concat("Click here, then\n");
                    } 
                    temp = temp.concat("Press " + String.valueOf(i) + ": " );
                    //if (i == strings_for_communication.size() - 1) {
                    //    temp = temp.concat("\n^ After selecting an option, click the game window to resume ^");
                    //}
			view_content_strings_.add(strings_for_communication.get(i));
			headings_.add(temp);
		}
                view_content_strings_.add("^ After selecting an option, click the game window to resume ^");
		result_ = new ArrayList<String>(headings_.size());
		addWhiteSpace(result_);
		for(int i = 0;i!=headings_.size();++i){
			result_.add(headings_.get(i)+view_content_strings_.get(i));
		}
		addWhiteSpace(result_);

	}
        
	private void addWhiteSpace(ArrayList<String> list){
		for(int i = 0; i!=whiteSpaceCount_;++i){list.add(System.lineSeparator());}
	}
	private void clear(){
		view_content_strings_.clear();
		headings_.clear();
	}
	public String getChoice(int i){
		if(i < 0){return "";}
		if(i < view_content_strings_.size()){
			return view_content_strings_.get(i);
		}
		return "";
	}
	public ArrayList<String> getContents(){

		return result_;
	}

}
