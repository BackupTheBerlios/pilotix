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

import javax.media.j3d.*;
import javax.vecmath.Point3f;
//import javax.media.j3d.ColoringAttributes;
import javax.vecmath.Color3f;

/**
 * Cette classe dessine un Shape3D qui ressemble à un vaisseau.
 * 
 * @author Grégoire Colbert
 */
public class J3DShipShape extends Shape3D {

    /**
     * Constructeur.
     */
    public J3DShipShape(Color3f aColor3f) {
        // On définit sa géométrie...
        TriangleArray thisShipGeometry = new TriangleArray(18,
                TriangleArray.COORDINATES | TriangleArray.COLOR_3);
        //TriangleArray thisShipGeometry = new
        // TriangleArray(18,TriangleArray.COORDINATES);
        thisShipGeometry.setCapability(GeometryArray.ALLOW_COLOR_WRITE);
        thisShipGeometry.setCapability(GeometryArray.ALLOW_COLOR_READ);

        // C'est un vaisseau qui pointe vers le haut
        /*
         * thisShipGeometry.setCoordinate(0,new Point3f( 0.0f , 5.0f , 2.0f));
         * thisShipGeometry.setCoordinate(1,new Point3f(-3.0f ,-5.0f , 2.0f));
         * thisShipGeometry.setCoordinate(2,new Point3f( 3.0f ,-5.0f , 2.0f));
         */

        thisShipGeometry.setCoordinate(0, new Point3f(0.0f, 5.0f, 2.0f));
        thisShipGeometry.setCoordinate(1, new Point3f(-3.0f, -5.0f, 2.0f));
        thisShipGeometry.setCoordinate(2, new Point3f(-1.0f, -3.0f, 3.0f));
        thisShipGeometry.setCoordinate(3, new Point3f(0.0f, 5.0f, 2.0f));
        thisShipGeometry.setCoordinate(4, new Point3f(1.0f, -3.0f, 3.0f));
        thisShipGeometry.setCoordinate(5, new Point3f(3.0f, -5.0f, 2.0f));
        Color3f grisClair = new Color3f(0.8f, 0.8f, 0.8f);
        Color3f gris = new Color3f(0.3f, 0.3f, 0.3f);
        thisShipGeometry.setColor(0, gris);
        thisShipGeometry.setColor(1, grisClair);
        thisShipGeometry.setColor(2, grisClair);
        thisShipGeometry.setColor(3, gris);
        thisShipGeometry.setColor(4, grisClair);
        thisShipGeometry.setColor(5, grisClair);

        thisShipGeometry.setCoordinate(6, new Point3f(-3.0f, -5.0f, 2.0f));
        thisShipGeometry.setCoordinate(7, new Point3f(0.0f, -5.0f, 2.0f));
        thisShipGeometry.setCoordinate(8, new Point3f(-1.0f, -3.0f, 3.0f));
        thisShipGeometry.setCoordinate(9, new Point3f(3.0f, -5.0f, 2.0f));
        thisShipGeometry.setCoordinate(10, new Point3f(1.0f, -3.0f, 3.0f));
        thisShipGeometry.setCoordinate(11, new Point3f(0.0f, -5.0f, 2.0f));
        Color3f bleuCiel = new Color3f(0.0f, 0.5f, 1.0f);
        Color3f orange = new Color3f(1.0f, 0.66f, 0.0f);
        Color3f rouge = new Color3f(1.0f, 0.0f, 0.0f);

        thisShipGeometry.setColor(6, orange);
        thisShipGeometry.setColor(7, rouge);
        thisShipGeometry.setColor(8, rouge);
        thisShipGeometry.setColor(9, orange);
        thisShipGeometry.setColor(10, rouge);
        thisShipGeometry.setColor(11, rouge);

        thisShipGeometry.setCoordinate(12, new Point3f(0.0f, -5.0f, 2.0f));
        thisShipGeometry.setCoordinate(13, new Point3f(1.0f, -3.0f, 3.0f));
        thisShipGeometry.setCoordinate(14, new Point3f(-1.0f, -3.0f, 3.0f));
        thisShipGeometry.setColor(12, rouge);
        thisShipGeometry.setColor(13, rouge);
        thisShipGeometry.setColor(14, rouge);

        thisShipGeometry.setCoordinate(15, new Point3f(0.0f, 5.0f, 2.0f));
        thisShipGeometry.setCoordinate(16, new Point3f(-1.0f, -3.0f, 3.0f));
        thisShipGeometry.setCoordinate(17, new Point3f(1.0f, -3.0f, 3.0f));
        thisShipGeometry.setColor(15, gris);
        thisShipGeometry.setColor(16, aColor3f);
        thisShipGeometry.setColor(17, aColor3f);

        this.setGeometry(thisShipGeometry);

        // Et son apparence
        Appearance thisShipAppearance = new Appearance();
        thisShipAppearance.setCapability(Appearance.ALLOW_MATERIAL_WRITE);
        //ColoringAttributes col = new
        // ColoringAttributes(aColor3f,ColoringAttributes.FASTEST);
        //thisShipAppearance.setColoringAttributes(col);
        Color3f black = new Color3f(0.0f, 0.0f, 0.0f);
        Color3f white = new Color3f(1.0f, 1.0f, 1.0f);
        thisShipAppearance.setMaterial(new Material(gris, orange, grisClair,
                white, 10.0f));
        this.setAppearance(thisShipAppearance);

    }
}
