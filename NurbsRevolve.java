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

public class NurbsRevolve extends Shape3D
{
    /**  
    *   ctor for NurbsEvolve
    *   
    *
    *
    *
    *
    *
    */
    public NurbsRevolve( int segments , int profilesegments  , float[]  profileknotSequence , 
                                       Point4[] profilecontrolPoints , Appearance app )
    {
        ///////////////////////////////////////////////////////////////////////
        // First create the Knots
        //
        int numControlPoints = profilecontrolPoints.length;
        int numKnots         = profileknotSequence.length;
        int order            = numKnots - numControlPoints;

        float[] knot = new float[numKnots];
        for (int i=0; i<numKnots; i++) 
        {
            knot[i] = profileknotSequence[i];
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
                points[i][j] = new Point4(profilecontrolPoints[index].x,
                                          profilecontrolPoints[index].y,
                                          profilecontrolPoints[index].z,
                                          profilecontrolPoints[index].w);
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

        Nurbs temporary = temp.revolve();

        Nurbs surface = temporary.tessellate(segments,profilesegments);
 
        float[] coord = surface.getCoordinates();
        int[] indices = surface.getCoordinateIndices(); 
  
  
        IndexedTriangleArray Ita  = new IndexedTriangleArray( coord.length/3 , TriangleArray.COORDINATES ,
                                                      indices.length );

        Ita.setCoordinates(0,coord );

        Ita.setCoordinateIndices(0,indices);

        this.setGeometry(Ita);  
        this.setAppearance(app);

    } 

} 
