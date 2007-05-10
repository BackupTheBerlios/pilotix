package org.pilotix.server;

import org.pilotix.common.Ball;
import org.pilotix.common.Vector;

public class ServerBall extends Ball {

/*
 * NEW    -Collision:oui-> REMOVE    -Traitement-> supprime
 * ADD    -Collision:oui-> TO_REMOVE -Traitement-> REMOVE
 * NORMAL -Collision:oui-> TO_REMOVE -Traitement-> REMOVE
 *
 * NEW    -Collision:non-> NEW       -Traitement-> ADD
 * ADD    -Collision:non-> ADD       -Traitement-> NORMAL
 * NORMAL -Collision:non-> NORMAL    -Traitement-> NORMAL
 *
 * REMOVE -Collision:pas-> REMOVE    -Traitement-> supprime
 *
 * explication :
 * exemple avec la première ligne :
 * si la balle est dans l'état NEW et qu'elle subit une collision (collision:oui)
 * elle passe dans l'état REMOVE, le traitement
 * (qui est une machine d'état déterministe) la supprimera.
 *
 * lorsqu'une ball est créée, elle est dans l'état NEW.
 *
 * supprime n'est pas un état mais la suppresion reelle de la balle
 */

    public static final int NEW = 0;
    //public static final int ADD = 1;
    public static final int NORMAL = 2;
    public static final int TO_REMOVE = 3;
    //public static final int REMOVE = 4;

    private Vector nextPosition = new Vector();

    public ServerBall( int id,Vector aPosition, Vector aSpeed) {
        super(id, aPosition, aSpeed);
        states = NEW;
    }

    public void computeSpeedFromForces() {
        nextPosition.set(position.plus(speed));
    }

    public Vector getNextPosition() {
        return nextPosition;
    }
}