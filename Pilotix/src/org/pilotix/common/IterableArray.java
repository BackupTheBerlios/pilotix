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
 * Similaire a un Array simple, IterableArray comporte neanmoins une chainage
 * bi-directionnel reliant toutes ses cases non vides. La principale differance
 * avec OldIterrableArray (qui va prochainement etre mis au grenier!), est qu'il
 * est possible d'inserer n'importe ou dans l'array, alors qu'il fallais
 * demander a IterrableArray de faire ce placement pour nous.
 * 
 * Pour de bonne performance il sera necessaire d'effectuer des insersions avans
 * le premier elements non vide de l'array, ou apres le derniers.
 * 
 * les acces a hasNext() next() et reset() qui serve pour le parcours chaine
 * devrons etre fait en exclusion mutuelle.
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
	}
	/**
	 * insertion de l'objet dans la case de numero donnee Attention les
	 * insertions au milieu peuvent prendre jusqu'a un temps nbmax
	 * 
	 * @param index
	 *            numero de la case ou sera stoque l'objet
	 * @param obj
	 *            objet a stoquer
	 * @throws Exception
	 *             quand l'array est pleine
	 */
	public synchronized void add(int index, Object obj)  {
		if (nb == max) {
			//throw new Exception("IterableArray Full");
			System.out.println("IterableArray Full");
		} else if (nb == 0) { // premiere insertion
			//System.out.println("insert Tete");
			first = last = current = index;
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
	 * recupere la donne stoque
	 * 
	 * @param index
	 *            numero de la case a recuperer
	 * @return l'objet stoque
	 */
	public Object get(int index) {//throws Exception {
		/*
		 * if (isFree[index]) throw new Exception("IterableArray2 already
		 * empty"); else
		 */
		return objects[index];
	}
	/**
	 * modifie la donne stoque
	 * 
	 * @param index
	 *            numero de la case a modifier
	 * @return l'objet stoque
	 */
	public void set(int index,Object obj) {//throws Exception {		
		objects[index] = obj;
	}
	/**
	 * retourne le nombre de case non vide
	 * @return
	 */
	public int size(){
		return nb;
	}
	/**
	 * 
	 * @param index
	 *            numero de la case a supprimer
	 * @throws Exception
	 *             si l'array est deja vide
	 */
	public synchronized void remove(int index) {
		if (nb == 0) {
			//throw new Exception("IterableArray already empty");
			System.out.println("IterableArray already empty");
		} else if (index == first) {
			if (nb == 1) {
				first = -1;
				last = -1;
				nb = 0;
			} else {
				first = next[index];
				prev[first] = -1;
			}
		} else if (index == last) {
			if (nb == 1) {
				first = -1;
				last = -1;
				nb = 0;
			} else {
				last = prev[index];
				next[last] = -1;
			}
		} else {
			int save = prev[index];
			next[prev[index]] = next[index];
			prev[next[index]] = save;
			nb--;
		}
		objects[index]=null;
	}
	/**
	 * efface tout le contenu de l'array
	 */
	public void clear() {
		nb = 0;
		first = -1;
		last = -1;
		current = -1;
		/*for (int i = 0; i < max; i++)
			objects[i] = null;*/
	}
	/**
	 * positionne le curseur sur la premier case non vide
	 *  
	 */
	public void setCursorOnFirst() {
		current = first;
	}
	/**
	 * positionne le curseur sur la derniere case non vide
	 *  
	 */
	public void setCursorOnLast() {
		current = last;
	}

	public boolean hasNext() {
		return current != -1;
	}
	public boolean hasPrev() {
		return current != -1;
	}

	public Object next() {
		Object result = objects[current];
		current = next[current];
		return result;
	}

	public Object prev() {
		Object result = objects[current];
		current = prev[current];
		return result;
	}

	private void afficherEtat() {
		System.out.println();
		for (int i = 0; i < max; i++) {
			if ((i == first) && (i == last)) {
				System.out.print("|");
			} else if(i == first){
				System.out.print("[");
			} else if(i== last){
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
	/*public static void main(String[] args) {
		IterableArray2 ia2 = new IterableArray2(10);
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
			
/*			System.out.println("Un");
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
*//*
		} catch (Exception e) {
			e.printStackTrace();
		}
	}*/
}