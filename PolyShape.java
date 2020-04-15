/**
*
*
*
*  <copyright notice goes here >
*
*
*
*
*
*
*/

package hsdc.engine;

import javax.media.j3d.*;
import javax.vecmath.*;
import com.sun.j3d.utils.geometry.*;
import java.io.*;
import java.awt.event.*;

import hsdc.common.*;

/**
*
*  This is a prototype class to experiment with dynamic PolygonArray .
*  The class extends the Java3D API's Shape3D node and handles the polygon
*  Tesselation details inside. 
*
*
*  @author:  <unascribed>
*
* 
*  @version : 1
*
*/

public class PolyShape extends Shape3D 
{
    /**
    *
    *
    *
    */   

    int[] m_strip = null; 
    float[]  flt  = null;    

    /*
    *
    *  
    *
    *
    *
    */ 
    private int EDIT_FLAG = -1;  
    public static int CREATE_NEW  = 0x1;
    public static int ADD_TO_LAST = 0x2; 
    
    /**
    *  ctor for the Polyshape class. Takes an array of coordinate points ,
    *  and StripCounts . StripCount Arrays should sum up to # of vertices 
    *
    */ 
    public PolyShape(float[] farray , int [] StripCounts ,Appearance app ) throws Exception
    {
        super();
        /////////////////////////////////////////////////
        //  Set the necessary capability bit to change the value when this 
        //  is part of a live scene graph 
        //     
        setCapability(ALLOW_GEOMETRY_READ);
        setCapability(ALLOW_GEOMETRY_WRITE); 
        //////////////////////////////////////////////////////  
        //  if No strip count is given , Assume Strips to be 1.
        //    
        //
        //
        if ( StripCounts != null ) 
        {
            m_strip = new int[StripCounts.length];
            System.arraycopy(StripCounts,0,m_strip,0,StripCounts.length);
        }
        else {
            m_strip = new int[1];
            m_strip[0] = (farray == null ) ? 0 : farray.length/3;
        }    

        //////////////////////////////////////////////////////////
        //
        //  if Vertex given , Copy to internal array for later 
        //  manipulation 
        //
        if ( farray != null )  
        {
            flt = new float[farray.length];
            System.arraycopy(farray,0,flt,0,farray.length);
            MakeRegularPolys(); 
            Tesellate();
        } 
        setAppearance(app);
    } 

    /**
    *  Routine to add a point to PolyShape 
    *   
    *  @param coord  array of floating point values  3 for each vertex
    *  @param Flag   Strip management flags
    *          ADD_TO_LAST -  add to last strip   
    *          CREATE_NEW  -  create a new strip 
    */
    public int add( float[] coord , int Flag ) 
    {

        ///////////////////////////////////////
        // Check whether we are in edit mode or not ,
        // if we are not in edit mode . do not do anything
        // 
        if ( EDIT_FLAG == -1 ) return -1; 

        ///////////////////////////////////////////////////////////////
        //
        //  Create a Temporary array , big enough to hold all the elements 
        //  and Copy the elements to new array and re-assign.
        //   
        float[] Temp = new float[flt.length + coord.length];
        System.arraycopy(flt,0,Temp,0,flt.length);
        System.arraycopy(coord,0,Temp,flt.length,coord.length); 
        flt = null; 
        flt = Temp;  

        ///////////////////////////////////////////////////////
        // Strip Management 
        //
        //
        switch( Flag ) 
        {
            case 0x1:
            {
               m_strip[m_strip.length-1] += coord.length/3;
            } 
            break;
            case 0x2:
            {
              int [] TempStrip = new int[ m_strip.length+1];
              System.arraycopy( m_strip,0,TempStrip,0,m_strip.length);
              TempStrip[TempStrip.length-1] = coord.length/3; 
              m_strip = TempStrip; 
              TempStrip =null;
            }    
            break;
        }
        return 1;  
    } 

    /**
    *
    *  Remove a Vertex From a PolyShape 
    *
    *  @param  x  x-coordinate 
    *  @param  y  y-coordinate 
    *  @param  z  z-coordinate 
    */
    public int delete( float x , float y , float z ) 
    {
        ////////////////////////////////////////////////
        // check the EDIT flag. if flag is not set do not do anything
        //
        //
        //
        if ( EDIT_FLAG == -1 ) return -1; 

        /////////////////////////////////////////////////////
        // Look up for the index 
        //
        //
        int index = -1;
        for(int i = 0; i < flt.length; i+=3 )
        {
            if ( flt[i] == x && flt[i+1] == y && flt[i+2] == z ) 
            {
                index = i;         
                break; 
            }  
        }
        ////////////////////////////////////////////////////
        // if not found , return
        //
        // 
        if ( index == -1 ) { return -1; }

        /////////////////////////////////////////////////////////////
        // Get the Strip index 
        //
        //   
        int strip = GetStrip(index);  

        if ( strip == -1 ) { return -1; }

        ////////////////////////////////////////////////////////////////////
        //
        // Call Delete and Adjust strip helper routine to do the real stuff
        //  
        return DeleteAndAdjustStrip( index , strip ); 
       
    }  
    
    /**
    *  This routine has to be called to edit ( Add or Delete vertex to 
    *  PolygonShape;
    *
    *   
    *
    */   
    public void BeginEdit() 
    { 
        EDIT_FLAG = 0;
    } 


    /**
    *
    *
    *
    *
    *
    *
    */
    public void EndEdit() 
    {
        if ( EDIT_FLAG == -1 ) 
            return;

        EDIT_FLAG = -1;  

        MakeRegularPolys(); 
        Tesellate();

    }   

