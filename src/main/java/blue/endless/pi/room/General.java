package blue.endless.pi.room;

import blue.endless.jankson.api.annotation.SerializedName;

public record General(
		int darkness,
		int threat,
		String bgm,
		@SerializedName("vision_limit")
		int visionLimit,
		int sector,
		int area,
		String[] areas,
		String designer,
		@SerializedName("spike_level")
		int spikeLevel,
		String name,
		@SerializedName("bg_color")
		int bgColor,
		boolean powered,
		@SerializedName("gravity_multiplier")
		int gravityMultiplier,
		@SerializedName("no_floor")
		boolean noFloor,
		 Magnet magnet,
		boolean[] focus,
		String[] tags
		) {
	
	
	public static record Magnet(
			@SerializedName("use_palettes")
			boolean usePalettes,
			@SerializedName("on_palette")
			int onPalette,
			@SerializedName("off_palette")
			int offPalette,
			@SerializedName("use_shader")
			boolean useShader
			) {
	}
}
