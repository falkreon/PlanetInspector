package blue.endless.pi.enigma;

import blue.endless.pi.datastruct.Rect;
import blue.endless.pi.datastruct.Vec2;

public enum Direction {
	EAST   (1, new Rect(6,0,1,7), new Rect(6,2,1,3), new Vec2( 1,  0)),
	NORTH  (2, new Rect(0,0,7,1), new Rect(2,0,3,1), new Vec2( 0, -1)),
	WEST   (3, new Rect(0,0,1,7), new Rect(0,2,1,3), new Vec2(-1,  0)),
	SOUTH  (4, new Rect(0,6,7,1), new Rect(2,6,3,1), new Vec2( 0,  1)),
	INVALID(0, new Rect(0,0,0,0), new Rect(0,0,0,0), new Vec2( 0,  0));
	
	private final int value;
	private final Rect wallRect;
	private final Rect doorRect;
	private final Vec2 offset;
	
	Direction(int value, Rect wall, Rect door, Vec2 offset) {
		this.value = value;
		this.wallRect = wall;
		this.doorRect = door;
		this.offset = offset;
	}
	
	public int value() {
		return this.value;
	}
	
	public Rect wallRect() {
		return wallRect;
	}
	
	public Rect doorRect() {
		return doorRect;
	}
	
	public Vec2 offset() {
		return this.offset;
	}
	
	public Direction opposite() {
		return switch(this) {
			case EAST -> WEST;
			case WEST -> EAST;
			case NORTH -> SOUTH;
			case SOUTH -> NORTH;
			case INVALID -> INVALID;
		};
	}
	
	public static Direction valueOf(int value) {
		return switch(value) {
			case 1 -> EAST;
			case 2 -> NORTH;
			case 3 -> WEST;
			case 4 -> SOUTH;
			default -> INVALID;
		};
	}
}
