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

public class ClockerThread extends Thread {

    private int framePerSecond;
    private ServerMainLoopThread serverMainLoopThread;

    public ClockerThread(int fps, ServerMainLoopThread aServerMainLoopThread)
            throws Exception {
        framePerSecond = fps;
        System.out.println("[ClockerThread] Frames/s = "+framePerSecond);
        serverMainLoopThread = aServerMainLoopThread;
    }

    public void run() {    
        while (true) {
            /*
             * switch (i){ case 0 : System.out.print(" \\ \r");break; case 1 :
             * System.out.print(" | \r");break; case 2 : System.out.print(" /
             * \r");break; case 3 : System.out.print(" - \r");break; } i++;
             */
            serverMainLoopThread.run();
            try {
                sleep(1000 / framePerSecond);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
