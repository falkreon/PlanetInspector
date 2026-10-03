package blue.endless.pi.enigma.util;

import java.io.BufferedInputStream;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.text.NumberFormat;
import java.time.Duration;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.zip.InflaterInputStream;

import blue.endless.jankson.api.Jankson;
import blue.endless.jankson.api.SyntaxError;
import blue.endless.jankson.api.document.ArrayElement;
import blue.endless.jankson.api.document.ObjectElement;
import blue.endless.jankson.api.document.PrimitiveElement;
import blue.endless.jankson.api.document.ValueElement;
import blue.endless.jankson.api.io.json.JsonWriterOptions;
import blue.endless.pi.enigma.domain.Version;
import blue.endless.pi.enigma.domain.World;
import blue.endless.pi.enigma.domain.WorldMeta;
import blue.endless.pi.enigma.wrapper.RoomInfo;
import blue.endless.pi.enigma.wrapper.ScreenInfo;
import blue.endless.pi.enigma.wrapper.WorldInfo;
import it.unimi.dsi.fastutil.ints.IntBidirectionalIterator;
import it.unimi.dsi.fastutil.ints.IntRBTreeSet;
import it.unimi.dsi.fastutil.ints.IntSortedSet;

public class EnigmaFormat {
	public static final double ORIGINAL_PI_VERSION = 0.775;
	public static final long ORIGINAL_INT_VERSION = 77;
	public static final long CURRENT_VERSION = 81;
	public static final String PI_ID = "planet_inspector";
	
	public static WorldInfo createWorld() {
		throw new RuntimeException("Not yet implemented");
	}
	
	public static final double createTimestamp(ZonedDateTime time) {
		// Planets timestamps are double precision floats representing days (whole and fractional) since 30 Dec 1899.
		ZonedDateTime origin = ZonedDateTime.parse("1899-12-30T00:00:00+00:00");
		Duration delta = Duration.between(origin, time);
		long seconds = delta.toSeconds();
		long days = seconds / 86400;
		long remainder = seconds - (days * 86400);
		
		double result = days + (remainder / 86400.0);
		
		// The game rounds timestamps to 4 places. We make a best effort here.
		NumberFormat fmt = NumberFormat.getNumberInstance();
		fmt.setGroupingUsed(false);
		fmt.setMaximumFractionDigits(5);
		return Double.parseDouble(fmt.format(result));
	}
	
	public ZonedDateTime decodeTimestamp(double timestamp) {
		ZonedDateTime origin = ZonedDateTime.parse("1899-12-30T00:00:00+00:00");
		
		ZonedDateTime justDays = origin.plusDays((long) timestamp);
		double fractionalDays = timestamp - Math.floor(timestamp);
		long seconds = (long) (fractionalDays * 86400);
		
		return justDays.plusSeconds(seconds);
	}
	
	public static WorldInfo load(Path worldFile) throws IOException, SyntaxError {
		ArrayList<byte[]> files = new ArrayList<>();
		ByteArrayOutputStream bytes = new ByteArrayOutputStream();
		try(InputStream in = Files.newInputStream(worldFile, StandardOpenOption.READ)) {
			InflaterInputStream zin = new InflaterInputStream(new BufferedInputStream(in));
			
			// Buffer all world data. This might break for unusually big worlds!
			
			int data = 0;
			while(data != -1) {
				data = zin.read();
				if (data == 0) {
					if (bytes.size() > 0) {
						files.add(bytes.toByteArray());
						bytes.reset();
					}
				} else if (data == -1) {
					break;
				} else {
					bytes.write(data);
				}
			}
			
			if (bytes.size() > 0) {
				files.add(bytes.toByteArray());
			}
			
			// Parse things
			
			if (files.size() != 2) {
				throw new SyntaxError("Expected 2 embedded jsons");
			}
			
			ObjectElement worldMetaObj = Jankson.readJsonObject(new ByteArrayInputStream(files.get(0)));
			
			// Check world version - Try to load absolutely anything within our target range!
			double enigmaVersion = worldMetaObj.getPrimitive("version").asDouble().orElse(-1.0);
			
			if (enigmaVersion > CURRENT_VERSION) {
				throw new SyntaxError("This enigma version is too new (version: "+enigmaVersion+")");
			} else if (enigmaVersion < ORIGINAL_PI_VERSION) {
				throw new SyntaxError("This enigma version is too old (version: "+enigmaVersion+")");
			}
			
			ObjectElement worldObj = Jankson.readJsonObject(new ByteArrayInputStream(files.get(1)));
			
			
			// DEBUG: Save the world and meta json in the same folder as the world was
			String fileName = worldFile.getFileName().toString();
			Path basePath = worldFile.getParent();
			if (fileName.endsWith(".mp_world")) {
				fileName = fileName.substring(0, fileName.length()-9);
			}
			String metaJsonName = fileName + ".meta.json";
			String worldJsonName = fileName + ".json";
			
			BufferedWriter metaWriter = Files.newBufferedWriter(basePath.resolve(metaJsonName));
			Jankson.writeJson(worldMetaObj, metaWriter, JsonWriterOptions.STRICT);
			metaWriter.flush(); metaWriter.close();
			
			BufferedWriter worldWriter = Files.newBufferedWriter(basePath.resolve(worldJsonName));
			Jankson.writeJson(worldObj, worldWriter, JsonWriterOptions.STRICT);
			worldWriter.flush(); worldWriter.close();
			// END DEBUG
			
			
			//World world = new World(worldMetaObj, worldObj);
			
			return WorldInfo.of(worldObj, worldMetaObj);
		}
	}

