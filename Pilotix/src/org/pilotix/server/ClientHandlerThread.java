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
//import java.net.Socket;
//import java.util.LinkedList;

public class ClientHandlerThread extends Thread {

    private int shipId;
    private MessageHandler messageHandler;
    //private ConnexionHandlerThread connexionHandlerThread;
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
        //System.out.println("[ClientHandlerThread] Launched ");
        messageHandler = aMessageHandler;
        //connexionHandlerThread = Environment.theCHT;
        //sera fait par PilotixCenter
        ship = new ServerShip();
        ship.set(theClientId, new Vector(500, 500), new ServerAngle(0),
                Ship.ADD);
        messageHandler.sendOWNSHIPINFOMessage(theClientId);
        state = READY;
        PilotixServer.theNewCHTs.add((Object) this);
        PilotixServer.theSMLT.newClient();
    }

    public void run() {
        //System.out.println("[ClientHandlerThread] has been started");
        while (active) {
            try {
                switch (messageHandler.receiveMessage()) {
                case MessageHandler.COMMAND:
                    ship.addCommand(messageHandler.getCommand());
                    break;
                case MessageHandler.SESSION:
                    state = WANTTOLEAVE;
                    active = false;
                    try {
                        PilotixServer.theIH.giveBackId(ship.getId());
                    } catch (Exception f) {
                        f.printStackTrace();
                        System.out.println("ATTENTION PB ID NON RENDU");
                    }
                    break;
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

    public ServerShip getShip() {
        return ship;
    }

    /*
     * public void sendMessage(int aMessage){ try{
     * messageHandler.sendFRAMEINFOMessage((byte)aMessage); }catch(Exception
     * e){ //e.printStackTrace(); }
     */

    /*public void send(byte[] anArea, int aSize) throws Exception {
        //try{
        messageHandler.sendBytes(anArea, aSize);
        //}catch(Exception e){
        //    e.printStackTrace();
        //}
    }*/
    
    public void sendArea() throws Exception {
        //try{
        messageHandler.send(PilotixServer.theSA);
        //}catch(Exception e){
        //    e.printStackTrace();
        //}
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
