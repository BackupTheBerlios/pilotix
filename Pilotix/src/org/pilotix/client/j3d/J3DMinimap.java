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

import org.pilotix.client.j3d.J3DCamera;
import javax.media.j3d.BranchGroup;
import javax.media.j3d.Canvas3D;

/**
 * Cette classe crée une caméra pointant vers le sol et la relie à un Canvas3D
 * donné. La caméra étant placée assez haut, la vue convient pour faire une
 * mini-carte.
 * 
 * @author Grégoire Colbert
 * 
 * @see J3DCamera
 * @see BranchGroup
 * @see Canvas3D
 *  
 */
public class J3DMinimap extends BranchGroup {

    private J3DCamera camera = null;

    /**
     * Crée une caméra et affiche ce qu'elle voit dans le Canvas3D fourni.
     * 
     * @param aCanvas3D
     *            le Canvas3D auquel relier la caméra
     */
    public J3DMinimap(Canvas3D aCanvas3D, float aX, float aY) {
        setCapability(BranchGroup.ALLOW_DETACH);
        camera = new J3DCamera(aCanvas3D);
        addChild(camera);
        camera.setCoordinates(aX, aY, (float) camera.getView()
                .getBackClipDistance());
        compile();
    }

    /**
     * Renvoie la caméra associée à cette mini-carte.
     * 
     * @return la caméra
     */
    public J3DCamera getCamera() {
        return camera;
    }
}
