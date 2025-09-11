package blue.endless.pi.room;

import java.util.ArrayList;

import blue.endless.jankson.api.annotation.SerializedName;
import blue.endless.jankson.api.document.ValueElement;

public record Screen(
		// Haven't seen enough of the schema yet
		@SerializedName("DECOR")
		ArrayList<ValueElement> decor,
		@SerializedName("BLOCKS")
		ArrayList<Block> blocks,
		@SerializedName("ELEVATORS")
		ArrayList<Elevator> elevators,
		@SerializedName("TUBES")
		ArrayList<ValueElement> tubes,
		@SerializedName("OBJECTS")
		ArrayList<ScreenObject> objects,
		@SerializedName("LIQUIDS")
		ArrayList<ValueElement> liquids,
		
		
		// Seen enough schema, we kind of know what's going on here
		@SerializedName("MAP")
		Map map,
		@SerializedName("ENEMIES")
		ArrayList<Enemy> enemies,
		@SerializedName("DOORS")
		ArrayList<Door> doors,
		@SerializedName("SCROLLS")
		long[] scrolls,
		
		@SerializedName("TILES")
		long[][][] tiles,
		
		int x,
		int y
		) {
	
}
