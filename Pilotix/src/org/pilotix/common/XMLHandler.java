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

import org.w3c.dom.Document;
import javax.xml.parsers.*;
import java.net.URL;

/**
 * Cette classe sert � construire un Document DOM � partir de l'URL d'un
 * fichier �crit en XML. Pour le moment elle contient �galement quelques
 * param�tres du client, qui seront d�plac�s par la suite.
 *
 * @author Lo�c Guibart
 * @author Gr�goire Colbert
 *
 * @see org.w3c.dom.Document
 */
public class XMLHandler {

    private boolean isValid = false;
    private static  DocumentBuilder docBuilder;

    /**
     * Construit une repr�sentation DOM d'un fichier XML � partir de son URL.
     * Par la suite ce constructeur ne prendra plus de param�tre...
     *
     * @param fileURL
     *            l'URL du nom du fichier XML � charger
     */
    public XMLHandler(URL fileURL) {
        try {
            DocumentBuilderFactory builderFactory = DocumentBuilderFactory
                    .newInstance();
            // l'URL de la DTD est indiqu�e dans le fichier XML,
            // inv�rifiable dans le cas d'un fichier XML situ� dans un JAR
            //builderFactory.setValidating(true);
            builderFactory.setValidating(false);
            docBuilder = builderFactory.newDocumentBuilder();

            Document document = docBuilder.parse(fileURL.openStream());
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
     *            URL du fichier XML � parser.
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
     *            adresse/nom du fichier XML � parser.
     * @return L'objet org.w3c.dom.Document correspondant au contenu du fichier
     *         XML
     */
    /*public static Document getDocumentFromFile(String uri) {
        try {
            return docBuilder.parse(uri);
        } catch (Exception e) {
            System.out.println("[XMLHandler.getDocumentFromFile]" + e.getMessage());
            return null;
        }
    }*/
}
