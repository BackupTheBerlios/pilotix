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

public class IdHandler {

    private int nbId;
    private int nbMaxId;
    private boolean[] tab;

    public IdHandler() {
        nbId = 0;
        nbMaxId = 0;        
    }
    
    public void setNbMaxIds(int nb){
    	nbMaxId = nb;
    	tab = new boolean[nbMaxId];
    }

    /*public synchronized void needToWait() throws InterruptedException {
        if (nbMaxId == nbId) {
            wait();
        }
    }*/

    public synchronized int getId() {
        int i = 0;        
        while (tab[i] == true) {
            i++;
        }
        tab[i] = true;
        nbId++;
        System.out.println("[IdHandler] New Id given : " + i);
        return i;
    }

    public synchronized void giveBackId(int indice) throws InterruptedException {
        tab[indice] = false;
        if (nbMaxId == nbId) {
            nbId--;
            //notify();

        } else {
            nbId--;
        }
        System.out.println("[IdHandler] players " + indice
                + " give back his Id");
        System.out.println("[IdHandler] Nb players :" + nbId);
    }
}
