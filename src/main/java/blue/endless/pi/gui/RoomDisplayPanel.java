package blue.endless.pi.gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

import javax.swing.JPanel;

import blue.endless.jankson.api.document.ArrayElement;
import blue.endless.jankson.api.document.ObjectElement;
import blue.endless.jankson.api.document.ValueElement;
import blue.endless.pi.Assets;
import blue.endless.pi.datastruct.Rect;
import blue.endless.pi.datastruct.Vec2;
import blue.endless.pi.enigma.ObjectType;
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
	
	private int scale = 2;
	
	private Consumer<MapObjectInfo> selectCallback = (it) -> {};
	
	private static record Selectable(MapObjectInfo object, Rect rect) {}
	private static List<Selectable> selectables = new ArrayList<>();
	private Selectable selectedObject = null;
	//private Rect selectedRect = null;
	
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
		}
		if (minX != Integer.MAX_VALUE && minY != Integer.MAX_VALUE) offset = new Vec2(minX, minY);
		
		
		selectables.clear();
		for(ScreenInfo s : room.screens()) {
			// Grab enemies and items
			ArrayElement objectsArr = s.json().getArray("OBJECTS");
			for(ValueElement val : objectsArr) {
				if (val instanceof ObjectElement objObj) {
					if (ObjectType.of(objObj) == ObjectType.ITEM) {
						MapObjectInfo.ItemInfo itemInfo = new MapObjectInfo.ItemInfo(s, objObj);
						int baseX = itemInfo.roomX() - (offset.x() * ScreenInfo.PIXEL_WIDTH);
						int baseY = itemInfo.roomY() - (offset.y() * ScreenInfo.PIXEL_HEIGHT);
						Rect r = new Rect(baseX-8, baseY-8, 16, 16);
						selectables.add(new Selectable(itemInfo, r));
					} else {
						MapObjectInfo objectInfo = new MapObjectInfo(s, objObj);
						int baseX = objectInfo.roomX() - (offset.x() * ScreenInfo.PIXEL_WIDTH);
						int baseY = objectInfo.roomY() - (offset.y() * ScreenInfo.PIXEL_HEIGHT);
						Rect r = new Rect(baseX-8, baseY-8, 16, 16);
						selectables.add(new Selectable(objectInfo, r));
					}
				}
			}
			
			ArrayElement enemiesArr = s.json().getArray("ENEMIES");
			for(ValueElement val : enemiesArr) {
				if (val instanceof ObjectElement enemyObj) {
					MapObjectInfo enemyInfo = new MapObjectInfo.EnemyInfo(s, enemyObj);
					int baseX = enemyInfo.roomX() - (offset.x() * ScreenInfo.PIXEL_WIDTH);
					int baseY = enemyInfo.roomY() - (offset.y() * ScreenInfo.PIXEL_HEIGHT);
					Rect r = new Rect(baseX-8, baseY-8, 16, 16);
					selectables.add(new Selectable(enemyInfo, r));
				}
			}
			
			ArrayElement elevatorsArr = s.json().getArray("ELEVATORS");
			for(ValueElement val : elevatorsArr) {
				if (val instanceof ObjectElement elevatorObj) {
					MapObjectInfo.MapElevatorInfo elevatorInfo = new MapObjectInfo.MapElevatorInfo(s, elevatorObj);
					int baseX = elevatorInfo.roomX() - (offset.x() * ScreenInfo.PIXEL_WIDTH);
					int baseY = elevatorInfo.roomY() - (offset.y() * ScreenInfo.PIXEL_HEIGHT);
					Rect r = new Rect(baseX-8, baseY-8, 16, 16);
					selectables.add(new Selectable(elevatorInfo, r));
				}
			}
			
			ArrayElement doorsArr = s.json().getArray("DOORS");
			for(ValueElement val : doorsArr) {
				if (val instanceof ObjectElement doorObj) {
					MapObjectInfo.DoorObjectInfo doorInfo = new MapObjectInfo.DoorObjectInfo(s, doorObj);
					int baseX = doorInfo.roomX() - (offset.x() * ScreenInfo.PIXEL_WIDTH);
					int baseY = doorInfo.roomY() - (offset.y() * ScreenInfo.PIXEL_HEIGHT);
					Rect r = new Rect(baseX-8, baseY-8, 16, 16);
					selectables.add(new Selectable(doorInfo, r));
				}
			}
		}
		
		int dx = maxX - minX + 1;
		int dy = maxY - minY + 1;
		
		Dimension mapSize = new Dimension(dx * 20 * 16 * scale, dy * 15 * 16 * scale);
		this.setMinimumSize(mapSize);
		this.setPreferredSize(mapSize);
		this.setMaximumSize(mapSize);
		
		this.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				int clickX = e.getX() / scale;
				int clickY = e.getY() / scale;
				
				for(Selectable s : selectables) {
					if (s.rect().contains(clickX, clickY)) {
						selectedObject = s;
						selectCallback.accept(s.object());
						RoomDisplayPanel.this.repaint();
						return;
					}
				}
				
				selectedObject = null;
				selectCallback.accept(null);
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
		
		BufferedImage availableReticleImage = Assets.getCachedImage("gui/available_reticle.png").orElseGet(Assets::missingImage);
		BufferedImage selectionReticleImage = Assets.getCachedImage("gui/selection_reticle.png").orElseGet(Assets::missingImage);
		int reticleHalfWidth = availableReticleImage.getWidth() / 2;
		int reticleHalfHeight = availableReticleImage.getHeight() / 2;
		
		for(Selectable selectable : selectables) {
			BufferedImage image = null;
			switch(selectable.object()) {
				case MapObjectInfo.ItemInfo item -> {
					image = item.item().getSprite();
				}
				case MapObjectInfo.EnemyInfo enemy -> {
					image = enemy.getSprite(world);
				}
				case MapObjectInfo.MapElevatorInfo elevator -> {
					image = Assets.getPalettedImage("objects/spr_Elevator_0.png", new int[] { 22, 40, 26 }).orElseGet(Assets::missingImage);
				}
				case MapObjectInfo.DoorObjectInfo door -> {
					image = door.sprite();
				}
				default -> {
					ObjectType type = selectable.object().type();
					switch(type) {
						case GUNSHIP -> {
							image = Assets.getPalettedImage("objects/gunship.png", new int[] { 0x10, 0x17, 0x28 }).orElseGet(Assets::missingImage);
						}
						
						case SCANNER -> {
							image = Assets.getPalettedImage("objects/map_scanner.png", new int[] { 0x10, 0x20, 0x30 }).orElseGet(Assets::missingImage);
						}
						
						case null, default -> { image = null; }
					}
					
				}
			}
			int halfWidth = 8;
			int halfHeight = 8;
			if (image != null) {
				halfWidth = image.getWidth() / 2;
				halfHeight = image.getHeight() / 2;
			}
			int baseX = selectable.object().roomX() - (offset.x() * ScreenInfo.PIXEL_WIDTH);
			int baseY = selectable.object().roomY() - (offset.y() * ScreenInfo.PIXEL_HEIGHT);
			//System.out.println("Selectable of type "+selectable.object.getClass().getSimpleName()+" is at "+baseX+", "+baseY+", with rect "+selectable.rect());
			if (image != null) {
				g.drawImage(image, (baseX - halfWidth) * scale, (baseY - halfHeight) * scale, image.getWidth() * scale, image.getHeight() * scale, null);
			} else {
				g.setColor(Color.GREEN);
				g.fillRect((baseX - 4) * scale, (baseY - 4) * scale, 8 * scale, 8 * scale);
			}
			
			if (Objects.equals(selectedObject, selectable)) {
				g.drawImage(selectionReticleImage,
						(baseX - reticleHalfWidth) * scale,
						(baseY - reticleHalfHeight) * scale,
						selectionReticleImage.getWidth() * scale,
						selectionReticleImage.getHeight() * scale, null);
			} else {
				g.drawImage(availableReticleImage,
						(baseX - reticleHalfWidth) * scale,
						(baseY - reticleHalfHeight) * scale,
						selectionReticleImage.getWidth() * scale,
						selectionReticleImage.getHeight() * scale, null);
			}
		}
	}
	
	public void setSelectCallback(Consumer<MapObjectInfo> callback) {
		this.selectCallback = callback;
	}
}