	/**
	 * Zaps old generation debug log elements, like item pools.
	 * @param world The world to remove Enigma logs for
	 */
	private static void removeEnigmaDebug(WorldInfo world) {
		ObjectElement debugLog = world.json().getObject("GENERATION_DEBUG_LOG");
		debugLog.remove("starting_items");
		debugLog.remove("major_pool");
		debugLog.remove("minor_pools");
		debugLog.remove("core_pool");
		debugLog.remove("placed_items");
		
		//if (!debugLog.containsKey(PI_ID)) {
		//	debugLog.put(PI_ID, new ArrayElement());
		//}
	}
	
	/**
	 * Logs an edit at the current date and time in the generation debug log
	 * @param world The world being edited
	 */
	public static void logEdit(WorldInfo world) {
		//ObjectElement debugLog = world.json().getObject("GENERATION_DEBUG_LOG");
		//if (!debugLog.containsKey(PI_ID)) {
		//	debugLog.put(PI_ID, new ArrayElement());
		//}
		//ArrayElement lines = debugLog.getArray(PI_ID);
		//lines.add(PrimitiveElement.of("Edited on "+LocalDateTime.now().atOffset(ZoneOffset.UTC).format(DateTimeFormatter.RFC_1123_DATE_TIME)));
		
		
	}
	
	private static void fixCrashingRooms(WorldInfo world) {
		for(RoomInfo room : world.rooms()) {
			preventRoomCrashes(room);
		}
	}
	
	public static void preventRoomCrashes(RoomInfo room) {
		for(ScreenInfo screen : room.screens()) {
			ArrayElement enemiesArray = screen.json().getArray("ENEMIES");
			for(ObjectElement obj : enemiesArray.asObjectArray()) {
				obj.computeIfAbsent("level", (it) -> PrimitiveElement.of(0));
				
				if (obj.getPrimitive("type").orElse(-1) == 39) {
					// Seen in the wild: Instances of Spore Spawn missing a "color" attribute
					obj.computeIfAbsent("color", (it) -> PrimitiveElement.of(0));
				}
			}
			
			screen.json().computeIfAbsent("DECOR", (it) -> new ArrayElement());
		}
		
		ObjectElement general = room.json().getObject("GENERAL");
		general.computeIfAbsent("sector", (it) -> PrimitiveElement.of(0));
		general.computeIfAbsent("bg_color", (it) -> PrimitiveElement.of(64));
		general.computeIfAbsent("vision_limit", (it) -> PrimitiveElement.of(0));
		general.computeIfAbsent("gravity_multiplier", (it) -> PrimitiveElement.of(1));
		general.computeIfAbsent("threat", (it) -> PrimitiveElement.of(0));
		general.computeIfAbsent("powered", (it) -> PrimitiveElement.of(1));
		general.computeIfAbsent("no_floor", (it) -> PrimitiveElement.of(0));
		general.computeIfAbsent("darkness", (it) -> PrimitiveElement.of(0));
		general.computeIfAbsent("spike_level", (it) -> PrimitiveElement.of(1));
		general.computeIfAbsent("bgm", (it) -> PrimitiveElement.of(""));
		general.computeIfAbsent("magnet", (it) -> {
			ObjectElement magnet = new ObjectElement();
			magnet.put("on_palette", PrimitiveElement.of(21));
			magnet.put("off_palette", PrimitiveElement.of(0));
			magnet.put("use_palettes", PrimitiveElement.of(0));
			magnet.put("use_shader", PrimitiveElement.of(0));
			return magnet;
		});
		
		room.json().computeIfAbsent("HAZARD", (it) -> new ObjectElement());
		ObjectElement hazard = room.json().getObject("HAZARD");
		//hazard.computeIfAbsent("type", (it) -> PrimitiveElement.of(0));
		if (hazard.getPrimitive("type").asInt().orElse(-1) < 0) hazard.put("type", PrimitiveElement.of(0));
		hazard.computeIfAbsent("tanks", (it) -> PrimitiveElement.of(1));
		hazard.computeIfAbsent("block_type", (it) -> PrimitiveElement.of(1));
		hazard.computeIfAbsent("style", (it) -> PrimitiveElement.of(0));
		hazard.computeIfAbsent("set", (it) -> PrimitiveElement.of(0));
		hazard.computeIfAbsent("color", (it) -> PrimitiveElement.of(22));
		
	}
	
