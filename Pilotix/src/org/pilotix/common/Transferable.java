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

    public static final byte AREA = 1;
    public static final byte SHIP = 3;
    public static final byte BALL = 4;
    public static final byte INFO = 2;   
    public static final byte COMMAND = 8;   

    /**
     * Met � jour l'objet � partir d'une cha�ne d'octets recup�r� via un messageHandler
     * 
     */
    public abstract void read(MessageHandler messageHandler) throws Exception;

    //public abstract void setFromBytes(byte[] bytes);

    /**
     * envoie des octets repr�sentant l'objet par le bi� d'un messageHandler
     */
    public abstract void write(MessageHandler messageHandler) throws Exception;
    //public abstract byte[] getAsBytes();

}