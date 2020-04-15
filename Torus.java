package hsdc.common;

import javax. media.j3d.*;
import javax.vecmath.*;
import java.lang.Math.*;
import java.awt.*;
import java.util.*;
import java.io.*;

public class  Torus extends Shape3D
{
    private static final float INNERRADIUS = 1.0f;
    private static final float OUTERRADIUS = 3.0f;
    private static final float ARCLENGTH = 0.1f;
    private static final float XPOSITION = 0.0f;
    private static final float YPOSITION = 0.0f;
    private static final float ZPOSITION = 0.0f;

    //private Shape3D storus;
    private Vector VX = new Vector();
    private Vector VY = new Vector();
    private Vector VZ = new Vector();
    private float[] qverts;
    private float x, y, z, theta, t, previousx, previousz,
        calct, rlower, rupper, num, mag, x1, x2, x3, x4, y1, y2, y3, y4,
        z1, z2, z3, z4, xn, yn, zn, secr, yval, r;
    private int half, numcirc, upcount = 1;
    private int vertCount = 0;
    private int normalcount = 0;
    private Image itex1;
    private Vector3f[] normals;


    /**
    *   Constructs a Torus of inner radius 1, outer radius 3,
    *   arclength of a quad 0.1,
    *   at coordinates 0, 0, 0,
    *   with null Appearance
    **/
    public  Torus()
    {
        this(INNERRADIUS, OUTERRADIUS, ARCLENGTH,
            XPOSITION, YPOSITION, ZPOSITION, null);
    }

    /**
    *   Constructs a Torus of inner radius 'ir', outer radius 'or',
    *   arclength of a quad 0.1,
    *   at coordinates 0, 0, 0,
    *   with null Appearance
    **/
    public  Torus(float ir, float or)
    {
        this(ir, or, ARCLENGTH,
            XPOSITION, YPOSITION, ZPOSITION, null);
    }

    /**
    *   Constructs a Torus of inner radius 1, outer radius 3,
    *   arclength of a quad 0.1,
    *   at coordinates 0, 0, 0,
    *   with Appearance torusAppearance
    **/
    public  Torus(Appearance torusAppearance)
    {
        this(INNERRADIUS, OUTERRADIUS, ARCLENGTH,
            XPOSITION, YPOSITION, ZPOSITION, torusAppearance);
    }

    /**
    *   Constructs a Torus of inner radius 'ir', outer radius 'or',
    *   arclength of a quad 'arclength',
    *   at coordinates 0, 0, 0,
    *   with null Appearance
    **/
    public  Torus(float ir, float or, float arclength)
    {
        this(ir, or, arclength,
            XPOSITION, YPOSITION, ZPOSITION, null);
    }

    /**
    *   Constructs a Torus of inner radius 'ir', outer radius 'or',
    *   arclength of a quad 0.1,
    *   at coordinates 0, 0, 0,
    *   with Appearance torusAppearance
    **/
    public  Torus(float ir, float or, Appearance torusAppearance)
    {
        this(ir, or, ARCLENGTH,
            XPOSITION, YPOSITION, ZPOSITION, torusAppearance);
    }

    /**
    *   Constructs a Torus of inner radius 'ir', outer radius 'or',
    *   arclength of a quad 'arclength',
    *   at coordinates 0, 0, 0,
    *   with Appearance torusAppearance
    **/
    public  Torus(float ir, float or, float arclength,
        Appearance torusAppearance)
    {
        this(ir, or, arclength,
            XPOSITION, YPOSITION, ZPOSITION, torusAppearance);
    }

