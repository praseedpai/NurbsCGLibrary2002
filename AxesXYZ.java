package hsdc.common;

import javax.media.j3d.*;

/*

  $Header: /var/cvsreps/sbuilder/./SOURCES/Impl/ch/isbiel/scenebuilder/nodes/java3d/AxesXYZ.java,v 2.0 1999/12/16 22:57:56 rufes1 Stable $

  Organisation:         Berner Fachhochschule / University of applied sciences
                        Biel School of Engineering and Architecture
                        CH-2501 Biel
                        Switzerland

  Copyright:            GNU General Public License

  Project:              SceneBuilder J3D

  Date:                 $Date: 1999/12/16 22:57:56 $

*/
/**
 * Class providing a simple shape3d showing the XYZ-axis system of 
 * the locale it is attached to. The X, Y and Z axis are arrowed  and lettered on 
 * the positive side. 
 *
 * @version $Revision: 2.0 $
 * @author $Author: rufes1 $
 */

//////
// The original version of this class was written by Claude Schwab.
// Thanks for supporting our diploma work with this.
//////
public class AxesXYZ extends Shape3D
{

    /**
      * Simple constructor with X-Axis red, Y-Axis green, Z-Axis blue and scale 1.
      */
    public AxesXYZ() {
        this(1f, 0f, 0f, 0f, 1f, 0f, 0f, 0f, 1f, 1f);
    }

    /**
      * Simple constructor with X-Axis red, Y-Axis green, Z-Axis blue.
      * The length of the axis defaults to 1m in both positive and 
      * negative direction on the axis (total length 2m). This length may be changed 
      * by specifying scale. A scale of 10 will produce 10m long axis. 
      *
      * @param scale factor for the initial size of the axis.
      */
    public AxesXYZ(float scale) {
        this(1f, 0f, 0f, 0f, 1f, 0f, 0f, 1f, 1f, scale);
    }


   /**
     * Specifies all colors in RGB-format for axis X, Y and Z.
     * Scale the size of the axis with parameter scale.
     *
     * @param rX,rY,rZ	red part for each axis
     * @param gX,gY,gZ	green part for each axis
     * @param bX,bY,bZ	blue part for each axis
     * @param scale		factor for the initial size of the axis
     *
     */
   public AxesXYZ(float rX, float gX, float bX,float rY, float gY, float bY,
 	          float rZ, float gZ, float bZ, float scale)
   {
      // couleurs des 3 axes
      float color[] = { rX,gX,bX, rX,gX,bX,     //X-Axis coloring

                        rX,gX,bX, rX,gX,bX,     //X-axis arrow coloring
                        rX,gX,bX, rX,gX,bX,
                        rX,gX,bX, rX,gX,bX,
                        rX,gX,bX, rX,gX,bX,

                        rX,gX,bX, rX,gX,bX,     //X-axis lettering coloring
                        rX,gX,bX, rX,gX,bX,

		                rY,gY,bY, rY,gY,bY,     //Y-Axis coloring

                        rY,gY,bY, rY,gY,bY,     //Y-axis arrow coloring
                        rY,gY,bY, rY,gY,bY,
                        rY,gY,bY, rY,gY,bY,
                        rY,gY,bY, rY,gY,bY,

                        rY,gY,bY, rY,gY,bY,     //Y-axis lettering coloring
                        rY,gY,bY, rY,gY,bY,
                        rY,gY,bY, rY,gY,bY,

		                rZ,gZ,bZ, rZ,gZ,bZ,     //Z-Axis coloring

                        rZ,gZ,bZ, rZ,gZ,bZ,     //Z-axis arrow coloring
                        rZ,gZ,bZ, rZ,gZ,bZ,
                        rZ,gZ,bZ, rZ,gZ,bZ,
                        rZ,gZ,bZ, rZ,gZ,bZ,

                        rZ,gZ,bZ, rZ,gZ,bZ,     //Z-axis lettering coloring
                        rZ,gZ,bZ, rZ,gZ,bZ, 
                        rZ,gZ,bZ, rZ,gZ,bZ };


      // construction des axes (tableau de lignes)
      LineArray axes = new LineArray(extremites.length / 3, LineArray.COORDINATES |
		                        LineArray.COLOR_3);

      // changement d'échelle des extrémités des 3 axes à l'aide de s_XYZ
      float scaledExtremites[] = new float[extremites.length];
      for (int i = 0; i < extremites.length; i++)
	   scaledExtremites[i] = extremites[i] * scale;

	axes.setCoordinates(0, scaledExtremites);
	axes.setColors(0, color);

	this.setGeometry(axes);
   }

   /**
    * Definition of the geometry for the 3 axis. 
    * For each, included is the geometry information for the 
    * axis, the arrow and the lettering.
    */
   protected static final float  extremites[] = {
   					      //X-axis
   					      -1.0f, 0.0f, 0.0f,
   					      1.0f, 0.0f, 0.0f,

                          //arrow on positive side of X-axis
                          1.0f, 0.0f, 0.0f,
                          0.95f, 0.025f, 0.025f,
                          1.0f, 0.0f, 0.0f,
                          0.95f, 0.025f, -0.025f,
                          1.0f, 0.0f, 0.0f,
                          0.95f, -0.025f, 0.025f,
                          1.0f, 0.0f, 0.0f,
                          0.95f, -0.025f, -0.025f,

                          //X-axis lettering
                          0.95f, -0.05f, 0.0f,
                          1.0f, -0.15f, 0.0f,
                          1.0f, -0.05f, 0.0f,
                          0.95f, -0.15f, 0.0f,

   					      //Y-axis
   					      0.0f, -1.0f, 0.0f,
   					      0.0f, 1.0f, 0.0f,

                          //arrow on positive side of Y-axis
                          0.0f, 1.0f, 0.0f,
                          0.025f, 0.95f, 0.025f,
                          0.0f, 1.0f, 0.0f,
                          0.025f, 0.95f, -0.025f,
                          0.0f, 1.0f, 0.0f,
                          -0.025f, 0.95f, 0.025f,
                          0.0f, 1.0f, 0.0f,
                          -0.025f, 0.95f, -0.025f,

                          //Y-axis lettering
                          0.05f, 1.0f, 0.0f,
                          0.075f, 0.95f, 0.0f,
                          0.075f, 0.95f, 0.0f,
                          0.1f, 1.0f, 0.0f,
                          0.075f, 0.95f, 0.0f,
                          0.075f, 0.9f, 0.0f,


   					      //Z-axis
   					      0.0f, 0.0f, -1.0f,
					      0.0f, 0.0f, 1.0f,

                          //arrow on positive side of Z-axis
                          0.0f, 0.0f, 1.0f,
                          0.025f, 0.025f, 0.95f,
                          0.0f, 0.0f, 1.0f,
                          0.025f, -0.025f, 0.95f,
                          0.0f, 0.0f, 1.0f,
                          -0.025f, 0.025f, 0.95f,
                          0.0f, 0.0f, 1.0f,
                          -0.025f, -0.025f, 0.95f,

                          //Z-axis lettering
                          0.0f, -0.05f, 1.0f,
                          0.0f, -0.05f, 0.95f,
                          0.0f, -0.05f, 0.95f,
                          0.0f, -0.15f, 1.0f,
                          0.0f, -0.15f, 1.0f,
                          0.0f, -0.15f, 0.95f

       };//extremities[]
}//AxisXYZ
