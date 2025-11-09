package blue.endless.pi.enigma.wrapper;

import java.awt.image.BufferedImage;

import blue.endless.jankson.api.document.ArrayElement;
import blue.endless.jankson.api.document.ObjectElement;
import blue.endless.jankson.api.document.PrimitiveElement;
import blue.endless.pi.enigma.ItemType;
import blue.endless.pi.enigma.ObjectType;
import blue.endless.pi.Assets;
import blue.endless.pi.enigma.Direction;
import blue.endless.pi.enigma.DoorType;
import blue.endless.pi.enigma.EnemyType;

public class MapObjectInfo {
	protected final ScreenInfo screen;
	protected final ObjectElement json;
	
	public MapObjectInfo(ScreenInfo screen, ObjectElement json) {
		this.screen = screen;
		this.json = json;
	}
	
	public ObjectElement json() {
		return json;
	}
	
	public int x() {
		return json.getPrimitive("x").asInt().orElse(0);
	}
	
	public int y() {
		return json.getPrimitive("y").asInt().orElse(0);
	}
	
	public int roomX() {
		return x() + (screen.x() * ScreenInfo.PIXEL_WIDTH);
	}
	
	public int roomY() {
		return y() + (screen.y() * ScreenInfo.PIXEL_HEIGHT);
	}
	
	public ObjectType type() {
		int rawType = json.getPrimitive("type").asInt().orElse(0);
		return ObjectType.of(rawType);
	}
	
	public static class EnemyInfo extends MapObjectInfo {
		public EnemyInfo(ScreenInfo screen, ObjectElement json) {
			super(screen, json);
		}
		
		public EnemyType enemy() {
			int rawType = json.getPrimitive("type").asInt().orElse(0);
			return EnemyType.values.get(rawType);
		}
		
		public BufferedImage getSprite(WorldInfo world) {
			ArrayElement enemiesData = world.json().getArray("ENEMY_DATA");
			int rawType = json.getPrimitive("type").asInt().orElse(0);
			int[] palette = new int[] { 0, 32, 48 };
			if (rawType >= 0 && rawType < enemiesData.size()) {
				ArrayElement specificEnemyData = enemiesData.getArray(rawType);
				int level = json.getPrimitive("level").asInt().orElse(0);
				if (level >= 0 && level < specificEnemyData.size()) {
					ObjectElement leveledEnemyObj = specificEnemyData.getObject(level);
					ArrayElement levelPalette = leveledEnemyObj.getArray("palette_data");
					if (levelPalette.size() > 0) {
						ArrayElement paletteData = levelPalette.getArray(0);
						if (paletteData.size() >= 3) {
							palette[0] = paletteData.getPrimitive(0).asInt().orElse(0);
							palette[1] = paletteData.getPrimitive(1).asInt().orElse(32);
							palette[2] = paletteData.getPrimitive(2).asInt().orElse(48);
							
							return EnemyType.values.get(rawType).getSprite(palette);
						}
					}
				}
			}
			return enemy().getSprite();
		}
	}
	
	public static class ItemInfo extends MapObjectInfo {
		public ItemInfo(ScreenInfo screen, ObjectElement json) {
			super(screen, json);
		}
		
		public ItemType item() {
			int rawItem = json.getPrimitive("item").asInt().orElse(0);
			ItemType result = ItemType.of(rawItem);
			return result;
		}

		public void setItem(ItemType item) {
			json.put("item", PrimitiveElement.of(item.id()));
		}
	}
	
	public static class MapElevatorInfo extends MapObjectInfo {
		public MapElevatorInfo(ScreenInfo screen, ObjectElement json) {
			super(screen, json);
		}

		public Direction dir() {
			return Direction.of(json.getPrimitive("dir").asInt().orElse(1));
		}
		
		public ElevatorInfo asElevator(WorldInfo world, RoomInfo room) {
			return new ElevatorInfo(world, room, screen, json);
		}
		
		public boolean isEscape() {
			return json.getPrimitive("dest_rm").asInt().orElse(-2) == -1;
		}
		
		public void setEscape(boolean escape) {
			int destRm = (escape) ? -1 : -2;
			json.put("dest_rm", PrimitiveElement.of(destRm));
			json.put("dest_id", PrimitiveElement.of(0));
		}
	}
	
	public static class DoorObjectInfo extends MapObjectInfo {
		public static int[][] DOOR_PALETTES = new int[][] {
			{ 0x02, 0x11, 0x21 },
			{ 0x02, 0x11, 0x21 },
			{ 0x07, 0x06, 0x16 }
		};
		public DoorObjectInfo(ScreenInfo screen, ObjectElement json) {
			super(screen, json);
		}
		
		public DoorType doorType() {
			return DoorType.of(json.getPrimitive("type").asInt().orElse(0));
		}
		
		public Direction dir() {
			return Direction.of(json.getPrimitive("pos").asInt().orElse(1));
		}
		
		public BufferedImage sprite() {
			
			//int[] palette = new int[] { 0x00, 0x10, 0x20 };
			int paletteIndex = json.getPrimitive("type").asInt().orElse(0);
			//if (paletteIndex >= 0 && paletteIndex < DOOR_PALETTES.length) {
			//	palette = DOOR_PALETTES[paletteIndex];
			//}
			int[] palette = DoorType.of(paletteIndex).palette();
			return switch(dir()) {
				case NORTH -> Assets.getPalettedImage("objects/door_north.png", palette).orElseGet(Assets::missingImage);
				case SOUTH -> Assets.getPalettedImage("objects/door_south.png", palette).orElseGet(Assets::missingImage);
				case EAST -> Assets.getPalettedImage("objects/door_east.png", palette).orElseGet(Assets::missingImage);
				case WEST -> Assets.getPalettedImage("objects/door_west.png", palette).orElseGet(Assets::missingImage);
				default -> {
					yield Assets.missingImage();
				}
			};
		}
		
	}
}
