package blue.endless.pi.enigma;

import java.awt.Color;

import org.jetbrains.annotations.Nullable;

public enum DoorType {
	ZERO         (0, new Color(  0, 0xC0, 0xFF)),
	BLUE         (1, new Color(  0, 0xC0, 0xFF)),
	MISSILE      (2, new Color(255,   0,   0)),
	BOSS         (3, new Color(255,   0, 255)),
	SUPER_MISSILE(4, new Color(  0, 255,   0)),
	UNKNOWN      (5, new Color(255, 128, 128)), // Probably power bomb
	IMPASSABLE   (6, new Color(128, 128, 255)),
	MOTHER_BRAIN (7, new Color(255, 255,   0)),
	COMBAT       (8, new Color(200, 200, 200)),
	;
	
	private final int value;
	private final Color color;
	
	DoorType(int value, Color color) {
		this.value = value;
		this.color = color;
	}
	
	public int value() {
		return this.value;
	}
	
	public Color color() {
		return this.color;
	}
	
	@Nullable
	public static DoorType of(int value) {
		for(DoorType t : values()) {
			if (t.value == value) return t;
		}
		return null;
	}
}
