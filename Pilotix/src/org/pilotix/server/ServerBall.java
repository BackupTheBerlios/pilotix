package org.pilotix.server;

import org.pilotix.common.Ball;
import org.pilotix.common.Vector;

public class ServerBall extends Ball {

   
    public static final int TO_DELETE = 2;
    //private int state;
    
    private Vector nextPosition = new Vector();
    
    //private Vector nextSpeed = new Vector();
    

    /**
     * @param aPosition
     * @param aSpeed
     */
    public ServerBall(Vector aPosition, Vector aSpeed) {
        super(aPosition, aSpeed);
        states = NEW;
    }

    /**
     * @return Returns the state.
     */
    public int getState() {
        return states;
    }

    /**
     * @param state The state to set.
     */
    public void setState(int state) {
        this.states = state;
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