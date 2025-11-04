package blue.endless.pi.gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;

import javax.swing.JButton;
import javax.swing.TransferHandler;

import blue.endless.pi.ItemStack;
import blue.endless.pi.enigma.ItemType;

public class ItemTile extends JButton {
	private int scale = 3;
	private ItemType item;
	private int count;
	private BufferedImage itemImage;
	private Runnable onClick = () -> {};
	
	public ItemTile() {
		this.setScale(3);
		this.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				onClick.run();
			}
		});
		
		setTransferHandler(new TransferHandler("item"));
		
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
		this.count = 1;
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
		
		if (this.count > 1) {
			String countString = Integer.toString(count);
			g.setFont(g.getFont().deriveFont(Font.BOLD));
			Rectangle2D bounds = g.getFontMetrics().getStringBounds(countString, g);
			int x = this.getWidth() - (int) bounds.getMaxX() - 2;
			int y = this.getHeight() - (int) bounds.getMaxY();
			
			Drawing.textWithOutline(g, x, y, countString, getFont().deriveFont(Font.BOLD, 6 * scale), Color.WHITE, Color.BLACK, 2.0f * scale);
		}
	}
	
	public void onClick(Runnable evt) {
		this.onClick = evt;
	}

	public void setStack(ItemStack stack) {
		this.item = stack.item();
		this.count = stack.count();
		
		this.itemImage = this.item.getSprite();
		this.setToolTipText(this.item.name());
		
		this.repaint();
	}
}
