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

import java.awt.GraphicsConfiguration;
import java.awt.GraphicsEnvironment;

import javax.media.j3d.BranchGroup;
import javax.media.j3d.Canvas3D;
import javax.media.j3d.GraphicsConfigTemplate3D;
import javax.media.j3d.Locale;
import javax.media.j3d.VirtualUniverse;
import javax.vecmath.Color3f;

import org.pilotix.client.ClientArea.Obstacle;
import org.pilotix.client.j3d.J3DArea;
import org.pilotix.client.j3d.J3DBall;
import org.pilotix.client.j3d.J3DCamera;
import org.pilotix.client.j3d.J3DMinimap;
import org.pilotix.client.j3d.J3DObstacle;
import org.pilotix.client.j3d.J3DShip;
import org.pilotix.common.Angle;
import org.pilotix.common.Ball;
import org.pilotix.common.IterableArray;
import org.pilotix.common.Ship;
import org.pilotix.common.Vector;
import org.pilotix.common.IterableArray.Action;

/**
 * <p>
 * Gère l'affichage des éléments 3D.
 * </p>
 *
 * <p>
 * Cette classe est censée être utilisée conjointement à une instance de
 * GUIPanel. Nous avons essayé de séparer les composants Java standards et ceux
 * liés à Java3D. Cette classe est le conteneur principal pour les composants
 * Java3D, tandis que GUIPanel est le conteneur principal pour les composants
 * Java standards.
 * </p>
 *
 * @author Grégoire Colbert
 *
 * @see GUIPanel
 */
public class Display3D {

    private VirtualUniverse universe = null;
    private Locale locale = null;
    private J3DCamera ownShip3DCamera = null;
    private Canvas3D mainCanvas3D = null;
    private Canvas3D minimapCanvas3D = null;

    private IterableArray shipsJ3D = new IterableArray(
        Environment.theClientArea.getNbMaxShips());
    private IterableArray ballsJ3D = new IterableArray(
        Environment.theClientArea.getNbMaxShips());
    private IterableArray obstaclesJ3D = new IterableArray(
        Environment.theClientArea.getNbMaxShips());

    private J3DMinimap minimapJ3D;

    /**
     * Crée un VirtualUniverse, une Locale, et deux Canvas3D. Le premier canvas
     * est pour le rendu de la vue principale. Le second canvas est pour le
     * rendu de la carte.
     */
    public Display3D() {
        // On crée l'univers et la locale. Ces instances dureront jusqu'à la
        // fermeture de l'application.
        universe = new VirtualUniverse();
        locale = new Locale(universe);

        GraphicsConfigTemplate3D template = new GraphicsConfigTemplate3D();
        GraphicsConfiguration config = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getBestConfiguration(
            template);
        mainCanvas3D = new Canvas3D(config);
        minimapCanvas3D = new Canvas3D(config);

        if (Environment.debug) {
            System.out.println("[Display3D] Constructeur");
        }
    }

    public void init() {

        float xMax = Environment.theClientArea.getXMax();
        float yMax = Environment.theClientArea.getYMax();

        minimapJ3D = new J3DMinimap(minimapCanvas3D, xMax / 2.0f, yMax / 2.0f);

        locale.addBranchGraph(minimapJ3D);

        // Ajuste la taille du Canvas3D de la minimap pour qu'on voit tout
        double w = minimapCanvas3D.getWidth();
        minimapCanvas3D.setSize((int) w, (int) (w * (yMax / xMax)));

        // Ajout du J3DArea dans objectsJ3D
        locale.addBranchGraph(new J3DArea(xMax, yMax));

        // Ajout des obstacles dans objectsJ3D (définis dans ClientArea)
        J3DObstacle tmpObstacleJ3D;
        Obstacle tmpObstacle;
        IterableArray obstacles = Environment.theClientArea.getObstacles();
        for (int i = 0; i < obstacles.size(); i++) {
            tmpObstacle = (Obstacle) obstacles.get(i);
            tmpObstacleJ3D = new J3DObstacle(
                tmpObstacle.upLeftCorner,
                tmpObstacle.downRightCorner,
                tmpObstacle.height,
                tmpObstacle.altitude,
                tmpObstacle.topTexture,
                tmpObstacle.sideTexture);
            obstaclesJ3D.add(i, tmpObstacleJ3D);
            locale.addBranchGraph(tmpObstacleJ3D);
        }

        ownShip3DCamera = new J3DCamera(mainCanvas3D);
        ownShip3DCamera.setCoordinates(0.0f, 0.0f, 150.0f);
    }

