package blue.endless.pi.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.DefaultListModel;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.ListModel;
import javax.swing.event.ListDataListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import blue.endless.jankson.api.document.ArrayElement;
import blue.endless.jankson.api.document.KeyValuePairElement;
import blue.endless.jankson.api.document.ObjectElement;
import blue.endless.jankson.api.document.PrimitiveElement;
import blue.endless.jankson.api.document.ValueElement;
import blue.endless.pi.enigma.ItemType;
import blue.endless.pi.enigma.wrapper.WorldInfo;

public class DebugLogFrame extends JFrame implements ListSelectionListener {
	private final WorldInfo world;
	private final ObjectElement debug;
	private JList<String> left = new JList<>();
	private JList<String> right = new JList<>();
	private final JScrollPane leftScroll = new JScrollPane(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
	private final JScrollPane rightScroll = new JScrollPane(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
	
	private List<String> simpleInfo = new ArrayList<>();
	private Map<String, List<String>> information = new HashMap<>(); // Ugh... do I want to add guava?
	
	public DebugLogFrame(WorldInfo world) {
		super("Debug Log");
		
		this.world = world;
		this.debug = world.json().getObject("GENERATION_DEBUG_LOG");
		ArrayList<String> items = new ArrayList<>();
		for (KeyValuePairElement kvp : debug) {
			switch(kvp.getValue()) {
				case PrimitiveElement prim -> {
					items.add(kvp.getKey()+": "+kvp.getValue().toString());
				}
				case ObjectElement obj -> {
					addItemsEntry(kvp.getKey(), obj);
				}
				case ArrayElement arr -> {
					for(int i=0; i<arr.size(); i++) {
						addItemsEntry(kvp.getKey()+"["+i+"]", arr.get(i));
					}
				}
				default -> throw new IllegalArgumentException("Unexpected value: " + kvp.getValue());
			}
		}
		/*
		if (debug.isEmpty()) {
			items.add("Empty Log");
			
		} else {
			
			items.add("starting_items");
			items.add("major_pool");
			for(int i=0; i<debug.getArray("minor_pools").size(); i++) {
				items.add("minor_pools."+i);
			}
			items.add("placed_items");
		}
		*/
		items.addAll(simpleInfo);
		items.addAll(information.keySet());
		
		left = new JList<String>(items.toArray(new String[items.size()]));
		left.setMinimumSize(new Dimension(200, 150));
		left.setPreferredSize(new Dimension(200, 150));
		left.setMaximumSize(new Dimension(200, -1));
		left.addListSelectionListener(this);
		leftScroll.setViewportView(left);
		leftScroll.revalidate();
		
		//Split the log into a left and right side
		this.getContentPane().setLayout(new BorderLayout());
		this.getContentPane().add(leftScroll, BorderLayout.WEST);
		
		rightScroll.setViewportView(right);
		this.getContentPane().add(rightScroll, BorderLayout.CENTER);
		
		this.setMinimumSize(new Dimension(640, 480));
		this.setPreferredSize(new Dimension(640,480));
		this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
	}
	
	private void addItemsEntry(String key, ValueElement value) {
		switch(value) {
			case PrimitiveElement prim -> simpleInfo.add(key + ": "+ value.toString());
			case ObjectElement obj -> {
				List<String> rightItems = new ArrayList<>();
				for(KeyValuePairElement kvp : obj) {
					rightItems.add(kvp.getKey() + ": " + represent(kvp.getValue()));
				}
				information.put(key, rightItems);
			}
			case ArrayElement arr -> {
				List<String> rightItems = new ArrayList<>();
				for(int i=0; i<arr.size(); i++) {
					rightItems.add(i+": "+represent(arr.get(i)));
				}
				information.put(key, rightItems);
			}
			default -> simpleInfo.add(key + ": unknown ("+value.getClass().getSimpleName()+")");
		}
		
		
		
	}
	
	/** Create a short, simple representation that hides complex information and fits on a line */
	private String represent(ValueElement value) {
		return switch(value) {
			case PrimitiveElement prim -> prim.toString();
			case ObjectElement o -> "{ }";
			case ArrayElement a -> "[ ]";
			default -> "unknown ("+value.getClass().getSimpleName()+")";
		};
	}


	@Override
	public void valueChanged(ListSelectionEvent e) {
		for(int i=e.getFirstIndex(); i<=e.getLastIndex(); i++) {
			if (left.isSelectedIndex(i)) {
				//Try to find right panel info
				List<String> rightItems = information.get(left.getModel().getElementAt(i));
				if (rightItems != null) {
					right = new JList<String>(rightItems.toArray(new String[rightItems.size()]));
					rightScroll.setViewportView(right);
					//DefaultListModel<String> model = new DefaultListModel<>();
					//model.addAll(rightItems);
					//right.setModel(model);
					//validate();
				} else {
					break;
				}
				
				//select(left.getModel().getElementAt(i));
				return;
			}
		}
		// TODO: Clear right panel if we get here, since nothing is selected
		right.setModel(new ItemArrayListModel(new ArrayElement()));
	}
	/*
	private void select(String elem) {
		if (elem.equals("starting_items")) {
			right.setModel(new ItemArrayListModel(debug.getArray("starting_items")));
		} else if (elem.equals("major_pool")) {
			right.setModel(new ItemArrayListModel(debug.getArray("major_pool")));
		} else if (elem.startsWith("minor_pools.")) {
			String s = elem.substring("minor_pools.".length());
			try {
				int i = Integer.parseInt(s);
				ArrayElement arr = debug.getArray("minor_pools").getArray(i);
				right.setModel(new ItemArrayListModel(arr));
			} catch (Throwable t) {
				right.setModel(new ItemArrayListModel(new ArrayElement()));
			}
		} else if (elem.equals("placed_items")) {
			DefaultListModel<String> placedItems = new DefaultListModel<>();
			ArrayElement placedItemsArr = debug.getArray("placed_items");
			for(int i=0; i<placedItemsArr.size(); i++) {
				ObjectElement obj = placedItemsArr.getObject(i);
				int roomId = obj.getPrimitive("room_id").asInt().orElse(0);
				// We're not using / displaying item-object id
				//int id = obj.getPrimitive("id").asInt().orElse(0);
				int oldVal = obj.getPrimitive("old_val").asInt().orElse(0);
				int newVal = obj.getPrimitive("new_val").asInt().orElse(0);
				
				RoomInfo room = null;
				if (roomId < world.rooms().size()) room = world.rooms().get(roomId);
				if (room == null) {
					placedItems.addElement(ItemType.of(oldVal).toString().toLowerCase()+" -> "+ItemType.of(newVal).toString().toLowerCase()+" in Room #"+roomId+" (unknown)");
				} else {
					placedItems.addElement(ItemType.of(oldVal).toString().toLowerCase()+" -> "+ItemType.of(newVal).toString().toLowerCase()+" in Room #"+roomId+", \""+room.name()+"\"");
				}
			}
			right.setModel(placedItems);
		} else {
			right.setModel(new ItemArrayListModel(new ArrayElement()));
		}
	}*/
	
	private static class ItemArrayListModel implements ListModel<String> {
		private final ArrayElement items;
		
		public ItemArrayListModel(ArrayElement items) {
			this.items = items;
		}

		@Override
		public String getElementAt(int index) {
			int itemId = items.getPrimitive(index).asInt().orElse(-1);
			return ItemType.of(itemId).toString().toLowerCase();
		}

		@Override
		public int getSize() {
			return items.size();
		}

		@Override
		public void removeListDataListener(ListDataListener l) {}
		@Override
		public void addListDataListener(ListDataListener l) {}
	}
	
}
