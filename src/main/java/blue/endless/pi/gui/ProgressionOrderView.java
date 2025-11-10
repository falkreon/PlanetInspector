package blue.endless.pi.gui;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.swing.AbstractAction;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;

import blue.endless.jankson.api.document.ObjectElement;
import blue.endless.jankson.api.document.PrimitiveElement;
import blue.endless.jankson.api.document.ValueElement;
import blue.endless.pi.enigma.ItemType;
import blue.endless.pi.enigma.wrapper.RoomInfo;
import blue.endless.pi.enigma.wrapper.ScreenInfo;
import blue.endless.pi.enigma.wrapper.WorldInfo;
import blue.endless.pi.gui.layout.Axis;
import blue.endless.pi.gui.layout.LinearLayout;
import blue.endless.pi.gui.layout.MultiItemAxisLayout;
import blue.endless.pi.gui.layout.SingleItemAxisLayout;
import blue.endless.pi.gui.view.AbstractView;
import blue.endless.pi.gui.view.ViewContext;

public class ProgressionOrderView extends AbstractView {
	private WorldInfo world;
	private DefaultListModel<Entry> activeItems = new DefaultListModel<Entry>();
	private DefaultListModel<Entry> availableItems = new DefaultListModel<Entry>();
	private JList<Entry> activeList = new JList<>(activeItems);
	private JList<Entry> availableList = new JList<>(availableItems);
	private JScrollPane activeScroll = new JScrollPane(activeList);
	private JScrollPane availableScroll = new JScrollPane(availableList);
	
	@SuppressWarnings("unused")
	private static record Entry(WorldInfo world, ObjectElement json) {
		
		public int x() {
			return json.getPrimitive("x").asInt().orElse(0);
		}
		
		public int y() {
			return json.getPrimitive("y").asInt().orElse(0);
		}
		
		public int roomId() {
			return json.getPrimitive("room_id").asInt().orElse(0);
		}
		
		public WorldInfo world() {
			return world;
		}
		
		public RoomInfo room() {
			int roomId = json.getPrimitive("room_id").asInt().orElse(0);
			if (roomId < 0 || roomId >= world.rooms().size()) return null;
			return world.rooms().get(roomId);
		}
		
		public ItemType item() {
			return ItemType.of(json.getPrimitive("item").asInt().orElse(0));
		}
		
		public int area() {
			return json.getPrimitive("area").asInt().orElse(0);
		}
		
		public int sector() {
			return json.getPrimitive("sector").asInt().orElse(0);
		}
		
		@Override
		public String toString() {
			return item().name() + ": " + world.areas().get(area()).name() + " (" + x() + ", "+ y() + ")";
		}
		
		public static Entry of(WorldInfo world, RoomInfo room, ScreenInfo screen, ItemType item) {
			ObjectElement obj = new ObjectElement();
			obj.put("x", PrimitiveElement.of(screen.x()));
			obj.put("y", PrimitiveElement.of(screen.y()));
			obj.put("room_id", PrimitiveElement.of(world.indexOf(room)));
			obj.put("item", PrimitiveElement.of(item.id()));
			obj.put("area", PrimitiveElement.of(room.area()));
			obj.put("sector", PrimitiveElement.of(0));
			return new Entry(world, obj);
		}
	}
	
	/*
	private static record EntryQ(int x, int y, WorldInfo world, RoomInfo room, ItemType item, int area, int sector) {
		public Entry(ObjectElement obj, WorldInfo world) {
			this(
				obj.getPrimitive("x").asInt().orElse(0),
				obj.getPrimitive("y").asInt().orElse(0),
				world,
				world.rooms().get(obj.getPrimitive("room_id").asInt().orElse(0)),
				ItemType.of(obj.getPrimitive("item").asInt().orElse(0)),
				obj.getPrimitive("area").asInt().orElse(0),
				obj.getPrimitive("sector").asInt().orElse(0)
				);
		}
		
		@Override
		public String toString() {
			return item.name() + ": " + world.areas().get(area).name() + x + ", "+ y;
		}
	}*/
	
