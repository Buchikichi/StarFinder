package to.kit.starfinder;
/*
 * 作成日: 2004/04/13
 */

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.event.MouseWheelEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.swing.JFrame;
import javax.swing.Timer;
import javax.swing.WindowConstants;

/**
 * StarFinder.
 * @author Hidetaka Sasai
 */
public final class StarFinderMain extends JFrame implements ActionListener {
	/** Command name of the timer. */
	private static final String TIMER_CMD = "timer";
	/** Space. */
	private final Space space = new Space();
	private final Timer timer = new Timer(10, this);
	private Point pressedPoint = new Point();
	private int dx;

	/**
	 * @throws IOException
	 */
	public void execute() throws IOException {
		setTitle("Star Finder");
		setSize(700, 700);
		setLocationRelativeTo(null);
		setBackground(Color.BLACK);
		setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		setVisible(true);
		initEvents();
		this.space.rotateV(-34);
		this.space.addToVisibleClass(700);
	}

	private void initEvents() {
		addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				int keyCode = e.getKeyCode();

				if (keyCode == KeyEvent.VK_ESCAPE) {
					System.exit(0);
				}
			}
		});
		addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				StarFinderMain.this.mousePressed(e);
			}
			@Override
			public void mouseReleased(MouseEvent e) {
				StarFinderMain.this.mouseReleased(e);
			}
		});
		addMouseMotionListener(new MouseMotionAdapter() {
			@Override
			public void mouseDragged(MouseEvent e) {
				StarFinderMain.this.mouseDragged(e);
			}
		});
		addMouseWheelListener(new MouseAdapter() {
			@Override
			public void mouseWheelMoved(MouseWheelEvent e) {
				StarFinderMain.this.mouseWheelMoved(e);
			}
		});
		this.timer.setActionCommand(TIMER_CMD);
	}

	protected void mousePressed(MouseEvent e) {
		int button = e.getButton();

		if (button == MouseEvent.BUTTON1) {
			this.dx = 0;
			this.pressedPoint = e.getPoint();
		} else if (button == MouseEvent.BUTTON3) {
			this.space.toggleConstellation();
		}
	}

	/**
	 * ボタンを離した時の動作.
	 * @param e マウスイベント
	 */
	protected void mouseReleased(MouseEvent e) {
		// Space.this.visibleClass = Space.this.mVisibleSv;
		repaint();
	}

	protected void mouseDragged(MouseEvent e) {
		int diffX = this.pressedPoint.x - e.getX();
		int diffY = this.pressedPoint.y - e.getY();

		if (Math.abs(diffX) < 30) {
			this.dx = diffX * 5;
		}
		if (this.dx != 0 && !this.timer.isRunning()) {
			this.timer.start();
		}
		if (Math.abs(diffY) < 30) {
			this.space.rotateV(diffY / 5);
			repaint();
		}
		this.pressedPoint = e.getPoint();
	}

	protected void mouseWheelMoved(MouseWheelEvent e) {
		int d = -e.getWheelRotation() * 10;

		this.space.addToVisibleClass(d);
		repaint();
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		String command = e.getActionCommand();

		if (TIMER_CMD.equals(command)) {
			int sign = this.dx < 0 ? -1 : 1;
			int step = Math.min(Math.abs(this.dx), 1);

			if (this.dx == 0) {
				this.timer.stop();
			} else {
				this.space.rotateH(this.dx);
				repaint();
			}
			this.dx -= sign * step;
		}
	}

	@Override
	public void paint(Graphics g) {
		Rectangle rect = getBounds();
		Insets insets = getInsets();
		int width = (int) (rect.getWidth() - insets.left - insets.right);
		int height = (int) (rect.getHeight() - insets.top - insets.bottom);
		int edgeLength = (width < height ? width : height);
		BufferedImage img = this.space.getImage(edgeLength);
		int left = insets.left;
		int margin = width - edgeLength;

		if (0 < margin) {
			left += margin / 2;
		}
		g.drawImage(img, left, insets.top, this);
	}

	/**
	 * Main.
	 * @param args Arguments
	 * @throws Exception Exception
	 */
	public static void main(String[] args) throws Exception {
		StarFinderMain app = new StarFinderMain();

		app.execute();
	}
}
