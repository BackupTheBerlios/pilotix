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

//import org.pilotix.common.*;

//import java.net.Socket;
import java.util.LinkedList;
//import java.util.Iterator;
//import java.io.OutputStream;

public class ServerMainLoopThread extends Thread {

    private int i = 0;
    private ServerArea theArea;
    private boolean newClientHandler = false;

    private LinkedList theShips;

    private byte[] bytes;
    private int bytesLength;

    public ServerMainLoopThread() throws Exception {
        System.out.println("[ServerMainLoopThread] is Started ");
        theArea = PilotixServer.theSA;
        theShips = theArea.getShips();

        PilotixServer.theCHTs = new LinkedList();
        PilotixServer.theNewCHTs = new LinkedList();

    }

    public void run() {

        //Supression des Clients desirant partir de la list des client
        for (int i = 0; i < PilotixServer.theCHTs.size(); i++) {
            int state = ((ClientHandlerThread) PilotixServer.theCHTs.get(i))
                    .getState();
            switch (state) {
            // Le client a quite la partie sortir
            case ClientHandlerThread.WANTTOLEAVE:
                ((ClientHandlerThread) PilotixServer.theCHTs.get(i))
                        .setState(ClientHandlerThread.TOBEKILL);
                break;
            // Le client a quitte violament
            case ClientHandlerThread.DECONNECTED:
                ((ClientHandlerThread) PilotixServer.theCHTs.get(i))
                        .setState(ClientHandlerThread.TOBEKILL);
                break;
            // Supression du client ayant quite
            case ClientHandlerThread.TOBEKILL:
                theShips.remove(((ClientHandlerThread) PilotixServer.theCHTs
                        .get(i)).getShip());
                Object toto = PilotixServer.theCHTs.get(i);
                PilotixServer.theCHTs.remove(toto);
                System.out.println("Ship id :"
                        + (((ClientHandlerThread) toto).getShip()).getId()
                        + " has been deleted");
                System.out
                        .println("[ServerMainLoopThread] is Now Running with "
                                + PilotixServer.theCHTs.size() + " player(s) ");
                break;
            }
        }
        //Ajout des nouveaux clients en attente sur la liste temporaire,
        //a la liste des Client principale,
        if (newClientHandler) {
            PilotixServer.theCHTs.addAll(PilotixServer.theNewCHTs);
            //Ajout des nouveaux ships 
            //et affectation et envoie au clients des numero de ship
            for (int i = 0; i < PilotixServer.theNewCHTs.size(); i++) {
                theShips.add(((ClientHandlerThread) (PilotixServer.theNewCHTs
                        .get(i))).getShip());
            }
            //effacement de la liste temporaire
            PilotixServer.theNewCHTs.clear();

            newClientHandler = false;
            System.out.println("[ServerMainLoopThread] is Now Running with "
                    + PilotixServer.theCHTs.size() + " player(s) ");

        }
        //lancement des calculs des resolution de collision et passage a la
        // frame suivante
        theArea.nextFrame();

        /*
         * switch (i){ case 0 : System.out.print(" \\ \r");break; case 1 :
         * System.out.print(" | \r");break; case 2 : System.out.print(" /
         * \r");break; case 3 : System.out.print(" - \r");break; } i++;
         */

        //recuperation via area de l'ensemble des ship :
        //bytes = theArea.getAsBytes();
        //bytesLength = theArea.getLengthInByte();
        //envoye de la frame courante a tous les autre ships

        for (int i = 0; i < PilotixServer.theCHTs.size(); i++) {
            try {
                //((ClientHandlerThread) PilotixServer.theCHTs.get(i)).send(bytes,
                //        bytesLength);
                ((ClientHandlerThread) PilotixServer.theCHTs.get(i)).sendArea();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public synchronized void newClient() {
        newClientHandler = true;
    }

}
