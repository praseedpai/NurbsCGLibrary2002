/*
*
*
*   <Copyright and Legal Notice Goes Here>
*
*
*
*
*/

package hsdc.controlpanel;
import java.awt.*;
import javax.media.j3d.*;
import java.util.*;
import java.io.*;
import javax.infobus.InfoBusMember;
import javax.infobus.InfoBusDataConsumer;
import javax.infobus.InfoBusBeanSupport;
import javax.infobus.InfoBusItemAvailableEvent;
import javax.infobus.InfoBusItemRevokedEvent;
import javax.infobus.InfoBus;
import javax.infobus.InfoBusDataProducerProxy;
import javax.infobus.InfoBusDataConsumerProxy;
import javax.infobus.*;
import java.beans.*;
import java.awt.event.*;
import javax.vecmath.*;
import java.text.*;

/**
*
*  ControlPanel class
*
*  @version 1 
*
*  @author  Praseed Pai K.T. 
*/


public class ControlPanel extends    Panel   
                          implements ActionListener,      // not used now , but we may need it
                                     ItemListener,        // For monitoring Combo box events
                                     InfoBusDataConsumer, // For Consuming Mouse Rotation and Translation  
                                                          // feedback
                                     InfoBusDataProducer , 
                                     Serializable 
{

    protected String currentShape = new String("BOX");
    protected String currentbck   = new String("BLUE"); 
    protected String currentren   = new String("SOLID");


   
    /*
    *
    *  Declare instances of combobox 
    *  
    */

    private Choice ShapeChoice    = new Choice();
    private Choice BckChoice      = new Choice();  
    private Choice RenderChoice   = new Choice();

    /*
     *
     *
     *   Declare Labels
     *
     *
     */
    private Label ShapeLabel    = new Label("Shape");
    private Label BackLabel     = new Label("BackGround");
    private Label RendLabel     = new Label("Render Mode ");
    private Label TransLabel = new Label("Translation");
    private Label RotLabel   = new Label("Rotation");     
    private Label TransValue    = new Label("               "); 
    private Label RotationValue = new Label("               "); 
   
    /*
     *  
     * For InfoBus support
     *
    */

    private  InfoBusBeanSupport  ims = null;

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

 
    /**
    *
    *   Constructor
    */

    public ControlPanel() throws Exception 
    {
        super();
        setLayout( new GridLayout(5, 2) ); 
       // setSize(1024,256);  
        ///////////////////////////////////////////////////////  
        //  Add Labels 
        //
        //
        add(TransLabel);
        add(TransValue);
        add(RotLabel); 
        add(RotationValue);
        //////////////////////////////////////////////////////
        //  Add Geometric primitives to ComboBox
        //
        // 
        ShapeChoice.addItem("BOX");
        ShapeChoice.addItem("CONE");
        ShapeChoice.addItem("CYLINDER");
        ShapeChoice.addItem("SPHERE");
        ShapeChoice.addItem("AXES");
        ShapeChoice.addItem("TORUS");
        ShapeChoice.addItem("MOBIUS");
        ShapeChoice.addItem("TRIANGLE");
        ShapeChoice.addItem("SPIRAL TORUS");
        ShapeChoice.addItem("SPRING");
        ShapeChoice.addItem("MY CYLINDER");
        ShapeChoice.addItem("THICK CYLINDER");
        ShapeChoice.addItem("TETRAHEDRON");
        ShapeChoice.addItem("MY CONE");
        ShapeChoice.addItem("KNOT");
        ShapeChoice.addItem("BEZIERSURFACE");
        ShapeChoice.addItem("SPLINESURFACE");
        ShapeChoice.addItem("HIDDEN LINE REMOVAL");
        ShapeChoice.addItem("NURBS BEZIER");   
        ShapeChoice.addItem("NURBS CIRCLE"); 
        ShapeChoice.addItem("NURBS TORUS"); 
        ShapeChoice.addItem("ARBITARY POLYGON"); 
        ////////////////////////////////////////// 
        // Background 
        // 
        // 
        BckChoice.addItem("BLUE");
        BckChoice.addItem("WHITE"); 
        /////////////////////////////////////////////////
        //  Rendering Modes
        //
        //
        //
        RenderChoice.addItem("SOLID");
        RenderChoice.addItem("MATERIAL");
        RenderChoice.addItem("WIREFRAME");
        RenderChoice.addItem("POINTS");
        //////////////////////////////////////////////////////
        //
        //  Add Event handlers
        //
        ShapeChoice.addItemListener(this); 
        BckChoice.addItemListener(this);
        RenderChoice.addItemListener(this);
        ///////////////////////////////////////////////////
        //
        //  Add to Panel
        // 
        add(ShapeLabel);
        add(ShapeChoice); 
        add(BackLabel); 
        add(BckChoice); 
        add(RendLabel); 
        add(RenderChoice); 


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
             throw e; 
             
        } 

        m_producerProxy = new InfoBusDataProducerProxy(this); 
        m_consumerProxy = new InfoBusDataConsumerProxy(this); 

    }
    /**
    *
    * Delegate the Call to InfoBusBeanSupport instance
    *  
    *
    */
    public InfoBus getInfoBus() 
    {
        return ims.getInfoBus();
    }
    /**
    *
    * Delegate the Call to InfoBusBeanSupport instance
    *  
    *
    */
    public void addInfoBusVetoableListener(VetoableChangeListener vcl) 
    {
        ims.addInfoBusVetoableListener(vcl);
    }

    /**
    *
    * Delegate the Call to InfoBusBeanSupport instance
    *  
    *
    */

    public void removeInfoBusVetoableListener(VetoableChangeListener vcl) 
    {
        ims.removeInfoBusVetoableListener(vcl);
    }

    /**
    *
    * Delegate the Call to InfoBusBeanSupport instance
    *  
    *
    */

    public void addInfoBusPropertyListener(PropertyChangeListener pcl) 
    {
        ims.addInfoBusPropertyListener(pcl);
    }

    /**
    *
    * Delegate the Call to InfoBusBeanSupport instance
    *  
    *
    */

    public void removeInfoBusPropertyListener(PropertyChangeListener pcl) 
    {
        ims.removeInfoBusPropertyListener(pcl);
    }

    /**
    * Listener for ListBoxes
    * 
    *
    *
    */
    public void itemStateChanged(ItemEvent e)
    {
        String st = null;  
        ItemSelectable It = (ItemSelectable)e.getItemSelectable();
        if (It == null ) {
            return;
        } 
         
        if (( st = (String) e.getItem() ) == null ) {
            return;
        }

        if ( It ==  ShapeChoice ) {    // Shape Combo Box
            setShape(st); 
        }
        else if ( It ==  BckChoice ) {
            setBackGroundColor(st); 
        }
        else  if ( It == RenderChoice ) 
        {
            setRenderingMode(st); 
        }
        
    }  
   
    /**
    *
    *  Currently not being used
    *
    *
    */ 
    public void actionPerformed( ActionEvent evt ) 
    {
       
    }
    /**
    * This method is called by the InfoBus class on behalf of a data producer 
    * that is announcing the  availability of a new data item by name. 
    *  
    *
    *
    */
    public void dataItemAvailable(InfoBusItemAvailableEvent e) 
    {
        if ( e == null ) 
        {
            return;
        } 

        ///////////////////////////////////////////////////
        //
        //  Get The Data item Name
        // 
        String name = e.getDataItemName();
            
        if ( name == null ) 
        {
            return ;
        } 

        if(name.equals("RotationParams")) 
        {
            Vector3d rotVector = (Vector3d)e.requestDataItem(m_consumerProxy,null);

            DecimalFormat fmt = new DecimalFormat("##.###");

            RotationValue.setText("X="+ fmt.format(rotVector.x) + " Y="+fmt.format(rotVector.y)+
                                  " Z = " + fmt.format(rotVector.z));
        }
        else if ( name.equals("TranslationParams")) 
        {
            Vector3d transVector = (Vector3d)e.requestDataItem(m_consumerProxy,null); 

            DecimalFormat fmt = new DecimalFormat("####.###");

            TransValue.setText("X="+ fmt.format(transVector.x) + " Y="+fmt.format(transVector.y)+
                               " Z = " + fmt.format(transVector.z));
        }
       else if ( name.equals("PanelSleep") ) {
           setSleep(false);     
       }
       else if ( name.equals("PanelWakeup") ) {
           setSleep(true); 
       }
      else if ( name.equals("WindowResized") ) {
          Window Ft = (Window)this.getParent();
          int h = Ft.getHeight()/3;
          int w = Ft.getWidth();
              


      }    

    }
         
    /*
    * Since we are passing only Objects ( not data items ) and we are not planning to modify       
    * the Data item , it is empty
    */
    public void dataItemRevoked(InfoBusItemRevokedEvent event) 
    {

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

        if ( name == null ) 
        {
            return;
        }

        if ( name.equals("NextShape") ) 
        {
            e.setDataItem(currentShape);
        }
        else if ( name.equals("CanvasBackGround") ) {
            e.setDataItem(currentbck);
        }  
        else if ( name.equals("RenderingMode") ) 
        {
             e.setDataItem(currentren); 
        }   
    }
 
    /**
    *
    *  Satisfy the compiler 
    */
    public void propertyChange(PropertyChangeEvent e) 
    {

    }

    /**
    *
    *
    *  Property Set for Shape
    *
    *
    *
    */
    private void setShape( String shape ) {

        ////////////////////////////////////////////////
        // In production quality code , call dataItemRevoked before 
        // setting the string reference to null 
        currentShape = null; 
        currentShape = (String) shape.toString();
        ///////////////////////////////////////////////////
        //
        //  Notify The Bus about the Arrival of new Shape
        //
        //  DataItemName :- NextShape
        //  Flavours     :- None
        //  Producer     :- this class 
        // 
        getInfoBus().fireItemAvailable("NextShape",null,m_producerProxy); 
    }

    /**
    *
    * Property Get for Shape
    *
    */
    private String getShape() {
        return currentShape ;
    }   

    /**
    *  Set method for BackGroundColor property
    *
    *
    */
    private void setBackGroundColor( String color ) {
        currentbck = null;
        currentbck = color.toString();
        ///////////////////////////////////////////////////
        //
        //  Notify The Bus about the Arrival of new BackGround
        //
        //  DataItemName :- NextShape
        //  Flavours     :- None
        //  Producer     :- this class 
        // 
        getInfoBus().fireItemAvailable("CanvasBackGround",null,m_producerProxy);
    }
   
    /**
    *  Get method for BackGroundColor property
    *
    *
    */
    private String getBackGroundColor() {
        return currentbck;
    } 

    /**
    *  set method for RenderingMode property
    * 
    *
    */
    private void setRenderingMode( String mode ) 
    {
        currentren = null;
        currentren = mode.toString();
        ///////////////////////////////////////////////////
        //
        //  Notify The Bus about the Arrival of new BackGround
        //
        //  DataItemName :- NextShape
        //  Flavours     :- None
        //  Producer     :- this class 
        // 
        getInfoBus().fireItemAvailable("RenderingMode",null,m_producerProxy);
    }
    /**
    *  get method for RenderingMode property
    * 
    *
    */
    private String getRenderingMode() {
        return currentren;
    } 

    /**
    *
    *
    *
    */
    private void setSleep(boolean b) 
    {
        setEnabled(b);  
        if ( isEnabled() )
        {
            setShape(getShape());
        } 
    }
    /**
    *
    *
    *
    */
    private boolean getSleep() 
    {
          return isEnabled();   

    } 
    


}

//ControlPanel.java
