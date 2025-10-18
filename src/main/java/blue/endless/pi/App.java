package blue.endless.pi;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.InflaterInputStream;

import javax.imageio.ImageIO;

import blue.endless.jankson.api.Jankson;
import blue.endless.jankson.api.SyntaxError;
import blue.endless.jankson.api.document.ArrayElement;
import blue.endless.jankson.api.document.KeyValuePairElement;
import blue.endless.jankson.api.document.ObjectElement;
import blue.endless.jankson.api.document.PrimitiveElement;
import blue.endless.jankson.api.document.ValueElement;
import blue.endless.jankson.api.io.StructuredDataReader;
import blue.endless.jankson.api.io.ValueElementReader;
import blue.endless.jankson.api.io.json.JsonWriterOptions;
import blue.endless.jankson.impl.io.objectwriter.RecordDeserializer;
import blue.endless.pi.datastruct.Vec2;
import blue.endless.pi.gui.EditorFrame;
import blue.endless.pi.gui.Tileset;
import blue.endless.pi.room.Door;
import blue.endless.pi.room.MinimapCell;
import blue.endless.pi.room.Room;
import blue.endless.pi.world.WorldMeta;

public class App {
	public static final int PALETTE_MASK = 0x1F << 12; // Bits 12-16, with palettes 00-0F being user palettes and 10-1F being system palettes
	public static final int ANIMATION_MASK = 0x7 << 17; // Bits 17-19
	public static final int CONVEYOR_MASK = 0x03 << 20; // Bits 20 and 21
	public static final int MIRRORED = 1 << 28;
	public static final int FLIPPED = 1 << 29;
	public static final int ROTATED = 1 << 30;
	
	public static void main(String... args) {
		Tileset.init();
		Optional<ObjectElement> itemsJson = Assets.readObject("items/items.json");
		if (itemsJson.isPresent()) {
			ItemType.load(itemsJson.get());
		}
		
		EditorFrame editor = new EditorFrame();
		editor.setVisible(true); // Launch the app proper!
		
		/*
		Path worldFile = Path.of("test.mp_world");
		
		ArrayList<byte[]> files = new ArrayList<>();
		ByteArrayOutputStream bytes = new ByteArrayOutputStream();
		try(InputStream in = Files.newInputStream(worldFile, StandardOpenOption.READ)) {
			InflaterInputStream zin = new InflaterInputStream(new BufferedInputStream(in));
			
			int data = 0;
			while(data != -1) {
				data = zin.read();
				if (data == 0) {
					if (bytes.size() > 0) {
						files.add(bytes.toByteArray());
						bytes.reset();
					}
				} else if (data == -1) {
					break;
				} else {
					bytes.write(data);
				}
			}
			
			if (bytes.size() > 0) {
				files.add(bytes.toByteArray());
			}
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(-1);
		}
		
		try(OutputStream out = Files.newOutputStream(Path.of("out_raw.mp_world"), StandardOpenOption.WRITE, StandardOpenOption.CREATE)) {
			DeflaterOutputStream dout = new DeflaterOutputStream(new BufferedOutputStream(out));
			
			ObjectElement worldMetaObj = Jankson.readJsonObject(new ByteArrayInputStream(files.get(0)));
			//System.out.println(worldMetaObj.toString());
			worldMetaObj.put("description", PrimitiveElement.of("THIS IS A MODIFIED WORLD\nUSE WITH CARE"));
			worldMetaObj.put("name_full", PrimitiveElement.of("EUGENIA - DEMO -69420"));
			
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			OutputStreamWriter writer = new OutputStreamWriter(baos, StandardCharsets.UTF_8);
			Jankson.writeJson(worldMetaObj, writer, JsonWriterOptions.ONE_LINE);
			writer.flush();
			byte[] metaFileBytes = baos.toByteArray();
			
			
			dout.write(metaFileBytes);
			//dout.write(files.get(0));
			dout.write(0);
			dout.write(files.get(1));
			dout.write(0);
			
			dout.finish();
			dout.flush();
			
		} catch (IOException | SyntaxError e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			System.exit(-1);
		}
		
		try {
			processWorld(files);
			
			
		} catch (IOException e) {
			e.printStackTrace();
		} catch (SyntaxError e) {
			e.printStackTrace();
		}*/
	}
	
	
	public static <T> ArrayList<T> unpackRecordList(ArrayElement arr, Class<T> clazz) throws IOException, SyntaxError {
		ArrayList<T> result = new ArrayList<>();
		for(ValueElement val : arr) {
			StructuredDataReader reader = ValueElementReader.of(val);
			RecordDeserializer<T> writer = new RecordDeserializer<>(clazz);
			reader.transferTo(writer);
			T t = writer.getResult();
			if (t != null) result.add(t);
		}
		return result;
	}
	
	public static <T> T unpackRecord(ValueElement val, Class<T> clazz) throws IOException, SyntaxError {
		StructuredDataReader reader = ValueElementReader.of(val);
		RecordDeserializer<T> writer = new RecordDeserializer<>(clazz);
		reader.transferTo(writer);
		T result = writer.getResult();
		if (result == null) throw new IOException();
		return result;
	}
	
