package blue.endless.pi.gui;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.beans.PropertyChangeListener;
import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.zip.Deflater;
import java.util.zip.DeflaterOutputStream;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;

import blue.endless.jankson.api.Jankson;
import blue.endless.jankson.api.SyntaxError;
import blue.endless.jankson.api.document.ArrayElement;
import blue.endless.jankson.api.document.ObjectElement;
import blue.endless.jankson.api.document.PrimitiveElement;
import blue.endless.jankson.api.document.ValueElement;
import blue.endless.jankson.api.io.json.JsonWriterOptions;
import blue.endless.pi.SchemaType;

/**
 * This is the main application window - but not a lot of real behavior lives here. It's mainly in the individual panels this frame hosts.
 */
public class EditorFrame extends JFrame {
	private static Map<String, SchemaType<?>> ROOM_SCHEMA = Map.of(
			"META", SchemaType.NON_EDITABLE,
			"GENERAL", SchemaType.NON_EDITABLE,
			"EVENTS", SchemaType.NON_EDITABLE,
			"PATHING", SchemaType.NON_EDITABLE,
			"HAZARD", SchemaType.NON_EDITABLE,
			"PALETTES", SchemaType.NON_EDITABLE,
			"SCREENS", SchemaType.NON_EDITABLE
			);
	
	private static Map<String, SchemaType<?>> SCREEN_SCHEMA = Map.of(
			"x", SchemaType.INT,
			"y", SchemaType.INT,
			"boss", SchemaType.NON_EDITABLE // boolean
			);
	
	static Map<String, SchemaType<?>> ROOM_GENERAL_SCHEMA = Map.ofEntries(
			Map.entry("name", SchemaType.STRING),
			Map.entry("designer", SchemaType.STRING),
			Map.entry("bg_color", SchemaType.INT),
			Map.entry("powered", SchemaType.NON_EDITABLE), // boolean
			Map.entry("no_floor", SchemaType.NON_EDITABLE), // boolean
			Map.entry("area", SchemaType.INT),
			Map.entry("sector", SchemaType.INT),
			Map.entry("bgm", SchemaType.STRING),
			Map.entry("gravity_multiplier", SchemaType.DOUBLE),
			Map.entry("focus", SchemaType.NON_EDITABLE),
			Map.entry("areas", SchemaType.NON_EDITABLE),
			Map.entry("magnet", SchemaType.NON_EDITABLE),
			Map.entry("tags", SchemaType.NON_EDITABLE)
			);
	
	private static Map<String, SchemaType<?>> WORLD_META_SCHEMA = Map.of(
			"id", SchemaType.INT,
			"world_version", SchemaType.DOUBLE,
			"version", SchemaType.DOUBLE,
			"description", SchemaType.STRING,
			"stats", SchemaType.DOUBLE,
			"name", SchemaType.STRING,
			"name_full", SchemaType.STRING,
			"key", SchemaType.DOUBLE
			);
	
	private ScrollingPlanetView planetView = new ScrollingPlanetView();
	private PropertyEditor propertyView = new PropertyEditor();
	
	private WorldInfo world;
	
