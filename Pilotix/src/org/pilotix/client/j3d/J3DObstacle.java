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

import org.pilotix.common.Vector;
import org.pilotix.client.Environment;
import org.pilotix.client.ResourceLocator;

import javax.media.j3d.Appearance;
import javax.media.j3d.ImageComponent2D;
import javax.media.j3d.Texture2D;
import javax.media.j3d.Texture;
import javax.media.j3d.QuadArray;
import javax.media.j3d.GeometryArray;
import javax.media.j3d.Material;
import javax.media.j3d.Shape3D;
import javax.media.j3d.TransparencyAttributes;
import javax.media.j3d.PolygonAttributes;
import javax.vecmath.TexCoord2f;
import javax.vecmath.Point3f;
import javax.vecmath.Color3f;
import com.sun.j3d.utils.image.TextureLoader;
import com.sun.j3d.utils.geometry.GeometryInfo;
import com.sun.j3d.utils.geometry.NormalGenerator;
import com.sun.j3d.utils.geometry.Stripifier;

/**
 * <p>
 * Cette classe construit un obstacle visuel à partir de ses
 * caractéristiques.
 * </p>
 */
public class J3DObstacle extends J3DObject {

    int nbFaces = 5; // On ne dessine pas la face cachee de l'obstacle

    /**
     * Construit un obstacle dont les coordonnées des coins sont fournis
     * (le système de coordonnées est celui du serveur), et recouvre
     * les faces avec les textures indiquées, dont les URLs seront trouvées
     * par ResourceLocator.
     */
    public J3DObstacle(Vector upLeftCorner,
                        Vector downRightCorner,
                        int height,
                        int altitude,
                        String topTexture,
                        String sideTexture) {

        super();

        // --------------------
        // GEOMETRIE : on réserve 4*nbFaces points
        QuadArray quadArray = new QuadArray(4*nbFaces,
                   GeometryArray.COORDINATES
                   | GeometryArray.TEXTURE_COORDINATE_2
                   | GeometryArray.NORMALS
                   | GeometryArray.COLOR_3);

        // Les points, nommés selon leur position par rapport à l'observateur
        // AuSol signifie z = 0.0f
        // EnAlt signifie en altitude, z = height*Environment.u3d
        Point3f pointBasGaucheAuSol = new Point3f(0.0f,
                                           0.0f,
                                           0.0f);
        Point3f pointBasGaucheEnAlt = new Point3f(0.0f,
                                               0.0f,
                                               height*Environment.u3d);
        Point3f pointBasDroiteAuSol = new Point3f((downRightCorner.x-upLeftCorner.x)*Environment.u3d,
                                           0.0f,
                                           0.0f);
        Point3f pointBasDroiteEnAlt = new Point3f((downRightCorner.x-upLeftCorner.x)*Environment.u3d,
                                               0.0f,
                                               height*Environment.u3d);
        Point3f pointHautDroiteAuSol = new Point3f((downRightCorner.x-upLeftCorner.x)*Environment.u3d,
                                             (upLeftCorner.y-downRightCorner.y)*Environment.u3d,
                                             0.0f);
        Point3f pointHautDroiteEnAlt = new Point3f((downRightCorner.x-upLeftCorner.x)*Environment.u3d,
                                             (upLeftCorner.y-downRightCorner.y)*Environment.u3d,
                                             height*Environment.u3d);
        Point3f pointHautGaucheAuSol = new Point3f(0.0f,
                                           (upLeftCorner.y-downRightCorner.y)*Environment.u3d,
                                           0.0f);
        Point3f pointHautGaucheEnAlt = new Point3f(0.0f,
                                           (upLeftCorner.y-downRightCorner.y)*Environment.u3d,
                                           height*Environment.u3d);

        // --------------------
        // FACE 1 : LE DESSUS DE L'OBSTACLE
        quadArray.setCoordinate(0, pointBasGaucheEnAlt);
        quadArray.setCoordinate(1, pointBasDroiteEnAlt);
        quadArray.setCoordinate(2, pointHautDroiteEnAlt);
        quadArray.setCoordinate(3, pointHautGaucheEnAlt);

        // --------------------
        // FACE 2 : FACE DU SUD
        quadArray.setCoordinate(4, pointBasGaucheAuSol);
        quadArray.setCoordinate(5, pointBasDroiteAuSol);
        quadArray.setCoordinate(6, pointBasDroiteEnAlt);
        quadArray.setCoordinate(7, pointBasGaucheEnAlt);

        // --------------------
        // FACE 3 : FACE DE L'EST
        quadArray.setCoordinate(8, pointBasDroiteAuSol);
        quadArray.setCoordinate(9, pointHautDroiteAuSol);
        quadArray.setCoordinate(10, pointHautDroiteEnAlt);
        quadArray.setCoordinate(11, pointBasDroiteEnAlt);

        // --------------------
        // FACE 4 : FACE DU NORD
        quadArray.setCoordinate(12, pointHautDroiteAuSol);
        quadArray.setCoordinate(13, pointHautGaucheAuSol);
        quadArray.setCoordinate(14, pointHautGaucheEnAlt);
        quadArray.setCoordinate(15, pointHautDroiteEnAlt);

        // --------------------
        // FACE 5 : FACE DE L'OUEST
        quadArray.setCoordinate(16, pointHautGaucheAuSol);
        quadArray.setCoordinate(17, pointBasGaucheAuSol);
        quadArray.setCoordinate(18, pointBasGaucheEnAlt);
        quadArray.setCoordinate(19, pointHautGaucheEnAlt);

        // --------------------
        // TEXTURES : sommet de l'obstacle
        TextureLoader loader = new TextureLoader(
               Environment.theRL.getResource(ResourceLocator.TEXTURE, topTexture),
               Environment.theGUI);
        ImageComponent2D topImage = loader.getImage();
        Texture2D texture2D = new Texture2D(Texture.BASE_LEVEL,
                                            Texture.RGB,
                                            topImage.getWidth(),
                                            topImage.getHeight());
        texture2D.setBoundaryModeS(Texture.WRAP);
        texture2D.setBoundaryModeT(Texture.WRAP);
        texture2D.setImage(0, topImage);

        // Définition des points d'accrochage de la texture
        TexCoord2f tmpTexCoord2f = new TexCoord2f();
        float x_sur_y = ((downRightCorner.x - upLeftCorner.x)*Environment.u3d)
                        / ((downRightCorner.y - upLeftCorner.y)*Environment.u3d);
        int nbRepetitions = 1;

        for (int i=0; i < nbFaces; i++) {
            tmpTexCoord2f.set(0.0f, nbRepetitions*x_sur_y);
            quadArray.setTextureCoordinate(0, i*4, tmpTexCoord2f);
            tmpTexCoord2f.set(0.0f, 0.0f);
            quadArray.setTextureCoordinate(0, i*4+1, tmpTexCoord2f);
            tmpTexCoord2f.set(nbRepetitions*1.0f, 0.0f);
            quadArray.setTextureCoordinate(0, i*4+2, tmpTexCoord2f);
            tmpTexCoord2f.set(nbRepetitions*1.0f, nbRepetitions*x_sur_y);
            quadArray.setTextureCoordinate(0, i*4+3, tmpTexCoord2f);
            quadArray.setColor(i*4, new Color3f(1.0f,0.0f,0.0f));
            quadArray.setColor(i*4+1, new Color3f(0.0f,1.0f,0.0f));
            quadArray.setColor(i*4+2, new Color3f(0.0f,0.0f,1.0f));
            quadArray.setColor(i*4+3, new Color3f(1.0f,1.0f,0.0f));
        }

        // --------------------
        // APPARENCE
        // Création de l'apparence et application des textures
        Appearance obstacleAppearance = new Appearance();
//        obstacleAppearance.setTexture(texture2D);

        // Material
        Material material = new Material();
        material.setColorTarget(Material.EMISSIVE);
        obstacleAppearance.setMaterial(material);

        // Transparence
        TransparencyAttributes transparency = new TransparencyAttributes();
        transparency.setTransparencyMode(TransparencyAttributes.NONE);
        transparency.setTransparency(0.0f);
        obstacleAppearance.setTransparencyAttributes(transparency);

        // Création du Shape3D
        Shape3D obstacleShape3D = new Shape3D();
        obstacleShape3D.setAppearance(obstacleAppearance);

        // Calcul des normales
        GeometryInfo geometryInfo = new GeometryInfo(quadArray);
//        geometryInfo.convertToIndexedTriangles();
        NormalGenerator normalGenerator = new NormalGenerator(0.01f);
        normalGenerator.generateNormals(geometryInfo);
//        Stripifier stripifier = new Stripifier();
//        stripifier.stripify(geometryInfo);
        obstacleShape3D.setGeometry(geometryInfo.getGeometryArray());

        // --------------------
        // POSITIONNEMENT DE L'OBJET DANS LA SCENE
        rotationTG.addChild(obstacleShape3D);
        this.setPosition(new Vector(upLeftCorner.x,
                                     downRightCorner.y));
        this.setAltitude(altitude);

        this.compile();
    }
}
