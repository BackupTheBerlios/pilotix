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

import org.pilotix.common.Information;

public class ServerMainLoopThread extends Thread {

	//used to compute statistics
	Long lastTime ;
	Integer nbTime ;
	Integer messageEvery = 30 ;
	Long total =0l;
	
    private boolean newClientHandler = false;

    private Information info = new Information();
    
    public ServerMainLoopThread() throws Exception {
    	lastTime = System.currentTimeMillis();
    	nbTime = 0;
    }

    public void run() {
    	
        //Supression des clients d�sirant partir
        for (int i = 0; i < PilotixServer.theCHTs.size(); i++) {
            ClientHandlerThread CHT = (ClientHandlerThread) PilotixServer.theCHTs.get(i);
            int status = CHT.getStatus();
            switch (status) {
            // Le client veut quitter la partie => sortir
            case ClientHandlerThread.WANTTOLEAVE:
                CHT.setStatus(ClientHandlerThread.TOBEKILL);
                break;
            // Le client a quitt� violemment
            case ClientHandlerThread.DECONNECTED:
                CHT.setStatus(ClientHandlerThread.TOBEKILL);
                break;
            // Supression du client ayant quitt�
            case ClientHandlerThread.TOBEKILL:
                PilotixServer.theSA.removeShip(CHT.getShip());
                PilotixServer.theCHTs.remove(CHT);
                System.out.println("[SMLT] Nb Client ="+PilotixServer.theCHTs.size());
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
            System.out.println("[SMLT] Nb Client ="+PilotixServer.theCHTs.size());
            
            // envoie de tous les noms a tout les autre vaisseaux
            for (Iterator iter2 = PilotixServer.theCHTs.iterator(); iter2.hasNext();) {
            	ClientHandlerThread CHT = (ClientHandlerThread) iter2.next();
            	info.setShipName(CHT.getShip().getId(),((ServerShip)CHT.getShip()).getName());
            	for (Iterator iter = PilotixServer.theCHTs.iterator(); iter.hasNext();) {
            		ClientHandlerThread CHTD = (ClientHandlerThread) iter.next();
            		try {
            			info.write(CHTD.getMessageHandler());
            		} catch (Exception e) {
            			e.printStackTrace();
            		}
            	}
            }            
        }

        PilotixServer.theSA.nextFrame();
        //envoye de la frame courante a tous les autre ships
        for (Iterator iter = PilotixServer.theCHTs.iterator(); iter.hasNext();) {
            ClientHandlerThread CHT = (ClientHandlerThread) iter.next();
            try {
                CHT.sendArea();
            } catch (Exception e) {
            	System.out.println("[SMLT] Ship "+CHT.getShip().getName()+" left The Game");
            }
        }
        /*if((nbTime % messageEvery ) == 0){
        	System.out.println("test:"+lastTime+" "+System.currentTimeMillis());
        	Long e = System.currentTimeMillis()- lastTime;
        	System.out.println("test:"+(((float)e/(float)1000) /(float)messageEvery));
        	
        	//System.out.println("total time to compute 1 frame : "+((float)total*1000 / (float)messageEvery));
        	nbTime=0;
        	//total=0l;
        	lastTime = System.currentTimeMillis();
        }
        nbTime++;*/
    }

    public synchronized void newClient() {
        newClientHandler = true;
    }
}