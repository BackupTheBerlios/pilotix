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

import org.pilotix.common.ResourceLocator;
import org.pilotix.common.Vector;
import org.pilotix.client.Environment;

/**
 * <p>
 * Cette classe construit une balle.
 * </p>
 */
public class J3DBall extends J3DObject {

    /**
     * Construit une balle.
     */
    public J3DBall(Vector position, int altitude) {

        super();

        // --------------------
        // GEOMETRIE : une sphère
        com.sun.j3d.utils.geometry.Sphere aSphere =
            new com.sun.j3d.utils.geometry.Sphere(1.0f);

        // --------------------
        // POSITIONNEMENT DE L'OBJET DANS LA SCENE
        rotationTG.addChild(aSphere);
        this.setPosition(position);
        this.setAltitude(altitude);

        this.compile();
        System.out.println("J3DBall construite");
    }
}
