package blue.endless.pi.enigma;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.util.Optional;

import javax.swing.JComboBox;

import org.jetbrains.annotations.Nullable;

import blue.endless.pi.Assets;
		
public enum DoorType {
	ZERO         (0, new Color(  0, 0xC0, 0xFF), new int[]{ 0x12, 0x22, 0x30 } ),
	BLUE         (1, new Color(  0, 0xC0, 0xFF), new int[]{ 0x12, 0x22, 0x30 } ),
	MISSILE      (2, new Color(255,   0,   0), new int[]{  7, 22, 48 } ),
	BOSS         (3, new Color(255,   0, 255), new int[]{  3, 20, 53 } ),
	SUPER_MISSILE(4, new Color(  0, 255,   0), new int[]{ 10, 26, 48 } ),
	POWER_BOMB   (5, new Color(255, 128, 128), new int[]{ 23, 40, 48 } ),
	IMPASSABLE   (6, new Color(128, 128, 255), new int[]{ 16, 32, 48 } ),
	MOTHER_BRAIN (7, new Color(255, 255,   0), new int[]{ 40, 56, 48 } ),
	COMBAT       (8, new Color(200, 200, 200), new int[]{ 16, 32, 48 } ),
	UNKNOWN_CYAN (9, new Color(0, 0, 0), new int[]{ 12, 44, 48 } ),
	UNKNOWN_BLUE (10,new Color(0, 0, 0), new int[]{  2, 18, 33 } )
	;
	
	private final int value;
	private final Color color;
	private int[] palette;
	
	DoorType(int value, Color color) {
		this.value = value;
		this.color = color;
		this.palette = new int[]{ 16, 32, 48 };
	}
	
	DoorType(int value, Color color, int[] palette) {
		this.value = value;
		this.color = color;
		this.palette = palette;
	}
	
	public int value() {
		return this.value;
	}
	
	public Color color() {
		return this.color;
	}
	
	public void paint(Graphics g, int x, int y, Direction direction) {
		paint(g, x, y, direction, this);
	}
	
	public static void paint(Graphics g, int x, int y, Direction direction, DoorType doorType) {
		Optional<BufferedImage> maybeDoorAtlas = Assets.getCachedImage("minimap/doors.png");
		if (maybeDoorAtlas.isEmpty()) return;
		BufferedImage doorAtlas = maybeDoorAtlas.get();
		
		int atlasX = doorType.value() * 14;
		int atlasY = direction.value() * 14;
		g.drawImage(doorAtlas, x, y, x+14, y+14, atlasX, atlasY, atlasX+14, atlasY+14, null);
	}
	
	@Nullable
	public static DoorType of(int value) {
		for(DoorType t : values()) {
			if (t.value == value) return t;
		}
		return null;
	}

	public int[] palette() {
		return palette;
	}
	
	public static JComboBox<DoorType> createControl() {
		return new JComboBox<>(DoorType.values());
	}
}
