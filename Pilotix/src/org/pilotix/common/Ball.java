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
 * Contient les informations relatives � une balle,
 * et les m�thodes d'encapsulation pour les transferts
 * sur le r�seau.
 *
 */
public class Ball extends PilotixElement implements Transferable {

    /**
     * Message de creation de Ball
     * <pre>
     * |      Octet   0     | Octet1-2| Octet3-4| Octet5-6 | Octet7-8 |
     * | 4bit | 3bit | 1bit | 2Octets | 2Octets | 2 Octet  | 2 Octet  |
     * |ShipID|BallID|   0  |    X    |    Y    |Vitesse X |Vitesse Y |
     * </pre>
     */
    public static final int ADD = 1;

    /**
     * Message de destruction de Ball
     * <pre>
     * |      Octet   0     |
     * | 4bit | 3bit | 1bit |
     * |ShipID|BallID|   1  |
     * </pre>
     *
     */
    public static final int REMOVE = 4;

    protected Vector speed = new Vector();
    public static int lengthInByte = 9;
    private byte[] byteCoded = new byte[lengthInByte];
    private int radius = 100;

    public Ball( int id, Vector aPosition, Vector aSpeed) {
        super();
        position.set(aPosition);
        speed.set(aSpeed);
        states = ADD;
        this.id = id;
    }

    public Ball() {
        super();
    }

    public void setFromBytes(byte[] bytes) {
        //flag = (byte)((bytes[0] & 240) >> 4);
        id = (byte) ((bytes[0] & 14) >> 1);
        states = (byte) (bytes[0] & 1);
        //if ((byte) (bytes[0] & 1)== 0){
        //    states = ADD;
        //}
        if (bytes.length > 1) {
            position.x = 0;
            position.y = 0;

            int inc = 1;
            for (int i = 0; i < 8; i++) {
                position.x += ((byte) (bytes[2] >> i) & 0x01) * inc;
                position.y += ((byte) (bytes[4] >> i) & 0x01) * inc;
                inc = inc << 1;
            }
            inc = 256;
            for (int i = 0; i < 8; i++) {
                position.x += ((byte) (bytes[1] >> i) & 0x01) * inc;
                position.y += ((byte) (bytes[3] >> i) & 0x01) * inc;
                inc = inc << 1;
            }

            inc = 1;
            
            speed.x = 0;
            speed.y = 0;
            for (int i = 0; i < 8; i++) {
                speed.x += ((byte) (bytes[6] >> i) & 0x01) * inc;
                speed.y += ((byte) (bytes[8] >> i) & 0x01) * inc;
                inc = inc << 1;
            }
            
            inc = 256;
            for (int i = 0; i < 7; i++) {
                speed.x += ((byte) (bytes[5] >> i) & 0x01) * inc;
                speed.y += ((byte) (bytes[7] >> i) & 0x01) * inc;
                inc = inc << 1;
            }
            
            if(((byte) (bytes[5] >> 7) & 0x01)==1){
                speed.x *=-1;
            }
            
            if(((byte) (bytes[7] >> 7) & 0x01)==1){
                speed.y *=-1;
            }
        }
    }

    public byte[] getAsBytes() {

        if (states == ADD) {
            byteCoded[0] = 0;
            byteCoded[0] = (byte) (id << 3);
            byteCoded[0] |= (byte) 0;

            byteCoded[1] = (byte) (position.x / 256);
            byteCoded[2] = (byte) position.x;
            byteCoded[3] = (byte) (position.y / 256);
            byteCoded[4] = (byte) position.y;

            
            byteCoded[5] = (byte) (Math.abs(speed.x)/256);
            byteCoded[6] = (byte) (Math.abs(speed.x) % 256);            
            byteCoded[7] = (byte) (Math.abs(speed.y)/256);
            byteCoded[8] = (byte) (Math.abs(speed.y) % 256);
            
            if(speed.x<0){
                byteCoded[5]|= 0x80;                
            }
            if(speed.y<0){
                byteCoded[7]|= 0x80;                
            }
           
            return byteCoded;
        } else if (states == REMOVE) {
            return new byte[] { (byte) ((id << 3) + 1)};
        } else {
            return null;
        }
    }

    public int getLengthInByte() {
        return lengthInByte;
    }

    public void nextFrame() {        
        position.set(position.plus(speed));
        //System.out.println("position Ball=" + position+" Speed="+speed);
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
}