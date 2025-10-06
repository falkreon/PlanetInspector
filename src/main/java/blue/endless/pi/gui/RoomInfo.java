package blue.endless.pi.gui;

import java.util.ArrayList;
import java.util.List;

import blue.endless.jankson.api.document.ArrayElement;
import blue.endless.jankson.api.document.ObjectElement;
import blue.endless.jankson.api.document.ValueElement;

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
}