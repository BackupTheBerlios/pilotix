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

package org.pilotix.server;

import org.pilotix.common.*;

//import java.util.LinkedList;
import java.net.ServerSocket;
import java.net.Socket;

public class ConnexionHandlerThread extends Thread {

    ServerSocket serverSocket;

    public ConnexionHandlerThread(int aPort) throws Exception {
        //System.out.println("[ConnexionHandler] Launched ");
        serverSocket = new ServerSocket(aPort);
    }

    public void run() {
        try {
            while (true) {
                //PilotixServer.theIH.needToWait();
                Socket socket = serverSocket.accept();
                //System.out.println(socket.getInetAddress()+":"+socket.getPort());
                new ClientHandlerThread(PilotixServer.theIH.getId(),
                        new MessageHandler(socket)).start();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /*
     * public int getId(){ if (nbmax == nb){ return -1; } int i = 0;
     * while(tab[i] == true){ i++; } tab[i] = true; nb++;
     * System.out.println("Nb players :"+nb); return i; }
     * 
     * public void giveBackId(int indice) { tab[indice] = false; nb--;
     * System.out.println("players "+indice+" give back his Id");
     * System.out.println("Nb players :"+nb);
     */
}
