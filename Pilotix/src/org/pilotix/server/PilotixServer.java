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

import org.pilotix.common.XMLHandler;
import org.pilotix.common.ResourceLocator;
import java.util.LinkedList;

/**
 * Pilotix : lance le serveur du jeu
 * 
 * Auteurs : - Flo
 * 
 */

public class PilotixServer {

	public static IdHandler theIH;
	public static ServerArea theSA;
	public static ServerMainLoopThread theSMLT;
	public static ClockerThread theCT;
	public static ConnexionHandlerThread theCHT;
	public static XMLHandler theXH;

	// Les instances de ClientHandlerThread
	public static boolean newCHTs;
	public static LinkedList<ClientHandlerThread> theNewCHTs = new LinkedList<ClientHandlerThread>();
	public static LinkedList<ClientHandlerThread> theCHTs = new LinkedList<ClientHandlerThread>();

	public static String dataPath = System.getProperty("pilotix.data.path") + "/";

	public static ResourceLocator theRL = new ResourceLocator(dataPath);

	/**
	 * Lance le serveur du jeu
	 * 
	 * @param port
	 *            port TCP sur lequel le serveur doit écouter
	 * @param fps
	 *            nombre d'images par seconde demandé (le serveur lui, fait ce
	 *            qu'il peut !)
	 */
	public PilotixServer(int port, int fps) throws Exception {
		// nbMaxPlayer = 4;
		System.out.println("-------- [PilotixServer()] --------");
		// System.out.println("[PilotixServer()] new IdHandler()");
		theIH = new IdHandler();
		// System.out.println("[PilotixServer()] new XMLHandler()");
		theXH = new XMLHandler();
		// System.out.println("[PilotixServer()] new ServerArea()");
		theSA = new ServerArea("defaut.pilotix.area.xml");
		// theSA.setMap("defaut.pilotix.area.xml");
		// System.out.println("[PilotixServer()] theIH.setNbMaxIds(theSA.getNbMaxShips())");
		theIH.setNbMaxIds(theSA.getNbMaxShips());
		// System.out.println("[PilotixServer()] new ServerMainLoopThread()");
		theSMLT = new ServerMainLoopThread();
		// System.out.println("[PilotixServer()] new ConnexionHandlerThread()");
		theCHT = new ConnexionHandlerThread(port);
		// System.out.println("[PilotixServer()] new ClockerThread()");
		theCT = new ClockerThread(fps, theSMLT);

		theCHT.start();
		// Environment.theCT.setPriority(Thread.MAX_PRIORITY);
		theCT.start();

	}

	/**
	 * Arrête la partie en cours
	 */
	public void endGame() {
		// Compléter cette méthode pour arrêter proprement le serveur
		// et réinitialiser tout ce qui doit l'être
		System.out.println("---- [PilotixServer.endGame()] ----");
		for (int i = 0; i < theCHTs.size(); i++) {
			System.out.println("[PilotixServer.endGame()] Appel de ClientHandlerThread.endGame() pour le client n°" + i);
			theCHTs.get(i).endGame();
		}
		theCHTs.clear();
		theCHT.endGame();
		theCT.endGame();
		// theSMLT.endGame();
	}

	/**
	 * Main du serveur, instancie un nouveau PilotixServer. La présence de
	 * l'option "-gui" permet de lancer l'interface graphique (classe
	 * ServerGUI).
	 */
	public static void main(String[] args) throws Exception {
		// Gestion des options en ligne de commande
		boolean withGui = false;
		for (int i = 0; i < args.length; i++) {
			// System.out.println(args[i]);
			if (args[i].equals("gui")) {
				withGui = true;
			}
		}

		// Lancement du serveur
		if (withGui) {
			System.out.println("[PilotixServer.main()] - withGui = true");
			new ServerGUI();
		} else {
			System.out.println("[PilotixServer.main()] - withGui = false");
			new PilotixServer(9000, 60);
		}
	}
}
