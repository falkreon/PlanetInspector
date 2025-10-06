package blue.endless.pi;

import java.awt.Color;
import java.awt.Graphics;

import org.jetbrains.annotations.Nullable;

public enum Icon {
	ITEM(1, 0xFFFFFF, new int[] {
		0b00000,
		0b00100,
		0b01010,
		0b00100,
		0b00000
	}),
	
	COLLECTED_ITEM(2, 0xFFFFFF, new int[] {
		0b00000,
		0b00100,
		0b01110,
		0b00100,
		0b00000
	}),
	
	BOSS(3, 0xFF0000, new int[] {
		0b01110,
		0b10101,
		0b10101,
		0b11111,
		0b01010
	}),
	
	DEFETATED_BOSS(4, 0x000000, new int[] {
		0b01110,
		0b10101,
		0b10101,
		0b11111,
		0b01010
	}),
	
	GUNSHIP(5, 0xAC7C00, new int[] {
		0b00000,
		0b01110,
		0b11111,
		0b11111,
		0b01010
	}),
	
	ELEVATOR_UP(6, 0xFFFFFF, new int[] {
		0b00000,
		0b00100,
		0b01110,
		0b01010,
		0b00000
	}),
	
	ELEVATOR_DOWN(7, 0xFFFFFF, new int[] {
		0b00000,
		0b01010,
		0b01110,
		0b00100,
		0b00000
	}),
	
	SCANNER(7, 0x00FF00, new int[] {
		0b01110,
		0b10001,
		0b10101,
		0b10001,
		0b01110
	}),
	;
	
	private final int value;
	private final Color color;
	private final int[] image;
	
	Icon(int value, int color, int[] image) {
		this.value = value;
		this.image = image;
		this.color = new Color(
				(color >> 16) & 0xFF,
				(color >> 8) & 0xFF,
				color & 0xFF
				);
	}
	
	public void paint(Graphics g, int x, int y) {
		g.setColor(color);
		for(int yi=0; yi<5; yi++) {
			int line = image[yi];
			for(int xi=0; xi<5; xi++) {
				boolean cell = (line & (0b10000 >> xi)) != 0;
				if (cell) g.fillRect(x+xi, y+yi, 1, 1);
			}
		}
	}
	
	@Nullable
	public static Icon valueOf(int i) {
		for(Icon icon : values()) {
			if (i==icon.value) return icon;
		}
		
		return null;
	}
}
