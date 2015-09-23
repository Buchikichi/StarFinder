package to.kit.starfinder;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import to.kit.starfinder.Constellation.ConstLine;

/**
 * 星座情報.
 * @author Hidetaka Sasai
 */
public final class Constellation implements Iterable<ConstLine> {
	/** 星座の名前. */
	private final String name;
	/** Latitude(緯度). */
	private int latitude;
	/** Longitude(経度). */
	private int longitude;
	/** 星座を結ぶ線情報. */
	private List<ConstLine> lineList = new ArrayList<>();

	/**
	 * インスタンスを生成.
	 * @param constellationName 星座の名前
	 */
	public Constellation(String constellationName) {
		this.name = constellationName;
	}

	/**
	 * 星座を結ぶ線情報を追加.
	 * @param begin 線の始まりかどうか
	 * @param star 結ぶ星
	 */
	public void add(boolean begin, Star star) {
		this.lineList.add(new ConstLine(begin, star));
	}

	// getter/setter
	/**
	 * @return 星座の名前
	 */
	public String getName() {
		return this.name;
	}
	/**
	 * Latitude(緯度)を取得.
	 * @return 緯度
	 */
	public int getLatitude() {
		return this.latitude;
	}
	/**
	 * Latitude(緯度)を設定.
	 * @param value 緯度
	 */
	public void setLatitude(final int value) {
		this.latitude = value;
	}
	/**
	 * Longitude(経度)を取得.
	 * @return the longitude
	 */
	public int getLongitude() {
		return this.longitude;
	}
	/**
	 * Longitude(経度)を設定.
	 * @param value 経度
	 */
	public void setLongitude(final int value) {
		this.longitude = value;
	}

	@Override
	public Iterator<ConstLine> iterator() {
		return this.lineList.iterator();
	}

	/**
	 * 線情報.
	 * @author Hidetaka Sasai
	 */
	public class ConstLine {
		/** 線の始まりかどうか. */
		private final boolean begin;
		/** 結ぶ星. */
		private final Star star;

		/**
		 * インスタンスを生成.
		 * @param begin 線の始まりかどうか
		 * @param star 結ぶ星
		 */
		ConstLine(boolean begin, Star star) {
			this.begin = begin;
			this.star = star;
		}

		/**
		 * @return the begin
		 */
		public boolean isBegin() {
			return this.begin;
		}

		/**
		 * @return the star
		 */
		public Star getStar() {
			return this.star;
		}
	}
}