    /**
     * Réinitialise ce Display3D.
     */
    public void reset() {
        // Si la J3DMinimap n'est pas nulle, on la détache du Canvas3D
        if (minimapJ3D != null) {
            //System.out.println("[Display3D.reset()] Suppression de la J3DMinimap");
            minimapJ3D.getCamera().getView().removeCanvas3D(minimapCanvas3D);
        }
        if (ownShip3DCamera != null) {
            ownShip3DCamera.getView().removeCanvas3D(mainCanvas3D);
            ownShip3DCamera = null;
        }

        obstaclesJ3D.clear();
        shipsJ3D.clear();
        ballsJ3D.clear();
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
     * Cette fonction sert à synchroniser la vue en 3D avec l'état du jeu tel
     * qu'il apparaît dans ClientArea au moment de l'appel. Elle est appelée par
     * ClientMainLoopThread.
     * Elle effectue un parcours des ships et met à jour les shipsJ3D
     */
    public void update() {
        // Mise a jour des vaisseaux
        IterableArray ships = Environment.theClientArea.getShips();
        ships.copyInto(shipsJ3D, actionShips);

        // Mise a jour des balles
        IterableArray balls = Environment.theClientArea.getBalls();
        balls.copyInto(ballsJ3D, actionBalls);
    }

    Action actionShips = new Action() {

        public Object add(Object aShip) {
            Ship ship = (Ship) aShip;
            System.out.println("[Display3D] New Ship id="+ship.getId());

            J3DShip shipJ3D = new J3DShip(
                "wipeout.pilotix.shape.xml",
                Environment.clientConfig.getColorFromId(ship.getId()));
            shipJ3D.setPosition(ship.getPosition());
            shipJ3D.setDirection(ship.getDirection());

            /*J3DObject shipJ3D = new J3DObject(
             "wipeout.pilotix.shape.xml",
             Environment.clientConfig.getColorFromId(ship.getId()),
             ship.getPosition(),
             ship.getDirection());*/

            if (ship.getId() == Environment.theClientArea.getOwnShipId()) {
                shipJ3D.addCamera(ownShip3DCamera);
            }
            locale.addBranchGraph(shipJ3D);
            return shipJ3D;
        };

        public void update(Object modele, Object modifie) {
            ((J3DShip) modifie).setPosition(((Ship) modele).getPosition());
            ((J3DShip) modifie).setDirection(((Ship) modele).getDirection());
        }

        public void remove(Object object){
            locale.removeBranchGraph((J3DShip)object);
            System.out.println("[Display3D] Remove Ship");
        }
    };

    Action actionBalls = new Action() {

        public Object add(Object aBall) {
            Ball ball = (Ball) aBall;
            System.out.println("[Display3D] Add Ball id="+ball.getId());
            J3DBall ballJ3D = new J3DBall(ball.getPosition(), 10);
            locale.addBranchGraph(ballJ3D);
            return ballJ3D;
        };

        public void update(Object modele, Object modifie) {
            ((J3DBall) modifie).setPosition(((Ball) modele).getPosition());
        }
        public void remove(Object object){
            locale.removeBranchGraph((J3DBall)object);
            System.out.println("[Display3D] Remove Ball");
        }
    };
}

