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

import org.pilotix.common.Angle;
import org.pilotix.common.Area;
import org.pilotix.common.Command;
import org.pilotix.common.Information;
import org.pilotix.common.MessageHandler;
import org.pilotix.common.Ship;

/**
 * Cette classe est utilis�e pour recevoir des messages
 * du serveur et pour mettre � jour ClientArea, Display3D
 * et le tableau des joueurs dans GUIPanel, en fonction
 * de ceux-ci.
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
    private Command tmpCommand = null;
    private Angle tmpAngle = null;
    private Ship tmpShip = null;

    /**
     * Constructeur.
     */
    public ClientMainLoopThread() {
        if (Environment.debug) {
            System.out.println("[CMLT] Constructeur");
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
            socket = new Socket(
                ip,
                port);
            if (Environment.debug) {
                System.out.println("[CMLT] Connect� � "
                        + socket.getInetAddress() + ":" + socket.getPort());
            }
            clientMessageHandler = new MessageHandler(
                socket);

            // A ce niveau, on sait que le serveur fonctionne
            // Il est donc judicieux d'initialiser maintenant l'aire locale
            Environment.theClientArea.init();

            if (Environment.debug) {
                System.out.println("[CMLT] D�but de la boucle");
            }
            while (!quit) {
                Object obj = clientMessageHandler.receive();
                if (obj instanceof Area) {
                    // On �crit le vaisseau re�u dans ClientArea
                    Environment.theClientArea.set((Area) obj);

                    // On met � jour l'affichage 3D
                    Environment.theDisplay3D.update();

                    // On met � jour la liste des joueurs pr�sents dans
                    // GUIPanel
                    Environment.theGUI.getGUIPanel().update();

                    // Puis on prend en compte les actions du joueur depuis la
                    // derni�re frame
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

                    // Enfin, on envoie la commande au serveur
                    //clientMessageHandler.sendCOMMANDMessage(tmpCommand);
                    clientMessageHandler.send(tmpCommand);
                } else if (obj instanceof Information) {                    
                    switch (((Information) obj).code) {
                    case Information.OWN_SHIP_ID:
                        // On re�oit notre num�ro de joueur
                        Environment.theClientArea
                            .setOwnShipId(((Information) obj).ownShipId);
                        /*System.out
                            .println("[CMLT]!!!!!!!!!!!!!!!!!! OWN_SHIP_ID : "
                                    + ((Information) obj).ownShipId);*/
                        break;
                    default:
                        break;
                    }
                } else{
                    System.out.println("Paquet Inconnue");
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
            System.out.println("[CMLT] ERREUR de connexion au serveur.");
            e.printStackTrace();
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
            info.code = Information.DECONNECT;
            clientMessageHandler.send(info);
            if (Environment.debug) {
                System.out.println("[CMLT.endGame] DECONNECT envoy�.");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        quit = true;
    }
}