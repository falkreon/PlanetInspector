package blue.endless.pi.room;

import blue.endless.jankson.api.annotation.SerializedName;

public record Elevator(
		int id,
		int x,
		int y,
		int dir,
		@SerializedName("traverse_rooms")
		int traverseRooms,
		@SerializedName("path_point")
		int pathPoint
		) {
}
