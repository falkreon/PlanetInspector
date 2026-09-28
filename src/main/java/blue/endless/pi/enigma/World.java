package blue.endless.pi.enigma;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

import blue.endless.jankson.api.document.ArrayElement;
import blue.endless.jankson.api.document.ObjectElement;
import blue.endless.jankson.api.document.PrimitiveElement;
import blue.endless.pi.enigma.util.Area;
import blue.endless.pi.enigma.util.EnigmaFormat;

public class World {
	private ObjectElement metadata;
	
	private ObjectElement general;
	private ObjectElement gunship;
	private ObjectElement samus;
	private ObjectElement rules;
	private ObjectElement objectives;
	private ObjectElement generationDebugLog;
	
	private int[] liquidsDamage = {
		0, 1, 0, 2, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0
	};
	
	private List<ObjectElement> sectors = new ArrayList<>();
	private List<Area> areas = new ArrayList<>();
	private List<ObjectElement> rooms = new ArrayList<>();
	private List<ObjectElement> hazards = new ArrayList<>();
	private List<ObjectElement> itemData = new ArrayList<>();
	private List<ArrayElement> enemyData = new ArrayList<>();
	private List<ObjectElement> progressionLog = new ArrayList<>();
	
	public World() {
		metadata = new ObjectElement();
		metadata.put("version", PrimitiveElement.of(EnigmaFormat.CURRENT_VERSION));
		makeUnique();
		metadata.put("author", PrimitiveElement.of(""));
		metadata.put("name", PrimitiveElement.of("UNTITLED"));
		metadata.put("name_full", PrimitiveElement.of("UNTITLED - DEMO 0"));
		metadata.put("world_version", PrimitiveElement.of(0));
		metadata.put("handcrafted", PrimitiveElement.of(true));
		metadata.put("modified", PrimitiveElement.of(true));
		double now = EnigmaFormat.createTimestamp(ZonedDateTime.now());
		metadata.put("creation_date", PrimitiveElement.of(now));
		metadata.put("save_date", PrimitiveElement.of(now));
		metadata.put("connect_compatible", PrimitiveElement.of(true));
		metadata.put("versus_compatible", PrimitiveElement.of(true));
		
		ObjectElement stats = new ObjectElement();
		
		stats.put("tags_used", new ArrayElement());
		stats.put("designers_used", new ArrayElement());
		
		stats.put("ship_hints", PrimitiveElement.of(true));  //?
		stats.put("hazard_runs", PrimitiveElement.of(true)); //?
		
		stats.put("cores", PrimitiveElement.of(true));
		
		stats.put("sectors", PrimitiveElement.of(1));
		stats.put("areas", PrimitiveElement.of(2));
		stats.put("bosses", PrimitiveElement.of(0));
		stats.put("rooms", PrimitiveElement.of(0));
		stats.put("screens", PrimitiveElement.of(0));
		stats.put("items", PrimitiveElement.of(0));
		
		stats.put("focus", PrimitiveElement.of(-1));
		stats.put("size", PrimitiveElement.of(1));
		stats.put("progression", PrimitiveElement.of(1));
		stats.put("style", PrimitiveElement.of(1));
		
		stats.put("room_hints", PrimitiveElement.of(false));
		
		
	}
	
	public World(ObjectElement meta, ObjectElement main) {
		
	}
	
	/**
	 * Generates a new, unique Id for this world
	 */
	public static long createWorldId() {
		long seconds = System.currentTimeMillis() / 1_000L; // Seconds since midnight, january 1, 1970
		return seconds * 1000L + (long) (Math.random() * 999);
	}
	
	public void makeUnique() {
		metadata.put("id", PrimitiveElement.of(createWorldId()));
	}
}
