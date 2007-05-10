/*
 Pilotix : a multiplayer piloting game.
 Copyright (C) 2003 Pilotix.Org

 This program is isFree software; you can redistribute it and/or
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
 * Similaire à un Array simple, IterableArray comporte néanmoins un chainage
 * bi-directionnel reliant toutes ses cases non vides. La principale différence
 * avec OldIterrableArray (qui va prochainement être mis au grenier!), est qu'il
 * est possible d'insérer n'importe où dans le tableau, alors qu'il fallait
 * demander à OldIterableArray de faire ce placement pour nous.
 *
 * Pour de bonnes performances il sera nécessaire d'effectuer des insertions avant
 * le premier élement non vide du tableau, ou après le dernier.
 *
 * Les accès à hasNext(), next() et reset() qui servent pour le parcours chainé
 * devront être faits en exclusion mutuelle.
 *
 */

public class IterableArray {

    /** contient les objets eux meme */
    private Object[] objects;
    /** la case est elle vide ? */
    //private boolean[] isFree;
    /** numero de la case non vide suivante -1 equivaut a null */
    private int[] prev;
    /** numero de la case non vide precedente -1 equivaut a null */
    private int[] next;
    /** nombre de case non vide dans l'array */
    private int nb;
    /** nombre de case potentiel de l'array */
    private int max;
    /** pointeur sur la premiere case non vide */
    private int first;
    /** pointeur sur la derniere case non vide */
    private int last;
    /** pointeur sur la case non vide en train d'etre parcouru */
    private int current;
    private int current2;

    public IterableArray(int nbmax) {
        objects = new Object[nbmax];
        //isFree = new boolean[nbmax];
        prev = new int[nbmax];
        next = new int[nbmax];
        /*
         * for (int i = 0; i < nbmax; i++) { //isFree[i] = true; prev[i] = i -
         * 1; next[i] = i + 1; } next[nbmax - 1] = -1;
         */
        nb = 0;
        max = nbmax;
        first = -1;
        last = -1;
        current = -1;
        current2 = -1;
    }

    /**
     * Insertion de l'objet dans la case de numéro donné. Attention les
     * insertions au milieu peuvent prendre jusqu'à un temps nbmax.
     *
     * @param index
     *            numero de la case où sera stocké l'objet
     * @param obj
     *            objet à stocker
     * @throws Exception
     *             quand le tableau est plein
     */
    public synchronized void add(int index, Object obj) {
        if (nb == max) {
            //throw new Exception("IterableArray Full");
            System.out.println("IterableArray Full");
        } else if (nb == 0) { // premiere insertion
            //System.out.println("insert Tete");
            first = last = current = current2 = index;
            next[index] = prev[index] = -1;
            //isFree[index]=false;
        } else if (index < first) { // insertion avant le premier
            prev[index] = -1;
            next[index] = first;
            prev[first] = index;
            first = index;
            //isFree[index]=false;
        } else if (last < index) { // insertion apres le dernier
            //System.out.println("insert fin");
            prev[index] = last;
            next[index] = -1;
            next[last] = index;
            last = index;
            //isFree[index]=false;
        } else {//insertion entre premier et dernier
            int currentSearch = first;
            while (next[currentSearch] < index) {
                currentSearch = next[currentSearch];
            }
            prev[next[currentSearch]] = index;
            next[index] = next[currentSearch];
            next[currentSearch] = index;
            prev[index] = currentSearch;
        }
        objects[index] = obj;
        nb++;
    }

    /**
     * Récupère la donnée stockée
     *
     * @param index
     *            numéro de la case à récupérer
     * @return l'objet stocké
     */
    public Object get(int index) {//throws Exception {
        /*
         * if (isFree[index]) throw new Exception("IterableArray2 already
         * empty"); else
         */
        return objects[index];
    }

    /**
     * Modifie la donnée stockée
     *
     * @param index
     *            numéro de la case à modifier
     * @param obj
     *            l'objet à écrire dans la case
     */
    public void set(int index, Object obj) {//throws Exception {
        objects[index] = obj;
    }

    /**
     * Teste la présence d'un objet à la case indiquée.
     *
     * @param index
     *            numéro de la case à tester
     * @return vrai s'il y a un objet, faux sinon
     */
    public boolean isNull(int index) {
        return objects[index] == null;
    }

    /**
     * retourne le nombre de cases non vides
     * @return le nombre de cases utilisées
     */
    public int size() {
        return nb;
    }