    /**
     * Initialise les composants d'affichage 3D.
     * Les objets sont stockés dans un IterableArray selon un ordre
     * bien précis :
     * <pre>
     * 0                 | J3DShip du joueur d'id=0
     * ...               | ...
     * getNbMaxShips()-1 | Dernier J3DShip possible (lu dans ClientArea)
     * getNbMaxShips()   | J3DObstacle n°1
     * ...               | ...
     * j                 | Dernier J3DObstacle (lu dans ClientArea)
     * ...               | ...
     * nbMaxObjectsJ3D-2 | J3DMinimap
     * nbMaxObjectsJ3D-1 | J3DArea
     * </pre>
     */
    /*
    public void init() {
        // Création du conteneur pour les J3DBalls
        ballsJ3D = new IterableArray(Environment.theClientArea.getNbMaxShips());

        // Création du conteneur pour les J3DObject
        objectsJ3D = new IterableArray(nbMaxObjectsJ3D);

        // Ajout de la minimap dans objectsJ3D
        float xMax = Environment.theClientArea.getXMax();
        float yMax = Environment.theClientArea.getYMax();
        objectsJ3D.add(nbMaxObjectsJ3D - 2, new J3DMinimap(
            minimapCanvas3D,
            xMax / 2.0f,
            yMax / 2.0f));
        locale.addBranchGraph((J3DMinimap) objectsJ3D.get(nbMaxObjectsJ3D - 2));

        // Ajuste la taille du Canvas3D de la minimap pour qu'on voit tout
        double w = minimapCanvas3D.getWidth();
        minimapCanvas3D.setSize((int) w, (int) (w * (yMax / xMax)));

        // Ajout du J3DArea dans objectsJ3D
        objectsJ3D.add(nbMaxObjectsJ3D - 1, new J3DArea(
            Environment.theClientArea.getXMax(),
            Environment.theClientArea.getYMax()));
        locale.addBranchGraph((BranchGroup) objectsJ3D.get(nbMaxObjectsJ3D - 1));

        // Ajout des obstacles dans objectsJ3D (définis dans ClientArea)
        Vector upLeftCorner = null;
        Vector downRightCorner = null;
        int altitude = 0;
        int height = 0;
        String topTexture = null;
        String sideTexture = null;
        for (int i = 0; i < Environment.theClientArea.getObstacles().size(); i++) {
            upLeftCorner = Environment.theClientArea.getObstacle(i).upLeftCorner;
            downRightCorner = Environment.theClientArea.getObstacle(i).downRightCorner;
            topTexture = Environment.theClientArea.getObstacle(i).topTexture;
            sideTexture = Environment.theClientArea.getObstacle(i).sideTexture;
            altitude = Environment.theClientArea.getObstacle(i).altitude;
            height = Environment.theClientArea.getObstacle(i).height;
            objectsJ3D.add(
                Environment.theClientArea.getNbMaxShips() + i,
                new J3DObstacle(
                    upLeftCorner,
                    downRightCorner,
                    height,
                    altitude,
                    topTexture,
                    sideTexture));
            locale.addBranchGraph((BranchGroup) objectsJ3D.get(Environment.theClientArea.getNbMaxShips()
                + i));
        }
    }
    */

    /**
     * Réinitialise ce Display3D.
     */
     /* N'EST PLUS UTILISE
    public void reset() {
        // Si la J3DMinimap n'est pas nulle, on la détache du Canvas3D
        if (!objectsJ3D.isNull(nbMaxObjectsJ3D - 2)) {
            System.out.println("[Display3D.reset()] Suppression de la J3DMinimap");
            ((J3DMinimap) objectsJ3D.get(nbMaxObjectsJ3D - 2)).getCamera().getView().removeCanvas3D(
                minimapCanvas3D);
        }
        if (ownShip3DCamera != null) {
            ownShip3DCamera.getView().removeCanvas3D(mainCanvas3D);
            ownShip3DCamera = null;
        }

        objectsJ3D.clear();
        ballsJ3D.clear();
    }
    */