	public EditorFrame() {
		this.setTitle("Planet Inspector");
		
		Container container = this.getContentPane();
		container.setLayout(new BorderLayout());
		container.add(planetView, BorderLayout.CENTER);
		container.add(propertyView, BorderLayout.EAST);
		
		this.setMinimumSize(new Dimension(800, 600));
		
		JMenuBar menuBar = new JMenuBar();
		JMenu fileMenu = new JMenu("File");
		JMenuItem saveAsMenuItem = new JMenuItem("Save As...");
		saveAsMenuItem.setAction(new AbstractAction("Save As...") {
			@Override
			public void actionPerformed(ActionEvent e) {
				saveAs();
			}
		});
		fileMenu.add(saveAsMenuItem);
		menuBar.add(fileMenu);
		
		JMenu mapMenu = new JMenu("Map");
		JMenuItem debugLogItem = new JMenuItem("Debug Log");
		debugLogItem.setAction(new AbstractAction("Debug Log") {
			@Override
			public void actionPerformed(ActionEvent e) {
				DebugLogFrame logFrame = new DebugLogFrame(world);
				logFrame.setVisible(true);
			}
		});
		mapMenu.add(debugLogItem);
		menuBar.add(mapMenu);
		
		
		this.setJMenuBar(menuBar);
		
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
	
	public void setWorld(ObjectElement obj, ObjectElement meta) {
		world = WorldInfo.of(obj, meta);
		
		//PlanetView.RoomInfo room = world.rooms().get(0);
		//propertyView.setObject(room.general(), ROOM_GENERAL_SCHEMA);
		/*
		ArrayElement roomsArray = obj.getArray("ROOMS");
		ObjectElement room = roomsArray.getObject(0);
		ObjectElement general = room.getObject("GENERAL");*/
		
		propertyView.setObject(meta, WORLD_META_SCHEMA);
		planetView.setWorld(world);
		planetView.setPropertiesConsumer((o, schema) -> {
			if (o == null) {
				propertyView.setObject(meta, WORLD_META_SCHEMA);
			} else {
				propertyView.setObject(o, schema);
			}
		});
	}
	
	public void saveAs() {
		try {
			/*
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			OutputStreamWriter writer = new OutputStreamWriter(baos, StandardCharsets.UTF_8);
			Jankson.writeJson(world.metaJson(), writer, JsonWriterOptions.ONE_LINE);
			writer.flush();
			byte[] metaFileBytes = baos.toByteArray();
			
			baos.reset();
			writer = new OutputStream(baos, StandardCharsets.UTF_8);
			Jankson.writeJson(world.json(), writer, JsonWriterOptions.ONE_LINE);
			*/
			world.metaJson().put("description", PrimitiveElement.of("MODIFIED WORLD\\nUSE WITH CARE"));
			world.metaJson().put("name", PrimitiveElement.of("PSEUDO-EUGENIA"));
			world.metaJson().put("name_full", PrimitiveElement.of("EUGENIA - DEMO 69420"));
			ObjectElement toolObj = new ObjectElement();
			toolObj.put("author", PrimitiveElement.of("FALKREON"));
			toolObj.put("editor_tool", PrimitiveElement.of("planet_inspector"));
			world.metaJson().put("external_editor", toolObj);
			
			
			FileOutputStream fileOut = new FileOutputStream("out.mp_world"); // TODO: File chooser
			DeflaterOutputStream deflaterOut = new DeflaterOutputStream(fileOut, new Deflater(), 4096, true);
			
			
			
			OutputStreamWriter writer = new OutputStreamWriter(deflaterOut, StandardCharsets.UTF_8);
			Jankson.writeJson(world.metaJson(), writer, JsonWriterOptions.ONE_LINE);
			writer.flush();
			deflaterOut.write(0);
			Jankson.writeJson(world.json(), writer, JsonWriterOptions.ONE_LINE);
			writer.flush();
			deflaterOut.write(0);
			
			deflaterOut.finish();
			deflaterOut.flush();
			deflaterOut.close();
			
			// --- diagnostics
			
			fileOut = new FileOutputStream("out.mp_world.json");
			//deflaterOut = new DeflaterOutputStream(fileOut, new Deflater(), 4096, true);
			writer = new OutputStreamWriter(fileOut, StandardCharsets.UTF_8);
			Jankson.writeJson(world.json(), writer, JsonWriterOptions.ONE_LINE);
			writer.flush();
			writer.close();
			
			// write meta
			
			fileOut = new FileOutputStream("meta_out.mp_world.json");
			writer = new OutputStreamWriter(fileOut, StandardCharsets.UTF_8);
			Jankson.writeJson(world.metaJson(), writer, JsonWriterOptions.ONE_LINE);
			writer.flush();
			writer.close();
			
			System.out.println("Saved.");
		} catch (IOException | SyntaxError ex) {
			ex.printStackTrace();
		}
	}
}
