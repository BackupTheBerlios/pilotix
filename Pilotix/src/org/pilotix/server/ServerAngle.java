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

public class ServerAngle extends Angle {

	private double directionX;
	private double directionY;

	public ServerAngle() {
		super();
	}

	public ServerAngle(int aDegree) {
		super(aDegree);
		set(aDegree);
	}

	public void set(float aDegree) {
		super.set(aDegree);
		directionX = Math.sin(Math.toRadians(this.floatValue()));
		directionY = Math.cos(Math.toRadians(this.floatValue()));
	}

	public double getX() {
		return directionX;
	}

	public double getY() {
		return directionY;
	}

	public void plus(Angle anAngle) {
		if ((this.floatValue() + anAngle.floatValue()) < 0) {
			set(((this.floatValue() + anAngle.floatValue()) % -360) + 360);
		} else {
			set(this.floatValue() + anAngle.floatValue());
		}
	}
}
