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
import java.awt.event.KeyListener;
import java.awt.event.KeyEvent;

import javax.media.j3d.BranchGroup;
import javax.media.j3d.Canvas3D;
import javax.media.j3d.GraphicsConfigTemplate3D;
import javax.media.j3d.Locale;
import javax.media.j3d.VirtualUniverse;
import javax.vecmath.Color3f;
import javax.media.j3d.Light;

import org.pilotix.client.ClientArea.Obstacle;
import org.pilotix.client.j3d.J3DArea;
import org.pilotix.client.j3d.J3DBall;
import org.pilotix.client.j3d.J3DCamera;
import org.pilotix.client.j3d.J3DMinimap;
import org.pilotix.client.j3d.J3DObstacle;
import org.pilotix.client.j3d.J3DShip;
import org.pilotix.client.j3d.J3DObject;
import org.pilotix.common.Angle;
import org.pilotix.common.Ball;
import org.pilotix.common.IterableArray;
import org.pilotix.common.Ship;
import org.pilotix.common.Vector;
import org.pilotix.common.IterableArray.Action;

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
 */
public class Display3D implements KeyListener {

    private VirtualUniverse universe = null;
    private Locale locale = null;
    private J3DCamera ownShip3DCamera = null;
    private Canvas3D mainCanvas3D = null;
    private Canvas3D minimapCanvas3D = null;
    private J3DArea areaJ3D = null;
    // Groupe des objets que la lumi�re ne doit pas traverser (est-ce bien �a?)
    private BranchGroup lightLimitsGroup = null;

    private IterableArray shipsJ3D = new IterableArray(
        Environment.theClientArea.getNbMaxShips());
    private IterableArray ballsJ3D = new IterableArray(
        Environment.theClientArea.getNbMaxBalls());
    private IterableArray obstaclesJ3D = new IterableArray(
        Environment.theClientArea.getNbMaxObstacles());

    private J3DMinimap minimapJ3D;

    // L'interface Action est definie dans IterableArray
    private Action actionShips = new Action() {

        public Object add(Object aShip) {
            Ship ship = (Ship) aShip;
            System.out.println("[Display3D] New Ship id="+ship.getId());

            /*
            J3DObject shipJ3D = new J3DObject(
             "wipeout.pilotix.shape.xml",
             Environment.clientConfig.getColorFromId(ship.getId()),
             ship.getPosition(),
             ship.getDirection());
            */

            // Cette m�thode permet d'avoir les �clairages (g�r�s dans J3DShip)
            // mais � terme il faudra coder les lumi�res dans le fichier XML
            // et on pourra alors utiliser la m�thode ci-dessus mise en commentaire
            J3DShip shipJ3D = new J3DShip(
                "wipeout.pilotix.shape.xml",
                Environment.clientConfig.getColorFromId(ship.getId()));
            shipJ3D.setPosition(ship.getPosition());
            shipJ3D.setDirection(ship.getDirection());

            // Ajout des limites (scopes) pour les lumi�res de ce vaisseau
            // (pour l'instant on se limite aux obstaclesJ3D)
/*
            for (int i=0; i<shipJ3D.lights.length; i++) {
                for (int j=0; j<obstaclesJ3D.size(); j++) {
                    System.out.println("Ajout de l'obstacle n�"+j+" pour la lumi�re n�"+i);
                    shipJ3D.lights[i].addScope((BranchGroup)obstaclesJ3D.get(j));
                }
                for (int j=0; j<shipsJ3D.size(); j++) {
                    System.out.println("Ajout du J3DShip n�"+j+" pour la lumi�re n�"+i);
                    shipJ3D.lights[i].addScope((BranchGroup)shipsJ3D.get(j));
                }
            }
*/
            if (ship.getId() == Environment.theClientArea.getOwnShipId()) {
                shipJ3D.addCamera(ownShip3DCamera);
            }
            locale.addBranchGraph(shipJ3D);
            return shipJ3D;
        }

        public void update(Object modele, Object modifie) {
            ((J3DShip) modifie).setPosition(((Ship) modele).getPosition());
            ((J3DShip) modifie).setDirection(((Ship) modele).getDirection());
        }

        public void remove(Object object){
            locale.removeBranchGraph((J3DShip)object);
        }
    };

    private Action actionBalls = new Action() {

        public Object add(Object aBall) {
            Ball ball = (Ball) aBall;
            System.out.println("[Display3D] Add Ball id="+ball.getId());
            J3DBall ballJ3D = new J3DBall(ball.getPosition(), 10);
            locale.addBranchGraph(ballJ3D);
            return ballJ3D;
        }

        public void update(Object modele, Object modifie) {
            ((J3DBall) modifie).setPosition(((Ball) modele).getPosition());
        }

        public void remove(Object object){
            locale.removeBranchGraph((J3DBall)object);
            System.out.println("[Display3D] Remove J3DBall");
        }
    };

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
        GraphicsConfiguration config = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getBestConfiguration(
            template);
        mainCanvas3D = new Canvas3D(config);
        minimapCanvas3D = new Canvas3D(config);

