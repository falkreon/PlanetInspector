package blue.endless.pi.enigma.wrapper;

import java.util.Optional;

import blue.endless.jankson.api.document.ArrayElement;
import blue.endless.jankson.api.document.BooleanElement;
import blue.endless.jankson.api.document.DoubleElement;
import blue.endless.jankson.api.document.LongElement;
import blue.endless.jankson.api.document.ObjectElement;
import blue.endless.jankson.api.document.PrimitiveElement;
import blue.endless.pi.enigma.Direction;
import blue.endless.pi.enigma.DoorType;

public record DoorInfo(WorldInfo world, RoomInfo room, ScreenInfo screen, ObjectElement json) {
	public int id() {
		return json.getPrimitive("id").asInt().orElse(0);
	}
	
	public int x() {
		return json.getPrimitive("x").asInt().orElse(0);
	}
	
	public int y() {
		return json.getPrimitive("y").asInt().orElse(0);
	}
	
	public int skin() {
		return json.getPrimitive("skin").asInt().orElse(0);
	}
	
	public Direction direction() {
		return Direction.of(json.getPrimitive("pos").asInt().orElse(1));
	}
	
	public boolean clearOnUse() {
		return switch(json.getPrimitive("clear_on_use")) {
			case BooleanElement b -> b.asBoolean().orElse(false);
			case LongElement l -> l.asLong().orElse(0L) != 0;
			case DoubleElement d -> d.asDouble().orElse(0.0) > 0.001;
			case null, default -> false;
		};
	}
	
	public DoorType type() {
		/*
		PrimitiveElement prim = json.getPrimitive("type");
		if (prim.isNull()) throw new NullPointerException("DoorType was not present!");
		int rawId = prim.asInt().orElse(-8);
		if (rawId<0 || rawId >= DoorType.values().length) System.out.println("Unknown DoorType "+rawId);
		if (rawId == 0) System.out.println("ZERO DOOR");*/
		return DoorType.of(json.getPrimitive("type").asInt().orElse(0));
	}
	
	public void setType(DoorType type) {
		json.put("type", PrimitiveElement.of(type.value()));
		int dir = json.getPrimitive("pos").asInt().orElse(1);
		screen.json().getObject("MAP").getArray("doors").set(dir, PrimitiveElement.of(type.value()));
	}
	
	public void fixMapType() {
		int dir = json.getPrimitive("pos").asInt().orElse(1);
		int type = json.getPrimitive("type").asInt().orElse(0);
		screen.json().getObject("MAP").getArray("doors").set(dir, PrimitiveElement.of(type));
	}
	
	public Optional<DoorInfo> getDestination() {
		int destRoomId = json.getPrimitive("dest_rm").asInt().orElse(-1);
		int destId = json.getPrimitive("dest_id").asInt().orElse(0);
		
		if (destRoomId < 0 || destRoomId >= world.rooms().size()) return Optional.empty();
		RoomInfo destRoom = world.rooms().get(destRoomId);
		for(ScreenInfo destScreen : destRoom.screens()) {
			ArrayElement doors = destScreen.json().getArray("DOORS");
			for(int i=0; i<doors.size(); i++) {
				ObjectElement door = doors.getObject(i);
				int doorId = door.getPrimitive("id").asInt().orElse(-1);
				if (doorId == destId) return Optional.of(new DoorInfo(world(), destRoom, destScreen, door));
			}
		}
		
		return Optional.empty();
	}
	
	public void setDestination(DoorInfo destination) {
		int destRoom = destination.world().indexOf(destination.room());
		int destId = destination.id();
		
		json.put("dest_rm", PrimitiveElement.of(destRoom));
		json.put("dest_id", PrimitiveElement.of(destId));
	}
	
	public void clearDestination() {
		json.put("dest_rm", PrimitiveElement.of(-1));
		json.put("dest_id", PrimitiveElement.of(0));
	}
}