    /**
    *   Constructs a Torus centered at 'xpos', 'ypos', 'zpos',
    *   with inner radius 'ir', outer radius 'or',
    *   arclength of a quad 'arclength',
    *   and Appearance 'torusAppearance'
    */
    public  Torus(float ir, float or, float arclength,
        float xpos, float ypos, float zpos, Appearance torusAppearance)
    {
        super();
        if (arclength <= 0.0)
        {
            arclength = 0.5f;
        }
        t = arclength;
        r = (or - ir) / 2;
        num = ((float) (2*Math.PI*r)/t);
        numcirc = (int) num;

        // Change the arclength to the closest value that fits.
        calct = ((float) (2*Math.PI*r)/numcirc);
        t = calct;
        theta = t/r;
        half = (int) ((r*Math.PI)/t)+1;

        // In case theres too many quads...
        if ( 2*(half*(6*(2*numcirc))) > 600000)
        {
            throw new IllegalArgumentException("Too detailed! Choose a bigger" +
                " arclength or smaller radius.");
        }
        qverts = new float[2*(half*(6*(2*(numcirc+1))))];

        rlower = or;// radius of first loop
        rupper = or - (r - (r*((float) Math.cos(theta))));// radius of second loop

        // upper half
        for (int k=0; k < half; k++)
        {
            for (int i=0 ; i < numcirc+1; i++)
            {
                x1 =  rlower*((float) Math.cos(theta*i));
                qverts[vertCount] = x1;
                vertCount++;

                y1 = r*((float) Math.sin(theta*(k)));
                qverts[vertCount] = y1;
                vertCount++;

                z1 =  rlower*((float) Math.sin(theta*i));
                qverts[vertCount] = -z1;
                vertCount++;

                x2 =  rlower*((float) Math.cos(theta*(i+1)));
                qverts[vertCount] = x2;
                vertCount++;

                y2 = r*((float) Math.sin(theta*(k)));
                qverts[vertCount] = y2;
                vertCount++;

                z2 =  rlower*((float) Math.sin(theta*(i+1)));
                qverts[vertCount] = -z2;
                vertCount++;

                x3 =  rupper*((float) Math.cos(theta*(i+1)));
                qverts[vertCount] = x3;
                vertCount++;

                y3 =  r*((float) Math.sin(theta*(k+1)));
                qverts[vertCount] = y3;
                vertCount++;

                z3 =  rupper*((float) Math.sin(theta*(i+1)));
                qverts[vertCount] = -z3;
                vertCount++;

                x4 =  rupper*((float) Math.cos(theta*i));
                qverts[vertCount] = x4;
                vertCount++;

                y4 =  r*((float) Math.sin(theta*(k+1)));
                qverts[vertCount] = y4;
                vertCount++;

                z4 =  rupper*((float) Math.sin(theta*i));
                qverts[vertCount] = -z4;
                vertCount++;
            }
            rlower = rupper;
            upcount++;
            rupper = or - (r - (r*((float) Math.cos(theta*upcount))));
        }

        rlower = or;// radius of first loop
        rupper = or - (r - (r*((float) Math.cos(theta))));// radius of second loop
        upcount = 0;

        // lower half (just mirror the upper half on y axis)
        int tempVertCount = vertCount;
        for (int k=0; k < tempVertCount; k = k+3)
        {
            qverts[vertCount] = qverts[k];
            vertCount++;
            qverts[vertCount] = -(qverts[k+1]);
            vertCount++;
            qverts[vertCount] = qverts[k+2];
            vertCount++;
        }

        // position the torus corectly
        for (int j=0; j < vertCount; j = j+3)
        {
            qverts[j] = qverts[j] + xpos;
            qverts[j+1] = qverts[j+1] + ypos;
            qverts[j+2] = qverts[j+2] + zpos;
        }

        QuadArray torusGeometry = new QuadArray( vertCount/3,
            QuadArray.COORDINATES | QuadArray.NORMALS);

        torusGeometry.setCapability( QuadArray.ALLOW_COLOR_READ );
        torusGeometry.setCapability( QuadArray.ALLOW_COLOR_WRITE );
        torusGeometry.setCapability( QuadArray.ALLOW_COORDINATE_READ );
        torusGeometry.setCapability( QuadArray.ALLOW_COORDINATE_WRITE );
        torusGeometry.setCapability( QuadArray.ALLOW_COUNT_READ );
        torusGeometry.setCoordinates( 0, qverts );

        // Calculate normals of all points.
        normals = new Vector3f[vertCount/3];
        for (int w = 0; w < vertCount; w = w + 3)
        {
            Vector3f norm = new Vector3f(0.0f, 0.0f, 0.0f);
            mag = qverts[w] * qverts[w] + qverts[w+1] *
                qverts[w+1] + qverts[w+2] * qverts[w+2];
            if (mag != 0.0)
            {
                mag = 1.0f / ((float) Math.sqrt(mag));
                xn = qverts[w]*mag;
                yn = qverts[w+1]*mag;
                zn = qverts[w+2]*mag;
                norm = new Vector3f(xn, yn, zn);
            }
            normals[normalcount] = norm;
            torusGeometry.setNormal(normalcount, norm);
            normalcount++;
        }

        this.setGeometry(torusGeometry);
        this.setAppearance(torusAppearance);

        //storus = this;
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
