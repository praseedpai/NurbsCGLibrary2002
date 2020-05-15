/*
*
*
*   <Copyright and Legal Notice Goes Here>
*
*
*
*
*/
package hsdc.common;

import javax.media.j3d.Transform3D;

/**
* The MouseRotateCallback interface allows a class to be notified
* when the rotation factors are changed by MouseRotateEcho class. The
* class that is interested in rotation factor changes implements this
* interface, and the object created with that class is registered
* with the MouseRotateEcho Class using the setUpRotateCallback method
*
* @version 1.0
* @author  Praseed Pai K.T.
*
*/ 
public interface MouseRotateCallback 
{

    /**
    *   rotX  - x rotation
    *   rotY  - y rotation
    *   rotZ  - z rotation
    *   Since rotation happens around (ie XY plane ) , rotZ will be always Zero
    *   when we support Rotation from an Arbitary view Point rotZ will have values
    */

    public void rotationChanged( double rotX , double rotY , double rotZ, Transform3D t3d);

}

// EOF MouseRotateCallback



