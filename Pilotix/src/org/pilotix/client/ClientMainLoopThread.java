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

import org.pilotix.common.Angle;
import org.pilotix.common.Command;
import org.pilotix.common.MessageHandler;
import org.pilotix.common.Ship;
import java.net.Socket;
import java.io.IOException;

/**
 * Cette classe est utilisée pour recevoir des messages du serveur et pour
 * mettre à jour ClientArea, Display3D et le tableau des joueurs dans GUIPanel
 * en fonction de ceux-ci.
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
    private Command tmpCommand = null;
    private Angle tmpAngle = null;
    private Ship tmpShip = null;

    /**
     * Constructeur.
     */
    public ClientMainLoopThread() {
        if (Environment.debug) {
            System.out.println("[ClientMainLoopThread] Constructeur");
        }
        tmpCommand = new Command();
        tmpAngle = new Angle();
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
                System.out.println("[ClientMainLoopThread] Connecté à "
                        + socket.getInetAddress() + ":" + socket.getPort());
            }
            clientMessageHandler = new MessageHandler(socket);

            // A ce niveau, on sait que le serveur fonctionne
            // Il est donc judicieux d'initialiser maintenant l'aire locale
            Environment.theClientArea.init();

            if (Environment.debug) {
                System.out
                        .println("[ClientMainLoopThread] Début de la boucle du thread");
            }
            while (!quit) {
                switch (clientMessageHandler.receiveMessage()) {
                case MessageHandler.FRAMEINFO:

                    // On écrit le vaisseau reçu dans ClientArea
                    Environment.theClientArea.setFromBytes(clientMessageHandler
                            .receiveBytes());

                    // On met à jour l'affichage 3D
                    Environment.theDisplay3D.update();

                    // On met à jour la liste des joueurs présents dans
                    // GUIPanel
                    Environment.theGUI.getGUIPanel().update();

                    // Puis on prend en compte les actions du joueur depuis la
                    // dernière frame
                    tmpAngle.set(Environment.theControls.getMouseVariation().x);
                    tmpCommand.setDirection(tmpAngle);
                    int[] aks = Environment.theControls.getKeyStatus();
                    if (aks[Environment.theControls.keyAccel] == Controls.PRESSED) {
                        tmpCommand.setAcceleration(2);
                    } else {
                        tmpCommand.setAcceleration(0);
                    }
                    tmpCommand.setAccessory(0);
                    tmpCommand.setProjectileId(0);

                    /* Enfin, on envoie la commande au serveur */
                    clientMessageHandler.sendCOMMANDMessage(tmpCommand);
                    break;
                case MessageHandler.OWNSHIPINFO:
                    int id = clientMessageHandler.getOwnShipId();
                    if (Environment.debug) {
                        System.out
                                .println("[ClientMainLoopThread] Reçu OWNSHIPINFO : "
                                        + id);
                    }
                    Environment.theClientArea.setOwnShipId(id);
                    break;
                default:
                    System.out
                            .println("[ClientMainLoopThread] ALERTE! Type de message "
                                    + clientMessageHandler.getFrameInfoCode()
                                    + " inconnu.");
                }
            }

            if (Environment.debug) {
                System.out
                        .println("[ClientMainLoopThread] Fin de la boucle et fin du thread");
                System.out
                        .println("[ClientMainLoopThread] Appel imminent de ClientArea.reset()");
            }
            Environment.theClientArea.reset();
            socket.close();
            if (Environment.debug) {
                System.out
                        .println("[ClientMainLoopThread] La socket est fermée. Au revoir!");
                System.out.println("------------------");
                System.out.println("");
            }

        } catch (IOException e) {
            System.out
                    .println("[ClientMainLoopThread] ERREUR - Connexion impossible au serveur.");
            e.printStackTrace();
        } catch (Exception e) {
            System.out.println("[ClientMainLoopThread] ERREUR. :-(");
            e.printStackTrace();
        }
    }

    /**
     * Arrête le thread quand le joueur quitte la partie.
     */
    public void endGame() {
        try {
            clientMessageHandler.sendSESSIONMessage(0);
            if (Environment.debug) {
                System.out
                        .println("[ClientMainLoopThread.endGame] Message SESSION(0) envoyé au serveur");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (Environment.debug) {
            System.out.println("[ClientMainLoopThread.endGame] Quit = true");
        }
        quit = true;
    }
}
