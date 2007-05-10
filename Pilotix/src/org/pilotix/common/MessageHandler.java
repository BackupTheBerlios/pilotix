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
import java.net.SocketException;

/**
 * Commentaire à refaire.
 */
public class MessageHandler {

    private InputStream input;
    private OutputStream output;

    public MessageHandler(Socket aSocket) throws Exception {
        input = aSocket.getInputStream();
        output = aSocket.getOutputStream();
    }

    public void close() {
        try {
            input.close();
            output.close();
        }
        catch (Exception e) {
            System.out.println("MessageHandler.close() - EXCEPTION");
        }
    }

    public void sendBytes(byte[] bytes) throws Exception{
        output.write(bytes, 0, bytes.length);
    }

    public void sendOneByte(byte aByte) throws Exception {
        byte[] bytes = { aByte};
            output.write(bytes, 0, 1);
    }

    public byte receiveOneByte() throws Exception  {
        byte[] result = new byte[1];
        input.read(result, 0, 1);
        return result[0];
    }

    public byte[] receiveNBytes(int nbByte) {
        byte[] result = new byte[nbByte];
        try {
            input.read(result, 0, nbByte);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }
}
