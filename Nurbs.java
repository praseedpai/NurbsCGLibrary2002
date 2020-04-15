/*
*
*
*
*
*
*
*
*
*
*
*/
package hsdc.nurbs;


/**
* NURBS curve and surface
* @author   unascribed
* @version  1.0
*
*
*
*/

public class Nurbs implements Cloneable {

    protected Knot       u;              // Horizontal Knot Array
    protected Knot       v;              // Vertical   Knot Array
    protected ControlNet controlNet;     // Control Grid 

    /**
    *
    *
    *  Do nothing constructor
    *
    *
    */
    
    public Nurbs() 
    {
     
    }

    /**
    * NURBS Constructor.
    * @param u the U knot sequence
    * @param v the V knot sequence
    * @param controlNet the array of Point4 control points
    * @exception java.lang.IllegalArgumentException when the
    *
    *
    */
    public Nurbs(Knot u, Knot v, ControlNet controlNet) 
    {
        //////////////////////////////////////////////////////////////
        //
        // Check for consistency
        //
        String errorString = getClass().getName() + ":  " +
                             "Number of knots in array must equal " +
                             "order + number of control points";
          

        if (u.numControlPoints != controlNet.numUControlPoints ||
            v.numControlPoints != controlNet.numVControlPoints   )
        {  
            throw new IllegalArgumentException(errorString);
        } 
        //////////////////////////////////////////////////////////////////
        //
        //
        //
        //
        this.u          = u;
        this.v          = v;
        this.controlNet = controlNet;
       
     }

     /**
     * Performs a deep copy of this NURBS.
     * @return a new instance of Nurbs
     */
     public Object clone() {
         try {
             Nurbs n      = (Nurbs) super.clone();  // Clone the base class
             n.u          = (Knot) u.clone();       // Clone the u knot
             n.v          = (Knot) v.clone();       // Clone the v knot
             n.controlNet = (ControlNet) controlNet.clone(); // Clone the Control Net
             return n;
         }
         catch (CloneNotSupportedException e) {
             String errorString = getClass().getName() + ":  " +
                                 "CloneNotSupportedException " +
                                 "for a Cloneable class";
             throw new InternalError(errorString);  // this should never happen
         }
     }

 
    /**
    * Access the number of U knots.
    * @return the number of U knots
    */
    public int getNumUKnots() {
        return u.numKnots;
    }


    /**
    * Access the number of V knots.
    * @return the number of V knots
    */
    public int getNumVKnots() {
        return v.numKnots;
    }


    /**
    * Access the NURB order in U.
    * @return the order in U of the NURB
    */
    public int getUOrder() {
        return u.order;
    }
    /**
    * Access the NURB order in V.
    * @return the order in V of the NURB
    */
    public int getVOrder() {
        return v.order;
    }

    /**
    * Access the number of U control points.
    * @return the number of U control points
    */

    public int getNumUControlPoints() {
        return controlNet.numUControlPoints;
    }
    /**
    * Access the number of V control points.
    * @return the number of V control points
    */
    public int getNumVControlPoints() {
        return controlNet.numVControlPoints;
    }
    /**
    * Access the U knot sequence.
    * @return the U knot sequence
    */

    public Knot getUKnot() {
        return u;
    }

    /**
    * Access the V knot sequence.
    * @return the V knot sequence
    */

    public Knot getVKnot() {
        return v;
    }


    /**
    * Set the U knot sequence.
    * @param u the new U knot sequence
    */

    public void setUKnot(Knot u) {
        this.u = u;
    }


    /**
    * Set the V knot sequence.
    * @param v the new V knot sequence
    */

    public void setVKnot(Knot v) {
        this.v = v;
    }

    /**
    * Set the ControlNet.
    * @param controlNet the new ControlNet
    */

    public void setControlNet(ControlNet controlNet) {
        this.controlNet = controlNet;
    }


    /**
    * Scale this NURBS uniformly.
    * @param scale the scale
    */

    public void scale(float scale) {
        controlNet.scale(scale);
    }

    /**
    * Scale this NURBS non-uniformly.
    * @param scale the scale
    */

    public void scale(float xscale, float yscale, float zscale) {
        controlNet.scale(xscale, yscale, zscale);
    }


    /**
    * Translate this NURBS.
    * @param x the translation in X
    * @param y the translation in Y
    * @param z the translation in Z
    */

    public void translate(float x, float y, float z) {
        controlNet.translate(x, y, z);
    }
    /**
    * Rotate this NURBS.
    * @param x the X component of the rotation axis
    * @param y the Y component of the rotation axis
    * @param z the Z component of the rotation axis
    * @param theta the rotation in radians
    */

    public void rotate(float x, float y, float z, float theta) {
        controlNet.rotate(x, y, z, theta);
    }


    /**
    * Transpose this NURBS by swapping U and V.
    */

    public void transpose() {
        Knot temp;

        temp = u;
        u    = v;
        v    = temp;

        controlNet.transpose();
    }

