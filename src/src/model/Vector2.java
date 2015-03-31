/**
 * Implementor: Alex Stewart
 */
package src.model;


/**
 * Vector2 objects represent a 2 dimensional, mathematical, integer vector or -
 * more abstractly - 2 integer numbers.
 * <p/>
 * This class is based off of the XNA game framework Vector2 implementation
 *
 * @author Alex Stewart
 */
public class Vector2 implements Comparable {
    // Class private values
    private Integer x_, y_;

// <editor-fold desc="CONSTRUCTORS" defaultstate="collapsed">

    /**
     * Constructs a new Vector2 object
     */
    public Vector2() {
        x_ = 0;
        y_ = 0;
    }

    /**
     * Constructs a new Vector2 object using the int values provided
     *
     * @param x The 'x' (first dimension) value of the vector
     * @param y The 'y' (second dimension) value of the vector
     */
    public Vector2(int x, int y) {
        x_ = x;
        y_ = y;
    }

    /**
     * Constructs a new Vector2 object using the Integer values provided
     *
     * @param x The 'x' (first dimension) value of the vector
     * @param y The 'y' (second dimension) value of the vector
     */
    public Vector2(Integer x, Integer y) {
        x_ = x;
        y_ = y;
    }
// </editor-fold>

// <editor-fold desc="CLASS ATTRIBUTES" defaultstate="collapsed">

    /**
     * GET a COPY of this vector's values
     * Use {@link #x()} or {@link #y()} to get single values
     *
     * @return A new Vector2 with this vector's values
     */
    public Vector2 value() {
        return new Vector2(x(), y());
    }

    /**
     * SET the values of this Vector2 object. Use {@link #setX(int x)} and
     * {@link #setY(int y)} to set values individually.
     *
     * @param x The 'x' (first dimension) value to set
     * @param y The 'y' (second dimension) value to set
     */
    public void setValue(int x, int y) {
        setValue(new Integer(x), new Integer(y));
    }

    /**
     * SET the values of this Vector2 object. Use {@link #setX(Integer X)} and
     * {@link #setY(Integer Y)} to set values individually.
     *
     * @param X
     * @param Y
     */
    public void setValue(Integer X, Integer Y) {
        x_ = X;
        y_ = Y;
    }

    /**
     * GET the 'x' value of this Vector2 as an Integer
     *
     * @return The 'x' (first dimension) value of this Vector2
     */
    public Integer x() {
        return x_;
    }

    /**
     * SET the 'x' value of this Vector2 to an int value
     *
     * @param x The 'x' (first dimension) value to SET
     */
    public void setX(int x) {
        setX(new Integer(x));
    }

    /**
     * SET the 'x' value of this Vector2 to an Integer value
     *
     * @param x The 'x' (first dimension) value to SET
     */
    public void setX(Integer x) {
        x_ = x;
    }

    /**
     * GET the 'y' value of this Vector2 as an Integer
     *
     * @return The 'y' (second dimension) value of this Vector2
     */
    public Integer y() {
        return y_;
    }

    /**
     * SET the 'y' value of this Vector2 to an int value
     *
     * @param y The 'y' (second dimension) value to SET
     */
    public void setY(int y) {
        setY(new Integer(y));
    }

    /**
     * SET the 'y' value of this Vector2 to an Integer value
     *
     * @param y The 'y' (second dimension) value to SET
     */
    public void setY(Integer y) {
        y_ = y;
    }
// </editor-fold>

// <editor-fold desc="PUBLIC METHODS" defaultstate="collapsed">

    /**
     * Calculates the rectangular area between this Vector2's component values.
     * <p>A = x * y</p>
     *
     * @return The result of the operation as an Integer
     */
    public Integer area() {
        return (x_ * y_);
    }

    public Double angle() {
        return Vector2.angleBetween(this, Vector2.zero());
    }

    /**
     * Gets the magnitude of this vector.
     * @return The magnitude of this vector, as a Double.
     */
    public Double magnitude() {
        return Math.sqrt(Math.pow(this.x(), 2) + Math.pow(this.y(), 2));
    }

    /**
     * Scales this Vector2's values by the scalar Double value, rounding down
     * <p>scalar * &lt;x, y&gt;</p>
     *
     * @param scalar The Double scalar value to apply to the values
     */
    public void scale(Double scalar) {
        x_ = (int) (x_.doubleValue() * scalar);
        y_ = (int) (y_.doubleValue() * scalar);
    }

    /**
     * Scales this Vector2's values by a scalar Integer value.
     * <p>scalar * &lt;x, y&gt;</p>
     *
     * @param scalar The scalar value as an Integer
     */
    public void scale(Integer scalar) {
        x_ *= scalar;
        y_ *= scalar;
    }

