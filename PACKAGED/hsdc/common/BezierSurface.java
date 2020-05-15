/**
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

//////////////////////////////////////////////////////////
//
//
//
//  Adapted from Paul Bourke Site 
//
//
//
//

public class BezierSurface extends Shape3D
{
       private static int NI = 4;
       private static int NJ = 5;

       private static int  RESOLUTIONI = 10*NI;
       private static int RESOLUTIONJ = 10*NJ;

       Point3f[] inp = null; 
       Point3f[] outp = null; 
       ///////////////////////////////////////
       //
       //  Compute the Blend Value 
       //
       //
 
       double BezierBlend(int k ,double mu,int n)
       {
              int nn,kn,nkn;
              double blend=1;

              nn = n;
              kn = k;
              nkn = n - k;

              while (nn >= 1) {
                   blend *= nn;
                   nn--;
                   if (kn > 1) {
                       blend /= (double)kn;
                       kn--;
                   }
                   if (nkn > 1) {
                        blend /= (double)nkn;
                        nkn--;
                   }
               }
               if (k > 0)
                   blend *= Math.pow(mu,(double)k);
               if (n-k > 0)
                   blend *= Math.pow(1-mu,(double)(n-k));

                  return(blend);
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
        public BezierSurface(Appearance ap ) 
        {
  
            inp = new Point3f[(NI+1)*(NJ+1)];
            int in = 0;
            ////////////////////////////////////////////
            //
            //  Compute the input point 
            //
            //
 
            for (int i=0;i<=NI;i++) 
            {
               for (int j=0;j<=NJ;j++) 
               {
                  inp[Ct(i,j,NJ+1)] = new Point3f((float)i/10,(float)j/10,(float) Math.random() );
            
               }
            }
           


          outp = new Point3f[ RESOLUTIONI * RESOLUTIONJ ];

          for(int i=0; i<  RESOLUTIONI * RESOLUTIONJ ; ++ i ) {
                outp[i] = new Point3f(0.0f,0.0f,0.0f);
          }


          for (int i=0;i<RESOLUTIONI;i++) 
          {
                double  mui = (i / (double)(RESOLUTIONI-1));
                 for (int j=0;j<RESOLUTIONJ;j++) 
                 {
                  double    muj =( j / (double)(RESOLUTIONJ-1));
                    

                     for (int ki=0;ki<=NI;ki++) 
                     {
                       double bi = BezierBlend(ki,mui,NI);
                        for (int kj=0;kj<=NJ;kj++) 
                        {
                            double  bj = BezierBlend(kj,muj,NJ);
                            outp[Ct(i,j,RESOLUTIONJ)].x += (inp[Ct(ki,kj,NJ+1)].x * bi * bj);
                            outp[Ct(i,j,RESOLUTIONJ)].y += (inp[Ct(ki,kj,NJ+1)].y * bi * bj);
                            outp[Ct(i,j,RESOLUTIONJ)].z += (inp[Ct(ki,kj,NJ+1)].z * bi * bj);
                         }
                      }
                 }
           }


           QuadArray qa = new QuadArray( (RESOLUTIONJ-1)*(RESOLUTIONI-1)*4,
                                         GeometryArray.COORDINATES );
           int Hes = 0; 
  
            for (int i=0;i<RESOLUTIONI-1;i++) {
               for (int j=0;j<RESOLUTIONJ-1;j++) {

                   qa.setCoordinate(Hes++,new  Point3f( (float)  outp[Ct(i,j,RESOLUTIONJ)].x, 
                                                               outp[Ct(i,j,RESOLUTIONJ)].y,    
                                                               outp[Ct(i,j,RESOLUTIONJ)].z));
                  
                   qa.setCoordinate(Hes++,new  Point3f( (float)  outp[Ct(i,j+1,RESOLUTIONJ)].x, 
                                                               outp[Ct(i,j+1,RESOLUTIONJ)].y,    
                                                               outp[Ct(i,j+1,RESOLUTIONJ)].z));
                   
                   qa.setCoordinate(Hes++,new  Point3f( (float)  outp[Ct(i+1,j+1,RESOLUTIONJ)].x, 
                                                               outp[Ct(i+1,j+1,RESOLUTIONJ)].y,    
                                                               outp[Ct(i+1,j+1,RESOLUTIONJ)].z));
                  
                   qa.setCoordinate(Hes++,new  Point3f( (float)  outp[Ct(i+1,j,RESOLUTIONJ)].x, 
                                                               outp[Ct(i+1,j,RESOLUTIONJ)].y,    
                                                               outp[Ct(i+1,j,RESOLUTIONJ)].z));
                   

                }
            }

             this.setGeometry(qa);
 





            this.setAppearance(ap);     



 
  



       }





}

