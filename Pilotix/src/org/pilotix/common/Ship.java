/*
 Pilotix : a multiplayer piloting game.
 Copyright (C) 2003 Pilotix.Org

 This program is free software; you can redistribute it and/or
 modify it under the terms of the GNU General Public License
 as published by the Free Software Foundation; either version 2
 of the License, or (at your option) any later version.

 This program is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 GNU General Public License for more details.

 You should have received a copy of the GNU General Public License
 along with this program; if not, write to the Free Software
 Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA 02111-1307, USA.
 */

package org.pilotix.common;

/**
 * Contient les informations relatives à un Vaisseau. Ship est spécialisée en
 * ServerShip pour le serveur et en ClientShip pour le client.
 * 
 * Contient également les méthodes d'encapsulation pour les transfert sur le
 * réseau
 * 
 * 
 * <pre>
 * | Octet 0 |  Octet1 | Octet2  |  Octet3-4 | Octet5-6 |  Octet7 | 
 * | 1 Octet | 1 Octet | 1 Octet |  2Octets  | 2Octets  | 1 Octet |
 * |flag SHIP|    id   |states   |     X     |    Y     |Direction|
 * </pre>
 * 
 * 
 */
public class Ship extends PilotixElement implements Transferable {

	public final static int ADD = 0;
	public final static int REMOVE = 1;
	public final static int NULL = 2;
	public final static int HIT = 3;
	public final static int ACCELERATING = 4;

	protected Vector speed;
	protected Angle direction;
	protected int radius = 400;

	public Ship() {
		super();
		id = -1;
		position = new Vector(0, 0);
		direction = new Angle(0);
		states = 0;
	}

	public Ship(Ship aShip) {
		states = aShip.states;
		id = aShip.id;
		position = new Vector(aShip.getPosition());
		direction = new Angle(aShip.getDirection());
	}

	/**
	 * Sets the caracteristique(direction, position) of the Ship.
	 * 
	 * @param aShip
	 *            where only pos and dir while be copied.
	 */

	public void set(Ship aShip) {
		states = aShip.states;
		id = aShip.id;
		position.set(aShip.getPosition());
		direction.set(aShip.getDirection());
	}

	public void setId(int anId) {
		id = anId;
	}

	public int getId() {
		return id;
	}

	/*
	 * public void setDirection(Angle aDirection) { direction = aDirection; }
	 */

	public int getRadius() {
		return radius;
	}

	/**
	 * Retrieves the direction of the Ship.
	 * 
	 * @return the current direction of the Ship
	 */
	public Angle getDirection() {
		return direction;
	}

	public void read(MessageHandler mh) {
		// Be careful flag SHIP is handled by en other process.
		// all indexes are shifted by one
		byte[] bytes = mh.receiveNBytes(8);
		id = bytes[0];
		states = bytes[1];

		position.x = 0;
		position.y = 0;
		int deg2 = 0;
		int inc = 1;
		for (int i = 0; i < 8; i++) {
			position.x += ((byte) (bytes[3] >> i) & 0x01) * inc;
			position.y += ((byte) (bytes[5] >> i) & 0x01) * inc;
			deg2 += ((byte) (bytes[7] >> i) & 0x01) * inc;
			inc = inc << 1;
		}
		inc = 256;
		for (int i = 0; i < 8; i++) {
			position.x += ((byte) (bytes[2] >> i) & 0x01) * inc;
			position.y += ((byte) (bytes[4] >> i) & 0x01) * inc;
			deg2 += ((byte) (bytes[6] >> i) & 0x01) * inc;
			inc = inc << 1;
		}

		/*
		 * inc = 1; int deg = 0; for (int i = 0; i < 8; i++) { deg += ((byte)
		 * (bytes[6] >> i) & 0x01) * inc; inc = inc << 1; }
		 */
		direction.set(deg2);
	}

	public void write(MessageHandler mh) throws Exception {
		byte[] bytes = new byte[9];
		bytes[0] = Transferable.SHIP;
		bytes[1] = (byte) id;
		bytes[2] = (byte) states;
		bytes[3] = (byte) (position.x / 256);
		bytes[4] = (byte) position.x;
		bytes[5] = (byte) (position.y / 256);
		bytes[6] = (byte) position.y;
		bytes[7] = (byte) (direction.intValue() / 256);
		bytes[8] = (byte) (direction.intValue());

		mh.sendBytes(bytes);
	}
}