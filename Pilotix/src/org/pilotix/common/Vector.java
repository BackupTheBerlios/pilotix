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
 * Vecteur 2D � multiples usages : position, vitesse, acc�leration.
 *
 * @author Florent Sithimolada
 */
public class Vector {

    public int x;
    public int y;

    /**
     * Cr�e un vecteur de valeur (0,0).
     */
    public Vector() {
        this(0,0);
    }

    /**
     * Cr�e un vecteur avec les valeurs fournies.
     *
     * @param px
     *            valeur de x
     * @param py
     *            valeur de y
     */
    public Vector(int px, int py) {
        x = px;
        y = py;
    }
    
    /**
     * Cr�e un vecteur identique � celui fourni.
     *
     * @param aVector
     *            le vecteur � dupliquer
     */
    public Vector(Vector aVector){
        x = aVector.x;
        y = aVector.y;
    }

    /**
     * Met � jour un vecteur avec les valeurs fournies.
     *
     * @param px
     *            nouvelle valeur de x
     * @param py
     *            nouvelle valeur de y
     */
    public void set(int px, int py) {
        x = px;
        y = py;
    }

    /**
     * Met � jour un vecteur avec un autre vecteur
     *
     * @param b
     *            le vecteur dont chaque composante sera
     *            copi�e dans le vecteur courant
     */
    public void set(Vector b) {
        x = b.x;
        y = b.y;
    }

    /**
     * Multiplie chaque terme du vecteur par les valeurs
     * correspondantes du vecteur pass� en param�tre.
     *
     * @param b
     *            le vecteur dont chaque composante sert
     *            de multiplicateur aux composantes du
     *            vecteur courant
     */
    public void setDot(Vector b) {
        x = x * b.x;
        y = y * b.y;
    }
    
    public int dot(Vector b) {
        return  x * b.x + y * b.y;
    }
    
    public Vector plus(Vector b) {
        Vector result = new Vector();
        result.x = x + b.x;
        result.y = y + b.y;
        return result;
    }
    
    public Vector less(Vector b) {
        Vector result = new Vector();
        result.x = x - b.x;
        result.y = y - b.y;
        return result;
    }
    
    public Vector mult(double aNumber) {
        Vector result = new Vector();
        result.x = (int) Math.round(aNumber * this.x);
        result.y = (int) Math.round(aNumber * this.y);
        return result;
    }

    
    public Vector plus(int x, int y) {
        Vector result = new Vector(this.x+x,this.y+y);
        return result;
    }
}
