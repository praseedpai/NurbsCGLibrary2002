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
*
*
*
*
*
*/

public class NurbSurface extends Shape3D 
{

     /*
     *
     *
     *
     */
     public NurbSurface( int uSegments , int vSegments , float[] uKnotSequence ,
                         float[]  vKnotSequence , int numUControlPoints , int numVControlPoints , Point4[] controlPoints ,
                         Appearance app )
     {

         //////////////////////////////////////////////
         //
         //
         //
         //
         //
         // First create the Knots for the U Knot
         //
         int numControlPoints = numUControlPoints;
         int numKnots         = uKnotSequence.length;
         int order            = numKnots - numControlPoints;
           
         //////////////////////////////////////////////////////
         //
         // Use System.arrayCopy later
         //  
         float[] knot = new float[numKnots];
         for (int i=0; i<numKnots; i++) 
         {
             knot[i] = uKnotSequence[i];
         }
         Knot u = new Knot(order, numControlPoints);
         u.setKnots(knot);

         ///////////////////////////////////////////////////////////
         //
         //  Create the Knots for the V Knot   
         //
         //
         numControlPoints = numVControlPoints;
         numKnots         = vKnotSequence.length;
         order            = numKnots - numControlPoints;

         knot = new float[numKnots];
         for (int i=0; i<numKnots; i++) 
         {
            knot[i] = vKnotSequence[i];
         }
         Knot v = new Knot(order, numControlPoints);
         v.setKnots(knot);

         /////////////////////////////////////////////////////////////////
         //
         //
         //
         //
         // Second create the ControlNet
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
                                                v.getNumControlPoints(), 
                                                points);

         ////////////////////////////////////////////////////////////
         // Finally we can construct the NURBS
         //
         //
         Nurbs  shape    = new Nurbs(u, v, controlNet);
         
         //////////////////////////////////////////////////////////////////////////
         // Now tessellate to desired smoothness
         //
         Nurbs  temp     = shape.tessellate(uSegments,
                                           vSegments );


         float[] coord = temp.getCoordinates();
         int[] indices = temp.getCoordinateIndices(); 
  
  
         IndexedTriangleArray Ita  = new IndexedTriangleArray( coord.length/3 , TriangleArray.COORDINATES ,
                                                      indices.length );

         Ita.setCoordinates(0,coord );

         Ita.setCoordinateIndices(0,indices);

         this.setGeometry(Ita);  
         this.setAppearance(app);


           
  
 



     }
  














}





