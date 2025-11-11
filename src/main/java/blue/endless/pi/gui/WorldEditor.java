package blue.endless.pi.gui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;

import javax.swing.AbstractAction;
import javax.swing.Box;
import javax.swing.Icon;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
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
import blue.endless.pi.BGM;
import blue.endless.pi.Preferences;
import blue.endless.pi.SchemaType;
import blue.endless.pi.enigma.EnemyType;
import blue.endless.pi.enigma.EnigmaFormat;
import blue.endless.pi.enigma.ObjectType;
import blue.endless.pi.enigma.wrapper.AreaInfo;
import blue.endless.pi.enigma.wrapper.RoomInfo;
import blue.endless.pi.enigma.wrapper.ScreenInfo;
import blue.endless.pi.enigma.wrapper.WorldInfo;
import blue.endless.pi.gui.view.AbstractView;
import blue.endless.pi.gui.view.CloseAware;
import blue.endless.pi.gui.view.ViewContext;

/**
 * This is the main application window - but not a lot of real behavior lives here. It's mainly in the individual panels this frame hosts.
 */
public class WorldEditor extends AbstractView implements CloseAware {
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
	/*
	private static Map<String, SchemaType<?>> WORLD_META_SCHEMA = Map.of(
			"id", SchemaType.INT,
			"world_version", SchemaType.DOUBLE,
			"version", SchemaType.DOUBLE,
			"description", SchemaType.STRING,
			"stats", SchemaType.DOUBLE,
			"name", SchemaType.STRING,
			"name_full", SchemaType.STRING,
			"key", SchemaType.DOUBLE
			);*/
	
	private ScrollingPlanetView planetView;
	private PropertyEditor propertyView = new PropertyEditor();
	
	private static WorldInfo world;
	
	private JMenu fileMenu = new JMenu("File");
	private JMenu worldMenu = new JMenu("World");
	private JMenu roomMenu = new JMenu("Room");
	
	private File curWorldsDir;
	private File curRoomsDir;
	
	private JLabel statusLine = new JLabel();
	
	public WorldEditor(ViewContext context) {
		super(context);
		
		planetView = new ScrollingPlanetView(context);
		mainPanel = planetView;
		rightPanel = propertyView;
		super.statusLine = statusLine;
		propertyView.setPreferredSize(new Dimension(400, -1));
		propertyView.setMinimumSize(new Dimension(400, -1));
		//planetView.setMinimumSize(new Dimension(600, 600));
		
		menuBar = new JMenuBar();
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
				context.attemptClose();
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
		
		JMenuItem areasItem = new JMenuItem("Areas");
		areasItem.setAction(new AbstractAction("Areas") {
			@Override
			public void actionPerformed(ActionEvent e) {
				AreaView areaView = new AreaView(context, world);
				context.go(areaView);
			}
		});
		worldMenu.add(areasItem);
		
		JMenuItem startingItemsItem = new JMenuItem("Starting Items");
		startingItemsItem.setAction(new AbstractAction("Starting Items") {
			@Override
			public void actionPerformed(ActionEvent e) {
				StartingItemsView view = new StartingItemsView(context, world);
				context.go(view);
			}
		});
		worldMenu.add(startingItemsItem);
		
		JMenuItem progressionOrderItem = new JMenuItem("Progression Order");
		progressionOrderItem.setAction(new AbstractAction("Progression Order") {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				ProgressionOrderView view = new ProgressionOrderView(context, world);
				context.go(view);
			}
		});
		worldMenu.add(progressionOrderItem);
		
