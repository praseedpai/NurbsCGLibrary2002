package hsdc.common;

import javax.media.j3d.*;
import javax.vecmath.*;
import java.lang.Math.*;
import java.awt.*;

public class GIODThickCylinder extends Shape3D
{
    //private Shape3D scylinder;
    private float[] qverts;
    private float[] qtex;
    private float x, y, z, thetao, thetai, to, ti, num,
        numcirc, xn, yn, zn, mag;
    private int vertCount = 0;
    private int normalcount = 0;
    private Vector3f[] normals;

    public  GIODThickCylinder(){
        this(30, 1.2f, 1.0f, 2.0f, 0.f, 0.f, 0.f, null);
    }
    /**
    * Constructs a Cylinder with 'n' quads (faces), 'wo' width (diameter)
    * of outer surface, 'wi' width (diameter) of inner surface,
    * 'h' height, centered at 'xpos', 'ypos', 'zpos',
    * and appearance 'cylinderAppearance'
    **/
    public  GIODThickCylinder(int n, float wo, float wi, float h, float xpos,
        float ypos, float zpos, Appearance cylinderAppearance)
    {
        qverts = new float[48*n];
        qtex = new float[48*n];

        to = ((float) (2*Math.PI*(wo/2))/n);
        thetao = to/(wo/2);

        ti = ((float) (2*Math.PI*(wi/2))/n);
        thetai = ti/(wi/2);

        numcirc = n;

        // Generate the outer walls...
        for (int i=0; i < numcirc; i++)
        {
            //bottom points
            x = (float) ((wo/2)*Math.cos(thetao*i));
            z = (float) ((wo/2)*Math.sin(thetao*i));
            qverts[vertCount]    = x;
            vertCount++;
            qverts[vertCount]    = 0-(h/2);
            vertCount++;
            qverts[vertCount]    = z;
            vertCount++;

            i++;

            x = (float) ((wo/2)*Math.cos(thetao*i));
            z = (float) ((wo/2)*Math.sin(thetao*i));
            qverts[vertCount]    = x;
            vertCount++;
            qverts[vertCount]    = 0-(h/2);
            vertCount++;
            qverts[vertCount]    = z;
            vertCount++;

            //top points
            x = (float) ((wo/2)*Math.cos(thetao*i));
            z = (float) ((wo/2)*Math.sin(thetao*i));
            qverts[vertCount]    = x;
            vertCount++;
            qverts[vertCount]    = 0+(h/2);
            vertCount++;
            qverts[vertCount]    = z;
            vertCount++;

            i--;

            x = (float) ((wo/2)*Math.cos(thetao*i));
            z = (float) ((wo/2)*Math.sin(thetao*i));
            qverts[vertCount]    = x;
            vertCount++;
            qverts[vertCount]    = 0+(h/2);
            vertCount++;
            qverts[vertCount]    = z;
            vertCount++;
        }

        // Generate the inner walls...
        for (int i=0; i < numcirc; i++)
        {
            //bottom points
            x = (float) ((wi/2)*Math.cos(thetai*i));
            z = (float) ((wi/2)*Math.sin(thetai*i));
            qverts[vertCount]    = x;
            vertCount++;
            qverts[vertCount]    = 0-(h/2);
            vertCount++;
            qverts[vertCount]    = z;
            vertCount++;

            i++;

            x = (float) ((wi/2)*Math.cos(thetai*i));
            z = (float) ((wi/2)*Math.sin(thetai*i));
            qverts[vertCount]    = x;
            vertCount++;
            qverts[vertCount]    = 0-(h/2);
            vertCount++;
            qverts[vertCount]    = z;
            vertCount++;

            //top points
            x = (float) ((wi/2)*Math.cos(thetai*i));
            z = (float) ((wi/2)*Math.sin(thetai*i));
            qverts[vertCount]    = x;
            vertCount++;
            qverts[vertCount]    = 0+(h/2);
            vertCount++;
            qverts[vertCount]    = z;
            vertCount++;

            i--;

            x = (float) ((wi/2)*Math.cos(thetai*i));
            z = (float) ((wi/2)*Math.sin(thetai*i));
            qverts[vertCount]    = x;
            vertCount++;
            qverts[vertCount]    = 0+(h/2);
            vertCount++;
            qverts[vertCount]    = z;
            vertCount++;
        }

        // Generate the top thickness cap
        for (int i=0; i < numcirc; i++)
        {
            //outer points
            x = (float) ((wo/2)*Math.cos(thetai*i));
            z = (float) ((wo/2)*Math.sin(thetai*i));
            qverts[vertCount]    = x;
            vertCount++;
            qverts[vertCount]    = 0+(h/2);
            vertCount++;
            qverts[vertCount]    = z;
            vertCount++;

            i++;

            x = (float) ((wo/2)*Math.cos(thetai*i));
            z = (float) ((wo/2)*Math.sin(thetai*i));
            qverts[vertCount]    = x;
            vertCount++;
            qverts[vertCount]    = 0+(h/2);
            vertCount++;
            qverts[vertCount]    = z;
            vertCount++;

            //inner points
            x = (float) ((wi/2)*Math.cos(thetai*i));
            z = (float) ((wi/2)*Math.sin(thetai*i));
            qverts[vertCount]    = x;
            vertCount++;
            qverts[vertCount]    = 0+(h/2);
            vertCount++;
            qverts[vertCount]    = z;
            vertCount++;

            i--;

            x = (float) ((wi/2)*Math.cos(thetai*i));
            z = (float) ((wi/2)*Math.sin(thetai*i));
            qverts[vertCount]    = x;
            vertCount++;
            qverts[vertCount]    = 0+(h/2);
            vertCount++;
            qverts[vertCount]    = z;
            vertCount++;
        }


        // Generate the bottom thickness cap
        for (int i=0; i < numcirc; i++)
        {
            //outer points
            x = (float) ((wo/2)*Math.cos(thetai*i));
            z = (float) ((wo/2)*Math.sin(thetai*i));
            qverts[vertCount]    = x;
            vertCount++;
            qverts[vertCount]    = 0-(h/2);
            vertCount++;
            qverts[vertCount]    = z;
            vertCount++;

            i++;

            x = (float) ((wo/2)*Math.cos(thetai*i));
            z = (float) ((wo/2)*Math.sin(thetai*i));
            qverts[vertCount]    = x;
            vertCount++;
            qverts[vertCount]    = 0-(h/2);
            vertCount++;
            qverts[vertCount]    = z;
            vertCount++;

            //inner points
            x = (float) ((wi/2)*Math.cos(thetai*i));
            z = (float) ((wi/2)*Math.sin(thetai*i));
            qverts[vertCount]    = x;
            vertCount++;
            qverts[vertCount]    = 0-(h/2);
            vertCount++;
            qverts[vertCount]    = z;
            vertCount++;

            i--;

            x = (float) ((wi/2)*Math.cos(thetai*i));
            z = (float) ((wi/2)*Math.sin(thetai*i));
            qverts[vertCount]    = x;
            vertCount++;
            qverts[vertCount]    = 0-(h/2);
            vertCount++;
            qverts[vertCount]    = z;
            vertCount++;
        }

        QuadArray cylinderGeometry = new QuadArray( vertCount/3,
                        QuadArray.COORDINATES | QuadArray.NORMALS);

        // Calculate normals of all points.
        normals = new Vector3f[vertCount/3];
        for (int s = 0; s < vertCount; s = s + 3)
        {
            Vector3f norm = new Vector3f(0.0f, 0.0f, 0.0f);
            mag = qverts[s] * qverts[s] + qverts[s+1] *
                    qverts[s+1] + qverts[s+2] * qverts[s+2];
            if (mag != 0.0)
            {
                mag = 1.0f / ((float) Math.sqrt(mag));
                xn = qverts[s]*mag;
                yn = qverts[s+1]*mag;
                zn = qverts[s+2]*mag;
                norm = new Vector3f(xn, yn, zn);
            }
            normals[normalcount] = norm;
            cylinderGeometry.setNormal(normalcount, norm);
            normalcount++;
        }

        //position the cylinder
        for (int j=0; j < vertCount; j = j+3)
        {
            qverts[j] = qverts[j] + xpos;
            qverts[j+1] = qverts[j+1] + ypos;
            qverts[j+2] = qverts[j+2] + zpos;
        }

        cylinderGeometry.setCapability( QuadArray.ALLOW_COLOR_READ );
        cylinderGeometry.setCapability( QuadArray.ALLOW_COLOR_WRITE );
        cylinderGeometry.setCapability( QuadArray.ALLOW_COORDINATE_READ );
        cylinderGeometry.setCapability( QuadArray.ALLOW_COORDINATE_WRITE );
        cylinderGeometry.setCapability( QuadArray.ALLOW_COUNT_READ );
        cylinderGeometry.setCoordinates( 0, qverts );

        if (cylinderAppearance != null){
            PolygonAttributes pa = new PolygonAttributes();
            pa.setCapability(PolygonAttributes.ALLOW_CULL_FACE_READ);
            pa.setCapability(PolygonAttributes.ALLOW_CULL_FACE_WRITE);
            pa.setCapability(PolygonAttributes.ALLOW_MODE_READ);
            pa.setCapability(PolygonAttributes.ALLOW_MODE_WRITE);
            pa.setCapability(PolygonAttributes.ALLOW_NORMAL_FLIP_READ);
            pa.setCapability(PolygonAttributes.ALLOW_NORMAL_FLIP_WRITE);
            pa.setCapability(PolygonAttributes.ALLOW_OFFSET_READ);
            pa.setCapability(PolygonAttributes.ALLOW_OFFSET_WRITE);
            pa.setCullFace(PolygonAttributes.CULL_NONE);
            //pa.setBackFaceNormalFlip(true);
            cylinderAppearance.setPolygonAttributes(pa);
            for (int i = 0; i<=20;i++){
                cylinderAppearance.setCapability(i);
            }
        }

        this.setGeometry(cylinderGeometry);
        this.setAppearance(cylinderAppearance);
        //scylinder = new Shape3D(cylinderGeometry, cylinderAppearance);
    }

    // Scaling works because QuadArray.ALLOW_COORDINATE_WRITE was set in
    // constructor.
    public void setScale(float xs, float ys, float zs)
    {
        QuadArray qa = (QuadArray) this.getGeometry();
        for (int i=0; i < qa.getVertexCount(); i++)
        {
            float[] q = new float[3];
            qa.getCoordinate(i, q);
            q[0] = xs * q[0];
            q[1] = ys * q[1];
            q[2] = ys * q[2];

            qa.setCoordinate(i, q);
        }
        this.setGeometry(qa);
    }

    public void setScale(float[] p_scale){
        if (p_scale.length != 3)
            throw new java.lang.IllegalArgumentException("Arraylength must be 3");
        scale = p_scale;
        this.setScale(scale[0], scale[1], scale[2]);
    }

    public float[] getScale(){
        return scale;
    }

    private float[] scale = new float[] {1.0f,1.0f,1.0f};
}
