package to.kit.starfinder;

import java.awt.Color;

/**
 * The Star.
 * @author Hidetaka Sasai
 */
public final class Star {
	/** 赤経(←→). */
	private final double raDeg;
	/** 赤緯(↑↓). */
	private final double decDeg;
	/** 等級. */
	private final int v;
	/** 色. */
	private final Color color;

	/**
	 * インスタンスを生成.
	 * @param ra 赤経
	 * @param dec 赤緯
	 * @param v 等級
	 * @param color 色
	 */
	public Star(double ra, double dec, int v, Color color) {
		this.decDeg = dec;
		this.raDeg = ra;
		this.v = v;
		this.color = color;
	}

	/**
	 * @return the longitude
	 */
	public double getRaDeg() {
		return this.raDeg;
	}
	/**
	 * @return the latitude
	 */
	public double getDecDeg() {
		return this.decDeg;
	}
	/**
	 * @return the v
	 */
	public int getV() {
		return this.v;
	}
	/**
	 * @return the color
	 */
	public Color getColor() {
		return this.color;
	}
}
