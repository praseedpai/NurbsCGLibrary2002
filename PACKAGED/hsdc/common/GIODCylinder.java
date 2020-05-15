package hsdc.common;

import javax.media.j3d.*;
import javax.vecmath.*;
import java.lang.Math.*;
import java.awt.*;

// Cylinder without caps; robinsm@sunyit.edu

public class GIODCylinder extends Shape3D
{
    //private Shape3D scylinder;
    private float[] qverts;
    private float[] qtex;
    private float x, y, z, theta, t, num,
        numcirc, calct, xn, yn, zn, mag;
    private int vertCount = 0;
    private int normalcount = 0;
    private Vector3f[] normals;

    public  GIODCylinder(){
        this(30, 1.0f, 2.0f, 0,0,0,null);
    }
    /**
    * Constructs a Cylinder with 'n' quads (faces), 'w' width (diameter),
    * 'h' height, centered at 'xpos', 'ypos', 'zpos',
    * and appearance 'cylinderAppearance'
    **/
    public  GIODCylinder(int n, float w, float h, float xpos,
        float ypos, float zpos, Appearance cylinderAppearance)
    {
        qverts = new float[12*(n+1)];
        qtex = new float[12*(n+1)];

        t = ((float) (2*Math.PI*(w/2))/n);
        theta = t/(w/2);

        numcirc = n;

        // Generate the walls only.. this is a cylinder without "ends"
        for (int i=0; i <= numcirc; i++)
        {
            //bottom points
            x = (float) ((w/2)*Math.cos(theta*i));
            z = (float) ((w/2)*Math.sin(theta*i));
            qverts[vertCount]    = x;
            vertCount++;
            qverts[vertCount]    = 0-(h/2);
            vertCount++;
            qverts[vertCount]    = z;
            vertCount++;

            i++;

            x = (float) ((w/2)*Math.cos(theta*i));
            z = (float) ((w/2)*Math.sin(theta*i));
            qverts[vertCount]    = x;
            vertCount++;
            qverts[vertCount]    = 0-(h/2);
            vertCount++;
            qverts[vertCount]    = z;
            vertCount++;

            //top points
            x = (float) ((w/2)*Math.cos(theta*i));
            z = (float) ((w/2)*Math.sin(theta*i));
            qverts[vertCount]    = x;
            vertCount++;
            qverts[vertCount]    = 0+(h/2);
            vertCount++;
            qverts[vertCount]    = z;
            vertCount++;

            i--;

            x = (float) ((w/2)*Math.cos(theta*i));
            z = (float) ((w/2)*Math.sin(theta*i));
            qverts[vertCount]    = x;
            vertCount++;
            qverts[vertCount]    = 0+(h/2);
            vertCount++;
            qverts[vertCount]    = z;
            vertCount++;
        }

        //position the cylinder
        for (int j=0; j < vertCount; j = j+3)
        {
            qverts[j] = qverts[j] + xpos;
            qverts[j+1] = qverts[j+1] + ypos;
            qverts[j+2] = qverts[j+2] + zpos;
        }

        QuadArray cylinderGeometry = new QuadArray( vertCount/3,
                        QuadArray.COORDINATES | QuadArray.NORMALS);

        cylinderGeometry.setCapability( QuadArray.ALLOW_COLOR_READ );
        cylinderGeometry.setCapability( QuadArray.ALLOW_COLOR_WRITE );
        cylinderGeometry.setCapability( QuadArray.ALLOW_COORDINATE_READ );
        cylinderGeometry.setCapability( QuadArray.ALLOW_COORDINATE_WRITE );
        cylinderGeometry.setCapability( QuadArray.ALLOW_COUNT_READ );
        cylinderGeometry.setCoordinates( 0, qverts );

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
