package blue.endless.pi.enigma.wrapper;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.zip.Deflater;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.InflaterInputStream;

import blue.endless.jankson.api.Jankson;
import blue.endless.jankson.api.SyntaxError;
import blue.endless.jankson.api.document.ArrayElement;
import blue.endless.jankson.api.document.ObjectElement;
import blue.endless.jankson.api.document.PrimitiveElement;
import blue.endless.jankson.api.document.ValueElement;
import blue.endless.jankson.api.io.json.JsonWriterOptions;
import blue.endless.pi.enigma.EnigmaFormat;
import blue.endless.pi.gui.MinimapBaseShape;

public record WorldInfo(ObjectElement json, ObjectElement metaJson, List<RoomInfo> rooms, List<AreaInfo> areas) {
	
	public static WorldInfo of(ObjectElement json, ObjectElement metaJson) {
		ArrayList<RoomInfo> rooms = new ArrayList<>();
		ArrayElement roomsArray = json.getArray("ROOMS");
		for(ValueElement val : roomsArray) {
			if (val instanceof ObjectElement obj) {
				rooms.add(RoomInfo.of(obj));
			}
		}
		
		ArrayList<AreaInfo> areas = new ArrayList<>();
		ArrayElement areasArray = json.getArray("AREAS");
		for(ValueElement val : areasArray) {
			if (val instanceof ObjectElement obj) {
				areas.add(new AreaInfo(obj));
			}
		}
		
		return new WorldInfo(json, metaJson, rooms, areas);
	}
	
	/**
	 * Gets the room_id of the specified room
	 * @param room The room to look up
	 * @return the id for this room, or -1 if the room is orphaned from this world.
	 */
	public int indexOf(RoomInfo room) {
		return rooms.indexOf(room);
	}
	
	public Optional<RoomInfo> roomAt(int x, int y) {
		for(RoomInfo room : rooms) {
			for(ScreenInfo screen : room.screens()) {
				if (screen.x() == x && screen.y() == y) return Optional.of(room);
			}
		}
		return Optional.empty();
	}
	
	public Optional<PlacedScreen> screenAt(int x, int y) {
		for(RoomInfo room : rooms) {
			for(ScreenInfo screen : room.screens()) {
				if (screen.x() == x && screen.y() == y) return Optional.of(new PlacedScreen(this, room, screen));
			}
		}
		return Optional.empty();
	}
	
	public void linkElevators(ElevatorInfo a, ElevatorInfo b) {
		a.setDestination(indexOf(b.room()), b.id());
		b.setDestination(indexOf(a.room()), a.id());
	}
	
