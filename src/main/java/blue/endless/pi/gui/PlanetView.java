package blue.endless.pi.gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import java.awt.dnd.DropTargetListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import javax.swing.JPanel;

import org.jetbrains.annotations.Nullable;

import blue.endless.jankson.api.document.ArrayElement;
import blue.endless.jankson.api.document.ObjectElement;
import blue.endless.jankson.api.document.PrimitiveElement;
import blue.endless.jankson.api.document.ValueElement;
import blue.endless.pi.Assets;
import blue.endless.pi.SchemaType;
import blue.endless.pi.datastruct.Vec2;
import blue.endless.pi.enigma.Direction;
import blue.endless.pi.enigma.DoorConnectionLogic;
import blue.endless.pi.enigma.Palette;
import blue.endless.pi.enigma.wrapper.AreaInfo;
import blue.endless.pi.enigma.wrapper.DoorInfo;
import blue.endless.pi.enigma.wrapper.ElevatorInfo;
import blue.endless.pi.enigma.wrapper.PlacedScreen;
import blue.endless.pi.enigma.wrapper.RoomInfo;
import blue.endless.pi.enigma.wrapper.ScreenInfo;
import blue.endless.pi.enigma.wrapper.WorldInfo;
import blue.endless.pi.gui.view.ViewContext;

public class PlanetView extends JPanel implements MouseListener, MouseMotionListener {
	private static final Color MAP_BACKGROUND = new Color(42, 42, 42);
	private WorldInfo world;
	private ViewContext context;
	private BiConsumer<ObjectElement, Map<String, SchemaType<?>>> propertiesConsumer = (o, s) -> {};
	
	private Consumer<RoomInfo> roomSelectionCallback = (it) -> {};
	private BiConsumer<RoomInfo, Vec2> roomRightClickCallback = (room, vec) -> {};
	private Consumer<RoomInfo> roomDoubleClickCallback = (it) -> {};
	private Consumer<File> roomFileDragCallback = (it) -> {};
	private Consumer<File> worldFileDragCallback = (it) -> {};
	private int selectedRoom = -1;
	private int dragRoom = -1;
	private int dragStartX = -1;
	private int dragStartY = -1;
	private int dragCurX = -1;
	private int dragCurY = -1;
	private boolean dirty = false;
	
