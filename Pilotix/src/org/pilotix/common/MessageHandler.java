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
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

/**
 * This class aim to transport Object via the TCPSocket sendAnObject() methodes
 * transforme AnObject into byte[] and send it thru the socket
 * 
 * 
 * getAnObject() methodes get anObject previously received by receiveMessage()
 * as byte[]
 */
public class MessageHandler {

    private InputStream input;
    private OutputStream output;

    //  message type for server to client
    //public static final byte FRAMEINFO = 4;
    //public static final byte OWNSHIPINFO = 9;

    // message type for client to server
    //public static final byte COMMAND = 10;
    //public static final byte SESSION = 11;

    private byte firstByte;
    private byte firstByteRest;
    private byte[] message;

    private byte messageType;

    private Angle direction;

    private byte frameInfoCode;

    //private Ship ship;
    private Command command;
    private int ownShipId;
    private int sessionCode;
    private int nbShip;
    private int byteLength;

    /**
     * while open the output inout stream of the socket
     */
    public MessageHandler(Socket aSocket) throws Exception {
        input = aSocket.getInputStream();
        output = aSocket.getOutputStream();
        message = new byte[100];
        direction = new Angle(0);
        //ship = new Ship();
        command = new Command();
    }

    public void sendBytes(byte[] anArea, int aSize) throws Exception {
        //System.out.print("[High]"+(byte)((anArea[0] & 240) >> 4));
        //System.out.print("[nb ship]"+(byte)(anArea[0] & 15));
        //System.out.println("[Size]"+aSize);
        for (int i = 0; i < aSize; i++) {
            message[i] = anArea[i];
        }
        output.write(message, 0, aSize);
    }

    public byte[] receiveBytes() {
        return message;
    }