	public ProgressionOrderView(ViewContext context, WorldInfo world) {
		super(context);
		this.world = world;
		
		Set<ItemType> foundItems = new HashSet<>();
		List<Entry> existingEntries = new ArrayList<>();
		List<Entry> potentialEntries = new ArrayList<>();
		
		//Grab the CURRENT list of progression items
		for(ValueElement val : world.json().getArray("PROGRESSION_LOG")) {
			if (val instanceof ObjectElement obj) {
				Entry entry = new Entry(world, obj);
				foundItems.add(entry.item());
				existingEntries.add(entry);
			}
		}
		
		//Find orphaned items
		for(RoomInfo room : world.rooms()) {
			for(ScreenInfo screen : room.screens()) {
				for(ValueElement val : screen.json().getArray("OBJECTS")) {
					if (val instanceof ObjectElement obj) {
						if (obj.getPrimitive("type").asInt().orElse(-1) == 0) {
							// Item object
							
							ItemType item = ItemType.of(obj.getPrimitive("item").asInt().orElse(0));
							if (!foundItems.contains(item)) {
								foundItems.add(item);
								Entry itemEntry = Entry.of(world, room, screen, item);
								potentialEntries.add(itemEntry);
							}
						}
					}
				}
			}
		}
		
		activeItems.addAll(existingEntries);
		availableItems.addAll(potentialEntries);
		
		
		LinearLayout mainLayout = new LinearLayout();
		mainLayout.setAxis(Axis.HORIZONTAL);
		mainLayout.setSpacing(16);
		mainLayout.setMainAxisLayout(MultiItemAxisLayout.FILL_UNIFORM);
		mainLayout.setCrossAxisLayout(SingleItemAxisLayout.FILL);
		mainPanel.setLayout(mainLayout);
		mainPanel.add(labeledComponent("Current Progression", activeScroll));
		
		JPanel midPanel = new JPanel();
		LinearLayout buttonStackLayout = new LinearLayout();
		buttonStackLayout.setAxis(Axis.VERTICAL);
		buttonStackLayout.setSpacing(16);
		buttonStackLayout.setMainAxisLayout(MultiItemAxisLayout.FILL_PROPORTIONAL);
		buttonStackLayout.setCrossAxisLayout(SingleItemAxisLayout.FILL);
		midPanel.setLayout(buttonStackLayout);
		mainPanel.add(midPanel);
		
		mainPanel.add(labeledComponent("Available Items", availableScroll));
		
		
		
		
		//rightPanel.setLayout(new BorderLayout());
		//rightPanel.add(availableList, BorderLayout.CENTER);
		
		JButton addButton = new JButton("Add");
		addButton.setAction(new AbstractAction("Add") {
			@Override
			public void actionPerformed(ActionEvent e) {
				addProgressionItem();
			}
		});
		midPanel.add(addButton);
		
		JButton removeButton = new JButton("Remove");
		removeButton.setAction(new AbstractAction("Remove") {
			@Override
			public void actionPerformed(ActionEvent e) {
				removeProgressionItem();
			}
		});
		midPanel.add(removeButton);
		
		JButton upButton = new JButton("Move Earlier");
		upButton.setAction(new AbstractAction("Move Earlier") {
			@Override
			public void actionPerformed(ActionEvent e) {
				moveItemUp();
			}
		});
		midPanel.add(upButton);
		
		JButton downButton = new JButton("Move Later");
		downButton.setAction(new AbstractAction("Move Later") {
			@Override
			public void actionPerformed(ActionEvent e) {
				moveItemDown();
			}
		});
		midPanel.add(downButton);
		
		//System.out.println("Existing progression: "+existingEntries);
		//System.out.println("Potential entries: "+potentialEntries);
	}
	
	private static JComponent labeledComponent(String title, JComponent component) {
		JPanel result = new JPanel();
		result.setBorder(new EmptyBorder(16, 16, 16, 16));
		result.setLayout(new BorderLayout());
		//result.add(new JLabel(title), BorderLayout.NORTH);
		component.setBorder(new TitledBorder(title));
		result.add(component, BorderLayout.CENTER);
		
		return result;
	}
	
	public void addProgressionItem() {
		int selected = availableList.getSelectedIndex();
		if (selected < 0) return;
		
		Entry selectedItem = availableItems.remove(selected);
		activeItems.addElement(selectedItem);
		world.json().getArray("PROGRESSION_LOG").add(selectedItem.json());
		context.markUnsaved();
		mainPanel.repaint();
		rightPanel.repaint();
	}
	
	public void removeProgressionItem() {
		int selected = activeList.getSelectedIndex();
		if (selected < 0) return;
		
		Entry subject = activeItems.remove(selected);
		availableItems.addElement(subject);
		world.json().getArray("PROGRESSION_LOG").remove(selected);
		context.markUnsaved();
		mainPanel.repaint();
		rightPanel.repaint();
	}
	
	public void moveItemUp() {
		int selected = activeList.getSelectedIndex();
		if (selected < 1) return; // Can't move item up if it's the first (zeroth) item
		
		Entry subject = activeItems.remove(selected);
		activeItems.add(selected - 1, subject);
		
		ValueElement elem = world.json().getArray("PROGRESSION_LOG").remove(selected);
		world.json().getArray("PROGRESSION_LOG").add(selected - 1, elem);
		activeList.setSelectedIndex(selected - 1);
		context.markUnsaved();
		mainPanel.repaint();
		rightPanel.repaint();
	}
	
	public void moveItemDown() {
		int selected = activeList.getSelectedIndex();
		if (selected < 0) return;
		if (selected >= activeItems.size()-1) return; // Can't move item down if it's the last item in the list
		
		Entry subject = activeItems.remove(selected);
		activeItems.add(selected + 1, subject);
		
		ValueElement elem = world.json().getArray("PROGRESSION_LOG").remove(selected);
		world.json().getArray("PROGRESSION_LOG").add(selected + 1, elem);
		activeList.setSelectedIndex(selected + 1);
		context.markUnsaved();
		mainPanel.repaint();
		rightPanel.repaint();
	}
	
}
