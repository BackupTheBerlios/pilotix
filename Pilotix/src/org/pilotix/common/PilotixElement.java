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

package org.pilotix.common;

/**
 * Cette classe est la classe parente de toutes les classes
 * qui définissent des objets physiques du jeu.
 *
 * @author Florent Sithimolada
 */
public abstract class PilotixElement {

    protected int id;    
    protected int states;
    protected Vector position = new Vector();
    
    
    //byte[] byteCoded = null;
    //public static int lengthInByte = 0;

    public PilotixElement(){};
    
    public int getId() {
        return id;
    }

    /*public abstract void setFromBytes(byte[] bytes);

    public abstract byte[] getAsBytes();    

    public abstract int getLengthInByte();*/
}
