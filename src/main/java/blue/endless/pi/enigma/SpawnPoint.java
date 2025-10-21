package blue.endless.pi.enigma;

import org.jetbrains.annotations.Nullable;

import blue.endless.jankson.api.document.ObjectElement;
import blue.endless.jankson.api.document.PrimitiveElement;
import blue.endless.pi.enigma.wrapper.PlacedScreen;
import blue.endless.pi.enigma.wrapper.RoomInfo;
import blue.endless.pi.enigma.wrapper.ScreenInfo;
import blue.endless.pi.enigma.wrapper.WorldInfo;

public class SpawnPoint {
	
	private String name;
	private Type type;
	private WorldInfo world;
	private RoomInfo room;
	private ScreenInfo screen;
	private int x;
	private int y;
	
	public SpawnPoint(WorldInfo world, ObjectElement obj) {
		this.world = world;
		
		this.name = obj.getPrimitive("name").asString().orElse("");
		this.type = Type.of(obj.getPrimitive("room_id").asInt().orElse(0));
		if (this.type == null) this.type = Type.INITIAL_SPAWN;
		
		int roomId = obj.getPrimitive("room_id").asInt().orElse(-1);
		int screenN = obj.getPrimitive("screen_n").asInt().orElse(0);
		if (roomId >= 0 && roomId < world.rooms().size()) {
			this.room = world.rooms().get(roomId);
			if (screenN >= 0 && screenN < this.room.screens().size()) {
				this.screen = this.room.screens().get(screenN);
			} else {
				this.screen = null;
			}
		} else {
			this.room = null;
			this.screen = null;
		}
		
		this.x = obj.getPrimitive("x").asInt().orElse(0);
		this.y = obj.getPrimitive("y").asInt().orElse(0);
	}
	
	public SpawnPoint(String name, Type type, WorldInfo world, RoomInfo room, ScreenInfo screen, int x, int y) {
		this.name = name;
		this.type = type;
		this.world = world;
		this.room = room;
		this.screen = screen;
		this.x = x;
		this.y = y;
	}
	
	public SpawnPoint(String name, Type type, WorldInfo world, PlacedScreen screen, int x, int y) {
		this.name = name;
		this.type = type;
		this.world = world;
		this.room = screen.room();
		this.screen = screen.screen();
		this.x = x;
		this.y = y;
	}
	
	public boolean isValid() {
		return world != null && room != null && screen != null;
	}
	
	public ObjectElement toJson() {
		ObjectElement result = new ObjectElement();
		result.put("name", PrimitiveElement.of(name));
		result.put("area", PrimitiveElement.of(room.area()));
		result.put("type", PrimitiveElement.of(1)); // Respawn Point
		result.put("room_id", PrimitiveElement.of(world.indexOf(room)));
		if (room != null && screen != null) {
			result.put("screen_n", PrimitiveElement.of(room.indexOf(screen)));
			result.put("world_x", PrimitiveElement.of(screen.x()));
			result.put("world_y", PrimitiveElement.of(screen.y()));
		} else {
			result.put("screen_n", PrimitiveElement.of(0));
			result.put("world_x", PrimitiveElement.of(0));
			result.put("world_y", PrimitiveElement.of(0));
		}
		
		result.put("x", PrimitiveElement.of(this.x));
		result.put("y", PrimitiveElement.of(this.y));
		return result;
	}
	
	public static enum Type {
		INITIAL_SPAWN(0),
		RESPAWN(1)
		;
		
		final int value;
		
		Type(int value) {
			this.value = value;
		}
		
		@Nullable
		public static Type of(int value) {
			for(Type type : values()) {
				if (type.value == value) return type;
			}
			return null;
		}
	}
}
