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

import org.pilotix.common.Vector;

import java.awt.*;
import java.awt.event.*;
import java.util.LinkedList;
import java.util.Iterator;
import javax.swing.event.MouseInputAdapter;

import java.awt.image.*;

/**
 * Le rôle de cette classe est de gérer la souris et le clavier. L'état de
 * toutes les touches du clavier est stocké dans un tableau, qui peut être
 * récupéré avec la fonction <code>getKeyStatus()</code>. La variation de
 * déplacement de la souris est récupérée avec <code>getMouseVariation()</code>.
 * La fonction setMainFrame doit être utilisée afin de récupérer les évènements
 * clavier. et la fonction setMouseComponent pour les évènements souris. Les
 * plugins peuvent s'enregistrer en tant que listeners pour récupérer les
 * évènements clavier.
 *
 * @author Loïc Guibart
 *
 * @.todo chargement des touches (dans Environnement) @.todo changement du
 * pointeur de la souris quand celui-ci est attaché à un composant
 */

public class Controls extends KeyAdapter implements KeyEventDispatcher {

    /**
     * Nombre de touches sur le clavier le nombre est volontairement trop
     * grand.
     */
    private static final int NB_KEYS = 256;

    /**
     * état de la touche : pas appuyée
     */
    public static final int NOTHING = 0;

    /**
     * état de la touche : pressée
     */
    public static final int PRESSED = 1;

    /**
     * état de la touche : pressée et relachée depuis le dernier appel à <code>getKeyStatus</code>
     */
    public static final int PRESSED_AND_RELEASED = 2;

    // touches d'actions
    public int keyAccel;

    /**
     * Caractère de la touche effectuant l'attachement / le détachement de la
     * souris à son composant
     */
    private char mouseGrabKey;

    /** code de la touche pour rapprocher la caméra */
    private char zoomInKey;
    /** code de la touche pour reculer la caméra */
    private char zoomOutKey;

    /**
     * gestionnaire de souris
     */
    private Mouse mm;

    /**
     * Tableau de l'état des touches
     */
    private int[] keyStatus;

    /**
     * Tableau indiquant si la touche était appuyée avant le dernier appel à
     * <code>getKeyStatus</code>.
     */
    private boolean[] wasPressed;

    /**
     * Indique si la souris est actuellement "attachée" à son composant
     */
    private boolean mouseGrabbed;

    /**
     * Indique si les évènements clavier sont traités
     */
    private boolean processKeyEvents;

    /**
     * Liste des listeners d'évènement clavier
     */
    private LinkedList keyListeners;

    /**
     * Construit un objet <code>Controls</code>
     */
    public Controls() {
        //Environnement.loadControls(this);
        mm = new Mouse();
        keyListeners = new LinkedList();
        mouseGrabbed = false;
        mouseGrabKey = KeyEvent.VK_G; // à changer
        keyStatus = new int[NB_KEYS];
        wasPressed = new boolean[NB_KEYS];
        processKeyEvents = false;
        keyAccel = KeyEvent.VK_UP;

        KeyboardFocusManager.getCurrentKeyboardFocusManager()
                .addKeyEventDispatcher(this);
    }

    /**
     * Indique à <code>Controls</code> où doivent être récupérés les
     * évènements claviers.
     *
     * @param gui
     *            Fenêtre principale du client.
     */
    public void setMainFrame(GUI gui) {
        /*
         * DefaultKeyboardFocusManager dkfm = new
         * DefaultKeyboardFocusManager();
         * KeyboardFocusManager.setCurrentKeyboardFocusManager(dkfm);
         * dkfm.addKeyEventDispatcher(new KeyEventDispatcher() { public boolean
         * dispatchKeyEvent(KeyEvent e) { switch(e.getID()) { case
         * KeyEvent.KEY_TYPED : keyTyped(e); break; case KeyEvent.KEY_PRESSED :
         * keyPressed(e); break; case KeyEvent.KEY_RELEASED : keyReleased(e);
         * break; } return true; }
         */

        /*
         * ajout d'un listener indiquant si position ou la taille de la fenêtre
         * sont modifiées
         */
        gui.addComponentListener(new ComponentAdapter() {

            public void componentResized(ComponentEvent e) {
                mm.frameChanged();
            }

            public void componentMoved(ComponentEvent e) {
                mm.frameChanged();
            }
        });
    }

