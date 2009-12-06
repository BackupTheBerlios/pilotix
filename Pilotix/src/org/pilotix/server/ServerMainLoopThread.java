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

import org.pilotix.common.Information;

public class ServerMainLoopThread extends Thread {

	// used to compute statistics
	Long lastTime;
	Integer nbTime;
	Integer messageEvery = 30;
	Long total = 0l;

	private boolean newClientHandler = false;

	private Information info = new Information();

	public ServerMainLoopThread() throws Exception {
		lastTime = System.currentTimeMillis();
		nbTime = 0;
	}

	public void run() {

		// Supression des clients désirant partir
		for (int i = 0; i < PilotixServer.theCHTs.size(); i++) {
			ClientHandlerThread myCHT = PilotixServer.theCHTs.get(i);
			int status = myCHT.getStatus();
			switch (status) {
			// Le client veut quitter la partie => sortir
			case ClientHandlerThread.WANTTOLEAVE:
				myCHT.setStatus(ClientHandlerThread.TOBEKILL);
				break;
			// Le client a quitté violemment
			case ClientHandlerThread.DECONNECTED:
				myCHT.setStatus(ClientHandlerThread.TOBEKILL);
				break;
			// Supression du client ayant quitté
			case ClientHandlerThread.TOBEKILL:
				PilotixServer.theSA.removeShip(myCHT.getShip());
				PilotixServer.theCHTs.remove(myCHT);
				System.out.println("[SMLT] Nb Client =" + PilotixServer.theCHTs.size());
				break;
			}
		}
		// Ajout des nouveaux clients (en attente sur la liste temporaire)
		if (newClientHandler) {
			PilotixServer.theCHTs.addAll(PilotixServer.theNewCHTs);
			// Ajout des nouveaux vaisseaux
			for (ClientHandlerThread myNewCHT : PilotixServer.theNewCHTs) {
				PilotixServer.theSA.addShip(myNewCHT.getShip());
			}
		}

		PilotixServer.theSA.nextFrame();
		// Envoi de la frame courante à tous les autres ships
		for (ClientHandlerThread myCHT : PilotixServer.theCHTs) {
			try {
				myCHT.sendArea();
			} catch (Exception e) {
				System.out.println("[SMLT] Ship " + myCHT.getShip().getName() + " left The Game");
			}
		}

		if (newClientHandler) {
			// Envoi de tous les noms à tous les autres vaisseaux
			for (ClientHandlerThread mySourceCHT : PilotixServer.theCHTs) {
				info.setShipName(mySourceCHT.getShip().getId(), mySourceCHT.getShip().getName());
				for (ClientHandlerThread myDestinationCHT : PilotixServer.theCHTs) {
					try {
						info.write(myDestinationCHT.getMessageHandler());
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}

			// effacement de la liste temporaire
			PilotixServer.theNewCHTs.clear();

			newClientHandler = false;
			System.out.println("[SMLT] Nb Client =" + PilotixServer.theCHTs.size());
		}

		/*
		 * if((nbTime % messageEvery ) == 0){
		 * System.out.println("test:"+lastTime+" "+System.currentTimeMillis());
		 * Long e = System.currentTimeMillis()- lastTime;
		 * System.out.println("test:"+(((float)e/(float)1000)
		 * /(float)messageEvery));
		 * 
		 * 
		 * 
		 * //System.out.println("total time to compute 1 frame : "+((float)total*
		 * 1000 / (float)messageEvery)); nbTime=0; //total=0l; lastTime =
		 * System.currentTimeMillis(); } nbTime++;
		 */
	}

	public synchronized void newClient() {
		newClientHandler = true;
	}
}