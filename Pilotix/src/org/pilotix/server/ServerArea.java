/*
 * Pilotix : a multiplayer piloting game. Copyright (C) 2003 Pilotix.Org
 * 
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation; either version 2 of the License, or (at your option) any later
 * version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * this program; if not, write to the Free Software Foundation, Inc., 59 Temple
 * Place - Suite 330, Boston, MA 02111-1307, USA.
 */

package org.pilotix.server;

import java.util.Iterator;
import java.util.LinkedList;

import org.pilotix.common.Area;
import org.pilotix.common.IterableArray;
import org.pilotix.common.ResourceLocator;
import org.pilotix.common.Vector;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class ServerArea extends Area {

    //private LinkedList ships = new LinkedList();
    private LinkedList balls = new LinkedList();
    private ServerShip tmpShip2;

    private LinkedList obstacles = new LinkedList();

    //private byte[] byteCoded;
    //private int lengthInByte;

    Obstacle borders = new Obstacle(new Vector(0, 65535), new Vector(65535, 0));

    Vector currentPosition;
    Vector returnedPosition;
    Vector returnedSpeed;
    Vector tmpVector = new Vector();
    int up;
    int down;
    int left;
    int right;
    private int radius;
    private ServerBall tmpBall;

    public ServerArea(String aMapFile) {
        super();
        setMap(aMapFile);
        //System.out.println("[Area Created]");

    }

    public void setMap(String aMapFile) {

        Document document = PilotixServer.theXH
                .getDocumentFromURL(PilotixServer.theRL.getResource(
                        ResourceLocator.AREA, aMapFile));
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
            obstacles.add(new Obstacle(new Vector(Integer
                    .parseInt(tmpXmlObstacle.getAttribute("upLeftCornerX")),
                    Integer.parseInt(tmpXmlObstacle
                            .getAttribute("upLeftCornerY"))), new Vector(
                    Integer.parseInt(tmpXmlObstacle
                            .getAttribute("downRightCornerX")), Integer
                            .parseInt(tmpXmlObstacle
                                    .getAttribute("downRightCornerY")))));
        }

        ships = new IterableArray(8);
    }

    public void addShip(ServerShip aShip) {
        ships.add(aShip.getId(), aShip);
    }

    public void removeShip(ServerShip aShip) {
        ships.remove(aShip.getId());
    }

    public void nextFrame() {

        //creation des differents projectils :
        /*
         * for (int i = 0; i < ships.size(); i++) { tmpShip = (ServerShip)
         * ships.get(i); tmpShip.commandPilotixElement(balls); }
         */

        //Calcule de prochaine trajectoirs des balls sans collisions:
        /*
         * for (int i = 0; i < balls.size(); i++) { tmpBall = (ServerBall)
         * balls.get(i); tmpBall.computeSpeedFromForces(); }
         */

        //Calcule de prochaine trajectoirs des ships sans collisions:
        for (ships.setCursorOnFirst(); ships.hasNext();) {
            tmpShip2 = (ServerShip) ships.next();
            tmpShip2.computeSpeedFromForces();
        }

        //modification des trajectoires en prenant en compte les collisions
        // ships/balls
        /*
         * for (int i = 0; i < ships.size(); i++) { tmpShip = (ServerShip)
         * ships.get(i); collideWithBalls(tmpShip); }
         */

        //modification des trajectoires en prenant en compte les collisions
        // ships/ships
        for (ships.setCursorOnFirst(); ships.hasNext();) {
            tmpShip2 = (ServerShip) ships.next();
            collideWithShips(tmpShip2);
        }

        //modification des trajectoires en prenant en compte les collisions
        //Ships/Obstacles et Ships/frontiere externes
        for (ships.setCursorOnFirst(); ships.hasNext();) {
            tmpShip2 = (ServerShip) ships.next();
            collideWithBoundary(tmpShip2);
            collideWithObstacle(tmpShip2);
        }

        // affectation des trajectoirs des ships
        for (ships.setCursorOnFirst(); ships.hasNext();) {
            tmpShip2 = (ServerShip) ships.next();
            tmpShip2.nextFrame();
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

    private void collideWithBalls(ServerShip aShip) {
        for (int i = 0; i < balls.size(); i++) {
            tmpBall = (ServerBall) balls.get(i);
            collideWithBall(tmpShip2, tmpBall);
        }

    }

    private void collideWithBall(ServerShip aShip, ServerBall aBall) {
        Vector va = aShip.getNextPosition().less(aShip.getPosition());
        Vector vb = aBall.getNextPosition().less(aBall.getPosition());
        Vector AB = aBall.getPosition().less(aShip.getPosition());
        Vector vab = vb.less(va);
        int rab = aShip.getRadius() + aBall.getRadius();

        //if (AB.dot(AB) <= rab * rab) {
        //} else {
        long a = vab.dot(vab);
        long b = 2 * vab.dot(AB);
        long c = (AB.dot(AB)) - (rab * rab);
        long q = (b * b) - (4 * a * c);
        if (q >= 0) {
            double sq = Math.sqrt(q);
            double d = ((double) 1) / ((double) (2 * a));
            double r1 = (-b + sq) * d;
            double r2 = (-b - sq) * d;
            r1 = Math.min(r1, r2);
            if ((0 < r1) && (r1 < 1)) {
                //System.out.println("r1 " + r1);
                aShip.setNextPosition(aShip.getPosition().plus(va.mult(r1))
                        .plus(vb.mult(1 - r1)));
                //aBall.setNextPosition(aBall.getPosition().plus(vb.mult(r1)).plus(
                //    va.mult(1 - r1)));

                tmpVector.set(aShip.getNextSpeed());
                aShip.setNextSpeed(aBall.getSpeed());
                //aBall.setNextSpeed(tmpVector);

                aBall.setState(ServerBall.TO_DELETE);

            } else {
                //System.out.println("no collision");
            }
        } else {
            //System.out.println("no collision");
        }
        //}

    }

    public void collideWithShips(ServerShip s1) {
        for (ships.setCursorOnFirst(); ships.hasNext();) {
            tmpShip2 = (ServerShip) ships.next();
            if (tmpShip2.getId() > s1.getId()) {
                collideWithShip(s1, tmpShip2);
            }
        }
    }

    public void collideWithShip(ServerShip sa, ServerShip sb) {
        Vector va = sa.getNextPosition().less(sa.getPosition());
        Vector vb = sb.getNextPosition().less(sb.getPosition());
        Vector AB = sb.getPosition().less(sa.getPosition());
        Vector vab = vb.less(va);
        int rab = sa.getRadius() + sb.getRadius();

        //if (AB.dot(AB) <= rab * rab) {
        //} else {
        long a = vab.dot(vab);
        long b = 2 * vab.dot(AB);
        long c = (AB.dot(AB)) - (rab * rab);
        long q = (b * b) - (4 * a * c);
        if (q >= 0) {
            double sq = Math.sqrt(q);
            double d = ((double) 1) / ((double) (2 * a));
            double r1 = (-b + sq) * d;
            double r2 = (-b - sq) * d;
            r1 = Math.min(r1, r2);
            if ((0 < r1) && (r1 < 1)) {
                //System.out.println("r1 " + r1);
                sa.setNextPosition(sa.getPosition().plus(va.mult(r1)).plus(
                        vb.mult(1 - r1)));
                sb.setNextPosition(sb.getPosition().plus(vb.mult(r1)).plus(
                        va.mult(1 - r1)));

                tmpVector.set(sa.getNextSpeed());
                sa.setNextSpeed(sb.getNextSpeed());
                sb.setNextSpeed(tmpVector);

            } else {
                //System.out.println("no collision");
            }
        } else {
            //System.out.println("no collision");
        }
        //}
    }

}
