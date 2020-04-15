/*
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

import javax.media.j3d.*;
import javax.vecmath.*;
import com.sun.j3d.utils.geometry.*;



/*
*
*  This class will draw a nurbs curve
*
*
*
*
*/

public class NurbsCurve extends Shape3D
{
    /**  
    *   ctor for NurbsCurve
    *   
    *
    *
    *
    *
    *
    */
    public NurbsCurve( int segments , float[]  knotSequence , Point4[] controlPoints , Appearance app )
    {
        ///////////////////////////////////////////////////////////////////////
        // First create the Knots
        //
        int numControlPoints = controlPoints.length;
        int numKnots         = knotSequence.length;
        int order            = numKnots - numControlPoints;

        float[] knot = new float[numKnots];
        for (int i=0; i<numKnots; i++) 
        {
            knot[i] = knotSequence[i];
        }

        Knot u = new Knot(order, numControlPoints);
        u.setKnots(knot);

        /////////////////////////////////////////////////////////////
        //
        //  Create a Fake verical Knot
        //
        //
        numControlPoints =  1;
        numKnots         =  1;
        order            =  0;                    // constant
        knot = new float[numKnots];
        knot[0]  = 0.00f;
        Knot v = new Knot(order, numControlPoints);
        v.setKnots(knot);
         
        //////////////////////////////////////////////////////////////////
        //
        //  Create the Control Net 
        //
        //
        //
        Point4[][] points = new Point4[u.getNumControlPoints()]
                                      [v.getNumControlPoints()];

        for (int i=0; i<u.getNumControlPoints(); i++) 
        {
            for (int j=0; j<v.getNumControlPoints(); j++) 
            {
                int index = (j + i*v.getNumControlPoints());
                points[i][j] = new Point4(controlPoints[index].x,
                                          controlPoints[index].y,
                                          controlPoints[index].z,
                                          controlPoints[index].w);
            }
        }


        ControlNet controlNet = new ControlNet(u.getNumControlPoints(),
                                               v.getNumControlPoints(), points);

        /////////////////////////////////////////////////////////////////////////
        // construct the NURBS and Tesselae
        //
        //
        //
        Nurbs temp = new Nurbs(u, v, controlNet);
        Nurbs curve = temp.tessellate(segments,0);
 
        ///////////////////////////////////////////////////////////////////////////
        //
        // Create a Line Strip Array to Render the Curve
        //
        //
        float[] coord = curve.getCoordinates();
        int [] strip = { (int) coord.length/3 };
        LineStripArray lsa = new LineStripArray(coord.length/3,
                                                LineStripArray.COORDINATES,strip );
        lsa.setCoordinates(0,coord );  
        this.setGeometry( lsa );  
        this.setAppearance(app);  

    } 

} 