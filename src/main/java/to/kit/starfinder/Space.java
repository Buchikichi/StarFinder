package to.kit.starfinder;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.List;

import to.kit.starfinder.Constellation.ConstLine;
import to.kit.starfinder.io.SpaceLoader;
import to.kit.starfinder.util.AstroUtils;

/**
 * Space.
 * @author Hidetaka Sasai
 */
public final class Space {
//	private static final int CSpaceVisibleClass = 800;
	/** 星情報. */
	private Star[] stars;
	/** 星座情報. */
	private List<Constellation> constList;
	private int center;
	/** 回転. */
	private double rotationH;
	private double latitude;

	/** 星座の線を表示. */
	private boolean showConstellation = true;
	/** 表示する星座のクラス. */
	private long visibleClass;
//	private long degreeY = 0L;
	private long degreeZ = 0L;
//	private long mVisibleSv;
//	private boolean isLightMode = false;

	/**
	 * 位置を変換.
	 * @param ioZ
	 * @param raDeg
	 * @param decDeg
	 * @return 表示位置
	 */
	private Point convPos(long[] ioZ, final double raDeg, final double decDeg) {
		Point pt = new Point(0, 0);
		long wx = pt.x;
		long wy = pt.y;
		long wz = ioZ[0];
		double raRad = (raDeg + this.rotationH) % 360 * Math.PI / 180.0;
		double decRad = decDeg * Math.PI / 180.0;
		double radX = (this.latitude * Math.PI) / 180.0;

		pt.y = (int) (Math.cos(decRad) * wy - Math.sin(decRad) * wz);
		ioZ[0] = (long) (Math.sin(decRad) * wy + Math.cos(decRad) * wz);
		wz = ioZ[0];
		pt.x = (int) (Math.cos(raRad) * wx - Math.sin(raRad) * wz);
		ioZ[0] = (long) (Math.sin(raRad) * wx + Math.cos(raRad) * wz);
		wy = pt.y;
		wz = ioZ[0];
		pt.y = (int) (Math.cos(radX) * wy - Math.sin(radX) * wz);
		ioZ[0] = (long) (Math.sin(radX) * wy + Math.cos(radX) * wz);
		if (ioZ[0] < 0L) {
			return pt;
		}
		wx = pt.x;
		wy = pt.y;
		pt.x = (int) (Math.cos(this.degreeZ) * wx - Math.sin(this.degreeZ) * wy);
		pt.y = (int) (Math.sin(this.degreeZ) * wx + Math.cos(this.degreeZ) * wy);
		return pt;
	}

	/**
	 * 星を描く.
	 * @param g Graphics
	 * @param star The Star
	 * @return
	 */
	private boolean drawStar(Graphics g, final Star star) {
		boolean result = false;
		long[] pz = { this.center };
		Point pt = convPos(pz, star.getRaDeg(), star.getDecDeg());

		if (0L < pz[0]) {
			g.setColor(star.getColor());
			g.drawLine(pt.x, pt.y, pt.x, pt.y);
			result = true;
		}
		return result;
	}

	private void drawConst(Graphics2D g) {
		Color constellationColor = new Color(32, 80, 128);

		for (Constellation constellation : this.constList) {
			Point prev = null;

			g.setColor(constellationColor);
			for (ConstLine line : constellation) {
				Star star = line.getStar();
				if (line.isBegin()) {
					// moveTo
					long[] pz = { this.center };
					prev = convPos(pz, star.getRaDeg(), star.getDecDeg());
				} else if (prev != null) {
					// lineTo
					long[] pz = { this.center };
					Point pt = convPos(pz, star.getRaDeg(), star.getDecDeg());
					if (0L < pz[0]) {
						g.drawLine(prev.x, prev.y, pt.x, pt.y);
					}
					prev = pt;
				}
			}
			long[] pz = { this.center };
			Point pt = convPos(pz, constellation.getLongitude(), constellation.getLatitude());
			if (0L < pz[0]) {
				g.setColor(Color.GRAY);
				g.drawString(constellation.getName(), pt.x, pt.y);
			}
		}
	}

	private void drawInfo(Graphics2D g, int count) {
		int y = -this.center + 16;

		g.setColor(Color.WHITE);
		g.drawString("l:" + -this.latitude, -this.center, y);
		y += 16;
		g.drawString("v:" + (this.visibleClass / 100.0), -this.center, y);
		y += 16;
		g.drawString(count + " stars", -this.center, y);
	}

	/**
	 * 描画イメージを取得.
	 * @param size 高さ/幅
	 * @return 描画イメージ
	 */
	public BufferedImage getImage(final int size) {
		BufferedImage image = new BufferedImage(size, size, BufferedImage.TYPE_INT_RGB);
		Graphics2D g = (Graphics2D) image.getGraphics();
		AffineTransform tx = g.getTransform();
		int count = 0;

		this.center = size / 2;
		tx.translate(this.center, this.center);
		g.setTransform(tx);
		g.setColor(Color.BLACK);
		if (this.showConstellation) {
			drawConst(g);
		}
		for (Star star : this.stars) {
			if (star.getV() < this.visibleClass) {
				drawStar(g, star);
				count++;
			}
		}
		drawInfo(g, count);
		return image;
	}

	/**
	 * 水平方向に回転する.
	 * @param dx 度合
	 */
	public void rotateH(int dx) {
		this.rotationH += dx / 8;
		this.rotationH = AstroUtils.trimDegree(this.rotationH);
	}

	/**
	 * 垂直方向に回転する.
	 * @param dy 度合
	 */
	public void rotateV(int dy) {
		this.latitude += dy;
		if (this.latitude < -90) {
			this.latitude = -90;
		} else if (90 < this.latitude) {
			this.latitude = 90;
		}
	}

	/**
	 * 星座の線の表示/非表示.
	 */
	public void toggleConstellation() {
		this.showConstellation = !this.showConstellation;
	}

	/**
	 * 表示する星を調整.
	 * @param d 度合
	 */
	public void addToVisibleClass(int d) {
		this.visibleClass += d;
		if (this.visibleClass < 100) {
			this.visibleClass = 100;
		}
	}

	/**
	 * インスタンスを生成.
	 */
	public Space() {
		SpaceLoader loader = new SpaceLoader();

		try {
			loader.load();
		} catch (IOException e) {
			// 基本的にありえない
			e.printStackTrace();
		}
		this.latitude = -34.0;
		this.stars = loader.getStars();
		this.constList = loader.getConstellationList();
		this.visibleClass = 700L;
	}
}
