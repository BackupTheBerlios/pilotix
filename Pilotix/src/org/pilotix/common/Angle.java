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
 * Angle en degree modulo entre 0 et 359
 */
public class Angle {

    protected int degree;

    /**
     * creation d'un angle de valeur 0
     */
    public Angle() {
        degree = 0;
    }

    /**
     * creation d'un angle avec une valeur passe en parametre
     * 
     * @param aDegree
     *            valeur l'angle
     */
    public Angle(int aDegree) {
        set(aDegree);
    }

    /**
     * mise a jour de angle avec une valeur passe en parametre
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
     * mise a jour de angle avec une valeur d'un autre angle
     * 
     * @param anAngle
     *            un angle
     */
    public void set(Angle anAngle) {
        degree = anAngle.degree;
    }

    /**
     * recupere la valeur de l'angle
     * 
     * @return un angle
     */
    public int get() {
        return degree;
    }

}