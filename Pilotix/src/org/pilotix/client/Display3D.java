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
import org.pilotix.common.Ship;
import org.pilotix.common.Vector;
import org.pilotix.client.j3d.*;

import java.awt.GraphicsEnvironment;
import java.awt.GraphicsConfiguration;
import javax.media.j3d.GraphicsConfigTemplate3D;
import javax.media.j3d.Canvas3D;
import javax.media.j3d.VirtualUniverse;
import javax.media.j3d.Locale;
import javax.vecmath.Color3f;

/**
 * <p>
 * G�re l'affichage des �l�ments 3D.
 * </p>
 * 
 * <p>
 * Cette classe est cens�e �tre utilis�e conjointement � une instance de
 * GUIPanel. Nous avons essay� de s�parer les composants Java standards et ceux
 * li�s � Java3D. Cette classe est le conteneur principal pour les composants
 * Java3D, tandis que GUIPanel est le conteneur principal pour les composants
 * Java standards.
 * </p>
 *
 * @author Gr�goire Colbert
 * 
 * @see GUIPanel
 * 
 * @.todo
 * <ul>
 * <li>D�boguer l'affichage initial des vaisseaux, car ceux dont l'id est
 * inf�rieur � celui du joueur sont cr��s avant l'ajout de la cam�ra et on ne
 * les voit dans la vue principale qu'apr�s avoir boug�! Toute aide est la
 * bienvenue!</li>
 * </ul>
 */
public class Display3D {

    private VirtualUniverse universe = null;
    private Locale locale = null;
    private J3DShip[] shipsJ3D = null;
    private J3DCamera ownShip3DCamera = null;
    private Canvas3D mainCanvas3D = null;
    private Canvas3D minimapCanvas3D = null;
    private J3DMinimap minimap = null;
    private J3DObject[] objectsJ3D = null;

    /**
     * Cr�e un VirtualUniverse, une Locale, et deux Canvas3D. Le premier canvas
     * est pour le rendu de la vue principale. Le second canvas est pour le
     * rendu de la carte.
     */
    public Display3D() {
        // On cr�e l'univers et la locale. Ces instances dureront jusqu'� la
        // fermeture de l'application.
        universe = new VirtualUniverse();
        locale = new Locale(universe);

        GraphicsConfigTemplate3D template = new GraphicsConfigTemplate3D();
        GraphicsConfiguration config = GraphicsEnvironment
                .getLocalGraphicsEnvironment().getDefaultScreenDevice()
                .getBestConfiguration(template);
        mainCanvas3D = new Canvas3D(config);
        minimapCanvas3D = new Canvas3D(config);

        if (Environment.debug) {
            System.out.println("[Display3D] Constructeur");
        }
    }

    /**
     * Initialise les composants d'affichage 3D.
     */
    public void init() {
        // Cr�ation de la liste pour les J3DShip
        shipsJ3D = new J3DShip[15];

        // R�initialisation de la liste des J3DShips
        for (int i = 0; i < shipsJ3D.length; i++) {
            shipsJ3D[i] = null;
        }

        // Cr�ation de la liste pour les J3DObject
        objectsJ3D = new J3DObject[255];

        // R�initialisation de la liste des J3DObject
        for (int i = 0; i < objectsJ3D.length; i++) {
            objectsJ3D[i] = null;
        }

        // Cr�ation de la minimap
        float xMax = Environment.theClientArea.getXMax();
        float yMax = Environment.theClientArea.getYMax();
        minimap = new J3DMinimap(minimapCanvas3D, xMax / 2.0f, yMax / 2.0f);
        locale.addBranchGraph(minimap);

        // Ajuste la taille du Canvas3D de la minimap pour qu'on voit tout
        double w = minimapCanvas3D.getWidth();
        minimapCanvas3D.setSize((int) w, (int) (w * (yMax / xMax)));

        // Ajout du J3DArea en tant qu'objet n�0
        objectsJ3D[0] = new J3DArea(Environment.theClientArea.getXMax(),
                                    Environment.theClientArea.getYMax());
        locale.addBranchGraph(objectsJ3D[0]);

        // Ajout des obstacles d�finis dans ClientArea
        Vector upLeftCorner = null;
        Vector downRightCorner = null;
        for (int i = 0; i < Environment.theClientArea.getObstacles().length; i++) {
            upLeftCorner = Environment.theClientArea.getObstacle(i).upLeftCorner;
            downRightCorner = Environment.theClientArea.getObstacle(i).downRightCorner;
            objectsJ3D[i+1] = new J3DObstacle(upLeftCorner,downRightCorner);
            locale.addBranchGraph(objectsJ3D[i+1]);
        }
    }

