package blue.endless.pi.gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;

import javax.swing.JButton;

import blue.endless.pi.ItemType;

public class ItemTile extends JButton {
	private int scale = 3;
	private ItemType item;
	private BufferedImage itemImage;
	private boolean selected = false;
	private Runnable onClick = () -> {};
	
	public ItemTile() {
		this.setScale(3);
		this.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				onClick.run();
			}
			
		});
	}
	
	public int scale() {
		return this.scale;
	}
	
	public ItemType item() {
		return this.item;
	}
	
	public void setScale(int scale) {
		this.scale = scale;
		Dimension sz = new Dimension(16*scale, 16*scale);
		this.setMinimumSize(sz);
		this.setMaximumSize(sz);
		this.setPreferredSize(sz);
		this.invalidate();
	}
	
	public void setItem(ItemType item) {
		this.item = item;
		if (item != null) {
			this.itemImage = item.getSprite();
			this.setToolTipText(item.name());
		} else {
			this.itemImage = null;
			this.setToolTipText(null);
		}
		this.repaint();
	}
	
	@Override
	public void paint(Graphics g) {
		g.setColor(Color.BLACK);
		g.fillRect(0, 0, getWidth(), getHeight());
		if (itemImage != null) {
			int diff = (this.getWidth() - (itemImage.getWidth() * scale)) / 2;
			g.drawImage(itemImage, diff, 0, itemImage.getWidth()*scale + diff, itemImage.getHeight()*scale, 0, 0, itemImage.getWidth(), itemImage.getHeight(), null);
		}
		if (selected) {
			g.setColor(Color.GREEN);
			g.drawRect(1, 1, this.getWidth()-2, this.getHeight()-2);
			g.drawRect(0, 0, this.getWidth(), this.getHeight());
		}
	}
	
	public void setOnClick(Runnable evt) {
		this.onClick = evt;
	}
}
