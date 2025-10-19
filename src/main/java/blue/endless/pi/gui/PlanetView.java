package blue.endless.pi.gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.ArrayList;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiConsumer;

import javax.swing.JPanel;

import blue.endless.jankson.api.document.ArrayElement;
import blue.endless.jankson.api.document.ObjectElement;
import blue.endless.jankson.api.document.PrimitiveElement;
import blue.endless.jankson.api.document.ValueElement;
import blue.endless.pi.Direction;
import blue.endless.pi.SchemaType;
import blue.endless.pi.datastruct.Vec2;

public class PlanetView extends JPanel implements MouseListener, MouseMotionListener {
	private static final Color MAP_BACKGROUND = new Color(63, 63, 63);
	private WorldInfo world;
	private BiConsumer<ObjectElement, Map<String, SchemaType<?>>> propertiesConsumer = (o, s) -> {};
	private int selectedRoom = -1;
	private int dragRoom = -1;
	private int dragStartX = -1;
	private int dragStartY = -1;
	private int dragCurX = -1;
	private int dragCurY = -1;
	private boolean dirty = false;
	
	public PlanetView() {
		this.setMinimumSize(new Dimension(1024, 768));
		this.setMaximumSize(new Dimension(1024, 768));
		this.setPreferredSize(new Dimension(1024, 768));
		this.setSize(new Dimension(1024, 768));
		this.addMouseListener(this);
		this.addMouseMotionListener(this);
		
		this.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				if (selectedRoom == -1) return;
				if (e.getKeyCode() == KeyEvent.VK_BACK_SPACE || e.getKeyCode() == KeyEvent.VK_DELETE) {
					
					dirty = true;
					world.deleteRoom(selectedRoom);
					selectedRoom = -1;
					PlanetView.this.repaint();
				}
			}
		});
		
		this.setFocusable(true);
	}
	
	public static final int CELL_SIZE = 20;
	public static final int DOOR_SIZE = 7;
	public static final int DOOR_OFFSET = (CELL_SIZE - DOOR_SIZE) / 2;
	
	public boolean isDirty() {
		return dirty;
	}
	
	public void setDirty(boolean dirty) {
		this.dirty = dirty;
	}
	
	@Override
	protected void paintComponent(Graphics g) {
		
		if (world == null) {
			g.setColor(Color.GRAY);
			g.fillRect(0, 0, this.getWidth(), this.getHeight());
			return;
		}
		
		//Precalc area colors
		ArrayList<Color> areaColors = new ArrayList<>();
		for(AreaInfo area : world.areas()) {
			areaColors.add(area.color());
		}
		
		//if (this.isOpaque()) {
		
		g.setColor(MAP_BACKGROUND);
		g.fillRect(0, 0, this.getWidth(), this.getHeight());
		//}
		// TODO Auto-generated method stub
		//super.paintComponent(g);
		if (world != null && world.rooms().size() > 0) {
			for(int ri = 0; ri<world.rooms().size(); ri++) {
				RoomInfo room = world.rooms().get(ri);
				boolean valid = room.validate();
				for(ScreenInfo screen : room.screens()) {
					int x = screen.x() * CELL_SIZE;
					int y = screen.y() * CELL_SIZE;
					Color areaColor = areaColors.get(screen.area()); // TODO: Bounds-check
					
					screen.paint(g, x, y, areaColor, ri == selectedRoom, ri == dragRoom, valid);
				}
			}
		}
		
		if (dragRoom != -1) {
			RoomInfo room = world.rooms().get(dragRoom);
			for(ScreenInfo screen : room.screens()) {
				int x = screen.x() * CELL_SIZE;
				int y = screen.y() * CELL_SIZE;
				
				x += (dragCurX - dragStartX) * CELL_SIZE;
				y += (dragCurY - dragStartY) * CELL_SIZE;
				g.setColor(Color.GREEN);
				g.drawRect(x, y, CELL_SIZE-1, CELL_SIZE-1);
			}
		}
	}
	
	public int roomAt(int cellX, int cellY) {
		if (world == null || world.rooms().size() == 0) return -1;
		
		for(int ri = 0; ri<world.rooms().size(); ri++) {
			RoomInfo room = world.rooms().get(ri);
			for(ScreenInfo screen : room.screens()) {
				if (screen.x() == cellX && screen.y() == cellY) return ri;
			}
		}
		
		return -1;
	}
	
	public void setWorld(WorldInfo world) {
		this.world = world;
		
		//TODO: extract information
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		int clickedRoom = roomAt(e.getX() / CELL_SIZE, e.getY() / CELL_SIZE);
		if (clickedRoom == -1) {
			if (selectedRoom != -1) {
				selectedRoom = -1;
				propertiesConsumer.accept(null, null);
				this.repaint();
			}
		} else {
			selectedRoom = clickedRoom;
			propertiesConsumer.accept(world.rooms().get(clickedRoom).general(), EditorFrame.ROOM_GENERAL_SCHEMA);
			this.repaint();
		}
		
		if (e.getClickCount() == 2) {
			// Let's go!
			System.out.println("Opening configurator for room " + clickedRoom);
			RoomConfiguratorFrame configurator = new RoomConfiguratorFrame(world, clickedRoom);
			configurator.setVisible(true);
		}
		/*
		if (world != null && world.rooms().size() > 0) {
			int x = e.getX() / CELL_SIZE;
			int y = e.getY() / CELL_SIZE;
			for(int ri = 0; ri<world.rooms().size(); ri++) {
				RoomInfo room = world.rooms().get(ri);
				for(ScreenInfo screen : room.screens()) {
					if (screen.x() == x && screen.y() == y) {
						selectedRoom = ri;
						// TODO: Select room general in properties panel
						this.repaint();
						propertiesConsumer.accept(room.general(), EditorFrame.ROOM_GENERAL_SCHEMA);
						return;
					}
				}
			}
		}*/
		
		//selectedRoom = -1;
		//propertiesConsumer.accept(null, null);
		//this.repaint();
		// TODO: Select world meta in properties panel
	}

	@Override
	public void mouseEntered(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseExited(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mousePressed(MouseEvent e) {
		// TODO Auto-generated method stub
		
		dragStartX = e.getX() / CELL_SIZE;
		dragStartY = e.getY() / CELL_SIZE;
		
		dragRoom = roomAt(dragStartX, dragStartY);
		if (dragRoom == -1) {
			dragStartX = -1;
			dragStartY = -1;
		}
		
		dragCurX = dragStartX;
		dragCurY = dragStartY;
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		if (dragRoom != -1) {
			
			int dx = dragCurX - dragStartX;
			int dy = dragCurY - dragStartY;
			if (dx == 0 && dy == 0) {
				// We didn't actually drag. Kill the operation
				dragRoom = -1;
				dragStartX = -1;
				dragStartY = -1;
				dragCurX = -1;
				dragCurY = -1;
				return;
			}
			
			
			//System.out.println("Relocating room "+dragRoom+" by "+dx+", "+dy+"...");
			
			boolean collision = false;
			RoomInfo room = world.rooms().get(dragRoom);
			for(ScreenInfo s : room.screens()) {
				int hitX = s.x() + dx;
				int hitY = s.y() + dy;
				
				int hitRoom = roomAt(hitX, hitY);
				if (hitRoom != -1 && hitRoom != dragRoom) {
					collision = true;
					break;
				}
			}
			
			if (!collision) {
				dirty = true;
				
				for(ScreenInfo s : room.screens()) {
					s.setPosition(s.x() + dx, s.y() + dy);
				}
				
				// De-stitch doors
				for(ScreenInfo s : room.screens()) {
					for(ObjectElement door : s.doors()) {
						int destRoom = door.getPrimitive("dest_rm").asInt().orElse(-1);
						int destId = door.getPrimitive("dest_id").asInt().orElse(0);
						
						if (destRoom >=0 && destRoom < world.rooms().size()) {
							RoomInfo reverseRoom = world.rooms().get(destRoom);
							
							findLinkedDoor:
							for (ScreenInfo reverseScreen : reverseRoom.screens()) {
								for(ObjectElement reverseDoor : reverseScreen.doors()) {
									int reverseDoorId = reverseDoor.getPrimitive("id").asInt().orElse(-1);
									if (reverseDoorId == destId) {
										reverseDoor.put("dest_rm", PrimitiveElement.of(-1));
										reverseDoor.put("dest_id", PrimitiveElement.of(0));
										break findLinkedDoor;
									}
								}
							}
						}
						
						door.put("dest_rm", PrimitiveElement.of(-1));
						door.put("dest_id", PrimitiveElement.of(0));
					}
				}
				
				//Stitch doors
				for(ScreenInfo s : room.screens()) {
					for(ObjectElement door : s.doors()) {
						Direction d = Direction.valueOf(door.getPrimitive("pos").asInt().orElse(0));
						int doorDestRoom = door.getPrimitive("dest_rm").asInt().orElse(0);
						
						Vec2 dest = new Vec2(s.x(), s.y()).add(d.offset());
						int destRoom = roomAt(dest.x(), dest.y());
						if (doorDestRoom != destRoom) {
							if (destRoom != -1) {
								
								RoomInfo destRoomObj = world.rooms().get(destRoom);
								boolean doorFound = false;
								findDoor:
								for(ScreenInfo destScreen : destRoomObj.screens()) {
									if (destScreen.x() == dest.x() && destScreen.y() == dest.y()) {
										for(ObjectElement destDoor : destScreen.doors()) {
											Direction destDirection = Direction.valueOf(destDoor.getPrimitive("pos").asInt().orElse(0));
											if (d == destDirection.opposite()) {
												//Found it!
												doorFound = true;
												
												// Link forward
												door.put("dest_rm", PrimitiveElement.of(destRoom));
												int destDoorId = destDoor.getPrimitive("id").asInt().orElse(0);
												door.put("dest_id", PrimitiveElement.of(destDoorId));
												
												// Link backward
												destDoor.put("dest_rm", PrimitiveElement.of(dragRoom));
												destDoor.put("dest_id", door.getPrimitive("id"));
												
												
												break findDoor;
											}
										}
									}
								}
								if (!doorFound) {
									// TODO: If door wasn't found by this point, we're in trouble. Flag room as invalid!
									System.out.println("Flagging door as invalid");
									door.put("dest_rm", PrimitiveElement.of(-1));
									door.put("dest_id", PrimitiveElement.of(0));
								}
								
							} else {
								//TODO: Flag room as invalid!
								System.out.println("Flagging door as invalid");
								door.put("dest_rm", PrimitiveElement.of(-1));
								door.put("dest_id", PrimitiveElement.of(0));
							}
						}
					}
				}
				
				// De-stitch elevators
				for(ScreenInfo screen : room.screens()) {
					for(ValueElement val : screen.json().getArray("ELEVATORS")) {
						if (val instanceof ObjectElement elevator) {
							int destRm = elevator.getPrimitive("dest_rm").asInt().orElse(-1);
							int destId = elevator.getPrimitive("dest_id").asInt().orElse(-1);
							if (destRm > 0 && destRm < world.rooms().size()) {
								RoomInfo destRoom = world.rooms().get(destRm);
								Optional<ObjectElement> maybeDest = destRoom.getElevator(destId);
								maybeDest.ifPresent(it -> {
									it.put("dest_rm", PrimitiveElement.of(-1));
									it.put("dest_id", PrimitiveElement.of(0));
								});
							}
							
							elevator.put("dest_rm", PrimitiveElement.of(-1));
							elevator.put("dest_id", PrimitiveElement.of(0));
						}
					}
				}
				// Stitch elevators
				
				for(ScreenInfo screen : room.screens()) {
					for(ValueElement val : screen.json().getArray("ELEVATORS")) {
						if (val instanceof ObjectElement elevator) {
							Direction d = Direction.valueOf(elevator.getPrimitive("dir").asInt().orElse(0));
							if (d == Direction.INVALID) continue; // Don't adjust elevators we don't understand
							
							
						}
					}
				}
				
				// Update Spawn Points
				ArrayElement spawnPointArray = world.json().getArray("SPAWN_POINTS");
				for(ValueElement val : spawnPointArray) {
					if (val instanceof ObjectElement spawnPointObj) {
						int roomId = spawnPointObj.getPrimitive("room_id").asInt().orElse(0);
						if (roomId == dragRoom) {
							int oldX = spawnPointObj.getPrimitive("world_x").asInt().orElse(0);
							int oldY = spawnPointObj.getPrimitive("world_y").asInt().orElse(0);
							spawnPointObj.put("world_x", PrimitiveElement.of(oldX + dx));
							spawnPointObj.put("world_y", PrimitiveElement.of(oldY + dy));
							System.out.println("Spawn point '"+spawnPointObj.getPrimitive("name").asString().get()+"' adjusted.");
						}
					}
				}
				
				// TODO XXX : Update PROGRESSION_LOG if this room is mentioned
				// TODO : Disconnect broken doors so they can be quickly validated
			} else {
				System.out.println("Can't drag room there: collision!");
			}
			
			dragRoom = -1;
			dragStartX = -1;
			dragStartY = -1;
			dragCurX = -1;
			dragCurY = -1;
			
			//System.out.println("Release: "+cellX+", "+cellY);
			
			
			
			this.repaint();
		}
	}

	public void setPropertiesConsumer(BiConsumer<ObjectElement, Map<String, SchemaType<?>>> propertiesConsumer) {
		this.propertiesConsumer = propertiesConsumer;
	}

	@Override
	public void mouseDragged(MouseEvent e) {
		if (dragRoom != -1) {
			int cellX = e.getX() / CELL_SIZE;
			int cellY = e.getY() / CELL_SIZE;
			
			dragCurX = cellX;
			dragCurY = cellY;
			
			this.repaint();
		}
	}

	@Override
	public void mouseMoved(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}
}
