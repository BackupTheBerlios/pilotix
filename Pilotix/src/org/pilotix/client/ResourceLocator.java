/*
 * Pilotix : a multiplayer piloting game. Copyright (C) 2003 Pilotix.Org
 * 
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation; either version 2 of the License, or (at your option) any later
 * version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * this program; if not, write to the Free Software Foundation, Inc., 59 Temple
 * Place - Suite 330, Boston, MA 02111-1307, USA.
 */

package org.pilotix.client;

import java.net.URL;
import java.io.File;

/**
 * Cette classe sert à renvoyer l'URL d'un fichier à
 * partir de son type (CONFIG, TEXTURE, SHAPE, etc),
 * en cherchant d'abord sur le disque, puis dans le Jar.
 */
public class ResourceLocator {

    private ClassLoader thisClassLoader;

    public final static int CONFIG   = 0,
                            TEXTURE  = 1,
                            SHIP     = 2,
                            SHAPE    = 3,
                            AREA     = 4;

    public ResourceLocator() {
        thisClassLoader = this.getClass().getClassLoader();
    }

    /**
    * Cette méthode renvoie l'URL de la ressource dont le
    * type et le nom sont fournis. La recherche est faites
    * prioritairement sur le disque, puis en cas d'échec,
    * dans le fichier Jar.
    *
    * @param resourceType
    *          le type du fichier
    * @param resourceName
    *          le nom du fichier avec son extension
    * @return
    *          l'URL du fichier, ou null s'il est introuvable
    */
    public URL getResource(int resourceType, String resourceName) {
        URL resURL = null;

        switch (resourceType) {
        case CONFIG:
            resURL = searchOnDisk("/" + Environment.configPath + resourceName);
            if (resURL == null) {
                resURL = thisClassLoader.getResource(
                               Environment.configPath + resourceName);
                if (Environment.debug) {
                    System.out.println("[ResourceLocator] Config dans Jar: "+resourceName);
                }
            }
            else if (Environment.debug) {
                System.out.println("[ResourceLocator] Config sur disque: "+resourceName);
            }
            break;
        case TEXTURE:
            resURL = searchOnDisk("/" + Environment.texturesPath + resourceName);
            if (resURL == null) {
                resURL = thisClassLoader.getResource(Environment.texturesPath
                        + resourceName);
                if (Environment.debug) {
                    System.out.println("[ResourceLocator] Texture dans Jar: "+resourceName);
                }
            }
            else if (Environment.debug) {
                System.out.println("[ResourceLocator] Texture sur disque: "+resourceName);
            }
            break;
        case SHIP:
            resURL = searchOnDisk("/" + Environment.shipsPath + resourceName);
            if (resURL == null) {
                resURL = thisClassLoader.getResource(Environment.shipsPath
                        + resourceName);
                if (Environment.debug) {
                    System.out.println("[ResourceLocator] Ship dans Jar: "+resourceName);
                }
            }
            else if (Environment.debug) {
                System.out.println("[ResourceLocator] Ship sur disque: "+resourceName);
            }
            break;
        case SHAPE:
            resURL = searchOnDisk("/" + Environment.shapesPath + resourceName);
            if (resURL == null) {
                resURL = thisClassLoader.getResource(Environment.shapesPath
                        + resourceName);
                if (Environment.debug) {
                    System.out.println("[ResourceLocator] Shape dans Jar: "+resourceName);
                }
            }
            else if (Environment.debug) {
                System.out.println("[ResourceLocator] Shape sur disque: "+resourceName);
            }
            break;
        case AREA:
            resURL = searchOnDisk("/" + Environment.areasPath + resourceName);
            if (resURL == null) {
                resURL = thisClassLoader.getResource(Environment.areasPath
                        + resourceName);
                if (Environment.debug) {
                    System.out.println("[ResourceLocator] Area dans Jar: "+resourceName);
                }
            }
            else if (Environment.debug) {
                System.out.println("[ResourceLocator] Area sur disque: "+resourceName);
            }
            break;
        default:
            resURL = null;
            break;
        }

        return resURL;
    }

    private URL searchOnDisk(String filePath) {
        try {
            File fl = new File(System.getProperty("user.dir") + filePath);
            if (fl.exists())
                return fl.toURL();
            else {
                return null;
            }
        } catch (java.net.MalformedURLException e) {
            e.printStackTrace();
            return null;
        }
    }
}
