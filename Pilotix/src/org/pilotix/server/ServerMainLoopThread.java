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
import java.util.LinkedList;

public class ServerMainLoopThread extends Thread {

    private boolean newClientHandler = false;
    private LinkedList theShips = PilotixServer.theSA.getShips();

    public ServerMainLoopThread() throws Exception {
    }

    public void run() {

        //Supression des Clients desirant partir de la list des client
        for(int i=0;i<PilotixServer.theCHTs.size();i++){        
            ClientHandlerThread CHT = (ClientHandlerThread) PilotixServer.theCHTs.get(i);
            int state = CHT.getState();
            switch (state) {
            // Le client a quite la partie sortir
            case ClientHandlerThread.WANTTOLEAVE:
                CHT.setState(ClientHandlerThread.TOBEKILL);
                break;
            // Le client a quitte violament
            case ClientHandlerThread.DECONNECTED:
                CHT.setState(ClientHandlerThread.TOBEKILL);
                break;
            // Supression du client ayant quite
            case ClientHandlerThread.TOBEKILL:
                theShips.remove(CHT.getShip());
                Object toto = CHT;
                PilotixServer.theCHTs.remove(toto);
                System.out.println("[ServerMainLoopThread] is Now Running with "
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
            for(Iterator iter = PilotixServer.theNewCHTs.iterator();iter.hasNext();){
                ClientHandlerThread newCHT = (ClientHandlerThread) iter.next();          
                theShips.add(newCHT.getShip());
            }
            //effacement de la liste temporaire
            PilotixServer.theNewCHTs.clear();

            newClientHandler = false;
            System.out.println("[ServerMainLoopThread] is Now Running with "
                + PilotixServer.theCHTs.size() + " player(s) ");

        }
        
        PilotixServer.theSA.nextFrame();
        //envoye de la frame courante a tous les autre ships
            for (Iterator iter = PilotixServer.theCHTs.iterator(); iter.hasNext();) {
        //Iterator iter3 = PilotixServer.theCHTs.iterator();
       // while (iter3.hasNext()) {
            ClientHandlerThread CHT = (ClientHandlerThread) iter.next();
            try {
                CHT.sendArea();
            } catch (Exception e) {
                //e.printStackTrace();
            }
        }
    }

    public synchronized void newClient() {
        newClientHandler = true;
    }

}