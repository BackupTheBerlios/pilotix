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
 * Contient les informations relatives à une balle,
 * et les méthodes d'encapsulation pour les transferts
 * sur le réseau.
 *
 */
public class Ball extends PilotixElement implements Transferable {

    /**
     * Message de creation de Ball
     *
     * <pre>
     *
     * | Octet 0 |  Octet1  | Octet2  | Octet3  | Octet4-5 | Octet6-7 | Octet8-9  | Octet10-11|
     * | 1 Octet |  1 Octet | 1 Octet | 1 Octet | 2Octets  | 2Octets  | 2 Octet   | 2 Octet   |
     * |flag BALL|states ADD| ShipID  | BallID  |    X     |    Y     | Vitesse X | Vitesse Y |
     *
     * </pre>
     */
    public static final int ADD = 1;

    /**
     * Message de destruction de Ball
     *
     * <pre>
     *
     * | Octet 0 |    Octet1    | Octet2  | Octet3  |
     * | 1 Octet |    1 Octet   | 1 Octet | 1 Octet |
     * |flag BALL|states REMOVE | ShipID  | BallID  |
     *
     * </pre>
     */
    public static final int REMOVE = 4;
    
   
    protected Vector speed = new Vector();
    public static int lengthInByte = 9;
    private int radius = 100;

    public Ball() {
        super();
    }

    public Ball(int id, Vector aPosition, Vector aSpeed) {
        super(id, ADD, aPosition);
        speed.set(aSpeed);
    }

    public Ball(Ball aBall) {
        this(aBall.getId(), aBall.getPosition(), aBall.getSpeed());
    }
    
    /**
     * @return le rayon d'action
     */
    public int getRadius() {
        return radius;
    }

    public Vector getSpeed() {
        return speed;
    }

    

    public void read(MessageHandler mh) throws Exception{
        byte[] bytes = null;
        states = mh.receiveOneByte();
        if (states == ADD) {

            bytes = mh.receiveNBytes(10);

            //shipId = bytes[0];
            id = bytes[1];

            position.x = 0;
            position.y = 0;

            int inc = 1;
            for (int i = 0; i < 8; i++) {
                position.x += ((byte) (bytes[3] >> i) & 0x01) * inc;
                position.y += ((byte) (bytes[5] >> i) & 0x01) * inc;
                inc = inc << 1;
            }
            inc = 256;
            for (int i = 0; i < 8; i++) {
                position.x += ((byte) (bytes[2] >> i) & 0x01) * inc;
                position.y += ((byte) (bytes[4] >> i) & 0x01) * inc;
                inc = inc << 1;
            }

            inc = 1;

            speed.x = 0;
            speed.y = 0;
            for (int i = 0; i < 8; i++) {
                speed.x += ((byte) (bytes[7] >> i) & 0x01) * inc;
                speed.y += ((byte) (bytes[9] >> i) & 0x01) * inc;
                inc = inc << 1;
            }

            inc = 256;
            for (int i = 0; i < 7; i++) {
                speed.x += ((byte) (bytes[6] >> i) & 0x01) * inc;
                speed.y += ((byte) (bytes[8] >> i) & 0x01) * inc;
                inc = inc << 1;
            }

            if (((byte) (bytes[6] >> 7) & 0x01) == 1) {
                speed.x *= -1;
            }

            if (((byte) (bytes[8] >> 7) & 0x01) == 1) {
                speed.y *= -1;
            }

            
        } else if (states == REMOVE) {
            // on ne fait rien la ball sera detuit
            bytes = mh.receiveNBytes(2);
            //shipId = bytes[0];
            id = bytes[1];
        }
    }

    public void write(MessageHandler mh) throws Exception{
        byte[] bytes = null;
        if (states == ADD) {
            bytes = new byte[12];
            bytes[0] = Transferable.BALL;
            bytes[1] = (byte) states;
            bytes[2] = (byte) id;//shipId;
            bytes[3] = (byte) id;

            bytes[4] = (byte) (position.x / 256);
            bytes[5] = (byte) position.x;
            bytes[6] = (byte) (position.y / 256);
            bytes[7] = (byte) position.y;

            bytes[8] = (byte) (Math.abs(speed.x) / 256);
            bytes[9] = (byte) (Math.abs(speed.x) % 256);
            bytes[10] = (byte) (Math.abs(speed.y) / 256);
            bytes[11] = (byte) (Math.abs(speed.y) % 256);

            if (speed.x < 0) {
                bytes[8] |= 0x80;
            }
            if (speed.y < 0) {
                bytes[10] |= 0x80;
            }
            
            mh.sendBytes(bytes);

        } else if (states == REMOVE) {
            bytes = new byte[4];
            bytes[0] = Transferable.BALL;
            bytes[1] = (byte) states;
            bytes[2] = (byte) id;//shipId;
            bytes[3] = (byte) id;

            
            mh.sendBytes(bytes);

        } else {
            //on n'envoie rien
        }
    }

    public void nextFrame() {
        position.set(position.plus(speed));
        //System.out.println("position Ball=" + position+" Speed="+speed);
    }


}
