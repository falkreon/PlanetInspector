package blue.endless.pi.gui;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;

import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollBar;

public class CustomScrollPane extends JPanel {
	private static final int SCROLLBAR_WIDTH = 20;
	
	private JScrollBar hScroll = new JScrollBar(JScrollBar.HORIZONTAL);
	private JScrollBar vScroll = new JScrollBar(JScrollBar.VERTICAL);
	private ViewHolder view = new ViewHolder();
	
	public CustomScrollPane() {
		this.add(hScroll);
		this.add(vScroll);
		this.add(this.view);
		
		hScroll.addAdjustmentListener(new AdjustmentListener() {
			@Override
			public void adjustmentValueChanged(AdjustmentEvent evt) {
				hScroll.getValue();
			}
			
		});
	}
	
	public void setView(TinyPanel view) {
		this.view.view = view;
	}
	
	@Override
	public void validate() {
		hScroll.setLocation(0, this.getHeight() - SCROLLBAR_WIDTH);
		hScroll.setSize(this.getWidth() - SCROLLBAR_WIDTH, SCROLLBAR_WIDTH);
		vScroll.setLocation(this.getWidth() - SCROLLBAR_WIDTH, 0);
		vScroll.setSize(SCROLLBAR_WIDTH, this.getHeight() - SCROLLBAR_WIDTH);
		view.setLocation(0, 0);
		view.setSize(this.getWidth() - SCROLLBAR_WIDTH, this.getHeight() - SCROLLBAR_WIDTH);
		
		// TODO: Update scroll bars and clamp positions
		
	}
	
	private class ViewHolder extends JComponent {
		private TinyPanel view;
		private int scale = 2;
		private int scrollX = 0;
		private int scrollY = 0;
		
		@Override
		public void paint(Graphics g) {
			if (view == null) {
				g.setColor(this.getBackground());
				g.fillRect(0, 0, this.getWidth(), this.getHeight());
				return;
			}
			
			if (g instanceof Graphics2D g2) {
				g2.translate(-scrollX, -scrollY);
				g2.scale(scale, scale);
				view.onPaint().invoker().accept(g2);
			}
		}
	}
}
