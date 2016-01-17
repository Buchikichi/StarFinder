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
import to.kit.starfinder.io.SpaceWriter;
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
	private double rotationRad;
	private double latitude;
	private double latRad;

	/** 星座の線を表示. */
	private boolean showConstellation = true;
	/** 表示する星座のクラス. */
	private long visibleClass;
//	private long degreeY = 0L;
	private double radZ = 0;
//	private long mVisibleSv;
//	private boolean isLightMode = false;

	/**
	 * 位置を変換.
	 * @param ra
	 * @param decDeg
	 * @return 表示位置
	 */
	private Point convPos(final double ra, final double decRad) {
		Point pt = new Point(0, 0);
		double raRad = ra + this.rotationRad;
		double radX = this.latRad;
		int wz = this.center;

		int wy = (int) (-Math.sin(decRad) * wz);
		wz = (int) (Math.cos(decRad) * wz);

		int wx = (int) (-Math.sin(raRad) * wz);
		wz = (int) (Math.cos(raRad) * wz);

		pt.y = (int) (Math.cos(radX) * wy - Math.sin(radX) * wz);
		wz = (int) (Math.sin(radX) * wy + Math.cos(radX) * wz);
		if (wz < 0L) {
			return null;
		}
		wy = pt.y;
		pt.x = (int) (Math.cos(this.radZ) * wx - Math.sin(this.radZ) * wy);
		pt.y = (int) (Math.sin(this.radZ) * wx + Math.cos(this.radZ) * wy);
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
		Point pt = convPos(star.getRa(), star.getDec());

		if (pt != null) {
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
					prev = convPos(star.getRa(), star.getDec());
				} else if (prev != null) {
					// lineTo
					Point pt = convPos(star.getRa(), star.getDec());
					if (pt != null) {
						g.drawLine(prev.x, prev.y, pt.x, pt.y);
					}
					prev = pt;
				}
			}
			Star star = constellation.getPos();
			Point pt = convPos(star.getRa(), star.getDec());
			if (pt != null) {
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
		g.drawString("r:" + this.rotationH, -this.center, y);
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
		this.rotationRad = this.rotationH * Math.PI / 180.0;
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
		this.latRad = (this.latitude * Math.PI) / 180.0;
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
		SpaceWriter writer = new SpaceWriter();

		try {
			loader.load();
			this.stars = loader.getStars();
			this.constList = loader.getConstellationList();
//			writer.save(this.stars);
			writer.saveConstellation(this.constList);
		} catch (IOException e) {
			// 基本的にありえない
			e.printStackTrace();
		}
	}
}