		JMenuItem makeUniqueItem = new JMenuItem("Make Unique");
		makeUniqueItem.setAction(new AbstractAction("Make Unique") {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (world == null) return;
				long seconds = System.currentTimeMillis() / 1_000L; // Seconds since midnight, january 1, 1970
				long newId = seconds * 1000L + (long) (Math.random() * 999);
				world.metaJson().put("id", PrimitiveElement.of(newId));
				context.markUnsaved();
				//planetView.getView().setDirty(true);
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
				context.markUnsaved();
				//planetView.getView().setDirty(true);
			}
		});
		worldMenu.add(cleanItem);
		worldMenu.setEnabled(false);
		menuBar.add(worldMenu);
		
		JMenuItem duplicateRoomItem = new JMenuItem("Duplicate Room");
		duplicateRoomItem.setAction(new AbstractAction("Duplicate Room") {
			@Override
			public void actionPerformed(ActionEvent e) {
				RoomInfo selectedRoom = WorldEditor.this.planetView.getView().getSelectedRoom();
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
				JOptionPane.showMessageDialog(null, "Authors: Falkreon (maintainer, code, reverse eng) Reverse Engineering Help: Frio");
			}
		});
		helpMenu.add(aboutItem);
		menuBar.add(helpMenu);
		
		planetView.getView().setRoomSelectionCallback(this::roomSelected);
		planetView.getView().setRoomDoubleClickCallback(this::roomOpened);
		planetView.getView().setRoomRightClickCallback(this::roomContext);
		planetView.getView().setRoomFileDragCallback(this::importRoom);
		
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
	/*
	public void setRoomProperties(ObjectElement room) {
		propertyView.setObject(null, null);
		if (world == null) return;
		propertyView.addExternalLine("Name", room, "name", SchemaType.IMMUTABLE);
		propertyView.addExternalLine("Designer", room, "designer", SchemaType.IMMUTABLE);
		propertyView.addExternalLine("Tags", room, "tags", SchemaType.STRING_LIST);
		propertyView.addExternalLine("Area", room, "area", SchemaType.INT); // TODO: Very change
		propertyView.addExternalLine("Music", room, "bgm", BGM.SCHEMA);
		propertyView.addExternalLine("Background", room, "bg_color", SchemaType.INT);
	}*/
	
	
	public void setRoomProperties(RoomInfo room) {
		propertyView.setObject(null, null);
		if (world == null || room == null) return;
		ObjectElement general = room.json().getObject("GENERAL");
		propertyView.addExternalLine("Name", general, "name", SchemaType.IMMUTABLE);
		propertyView.addExternalLine("Designer", general, "designer", SchemaType.IMMUTABLE);
		propertyView.addExternalLine("Tags", general, "tags", SchemaType.STRING_LIST);
		
		record AreaId(String name, int id) {
			public String toString() { return name; }
		}
		AreaId[] areas = new AreaId[world.areas().size()];
		for(int i=0; i<world.areas().size(); i++) areas[i] = new AreaId(world.areas().get(i).name(), i);
		JComboBox<AreaId> areaBox = new JComboBox<AreaId>(areas);
		areaBox.setSelectedIndex(general.getPrimitive("area").asInt().orElse(0));
		areaBox.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				int areaNumber = areaBox.getSelectedIndex();
				if (areaNumber >= 0 && areaNumber < areas.length) {
					general.put("area", PrimitiveElement.of(areaNumber));
					for(ScreenInfo screen : room.screens()) {
						screen.json().getObject("MAP").put("area", PrimitiveElement.of(areaNumber));
					}
					//planetView.getView().setDirty(true);
					context.markUnsaved();
					planetView.repaint();
				}
			}
		});
		propertyView.addExternalLine("Area", areaBox);
		
		propertyView.addExternalLine("Music", general, "bgm", BGM.SCHEMA);
		propertyView.addExternalLine("Background", general, "bg_color", SchemaType.IMMUTABLE_INT);
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
				//setRoomProperties(o);
				//propertyView.setObject(o, schema);
			}
		});
	}
	
	public void setWorld(WorldInfo world) {
		WorldEditor.world = world;
		setWorldProperties();
		//propertyView.setObject(world.metaJson(), WORLD_META_SCHEMA);
		planetView.setWorld(world);
		planetView.setPropertiesConsumer((o, schema) -> {
			if (o == null) {
				setWorldProperties();
				//propertyView.setObject(world.metaJson(), WORLD_META_SCHEMA);
			} else {
				//propertyView.setObject(o, schema);
				//setRoomProperties(o);
			}
		});
	}
	
	public boolean attemptClose() {
		if (context.isUnsaved()) {
		//if (planetView.getView().isDirty()) {
			// TODO: Fix this confirmation dialog because it's kind of confusing as is
			
			int selectedResult = JOptionPane.showOptionDialog(
					planetView,
					"This world has unsaved data. Are you sure you want to quit?",
					"Really Quit?",
					JOptionPane.OK_CANCEL_OPTION,
					JOptionPane.WARNING_MESSAGE,
					null,
					new Object[] { "Quit", "Cancel" },
					"Cancel"
					);
			
			// I suspect OK_OPTION only works here because Quit is the zeroth element in the array
			if (selectedResult != JOptionPane.OK_OPTION) {
				return false;
			}
		}
		//this.setVisible(false);
		return true;
	}
	
	public void open() {
		try {
			JFileChooser chooser = new JFileChooser();
			FileNameExtensionFilter filter = new FileNameExtensionFilter("Planets Enigma worlds", "mp_world");
			chooser.setFileFilter(filter);
			chooser.setCurrentDirectory(curWorldsDir);
			int result = chooser.showOpenDialog(null);
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
			context.clearUnsaved();
			planetView.repaint();
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
			
			chooser.setCurrentDirectory(curWorldsDir);
			chooser.setSelectedFile(new File(curWorldsDir, defaultFileName));
			int result = chooser.showSaveDialog(null);
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
			
			File selectedFolder = outputFile.getParentFile();
			if (selectedFolder != null) curWorldsDir = selectedFolder;
			
			// Make last minute edits to be sure this file is marked as externally edited
			EnigmaFormat.prepareForSave(world);
			
			
			
			world.save(outputFile.toPath());
			
			context.clearUnsaved();
			//planetView.getView().setDirty(false);
			System.out.println("Saved.");
		} catch (IOException | SyntaxError ex) {
			ex.printStackTrace();
		}
	}
	
	public void importRoom() {
		if (world == null) return;
		
		JFileChooser chooser = new JFileChooser();
		FileNameExtensionFilter filter = new FileNameExtensionFilter("Planets Enigma rooms", "mp_room");
		chooser.setFileFilter(filter);
		chooser.setCurrentDirectory(curRoomsDir);
		int result = chooser.showOpenDialog(null);
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
		
		importRoom(roomFile);
			
	}
	
	public void importRoom(File roomFile) {
		try {
			RoomInfo room = RoomInfo.load(roomFile.toPath());
			
			for(ScreenInfo screen : room.screens()) {
				ArrayElement enemiesArray = screen.json().getArray("ENEMIES");
				for(ValueElement val : enemiesArray) {
					if (val instanceof ObjectElement obj) {
						obj.computeIfAbsent("level", (it)->PrimitiveElement.of(0));
					}
				}
			}
			
			addRoom(room);
		} catch (IOException | SyntaxError ex) {
			JOptionPane.showOptionDialog(
					(Component) planetView,
					"Could not load the file '"+roomFile.getName()+"'.\n"+ex.getLocalizedMessage(),
					"Unable to Open File",
					JOptionPane.ERROR_MESSAGE,
					JOptionPane.DEFAULT_OPTION,
					(Icon) null,
					new Object[] { "Ok" },
					"Ok");
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
		
		//planetView.getView().setDirty(true);
		validateWorld();
		context.markUnsaved();
		planetView.repaint();
	}
	
	public static boolean hasGunship(WorldInfo world) {
		for(RoomInfo room : world.rooms()) {
			for(ScreenInfo screen : room.screens()) {
				int objectCount = screen.objectCount();
				for(int i=0; i<objectCount; i++) {
					ObjectElement obj = screen.object(i);
					if (obj.getPrimitive("type").asInt().orElse(-1) == ObjectType.GUNSHIP.value()) return true;
				}
			}
		}
		
		return false;
	}
	
	public static boolean hasMotherBrain(WorldInfo world) {
		for(RoomInfo room : world.rooms()) {
			if (room.isBossRoom() && room.bossId() == EnemyType.MOTHER_BRAIN_ID) return true;
		}
		return false;
	}
	
	public static boolean hasEscapeElevator(WorldInfo world) {
		for(RoomInfo room : world.rooms()) {
			for(ScreenInfo screen : room.screens()) {
				ArrayElement elevators = screen.json().getArray("ELEVATORS");
				for(ValueElement val : elevators) {
					if (val instanceof ObjectElement elevator) {
						if (elevator.getPrimitive("dest_rm").asInt().orElse(-2) == -1) return true;
					}
				}
			}
		}
		
		return false;
	}
	
	public void validateWorld() {
		if (!hasGunship(world)) {
			statusLine.setText("There is no Gunship - the player cannot spawn!");
			statusLine.setForeground(Color.RED);
			return;
		}
		
		if (!hasMotherBrain(world)) {
			statusLine.setText("There is no Mother Brain room - the player cannot win!");
			statusLine.setForeground(Color.RED);
			return;
		}
		
		if (!hasEscapeElevator(world)) {
			statusLine.setText("There is no escape elevator - cannot verify that escape is survivable!");
			statusLine.setForeground(Color.ORANGE);
			return;
		}
		
		statusLine.setText("");
		statusLine.setBackground(null);
		statusLine.setForeground(null);
	}
	
	public void roomSelected(RoomInfo room) {
		if (room != null) {
			roomMenu.setEnabled(true);
			setRoomProperties(room);
		} else {
			roomMenu.setEnabled(false);
			validateWorld();
		}
	}
	
	public void roomOpened(RoomInfo room) {
		context.go(new RoomConfiguratorView(context, world, world.indexOf(room)));
	}
	
	public void roomContext(RoomInfo room) {
		
	}
}
