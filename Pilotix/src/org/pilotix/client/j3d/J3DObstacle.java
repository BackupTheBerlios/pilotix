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
import com.sun.j3d.utils.geometry.Box;
import javax.media.j3d.Appearance;
import com.sun.j3d.utils.image.TextureLoader;
import javax.media.j3d.ImageComponent2D;
import javax.media.j3d.Texture2D;
import javax.media.j3d.Texture;

/**
 * <p>
 * Cette classe construit un obstacle visuel à partir de ses
 * caractéristiques.
 * </p>
 */
public class J3DObstacle extends J3DObject {

    /**
     * Construit un obstacle dont les coordonnées des coins sont fournis
     * (le système de coordonnées est celui du serveur), et recouvre
     * les faces avec la texture indiquée, dont l'URL sera trouvée
     * par ResourceLocator.
     */
    public J3DObstacle(Vector upLeftCorner,
                        Vector downRightCorner,
                        int height,
                        int altitude,
                        String texture) {

        super();

        TextureLoader loader = new TextureLoader(
               Environment.theRL.getResource(ResourceLocator.TEXTURE, texture),
               Environment.theGUI);
        ImageComponent2D image = loader.getImage();
        Texture2D texture2D = new Texture2D(Texture.BASE_LEVEL,
                                            Texture.INTENSITY,
                                            256, 256);
        texture2D.setBoundaryModeS(Texture.WRAP);
        texture2D.setBoundaryModeT(Texture.WRAP);
        texture2D.setImage(0, image);

        Appearance obstacleAppearance = new Appearance();
        obstacleAppearance.setTexture(texture2D);

        Box obstacleShape = new Box(((downRightCorner.x - upLeftCorner.x) / 2)*Environment.u3d,
                                     ((upLeftCorner.y - downRightCorner.y) / 2)*Environment.u3d,
                                     height*Environment.u3d,
                                     obstacleAppearance);
        rotationTG.addChild(obstacleShape);

        this.setPosition(new Vector(
                upLeftCorner.x + ((downRightCorner.x - upLeftCorner.x) / 2),
                downRightCorner.y + ((upLeftCorner.y - downRightCorner.y) / 2)));

        this.setAltitude(altitude);

        this.compile();
    }
}
