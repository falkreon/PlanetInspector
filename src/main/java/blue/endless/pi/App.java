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
import blue.endless.jankson.api.document.KeyValuePairElement;
import blue.endless.jankson.api.document.ObjectElement;
import blue.endless.jankson.api.document.ValueElement;
import blue.endless.jankson.api.io.ObjectWriterFactory;
import blue.endless.jankson.api.io.StructuredData;
import blue.endless.jankson.api.io.StructuredDataReader;
import blue.endless.jankson.api.io.ValueElementReader;
import blue.endless.jankson.api.io.ValueElementWriter;
import blue.endless.jankson.impl.io.objectwriter.RecordDeserializer;

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
	
	
	public static void processRoom(List<byte[]> roomFiles) throws IOException, SyntaxError {
		if (roomFiles.size() != 1) {
			throw new IllegalArgumentException("Typically rooms contain only one json file. We don't know how to process more than that.");
		}
		
		ObjectElement value = Jankson.readJsonObject(new ByteArrayInputStream(roomFiles.get(0)));
		for(KeyValuePairElement entry : value) {
			System.out.println(entry.getKey() + ":");
			System.out.println(entry.getValue().toString());
			if (entry.getKey().equals("GENERAL")) {
				System.out.println("Processing general info");
				StructuredDataReader reader = ValueElementReader.of(entry.getValue());
				System.out.println(reader.getClass().getCanonicalName());
				RecordDeserializer<RoomGeneral> writer = new RecordDeserializer<>(RoomGeneral.class);
				reader.transferTo(writer);
				RoomGeneral room = writer.getResult();
				if (room == null) throw new IllegalStateException();
				System.out.println(room);
			}
		}
	}
	
}