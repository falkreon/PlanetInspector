package blue.endless.pi.room;

import blue.endless.jankson.api.annotation.SerializedName;

public record Meta(
		int boss,
		long id,
		@SerializedName("content_version")
		int contentVersion,
		boolean landsite,
		boolean playable,
		@SerializedName("boss_room")
		int bossRoom,
		@SerializedName("last_patch")
		float lastPatch,
		@SerializedName("last_save")
		float lastSave
		) {
	
}
