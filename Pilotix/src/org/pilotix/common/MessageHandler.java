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

   
    private byte[] message;
    private byte messageType;

    private Command command = new Command();
    private Information info = new Information();

    private int nbShip;
    private int byteLength;
    private Object result;
    private Area tmpArea;

    public MessageHandler(Socket aSocket) throws Exception {
        input = aSocket.getInputStream();
        output = aSocket.getOutputStream();
        message = new byte[100];
        tmpArea = new Area();
    }

    public void send(Transferable aMessage) {
        try {
        	
            output.write(aMessage.getAsBytes(), 0, aMessage.getLengthInByte());
            //System.out.println("Packet Send,"+ aMessage.getLengthInByte());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Object receive() throws Exception {

        //getByteFromInput(message, 0, 1);
        input.read(message, 0, 1);
        byte firstByte = message[0];
        byte messageType = (byte) ((firstByte & 240) >> 4);
        byte firstByteRest = (byte) (firstByte & 15);

        switch (messageType) {
        case Transferable.AREA:
            nbShip = firstByteRest;
            //getByteFromInput(message, 1, nbShip * 6);
        	input.read(message, 1, nbShip * 6);
        tmpArea.setFromBytes(message);
            result = (Object) tmpArea;
            break;
        case Transferable.COMMAND:
            input.read(message, 1, 2);
            //getByteFromInput(message, 1, 2);
            //Command aCommand = new Command();
            command.setFromBytes(message);
            result = (Object) command;
            break;
        case Transferable.INFO:
            input.read(message, 1, 1);
            //getByteFromInput(message, 1, 1);
            //Command aCommand = new Command();
            info.setFromBytes(message);
            result = (Object) info;
            break;
        default:
            System.out.println("[MessageHandler] Flag inconnu :" + messageType);
            result = null;
            break;
        }
        return result;

    }

    /*private void getByteFromInput(byte[] bytes, int off, int nbByte)
            throws Exception {
        int i = 0;
        int offset = off;
        while (offset < nbByte) {
            i = input.read(bytes, offset, nbByte - offset + off);
            if (i > 0) {
                offset += i;
            } else {
                //System.out.println("Le Client a quité sauvagement !!!");
                throw new Exception("Le client est sorti sauvagement!");
            }
        }
        //System.out.print("[offset :"+offset+"]");
    }*/
    
}
