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

//import java.util.Iterator;
import java.util.Iterator;
import java.util.LinkedList;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

//import java.util.ArrayList;

public class ServerArea {

    public LinkedList theShips;
    private ServerShip tmpShip;

    private LinkedList obstacles = new LinkedList();

    private CollisionHandler collisionHandler;
    private byte[] byteCoded;
    private int LengthOfByteToSend;

    byte ShipBytesLength = Ship.bytesLength;

    Vector coin1 = new Vector(
        0,
        0);
    Vector coin2 = new Vector(
        65535,
        65535);
    int radius = 300;
    int coin1x = coin1.x + radius;
    int coin2x = coin2.x - radius;
    int coin1y = coin1.y + radius;
    int coin2y = coin2.y - radius;

    Vector currentPosition;
    Vector returnedPosition;
    Vector returnedSpeed;
    int up;
    int down;
    int left;
    int right;

    public ServerArea() {
        System.out.println("[Area Created]");
        theShips = new LinkedList();
        collisionHandler = new CollisionHandler();
        byteCoded = new byte[5000];
    }

    public void collideWithBoundary(ServerShip s) {

        currentPosition = s.getCurrentPosition();
        returnedPosition = s.getNextPosition();
        returnedSpeed = s.getNextSpeed();
 
        if ((returnedPosition.x - radius) < coin1x) {
            returnedPosition.x = coin1x + radius;
            returnedSpeed.x = 0;
        } else if ((returnedPosition.x + radius) > coin2x) {
            returnedPosition.x = coin2x - radius;
            returnedSpeed.x = 0;
        }

        if ((returnedPosition.y - radius) < coin1y) {
            returnedPosition.y = coin1y + radius;
            returnedSpeed.y = 0;
        } else if ((returnedPosition.y + radius) > coin2y) {
            returnedPosition.y = coin2y - radius;
            returnedSpeed.y = 0;
        }

        //s.setNextPosition(returnedPosition);
        //s.setNextSpeed(returnedSpeed);
    }

    public void collideWithObstacle(ServerShip s) {
        currentPosition = s.getCurrentPosition();
        returnedPosition = s.getNextPosition();
        returnedSpeed = s.getNextSpeed();
        for (Iterator iter = obstacles.iterator(); iter.hasNext();) {
            //System.out.println("ob");
            Obstacle obstacle = (Obstacle) iter.next();
            up = obstacle.upLeftCorner.y;
            down = obstacle.downRightCorner.y;
            left = obstacle.upLeftCorner.x;
            right = obstacle.downRightCorner.x;
            if (((left - radius) < returnedPosition.x) && (returnedPosition.x < (right+radius))
                && ((down-radius) < returnedPosition.y) && (returnedPosition.y < (up+radius))) {
                //System.out.println("ouou");
                if (currentPosition.x < left) {
                    returnedSpeed.x = 0;
                    returnedPosition.x = left -radius;
                } else if (right < currentPosition.x) {
                    returnedSpeed.x = 0;
                    returnedPosition.x = right +radius;
                }
                if (currentPosition.y < down) {
                    returnedSpeed.y = 0;
                    returnedPosition.y = down-radius;
                } else if (up < currentPosition.y) {
                    returnedSpeed.y = 0;
                    returnedPosition.y = up+radius;
                }
                //returnedSpeed.x = 0;
                //returnedSpeed.y = 0;

                /*if ((left > currentPosition.x) && (currentPosition.x > right)) {
                    returnedSpeed.y = 0;
                    if ((currentPosition.x - left) < (right - currentPosition.x)) {
                        returnedPosition.x = left - radius;
                    } else {
                        returnedPosition.x = right + radius;
                    }
                }

                if ((down < currentPosition.y) && (currentPosition.y < up)) {
                    returnedSpeed.x = 0;
                    if ((currentPosition.y - down) < (up - currentPosition.y)) {
                        returnedPosition.y = down - radius;
                    } else {
                        returnedPosition.y = up + radius;
                    }

                }*/
            }
            s.setNextPosition(returnedPosition);
            s.setNextSpeed(returnedSpeed);
        }
    }