    /*
    *
    *
    *
    *
    *
    *
    *
    */
    private int GetStrip(int ij) 
    {
         int Sum = 0;
    
         for( int i = 0; i< m_strip.length; ++i )
         {
               Sum += m_strip[i]; 
               if ( ij <= Sum )  
               {
                   return i; 
               }   
           

         }    
         return -1;  
    
    }     

    /**
    *
    *
    *
    *
    *
    *
    *
    */
    private int DeleteAndAdjustStrip( int index , int stripindex )
    {
        m_strip[stripindex]--;
        float[] Temp = new float[ flt.length-3]; 
        System.arraycopy(flt ,0,Temp,0,index);
        System.arraycopy(flt,index+1,Temp,index,(flt.length - index + 1 ));   
        flt = null;
        flt = Temp; 
        return 1;
    }


    /**
    *
    *  The class expects data set as closed polygons to triangulate it 
    *  better. if some values are not found be in confirmance with this ,
    *  additional vertex data will be inserted to normalize the data.
    *
    *
    *
    */
    private int MakeRegularPolys() 
    {
        //////////////////////////////////////////////////////////////////
        //  
        //  Create a Temporary Array with sufficient length
        //
        //
        //
        float Temp[]  = new float[ flt.length + (m_strip.length*3)*3 ];
        ///////////////////////////////////////////////////////////////////////////////////
        //   valid_index  := To Keep track of Next insertion point in the Temp Array
        //   count_so_far := running sum of counts of Vertex
        //   prev_count   := previos value of count_so_far
        //   count_zero   := count of zero element strips     

        int valid_index  = 0;
        int count_so_far = 0;   
        int prev_count = 0;  
        int count_zero = 0; 
         
        /////////////////////////////////////////////////////////////////
        //  Iterate over the strip array and insert dummy vertexes to make
        //  each strip a closed polygon. 
        //  
        for( int i = 0; i < m_strip.length; ++i )
        {
            if ( m_strip[i] ==  0 ) 
            {
               count_zero++;  
               continue; 
            }        

            prev_count = count_so_far;
            count_so_far += m_strip[i]*3;
         
            if ( m_strip[i] == 1 )  
            {
              System.arraycopy(flt,prev_count,Temp,valid_index,3);
              ////////////////////////////////////////////////////
              //  Bump the index count and replicate value two times
              //
              valid_index+=3; 
              float x = flt[prev_count];
              float y = flt[prev_count+1];  
              float z = flt[prev_count+2];  
              /////////////////////////////////////////////////////
              //
              //
              // 
              Temp[valid_index++] =  x;
              Temp[valid_index++] =  y;
              Temp[valid_index++] =  z;
              Temp[valid_index++] =  x;
              Temp[valid_index++] =  y;
              Temp[valid_index++] =  z;
              m_strip[i]+=2;
 
              
           } 
           else if ( m_strip[i] == 2 ) 
           {
              System.arraycopy(flt,prev_count,Temp,valid_index,6);
              ////////////////////////////////////////////////////
              //  Bump the index count and replicate value 1 time
              //
              valid_index+=6; 
              Temp[valid_index++] =  flt[prev_count];
              Temp[valid_index++] =  flt[prev_count+1];
              Temp[valid_index++] =  flt[prev_count+2];
              m_strip[i]+=1;
 
  
           }
           else {

              
              System.arraycopy(flt,prev_count,Temp,valid_index,m_strip[i]*3);
              valid_index += m_strip[i]*3; 
              
              float x = flt[prev_count];
              float y = flt[prev_count+1];
              float z = flt[prev_count+2];
              
              int last_count = (prev_count + m_strip[i]*3)-3;
 
              if ( !(flt[last_count] == x && flt[last_count] == y &&
                                    flt[last_count] == z ) )    
              {
                   Temp[valid_index++] =  x;
                   Temp[valid_index++] =  y;
                   Temp[valid_index++] =  z;
                   m_strip[i]+=1; 
              }    

           }  

           
  
         }

         if (count_zero != 0 ) 
         {
               int vdata = m_strip.length-count_zero;
               int TempStrip[] = new int[vdata]; 
               int c = 0; 
               for(int i=0; i < m_strip.length; ++ i ) 
               {
                   if ( m_strip[i] != 0 ) 
                   {
                        TempStrip[c] = m_strip[i];
                        c++;  
                   }             
                
               }

               m_strip = null;
               m_strip = TempStrip;  

        }
        flt = new float[ valid_index];
        System.arraycopy(Temp, 0, flt,0,valid_index);    
        Temp = null;  
        return 1;  
        

             
    }

 
    /*
    *
    *  Call the Triangulator and Normal generator to Tesellate the aribitary polygon
    *  to convex Triangle list
    *
    *
    *
    *
    */
    private void Tesellate() 
    { 
        Geometry geom = GeometryArrayFromPolygon(flt,m_strip);  
        setGeometry(geom);
    } 
    
    /*
    *
    *
    *
    *
    */
    /**
    *
    *
    *
    *
    *
    */    
    public static GeometryArray GeometryArrayFromPolygon(float[]  coord , int[] StripCounts) 
    {
         GeometryInfo geom = null;  
         int length = coord.length/3;
         if ( length <  3 )  
         {
             throw new RuntimeException("Polygon needs at least 3 vertex");
         } 
  
         try {
          geom = new GeometryInfo( GeometryInfo.POLYGON_ARRAY );
          geom.setCoordinates(coord);
          geom.setStripCounts(StripCounts);   
          Triangulator Ti = new Triangulator();
          Ti.triangulate(geom);
          NormalGenerator Tn = new NormalGenerator();
          Tn.generateNormals(geom);
          Stripifier sf = new Stripifier();
          sf.stripify(geom); 
         }
         catch(Exception e ) {
            return null;  
         } 
         return geom.getGeometryArray();

    }
   
    

}
