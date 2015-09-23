package to.kit.starfinder;

/**
 * 各種計算.
 * @author Hidetaka Sasai
 */
public final class MyMath {
	private static final double DEG_PRECISION = 16D;
	private static final int MAX_DEG = (int) (360D * DEG_PRECISION);
	private static final int TRANS = 0x100; // 0x186a0
	private static final long tblSin[] = new long[360 * (int) DEG_PRECISION];
	private static final long tblCos[] = new long[360 * (int) DEG_PRECISION];

	private MyMath() {
		// nop
	}

	static {
		for (int ix = 0; ix < 360D * DEG_PRECISION; ix++) {
			tblSin[ix] = (long) (Math.sin((ix * Math.PI) / DEG_PRECISION / 180D) * TRANS);
			tblCos[ix] = (long) (Math.cos((ix * Math.PI) / DEG_PRECISION / 180D) * TRANS);
		}
	}

	/**
	 * 角度を制限.
	 * @param degree 角度
	 * @return 調整後の角度
	 */
	public static long trimDegree(final long degree) {
		long deg = degree;

		while (deg < 0L) {
			deg += MAX_DEG;
		}
		return deg % MAX_DEG;
	}

	/**
	 * SIN変換.
	 * @param degree 角度
	 * @param val 値
	 * @return sin
	 */
	public static int sin(final long degree, final long val) {
		long deg = trimDegree(degree);
		return (int) ((val * tblSin[(int) (deg % MAX_DEG)]) / TRANS);
	}

	/**
	 * COS変換.
	 * @param degree 角度
	 * @param val 値
	 * @return cos
	 */
	public static int cos(final long degree, final long val) {
		long deg = degree;
		while (deg < 0L) {
			deg += MAX_DEG;
		}
		return (int) ((val * tblCos[(int) (deg % MAX_DEG)]) / TRANS);
	}

	/**
	 * 角度を変換.
	 * @param deg 角度
	 * @return 角度
	 */
	public static long deg(long deg) {
		return (long) (deg * DEG_PRECISION);
	}
}