	public void deleteRoom(int room) {
		if (room >= 0 && room < rooms.size()) {
			RoomInfo orphan = rooms.get(room);
			rooms.remove(room);
			ArrayElement roomsArray = json.getArray("ROOMS");
			roomsArray.remove(room);
			
			ObjectElement stats = metaJson.getObject("stats");
			
			// Fix the counts in metaJson `stats.rooms` and `stats.screens` fields to keep map traversal stats accurate
			int screens = (int) stats.getPrimitive("screens").asDouble().orElse(0);
			int rooms = (int) stats.getPrimitive("rooms").asDouble().orElse(0);
			
			int traversableOrphanScreens = 0;
			for(ScreenInfo screen : orphan.screens()) {
				if (screen.mapShape() != MinimapBaseShape.BLANK) traversableOrphanScreens++;
			}
			
			rooms = Math.max(rooms - 1, 0);
			screens = Math.max(screens - traversableOrphanScreens, 0);
			
			stats.put("screens", PrimitiveElement.of(screens));
			stats.put("rooms", PrimitiveElement.of(rooms));
			
			// Did we remove a boss room? What was the boss's id? (boss Id will probably be set to -1 for non-boss rooms)
			boolean bossRoom = orphan.isBossRoom();
			int bossId = orphan.bossId();
			if (bossRoom) {
				// Reduce the boss count in metaJson `stats.bosses` field
				int bossCount = stats.getPrimitive("bosses").asInt().orElse(0);
				bossCount = Math.max(bossCount - 1, 0);
				stats.put("bosses", PrimitiveElement.of(bossCount));
				
				// Remove the boss id entry from `GENERAL.gate_bosses[*]`
				Iterator<ValueElement> gateBosses = json.getObject("GENERAL").getArray("gate_bosses").iterator();
				while(gateBosses.hasNext()) {
					ValueElement value = gateBosses.next();
					if (value instanceof PrimitiveElement prim) {
						if (prim.asInt().getAsInt() == bossId) {
							gateBosses.remove();
							break;
						}
					}
				}
			}
			
			// Remove any spawn points referring to this room, and decrement the roomId references for Ids after this room.
			Iterator<ValueElement> spawnPoints = json.getArray("SPAWN_POINTS").iterator();
			while(spawnPoints.hasNext()) {
				ValueElement value = spawnPoints.next();
				if (value instanceof ObjectElement obj) {
					int roomId = obj.getPrimitive("room_id").asInt().orElse(0);
					if (roomId == room) {
						spawnPoints.remove();
					} else if (roomId > room) {
						// Move room Ids down for references to subsequent indices
						obj.put("room_id", PrimitiveElement.of(roomId - 1));
					}
				}
			}
			
			
			
			for(RoomInfo checkRoom : rooms()) {
				for (ScreenInfo checkScreen : checkRoom.screens()) {
					
					// Remove any door references to this room, and decrement the dest_rm references for Ids after this room.
					for(ObjectElement checkDoor : checkScreen.doors()) {
						int destRm = checkDoor.getPrimitive("dest_rm").asInt().orElse(-1);
						if (destRm == room) {
							// Mark the door as invlaid for quick validation
							checkDoor.put("dest_rm", PrimitiveElement.of(-1));
						} else if (destRm > room) {
							// Move room Ids down for references to subsequent indices
							checkDoor.put("dest_rm", PrimitiveElement.of(destRm - 1));
						}
					}
					
					// Remove any elevator references to this room, and decrement the dest_rm references for Ids after this room.
					for(ValueElement checkElevatorVal : checkScreen.json().getArray("ELEVATORS")) {
						if (checkElevatorVal instanceof ObjectElement checkElevator) {
							int destRm = checkElevator.getPrimitive("dest_rm").asInt().orElse(-1);
							if (destRm == room) {
								// Mark the elevator as invalid for quick validation
								checkElevator.put("dest_rm", PrimitiveElement.of(-1));
							} else if (destRm > room) {
								// Move room Ids down for references to subsequent indices
								checkElevator.put("dest_rm", PrimitiveElement.of(destRm - 1));
							}
						}
					}
				}
			}
			
			// Remove any references to this room from the Progression Log, and decrement references to later Ids
			// TODO: This will become incompatible upon the introduction of sectors
			Iterator<ValueElement> progressionLog = json.getArray("PROGRESSION_LOG").iterator();
			while(progressionLog.hasNext()) {
				if (progressionLog.next() instanceof ObjectElement logEntry) {
					int roomId = logEntry.getPrimitive("room_id").asInt().orElse(0);
					if (roomId == room) {
						progressionLog.remove();
					} else if (roomId > room) {
						logEntry.put("room_id", PrimitiveElement.of(roomId - 1));
					}
				}
			}
			
		}
	}
	
