package org.pilotix.common;

/**
 * Classe servant � v�hiculer diverses informations
 * entre le client et le serveur.
 */
public class Information implements Transferable {

    /**
     * <pre>
     * |   Octet 0   | Octet 1 |
     * | 4bit | 4bit | 1 Octet |
     * | Flag |   1  |OwnShipId|
     * </pre>
     */
    public static final byte OWN_SHIP_ID = 1;

    /**
     * <pre>
     * |   Octet 0   | Octet 1 |  Octet  2  |  Octet  3  |  Octet  4  |...
     * | 4bit | 4bit | 1 Octet |  1 Octet   |  1 Octet   |  1 Octet   |
     * | Flag |   2  | Area Id |StringLength|   Char 0   |   Char 1   |...
     * </pre>
     */
    public static final byte AREA_ID = 2;

    /**
     * <pre>
     * |   Octet 0   |
     * | 4bit | 4bit |
     * | Flag |   3  |
     * </pre>
     */
    public static final byte DECONNECT = 3;

    private int lengthInByte = 0;
    private byte[] byteCoded;

    public int code = 0;
    public int ownShipId = 0;
    public String areaId;

    public Information() {

    }
    

    public void setCode(int aCode){
        code = aCode;
    }
    public void setOwnShipId(int anId){
        ownShipId = anId;
        code = OWN_SHIP_ID;
    }
    public void setAreaId(String anAreaId){
        areaId = anAreaId;
        code = AREA_ID;
    }
    public void setDeconnected(){
        code = DECONNECT;
    }
    

    public void setFromBytes(byte[] bytes) {
        code = (byte) (bytes[0] & 15);
        switch (code) {
        case OWN_SHIP_ID:
            ownShipId = bytes[1];
            
            break;
        case AREA_ID:
            areaId = new String(bytes, 2, bytes.length - 2);
            break;
        case DECONNECT:
            break;
        }
    }

    public byte[] getAsBytes() {
        byteCoded = new byte[getLengthInByte()];
        byteCoded[0] = 0;
        byteCoded[0] = (byte) (Transferable.INFO << 4);
        byteCoded[0] |= (byte) code;
        switch (code) {
        case OWN_SHIP_ID:
            byteCoded[1] = (byte) ownShipId;
            break;
        case AREA_ID:
            byteCoded[1] = (byte) areaId.length();
            for (int i = 0; i < areaId.length(); i++) {
                byteCoded[2 + i] = (byte) areaId.charAt(i);
            }
            break;
        case DECONNECT:
            break;
        }
        //System.out.println("INFO OWNSHIP!!!"+ownShipId);
        return byteCoded;
    }

    public int getLengthInByte() {
        switch (code) {
        case OWN_SHIP_ID:
            lengthInByte = 2;
            break;
        case AREA_ID:
            lengthInByte = areaId.length() + 2;
            break;
        case DECONNECT:
            lengthInByte = 1;
            break;
        }
        return lengthInByte;
    }
}
