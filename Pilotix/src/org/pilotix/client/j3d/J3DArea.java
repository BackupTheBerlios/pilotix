/*
 Pilotix : a multiplayer piloting game.
 Copyright (C) 2003 Pilotix.Org

 This program is free software; you can redistribute it and/or
 modify it under the terms of the GNU General Public License
 as published by the Free Software Foundation; either version 2
 of the License, or (at your option) any later version.

 This program is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 GNU General Public License for more details.

 You should have received a copy of the GNU General Public License
 along with this program; if not, write to the Free Software
 Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA 02111-1307, USA.
 */

package org.pilotix.client.j3d;

import javax.media.j3d.Appearance;
import javax.media.j3d.ColoringAttributes;
import javax.media.j3d.GeometryArray;
import javax.media.j3d.ImageComponent2D;
import javax.media.j3d.LineArray;
import javax.media.j3d.QuadArray;
import javax.media.j3d.Shape3D;
import javax.media.j3d.Texture;
import javax.media.j3d.Texture2D;
import javax.media.j3d.Transform3D;
import javax.media.j3d.TransformGroup;
import javax.vecmath.Color3f;
import javax.vecmath.Point3f;
import javax.vecmath.TexCoord2f;
import javax.vecmath.Vector3f;

import org.pilotix.client.Environment;
import org.pilotix.common.ResourceLocator;

import com.sun.j3d.utils.image.TextureLoader;

/**
 * <p>
 * Cette classe regroupe les objets Java3D qui constituent visuellement l'aire
 * de jeu.
 * </p>
 */
public class J3DArea extends J3DObject {

