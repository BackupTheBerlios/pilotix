package org.pilotix.common;

/**
 * Classe servant à véhiculer diverses informations entre le client et le
 * serveur.
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
	 * |  Octet 0  |  Octet  1  |  Octet  2  |  Octet  3  |  Octet  4-4+n |
	 * |  1 Octet  |  1  Octet  |  1 Octet   |  1 Octet   |     1 Octet   |   
	 * | Flag INFO |FlagShipName|   ShipId   |StringLength|    Char 0-n   |
	 * </pre>
	 */
	public static final byte SHIP_NAME = 4;
	/**
	 * <pre>
	 * |  Octet 0  |    Octet 1   |
	 * |  1 Octet  |    1 Octet   |   
	 * | Flag INFO |flag Deconnect|
	 * </pre>
	 */
	public static final byte DECONNECT = 3;

	private int type = 0;
	private int ownShipId = 0;
	private String areaId;
	private String shipName;
	private int shipId;

	public Information() {

	}

	public void setCode(int aCode) {
		type = aCode;
	}

	public void setOwnShipId(int anId) {
		ownShipId = anId;
		type = OWN_SHIP_ID;
	}

	public void setAreaId(String anAreaId) {
		areaId = anAreaId;
		type = AREA_ID;
	}

	public void setShipName(int aShipId, String aShipName) {
		shipName = aShipName;
		shipId = aShipId;
		type = SHIP_NAME;
	}

	public void setDeconnected() {
		type = DECONNECT;
	}

	public void read(MessageHandler mh) throws Exception {
		type = mh.receiveOneByte();
		if (type == OWN_SHIP_ID) {
			ownShipId = mh.receiveOneByte();
		} else if (type == SHIP_NAME) {
			shipId = mh.receiveOneByte();
			int length = mh.receiveOneByte();
			shipName = new String(mh.receiveNBytes(length));
		}
	}

	public void write(MessageHandler mh) throws Exception {
		byte[] bytes;
		if (type == OWN_SHIP_ID) {
			bytes = new byte[3];
			bytes[0] = Transferable.INFO;
			bytes[1] = OWN_SHIP_ID;
			bytes[2] = (byte) ownShipId;
			mh.sendBytes(bytes);
		} else if (type == DECONNECT) {
			bytes = new byte[2];
			bytes[0] = Transferable.INFO;
			bytes[1] = DECONNECT;
			mh.sendBytes(bytes);
		} else if (type == SHIP_NAME) {
			bytes = new byte[4 + shipName.length()];
			bytes[0] = Transferable.INFO;
			bytes[1] = SHIP_NAME;
			bytes[2] = (byte) shipId;
			bytes[3] = (byte) shipName.length();
			for (int i = 0, n = shipName.length(); i < n; i++) {
				bytes[4 + i] = (byte) shipName.charAt(i);
			}
			mh.sendBytes(bytes);
		}
	}

	/**
	 * @return Returns the shipId.
	 */
	public int getShipId() {
		return shipId;
	}

	/**
	 * @param shipId
	 *            The shipId to set.
	 */
	public void setShipId(int shipId) {
		this.shipId = shipId;
	}

	/**
	 * @return Returns the shipName.
	 */
	public String getShipName() {
		return shipName;
	}

	/**
	 * @param shipName
	 *            The shipName to set.
	 */
	public void setShipName(String shipName) {
		this.shipName = shipName;
	}

	/**
	 * @return Returns the type.
	 */
	public int getType() {
		return type;
	}

	/**
	 * @param type
	 *            The type to set.
	 */
	public void setType(int type) {
		this.type = type;
	}

	/**
	 * @return Returns the areaId.
	 */
	public String getAreaId() {
		return areaId;
	}

	/**
	 * @return Returns the ownShipId.
	 */
	public int getOwnShipId() {
		return ownShipId;
	}
}