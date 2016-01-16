package to.kit.starfinder.util;

import java.awt.Color;
import java.math.BigDecimal;

import org.apache.commons.lang3.math.NumberUtils;

/**
 * 星に関するユーティリティクラス.
 * @author Hidetaka Sasai
 */
public final class AstroUtils {
	private static final BigDecimal B240 = new BigDecimal(240);
	private static final BigDecimal B3600 = new BigDecimal(3600);
	private static final int MAX_DEG = 360;

	/**
	 * Convert RA to RA degree.
	 * @param ra
	 * @return degree
	 */
	public static BigDecimal toRaDeg(final String ra) {
		String[] element = ra.split("[\\s]");
		BigDecimal hh = new BigDecimal(NumberUtils.toInt(element[0]) * 3600);
		BigDecimal mm = new BigDecimal(NumberUtils.toInt(element[1]) * 60);
		BigDecimal val = new BigDecimal(element[2]).add(hh).add(mm);

		return val.divide(B240, 8, BigDecimal.ROUND_HALF_UP);
	}

	/**
	 * Convert DEC to DEC degree.
	 * @param dec
	 * @return degree
	 */
	public static BigDecimal toDecDeg(final String dec) {
		String[] element = dec.split("[\\s]");
		String hh = element[0];
		BigDecimal mm = new BigDecimal(NumberUtils.toInt(element[1]) * 60);
		BigDecimal val = new BigDecimal(element[2]).add(mm);

		if (hh.startsWith("-")) {
			val = val.negate();
		}
		return val.divide(B3600, 8, BigDecimal.ROUND_HALF_UP).add(new BigDecimal(hh));
	}

	/**
	 * Get a color.
	 * @param spect
	 * @param vmag
	 * @return Color
	 */
	public static Color calcColor(final String spect, int vmag) {
		int rr;
		int gg;
		int bb;
		char type = 0 < spect.length() ? spect.charAt(0) : 0;

		switch (type) {
		case 'O':
			rr = 34;
			gg = 102;
			bb = 255;
			break;

		case 'B':
			rr = 102;
			gg = 255;
			bb = 255;
			break;

		case 'A':
			rr = 255;
			gg = 255;
			bb = 255;
			break;

		case 'F':
			rr = 255;
			gg = 255;
			bb = 102;
			break;

		case 'G':
			rr = 255;
			gg = 255;
			bb = 34;
			break;

		case 'K':
			rr = 255;
			gg = 136;
			bb = 68;
			break;

		case 'M':
			rr = 255;
			gg = 0;
			bb = 0;
			break;

		case 67: // 'C'
		case 68: // 'D'
		case 69: // 'E'
		case 72: // 'H'
		case 73: // 'I'
		case 74: // 'J'
		case 76: // 'L'
		case 78: // 'N'
		default:
			rr = 34;
			gg = 34;
			bb = 204;
			break;
		}
		short brightness;
		if (vmag < 150)
			brightness = 100;
		else if (vmag < 250)
			brightness = 90;
		else if (vmag < 350)
			brightness = 85;
		else if (vmag < 450)
			brightness = 80;
		else if (vmag < 550)
			brightness = 70;
		else if (vmag < 650)
			brightness = 60;
		else
			brightness = 50;
		rr = (rr * brightness) / 100;
		gg = (gg * brightness) / 100;
		bb = (bb * brightness) / 100;
		return new Color(rr, gg, bb);
	}

	/**
	 * 角度を制限.
	 * @param degree 角度
	 * @return 調整後の角度
	 */
	public static double trimDegree(final double degree) {
		double deg = degree % MAX_DEG;

		while (deg < 0L) {
			deg += MAX_DEG;
		}
		return deg;
	}

	private AstroUtils() {
		// nop
	}

	/**
	 * main for debug.
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {
		System.out.println(toRaDeg("02 31 47.0753"));
		System.out.println(toDecDeg("+89 15 50.897"));

		System.out.println(toRaDeg("06 22 41.9874"));
		System.out.println(toDecDeg("-17 57 21.300"));
	}
}