    /**
     * R�initialise ce Display3D.
     */
    public void reset() {
        if (minimap != null) {
            if (Environment.debug) {
                System.out
                        .println("[Display3D.reset] Suppression imminente de la minimap");
            }
            minimap.getCamera().getView().removeCanvas3D(minimapCanvas3D);
            locale.removeBranchGraph(minimap);
            minimap = null;
        }
        for (int i = 0; i < shipsJ3D.length; i++) {
            if (shipsJ3D[i] != null) {
                if (Environment.debug) {
                    System.out
                            .println("[Display3D.reset] Retrait du vaisseau d'id="
                                    + i + " de la Locale");
                }
                locale.removeBranchGraph(shipsJ3D[i]);
                if (Environment.debug) {
                    System.out
                            .println("[Display3D.reset] Mise � null de shipsJ3D["
                                    + i + "]");
                }
                shipsJ3D[i] = null;
            }
        }
        if (ownShip3DCamera != null) {
            if (Environment.debug) {
                System.out
                        .println("[Display3D.reset] On d�tache ownShip3DCamera de mainCanvas3D");
            }
            ownShip3DCamera.getView().removeCanvas3D(mainCanvas3D);
            if (Environment.debug) {
                System.out
                        .println("[Display3D.reset] Mise � null de ownShip3DCamera");
            }
            ownShip3DCamera = null;
        }
        for (int i = 0; i < objectsJ3D.length; i++) {
            if (objectsJ3D[i] != null) {
                if (Environment.debug) {
                    System.out
                            .println("[Display3D.reset] Retrait de l'objet 3D n�"
                                    + i + " de la Locale");
                }
                locale.removeBranchGraph(objectsJ3D[i]);
                if (Environment.debug) {
                    System.out
                            .println("[Display3D.reset] Mise � null de objectsJ3D["
                                    + i + "]");
                }
                objectsJ3D[i] = null;
            }
        }
    }

    /**
     * Renvoie le Canvas3D de la vue principale.
     *
     * @return le Canvas3D principal
     */
    public Canvas3D getMainCanvas3D() {
        return mainCanvas3D;
    }

    /**
     * Renvoie le Canvas3D de la mini-carte.
     *
     * @return le Canvas3D secondaire
     */
    public Canvas3D getMinimapCanvas3D() {
        return minimapCanvas3D;
    }

    /**
     * Ajoute un vaisseau dans l'affichage 3D. Cette fonction est appel�e par
     * updateShip().
     * 
     * @param aShipId
     *            l'identifiant du vaisseau � ajouter
     * @param aPosition
     *            la position du vaisseau
     * @param aDirection
     *            la direction du vaisseau
     */
    private void addShip(int aShipId, Vector aPosition, Angle aDirection) {
        if (shipsJ3D[aShipId] == null) {
            Color3f thisShipColor = Environment.theXMLConfigHandler
                    .getColorFromId(aShipId);
            shipsJ3D[aShipId] = new J3DShip("wipeout.pilotix.shape.xml",thisShipColor);

            // Si c'est le vaisseau du joueur qu'on vient de cr�er, on lui
            // ajoute une cam�ra
            if (Environment.theClientArea.getOwnShipId() == aShipId) {
                if (Environment.debug) {
                    System.out
                            .println("[Display3D.addShip] ===> Ajout d'une cam�ra sur MON vaisseau (id="
                                    + aShipId + ")");
                }
                ownShip3DCamera = new J3DCamera(mainCanvas3D);
                ownShip3DCamera.setCoordinates(0.0f, 0.0f, 150.0f);
                shipsJ3D[aShipId].addCamera(ownShip3DCamera);
            }

            // Ajout du J3DShip dans la locale
            locale.addBranchGraph(shipsJ3D[aShipId]);

            if (Environment.debug) {
                if (Environment.theClientArea.getOwnShipId() == aShipId) {
                    System.out
                            .println("[Display3D.addShip] ===> Ajout dans Locale de MON vaisseau id="
                                    + aShipId);
                } else {
                    System.out
                            .println("[Display3D.addShip] ===> Ajout dans Locale vaisseau id="
                                    + aShipId);
                }
                System.out.println("[Display3D.addShip] La locale comporte "
                        + locale.numBranchGraphs() + " branches");
            }

            // Demande de rafra�chissement de la vue pour que l'on puisse voir
            // les branches de la locale d�j� pr�sentes avant l'ajout de la
            // cam�ra
            // Mais je n'ai pas encore trouv� la bonne m�thode, on dirait...
            /*
             * if (ownShip3DCamera!=null) {
             * ownShip3DCamera.getView().repaint(); } mainCanvas3D.validate();
             * minimap.getCamera().getView().repaint();
             * minimapCanvas3D.validate();
             */

            // Appel de setShip pour modifier la position, la direction et
            // l'�tat du vaisseau
            setShip(aShipId, aPosition, aDirection, Ship.ADD);
        } else {
            System.err
                    .println("[Display3D.addShip] ALERTE : utilisez setShip() et non addShip()"
                            + " car le vaisseau dont l'id est "
                            + aShipId
                            + " existe d�j� dans la liste shipsJ3D.");
            setShip(aShipId, aPosition, aDirection, Ship.NULL);
        }
    }

