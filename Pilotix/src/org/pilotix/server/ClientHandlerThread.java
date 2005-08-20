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

import org.pilotix.common.*;

public class ClientHandlerThread extends Thread {

    private int shipId;
    private MessageHandler messageHandler;
    private ServerShip ship;
    private boolean quit = false;

    private int status = LOGINING;
    public static final int LOGINING = 1;
    public static final int READY = 2;
    public static final int LOOPING = 3;
    public static final int WANTTOLEAVE = 4;
    public static final int DECONNECTED = 5;
    public static final int TOBEKILL = 6;

    public ClientHandlerThread(int theClientId, MessageHandler aMessageHandler)
            throws Exception {
        messageHandler = aMessageHandler;
        ship = new ServerShip();
        ship.set(theClientId, new Vector(500, 500), new ServerAngle(0), Ship.ADD);
        Information info = new Information();
        info.code = Information.OWN_SHIP_ID;
        info.ownShipId = theClientId;
        info.write(messageHandler);
        status = READY;
        PilotixServer.theNewCHTs.add((Object) this);
        PilotixServer.theSMLT.newClient();
    }

    public void run() {
        quit = false;
        while (!quit) {
            try {
                int flag = messageHandler.receiveOneByte();
                if (flag  == Transferable.COMMAND){
                    Command com = new Command();
                    com.read(messageHandler);
                    ship.addCommand(com);
                }else if (flag == Transferable.INFO){
                    Information info = new Information();
                    info.read(messageHandler); // Je vois pas trop l'intérêt
                                               // de passer par Information.read()
                    if (info.code == Information.DECONNECT){
                        quit = true;
                        try {
                            PilotixServer.theIH.giveBackId(ship.getId());
                        } catch (Exception f) {
                            f.printStackTrace();
                            System.out.println("[ClientHandlerThread.run()] Attention, id="+ship.getId()+" non rendu (cas numéro 1)!");
                        }
//                        messageHandler.close(); // Entraine une exception "Socket closed"
                                                  // dans le serveur
                        status = WANTTOLEAVE;
                    }
                }
            } catch (Exception e) {
                quit = true;
                System.out.println("[ClientHandlerThread]  Ship "
                        + ship.getId() + " Has gone !");
                try {
                    PilotixServer.theIH.giveBackId(ship.getId());
                } catch (Exception f) {
                    f.printStackTrace();
                    System.out.println("[ClientHandlerThread.run()] Attention, id="+ship.getId()+" non rendu (cas numéro 2)!");
                }
                status = DECONNECTED;
            }
        }
    }

    public void sendArea() throws Exception {
        PilotixServer.theSA.write(messageHandler);
    }

    public ServerShip getShip() {
        return ship;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int aStatus) {
        status = aStatus;
        if (aStatus == ClientHandlerThread.TOBEKILL) {
            ship.setStates(Ship.REMOVE);
        }
    }

    public void endGame() {
        ship.setStates(Ship.REMOVE);
        try {
            sendArea();
//            PilotixServer.theIH.giveBackId(ship.getId());
        } catch (Exception e) {
            System.out.println("[PilotixServer.endGame()] EXCEPTION par sendArea() : ");
            e.printStackTrace();
        }
        quit = true;
        System.out.println("[ClientHandlerThread.endGame()] Fermeture des sockets pour le client n°"+ship.getId());
        messageHandler.close();
    }
}
