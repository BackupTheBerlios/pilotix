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

import java.util.Iterator;
import java.util.LinkedList;

import org.pilotix.common.Area;
import org.pilotix.common.ResourceLocator;
import org.pilotix.common.Ship;
import org.pilotix.common.Transferable;
import org.pilotix.common.Vector;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class ServerArea extends Area {

    public LinkedList theShips;
    private ServerShip tmpShip;

    private LinkedList obstacles = new LinkedList();

    private byte[] byteCoded;
    private int lengthInByte;

    Obstacle borders = new Obstacle(new Vector(0, 65535), new Vector(65535, 0));

    Vector currentPosition;
    Vector returnedPosition;
    Vector returnedSpeed;
    int up;
    int down;
    int left;
    int right;
    private int radius;

    public ServerArea() {
        System.out.println("[Area Created]");
        theShips = new LinkedList();
        byteCoded = new byte[5000];
    }

    public void setMap(String aMapFile) {

        Document document = PilotixServer.theXH.getDocumentFromURL(
        		PilotixServer.theRL.getResource(ResourceLocator.AREA, aMapFile));
        Element rootNode = document.getDocumentElement();

        borders.upLeftCorner.x = 0;
        borders.upLeftCorner.y = Integer.parseInt(rootNode
            .getAttribute("height"));
        borders.downRightCorner.x = Integer.parseInt(rootNode
            .getAttribute("width"));
        borders.downRightCorner.y = 0;

        NodeList theObstacles = rootNode.getElementsByTagName("Obstacle");

        NodeList tmp;

        Element tmpXmlObstacle = null;

        for (int i = 0; i < theObstacles.getLength(); i++) {
            tmpXmlObstacle = (Element) theObstacles.item(i);
            obstacles
                .add(new Obstacle(
                    new Vector(Integer.parseInt(tmpXmlObstacle
                        .getAttribute("upLeftCornerX")), Integer
                        .parseInt(tmpXmlObstacle.getAttribute("upLeftCornerY"))),
                    new Vector(Integer.parseInt(tmpXmlObstacle
                        .getAttribute("downRightCornerX")), Integer
                        .parseInt(tmpXmlObstacle
                            .getAttribute("downRightCornerY")))));
        }
    }

    public LinkedList getShips() {
        return theShips;
    }

    public byte[] getAsBytes() {
        byte[] tmp;
        byteCoded[0] = 0;
        byteCoded[0] = (byte) (Transferable.AREA << 4);
        byteCoded[0] |= (byte) theShips.size();
        lengthInByte = 1;
        for (int i = 0; i < theShips.size(); i++) {
            tmpShip = (ServerShip) theShips.get(i);
            tmp = tmpShip.getAsBytes();

            for (int j = 0; j < Ship.lengthInByte; j++) {
                byteCoded[lengthInByte + j] = tmp[j];

            }
            lengthInByte += Ship.lengthInByte;
        }
        return byteCoded;
    }

    public int getLengthInByte() {
        return lengthInByte;
    }

    public void nextFrame() {

        //Calcule de trajectoirs des ships sans obstacle:
        for (int i = 0; i < theShips.size(); i++) {
            tmpShip = (ServerShip) theShips.get(i);
            tmpShip.computeSpeedFromForces();
        }
        /*for (int i = 0; i < theShips.size(); i++) {
            tmpShip = (ServerShip) theShips.get(i);
            collideWithShips(tmpShip);
        }*/

        //Ships/Wall collision : :
        for (int i = 0; i < theShips.size(); i++) {
            tmpShip = (ServerShip) theShips.get(i);
            collideWithBoundary(tmpShip);
            collideWithObstacle(tmpShip);
        }
        // affectation des trajectoirs des ships	
        for (int i = 0; i < theShips.size(); i++) {
            tmpShip = (ServerShip) theShips.get(i);
            tmpShip.nextFrame();
        }
    }

    public void collideWithBoundary(ServerShip s) {

        currentPosition = s.getPosition();
        returnedPosition = s.getNextPosition();
        returnedSpeed = s.getNextSpeed();
        radius = s.getRadius();
        up = borders.upLeftCorner.y;
        down = borders.downRightCorner.y;
        left = borders.upLeftCorner.x;
        right = borders.downRightCorner.x;

        if (returnedPosition.x < (left + radius)) {
            returnedPosition.x = left + radius;
            returnedSpeed.x = 0;
        } else if (returnedPosition.x > (right - radius)) {
            returnedPosition.x = right - radius;
            returnedSpeed.x = 0;
        }
        if (returnedPosition.y > (up - radius)) {
            returnedPosition.y = up - radius;
            returnedSpeed.y = 0;
        } else if (returnedPosition.y < (down + radius)) {
            returnedPosition.y = down + radius;
            returnedSpeed.y = 0;
        }
    }

    public void collideWithObstacle(ServerShip s) {
        currentPosition = s.getPosition();
        returnedPosition = s.getNextPosition();
        returnedSpeed = s.getNextSpeed();
        for (Iterator iter = obstacles.iterator(); iter.hasNext();) {
            Obstacle obstacle = (Obstacle) iter.next();
            up = obstacle.upLeftCorner.y;
            down = obstacle.downRightCorner.y;
            left = obstacle.upLeftCorner.x;
            right = obstacle.downRightCorner.x;
            if (((left - radius) < returnedPosition.x)
                    && (returnedPosition.x < (right + radius))
                    && ((down - radius) < returnedPosition.y)
                    && (returnedPosition.y < (up + radius))) {
                if (currentPosition.x < left) {
                    returnedSpeed.x = 0;
                    returnedPosition.x = left - radius;
                } else if (right < currentPosition.x) {
                    returnedSpeed.x = 0;
                    returnedPosition.x = right + radius;
                }
                if (currentPosition.y < down) {
                    returnedSpeed.y = 0;
                    returnedPosition.y = down - radius;
                } else if (up < currentPosition.y) {
                    returnedSpeed.y = 0;
                    returnedPosition.y = up + radius;
                }
            }
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

    public void collideWithShips(ServerShip s1) {
        for (int i = 0; i < theShips.size(); i++) {
            tmpShip = (ServerShip) theShips.get(i);
            if (tmpShip.getId() > s1.getId()) {
                collideWithShip(s1, tmpShip);
            }
        }
    }

    public void collideWithShip(ServerShip sa, ServerShip sb) {
        Vector va = sa.getNextPosition().less(sa.getPosition());
        Vector vb = sb.getNextPosition().less(sb.getPosition());
        Vector AB = sb.getPosition().less(sa.getPosition());
        Vector vab = vb.less(va);
        int rab = sa.getRadius() + sb.getRadius();
        
        if (AB.dot(AB) <= rab * rab) {
            //System.out.println("currently overlapping between "+sa.getId()+" and "+sb.getId());
            
        } else {
            long a = vab.dot(vab);
            //System.out.println("a"+a);
            long b = 2 * vab.dot(AB);
            //System.out.println("b"+b);
            long c = (AB.dot(AB)) - (rab * rab);
            //System.out.println("c"+c);
            
            long q = (b * b) - (4 * a * c);
            if (q >= 0) {
                double sq = Math.sqrt(q);
                double d = ((double)1) / ((double)(2 * a));
                double r1 = (-b + sq) * d;
                double r2 = (-b - sq) * d;
                r1 = Math.min(r1, r2);
                if (r1 >= 0) {
                    //System.out.println("Collision");
                    Vector tmpA = sa.getPosition().plus(va.mult(r1));
                    sa.setNextPosition(tmpA.plus(vb.mult(1 - r1)));
                    Vector tmpB = sb.getPosition().plus(vb.mult(r1));
                    sb.setNextPosition(tmpB.plus(va.mult(1 - r1)));
                    
                    Vector tmpSpeed = sa.getNextSpeed();                    
                    sa.setNextSpeed(sa.getNextSpeed());
                    sb.setNextSpeed(tmpSpeed);
                    
                } else {
                    //System.out.println("no collision");
                //System.out.println("Collision between "+sa.getId()+" and "+sb.getId());
                }
            }else{
//              System.out.println("no collision");
            }
        }
    }

}