/**
*
*
*  <Copy right notice goes here>
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
*
*/

package hsdc.common;


import javax.media.j3d.*;
import javax.vecmath.*;
import com.sun.j3d.utils.geometry.*;

/**
*
*  Following class allows us to extract so-called Euler angles ( Rotation angles around X,Y,Z Axes )
*  from a Transform3D matrix. See Matrix and Quaternion FAQ at 
*
*  http://www.flipcode.com/documents/matrfaq.html   Question # 36 and 37  
*
*
*
*
*/

public class EulerUtils 
{
    public static double PRECISION = 1E-05; 
    /** 
    *
    *
    *
    *
    *
    */  
    public static Vector3f GetEulerAngle( Transform3D Trans ) 
    {
        Matrix3d RotMatrix = new Matrix3d();
        Vector3f RetVector = new Vector3f();
        double   Cosinus   = 0.0;
        double   Rx        = 0.0;
        double   Ry        = 0.0;
 
        Trans.get(RotMatrix);

        RetVector.y =  -(float) Math.asin(RotMatrix.m02);
        Cosinus = Math.cos(RetVector.y);


        if ( Math.abs(Cosinus) > EulerUtils.PRECISION  ) 
        {
            Rx = RotMatrix.m22 / Cosinus;
            Ry = - (RotMatrix.m12/Cosinus);
            RetVector.x = (float)Math.atan2(Ry,Rx);  

            Rx = RotMatrix.m00 / Cosinus;
            Ry = - (RotMatrix.m01/Cosinus);
            RetVector.z = (float)Math.atan2(Ry,Rx);  
        }   
        else {
       
            RetVector.x = 0.0f;
            Rx = RotMatrix.m11;
            Ry = RotMatrix.m10;
            RetVector.z = (float)Math.atan2(Ry,Rx);  
   
        } 

        RetVector.x = EulerUtils.AngleAdjust(RetVector.x);
        RetVector.y = EulerUtils.AngleAdjust(RetVector.y);
        RetVector.z = EulerUtils.AngleAdjust(RetVector.z);   
        return RetVector; 
    }
  
    /**
    *
    *
    *
    *
    */ 
    public static float AngleAdjust(float value) 
    {
           
         if ( value  >=  2.0*Math.PI ) 
         {
              return  value - (float)(2.0d*Math.PI);
         } 
         else if ( value < 0.0 ) {
              return value + (float)(2.0d*Math.PI); 
         }
         return value;  

    } 


   public static Vector3f getRotAngle(Transform3D t3D)
	{
		Matrix3d m1 = new Matrix3d();
		double c,tRx,tRy;
		Vector3f Angles = new Vector3f();

		t3D.get(m1);
		Angles.y = (float)Math.asin(m1.getElement(0,2));
		c = Math.cos(Angles.y);

		if (Math.abs(c) > 0.00001)
		{
			tRx = m1.getElement(2,2)/c;
			tRy = -m1.getElement(1,2)/c;
			Angles.x = (float)Math.atan2(tRy,tRx);

			tRx = m1.getElement(0,0)/c;
			tRy = -m1.getElement(0,1)/c;
			Angles.z = (float)Math.atan2(tRy,tRx);
		}
		else
		{
			Angles.x = (float)0.0;

			tRx = m1.getElement(1,1)/c;
			tRy = m1.getElement(1,0)/c;
			Angles.z = (float)Math.atan2(tRy,tRx);
		}

		if (Angles.x < 0.0)
		{
			Angles.x+=2*Math.PI;
		}
		else if (Angles.x > (2*Math.PI))
		{
			Angles.x-=2*Math.PI;
		}
		if (Angles.y < 0.0)
		{
			Angles.y+=2*Math.PI;
		}
		else if (Angles.y > (2*Math.PI))
		{
			Angles.y-=2*Math.PI;
		}
		if (Angles.z < 0.0)
		{
			Angles.z+=2*Math.PI;
		}
		else if (Angles.z > (2*Math.PI))
		{
			Angles.z-=2*Math.PI;
		}

		if ( ( Angles.x < 0.001 ) && ( Angles.x > -0.001 ) )
		{
			Angles.x = (float)0.0;
		}
		if ( ( Angles.y < 0.001 ) && ( Angles.y > -0.001 ) )
		{
			Angles.y = (float)0.0;
		}
		if ( ( Angles.z < 0.001 ) && ( Angles.z > -0.001 ) )
		{
			Angles.z = (float)0.0;
		}

		if ( Angles.x == 0.0)
		{
			Angles.x = Math.abs(Angles.x);
		}
		if ( Angles.y == 0.0)
		{
			Angles.y = Math.abs(Angles.y);
		}
		if ( Angles.z == 0.0)
		{
			Angles.z = Math.abs(Angles.z);
		}

		if ( Angles.x == 2*Math.PI)
		{
			Angles.x = (float)0.0;
		}
		if ( Angles.y == 2*Math.PI)
		{
			Angles.y = (float)0.0;
		}
		if ( Angles.z == 2*Math.PI)
		{
			Angles.z = (float)0.0;
		}

		return(Angles);
	} 

}