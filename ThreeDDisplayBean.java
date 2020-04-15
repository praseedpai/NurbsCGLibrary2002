/*
*
*
*   <Copyright and Legal Notice Goes Here>
*
*
*
*
*/
package hsdc.threedpack;

import java.applet.Applet;
import java.awt.BorderLayout;
import java.awt.event.*;
import java.awt.GraphicsConfiguration;
import com.sun.j3d.utils.applet.MainFrame;
import com.sun.j3d.utils.geometry.ColorCube;
import com.sun.j3d.utils.universe.*;
import com.sun.j3d.utils.geometry.*;
import javax.media.j3d.*;
import javax.vecmath.*;
import java.awt.Button;
import javax.media.j3d.Transform3D;
import com.sun.j3d.utils.behaviors.mouse.*; 
import java.util.*;
import java.io.*;
import javax.infobus.InfoBusMember;
import javax.infobus.InfoBusDataConsumer;
import javax.infobus.InfoBusMemberSupport;
import javax.infobus.InfoBusItemAvailableEvent;
import javax.infobus.InfoBusItemRevokedEvent;
import javax.infobus.InfoBus;
import javax.infobus.*;
import java.beans.*;
import java.awt.Panel;
import java.awt.Frame;
import java.awt.Dimension;
import java.awt.Menu;
import java.awt.MenuBar;
import java.awt.MenuItem;
import com.sun.j3d.loaders.objectfile.ObjectFile;
import com.sun.j3d.loaders.ParsingErrorException;
import com.sun.j3d.loaders.IncorrectFormatException;
import com.sun.j3d.loaders.Scene;
import java.awt.*;
import java.net.URL;
import hsdc.common.MouseRotateCallback;
import hsdc.common.ObjFileDataItem;
import hsdc.common.Torus;
import hsdc.common.AxesXYZ;
import hsdc.common.MouseRotateEcho;
import hsdc.common.EulerUtils;
import hsdc.nurbs.*;

/**
*
*
*   ThreeDDisplayBean takes care of all rendering Activities
*
*
*
*
*
**/

