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

import java.util.LinkedList;

import org.pilotix.common.Angle;
import org.pilotix.common.Command;
import org.pilotix.common.IterableArray;
import org.pilotix.common.Ship;
import org.pilotix.common.Vector;

public class ServerShip extends Ship {

    protected LinkedList forces;

    protected Vector nextPosition;
    protected Vector nextSpeed;

    private Vector tmpVector;
    private Angle tmpAngle;

    protected int radius = 300;
    private byte FlagId = 7;

    protected Command command;

	private String name;

    public ServerShip() {
        super();
        nextPosition = new Vector(0, 0);
        nextSpeed = new Vector(0, 0);
        speed = new Vector(0, 0);
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
        position.set(aPosition);
        direction.set(aDirection);
    }

    public void setCommand(Command aCommand) {
        command.set(aCommand);
    }

    public void addCommand(Command aCommand) {
        tmpAngle.set((command.getDirection()).floatValue()
            + ((aCommand.getDirection()).floatValue()));
        command.setDirection(tmpAngle);
        command.setAcceleration(aCommand.getAcceleration());
        command.setToolId(aCommand.getToolId());
        command.setBallId(aCommand.getBallId());
    }

    public void computeSpeedFromForces() {
        ((ServerAngle) direction).plus(command.getDirection());
        tmpVector.set(
            (int) (((ServerAngle) direction).getX() * command.getAcceleration() * 10),
            (int) (((ServerAngle) direction).getY() * command.getAcceleration() * 10));
        nextSpeed.set(speed.x + tmpVector.x, speed.y + tmpVector.y);
        nextPosition.set(position.x + nextSpeed.x, position.y + nextSpeed.y);
        command.setAcceleration(0);
        tmpAngle.set(0);
        command.setDirection(tmpAngle);

    }

    public void nextFrame() {
        position.set(nextPosition);
        speed.set(nextSpeed);
    }

    public Vector getSpeed() {
        return speed;
    }

    public Vector getNextPosition() {
        return nextPosition;
    }

    public Vector getNextSpeed() {
        return nextSpeed;
    }

    public void setNextPosition(Vector aPosition) {
        nextPosition.set(aPosition);
    }

    public void setNextSpeed(Vector aSpeed) {
        nextSpeed.set(aSpeed);
    }

    public int getAcceleration() {
        return command.getAcceleration();
    }

    public Command getCommand() {
        return command;
    }

    public void commandPilotixElement(IterableArray balls) {
        if (command.getToolId() == 1) {
            command.setToolId(0);
            if (balls.isNull(id)) {
                balls.add(
                    id,
                    new ServerBall(
                        id,
                        position.plus(
                            (int) (((ServerAngle) direction).getX() * (double) radius),
                            (int) (((ServerAngle) direction).getY() * (double) radius)),
                        speed.plus(
                            (int) (((ServerAngle) direction).getX() * (double) 200),
                            (int) (((ServerAngle) direction).getY() * (double) 200))));
            } else {

            }
        }

    }

	public void setName(String shipName) {
		name = shipName;
	}
	
	public String getName() {
		return name;
	}
}