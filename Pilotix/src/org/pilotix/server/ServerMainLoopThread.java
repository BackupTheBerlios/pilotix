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

import java.util.Iterator;

public class ServerMainLoopThread extends Thread {
  
    private boolean newClientHandler = false;

    public ServerMainLoopThread() throws Exception {
    }

    public void run() {
        //Supression des clients désirant partir
        for (int i = 0; i < PilotixServer.theCHTs.size(); i++) {
            ClientHandlerThread CHT = (ClientHandlerThread) PilotixServer.theCHTs.get(i);
            int state = CHT.getState();
            switch (state) {
            // Le client veut quitter la partie => sortir
            case ClientHandlerThread.WANTTOLEAVE:
                CHT.setState(ClientHandlerThread.TOBEKILL);
                break;
            // Le client a quitté violemment
            case ClientHandlerThread.DECONNECTED:
                CHT.setState(ClientHandlerThread.TOBEKILL);
                break;
            // Supression du client ayant quitté
            case ClientHandlerThread.TOBEKILL:
                PilotixServer.theSA.removeShip(CHT.getShip());
                PilotixServer.theCHTs.remove(CHT);
                System.out.println("[ServerMainLoopThread]Running with "
                    + PilotixServer.theCHTs.size() + " player(s) ");
                break;
            }
        }
        // Ajout des nouveaux clients (en attente sur la liste temporaire)
        if (newClientHandler) {
            PilotixServer.theCHTs.addAll(PilotixServer.theNewCHTs);
            //Ajout des nouveaux vaisseaux
            for (Iterator iter = PilotixServer.theNewCHTs.iterator(); iter.hasNext();) {
                ClientHandlerThread newCHT = (ClientHandlerThread) iter.next();
                //theShips.add(newCHT.getShip());
                PilotixServer.theSA.addShip(newCHT.getShip());

            }
            //effacement de la liste temporaire
            PilotixServer.theNewCHTs.clear();

            newClientHandler = false;
            System.out.println("[ServerMainLoopThread]Running with "
                + PilotixServer.theCHTs.size() + " player(s) ");

        }

        PilotixServer.theSA.nextFrame();
        //envoye de la frame courante a tous les autre ships
        for (Iterator iter = PilotixServer.theCHTs.iterator(); iter.hasNext();) {
            ClientHandlerThread CHT = (ClientHandlerThread) iter.next();
            try {
                CHT.sendArea();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public synchronized void newClient() {
        newClientHandler = true;
    }

}