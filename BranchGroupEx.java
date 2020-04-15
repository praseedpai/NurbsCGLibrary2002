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

import javax.media.j3d.BranchGroup;
import java.io.Serializable;
import java.io.IOException;
import java.lang.ClassNotFoundException;

/**
 * BranchGroupEx.java
 * 
 * BranchGroupEx is nothing but BranchGroup with serialization  
 * support. We suspect this is necessary for using the  BranchGroup 
 * as an infobus Dataitem
 *
 * @version 1
 *
 * @author        Praseed Pai K.T.               
 */

public class BranchGroupEx extends javax.media.j3d.BranchGroup
                                   implements Serializable 
{

    /**
    *
    *
    *  Constructor just delegates the call to javax.media.j3d.BranchGroup
    *  Constructor
    *
    */
    public BranchGroupEx() 
    {
         super();
    }
    /**
    *
    *   Serialization support
    *
    *
    *
    */
    private void writeObject(java.io.ObjectOutputStream out) throws IOException 
    {
        out.defaultWriteObject();
    }

    /**
    *
    *
    *   Serialization support
    *
    *
    *
    */

    private void readObject(java.io.ObjectInputStream in) throws IOException, ClassNotFoundException 
    {
        in.defaultReadObject();
    }
} 

// EOF BranchGroupEx.java
