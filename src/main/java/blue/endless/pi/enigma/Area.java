package blue.endless.pi.enigma;

import java.awt.Color;

import blue.endless.jankson.api.document.ObjectElement;

public record Area(String bgm, String name, int color) {
	public Area(ObjectElement obj) {
		this(
			obj.getPrimitive("bgm").asString().orElse("bgm_Ambience"),
			obj.getPrimitive("name").asString().orElse("ERROR"),
			obj.getPrimitive("color").asInt().orElseGet(() -> (int) obj.getPrimitive("color").asDouble().orElse(0x808080))
			);
	}
	
	public Color awtColor() {
		int b = (color >> 16) & 0xFF;
		int g = (color >> 8) & 0xFF;
		int r = color & 0xFF;
		return new Color(r, g, b);
	}
}
