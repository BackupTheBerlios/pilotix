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

/*
 * 
 * Vector : type de base pour la représentation 2D
 * 
 * Auteurs : - Florent (dernière modif : 16/04/2003)
 *  
 */
package org.pilotix.common;

/**
 * This class defines a basic type to handle 2D objects caracteristics.
 * 
 * @see PilotixElement
 * 
 * @author Florent Sithimolada
 */
public class Vector {

    public int x;
    public int y;

    public Vector(int px, int py) {
        x = px;
        y = py;
    }

    public void set(int px, int py) {
        x = px;
        y = py;
    }

    public void set(Vector b) {
        x = b.x;
        y = b.y;
    }

    public void dot(Vector b) {
        x = x * b.x;
        y = y * b.y;
    }
}
