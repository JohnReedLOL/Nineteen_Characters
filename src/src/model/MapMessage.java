/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package src.model;

import java.io.Serializable;
import src.Key_Commands;

/**
 *
 * @author JohnReedLOL
 */
public class MapMessage implements Serializable {

    private static final long serialVersionUID = 100;
    
    public MapMessage(String uid, String u, Key_Commands c, int w, int h, String o) {
        unique_id_ = uid;
        username_ = u;
        command_ = c;
        width_from_center_ = w;
        height_from_center_ = h;
        optional_text_ = o;
    }
    
    final String unique_id_;
    final String username_;
    final Key_Commands command_;
    final int width_from_center_;
    final int height_from_center_;
    final String optional_text_;
}
