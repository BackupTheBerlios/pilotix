/*
 * Created on 20 mai 2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
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
	/** Objet Command positionné à partir de l'état des touches */
	private Command cmd;
	/** Angle de rotation du vaisseau */
	private Angle angle;
	
	/**
	 * Constructeur : récupère les codes des touches définis dans le fichier de configuration.
	 */
	public ControlCommand () {
		HashMap keys = Environment.userConfig.getBuiltInVars("keymap");
		
		// récupération des touches définies
		try {
			accelKey = getKeyCode((String)keys.get("ACCELERATE"));
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
		cmd.setAccessory(0);
		cmd.setProjectileId(0);
		
		// actions des touches
		if (keyStatus[accelKey] == Controls.PRESSED) cmd.setAcceleration(2);
		
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
