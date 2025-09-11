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
		@SerializedName("PATHING")
		ArrayList<PathNode> pathing,
		@SerializedName("PALETTES")
		ArrayList<PaletteEntry> palettes,
		@SerializedName("SCREENS")
		ArrayList<Screen> screens,
		
		@SerializedName("EVENTS")
		ArrayList<Event> events
		) {
	
}
