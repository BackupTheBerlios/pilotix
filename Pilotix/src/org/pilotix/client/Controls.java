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
 * Le r�le de cette classe est de g�rer la souris et le clavier. L'�tat de
 * toutes les touches du clavier est stock� dans un tableau, qui peut �tre
 * r�cup�r� avec la fonction <code>getKeyStatus()</code>. La variation de
 * d�placement de la souris est r�cup�r�e avec <code>getMouseVariation()</code>.
 * La fonction setMainFrame doit �tre utilis�e afin de r�cup�rer les �v�nements
 * clavier. et la fonction setMouseComponent pour les �v�nements souris. Les
 * plugins peuvent s'enregistrer en tant que listeners pour r�cup�rer les
 * �v�nements clavier.
 * 
 * @author Lo�c Guibart
 * 
 * @.todo chargement des touches (dans Environnement) @.todo changement du
 * pointeur de la souris quand celui-ci est attach� � un composant
 */

public class Controls extends KeyAdapter implements KeyEventDispatcher {

    /**
     * Nombre de touches sur le clavier le nombre est volontairement trop
     * grand.
     */
    private static final int NB_KEYS = 256;

    /**
     * �tat de la touche : pas appuy�e
     */
    public static final int NOTHING = 0;

    /**
     * �tat de la touche : press�e
     */
    public static final int PRESSED = 1;

    /**
     * �tat de la touche : press�e et relach�e depuis le dernier appel � <code>getKeyStatus</code>
     */
    public static final int PRESSED_AND_RELEASED = 2;

    // touches d'actions
    public int keyAccel;

    /**
     * Caract�re de la touche effectuant l'attachement / le d�tachement de la
     * souris � son composant
     */
    private char mouseGrabKey;

    /**
     * gestionnaire de souris
     */
    private Mouse mm;

    /**
     * Tableau de l'�tat des touches
     */
    private int[] keyStatus;

    /**
     * Tableau indiquant si la touche �tait appuy�e avant le dernier appel �
     * <code>getKeyStatus</code>.
     */
    private boolean[] wasPressed;

    /**
     * Indique si la souris est actuellement "attach�e" � son composant
     */
    private boolean mouseGrabbed;

    /**
     * Indique si les �v�nements clavier sont trait�s
     */
    private boolean processKeyEvents;

    /**
     * Liste des listeners d'�v�nement clavier
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
        mouseGrabKey = KeyEvent.VK_G; // � changer
        keyStatus = new int[NB_KEYS];
        wasPressed = new boolean[NB_KEYS];
        processKeyEvents = false;
        keyAccel = KeyEvent.VK_UP;

        KeyboardFocusManager.getCurrentKeyboardFocusManager()
                .addKeyEventDispatcher(this);
    }

    /**
     * Indique � <code>Controls</code> o� doivent �tre r�cup�r�s les
     * �v�nements claviers.
     * 
     * @param gui
     *            Fen�tre principale du client.
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
         * ajout d'un listener indiquant si position ou la taille de la fen�tre
         * sont modifi�es
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
     * @return Tableau d'entier contenant l'�tat de toutes les touches.
     */
    public int[] getKeyStatus() {
        for (int i = 0; i < NB_KEYS; i++)
            if (keyStatus[i] == PRESSED) wasPressed[i] = true;
        return keyStatus;
    }

    /**
     * Traitement d'un �v�nement clavier
     * 
     * @param ke
     *            Ev�nement � traiter.
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
     * Active ou d�sactive les contr�les.
     * 
     * @param isActive
     *            Indique si les contr�les doivent �tre activ�s.
     */
    public void active(boolean isActive) {
        processKeyEvents = isActive;
    }

    /**
     * Transmet l'�v�nement "touche appuy�e" aux listeners
     * 
     * @param e
     *            Ev�nement clavier.
     */
    private void fireTypedEvent(KeyEvent e) {
        Iterator it = keyListeners.listIterator();
        while (it.hasNext())
            ((KeyListener) it.next()).keyTyped(e);
    }

    /**
     * Transmet l'�v�nement "touche press�e" aux listeners
     * 
     * @param e
     *            Ev�nement clavier.
     */
    private void firePressedEvent(KeyEvent e) {
        Iterator it = keyListeners.listIterator();
        while (it.hasNext())
            ((KeyListener) it.next()).keyPressed(e);
    }

    /**
     * Transmet l'�v�nement "touche relach�e" aux listeners
     * 
     * @param e
     *            Ev�nement clavier.
     */
    private void fireReleasedEvent(KeyEvent e) {
        Iterator it = keyListeners.listIterator();
        while (it.hasNext())
            ((KeyListener) it.next()).keyReleased(e);
    }

    /**
     * Ajoute un listener d'�v�nement clavier
     * 
     * @param kl
     *            Listener � ajouter.
     */
    public void addListener(KeyListener kl) {
        keyListeners.add(kl);
    }

    /**
     * Retire un listener d'�v�nement clavier
     * 
     * @param kl
     *            Listener � supprimer.
     */
    public void removeListener(KeyListener kl) {
        keyListeners.remove(kl);
    }

    /**
     * Traitement d'un �v�nement "touche press�e"
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
     * Traitement d'un �v�nement "touche relach�e"
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
     * Traitement d'un �v�nement "touche appuy�e"
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
     * Associe le gestionnaire de souris � un composant AWT. Les �v�nements
     * souris vont �tre r�cup�r�s dans ce composant. Le pointeur de la souris
     * peut �tre attach� � ce composant ce qui fait que le pointeur ne sortira
     * pas du composant.
     * 
     * @param comp
     *            Composant AWT o� vont �tre r�cup�r�s les �v�nements souris.
     */
    public void setMouseComponent(Component comp) {
        mm.setComponent(comp);
    }

    /**
     * Enl�ve l'association entre la souris et le composant
     */
    /*
     * public void unsetMouseComponent() { mm.unsetComponent();
     */

    /**
     * R�cup�re la variation de la position de la souris depuis le dernier
     * appel
     * 
     * @return Vecteur contenant la variation de la position en x et y.
     */
    public Vector getMouseVariation() {
        return mm.getMouseVariation();
    }

    /**
     * Classe interne effectuant la gestion des �v�nements souris
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
         * fen�tre
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
            // mettre ici le code de l'action effectu�e par la molette
        }
    }

}
