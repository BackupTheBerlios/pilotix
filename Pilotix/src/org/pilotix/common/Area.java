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

import org.pilotix.server.ServerBall;

/**
 * Contient les informations relatives à l'aire de jeu, et les méthodes
 * d'encapsulation pour les transferts sur le réseau.
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
        balls = new IterableArray(nbMaxShips); // LIMITE : 1 BALL PAR SHIP
        nbShips = 0;
        tmpShip = new Ship();
        tmpBall = new Ball();
    }

    public void set(Area anArea) {
        nbShips = anArea.nbShips;
        ships = anArea.ships;
        nbBalls = anArea.nbBalls;
        balls = anArea.balls;
        lengthInByte = anArea.lengthInByte;
    }

    public void setFromBytes(byte[] bytes) {

        nbShips = (byte) (bytes[0] & 15);

        int index = 1;
        tmpByte = new byte[Ship.lengthInByte];
        //ships.clear(); //TODO a supprimer !!! et a tester
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

        
        int nbBallUpdate=0;
        nbBallUpdate = bytes[index];
        
        index++;
        for (int i = 0; i < nbBallUpdate; i++) {
            if ((bytes[index] & 1) == 0) {
                tmpByte = new byte[Ball.lengthInByte];
                tmpByte[0] = bytes[index];
                tmpByte[1] = bytes[index + 1];
                tmpByte[2] = bytes[index + 2];
                tmpByte[3] = bytes[index + 3];
                tmpByte[4] = bytes[index + 4];
                tmpByte[5] = bytes[index + 5];
                tmpByte[6] = bytes[index + 6];
                tmpByte[7] = bytes[index + 7];
                tmpByte[8] = bytes[index + 8];
                tmpBall.setFromBytes(tmpByte);
                System.out.println("Ajout de la balle d'id=" + tmpBall.getId());
                nbBalls++;
                if (balls.isNull(tmpBall.getId())) {
                    balls.add(tmpBall.getId(), tmpBall);
                } else {
                    System.out.println("Pb tentative d'ajout a la meme place");
                }
                index = index + Ball.lengthInByte;
            } else {
                byte[] b = new byte[1];
                b[0] = bytes[index];
                tmpBall.setFromBytes(b);
                balls.remove(tmpBall.getId());
                index++;
                nbBalls--;
            }
        }
        //System.out.println("nb balls=" + nbBalls);

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

        int shipToAdd = 0;
        for (balls.setCursor1OnFirst(); balls.cursor1hasNext();) {
            int state = ((ServerBall) (balls.cursor1next())).getState();
            if ((state == ServerBall.ADD) || (state == ServerBall.REMOVE)) {
                shipToAdd++;
            }

        }
            byteCoded[lengthInByte] = (byte) shipToAdd;
            lengthInByte++;
        if (shipToAdd != 0) {
            for (balls.setCursor1OnFirst(); balls.cursor1hasNext();) {
                tmp = ((Ball) balls.cursor1next()).getAsBytes();
                if (tmp != null) {
                    for (int j = 0; j < tmp.length; j++) {
                        byteCoded[lengthInByte + j] = tmp[j];
                    }
                    lengthInByte += tmp.length;
                }
            }
        }
        /*if(balls.size()!=0){
         System.out.print("||");
         for(int i=0;i<byteCoded.length;i++){
         System.out.print(byteCoded[i]+",");                
         }
         System.out.println("||");            
         }*/

        return byteCoded;
    }

    public int getLengthInByte() {
        //System.out.println("taille total="+lengthInByte);
        return lengthInByte;
    }

    /**
     * @return Returns the nbMaxShips.
     */
    public int getNbMaxShips() {
        return nbMaxShips;
    }

    /**
     * @return le nombre de balles
     */
    public int getNbBalls() {
        return nbBalls;
    }
}