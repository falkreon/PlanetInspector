package blue.endless.pi;

import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.zip.InflaterInputStream;

import javax.imageio.ImageIO;

import blue.endless.jankson.api.Jankson;
import blue.endless.jankson.api.SyntaxError;
import blue.endless.jankson.api.document.ArrayElement;
import blue.endless.jankson.api.document.ObjectElement;
import blue.endless.jankson.api.document.PrimitiveElement;
import blue.endless.jankson.api.document.ValueElement;
import blue.endless.jankson.api.io.StructuredDataReader;
import blue.endless.jankson.api.io.ValueElementReader;
import blue.endless.jankson.impl.io.objectwriter.RecordDeserializer;
import blue.endless.pi.room.Door;
import blue.endless.pi.room.Room;
import blue.endless.pi.room.Screen;

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
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(-1);
		}
		
		try {
			Room room = processRoom(files);
			
			int startX = Integer.MAX_VALUE;
			int startY = Integer.MAX_VALUE;
			int endX = Integer.MIN_VALUE;
			int endY = Integer.MIN_VALUE;
			for(Screen s : room.screens()) {
				if (s.x() < startX) startX = s.x();
				if (s.y() < startY) startY = s.y();
				if (s.x() > endX) endX = s.x();
				if (s.y() > endY) endY = s.y();
			}
			
			int roomWidth = endX - startX + 1;
			int roomHeight = endY - startY + 1;
			System.out.println("Width: ["+startX+".."+endX+"] = "+roomWidth);
			System.out.println("Height: ["+startY+".."+endY+"] = "+roomHeight);
			
			BufferedImage image = new BufferedImage(roomWidth * 20, roomHeight * 15, BufferedImage.TYPE_4BYTE_ABGR);
			
			for(Screen s : room.screens()) {
				//Screen first = room.screens().get(0);
				System.out.println("Coords: "+s.x()+", "+s.y());
				System.out.println(Arrays.toString(s.map().doors()));
				
				
				// Planets screens are 20x15
				long[][][] tiles = s.tiles();
				long[][] fg = s.tiles()[0];
				long[][] mid = s.tiles()[1];
				long[][] bg = s.tiles()[2];
				
				int baseX = (s.x() - startX) * 20;
				int baseY = (s.y() - startY) * 15;
	
				for(int y=0; y<15; y++) {
					for(int x=0; x<20; x++) {
						long fgTile = get(x, y, fg) & 0x7F;
						long midTile = get(x, y, mid) & 0x7F;
						long bgTile = get(x, y, bg) & 0x7F;
						long tile = fgTile;
						if (tile == 0) tile = midTile;
						if (tile == 0) tile = bgTile;
						
						String str = Long.toString(tile & 0x7F, 16);
						while(str.length() < 2) str = " " + str;
						System.out.print(str);
						System.out.print(" ");
						
						int rgb = 0xFF_000000;
						
						if (midTile != 0) {
							rgb = 0xFF_4444FF;
						} else if (fgTile != 0 || bgTile != 0) {
							//rgb = 0xFF_2222EE;
							rgb = 0xFF_0000AA;
						} else {
							rgb = 0xFF_0000AA;
						}
						
						// Override
						if (x == 0) {
							
						}
						
						image.setRGB(baseX + x, baseY + y, rgb);
					}
					System.out.println();
				}
				System.out.println();
				
				for(Door d : s.doors()) {
					System.out.println("Door at "+s.x()+","+s.y());
					System.out.println("Pos: " + d.pos()+", ClearOnUse: "+d.clearOnUse());
					
				}
				
				
				System.out.println("Walls: "+Arrays.toString(s.map().walls()));
				if (s.map().walls()[4] == 1) {
					//Top Wall
					for(int i=0; i<20; i++) {
						image.setRGB(baseX + i, baseY + 14, 0xFF_FFFFFF);
					}
				} else if (s.map().walls()[4] == 3) {
					for(int i=0; i<20; i++) {
						image.setRGB(baseX + i, baseY + 14, 0xFF_FFFFFF);
					}
					image.setRGB(baseX + 8, baseY + 14, 0xFF_0000FF);
					image.setRGB(baseX + 9, baseY + 14, 0xFF_0000FF);
					image.setRGB(baseX + 10, baseY + 14, 0xFF_0000FF);
					image.setRGB(baseX + 11, baseY + 14, 0xFF_0000FF);
				}
				
				if (s.map().walls()[3] == 1) {
					// Left Wall
					for(int i=0; i<15; i++) {
						image.setRGB(baseX, baseY + i, 0xFF_FFFFFF);
					}
				} else if (s.map().walls()[3] == 3) {
					// Left Door
					for(int i=0; i<15; i++) {
						image.setRGB(baseX, baseY + i, 0xFF_FFFFFF);
					}
					image.setRGB(baseX, baseY + 5, 0xFF_0000FF);
					image.setRGB(baseX, baseY + 6, 0xFF_0000FF);
					image.setRGB(baseX, baseY + 7, 0xFF_0000FF);
				}
				
				if (s.map().walls()[2] == 1) {
					//Top Wall
					for(int i=0; i<20; i++) {
						image.setRGB(baseX + i, baseY, 0xFF_FFFFFF);
					}
				} else if (s.map().walls()[2] == 3) {
					for(int i=0; i<20; i++) {
						image.setRGB(baseX + i, baseY, 0xFF_FFFFFF);
					}
					image.setRGB(baseX + 8, baseY, 0xFF_0000FF);
					image.setRGB(baseX + 9, baseY, 0xFF_0000FF);
					image.setRGB(baseX + 10, baseY, 0xFF_0000FF);
					image.setRGB(baseX + 11, baseY, 0xFF_0000FF);
				}
				
				if (s.map().walls()[1] == 1) {
					for(int i=0; i<15; i++) {
						image.setRGB(baseX + 19, baseY + i, 0xFF_FFFFFF);
					}
				} else if (s.map().walls()[1] == 3) {
					// Right Door
					for(int i=0; i<15; i++) {
						image.setRGB(baseX + 19, baseY + i, 0xFF_FFFFFF);
					}
					image.setRGB(baseX + 19, baseY + 5, 0xFF_0000FF);
					image.setRGB(baseX + 19, baseY + 6, 0xFF_0000FF);
					image.setRGB(baseX + 19, baseY + 7, 0xFF_0000FF);
				}
				
				/*
				if (s.map().doors()[3] == 1) {
					for(int i=0; i<5; i++) image.setRGB(baseX, baseY + i, 0xFF_FFFFFF);
					image.setRGB(baseX, baseY + 5, 0xFF_0000FF);
					image.setRGB(baseX, baseY + 6, 0xFF_0000FF);
					image.setRGB(baseX, baseY + 7, 0xFF_0000FF);
					for(int i=8; i<15; i++) image.setRGB(baseX, baseY + i, 0xFF_FFFFFF);
				}*/
			}
			
			
			//room.general().visionLimit = 1;
			//room.general().bgColor = 2;
			
			File f = new File("image.png");
			ImageIO.write(image, "png", f);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (SyntaxError e) {
			e.printStackTrace();
		}
	}
	
	/*
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
	}*/
	
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
		System.out.println(room);
		
		return room;
	}
	
}