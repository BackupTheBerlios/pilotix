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

import java.util.TreeMap;
import java.util.LinkedList;
import java.awt.GraphicsEnvironment;
import java.awt.GraphicsConfiguration;
import javax.media.j3d.GraphicsConfigTemplate3D;
import javax.media.j3d.Canvas3D;
import javax.media.j3d.VirtualUniverse;
import javax.media.j3d.Locale;
import javax.media.j3d.BranchGroup;
import javax.vecmath.Color3f;

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
    private TreeMap objectsJ3D = null;
    private J3DCamera ownShip3DCamera = null;
    private Canvas3D mainCanvas3D = null;
    private Canvas3D minimapCanvas3D = null;

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
        // Création du conteneur pour les J3DObject
        objectsJ3D = new TreeMap();

        // Ajout de la minimap dans objectsJ3D
        float xMax = Environment.theClientArea.getXMax();
        float yMax = Environment.theClientArea.getYMax();
        objectsJ3D.put("J3DMinimap",
                    new J3DMinimap(minimapCanvas3D, xMax / 2.0f, yMax / 2.0f));
        locale.addBranchGraph((J3DMinimap)objectsJ3D.get("J3DMinimap"));

        // Ajuste la taille du Canvas3D de la minimap pour qu'on voit tout
        double w = minimapCanvas3D.getWidth();
        minimapCanvas3D.setSize((int) w, (int) (w * (yMax / xMax)));

        // Ajout du J3DArea dans objectsJ3D
        objectsJ3D.put("J3DArea",
                       new J3DArea(Environment.theClientArea.getXMax(),
                                    Environment.theClientArea.getYMax()));
        locale.addBranchGraph((BranchGroup)objectsJ3D.get("J3DArea"));

        // Ajout des obstacles dans objectsJ3D (définis dans ClientArea)
        Vector upLeftCorner = null;
        Vector downRightCorner = null;
        int altitude = 0;
        int height = 0;
        String texture = null;
        for (int i = 0; i < Environment.theClientArea.getObstacles().length; i++) {
            upLeftCorner = Environment.theClientArea.getObstacle(i).upLeftCorner;
            downRightCorner = Environment.theClientArea.getObstacle(i).downRightCorner;
            texture = Environment.theClientArea.getObstacle(i).texture;
            altitude = Environment.theClientArea.getObstacle(i).altitude;
            height = Environment.theClientArea.getObstacle(i).height;
            objectsJ3D.put("obstacle"+i,
                           new J3DObstacle(upLeftCorner,downRightCorner,height,altitude,texture));
            locale.addBranchGraph((BranchGroup)objectsJ3D.get("obstacle"+i));
        }
    }

    /**
     * Réinitialise ce Display3D.
     */
    public void reset() {
        if (objectsJ3D.containsKey("J3DMinimap")) {
            ((J3DMinimap)objectsJ3D.get("J3DMinimap"))
                  .getCamera().getView().removeCanvas3D(minimapCanvas3D);
        }
        if (ownShip3DCamera != null) {
            ownShip3DCamera.getView().removeCanvas3D(mainCanvas3D);
            ownShip3DCamera = null;
        }

        LinkedList tmplist = new LinkedList(objectsJ3D.values());
        for (int i=0; i<tmplist.size(); i++) {
            if (Environment.debug) {
                System.out.println("[Display3D.reset] locale -= "
                               +tmplist.get(i).toString());
            }
            locale.removeBranchGraph((BranchGroup)tmplist.get(i));
        }
        objectsJ3D.clear();
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
     * Ajoute un vaisseau dans l'affichage 3D. Cette fonction est appelée par
     * updateShip().
     * 
     * @param aShipId
     *            l'identifiant du vaisseau à ajouter
     * @param aPosition
     *            la position du vaisseau
     * @param aDirection
     *            la direction du vaisseau
     */
    private void addShip(int aShipId, Vector aPosition, Angle aDirection, int aAltitude) {
        if (objectsJ3D.get("ship"+aShipId) == null) {
            Color3f thisShipColor = Environment.theXMLConfigHandler
                    .getColorFromId(aShipId);
            objectsJ3D.put("ship"+aShipId,
                         new J3DShip("wipeout.pilotix.shape.xml",thisShipColor));

            // Si c'est le vaisseau du joueur qu'on vient de créer, on lui
            // ajoute une caméra
            if (Environment.theClientArea.getOwnShipId() == aShipId) {
                ownShip3DCamera = new J3DCamera(mainCanvas3D);
                ownShip3DCamera.setCoordinates(0.0f, 0.0f, 150.0f);
                ((J3DShip)objectsJ3D.get("ship"+aShipId)).addCamera(ownShip3DCamera);
            }

            // Ajout du J3DShip dans la locale
            locale.addBranchGraph((BranchGroup)objectsJ3D.get("ship"+aShipId));

            // Appel de setShip pour modifier la position, la direction et
            // l'état du vaisseau
            setShip(aShipId, aPosition, aDirection, aAltitude, Ship.ADD);
        } else {
            System.err
                    .println("[Display3D.addShip] ALERTE : utilisez setShip() et non addShip()"
                            + " car le vaisseau dont l'id est "
                            + aShipId
                            + " existe déjà dans la liste objectsJ3D.");
            setShip(aShipId, aPosition, aDirection, aAltitude, Ship.NULL);
        }
    }

    /**
     * Retire un vaisseau de l'affichage 3D.
     * Cette fonction est appelée par updateShip().
     *
     * @param aShipId
     *            l'identifiant du vaisseau à retirer
     */
    private void removeShip(int aShipId) {
        if (aShipId == Environment.theClientArea.getOwnShipId()) {
            // Si le vaisseau est celui du joueur, on retire la caméra
            ((J3DShip)objectsJ3D.get("ship"+Environment.theClientArea.getOwnShipId()))
                    .getCamera().getView().removeCanvas3D(mainCanvas3D);
        }
        // On retire une branche de la locale.
        locale.removeBranchGraph((BranchGroup)objectsJ3D.get("ship"+aShipId));
        // On retire le vaisseau de la liste des objets 3D
        objectsJ3D.remove("ship"+aShipId);
    }

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
    private void setShip(int aShipId, Vector aPosition, Angle aDirection,
                          int aAltitude, int aState) {
        if (objectsJ3D.containsKey("ship"+aShipId)) {
            ((J3DShip)objectsJ3D.get("ship"+aShipId)).setPosition(aPosition);
            ((J3DShip)objectsJ3D.get("ship"+aShipId)).setDirection(aDirection);
            ((J3DShip)objectsJ3D.get("ship"+aShipId)).setAltitude(aAltitude);
            //((J3DShip)objectsJ3D.get("ship"+aShipId)).setState(aState); // A FAIRE PLUS TARD
        } else {
            System.err.println("[Display3D.setShip] ERREUR: ship[" + aShipId
                    + "]==null");
        }
    }

    /**
     * Crée, met à jour l'état, ou supprime un vaisseau dans l'affichage 3D, en
     * consultant l'état du vaisseau dans le tableau "ships" de ClientArea pour
     * savoir ce qu'il faut faire.
     * Cette fonction est appelée par la fonction update().
     *
     * @param aShipId
     *            l'identifiant du vaisseau à modifier
     */
    private void updateShip(int aShipId) {
        // Si le ship dans ClientArea n'est pas null...
        if (! Environment.theClientArea.shipIsNull(aShipId)) {
            if (objectsJ3D.containsKey("ship"+aShipId)) {
                // ...et le J3DShip non plus, on met simplement à jour la vue 3D
                setShip(aShipId,
                        Environment.theClientArea.getShipPosition(aShipId),
                        Environment.theClientArea.getShipDirection(aShipId),
                        100,
                        Environment.theClientArea.getShipStates(aShipId));
            }
            else {
                // ...sinon si seul le J3DShip est null, on ajoute ce vaisseau
                addShip(aShipId,
                        Environment.theClientArea.getShipPosition(aShipId),
                        Environment.theClientArea.getShipDirection(aShipId),
                        100);
            }
        }
        else if (objectsJ3D.containsKey("ship"+aShipId)) {
            // Sinon si le ship est null mais pas le shipJ3D,
            // il faut faire disparaître le shipJ3D
            removeShip(aShipId);
        }
        // Si le ship est null, et le J3DShip aussi, on ne fait rien
    }

    /**
     * Cette fonction sert à synchroniser la vue en 3D avec l'état du jeu tel qu'il
     * apparaît dans ClientArea au moment de l'appel. Elle est appelée par
     * ClientMainLoopThread.
     */
    public void update() {
        for (int i = 0; i < Environment.theClientArea.getShips().length; i++) {
            updateShip(i);
        }
    }
}
