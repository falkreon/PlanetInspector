package blue.endless.pi.room;

import blue.endless.jankson.api.annotation.SerializedName;

public record Hazard(
		int tanks,
		@SerializedName("block_type")
		int blockType,
		int set,
		int type,
		int color,
		int style
		) {
	
}
