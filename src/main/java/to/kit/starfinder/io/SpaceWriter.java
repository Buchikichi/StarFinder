package to.kit.starfinder.io;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import net.arnx.jsonic.JSON;
import to.kit.starfinder.Constellation;
import to.kit.starfinder.Constellation.ConstLine;
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

	/**
	 * @param list
	 * @throws IOException
	 */
	public void saveConstellation(List<Constellation> list) throws IOException {
		List<ConstellationInfo> infoList = new ArrayList<>();
		LineInfo lineInfo = null;

		for (Constellation cons : list) {
			ConstellationInfo info = new ConstellationInfo();

			info.name = cons.getName();
			info.pos = cons.getPos().getId();
			for (ConstLine line : cons) {
				if (line.isBegin()) {
					lineInfo = new LineInfo();
					lineInfo.from = line.getStar().getId();
					info.list.add(lineInfo);
				} else if (lineInfo != null) {
					lineInfo.to = line.getStar().getId();
				}
			}
			if (info.list.isEmpty()) {
				continue;
			}
			infoList.add(info);
		}

		try (FileWriter out = new FileWriter("constellation.json")) {
			out.write(JSON.encode(infoList, false));
		}
	}

	public class ConstellationInfo {
		public String name;
		public int pos;
		public List<LineInfo> list = new ArrayList<>();
	}

	public class LineInfo {
		public int from;
		public int to;
	}
}
