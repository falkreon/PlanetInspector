package blue.endless.pi.gui;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;

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
import blue.endless.jankson.api.document.ValueElement;
import blue.endless.pi.Assets;
import blue.endless.pi.Preferences;
import blue.endless.pi.SchemaType;
import blue.endless.pi.enigma.EnigmaFormat;
import blue.endless.pi.enigma.wrapper.AreaInfo;
import blue.endless.pi.enigma.wrapper.RoomInfo;
import blue.endless.pi.enigma.wrapper.ScreenInfo;
import blue.endless.pi.enigma.wrapper.WorldInfo;

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
	
	private JMenu fileMenu = new JMenu("File");
	private JMenu worldMenu = new JMenu("World");
	private JMenu roomMenu = new JMenu("Room");
	
	private File curWorldsDir;
	private File curRoomsDir;
	
	
	public EditorFrame() {
		this.setTitle("Planet Inspector");
		this.setIconImage(Assets.getCachedImage("icon.png").orElseGet(Assets::missingImage));
		
		Container container = this.getContentPane();
		container.setLayout(new BorderLayout());
		container.add(planetView, BorderLayout.CENTER);
		container.add(propertyView, BorderLayout.EAST);
		
		this.setMinimumSize(new Dimension(800, 600));
		
		JMenuBar menuBar = new JMenuBar();
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
		fileMenu.addSeparator();
		
		JMenuItem exitItem = new JMenuItem("Exit");
		exitItem.setAction(new AbstractAction("Exit") {
			@Override
			public void actionPerformed(ActionEvent e) {
				exit();
			}
		});
		fileMenu.add(exitItem);
		
		menuBar.add(fileMenu);
		
		
		JMenuItem importRoomItem = new JMenuItem("Import Room...");
		importRoomItem.setAction(new AbstractAction("Import Room...") {
			@Override
			public void actionPerformed(ActionEvent e) {
				importRoom();
			}
		});
		worldMenu.add(importRoomItem);
		
		JMenuItem makeUniqueItem = new JMenuItem("Make Unique");
		makeUniqueItem.setAction(new AbstractAction("Make Unique") {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (world == null) return;
				long seconds = System.currentTimeMillis() / 1_000L; // Seconds since midnight, january 1, 1970
				long newId = seconds * 1000L + (long) (Math.random() * 999);
				world.metaJson().put("id", PrimitiveElement.of(newId));
				planetView.getView().setDirty(true);
				setWorldProperties();
			}
		});
		worldMenu.add(makeUniqueItem);
		
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
		
		
		
		JMenuItem cleanItem = new JMenuItem("Clean");
		cleanItem.setAction(new AbstractAction("Clean") {
			@Override
			public void actionPerformed(ActionEvent e) {
				for(RoomInfo room : world.rooms()) {
					for(ScreenInfo screen : room.screens()) {
						screen.clean();
					}
				}
				planetView.getView().setDirty(true);
			}
		});
		worldMenu.add(cleanItem);
		worldMenu.setEnabled(false);
		menuBar.add(worldMenu);
		
		JMenuItem duplicateRoomItem = new JMenuItem("Duplicate Room");
		duplicateRoomItem.setAction(new AbstractAction("Duplicate Room") {
			@Override
			public void actionPerformed(ActionEvent e) {
				RoomInfo selectedRoom = EditorFrame.this.planetView.getView().getSelectedRoom();
				if (selectedRoom == null) return;
				
				ObjectElement copyObj = selectedRoom.json().clone();
				RoomInfo copy = RoomInfo.of(copyObj);
				addRoom(copy);
			}
		});
		roomMenu.add(duplicateRoomItem);
		roomMenu.setEnabled(false);
		menuBar.add(roomMenu);
		
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
		
		planetView.getView().setRoomSelectionCallback(this::roomSelected);
		
		this.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		this.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				exit();
			}
		});
		
		this.curWorldsDir = Preferences.defaultWorldsDir.toFile();
		this.curRoomsDir = Preferences.defaultRoomsDir.toFile();
	}
	
	public void setWorldProperties() {
		propertyView.setObject(null, null);
		if (world == null) return;
		propertyView.addExternalLine("Short Name", world.metaJson(), "name", SchemaType.STRING);
		propertyView.addExternalLine("Full Name", world.metaJson(), "name_full", SchemaType.STRING);
		propertyView.addExternalLine("Version", world.metaJson(), "world_version", SchemaType.INT);
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
			// TODO: Fix this confirmation dialog because it's kind of confusing as is
			
			int selectedResult = JOptionPane.showConfirmDialog(this, "This world has unsaved data. Are you sure you want to quit?", "Really Quit?", JOptionPane.OK_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE);
			if (selectedResult == JOptionPane.CANCEL_OPTION) {
				return;
			}
		}
		this.setVisible(false);
		System.exit(0);
	}
	
	public void open() {
		try {
			JFileChooser chooser = new JFileChooser();
			FileNameExtensionFilter filter = new FileNameExtensionFilter("Planets Enigma worlds", "mp_world");
			chooser.setFileFilter(filter);
			chooser.setCurrentDirectory(curWorldsDir);
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
			File worldFile = chooser.getSelectedFile();
			
			File selectedFolder = worldFile.getParentFile();
			if (selectedFolder != null) curWorldsDir = selectedFolder;
			
			
			WorldInfo world = WorldInfo.load(worldFile.toPath());
			this.setWorld(world);
			worldMenu.setEnabled(true);
			this.repaint();
		} catch (IOException | SyntaxError ex) {
			ex.printStackTrace();
		}
	}
	
	public void saveAs() {
		if (world == null) return;
		
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
			EnigmaFormat.prepareForSave(world);
			
			
			
			world.save(outputFile.toPath());
			
			planetView.getView().setDirty(false);
			System.out.println("Saved.");
		} catch (IOException | SyntaxError ex) {
			ex.printStackTrace();
		}
	}
	
	public void importRoom() {
		if (world == null) return;
		
		try {
			JFileChooser chooser = new JFileChooser();
			FileNameExtensionFilter filter = new FileNameExtensionFilter("Planets Enigma rooms", "mp_room");
			chooser.setFileFilter(filter);
			chooser.setCurrentDirectory(curRoomsDir);
			int result = chooser.showOpenDialog(this);
			if (result == JFileChooser.CANCEL_OPTION) {
				return;
			}
			if (result == JFileChooser.ERROR_OPTION) {
				System.out.println("Error selecting a room file.");
				return;
			}
			if (result != JFileChooser.APPROVE_OPTION) {
				System.out.println("Unknown result code: "+result);
				return;
			}
			File roomFile = chooser.getSelectedFile();
			
			File selectedFolder = roomFile.getParentFile();
			if (selectedFolder != null) curRoomsDir = selectedFolder;
		
			RoomInfo room = RoomInfo.load(roomFile.toPath());
			
			addRoom(room);
		} catch (IOException | SyntaxError ex) {
			ex.printStackTrace();
		}
	}
	
	public void addRoom(RoomInfo room) {
		world.rooms().add(room);
		world.json().getArray("ROOMS").add(room.json());
		
		int roomId = world.rooms().size() - 1; // The new last element's index is the room Id of the imported room
		ArrayElement areasArr = room.json().getObject("GENERAL").getArray("areas");
		List<String> validRoomAreas = new ArrayList<>();
		for(ValueElement val : areasArr) {
			if (val instanceof PrimitiveElement prim) {
				Optional<String> opt = prim.asString();
				if (opt.isPresent()) validRoomAreas.add(opt.get().toUpperCase(Locale.ROOT));
			}
		}
		
		
		int selected = -1;
		for(int i=0; i<world.areas().size(); i++) {
			AreaInfo worldArea = world.areas().get(i);
			String id = worldArea.name().toUpperCase(Locale.ROOT);
			if (validRoomAreas.contains(id)) {
				selected = i;
				break;
			}
			
		}
		
		if (selected == -1) selected = 1;
		
		room.setArea(selected);
		
		// Update gate bosses - boss count will be fixed up in post
		if (room.isBossRoom()) {
			boolean addBoss = true;
			for(ValueElement val : world.json().getObject("GENERAL").getArray("gate_bosses")) {
				if (val instanceof PrimitiveElement prim && prim.asInt().orElse(-1) == room.bossId()) {
					addBoss = false;
				}
			}
			
			if (addBoss) world.json().getObject("GENERAL").getArray("gate_bosses").add(PrimitiveElement.of(room.bossId()));
		}
		
		eachScreen:
		for(int i=0; i<room.screens().size(); i++) {
			ScreenInfo screen = room.screens().get(i);
			
			ArrayElement arr = screen.json().getArray("OBJECTS");
			for(ValueElement val : arr) {
				if (val instanceof ObjectElement obj) {
					
					int objType = obj.getPrimitive("type").asInt().orElse(0);
					if (objType == 1) { // GUNSHIP
						System.out.println("Adding spawn and respawn for gunship...");
						int objX = obj.getPrimitive("x").asInt().orElse(0);
						int objY = obj.getPrimitive("y").asInt().orElse(0);
						world.addGunshipSpawn(roomId, i, room, screen, objX, objY);
						continue eachScreen;
					}
				}
			}
			
			ArrayElement elevators = screen.json().getArray("ELEVATORS");
			if (!elevators.isEmpty()) {
				if (elevators.get(0) instanceof ObjectElement obj) {
					int objX = obj.getPrimitive("x").asInt().orElse(0);
					int objY = obj.getPrimitive("y").asInt().orElse(0);
					world.addElevatorRespawn(roomId, i, room, screen, objX, objY - 15);
				}
			}
		}
		
		planetView.getView().setDirty(true);
		repaint();
	}
	
	public void roomSelected(RoomInfo room) {
		if (room != null) {
			roomMenu.setEnabled(true);
		} else {
			roomMenu.setEnabled(false);
		}
	}
}
