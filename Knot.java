/*
*
*
*  <Copyright notice goes here>
*
*
*
*
*
*
*/

package hsdc.nurbs;

/**
*  Constarints :-
*
*  Knot Array value should be always ascending. 
*  Number of Knots ( elements in Knot Array ) should be Order + numControlPoints
*
*
*
*
*
*/

public class Knot implements Cloneable 
{

    protected int     order;

    protected int     numControlPoints;

    protected int     numKnots;

    protected float[] knot;

    public static final float TOLERANCE = 1.0E-7f;


    /**
    * Constructor.
    * @param order the order of the Knot
    * @param numControlPoints the number of control points
    */

    public Knot(int order, int numControlPoints) {
        this.order            = order;
        this.numControlPoints = numControlPoints;
        this.numKnots         = order + numControlPoints;
    }

    /**
    * Constructor.
    * @param order the order of the Knot
    * @param numControlPoints the number of control points
    * @param knot the array of knot values
    * @exception java.lang.IllegalArgumentException when the
    * number of knots does not equal the order plus the number
    * of control points
    */
    public Knot(int order, int numControlPoints, float[] knot) {
        String errorString = getClass().getName() + ":  " +
                             "Number of knots in array must equal " +
                             "order + number of control points";
        if (knot.length != order + numControlPoints)
            throw new IllegalArgumentException(errorString);

        this.order            = order;
        this.numControlPoints = numControlPoints;
        this.numKnots         = order + numControlPoints;
        this.knot             = knot;
    }

    /**
    * Return number of control points.
    * @return the number of control points
    */

    public int getNumControlPoints() {
        return numControlPoints;
    }

    /**
    * Accessor method for the knot[] array
    * @param knot the array of knots
    */

    public void setKnots(float[] knot) {
        String errorString = getClass().getName() + ":  " +
                             "Number of knots in array must equal " +
                             "order + number of control points";
        if (knot.length != order + numControlPoints)
            throw new IllegalArgumentException(errorString);

        this.knot     = knot;
        this.numKnots = knot.length;
    }


    /**
    * Accessor method for the knot[] array
    * @return an array of knots
    */

    public float[] getKnots() {
        return knot;
    }

     /**
     * Determine where in the knot sequence t lies.
     * The knot parameter t must lie within the half-open
     * interval <tt>knot[0] <= t < knot[knot.length-1]</tt>.
     * @param knot the array of knot values
     * @param t the value to compare
     * @return the array index of the highest-index knot
     * which is less than or equal to t
     * @exception java.lang.IllegalArgumentException when the
     * knot parameter t is not within the half-open interval
     */
    protected static int interval(float[] knot, float t) {
        //
        // knot is an ordered (non-decreasing) array, so
        // we could make this search a lot smarter, but
        // doesn't pay unless knot sequence is large.
        //
        if (knot[0]<=t) {
            for (int i=0; i<knot.length-1; i++) {
                if (t<knot[i+1])
                    return i;
            }
        }
        throw new IllegalArgumentException();
    }


    /**
     * Produce numKnots knots between tmin and tmax, with
     * tmin, tmax repeated order times and the remaining knots
     * distributed uniformly between.  Existing knot sequence
     * will be overwritten.
     * @param tmin the minimum knot value
     * @param tmax the maximum knot value
     */
    protected void makeKnots(float tmin, float tmax) {
        //
        // Replicate start and end knots order times
        //
        knot = new float[numKnots];
        for (int i=0; i<order; i++) {
            knot[i]                    = tmin;
            knot[i + numControlPoints] = tmax;
        }

        //
        // Evenly distribute remainder
        //
        float interval = (tmax - tmin) / (numKnots - order - order + 1);
        for (int i=order; i<numControlPoints; i++)
            knot[i] = tmin + (i - order + 1) * interval;
    }


    /**
     * This function returns true if one and two
     * are equal, or their difference is less than
     * the constant tolerance.
     * @param one the first float
     * @param two the second float
     * @return true if approximately equal
     */
    private boolean equal(float one, float two) {
        return ((float) Math.abs(one - two) <= TOLERANCE);
    }


    /**
     * Merge the knot sequence knot2 into the current
     * knot.  Do not add knot parameters from knot2
     * which duplicate parameters already in this knot.
     * @param knot2 the Knot to merge into this
     * @return a new Knot
     */
    protected Knot unionKnots(Knot knot2) {
        boolean done = false;
        int numNewKnots = 0;
        int i1  = 0;
        int i2  = 0;
        float t;

        //
        // If no knot duplication, we will have at most
        // this.numKnots + knot2.numKnots, so allocate
        // enough room.
        //
        float[] newknot = new float[this.numKnots + knot2.numKnots];

        while (!done) {
            if (equal(this.knot[i1], knot2.knot[i2])) {
                t = this.knot[i1];
                i1++;
                i2++;
            }
            else {
                if (this.knot[i1] < knot2.knot[i2]) {
                    t = this.knot[i1];
                    i1++;
                }
                else {
                    t = knot2.knot[i2];
                    i2++;
                }
            }
            newknot[numNewKnots] = t;   // store knot parameter
            numNewKnots++;
            done = (i1 >= this.numKnots || i2 >= knot2.numKnots);
        }

        //
        // If both are equal copy one and increment both,
        // otherwise take the smaller of the two knots
        // 
        if (i1 < this.numKnots) {
            for (int i=i1; i<this.numKnots; i++) {
                newknot[numNewKnots] = this.knot[i];
                numNewKnots++;
            }
        }
        else {
            for (int i=i2; i<knot2.numKnots; i++) {
                newknot[numNewKnots] = knot2.knot[i];
                numNewKnots++;
            }
        }
        float[] temp = new float[numNewKnots];
        System.arraycopy(newknot, 0, temp, 0, numNewKnots);
        return new Knot(order, numNewKnots - order, temp);
    }


    /**
     * Performs a deep copy of this Knot.
     * @return a new instance of Knot
     */
    public Object clone() {
        try {
            Knot klone = (Knot) super.clone();
            System.arraycopy(knot, 0,   klone.knot, 0, numKnots);
            return klone;
        }
        catch (CloneNotSupportedException e) {
            String errorString = getClass().getName() + ":  " +
                                 "CloneNotSupportedException " +
                                 "for a Cloneable class";
            throw new InternalError(errorString);  // this should never happen
        }
    }


   

}