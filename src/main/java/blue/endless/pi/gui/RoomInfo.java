package blue.endless.pi.gui;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.OptionalInt;

import blue.endless.jankson.api.document.ArrayElement;
import blue.endless.jankson.api.document.ObjectElement;
import blue.endless.jankson.api.document.ValueElement;
import blue.endless.pi.Palette;

public record RoomInfo(ObjectElement json, ObjectElement general, List<ScreenInfo> screens) {
	public static RoomInfo of(ObjectElement roomJson) {
		ObjectElement general = roomJson.getObject("GENERAL");
		
		ArrayList<ScreenInfo> screens = new ArrayList<>();
		ArrayElement screensArray = roomJson.getArray("SCREENS");
		for(ValueElement elem : screensArray) {
			if (elem instanceof ObjectElement obj) {
				screens.add(new ScreenInfo(obj));
			}
		}
		
		return new RoomInfo(roomJson, general, List.copyOf(screens));
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
	
	public Optional<ObjectElement> getElevator(int id) {
		for(ScreenInfo screen : screens) {
			ArrayElement arr = screen.json().getArray("ELEVATORS");
			for(ValueElement elem : arr) {
				if (elem instanceof ObjectElement obj) {
					OptionalInt curId = obj.getPrimitive("id").asInt();
					if (curId.isPresent() && curId.getAsInt() == id) return Optional.of(obj); 
				}
			}
		}
		
		return Optional.empty();
	}
	
	public boolean validate() {
		for(ScreenInfo screen : screens) {
			for(ObjectElement door : screen.doors()) {
				int dest_rm = door.getPrimitive("dest_rm").asInt().orElse(-1);
				if (dest_rm == -1) {
					return false;
				}
				// Find destination tile for door.
				
			}
		}
		
		return true;
	}
}