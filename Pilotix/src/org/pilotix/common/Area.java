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

/**
 * Contient les informations relatives � l'aire de jeu, et les m�thodes
 * d'encapsulation pour les transferts sur le r�seau.
 * 
 * <pre>
 * 
 *     |   Octet 0   | Octet 1- 6 | Octet 7-12 |...
 *     | 4bit | 4bit |  6 Octets  | 6 Octets   |
 *     | Flag |nbShip|   a Ship   |   a Ship   |...
 *  
 *  ...|   Octet  n  | Octet  n+1 |Octet n+2n+9|... 
 *     |   1 Octet   |  1  Octet  |   7 Octet  |
 *  ...|   nbBall    | DeleteBall |   NewBall  |...
 *  
 *  
 * </pre>
 */

public class Area implements Transferable {

    protected int nbMaxShips = 8; // a recuperer dans la map
    protected int nbShips;
    protected int nbBalls;
    protected IterableArray ships;
    protected IterableArray balls;
    private int lengthInByte;
    private Ship tmpShip;
    private Ball tmpBall;
    private byte[] tmpByte;
    private byte[] byteCoded = null;

    public Area() {
        byteCoded = new byte[5000];
        ships = new IterableArray(nbMaxShips);
        balls = new IterableArray(nbMaxShips);
        nbShips = 0;
        tmpShip = new Ship();
        tmpBall = new Ball();
    }

    public void set(Area anArea) {
        nbShips = anArea.nbShips;
        ships = anArea.ships;
        lengthInByte = anArea.lengthInByte;
    }

    public void setFromBytes(byte[] bytes) {

        nbShips = (byte) (bytes[0] & 15);

        int index = 1;
        tmpByte = new byte[Ship.lengthInByte];
        ships.clear(); //TODO a supprimer !!! et a tester
        for (int i = 0; i < nbShips; i++) {
            tmpByte[0] = bytes[index];
            tmpByte[1] = bytes[index + 1];
            tmpByte[2] = bytes[index + 2];
            tmpByte[3] = bytes[index + 3];
            tmpByte[4] = bytes[index + 4];
            tmpByte[5] = bytes[index + 5];

            tmpShip.setFromBytes(tmpByte);
            if (tmpShip.getStates() == Ship.REMOVE) {
                ships.remove(tmpShip.getId());
            } else if (ships.isNull(tmpShip.getId())) {
                ships.add(tmpShip.getId(), new Ship(tmpShip));
            } else {
                ((Ship) ships.get(tmpShip.getId())).set(tmpShip);
            }
            index = index + Ship.lengthInByte;
        }

        nbBalls = bytes[index];
        index++;
        for (int i = 0; i < nbBalls; i++) {
            if ((bytes[index] & 1) == 0) {
                tmpByte = new byte[Ball.lengthInByte];
                tmpByte[0] = bytes[index];
                tmpByte[1] = bytes[index + 1];
                tmpByte[2] = bytes[index + 2];
                tmpByte[3] = bytes[index + 3];
                tmpByte[4] = bytes[index + 4];
                tmpByte[5] = bytes[index + 5];
                tmpByte[6] = bytes[index + 6];
                tmpBall.setFromBytes(tmpByte);
                balls.add(tmpBall.getId(), tmpBall);
                index = index + Ball.lengthInByte;
            } else {
                byte[] b = new byte[1];
                b[0] = bytes[index];
                tmpBall.setFromBytes(b);
                index++;
            }
        }

    }

    /*
     * public void setFromBytes(byte[] bytes) {
     *
     * nbShips = (byte) (bytes[0] & 15);
     *
     * int index = 1; tmpByte = new byte[Ship.lengthInByte];
     *
     * for (int i = 0; i < nbShips; i++) { tmpByte[0] = bytes[index]; tmpByte[1] =
     * bytes[index + 1]; tmpByte[2] = bytes[index + 2]; tmpByte[3] = bytes[index +
     * 3]; tmpByte[4] = bytes[index + 4]; tmpByte[5] = bytes[index + 5];
     *
     * tmpShip.setFromBytes(tmpByte); if (tmpShip.getStates() == Ship.REMOVE) {
     * ships[tmpShip.getId()] = null; } else { if (ships[tmpShip.getId()] ==
     * null) { ships[tmpShip.getId()] = new Ship(); }
     * ships[tmpShip.getId()].set(tmpShip); } index = index + Ship.lengthInByte; } }
     */

    public byte[] getAsBytes() {

        byte[] tmp;
        byteCoded[0] = 0;
        byteCoded[0] = (byte) (Transferable.AREA << 4);
        byteCoded[0] |= (byte) ships.size();
        lengthInByte = 1;
        for (ships.setCursor1OnFirst(); ships.cursor1hasNext();) {
            tmp = ((Ship) ships.cursor1next()).getAsBytes();
            for (int j = 0; j < Ship.lengthInByte; j++) {
                byteCoded[lengthInByte + j] = tmp[j];
            }
            lengthInByte += Ship.lengthInByte;
        }
        return byteCoded;
    }

    public int getLengthInByte() {
        return lengthInByte;
    }

    /**
     * @return Returns the nbMaxShips.
     */
    public int getNbMaxShips() {
        return nbMaxShips;
    }
}