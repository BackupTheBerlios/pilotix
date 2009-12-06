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

import org.w3c.dom.Element;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import javax.vecmath.Color3f;

import org.pilotix.common.ResourceLocator;

/**
 * Cette classe représente le fichier de configuration du client.
 * 
 * @author Loïc Guibart
 */
public class ClientConfigHandler {
	private final static String cfgFileName = "pilotix.client.config.xml";
	private Document configDocument;
	private Element documentElement;

	/**
	 * Construit la représentation du contenu du fichier de configuration du
	 * client
	 */
	public ClientConfigHandler() {
		configDocument = Environment.theXMLHandler.getDocumentFromURL(Environment.theRL.getResource(ResourceLocator.CONFIG, cfgFileName));
		documentElement = configDocument.getDocumentElement();
	}

	/**
	 * Renvoie la couleur du vaisseau dont l'id est passé en paramêtre. Le
	 * fichier XML de configuration doit contenir un joueur dont l'id vaut le
	 * numéro de son vaisseau (0, 1, etc).
	 * 
	 * @param aShipId
	 *            l'identifiant du vaisseau
	 * @return la couleur du vaisseau ayant cet identifiant (le blanc est la
	 *         couleur renvoyée par défaut)
	 */
	public final Color3f getColorFromId(int aShipId) {
		boolean found = false;
		String theColorIdent = null;

		Element colorsElt = (Element) (documentElement.getElementsByTagName("colors").item(0));
		NodeList pilotList = colorsElt.getElementsByTagName("pilot");
		for (int i = 0; !found && i < pilotList.getLength(); i++) {
			Element pilot = (Element) pilotList.item(i);
			String id = pilot.getAttribute("id");
			if (Integer.parseInt(id) == aShipId) {
				found = true;
				theColorIdent = pilot.getAttribute("rgb");
			}
		}
		if (!found) {
			theColorIdent = "1.0;1.0;1.0";
		}
		return getColor(theColorIdent);
	}

	/**
	 * Renvoie une instance de Color3f correspondant à la couleur passée en
	 * paramêtre.
	 * 
	 * @param colorIdent
	 *            Couleur à convertir, sous la forme "R;G;B".
	 * @return la couleur demandée
	 */
	// la méthode se trouve ici pour le moment, mais comme elle servira
	// également pour la récupération
	// des couleurs des vaisseaux, il faudra trouver un endroit où la mettre
	private Color3f getColor(String colorIdent) {
		java.util.StringTokenizer st = new java.util.StringTokenizer(colorIdent, ";");
		float r = Float.parseFloat(st.nextToken());
		float g = Float.parseFloat(st.nextToken());
		float b = Float.parseFloat(st.nextToken());
		return new Color3f(r, g, b);
	}
}
