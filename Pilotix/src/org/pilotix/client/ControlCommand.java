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
import org.pilotix.common.Command;

/**
 * Classe effectuant le lien entre les entrées (touches du clavier, souris) et
 * les données concernant le vaisseau à envoyer au serveur. L'objet Command qui
 * constitue ces données est positionné par la méthode getCommand().
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
	 * Constructeur : récupère les codes des touches définis dans le fichier de
	 * configuration.
	 */
	public ControlCommand() {
		// récupération des touches définies
		accelKey = Environment.theControls.getKeyCodeFromAction("ACCELERATE");
		useKey = Environment.theControls.getKeyCodeFromAction("USE");

		cmd = new Command();
		angle = new Angle();
	}

	/**
	 * Positionne et renvoie l'objet Command à envoyer au serveur à partir de
	 * l'état des touches.
	 * 
	 * @return L'objet Command positionné
	 */
	public Command getCommand() {
		int[] keyStatus = Environment.theControls.getKeyStatus();

		cmd.setAcceleration(0);
		cmd.setToolId(0);
		cmd.setBallId(0);

		// actions des touches
		if (keyStatus[accelKey] == Controls.PRESSED)
			cmd.setAcceleration(2);
		if (keyStatus[useKey] == Controls.PRESSED)
			cmd.setToolId(1);

		// actions de la souris
		angle.set(Environment.theControls.getMouseVariation().x * 0.5f);
		cmd.setDirection(angle);

		return cmd;
	}
}
