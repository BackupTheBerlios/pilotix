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

package org.pilotix.client;

import java.io.IOException;
import java.net.Socket;

import org.pilotix.common.Information;
import org.pilotix.common.MessageHandler;
import org.pilotix.common.Ship;
import org.pilotix.common.Transferable;

/**
 * Cette classe est utilis�e pour recevoir des messages
 * du serveur et pour mettre � jour ClientArea, Display3D
 * et le tableau des joueurs dans GUIPanel, en fonction
 * de ces messages.
 *
 * @see MessageHandler
 * @see ClientArea
 * @see GUIPanel
 * @see Display3D
 *
 * @author Florent Sithimolada
 * @author Lo�c Guibart
 * @author Gr�goire Colbert
 */
public class ClientMainLoopThread extends Thread {

    private boolean quit;
    private ClientArea clientArea = null;
    private MessageHandler clientMessageHandler = null;
    private Socket socket = null;

    /**
     * Constructeur.
     */
    public ClientMainLoopThread() {
        if (Environment.debug) {
            System.out.println("[CMLT] Constructeur");
        }
    }

    /**
     * Boucle sur chaque frame en provenance du serveur.
     */
    public void run() {
        quit = false;

        try {
            String ip = Environment.theServerIP;
            int port = Environment.theServerPort.intValue();
            socket = new Socket(ip, port);
            if (Environment.debug) {
                System.out.println("[CMLT] Connect� � "
                    + socket.getInetAddress() + ":" + socket.getPort());
            }
            clientMessageHandler = new MessageHandler(socket);

            // A ce niveau, on sait que le serveur fonctionne
            // Il est donc judicieux d'initialiser maintenant l'aire locale
            Environment.theClientArea.init();
            Environment.theGUI.getGUIPanel().beginGame();

            if (Environment.debug) {
                System.out.println("[CMLT] D�but de la boucle");
            }

            while (!quit) {
                int flag = clientMessageHandler.receiveOneByte();

                if (flag == Transferable.AREA) {
                    Environment.theClientArea.read(clientMessageHandler);
                    // Effectue les calculs c�t� client
                    Environment.theClientArea.nextFrame();
                    // On met � jour l'affichage 3D
                    Environment.theDisplay3D.update();
                    // On met � jour la liste des joueurs pr�sents
                    Environment.theGUI.getGUIPanel().update();
                    // On prend en compte l'action du joueur
                    (Environment.controlCmd.getCommand()).write(clientMessageHandler);
                } else if (flag == Transferable.INFO) {
                	//int typeInfo = clientMessageHandler.receiveOneByte();
                	Information info = new Information();
                	info.read(clientMessageHandler);
                	switch (info.getType()) {
                	case Information.OWN_SHIP_ID :
                		// On re�oit notre num�ro de joueur
                		System.out.println("[CMLT] Re�u Information, type OWN_SHIP_ID :"+info.getOwnShipId());
                		Environment.theClientArea.setOwnShipId(info.getOwnShipId());
                		// il doit alors envoyer sont propre nom
                		Information info2 = new Information();
                		// a remplacer par le vrai nom
                		info2.setShipName(info.getOwnShipId(),"ship"+info.getOwnShipId());
                		info2.write(clientMessageHandler);
                	break;
                	case Information.DECONNECT :
                		System.out.println("[CMLT] Re�u Information, type DECONNECT");
                	break;
                	case Information.AREA_ID :
                		System.out.println("[CMLT] Re�u Information, type AREA_ID");
                	break;
                	case Information.SHIP_NAME :
                		System.out.println(info.getShipId()+" is "+info.getShipName());
                		Environment.theDisplay3D.setJ3DShipsName(info.getShipId(), info.getShipName());
                		break;
                	}
                    /*switch (typeInfo) {
                        case Information.OWN_SHIP_ID :
                            // On re�oit notre num�ro de joueur
                            System.out.println("[CMLT] Re�u Information, type OWN_SHIP_ID");
                        	int shipId = (int)clientMessageHandler.receiveOneByte();
                            Environment.theClientArea.setOwnShipId(shipId);
                            // il doit alors envoyer sont nom
                            Information info = new Information();
                            // a remplacer par le vrai nom
                            info.setShipName(shipId,"ship"+shipId);
                            info.write(clientMessageHandler);
                            break;
                        case Information.DECONNECT :
                            System.out.println("[CMLT] Re�u Information, type DECONNECT");
                            break;
                        case Information.AREA_ID :
                            System.out.println("[CMLT] Re�u Information, type AREA_ID");
                            break;
                        case Information.SHIP_NAME :
                        	Information info = new Information();
                        // a remplacer par le vrai nom
                        info.read();
                        info.write(clientMessageHandler);
                            break;
                    }*/
                } else {
                    System.out.println("[CMLT] Re�u paquet inconnu, flag = "+flag);
                }
            }

            if (Environment.debug) {
                System.out.println("[CMLT] Fin de la boucle.");
            }
            Environment.theClientArea.reset();
            socket.close();
            if (Environment.debug) {
                System.out.println("[CMLT] Socket ferm�e. A+\n-----\n");
            }

        } catch (IOException e) {
            System.out.println("[CMLT] ERREUR : connexion refus�e.");
            Environment.theGUI.getGUIPanel().displayMessageConnectionRefused();
        } catch (Exception e) {
            System.out.println("[CMLT] ERREUR. :-(");
            e.printStackTrace();
        }
    }

    /**
     * Arr�te le thread quand le joueur quitte la partie.
     */
    public void endGame() {
        try {
            Information info = new Information();
            info.setDeconnected();
            info.write(clientMessageHandler);
            if (Environment.debug) {
                System.out.println("[CMLT.endGame] DECONNECT envoy�.");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        quit = true;
    }
}
