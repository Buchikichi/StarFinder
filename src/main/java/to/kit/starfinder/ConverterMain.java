package to.kit.starfinder;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.math.NumberUtils;

import net.arnx.jsonic.JSON;
import to.kit.starfinder.util.AstroUtils;

public class ConverterMain {
	private static final String PROPER_NAME = "/hip_proper_name.csv";
	private static final String POSITION_NAME = "/position_utf8.csv";

	private List<String> loadProperName() throws IOException {
		List<String> list;
		try (InputStream in = ConverterMain.class.getResourceAsStream(PROPER_NAME)) {
			list = IOUtils.readLines(in);
		}
		return list;
	}

	private List<String> loadPosition() throws IOException {
		List<String> list;
		try (InputStream in = ConverterMain.class.getResourceAsStream(POSITION_NAME)) {
			list = IOUtils.readLines(in);
		}
		return list;
	}

	private void execute() throws IOException {
		List<Object> list = new ArrayList<>();

		for (String row : loadProperName()) {
			String[] element = row.split(",");
			Proper rec = new Proper();

			rec.name = element[1];
			rec.star = NumberUtils.toInt(element[0]);

			list.add(rec);
		}
		for (String row : loadPosition()) {
			String[] element = row.split(",");
			String ra = element[4] + " " + element[5] + " 00";
			String dec = element[6] + " 00 00";
			Position rec = new Position();

			rec.name = element[2];
			rec.text = element[1] + "|" + element[2] + "|" + element[3];
			rec.longitude = AstroUtils.toRaRad(ra);
			rec.latitude = AstroUtils.toDecRad(dec);

			list.add(rec);
//			System.out.println(rec.longitude);
		}
		String json = JSON.encode(list);
		try (FileWriter out = new FileWriter(new File("names.json"))) {
			IOUtils.write(json, out);
		}
	}

	public static void main(String[] args) throws Exception {
		ConverterMain app = new ConverterMain();

		app.execute();
	}

	class Proper {
		public String name;
		public int star;
	}
	class Position {
		public String name;
		public String text;
		public Number longitude;
		public Number latitude;
	}
}
