package blue.endless.pi.enigma;

import org.jetbrains.annotations.Nullable;

public enum Hazard {
	ATMOSPHERIC(0),
	CONTACT(1),
	THERMAL(2),
	ULTRAHOT(3),
	SUB_ZERO(4),
	ACID(5),
	TOXIC(6),
	RAZOR(7),
	BLUE_PHAZON(8),
	RED_PHAZON(9),
	PURE_PHAZON(10),
	DARK_AIR(11),
	CORRUPTION(12),
	CUSTOM_1(13),
	CUSTOM_2(14),
	CUSTOM_3(15),
	CUSTOM_4(16),
	CUSTOM_5(17),
	CUSTOM_6(18),
	CUSTOM_7(19)
	;
	
	private int id;
	
	Hazard(int id) {
		this.id = id;
	}
	
	public int id() { return id; }
	
	public static @Nullable Hazard of(int id) {
		for(Hazard h : values()) {
			if (h.id == id) return h;
		}
		
		return null;
	}
}
