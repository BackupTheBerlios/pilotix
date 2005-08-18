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
 * Cette classe est utilisée pour recevoir des messages
 * du serveur et pour mettre à jour ClientArea, Display3D
 * et le tableau des joueurs dans GUIPanel, en fonction
 * de ces messages.
 *
 * @see MessageHandler
 * @see ClientArea
 * @see GUIPanel
 * @see Display3D
 *
 * @author Florent Sithimolada
 * @author Loïc Guibart
 * @author Grégoire Colbert
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
                System.out.println("[CMLT] Connecté à "
                    + socket.getInetAddress() + ":" + socket.getPort());
            }
            clientMessageHandler = new MessageHandler(socket);

            // A ce niveau, on sait que le serveur fonctionne
            // Il est donc judicieux d'initialiser maintenant l'aire locale
            Environment.theClientArea.init();
            Environment.theGUI.getGUIPanel().beginGame();

            if (Environment.debug) {
                System.out.println("[CMLT] Début de la boucle");
            }

            while (!quit) {
                int flag = clientMessageHandler.receiveOneByte();

                if (flag == Transferable.AREA) {
                    Environment.theClientArea.read(clientMessageHandler);
                    // Effectue les calculs côté client
                    Environment.theClientArea.nextFrame();
                    // On met à jour l'affichage 3D
                    Environment.theDisplay3D.update();
                    // On met à jour la liste des joueurs présents
                    Environment.theGUI.getGUIPanel().update();
                    // On prend en compte l'action du joueur
                    (Environment.controlCmd.getCommand()).write(clientMessageHandler);
                } else if (flag == Transferable.INFO) {
                    int typeInfo = clientMessageHandler.receiveOneByte();
                    switch (typeInfo) {
                        case Information.OWN_SHIP_ID :
                            // On reçoit notre numéro de joueur
                            System.out.println("[CMLT] Reçu Information, type OWN_SHIP_ID");
                            Environment.theClientArea.setOwnShipId(clientMessageHandler.receiveOneByte());
                            break;
                        case Information.DECONNECT :
                            System.out.println("[CMLT] Reçu Information, type DECONNECT");
                            break;
                        case Information.AREA_ID :
                            System.out.println("[CMLT] Reçu Information, type AREA_ID");
                            break;
                    }
                } else {
                    System.out.println("[CMLT] Reçu paquet inconnu, flag = "+flag);
                }
            }

            if (Environment.debug) {
                System.out.println("[CMLT] Fin de la boucle.");
            }
            Environment.theClientArea.reset();
            socket.close();
            if (Environment.debug) {
                System.out.println("[CMLT] Socket fermée. A+\n-----\n");
            }

        } catch (IOException e) {
            System.out.println("[CMLT] ERREUR : connexion refusée.");
            Environment.theGUI.getGUIPanel().displayMessageConnectionRefused();
        } catch (Exception e) {
            System.out.println("[CMLT] ERREUR. :-(");
            e.printStackTrace();
        }
    }

    /**
     * Arrête le thread quand le joueur quitte la partie.
     */
    public void endGame() {
        try {
            Information info = new Information();
            info.code = Information.DECONNECT;
            info.write(clientMessageHandler);
            if (Environment.debug) {
                System.out.println("[CMLT.endGame] DECONNECT envoyé.");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        quit = true;
    }
}
