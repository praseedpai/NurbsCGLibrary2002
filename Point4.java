/**
*
*
*  <Copy right Notice goes here>
*
*
*
*
*
*
*/

package hsdc.nurbs;

import java.lang.*;

/**
*
*  A Custom Point class to do Nurbs Specific Manipulations 
*
*  This is used to represent Homogeneous coordinates in 3 space
*   
*  Java3D ( vecmath package ) Point4f does not support Rotation  Operation
*  
*  That is why this Custom Point4 class is being used  
*
*/

public class Point4 
{
    /**
    *  constant - minimum value ( used as a gauard )
    *
    */

    public static final float TOLERANCE = 1.0E-7f;

    /**
    * The x coordinate value
    *
    */

    public float x;


    /**
    * The y coordinate value
    *
    */

    public float y;


    /**
    *  The z coordinate value
    *
    */

    public float z;

    /**
    * The w coordinate. ( it can  be considered as a scale factor );
    * This is the last element in Homogeneous co-ordinate
    */

    public float w;

   
    /**
     * Constructor.  Initializes coordinates to 0.0f.
     */
    public Point4() {
        this(0.0f, 0.0f, 0.0f, 0.0f);
    }


    /**
     * Constructor.
     * @param x the x coordinate.
     * @param y the y coordinate.
     * @param z the z coordinate.
     * @param w the w coordinate.
     */
    public Point4(float x, float y, float z, float w) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.w = w;
    }


    /**
     * Copy Constructor.
     * @param point a Point4 object to copy.
     */
    public Point4(Point4 point) {
        this.x = point.x;
        this.y = point.y;
        this.z = point.z;
        this.w = point.w;
    }

    /**
     * Set the values of this Point4.
     * @param x the x coordinate.
     * @param y the y coordinate.
     * @param z the z coordinate.
     * @param w the w coordinate.
     */
    public void setValue(float x, float y, float z, float w) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.w = w;
    }


    /**
     * Check to see if this Point4 approximately equals
     * another.  "Approximately" means within one part
     * in ten million.  This is useful when comparing
     * to within roundoff error.
     * @param p1 the Point4 to compare
     */
    public boolean approxEquals(Point4 p1) {
        if (Math.abs(this.x - p1.x) <= TOLERANCE &&
            Math.abs(this.y - p1.y) <= TOLERANCE &&
            Math.abs(this.z - p1.z) <= TOLERANCE &&
            Math.abs(this.w - p1.w) <= TOLERANCE    )
            return true;
        return false;
    }


    /**
     * Add another Point4 to this one.
     * @param p1 the Point4 to add
     * Second Point is used to depict a Vector  
     */
    public void add(Point4 p1) {
        this.x += p1.x;
        this.y += p1.y;
        this.z += p1.z;
        this.w += p1.w;
    }


    /**
     * Translate this Point4.
     * @param x the X translation component
     * @param y the Y translation component
     * @param z the Z translation component
     * Too often this.w is 1   
     */
    public void translate(float x, float y, float z) {
        this.x = this.x + this.w*x;
        this.y = this.y + this.w*y;
        this.z = this.z + this.w*z;
    }


    /**
     * Scale this Point4 uniformly.
     * @param scale the uniform scale factor
     * Delegates the call to scale routine which takes 3 factors ( non- uniform scale )
     */
    public void scale(float scale) {
        this.scale(scale, scale, scale);
    }


    /**
     * Scale this Point4 non-uniformly.
     * @param xscale the x scale factor
     * @param yscale the y scale factor
     * @param zscale the z scale factor
     */
    public void scale(float xscale, float yscale, float zscale) {
        this.x *= xscale;
        this.y *= yscale;
        this.z *= zscale;
    }


    /**
     * Apply a rotation transformation to this Point4.
     * @param xaxis the X component of the rotation axis
     * @param yaxis the Y component of the rotation axis
     * @param zaxis the Z component of the rotation axis
     * @param angle the angle of the rotation in radians
     * This routine is analogous to OpenGL glrotatef function  
     */
    public void rotate(float xaxis, float yaxis, float zaxis, float angle) {
        float cos = (float) Math.cos(angle);
        float sin = (float) Math.sin(angle);
        float x = this.x * ( xaxis*xaxis  +  cos*(1f - xaxis*xaxis)) +
                  this.y * ( zaxis*sin    +  xaxis*yaxis*(1f - cos)) +
                  this.z * ( yaxis*sin    +  xaxis*zaxis*(1f - cos));
        float y = this.x * ( zaxis*sin    +  xaxis*yaxis*(1f - cos)) +
                  this.y * ( yaxis*yaxis  +  cos*(1f - yaxis*yaxis)) +
                  this.z * (-xaxis*sin    +  yaxis*zaxis*(1f - cos));
        float z = this.x * (-yaxis*sin    +  xaxis*zaxis*(1f - cos)) +
                  this.y * ( xaxis*sin    +  yaxis*zaxis*(1f - cos)) +
                  this.z * ( zaxis*zaxis  +  cos*(1f - zaxis*zaxis));
        this.x = x;
        this.y = y;
        this.z = z;
    }

 




}

