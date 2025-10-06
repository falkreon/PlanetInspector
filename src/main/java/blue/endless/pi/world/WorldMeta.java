package blue.endless.pi.world;

import blue.endless.jankson.api.annotation.SerializedName;

public record WorldMeta(
		double id,
		@SerializedName("world_version")
		float worldVersion,
		float version,
		String description,
		WorldStats stats,
		String name,
		@SerializedName("name_full")
		String fullName,
		float key
		) {
	
}
