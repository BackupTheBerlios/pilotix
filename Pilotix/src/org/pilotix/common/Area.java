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
 * Contient les informations relatives à l'aire de jeu, et les méthodes
 * d'encapsulation pour les transferts sur le réseau.
 * 
 * <pre>
 * 
 *  |  Octet 0 | Octet 2- 11 |...
 *  |  1 Octet |  9 Octets   |
 *  |Flag AREA |   a Ship    |...
 *  
 *  | Octet  n+1n+13 |...
 *  |    13  Octet   |   
 *  |    Ball ADD    |...
 *  
 *  | Octet n+14n+19 |...
 *  |     5 Octet    |
 *  |    Ball REMOVE |...
 *  
 *  |  Octet 0 |
 *  |  1 Octet |
 *  |Flag AREA |
 * 
 * </pre>
 */


public class Area implements Transferable {

    protected int nbMaxShips = 8; // a recuperer dans la map
    protected int nbMaxBalls = 32; // a recuperer dans la map
    protected int nbMaxObstacles = 32; // a recuperer dans la map
    protected int nbShips;
    protected int nbBalls;
    protected IterableArray ships;
    protected IterableArray balls;
    private int lengthInByte;
    private Ship tmpShip;
    private Ball tmpBall;
    private byte[] tmpByte;
    //private byte[] byteCoded = null;

    public Area() {
        //byteCoded = new byte[5000];
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

    /**
     * @return Le nombre maximum de vaisseaux
     */
    public int getNbMaxShips() {
        return nbMaxShips;
    }

    /**
     * @return Le nombre maximum de balles
     */
    public int getNbMaxBalls() {
        return nbMaxBalls;
    }

    /**
     * @return Le nombre maximum d'obstacles
     */
    public int getNbMaxObstacles() {
        return nbMaxObstacles;
    }

    /**
     * @return le nombre de balles
     */
    public int getNbBalls() {
        return nbBalls;
    }
    
    
    public IterableArray getShips() {
        return ships;
    }

    /**
     * @return le nombre de balles
     */
    public IterableArray getBalls() {
        return balls;
    }

    public void read(MessageHandler mh) throws Exception {
        byte flag = mh.receiveOneByte();
        while (flag != Transferable.AREA) {
            if (flag == Transferable.BALL) {
                tmpBall.read(mh);
                if (tmpBall.getStates() == Ball.ADD) {
                    balls.add(tmpBall.getId(), new Ball(tmpBall));
                    nbBalls++;
                } else if (tmpBall.getStates() == Ball.REMOVE) {
                    balls.remove(tmpBall.getId());
                    nbBalls--;
                }

            } else if (flag == Transferable.SHIP) {
                tmpShip.read(mh);
                if (tmpShip.getStates() == Ship.REMOVE) {
                    ships.remove(tmpShip.getId());
                } else if (ships.isNull(tmpShip.getId())) {
                    ships.add(tmpShip.getId(), new Ship(tmpShip));
                } else {
                    ((Ship) ships.get(tmpShip.getId())).set(tmpShip);
                }
            }
            flag = mh.receiveOneByte();
        }
    }

    public void write(MessageHandler mh) throws Exception {
        mh.sendOneByte(Transferable.AREA);
        for (ships.cursor1OnFirst(); ships.cursor1IsNotNull();ships.cursor1Next())
            ((Ship) (ships.cursor1Get())).write(mh);

        for (balls.cursor1OnFirst(); balls.cursor1IsNotNull();balls.cursor1Next())
            ((Ball) (balls.cursor1Get())).write(mh);
        mh.sendOneByte(Transferable.AREA);
    }

}
