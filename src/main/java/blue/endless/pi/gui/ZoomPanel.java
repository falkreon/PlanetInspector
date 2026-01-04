package blue.endless.pi.gui;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import javax.swing.JComponent;

public abstract class ZoomPanel extends JComponent {
	private int scale = 2;
	private int xofs = 0;
	private int yofs = 0;
	private BufferedImage buf;
	
	public ZoomPanel() {
		this.setDoubleBuffered(false);
		this.setFocusable(true);
	}
	
	@Override
	public void paint(Graphics g) {
		int bufWidthNeeded = this.getWidth() / scale;
		int bufHeightNeeded = this.getHeight() / scale;
		
		if (buf == null || buf.getWidth() < bufWidthNeeded || buf.getHeight() < bufHeightNeeded) {
			buf = new BufferedImage(bufWidthNeeded, bufHeightNeeded, BufferedImage.TYPE_INT_ARGB);
		}
		
		Graphics g2 = buf.getGraphics();
		if (g2 instanceof Graphics2D g3) {
			paintScaled(g3);
		}
		
		g.setColor(getBackground());
		g.fillRect(0, 0, this.getWidth(), this.getHeight());
		g.drawImage(buf, -xofs, -yofs, buf.getWidth() * scale, buf.getHeight() * scale, null);
	}
	
	public abstract void paintScaled(Graphics2D g);
	
	//public abstract void processClick()
	
	private int scale(int value) {
		if (value < 0) return value;
		return value * scale;
	}
	
	@Override
	public Dimension getMinimumSize() {
		Dimension unscaled = super.getMinimumSize();
		return new Dimension(scale(unscaled.width), scale(unscaled.height));
	}
	
	@Override
	public Dimension getPreferredSize() {
		Dimension unscaled = super.getPreferredSize();
		return new Dimension(scale(unscaled.width), scale(unscaled.height));
	}
	
	@Override
	public Dimension getMaximumSize() {
		Dimension unscaled = super.getMaximumSize();
		return new Dimension(scale(unscaled.width), scale(unscaled.height));

	}
}