    public void send(Message aMessage) {
        try {
            
        output.write(aMessage.getAsBytes(), 0,
                aMessage.getLengthInByte());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Object receive() throws Exception {

        getByteFromInput(message, 0, 1);
        firstByte = message[0];
        messageType = (byte) ((firstByte & 240) >> 4);
        firstByteRest = (byte) (firstByte & 15);

        Object result;
        switch (messageType) {
        case Message.AREA:
            nbShip = firstByteRest;
            getByteFromInput(message, 1, nbShip * 6);
            Area area = new Area();
            area.setFromBytes(message);
            result = (Object) area;
            break;
        case Message.COMMAND:           
            getByteFromInput(message, 1, 2);            
            //Command aCommand = new Command();
            command.setFromBytes(message);
            result = (Object) command;
            break;
        default:
            System.out.println("[MessageHandler] Flag inconnu :" + messageType);
            result = null;
            break;
        }
        return result;

    }

    /**
     * This call is blocking use getXXX() to retrives the content
     * 
     * @return the message'code in order to identifie type of message
     */
    public byte receiveMessage() throws Exception {

        getByteFromInput(message, 0, 1);
        firstByte = message[0];
        messageType = (byte) ((firstByte & 240) >> 4);
        firstByteRest = (byte) (firstByte & 15);

        //System.out.print("[High]"+(byte)((message[0] & 240) >> 4));
        //System.out.println("[nb ship]"+(byte)(message[0] & 15));

        switch (messageType) {
        case Message.AREA:
            nbShip = firstByteRest;
            getByteFromInput(message, 1, nbShip * 6);
            break;
        /*case SHIP:
         ship.setId(firstByteRest);
         getByteFromInput(message, 0, 1);
         ship.setStates((byte) ((message[0] & 240) >> 4));
         getByteFromInput(message, 0, 4);
         extractXandY(message);
         getByteFromInput(message, 0, 1);
         int inc = 1;
         int deg = 0;
         for (int i = 0; i < 8; i++) {
         deg += ((byte) (message[0] >> i) & 0x01) * inc;
         inc = inc << 1;
         }
         direction.set(deg * 2);
         ship.setDirection(direction);
         break;*/
        case Message.COMMAND:
            command.setAcceleration(firstByteRest);
            getByteFromInput(message, 0, 2);
            //getByteFromInput(message, 0, 3);
            direction.set(message[0] * 3);
            command.setDirection(direction);
            command.setAccessory(((message[1] & 240) >> 4));
            command.setProjectileId((message[1] & 15));
            //command.setProjectileId((message[2] & 15));
            break;
        case Message.INFO:
            ownShipId = firstByteRest;
            break;
        case Message.SESSION:
            sessionCode = firstByteRest;
            break;
        default:
            System.out.println("[MessageHandler] Flag inconnu :" + messageType);
            break;
        }
        return messageType;
    }

    /*
     * public void sendFRAMEINFOMessage(byte anFrameInfoCode) throws Exception{
     * message[0] = 0; message[0] = (byte)(FRAMEINFO < < 4); message[0] |=
     * anFrameInfoCode; output.write(message,0,1);
     */

    /*public void sendSHIPMessage(Ship aShip) throws Exception {
     message[0] = 0;
     message[0] = (byte) (SHIP << 4);
     message[0] |= (byte) aShip.getId();
     message[1] = 0;
     message[1] = (byte) (aShip.getStates() << 4);

     message[2] = (byte) (aShip.getPosition().x / 256);
     //System.out.println("High X"+(byte)aShip.getPosition().x/256);
     //System.out.println("Low X"+(byte)aShip.getPosition().x);

     message[3] = (byte) aShip.getPosition().x;
     message[4] = (byte) (aShip.getPosition().y / 256);
     message[5] = (byte) aShip.getPosition().y;
     //System.out.println(aShip.getPosition().y);

     message[6] = (byte) (aShip.getDirection().get() / 2);
     output.write(message, 0, 7);
     }*/

    public void sendCOMMANDMessage(Command aCommand) throws Exception {
        message[0] = 0;
        message[0] = (byte) (Message.COMMAND << 4);
        message[0] |= (byte) aCommand.getAcceleration();
        message[1] = (byte) (aCommand.getDirection().get() / 3);
        message[2] = 0;
        message[3] = (byte) (aCommand.getAccessory() << 4);
        message[3] |= (byte) aCommand.getProjectileId();
        output.write(message, 0, 4);
    }

    public void sendOWNSHIPINFOMessage(int ownShipId) throws Exception {
        message[0] = 0;
        message[0] = (byte) (Message.INFO << 4);
        message[0] |= (byte) ownShipId;
        output.write(message, 0, 1);
    }

    public void sendSESSIONMessage(int aSessionCode) throws Exception {
        message[0] = 0;
        message[0] = (byte) (Message.SESSION << 4);
        message[0] |= (byte) aSessionCode;
        output.write(message, 0, 1);
    }

    private void getByteFromInput(byte[] bytes, int off, int nbByte)
            throws Exception {
        int i = 0;
        int offset = off;
        while (offset < nbByte) {
            i = input.read(bytes, offset, nbByte - offset + off);
            if (i > 0) {
                offset += i;
            } else {
                //System.out.println("Le Client a quité sauvagement !!!");
                Exception e = new Exception();
                throw e;
            }
        }
        //System.out.print("[offset :"+offset+"]");
    }

    /*private void extractXandY(byte[] bytes) {
     int x = 0;
     int y = 0;
     
     //  int inc = 1; for (int i=0;i <8;i++){ inc; inc; inc = inc < < 1; }
     //inc = 256; inc; inc; inc = 512; inc; inc;
     

     int inc = 1;
     for (int i = 0; i < 8; i++) {
     x += ((byte) (message[1] >> i) & 0x01) * inc;
     y += ((byte) (message[3] >> i) & 0x01) * inc;
     inc = inc << 1;
     }
     inc = 256;
     for (int i = 0; i < 8; i++) {
     x += ((byte) (message[0] >> i) & 0x01) * inc;
     y += ((byte) (message[2] >> i) & 0x01) * inc;
     inc = inc << 1;
     }

     

     ship.setPosition(new Vector(x, y));
     //System.out.println(x);
     //System.out.println(y);
     }*/

    /*public Ship getShip() {
     return ship;
     }*/

    public byte getFrameInfoCode() {
        return frameInfoCode;
    }

    public Command getCommand() {
        return command;
    }

    public int getOwnShipId() {
        return ownShipId;
    }

    /*public int getSessionCode() {
     return sessionCode;
     }*/
}