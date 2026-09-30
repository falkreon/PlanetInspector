package blue.endless.pi.enigma;

import java.time.ZonedDateTime;
import java.util.HashSet;
import java.util.Set;

import blue.endless.jankson.api.annotation.Deserializer;
import blue.endless.jankson.api.annotation.SerializedName;
import blue.endless.jankson.api.document.ObjectElement;
import blue.endless.pi.enigma.util.EnigmaFormat;

public class WorldMeta {
	/** Generally referred to in patch notes as a "world format version". */
	public Version version = new Version(EnigmaFormat.CURRENT_VERSION);
	
	/** The world Id. */
	public long id = 0L;
	
	/** The monotonic version of this World. Affects whether importing a world will overwrite another one with the same id. */
	public long world_version = World.createWorldId();
	
	/** The author of the World - if "handcrafted" and "modified" are both false, set this to the empty string. */
	public String author = "";
	/** Short name of the world, without the "DEMO number" */
	public String name = "UNTITLED";
	/** Full name of the world, including the "DEMO number" */
	@SerializedName("name_full")
	public String nameFull = "UNTITLED - DEMO 0";
	
	/** The plot text crawl. Currently PI overwrites this with stats when you save a world. */
	public String description = "";
	
	/** True if this world was created from scratch by a tool like World Studio or PI. */
	public boolean handcrafted = true;
	
	/** True if this world was modified by an external tool. Always true for anything saved by PI */
	public boolean modified = true;
	
	@SerializedName("external_editor")
	public String externalEditor = EnigmaFormat.PI_ID;
	
	/** Timestamp for when the world was created */
	@SerializedName("creation_date")
	public double creationDate = EnigmaFormat.createTimestamp(ZonedDateTime.now());
	
	/** Timestamp for when the world was last saved */
	@SerializedName("save_date")
	public double saveDate = EnigmaFormat.createTimestamp(ZonedDateTime.now());
	
	@SerializedName("connect_compatible")
	public boolean connectCompatible = true;
	
	@SerializedName("versus_compatible")
	public boolean versusCompatible = true;
	
	/** Should we warn users about experimental content, like arm blades or spin boost? */
	@SerializedName("experimental_warning")
	public boolean experimentalWarning = false;
	
	/** Should we warn users about excessive challenge? This is typically driven by tags */
	@SerializedName("challenge_warning")
	public boolean challengeWarning = false;
	
	/** Stats block. REQUIRED! */
	public Stats stats = new Stats();
	
	public static class Stats {
		// === Numeric stats
		public long sectors = 1;
		public long areas = 2;
		public long rooms = 0;
		public long screens = 0;
		public long bosses = 0;
		public long items = 0;
		
		// === Enumerated items
		
		/** 0: off, 1: on - copy of a flag in world */
		@SerializedName("ship_hints")
		public int shipHints = 0;
		
		@SerializedName("room_hints")
		public boolean roomHints = false;
		
		/** 0: off, 1: allowed, 2: forced - copy of a flag on world */
		@SerializedName("hazard_runs")
		public int hazardRuns = 0;
		
		public int layout = 0;
		public int progression = 0;
		public int size = 0;
		public int style = 0;
		
		public int focus = -1; // bitmask probably - -1 is a good sentinel because it will always mean "focus everything"
		
		// === Flags
		
		public boolean cores = true;
		
		// === Sets
		
		@SerializedName("tags_used")
		public Set<String> tagsUsed = new HashSet<>();
		
		@SerializedName("designers_used")
		public Set<String> designersUsed = new HashSet<>();
		
		/** Not including hidden stuff like power suit / beam / visor */
		@SerializedName("starting_items")
		public Set<Integer> startingItems = new HashSet<>();
	}
	
	@Deserializer
	public static WorldMeta fromJson(ObjectElement obj) {
		WorldMeta result = new WorldMeta();
		result.version = Version.of(obj.getPrimitive("version"));
		
		
		return result;
	}
}

