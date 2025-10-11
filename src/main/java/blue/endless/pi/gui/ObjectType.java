package blue.endless.pi.gui;

import blue.endless.jankson.api.document.ObjectElement;

/**
 * The kind of object indicated by a "$.ROOMS[*].SCREENS[*].OBJECTS[*].type" key
 */
public enum ObjectType {
	ITEM(0),
	GUNSHIP(1)
	;
	
	private final int value;
	
	ObjectType(int value) {
		this.value = value;
	}
	
	public int value() { return value; }
	
	public static ObjectType of(int value) {
		for(ObjectType o : values()) {
			if (o.value == value) return o;
		}
		
		return null;
	}
	
	public static ObjectType of(ObjectElement obj) {
		int rawType = obj.getPrimitive("type").asInt().orElse(0);
		return ObjectType.of(rawType);
	}
}
