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

package org.pilotix.common;

import org.w3c.dom.Element;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import javax.xml.parsers.*;
import java.net.URL;
import javax.vecmath.Color3f;

/**
 * Cette classe sert à construire un Document DOM à partir de l'URL d'un
 * fichier écrit en XML. Pour le moment elle contient également quelques
 * paramètres du client, qui seront déplacés par la suite.
 *
 * @author Loïc Guibart
 * @author Grégoire Colbert
 *
 * @see org.w3c.dom.Document
 */
public class XMLHandler {

    private Document document = null;
    protected Element rootNode = null;
    private boolean isValid = false;
    private DocumentBuilder docBuilder;

    /**
     * Construit une représentation DOM d'un fichier XML à partir de son URL.
     * Par la suite ce constructeur ne prendra plus de paramètre...
     *
     * @param fileURL
     *            l'URL du nom du fichier XML à charger
     */
    public XMLHandler(URL fileURL) {
        try {
            DocumentBuilderFactory builderFactory = DocumentBuilderFactory
                    .newInstance();
            // l'URL de la DTD est indiquée dans le fichier XML,
            // invérifiable dans le cas d'un fichier XML situé dans un JAR
            //builderFactory.setValidating(true);
            builderFactory.setValidating(false);
            docBuilder = builderFactory.newDocumentBuilder();

            document = docBuilder.parse(fileURL.openStream());
            rootNode = document.getDocumentElement();
            isValid = true;
        } catch (Exception e) {
            System.out.println("[XMLHandler(URL)]" + e.getMessage());
        }
    }

    public XMLHandler() {
        try {
            DocumentBuilderFactory builderFactory = DocumentBuilderFactory
                    .newInstance();
            builderFactory.setValidating(false);
            docBuilder = builderFactory.newDocumentBuilder();
        } catch (Exception e) {
            System.out.println("[XMLHandler()]" + e.getMessage());
        }
    }

    /**
     * Parse un fichier XML et renvoie le Document correspondant.
     * 
     * @param fileURL
     *            URL du fichier XML à parser.
     * @return L'objet org.w3c.dom.Document correspondant au contenu du fichier
     *         XML
     */
    public Document getDocumentFromURL(URL fileURL) {
        try {            
            return docBuilder.parse(fileURL.openStream());
        } catch (Exception e) {
            e.printStackTrace();
            //System.out.println("[XMLHandler.getDocumentFromURL]" + e.getMessage());
            return null;
        }
    }

    /**
     * Parse un fichier XML et renvoie le Document correspondant.
     *
     * @param uri
     *            adresse/nom du fichier XML à parser.
     * @return L'objet org.w3c.dom.Document correspondant au contenu du fichier
     *         XML
     */
    public Document getDocumentFromFile(String uri) {
        try {
            return docBuilder.parse(uri);
        } catch (Exception e) {
            System.out.println("[XMLHandler.getDocumentFromFile]" + e.getMessage());
            return null;
        }
    }

    /**
     * Renvoie la hauteur de la fenêtre du jeu.
     * 
     * @return la hauteur de l'interface, en pixels
     */
    public final int getInterfaceHeight() {
        String h = ((Element) rootNode.getElementsByTagName("interface")
                .item(0)).getAttribute("h");
        return Integer.parseInt(h);
    }

    /**
     * Renvoie la longueur de la fenêtre du jeu.
     * 
     * @return la longueur de l'interface, en pixels
     */
    public final int getInterfaceLength() {
        String l = ((Element) rootNode.getElementsByTagName("interface")
                .item(0)).getAttribute("l");
        return Integer.parseInt(l);
    }

    /**
     * Renvoie une instance de Color3f correspondant à la couleur dont
     * l'identifiant est passé en paramêtre. Cet identifiant doit exister dans
     * le fichier de configuration XML, sinon la couleur blanche sera renvoyée
     * par défaut.
     * 
     * @param colorIdent
     *            le nom de la couleur
     * @return la couleur demandée
     */
    public final Color3f getColor(String colorIdent) {
        float[] color = new float[3];
        boolean found = false;

        NodeList colorList = ((Element) rootNode.getElementsByTagName(
                "couleurs").item(0)).getElementsByTagName("couleur");

        for (int i = 0; !found && i < colorList.getLength(); i++) {
            Element xmlColor = (Element) colorList.item(i);
            if (xmlColor.getAttribute("id").equals(colorIdent)) {
                found = true;
                color[0] = Float.parseFloat(xmlColor.getAttribute("r"));
                color[1] = Float.parseFloat(xmlColor.getAttribute("v"));
                color[2] = Float.parseFloat(xmlColor.getAttribute("b"));
            }
        }

        if (!found) {
            color[0] = 1.0f;
            color[1] = 1.0f;
            color[2] = 1.0f;
        }
        return new Color3f(color);
    }

    /**
     * Renvoie la couleur du vaisseau dont l'id est passé en paramêtre. Le
     * fichier XML de configuration doit contenir un joueur dont l'id vaut le
     * numéro de son vaisseau (0, 1, etc).
     * 
     * @param aShipId
     *            l'identifiant du vaisseau
     * @return la couleur du vaisseau ayant cet identifiant
     */
    public final Color3f getColorFromId(int aShipId) {
        boolean found = false;
        String theColorIdent = null;
        Integer tmpShipId = new Integer(aShipId);

        NodeList playerList = ((Element) rootNode.getElementsByTagName(
                "joueurs").item(0)).getElementsByTagName("joueur");
        for (int i = 0; !found && i < playerList.getLength(); i++) {
            Element xmlPlayer = (Element) playerList.item(i);
            String id = xmlPlayer.getAttribute("id");
            if (id.equals(tmpShipId.toString())) {
                found = true;
                theColorIdent = xmlPlayer.getAttribute("couleur");
            }
        }
        if (theColorIdent == null) {
            theColorIdent = "Any";
        }
        return getColor(theColorIdent);
    }
}
