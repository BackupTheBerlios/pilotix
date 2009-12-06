package org.pilotix.common;

/**
 * Pour être transmis par le réseau, un Object doit être converti en une chaîne
 * de bits, et inversement.
 * 
 * Ces méthodes sont utilisées par MessageHandler
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
	 * Met à jour l'objet à partir d'une chaîne d'octets recupéré via un
	 * messageHandler
	 * 
	 */
	public abstract void read(MessageHandler messageHandler) throws Exception;

	// public abstract void setFromBytes(byte[] bytes);

	/**
	 * envoie des octets représentant l'objet par le bié d'un messageHandler
	 */
	public abstract void write(MessageHandler messageHandler) throws Exception;
	// public abstract byte[] getAsBytes();

}