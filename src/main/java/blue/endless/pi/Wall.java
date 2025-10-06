package blue.endless.pi;

public enum Wall {
	OPEN(0),
	SOLID(1),
	SECRET(2),
	DOOR(3),
	UNKNOWN_4(4),
	UNKNOWN_5(5),
	UNKNOWN_6(6),
	UNKNOWN(-1)
	;
	
	private final int value;
	
	Wall(int value) {
		this.value = value;
	}
	
	public int value() {
		return this.value;
	}
	
	public static Wall valueOf(int value) {
		for(Wall w : values()) {
			if (w.value == value) return w;
		}
		
		return UNKNOWN;
	}
}
