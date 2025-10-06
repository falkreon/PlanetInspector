package blue.endless.pi.room;

import java.awt.Color;
import java.awt.Graphics;
import java.util.Arrays;
import java.util.List;

import blue.endless.pi.Area;
import blue.endless.pi.Direction;
import blue.endless.pi.DoorType;
import blue.endless.pi.Icon;
import blue.endless.pi.Wall;
import blue.endless.pi.datastruct.Rect;

public record MinimapCell(
		int base,
		int[] doors,
		int[] icons,
		int area,
		int elevator,
		int[] walls
		) {
	
	public MinimapCell() {
		this(
			2,
			new int[] { 0, 0, 0, 0, 0 },
			new int[0],
			0,
			0,
			new int[] { 0, 0, 0, 0, 0 }
			);
	}
	
	public void paint(Graphics g, int x, int y, List<Area> areas, List<Door> doors) {
		
		Color areaColor = new Color(126, 126, 126);
		if (area < areas.size()) {
			areaColor = areas.get(area).awtColor();
		}
		
		Color wallColor = new Color(255, 255, 255);
		
		//Deal with "base"
		switch(base) {
			case 2 -> {
				//Square
				g.setColor(areaColor);
				g.fillRect(x, y, 7, 7);
				
				
				g.setColor(new Color(255, 255, 255));
				for(Direction d : Direction.values()) {
					if (d == Direction.INVALID) continue; 
					Wall w = Wall.valueOf(walls[d.value()]);
					Rect wallRect = d.wallRect();
					Rect doorRect = d.doorRect();
					
					//System.out.println("Drawing wall of type "+w);
					
					
					
					if (w != Wall.OPEN) {
						
						if (w == Wall.SOLID | w == Wall.DOOR) {
							g.setColor(new Color(255,255,255));
							g.fillRect(wallRect.x() + x, wallRect.y() + y, wallRect.width(), wallRect.height());
						}
						
						if (w == Wall.DOOR) {
							//What kind of door?
							//System.out.println("Door #"+doors()[d.value()]);
							
							Color doorColor = new Color(128, 128, 128);
							
							/*
							Door found = null;
							for(Door door : doors) {
								if (door.pos() == d.value()) {
									found = door;
								}
							}
							
							if (found == null) {
								if (!doors.isEmpty()) {
									System.out.println("Anomalous screen doors while looking for pos "+d.value()+": "+doors);
								}
								
								//doorColor = areaColor;
								//int doorId = doors()[d.value()];
								//DoorType doorType = DoorType.of(doorId);
								
								//if (doorType != null) doorColor = doorType.color();
								doorColor = new Color(0, 0, 0);
							} else {
								DoorType doorType = DoorType.of(found.type());
								if (doorType != null) doorColor = doorType.color();
							}*/
							int doorTypeId = doors()[d.value()];
							if (doorTypeId == -1) {
								doorColor = new Color(0, 0, 0, 0);
							} else {
								DoorType doorType = DoorType.of(doorTypeId);
								if (doorType != null) doorColor = doorType.color();
							}
							/*
							Color doorColor = switch(doorId) {
								case 0 -> areaColor;
								case 1 -> areaColor;
								case 2 -> new Color(255, 0, 0); // Actual Red Door in minimap
								case 3 -> new Color(255, 0, 255); // Actual Purple Door in minimap
								case 4 -> new Color(0, 255, 0); // Theorized to be a super missile door
								case 6 -> new Color(128, 128, 255);
								case 7 -> new Color(255, 255, 0); // Mother Brain door
								case 8 -> new Color(200, 200, 200); // Combat door
								default -> {
									System.out.println("Door value: "+doorId);
									yield new Color(128, 128, 128);
								}
							};*/
							
							
							g.setColor(areaColor);
							g.fillRect(doorRect.x() + x, doorRect.y() + y, doorRect.width(), doorRect.height());
							g.setColor(doorColor);
							g.fillRect(doorRect.x() + x, doorRect.y() + y, doorRect.width(), doorRect.height());
							//Door found = null;
							//for(Door door : doors) {
							//	if (door.id() == doorId) {
							//		found = door;
							//	}
							//}
							/*
							if (found != null) {
								//System.out.println("Found: "+found);
								if (found.type() == 1) {
									g.setColor(new Color(255, 0, 0));
								} else if (found.type() == 2) {
									g.setColor(new Color(200,0,200));
								} else if (found.type() == 3) {
									g.setColor(new Color(128,128, 128));
								} else {
									g.setColor(new Color(255, 255, 0));
								}
								
								g.fillRect(doorRect.x() + x, doorRect.y() + y, doorRect.width(), doorRect.height());
							} else {
								//System.out.println("Dir Id: "+d.value()+" Door Id: "+doorId+" Doors: "+doors);
								g.setColor(areaColor);
								g.fillRect(doorRect.x() + x, doorRect.y() + y, doorRect.width(), doorRect.height());
							}*/
							
							//int doorId = doors()[d.value()];
							//Door door = doors.get(doorId);
							//System.out.println(door);
							
							
						} else if (w == Wall.SECRET) {
							g.setColor(new Color(255,255,255, 100));
							g.fillRect(wallRect.x() + x, wallRect.y() + y, wallRect.width(), wallRect.height());
						} else if (w == Wall.UNKNOWN_4) {
							g.setColor(new Color(255, 0, 0));
							g.fillRect(doorRect.x() + x, doorRect.y() + y, doorRect.width(), doorRect.height());
						}
					}
					
				}
				
			}
			
			case 3 -> {
				//SW
				g.setColor(areaColor);
				g.fillPolygon(new int[]{x, x+7, x+7}, new int[]{y, y, y+7}, 3);
				g.setColor(wallColor);
				g.drawLine(x, y, x+6, y+6);
			}
			
			case 4 -> {
				//SE
				g.setColor(areaColor);
				g.fillPolygon(new int[]{x, x+7, x}, new int[]{y, y, y+7}, 3);
				g.setColor(wallColor);
				g.drawLine(x, y+6, x+6, y);
			}
			
			case 5 -> {
				//NE
				g.setColor(areaColor);
				g.fillPolygon(new int[]{x, x+7, x}, new int[]{y, y+7, y+7}, 3);
				g.setColor(wallColor);
				g.drawLine(x, y, x+6, y+6);
			}
			
			case 6 -> {
				//NW
				g.setColor(areaColor);
				g.fillPolygon(new int[]{x, x+7, x+7}, new int[]{y+7, y, y+7}, 3);
				g.setColor(wallColor);
				g.drawLine(x, y+6, x+6, y);
			}
			
			case 7 -> {
				//Horizontal tube
				g.setColor(wallColor);
				//g.fillRect(x,   y, 1, 7);
				//g.fillRect(x+6, y, 1, 7);
				g.fillRect(x, y+2, 7, 1);
				g.fillRect(x, y+4, 7, 1);
				
				g.setColor(areaColor);
				g.fillRect(x, y+3, 7, 1);
			}
			
			case 8 -> {
				//Vertical tube
				g.setColor(wallColor);
				//g.fillRect(x,   y, 7, 1);
				//g.fillRect(x, y+6, 7, 1);
				g.fillRect(x+2, y, 1, 7);
				g.fillRect(x+4, y, 1, 7);
				
				g.setColor(areaColor);
				g.fillRect(x+3, y, 1, 7);
			}
			
			default -> {
				g.setColor(new Color(255, 255, 0));
				g.fillRect(x + 2, y + 2, 3, 3);
			}
		}
		
		
		if (icons.length != 0) {
			for(int i : icons) {
				Icon icon = Icon.valueOf(i);
				if (icon != null) {
					icon.paint(g, x+1, y+1);
					
				}
			}
			//System.out.println("Icons for "+x+","+y+": "+Arrays.toString(icons));
		}
	}
}
