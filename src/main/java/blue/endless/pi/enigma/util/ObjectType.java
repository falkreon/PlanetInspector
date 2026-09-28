package blue.endless.pi.enigma.util;

import java.util.HashMap;
import java.util.Map;

import org.jetbrains.annotations.Nullable;

import blue.endless.jankson.api.document.KeyValuePairElement;
import blue.endless.jankson.api.document.ObjectElement;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;

/**
 * The kind of object indicated by a "$.ROOMS[*].SCREENS[*].OBJECTS[*].type" key
 */
public class ObjectType {
	public static ObjectType ITEM;
	public static ObjectType GUNSHIP;
	/**
	ITEM(0),
	GUNSHIP(1),
	
	SCANNER(3),
	
	SPAWN_POINT(7),
	SAVE_STATION(8),
	SPAZER_BARRIER(10)
	;*/
	
	public static final Map<String, ObjectType> byName = new HashMap<>();
	public static final Int2ObjectOpenHashMap<ObjectType> values = new Int2ObjectOpenHashMap<ObjectType>();
	
	public static void load(ObjectElement obj) {
		for(KeyValuePairElement kvp : obj) {
			if (kvp.getValue() instanceof ObjectElement ob2) {
				String objectKey = kvp.getKey();
				ObjectType objectType = new ObjectType(ob2, objectKey);
				if (objectType.id() == -1) {
					System.out.println("Error loading '"+objectKey+"': "+ob2);
					continue;
				}
				
				byName.put(objectKey, objectType);
				values.put(objectType.id(), objectType);
			}
		}
		GUNSHIP = values.get(1);
	}
	
	private final int id;
	private final String name;
	private final String stringId;
	private final String spriteResource;
	
	private ObjectType(ObjectElement obj, String objectKey) {
		this.id = obj.getPrimitive("id").asInt().orElse(-1);
		this.name = obj.getPrimitive("name").orElse("Unknown");
		this.stringId = objectKey;
		this.spriteResource = obj.getPrimitive("sprite").asString().orElse("");
	}
	
	public int id() { return id; }
	public String name() { return name; }
	public String stringId() { return stringId; }
	public String spriteResource() { return spriteResource; }
	
	@Override
	public String toString() {
		return "{id: "+id+", name: '"+name+"', stringId: '"+stringId+"', spriteResource: '"+spriteResource+"'}";
	}
	
	
	public static @Nullable ObjectType of(int id) {
		return values.getOrDefault(id, null);
	}
	
	public static ObjectType of(ObjectElement obj) {
		int rawType = obj.getPrimitive("type").asInt().orElse(0);
		return ObjectType.of(rawType);
	}
}