    /**
     * Supprime l'objet situé à l'emplacement donné.
     *
     * @param index
     *            numéro de la case à supprimer
     * @throws Exception
     *             si le tableau est déjà vide
     */
    public synchronized void remove(int index) {
        //System.out.println("[IterrableArray.remove(index="+index+") nb="+nb);
        if (nb == 0) {
            //throw new Exception("IterableArray already empty");
            System.out.println("[IterableArray] already empty " + index);
        } else if (index == first) {
            if (nb == 1) {
                first = -1;
                last = -1;
                nb = 0;
            } else {
                //System.out.println("[IterableArray.remove()] Cas index==first et nb>1, first:=next[index]:="+next[index]);
                first = next[index];
                prev[first] = -1;
                nb--;
            }
        } else if (index == last) {
            if (nb == 1) {
                first = -1;
                last = -1;
                nb = 0;
            } else {
                last = prev[index];
                next[last] = -1;
                nb--;
            }
        } else {
            int save = prev[index];
            next[prev[index]] = next[index];
            prev[next[index]] = save;
            nb--;
        }
        objects[index] = null;
    }

    /**
     * efface tout le contenu de l'array
     */
    public void clear() {
        nb = 0;
        first = -1;
        last = -1;
        current = -1;
        current2 = -1;
        for (int i = 0; i < max; i++)
            objects[i] = null;
    }

    /**
     * positionne le curseur sur la premier case non vide
     *
     */
    public void cursor1OnFirst() {
        current = first;
    }

    /**
     * positionne le curseur sur la derniere case non vide
     *
     */
    /*public void cursor1OnLast() {
     current = last;
     }*/

    public boolean cursor1IsNotNull() {
        return current != -1;
    }

    public boolean cursor1IsNull() {
        return current == -1;
    }

    public Object cursor1Get() {
        return objects[current];
    }

    public int cursor1GetIndex() {
        return current;
    }

    public void cursor1Next() {
        current = next[current];
    }

    /*public Object cursor1getAndStepNext() {
     Object result = objects[current];
     current = next[current];
     return result;
     }*/

    /*public Object cursor1getAndStepPrev() {
     Object result = objects[current];
     current = prev[current];
     return result;
     }*/

    /**
     * positionne le curseur sur la premier case non vide
     *
     */
    public void cursor2OnFirst() {
        current2 = first;
    }

    /**
     * positionne le curseur sur la derniere case non vide
     *
     */
    /*public void setCursor2OnLast() {
     current2 = last;
     }*/

    public boolean cursor2IsNotNull() {
        return current2 != -1;
    }

    public void cursor2Next() {
        current2 = next[current2];
    }

    public Object cursor2Get() {
        return objects[current2];
    }

    public int cursor2GetIndex() {
        return current2;
    }

    /*public boolean cursor2hasPrev() {
     return current2 != -1;
     }*/

    /*public Object cursor2getAndStepNext() {
     Object result = objects[current2];
     current2 = next[current2];
     return result;
     }

     public Object cursor2getAndStepPrev() {
     Object result = objects[current2];
     current2 = prev[current2];
     return result;
     }*/

    private void afficherEtat() {
        System.out.println();
        for (int i = 0; i < max; i++) {
            if ((i == first) && (i == last)) {
                System.out.print("|");
            } else if (i == first) {
                System.out.print("[");
            } else if (i == last) {
                System.out.print("]");
            } else {
                System.out.print(" ");
            }
        }
        System.out.println();
        for (int i = 0; i < max; i++) {
            if (prev[i] == -1) {
                System.out.print("X");
            } else {
                System.out.print(prev[i]);
            }
        }
        System.out.println();
        for (int i = 0; i < max; i++) {
            if (objects[i] == null) {
                System.out.print(" ");
            } else {
                System.out.print("#");
            }
        }
        System.out.println();
        for (int i = 0; i < max; i++) {
            if (next[i] == -1) {
                System.out.print("X");
            } else {
                System.out.print(next[i]);
            }
        }
        System.out.println();
    }

