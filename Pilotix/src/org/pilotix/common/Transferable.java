package org.pilotix.common;

/**
 * Pour être transmis par le réseau, un Object
 * doit être converti en une chaîne de bits,
 * et inversement.
 *
 * Ces méthodes sont utilisées par MessageHandler
 *
 * @see MessageHandler
 */
public interface Transferable {

    //public byte[] byteCoded = null;
    public static final byte AREA = 1;
    public static final byte INFO = 2;

    // message type for client to server
    public static final byte COMMAND = 8;
    public static final byte SESSION = 9;

    /**
     * Met à jour l'objet à partir d'une chaîne d'octets
     * @param bytes
     *         le tableau contenant l'objet
     */
    public abstract void setFromBytes(byte[] bytes);

    /**
     * Retourne une chaîne d'octets représentant l'objet
     */
    public abstract byte[] getAsBytes();

    /**
     * Retourne la taille en octets de la chaîne de bits
     * renvoyée par <code>getAsBytes</code>
     *
     */
    public abstract int getLengthInByte();
}