    /**
    * Refine this NURBS in the U and V directions by inserting
    * new knots and control points.
    * Employs recursive subdivision using the Oslo algorithm.
    * Refinement is done through recursive subdivision by adding
    * uSegments knots in the U direction and vSegments knots in
    * the V direction.  (This also adds uSegments*vSegments
    * control points since the order remains constant.)
    * @param uSegments the number of new control points
    * @param vSegments the number of new control points
    * @return the refined Nurbs
    *   
    */

    public Nurbs tessellate(int uSegments, int vSegments) 
    {
        //////////////////////////////////////////////////////////////////
        // Declare the return Nurbs.  It initially refers
        // to "this" to account for the case where
        // uSegments == vSegments == 0 ( Nothing to do and extremeties )
        //
        //
        //

        Nurbs temp = this;

        ///////////////////////////////////////////////////////////////// 
        //
        // Tesselate in U direction first ( to increase granularity of Net )
        //

        if (uSegments > 0) 
        {
            temp = this.tessellateU(uSegments);
        }

        //////////////////////////////////////////////////////////////////////
        //
        //
        //
        //
        if (vSegments > 0) 
        {
            ///////////////////////////////////////////////////////////////
            // If this is a surface, transpose (swap U, V),
            // then tessellate in new U direction (= old V direction)
            //

            if (uSegments <=0)
                temp = (Nurbs) this.clone();

            temp.transpose();

            Nurbs temp2 = temp.tessellateU(vSegments);    

            ////////////////////////////////////////////////////////////////////
            // Transpose back to its original configuration
            // and return
            //
            temp2.transpose();
            return temp2;
        }
        else {

            return temp;  // We are finished for a surface 
        }

    }

    /**
    * 
    * Refine this NURBS in the U direction by inserting new knots
    * and control points.
    * Refinement is done via recursive subdivision by adding
    * segments new knots.
    * @param uSegments the number of new control points
    * @return the refined Nurbs
    *
    *
    */
    private Nurbs tessellateU(int uSegments) 
    {

        Nurbs temp    = new Nurbs();

        /////////////////////////////////////////////////////////////
        // First add new knots to U knot sequence
        //
        int uOrder    = this.getUOrder();
        int numUKnots = Math.max(uSegments, this.getUOrder()*2 +1);
        int numUControlPoints = numUKnots - this.getUOrder();

        temp.u = new Knot(uOrder, numUControlPoints);
        temp.u.makeKnots(u.knot[0], u.knot[u.numKnots-1]);
        temp.u = temp.u.unionKnots(this.u);

        ///////////////////////////////////////////////// 
        // Copy the V knot sequence
        //
        temp.v        = (Knot) v.clone();
 
        //////////////////////////////////////////////////
        //
        //  Create a new Control Net
        //
        //
        Point4[][] tmppoints = new Point4[temp.u.numControlPoints]
                                         [temp.v.numControlPoints];

        for (int i=0; i<temp.u.numControlPoints; i++) 
            for (int j=0; j<temp.v.numControlPoints; j++) 
                tmppoints[i][j] = new Point4();

        ////////////////////////////////////////////////////////////
        //
        //
        //
        //

        ControlNet tmpcontrol = new ControlNet(temp.u.numControlPoints,
                                               temp.v.numControlPoints,
                                               tmppoints);
        temp.setControlNet(tmpcontrol);

        ////////////////////////////////////////////////////////////////////
        //
        //
        //
        //
        //
        for (int i=0; i<temp.u.numControlPoints; i++) 
        {
            //////////////////////////////////////////////////////////////////
            // knotIndex tells us where this particular new knot lies
            // in the old knot series

            int knotIndex = Knot.interval(this.u.knot, temp.u.knot[i]);
            this.subdivide(temp, knotIndex, i);
        }
        
        return temp; 
  
    }   

    /**
    * Calculates the control points corresponding to new knots.
    * This is called once for each new knot and creates
    * numVControlPoints new control points.
    * @param temp the Nurbs which will contain the new control points
    * @param oldKnotIndex  the index of the old knot
    * @param newKnotIndex the index of the new knot
    *
    * 
    */
    protected void subdivide(Nurbs temp, int oldKnotIndex, int newKnotIndex) 
    {
        int ttsize = getUOrder();

        Point4[][] tmppoints = new Point4[getUOrder()][oldKnotIndex+1];

        for (int kk=0; kk<getNumVControlPoints(); kk++) 
        {
            for (int j=oldKnotIndex-getUOrder()+1; j<oldKnotIndex+1; j++) 
            {
                 tmppoints[0][j] = this.controlNet.controlPoints[j][kk];
            }

            for (int i=1; i<getUOrder(); i++) {
                for (int j=oldKnotIndex-getUOrder()+1+i; j<oldKnotIndex+1; j++) 
                {
                    float t1 = temp.u.knot[newKnotIndex  + getUOrder() - i] -
                               this.u.knot[j];

                    float t2 = this.u.knot[j + getUOrder() - i] -
                               temp.u.knot[newKnotIndex  + getUOrder() - i];

                    float tmul = 1.0f / (t1 + t2);
                    ///////////////////////////////////////////////////
                    // interpolate new control points using old
                    // control points

                    tmppoints[i][j] = new Point4(
                    (t1*tmppoints[i-1][j].x + t2*tmppoints[i-1][j-1].x) * tmul,
                    (t1*tmppoints[i-1][j].y + t2*tmppoints[i-1][j-1].y) * tmul,
                    (t1*tmppoints[i-1][j].z + t2*tmppoints[i-1][j-1].z) * tmul,
                    (t1*tmppoints[i-1][j].w + t2*tmppoints[i-1][j-1].w) * tmul);
                }
            }
            temp.controlNet.controlPoints[newKnotIndex][kk] =
                                tmppoints[getUOrder()-1][oldKnotIndex];
        }
    

    } 


