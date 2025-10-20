package blue.endless.pi.gui.layout;

import java.awt.Dimension;

import blue.endless.pi.datastruct.Vec2;

public enum Axis {
	HORIZONTAL(new Vec2(1, 0)),
	VERTICAL(new Vec2(0, 1));
	
	private final Vec2 direction;
	
	Axis(Vec2 direction) {
		this.direction = direction;
	}
	
	public Axis opposite() {
		if (this == HORIZONTAL) {
			return VERTICAL;
		} else {
			return HORIZONTAL;
		}
	}
	
	public Vec2 direction() { return direction; }
	
	public int select(int x, int y) {
		if (this == HORIZONTAL) {
			return x;
		} else {
			return y;
		}
	}
	
	public int select(Dimension d) {
		return select(d.width, d.height);
	}
	
	public Vec2 arrange(int main, int cross) {
		if (this == HORIZONTAL) {
			return new Vec2(main, cross);
		} else {
			return new Vec2(cross, main);
		}
	}
}