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

import java.util.*;
import org.w3c.dom.*;

/**
 * Cette classe représente un fichier de configuration utilisateur. Elle permet
 * de récupérer les champs contenus dans ce fichier.
 * 
 * @author Loïc Guibart
 */
public class UserConfigHandler {

    private final static String cfgFileName = ".PilotixClientConfig.xml";
    private final static String builtInString = "BuiltIn";
    private final static String plugInString = "PlugIn";

    private Document configDocument;
    private Element documentElement;
    private HashMap builtInVars;
    private HashMap plugInVars;

    /**
     * Construit la représentation du contenu du fichier de configuration d'un
     * utilisateur.
     * 
     * @param userName
     *            Nom de l'utilisateur.
     */
    public UserConfigHandler(String userName) {
        configDocument = Environment.theXMLHandler
                .getDocumentFromURL(Environment.theRL.getResource(
                        ResourceLocator.CONFIG, userName + cfgFileName));
        documentElement = configDocument.getDocumentElement();
    }

    /**
     * Renvoie les variables d'une section de type BuiltIn.
     * 
     * @param builtInName
     *            Nom de la section.
     * @return HashMap contenant les associations nom-valeur des variables de
     *         la section.
     */
    public HashMap getBuiltInVars(String builtInName) {
        Element elt = getElementByTypeAndName(builtInString, builtInName);
        if (elt == null)
            return null;
        else
            return getVarsFromElement(elt);
    }

    /**
     * Renvoie les variables d'une section de type PlugIn.
     * 
     * @param plugInName
     *            Nom de la section.
     * @return HashMap contenant les associations nom-valeur des variables de
     *         la section.
     */
    public HashMap getPlugInVars(String plugInName) {
        Element elt = getElementByTypeAndName(plugInString, plugInName);
        if (elt == null)
            return null;
        else
            return getVarsFromElement(elt);
    }

    /**
     * Renvoie une section de type et de nom donnés.
     * 
     * @param type
     *            Type de la section (BuiltIn, PlugIn).
     * @param name
     *            Nom de la section (correspond à l'attribut "name" de
     *            l'élément).
     * @return Element correspondant à la section dans le Document.
     */
    private Element getElementByTypeAndName(String type, String name) {
        NodeList nl = documentElement.getElementsByTagName(type);
        if (nl.getLength() == 0)
            return null;
        else {
            Element elt = null;
            int i = 0;
            boolean found = false;
            while (!found && i < nl.getLength()) {
                elt = (Element) nl.item(i);
                if (elt.getAttribute("name").equals(name)) found = true;
                i++;
            }
            if (found)
                return elt;
            else
                return null;
        }
    }

    /**
     * Récupère les variables d'une section.
     * 
     * @param elt
     *            Element correspondant à la section dans le Document.
     * @return HashMap contenant les associations nom-valeur des variables de
     *         la section.
     */
    private HashMap getVarsFromElement(Element elt) {
        NodeList nl = elt.getElementsByTagName("Var");
        HashMap result = new HashMap(nl.getLength(), 1);
        Element var;
        for (int i = 0; i < nl.getLength(); i++) {
            var = (Element) nl.item(i);
            result.put(var.getAttribute("name"), var.getFirstChild()
                    .getNodeValue());
        }
        return result;
    }
}
