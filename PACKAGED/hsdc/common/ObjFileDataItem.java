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

import javax.infobus.*;
import java.util.Vector;
import java.util.Enumeration;
import java.util.Locale;
import java.net.URL;



public class ObjFileDataItem     implements DataItem, 
                                            DataItemChangeManager
{

    private String  ItemName   = new String("");    
    private URL     UrlName    = null;   

    /* 
    *
    *  Helper Class for DataItemChangeManager
    *
    */

    protected DataItemChangeManagerSupport  m_changeSupport = null;
    /* 
    *
    *  Who is the Producer ?
    *
    */

    protected InfoBusEventListener          m_source = null;
 
     

    public  ObjFileDataItem(URL url , String DataName , InfoBusEventListener source ) 
    {
        ItemName = DataName;
        UrlName  = url;
        m_source = source;
        m_changeSupport = new DataItemChangeManagerSupport(this);
    }



    /**
    * 
    *  Delegate the Call to DataItemChangeManagerSupport instance 
    *
    */
    public void	addDataItemChangeListener( DataItemChangeListener l )
    {
        m_changeSupport.addDataItemChangeListener( l );
    }
    /**
    * 
    *  Delegate the Call to DataItemChangeManagerSupport instance 
    *
    */
    public void	removeDataItemChangeListener(DataItemChangeListener l)
    {
        m_changeSupport.removeDataItemChangeListener( l );
    }

    /**
    * Returns the InfoBusEventListener which offers this DataItem on the
    * InfoBus
    */
    public InfoBusEventListener getSource()
    {
        return m_source;
    }

    /**
    * ObjFileDataItem supports a "Name" property, which returns the data
    * name supplied to the constructor.
    */
    public Object getProperty( String propertyName )
    {
        if ( "Name".equalsIgnoreCase(propertyName) )
        {
            return ItemName;
        }
        else return null;
    }

    /**
    * Mandatory if one is using DataItemRevoked method to annonce certain 
    * dataitems are sterile from now onwards. 
    * 
    */

    public void release()
    {
        UrlName  = null;
        
    }

    
    /**
    *
    *
    *
    */
    public URL getUrl() {
        return UrlName;
    } 

}
