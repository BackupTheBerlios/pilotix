package org.pilotix.common;



public interface Message {
        
    //public byte[] byteCoded = null;
    public static final byte FRAMEINFO = 4;
    public static final byte OWNSHIPINFO = 9;

    // message type for client to server
    public static final byte COMMAND = 10;
    public static final byte SESSION = 11;
    
    public abstract void setFromBytes(byte[] bytes);

    public abstract byte[] getAsBytes();    

    public abstract int getLengthInByte();
}
