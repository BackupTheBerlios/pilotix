package org.pilotix.common;

/**
 * Pour �tre transmis par le r�seau, un Object
 * doit �tre converti en une cha�ne de bits,
 * et inversement.
 *
 * Ces m�thodes sont utilis�es par MessageHandler
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
     * Met � jour l'objet � partir d'une cha�ne d'octets
     * @param bytes
     *         le tableau contenant l'objet
     */
    public abstract void setFromBytes(byte[] bytes);

    /**
     * Retourne une cha�ne d'octets repr�sentant l'objet
     */
    public abstract byte[] getAsBytes();

    /**
     * Retourne la taille en octets de la cha�ne de bits
     * renvoy�e par <code>getAsBytes</code>
     *
     */
    public abstract int getLengthInByte();
}
