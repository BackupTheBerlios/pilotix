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

import javax.media.j3d.BranchGroup;
import javax.media.j3d.Shape3D;
import javax.media.j3d.Transform3D;
import javax.media.j3d.TransformGroup;
import javax.vecmath.Matrix4f;

import org.pilotix.client.Environment;
import org.pilotix.common.Angle;
import org.pilotix.common.Vector;

/**
 * Classe mère de tous les objets en 3D du jeu.
 *
 * @author Grégoire Colbert
 */
public class J3DObject extends BranchGroup {

    protected TransformGroup translationTG;
    protected TransformGroup rotationTG;
    protected Shape3D theObjectShape;
    private Transform3D translation;
    private Transform3D rotation;
    private Matrix4f tmpMatrix4f;

    /**
     * Construit l'arborescence Java3D minimale d'un objet 3D qui peut ensuite
     * être inséré dans le tableau objectsJ3D de Display3D.
     * Ce constructeur n'associe cependant à cette structure AUCUNE forme 3D.
     * Il ne doit servir que pour les objets 3D qui ne peuvent pas être décrits
     * entièrement (ou pas du tout) avec un fichier ".PilotixShape.xml" et
     * doivent donc faire l'objet d'une classe Java spécifique.
     * Ces classes spécifiques peuvent donc hériter de J3DObject sans avoir
     * un paramètre "fichier de forme 3D" en paramètre de leur constructeur.
     *
     * C'est le cas par exemple de J3DArea car c'est un rectangle non colorié,
     * alors que Shape3DHandler ne permet de générer que des triangles coloriés.
     */
    public J3DObject() {
        this.setCapability(BranchGroup.ALLOW_DETACH);

        translationTG = new TransformGroup();
        translationTG.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);

        rotationTG = new TransformGroup();
        rotationTG.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);

        translationTG.addChild(rotationTG);
        this.addChild(translationTG);

        translation = new Transform3D();
        rotation = new Transform3D();
        tmpMatrix4f = new Matrix4f(1.0f,0.0f,0.0f,0.0f,
                                    0.0f,1.0f,0.0f,0.0f,
                                    0.0f,0.0f,1.0f,0.0f,
                                    0.0f,0.0f,0.0f,1.0f);
    }

    /**
     * Construit un objet 3D pouvant être affiché par Display3D,
     * à partir d'un fichier contenant ses propriétés géométriques,
     * qui est recherché dans le répertoire pilotix.config.path/shapes
     * (par défaut ce répertoire est data/shapes/).
     *
     * @param shapeURL le nom d'un fichier ".PilotixShape.xml" dans "data/shapes"
     */
    public J3DObject(String shapeURL) {
        this();
        try {
            Shape3DHandler shape3DHandler = new Shape3DHandler();
            theObjectShape = shape3DHandler.getShape3DFromURL(shapeURL);
        } catch (Exception e) {
            e.printStackTrace();
        }
        rotationTG.addChild(theObjectShape);
    }
    
    
   
    /**
     * Définit la position de cet objet dans le plan horizontal sans modifier
     * l'altitude.
     * Le système de coordonnées du paramètre <code>aVector</code> est celui
     * du serveur, une multiplication par Environment.u3d est donc effectuée
     * pour avoir les coordonnées dans le client (réelles et non entières).
     *
     * @param aVector
     *            un vecteur définissant la position en X et en Y de l'objet.
     */
    public final void setPosition(Vector aVector) {
        tmpMatrix4f.setElement(0, 3, aVector.x * Environment.u3d);
        tmpMatrix4f.setElement(1, 3, aVector.y * Environment.u3d);
        translation.set(tmpMatrix4f);
        translationTG.setTransform(translation);
    }

    /**
     * Définit l'altitude de cet objet sans modifier sa position dans le plan
     * horizontal.
     * Le système de coordonnées du paramètre <code>altitude</code> est celui
     * du serveur, une multiplication par Environment.u3d est donc effectuée
     * pour avoir les coordonnées dans le client (réelles et non entières).
     *
     * @param altitude
     *            un entier représentant la nouvelle altitude de l'objet (sa
     *            position dans le plan horizontal ne changera pas).
     */
    public final void setAltitude(int altitude) {
        tmpMatrix4f.setElement(2, 3, altitude * Environment.u3d);
        translation.set(tmpMatrix4f);
        translationTG.setTransform(translation);
    }

    /**
     * Définit la direction de cet objet dans le plan horizontal.
     *
     * @param angle
     *            une instance de la classe Angle représentant la direction de
     *            l'objet en degrés (0 étant vers le haut, 90 à droite, etc.)
     */
    public final void setDirection(Angle angle) {
        rotation.rotZ(-Math.toRadians((double) angle.get()));
        rotationTG.setTransform(rotation);
    }
}
