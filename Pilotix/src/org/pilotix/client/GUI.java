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

import java.util.ResourceBundle;
import java.awt.BorderLayout;
import javax.swing.JFrame;
import java.awt.Toolkit;
import java.awt.Dimension;
import javax.swing.WindowConstants;

/**
 * <p>
 * Cette classe crée une fenêtre pour le client Pilotix.
 * </p>
 * 
 * <p>
 * Tout son contenu, y compris la barre de menu, est ajouté en instanciant la
 * classe GUIPanel. Cette séparation entre la fenêtre et son contenu rendra la
 * création d'une applet plus facile si un jour on décide de faire une applet.
 * </p>
 * 
 * @see GUIPanel
 * @see JFrame
 * 
 * @author Grégoire Colbert
 */
public class GUI extends JFrame {

    private GUIPanel guipanel = null;

    /**
     * Crée une JFrame et la remplit avec un objet de la classe GUIPanel.
     */
    public GUI() {
        setTitle(ResourceBundle.getBundle(
                Environment.propertiesPath + "i18nClient").getString(
                "mainFrameTitle"));
        setSize(Environment.theXMLConfigHandler.getInterfaceLength(),
                Environment.theXMLConfigHandler.getInterfaceHeight());
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        getContentPane().setLayout(new BorderLayout());
        guipanel = new GUIPanel();
        getContentPane().add("Center", guipanel);

        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        Dimension frameSize = getSize();
        if (frameSize.height > screenSize.height) {
            frameSize.height = screenSize.height;
        }
        if (frameSize.width > screenSize.width) {
            frameSize.width = screenSize.width;
        }
        setLocation((screenSize.width - frameSize.width) / 2,
                (screenSize.height - frameSize.height) / 2);
        setVisible(true);
    }

    /**
     * renvoie l'instance de GUIPanel associée avec cette fenêtre GUI.
     * 
     * @return l'instance de GUIPanel
     */
    public GUIPanel getGUIPanel() {
        return guipanel;
    }
}
