/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package src.controller;
import java.io.BufferedReader;
import java.io.InputStreamReader;

import src.model.MapAvatar_Relation;
/**
 * Uses keyboard input to control the avatar
 * @author JohnReedLOL
 */
public final class AvatarController
{
    private final Avatar my_avatar_;
    
    AvatarController(Avatar avatar) {
        my_avatar_ = avatar;
    }
    
    private void runTheGame() throws Exception {
    	
    	BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
    	int last = 0;
    	int current = 0;
    	
    	while ( (current = in.read() ) != -1 ) {
			my_avatar_.input((char)last, (char)current);
			last = current;
    	}
    	
    	in.close();
    }
    
}
