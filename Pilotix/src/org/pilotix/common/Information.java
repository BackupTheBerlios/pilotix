package org.pilotix.common;

/**
 * Classe servant à véhiculer diverses informations
 * entre le client et le serveur.
 */
public class Information implements Transferable {

    /**
     * <pre>
     * | Octet 0 |    Octet 1   | Octet 2 |
     * | 1 Octet |    1 Octet   | 1 Octet |
     * |Flag INFO|Flag OwnShipId|   id    |
     * </pre>
     */
    public static final byte OWN_SHIP_ID = 1;
    /**
     * <pre>
     * |  Octet 0  |  Octet 1  |  Octet  2  |  Octet  3-3+n |
     * |  1 Octet  |  1 Octet  |  1 Octet   |     1 Octet   |   
     * | Flag INFO |Flag AreaID|StringLength|    Char 0-n   |
     * </pre>
     */
    public static final byte AREA_ID = 2;
    /**
     * <pre>
     * |  Octet 0  |    Octet 1   |
     * |  1 Octet  |    1 Octet   |   
     * | Flag INFO |flag Deconnect|
     * </pre>
     */
    public static final byte DECONNECT = 3;
    

    public int code = 0;
    public int ownShipId = 0;
    public String areaId;

    public Information() {

    }

    public void setCode(int aCode) {
        code = aCode;
    }

    public void setOwnShipId(int anId) {
        ownShipId = anId;
        code = OWN_SHIP_ID;
    }

    public void setAreaId(String anAreaId) {
        areaId = anAreaId;
        code = AREA_ID;
    }

    public void setDeconnected() {
        code = DECONNECT;
    }

    public void read(MessageHandler mh)  throws Exception {        
        code = mh.receiveOneByte();
        if (code == OWN_SHIP_ID) {
            ownShipId = mh.receiveOneByte();
        } 
    }

    public void write(MessageHandler mh) throws Exception{
        byte[] bytes;
        if (code == OWN_SHIP_ID) {
            bytes = new byte[3];
            bytes[0] = Transferable.INFO;
            bytes[1] = OWN_SHIP_ID;
            bytes[2] = (byte)ownShipId;            
            mh.sendBytes(bytes);
        } else if(code == DECONNECT){
            bytes = new byte[2];
            bytes[0] = Transferable.INFO;
            bytes[1] = DECONNECT;
            mh.sendBytes(bytes);
        }
    }
    
}