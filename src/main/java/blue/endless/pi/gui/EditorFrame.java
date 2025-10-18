package blue.endless.pi.gui;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.zip.Deflater;
import java.util.zip.DeflaterOutputStream;

import javax.swing.AbstractAction;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.filechooser.FileNameExtensionFilter;

import blue.endless.jankson.api.Jankson;
import blue.endless.jankson.api.SyntaxError;
import blue.endless.jankson.api.document.ArrayElement;
import blue.endless.jankson.api.document.ObjectElement;
import blue.endless.jankson.api.document.PrimitiveElement;
import blue.endless.jankson.api.io.json.JsonWriterOptions;
import blue.endless.pi.SchemaType;

/**
 * This is the main application window - but not a lot of real behavior lives here. It's mainly in the individual panels this frame hosts.
 */
public class EditorFrame extends JFrame {
	static Map<String, SchemaType<?>> ROOM_SCHEMA = Map.of(
			"META", SchemaType.NON_EDITABLE,
			"GENERAL", SchemaType.NON_EDITABLE,
			"EVENTS", SchemaType.NON_EDITABLE,
			"PATHING", SchemaType.NON_EDITABLE,
			"HAZARD", SchemaType.NON_EDITABLE,
			"PALETTES", SchemaType.NON_EDITABLE,
			"SCREENS", SchemaType.NON_EDITABLE
			);
	
	static Map<String, SchemaType<?>> SCREEN_SCHEMA = Map.of(
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
		JMenuItem openMenuItem = new JMenuItem("Open...");
		openMenuItem.setAction(new AbstractAction("Open...") {
			@Override
			public void actionPerformed(ActionEvent e) {
				open();
			}
		});
		fileMenu.add(openMenuItem);
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
	
	public void setWorld(WorldInfo world) {
		propertyView.setObject(world.metaJson(), WORLD_META_SCHEMA);
		planetView.setWorld(world);
		planetView.setPropertiesConsumer((o, schema) -> {
			if (o == null) {
				propertyView.setObject(world.metaJson(), WORLD_META_SCHEMA);
			} else {
				propertyView.setObject(o, schema);
			}
		});
	}
	
	public void open() {
		try {
			JFileChooser chooser = new JFileChooser();
			FileNameExtensionFilter filter = new FileNameExtensionFilter("Planets Enigma worlds", "mp_world");
			chooser.setFileFilter(filter);
			File basePath = new File(".").getCanonicalFile();
			int result = chooser.showOpenDialog(this);
			if (result == JFileChooser.CANCEL_OPTION) {
				return;
			}
			if (result == JFileChooser.ERROR_OPTION) {
				System.out.println("Error selecting a world file.");
				return;
			}
			if (result != JFileChooser.APPROVE_OPTION) {
				System.out.println("Unknown result code: "+result);
				return;
			}
			File roomFile = chooser.getSelectedFile();
			
			
			WorldInfo world = WorldInfo.load(roomFile.toPath());
			this.setWorld(world);
			this.repaint();
		} catch (IOException | SyntaxError ex) {
			ex.printStackTrace();
		}
	}
	
	public void saveAs() {
		try {
			JFileChooser chooser = new JFileChooser();
			FileNameExtensionFilter filter = new FileNameExtensionFilter("Planets Enigma worlds", "mp_world");
			chooser.setFileFilter(filter);
			String defaultFileName = world.metaJson().getPrimitive("name").asString().orElse("untitled").replace(' ', '_') + ".mp_world";
			File basePath = new File(".").getCanonicalFile();
			chooser.setSelectedFile(new File(basePath, defaultFileName));
			int result = chooser.showSaveDialog(this);
			if (result == JFileChooser.CANCEL_OPTION) {
				return;
			}
			if (result == JFileChooser.ERROR_OPTION) {
				System.out.println("Error selecting a save file.");
				return;
			}
			if (result != JFileChooser.APPROVE_OPTION) {
				System.out.println("Unknown result code: "+result);
				return;
			}
			File outputFile = chooser.getSelectedFile();
			
			// Make last minute edits to be sure this file is marked as externally edited
			
			// Grab stats and include in the preview
			ObjectElement stats = world.metaJson().getObject("stats");
			int roomCount = stats.getPrimitive("rooms").asInt().orElse(0);
			int bossCount = stats.getPrimitive("bosses").asInt().orElse(0);
			int areaCount = stats.getPrimitive("areas").asInt().orElse(0);
			world.metaJson().put("description", PrimitiveElement.of("MODIFIED WORLD, USE WITH CARE. ROOMS:"+roomCount+" AREAS:"+areaCount+" BOSSES:"+bossCount));
			ObjectElement toolObj = new ObjectElement();
			ArrayElement authorsArr = new ArrayElement();
			authorsArr.add(PrimitiveElement.of("FALKREON"));
			toolObj.put("authors", authorsArr);
			toolObj.put("editor_tool", PrimitiveElement.of("planet_inspector"));
			toolObj.put("tags", new ArrayElement());
			world.metaJson().put("external_editor", toolObj);
			
			world.save(outputFile.toPath());
			
			System.out.println("Saved.");
		} catch (IOException | SyntaxError ex) {
			ex.printStackTrace();
		}
	}
}
