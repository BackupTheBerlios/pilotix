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

public class ResourceLocator {

    private ClassLoader thisClassLoader;

    public final static int CONFIG = 0, TEXTURE = 1, SHIP = 2, SHAPE = 3,
            AREA = 4;

    public ResourceLocator() {
        thisClassLoader = this.getClass().getClassLoader();
    }

    public URL getResource(int resourceType, String resourceName) {
        URL resURL = null;
        switch (resourceType) {
        case CONFIG:
            System.out.println("CONFIG LOADING");
            /*
             * resURL = this.getClass().getResource(Environment.configPath +
             * resourceName); if (resURL == null) resURL =
             * this.getClass().getResource("/" + Environment.configPath +
             * resourceName); if (resURL == null) resURL =
             * ClassLoader.getSystemResource(Environment.configPath +
             * resourceName); if (resURL == null) resURL =
             * ClassLoader.getSystemResource("/" + Environment.configPath +
             * resourceName); javax.swing.JOptionPane.showMessageDialog(null,
             * "URL=" + resURL);
             */
            System.out.println(resourceName);
            resURL = searchOnDisk("/" + Environment.configPath + resourceName);
            if (resURL == null) {
                System.out.println("SearchOndisk dont work");
                resURL = thisClassLoader.getResource(Environment.configPath
                        + resourceName);
            } else {
                System.out.println("SearchOndisk work");
            }
            //JOptionPane.showMessageDialog(null,"URL=" + resURL);
            break;

        //return ClassLoader.getSystemResource(Environment.configPath +
        // resourceName);
        case TEXTURE:
            System.out.println("SHIP LOADING");           
            resURL = searchOnDisk("/" + Environment.texturesPath + resourceName);
            if (resURL == null) {
                System.out.println("SearchOndisk dont work");
                resURL = thisClassLoader.getResource(Environment.texturesPath
                        + resourceName);
            } else {
                System.out.println("SearchOndisk work");
            }
            break;
        case SHIP:
            System.out.println("SHIPE LOADING");           
            resURL = searchOnDisk("/" + Environment.shipsPath + resourceName);
            if (resURL == null) {
                System.out.println("SearchOndisk dont work");
                resURL = thisClassLoader.getResource(Environment.shipsPath
                        + resourceName);
            } else {
                System.out.println("SearchOndisk work");
            }            
            break;
        case SHAPE:
            System.out.println("SHAPE LOADING");            
            resURL = searchOnDisk("/" + Environment.shapesPath + resourceName);
            if (resURL == null) {
                System.out.println("SearchOndisk dont work");
                resURL = thisClassLoader.getResource(Environment.shapesPath
                        + resourceName);
            } else {
                System.out.println("SearchOndisk work");
            }
            break;
        case AREA:
            System.out.println("AREA LOADING");          
            resURL = searchOnDisk("/" + Environment.areasPath + resourceName);
            if (resURL == null) {
                System.out.println("SearchOndisk dont work");
                resURL = thisClassLoader.getResource(Environment.areasPath
                        + resourceName);                
            } else {
                System.out.println("SearchOndisk work");
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
            //System.out.println("||||||"+System.getProperty("user.dir")+"|||||||");
            File fl = new File(System.getProperty("user.dir") + filePath);
            if (fl.exists())
                return fl.toURL();
            else
                return null;
        } catch (java.net.MalformedURLException e) {
            return null;
        }
    }
}