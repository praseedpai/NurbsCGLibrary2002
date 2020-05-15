/**
*
*   
*
*   <Copy right notice goes here >
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

package hsdc.common;

import javax.media.j3d.*;
import javax.media.j3d.TriangleArray;

import javax.vecmath.*;
import java.lang.*;
import com.sun.j3d.utils.geometry.*;


/**
*
*
*  This is an Attempt to create a Primitive Akin to Open-GL  GL_POLYGON Primitive
*  Java3D only supports rendering of Triangles or convex Quads which can be drawn as triangle. 
*  Following class will take an Array of Polygon as an argument to the constructor and Tesselates
*  it into Triangle using Triangulator class packaged with Java3D utils package.
*
*
*
*/

public class PolygonArray  
{
   /**
    *
    *
    *
    *
    *
    */    
    public static GeometryArray GeometryArrayFromPolygon(float[]  coord , int[] StripCounts) throws Exception
    {
         GeometryInfo geom = null;  
         int length = coord.length/3;
         if ( length <  3 )  
         {
             throw new RuntimeException("Polygon needs at least 3 vertex");
         } 
  
         try {
          geom = new GeometryInfo( GeometryInfo.POLYGON_ARRAY );
          geom.setCoordinates(coord);
          geom.setStripCounts(StripCounts);   
          Triangulator Ti = new Triangulator();
          Ti.triangulate(geom);
          NormalGenerator Tn = new NormalGenerator();
          Tn.generateNormals(geom);
          Stripifier sf = new Stripifier();
          sf.stripify(geom); 
         }
         catch(Exception e ) {
            throw e;  
         } 
         return geom.getGeometryArray();

    }

}





