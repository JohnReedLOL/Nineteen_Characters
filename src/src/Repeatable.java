/**
*
*/
package src;

import src.model.Vector2;

/**
* @author Matthew and Reed, John
*
*/
public abstract class Repeatable {
    void executeLoop(Vector2 start, Vector2 end) {
        executeLoop(start.x(), start.y(), end.x(), end.y());
    }

    void executeLoop(int top_left_x, int top_left_y, int width, int height) {
        for (int y = top_left_y; y > top_left_y - height; --y) {
            for (int x = top_left_x; x < top_left_x + width; ++x) {
                toRepeat(x, y);
            }
        }
    }

    abstract void toRepeat(int x, int y);
}