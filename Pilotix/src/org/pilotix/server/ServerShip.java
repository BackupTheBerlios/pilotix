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
import java.util.LinkedList;

public class ServerShip extends org.pilotix.common.Ship {

    protected LinkedList forces;

    //protected Vector currentPosition;
    protected Vector currentSpeed;

    protected Vector nextPosition;
    protected Vector nextSpeed;

    private Vector tmpVector;
    private Angle tmpAngle;

    protected int radius = 300;
    private byte FlagId = 7;

    protected Command command;

    public ServerShip() {
        super();
        nextPosition = new Vector(0, 0);
        nextSpeed = new Vector(0, 0);
        currentSpeed = new Vector(0, 0);
        tmpVector = new Vector(0, 0);
        direction = (Angle) new ServerAngle(0);
        command = new Command();
        tmpAngle = new Angle(0);
        command.setDirection(tmpAngle);

    }

    public void set(int aShipId, Vector aPosition, ServerAngle aDirection,
            int theStates) {
        id = aShipId;
        states = theStates;
        currentPosition.set(aPosition);
        direction.set(aDirection);
    }

    public void setCommand(Command aCommand) {
        command.set(aCommand);
    }

    public void addCommand(Command aCommand) {
        tmpAngle.set((command.getDirection()).get()
                + ((aCommand.getDirection()).get()));
        command.setDirection(tmpAngle);
        command.setAcceleration(aCommand.getAcceleration());
        //command.setAccessory(aCommand.getAccessory());
        //command.SetProjectileId(aCommand.getProjectileId());
    }

    public void computeSpeedFromForces() {
        ((ServerAngle) direction).plus(command.getDirection());
        tmpVector.set((int) (((ServerAngle) direction).getX()
                * command.getAcceleration() * 10),
                (int) (((ServerAngle) direction).getY()
                        * command.getAcceleration() * 10));
        //System.out.println("[ServerShip] Ship "+id+" Deg "+direction.get());
        //System.out.println("[ServerShip] Ship "+id+" F "+tmpVector);
        nextSpeed.set(currentSpeed.x + tmpVector.x, currentSpeed.y
                + tmpVector.y);
        //System.out.println("[ServerShip] Ship "+id+" V "+nextSpeed);
        //System.out.println(currentPosition);
        nextPosition.set(currentPosition.x + nextSpeed.x, currentPosition.y
                + nextSpeed.y);
        //System.out.println("[ServerShip] Ship "+id+" X "+nextPosition);
        command.setAcceleration(0);
        tmpAngle.set(0);
        command.setDirection(tmpAngle);

    }

    public void nextFrame() {
        currentPosition.set(nextPosition);
        currentSpeed.set(nextSpeed);
    }

    public Vector getCurrentPosition() {
        return currentPosition;
    }

    public Vector getCurrentSpeed() {
        return currentSpeed;
    }

    public Vector getNextPosition() {
        return nextPosition;
    }

    public Vector getNextSpeed() {
        return nextSpeed;
    }

    public void setNextPosition(Vector aPosition) {
        nextPosition = aPosition;
    }

    public void setNextSpeed(Vector aSpeed) {
        nextSpeed = aSpeed;
    }

    public Angle getDirection() {
        return direction;
    }

    public int getAcceleration() {
        return command.getAcceleration();
    }

    public Command getCommand() {
        return command;
    }

}
  