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

package org.pilotix.server;

import org.pilotix.common.*;

class CollisionHandler {

    Vector coin1 = new Vector(0, 0);
    Vector coin2 = new Vector(65535, 65535);
    int radius = 300;
    int coin1x = coin1.x + radius;
    int coin2x = coin2.x - radius;
    int coin1y = coin1.y + radius;
    int coin2y = coin2.y - radius;

    /*public void collideWithBoundary(ServerShip s) {

        Vector currentPosition = s.getPosition();
        Vector returnedPosition = s.getNextPosition();
        Vector returnedSpeed = s.getNextSpeed();

        if ((currentPosition.x - radius) < coin1x) {
            returnedPosition.x = coin1x + radius;
            returnedSpeed.x = 0;
        } else if ((currentPosition.x + radius) > coin2x) {
            returnedPosition.x = coin2x - radius;
            returnedSpeed.x = 0;
        }

        if ((currentPosition.y - radius) < coin1y) {
            returnedPosition.y = coin1y + radius;
            returnedSpeed.y = 0;
        } else if ((currentPosition.y + radius) > coin2y) {
            returnedPosition.y = coin2y - radius;
            returnedSpeed.y = 0;
        }

        s.setNextPosition(returnedPosition);
        s.setNextSpeed(returnedSpeed);
    }*/

}
