package blue.endless.pi.enigma;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import blue.endless.jankson.api.document.ArrayElement;
import blue.endless.jankson.api.document.ObjectElement;
import blue.endless.jankson.api.document.PrimitiveElement;
import blue.endless.pi.enigma.wrapper.AreaInfo;
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
		lines.add(PrimitiveElement.of("Edited on "+LocalDateTime.now().format(DateTimeFormatter.RFC_1123_DATE_TIME)));
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
	}
	
	/**
	 * Goes over the sector and makes sure it's appropriately sized for its contents. This is incredibly important to
	 * make sure Planets reserves enough space for explored map tiles. Not doing this will crash the game!
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
		
		regenGateBosses(world);
		
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