	/*
	public static long[] unpackLongArray(ArrayElement arr) {
		long[] result = new long[arr.size()];
		int i = 0;
		
		for(ValueElement val : arr) {
			if (val instanceof PrimitiveElement prim) {
				result[i] = prim.asLong().getAsLong();
			}
			i++;
		}
		
		return result;
	}
	
	public static long[][] unpackLongArrayArray(ArrayElement arr) {
		long[][] result = new long[arr.size()][];
		int i=0;
		for(ValueElement val : arr) {
			if (val instanceof ArrayElement arr2) {
				result[i] = unpackLongArray(arr2);
			}
			i++;
		}
		
		return result;
	}
	
	public static long[][][] unpackLongArrayArrayArray(ArrayElement arr) {
		long[][][] result = new long[arr.size()][][];
		int i=0;
		for(ValueElement val : arr) {
			if (val instanceof ArrayElement arr2) {
				result[i] = unpackLongArrayArray(arr2);
			}
			i++;
		}
		
		return result;
	}*/
	
	public static int[] unpackIntArray(ArrayElement arr) {
		int[] result = new int[arr.size()];
		int i = 0;
		
		for(ValueElement val : arr) {
			if (val instanceof PrimitiveElement prim) {
				result[i] = prim.asInt().orElse((int) prim.asDouble().orElse(0));
			}
			i++;
		}
		
		return result;
	}
	
	public static int[][] unpackIntArray2D(ArrayElement arr) {
		int[][] result = new int[arr.size()][];
		int i=0;
		for(ValueElement val : arr) {
			if (val instanceof ArrayElement arr2) {
				result[i] = unpackIntArray(arr2);
			}
			i++;
		}
		
		return result;
	}
	
	public static int[][][] unpackIntArray3D(ArrayElement arr) {
		int[][][] result = new int[arr.size()][][];
		int i=0;
		for(ValueElement val : arr) {
			if (val instanceof ArrayElement arr2) {
				result[i] = unpackIntArray2D(arr2);
			}
			i++;
		}
		
		return result;
	}
	
	
	private static long get(int x, int y, long[][] data) {
		if (data.length > x) {
			long[] column = data[x];
			if (column.length > y) {
				return column[y];
			}
		}
		
		return 0L;
	}
	
	public static Room processRoom(List<byte[]> roomFiles) throws IOException, SyntaxError {
		if (roomFiles.size() != 1) {
			throw new IllegalArgumentException("Typically rooms contain only one json file. We don't know how to process more than that.");
		}
		
		ObjectElement value = Jankson.readJsonObject(new ByteArrayInputStream(roomFiles.get(0)));
		Room room = unpackRecord(value, Room.class);
		
		Files.write(Path.of("room_file.json"), roomFiles.get(0));
		try(FileWriter fw = new FileWriter(new File("jankson_out_room.json"))) {
			Jankson.writeJson(value, fw, JsonWriterOptions.STRICT);
			fw.flush();
		}
		
		System.out.println(room);
		
		
		return room;
	}
	
