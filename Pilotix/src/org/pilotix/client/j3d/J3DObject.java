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

import javax.media.j3d.Group;
import javax.media.j3d.BranchGroup;
import javax.media.j3d.Shape3D;
import javax.media.j3d.Transform3D;
import javax.media.j3d.TransformGroup;
import javax.vecmath.Matrix4f;
import javax.vecmath.Color3f;

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
    private J3DCamera camera;
    private boolean cameraCanRotate = false;

    /**
     * Construit l'arborescence Java3D minimale d'un objet 3D qui peut ensuite
     * être inséré dans le tableau objectsJ3D de Display3D.
     * Ce constructeur n'associe cependant à cette structure AUCUNE forme 3D.
     * Il ne doit servir que pour les objets 3D qui ne peuvent pas être décrits
     * entièrement (ou pas du tout) avec un fichier ".pilotix.shape.xml" et
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
        // Les deux lignes suivantes servent à changer la caméra de place
        // dans l'arborescence : on peut alors la rattacher soit à translationTG,
        // soit à rotationTG (par la méthode cameraRotationSwitch())
        translationTG.setCapability(Group.ALLOW_CHILDREN_WRITE);
        translationTG.setCapability(Group.ALLOW_CHILDREN_EXTEND);

        rotationTG = new TransformGroup();
        rotationTG.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
        // Les deux lignes suivantes servent à changer la caméra de place
        // dans l'arborescence : on peut alors la rattacher soit à translationTG,
        // soit à rotationTG (par la méthode cameraRotationSwitch())
        rotationTG.setCapability(Group.ALLOW_CHILDREN_WRITE);
        rotationTG.setCapability(Group.ALLOW_CHILDREN_EXTEND);

        translationTG.addChild(rotationTG);
        this.addChild(translationTG);

        translation = new Transform3D();
        rotation = new Transform3D();
        tmpMatrix4f = new Matrix4f(1.0f, 0.0f, 0.0f, 0.0f,
                                   0.0f, 1.0f, 0.0f, 0.0f,
                                   0.0f, 0.0f, 1.0f, 0.0f,
                                   0.0f, 0.0f, 0.0f, 1.0f);
        camera = null;


        // Une lumiere d'ambiance

        javax.media.j3d.BoundingSphere areaLightBounds = new javax.media.j3d.BoundingSphere(
                                                    new javax.vecmath.Point3d(1000.0d, 1000.0d, 100.0d),
                                                    6000.0d);
        javax.media.j3d.AmbientLight areaLight = new javax.media.j3d.AmbientLight(
                                                 new Color3f(1.0f, 1.0f, 0.0f));
        areaLight.setInfluencingBounds(areaLightBounds);
        areaLight.setEnable(true);
        this.addChild(areaLight);


    }

    /**
     * Construit un objet 3D pouvant être affiché par Display3D,
     * à partir d'un fichier contenant ses propriétés géométriques,
     * qui est recherché dans le répertoire pilotix.config.path/shapes
     * (par défaut ce répertoire est data/shapes/).
     *
     * @param aShapeURL
     *        le nom d'un fichier ".pilotix.shape.xml" dans "data/shapes"
     * @param aDynamicColor
     *        la couleur à utiliser si l'attribut rgb="dynamic" dans le fichier XML
     */
    public J3DObject(String aShapeURL, Color3f aDynamicColor) {
        this();
        try {
            Shape3DHandler shape3DHandler = new Shape3DHandler();
            theObjectShape = shape3DHandler.getShape3DFromURL(aShapeURL, aDynamicColor);
        } catch (Exception e) {
            e.printStackTrace();
        }
        rotationTG.addChild(theObjectShape);
    }


    public J3DObject(String aShapeURL, Color3f aDynamicColor,Vector position,Angle direction) {
        this();
        try {
            Shape3DHandler shape3DHandler = new Shape3DHandler();
            theObjectShape = shape3DHandler.getShape3DFromURL(aShapeURL, aDynamicColor);
        } catch (Exception e) {
            e.printStackTrace();
        }
        rotationTG.addChild(theObjectShape);
        setPosition(position);
        setDirection(direction);
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

    /**
     * Ajoute une J3DCamera, qui ne tournera pas, au-dessus de ce J3DObject.
     *
     * @param aCamera
     *            la caméra à mettre au-dessus de cet objet
     */
    public void addCamera(J3DCamera aCamera) {
        addCamera(aCamera, false);
    }

    /**
     * Ajoute une J3DCamera au-dessus de ce J3DObject.
     *
     * @param aCamera
     *            la caméra à mettre au-dessus de cet objet
     * @param canRotate
     *            détermine si la caméra peut tourner dans le plan X-Y.
     *            <ul>
     *            <li>Mis à <code>false</code>, la caméra suivra le
     *            J3DObject mais ne tournera pas avec lui.</li>
     *            <li>Mis à <code>true</code>, le nez du vaisseau (si l'objet
     *            est un vaisseau) pointera toujours vers le haut de l'écran,
     *            ce qui veut dire que c'est l'arrière-plan qui tournera et
     *            non le vaisseau.</li>
     *            </ul>
     *            Mettre <code>canRotate</code> à vrai doit rendre
     *            l'affichage plus lent, l'option par défaut est donc <code>false</code>.
     */
    public void addCamera(J3DCamera aCamera, boolean canRotate) {
        camera = aCamera;
        cameraCanRotate = canRotate;
        if (cameraCanRotate) {
            rotationTG.addChild(camera);
        } else {
            translationTG.addChild(camera);
        }
    }

    /**
     * Supprime la J3DCamera associée à ce J3DObject, si elle existe;
     * ne fait rien dans le cas contraire.
     */
    public void removeCamera() {
        if (camera!=null) {
            if (cameraCanRotate) {
                rotationTG.removeChild(camera);
            }
            else {
                translationTG.removeChild(camera);
            }
            camera = null;
        }
    }

    /**
     * Permet de changer le comportement de rotation de la caméra.
     * Si la caméra tournait avec ce J3DObject autour de l'axe Z, elle
     * ne tournera plus (son angle supérieur gauche sera vers le Nord-Ouest,
     * son angle supérieur droit vers le Nord-Est, etc.);
     * inversement, si elle ne pouvait pas tourner, elle reproduira désormais
     * les mouvements de rotation appliqués à ce J3DObject.
     */
    public void cameraRotationSwitch() {
        if (camera!=null) {
            camera.detach();
            if (cameraCanRotate) {
                translationTG.addChild(camera);
            }
            else {
                rotationTG.addChild(camera);
            }
            cameraCanRotate = !cameraCanRotate;
        }
    }

    /**
     * Renvoie la caméra associée avec ce J3DObject, si elle existe.
     *
     * @return la caméra qui suit ce J3DObject, ou <code>null</code> si elle
     *         n'existe pas.
     */
    public final J3DCamera getCamera() {
        return camera;
    }
}
