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

package org.pilotix.server;

import org.pilotix.common.XMLHandler;
//import org.pilotix.common.*;
import java.util.LinkedList;


/**
 * Pilotix : lance le serveur du jeu
 * 
 * Auteurs : - Flo (derni�re modif : 1/09/2003)
 *  
 */

public class PilotixServer {
    
    public static int nbMaxPlayer;

    public static IdHandler theIH;
    public static ServerArea theSA;
    public static ServerMainLoopThread theSMLT;
    public static ClockerThread theCT;
    public static ConnexionHandlerThread theCHT;
    public static XMLHandler theXH;

    public static boolean newCHTs;
    public static LinkedList theNewCHTs;
    public static LinkedList theCHTs;
    
    public static String dataPath = System.getProperty("pilotix.data.path")
    + "/";
    /**
     * Lance le serveur du jeu
     * 
     * @param port
     *            port TCP sur lequel le serveur doit �couter
     * @param fps
     *            nombre d'images par seconde demand� (le serveur lui, fait se
     *            qu'il peut !)
     */

    public PilotixServer(int port, int fps) throws Exception {
        nbMaxPlayer = 4;
        theIH = new IdHandler(nbMaxPlayer);
        theXH = new XMLHandler();
        theSA = new ServerArea();
        theSA.setMap(dataPath+"areas/defaut.pilotix.area.xml");
        theSMLT = new ServerMainLoopThread();
        theCHT = new ConnexionHandlerThread(port);
        theCT = new ClockerThread(fps,theSMLT);

        theCHT.start();
        //Environment.theCT.setPriority(Thread.MAX_PRIORITY);
        theCT.start();

    }

    /**
     * Main du serveur, instancie un nouveau PilotixServer. N'utilise aucun
     * param�tre pour l'instant.
     */
    public static void main(String[] args) throws Exception {
        PilotixServer server = new PilotixServer(9000, 30);
    }
}
