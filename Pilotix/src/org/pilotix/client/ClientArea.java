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

import org.pilotix.common.Ship;
import org.pilotix.common.Vector;
import org.w3c.dom.NodeList;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * <p>
 * Cette classe est une copie locale de l'instance de ServerArea qui se trouve
 * sur le serveur.
 * </p>
 * 
 * <p>
 * La boucle principale (ClientMainLoopThread) récupère les informations du
 * serveur puis met à jour cette classe. Ensuite ClientArea met à jour
 * Display3D (si nécessaire).
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
 * @see org.pilotix.server.ServerArea
 * 
 * @author Grégoire Colbert
 * @author Florent Sithimolada
 */
public class ClientArea  extends org.pilotix.common.Area { // pour plus tard
                         // (peut-etre)

    //private Ship[] ships;
    //private int nbShips;
    private Obstacle[] obstacles;
    private int ownShipId;
    private float xMax = 100.00f; // Valeur par défaut, écrasée par setArea()
    private float yMax = 100.00f; // Valeur par défaut, écrasée par setArea()
    private byte[] tmpByte;
    private int index;
    private ClientShip tmpClientShip;


    /**
     * Ce constructeur crée un tableau pour conserver une copie locale des
     * vaisseaux actuellement sur le serveur.
     */
    public ClientArea() {
        //super();
        if (Environment.debug) {
            System.out.println("[ClientArea] Constructeur");
        }
        tmpByte = new byte[6];
        tmpClientShip = new ClientShip();
    }

    /**
     * Initialise le tableau des vaisseaux locaux
     */
    public void init() {
        // Création de la liste pour les org.pilotix.common.Ship
        ships = new Ship[15];
        // Réinitialisation de la liste des Ships
        for (int i = 0; i < ships.length; i++) {
            ships[i] = null;
        }
        this.setArea("defaut.pilotix.area.xml"); // TEMPORAIRE, DEVRA ETRE ENVOYE PAR LE SERVEUR
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
        for (int i = 0; i < ships.length; i++) {
            if (ships[i] != null) {
                if (Environment.debug) {
                    System.out
                            .println("[ClientArea.reset] Mise à null de ships["
                                    + i + "]");
                }
                ships[i] = null;
            }
        }
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
        Document document = Environment.theXMLHandler.getDocumentFromURL(
                             Environment.theRL.getResource(
                                          ResourceLocator.AREA, aAreaFile));
        Element rootNode =null;
        try {
            rootNode = document.getDocumentElement();
        } catch (Exception e) {
            e.printStackTrace();
        }
        

        // Définition des limites externes de l'aire de jeu
        xMax = Integer.parseInt(rootNode.getAttribute("width"))*Environment.u3d;
        yMax = Integer.parseInt(rootNode.getAttribute("height"))*Environment.u3d;

        // Définition des limites internes de l'aire de jeu (obstacles)
        NodeList theObstacles = rootNode.getElementsByTagName("Obstacle");
        NodeList tmp;
        Element upLeftCorner;
        Element downRightCorner;

        obstacles = new Obstacle[theObstacles.getLength()];

        for (int i = 0; i < theObstacles.getLength(); i++) {
            tmp = ((Element) theObstacles.item(i))
                      .getElementsByTagName("UpLeftCorner");
            upLeftCorner = (Element) tmp.item(0);
            tmp = ((Element) theObstacles.item(i))
                      .getElementsByTagName("DownRightCorner");
            downRightCorner = (Element) tmp.item(0);
            obstacles[i] = new Obstacle(
                   new Vector(
                      Integer.parseInt(upLeftCorner.getAttribute("X")),
                      Integer.parseInt(upLeftCorner.getAttribute("Y"))),
                   new Vector(
                      Integer.parseInt(downRightCorner.getAttribute("X")),
                      Integer.parseInt(downRightCorner.getAttribute("Y"))));
        }
    }

    public class Obstacle {
        public Vector upLeftCorner;
        public Vector downRightCorner;

        public Obstacle(Vector upLeftCorner, Vector downRightCorner) {
            this.upLeftCorner = upLeftCorner;
            this.downRightCorner = downRightCorner;
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
     * @return une instance de Obstacle,
     *         ou null si l'identifiant fourni n'est pas celui d'un vaisseau
     */
    public Obstacle getObstacle(int i) {
        if (i > obstacles.length) {
            return null;
        }
        else {
            return (Obstacle) obstacles[i];
        }
    }

    

    /**
     * Renvoie le vaisseau dont l'identificateur est fourni.
     *
     * @param shipID
     *            l'identification du vaisseau à renvoyer
     * @return une instance de Ship, ou null si l'identifiant fourni n'est pas
     *         celui d'un vaisseau
     */
    public Ship getShip(int shipID) {
        return (Ship) ships[shipID];
    }

    /**
     * Renvoie l'ensemble des vaisseaux dans un tableau.
     *
     * @return le tableau des vaisseaux
     */
    public Ship[] getShips() {
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

    /*public void update(byte[] bytes) {        
        //  | Octet 0 | Octet 1- 6 | Octet 7-12 |... | 4bit | 4bit | | | |
        //  Flag4|nbShip| aShip | aShip |...        
        //Flag = (byte)((bytes[0] & 240) >> 4);
        nbShips = (byte) (bytes[0] & 15);

        index = 1;
        //System.out.println("nbShip :"+nbShips);
        for (int i = 0; i < nbShips; i++) {
            tmpByte[0] = bytes[index];
            tmpByte[1] = bytes[index + 1];
            tmpByte[2] = bytes[index + 2];
            tmpByte[3] = bytes[index + 3];
            tmpByte[4] = bytes[index + 4];
            tmpByte[5] = bytes[index + 5];

            tmpClientShip.setFromBytes(tmpByte);
            //setShip(tmpClientShip);
            
            if (tmpClientShip.getStates() == Ship.REMOVE) {
                if (Environment.debug) {
                    System.out
                            .println("[ClientArea.setShip] REMOVE ====> ships[id="
                                    + tmpClientShip.getId() + "]=null");
                }
                ships[tmpClientShip.getId()] = null;
            } else {
                if (ships[tmpClientShip.getId()] == null) {
                    ships[tmpClientShip.getId()] = new Ship();
                }
                ships[tmpClientShip.getId()].set((Ship) tmpClientShip);
                //System.out.println("Ship id maj :" +aShip.getId());
            }
            
            index = index + 6;
        }
    }*/
    
    /**
     * Cette méthode met à jour la zone locale avec le vaisseau (Ship) fourni
     * en paramètre. L'action effectuée dépend du champ <code>states</code>
     * du vaisseau. Cette méthode est appelée par ClientMainLoopThread sur
     * réception d'un message de type "SHIP".
     *
     * @param aShip
     *            le vaisseau à ajouter, à enlever ou à mettre à jour
     */
    /*public void setShip(Ship aShip) {
        
         //Si le vaisseau doit être enlevé, on le met à null dans le tableau
         //ships[]
         
        if (aShip.getStates() == Ship.REMOVE) {
            if (Environment.debug) {
                System.out
                        .println("[ClientArea.setShip] REMOVE ====> ships[id="
                                + aShip.getId() + "]=null");
            }
            ships[aShip.getId()] = null;
        } else {
            if (ships[aShip.getId()] == null) {
                ships[aShip.getId()] = new Ship();
            }
            ships[aShip.getId()].set((Ship) aShip);
            //System.out.println("Ship id maj :" +aShip.getId());
        }
    }*/
}
