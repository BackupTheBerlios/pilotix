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
 * La boucle principale (ClientMainLoopThread) r�cup�re les informations du
 * serveur puis met � jour cette classe. Ensuite ClientArea met � jour Display3D
 * (si n�cessaire).
 * </p>
 * 
 * <p>
 * Les autres "plug-ins" peuvent consulter ClientArea pour mettre � jour leur
 * �tat (d�tections, etc.)
 * </p>
 *
 * @see Display3D
 * @see ClientMainLoopThread
 * @see Ship
 * @see org.pilotix.common.Area
 * @see org.pilotix.server.ServerArea
 * 
 * @author Gr�goire Colbert
 * @author Florent Sithimolada
 */
public class ClientArea extends org.pilotix.common.Area {

    private IterableArray obstacles;
    private int ownShipId;
    private float xMax = 100.00f; // Valeur par d�faut, �cras�e par setArea()
    private float yMax = 100.00f; // Valeur par d�faut, �cras�e par setArea()

    /**
     * Ce constructeur cr�e un tableau pour conserver une copie locale des
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
        // R�initialisation de la liste des Ships
        ships.clear();
        balls.clear();
        this.setArea("defaut.pilotix.area.xml"); // TEMPORAIRE, DEVRA ETRE
        // ENVOYE PAR LE SERVEUR
        if (Environment.debug) {
            System.out.println("[ClientArea.init] Appel imminent de Display3D.init()");
        }
        Environment.theDisplay3D.init();
    }

    /**
     * R�initialise ce ClientArea (appel� par ClientMainLoopThread quand on
     * quitte)
     */
    public void reset() {
        ships.clear();
        balls.clear();
        if (Environment.debug) {
            System.out.println("[ClientArea.reset] Appel imminent de Display3D.reset()");
        }
        Environment.theDisplay3D.reset();
    }

    /**
     * Cette m�thode sert � d�finir quel est le fichier d'aire de jeu �
     * utiliser, et met � jour ClientArea avec les informations qu'il contient.
     *
     * @param aAreaFile
     *            le nom du fichier ".pilotix.area.xml" � utiliser
     */
    public void setArea(String aAreaFile) {
        Document document = Environment.theXMLHandler.getDocumentFromURL(Environment.theRL.getResource(
            ResourceLocator.AREA,
            aAreaFile));
        Element rootNode = null;
        try {
            rootNode = document.getDocumentElement();
        } catch (Exception e) {
            e.printStackTrace();
        }

        // D�finition des limites externes de l'aire de jeu
        xMax = Integer.parseInt(rootNode.getAttribute("width"))
            * Environment.u3d;
        yMax = Integer.parseInt(rootNode.getAttribute("height"))
            * Environment.u3d;

        // D�finition des limites internes de l'aire de jeu (obstacles)
        NodeList theObstacles = rootNode.getElementsByTagName("Obstacle");
        obstacles = new IterableArray(theObstacles.getLength());
        Element tmpXmlObstacle = null;
        for (int i = 0; i < theObstacles.getLength(); i++) {
            tmpXmlObstacle = (Element) theObstacles.item(i);
            obstacles.add(
                i,
                new Obstacle(
                    new Vector(
                        Integer.parseInt(tmpXmlObstacle.getAttribute("upLeftCornerX")),
                        Integer.parseInt(tmpXmlObstacle.getAttribute("upLeftCornerY"))),
                    new Vector(
                        Integer.parseInt(tmpXmlObstacle.getAttribute("downRightCornerX")),
                        Integer.parseInt(tmpXmlObstacle.getAttribute("downRightCornerY"))),
                    Integer.parseInt(tmpXmlObstacle.getAttribute("height")),
                    Integer.parseInt(tmpXmlObstacle.getAttribute("altitude")),
                    tmpXmlObstacle.getAttribute("topTexture"),
                    tmpXmlObstacle.getAttribute("sideTexture")));
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
    public IterableArray getObstacles() {
        return obstacles;
    }

    /**
     * Renvoie l'obstacle dont le num�ro est fourni.
     *
     * @param i
     *            l'identification du vaisseau � renvoyer
     * @return une instance de Obstacle, ou null si l'identifiant fourni n'est
     *         pas celui d'un vaisseau
     */
    public Obstacle getObstacle(int i) {
        return (Obstacle) obstacles.get(i);
    }

    /**
     * Renvoie la position de la balle dont le num�ro est fourni.
     *
     * @param i
     *            l'identification de la balle dont on veut la position
     * @return une instance de Vector qui correspond � la position
     */
    public Vector getBallPosition(int i) {
        return ((Ball) balls.get(i)).getPosition();
    }

    /**
     * Renvoie la vitesse de la balle dont le num�ro est fourni.
     *
     * @param i
     *            l'identification de la balle dont on veut la position
     * @return une instance de Vector qui correspond � la position
     */
    public Vector getBallSpeed(int i) {
        return ((Ball) balls.get(i)).getSpeed();
    }

    /**
     * Renvoie la balle dont le num�ro est fourni.
     *
     * @param i le num�ro de la balle
     * @return une instance de Ball
     */
    public Ball getBall(int i) {
        return (Ball) balls.get(i);
    }

    /**
     * Teste si la balle indiqu�e existe.
     *
     * @param aBallId
     *            la balle dont l'existence doit �tre test�e
     * @return vrai si la balle existe, faux sinon
     */
    public final boolean ballIsNull(int aBallId) {
        return balls.get(aBallId) == null;
    }

    /**
     * Teste si le vaisseau indiqu� existe.
     *
     * @param aShipId
     *            le vaisseau dont l'existence doit �tre test�e
     * @return vrai si le vaisseau existe, faux sinon
     */
    public final boolean shipIsNull(int aShipId) {
        return ships.get(aShipId) == null;
    }

    /**
     * Renvoie la position du vaisseau dont l'identifiant est pass� en
     * param�tre.
     *
     * @param aShipId
     *            le vaisseau dont la position doit �tre renvoy�e
     * @return la position du vaisseau
     */
    public final Vector getShipPosition(int aShipId) {
        return ((Ship) ships.get(aShipId)).getPosition();
        //return ships[aShipId].getPosition();
    }

    /**
     * Renvoie la direction du vaisseau dont l'identifiant est pass� en
     * param�tre.
     * 
     * @param aShipId
     *            le vaisseau dont la direction doit �tre renvoy�e
     * @return la direction du vaisseau
     */
    public final Angle getShipDirection(int aShipId) {
        return ((Ship) ships.get(aShipId)).getDirection();
        //return ships[aShipId].getDirection();
    }

    /**
     * Renvoie l'�tat du vaisseau dont l'identifiant est pass� en param�tre.
     * 
     * @param aShipId
     *            le vaisseau dont l'�tat doit �tre renvoy�
     * @return l'�tat du vaisseau
     */
    public final int getShipStates(int aShipId) {
        return ((Ship) ships.get(aShipId)).getStates();
        //return ships[aShipId].getStates();
    }

    /**
     * Renvoie le vaisseau dont l'identificateur est fourni.
     *
     * @param shipID
     *            l'identification du vaisseau � renvoyer
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
    public IterableArray getShips() {
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
     * D�finit l'identifiant du vaisseau du joueur
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

    /**
     * Met � jour la position de certains �l�ments qui ne sont pas
     * mis � jour � chaque frame par le serveur (position des balles
     * par exemple).
     */
    public void nextFrame() {
        for (balls.cursor1OnFirst(); balls.cursor1IsNotNull(); balls.cursor1Next()) {
            ((Ball) balls.cursor1Get()).nextFrame();
        }
    }
}