     /**
     * Do a surface of revolution about the Y axis.
     * Assumes curve is in X-Y plane
     * The profile NURBS will first be projected onto the Z=0 plane,
     * the resulting projection will be revolved about the Y axis.
     * Currently, thetamin and thetamax are ignored and 0, 2*PI is
     * used in all cases.
     * @param thetamin the angle at which to start the revolution
     * @param thetamax the angle at which to end the revolution
     * @return a Nurbs surface.
     */
     public Nurbs revolve() {
        //////////////////////////////////////////////////////////////////////
        // Set up the parameters
        // 
        //
        int order            =  3;
        int numControlPoints =  9;
        int numKnots         = 12;
        ////////////////////////////////////////////////////////////////////
        // First create the Knots for the circular cross section
        //
        //

        float[] temp = { 0.00f, 0.00f, 0.00f,
                         0.25f, 0.25f,
                         0.50f, 0.50f,
                         0.75f, 0.75f,
                         1.00f, 1.00f, 1.00f };

        Knot tmpUKnot = new Knot(order, numControlPoints);
        tmpUKnot.setKnots(temp);

        ////////////////////////////////////////////////////////////////////
        // Create an Empty ControlNet (with default values )
        //
        Point4[][] tmppoints = new Point4[tmpUKnot.numControlPoints]
                                         [u.numControlPoints];
        for (int i=0; i<tmpUKnot.numControlPoints; i++) {
            for (int j=0; j<u.numControlPoints; j++) {
                tmppoints[i][j] = new Point4();
            }
        }

        //////////////////////////////////////////////////////////////////////
        // Copy profile curve,But before assigning project to Z-Axis
        //
        for (int i=0; i<u.numControlPoints; i++) {
            tmppoints[0][i].x = controlNet.controlPoints[i][0].x;
            tmppoints[0][i].y = controlNet.controlPoints[i][0].y;
            tmppoints[0][i].z = 0.0f;            // Project onto Z=0 plane
            tmppoints[0][i].w = controlNet.controlPoints[i][0].w;
        }
        
        ////////////////////////////////////////////////////////////////////
        //
        //  ww is the value of cos(45) and sin(45)
        //
        //

        float ww = (float) Math.sqrt(2.0)/2.0f;


        //
        // Sweep about Y axis.  The V direction will be
        // a circle around the Y axis.
        //

        for (int i=0; i<u.numControlPoints; i++) 
        {
            for (int j=1; j<tmpUKnot.numControlPoints; j++) {
                tmppoints[j][i].y = tmppoints[0][i].y;
            }

            float radius = tmppoints[0][i].x;
            float weight = tmppoints[0][i].w;

            int jj = 1;
            for (int j=0; j<4; j++) {
                tmppoints[jj  ][i].y *= ww;
                tmppoints[jj  ][i].w  = ww * weight;
                tmppoints[jj+1][i].w  = weight;
                jj += 2;
            }

            //////////////////////////////////////////////////////////
            //
            // Finding points on Circle ( at each octant )
            //  
            tmppoints[1][i].x =  radius * ww;
            tmppoints[1][i].z =  radius * ww;
            tmppoints[7][i].x =  radius * ww;
            tmppoints[7][i].z = -radius * ww;
            tmppoints[2][i].x =  0.0f;
            tmppoints[2][i].z =  radius;
            tmppoints[6][i].x =  0.0f;
            tmppoints[6][i].z = -radius;
            tmppoints[3][i].x = -radius * ww;
            tmppoints[3][i].z =  radius * ww;
            tmppoints[5][i].x = -radius * ww;
            tmppoints[5][i].z = -radius * ww;
            tmppoints[4][i].x = -radius;
            tmppoints[4][i].z =  0.0f;
            tmppoints[8][i].x =  radius;
            tmppoints[8][i].z =  0.0f;

         

        }
 
        ControlNet tmpcontrol = new ControlNet(tmpUKnot.numControlPoints,
                                               u.numControlPoints,
                                               tmppoints);
        return new Nurbs(tmpUKnot, (Knot) u.clone(), tmpcontrol);
 
     }     

 

    


    
    /**
    *
    *
    *
    *
    *
    */
    public float[] getCoordinates() {

        return controlNet.getCoordinates();       
    }

    /**
    *
    *
    *   Get The Coordinate indices
    *
    *
    */
    public int[] getCoordinateIndices() 
    {
        return controlNet.getCoordinateIndices();       
    }  

      
     

    

 
}