public class ThreeDDisplayBean  extends    Panel          // as good as extending Panel         
                                implements InfoBusBean,         // Mandatory for InfoBus support
                                           InfoBusDataConsumer,   // I'm a producer
                                           InfoBusDataProducer,   // I'm a consumer too
                                           Serializable,            
                                           MouseRotateCallback ,  // For monitoring Rotation params
                                           MouseBehaviorCallback  // For monitoring Translation and scale
{

   /*
   *  SimpleUniverse is all u need , if ur not writing a VR application 
   *
   */ 
   private SimpleUniverse u = null;

   /*
   *
   *  
   *
   */
   ObjFileDataItem DataItemReference  =   null;

   /*    
   *
   *  BackGround management
   *
   */
   private Background whitebck = new Background(1f,1f,1f);
   private Background greenbck = new Background(0.0f,1.0f,0.0f);
   private Background CurrBack = null;

   /*
   *
   *  Data structures for Scene Graph Management 
   *
   *
   */
   private BranchGroup scene   = null;
   private BranchGroup CurrBranch = null;
   private BranchGroup CurrShapeBranch = null;

   /*
   *
   * Bounding Volume is needed for Light , BackGround and Picking
   *
   */
   BoundingSphere bounds = new BoundingSphere(new Point3d(0.0d,0.0d,0.0d),Double.MAX_VALUE );
  
   /**
   *
   *   For Mouse Support
   *
   */
   TransformGroup oTrans = null; 

   /**
   *
   * Following class speeds up the infobus development and it can be 
   * Considered as the Adapter equivalent of the InfoBusMember
   */
   private InfoBusBeanSupport ims;   

   /* For Security Reason , Always use Proxies to eliminate 
   * Unwanted Introspection 
   *      ProducerProxy 
   */

   private InfoBusDataProducerProxy     m_producerProxy = null;

   /* For Security Reason , Always use Proxies to eliminate 
   *  Unwanted Introspection 
   *      ConsumerProxy   Not used now , but can be used later
   */

   private InfoBusDataConsumerProxy     m_consumerProxy = null;

   /*
   *
   *
   *
   *
   */

   private Panel FrontView  = new Panel();
   private Panel BackView   = new Panel();



   /*
   *      0 - Box  ( Cube )
   *      1 - Cylinder 
   *      2 - Cone 
   *      3 - Sphere      
   */
   private int    CurrShapeid = 0;

   /*
   *    1-  Blue
   *    0 - White
   */

   private int    CurrBackid  = 1;


   /*  
   *  0 -  "SOLID"
   *  1 -  "MATERIAL"
   *  2 -  "WIREFRAME"
   *  3 -  "POINTS"
   */
   private int CurrRendMode = 0;  

   /*
   *
   *
   *
   */
   private Vector3d TransVector = null;
   private Vector3d RotVector   = null;

   /**
    *
    *
    *
    *
    *
    */
     TransformGroup AxisGlobal = null;   

    /**
     *
     *
     */
     Canvas3D FrontViewCanvas ;
     Canvas3D BackViewCanvas;  
        

    /**
    *
    *
    *  Ctor For The ThreeDDisplayBean
    *
    *
    */

    public ThreeDDisplayBean() throws Exception 
    {
        super(); 
        setLayout(new GridLayout(1,2));  
        ////////////////////////////////////////////////////// 
        //   InfoBus related Stuff
        // 
        //

        ims = new InfoBusBeanSupport(null);

        try 
        {
            ims.joinInfoBus("JAVA3DGraphicsControllerBus");
            ims.getInfoBus().addDataConsumer(this);
            ims.getInfoBus().addDataProducer(this);
        }
        catch (java.beans.PropertyVetoException e) 
        {
            System.out.println("PropertyVetoException Thrown"); 
            System.out.println(e.toString());
            throw e;  
        }
        catch ( Exception e ) 
        { 
             System.out.println("Failed to Join InfoBus Exiting .... "); 
             System.out.println(e.toString());
             throw e;
        } 

        m_producerProxy = new InfoBusDataProducerProxy(this);
        m_consumerProxy = new InfoBusDataConsumerProxy(this);

        ///////////////////////////////////////////////////////////
        //
        //  Visibility related stuff
        //   

        init();
        setVisible(true);        


    }

    /**
    *
    *  Method for Setting InfoBus
    *  Routes the call to InfoBusMemberSupport instance
    *
    */
    public void setInfoBus(InfoBus newInfoBus) 
                           throws PropertyVetoException 
    {
        ims.setInfoBus(newInfoBus);
    }

    /**
    *
    *  Method for retrieving the active InfoBus
    *  Routes the call to InfoBusMemberSupport instance
    *
    */
    public InfoBus getInfoBus() 
    {
       return ims.getInfoBus();
    }
    /**
    *
    * Delegate the Call to InfoBusMemberSupport instance
    *
    */
    public void addInfoBusVetoableListener(VetoableChangeListener vcl) 
    {
       ims.addInfoBusVetoableListener(vcl);
    }

    /**
    *
    * Delegate the Call to InfoBusMemberSupport instance
    *
    */
    public void removeInfoBusVetoableListener(VetoableChangeListener vcl) 
    {
        ims.removeInfoBusVetoableListener(vcl);
    }
   
    /**
    *
    * Delegate the Call to InfoBusMemberSupport instance
    *
    */
    public void addInfoBusPropertyListener(PropertyChangeListener pcl) 
    {
        ims.addInfoBusPropertyListener(pcl);
    }
    /**
    *
    * Delegate the Call to InfoBusMemberSupport instance
    *
    */
    public void removeInfoBusPropertyListener(PropertyChangeListener pcl) 
    {
               ims.removeInfoBusPropertyListener(pcl);
    }
    /**
    *
    */
    public String getInfoBusName() 
    {
        return ims.getInfoBusName();
    } 
    /**
    *
    */ 
    public void setInfoBusName(java.lang.String newBusName) 
    {
        try { 
            ims.setInfoBusName(newBusName);
        }
        catch(Exception e ) {

        }

    }

    /**
    *
    *  Stub method to satisfy the compiler ( We haven't implemented propertyChange support)
    *  This may change in future
    */
    public void propertyChange(PropertyChangeEvent e) 
    {
 

    }

    /**
    *
    *
    *  Most important method for the Data consumer in an infobus
    *
    *
    *
    *
    *
    */
    public void dataItemAvailable(InfoBusItemAvailableEvent e) 
    {
        
        ///////////////////////////////////////////////////
        //
        //  Get The Data item Name
        // 
        String name = e.getDataItemName();
        if ( name == null ) {
            return;  
        }   

              
        if (name.equals("CanvasBackGround")) 
        {
            String bckgnd = (String)e.requestDataItem(m_consumerProxy,null);

            if ( bckgnd.equals("BLUE") )
                CurrBackid = 1;
            else if (  bckgnd.equals("WHITE") )
                CurrBackid = 0;

            swapBackGround();  
        }
        else if ( name.equals("NextShape") ) {

            String shape   =   (String)e.requestDataItem(m_consumerProxy,null);
            ////////////////////////////////////////////////////////////////  
            //      0 - Box  ( Cube )
            //      1 - Cylinder 
            //      2 - Cone 
            //      3 - Sphere      
            // 
            if ( shape.equals("BOX") )
                CurrShapeid = 0;
            else if (  shape.equals("CYLINDER") )
                CurrShapeid  = 1;
            else if (  shape.equals("CONE") )
                CurrShapeid = 2;
            else if (  shape.equals("SPHERE") )
                CurrShapeid = 3;
            else if (  shape.equals("TORUS") )
                CurrShapeid = 5;
            else if (  shape.equals("AXES"))
                CurrShapeid = 4; 
            else if (  shape.equals("MOBIUS"))
                CurrShapeid = 6;
            else if (  shape.equals("TRIANGLE") )	 
                CurrShapeid = 7; 
            else if (  shape.equals("SPIRAL TORUS") )
                CurrShapeid = 8;
            else if (  shape.equals("SPRING") )
                CurrShapeid = 9;  
	      else if (  shape.equals("MY CYLINDER") )
                CurrShapeid = 10;
            else if (  shape.equals("THICK CYLINDER") )
                CurrShapeid = 11;
            else if (  shape.equals("TETRAHEDRON") )
                CurrShapeid = 12;
            else if (  shape.equals("MY CONE") )
                CurrShapeid = 13;
            else if (  shape.equals("KNOT") )
                CurrShapeid = 14;
            else if (  shape.equals("BEZIERSURFACE") )
                CurrShapeid = 15;
            else if (  shape.equals("SPLINESURFACE") )
                CurrShapeid = 16;
            else if (  shape.equals("HIDDEN LINE REMOVAL") )
                CurrShapeid = 17;
            else if (  shape.equals("NURBS BEZIER") )
                CurrShapeid = 18;       
            else if (  shape.equals("NURBS CIRCLE") )
                CurrShapeid = 19;
            else if (  shape.equals("NURBS TORUS"))
                CurrShapeid = 20;
            else if ( shape.equals("ARBITARY POLYGON") )
                CurrShapeid = 21;

 
            swapShape();


        } 
        else if ( name.equals("RenderingMode") ) 
        {
             String shape   =   (String)e.requestDataItem(m_consumerProxy,null);
             ////////////////////////////////////////////////////////////////  
             //  0 -  "SOLID"
             //  1 -  "MATERIAL"
             //  2 -  "WIREFRAME"
             //  3 -  "POINTS"

             if ( shape.equals("SOLID") )
                 CurrRendMode = 0;
             else if (  shape.equals("MATERIAL") )
                 CurrRendMode  = 1;
             else if (  shape.equals("WIREFRAME") )
                 CurrRendMode = 2;
             else if (  shape.equals("POINTS") )
                 CurrRendMode = 3;

             swapShape();

        }
        else if ( name.equals("LoadNextFile"))  
        {
                   DataItemReference= ( ObjFileDataItem )
                                          e.requestDataItem(m_consumerProxy,null);
                   DisplayScene( DataItemReference.getUrl()); 

        }
       else if ( name.equals("WindowResized") ) {
          Window Ft = (Window)this.getParent();
          int h = Ft.getHeight()*2/3;
          int w = Ft.getWidth()/2;
          setSize(w*2,h); 
          FrontView.setSize( w-5 , h  );
          BackView.setSize(  w-5 , h  );
          FrontViewCanvas.setSize(w-7,h);
          BackViewCanvas.setSize(w-7,h); 

       }  
         


    } 

    /**
    *
    *
    *  Handler for DataItem Revoked Event 
    *
    */

    public void dataItemRevoked ( InfoBusItemRevokedEvent ibe )
    {
        if ( ibe == null )
        {
            return;
        }

        String s = ibe.getDataItemName();

        //////////////////////////////////////////////////////////////////
        // see if the name of the item revoked matches our data
        //
        //

        if ( ( s != null ) && s.equals( "LoadNextFile" ) )
        {
            //next check to see if we were holding a copy
            if ( DataItemReference != null )
            {
                    ((DataItem)DataItemReference).release();
                    DataItemReference=null;    
            }
        }
    }

    /**
    *
    *
    *
    *
    */
    public void dataItemRequested(InfoBusItemRequestedEvent e) 
    {
        String name = e.getDataItemName();

        if ( name.equals("RotationParams") ) 
        {
            e.setDataItem(RotVector);
        }
        else if ( name.equals("TranslationParams") ) 
        {
            e.setDataItem(TransVector);
        }  

    }
    /**
    *
    *  Create an Apperance Based On  Rendering Mode selected 
    *
    *
    *
    */ 
    public Appearance createAppearance( int Mode , float redc , float greenc , float bluec )
    {
        Appearance app = new Appearance();
        ColoringAttributes col = new ColoringAttributes();
        col.setColor(redc,greenc,bluec);  
        col.setShadeModel(ColoringAttributes.SHADE_GOURAUD);
        app.setColoringAttributes(col);  

        switch(Mode) 
        {
            case 0: // Solid
            {
                break;                 
            }
            case 1:  // Material 
            {  
                app.setMaterial(new Material( new Color3f(0.2f,0.2f,0.2f),
                                              new Color3f(0.0f,0.0f,0.0f), 
                                              new Color3f(redc,greenc,bluec) ,
                                              new Color3f(1,1,1),3.5f));   
                break;
            }
            case  2: // Wireframe
            {
                PolygonAttributes pa = new PolygonAttributes();
   	          pa.setPolygonMode(pa.POLYGON_LINE);
	          pa.setCullFace(pa.CULL_NONE);
	          app.setPolygonAttributes(pa);
                break; 
            }
            case  3: // Points
            {
                PolygonAttributes pa = new PolygonAttributes();
   	          pa.setPolygonMode(pa.POLYGON_POINT);
	          pa.setCullFace(pa.CULL_NONE);
	          app.setPolygonAttributes(pa);
                PointAttributes pta = new PointAttributes();
                pta.setPointSize(5.0f);
     	          app.setPointAttributes(pta);
	          break; 
             }
        }
 
    return app;  
    
    } 

    /*
    *
    * Create a Cone
    *
    *
    */

    private TransformGroup MakeCone() 
    {
        Transform3D trn = new Transform3D();
        TransformGroup grp = new TransformGroup(trn);
        grp.addChild( new Cone(0.7f,0.7f,Cone.GENERATE_NORMALS,createAppearance(CurrRendMode,1.0f,0.0f,0.0f)) );  
        return grp;
    } 
    /*
    *
    * Create a Box
    *
    *
    */
    private TransformGroup MakeBox()  
    {
        Transform3D trn = new Transform3D();
        TransformGroup grp = new TransformGroup(trn);
       // grp.addChild( new Box(0.3f,0.3f,0.3f,createAppearance(CurrRendMode,0.0f,1.0f,0.0f)) );  
        grp.addChild( new ColorCube(0.6f));  
 
        return grp;
    }


    /*
    *
    * Create a Cylinder
    *
    *
    */
    private TransformGroup MakeCylinder() {
        Transform3D trn = new Transform3D();
        TransformGroup grp = new TransformGroup(trn);
        grp.addChild( new Cylinder(0.5f,1.2f,createAppearance(CurrRendMode,0.0f,0.0f,0.1f)));  
        return grp;
    }

    /*
    *
    * Create a Sphere
    *
    *
    */
    private TransformGroup MakeSphere() {
        Transform3D trn = new Transform3D();
        TransformGroup grp = new TransformGroup(trn);
        grp.addChild( new Sphere(0.7f,createAppearance(CurrRendMode,1.0f,1.0f,0.0f)));  
        return grp;
    }

    /*
    *
    *   Draw the Axis Diagram 
    *
    *
    */
    private TransformGroup MakeAxes() {
        Transform3D trn = new Transform3D();
        TransformGroup grp = new TransformGroup(trn); 
        grp.addChild(new hsdc.common.AxesXYZ(0.1f)); 
        return grp;
    }    

    /*
    *
    *
    *  Draw a Torus 
    *
    */
    private TransformGroup MakeTorus() {
        Transform3D trn = new Transform3D();
        TransformGroup grp = new TransformGroup(trn); 
        grp.addChild(new hsdc.common.Torus(createAppearance(CurrRendMode,1.0f,1.0f,0.0f))); 
        return grp;
    } 

    /*
    *
    *
    *  Draw a Mobius Strip  
    *
    */
    private TransformGroup MakeMobius() {
        Transform3D trn = new Transform3D();
        TransformGroup grp = new TransformGroup(trn); 
        grp.addChild(new hsdc.common.Mobius(createAppearance(CurrRendMode,1.0f,1.0f,0.0f))); 
        return grp;
    } 

    /*
    *
    *
    *  Draw a SPIRAL Torus
    *
    */

    private TransformGroup MakeSpiralTorus() {
        Transform3D trn = new Transform3D();
        TransformGroup grp = new TransformGroup(trn); 
        grp.addChild(new hsdc.common.SpiralTorus(createAppearance(CurrRendMode,1.0f,1.0f,0.0f))); 
        return grp;
    } 

    /*
    *
    *
    *  Draw a Spring
    *
    */

    private TransformGroup MakeSpring() {
        Transform3D trn = new Transform3D();
        TransformGroup grp = new TransformGroup(trn); 
        grp.addChild(new hsdc.common.Spring(createAppearance(CurrRendMode,1.0f,1.0f,0.0f))); 
        return grp;
    } 

    /*
    *
    *
    *  Draw a Cylinder  ( using  custom algorithm )
    *
    */

    private TransformGroup MakeAltCylinder() {
        Transform3D trn = new Transform3D();
        TransformGroup grp = new TransformGroup(trn); 
        grp.addChild(new hsdc.common.GIODCylinder(30,1.0f,2.0f,0,0,0,createAppearance(CurrRendMode,1.0f,1.0f,0.0f))); 
        return grp;
    } 
 
    /*
    *
    *
    *  Draw a ThickCylinder
    *
    */

    private TransformGroup MakeThickCylinder() {
        Transform3D trn = new Transform3D();
        TransformGroup grp = new TransformGroup(trn); 
        grp.addChild(new hsdc.common.GIODThickCylinder(30,1.2f,1.0f,2.0f,0.f,0.f,0.f,createAppearance(CurrRendMode,1.0f,1.0f,0.0f))); 
        return grp;
    } 

     /*
    *
    *
    *  Draw a Knot
    *
    */

    private TransformGroup MakeKnot() {
        Transform3D trn = new Transform3D();
        TransformGroup grp = new TransformGroup(trn); 
        grp.addChild(new hsdc.common.Knot(createAppearance(CurrRendMode,1.0f,1.0f,0.0f))); 
        return grp;
    } 

    /*
    *
    *
    *  Draw a Triangle
    *
    */

    private TransformGroup MakeTriangle() {
        Transform3D trn = new Transform3D();
        TransformGroup grp = new TransformGroup(trn); 
        grp.addChild(new hsdc.common.Triangle3D()); 
        return grp;
    } 

    /*
    *
    *
    *  Draw a Tetrahedron
    *
    */

    private TransformGroup MakeTetrahedron() {
        Transform3D trn = new Transform3D();
        TransformGroup grp = new TransformGroup(trn); 
        grp.addChild(new hsdc.common.Tetrahedron(createAppearance(CurrRendMode,1.0f,1.0f,0.0f))); 
        return grp;
    } 

   /*
    *
    *
    *  Draw a Cone
    *
    */

    private TransformGroup MakeAltCone() {
        Transform3D trn = new Transform3D();
        TransformGroup grp = new TransformGroup(trn); 
        grp.addChild(new hsdc.common.Cone(createAppearance(CurrRendMode,1.0f,1.0f,0.0f))); 
        return grp;
    } 

    /*
    *
    *
    *  Draw a Bezier Curve
    *
    */

    private TransformGroup MakeBezier() {
        Transform3D trn = new Transform3D();
        TransformGroup grp = new TransformGroup(trn); 
        grp.addChild(new hsdc.common.BezierSurface(createAppearance(CurrRendMode,1.0f,1.0f,0.0f))); 
        return grp;
    } 


    /*
    *
    *
    *  Draw a BSpline Curve
    *
    */

    private TransformGroup MakeBSpline() {
        Transform3D trn = new Transform3D();
        TransformGroup grp = new TransformGroup(trn); 
        grp.addChild(new hsdc.common.BSpline(createAppearance(CurrRendMode,1.0f,1.0f,0.0f))); 
        return grp;
    } 

    /*
    *
    *
    *  Draw a BSpline Curve
    *
    */
    private TransformGroup MakeHiddenLine() {
        ///////////////////////////////////////////////////////
        //
        // Declare some local variables
        //
        //
        Appearance    wire   = null; 
        OrderedGroup  grp = null;
        PolygonAttributes  Pa = null; 
        PolygonAttributes  wirePa = null; 
        PolygonAttributes  solidPa = null; 
        Sphere spr = null;
        com.sun.j3d.utils.geometry.Box  bx = null;  
        float dynamicOffset = 1.5f;
        float staticOffset = -10.0f;  
        Color3f colort = null; 

        switch(CurrBackid)
        {
            case 0:
              colort = new Color3f(1.0f,1.0f,1.0f); 
              break;       
            case 1:
              colort = new Color3f(0.0f,0.0f,1.0f); 
              break;

        } 
 
        /////////////////////////////////////////////////////////////
        //
        //  Create a new Transformgroup
        //
        // 
        TransformGroup objTrans = new TransformGroup();
	  objTrans.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
        ////////////////////////////////////////////////////////////////////
        // Create a Sphere.  We will display this as both wireframe and 
  	  // solid to make a hidden line display
	  // wireframe
        //
   	  wire         = new Appearance();
        grp          = new OrderedGroup();
   	  Pa           = new PolygonAttributes(
			         PolygonAttributes.POLYGON_LINE, 
			         PolygonAttributes.CULL_BACK, 
			         0.0f);
  	  wire.setPolygonAttributes(Pa);
	  spr  = new Sphere(0.3f, wire);
        grp.addChild(spr);  
        objTrans.addChild(grp);

        //////////////////////////////////////////
        //
        //  Make a Solid Sphere
        //
        //
        Color3f red = new Color3f(0.0f, 0.0f, 0.0f);
	  ColoringAttributes solidCa = new ColoringAttributes(colort,
	    ColoringAttributes.SHADE_FLAT);
    	  Material solidMat = new Material();
	  solidMat.setLightingEnable(false);
	  Appearance solid = new Appearance();
	  solid.setMaterial(solidMat);
	  solid.setColoringAttributes(solidCa);
	  solidPa = new PolygonAttributes(
			PolygonAttributes.POLYGON_FILL, 
			PolygonAttributes.CULL_BACK, 
			0.0f);
	  solidPa.setPolygonOffsetFactor(1.5f);
	  solidPa.setPolygonOffset(-10.0f);
	  solidPa.setCapability(PolygonAttributes.ALLOW_OFFSET_WRITE);
	  solid.setPolygonAttributes(solidPa);
	  Sphere solidSphere = new Sphere(0.3f, solid);
        grp.addChild(solidSphere);

        ///////////////////////////////////////////////////////////////////
        //
        //
        //
        //
        //
        TransformGroup tgrp = new TransformGroup();
        Transform3D tra = new Transform3D();
        tra.setTranslation(new Vector3f(0.0f,0.0f,-1.0f) );
        tgrp.setTransform(tra);
        wire = new Appearance();
        grp = new OrderedGroup(); 
        wirePa = new PolygonAttributes(
				       PolygonAttributes.POLYGON_LINE, 
				       PolygonAttributes.CULL_BACK, 
				       0.0f);
   	   wire.setPolygonAttributes(wirePa);
	   com.sun.j3d.utils.geometry.Box  wireBox = new com.sun.j3d.utils.geometry.Box (0.3f,0.3f,0.3f,wire);
	   tgrp.addChild(grp);
         grp.addChild(wireBox);
 
         // solid
   	   red = new Color3f(0.0f, 0.0f, 0.0f);
	   solidCa = new ColoringAttributes(colort,
	   ColoringAttributes.SHADE_FLAT);
	   solidMat = new Material();
	   solidMat.setLightingEnable(false);
	   solid = new Appearance();
	   solid.setMaterial(solidMat);
	   solid.setColoringAttributes(solidCa);
	   solidPa = new PolygonAttributes(
			PolygonAttributes.POLYGON_FILL, 
			PolygonAttributes.CULL_BACK, 
			0.0f);
	   solidPa.setPolygonOffsetFactor(dynamicOffset);
	   solidPa.setPolygonOffset(staticOffset);
	   solidPa.setCapability(PolygonAttributes.ALLOW_OFFSET_WRITE);
	   solid.setPolygonAttributes(solidPa);
	   wireBox = new com.sun.j3d.utils.geometry.Box (0.3f,0.3f,0.3f,solid);
  	   grp.addChild(wireBox);
         objTrans.addChild(tgrp); 
 	   return objTrans;

    } 

    /*
    *
    *
    *  Draw a Nurbs  Surface 
    *
    */

    private TransformGroup MakeNurbs() {
        Transform3D trn = new Transform3D();
        TransformGroup grp = new TransformGroup(trn); 
        
        float [] uSeq = {0f,0f,0f,0f,1f,1f,1f,1f };
        float [] vSeq = {0f,0f,0f,0f,1f,1f,1f,1f };

        Point4 Pts[] = { new Point4( -9 , -2 ,  8,  1),
                             new Point4( -4 ,  1  , 8 , 1),
                         new Point4(4 , -3,   6,  1),
                        new Point4(10 , -1  , 8 , 1),
                        new Point4(-6 ,  3 ,  4 , 1),
                         new Point4(0 , -1 ,  4 , 1),
                         new Point4(2 , -1 ,  4 , 1),
                         new Point4(6 ,  2 ,  4 , 1),
                       new Point4(-10 , -2 , -2 , 1),
                       new Point4( -4 , -4 , -2 , 1),
                       new Point4(  4 , -1 , -2 , 1),
                       new Point4( 10 ,  0 , -2 , 1),
                       new Point4( -9 ,  2 , -6 , 1),
                       new Point4( -4 , -4 , -5 , 1),
                       new Point4(  4 ,  3 , -5 , 1),
                       new Point4(  9 , -2 , -6 , 1)};
  


        grp.addChild(new hsdc.nurbs.NurbSurface( 20 ,20 ,uSeq,vSeq,4,4,Pts,
                     createAppearance(2,1.0f,1.0f,0.0f)));

        return grp;
    } 


    /*
    *
    *
    *  Draw a Nurbs  curve 
    *
    */

    private TransformGroup MakeNurbCircle() {
        Transform3D trn = new Transform3D();
        TransformGroup grp = new TransformGroup(trn); 

        int segments = 100;

        float[]  knotSequence = {0.00f, 0.0f, 0.0f, 0.25f, 
                        0.25f, 0.5f, 0.5f, 0.75f, 
                        0.75f, 1.0f, 1.0f, 1.00f };

 
        Point4[] Pts = { new Point4(1f,0f,0f,1f),
                             new Point4(0.707107f,0.707107f,0f,0.707107f),
                             new Point4(0f,1f,0f,1f),
                             new Point4(-0.707107f,0.707107f,0f,0.707107f),
                             new Point4(-1f,0f,0f,1f),
                             new Point4(-0.707107f,-0.707107f,0f,0.707107f),
                             new Point4( 0f,-1f,0f,1f),
                             new Point4( 0.707107f,-0.707107f,0f,0.707107f),
                             new Point4( 1f,0f,0f,1f) };




        grp.addChild(new hsdc.nurbs.NurbsCurve( 100,knotSequence ,Pts,
                     createAppearance(2,1.0f,1.0f,0.0f)));

        return grp;
    } 


    /*
    *
    *
    *  Draw a Nurbs Torus
    *
    */
    private TransformGroup MakeNurbTorus() {
        Transform3D trn = new Transform3D();
        TransformGroup grp = new TransformGroup(trn); 

        int segments = 40;
        int profsegments = 40;


        float[]  knotSequence = { 0.00f, 0.0f, 0.0f, 0.25f, 
                               0.25f, 0.5f, 0.5f, 0.75f, 
                               0.75f, 1.0f, 1.0f, 1.00f };


 
        Point4[] Pts = { new Point4(3f,0f,0f,1f),
                             new Point4(2.121320f,0.707107f,0f,0.707107f),
                             new Point4(2f,1f,0f,1f),
                             new Point4(0.707107f,0.707107f,0f,0.707107f),
                             new Point4(1f,0f,0f,1f),
                             new Point4(0.707107f,-0.707107f,0f,0.707107f),
                             new Point4(2f,-1f,0f,1f),
                             new Point4(2.121320f,-0.707107f,0f,0.707107f),
                             new Point4(3f,0f,0f,1f)  };


        grp.addChild(new hsdc.nurbs.NurbsRevolve( 40,40,knotSequence ,Pts,
                     createAppearance(2,1.0f,1.0f,0.0f)));

        return grp;
    } 

    /**
    *
    *
    *  Use PolygonArray . Vertex Data is taken from Sun's Java Tutorial ( chap. 3 )
    *
    *
    *
    */
    public TransformGroup MakeArbPolygon()
    {

        float[] data = new float[69*3];         
        int i = 0;

        data[i++]= -1.3f; data[i++]= -0.3f; data[i++]= 0.3f; //0
        data[i++]= -0.9f; data[i++]= -0.3f; data[i++]= 0.3f; //1
        data[i++]= -0.8f; data[i++]= -0.1f; data[i++]= 0.3f; //2
        data[i++]= -0.6f; data[i++]= -0.1f; data[i++]= 0.3f; //3
        data[i++]= -0.5f; data[i++]= -0.3f; data[i++]= 0.3f; //4
        data[i++]=  0.2f; data[i++]= -0.3f; data[i++]= 0.3f; //5
        data[i++]=  0.3f; data[i++]= -0.1f; data[i++]= 0.3f; //6
        data[i++]=  0.5f; data[i++]= -0.1f; data[i++]= 0.3f; //7
        data[i++]=  0.6f; data[i++]= -0.3f; data[i++]= 0.3f; //8
        data[i++]=  1.3f; data[i++]= -0.3f; data[i++]= 0.3f; //9
        data[i++]=  1.2f; data[i++]= -0.1f; data[i++]= 0.3f; //10
        data[i++]=  0.5f; data[i++]=  0.0f; data[i++]= 0.3f; //11
        data[i++]=  0.1f; data[i++]=  0.3f; data[i++]= 0.3f; //12
        data[i++]= -0.5f; data[i++]=  0.3f; data[i++]= 0.3f; //13
        data[i++]= -1.1f; data[i++]=  0.0f; data[i++]= 0.3f; //14
        data[i++]= -1.3f; data[i++]=  0.0f; data[i++]= 0.3f; //15
        data[i++]= -1.3f; data[i++]= -0.3f; data[i++]= 0.3f; //16
        System.out.println("end polygon; total vertex count: "+i/3);

        data[i++]= -1.3f; data[i++]= -0.3f; data[i++]=-0.3f; // 0 17
        data[i++]= -1.3f; data[i++]=  0.0f; data[i++]=-0.3f; // 1 18
        data[i++]= -1.1f; data[i++]=  0.0f; data[i++]=-0.3f; // 2 19
        data[i++]= -0.5f; data[i++]=  0.3f; data[i++]=-0.3f; // 3 20
        data[i++]=  0.1f; data[i++]=  0.3f; data[i++]=-0.3f; // 4 21
        data[i++]=  0.5f; data[i++]=  0.0f; data[i++]=-0.3f; // 5 22
        data[i++]=  1.2f; data[i++]= -0.1f; data[i++]=-0.3f; // 6 23
        data[i++]=  1.3f; data[i++]= -0.3f; data[i++]=-0.3f; // 7 24
        data[i++]=  0.6f; data[i++]= -0.3f; data[i++]=-0.3f; // 8 25
        data[i++]=  0.5f; data[i++]= -0.1f; data[i++]=-0.3f; // 9 26
        data[i++]=  0.3f; data[i++]= -0.1f; data[i++]=-0.3f; //10 27
        data[i++]=  0.2f; data[i++]= -0.3f; data[i++]=-0.3f; //11 28
        data[i++]= -0.5f; data[i++]= -0.3f; data[i++]=-0.3f; //12 29
        data[i++]= -0.6f; data[i++]= -0.1f; data[i++]=-0.3f; //13 30
        data[i++]= -0.8f; data[i++]= -0.1f; data[i++]=-0.3f; //14 31
        data[i++]= -0.9f; data[i++]= -0.3f; data[i++]=-0.3f; //15 32
        data[i++]= -1.3f; data[i++]= -0.3f; data[i++]=-0.3f; //16 33
        System.out.println("end polygon; total vertex count: "+i/3);
                                                                  
        data[i++]=  1.3f; data[i++]= -0.3f; data[i++]=-0.3f; // 0 34
        data[i++]=  1.2f; data[i++]= -0.1f; data[i++]=-0.3f; // 1 35
        data[i++]=  1.2f; data[i++]= -0.1f; data[i++]= 0.3f; // 2 36
        data[i++]=  1.3f; data[i++]= -0.3f; data[i++]= 0.3f; // 3 37
        data[i++]=  1.3f; data[i++]= -0.3f; data[i++]=-0.3f; // 4 38
        System.out.println("end polygon; total vertex count: "+i/3);

        data[i++]=  1.2f; data[i++]= -0.1f; data[i++]=-0.3f; // 0 39
        data[i++]=  0.5f; data[i++]=  0.0f; data[i++]=-0.3f; // 1 40
        data[i++]=  0.5f; data[i++]=  0.0f; data[i++]= 0.3f; // 2 41
        data[i++]=  1.2f; data[i++]= -0.1f; data[i++]= 0.3f; // 3 42
        data[i++]=  1.2f; data[i++]= -0.1f; data[i++]=-0.3f; // 4 43
        System.out.println("end polygon; total vertex count: "+i/3);

        data[i++]=  0.5f; data[i++]=  0.0f; data[i++]=-0.3f; // 0 44
        data[i++]=  0.1f; data[i++]=  0.3f; data[i++]=-0.3f; // 1 45
        data[i++]=  0.1f; data[i++]=  0.3f; data[i++]= 0.3f; // 2 46
        data[i++]=  0.5f; data[i++]=  0.0f; data[i++]= 0.3f; // 3 47
        data[i++]=  0.5f; data[i++]=  0.0f; data[i++]=-0.3f; // 4 48
        System.out.println("end polygon; total vertex count: "+i/3);

        data[i++]=  0.1f; data[i++]=  0.3f; data[i++]=-0.3f; // 0 49
        data[i++]= -0.5f; data[i++]=  0.3f; data[i++]=-0.3f; // 1 50
        data[i++]= -0.5f; data[i++]=  0.3f; data[i++]= 0.3f; // 2 51
        data[i++]=  0.1f; data[i++]=  0.3f; data[i++]= 0.3f; // 3 52
        data[i++]=  0.1f; data[i++]=  0.3f; data[i++]=-0.3f; // 4 53
        System.out.println("end polygon; total vertex count: "+i/3);

        data[i++]= -0.5f; data[i++]=  0.3f; data[i++]=-0.3f; // 0 54
        data[i++]= -1.1f; data[i++]=  0.0f; data[i++]=-0.3f; // 1 55
        data[i++]= -1.1f; data[i++]=  0.0f; data[i++]= 0.3f; // 2 56
        data[i++]= -0.5f; data[i++]=  0.3f; data[i++]= 0.3f; // 3 57
        data[i++]= -0.5f; data[i++]=  0.3f; data[i++]=-0.3f; // 4 58
        System.out.println("end polygon; total vertex count: "+i/3);

        data[i++]= -1.1f; data[i++]=  0.0f; data[i++]=-0.3f; // 0 59
        data[i++]= -1.3f; data[i++]=  0.0f; data[i++]=-0.3f; // 1 60
        data[i++]= -1.3f; data[i++]=  0.0f; data[i++]= 0.3f; // 2 61
        data[i++]= -1.1f; data[i++]=  0.0f; data[i++]= 0.3f; // 3 62
        data[i++]= -1.1f; data[i++]=  0.0f; data[i++]=-0.3f; // 4 63
        System.out.println("end polygon; total vertex count: "+i/3);
                                                                    
        data[i++]= -1.3f; data[i++]=  0.0f; data[i++]=-0.3f; // 0 64
        data[i++]= -1.3f; data[i++]= -0.3f; data[i++]=-0.3f; // 1 65
        data[i++]= -1.3f; data[i++]= -0.3f; data[i++]= 0.3f; // 2 66
        data[i++]= -1.3f; data[i++]=  0.0f; data[i++]= 0.3f; // 3 67
        data[i++]= -1.3f; data[i++]=  0.0f; data[i++]=-0.3f; // 4 68
        System.out.println("end polygon; total vertex count: "+i/3);

        int[] stripCount = {17,17,5,5,5,5,5,5,5};  // ******
        TransformGroup grp = new TransformGroup(); 
 

        try { 
        
        Shape3D shp = new Shape3D();

        shp.setGeometry(hsdc.common.PolygonArray.GeometryArrayFromPolygon(data,stripCount));
        shp.setAppearance(createAppearance(2,1.0f,1.0f,0.0f));  
      
        grp.addChild(shp);                    
        }
        catch(Exception e ) 
        {
            e.printStackTrace(); 
            return null;          

        }  

        return grp; 

        

    }    


 
 

    /*
    *  Change the Background color
    *
    *
    */

    private void swapBackGround() 
    {
        try {   
            switch(CurrBackid)
            {
                case 1:
                {
                    if ( CurrBranch != null ) 
                    { 
                        ((BranchGroup)CurrBranch).detach();
                        CurrBranch = null;
                    } 
                    CurrBranch = new BranchGroup();
                    CurrBranch.setCapability(BranchGroup.ALLOW_DETACH);
                    CurrBack = new Background(0.0f,0.0f,1.0f);
                    CurrBack.setApplicationBounds(bounds);
                    CurrBranch.addChild( CurrBack);
                    scene.addChild(CurrBranch);    
                    break; 
                }
                case 0:
                {
                    if ( CurrBranch != null ) 
                    { 
                        ((BranchGroup)CurrBranch).detach();
                        CurrBranch = null;
                    } 

                    CurrBranch = new BranchGroup();
                    CurrBranch.setCapability(BranchGroup.ALLOW_DETACH);  
                    CurrBack = new Background(1.0f,1.0f,1.0f);
                    CurrBack.setApplicationBounds(bounds);
                    CurrBranch.addChild( CurrBack); 
                    scene.addChild(CurrBranch);    
                    break;         
                }
                             
            }               

         }
         catch(Exception e ) 
         {
            e.printStackTrace(); 
         }
        return;
    
    }  

    /*
    *
    *
    *
    *
    *
    */
    private void swapShape() 
    {
        if ( CurrShapeBranch != null ) 
        {
            CurrShapeBranch.detach();
            CurrShapeBranch = null;
        }
        CurrShapeBranch = new BranchGroup();
        CurrShapeBranch.setCapability(BranchGroup.ALLOW_DETACH); 
        oTrans = new  TransformGroup() ; 
        oTrans.setCapability(TransformGroup.ALLOW_TRANSFORM_READ); 
        oTrans.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE); 
        createMouseBehavior(oTrans);
        CurrShapeBranch.addChild(oTrans);
              
        switch(CurrShapeid)
        {
            case 0:
                oTrans.addChild(MakeBox());
                break;  
            case 1:
                oTrans.addChild(MakeCylinder());
                break;  
            case 2:
                oTrans.addChild(MakeCone());
                break;   
            case 3:
                oTrans.addChild(MakeSphere());
                break;  
            case 4:
                oTrans.addChild(MakeAxes());
                break;
            case 5:
                oTrans.addChild(MakeTorus());
                break; 
            case 6: 
                oTrans.addChild(MakeMobius());
                break; 
            case 7:
                oTrans.addChild(MakeTriangle());
                break; 
            case 8: 
                oTrans.addChild(MakeSpiralTorus());
                break; 
            case 9: 
                oTrans.addChild(MakeSpring());
                break; 
            case 10: 
                oTrans.addChild(MakeAltCylinder());
                break; 
            case 11: 
                oTrans.addChild(MakeThickCylinder());
                break; 
            case 12: 
                oTrans.addChild(MakeTetrahedron());
                break; 
            case 13: 
                oTrans.addChild(MakeAltCone());
                break; 
            case 14: 
                oTrans.addChild(MakeKnot());
                break; 
            case 15: 
                oTrans.addChild(MakeBezier());
                break; 
            case 16: 
                oTrans.addChild(MakeBSpline());
                break; 
            case 17: 
                oTrans.addChild(MakeHiddenLine());
                break; 
            case 18:
                oTrans.addChild(MakeNurbs());   
                break; 
            case 19:
                oTrans.addChild(MakeNurbCircle());
                break; 
            case 20:
                oTrans.addChild(MakeNurbTorus());
                break; 
            case 21:
                oTrans.addChild(MakeArbPolygon());   
                break;
        
         }

         scene.addChild(CurrShapeBranch);

     }

    /*
    *  
    *
    *
    *
    */
    private void swapRenderingMode()  
    {

    } 

    /*
    *
    *
    *
    */
    public void init() 
    {
      
        GraphicsConfiguration config =
        SimpleUniverse.getPreferredConfiguration();
        FrontViewCanvas = new Canvas3D(config);
        BackViewCanvas  = new Canvas3D(config);
        FrontView.setLayout(new BorderLayout());
        FrontView.add("Center",FrontViewCanvas);
        BackView.setLayout(new BorderLayout());
        BackView.add("Center",BackViewCanvas);
        FrontView.setVisible(true);
        BackView.setVisible(true);
        add(FrontView);
        add(BackView);
       
        ///////////////////////////////////////////////////////////////
        //
        //
        //
        //
        VirtualUniverse universe   = new VirtualUniverse();
        javax.media.j3d.Locale          locale  = new javax.media.j3d.Locale(universe);
        PhysicalBody    body    = new PhysicalBody();
        PhysicalEnvironment environment = new PhysicalEnvironment(); 

        View  viewX = new View();
        View  viewY = new View();

        viewX.addCanvas3D(FrontViewCanvas);
        viewY.addCanvas3D(BackViewCanvas);
        viewY.setProjectionPolicy ( View.PARALLEL_PROJECTION );
  

        viewX.setPhysicalBody(body);
        viewY.setPhysicalBody(body);
        
        viewX.setPhysicalEnvironment(environment);
        viewY.setPhysicalEnvironment(environment);
        
        ViewPlatform VPX = new ViewPlatform();
        ViewPlatform VPY = new ViewPlatform(); 

        viewX.attachViewPlatform(VPX);
        viewY.attachViewPlatform(VPY);  

        Transform3D sceneTransformY = new Transform3D();
        sceneTransformY.rotY(Math.PI);
        Transform3D Temp = new Transform3D();
        Temp.setTranslation(new Vector3f(0f,0f,2.41f));  
        sceneTransformY.mul(Temp);
          
        Transform3D sceneTransformX = new Transform3D();
   
  
        sceneTransformX.setTranslation(new Vector3f(0f,0f,2.41f));  
        
        TransformGroup VPXTG = new TransformGroup(sceneTransformX); 
        TransformGroup VPYTG = new TransformGroup(sceneTransformY);

        VPXTG.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
        VPXTG.setCapability(TransformGroup.ALLOW_TRANSFORM_READ); 

        VPYTG.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
        VPYTG.setCapability(TransformGroup.ALLOW_TRANSFORM_READ); 

        VPXTG.addChild(VPX);
        VPYTG.addChild(VPY); 
 
    

      
        //////////////////////////////////////////////
        //
        // Set the BackGround
        // 
        
        whitebck.setApplicationBounds(bounds);
        greenbck.setApplicationBounds(bounds);
          
        // Create a simple scene and attach it to the virtual universe

        scene = new BranchGroup();
        scene.setCapability(Group.ALLOW_CHILDREN_EXTEND);
        scene.setCapability(Group.ALLOW_CHILDREN_WRITE);
        scene.setCapability(Group.ALLOW_CHILDREN_READ);
        scene.setCapability(BranchGroup.ALLOW_DETACH);
        scene.addChild(VPXTG);
        scene.addChild(VPYTG);   
        scene.addChild(createAxis());  
        ///////////////////////////////////////////////
        //
        //
        AmbientLight am = new AmbientLight();
        am.setInfluencingBounds(bounds);
        DirectionalLight lt = new DirectionalLight();    
        lt.setInfluencingBounds(bounds);

        scene.addChild(am);
        scene.addChild(lt);
                
        locale.addBranchGraph(scene); 
             
        ////////////////////////////////////////////
        //  Render the Shape on the screen
        //
        //
        swapBackGround() ;
        swapShape();
    

        
    }

    //////////////////////////////////////////////////
    //
    //  Put all de-initialization code here
    //
    public void destroy() {
	//u.removeAllLocales();
    }


    //////////////////////////////////////////////////
    //
    //
    //
    //
    private TransformGroup createMouseBehavior(TransformGroup oTrans )
    {
        MouseRotateEcho  rotator = new MouseRotateEcho( oTrans ); 
        rotator.setUpRotateCallback( this );  
        MouseTranslate trans = new MouseTranslate( oTrans ); 
        trans.setupCallback(this);  
        MouseZoom zoomer = new MouseZoom( oTrans ); 
        zoomer.setupCallback(this);  
        rotator.setSchedulingBounds( bounds ); 
        trans.setSchedulingBounds( bounds ); 
        zoomer.setSchedulingBounds( bounds ); 
        oTrans.addChild( rotator ); 
        oTrans.addChild( trans ); 
        oTrans.addChild( zoomer ); 
        return oTrans; 
    }
    /* 
    *
    * Callback for MouseRotateEcho
    *
    */
    public void rotationChanged( double rotX , double rotY , double rotZ,Transform3D t3d )
    {
        RotVector = new Vector3d( rotX , rotY , rotZ );
        System.out.println("==========================================");
        System.out.println("X = "+rotX + "   Y = " + rotY + "  Z= " + rotZ );
        Vector3f     vs = EulerUtils.getRotAngle(t3d);
        System.out.println("X = "+vs.x + "   Y = " + vs.y + "  Z= " + vs.z );
 
        
        Matrix4d mat = new Matrix4d();
        t3d.get(mat);
        mat.setTranslation(new Vector3d(-0.8 , -0.5 ,0 )); 

        AxisGlobal.setTransform(new Transform3D(mat));
        
        getInfoBus().fireItemAvailable("RotationParams",null,m_producerProxy); 
    }

    /*
    *  Callback for MouseTranslate and MouseZoom
    *
    *
    */
    public void transformChanged( int type , Transform3D transform ) 
    {
        Matrix4d mat = new Matrix4d();
        transform.get(mat);
        if ( type == MouseBehaviorCallback.TRANSLATE || type == MouseBehaviorCallback.ZOOM )   
        {
            TransVector  = new Vector3d();
            mat.get(TransVector); 
            getInfoBus().fireItemAvailable("TranslationParams",null,m_producerProxy); 
        }
    }
        
    /**
    *
    *
    *
    */
    private void DisplayScene( URL FileName ) 
    {
        BranchGroup ViewScene =  getSceneGraphFromFile(FileName);
        if ( ViewScene == null ) 
        {
            return ;

        }

        if ( CurrShapeBranch != null )  
        {
            CurrShapeBranch.detach();
            CurrShapeBranch = null;     
        }

        CurrShapeBranch = ViewScene;
        ViewScene = null;  
        System.gc();               
        scene.addChild(CurrShapeBranch); 
    }
    
    /*
    *
    *  This routine will Retrive a Scene Graph from the ObjFile with the 
    *  help of sun's ObjLoader
    *
    *
    *
    *
    */
    private BranchGroup getSceneGraphFromFile(URL FileName) 
    {
          ////////////////////////////////////////////////////////////////////////  
	    // Create the root of the branch graph
          // and set the necessary capability Bit 
          //
          //
 	    BranchGroup objRoot = new BranchGroup();
          objRoot.setCapability(BranchGroup.ALLOW_DETACH);    
          ////////////////////////////////////////////////////////////////////////         
          // 
          // Create a Transformgroup to scale all objects so they
          // appear in the scene.
          //
          TransformGroup ObjScale   =    new TransformGroup();
          Transform3D    Trans3D    =    new Transform3D();
          Trans3D.setScale(1.0);
          ObjScale.setTransform(Trans3D);
          objRoot.addChild(ObjScale);

          //////////////////////////////////////////////////////////////////
	    //
          // Create the transform group node and initialize it to the
	    // identity and set the CapabilityBit So that Mouse Helper 
          // classes can Operate on it 
          //
          //
	    TransformGroup objTrans = new TransformGroup();
	    objTrans.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
	    objTrans.setCapability(TransformGroup.ALLOW_TRANSFORM_READ);
	    ObjScale.addChild(objTrans);

          ///////////////////////////////////////////////////////////////////////////
          //
          // Always set the flag to ObjectFile.RESIZE so that the Scene Gemetry fits
          // into the unit  cube bounding volume
          //
          // 
          // 
          int flags = ObjectFile.RESIZE;
          //////////////////////////////////////////////////////////////////////////////////
          //
          //  Create an Instance of ObjectFile class with a default crease angle ( for normal
          //  generator ) of 60
          //   
          ObjectFile f = new ObjectFile(flags, 
 	                                 (float)(60 * Math.PI / 180.0));

          Scene s = null;
	
          try {
	        s = f.load(FileName);
	    }
	    catch (FileNotFoundException e) {
	        System.err.println(e);
	        return null;
	    }
	    catch (ParsingErrorException e) {
	        System.err.println(e);
        	  return null;
	    }
	    catch (IncorrectFormatException e) {
        	  return null;
	    }
          catch (Exception e ) {
              return null;
          } 

          createMouseBehavior(objTrans); 
	    objTrans.addChild(s.getSceneGroup());

       
          /////////////////////////////////////////////////////// 
          // Set up the background
          //
          //
          // 
          Color3f bgColor = new Color3f(0.05f, 0.05f, 0.5f);
          Background bgNode = new Background(bgColor);
          bgNode.setApplicationBounds(bounds);
          objRoot.addChild(bgNode);
          ////////////////////////////////////////////////////////////////////////////////
          // 
          //
          //
          // Set up the ambient light
          Color3f ambientColor = new Color3f(0.1f, 0.1f, 0.1f);
          AmbientLight ambientLightNode = new AmbientLight(ambientColor);
          ambientLightNode.setInfluencingBounds(bounds);
          objRoot.addChild(ambientLightNode);
          ////////////////////////////////////////////////////////////////////  
          // 
          // Set up the directional lights
          //
          //
          Color3f light1Color = new Color3f(1.0f, 1.0f, 0.9f);
          Vector3f light1Direction  = new Vector3f(1.0f, 1.0f, 1.0f);
          Color3f light2Color = new Color3f(1.0f, 1.0f, 1.0f);
          Vector3f light2Direction  = new Vector3f(-1.0f, -1.0f, -1.0f);

          DirectionalLight light1
                    = new DirectionalLight(light1Color, light1Direction);
          light1.setInfluencingBounds(bounds);
       
          DirectionalLight light2
                    = new DirectionalLight(light2Color, light2Direction);
          light2.setInfluencingBounds(bounds);
          ////////////////////////////////////////////////////////////////////
          //  Add Light Sources to Scene Graph 
          //
          //  
          objRoot.addChild(light1);
          objRoot.addChild(light2);
          return objRoot;
    }

 
    /**
    *
    *
    *
    *
    */
    private PlatformGeometry createAxis()
    {
        PlatformGeometry pg = new PlatformGeometry();
        AxisGlobal = MakeAxes();
        AxisGlobal.setCapability(TransformGroup.ALLOW_TRANSFORM_READ);
        AxisGlobal.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
        //int height = GlobalCanvas.getHeight();
        //int width  = GlobalCanvas.getWidth();
        //System.out.println("Hieght = " + height );
        //System.out.println("Width = " + width );
 
        //Point3d pt = new Point3d(); 
        //GlobalCanvas.getPixelLocationInImagePlate( (int)width,(int)height,pt);
        //System.out.println("Tr Hieght = " + pt.x );
        //System.out.println(" Tr Width = " + pt.y );

        Transform3D t3d = new Transform3D();
        t3d.setTranslation(new Vector3f(-0.8f , -0.5f ,0f )); 
          
        AxisGlobal.setTransform(t3d);
        pg.addChild( AxisGlobal );
        return pg; 

    }


}


// EOF ThreeDDisplay.java