        if (Environment.debug) {
            System.out.println("[Display3D] Constructeur");
        }
    }

    public void init() {
        // Ce Display3D doit �tre dans les �couteurs de Controls
        // (il doit r�agir quand l'utilisateur veut d�placer la cam�ra)
        Environment.theControls.addListener(this);

        float xMax = Environment.theClientArea.getXMax();
        float yMax = Environment.theClientArea.getYMax();

        minimapJ3D = new J3DMinimap(minimapCanvas3D, xMax / 2.0f, yMax / 2.0f);

        locale.addBranchGraph(minimapJ3D);

        // Ajuste la taille du Canvas3D de la minimap pour qu'on voit tout
        double w = minimapCanvas3D.getWidth();
        minimapCanvas3D.setSize((int) w, (int) (w * (yMax / xMax)));

        // Ajout du J3DArea dans la Locale
        areaJ3D = new J3DArea(xMax, yMax);
        locale.addBranchGraph(areaJ3D);

        // Ajout des obstacles dans objectsJ3D (d�finis dans ClientArea)
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
        ownShip3DCamera.lookAtOriginRotX(100.0f, (float)Math.PI / 2.0f);
    }

    /**
     * R�initialise ce Display3D.
     */
    public void reset() {
        // Ce Display3D ne doit plus �tre dans les �couteurs de Controls
        Environment.theControls.removeListener(this);

        // Si la J3DMinimap n'est pas nulle, on la d�tache du Canvas3D
        if (minimapJ3D != null) {
            minimapJ3D.getCamera().getView().removeCanvas3D(minimapCanvas3D);
            locale.removeBranchGraph(minimapJ3D);
            minimapJ3D = null;
        }
        // Si la camera du joueur actif n'est pas nulle, on la d�tache du Canvas3D
        if (ownShip3DCamera != null) {
            ownShip3DCamera.getView().removeCanvas3D(mainCanvas3D);
            ownShip3DCamera = null;
        }

        // On vide la Locale (oui, deux boucles sont necessaires)
        BranchGroup array_branchgroups[] = new BranchGroup[locale.numBranchGraphs()];
        int i = 0;
        for (java.util.Enumeration bgs = locale.getAllBranchGraphs(); bgs.hasMoreElements() ;) {
            array_branchgroups[i] = (BranchGroup)bgs.nextElement();
            i++;
        }
        for (int j=0; j < i; j++) {
            locale.removeBranchGraph((BranchGroup) array_branchgroups[j]);
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
     * Cette fonction sert � synchroniser la vue en 3D avec l'�tat du jeu tel
     * qu'il appara�t dans ClientArea au moment de l'appel. Elle est appel�e par
     * ClientMainLoopThread.
     * Elle effectue un parcours des ships et met � jour les shipsJ3D
     */
    public void update() {
        // Mise a jour des vaisseaux
        IterableArray ships = Environment.theClientArea.getShips();
        ships.copyInto(shipsJ3D, actionShips);

        // Mise a jour des balles
        IterableArray balls = Environment.theClientArea.getBalls();
        balls.copyInto(ballsJ3D, actionBalls);
    }

    /**
     * R�pond aux �v�nements claviers et souris relatifs � l'affichage.
     */
    public void keyTyped(KeyEvent e) {
    }

    /**
     * R�pond aux �v�nements claviers et souris relatifs � l'affichage.
     */
    public void keyPressed(KeyEvent e) {
        if (e.getKeyChar() == '+') {
            ownShip3DCamera.lookAtOriginRotX(ownShip3DCamera.getDistanceFromParent()-2.5f,
                                             ownShip3DCamera.getAngleYOZ());
        }
        else if (e.getKeyChar() == '-') {
            ownShip3DCamera.lookAtOriginRotX(ownShip3DCamera.getDistanceFromParent()+2.5f,
                                             ownShip3DCamera.getAngleYOZ());
        }
        else if (e.getKeyChar() == '0') {
            ((J3DShip)shipsJ3D.get(Environment.theClientArea.getOwnShipId())).cameraRotationSwitch();
        }
        else if (e.getKeyChar() == '2') {
            ownShip3DCamera.lookAtOriginRotX(ownShip3DCamera.getDistanceFromParent(),
                                             ownShip3DCamera.getAngleYOZ()-(float)Math.PI/72.0f);
        }
        else if (e.getKeyChar() == '5') {
            ownShip3DCamera.lookAtOriginRotX(ownShip3DCamera.getDistanceFromParent(),
                                             (float)Math.PI/2.0f);
        }
        else if (e.getKeyChar() == '8') {
            ownShip3DCamera.lookAtOriginRotX(ownShip3DCamera.getDistanceFromParent(),
                                             ownShip3DCamera.getAngleYOZ()+(float)Math.PI/72.0f);
        }
    }

    /**
     * R�pond aux �v�nements claviers et souris relatifs � l'affichage.
     */
    public void keyReleased(KeyEvent e) {

    }

}
