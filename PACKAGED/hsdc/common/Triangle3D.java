/*
*
*
*  <CopyRight Notice Goes Here>
*
*
*
*
*
*/
package hsdc.common;

import javax.media.j3d.*;
import javax.vecmath.*;

/**
*  The Following JavaBean is used to create a 3D Triangle 
*
*
*
*
*
*/

public class Triangle3D   extends   Shape3D
{

    public Triangle3D()
    {
        ///////////////////////////////////////////////////////
        //
        //  Allocate Vertex
        //
        // 
        Point3f[] TriPts = new Point3f[6];
        TriPts[0] = new Point3f(-0.5f,0.0f,0.5f );
        TriPts[1] = new Point3f(0.5f,0.0f,0.5f );
        TriPts[2] = new Point3f(0.0f,0.5f,0.5f );
        TriPts[3] = new Point3f(-0.5f,0.0f,0.5f );
        TriPts[4] = new Point3f(0.0f,0.0f,0.5f );

        TriPts[5] = new Point3f(0.0f,0.5f,0.5f );
        ////////////////////////////////////////////////////////////
        //  Allocate Color
        //
        // 
        Color3f red    = new Color3f(1.0f,0.0f,0.0f);
        Color3f green  = new Color3f(0.0f,1.0f,0.0f);
        Color3f blue   = new Color3f(0.0f,0.0f,1.0f); 
          
        Color3f[] colors = new Color3f[6];

        colors[0] = red;
        colors[1] = green;
        colors[2] = blue;
        colors[3] = red;
        colors[4] = green;
        colors[5] = blue;
        
        Vector3f frnormal   = new Vector3f(0.0f,0.0f,1.0f);
        Vector3f bcknormal  = new Vector3f(0.0f,0.0f,-1.0f);

        Vector3f[] normals =  new Vector3f[6];

        normals[0] = frnormal;   
        normals[1] = frnormal;   
        normals[2] = frnormal;   
        normals[3] = bcknormal;   
        normals[4] = bcknormal;   
        normals[5] = bcknormal;   

        TriangleArray TriArr = new TriangleArray( 6 , GeometryArray.COORDINATES | 
                                                      GeometryArray.COLOR_3 |
                                                      GeometryArray.NORMALS );

        TriArr.setCoordinates(0,TriPts);
        TriArr.setColors(0,colors);
        TriArr.setNormals(0,normals);
        this.setGeometry(TriArr);      

    } 








}

