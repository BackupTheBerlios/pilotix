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
import org.pilotix.common.ResourceLocator;
import org.pilotix.common.Ship;
import org.pilotix.common.Vector;
import org.pilotix.common.IterableArray;
import org.pilotix.common.Ball;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 * <p>
 * Cette classe est une copie locale de l'instance de ServerArea qui se trouve
 * sur le serveur.
 * </p>
 * 
 * <p>
 * La boucle principale (ClientMainLoopThread) récupère les informations du
 * serveur puis met à jour cette classe. Ensuite ClientArea met à jour Display3D
 * (si nécessaire).
 * </p>
 * 
 * <p>
 * Les autres "plug-ins" peuvent consulter ClientArea pour mettre à jour leur
 * état (détections, etc.)
 * </p>
 *
 * @see Display3D
 * @see ClientMainLoopThread
 * @see Ship
 * @see org.pilotix.common.Area
 * @see org.pilotix.server.ServerArea
 * 
 * @author Grégoire Colbert
 * @author Florent Sithimolada
 */
public class ClientArea extends org.pilotix.common.Area {

    private Obstacle[] obstacles;
    private int ownShipId;
    private float xMax = 100.00f; // Valeur par défaut, écrasée par setArea()
    private float yMax = 100.00f; // Valeur par défaut, écrasée par setArea()

    /**
     * Ce constructeur crée un tableau pour conserver une copie locale des
     * vaisseaux actuellement sur le serveur.
     */
    public ClientArea() {
        super();
        if (Environment.debug) {
            System.out.println("[ClientArea] Constructeur");
        }
    }

    /**
     * Initialise le tableau des vaisseaux locaux
     */
    public void init() {
        // Réinitialisation de la liste des Ships
        /*
        for (int i = 0; i < ships.length; i++) {
            ships[i] = null;
        }
        */
        ships.clear();
        balls.clear();
        this.setArea("defaut.pilotix.area.xml"); // TEMPORAIRE, DEVRA ETRE
                                                 // ENVOYE PAR LE SERVEUR
        if (Environment.debug) {
            System.out
                    .println("[ClientArea.init] Appel imminent de Display3D.init()");
        }
        Environment.theDisplay3D.init();
    }

    /**
     * Réinitialise ce ClientArea (appelé par ClientMainLoopThread quand on
     * quitte)
     */
    public void reset() {
        ships.clear();
        balls.clear();
        if (Environment.debug) {
            System.out
                    .println("[ClientArea.reset] Appel imminent de Display3D.reset()");
        }
        Environment.theDisplay3D.reset();
    }

