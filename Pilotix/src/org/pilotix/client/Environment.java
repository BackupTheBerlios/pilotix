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

import org.pilotix.common.ResourceLocator;
import org.pilotix.common.XMLHandler;

public class Environment {

    public static ResourceLocator theRL = null;

    public static GUI theGUI = null;
    public static ClientMainLoopThread theClientMainLoopThread = null;
    public static Controls theControls = null;
    public static ControlCommand controlCmd = null;
    public static Display3D theDisplay3D = null;
    public static ClientArea theClientArea = null;
    public static XMLHandler theXMLConfigHandler = null;
    public static XMLHandler theXMLHandler = null;
    public static UserConfigHandler userConfig = null;
    public static ClientConfigHandler clientConfig = null;

    public static String theServerIP = new String("localhost");
    public static Integer theServerPort = new Integer(9000);

    public static String dataPath = System.getProperty("pilotix.data.path")
            + "/";

    public static String propertiesPath = "properties/";
    //public static String propertiesPath = System.getProperty("pilotix.properties.path")
    //+ "/";
    
    public static final float u3d = 0.01f;
    public static boolean debug = true;
}
