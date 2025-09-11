package blue.endless.pi.room;

import blue.endless.jankson.api.annotation.SerializedName;

public record Door(
		int id,
		int x,
		int y,
		int pos,
		int type,
		int skin,
		@SerializedName("path_point")
		int pathPoint,
		@SerializedName("clear_on_use")
		boolean clearOnUse
		) {
}