	public void addGunshipSpawn(int roomId, int screenId, RoomInfo room, ScreenInfo screen, int x, int y) {
		ArrayElement arr = json().getArray("SPAWN_POINTS");
		
		ObjectElement respawnPoint = new ObjectElement();
		respawnPoint.put("name", PrimitiveElement.of("GUNSHIP"));
		respawnPoint.put("area", PrimitiveElement.of(room.area()));
		respawnPoint.put("type", PrimitiveElement.of(2)); // Respawn Point
		respawnPoint.put("room_id", PrimitiveElement.of(roomId));
		respawnPoint.put("screen_n", PrimitiveElement.of(screenId));
		respawnPoint.put("world_x", PrimitiveElement.of(screen.x()));
		respawnPoint.put("world_y", PrimitiveElement.of(screen.y()));
		respawnPoint.put("x", PrimitiveElement.of(x));
		respawnPoint.put("y", PrimitiveElement.of(y-35));
		arr.add(0, respawnPoint);
		
		ObjectElement spawn = new ObjectElement();
		spawn.put("name", PrimitiveElement.of("GUNSHIP"));
		spawn.put("area", PrimitiveElement.of(room.area()));
		spawn.put("type", PrimitiveElement.of(1)); // Initial spawn point
		spawn.put("room_id", PrimitiveElement.of(roomId));
		spawn.put("screen_n", PrimitiveElement.of(screenId));
		spawn.put("world_x", PrimitiveElement.of(screen.x()));
		spawn.put("world_y", PrimitiveElement.of(screen.y()));
		spawn.put("x", PrimitiveElement.of(x));
		spawn.put("y", PrimitiveElement.of(y));
		arr.add(0, spawn);
		
		
		
	}
	
	public void addElevatorRespawn(int roomId, int screenId, RoomInfo room, ScreenInfo screen, int x, int y) {
		ArrayElement arr = json().getArray("SPAWN_POINTS");
		
		ObjectElement spawn = new ObjectElement();
		spawn.put("name", PrimitiveElement.of(areas.get(room.area()).name()));
		spawn.put("area", PrimitiveElement.of(room.area()));
		spawn.put("type", PrimitiveElement.of(1)); // Respawn Point
		spawn.put("room_id", PrimitiveElement.of(roomId));
		spawn.put("screen_n", PrimitiveElement.of(screenId));
		spawn.put("world_x", PrimitiveElement.of(screen.x()));
		spawn.put("world_y", PrimitiveElement.of(screen.y()));
		spawn.put("x", PrimitiveElement.of(x));
		spawn.put("y", PrimitiveElement.of(-6));
		arr.add(spawn);
	}
	
	
	public static WorldInfo load(Path worldFile) throws IOException, SyntaxError {
		ArrayList<byte[]> files = new ArrayList<>();
		ByteArrayOutputStream bytes = new ByteArrayOutputStream();
		try(InputStream in = Files.newInputStream(worldFile, StandardOpenOption.READ)) {
			InflaterInputStream zin = new InflaterInputStream(new BufferedInputStream(in));
			
			// Buffer all world data. Yes, I know this is a bad idea.
			
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
			
			// Check world version
			double enigmaVersion = worldMetaObj.getPrimitive("version").asDouble().orElse(-1.0);
			
			if ((enigmaVersion - EnigmaFormat.CURRENT_VERSION) > 0.0001) {
				throw new SyntaxError("Cannot open this world version (version: "+enigmaVersion+")");
			}
			
			ObjectElement worldObj = Jankson.readJsonObject(new ByteArrayInputStream(files.get(1)));
			
			// We can do additional validation here but let's try to load anything we can.
			
			return WorldInfo.of(worldObj, worldMetaObj);
		}
	}
	
	public void save(Path worldFile) throws IOException, SyntaxError {
		try (OutputStream fileOut = Files.newOutputStream(worldFile, StandardOpenOption.CREATE)) {
			DeflaterOutputStream deflaterOut = new DeflaterOutputStream(fileOut, new Deflater(), 4096, true);
			
			OutputStreamWriter writer = new OutputStreamWriter(deflaterOut, StandardCharsets.UTF_8);
			Jankson.writeJson(metaJson(), writer, JsonWriterOptions.ONE_LINE);
			writer.flush();
			deflaterOut.write(0);
			Jankson.writeJson(json(), writer, JsonWriterOptions.ONE_LINE);
			writer.flush();
			deflaterOut.write(0);
			
			deflaterOut.finish();
			deflaterOut.flush();
			deflaterOut.close();
		}
	}
	
}