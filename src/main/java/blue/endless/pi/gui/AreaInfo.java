package blue.endless.pi.gui;

import java.awt.Color;

import blue.endless.jankson.api.document.ObjectElement;
import blue.endless.jankson.api.document.PrimitiveElement;

public record AreaInfo(ObjectElement json) {
	
	public String bgm() {
		return json.getPrimitive("bgm").asString().orElse("");
	}
	
	public String name() {
		return json.getPrimitive("name").asString().orElse("");
	}
	
	public int bgrColor() {
		return json.getPrimitive("color").asInt().orElse(0xFF0000);
	}
	
	public Color color() {
		int bgr = bgrColor();
		int r = bgr & 0xFF;
		int g = (bgr >> 8) & 0xFF;
		int b = (bgr >> 16) & 0xFF;
		return new Color(r, g, b);
	}
	
	public void setColor(Color c) {
		int r = c.getRed();
		int g = c.getGreen();
		int b = c.getBlue();
		int bgr = (b << 16) | (g << 8) | r;
		json.put("color", PrimitiveElement.of(bgr));
	}
}