	/**
	 * Goes through all rooms and ensures that `GENERAL.gate_bosses` contains one and only one of the boss for each
	 * boss room present.
	 * @param world The world to search and regen gate bosses for.
	 */
	private static void regenGateBosses(WorldInfo world) {
		IntSortedSet gateBosses = new IntRBTreeSet();
		for (RoomInfo room : world.rooms()) {
			if (room.isBossRoom()) {
				if (room.bossId() == EnemyType.MOTHER_BRAIN_ID) continue;
				gateBosses.add(room.bossId());
			}
		}
		ArrayElement gateBossArray = new ArrayElement();
		IntBidirectionalIterator i = gateBosses.iterator();
		while(i.hasNext()) {
			gateBossArray.add(PrimitiveElement.of(i.nextInt()));
		}
		ObjectElement general = world.json().getObject("GENERAL");
		general.put("gate_bosses", gateBossArray);
	}
	
	private static void fixProgressionItemAreas(WorldInfo world) {
		if (world.rooms().size() == 0) {
			// The best thing we can do is nuke the progression log, it will only cause us grief like this.
			world.json().put("PROGRESSION_LOG", new ArrayElement());
			return;
		}
		
		for(ValueElement val : world.json().getArray("PROGRESSION_LOG")) {
			if (val instanceof ObjectElement entry) {
				int roomId = entry.getPrimitive("room_id").asInt().orElse(-1);
				if (roomId < 0 || roomId >= world.rooms().size()) {
					// This will definitely crash enigma
					entry.put("room_id", PrimitiveElement.of(0));
					entry.put("area", PrimitiveElement.of(world.rooms().get(0).area()));
				} else {
					RoomInfo room = world.rooms().get(roomId);
					entry.put("area", PrimitiveElement.of(room.area()));
				}
			}
		}
	}
	
	/**
	 * Make sure the world has an external_editor key with editor_tool set to Planet Inspector. Also initialize the
	 * authors and tags arrays if they're not found.
	 * @param world The world to mark as externally edited.
	 */
	private static void markAsEdited(WorldInfo world) {
		world.metadata().modified = true;
		world.metadata().externalEditor = PI_ID;
		//world.metaJson().put("modified", PrimitiveElement.of(true));
		//world.metaJson().put("modification_tool", PrimitiveElement.of(PI_ID));
	}
	
	/**
	 * Fix spawn points. Make sure there's one spawn point at each elevator and gunship
	 * @param world
	 */
	/*
	private static void fixSpawnPoints(WorldInfo world) {
		for(RoomInfo room : world.rooms()) {
			for(ScreenInfo screen : room.screens()) {
				ArrayElement elevators = screen.json().getArray("ELEVATORS");
				if (!elevators.isEmpty()) {
					if (elevators.get(0) instanceof ObjectElement obj) {
						int objX = obj.getPrimitive("x").asInt().orElse(0);
						int objY = obj.getPrimitive("y").asInt().orElse(0);
						int areaId = room.area();
						AreaInfo area = (areaId >= 0 && areaId < world.areas().size()) ? world.areas().get(areaId) : world.areas().get(0);
						SpawnPoint elevatorSpawn = new SpawnPoint(area.name(), SpawnPoint.Type.RESPAWN, world, room, screen, objX, objY - 15);
					}
				}
			}
		}
	}*/
	
