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
 * Angle en degr�s compris entre 0 et 359,
 * les angles en dehors de cette plage �tant
 * ramen�s dedans par un modulo.
 */
public class Angle {

    protected int degree;

    /**
     * Cr�e un angle de valeur 0.
     */
    public Angle() {
        degree = 0;
    }

    /**
     * Cr�e un angle avec la valeur pass�e en param�tre.
     *
     * @param aDegree
     *            la valeur de l'angle
     */
    public Angle(int aDegree) {
        set(aDegree);
    }

    /**
     * Met � jour cet angle avec la valeur pass�e en param�tre.
     *
     * @param aDegree
     *            valeur l'angle
     */
    public void set(int aDegree) {
        if (aDegree < 0) {
            degree = aDegree % (-360);
        } else {
            degree = aDegree % (360);
        }
    }

    /**
     * Mise � jour de l'angle avec la valeur d'un autre angle.
     *
     * @param anAngle
     *            l'angle dont on veut recopier la valeur
     */
    public void set(Angle anAngle) {
        degree = anAngle.degree;
    }

    /**
     * R�cup�re la valeur de l'angle.
     *
     * @return un angle
     */
    public int get() {
        return degree;
    }

}
