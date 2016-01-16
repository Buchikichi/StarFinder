package to.kit.starfinder.io;

import java.io.FileWriter;
import java.io.IOException;

import net.arnx.jsonic.JSON;
import to.kit.starfinder.Star;

/**
 * Hipparcos Main Catalog を、JSON形式で保存.
 * @author Hidetaka Sasai
 */
public final class SpaceWriter {
	private static final String FILE_NAME = "hipparcos.json";

	/**
	 * 保存.
	 * @param list 星リスト
	 * @throws IOException 入出力例外
	 */
	public void save(Star[] list) throws IOException {
		try (FileWriter out = new FileWriter(FILE_NAME)) {
			out.write(JSON.encode(list, false));
		}
	}
}
