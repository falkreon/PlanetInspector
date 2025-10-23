package blue.endless.pi.datastruct;

public record Rect(int x, int y, int width, int height) {
	public boolean contains(int x, int y) {
		return
				x>=this.x &&
				y>=this.y &&
				x < this.x+this.width
				&& y < this.y+this.height;
	}
}
