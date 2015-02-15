/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package src;

/**
 *
 * @author JohnReedLOL
 */
public abstract class AreaFunctor {

    /**
     * casts an area effect
     * @author Reed, John-Michael
     * @param x_center - center of area effect
     * @param y_center - center of area effect
     * @param radius - diameter/2 of area effect
     */
    public void effectArea(int x_center, int y_center, int radius, int strength) {
        int left_edge = x_center - radius;
        int right_edge = x_center + radius;
        int top = y_center + radius;
        int bottom = y_center - radius;
        for (int i = top; i >= bottom; --i) {
            for (int j = left_edge; j <= right_edge; ++j) {
                repeat(j, i, strength);
            }
        }
    }

    /**
     * single iteration of area effect
     *
     * @author Reed, John-Michael
     * @param x_pos - x position to be effected
     * @param y_pos - y position to be effected
     * @param strength - how much to effect it by
     */
    abstract public void repeat(int x_pos, int y_pos, int strength);
}