    /**
     * Cette fonction sert à synchroniser la vue en 3D avec l'état du jeu tel
     * qu'il apparaît dans ClientArea au moment de l'appel. Elle est appelée par
     * ClientMainLoopThread.
     * Elle effectue un parcours des ships et met à jour les shipsJ3D
     */
     /* N'EST PLUS UTILISE
    public void update() {
        for (int i = 0; i < Environment.theClientArea.getNbMaxShips(); i++) {
            updateBall(i);
        }
        for (int i = 0; i < Environment.theClientArea.getNbMaxShips(); i++) {
            updateShip(i);
        }
    }
    */

    /**
     * Ajoute un vaisseau dans l'affichage 3D. Cette fonction est appelée par
     * updateShip().
     *
     * @param aShipId
     *            l'identifiant du vaisseau à ajouter
     * @param aPosition
     *            la position du vaisseau
     * @param aDirection
     *            la direction du vaisseau
     * @param aAltitude
     *            l'altitude du vaisseau
     */
     /*
    private void addShip(int aShipId, Vector aPosition, Angle aDirection,
            int aAltitude) {
        if (objectsJ3D.isNull(aShipId)) {
            Color3f thisShipColor = Environment.clientConfig.getColorFromId(aShipId);
            objectsJ3D.add(aShipId, new J3DShip(
                "wipeout.pilotix.shape.xml",
                thisShipColor));

            // Si c'est le vaisseau du joueur qu'on vient de créer, on lui
            // ajoute une caméra
            if (Environment.theClientArea.getOwnShipId() == aShipId) {
                ownShip3DCamera = new J3DCamera(mainCanvas3D);
                ownShip3DCamera.setCoordinates(0.0f, 0.0f, 150.0f);
                ((J3DShip) objectsJ3D.get(aShipId)).addCamera(ownShip3DCamera);
            }

            // Ajout du J3DShip dans la locale
            locale.addBranchGraph((BranchGroup) objectsJ3D.get(aShipId));

            // Appel de setShip pour modifier la position, la direction et
            // l'état du vaisseau
            setShip(aShipId, aPosition, aDirection, aAltitude, Ship.ADD);
        } else {
            System.err.println("[Display3D.addShip] ALERTE : utilisez setShip() et non addShip()"
                + " car le vaisseau dont l'id est "
                + aShipId
                + " existe déjà dans la liste objectsJ3D.");
            setShip(aShipId, aPosition, aDirection, aAltitude, Ship.NULL);
        }
    }
*/

