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

    /**
     * <pre>
     * |   Octet 0  | Octet 1 | Octet 2 | Octet 3 | Octet 4 |
     * |   1 Octet  | 1 Octet | 1 Octet | 1 Octet | 1 Octet |
     * |Flag COMMAND| Accele  | Directi | ToolId  | BallId  |
     * </pre>
     */
    
    private Angle direction;
    private int acceleration;
    private int toolId;
    private int ballId;
    
    public Command() {
        direction = new Angle(0);
        acceleration = 0;
        toolId = 0;
        ballId = 0;
    }

    public void set(Command aCommand) {
        direction.set((aCommand.getDirection()).get());
        acceleration = aCommand.getAcceleration();
        toolId = aCommand.getToolId();
        ballId = aCommand.getBallId();
    }

    public void setDirection(Angle aDirection) {
        direction.set(aDirection.get());
    }

    public void setAcceleration(int anAcceleration) {
        acceleration = anAcceleration;
    }

    public void setToolId(int aToolId) {
        toolId = aToolId;
    }

    public void setBallId(int anProjectileId) {
        ballId = anProjectileId;
    }

    public Angle getDirection() {
        return direction;
    }

    public int getAcceleration() {
        return acceleration;
    }

    public int getToolId() {
        return toolId;
    }

    public int getBallId() {
        return ballId;
    }
    
   
    
    public void read(MessageHandler mh){
        
        byte[] bytes = mh.receiveNBytes(4);
        acceleration = bytes[0];
        direction.set(bytes[1]*3);
        toolId = bytes[2];
        ballId = bytes[3];
    }
    
    public void write(MessageHandler mh)throws Exception{
        byte[] bytes = new byte[5];
        bytes[0]=Transferable.COMMAND;
        bytes[1]=(byte) acceleration;
        bytes[2]=(byte) (direction.get() / 3);
        bytes[3]=(byte) toolId;
        bytes[4]=(byte) ballId;
        mh.sendBytes(bytes);
    }
    
    
}