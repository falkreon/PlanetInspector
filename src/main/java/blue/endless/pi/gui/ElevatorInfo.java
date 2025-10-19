package blue.endless.pi.gui;

import blue.endless.jankson.api.document.ObjectElement;
import blue.endless.jankson.api.document.PrimitiveElement;
import blue.endless.pi.Direction;

public record ElevatorInfo(RoomInfo room, ScreenInfo screen, ObjectElement json) {
	public int id() {
		return json.getPrimitive("id").asInt().orElse(0);
	}
	
	public Direction direction() {
		return Direction.valueOf(json.getPrimitive("dir").asInt().orElse(0));
	}
	
	public int destRoomId() {
		return json.getPrimitive("destRm").asInt().orElse(-1);
	}
	
	public int destId() {
		return json.getPrimitive("dest_id").asInt().orElse(0);
	}
	
	public void setDestination(int roomId, int elevatorId) {
		json.put("dest_rm", PrimitiveElement.of(roomId));
		json.put("dest_id", PrimitiveElement.of(elevatorId));
	}
	
	public void clearDestination() {
		json.put("dest_rm", PrimitiveElement.of(-2));
		json.put("dest_id", PrimitiveElement.of(0));
	}
}
