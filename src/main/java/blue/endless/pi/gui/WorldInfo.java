package blue.endless.pi.gui;

import java.util.ArrayList;
import java.util.List;

import blue.endless.jankson.api.document.ArrayElement;
import blue.endless.jankson.api.document.ObjectElement;
import blue.endless.jankson.api.document.ValueElement;

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
}