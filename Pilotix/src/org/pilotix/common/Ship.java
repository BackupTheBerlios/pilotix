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

//import java.util.LinkedList;

public class Ship extends PilotixElement {

    public final static int ADD = 0;
    public final static int REMOVE = 1;
    public final static int NULL = 2;
    public final static int HIT = 3;
    public final static int ACCELERATING = 4;

    public static final byte bytesLength = 6;

    protected int id;
    protected int states;
    protected Vector currentPosition;
    protected Angle direction;
    protected int radius = 10;

    //private byte[] byteCoded;
    private int inc;
    private int deg;

    public Ship() {
        super();
        id = -1;
        currentPosition = new Vector(0, 0);
        direction = new Angle(0);
        states = 0;
        byteCoded = new byte[bytesLength];
    }

    /*
     * public Ship(int aShipId, Vector aPosition, Angle aDirection, int
     * theStates){ super(); id = aShipId; currentPosition = aPosition;
     * direction = aDirection; states = theStates;
     */

    /**
     * Sets the caracteristique(direction, position) of the Ship.
     * 
     * @param aShip
     *            where only pos and dir while be copied.
     */

    public void set(Ship aShip) {
        states = aShip.states;
        id = aShip.id;
        currentPosition.set(aShip.getPosition());
        direction.set(aShip.getDirection());
    }

    public void setId(int anId) {
        id = anId;
    }

    public void setPosition(Vector aPosition) {
        currentPosition = aPosition;
    }

    public void setDirection(Angle aDirection) {
        direction = aDirection;
    }

    public void setStates(int theStates) {
        states = theStates;
    }

    public int getId() {
        return id;
    }

    /**
     * Retrieves the position of the Ship.
     * 
     * @return the current position of the Ship
     */
    public Vector getPosition() {
        return currentPosition;
    }

    /**
     * Retrieves the direction of the Ship.
     * 
     * @return the current direction of the Ship
     */
    public Angle getDirection() {
        return direction;
    }

    public int getStates() {
        return states;
    }

    //methodes pour la transmission/reception du ship

    /*
     * | Octet 0 | Octet 1 | Octet 2 | Octet 3 | Octet 4 | Octet 5 | | 4bit |
     * 4bit | 2 Octets | 2 Octets | | | Id |States| X | Y | Direction |
     */

    public void setFromBytes(byte[] bytes) {
        //flag = (byte)((bytes[0] & 240) >> 4);
        id = (byte) ((bytes[0] & 240) >> 4);
        states = (byte) (bytes[0] & 15);

        currentPosition.x = 0;
        currentPosition.y = 0;

        inc = 1;
        for (int i = 0; i < 8; i++) {
            currentPosition.x += ((byte) (bytes[2] >> i) & 0x01) * inc;
            currentPosition.y += ((byte) (bytes[4] >> i) & 0x01) * inc;
            inc = inc << 1;
        }
        inc = 256;
        for (int i = 0; i < 8; i++) {
            currentPosition.x += ((byte) (bytes[1] >> i) & 0x01) * inc;
            currentPosition.y += ((byte) (bytes[3] >> i) & 0x01) * inc;
            inc = inc << 1;
        }

        inc = 1;
        deg = 0;
        for (int i = 0; i < 8; i++) {
            deg += ((byte) (bytes[5] >> i) & 0x01) * inc;
            inc = inc << 1;
        }
        direction.set(deg * 2);
    }

    public byte[] getAsBytes() {

        byteCoded[0] = 0;
        byteCoded[0] = (byte) (id << 4);
        byteCoded[0] |= (byte) states;

        byteCoded[1] = (byte) (currentPosition.x / 256);
        byteCoded[2] = (byte) currentPosition.x;
        byteCoded[3] = (byte) (currentPosition.y / 256);
        byteCoded[4] = (byte) currentPosition.y;
        byteCoded[5] = (byte) (direction.get() / 2);

        return byteCoded;
    }
}
  