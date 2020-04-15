/*
*
*
*   <Copyright and Legal Notice Goes Here>
*
*
*
*
*/
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
import java.awt.datatransfer.*;
import java.net.URL;
import java.beans.*;
import hsdc.common.*;
import hsdc.threedpack.*;
import hsdc.controlpanel.*;


/**
*
*
*   This is the main Application which acts a container for ThreeDDisplayBean  
*   and ControlPanel JavaBean. The Interaction bet'n Beans are done through
*   InfoBus. 
*
*
*
*/
public class ProtoUniverse extends    Frame 
                           implements InfoBusMember,         // Mandatory for InfoBus support
                                      InfoBusDataConsumer,   // I'm a producer
                                      InfoBusDataProducer,   // I'm a consumer too
                                      InfoBusDataController , // I may control traffic too
                                      Serializable,          //  
                                      ActionListener,         // Menu , Button handler
                                      ComponentListener    // To Monitor the Change in Size
      
{

    /*
     *  ThreeDDisplayBean  is instantiated using Beans.Instantiate 
     *
    */ 
  
    private ThreeDDisplayBean Display3D =  null;

    /*
     *  ControlPanel is instantiated using Beans.Instantiate 
     *  ControlPanel and ThreeDDisplayBean  communicate Via infobus
     *  (So do the communication bet'n Container and Beans )
    */

    private ControlPanel   ShapePanel =  null ;   //new ControlPanel();

    /*
     *  Since Passing Vertex data has to be worked out , For the time being 
     *  Container converts the filename path to a Url and this is being Transferred
     *  through the infobus using an Instance Of  ObjFileDataItem
     *
    */

    private URL  ObjFileUrl = null;
    /*
     * The hsdc.common.ObjFileDataItem ( an implementation of InofBus  DataItem interface )
     * is used to pass the The Wavefront Obj File Path to Display Bean.
     *
     *
    */
    private ObjFileDataItem  obj_data = null;        

    /*
    *
    * Following class speeds up the infobus development and it can be 
    * Considered as the Adapter equivalent of the InfoBusMember
    */

    private InfoBusMemberSupport ims;  


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
    *  Synchronization Object for InfoBus Data
    *
    */ 

    private Object sync_obj_lock = new Object();

    /*
    *
    *  Synchronization Object for InfoBus Data
    *
    */ 
    private Object sync_event_lock = new Object(); 
    

    /*
     *
     *  Menu Management 
     *
     *
    */ 

    MenuBar   MainBar    = new MenuBar();
    Menu      FileMenu   = new Menu("File");
    MenuItem  GeomFileItem = new MenuItem("Load Geometry File");
    MenuItem  PanelItem    = new MenuItem("Panel");
    MenuItem  ExitItem     = new MenuItem("Exit");

    /*
    *
    *
    *  Ctor For The ProtoUniverse
    *
    *
    */

    public ProtoUniverse()  throws java.io.IOException,java.lang.ClassNotFoundException
    {
        super("Container for ControlPanel and ThreeDDisplayBean"); 
        setLayout(new BorderLayout()); 
        setSize(1000,600);    
        ////////////////////////////////////////////////////// 
        //   InfoBus related Stuff
        // 
        //
        ims = new InfoBusMemberSupport(null);
        //////////////////////////////////////////////////////
        // Proxy to eliminate undwanted introspection using Java
        // Reflection API
        //  
        m_producerProxy = new InfoBusDataProducerProxy ( this );

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
            System.exit(0);  
        }
        catch ( Exception e ) 
        { 
            System.out.println("Failed to Join InfoBus Exiting .... "); 
            System.out.println(e.toString());
            System.exit(0); 

        }
        ///////////////////////////////////////////////////////////////
        //  Use Beans.Instantiate to Load the Bean
        //
        // 
        try {
	    ClassLoader cl = this.getClass().getClassLoader();
	    Object o = Beans.instantiate(cl,"hsdc.controlpanel.ControlPanel");
	    ShapePanel = (ControlPanel) o;
            o=null;
            o = Beans.instantiate(cl,"hsdc.threedpack.ThreeDDisplayBean");
            Display3D = (ThreeDDisplayBean)o;
            o=null;
     	  } 
        catch( java.io.IOException ex ) 
        {
            String  error = "Couldn't instantiate bean " + ex;
	      System.err.println(error);
	      System.exit(0); 
        } 
        catch( java.lang.ClassNotFoundException ex ) 
        {
            String  error = "Couldn't instantiate bean " + ex;
	      System.err.println(error);
	      System.exit(0); 
        }
        catch (Exception ex) {
   	      String  error = "Couldn't instantiate bean " + ex;
	      System.err.println(error);
	      System.exit(0); 
	  }

        ///////////////////////////////////////////////
        //
        //   Menu Management Function 
        // 
        FileMenu.add(GeomFileItem);
        FileMenu.add(PanelItem);
        FileMenu.add(ExitItem); 
        MainBar.add(FileMenu);
        ExitItem.addActionListener(this);
        PanelItem.addActionListener(this);
        GeomFileItem.addActionListener(this);
        setMenuBar(MainBar);
        /////////////////////////////////////////////////
        // 
        //    Close Hanler , to gracefuly exit out of program
        //
        addWindowListener( new WindowAdapter() {

            public void windowClosing(WindowEvent e ) {
                System.exit(0);
            }
        } ); 

        addComponentListener(this);
        /////////////////////////////////////////////////////////
        //  Display related stuff
        //
        //
        init();  
        getInfoBus().fireItemAvailable("WindowResized",null, m_producerProxy);      
        pack(); 
        setVisible(true);        
      
    }

    /**
    *
    *  Method for Setting InfoBus
    *  Routes the call to InfoBusMemberSupport instance
    *
    */
    public void setInfoBus(InfoBus newInfoBus) throws PropertyVetoException 
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
    /*
    *  Following methods are InfoBusDataController related ones ,
    *  Since we are not acting as a controller , it has been stubed.
    *  Later it will be handy
    *
    */
    public void setConsumerList ( Vector consumers ) 
    {

    }
	
    public void setProducerList ( Vector producers )
    {

    }
	
    public void addDataConsumer ( InfoBusDataConsumer consumer )
    {

    }

    public void addDataProducer ( InfoBusDataProducer producer )
    {

    }
	
    public void removeDataConsumer ( InfoBusDataConsumer consumer )
    {

    }
	
    public void removeDataProducer ( InfoBusDataProducer producer )
    {

    }
	
    public boolean fireItemAvailable ( String dataItemName, DataFlavor[] flavors, InfoBusDataProducer source )
    {	
        return false;  // satisy the compiler
    }
	
    public boolean fireItemRevoked ( String dataItemName, InfoBusDataProducer producer ) 
    {
        return false;
    }
	
    public boolean findDataItem ( String dataItemName, DataFlavor[] flavors,
                                  InfoBusDataConsumer consumer, Vector foundItem) 
    {
        return false;
    }

    public boolean findMultipleDataItems ( String dataItemName, DataFlavor[] flavors,
                                           InfoBusDataConsumer consumer, 
                                           Vector foundItems) 
    {
        return false;
    }

    /*
    *
    *  Stub method to satisfy the compiler ( We haven't implemented propertyChange support)
    *  This may change in future
    */
    public void propertyChange(PropertyChangeEvent e) 
    {
 
    }
    /*
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

    } 
    /*
    *
    *
    *  For the Time Being a Stub method to satisfy the compiler
    *
    */
    public void dataItemRevoked(InfoBusItemRevokedEvent e)
    {

    }

    /*
    *   Here we put data into InfoBus , This method has been called
    *   by InfoBus on behalf of a Consumer 
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
        
        synchronized( sync_obj_lock )
        {
            if ( name.equals("LoadNextFile") ) 
            {
                obj_data =null; 
                obj_data = new  ObjFileDataItem( ObjFileUrl ,"LoadNextFile",m_producerProxy); 
                e.setDataItem(obj_data);
            }
   
        } 


    }

    /**
    *
    *
    *
    */
    public void init() 
    {
        add("Center", Display3D);
        add("South",ShapePanel);

       //add(Display3D);
       //add(ShapePanel); 
    }
    /*
    *
    *  Perform all the Clean up's here
    *
    *
    */
    public void destroy() 
    {
	
    }

    /* 
     *  Menu Event Handler 
     *
     *
    */

    public void actionPerformed( ActionEvent evt ) 
    {
        Object source = evt.getSource();

        if ( source == ExitItem )      // Pressed Exit Button
        {
             System.exit(0);

        }
        else if ( source == GeomFileItem ) 
        {
             FileDialog fd = new FileDialog(this);
             fd.setMode(FileDialog.LOAD); 
             fd.setDirectory("*.obj");

             fd.show(); 

             String FileName      = fd.getFile();
             String DirectoryName = fd.getDirectory(); 

             if (FileName == null ) {
                 return;
             }

             try {

                 ObjFileUrl = new URL( "file:"+DirectoryName+FileName);
             }
             catch(java.net.MalformedURLException e ) 
             {
                 e.printStackTrace();
                 return;
             }

             synchronized(sync_obj_lock)
             {
                 if ( obj_data != null ) {
                     synchronized( sync_event_lock )
                     {
                         getInfoBus().fireItemRevoked("LoadNextFile",m_producerProxy); 
                     }
                 }
             } 

             synchronized(sync_event_lock)
             {
               getInfoBus().fireItemAvailable("LoadNextFile",null,m_producerProxy); 
             } 

             ////////////////////////////////////////////////////////
             //  Fire ItemAvailable Event to a Specific Consumer to improve
             //  throughput. 
             //
             
                 getInfoBus().fireItemAvailable("PanelSleep",null,m_producerProxy,ShapePanel);  
             
                
          }

          else if ( source == PanelItem )  
          {
              getInfoBus().fireItemAvailable("PanelWakeup",null,m_producerProxy,ShapePanel);  
          }
           

    }
    /**********************************************************
    *  Following methods are ComponentListener implementation methods
    *
    *
    *
    */


    public void componentResized(ComponentEvent e) 
    {
         getInfoBus().fireItemAvailable("WindowResized",null,m_producerProxy);      
    }

    public void componentMoved(ComponentEvent e) 
    {

    }

    public void componentShown(ComponentEvent e) 
    {

    }

    public void componentHidden(ComponentEvent e) 
    {

    }
    
 
    /**
    * The following allows ProtoUniverse to be run as an application
    * 
    *
    **/
    public static void main(String[] args) 
    {
       try 
       { 
           new ProtoUniverse();
       }
       catch(Exception e ) 
       {
           e.printStackTrace();
           System.exit(0); 
       } 

    }

}


// EOF ProtoUniverse.java