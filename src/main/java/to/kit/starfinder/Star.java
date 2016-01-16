package to.kit.starfinder;

import java.awt.Color;

import net.arnx.jsonic.JSONHint;

/**
 * The Star.
 * @author Hidetaka Sasai
 */
public final class Star {
	/** ID. */
	private final int id;
	/** 赤経(←→). */
	private final double ra;
	/** 赤緯(↑↓). */
	private final double dec;
	/** 等級. */
	private final int v;
	/** 色. */
	private final Color color;
	/** spect_type. */
	private final String s;

	/**
	 * インスタンスを生成.
	 * @param id ID
	 * @param ra 赤経
	 * @param dec 赤緯
	 * @param v 等級
	 * @param color 色
	 * @param spect spect_type
	 */
	public Star(int id, double ra, double dec, int v, Color color, String spect) {
		this.id = id;
		this.dec = dec;
		this.ra = ra;
		this.v = v;
		this.color = color;
		this.s = spect;
	}

	/**
	 * @return the id
	 */
	public int getId() {
		return this.id;
	}
	/**
	 * @return the longitude
	 */
	public double getRa() {
		return this.ra;
	}
	/**
	 * @return the latitude
	 */
	public double getDec() {
		return this.dec;
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
	@JSONHint(ignore=true)
	public Color getColor() {
		return this.color;
	}
	/**
	 * @return color
	 */
	public String getS() {
		return this.s;
	}
}
