package blue.endless.pi.gui;

import blue.endless.jankson.api.document.ObjectElement;
import blue.endless.jankson.api.document.PrimitiveElement;
import blue.endless.pi.ItemType;

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
}
