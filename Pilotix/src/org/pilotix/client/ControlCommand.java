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
 * Classe effectuant le lien entre les entr�es (touches du clavier, souris) et les donn�es
 * concernant le vaisseau � envoyer au serveur.
 * L'objet Command qui constitue ces donn�es est positionn� par la m�thode getCommand().
 * 
 * @author Lo�c Guibart
 * @see Command
 */
public class ControlCommand {
	/** code de la touche d'acceleration */
	private int accelKey;
	/** Objet Command positionn� � partir de l'�tat des touches */
	private Command cmd;
	/** Angle de rotation du vaisseau */
	private Angle angle;
	
	/**
	 * Constructeur : r�cup�re les codes des touches d�finis dans le fichier de configuration.
	 */
	public ControlCommand () {
		HashMap keys = Environment.userConfig.getBuiltInVars("keymap");
		
		// r�cup�ration des touches d�finies
		try {
			accelKey = getKeyCode((String)keys.get("ACCELERATE"));
		} catch (Exception e) {
			System.err.println("Erreur lors de la r�cup�ration de la config des touches");
			e.printStackTrace();
		}
		
		cmd = new Command();
		angle = new Angle();
	}
	
	/**
	 * Positionne et renvoie l'objet Command � envoyer au serveur � partir de l'�tat des touches.
     * @return L'objet Command positionn�
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
	 * Effectue la correspondance entre un intutil� de touche et son code (voir KeyEvent).
	 * Malheureusement cette m�thode n'existe pas dans KeyEvent... Je n'ai trouv� que ce
	 * moyen de faire mais c'est pas terrible...
	 * @param touche
	 * @return code correspondant.
	 */
	
	private int getKeyCode(String key) throws Exception {
		return ((Class.forName("java.awt.event.KeyEvent")).getField(key)).getInt(null);
	}
}