package blue.endless.pi.gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;

import javax.swing.JComponent;

public class TestCard extends JComponent {
	private final Color color;
	public TestCard() {
		int r = (int) (Math.random() * 100 + 155);
		int g = (int) (Math.random() * 100 + 155);
		int b = (int) (Math.random() * 100 + 155);
		color = new Color(r, g, b);
		
		this.setMinimumSize(new Dimension(32, 32));
		this.setPreferredSize(new Dimension(32, 32));
		this.setMaximumSize(new Dimension(32, 32));
		
		this.setSize(new Dimension(8, 8));
	}
	
	@Override
	public void paint(Graphics g) {
		g.setColor(color.darker());
		
		g.fillRect(0, 0, this.getWidth(), this.getHeight());
		
		int marginsX = this.getWidth() - 32;
		int marginsY = this.getHeight() - 32;
		int leftMargin = marginsX / 2;
		int topMargin = marginsY / 2;
		
		g.setColor(color);
		g.fillRect(leftMargin, topMargin, 32, 32);
	}
}
