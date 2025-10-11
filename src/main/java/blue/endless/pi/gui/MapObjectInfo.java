package blue.endless.pi.gui;

import blue.endless.jankson.api.document.ObjectElement;
import blue.endless.pi.ItemType;

public class MapObjectInfo {
	protected final ObjectElement json;
	
	public MapObjectInfo(ObjectElement json) {
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
	
	public ObjectType type() {
		int rawType = json.getPrimitive("type").asInt().orElse(0);
		return ObjectType.of(rawType);
	}
	
	public static class EnemyInfo extends MapObjectInfo {
		public EnemyInfo(ObjectElement json) {
			super(json);
		}
		
		
	}
	
	public static class ItemInfo extends MapObjectInfo {
		public ItemInfo(ObjectElement json) {
			super(json);
		}
		
		public ItemType item() {
			int rawItem = json.getPrimitive("item").asInt().orElse(0);
			ItemType result = ItemType.byId(rawItem);
			return result;
		}
	}
}
