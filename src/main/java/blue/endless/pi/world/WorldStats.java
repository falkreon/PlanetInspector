package blue.endless.pi.world;

import blue.endless.jankson.api.annotation.SerializedName;

public record WorldStats(
		@SerializedName("starting_items")
		int[] startingItems,
		int bosses,
		int sectors,
		int size,
		int focus,
		int screens,
		int layout,
		int rooms,
		int areas,
		@SerializedName("ship_hints")
		int shipHints,
		int progression,
		@SerializedName("hazard_runs")
		int hazardRuns,
		int cores,
		int items,
		int style
		) {
}
