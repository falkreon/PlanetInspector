package blue.endless.pi;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.InflaterInputStream;

import blue.endless.jankson.api.Jankson;
import blue.endless.jankson.api.SyntaxError;
import blue.endless.jankson.api.document.ArrayElement;
import blue.endless.jankson.api.document.KeyValuePairElement;
import blue.endless.jankson.api.document.ObjectElement;
import blue.endless.jankson.api.document.PrimitiveElement;
import blue.endless.jankson.api.document.ValueElement;
import blue.endless.jankson.api.io.ObjectWriterFactory;
import blue.endless.jankson.api.io.StructuredData;
import blue.endless.jankson.api.io.StructuredDataReader;
import blue.endless.jankson.api.io.ValueElementReader;
import blue.endless.jankson.api.io.ValueElementWriter;
import blue.endless.jankson.impl.io.objectwriter.RecordDeserializer;
import blue.endless.pi.room.Block;
import blue.endless.pi.room.Door;
import blue.endless.pi.room.Elevator;
import blue.endless.pi.room.Enemy;
import blue.endless.pi.room.General;
import blue.endless.pi.room.Hazard;
import blue.endless.pi.room.Map;
import blue.endless.pi.room.Meta;
import blue.endless.pi.room.PaletteEntry;
import blue.endless.pi.room.PathNode;
import blue.endless.pi.room.Screen;
import blue.endless.pi.room.ScreenObject;

public class App {
	
	public static void main(String... args) {
		
		Path worldFile = Path.of("test.mp_room");
		
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
			
			//data = zin.readAllBytes();
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(-1);
		}
		