    /**
     * affiche l'aire de jeu.
     */
    public J3DArea(float aXMax, float aYMax) {
        // Le sol
        Shape3D areaGroundShape3D = new Shape3D();

        // La geometrie du sol
        QuadArray quadArray = new QuadArray(4, GeometryArray.COORDINATES
                | GeometryArray.TEXTURE_COORDINATE_2
                | GeometryArray.COLOR_3);
        Point3f tmpPoint3f = new Point3f();
        tmpPoint3f.set(0.0f, 0.0f, -1.0f);
        quadArray.setCoordinate(0, tmpPoint3f);
        tmpPoint3f.set(aXMax, 0.0f, -1.0f);
        quadArray.setCoordinate(1, tmpPoint3f);
        tmpPoint3f.set(aXMax, aYMax, -1.0f);
        quadArray.setCoordinate(2, tmpPoint3f);
        tmpPoint3f.set(0.0f, aYMax, -1.0f);
        quadArray.setCoordinate(3, tmpPoint3f);

        int nbPoints = 8;
        float decalage = 15.0f;
        TexCoord2f tmpTexCoord2f = new TexCoord2f();
        tmpTexCoord2f.set(0.0f, 0.0f);
        quadArray.setTextureCoordinate(0, 0, tmpTexCoord2f);
        tmpTexCoord2f.set(nbPoints + 1, 0.0f);
        quadArray.setTextureCoordinate(0, 1, tmpTexCoord2f);
        tmpTexCoord2f.set(nbPoints + 1, (nbPoints + 1) * aYMax / aXMax);
        quadArray.setTextureCoordinate(0, 2, tmpTexCoord2f);
        tmpTexCoord2f.set(0.0f, (nbPoints + 1) * aYMax / aXMax);
        quadArray.setTextureCoordinate(0, 3, tmpTexCoord2f);
        areaGroundShape3D.setGeometry(quadArray);

        quadArray.setColor(0, new Color3f(0.8f,0.67f,0.47f));
        quadArray.setColor(1, new Color3f(0.8f,0.67f,0.47f));
        quadArray.setColor(2, new Color3f(0.8f,0.67f,0.47f));
        quadArray.setColor(3, new Color3f(0.8f,0.67f,0.47f));

        // La texture du sol
        //java.net.URL url =
        // this.getClass().getClassLoader().getResource("textures/grnd.jpg");
        java.net.URL url = Environment.theRL.getResource(
                ResourceLocator.TEXTURE, "grnd.jpg");
        //javax.swing.JOptionPane.showMessageDialog(Environment.theGUI,"url="+url);
        TextureLoader loader = new TextureLoader(url, Environment.theGUI);
        ImageComponent2D image = loader.getImage();
        Texture2D texture = new Texture2D(Texture.BASE_LEVEL,
                                          Texture.RGB,
                                          image.getWidth(),
                                          image.getHeight());
        texture.setBoundaryModeS(Texture.WRAP);
        texture.setBoundaryModeT(Texture.WRAP);
        texture.setImage(0, image);

        Appearance groundAppearance = new Appearance();
//        groundAppearance.setTexture(texture);
        areaGroundShape3D.setAppearance(groundAppearance);

        this.addChild(areaGroundShape3D);

        // Le bord du terrain
        Shape3D borderShape3D = new Shape3D();
        LineArray borderLineArray = new LineArray(8, LineArray.COORDINATES);

        borderLineArray.setCoordinate(0, new Point3f(0.0f, 0.0f, 0.0f));
        borderLineArray.setCoordinate(1, new Point3f(0.0f, aYMax, 0.0f));

        borderLineArray.setCoordinate(2, new Point3f(0.0f, aYMax, 0.0f));
        borderLineArray.setCoordinate(3, new Point3f(aXMax, aYMax, 0.0f));

        borderLineArray.setCoordinate(4, new Point3f(aXMax, aYMax, 0.0f));
        borderLineArray.setCoordinate(5, new Point3f(aXMax, 0.0f, 0.0f));

        borderLineArray.setCoordinate(6, new Point3f(aXMax, 0.0f, 0.0f));
        borderLineArray.setCoordinate(7, new Point3f(0.0f, 0.0f, 0.0f));

        borderShape3D.setGeometry(borderLineArray);

        Appearance borderAppearance = new Appearance();
        ColoringAttributes borderColoringAttributes = new ColoringAttributes(
                new Color3f(1.0f, 1.0f, 0.0f), ColoringAttributes.FASTEST);
        borderAppearance.setColoringAttributes(borderColoringAttributes);
        borderShape3D.setAppearance(borderAppearance);
        this.addChild(borderShape3D);

        // Un ensemble de points regulierement espaces
        Shape3D pointsShape3D = new Shape3D();
        float incrementX = aXMax / (nbPoints + 1);
        float incrementY = incrementX * aYMax / aXMax;
        //PointArray aPointArray = new
        // PointArray(nbPoints*nbPoints,PointArray.COORDINATES);
        int k = 0;
        for (float i = 1; i <= nbPoints; i++) {
            for (float j = 1; j <= nbPoints; j++) {
                //aPointArray.setCoordinate(k,new Point3f( i*incrementX,
                // j*incrementY, 0.0f));

                TransformGroup tgSphere = new TransformGroup();
                Transform3D t3dSphere = new Transform3D();
                t3dSphere.setTranslation( new Vector3f(i * incrementX,
                                                       j * incrementY,
                                                       0.0f));
                tgSphere.setTransform(t3dSphere);
                com.sun.j3d.utils.geometry.Sphere aSphere =
                        new com.sun.j3d.utils.geometry.Sphere(5.0f);
                tgSphere.addChild(aSphere);
                this.addChild(tgSphere);

                k++;
            }
        }
        /*
         * pointsShape3D.setGeometry(aPointArray);
         *
         * Appearance pointsAppearance = new Appearance(); ColoringAttributes
         * pointsColoringAttributes = new ColoringAttributes( new Color3f(1.0f,
         * 1.0f, 1.0f), ColoringAttributes.FASTEST);
         * pointsAppearance.setColoringAttributes(pointsColoringAttributes);
         *
         * pointsShape3D.setAppearance(pointsAppearance);
         * this.addChild(pointsShape3D);
         */

        // Une lumiere d'ambiance
        /*
        javax.media.j3d.BoundingSphere areaLightBounds = new javax.media.j3d.BoundingSphere(
                new Point3d(aXMax / 2, aYMax / 2, 0.0f), java.lang.Math.max(
                        aXMax, aYMax));
        javax.media.j3d.AmbientLight areaLight = new AmbientLight(new Color3f(
                1.0f, 1.0f, 1.0f));
        areaLight.setInfluencingBounds(areaLightBounds);
        this.addChild(areaLight);
*/
        this.compile();
    }

}