    public LinkedList getShips() {
        return theShips;
    }

    public byte[] getAsByte() {
        /*
         | Octet 0     | Octet 1-6 | Octet 7-12|... 
         | 4bit | 4bit | 6 Octet | 6  Octet |... 
         |Flag 4|nbship| Ship 0  | Ship 1 |...
         */
        byte[] tmp;
        byteCoded[0] = 0;
        byteCoded[0] = (byte) (MessageHandler.FRAMEINFO << 4);
        byteCoded[0] |= (byte) theShips.size();
        LengthOfByteToSend = 1;
        for (int i = 0; i < theShips.size(); i++) {
            tmpShip = (ServerShip) theShips.get(i);
            tmp = tmpShip.getAsBytes();

            for (int j = 0; j < ShipBytesLength; j++) {
                byteCoded[LengthOfByteToSend + j] = tmp[j];

            }
            LengthOfByteToSend += ShipBytesLength;
        }
        return byteCoded;
    }

    public int getLengthOfByte() {
        return LengthOfByteToSend;
    }

    public void nextFrame() {

        //calcul des force des liaisons tresor ships

        //Calcule de trajectoirs des ships:

        for (int i = 0; i < theShips.size(); i++) {
            tmpShip = (ServerShip) theShips.get(i);
            //if( tmpShip != null){
            tmpShip.computeSpeedFromForces();
            //}else{}
        }

        // Ships/Ships collision :
        //for(int i=0;i<ships.size();i++){
        //    for(int j=0;j<ships.size();j++){
        //        collideShips(ships.get(i),ships.get(j));
        //    }
        //}

        // Ships/bullet collision :
        //for(int i=0;i<ships.size();i++){
        //    for(int j=0;j<ships.size();j++){
        //        collideShips(ships.get(i),ships.get(j));
        //    }
        //}

        //Ships/Wall collision : :
        for (int i = 0; i < theShips.size(); i++) {
            tmpShip = (ServerShip) theShips.get(i);
            //if( tmpShip != null){
            collideWithBoundary(tmpShip);
            collideWithObstacle(tmpShip);
            //}
        }

        for (int i = 0; i < theShips.size(); i++) {
            tmpShip = (ServerShip) theShips.get(i);
            //if( tmpShip != null){
            tmpShip.nextFrame();
            //}

        }
    }

    public void setMap(String aMapFile) {

        Document document = PilotixServer.theXH.getDocumentFromFile(aMapFile);
        Element rootNode = document.getDocumentElement();

        coin2.set(
            Integer.parseInt(rootNode.getAttribute("height")),
            Integer.parseInt(rootNode.getAttribute("width")));

        NodeList theObstacles = rootNode.getElementsByTagName("Obstacle");

        NodeList tmp;
        Element upLeftCorner;
        Element downRightCorner;

        for (int i = 0; i < theObstacles.getLength(); i++) {
            tmp = ((Element) theObstacles.item(i))
                .getElementsByTagName("UpLeftCorner");
            upLeftCorner = (Element) tmp.item(0);
            tmp = ((Element) theObstacles.item(i))
                .getElementsByTagName("DownRightCorner");
            downRightCorner = (Element) tmp.item(0);
            obstacles.add(new Obstacle(
                new Vector(
                    Integer.parseInt(upLeftCorner.getAttribute("X")),
                    Integer.parseInt(upLeftCorner.getAttribute("Y"))),
                new Vector(
                    Integer.parseInt(downRightCorner.getAttribute("X")),
                    Integer.parseInt(downRightCorner.getAttribute("Y")))));
        }
    }

    class Obstacle {

        public Vector upLeftCorner;
        public Vector downRightCorner;

        public Obstacle(Vector upLeftCorner, Vector downRightCorner) {
            this.upLeftCorner = upLeftCorner;
            this.downRightCorner = downRightCorner;
        }

    }
}
