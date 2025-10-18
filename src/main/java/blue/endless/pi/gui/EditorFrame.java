package blue.endless.pi.gui;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.util.Map;

import javax.swing.AbstractAction;
import javax.swing.Box;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileNameExtensionFilter;

import blue.endless.jankson.api.SyntaxError;
import blue.endless.jankson.api.document.ArrayElement;
import blue.endless.jankson.api.document.ObjectElement;
import blue.endless.jankson.api.document.PrimitiveElement;
import blue.endless.pi.SchemaType;

/**
 * This is the main application window - but not a lot of real behavior lives here. It's mainly in the individual panels this frame hosts.
 */
public class EditorFrame extends JFrame {
	static Map<String, SchemaType<?>> ROOM_SCHEMA = Map.of(
			"META", SchemaType.IMMUTABLE,
			"GENERAL", SchemaType.IMMUTABLE,
			"EVENTS", SchemaType.IMMUTABLE,
			"PATHING", SchemaType.IMMUTABLE,
			"HAZARD", SchemaType.IMMUTABLE,
			"PALETTES", SchemaType.IMMUTABLE,
			"SCREENS", SchemaType.IMMUTABLE
			);
	
	static Map<String, SchemaType<?>> SCREEN_SCHEMA = Map.of(
			"x", SchemaType.INT,
			"y", SchemaType.INT,
			"boss", SchemaType.IMMUTABLE // boolean
			);
	
