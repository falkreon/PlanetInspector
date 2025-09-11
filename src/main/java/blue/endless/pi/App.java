package blue.endless.pi;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.InflaterInputStream;

import blue.endless.jankson.api.Jankson;
import blue.endless.jankson.api.SyntaxError;
import blue.endless.jankson.api.document.ArrayElement;
import blue.endless.jankson.api.document.ObjectElement;
import blue.endless.jankson.api.document.PrimitiveElement;
import blue.endless.jankson.api.document.ValueElement;
import blue.endless.jankson.api.io.StructuredDataReader;
import blue.endless.jankson.api.io.ValueElementReader;
import blue.endless.jankson.impl.io.objectwriter.RecordDeserializer;
import blue.endless.pi.room.Room;

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
			processRoom(files);
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