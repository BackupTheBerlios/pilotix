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
 * Classe m�re de tous les objets en 3D du jeu.
 *
 * @author Gr�goire Colbert
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
     * �tre ins�r� dans le tableau objectsJ3D de Display3D.
     * Ce constructeur n'associe cependant � cette structure AUCUNE forme 3D.
     * Il ne doit servir que pour les objets 3D qui ne peuvent pas �tre d�crits
     * enti�rement (ou pas du tout) avec un fichier ".pilotix.shape.xml" et
     * doivent donc faire l'objet d'une classe Java sp�cifique.
     * Ces classes sp�cifiques peuvent donc h�riter de J3DObject sans avoir
     * un param�tre "fichier de forme 3D" en param�tre de leur constructeur.
     *
     * C'est le cas par exemple de J3DArea car c'est un rectangle non colori�,
     * alors que Shape3DHandler ne permet de g�n�rer que des triangles colori�s.
     */
    public J3DObject() {
        this.setCapability(BranchGroup.ALLOW_DETACH);

        translationTG = new TransformGroup();
        translationTG.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
        // Les deux lignes suivantes servent � changer la cam�ra de place
        // dans l'arborescence : on peut alors la rattacher soit � translationTG,
        // soit � rotationTG (par la m�thode cameraRotationSwitch())
        translationTG.setCapability(Group.ALLOW_CHILDREN_WRITE);
        translationTG.setCapability(Group.ALLOW_CHILDREN_EXTEND);

        rotationTG = new TransformGroup();
        rotationTG.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
        // Les deux lignes suivantes servent � changer la cam�ra de place
        // dans l'arborescence : on peut alors la rattacher soit � translationTG,
        // soit � rotationTG (par la m�thode cameraRotationSwitch())
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
     * Construit un objet 3D pouvant �tre affich� par Display3D,
     * � partir d'un fichier contenant ses propri�t�s g�om�triques,
     * qui est recherch� dans le r�pertoire pilotix.config.path/shapes
     * (par d�faut ce r�pertoire est data/shapes/).
     *
     * @param aShapeURL
     *        le nom d'un fichier ".pilotix.shape.xml" dans "data/shapes"
     * @param aDynamicColor
     *        la couleur � utiliser si l'attribut rgb="dynamic" dans le fichier XML
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
     * D�finit la position de cet objet dans le plan horizontal sans modifier
     * l'altitude.
     * Le syst�me de coordonn�es du param�tre <code>aVector</code> est celui
     * du serveur, une multiplication par Environment.u3d est donc effectu�e
     * pour avoir les coordonn�es dans le client (r�elles et non enti�res).
     *
     * @param aVector
     *            un vecteur d�finissant la position en X et en Y de l'objet.
     */
    public final void setPosition(Vector aVector) {
        tmpMatrix4f.setElement(0, 3, aVector.x * Environment.u3d);
        tmpMatrix4f.setElement(1, 3, aVector.y * Environment.u3d);
        translation.set(tmpMatrix4f);
        translationTG.setTransform(translation);
    }

    /**
     * D�finit l'altitude de cet objet sans modifier sa position dans le plan
     * horizontal.
     * Le syst�me de coordonn�es du param�tre <code>altitude</code> est celui
     * du serveur, une multiplication par Environment.u3d est donc effectu�e
     * pour avoir les coordonn�es dans le client (r�elles et non enti�res).
     *
     * @param altitude
     *            un entier repr�sentant la nouvelle altitude de l'objet (sa
     *            position dans le plan horizontal ne changera pas).
     */
    public final void setAltitude(int altitude) {
        tmpMatrix4f.setElement(2, 3, altitude * Environment.u3d);
        translation.set(tmpMatrix4f);
        translationTG.setTransform(translation);
    }

    /**
     * D�finit la direction de cet objet dans le plan horizontal.
     *
     * @param angle
     *            une instance de la classe Angle repr�sentant la direction de
     *            l'objet en degr�s (0 �tant vers le haut, 90 � droite, etc.)
     */
    public final void setDirection(Angle angle) {
        rotation.rotZ(-Math.toRadians((double) angle.floatValue()));
        rotationTG.setTransform(rotation);
    }

    /**
     * Ajoute une J3DCamera, qui ne tournera pas, au-dessus de ce J3DObject.
     *
     * @param aCamera
     *            la cam�ra � mettre au-dessus de cet objet
     */
    public void addCamera(J3DCamera aCamera) {
        addCamera(aCamera, false);
    }

    /**
     * Ajoute une J3DCamera au-dessus de ce J3DObject.
     *
     * @param aCamera
     *            la cam�ra � mettre au-dessus de cet objet
     * @param canRotate
     *            d�termine si la cam�ra peut tourner dans le plan X-Y.
     *            <ul>
     *            <li>Mis � <code>false</code>, la cam�ra suivra le
     *            J3DObject mais ne tournera pas avec lui.</li>
     *            <li>Mis � <code>true</code>, le nez du vaisseau (si l'objet
     *            est un vaisseau) pointera toujours vers le haut de l'�cran,
     *            ce qui veut dire que c'est l'arri�re-plan qui tournera et
     *            non le vaisseau.</li>
     *            </ul>
     *            Mettre <code>canRotate</code> � vrai doit rendre
     *            l'affichage plus lent, l'option par d�faut est donc <code>false</code>.
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
     * Supprime la J3DCamera associ�e � ce J3DObject, si elle existe;
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
     * Permet de changer le comportement de rotation de la cam�ra.
     * Si la cam�ra tournait avec ce J3DObject autour de l'axe Z, elle
     * ne tournera plus (son angle sup�rieur gauche sera vers le Nord-Ouest,
     * son angle sup�rieur droit vers le Nord-Est, etc.);
     * inversement, si elle ne pouvait pas tourner, elle reproduira d�sormais
     * les mouvements de rotation appliqu�s � ce J3DObject.
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
     * Renvoie la cam�ra associ�e avec ce J3DObject, si elle existe.
     *
     * @return la cam�ra qui suit ce J3DObject, ou <code>null</code> si elle
     *         n'existe pas.
     */
    public final J3DCamera getCamera() {
        return camera;
    }
}
