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

public class Command implements Transferable {

    public static int lengthInByte = 3;
    private byte[] byteCoded = new byte[lengthInByte];
    private Angle direction;
    private int acceleration;
    private int accessory;
    private int projectileId;
    
    
    private int velocity;
    private int toolId;
    private int ballId;

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
    
    
    // |   Octet 0   | Octet 1 |   Octet 2   |
    // | 4bit | 4bit | 1 Octet | 4bit | 4bit |
    // | Flag |Accele| Directi |ToolId|BallId|
    
    
    
    public void setFromBytes(byte[] bytes) {
        
        
       /* command.setAcceleration(firstByteRest);
        getByteFromInput(message, 0, 3);
        direction.set(message[0] * 3);
        command.setDirection(direction);
        command.setAccessory(((message[1] & 240) >> 4));
        command.setProjectileId((message[2] & 15));*/
        
        
        acceleration = (bytes[0] & 15);
        direction.set(bytes[1] * 3);
        accessory = ((bytes[2] & 240) >> 4);
        projectileId = ((bytes[3] & 15));
        
        //flag = (byte)((bytes[0] & 240) >> 4);
       /* id = (byte) ((bytes[0] & 240) >> 4);
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
        }*/
    }

    public byte[] getAsBytes() {
        
        byteCoded[0] = 0;
        byteCoded[0] = (byte) (Transferable.COMMAND << 4);
        byteCoded[0] |= (byte) acceleration;
        byteCoded[1] = (byte) (direction.get() / 3);
        byteCoded[2] = 0;
        byteCoded[2] = (byte) (accessory << 4);
        byteCoded[2] |= (byte) projectileId;
        
        /*message[0] = 0;
        message[0] = (byte) (COMMAND << 4);
        message[0] |= (byte) aCommand.getAcceleration();
        message[1] = (byte) (aCommand.getDirection().get() / 3);
        message[2] = 0;
        message[3] = (byte) (aCommand.getAccessory() << 4);
        message[3] |= (byte) aCommand.getProjectileId();
        output.write(message, 0, 4);*/
        

        /*byteCoded[0] = 0;
        byteCoded[0] = (byte) (id << 4);
        byteCoded[0] |= (byte) states;

        byteCoded[1] = (byte) (position.x / 256);
        byteCoded[2] = (byte) position.x;
        byteCoded[3] = (byte) (position.y / 256);
        byteCoded[4] = (byte) position.y;
        
        byteCoded[5] = (byte) speed.x;
        byteCoded[6] = (byte) speed.y;
        */
        return byteCoded;
    }
    
    public int getLengthInByte(){
        return lengthInByte;
    }
}