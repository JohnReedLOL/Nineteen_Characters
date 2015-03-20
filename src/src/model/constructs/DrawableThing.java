package src.model.constructs;

import java.awt.Color;
import java.io.Serializable;
import java.util.ArrayList;

import src.model.MapDrawableThing_Relation;

/**
 * Drawable things are things that may be drawn on the screen. Such as
 * Entity/Item/etc.
 *
 * @author JohnReedLOL
 */
abstract public class DrawableThing implements Serializable{

    // names of items and terrain should be non-unique.
    // names of entities should be unique to fit in a hashmap.
    public final String name_;

    public String getName() {
        return name_;
    }

    protected DrawableThing(String name, char representation) {
        name_ = name;
        single_character_representation_ = representation;
        is_visible_ = true;
    }

    protected DrawableThing(String name, char representation, boolean is_visible) {
        name_ = name;
        single_character_representation_ = representation;
        is_visible_ = is_visible;
    }

    protected DrawableThing(String name, char representation, Color col_) {
        name_ = name;
        single_character_representation_ = representation;
        is_visible_ = true;
        color_ = col_;
    }

    protected DrawableThing(String name, char representation, Color col_, boolean is_visible) {
        name_ = name;
        single_character_representation_ = representation;
        is_visible_ = is_visible;
        color_ = col_;
    }

    abstract public MapDrawableThing_Relation getMapRelation();

    /**
     * returns the statspack(stats) without the items (default stats)
     *
     * @author Jessan
     */
    public DrawableThingStatsPack getStatsPack() {
        return this.stats_pack_;
    }

    abstract public boolean isPassable();

    private boolean is_visible_;

    /**
     * Get if Drawable Thing is viewable
     *
     * @return boolean
     */
    public boolean isVisible() {
        return this.is_visible_;
    }

    /**
     * Set if Drawable is viewable
     *
     * @param is_visible
     */
    public void setViewable(boolean is_visible) {
        is_visible_ = is_visible;
    }

    // For things that take up only  1 tile or need to appear on a minimap
    private char single_character_representation_;
    private Color color_ = Color.BLACK;//Default color to black

    public Color getColor() {
        return color_;
    }

    protected void setColor(Color col_) {
        color_ = col_;
    }

    /**
     * Get character representation.
     *
     * @return Character being used to represent this Drawable Thing.
     */
    public char getRepresentation() {
        return this.single_character_representation_;
    }

    /**
     * Set character representation.
     *
     * @param c
     */
    public void setRepresentation(char c) {
        single_character_representation_ = c;
    }

    private DrawableThingStatsPack stats_pack_ = new DrawableThingStatsPack();

    public ArrayList<String> getInteractionOptionStrings() {
        ArrayList<String> options = new ArrayList<String>();
        return options;
    }

    public ArrayList<String> getConversationStarterStrings() {
        ArrayList<String> options = new ArrayList<String>();
        return options;
    }

    public ArrayList<String> getConversationContinuationStrings(String what_you_just_said_to_me, Entity who_is_talking_to_me) {
        ArrayList<String> options = new ArrayList<String>();
        return options;
    }

    public ArrayList<String> getListOfItemsYouCanUseOnMe() {
        ArrayList<String> options = new ArrayList<String>();
        return options;
    }

    public ArrayList<String> endConversation() {
        ArrayList<String> silence = new ArrayList<>();
        silence.add(" [ End of Conversation]");
        return silence;
    }
}