    /**
     * Renvoie le tableau de statut des touches.
     *
     * @return Tableau d'entier contenant l'état de toutes les touches.
     */
    public int[] getKeyStatus() {
        for (int i = 0; i < NB_KEYS; i++)
            if (keyStatus[i] == PRESSED) wasPressed[i] = true;
        return keyStatus;
    }

    /**
     * Traitement d'un évènement clavier
     *
     * @param ke
     *            Evènement à traiter.
     */
    public boolean dispatchKeyEvent(KeyEvent ke) {
        if (processKeyEvents) {
            switch (ke.getID()) {
            case KeyEvent.KEY_TYPED:
                keyTyped(ke);
                break;
            case KeyEvent.KEY_PRESSED:
                keyPressed(ke);
                break;
            case KeyEvent.KEY_RELEASED:
                keyReleased(ke);
                break;
            }
        }
        return false;
    }

    /**
     * Active ou désactive les contrôles.
     *
     * @param isActive
     *            Indique si les contrôles doivent être activés.
     */
    public void active(boolean isActive) {
        processKeyEvents = isActive;
    }

    /**
     * Transmet l'évènement "touche appuyée" aux listeners
     *
     * @param e
     *            Evènement clavier.
     */
    private void fireTypedEvent(KeyEvent e) {
        Iterator it = keyListeners.listIterator();
        while (it.hasNext())
            ((KeyListener) it.next()).keyTyped(e);
    }

    /**
     * Transmet l'évènement "touche pressée" aux listeners
     *
     * @param e
     *            Evènement clavier.
     */
    private void firePressedEvent(KeyEvent e) {
        Iterator it = keyListeners.listIterator();
        while (it.hasNext())
            ((KeyListener) it.next()).keyPressed(e);
    }

    /**
     * Transmet l'évènement "touche relachée" aux listeners
     *
     * @param e
     *            Evènement clavier.
     */
    private void fireReleasedEvent(KeyEvent e) {
        Iterator it = keyListeners.listIterator();
        while (it.hasNext())
            ((KeyListener) it.next()).keyReleased(e);
    }

    /**
     * Ajoute un listener d'évènement clavier
     *
     * @param kl
     *            Listener à ajouter.
     */
    public void addListener(KeyListener kl) {
        keyListeners.add(kl);
    }

    /**
     * Retire un listener d'évènement clavier
     *
     * @param kl
     *            Listener à supprimer.
     */
    public void removeListener(KeyListener kl) {
        keyListeners.remove(kl);
    }

    /**
     * Traitement d'un évènement "touche pressée"
     *
     * @param e
     *            Evenement clavier.
     */
    public void keyPressed(KeyEvent e) {
        keyStatus[e.getKeyCode()] = PRESSED;
        wasPressed[e.getKeyCode()] = false;
        firePressedEvent(e);
    }

    /**
     * Traitement d'un évènement "touche relachée"
     *
     * @param e
     *            Evenement clavier.
     */
    public void keyReleased(KeyEvent e) {
        int code = e.getKeyCode();
        if (wasPressed[code])
            keyStatus[code] = NOTHING;
        else
            keyStatus[code] = PRESSED_AND_RELEASED;
        fireReleasedEvent(e);
    }

    /**
     * Traitement d'un évènement "touche appuyée"
     *
     * @param e
     *            Evenement clavier.
     */
    public void keyTyped(KeyEvent e) {
        //System.out.println("keyTyped !" + e.getKeyChar());
        if (e.getKeyChar() == 'g') {
            if (mouseGrabbed) {
                mouseGrabbed = false;
                mm.releaseMouse();
            } else {
                mouseGrabbed = true;
                mm.grabMouse();
            }
        }
        fireTypedEvent(e);
    }

