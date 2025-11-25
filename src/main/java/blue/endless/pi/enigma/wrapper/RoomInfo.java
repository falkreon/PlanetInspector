package blue.endless.pi.enigma.wrapper;

import java.awt.Color;
import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.zip.InflaterInputStream;

import blue.endless.jankson.api.Jankson;
import blue.endless.jankson.api.SyntaxError;
import blue.endless.jankson.api.document.ArrayElement;
import blue.endless.jankson.api.document.ObjectElement;
import blue.endless.jankson.api.document.PrimitiveElement;
import blue.endless.jankson.api.document.ValueElement;
import blue.endless.pi.enigma.EnemyType;
import blue.endless.pi.enigma.EnigmaFormat;
import blue.endless.pi.enigma.Palette;

public record RoomInfo(ObjectElement json, ObjectElement general, List<ScreenInfo> screens) {
	public static RoomInfo of(ObjectElement roomJson) {
		ObjectElement general = roomJson.getObject("GENERAL");
		
		ArrayList<ScreenInfo> screens = new ArrayList<>();
		ArrayElement screensArray = roomJson.getArray("SCREENS");
		for(ObjectElement screenObj : screensArray.asObjectArray()) {
			screens.add(new ScreenInfo(screenObj));
			
			// Look for bosses
			for(ObjectElement enemyObj : screenObj.getArray("ENEMIES").asObjectArray()) {
				int enemyTypeId = (int) enemyObj.getPrimitive("type").orElse(-1);
				EnemyType enemyType = EnemyType.of(enemyTypeId);
				if (enemyType != null && enemyType.isBoss()) {
					ObjectElement meta = roomJson.getObject("META");
					meta.put("boss_room", PrimitiveElement.of(1));
					meta.put("boss", PrimitiveElement.of(enemyTypeId));
				}
			}
		}
		
		RoomInfo result = new RoomInfo(roomJson, general, List.copyOf(screens));
		
		EnigmaFormat.preventRoomCrashes(result);
		return result;
	}

	public String name() {
		return general.getPrimitive("name").asString().orElse("");
	}
	
	public Color getPaletteColor(int paletteId, int colorInPalette) {
		ArrayElement palettesArray = json.getArray("PALETTES");
		if (palettesArray.size() > paletteId) {
			ObjectElement paletteObj = palettesArray.getObject(paletteId);
			
			ArrayElement palette = paletteObj.getArray("f").getArray(0);
			if (palette.size() <= colorInPalette) return Palette.get(0);
			int color = palette.getPrimitive(colorInPalette).asInt().orElse(0);
			return Palette.get(color);
		}
		
		return Color.BLACK;
	}
	
	public Optional<ObjectElement> getDoor(int id) {
		for(ScreenInfo screen : screens) {
			ArrayElement arr = screen.json().getArray("DOORS");
			for(ValueElement elem : arr) {
				if (elem instanceof ObjectElement obj) {
					OptionalInt curId = obj.getPrimitive("id").asInt();
					if (curId.isPresent() && curId.getAsInt() == id) return Optional.of(obj); 
				}
			}
		}
		
		return Optional.empty();
	}
	
	/**
	 * Returns the screen_n for the screen within this room, or -1 if the screen doesn't exist within this room
	 * @param screen The screen to lookup the index for
	 * @return The index of the specified screen, or -1 if it's not in this room.
	 */
	public int indexOf(ScreenInfo screen) {
		return screens.indexOf(screen);
	}
	
	public Optional<ElevatorInfo> getElevator(WorldInfo world, int id) {
		for(ScreenInfo screen : screens) {
			ArrayElement arr = screen.json().getArray("ELEVATORS");
			for(ValueElement elem : arr) {
				if (elem instanceof ObjectElement obj) {
					OptionalInt curId = obj.getPrimitive("id").asInt();
					if (curId.isPresent() && curId.getAsInt() == id) {
						return Optional.of(new ElevatorInfo(world, this, screen, obj));
					}
				}
			}
		}
		
		return Optional.empty();
	}
	
	public int area() {
		return json().getObject("GENERAL").getPrimitive("area").asInt().orElse(0);
	}
	
	public boolean isBossRoom() {
		return json().getObject("META").getPrimitive("boss_room").asInt().orElse(0) != 0;
	}
	
	public int bossId() {
		return json().getObject("META").getPrimitive("boss").asInt().orElse(-1);
	}
	
	public void setArea(int area) {
		this.json().getObject("GENERAL").put("area", PrimitiveElement.of(area));
		for(ScreenInfo screen : screens) screen.setArea(area);
	}
	
	public boolean validate() {
		for(ScreenInfo screen : screens) {
			for(ObjectElement door : screen.doors()) {
				int dest_rm = door.getPrimitive("dest_rm").asInt().orElse(-1);
				if (dest_rm == -1) {
					return false;
				}
			}
			
			ArrayElement arr = screen.json().getArray("ELEVATORS");
			for(ValueElement elem : arr) {
				if (elem instanceof ObjectElement obj) {
					int dest_rm = obj.getPrimitive("dest_rm").asInt().orElse(-2);
					if (dest_rm == -2) {
						return false;
					}
				}
			}
		}
		
		return true;
	}
	
	public static RoomInfo load(Path roomFile) throws IOException, SyntaxError {
		ArrayList<byte[]> files = new ArrayList<>();
		ByteArrayOutputStream bytes = new ByteArrayOutputStream();
		try(InputStream in = Files.newInputStream(roomFile, StandardOpenOption.READ)) {
			InflaterInputStream zin = new InflaterInputStream(new BufferedInputStream(in));
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
		}
		
		if (bytes.size() > 0) {
			files.add(bytes.toByteArray());
		}
		
		if (files.size() != 1) throw new IOException("Expected a single encoded json, found "+files.size());
		
		ObjectElement roomObj = Jankson.readJsonObject(new ByteArrayInputStream(files.get(0)));
		return RoomInfo.of(roomObj);
	}

	
	
}