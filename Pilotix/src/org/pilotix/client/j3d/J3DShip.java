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

import org.pilotix.client.Display3D;

import javax.media.j3d.BoundingSphere;
import javax.media.j3d.Light;
import javax.media.j3d.SpotLight;
import javax.media.j3d.PointLight;
import javax.media.j3d.Fog;
import javax.media.j3d.LinearFog;
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
 * Optionnellement, comme avec tous les J3DObject, une J3DCamera peut être
 * ajoutée et elle suivra alors le J3DShip. Voir la classe J3DCamera pour plus
 * d'informations sur les caméras.
 * </p>
 *
 * @see J3DObject
 * @see org.pilotix.common.Ship
 * @see J3DCamera
 *
 * @author Grégoire Colbert
 */
public class J3DShip extends J3DObject {
    public Light[] lights;

    /**
     * Crée une représentation 3D d'un vaisseau.
     */
    public J3DShip(String shipShapeURL, Color3f aShipColor) {
        super(shipShapeURL, aShipColor);

        lights = new Light[3];

        // Creation de deux phares blancs a l'avant du vaisseau
        float spotRange = 150.0f;
        Point3d leftSpotPosition = new Point3d(-0.5f, 0.0f, 2.5f);
        BoundingSphere leftSpotBounds = new BoundingSphere(
                                        leftSpotPosition,spotRange);
        lights[0] = new SpotLight(
                                new Color3f(1.0f, 1.0f, 1.0f),
                                new Point3f(leftSpotPosition),
                                new Point3f(1.0f, 0.0f, 0.0003f),
                                new Vector3f(0.0f, 1.0f, 0.1f),
                                (float) java.lang.Math.PI / 6,
                                16.0f);
        lights[0].setInfluencingBounds(leftSpotBounds);
        rotationTG.addChild(lights[0]);

        Point3d rightSpotPosition = new Point3d(0.5f, 0.0f, 2.5f);
        BoundingSphere rightSpotBounds = new BoundingSphere(
                                         rightSpotPosition, spotRange);
        lights[1] = new SpotLight(
                                new Color3f(1.0f, 1.0f, 1.0f),
                                new Point3f(rightSpotPosition),
                                new Point3f(1.0f, 0.0f, 0.0003f),
                                new Vector3f(0.0f, 1.0f, 0.1f),
                                (float) java.lang.Math.PI / 6,
                                16.0f);
        lights[1].setInfluencingBounds(rightSpotBounds);
        rotationTG.addChild(lights[1]);

        // Lumiere du reacteur
        Point3d reactorLightPosition = new Point3d(0.0f, -3.0f, 0.0f);
        BoundingSphere reactorLightBounds = new BoundingSphere(
                                            reactorLightPosition, 50.0f);
        lights[2] = new SpotLight(
                new Color3f(1.0f, 0.0f, 0.0f),
                new Point3f(reactorLightPosition),
                new Point3f(1.0f, 0.01f, 0.001f),
                new Vector3f(0.0f, -1.0f, 0.1f),
                (float) java.lang.Math.PI / 8,
                32.0f);
        lights[2].setInfluencingBounds(reactorLightBounds);
        rotationTG.addChild(lights[2]);

        // Lumiere du cockpit de la couleur du joueur
        Point3d cockpitLightPosition = new Point3d(0.0f, 0.0f, 5.0f);
        BoundingSphere cockpitLightBounds = new BoundingSphere(
                                            cockpitLightPosition, 30.0f);
        Light cockpitLight = new PointLight(
                true,
                //aShipColor,
                new Color3f(0.3f, 0.3f, 0.3f),
                new Point3f(cockpitLightPosition),
                new Point3f(1.0f, 0.0f,0.009f));
        cockpitLight.setInfluencingBounds(cockpitLightBounds);
        rotationTG.addChild(cockpitLight);

/*
        Fog fog = new LinearFog(new Color3f(0.0f,0.9f,0.0f),1.0f,200.0f);
        rotationTG.addChild(fog);
*/

    }
}