	public PlanetView(ViewContext context) {
		this.context = context;
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
					roomSelectionCallback.accept(null);
					PlanetView.this.repaint();
					
					e.consume();
				}
				
			}
		});
		
		// This constructor has side effects
		new DropTarget(this, new DropTargetListener() {
			@Override
			public void dragEnter(DropTargetDragEvent e) {}
			@Override
			public void dragExit(DropTargetEvent e) {}
			@Override
			public void dragOver(DropTargetDragEvent e) {}
			@Override
			public void dropActionChanged(DropTargetDragEvent e) {}
			@Override
			public void drop(DropTargetDropEvent e) {
				e.acceptDrop(DnDConstants.ACTION_COPY);
				try {
					Transferable data = e.getTransferable();
					if (data.isDataFlavorSupported(DataFlavor.javaFileListFlavor)) {
						@SuppressWarnings("unchecked")
						List<File> files = (List<File>) data.getTransferData(DataFlavor.javaFileListFlavor);
						for(File f : files) {
							if (f.getName().endsWith(".mp_room")) {
								roomFileDragCallback.accept(f);
							} else if (f.getName().endsWith(".mp_world")) {
								worldFileDragCallback.accept(f);
							}
						}
					}
				} catch (Throwable t) {
					t.printStackTrace();
				}
			}
			
		});
		
		this.setFocusable(true);
	}
	
	public static final int CELL_SIZE = 14 * 3;
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
		BufferedImage bases = Assets.getCachedImage("minimap/bases.png").orElseGet(Assets::missingImage);
		BufferedImage walls = Assets.getCachedImage("minimap/walls.png").orElseGet(Assets::missingImage);
		BufferedImage doors = Assets.getCachedImage("minimap/doors.png").orElseGet(Assets::missingImage);
		BufferedImage icons = Assets.getCachedImage("minimap/icons_big.png").orElseGet(Assets::missingImage);
		ArrayList<Color> areaColors = new ArrayList<>();
		ArrayList<BufferedImage> areaBases = new ArrayList<>();
		for(AreaInfo area : world.areas()) {
			areaColors.add(area.color());
			areaBases.add(Palette.getColorizedCopy(bases, area.color()));
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
					
					//Color areaColor = areaColors.get(screen.area()); // TODO: Bounds-check
					//screen.paint(g, x, y, areaColor, ri == selectedRoom, ri == dragRoom, valid);
					
					
					int base = screen.json().getObject("MAP").getPrimitive("base").asInt().orElse(0) - 1;
					int area = screen.area();
					BufferedImage areaBase = (area >= 0 && area < areaBases.size()) ? areaBases.get(screen.area()) : areaBases.get(0);
					int baseAtlasX = base * 14;
					int baseAtlasY = 0;
					g.drawImage(areaBase, x, y, x+CELL_SIZE, y+CELL_SIZE, baseAtlasX, baseAtlasY, baseAtlasX+14, baseAtlasY+14, null);
					
					ArrayElement wallsArray = screen.json().getObject("MAP").getArray("walls");
					for(int dir=0; dir<=4; dir++) {
						// Draw walls
						int wall = wallsArray.getPrimitive(dir).asInt().orElse(0);
						int wallAtlasX = wall * 14;
						int wallAtlasY = dir * 14;
						g.drawImage(walls, x, y, x+CELL_SIZE, y+CELL_SIZE, wallAtlasX, wallAtlasY, wallAtlasX+14, wallAtlasY+14, null);
					}
					
					// Draw icons
					ArrayElement iconsArray = screen.json().getObject("MAP").getArray("icons");
					if (!iconsArray.isEmpty()) {
						int icon = (int) iconsArray.getPrimitive(0).orElse(-1) - 1;
						int iconAtlasX = icon * 14;
						int iconAtlasY = 0;
						g.drawImage(icons, x, y, x+CELL_SIZE, y+CELL_SIZE, iconAtlasX, iconAtlasY, iconAtlasX+14, iconAtlasY+14, null);
					}
					
					ArrayElement doorsArray = screen.json().getObject("MAP").getArray("doors");
					for(int dir=0; dir<=4; dir++) {
						int door = doorsArray.getPrimitive(dir).asInt().orElse(-1);
						int doorAtlasX = door * 14;
						int doorAtlasY = dir * 14;
						g.drawImage(doors, x, y, x+CELL_SIZE, y+CELL_SIZE, doorAtlasX, doorAtlasY, doorAtlasX+14, doorAtlasY+14, null);
					}
					
					if (ri == selectedRoom) {
						g.setColor(new Color(255, 255, 255, 80));
						g.fillRect(x,  y, CELL_SIZE, CELL_SIZE);
					}
					
					if (!valid) {
						g.setColor(new Color(255, 0, 0, 128));
						g.fillRect(x,  y, CELL_SIZE, CELL_SIZE);
					}
					
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
		
		int maxX = 1;
		int maxY = 1;
		for(RoomInfo room : world.rooms()) {
			for(ScreenInfo screen : room.screens()) {
				maxX = Math.max(maxX, screen.x());
				maxY = Math.max(maxY, screen.y());
			}
		}
		Dimension sz = new Dimension((maxX + 2) * CELL_SIZE, (maxY + 2) * CELL_SIZE);
		this.setPreferredSize(sz);
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		//System.out.println(e.getButton());
		this.requestFocusInWindow();
		
		int clickedRoom = roomAt(e.getX() / CELL_SIZE, e.getY() / CELL_SIZE);
		if (clickedRoom == -1) {
			if (selectedRoom != -1) {
				selectedRoom = -1;
				propertiesConsumer.accept(null, null);
				roomSelectionCallback.accept(null);
				this.repaint();
			}
		} else {
			
			selectedRoom = clickedRoom;
			RoomInfo room = world.rooms().get(clickedRoom);
			propertiesConsumer.accept(room.general(), WorldEditor.ROOM_GENERAL_SCHEMA);
			roomSelectionCallback.accept(room);
			
			
			if (e.getButton() == MouseEvent.BUTTON3) {
				roomRightClickCallback.accept(room, new Vec2(e.getX(), e.getY()));
			}
			
			this.repaint();
		}
		
		if (e.getClickCount() == 2) {
			// Let's go!
			if (clickedRoom < 0) return;
			System.out.println("Opening configurator for room " + clickedRoom);
			roomDoubleClickCallback.accept(world.rooms().get(clickedRoom));
			//TODO: Fix
			//RoomConfiguratorView configurator = new RoomConfiguratorView(context, world, clickedRoom);
			//configurator.setVisible(true);
		}
		
	}

	@Override
	public void mouseEntered(MouseEvent arg0) {}

	@Override
	public void mouseExited(MouseEvent arg0) {}

	@Override
	public void mousePressed(MouseEvent e) {
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
				// Transfer from drag into select
				selectedRoom = dragRoom;
				roomSelectionCallback.accept(world.rooms().get(selectedRoom));
				
				// We didn't actually drag. Kill the operation
				dragRoom = -1;
				dragStartX = -1;
				dragStartY = -1;
				dragCurX = -1;
				dragCurY = -1;
				
				selectedRoom = dragRoom;
				
				this.repaint();
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
				context.markUnsaved();
				
				for(ScreenInfo s : room.screens()) {
					s.setPosition(s.x() + dx, s.y() + dy);
				}
				
				// De-stitch doors
				DoorConnectionLogic.unstitchAll(world, room);
				
				//Stitch doors
				
				for(ScreenInfo s : room.screens()) {
					for(ObjectElement door : s.doors()) { // TODO: Better way to do this
						DoorInfo reified = new DoorInfo(world, room, s, door);
						Vec2 dest = new Vec2(s.x(), s.y()).add(reified.direction().offset());
						
						int destRoomId = roomAt(dest.x(), dest.y());
						if (destRoomId != -1) {
							RoomInfo destRoom = world.rooms().get(destRoomId);
							
							findOppositeDoor:
							for(ScreenInfo destScreen : destRoom.screens()) {
								if (destScreen.x() == dest.x() && destScreen.y() == dest.y()) {
									for(ObjectElement destDoor : destScreen.doors()) {
										DoorInfo reifiedDest = new DoorInfo(world, destRoom, destScreen, destDoor);
										if (reifiedDest.direction() == reified.direction().opposite()) {
											DoorConnectionLogic.stitch(reified, reifiedDest);
											break findOppositeDoor;
										}
									}
								}
							}
						}
					}
				}
				
				// De-stitch elevators
				for(ScreenInfo screen : room.screens()) {
					for(ValueElement val : screen.json().getArray("ELEVATORS")) {
						if (val instanceof ObjectElement elevator) {
							int destRm = elevator.getPrimitive("dest_rm").asInt().orElse(-2);
							int destId = elevator.getPrimitive("dest_id").asInt().orElse(0);
							if (destRm > 0 && destRm < world.rooms().size()) {
								RoomInfo destRoom = world.rooms().get(destRm);
								Optional<ElevatorInfo> maybeDest = destRoom.getElevator(world, destId);
								maybeDest.ifPresent(ElevatorInfo::clearDestination);
							}
							// De-stitch the source elevator
							elevator.put("dest_rm", PrimitiveElement.of(-2));
							elevator.put("dest_id", PrimitiveElement.of(0));
						}
					}
				}
				
				// Stitch elevators
				for(ScreenInfo screen : room.screens()) {
					for(ValueElement val : screen.json().getArray("ELEVATORS")) {
						if (val instanceof ObjectElement elevatorObj) {
							ElevatorInfo sourceElevator = new ElevatorInfo(world, room, screen, elevatorObj);
							
							switch(sourceElevator.direction()) {
								case WEST, EAST -> {} // For now, don't touch these!
								case NORTH -> {
									int xi = screen.x();
									for(int yi = screen.y() - 1; yi >= 0; yi--) {
										//System.out.println("Searching "+xi+", "+yi);
										Optional<PlacedScreen> maybeScreen = world.screenAt(xi, yi);
										if (maybeScreen.isPresent()) {
											if (maybeScreen.get().room().equals(room)) continue; // Don't link to ourselves
											if (maybeScreen.get().screen().hasElevators()) {
												// We need to either bail (because we've hit an elevator that won't
												// connect) or stitch to a matched elevator
												Optional<ElevatorInfo> match = maybeScreen.get().getElevator(Direction.SOUTH);
												if (match.isPresent()) {
													world.linkElevators(sourceElevator, match.get());
													break;
												}
											}
										}
									}
								}
								
								case SOUTH -> {
									int xi = screen.x();
									for(int yi = screen.y() + 1; yi<=70; yi++) {
										Optional<PlacedScreen> maybeScreen = world.screenAt(xi, yi);
										if (maybeScreen.isPresent()) {
											if (maybeScreen.get().room().equals(room)) continue; // Don't link to ourselves
											if (maybeScreen.get().screen().hasElevators()) {
												// We need to either bail (because we've hit an elevator that won't
												// connect) or stitch to a matched elevator
												Optional<ElevatorInfo> match = maybeScreen.get().getElevator(Direction.NORTH);
												if (match.isPresent()) {
													world.linkElevators(sourceElevator, match.get());
													break;
												}
											}
										}
									}
									
								}
								default -> {}
							}
							
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
				
				
				for(ValueElement val : world.json().getArray("PROGRESSION_LOG")) {
					if (val instanceof ObjectElement entry) {
						int entryRoomId = entry.getPrimitive("room_id").asInt().orElse(-1);
						if (entryRoomId == dragRoom) {
							int oldX = entry.getPrimitive("x").asInt().orElse(0);
							int oldY = entry.getPrimitive("y").asInt().orElse(0);
							entry.put("x", PrimitiveElement.of(oldX + dx));
							entry.put("y", PrimitiveElement.of(oldY + dy));
						}
					}
				}
				
			}
			
			dragRoom = -1;
			dragStartX = -1;
			dragStartY = -1;
			dragCurX = -1;
			dragCurY = -1;
			
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

	@Nullable
	public RoomInfo getSelectedRoom() {
		if (selectedRoom < 0 || selectedRoom >= world.rooms().size()) return null;
		return world.rooms().get(selectedRoom);
	}

	public void setRoomSelectionCallback(Consumer<RoomInfo> callback) {
		this.roomSelectionCallback = callback;
		
	}

	public void setRoomDoubleClickCallback(Consumer<RoomInfo> callback) {
		this.roomDoubleClickCallback = callback;
	}
	
	public void setRoomRightClickCallback(BiConsumer<RoomInfo, Vec2> callback) {
		this.roomRightClickCallback = callback;
	}
	
	public void setRoomFileDragCallback(Consumer<File> callback) {
		this.roomFileDragCallback = callback;
	}
	
	public void setWorldFileDragCallback(Consumer<File> callback) {
		this.worldFileDragCallback = callback;
	}
	
}
