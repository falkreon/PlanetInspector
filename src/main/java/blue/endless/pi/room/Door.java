package blue.endless.pi.room;

import java.util.OptionalDouble;
import java.util.OptionalInt;

import blue.endless.jankson.api.annotation.SerializedName;
import blue.endless.jankson.api.document.ObjectElement;
import blue.endless.jankson.api.document.PrimitiveElement;

public record Door(
		int id,
		int x,
		int y,
		int pos,
		int type,
		int skin,
		@SerializedName("path_point")
		int pathPoint,
		@SerializedName("clear_on_use")
		boolean clearOnUse,
		@SerializedName("dest_id")
		int destId,
		@SerializedName("dest_rm")
		int destRoom
		) {
	
	public Door(ObjectElement elem) {
		this(
			extractInt(elem.getPrimitive("id"), -1),
			elem.getPrimitive("x").asInt().orElse(0),
			elem.getPrimitive("y").asInt().orElse(0),
			extractInt(elem.getPrimitive("pos"), -1),
			elem.getPrimitive("type").asInt().orElse(0),
			elem.getPrimitive("skin").asInt().orElse(0),
			elem.getPrimitive("path_point").asInt().orElse(0),
			elem.getPrimitive("clear_on_use").asBoolean().orElse(true),
			extractInt(elem.getPrimitive("dest_id"), 0),
			extractInt(elem.getPrimitive("dest_rm"), 0)
			);
	}
	
	private static int extractInt(PrimitiveElement elem, int defaultValue) {
		OptionalInt opt = elem.asInt();
		if (opt.isPresent()) return opt.getAsInt();
		
		OptionalDouble opt2 = elem.asDouble();
		if (opt2.isPresent()) return (int) opt2.getAsDouble();
		
		return defaultValue;
	}
}
