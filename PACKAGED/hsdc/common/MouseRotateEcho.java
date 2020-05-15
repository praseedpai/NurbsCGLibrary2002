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

import java.awt.*;
import java.awt.event.*;
import java.util.*;
import javax.media.j3d.*;
import javax.vecmath.*;
import com.sun.j3d.utils.behaviors.mouse.*; 

/**
*
*  See The Documentation from Sun MicroSystems On MouseRotate to 
*  learn about the Usage of this class.
*
*
*
*
*
*/

public class MouseRotateEcho  extends MouseBehavior 
{


    double x_angle, y_angle;

    /*
    *
    *  For each pixel movement horizontally, How much To go. One can
    *  Alter this by calling setFactor method
    *
    */ 

    double x_factor = .03;

    /*
    *
    *  For each pixel movement vertically, How much To go. One can
    *  Alter this by calling setFactor method
    *
    */  

    double y_factor = .03;


    /*
    *  Total Rotation Taken 
    *  
    *
    */

    double x_rotTotal = 0.0; 
    double y_rotTotal = 0.0;
    double z_rotTotal = 0.0;
     
    /*
    *
    *  References to mouse callback function ( for Translation and Scale )
    *
    */

    private MouseBehaviorCallback callback = null;
    
      
    /*
    *
    *  References to mouse callback function ( for Translation and Scale )
    *
    */  

    private MouseRotateCallback   rotcallBack = null;


    /*
    * Use This method to Set the Initial Rotation Factors for 
    * TransformGroup 
    *
    */  

    public void setupRotation( double x_rot , double y_rot , double z_rot ) 
    {
        x_rotTotal = x_rot; 
        y_rotTotal = y_rot;
        z_rotTotal = z_rot;
    }

    /**
    * Creates a rotate behavior given the transform group.
    * @param transformGroup The transformGroup to operate on.
    */

    public MouseRotateEcho(TransformGroup transformGroup) {
        super(transformGroup);
    }

    /**
    * Creates a default mouse rotate behavior.
    */

    public MouseRotateEcho() {
        super(0);
    }

    /**
    * Creates a rotate behavior.
    * Note that this behavior still needs a transform
    * group to work on (use setTransformGroup(tg)) and
    * the transform group must add this behavior.
    * @param flags interesting flags (wakeup conditions).
    */

    public MouseRotateEcho(int flags) {
        super(flags);
    }

    /**
    * Creates a rotate behavior that uses AWT listeners and behavior
    * posts rather than WakeupOnAWTEvent.  The behavior is added to the
    * specified Component. A null component can be passed to specify
    * the behavior should use listeners.  Components can then be added
    * to the behavior with the addListener(Component c) method.
    * @param c The Component to add the MouseListener
    * and MouseMotionListener to.
    */

    public MouseRotateEcho(Component c) {
        super(c, 0);
    }

    /**
    * Creates a rotate behavior that uses AWT listeners and behavior
    * posts rather than WakeupOnAWTEvent.  The behaviors is added to
    * the specified Component and works on the given TransformGroup.
    * A null component can be passed to specify the behavior should use
    * listeners.  Components can then be added to the behavior with the
    * addListener(Component c) method.
    * @param c The Component to add the MouseListener and
    * MouseMotionListener to.
    * @param transformGroup The TransformGroup to operate on.
    */

    public MouseRotateEcho(Component c, TransformGroup transformGroup) {
	super(c, transformGroup);
    }

    /**
    * Creates a rotate behavior that uses AWT listeners and behavior
    * posts rather than WakeupOnAWTEvent.  The behavior is added to the
    * specified Component.  A null component can be passed to specify
    * the behavior should use listeners.  Components can then be added to
    * the behavior with the addListener(Component c) method.
    * Note that this behavior still needs a transform
    * group to work on (use setTransformGroup(tg)) and the transform
    * group must add this behavior.
    * @param flags interesting flags (wakeup conditions).
    */

    public MouseRotateEcho(Component c, int flags) {
        super(c, flags);
    }
    /*
    *
    *
    */
   
    public void initialize() 
    {
        super.initialize();
        x_angle = 0;
	  y_angle = 0;
	  if ((flags & INVERT_INPUT) == INVERT_INPUT) {
	      invert = true;
	      x_factor *= -1;
	      y_factor *= -1;
        }
    }
    

    /**
    * Return the x-axis movement multipler.
    *
    */

    public double getXFactor() {
        return x_factor;
    }

    /**
    * Return the y-axis movement multipler.
    *
    */

    public double getYFactor() {
        return y_factor;
    }

    /**
    * Set the x-axis amd y-axis movement multipler with factor.
    */
    public void setFactor( double factor) {
        x_factor = y_factor = factor;
    }

