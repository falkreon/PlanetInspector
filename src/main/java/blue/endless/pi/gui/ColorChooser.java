package blue.endless.pi.gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.util.function.Consumer;

import javax.swing.JComponent;

public class ColorChooser extends JComponent {
	private BufferedImage palette = new BufferedImage(64, 64, BufferedImage.TYPE_INT_ARGB);
	
	private float selectedHue = 0;
	private float selectedSat = 0;
	private float selectedLit = 0.8f;
	private Consumer<Color> editCallback = (it) -> {};
	
	public ColorChooser() {
		Dimension d = new Dimension(100, 64);
		setMinimumSize(d);
		setPreferredSize(d);
		setMaximumSize(d);
		
		setLightness(0.8f);
		
		this.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				float hue = e.getX() / (float) e.getComponent().getWidth();
				float sat = e.getY() / (float) e.getComponent().getHeight();
				setColor(hue, sat);
			}
		});
	}
	
	public Color selectedColor() {
		return Color.getHSBColor(selectedHue, selectedSat, selectedLit);
	}
	
	public void setLightness(float lightness) {
		this.selectedLit = lightness;
		
		for(int y=0; y<64; y++) {
			float sat = y / 64.0f;
			for(int x=0; x<64; x++) {
				float hue = x / 64.0f;
				Color cell = Color.getHSBColor(hue, sat, 0.4f);
				palette.setRGB(x, y, cell.getRGB());
			}
		}
	}
	
	public void setColor(Color c) {
		float[] hsv = new float[3];
		hsv = Color.RGBtoHSB(c.getRed(), c.getGreen(), c.getBlue(), hsv);
		this.selectedHue = hsv[0];
		this.selectedSat = hsv[1];
		this.setLightness(hsv[2]);
		this.repaint();
		
		editCallback.accept(c);
	}
	
	public void setColor(float hue, float sat) {
		this.selectedHue = hue;
		this.selectedSat = sat;
		this.repaint();
		
		editCallback.accept(selectedColor());
	}
	
	@Override
	public void paint(Graphics g) {
		if (g instanceof Graphics2D gr) {
			gr.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
			gr.drawImage(palette, 0, 0, this.getWidth(), this.getHeight(), null);
			
			int x = (int) (this.getWidth() * selectedHue);
			int y = (int) (this.getHeight() * selectedSat);
			gr.setColor(Color.BLACK);
			gr.drawOval(x-3, y-3, 7, 7);
			gr.setColor(Color.WHITE);
			gr.drawOval(x-2, y-2, 5, 5);
		}
	}

	public void setEditCallback(Consumer<Color> callback) {
		this.editCallback = callback;
	}
	
}