    /**
     * Retire un vaisseau de l'affichage 3D. Cette fonction est appelée par
     * updateShip().
     *
     * @param aShipId
     *            l'identifiant du vaisseau à retirer
     */
     /*
    private void removeShip(int aShipId) {
        if (aShipId == Environment.theClientArea.getOwnShipId()) {
            // Si le vaisseau est celui du joueur, on retire la caméra
            ((J3DShip) objectsJ3D.get(Environment.theClientArea.getOwnShipId())).getCamera().getView().removeCanvas3D(
                mainCanvas3D);
        }
        // On retire une branche de la locale.
        locale.removeBranchGraph((BranchGroup) objectsJ3D.get(aShipId));
        // On retire le vaisseau de la liste des objets 3D
        objectsJ3D.remove(aShipId);
    }
*/
    /**
     * Modifie les paramètres d'un vaisseau existant dans l'affichage 3D. Cette
     * fonction est appelée par updateShip().
     *
     * @param aShipId
     *            l'identifiant du vaisseau à modifier
     * @param aPosition
     *            la position du vaisseau
     * @param aDirection
     *            la direction du vaisseau
     * @param aAltitude
     *            l'altitude du vaisseau
     * @param aState
     *            l'état du vaisseau
     */
     /*
    private void setShip(int aShipId, Vector aPosition, Angle aDirection,
            int aAltitude, int aState) {
        if (!objectsJ3D.isNull(aShipId)) {
            ((J3DShip) objectsJ3D.get(aShipId)).setPosition(aPosition);
            ((J3DShip) objectsJ3D.get(aShipId)).setDirection(aDirection);
            ((J3DShip) objectsJ3D.get(aShipId)).setAltitude(aAltitude);
            //((J3DShip)objectsJ3D.get("ship"+aShipId)).setState(aState); // A
            // FAIRE PLUS TARD
        } else {
            System.err.println("[Display3D.setShip] ERREUR: ship[" + aShipId
                + "]==null");
        }
    }
*/
    /**
     * Crée, met à jour l'état, ou supprime un vaisseau dans l'affichage 3D, en
     * consultant l'état du vaisseau dans le tableau "ships" de ClientArea pour
     * savoir ce qu'il faut faire. Cette fonction est appelée par la fonction
     * update().
     *
     * @param aShipId
     *            l'identifiant du vaisseau à modifier
     */
    /* N'EST PLUS UTILISE
    private void updateShip(int aShipId) {
        // Si le ship dans ClientArea n'est pas null...
        if (!Environment.theClientArea.shipIsNull(aShipId)) {
            if (!objectsJ3D.isNull(aShipId)) {
                // ...et le J3DShip non plus, on met simplement à jour la vue 3D
                setShip(
                    aShipId,
                    Environment.theClientArea.getShipPosition(aShipId),
                    Environment.theClientArea.getShipDirection(aShipId),
                    100,
                    Environment.theClientArea.getShipStates(aShipId));
            } else {
                // ...sinon si seul le J3DShip est null, on ajoute ce vaisseau
                addShip(
                    aShipId,
                    Environment.theClientArea.getShipPosition(aShipId),
                    Environment.theClientArea.getShipDirection(aShipId),
                    100);
            }
        } else if (!objectsJ3D.isNull(aShipId)) {
            // Sinon si le ship est null mais pas le shipJ3D,
            // il faut faire disparaître le shipJ3D
            removeShip(aShipId);
        }
        // Si le ship est null, et le J3DShip aussi, on ne fait rien
    }
    */

    /**
     * Crée, met à jour l'état, ou supprime une balle dans l'affichage 3D, en
     * consultant l'état de la balle dans le tableau "balls" de Area pour
     * savoir ce qu'il faut faire. Cette fonction est appelée par la fonction
     * update().
     *
     * @param aBallId
     *            l'identifiant de la balle à modifier
     */
     /* N'EST PLUS UTILISE
    private void updateBall(int aBallId) {
        // Si la balle dans ClientArea n'est pas nulle...
        if (!Environment.theClientArea.ballIsNull(aBallId)) {
            if (!ballsJ3D.isNull(aBallId)) {
                // ...et le J3DBall non plus, on met simplement à jour la vue 3D
                Environment.theClientArea.getBall(aBallId).nextFrame();
                ((J3DBall) ballsJ3D.get(aBallId)).setPosition(Environment.theClientArea.getBallPosition(aBallId));
            } else {
                // ...sinon si seule la J3DBall est nulle, on ajoute cette balle
                ballsJ3D.add(aBallId, new J3DBall(
                    Environment.theClientArea.getBallPosition(aBallId),
                    100));
                locale.addBranchGraph((BranchGroup) ballsJ3D.get(aBallId));
            }
        } else if (!ballsJ3D.isNull(aBallId)) {
            // Sinon si la balle est nulle mais pas la J3DBall,
            // il faut faire disparaître la J3DBall.
            // On retire une branche de la locale.
            locale.removeBranchGraph((BranchGroup) ballsJ3D.get(aBallId));
            // On retire la balle de la liste des balles
            ballsJ3D.remove(aBallId);
        }
        // Si la balle est nulle dans ClientArea et dans ballsJ3D,
        // on ne fait rien
    }
    */
