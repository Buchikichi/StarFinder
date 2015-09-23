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
	private long rotation;

	/** 表示する星座のクラス. */
	private long visibleClass;
	private long degreeX = 0L;
//	private long degreeY = 0L;
	private long degreeZ = 0L;
//	private long mVisibleSv;
//	private boolean isLightMode = false;

	/**
	 * 位置を変換.
	 * @param ioZ
	 * @param longitude
	 * @param latitude
	 * @return 表示位置
	 */
	private Point convPos(long[] ioZ, final long longitude, final long latitude) {
		Point pt = new Point(0, 0);
		long wx = pt.x;
		long wy = pt.y;
		long wz = ioZ[0];
		long degY = longitude - this.rotation;
		long degX = latitude;
		pt.y = MyMath.cos(degX, wy) - MyMath.sin(degX, wz);
		ioZ[0] = MyMath.sin(degX, wy) + MyMath.cos(degX, wz);
		wz = ioZ[0];
		pt.x = MyMath.cos(degY, wx) - MyMath.sin(degY, wz);
		ioZ[0] = MyMath.sin(degY, wx) + MyMath.cos(degY, wz);
		wy = pt.y;
		wz = ioZ[0];
		pt.y = MyMath.cos(this.degreeX, wy) - MyMath.sin(this.degreeX, wz);
		ioZ[0] = MyMath.sin(this.degreeX, wy) + MyMath.cos(this.degreeX, wz);
		if (ioZ[0] < 0L) {
			return pt;
		}
		wx = pt.x;
		wy = pt.y;
		pt.x = MyMath.cos(this.degreeZ, wx) - MyMath.sin(this.degreeZ, wy);
		pt.y = MyMath.sin(this.degreeZ, wx) + MyMath.cos(this.degreeZ, wy);
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
		Point pt = convPos(pz, star.getLongitude(), star.getLatitude());

		if (0L < pz[0]) {
			g.setColor(star.getColor());
			g.drawLine(pt.x, pt.y, pt.x, pt.y);
			result = true;
		}
		return result;
	}

	private void drawConst(Graphics2D g) {
		for (Constellation constellation : this.constList) {
			Point prev = null;

			g.setColor(new Color(34, 102, 102));
			for (ConstLine line : constellation) {
				Star star = line.getStar();
				if (line.isBegin()) {
					// moveTo
					long[] pz = { this.center };
					prev = convPos(pz, star.getLongitude(), star.getLatitude());
				} else if (prev != null) {
					// lineTo
					long[] pz = { this.center };
					Point pt = convPos(pz, star.getLongitude(), star.getLatitude());
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

	/**
	 * 描画イメージを取得.
	 * @param size 高さ/幅
	 * @return 描画イメージ
	 */
	public BufferedImage getImage(final int size) {
		BufferedImage image = new BufferedImage(size, size, BufferedImage.TYPE_INT_RGB);
		Graphics2D g = (Graphics2D) image.getGraphics();
		AffineTransform tx = g.getTransform();

		this.center = size / 2;
		tx.translate(this.center, this.center);
		g.setTransform(tx);
		g.setColor(Color.BLACK);
		drawConst(g);
		for (Star star : this.stars) {
			if (star.getV() < this.visibleClass) {
				drawStar(g, star);
			}
		}
		return image;
	}

	/**
	 * 回転する.
	 * @param dx 度合
	 */
	public void rotate(int dx) {
		this.rotation += dx;
		this.rotation = MyMath.trimDegree(this.rotation);
//		if (this.rotation < 0L)
//			this.rotation += MyMath.DegMax;
//		if (MyMath.DegMax < this.rotation)
//			this.rotation -= MyMath.DegMax;
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
		this.degreeX = MyMath.deg(-34L);
		this.stars = loader.getStars();
		this.constList = loader.getConstellationList();
//		this.visibleClass = 500L;
		this.visibleClass = 1000L;
//		this.mVisibleSv = 0L;
	}
}
