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

import javax.media.j3d.BoundingSphere;
import javax.media.j3d.Light;
import javax.media.j3d.SpotLight;
import javax.media.j3d.PointLight;
import javax.vecmath.Color3f;
import javax.vecmath.Point3d;
import javax.vecmath.Point3f;
import javax.vecmath.Vector3f;

/**
 * <p>
 * Cette classe regroupe les objets Java3D qui constituent visuellement un
 * vaisseau. Cette classe est générique, elle peut être utilisée pour n'importe
 * quel joueur et peut afficher n'importe quel équipement. Pour l'aspect
 * non-graphique d'un vaisseau, voir la classe Ship.
 * </p>
 * 
 * <p>
 * Techniquement, cette classe hérite de J3DObject.
 * Optionnellement, une J3DCamera peut être
 * ajoutée et elle suivra alors le J3DShip. Voir la classe J3DCamera pour plus
 * d'informations sur les caméras.
 * </p>
 *
 * @see org.pilotix.common.Ship
 * @see J3DCamera
 * 
 * @author Grégoire Colbert
 */
public class J3DShip extends J3DObject {

    private J3DCamera camera = null;

    /**
     * Crée une représentation 3D d'un vaisseau.
     */
    public J3DShip(String shipShapeURL, Color3f aShipColor) {
        super(shipShapeURL, aShipColor);

        // Creation de deux phares blancs a l'avant du vaisseau
        float spotRange = 150.0f;
        Point3d leftSpotPosition = new Point3d(-0.5f, 0.0f, 2.5f);
        BoundingSphere leftSpotBounds = new BoundingSphere(
                                        leftSpotPosition,spotRange);
        Light thisShipLeftSpot = new SpotLight(
                                new Color3f(1.0f, 1.0f, 1.0f),
                                new Point3f(leftSpotPosition),
                                new Point3f(1.0f, 0.0f, 0.0003f),
                                new Vector3f(0.0f, 1.0f, 0.1f),
                                (float) java.lang.Math.PI / 6,
                                16.0f);
        thisShipLeftSpot.setInfluencingBounds(leftSpotBounds);
        rotationTG.addChild(thisShipLeftSpot);

        Point3d rightSpotPosition = new Point3d(0.5f, 0.0f, 2.5f);
        BoundingSphere rightSpotBounds = new BoundingSphere(
                                         rightSpotPosition, spotRange);
        Light thisShipRightSpot = new SpotLight(
                                new Color3f(1.0f, 1.0f, 1.0f),
                                new Point3f(rightSpotPosition),
                                new Point3f(1.0f, 0.0f, 0.0003f),
                                new Vector3f(0.0f, 1.0f, 0.1f),
                                (float) java.lang.Math.PI / 6,
                                16.0f);
        thisShipRightSpot.setInfluencingBounds(rightSpotBounds);
        rotationTG.addChild(thisShipRightSpot);

        // Lumiere du reacteur
        Point3d reactorLightPosition = new Point3d(0.0f, -3.0f, 0.0f);
        BoundingSphere reactorLightBounds = new BoundingSphere(
                                            reactorLightPosition, 50.0f);
        Light thisShipReactorLight = new SpotLight(
                new Color3f(1.0f, 0.0f, 0.0f),
                new Point3f(reactorLightPosition),
                new Point3f(1.0f, 0.01f, 0.001f),
                new Vector3f(0.0f, -1.0f, 0.1f),
                (float) java.lang.Math.PI / 8,
                32.0f);
        thisShipReactorLight.setInfluencingBounds(reactorLightBounds);
        rotationTG.addChild(thisShipReactorLight);

        // Lumiere du cockpit de la couleur du joueur
        Point3d cockpitLightPosition = new Point3d(0.0f, 0.0f, 5.0f);
        BoundingSphere cockpitLightBounds = new BoundingSphere(
                                            cockpitLightPosition, 30.0f);
        Light thisShipCockpitLight = new PointLight(
                true,
                //aShipColor,
                new Color3f(0.3f, 0.3f, 0.3f),
                new Point3f(cockpitLightPosition),
                new Point3f(1.0f, 0.0f,0.009f));
        thisShipCockpitLight.setInfluencingBounds(cockpitLightBounds);
        rotationTG.addChild(thisShipCockpitLight);

        /*
         * Fog fog = new LinearFog(new Color3f(0.0f,1.0f,0.0f),10.0f,200.0f);
         * translationTG.addChild(fog);
         */
    }

    /**
     * Ajoute une J3DCamera, qui ne tournera pas, au-dessus de ce vaisseau.
     *
     * @param aCamera
     *            la caméra à mettre au-dessus de ce vaisseau
     */
    public void addCamera(J3DCamera aCamera) {
        addCamera(aCamera, false);
    }

    /**
     * Ajoute une J3DCamera au-dessus de ce vaisseau.
     *
     * @param aCamera
     *            la caméra à mettre au-dessus de ce vaisseau
     * @param canRotate
     *            détermine si la caméra peut tourner dans le plan X-Y.
     *            <ul>
     *            <li>Mis à <code>false</code>, la caméra suivra le
     *            vaisseau du joueur mais ne tournera pas avec lui.</li>
     *            <li>Mis à <code>true</code>, le nez du vaisseau pointera
     *            toujours vers le haut de l'écran, ce qui veut dire que c'est
     *            l'arrière-plan qui tournera et non le vaisseau.</li>
     *            </ul>
     *            Mettre <code>canRotate</code> à vrai doit rendre
     *            l'affichage plus lent, l'option par défaut est donc <code>false</code>.
     */
    public void addCamera(J3DCamera aCamera, boolean canRotate) {
        camera = aCamera;
        if (canRotate) {
            rotationTG.addChild(aCamera);
        } else {
            translationTG.addChild(aCamera);
        }
    }

    /**
     * Renvoie la caméra associée avec ce vaisseau, si elle existe.
     *
     * @return la caméra qui suit ce vaisseau, ou <code>null</code> si elle
     *         n'existe pas.
     */
    public final J3DCamera getCamera() {
        return camera;
    }
}
