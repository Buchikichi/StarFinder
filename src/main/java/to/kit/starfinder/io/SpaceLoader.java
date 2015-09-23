package to.kit.starfinder.io;

import java.awt.Color;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.math.NumberUtils;

import to.kit.starfinder.Constellation;
import to.kit.starfinder.MyMath;
import to.kit.starfinder.Space;
import to.kit.starfinder.Star;

/**
 * 星情報をロードするためのクラス.
 * @author Hidetaka Sasai
 */
public final class SpaceLoader {
	/** BSC5. */
	private static final String BSC5_FILE = "/bsc5.bin";
	/** A file of constellation. */
	private static final String CONSTELLATION_FILE = "/constellation.txt";
	/** stars. */
	private Star[] stars;
	/** constellation list. */
	private List<Constellation> constellationList = new ArrayList<>();

	/**
	 * @param btSt
	 * @param vv
	 * @return
	 */
	private static Color calcColor(byte btSt[], long vv) {
		int rr;
		int gg;
		int bb;
		switch (btSt[0]) {
			case 79 : // 'O'
				rr = 34;
				gg = 102;
				bb = 255;
				break;

			case 66 : // 'B'
				rr = 102;
				gg = 255;
				bb = 255;
				break;

			case 65 : // 'A'
				rr = 255;
				gg = 255;
				bb = 255;
				break;

			case 70 : // 'F'
				rr = 255;
				gg = 255;
				bb = 102;
				break;

			case 71 : // 'G'
				rr = 255;
				gg = 255;
				bb = 34;
				break;

			case 75 : // 'K'
				rr = 255;
				gg = 136;
				bb = 68;
				break;

			case 77 : // 'M'
				rr = 255;
				gg = 0;
				bb = 0;
				break;

			case 67 : // 'C'
			case 68 : // 'D'
			case 69 : // 'E'
			case 72 : // 'H'
			case 73 : // 'I'
			case 74 : // 'J'
			case 76 : // 'L'
			case 78 : // 'N'
			default :
				rr = 34;
				gg = 34;
				bb = 204;
				break;
		}
		short brightness;
		if (vv < 150L)
			brightness = 100;
		else if (vv < 250L)
			brightness = 90;
		else if (vv < 350L)
			brightness = 85;
		else if (vv < 450L)
			brightness = 80;
		else if (vv < 550L)
			brightness = 70;
		else if (vv < 650L)
			brightness = 60;
		else
			brightness = 50;
		rr = (rr * brightness) / 100;
		gg = (gg * brightness) / 100;
		bb = (bb * brightness) / 100;
		return new Color(rr, gg, bb);
	}


	/**
	 * バイトオーダーを変換.
	 * @param val 値
	 * @return 変換後の値
	 */
	private short swapShort(int val) {
		return (short) ((val & 0xff) << 8 | (val >> 8 & 0xff));
	}

	/**
	 * 星情報の読み込み.
	 * @return 星情報
	 * @throws IOException 入出力エラー
	 */
	private Star[] loadStars() throws IOException {
		List<Star> list = new ArrayList<>();
		byte[] btSt = new byte[2];
		try (InputStream in = Star.class.getResourceAsStream(BSC5_FILE);
				DataInputStream data = new DataInputStream(in)) {
			while (0 < data.available()) {
				long longitude = swapShort(data.readUnsignedShort());
				long latitude = swapShort(data.readUnsignedShort());
				long v = swapShort(data.readUnsignedShort());
				data.read(btSt);
				list.add(new Star(latitude, longitude, v, calcColor(btSt, v)));
				//stars[mStarNum] = star;
			}
		}
		return list.toArray(new Star[list.size()]);
	}

	private List<Constellation> loadConstellation() throws IOException {
		List<Constellation> list = new ArrayList<>();
		Constellation cons = null;
		short phase = 0;

		try (InputStream stream = Space.class.getResourceAsStream(CONSTELLATION_FILE);
				BufferedReader reader = new BufferedReader(new InputStreamReader(stream))) {
			String line;

			while ((line = reader.readLine()) != null) {
				if (line.startsWith("/")) {
					continue;
				}
				if (line.startsWith("#")) {
					cons = new Constellation(line.substring(1));
					list.add(cons);
					phase = 0;
					continue;
				}
				if (cons == null) {
					continue;
				}
				switch (phase) {
				case 0: // '\0'
					String[] elements = line.split(",");
					int hh = NumberUtils.toInt(elements[0]);
					int mm = NumberUtils.toInt(elements[1]);
					cons.setLongitude((int) MyMath.deg((hh * 60L + mm) / 4L));
					phase++;
					break;

				case 1: // '\001'
					cons.setLatitude((int) MyMath.deg(NumberUtils.toInt(line)));
					phase++;
					break;

				default:
					line = line.replaceAll("[^-+0-9].*", "");
					int val = NumberUtils.toInt(line);
					Star star = this.stars[Math.abs(val) - 1];
					cons.add(val < 0, star);
					break;
				}
			}
		}
		return list;
	}

	/**
	 * 星情報を読み込む.
	 * @throws IOException 入出力例外
	 */
	public void load() throws IOException {
		this.stars = loadStars();
		this.constellationList = loadConstellation();
	}

	/**
	 * @return the stars
	 */
	public Star[] getStars() {
		return this.stars;
	}

	/**
	 * @return the constellationList
	 */
	public List<Constellation> getConstellationList() {
		return this.constellationList;
	}
}
