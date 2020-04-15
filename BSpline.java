/**
*
*
*  <CopyRight goes here >
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
*
*
*  Adapted from Paul Bourke Site 
*
*
*
*
*
*/


public class BSpline extends Shape3D
{
    private static int NI = 3;
    private static int NJ = 4;

    private static int TI = 3;
    private static int TJ = 3;

   
    private static int  RESOLUTIONI = 30;
    private static int RESOLUTIONJ = 40;

    private   int[] KnotsI  = new int[NI+TI+1];
    private  int[] KnotsJ  = new int[NJ+TJ+1];
 

    Point3f[] inp = null; 
    Point3f[] outp = null; 
    /////////////////////////////////////////////////
    //   
    // Calculate Knot Values
    //
    //    

    void SplineKnots(int[] u,int n,int t)
    {
        int j;
        for (j=0;j<=n+t;j++) 
        {
             if (j < t)
                 u[j] = 0;
             else if (j <= n)
                 u[j] = j - t + 1;
             else if (j > n)
                 u[j] = n - t + 2;	
         }
    }

    /*
      Calculate the blending value, this is done recursively.
   
      If the numerator and denominator are 0 the expression is 0.
      If the deonimator is 0 the expression is 0
     
    */
    double SplineBlend(int k,int t,int[] u,double v)
    {
        double value;

        if (t == 1) 
        {
            if ((u[k] <= v) && (v < u[k+1]))
                value = 1;
            else
                value = 0;
        } 
        else 
        {
            if ((u[k+t-1] == u[k]) && (u[k+t] == u[k+1]))
                value = 0;
            else if (u[k+t-1] == u[k]) 
                value = (u[k+t] - v) / (u[k+t] - u[k+1]) * SplineBlend(k+1,t-1,u,v);
            else if (u[k+t] == u[k+1])
                value = (v - u[k]) / (u[k+t-1] - u[k]) * SplineBlend(k,t-1,u,v);
            else
                value = (v - u[k]) / (u[k+t-1] - u[k]) * SplineBlend(k,t-1,u,v) + 
                  (u[k+t] - v) / (u[k+t] - u[k+1]) * SplineBlend(k+1,t-1,u,v);
         }
         return(value);
       }
 
 
     //////////////////////////////////////
     //
     //
     //
     //
     int Ct( int r , int c,int numcol ) 
     {
                     return r*numcol + c ;      
     } 
     /////////////////////////////////////////////////////////
     //
     //
     //
     //
     public BSpline(Appearance ap ) 
     {
         int i,j,ki,kj;
         double intervalI,incrementI;
         double intervalJ,incrementJ;
         double bi,bj;


         inp = new Point3f[(NI+1)*(NJ+1)];
         int in = 0;
         ////////////////////////////////////////////
         //
         //  Compute the input point 
         //
         //
         for (i=0;i<=NI;i++) 
         {
             for ( j=0;j<=NJ;j++) 
             {
                 inp[Ct(i,j,NJ+1)] = new Point3f((float)i/10,
                                                 (float)j/10,
                                                 (float) Math.random() );
            
             }
         }


         incrementI = (NI - TI + 2) / 
                      ((double)RESOLUTIONI - 1);

         incrementJ = (NJ - TJ + 2) / 
                      ((double)RESOLUTIONJ - 1);


         /* Calculate the knots */

         SplineKnots(KnotsI,NI,TI);
         SplineKnots(KnotsJ,NJ,TJ);

         
         outp = new Point3f[ RESOLUTIONI * RESOLUTIONJ ];
         for(i=0; i<  RESOLUTIONI * RESOLUTIONJ ; ++ i ) {
             outp[i] = new Point3f(0.0f,0.0f,0.0f);
         }

         intervalI = 0;
         for (i=0;i<RESOLUTIONI-1;i++) 
         {
             intervalJ = 0;
             for (j=0;j<RESOLUTIONJ-1;j++) 
             {
                 outp[Ct(i,j,RESOLUTIONJ)].x= 0;
                 outp[Ct(i,j,RESOLUTIONJ)].y= 0;
                 outp[Ct(i,j,RESOLUTIONJ)].z =0;
                 for (ki=0;ki<=NI;ki++) 
                 {
                    for (kj=0;kj<=NJ;kj++) 
                    {
                        bi = SplineBlend(ki,TI,KnotsI,intervalI);
                        bj = SplineBlend(kj,TJ,KnotsJ,intervalJ);
                        outp[Ct(i,j,RESOLUTIONJ)].x += (inp[Ct(ki,kj,NJ+1)].x * bi * bj);
                        outp[Ct(i,j,RESOLUTIONJ)].y += (inp[Ct(ki,kj,NJ+1)].y * bi * bj);
                        outp[Ct(i,j,RESOLUTIONJ)].z += (inp[Ct(ki,kj,NJ+1)].z * bi * bj);
                    }
                 }
                 intervalJ += incrementJ;
              }
              intervalI += incrementI;
          }
   
 
          intervalI = 0;
          for (i=0;i<RESOLUTIONI-1;i++) 
          {
              outp[Ct(i,RESOLUTIONJ-1,RESOLUTIONJ)].x = 0;
              outp[Ct(i,RESOLUTIONJ-1,RESOLUTIONJ)].y = 0;
              outp[Ct(i,RESOLUTIONJ-1,RESOLUTIONJ)].z = 0;

              for (ki=0;ki<=NI;ki++) 
              {
                  bi = SplineBlend(ki,TI,KnotsI,intervalI);
                  outp[Ct(i,RESOLUTIONJ-1,RESOLUTIONJ)].x += (inp[Ct(ki,NJ,NJ+1)].x * bi);
                  outp[Ct(i,RESOLUTIONJ-1,RESOLUTIONJ)].y += (inp[Ct(ki,NJ,NJ+1)].y * bi);
                  outp[Ct(i,RESOLUTIONJ-1,RESOLUTIONJ)].z += (inp[Ct(ki,NJ,NJ+1)].z * bi);
              }
              intervalI += incrementI;
          }


          outp[Ct(i,RESOLUTIONJ-1,RESOLUTIONJ)].x = inp[Ct(NI,NJ,NJ+1)].x;
          outp[Ct(i,RESOLUTIONJ-1,RESOLUTIONJ)].y = inp[Ct(NI,NJ,NJ+1)].y; 
          outp[Ct(i,RESOLUTIONJ-1,RESOLUTIONJ)].z = inp[Ct(NI,NJ,NJ+1)].z;
          intervalJ = 0;

          for (j=0;j<RESOLUTIONJ-1;j++) {
              outp[Ct(RESOLUTIONI-1,j,RESOLUTIONJ)].x = 0;
              outp[Ct(RESOLUTIONI-1,j,RESOLUTIONJ)].y = 0;
              outp[Ct(RESOLUTIONI-1,j,RESOLUTIONJ)].z = 0;

              for (kj=0;kj<=NJ;kj++) 
              {
                 bj = SplineBlend(kj,TJ,KnotsJ,intervalJ);
                 outp[Ct(RESOLUTIONI-1,j,RESOLUTIONJ)].x += (inp[Ct(NI,kj,NJ+1)].x * bj);
                 outp[Ct(RESOLUTIONI-1,j,RESOLUTIONJ)].y += (inp[Ct(NI,kj,NJ+1)].y * bj);
                 outp[Ct(RESOLUTIONI-1,j,RESOLUTIONJ)].z += (inp[Ct(NI,kj,NJ+1)].z * bj);
             }
             intervalJ += incrementJ;
         }

         outp[Ct(RESOLUTIONI-1,j,RESOLUTIONJ)].x = inp[Ct(NI,NJ,NJ+1)].x;
         outp[Ct(RESOLUTIONI-1,j,RESOLUTIONJ)].y = inp[Ct(NI,NJ,NJ+1)].y; 
         outp[Ct(RESOLUTIONI-1,j,RESOLUTIONJ)].z = inp[Ct(NI,NJ,NJ+1)].z;

         QuadArray qa = new QuadArray( (RESOLUTIONJ-1)*(RESOLUTIONI-1)*4,
                                         GeometryArray.COORDINATES );
           int Hes = 0; 
           for (i=0;i<RESOLUTIONI-1;i++) 
           {
               for(j=0;j<RESOLUTIONJ-1;j++) {
                   qa.setCoordinate(Hes++,
                       new  Point3f(outp[Ct(i,j,RESOLUTIONJ)].x, 
                                    outp[Ct(i,j,RESOLUTIONJ)].y,    
                                    outp[Ct(i,j,RESOLUTIONJ)].z));
                  
                   qa.setCoordinate(Hes++,
                        new  Point3f(outp[Ct(i,j+1,RESOLUTIONJ)].x, 
                                     outp[Ct(i,j+1,RESOLUTIONJ)].y,    
                                     outp[Ct(i,j+1,RESOLUTIONJ)].z));
                   
                   qa.setCoordinate(Hes++,
                       new  Point3f(outp[Ct(i+1,j+1,RESOLUTIONJ)].x, 
                                    outp[Ct(i+1,j+1,RESOLUTIONJ)].y,    
                                    outp[Ct(i+1,j+1,RESOLUTIONJ)].z));
                  
                   qa.setCoordinate(Hes++,
                       new  Point3f(outp[Ct(i+1,j,RESOLUTIONJ)].x, 
                                    outp[Ct(i+1,j,RESOLUTIONJ)].y,    
                                    outp[Ct(i+1,j,RESOLUTIONJ)].z));
                   

                }
            }

        this.setGeometry(qa);
        this.setAppearance(ap);     
    }
}

// EOF Bspline.java