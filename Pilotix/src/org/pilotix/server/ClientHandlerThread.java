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
    private boolean active = true;

    private int state = LOGINING;
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
        ship.set(theClientId, new Vector(500, 500), new ServerAngle(0),
                Ship.ADD);
        Information info = new Information();
        info.code = Information.OWN_SHIP_ID;
        info.ownShipId = theClientId;
        info.write(messageHandler);
        state = READY;
        PilotixServer.theNewCHTs.add((Object) this);
        PilotixServer.theSMLT.newClient();
    }

    public void run() {
        while (active) {
            try {
                int flag = messageHandler.receiveOneByte();
                if (flag  == Transferable.COMMAND){
                    Command com = new Command();
                    com.read(messageHandler);
                    ship.addCommand(com);
                }else if (flag == Transferable.INFO){
                    Information info = new Information();
                    info.read(messageHandler);
                    if (info.code == Information.DECONNECT){
                        state = WANTTOLEAVE;
                        active = false;
                        try {
                            PilotixServer.theIH.giveBackId(ship.getId());
                        } catch (Exception f) {
                            f.printStackTrace();
                            System.out.println("ATTENTION PB ID NON RENDU");
                        }
                    }
                }

            } catch (Exception e) {
                active = false;
                System.out.println("[ClientHandlerThread]  Ship "
                        + ship.getId() + " Has gone !");
                try {
                    PilotixServer.theIH.giveBackId(ship.getId());
                } catch (Exception f) {
                    f.printStackTrace();
                    System.out.println("ATTENTION PB ID NON RENDU");
                }
                state = DECONNECTED;
            }
        }
    }

    public void sendArea()throws Exception {
            PilotixServer.theSA.write(messageHandler);
    }

    public ServerShip getShip() {
        return ship;
    }

    public int getState() {
        return state;
    }

    public void setState(int aState) {
        state = aState;
        if (aState == ClientHandlerThread.TOBEKILL) {
            ship.setStates(Ship.REMOVE);
        }
    }
}