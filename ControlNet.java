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
*/

package hsdc.nurbs;


/**
*   
* Representation of a network of control points for a
* NURBS curve or surface.
* @author   unascribed
* @version  1.0
*
* ControlNet is CAGD term and a (grid)  data structure used to store Triangular Patches
*
*/
public class ControlNet implements Cloneable 
{
    protected int        numUControlPoints;     // # of ctrl pts in horz. dir
    protected int        numVControlPoints;     // # of ctrl pts in Vert dir. 
    protected Point4[][] controlPoints;         // Control Point array 


    /* 
    * Private constructor defined so that super.clone() will
    * be able to copy member variables.
    */

    private ControlNet() 
    {

    }

    /**
    * Constructs a ControlNet from the specified Point4 array.
    * @param numUControlPoints the number of control points in the U
    *        direction
    * @param numVControlPoints the number of control points in the V
    *        direction
    * @param controlPoints the array of Point4 control points
    */

    public ControlNet(int numUControlPoints, int numVControlPoints,
                      Point4[][] controlPoints) {
        this.numUControlPoints = numUControlPoints;
        this.numVControlPoints = numVControlPoints;
        this.controlPoints = controlPoints;
    }

    /**
    * Constructs a ControlNet with the specified U, V dimensions.
    * @param numUControlPoints the number of control points in the U
    *        direction
    * @param numVControlPoints the number of control points in the V
    *        direction
    */

    public ControlNet(int numUControlPoints, int numVControlPoints) {
        this(numUControlPoints, numVControlPoints,
             new Point4[numUControlPoints][numVControlPoints]);
    }

    /**
    * Transpose the control points by swapping U and V.
    * Sort of Matrix Transpose operation  
    */

    public void transpose() {
        Point4[][] newPoints = new Point4[numVControlPoints][numUControlPoints];
        for (int i=0; i<numUControlPoints; i++) {
            for (int j=0; j<numVControlPoints; j++) {
                newPoints[j][i] = controlPoints[i][j];
            }
        }
        controlPoints = newPoints;
        int itemp = numUControlPoints;
        numUControlPoints = numVControlPoints;
        numVControlPoints = itemp;
    }


    /**
    * Scale the control points.
    * @param scale the scale
    */

    public void scale(float scale) {
        for (int i=0; i<numUControlPoints; i++) {
            for (int j=0; j<numVControlPoints; j++) {
                controlPoints[i][j].scale(scale);
            }
        }
    }


    /**
    * Scale the control points.
    * @param scale the scale
    */

    public void scale(float xscale, float yscale, float zscale) {
        for (int i=0; i<numUControlPoints; i++) {
            for (int j=0; j<numVControlPoints; j++) {
                controlPoints[i][j].scale(xscale, yscale, zscale);
            }
        }
    }


    /**
    * Translate the control points.
    * @param x the translation in x
    * @param y the translation in y
    * @param z the translation in z
    */

    public void translate(float x, float y, float z) {
        for (int i=0; i<numUControlPoints; i++) {
            for (int j=0; j<numVControlPoints; j++) {
                controlPoints[i][j].translate(x, y, z);
            }
        }
    }


    /**
     * Rotate the control points.
     * @param x the x component of the rotation axis
     * @param y the y component of the rotation axis
     * @param z the z component of the rotation axis
     * @param theta the rotation in radians
     */
    public void rotate(float x, float y, float z, float theta) {
        for (int i=0; i<numUControlPoints; i++) {
            for (int j=0; j<numVControlPoints; j++) {
                controlPoints[i][j].rotate(x, y, z, theta);
            }
        }
    }


    /**
     * Performs a deep copy of this ControlNet.
     */
    public Object clone() {
        try {
            ControlNet newnet = (ControlNet) super.clone();
            System.arraycopy(controlPoints, 0, newnet.controlPoints, 0,
                             numUControlPoints*numVControlPoints);
            return newnet;
        }
        catch (CloneNotSupportedException e) {
            String errorString = getClass().getName() + ":  " +
                                 "CloneNotSupportedException " +
                                 "for a Cloneable class";
            throw new InternalError(errorString);  // this should never happen
        }
    }


    
    /**
    *
    *  Get the Co-ordinates as arrays of float. Suitable to feed into
    *  Java3D rendering pipeline using IndexedTriangleArray 
    *
    *
    */
    public float[] getCoordinates() {

        float[] retVal = new float[numUControlPoints*numVControlPoints*3];

        /////////////////////////////////////////////////////////////////////////
        // Really, really need to sort through controlPoint
        // list here and eliminate duplicate points.  Otherwise
        // automatic normal generation gets screwed up.
        //
        int index = 0; 
 
        for (int i=0; i<numUControlPoints; i++) {
            for (int j=0; j<numVControlPoints; j++) {
                retVal[index++] = controlPoints[i][j].x/controlPoints[i][j].w;
                retVal[index++] = controlPoints[i][j].y/controlPoints[i][j].w; 
                retVal[index++] = controlPoints[i][j].z/controlPoints[i][j].w; 
                               
            }
        }
 
        return retVal;


    }

    /**
    *
    *
    *   Get The Coordinate indices to feed into Java3D rendering Pipe-line
    *   using IndexedTriangleArray data structure
    *
    */
    public int[] getCoordinateIndices() 
    {
       int[] indices = null;
        if (numVControlPoints > 1) {
            indices  = new int[6*(numUControlPoints-1)*(numVControlPoints-1)];
        }
        else {
            indices  = new int[numUControlPoints*numVControlPoints+1];
        }
        int   position = 0;
        for (int j=0; j<numUControlPoints-1; j++) {
            if (numVControlPoints >1) {
                for (int i=0; i<numVControlPoints-1; i++) {
                    int index = i + j*numVControlPoints;
                    indices[position++] = index;
                    indices[position++] = index+1;
                    indices[position++] = index+numVControlPoints;
                    indices[position++] = index+numVControlPoints;
                    indices[position++] = index+1;
                    indices[position++] = index+1+numVControlPoints;
                    
                }
            }
            else {
                indices[j] = j;
            }
        }
        if (numVControlPoints >1) {
            // Do nothing
        }
        else {
            indices[indices.length-2] = numUControlPoints-1;
            indices[indices.length-1] = -1;
        }
        return indices;




    }  

 

    





}