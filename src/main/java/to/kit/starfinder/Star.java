package to.kit.starfinder;

import java.awt.Color;

/**
 * The Star.
 * @author Hidetaka Sasai
 */
public final class Star {
	/** 緯度(↑↓). */
	private final long latitude;
	/** 経度(←→). */
	private final long longitude;
	/** 等級. */
	private final long v;
	/** 色. */
	private final Color color;

	/**
	 * インスタンスを生成.
	 * @param latitude 緯度
	 * @param longigude 経度
	 * @param v 等級
	 * @param color 色
	 */
	public Star(long latitude, long longigude, long v, Color color) {
		this.latitude = latitude;
		this.longitude = longigude;
		this.v = v;
		this.color = color;
	}

	/**
	 * @return the latitude
	 */
	public long getLatitude() {
		return this.latitude;
	}
	/**
	 * @return the longitude
	 */
	public long getLongitude() {
		return this.longitude;
	}
	/**
	 * @return the v
	 */
	public long getV() {
		return this.v;
	}
	/**
	 * @return the color
	 */
	public Color getColor() {
		return this.color;
	}
}