    public void copyInto(IterableArray cible, Action action) {
        // Initialisation des curseurs dans la source et dans la cible
        this.cursor1OnFirst();
        cible.cursor1OnFirst();
        while (this.cursor1IsNotNull() || cible.cursor1IsNotNull()) {
            if (cible.cursor1IsNull()) {
                // Le cas où la cible est vide (et donc la source ne l'est pas,
                // car dans le cas contraire on serait sorti du "while")
                // => ajout de la source dans la cible
                cible.add(current, action.add(objects[current]));
                cible.cursor1SetIndexAfter(current);
                this.cursor1Next();
            } else {
                // Les cas où la cible n'est pas vide...
                if (this.cursor1IsNull()) {
                    // Si la source est vide
                    // => suppression de l'objet dans la cible
                    //System.out.println("Début du cas cible pas vide et source vide, cursor1="+current);
                    action.remove(cible.get(cible.current));
                    cible.remove(cible.current);
                    cible.cursor1Next();
                    //System.out.println("Fin du cas cible pas vide et source vide, cursor1:="+current);
                } else if (cible.current < current) {
                    //System.out.println("remove on leser");
                    //  suppression de l'objet
                    //System.out.println("Début du cas cible.current="+cible.current+" < current="+current);
                    action.remove(cible.get(cible.current));
                    cible.remove(cible.current);
                    cible.cursor1SetIndexAfter(current);
                    this.cursor1Next();
                    //System.out.println("Fin du cas -> cible.current:="+cible.current+" et current:="+current);
                } else if (cible.current > current) {
                    //ajout d'un nouvel objet
                    //System.out.println("new add");
                    cible.add(current, action.add(objects[current]));
                    this.cursor1Next();
                } else if (cible.current == current) {
                    //System.out.println("update");
                    //mise a jour de objet
                    action.update(objects[current], cible.get(cible.current));
                    cible.cursor1Next();
                    this.cursor1Next();
                }
            }
        }
    }


    private void cursor1SetIndexAfter(int index) {
       current=first;
       //System.out.println("cursor after"+index);
       //System.out.println("First="+current);
       while ((current != -1)&& (current <= index)){
           //System.out.println("current"+ current);
           //System.out.println("Next current"+next[current]);
           current = next[current];
        }
        //System.out.println("finish="+current);
       //current = anIndex
    }

    public interface Action {

        public Object add(Object obj);

        public void update(Object modele, Object modifie);

        public void remove(Object object);

    }

    /*
     public static void main(String[] args) {
     IterableArray ia2 = new IterableArray(10);
     System.out.println("Test de IterrableArray");
     try {
     ia2.afficherEtat();
     ia2.add(5,"Cinq");
     ia2.afficherEtat();
     ia2.add(2,"Deux");
     ia2.afficherEtat();
     ia2.add(8,"Huit");
     ia2.afficherEtat();
     ia2.add(7,"Sept");
     ia2.afficherEtat();
     ia2.add(0,"Zero");
     ia2.afficherEtat();
     ia2.remove(8);
     ia2.afficherEtat();
     ia2.remove(0);
     ia2.afficherEtat();
     ia2.remove(5);
     ia2.afficherEtat();

     ia2.setCursorOnFirst();
     while (ia2.hasNext()) {
     System.out.print(ia2.next());
     }
     System.out.println();

     ia2.setCursorOnLast();
     while (ia2.hasPrev()) {
     System.out.print(ia2.prev());
     }
     System.out.println();

     System.out.println("Un");
     ia2.add(1, "Un");
     System.out.println(ia2.get(1));
     ia2.afficherEtat();
     System.out.println("-");
     ia2.clear();

     System.out.println("UnDeux");
     ia2.add(1, "Un");
     ia2.add(2, "Deux");
     ia2.setCursorOnFirst();
     while (ia2.hasNext()) {
     System.out.print(ia2.next());
     }
     ia2.afficherEtat();
     System.out.println("\n-");
     ia2.clear();

     System.out.println("UnTrois");
     ia2.add(1, "Un");
     ia2.add(2, "Deux");
     ia2.remove(2);
     ia2.add(3, "Trois");
     ia2.setCursorOnFirst();
     while (ia2.hasNext()) {
     System.out.print(ia2.next());
     }
     ia2.afficherEtat();
     System.out.println("\n-");
     ia2.clear();

     System.out.println("TroisDeuxUn");
     ia2.add(1, "Un");
     ia2.add(2, "Deux");
     ia2.add(3, "Trois");
     ia2.setCursorOnLast();
     while (ia2.hasPrev()) {
     System.out.print(ia2.prev());
     }
     ia2.afficherEtat();
     System.out.println("\n-");
     ia2.clear();

     System.out.println("UnTroisCinqSept");
     ia2.add(1, "Un");
     ia2.afficherEtat();
     ia2.add(7, "Sept");
     ia2.afficherEtat();
     ia2.add(3, "Trois");
     ia2.afficherEtat();
     ia2.add(5, "Cinq");
     ia2.afficherEtat();
     ia2.setCursorOnFirst();
     while (ia2.hasNext()) {
     System.out.print(ia2.next());
     }
     //ia2.afficherEtat();
     System.out.println("\n-");
     ia2.clear();
     } catch (Exception e) {
     e.printStackTrace();
     }
     }
     */
}