	public static void processWorld(List<byte[]> worldFiles) throws IOException, SyntaxError {
		if (worldFiles.size() != 2) {
			throw new IllegalArgumentException("Expected 2 embedded jsons");
		}
		
		ObjectElement worldMetaObj = Jankson.readJsonObject(new ByteArrayInputStream(worldFiles.get(0)));
		WorldMeta worldMeta = unpackRecord(worldMetaObj, WorldMeta.class);
		System.out.println(worldMeta);
		
		ObjectElement worldObj = Jankson.readJsonObject(new ByteArrayInputStream(worldFiles.get(1)));
		Files.write(Path.of("world_file.json"), worldFiles.get(1));
		try(FileWriter fw = new FileWriter(new File("jankson_out_world.json"))) {
			Jankson.writeJson(worldObj, fw, JsonWriterOptions.STRICT);
			fw.flush();
		}
		
		Map<Vec2, MinimapCell> minimap = new HashMap<>();
		List<Area> areas = new ArrayList<>();
		Map<Vec2, List<Door>> roomDoors = new HashMap<>();
		Map<Vec2, Integer> cellToRoom = new HashMap<>();
		//Multimap<Vec2, Door> doors;
		
		for(KeyValuePairElement kvp : worldObj) {
			String key = kvp.getKey();
			
			switch(key) {
				case "HAZARDS" -> {
					
				}
				case "MODES" -> {
					
				}
				case "GENERATION_DEBUG_LOG" -> {
					
				}
				case "EVENTS" -> {
					
				}
				case "ITEM_DATA" -> {
					if (kvp.getValue() instanceof ArrayElement arr) {
						Set<ItemType> itemsPresent = new HashSet<>();
						int i = 0;
						for(ValueElement val : arr) {
							ItemType item = ItemType.of(i);
							//if (item == Item.INVALID) System.out.println(i+": "+val);
							itemsPresent.add(ItemType.of(i));
							i++;
						}
						
						//System.out.println("Items Described: "+itemsPresent.toString());
					}
				}
				case "FLAGS" -> {
					
				}
				case "LOGS" -> {
					
				}
				case "CUTSCENES" -> {
					
				}
				case "AREAS" -> {
					if (kvp.getValue() instanceof ArrayElement arr) {
						for(ValueElement areaElem : arr) {
							if (areaElem instanceof ObjectElement obj) {
								areas.add(new Area(obj));
							}
						}
					}
				}
				case "TIMERS" -> {
					
				}
				case "SPAWN_POINTS" -> {
					
				}
				case "SYSTEM_PALETTES" -> {
					
				}
				case "GENERAL" -> {
					//System.out.println(kvp.getValue());
				}
				case "VALUES" -> {
					
				}
				case "ROOMS" -> {
					
					
					if (kvp.getValue() instanceof ArrayElement arr) {
						int roomId = 0;
						for(ValueElement roomElem : arr) {
							/*
							try {
								Room room = unpackRecord(val, Room.class);
							} catch (Throwable t) {
								//System.out.println(val);
							}*/
							
							
							if (roomElem instanceof ObjectElement room) {
								//System.out.println("Room id: "+roomId);
								ArrayElement screens = room.getArray("SCREENS");
								for(ValueElement screenElem : screens) {
									if (screenElem instanceof ObjectElement screen) {
										int x = screen.getPrimitive("x").asInt().orElse(-1);
										int y = screen.getPrimitive("y").asInt().orElse(-1);
										if (x == -1 || y == -1) {
											System.out.println("MISPLACED SCREEN??");
										}
										cellToRoom.put(new Vec2(x, y), roomId);
										
										ObjectElement minimapElem = screen.getObject("MAP");
										MinimapCell cell = new MinimapCell();
										if (minimapElem.size() > 0) {
											cell = unpackRecord(minimapElem, MinimapCell.class);
											minimap.put(new Vec2(x, y), cell);
										}
										
										ArrayElement doorArray = screen.getArray("DOORS");
										//System.out.println(doorArray);
										Arrays.fill(cell.doors(), -1);
										for(ValueElement doorElem : doorArray) {
											List<Door> doorsList = roomDoors.computeIfAbsent(new Vec2(x, y), (vec)->new ArrayList<>());
											//System.out.println("  door @ "+x+","+y+": "+doorElem);
											if (doorElem instanceof ObjectElement obj) {
												Door d = new Door(obj);
												doorsList.add(d);
												cell.doors()[d.pos()] = d.type();
											}
										}
									} else {
										System.out.println("FAILED door conversion");
									}
								}
							}
							
							roomId++;
						}
						//ArrayList<Room> rooms = unpackRecordList(arr, Room.class);
						//System.out.println("  "+rooms.size()+" rooms unpacked.");
					}
					
					//System.out.println("Minimap: "+minimap);
					
					
				}
				case "RULES" -> {
					//System.out.println(kvp.getValue());
				}
				case "LIQUIDS_DAMAGE" -> {
					
				}
				case "PROGRESSION_LOG" -> {
					
				}
				case "SAMUS" -> {
					
				}
				case "SECTORS" -> {
					
				}
				case "ENEMY_DATA" -> {
					
				}
				default -> {
					System.out.println("Unhandled key: " + kvp.getKey());
				}
			}
			
			//System.out.println(kvp.getValue());
		}
		
		
		// Render the minimap
		//Figure out world size
		if (minimap.size() > 0) {
			int minX = Integer.MAX_VALUE;
			int minY = Integer.MAX_VALUE;
			int maxX = Integer.MIN_VALUE;
			int maxY = Integer.MIN_VALUE;
			
			for(Vec2 s : minimap.keySet()) {
				minX = Math.min(minX, s.x());
				maxX = Math.max(maxX, s.x());
				minY = Math.min(minY, s.y());
				maxY = Math.max(maxY, s.y());
			}
			
			int cellsWide = maxX - minX + 1;
			int cellsHigh = maxY - minY + 1;
			
			BufferedImage mapImage = new BufferedImage(cellsWide * 7, cellsHigh * 7, BufferedImage.TYPE_INT_ARGB);
			Graphics g = mapImage.getGraphics();
			for(Map.Entry<Vec2, MinimapCell> entry : minimap.entrySet()) {
				int cellX = entry.getKey().x() - minX;
				int cellY = entry.getKey().y() - minY;
				
				int dx = cellX * 7;
				int dy = cellY * 7;
				
				Integer roomId = cellToRoom.get(new Vec2(cellX, cellY));
				List<Door> roomDoorList = (roomId == null) ? new ArrayList<>() : roomDoors.computeIfAbsent(new Vec2(cellX, cellY), (id) -> new ArrayList<>());
				entry.getValue().paint(g, dx, dy, areas, roomDoorList);
			}
			g.dispose();
			
			File f = new File("world_map.png");
			ImageIO.write(mapImage, "png", f);
		}
		
		EditorFrame editor = new EditorFrame();
		editor.setWorld(worldObj, worldMetaObj);
		editor.setVisible(true); // Launch the app proper!
	}
	
}