    // TODO: IMPLEMENT
    public Vector2 unitVector() {
        return Vector2.zero();
    }

    /**
     * Calculates the difference between two provided Vector2 values
     * <p><p>&lt;c.x, c.y&gt; = &lt;a.x - b.x, a.y - b.y&gt;</p>
     *
     * @param a The minuend in the operation
     * @param b The subtrahend in the operation
     * @return A new Vector2 object with the result of the operation
     */
    public static Vector2 subtract(Vector2 a, Vector2 b) {
        Vector2 ret = a;
        ret.setX(a.x() - b.x());
        ret.setY(a.y() - b.y());
        return ret;
    }

    /**
     * Returns a String with the values of this Vector2 contained within.
     * <p><strong>FORMAT:</strong> &lt;x, y&gt;</p>
     */
    @Override
    public String toString() {
        return ("<" + x_.toString() + ", " + y_.toString() + ">");
    }


// </editor-fold>

// <editor-fold desc="STATIC METHODS" defaultstate="collapsed">

    /**
     * Adds the two supplied Vector2 values and returns the result as a new
     * Vector2. This method follows normal vector addition rules.
     * <p>&lt;c.x, c.y&gt; = &lt;a.x + b.x, a.y + b.y&gt;</p>
     *
     * @param a The first vector to add.
     * @param b The second vector to add.
     * @return The result of the addition as a new Vector2
     */
    public static Vector2 add(Vector2 a, Vector2 b) {
        Vector2 ret = a;
        ret.setX(a.x() + b.x());
        ret.setY(a.y() + b.y());
        return ret;
    }

    /**
     * Returns the angle that a Vector2 a is above a Vector2 base. The returned value is in units
     * of radians.
     * @param a The Vector2 for which the angle should be gotten
     * @param base The Vector2 base-vector from which the angle should span
     * @return the angle that a Vector2 a is above a Vector2 base. The returned value is in units
     * of radians.
     */
    public static Double angleBetween(Vector2 a, Vector2 base) {
        return Math.acos(Vector2.dot(a, base) / (a.magnitude() * base.magnitude()));
    }

    /**
     * Calculates the distance between two vector objects' values. This
     * operation wraps the {@link this.subtract(Vector2, Vector2)} operation.
     * <p>D = target - reference</p>
     *
     * @param target    The target of the distance call
     * @param reference The reference (0-point) of the distance call
     * @return The result as a new Vector2 object
     */
    public static Vector2 distance(Vector2 target, Vector2 reference) {
        return Vector2.subtract(target, reference);
    }

    /**
     * Calculates the vector dot product of two provided Vector2 objects.
     * <p>R = a.x * b.x + a.y * b.y</p>
     * Use {@link #scale(Integer)} to scale a vector.
     *
     * @param a The first vector in the operation
     * @param b The second vector in the operation
     * @return The result of the calculation as a scalar Integer value
     */
    public static Integer dot(Vector2 a, Vector2 b) {
        return (a.x() * b.x() + a.y() * b.y());
    }

    /**
     * Returns a new Vector2 of value &lt;1, 1&gt;
     *
     * @return Returns a new Vector2 of value &lt;1, 1&gt;
     */
    public static Vector2 one() {
        return new Vector2(1, 1);
    }

    /**
     * Returns a new Vector2 of value &lt;0,0&gt;
     *
     * @return a new Vector2 of value &lt;0,0&gt;
     */
    public static Vector2 zero() {
        return new Vector2(0, 0);
    }

// </editor-fold>

    // <editor-fold desc="INTERFACE IMPLEMENTATIONS" defaultstate="collapsed">
    /* COMPARABLE */
    // Floating point precision to use when comparing vectors
    private static int PREC = 4;

    /**
     * Compares the magnitudes of this Vector2 object to a provided object and
     * returns the result.
     *
     * @param o The Vector2 object to compare to
     * @return <p>-1: this.MAG < o.MAG</p>
     * <p>0: this.MAG == o.MAG</p>
     * <p>1: this.MAG > o.MAG</p>
     */
    @Override
    public int compareTo(Object o) {
        double a = Math.sqrt(Math.pow(x_.doubleValue(), 2) + Math.pow(y_.doubleValue(), 2));
        double b = Math.sqrt(Math.pow(((Vector2) o).x().doubleValue(), 2) + Math.pow(((Vector2) o).y().doubleValue(), 2));
// round the difference to PREC number of decimal places
        a = Math.round(((a - b) * (10 * PREC)) / (10 * PREC));
        if (a < 0)
            return -1;
        else if (a == 0)
            return 0;
        else
            return 1;
    }
    // </editor-fold>
}