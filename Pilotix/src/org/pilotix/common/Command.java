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

package org.pilotix.common;

public class Command {

    private Angle direction;
    private int acceleration;
    private int accessory;
    private int projectileId;

    public Command() {
        direction = new Angle(0);
        acceleration = 0;
        accessory = 0;
        projectileId = 0;
    }

    public void set(Command aCommand) {
        direction.set((aCommand.getDirection()).get());
        acceleration = aCommand.getAcceleration();
        accessory = aCommand.getAccessory();
        projectileId = aCommand.getProjectileId();
    }

    public void setDirection(Angle aDirection) {
        direction.set(aDirection.get());
    }

    public void setAcceleration(int anAcceleration) {
        acceleration = anAcceleration;
    }

    public void setAccessory(int anAccessory) {
        accessory = anAccessory;
    }

    public void setProjectileId(int anProjectileId) {
        projectileId = anProjectileId;
    }

    public Angle getDirection() {
        return direction;
    }

    public int getAcceleration() {
        return acceleration;
    }

    public int getAccessory() {
        return accessory;
    }

    public int getProjectileId() {
        return projectileId;
    }
}