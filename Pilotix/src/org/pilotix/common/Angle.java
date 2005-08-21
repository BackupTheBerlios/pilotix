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

    //protected int oldDegree;
    private float degree;
    private int tmpDegree;
    /**
     * Cr�e un angle de valeur 0.
     */
    public Angle() {
        //oldDegree = 0;
        degree = 0;
    }

    /**
     * Cr�e un angle avec la valeur d'un autre angle en param�tre.
     *
     * @param anAngle
     *            un angle
     */
    public Angle(Angle anAngle) {
        //set(anAngle.oldDegree);
        set(anAngle.degree);
    }
    
    /**
     * Cr�e un angle avec la valeur pass�e en param�tre.
     *
     * @param aDegree
     *            la valeur de l'angle
     */
    /*public Angle(int aDegree) {
        set((float)aDegree);
    }*/
    
    public Angle(float aDegree) {
        set(aDegree);
    }

    /**
     * Met � jour cet angle avec la valeur pass�e en param�tre.
     *
     * @param aDegree
     *            valeur l'angle
     */
    /*public void set(int aDegree) {
        if (aDegree < 0) {
            oldDegree = aDegree % (-360);
        } else {
            oldDegree = aDegree % (360);
        }
    }*/
    
    public void set(float degree) {
    	if (degree < 0) {
            this.degree = degree % (-360);
        } else {
            this.degree = degree % (360);
        }
    }

    /**
     * Mise � jour de l'angle avec la valeur d'un autre angle.
     *
     * @param anAngle
     *            l'angle dont on veut recopier la valeur
     */
    public void set(Angle anAngle) {
        //oldDegree = anAngle.oldDegree;
        degree = anAngle.degree;
    }

    /**
     * R�cup�re la valeur de l'angle.
     *
     * @return un angle
     */
    public int intValue() {
    	return (int)degree;
    }
    
    public float floatValue() {
        return degree;
    }
    
    public byte[] getBytes() {
    	byte[] result = new byte[2];
        
    	tmpDegree = (int)(degree*100);
    	
    	result[0]= (byte) (((byte)(Math.abs(tmpDegree) / 256))| (tmpDegree<0?0x80:0x00));
    	result[1]=(byte) (Math.abs(tmpDegree));
        return result;
    }
    
    public void setBytes(byte[] bytes,int offset) {
    	tmpDegree = 0;
        int inc = 1;
        int inc2 = 256;
        for (int i = 0; i < 8; i++) {
        	// most left bit code negative
        	if(i==7&&((byte) (bytes[offset] >> i) & 0x01)==1){
            	tmpDegree *= -1;
            }else{
            	tmpDegree += ((byte) (bytes[offset] >> i) & 0x01) * inc2;
            }
            tmpDegree += ((byte) (bytes[offset+1] >> i) & 0x01) * inc;
            inc = inc << 1;
            inc2 = inc2 << 1;
        }
        degree = tmpDegree/100;
    }
}
