/*
 * Created on 10 avr. 2004
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package org.pilotix.common;


/**
 * @author flo
 *
 * To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
public class Area extends PilotixElement {

    protected int nbShips;
    protected Ship[] ships;
    
    public Area(){
        ships = new Ship[16];
        nbShips = 0;
    }
    
   
    public void setFromBytes(byte[] bytes) {
        /*
         * | Octet 0 | Octet 1- 6 | Octet 7-12 |... | 4bit | 4bit | | | |
         * Flag4|nbShip| aShip | aShip |...
         */

        //Flag = (byte)((bytes[0] & 240) >> 4);
        nbShips = (byte) (bytes[0] & 15);

        int index = 1;
        byte[] tmpByte = new byte[6];
        //System.out.println("nbShip :"+nbShips);
        Ship tmpClientShip = new Ship();
        for (int i = 0; i < nbShips; i++) {
            tmpByte[0] = bytes[index];
            tmpByte[1] = bytes[index + 1];
            tmpByte[2] = bytes[index + 2];
            tmpByte[3] = bytes[index + 3];
            tmpByte[4] = bytes[index + 4];
            tmpByte[5] = bytes[index + 5];
            
            tmpClientShip.setFromBytes(tmpByte);
            //setShip(tmpClientShip);
            if (tmpClientShip.getStates() == Ship.REMOVE) {            
                ships[tmpClientShip.getId()] = null;
            } else {
                if (ships[tmpClientShip.getId()] == null) {
                    ships[tmpClientShip.getId()] = new Ship();
                }
                ships[tmpClientShip.getId()].set((Ship) tmpClientShip);
                //System.out.println("Ship id maj :" +aShip.getId());
            }
            index = index + 6;
        }
    }
    

    
    //| Octet 0     | Octet 1-6 | Octet 7-12|... 
    //| 4bit | 4bit |  6 Octet  |  6  Octet |... 
    //|Flag 4|nbship|   Ship 0  |   Ship 1  |...
    public byte[] getAsBytes() {
                        
        byte[] tmp;
        byteCoded[0] = 0;
        byteCoded[0] = (byte) (MessageHandler.FRAMEINFO << 4);
        byteCoded[0] |= (byte) nbShips;
        bytesLength = 1;
       
        for (int i = 0; i < nbShips; i++) {            
            tmp = ships[i].getAsBytes();
            for (int j = 0; j < Ship.bytesLength; j++) {
                byteCoded[bytesLength + j] = tmp[j];
            }
            bytesLength += Ship.bytesLength;
        }
        return byteCoded;
    }
    
}
