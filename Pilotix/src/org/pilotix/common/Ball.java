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
 * <pre>
 * |   Octet 0   | Octet1-2| Octet3-4| Octet 5 | Octet 6 |
 * | 4bit | 4bit | 2Octets | 2Octets | 1 Octet | 1 Octet |
 * |  Id  |States|    X    |    Y    |Vitesse X|Vitesse Y|
 * </pre>
 */
public class Ball extends PilotixElement implements Message{

    Vector speed;
    public static int lengthInByte = 7;
    private byte[] byteCoded = new byte[lengthInByte];
    
    public Ball() {
        speed = new Vector();
    }

    public void setFromBytes(byte[] bytes) {
        //flag = (byte)((bytes[0] & 240) >> 4);
        id = (byte) ((bytes[0] & 240) >> 4);
        states = (byte) (bytes[0] & 15);

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
        for (int i = 0; i < 8; i++) {
            speed.x += ((byte) (bytes[5] >> i) & 0x01) * inc;
            speed.y += ((byte) (bytes[6] >> i) & 0x01) * inc;
            inc = inc << 1;
        }
    }

    public byte[] getAsBytes() {

        byteCoded[0] = 0;
        byteCoded[0] = (byte) (id << 4);
        byteCoded[0] |= (byte) states;

        byteCoded[1] = (byte) (position.x / 256);
        byteCoded[2] = (byte) position.x;
        byteCoded[3] = (byte) (position.y / 256);
        byteCoded[4] = (byte) position.y;
        
        byteCoded[5] = (byte) speed.x;
        byteCoded[6] = (byte) speed.y;

        return byteCoded;
    }
    
    public int getLengthInByte(){
        return lengthInByte;
    }
    
}
