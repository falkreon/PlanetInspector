package blue.endless.pi.room;

import blue.endless.jankson.api.annotation.SerializedName;

public record ScreenObject(
		int id,
		int x,
		int y,
		@SerializedName("path_point")
		int pathPoint,
		int type,
		int item
		) {
}
