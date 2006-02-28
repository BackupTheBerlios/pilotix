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
import org.w3c.dom.Element;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.pilotix.common.ResourceLocator;

/**
 * Cette classe représente un fichier de configuration utilisateur. Elle permet
 * de récupérer les champs contenus dans ce fichier.
 * Les champs obligatoires (tous ceux qui ne correspondent pas aux plugins)
 * sont recopiés dans des variables locales. Ces valeurs sont recopiées
 * à nouveau dans le Document lors de la sauvegarde.
 *
 * @author Loïc Guibart
 */
public class UserConfigHandler {

    private final static String cfgFileName = ".pilotix.pilot.xml";
    //private final static String builtInString = "BuiltIn";
    private final static String plugInString = "PlugIn";

    private Document configDocument;
    private Element documentElement;
    private String firstName;
    private String familyName;
    // photo et commentaire ?
    private int interfaceHeight, interfaceLength;
    private HashMap keymap;
    private String favoriteShipName;

    /*private HashMap builtInVars;
    private HashMap plugInVars;*/

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
                        ResourceLocator.PILOT, userName + cfgFileName));
        documentElement = configDocument.getDocumentElement();
        parseConfig();
    }

    private void parseConfig() {
        NodeList nl;
        // --- description
        nl = documentElement.getElementsByTagName("Description");
        Element topElt = (Element)nl.item(0);
        firstName = getChildElementValue(topElt, "FirstName");
        familyName = getChildElementValue(topElt, "FamilyName");
        // --- interface
        nl = documentElement.getElementsByTagName("Interface");
        topElt = (Element)nl.item(0);
        interfaceLength = Integer.parseInt(topElt.getAttribute("l"));
        interfaceHeight = Integer.parseInt(topElt.getAttribute("h"));
        // --- favoriteShip
        nl = documentElement.getElementsByTagName("Ship");
        topElt = (Element)nl.item(0);
        favoriteShipName = topElt.getAttribute("name");
        // --- keymap
        nl = documentElement.getElementsByTagName("Keymap");
        topElt = (Element)nl.item(0);
        keymap = getValuesFromElements(topElt, "Action");
    }


    private String getChildElementValue(Element elt, String childName) {
        NodeList nl = elt.getElementsByTagName(childName);
        return ((Element)nl.item(0)).getFirstChild().getNodeValue();
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String name) {
        firstName = name;
    }

    public String getFamilyName() {
        return familyName;
    }

    public void setFamilyName(String name) {
        familyName = name;
    }

    /**
     * Renvoie la longueur de la fenêtre du jeu.
     *
     * @return la longueur de l'interface, en pixels
     */
    public int getInterfaceLength() {
        return interfaceLength;
    }

    /**
     * Renvoie la hauteur de la fenêtre du jeu.
     *
     * @return la hauteur de l'interface, en pixels
     */
    public int getInterfaceHeight() {
        return interfaceHeight;
    }

    public void setInterfaceDimensions(int length, int height) {
        interfaceLength = length;
        interfaceHeight = height;
    }

    public String getFavoriteShipName() {
        return favoriteShipName;
    }

    public void setFavoriteShipName(String name) {
        favoriteShipName = name;
    }

    public HashMap getKeymap() {
        return keymap;
    }

    public void setKeymap(HashMap aKeymap) {
        keymap = aKeymap;
    }





    /**
     * Renvoie les variables d'une section de type BuiltIn.
     *
     * @param builtInName
     *            Nom de la section.
     * @return HashMap contenant les associations nom-valeur des variables de
     *         la section.
     */
    /*public HashMap getBuiltInVars(String builtInName) {
        Element elt = getElementByTypeAndName(builtInString, builtInName);
        if (elt == null)
            return null;
        else
            return getVarsFromElement(elt);
    }*/

    /**
     * Renvoie les variables d'une section de type PlugIn.
     *
     * @param plugInName
     *            Nom de la section.
     * @return HashMap contenant les associations nom-valeur des variables de
     *         la section.
     */
    public HashMap getPlugInVars(String plugInName) {
        NodeList nl = documentElement.getElementsByTagName(plugInString);
        if (nl.getLength() == 0)
            return null;
        else {
            Element elt = null;
            int i = 0;
            while (i < nl.getLength()) {
                elt = (Element) nl.item(i);
                if (elt.getAttribute("name").equals(plugInName))
                    return getValuesFromElements(elt, "Var");
                i++;
            }
            return null;
        }
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
    /*private Element getElementByTypeAndName(String type, String name) {
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
    }*/

    /**
     * Renvoie les couples nom-valeur d'un ensemble d'éléments de la forme
     * <pre><tagName name="nom">valeur</tagName></pre>.
     * @param topElement Element père des éléments concernés.
     * @param tagName Nom des éléments concernés.
     * @return HashMap contenant les associations nom-valeur des éléments concernés.
     */
    private HashMap getValuesFromElements(Element topElement, String tagName) {
        NodeList nl = topElement.getElementsByTagName(tagName);
        HashMap result = new HashMap(nl.getLength(), 1);
        Element elt;
        for (int i = 0; i < nl.getLength(); i++) {
            elt = (Element) nl.item(i);
            result.put(elt.getAttribute("name"), elt.getFirstChild()
                    .getNodeValue());
        }
        return result;
    }
}
