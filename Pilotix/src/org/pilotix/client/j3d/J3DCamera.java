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

import org.pilotix.client.Environment;
import javax.media.j3d.Canvas3D;
import javax.media.j3d.PhysicalBody;
import javax.media.j3d.PhysicalEnvironment;
import javax.media.j3d.Transform3D;
import javax.media.j3d.TransformGroup;
import javax.media.j3d.View;
import javax.media.j3d.ViewPlatform;
import javax.vecmath.Vector3f;

/**
 * <p>
 * Cette classe regroupe les classes Java3D à instancier pour voir ce qui se
 * passe dans le monde virtuel de Pilotix.
 * </p>
 * 
 * <p>
 * Techniquement, cette classe doit être utilisée comme un TransformGroup. Elle
 * crée un objet View, un objet ViewPlatform et un Transform3D.
 * </p>
 * 
 * @see TransformGroup
 * @see ViewPlatform
 * @see View
 * @see Transform3D
 * 
 * @author Grégoire Colbert
 */
public class J3DCamera extends TransformGroup {

    private View view = null;
    private ViewPlatform viewPlatform = null;
    private Transform3D trans3D = null;

    /**
     * Crée un TransformGroup et le remplit avec une ViewPlatform, une View et
     * une matrice Transform3D qui sert à stocker la position de la caméra.
     * Nous appelons finalement la méthode setCoordinates avec x=0.0f, y=0.0f
     * et z=150.0f ce qui place la caméra au dessus des objets du BranchGroup
     * où ce TransformGroup est ajouté (le x et le y sont relatifs au
     * BranchGroup).
     * 
     * @param aCanvas3D
     *            le Canvas3D où vous voulez afficher les images vues par cette
     *            caméra.
     * 
     * @see TransformGroup
     * @see ViewPlatform
     * @see View
     * @see Transform3D
     */
    public J3DCamera(Canvas3D aCanvas3D) {
        super();
        this.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);

        viewPlatform = new ViewPlatform();
        this.addChild(viewPlatform);

        view = new View();
        view.setBackClipPolicy(View.VIRTUAL_EYE);
        float xDim = Environment.theClientArea.getXMax();
        double hauteurCameraMinimap = 0.5f * xDim
                / java.lang.Math.tan(0.5f * view.getFieldOfView());
        view.setBackClipDistance(hauteurCameraMinimap);
        view.setFrontClipPolicy(View.VIRTUAL_EYE);
        view.setFrontClipDistance(10.0f);
        view.addCanvas3D(aCanvas3D);
        view.attachViewPlatform(viewPlatform);

        view.setPhysicalBody(new PhysicalBody());
        view.setPhysicalEnvironment(new PhysicalEnvironment());

        trans3D = new Transform3D();
        setCoordinates(0.0f, 0.0f, 200.0f);
    }

    /**
     * Met la caméra à la position (x, y, z) fournie. Nous faisons cela en
     * appliquant un objet Vector3f au champ Transform3D de la caméra. Ces
     * valeurs sont relatives au noeud-père de cet objet J3DCamera.
     */
    public void setCoordinates(float x, float y, float z) {
        trans3D.setTranslation(new Vector3f(x, y, z));
        this.setTransform(trans3D);
    }

    /**
     * Renvoie l'objet View de cette caméra, qui référence le Canvas3D sur
     * lequel se fait l'affichage.
     *
     * @return l'objet View de cette caméra
     */
    public View getView() {
        return view;
    }

    /**
     * Renvoie l'objet ViewPlatform de cette caméra.
     * 
     * @return l'objet ViewPlatform de cette caméra
     */
    private ViewPlatform getViewPlatform() {
        return viewPlatform;
    }

    /**
     * Renvoie l'objet Transform3D de cette caméra.
     * 
     * @return l'objet Transfom3D de cette caméra
     */
    private Transform3D getTransform3D() {
        return trans3D;
    }

    /**
     * Applique à cette caméra l'objet Transform3D fourni.
     * 
     * @param aTransform3D
     *            la transformation à appliquer
     */
    private void setTransform3D(Transform3D aTransform3D) {
        trans3D = aTransform3D;
    }

}