	private static void updateMetadata(WorldInfo world) {
		WorldMeta.Stats stats = world.metadata().stats;
		stats.bosses = 0;
		stats.screens = 0;
		stats.rooms = 0;
		stats.areas = world.areas().size() - 2;
		stats.items = 0;
		
		for (RoomInfo room : world.rooms()) {
			if (room.isBossRoom()) stats.bosses++;
			stats.rooms++;
			
			for(ScreenInfo screen : room.screens()) {
				stats.screens++;
				
				for(ObjectElement obj : screen.json().getArray("OBJECTS").asObjectArray()) {
					switch(obj.getPrimitive("type").asInt().orElse(-1)) {
						case 0 -> stats.items++;
					}
				}
			}
		}
		
		/*
		ObjectElement stats = world.metaJson().getObject("stats");
		stats.put("bosses", PrimitiveElement.of(bosses));
		stats.put("screens", PrimitiveElement.of(screens));
		stats.put("rooms", PrimitiveElement.of(rooms));
		stats.put("areas", PrimitiveElement.of(areas));
		stats.put("items", PrimitiveElement.of(items));
		stats.put("sectors", PrimitiveElement.of(1));
		stats.computeIfAbsent("tags_used", (it) -> new ArrayElement());
		stats.computeIfAbsent("designers_used", (it) -> new ArrayElement());
		*/
		
		//world.metaJson().put("stats", stats); // If it didn't exist, put it back.
		
		
		
		//Grab stats and include in the preview
		
		String statusText = "";
		statusText += "AREAS:  " + stats.areas + ";";
		statusText += "ROOMS:  " + stats.rooms + ";";
		statusText += "SCREENS:" + stats.screens + ";";
		statusText += "BOSSES: " + stats.bosses + ";";
		statusText += "ITEMS:  " + stats.items + ";";
		
		//world.metaJson().put("description", PrimitiveElement.of(statusText));
		world.metadata().description = statusText;
	}
	
	/**
	 * Goes over the sector and makes sure it's appropriately sized for its contents. This is incredibly important to
	 * make sure Planets reserves enough space for explored map tiles. Not doing this will crash the game!
	 * 
	 * <p>
	 * Also ensures that all rooms are marked with sector zero, fixing a second crash.
	 * 
	 * @param world The world to fix the sector for
	 */
	private static void fixSector(WorldInfo world) {
		int maxX = 0;
		int maxY = 0;
		for(RoomInfo room : world.rooms()) {
			for(ScreenInfo screen : room.screens()) {
				maxX = Math.max(maxX, screen.x());
				maxY = Math.max(maxY, screen.y());
			}
			room.json().getObject("GENERAL").put("sector", PrimitiveElement.of(0));
		}
		
		ArrayElement sectors = world.json().getArray("SECTORS");
		if (sectors.isEmpty()) sectors.add(new ObjectElement());
		ObjectElement sector = sectors.getObject(0);
		sector.put("x", PrimitiveElement.of(0));
		sector.put("y", PrimitiveElement.of(0));
		sector.put("width", PrimitiveElement.of(maxX + 1));
		sector.put("height", PrimitiveElement.of(maxY + 1));
		sector.put("name", PrimitiveElement.of(""));
		sector.put("elevator_screens", new ArrayElement()); // TODO: Regenerate elevator tracks
		ArrayElement connections = new ArrayElement();
		connections.add(PrimitiveElement.of("1"));
		sector.put("connections", connections);
	}
	
	/**
	 * Compute status text and include it in the world description.
	 * @param world the world to fix stats for.
	 */
	public static void createStatsText(WorldInfo world) {
		
	}
	
	/**
	 * Does last-minute edits to the world json and metaJson to ensure consistency. Fills in stats, double-checks gate
	 * bosses, marks the world as edited, etc.
	 * @param world the world to prepare for saving
	 */
	public static void prepareForSave(WorldInfo world) {
		world.metadata().version = new Version(CURRENT_VERSION);
		//world.metaJson().put("version", PrimitiveElement.of(CURRENT_VERSION));
		
		removeEnigmaDebug(world);
		logEdit(world);
		fixCrashingRooms(world);
		
		regenGateBosses(world);
		fixProgressionItemAreas(world);
		
		updateMetadata(world);
		
		
		markAsEdited(world);
		
		fixSector(world);
	}
}
