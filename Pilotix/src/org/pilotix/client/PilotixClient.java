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

import javax.swing.UIManager;

import org.pilotix.common.ResourceLocator;
import org.pilotix.common.XMLHandler;

/**
 * La classe qui crée le client.
 * 
 * @author Grégoire Colbert
 * 
 * @see XMLHandler
 * @see ClientArea
 * @see Controls
 * @see Display3D
 * @see GUI
 * @see ClientMainLoopThread
 */
public class PilotixClient {

	/**
	 * Crée tous les composants fondamentaux du client.
	 */
	public PilotixClient() {
		Environment.theRL = new ResourceLocator(Environment.dataPath);

		/*
		 * Environment.theXMLConfigHandler = new XMLHandler(Environment.theRL
		 * .getResource(ResourceLocator.CONFIG, "pilotix-client-config.xml"));
		 */

		Environment.theXMLHandler = new XMLHandler();

		/*
		 * test du fonctionnement de UserConfigHandler à terme il faudra une
		 * fenetre de selection de l'utilisateur avant de créer userConfig
		 */
		Environment.clientConfig = new ClientConfigHandler();
		Environment.userConfig = new UserConfigHandler("Florent");
		// System.out.println(Environment.userConfig.getKeymap());
		// System.out.println(Environment.userConfig.getPlugInVars("AutoPilot"));

		Environment.theControls = new Controls();

		Environment.theClientArea = new ClientArea();

		Environment.theDisplay3D = new Display3D();

		Environment.theGUI = new GUI();

		Environment.theControls.setMainFrame(Environment.theGUI);
		Environment.controlCmd = new ControlCommand();

	}

	/**
	 * Crée une instance de la classe PilotixClient.
	 */
	public static void main(String[] args) throws Exception {
		UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		new PilotixClient();
	}

}
