package blue.endless.pi.enigma;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

import blue.endless.jankson.api.document.ArrayElement;
import blue.endless.jankson.api.document.ObjectElement;
import blue.endless.jankson.api.document.PrimitiveElement;
import blue.endless.jankson.api.document.ValueElement;
import blue.endless.pi.enigma.wrapper.RoomInfo;
import blue.endless.pi.enigma.wrapper.ScreenInfo;
import blue.endless.pi.enigma.wrapper.WorldInfo;
import it.unimi.dsi.fastutil.ints.IntBidirectionalIterator;
import it.unimi.dsi.fastutil.ints.IntRBTreeSet;
import it.unimi.dsi.fastutil.ints.IntSortedSet;

public class EnigmaFormat {
	public static final double CURRENT_VERSION = 0.775;
	public static final String PI_ID = "planet_inspector";
	
	public static WorldInfo createWorld() {
		throw new RuntimeException("Not yet implemented");
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
		
		if (!debugLog.containsKey(PI_ID)) {
			debugLog.put(PI_ID, new ArrayElement());
		}
	}
	
	/**
	 * Logs an edit at the current date and time in the generation debug log
	 * @param world The world being edited
	 */
	public static void logEdit(WorldInfo world) {
		ObjectElement debugLog = world.json().getObject("GENERATION_DEBUG_LOG");
		if (!debugLog.containsKey(PI_ID)) {
			debugLog.put(PI_ID, new ArrayElement());
		}
		ArrayElement lines = debugLog.getArray(PI_ID);
		lines.add(PrimitiveElement.of("Edited on "+LocalDateTime.now().atOffset(ZoneOffset.UTC).format(DateTimeFormatter.RFC_1123_DATE_TIME)));
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
		ObjectElement toolObj = world.metaJson().getObject("external_editor");
		if (toolObj.isEmpty()) {
			// If it's empty or fake, make sure it exists now as part of the meta json
			world.metaJson().put("external_editor", toolObj);
		}
		toolObj.computeIfAbsent("authors", (it) -> new ArrayElement());
		toolObj.computeIfAbsent("tags", (it) -> new ArrayElement());
		toolObj.put("editor_tool", PrimitiveElement.of(PI_ID));
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
		int bosses = 0;
		int screens = 0;
		int rooms = 0;
		int areas = world.areas().size() - 2;
		int items = 0;
		
		for (RoomInfo room : world.rooms()) {
			if (room.isBossRoom()) bosses++;
			rooms++;
			
			for(ScreenInfo screen : room.screens()) {
				screens++;
				
				for(ObjectElement obj : screen.json().getArray("objects").asObjectArray()) {
					switch(obj.getPrimitive("type").asInt().orElse(-1)) {
						case 0 -> items++;
					}
				}
			}
		}
		
		ObjectElement stats = world.metaJson().getObject("stats");
		stats.put("bosses", PrimitiveElement.of(bosses));
		stats.put("screens", PrimitiveElement.of(screens));
		stats.put("rooms", PrimitiveElement.of(rooms));
		stats.put("areas", PrimitiveElement.of(areas));
		stats.put("items", PrimitiveElement.of(items));
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
	 * Does last-minute edits to the world json and metaJson to ensure consistency. Fills in stats, double-checks gate
	 * bosses, marks the world as edited, etc.
	 * @param world the world to prepare for saving
	 */
	public static void prepareForSave(WorldInfo world) {
		
		removeEnigmaDebug(world);
		logEdit(world);
		fixCrashingRooms(world);
		
		regenGateBosses(world);
		fixProgressionItemAreas(world);
		
		updateMetadata(world);
		
		// Grab stats and include in the preview
		ObjectElement stats = world.metaJson().getObject("stats");
		int roomCount = stats.getPrimitive("rooms").asInt().orElse(0);
		int bossCount = stats.getPrimitive("bosses").asInt().orElse(0);
		int areaCount = stats.getPrimitive("areas").asInt().orElse(0);
		world.metaJson().put("description", PrimitiveElement.of("MODIFIED WORLD, USE WITH CARE; ROOMS: "+roomCount+"; AREAS: "+areaCount+"; BOSSES: "+bossCount));
		
		
		markAsEdited(world);
		
		fixSector(world);
	}
}
