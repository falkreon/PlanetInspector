package blue.endless.pi.gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.function.Consumer;

import javax.swing.JPanel;

import blue.endless.jankson.api.document.ArrayElement;
import blue.endless.jankson.api.document.ObjectElement;
import blue.endless.jankson.api.document.ValueElement;
import blue.endless.pi.Assets;
import blue.endless.pi.datastruct.Vec2;
import blue.endless.pi.enigma.wrapper.MapObjectInfo;
import blue.endless.pi.enigma.wrapper.RoomInfo;
import blue.endless.pi.enigma.wrapper.ScreenInfo;
import blue.endless.pi.enigma.wrapper.WorldInfo;

public class RoomDisplayPanel extends JPanel {
	private final WorldInfo world;
	private int roomId;
	private final RoomInfo room;
	private HashMap<Vec2, BufferedImage> tiles = new HashMap<>();
	private Vec2 offset = new Vec2(0, 0);
	
	private final ArrayList<MapObjectInfo.ItemInfo> items = new ArrayList<>();
	private final ArrayList<MapObjectInfo.EnemyInfo> enemies = new ArrayList<>();
	private final ArrayList<MapObjectInfo> objects = new ArrayList<>();
	
	private int scale = 2;
	
	private Consumer<MapObjectInfo.ItemInfo> itemSelectCallback = (it) -> {};
	private Consumer<MapObjectInfo.EnemyInfo> enemySelectCallback = (it) -> {};
	// final int SCREEN_WIDTH = 20 * 16;
	//private final int SCREEN_HEIGHT = 15 * 16;
	
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
						items.add(new MapObjectInfo.ItemInfo(s,                     objObj));
					} else {
						objects.add(new MapObjectInfo(s, objObj));
					}
				}
			}
			
			ArrayElement enemiesArr = s.json().getArray("ENEMIES");
			for(ValueElement val : enemiesArr) {
				if (val instanceof ObjectElement enemyObj) {
					enemies.add(new MapObjectInfo.EnemyInfo(s, enemyObj));
				}
			}
		}
		if (minX != Integer.MAX_VALUE && minY != Integer.MAX_VALUE) offset = new Vec2(minX, minY);
		
		int dx = maxX - minX + 1;
		int dy = maxY - minY + 1;
		
		Dimension mapSize = new Dimension(dx * 20 * 16 * scale, dy * 15 * 16 * scale);
		this.setMinimumSize(mapSize);
		this.setPreferredSize(mapSize);
		this.setMaximumSize(mapSize);
		
		this.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				for(MapObjectInfo.ItemInfo item : items) {
					int halfWidth = 8;
					int halfHeight = 8;
					int baseX = item.roomX() - (offset.x() * ScreenInfo.PIXEL_WIDTH);
					int baseY = item.roomY() - (offset.y() * ScreenInfo.PIXEL_HEIGHT);
					
					if (e.getX() >= baseX-halfWidth && e.getY() >= baseY-halfHeight && e.getX() < baseX+halfWidth && e.getY() < baseY+halfHeight) {
						//System.out.println("Clicked on item "+item.item().name());
						itemSelectCallback.accept(item);
					}
				}
			}
		});
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
				int screenX = ScreenInfo.PIXEL_WIDTH * (pos.x() - offset.x()); screenX *= scale;
				int screenY = ScreenInfo.PIXEL_HEIGHT * (pos.y() - offset.y()); screenY *= scale;
				g.drawImage(tileImage, screenX, screenY, ScreenInfo.PIXEL_WIDTH * scale, ScreenInfo.PIXEL_HEIGHT * scale, null);
			}
		}
		
		for(MapObjectInfo object : objects) {
			ObjectType type = object.type();
			if (type == null) {
				g.drawImage(Assets.missingImage(), object.roomX(), object.roomY(), null);
			} else if (type == ObjectType.GUNSHIP) {
				BufferedImage image = Assets.getCachedImage("objects/gunship.png").orElseGet(Assets::missingImage);
				int halfWidth = image.getWidth() / 2;
				int halfHeight = image.getHeight() / 2;
				int baseX = object.roomX() - (offset.x() * ScreenInfo.PIXEL_WIDTH);
				int baseY = object.roomY() - (offset.y() * ScreenInfo.PIXEL_HEIGHT);
				g.drawImage(image, (baseX - halfWidth) * scale, (baseY - halfHeight) * scale, image.getWidth() * scale, image.getHeight() * scale, null);
			}
		}
		
		for(MapObjectInfo.ItemInfo item : items) {
			BufferedImage image = item.item().getSprite();
			//Optional<BufferedImage> maybeImage = Assets.getCachedImage("items/"+item.item().spriteResource()+".png");
			//if (maybeImage.isPresent()) {
				//BufferedImage image = maybeImage.get();
				
				// Draw the item
				int halfWidth = image.getWidth() / 2;
				int halfHeight = image.getHeight() / 2;
				int baseX = item.roomX() - (offset.x() * ScreenInfo.PIXEL_WIDTH);
				int baseY = item.roomY() - (offset.y() * ScreenInfo.PIXEL_HEIGHT);
				g.drawImage(image, (baseX - halfWidth) * scale, (baseY - halfHeight) * scale, image.getWidth() * scale, image.getHeight() * scale, null);
				
				// Draw a selection box around it
				g.setColor(Color.WHITE);
				g.drawRect((baseX - halfWidth - 1) * scale, (baseY - halfHeight - 1) * scale, (image.getWidth() + 2) * scale, (image.getHeight() + 2) * scale);
			//}
		}
		
		for(MapObjectInfo.EnemyInfo enemy : enemies) {
			BufferedImage image = (enemy.enemy() == null) ? Assets.missingImage() : enemy.enemy().getSprite();
			int halfWidth = image.getWidth() / 2;
			int halfHeight = image.getHeight() / 2;
			int baseX = enemy.roomX() - (offset.x() * ScreenInfo.PIXEL_WIDTH);
			int baseY = enemy.roomY() - (offset.y() * ScreenInfo.PIXEL_HEIGHT);
			g.drawImage(image, (baseX - halfWidth) * scale, (baseY - halfHeight) * scale, null);
		}
	}

	public void setItemSelectCallback(Consumer<MapObjectInfo.ItemInfo> callback) {
		this.itemSelectCallback = callback;
	}
}