		try {
			processRoom(files);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (SyntaxError e) {
			e.printStackTrace();
		}
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
	
	
	public static void processRoom(List<byte[]> roomFiles) throws IOException, SyntaxError {
		if (roomFiles.size() != 1) {
			throw new IllegalArgumentException("Typically rooms contain only one json file. We don't know how to process more than that.");
		}
		
		ObjectElement value = Jankson.readJsonObject(new ByteArrayInputStream(roomFiles.get(0)));
		//System.out.println(value.toString());
		for(KeyValuePairElement entry : value) {
			//System.out.println("Processing "+entry.getKey());
			
			if (entry.getKey().equals("GENERAL")) {
				General general = unpackRecord(entry.getValue(), General.class);
				System.out.println(general);
			} else if (entry.getKey().equals("HAZARD")) {
				Hazard hazard = unpackRecord(entry.getValue(), Hazard.class);
				System.out.println(hazard);
			} else if (entry.getKey().equals("META")) {
				Meta meta = unpackRecord(entry.getValue(), Meta.class);
				System.out.println(meta);
			} else if (entry.getKey().equals("PATHING")) {
				if (entry.getValue() instanceof ArrayElement arr) {
					ArrayList<PathNode> pathing = unpackRecordList(arr, PathNode.class);
					System.out.println("Pathing"+pathing.toString());
				} else {
					throw new IllegalStateException();
				}
			} else if (entry.getKey().equals("PALETTES")) {
				if (entry.getValue() instanceof ArrayElement arr) {
					ArrayList<PaletteEntry> palette = unpackRecordList(arr, PaletteEntry.class);
					System.out.println("Palette"+palette.toString());
				} else {
					throw new IllegalStateException();
				}
			} else if (entry.getKey().equals("EVENTS")) {
				if (entry.getValue() instanceof ArrayElement arr) {
					for(ValueElement val : arr) {
						System.out.println(val);
						throw new IllegalStateException("Let's capture this value!");
					}
				}
			} else if (entry.getKey().equals("SCREENS")) {
				if (entry.getValue() instanceof ArrayElement arr) {
					System.out.println("Processing screen info...");
					
					for(ValueElement val : arr) {
						if (val instanceof ObjectElement obj) {
							Screen scr = processScreen(obj);
							System.out.println("   " + scr);
						}
					}
					System.out.println("Processed.");
				} else {
					throw new IllegalStateException();
				}
			} else {
				System.out.println(entry.getKey() + ":");
				System.out.println(entry.getValue().toString());
			}
		}
	}
	
	public static Screen processScreen(ObjectElement screen) throws IOException, SyntaxError {
		//Screen result = unpackRecord(screen, Screen.class);
		//System.out.println(screenObj);
		
		int x = 0;
		int y = 0;
		ArrayList<Enemy> enemies = null;
		ArrayList<Door> doors = null;
		ArrayList<ScreenObject> objects = new ArrayList<>();
		ArrayList<Block> blocks = new ArrayList<>();
		ArrayList<Elevator> elevators = new ArrayList<>();
		
		Map map = null;
		
		ArrayList<ValueElement> tubes = new ArrayList<>();
		ArrayList<ValueElement> liquids = new ArrayList<>();
		ArrayList<ValueElement> decor = new ArrayList<>();
		
		
		long[] scrolls = new long[0];
		long[][][] tiles = new long[0][][];
		
		for(KeyValuePairElement entry : screen) {
			//System.out.println("  "+entry.getKey());
			
			if (entry.getKey().equals("ENEMIES")) {
				if (entry.getValue() instanceof ArrayElement arr) {
					enemies = unpackRecordList(arr, Enemy.class);
				}
			} else if (entry.getKey().equals("DOORS")) {
				if (entry.getValue() instanceof ArrayElement arr) {
					doors = unpackRecordList(arr, Door.class);
				}
			} else if (entry.getKey().equals("MAP")) {
				map = unpackRecord(entry.getValue(), Map.class);
			} else if (entry.getKey().equals("x")) {
				x = ((PrimitiveElement) entry.getValue()).asInt().getAsInt();
			} else if (entry.getKey().equals("y")) {
				y = ((PrimitiveElement) entry.getValue()).asInt().getAsInt();
			} else if (entry.getKey().equals("TUBES")) {
				if (entry.getValue() instanceof ArrayElement arr) {
					for(ValueElement val : arr) {
						tubes.add(val);
						System.out.println(val);
					}
				}
			} else if (entry.getKey().equals("OBJECTS")) {
				if (entry.getValue() instanceof ArrayElement arr) {
					objects = unpackRecordList(arr, ScreenObject.class);
				}
			} else if (entry.getKey().equals("SCROLLS")) {
				if (entry.getValue() instanceof ArrayElement arr) {
					scrolls = arr.asLongArray().orElse(scrolls);
				}
			} else if (entry.getKey().equals("LIQUIDS")) {
				if (entry.getValue() instanceof ArrayElement arr) {
					for(ValueElement val : arr) {
						liquids.add(val);
						System.out.println(val);
					}
				}
			} else if (entry.getKey().equals("DECOR")) {
				if (entry.getValue() instanceof ArrayElement arr) {
					for(ValueElement val : arr) {
						decor.add(val);
						System.out.println(val);
					}
				}
			} else if (entry.getKey().equals("BLOCKS")) {
				if (entry.getValue() instanceof ArrayElement arr) {
					blocks = unpackRecordList(arr, Block.class);
				}
			} else if (entry.getKey().equals("ELEVATORS")) {
				if (entry.getValue() instanceof ArrayElement arr) {
					elevators = unpackRecordList(arr, Elevator.class);
				}
			} else if (entry.getKey().equals("TILES")) {
				if (entry.getValue() instanceof ArrayElement yArr) {
					tiles = new long[yArr.size()][][];
					int yi = 0;
					
					for(ValueElement yVal : yArr) {
						if (yVal instanceof ArrayElement xArr) {
							tiles[yi] = new long[xArr.size()][];
							int xi = 0;
							
							for(ValueElement zVal : xArr) {
								if (zVal instanceof ArrayElement zArr) {
									tiles[yi][xi] = new long[zArr.size()];
									int zi = 0;
									
									for(ValueElement lVal : zArr) {
										if (lVal instanceof PrimitiveElement prim) {
											tiles[yi][xi][zi] = prim.asLong().getAsLong();
										}
										zi++;
									}
								}
									
								xi++;
							}
						}
						
						yi++;
					}
				}
			} else {
				System.out.println(entry.getKey()+":");
				System.out.println(entry.getValue().toString());
			}
		}
		
		Screen result = new Screen(decor, blocks, elevators, tubes, objects, liquids, map, enemies, doors, scrolls, tiles, x, y);
		return result;
	}
	
}