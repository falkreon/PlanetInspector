package blue.endless.pi.room;

import java.util.ArrayList;

import blue.endless.jankson.api.annotation.SerializedName;

public record Room(
		@SerializedName("GENERAL")
		General general,
		@SerializedName("HAZARD")
		Hazard hazard,
		@SerializedName("META")
		Meta meta,
		@SerializedName("MAP")
		Map map,
		@SerializedName("PATHING")
		ArrayList<PathNode> pathing,
		@SerializedName("PALETTES")
		ArrayList<PaletteEntry> palettes
		
		
		//ArrayList<Screen> screens
		
		// TODO: EVENTS (empty)
		// TODO: SCREENS (most of the remaining data!
		
		) {
	
}
