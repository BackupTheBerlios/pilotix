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

package org.pilotix.common;

/**
 * This class is an array where all non-free box are linked and can be itered
 * like a LinkedList.
 * 
 * @author Florent Sithimolada
 */

public class IterableArray {

    private Object[] objects;
    private boolean[] free;
    private int[] prev;
    private int[] next;
    private int nb;
    private int max;
    private int nextFree;
    private int lastInserted;
    private int current;

    /**
     * @param nbmax
     *            size max of the Array
     */
    public IterableArray(int nbmax) {
        objects = new Object[nbmax];
        free = new boolean[nbmax];
        prev = new int[nbmax];
        next = new int[nbmax];
        for (int i = 0; i < nbmax; i++) {
            free[i] = true;
            prev[i] = i - 1;
            next[i] = i + 1;
        }
        next[nbmax - 1] = -1;
        nb = 0;
        max = nbmax;
        nextFree = 0;
        lastInserted = -1;
        current = -1;
    }

    /**
     * @param obj
     *            the Object to insert
     * @return address where the object as been inserted
     */
    public synchronized int add(Object obj) throws Exception {
        if (nb >= max) {
            System.out.println("Array Full !!!!");
            Exception e = new Exception();
            throw e;
        } else {
            objects[nextFree] = obj;
            free[nextFree] = false;
            nb++;
            if (lastInserted == -1) {
                lastInserted = nextFree;
                nextFree = next[nextFree];
                prev[nextFree] = -1;// pas oblibgatoire
                prev[lastInserted] = -1;
                next[lastInserted] = -1;
            } else {
                next[lastInserted] = nextFree;
                prev[nextFree] = lastInserted;
                lastInserted = nextFree;
                nextFree = next[nextFree];
                //prev[nextFree] = -1;// pas oblibgatoire
                next[lastInserted] = -1; // pas oblibgatoire
            }
            current = lastInserted;
            return lastInserted;
        }
    }

    /**
     * @param indice
     *            indice of the object that as to be remove
     */
    public synchronized void remove(int indice) throws Exception {
        if ((indice < 0) || (max < indice)) {
            System.out.println("indice Out of bounds !!!!");
            Exception e = new Exception();
            throw e;
        }
        if (free[indice] == true) {
            System.out.println("indice already free !!!");
            Exception e = new Exception();
            throw e;
        }
        free[indice] = true;
        objects[indice] = null;
        if ((prev[indice] == -1) && (next[indice] == -1)) {
            lastInserted = -1;
        } else if (prev[indice] == -1) {
            prev[next[indice]] = -1;
            lastInserted = next[indice];
        } else if (next[indice] == -1) {
            next[prev[indice]] = -1;
            lastInserted = prev[indice];
        } else {
            prev[next[indice]] = prev[indice];
            next[prev[indice]] = next[indice];
        }
        current = lastInserted;
        // insertion en tete a NextFree de indice
        next[indice] = nextFree;
        nextFree = indice;
        nb--;
    }

    /**
     * @param indice
     *            the Object to get
     * @return the Object
     */
    public Object get(int indice) {
        return objects[indice];
    }

    /**
     * @return the number of Object in the Array
     */
    public int size() {
        return nb;
    }

    /**
     * @return if this is one more left object to get
     */
    public boolean hasNext() {
        if (current == -1) {
            current = lastInserted;
            return false;
        } else {
            return true;
        }
    }

    /**
     * @return return the next object currently pointed by the an interne
     *         cursor
     */
    public Object next() {
        int saveCurrent = current;
        current = prev[current];
        return objects[saveCurrent];
    }
    
    /**
     * @return return set the in Builded cursor to the begining of the list 
     */
    public void reset() {
        current = lastInserted;
    }
}
