package to.kit.starfinder.io;

import java.awt.Color;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.math.NumberUtils;

import to.kit.starfinder.Constellation;
import to.kit.starfinder.Space;
import to.kit.starfinder.Star;
import to.kit.starfinder.util.AstroUtils;

/**
 * 星情報をロードするためのクラス.
 * @author Hidetaka Sasai
 */
public final class SpaceLoader {
	/** Hipparcos Catalog. */
	private static final String HIP_FILE = "/hipparcos.txt";
	/** A file of constellation. */
	private static final String HIP_CONSTELLATION_FILE = "/hip_constellation_line.csv";
	/** A file of constellation. */
	private static final String CONSTELLATION_FILE = "/constellation.txt";
	/** constellation list. */
	private final List<Constellation> constellationList = new ArrayList<>();
	/** stars map. */
	private final Map<String, Star> hipMap = new HashMap<>();
	/** stars. */
	private Star[] stars;

	/**
	 * 星情報の読み込み.
	 * @return 星情報
	 * @throws IOException 入出力エラー
	 */
	private Star[] loadStars() throws IOException {
		List<Star> list = new ArrayList<>();

		try (InputStream in = Star.class.getResourceAsStream(HIP_FILE);
				BufferedReader r = new BufferedReader(new InputStreamReader(in))) {
			for (;;) {
				String line = r.readLine();
				if (line == null) {
					break;
				}
				if (!line.startsWith("|") || line.startsWith("|ra")) {
					continue;
				}
				String[] elements = line.split("[|]");
				String vmag = elements[5].trim();
				if (vmag.isEmpty()) {
					continue;
				}
				String hip = elements[4].trim();
				String spect = elements[3].trim();
				BigDecimal ra = AstroUtils.toRaDeg(elements[1].trim());
				BigDecimal dec = AstroUtils.toDecDeg(elements[2].trim());
				int v = (int) (NumberUtils.toDouble(vmag) * 100);
				Color c = AstroUtils.calcColor(spect, v);
				Star star = new Star(ra.doubleValue(), dec.doubleValue(), v, c);

				list.add(star);
				this.hipMap.put(hip, star);
			}
		}
		return list.toArray(new Star[list.size()]);
	}

	private void loadConstellationHip(List<Constellation> list) throws IOException {
		Constellation cons = null;

		try (InputStream stream = Space.class.getResourceAsStream(HIP_CONSTELLATION_FILE);
				BufferedReader reader = new BufferedReader(new InputStreamReader(stream))) {
			String line;

			while ((line = reader.readLine()) != null) {
				if (line.startsWith("#")) {
					continue;
				}
				String[] elements = line.split(",");
				String name = elements[0];
				Star s1 = this.hipMap.get(elements[1]);
				Star s2 = this.hipMap.get(elements[2]);

				if (cons == null || !name.equals(cons.getName())) {
					cons = new Constellation(name);
					cons.setLongitude((int) s1.getRaDeg());
					cons.setLatitude((int) s1.getDecDeg());
					list.add(cons);
				}
				cons.add(true, s1);
				cons.add(false, s2);
			}
		}
	}

	private void loadConstellation(List<Constellation> list) throws IOException {
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
					cons.setLongitude((int) ((hh * 60L + mm) / 4L));
					phase++;
					break;

				case 1: // '\001'
					cons.setLatitude(NumberUtils.toInt(line));
					phase++;
					break;

				default:
//					line = line.replaceAll("[^-+0-9].*", "");
//					int val = NumberUtils.toInt(line);
//					Star star = this.stars[Math.abs(val) - 1];
//					cons.add(val < 0, star);
					break;
				}
			}
		}
	}

	/**
	 * 星情報を読み込む.
	 * @throws IOException 入出力例外
	 */
	public void load() throws IOException {
		this.stars = loadStars();
		loadConstellation(this.constellationList);
		loadConstellationHip(this.constellationList);
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
