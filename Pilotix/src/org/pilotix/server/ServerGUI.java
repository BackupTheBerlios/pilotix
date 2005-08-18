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

package org.pilotix.server;

import java.lang.Exception;
import java.util.ResourceBundle;
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JFrame;
import javax.swing.WindowConstants;
import javax.swing.JButton;

/**
 * <p>
 * Cette classe crée une fenêtre pour le serveur Pilotix.
 * </p>
 *
 * @author Grégoire Colbert
 */
public class ServerGUI extends JFrame implements ActionListener {

    public static String propertiesPath = "properties/";

    private PilotixServer pilotixServer = null;
    private JButton startStopButton = null;

    /**
     * Crée une JFrame
     */
    public ServerGUI() {
        setTitle(ResourceBundle.getBundle(
                         propertiesPath + "i18nServer")
                         .getString("server_FrameTitle"));
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        getContentPane().setLayout(new BorderLayout());
        setSize(200,100);

        // Bouton permettant de démarrer/arrêter le serveur
        startStopButton = new JButton(ResourceBundle.getBundle(
                         propertiesPath + "i18nServer")
                         .getString("startServer_ButtonName"));
        startStopButton.setActionCommand("startServer");
        startStopButton.addActionListener(this);
        getContentPane().add("Center", startStopButton);

        setVisible(true);
    }

    /**
     * Écouteur de boutons : fait des choses en fonction du champ ActionCommand
     * des boutons.
     *
     * @param evt
     *            l'instance de ActionEvent à traiter.
     */
    public void actionPerformed(ActionEvent evt) {
        String str = evt.getActionCommand();
        if (str.equals("startServer")) {
            try {
                pilotixServer = new PilotixServer(9000, 30);
                startStopButton.setActionCommand("stopServer");
                startStopButton.setText(ResourceBundle.getBundle(
                         propertiesPath + "i18nServer")
                         .getString("stopServer_ButtonName"));
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        } else if (str.equals("stopServer")) {
            pilotixServer.endGame();
            startStopButton.setActionCommand("startServer");
            startStopButton.setText(ResourceBundle.getBundle(
                         propertiesPath + "i18nServer")
                         .getString("startServer_ButtonName"));
        }
    }
}
