package org.pilotix.server;

import org.pilotix.common.Ball;
import org.pilotix.common.Vector;

public class ServerBall extends Ball {

    /** lorsque la balle doit etre diffuse*/
    public static final int NEW = 0;
    /** lorsque la balle doit etre supprime*/
    public static final int TO_DELETE = 1;

    private int state;
    
    private Vector nextPosition = new Vector();
    
    //private Vector nextSpeed = new Vector();
    

    /**
     * @param aPosition
     * @param aSpeed
     */
    public ServerBall(Vector aPosition, Vector aSpeed) {
        super(aPosition, aSpeed);
        state = NEW;
    }

    /**
     * @return Returns the state.
     */
    public int getState() {
        return state;
    }

    /**
     * @param state The state to set.
     */
    public void setState(int state) {
        this.state = state;
    }

    /**
     * 
     */
    public void computeSpeedFromForces() {
        nextPosition.set(position.plus(speed));        
    }

    
    public Vector getPosition() {        
        return position;
    }
    
    public Vector getNextPosition() {        
        return nextPosition;
    }

    public Vector getSpeed() {        
        return speed;
    }
   

    
}