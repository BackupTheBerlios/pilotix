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

/*
 * 
 * Vector : type de base pour la représentation 2D
 * 
 * Auteurs : - Florent (dernière modif : 16/04/2003)
 *  
 */
package org.pilotix.common;

/**
 * vecteur 2D a multiple usage : position vitesse acceleration
 */
public class Vector {

    public int x;

    public int y;
    /**
     * creation d'un vecteur  valeur initial = (0,0)
     * 
     * @param px
     *            valeur x
     * @param py
     *            valeur y 
     */
    public Vector() {
        this(0,0);
    }
    /**
     * creation d'un vecteur avec deux valeur initial
     * 
     * @param px
     *            valeur x
     * @param py
     *            valeur y 
     */
    public Vector(int px, int py) {
        x = px;
        y = py;
    }
    /**
     * mise a jour d'un vecteur avec deux valeur initial
     * 
     * @param px
     *            valeur x
     * @param py
     *            valeur y 
     */
    public void set(int px, int py) {
        x = px;
        y = py;
    }
    /**
     * mise a jour d'un vecteur avec un autre vecteur
     * 
     * @param px
     *            valeur x
     * @param py
     *            valeur y 
     */
    public void set(Vector b) {
        x = b.x;
        y = b.y;
    }
    /**
     * produit  vectoriel entre 2 vecteur
     * 
     * @param px
     *            valeur x
     * @param py
     *            valeur y 
     */
    public void dot(Vector b) {
        x = x * b.x;
        y = y * b.y;
    }
}