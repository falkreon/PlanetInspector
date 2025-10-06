package blue.endless.pi.gui;

public enum MinimapBaseShape {
	BLANK(1),
	SQUARE(2),
	SLOPE_SW(3),
	SLOPE_SE(4),
	SLOPE_NE(5),
	SLOPE_NW(6),
	TUBE_H(7),
	TUBE_V(8),
	;
	
	private final int value;
	
	MinimapBaseShape(int value) {
		this.value = value;
	}
	
	public int value() {
		return this.value;
	}
	
	public static MinimapBaseShape of(int value) {
		for(MinimapBaseShape shape : values()) {
			if (shape.value == value) return shape;
		}
		
		return SQUARE;
	}
}
