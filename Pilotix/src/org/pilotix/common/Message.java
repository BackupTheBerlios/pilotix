package org.pilotix.common;

/**
 * Pour etre transmis via le reseau, un object
 * doit etre convertis en un chaine de bit
 * et inversement.
 * 
 * ces methodes sont utilises par MessageHandler
 * 
 * @see MessageHandler
 *
 */
public interface Message {
        
    //public byte[] byteCoded = null;
    public static final byte AREA = 1;
    public static final byte INFO = 2;

    // message type for client to server
    public static final byte COMMAND = 8;
    public static final byte SESSION = 9;
    /**
     * met a jour l'objet a partir d'une chaine d'octet
     * @param bytes 
     */
    public abstract void setFromBytes(byte[] bytes);
    
    /**
     * retourne une chaine d'octet representant l'objet 
     */
    public abstract byte[] getAsBytes();    
    /**
     * retourne la taille en octet de la chaine de bit renvoye par
     * getAsBytes
     * 
     */
    public abstract int getLengthInByte();
}