    /**
     * Retire un vaisseau de l'affichage 3D.
     * Cette fonction est appel�e par updateShip().
     *
     * @param aShipId
     *            l'identifiant du vaisseau � retirer
     */
    private void removeShip(int aShipId) {
        if (aShipId == Environment.theClientArea.getOwnShipId()) {
            if (Environment.debug) {
                System.out
                        .println("[Display3D.removeShip] On d�branche la cam�ra de MON vaisseau d'id="
                                + aShipId);
                System.out
                        .println("[Display3D.removeShip] ====> Retrait de MON vaisseau (id="
                                + aShipId + ")");
            }
            /*
             * Si le vaisseau est celui du joueur, on retire la cam�ra
             */
            shipsJ3D[Environment.theClientArea.getOwnShipId()].getCamera()
                    .getView().removeCanvas3D(mainCanvas3D);
        }
        else if (Environment.debug) {
            System.out
                    .println("[Display3D.removeShip] ====> Retrait d'un AUTRE vaisseau (id="
                            + aShipId + ")");
            // Gardons ce test sous le coude pour le jour o� on pourra comprendre
            // pourquoi la m�thode removeShip est parfois appel�e deux fois au lieu
            // d'une...
            // 18/03/2004 : normalement cette erreur ne doit PLUS JAMAIS se produire,
            // donc le test que shipsJ3D[aShipId] n'est pas nul avant de toucher � la
            // locale devrait �tre inutile.
            if (shipsJ3D[aShipId] == null) {
                System.out.println("[Display3D.removeShip] DIABLE - shipsJ3D["
                        + aShipId + "]==null! VEUILLEZ SIGNALER CETTE ERREUR SVP!");
            }
        }

        // On retire une branche de la locale.
        locale.removeBranchGraph(shipsJ3D[aShipId]);
        if (Environment.debug) {
            System.out.println("[Display3D.removeShip] shipsJ3D[id="
                    + aShipId + "]=null");
        }
        shipsJ3D[aShipId] = null;
    }

    /**
     * Modifie les param�tres d'un vaisseau existant dans l'affichage 3D. Cette
     * fonction est appel�e par updateShip().
     *
     * @param aShipId
     *            l'identifiant du vaisseau � modifier
     * @param aPosition
     *            la position du vaisseau
     * @param aDirection
     *            la direction du vaisseau
     * @param aState
     *            l'�tat du vaisseau
     */
    private void setShip(int aShipId, Vector aPosition, Angle aDirection,
            int aState) {
        if (shipsJ3D[aShipId] != null) {
            shipsJ3D[aShipId].setPosition(aPosition);
            shipsJ3D[aShipId].setDirection(aDirection);
            shipsJ3D[aShipId].setAltitude(100);
            //shipsJ3D[aShipId].setState(aState); // A FAIRE PLUS TARD
        } else {
            System.err.println("[Display3D.setShip] ERREUR: ship[" + aShipId
                    + "]==null");
        }
    }

    /**
     * Cr�e, met � jour l'�tat, ou supprime un vaisseau dans l'affichage 3D, en
     * consultant l'�tat du vaisseau dans le tableau "ships" de ClientArea pour
     * savoir ce qu'il faut faire.
     * Cette fonction est appel�e par la fonction update().
     *
     * @param aShipId
     *            l'identifiant du vaisseau � modifier
     */
    private void updateShip(int aShipId) {
        // Si le ship dans ClientArea n'est pas null...
        if (Environment.theClientArea.getShip(aShipId) != null) {
            if (shipsJ3D[aShipId] != null) {
                // ...et le J3DShip non plus, on met simplement � jour la vue 3D
                setShip(aShipId,
                        Environment.theClientArea.getShip(aShipId).getPosition(),
                        Environment.theClientArea.getShip(aShipId).getDirection(),
                        Environment.theClientArea.getShip(aShipId).getStates());
            }
            else {
                // ...sinon si seul le J3DShip est null, on ajoute ce vaisseau
                addShip(aShipId,
                        Environment.theClientArea.getShip(aShipId).getPosition(),
                        Environment.theClientArea.getShip(aShipId).getDirection());
            }
        }
        else if (shipsJ3D[aShipId]!=null) {
            // Sinon si le ship est null mais pas le shipJ3D,
            // il faut faire dispara�tre le shipJ3D
            removeShip(aShipId);
        }
        // Si le ship est null, et le J3DShip aussi, on ne fait rien
    }

    /**
     * Cette fonction sert � synchroniser la vue en 3D avec l'�tat du jeu tel qu'il
     * appara�t dans ClientArea au moment de l'appel. Elle est appel�e par
     * ClientMainLoopThread.
     */
    public void update() {
        for (int i = 0; i < shipsJ3D.length; i++) {
            updateShip(i);
        }
    }
}
