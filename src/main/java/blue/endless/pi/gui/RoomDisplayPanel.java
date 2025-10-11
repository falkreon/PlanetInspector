package blue.endless.pi.gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Optional;

import javax.swing.JPanel;

import blue.endless.jankson.api.document.ArrayElement;
import blue.endless.jankson.api.document.ObjectElement;
import blue.endless.jankson.api.document.ValueElement;
import blue.endless.pi.Assets;
import blue.endless.pi.datastruct.Vec2;

public class RoomDisplayPanel extends JPanel {
	private final WorldInfo world;
	private int roomId;
	private final RoomInfo room;
	private HashMap<Vec2, BufferedImage> tiles = new HashMap<>();
	private Vec2 offset = new Vec2(0, 0);
	
	private final ArrayList<MapObjectInfo.ItemInfo> items = new ArrayList<>();
	private final ArrayList<MapObjectInfo.EnemyInfo> enemies = new ArrayList<>();
	
	private float scale = 1.0f;
	private final int SCREEN_WIDTH = 20 * 16;
	private final int SCREEN_HEIGHT = 15 * 16;
	
	public RoomDisplayPanel(WorldInfo world, int roomId) {
		this.room = world.rooms().get(roomId);
		this.world = world;
		this.roomId = roomId;
		
		int minX = Integer.MAX_VALUE;
		int minY = Integer.MAX_VALUE;
		int maxX = Integer.MIN_VALUE;
		int maxY = Integer.MIN_VALUE;
		for(ScreenInfo s : room.screens()) {
			minX = Math.min(minX, s.x());
			minY = Math.min(minY, s.y());
			maxX = Math.max(maxX, s.x());
			maxY = Math.max(maxY, s.y());
			
			// Grab enemies and items
			ArrayElement objectsArr = s.json().getArray("OBJECTS");
			for(ValueElement val : objectsArr) {
				if (val instanceof ObjectElement objObj) {
					if (ObjectType.of(objObj) == ObjectType.ITEM) {
						items.add(new MapObjectInfo.ItemInfo(objObj));
					}
				}
			}
			
			ArrayElement enemiesArr = s.json().getArray("ENEMIES");
			for(ValueElement val : enemiesArr) {
				if (val instanceof ObjectElement enemyObj) {
					enemies.add(new MapObjectInfo.EnemyInfo(enemyObj));
				}
			}
		}
		if (minX != Integer.MAX_VALUE && minY != Integer.MAX_VALUE) offset = new Vec2(minX, minY);
		
		int dx = maxX - minX + 1;
		int dy = maxY - minY + 1;
		
		Dimension mapSize = new Dimension(dx * 20 * 16, dy * 15 * 16);
		this.setMinimumSize(mapSize);
		this.setPreferredSize(mapSize);
		this.setMaximumSize(mapSize);
	}
	
	@Override
	public void paint(Graphics g) {
		g.setColor(Color.BLACK);
		g.fillRect(0,  0, this.getWidth(), this.getHeight());
		
		for(ScreenInfo screen : room.screens()) {
			Vec2 pos = new Vec2(screen.x(), screen.y());
			BufferedImage tileImage = tiles.get(pos);
			if (tileImage == null) {
				System.out.println("Rendering tile image for screen at "+pos);
				tileImage = screen.createImage(room);
				tiles.put(pos, tileImage);
			}
			
			if (tileImage != null) {
				g.drawImage(tileImage, SCREEN_WIDTH * (pos.x() - offset.x()), SCREEN_HEIGHT * (pos.y() - offset.y()), null);
			}
		}
		
		for(MapObjectInfo.ItemInfo item : items) {
			Optional<BufferedImage> maybeImage = Assets.getCachedImage("items/"+item.item().spriteResource()+".png");
			if (maybeImage.isPresent()) {
				BufferedImage image = maybeImage.get();
				
				// Draw the item
				int halfWidth = image.getWidth() / 2;
				int halfHeight = image.getHeight() / 2;
				g.drawImage(image, item.x() - halfWidth, item.y() - halfHeight, null);
				
				// Draw a selection box around it
				g.setColor(Color.WHITE);
				g.drawRect(item.x() - halfWidth - 1, item.y() - halfHeight - 1, image.getWidth() + 2, image.getHeight() + 2);
			}
		}
	}
}
