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

import java.util.HashMap;
import org.pilotix.common.*;

/**
 * Classe effectuant le lien entre les entrées (touches du clavier, souris) et les données
 * concernant le vaisseau à envoyer au serveur.
 * L'objet Command qui constitue ces données est positionné par la méthode getCommand().
 * 
 * @author Loïc Guibart
 * @see Command
 */
public class ControlCommand {
	/** code de la touche d'acceleration */
	private int accelKey;
	/** code de la touche d'action */
	private int useKey;
	/** Objet Command positionné à partir de l'état des touches */
	private Command cmd;
	/** Angle de rotation du vaisseau */
	private Angle angle;
	
	/**
	 * Constructeur : récupère les codes des touches définis dans le fichier de configuration.
	 */
	public ControlCommand () {
		HashMap keys = Environment.userConfig.getKeymap();
		
		// récupération des touches définies
		try {
			accelKey = getKeyCode((String)keys.get("ACCELERATE"));
			useKey = getKeyCode((String)keys.get("USE"));
			
		} catch (Exception e) {
			System.err.println("Erreur lors de la récupération de la config des touches");
			e.printStackTrace();
		}
		
		cmd = new Command();
		angle = new Angle();
	}
	
	/**
	 * Positionne et renvoie l'objet Command à envoyer au serveur à partir de l'état des touches.
     * @return L'objet Command positionné
	 */
	public Command getCommand() {
		int[] keyStatus = Environment.theControls.getKeyStatus();

		cmd.setAcceleration(0);
		cmd.setToolId(0);
		cmd.setBallId(0);
		
		// actions des touches
		if (keyStatus[accelKey] == Controls.PRESSED) cmd.setAcceleration(2);
		if (keyStatus[useKey] == Controls.PRESSED) cmd.setToolId(1);
		
		// actions de la souris
		angle.set(Environment.theControls.getMouseVariation().x);
		cmd.setDirection(angle);
		
		return cmd;
	}
	
	
	/**
	 * Effectue la correspondance entre un intutilé de touche et son code (voir KeyEvent).
	 * Malheureusement cette méthode n'existe pas dans KeyEvent... Je n'ai trouvé que ce
	 * moyen de faire mais c'est pas terrible...
	 * @param touche
	 * @return code correspondant.
	 */
	
	private int getKeyCode(String key) throws Exception {
		return ((Class.forName("java.awt.event.KeyEvent")).getField(key)).getInt(null);
	}
}