    /**
     * Cette méthode sert à définir quel est le fichier d'aire de jeu à
     * utiliser, et met à jour ClientArea avec les informations qu'il contient.
     * 
     * @param aAreaFile
     *            le nom du fichier ".pilotix.area.xml" à utiliser
     */
    public void setArea(String aAreaFile) {
        Document document = Environment.theXMLHandler
                .getDocumentFromURL(Environment.theRL.getResource(
                        ResourceLocator.AREA, aAreaFile));
        Element rootNode = null;
        try {
            rootNode = document.getDocumentElement();
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Définition des limites externes de l'aire de jeu
        xMax = Integer.parseInt(rootNode.getAttribute("width"))
                * Environment.u3d;
        yMax = Integer.parseInt(rootNode.getAttribute("height"))
                * Environment.u3d;

        // Définition des limites internes de l'aire de jeu (obstacles)
        NodeList theObstacles = rootNode.getElementsByTagName("Obstacle");
        obstacles = new Obstacle[theObstacles.getLength()];
        Element tmpXmlObstacle = null;
        for (int i = 0; i < theObstacles.getLength(); i++) {
            tmpXmlObstacle = (Element) theObstacles.item(i);
            obstacles[i] = new Obstacle(new Vector(Integer
                    .parseInt(tmpXmlObstacle.getAttribute("upLeftCornerX")),
                    Integer.parseInt(tmpXmlObstacle
                            .getAttribute("upLeftCornerY"))), new Vector(
                    Integer.parseInt(tmpXmlObstacle
                            .getAttribute("downRightCornerX")), Integer
                            .parseInt(tmpXmlObstacle
                                    .getAttribute("downRightCornerY"))),
                    Integer.parseInt(tmpXmlObstacle.getAttribute("height")),
                    Integer.parseInt(tmpXmlObstacle.getAttribute("altitude")),
                    tmpXmlObstacle.getAttribute("topTexture"), tmpXmlObstacle
                            .getAttribute("sideTexture"));
        }
    }

    public class Obstacle {
        public Vector upLeftCorner;
        public Vector downRightCorner;
        public int altitude;
        public int height;
        public String topTexture;
        public String sideTexture;

        public Obstacle(Vector upLeftCorner, Vector downRightCorner,
                int height, int altitude, String topTexture, String sideTexture) {
            this.upLeftCorner = upLeftCorner;
            this.downRightCorner = downRightCorner;
            this.height = height;
            this.altitude = altitude;
            this.topTexture = topTexture;
            this.sideTexture = sideTexture;
        }
    }

    /**
     * Renvoie l'ensemble des obstacles dans un tableau.
     *
     * @return le tableau des obstacles
     */
    public Obstacle[] getObstacles() {
        return obstacles;
    }

    /**
     * Renvoie l'obstacle dont le numéro est fourni.
     *
     * @param i
     *            l'identification du vaisseau à renvoyer
     * @return une instance de Obstacle, ou null si l'identifiant fourni n'est
     *         pas celui d'un vaisseau
     */
    public Obstacle getObstacle(int i) {
        if (i > obstacles.length) {
            return null;
        } else {
            return (Obstacle) obstacles[i];
        }
    }

    /**
     * Renvoie la position de la balle dont le numéro est fourni.
     *
     * @param i
     *            l'identification de la balle dont on veut la position
     * @return une instance de Vector qui correspond à la position
     */
    public Vector getBallPosition(int i) {
        return ((Ball) balls.get(i)).getPosition();
    }

    /**
     * Renvoie la vitesse de la balle dont le numéro est fourni.
     *
     * @param i
     *            l'identification de la balle dont on veut la position
     * @return une instance de Vector qui correspond à la position
     */
    public Vector getBallSpeed(int i) {
        return ((Ball) balls.get(i)).getSpeed();
    }

    /**
     * Teste si la balle indiquée existe.
     *
     * @param aBallId
     *            la balle dont l'existence doit être testée
     * @return vrai si la balle existe, faux sinon
     */
    public final boolean ballIsNull(int aBallId) {
//        if (aBallId >= 0 && aBallId < getNbBalls()) {
            return balls.get(aBallId) == null;
/*        }
        else {
            return false;
        }
*/
    }


    /**
     * Teste si le vaisseau indiqué existe.
     *
     * @param aShipId
     *            le vaisseau dont l'existence doit être testée
     * @return vrai si le vaisseau existe, faux sinon
     */
    public final boolean shipIsNull(int aShipId) {
        return ships.get(aShipId) == null;
    }

    /**
     * Renvoie la position du vaisseau dont l'identifiant est passé en
     * paramètre.
     *
     * @param aShipId
     *            le vaisseau dont la position doit être renvoyée
     * @return la position du vaisseau
     */
    public final Vector getShipPosition(int aShipId) {
        return ((Ship)ships.get(aShipId)).getPosition();
        //return ships[aShipId].getPosition();
    }

    /**
     * Renvoie la direction du vaisseau dont l'identifiant est passé en
     * paramètre.
     * 
     * @param aShipId
     *            le vaisseau dont la direction doit être renvoyée
     * @return la direction du vaisseau
     */
    public final Angle getShipDirection(int aShipId) {
        return ((Ship)ships.get(aShipId)).getDirection();
        //return ships[aShipId].getDirection();
    }

    /**
     * Renvoie l'état du vaisseau dont l'identifiant est passé en paramètre.
     * 
     * @param aShipId
     *            le vaisseau dont l'état doit être renvoyé
     * @return l'état du vaisseau
     */
    public final int getShipStates(int aShipId) {
        return ((Ship)ships.get(aShipId)).getStates();
        //return ships[aShipId].getStates();
    }

    /**
     * Renvoie le vaisseau dont l'identificateur est fourni.
     *
     * @param shipID
     *            l'identification du vaisseau à renvoyer
     * @return une instance de Ship, ou null si l'identifiant fourni n'est pas
     *         celui d'un vaisseau
     */
    /*
     * public Ship getShip(int shipID) { return (Ship) ships[shipID]; }
     */
    /**
     * Renvoie l'ensemble des vaisseaux dans un tableau.
     *
     * @return le tableau des vaisseaux
     */
     /*
    public Ship[] getShips() {
        return ships;
    }
    */
    public IterableArray getShips(){
        return ships;
    }

    /**
     * Renvoie l'identifiant du vaisseau du joueur
     * 
     * @return l'identifiant du vaisseau du joueur
     */
    public int getOwnShipId() {
        return ownShipId;
    }

    /**
     * Renvoie le nombre de vaisseaux actuellement dans la partie
     * 
     * @return le nombre de vaisseaux
     */
    public int getNbShips() {
        return nbShips;
    }

    /**
     * Définit l'identifiant du vaisseau du joueur
     * 
     * @param shipId
     *            l'identifiant du vaisseau tel que fourni par le serveur
     */
    public void setOwnShipId(int shipId) {
        ownShipId = shipId;
    }

    /**
     * Renvoie la valeur maximale de X pour l'aire de jeu en cours.
     * 
     * @return la valeur maximale de X
     */
    public final float getXMax() {
        return xMax;
    }

    /**
     * Renvoie la valeur maximale de Y pour l'aire de jeu en cours.
     * 
     * @return la valeur maximale de Y
     */
    public final float getYMax() {
        return yMax;
    }
}