    /**
    * Set the x-axis amd y-axis movement multipler with xFactor and yFactor
    * respectively.
    */
    public void setFactor( double xFactor, double yFactor) {
        x_factor = xFactor;
        y_factor = yFactor;    
    }
    /**
    *
    *
    *
    */
    public void processStimulus (Enumeration criteria) {
        WakeupCriterion wakeup;
	  AWTEvent[] events;
 	  MouseEvent evt;
	
	  while (criteria.hasMoreElements()) {
	      wakeup = (WakeupCriterion) criteria.nextElement();

	      if (wakeup instanceof WakeupOnAWTEvent) {
		    events = ((WakeupOnAWTEvent)wakeup).getAWTEvent();
		    if (events.length > 0) {
		        evt = (MouseEvent) events[events.length-1];
		        doProcess(evt);
		    }
	       }
	      else if (wakeup instanceof WakeupOnBehaviorPost) {
		    while (true) {
		        // access to the queue must be synchronized
		        synchronized (mouseq) {
			      if (mouseq.isEmpty()) break;
  			      evt = (MouseEvent)mouseq.remove(0);

 			      // consolidate MOUSE_DRAG events
 			      while ((evt.getID() == MouseEvent.MOUSE_DRAGGED) &&
			              !mouseq.isEmpty() &&
			              (((MouseEvent)mouseq.get(0)).getID() ==
				         MouseEvent.MOUSE_DRAGGED)) {
			          evt = (MouseEvent)mouseq.remove(0);
			      }
		        }
		        doProcess(evt);
		   }
	      }
	  }
	wakeupOn (mouseCriterion);
    }

    /*
    *
    *
    *   Here we are going to make a small change to get the Behavior
    *
    *
    */

    void doProcess(MouseEvent evt) {
	int id;
	int dx, dy;

	processMouseEvent(evt);
	if (((buttonPress)&&((flags & MANUAL_WAKEUP) == 0)) ||
	    ((wakeUp)&&((flags & MANUAL_WAKEUP) != 0))) {
	    id = evt.getID();
	    if ((id == MouseEvent.MOUSE_DRAGGED) && 
		!evt.isMetaDown() && ! evt.isAltDown()){
		x = evt.getX();
		y = evt.getY();
		
		dx = x - x_last;
		dy = y - y_last;
		
		if (!reset){	    
		    x_angle = dy * y_factor;
		    y_angle = dx * x_factor;
		    
		    transformX.rotX(x_angle);
		    transformY.rotY(y_angle);
		    
		    transformGroup.getTransform(currXform);
		    
		    Matrix4d mat = new Matrix4d();
		    // Remember old matrix
		    currXform.get(mat);
		    
		    // Translate to origin
		    currXform.setTranslation(new Vector3d(0.0,0.0,0.0));
		    if (invert) {
			currXform.mul(currXform, transformX);
			currXform.mul(currXform, transformY);
		    } else {
			currXform.mul(transformX, currXform);
			currXform.mul(transformY, currXform);
		    }
		    
		    // Set old translation back
		    Vector3d translation = new 
		    Vector3d(mat.m03, mat.m13, mat.m23);
		    currXform.setTranslation(translation);
		    
		    // Update xform
		    transformGroup.setTransform(currXform);
		    
		    transformChanged( currXform );
		    
		    if (callback!=null)
			callback.transformChanged( MouseBehaviorCallback.ROTATE,
						   currXform );

                    /* Following code`s purpose is to let a class know the current
                     *  Rotation factors
                     */   

                    if ( rotcallBack != null ) 
                    {
                        x_rotTotal += x_angle;
                        y_rotTotal += y_angle;
                        double SignXFactor = ( x_rotTotal >= 0.0 ) ? 1.0 : -1.0;
                        double SignYFactor = ( y_rotTotal >= 0.0 ) ? 1.0 :  -1.0;  

                        
                        if ( Math.abs(x_rotTotal) > 2*Math.PI ) {
                            x_rotTotal -= SignXFactor*2*Math.PI ;
                        }
                          
                        if ( Math.abs(y_rotTotal) > 2*Math.PI ) {
                            y_rotTotal -= SignYFactor*2*Math.PI ;
                        }

                                           
                        if ( rotcallBack != null ) { 
                                rotcallBack.rotationChanged( x_rotTotal , y_rotTotal , z_rotTotal,currXform ); 
                        } 

                    }  

		}
		else {
		    reset = false;
		}
		
		x_last = x;
		y_last = y;
	    }
	    else if (id == MouseEvent.MOUSE_PRESSED) {
		x_last = evt.getX();
		y_last = evt.getY();
	    }
	}
    }
    
    /**
    * Users can overload this method  which is called every time
    * the Behavior updates the transform
    *
    * Default implementation does nothing
    */
    public void transformChanged( Transform3D transform ) 
    {

    }

    /**
    * The transformChanged method in the callback class will
    * be called every time the transform is updated
    */

    public void setupCallback( MouseBehaviorCallback callback ) 
    {
        this.callback = callback;
    }
    /**
    * The rotationChanged method in the callback class will be 
    * called every time there is change in Rotation factors  
    */      

    public void setUpRotateCallback( MouseRotateCallback callback ) {
        rotcallBack = callback;
    }


}

//Eof MouseRotateEcho.java