	static Map<String, SchemaType<?>> ROOM_GENERAL_SCHEMA = Map.ofEntries(
			Map.entry("name", SchemaType.STRING),
			Map.entry("designer", SchemaType.STRING),
			Map.entry("bg_color", SchemaType.INT),
			Map.entry("powered", SchemaType.IMMUTABLE), // boolean
			Map.entry("no_floor", SchemaType.IMMUTABLE), // boolean
			Map.entry("area", SchemaType.INT),
			Map.entry("sector", SchemaType.INT),
			Map.entry("bgm", SchemaType.STRING),
			Map.entry("gravity_multiplier", SchemaType.DOUBLE),
			Map.entry("focus", SchemaType.IMMUTABLE),
			Map.entry("areas", SchemaType.IMMUTABLE),
			Map.entry("magnet", SchemaType.IMMUTABLE),
			Map.entry("tags", SchemaType.IMMUTABLE)
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
		
		JMenu worldMenu = new JMenu("World");
		JMenuItem debugLogItem = new JMenuItem("Debug Log");
		debugLogItem.setAction(new AbstractAction("Debug Log") {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (world == null) return;
				DebugLogFrame logFrame = new DebugLogFrame(world);
				logFrame.setVisible(true);
			}
		});
		worldMenu.add(debugLogItem);
		JMenuItem makeUniqueItem = new JMenuItem("Make Unique");
		makeUniqueItem.setAction(new AbstractAction("Make Unique") {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (world == null) return;
				long newId = (long) (Math.random() * Integer.MAX_VALUE);
				world.metaJson().put("id", PrimitiveElement.of(newId));
				setWorldProperties();
			}
		});
		worldMenu.add(makeUniqueItem);
		menuBar.add(worldMenu);
		
		menuBar.add(Box.createHorizontalGlue());
		
		JMenu helpMenu = new JMenu("Help");
		JMenuItem aboutItem = new JMenuItem("About Planet Inspector");
		aboutItem.setAction(new AbstractAction("About") {
			@Override
			public void actionPerformed(ActionEvent e) {
				JOptionPane.showMessageDialog(EditorFrame.this, "Authors: Falkreon (maintainer, code, reverse eng) Reverse Engineering Help: Frio");
			}
		});
		helpMenu.add(aboutItem);
		menuBar.add(helpMenu);
		
		this.setJMenuBar(menuBar);
		
		this.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		this.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				exit();
			}
		});
	}
	
	public void setWorldProperties() {
		propertyView.setObject(null, null);
		if (world == null) return;
		propertyView.addExternalLine("Short Name", world.metaJson(), "name", SchemaType.STRING);
		propertyView.addExternalLine("Full Name", world.metaJson(), "name_full", SchemaType.STRING);
		propertyView.addExternalLine("Authors", world.metaJson().getObject("external_editor"), "authors", SchemaType.STRING_LIST);
		propertyView.addExternalLine("Tags", world.metaJson().getObject("external_editor"), "tags", SchemaType.STRING_LIST);
		propertyView.addExternalLine("Id", world.metaJson(), "id", SchemaType.IMMUTABLE_INT);
	}
	
	public void setWorld(ObjectElement obj, ObjectElement meta) {
		world = WorldInfo.of(obj, meta);
		
		setWorldProperties();
		//propertyView.setObject(meta, WORLD_META_SCHEMA);
		planetView.setWorld(world);
		planetView.setPropertiesConsumer((o, schema) -> {
			if (o == null) {
				setWorldProperties();
				//propertyView.setObject(meta, WORLD_META_SCHEMA);
			} else {
				propertyView.setObject(o, schema);
			}
		});
	}
	
	public void setWorld(WorldInfo world) {
		this.world = world;
		setWorldProperties();
		//propertyView.setObject(world.metaJson(), WORLD_META_SCHEMA);
		planetView.setWorld(world);
		planetView.setPropertiesConsumer((o, schema) -> {
			if (o == null) {
				setWorldProperties();
				//propertyView.setObject(world.metaJson(), WORLD_META_SCHEMA);
			} else {
				propertyView.setObject(o, schema);
			}
		});
	}
	
	public void exit() {
		if (planetView.getView().isDirty()) {
			int selectedResult = JOptionPane.showConfirmDialog(this, "This world has unsaved data. Are you sure you want to quit?", "Really Quit?", JOptionPane.OK_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE);
			if (selectedResult == JOptionPane.CANCEL_OPTION) {
				return;
			}
		}
		// TODO: Pop up a close dialog.
		this.setVisible(false);
		System.exit(0);
	}
	
	public void open() {
		try {
			JFileChooser chooser = new JFileChooser();
			FileNameExtensionFilter filter = new FileNameExtensionFilter("Planets Enigma worlds", "mp_world");
			chooser.setFileFilter(filter);
			File basePath = new File(".").getCanonicalFile();
			chooser.setSelectedFile(basePath);
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
			
			// Clear the generator debug log; it's useless now
			world.json().put("GENERATION_DEBUG_LOG", new ObjectElement());
			
			// Grab stats and include in the preview
			ObjectElement stats = world.metaJson().getObject("stats");
			int roomCount = stats.getPrimitive("rooms").asInt().orElse(0);
			int bossCount = stats.getPrimitive("bosses").asInt().orElse(0);
			int areaCount = stats.getPrimitive("areas").asInt().orElse(0);
			world.metaJson().put("description", PrimitiveElement.of("MODIFIED WORLD, USE WITH CARE; ROOMS: "+roomCount+"; AREAS: "+areaCount+"; BOSSES: "+bossCount));
			
			// Make sure the editor_tool key is there to mark this world as edited
			ObjectElement toolObj = world.metaJson().getObject("external_editor");
			if (toolObj.isEmpty()) {
				// If it's empty or fake, make sure it exists now as part of the meta json
				world.metaJson().put("external_editor", toolObj);
			}
			toolObj.computeIfAbsent("authors", (it) -> new ArrayElement());
			toolObj.computeIfAbsent("tags", (it) -> new ArrayElement());
			toolObj.put("editor_tool", PrimitiveElement.of("planet_inspector"));
			
			world.save(outputFile.toPath());
			
			planetView.getView().setDirty(false);
			System.out.println("Saved.");
		} catch (IOException | SyntaxError ex) {
			ex.printStackTrace();
		}
	}
}