    /**
     * Associe le gestionnaire de souris à un composant AWT. Les évènements
     * souris vont être récupérés dans ce composant. Le pointeur de la souris
     * peut être attaché à ce composant ce qui fait que le pointeur ne sortira
     * pas du composant.
     *
     * @param comp
     *            Composant AWT où vont être récupérés les évènements souris.
     */
    public void setMouseComponent(Component comp) {
        mm.setComponent(comp);
    }

    /**
     * Enlève l'association entre la souris et le composant
     */
    /*
     * public void unsetMouseComponent() { mm.unsetComponent();
     */

    /**
     * Récupère la variation de la position de la souris depuis le dernier
     * appel
     *
     * @return Vecteur contenant la variation de la position en x et y.
     */
    public Vector getMouseVariation() {
        return mm.getMouseVariation();
    }

    /**
     * Classe interne effectuant la gestion des évènements souris
     */
    class Mouse extends MouseInputAdapter implements MouseWheelListener {

        private Vector mousePosition, oldMousePosition, variation;
        private Point windowPos;
        private int xCenter, yCenter;
        private Robot mouseMover;
        private Component component;
        private boolean grabbed;
        private boolean[] buttonPressed;

        int[] pixels;
        Image image;
        Cursor hideCursor;
        Cursor defaultCursor;

        public Mouse() {
            component = null;
            mousePosition = new Vector(0, 0);
            oldMousePosition = new Vector(0, 0);
            variation = new Vector(0, 0);
            grabbed = false;
            buttonPressed = new boolean[4];

            pixels = new int[16 * 16];
            image = Toolkit.getDefaultToolkit().createImage(
                    new MemoryImageSource(16, 16, pixels, 0, 16));
            hideCursor = Toolkit.getDefaultToolkit().createCustomCursor(image,
                    new Point(0, 0), "hidden_cursor");
        }

        public void setComponent(Component comp) {
            component = comp;
            windowPos = component.getLocationOnScreen();
            xCenter = component.getWidth() / 2;
            yCenter = component.getHeight() / 2;

            //System.out.println("WindowPos : " + windowPos);
            //System.out.println("xCenter=" + xCenter + " yCenter=" +
            // yCenter);

            try {
                mouseMover = new Robot();
            } catch (AWTException e) {
            }

            component.addMouseListener(this);
            component.addMouseMotionListener(this);
            component.addMouseWheelListener(this);
        }

        public void unsetComponent() {
            component = null;
        }

        /**
         * Traitement de la modification de la taille ou de la position de la
         * fenêtre
         */
        public void frameChanged() {
            if (component != null) {
                windowPos = component.getLocationOnScreen();
                xCenter = component.getWidth() / 2;
                yCenter = component.getHeight() / 2;
            }
        }

        public void grabMouse() {
            grabbed = true;
            defaultCursor = component.getCursor();
            component.setCursor(hideCursor);
        }

        public void releaseMouse() {
            grabbed = false;
            component.setCursor(defaultCursor);
        }

        public Vector getMouseVariation() {
            variation.x = mousePosition.x - oldMousePosition.x;
            variation.y = mousePosition.y - oldMousePosition.y;
            if (grabbed) {
                mouseMover.mouseMove(xCenter + windowPos.x, yCenter
                        + windowPos.y);
                oldMousePosition.x = xCenter;
                oldMousePosition.y = yCenter;
            } else {
                oldMousePosition.x = mousePosition.x;
                oldMousePosition.y = mousePosition.y;
            }
            return variation;
        }

        public void mouseClicked(MouseEvent e) {
        }

        public void mousePressed(MouseEvent e) {
            buttonPressed[e.getButton()] = true;
        }

        public void mouseReleased(MouseEvent e) {
            buttonPressed[e.getButton()] = false;
        }

        public void mouseMoved(MouseEvent e) {
            mousePosition.x = e.getX();
            mousePosition.y = e.getY();
            //System.out.println("Mouse moved : x=" + mousePosition.x + " y="
            // + mousePosition.y);
        }

        public void mouseDragged(MouseEvent e) {
        }

        public void mouseWheelMoved(MouseWheelEvent e) {
            // mettre ici le code de l'action effectuée par la molette

        }
    }

}
