package blue.endless.pi.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.util.ArrayList;

import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.ListModel;
import javax.swing.event.ListDataListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import blue.endless.jankson.api.document.ArrayElement;
import blue.endless.jankson.api.document.ObjectElement;
import blue.endless.pi.Item;

public class DebugLogFrame extends JFrame implements ListSelectionListener {
	private final WorldInfo world;
	private final ObjectElement debug;
	private final JList<String> left;
	private final JList<Item> right;
	
	
	public DebugLogFrame(WorldInfo world) {
		super("Debug Log");
		
		this.world = world;
		this.debug = world.json().getObject("GENERATION_DEBUG_LOG");
		ArrayList<String> items = new ArrayList<>();
		items.add("starting_items");
		items.add("major_pool");
		for(int i=0; i<debug.getArray("minor_pools").size(); i++) {
			items.add("minor_pools."+i);
		}
		items.add("placed_items");
		left = new JList<String>(items.toArray(new String[items.size()]));
		left.addListSelectionListener(this);
		
		//Split the log into a left and right side
		this.getContentPane().setLayout(new BorderLayout());
		this.getContentPane().add(left, BorderLayout.WEST);
		
		right = new JList<Item>();
		this.getContentPane().add(right, BorderLayout.CENTER);
		
		this.setMinimumSize(new Dimension(640, 480));
		this.setPreferredSize(new Dimension(640,480));
		this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
	}


	@Override
	public void valueChanged(ListSelectionEvent e) {
		for(int i=e.getFirstIndex(); i<=e.getLastIndex(); i++) {
			if (left.isSelectedIndex(i)) {
				select(left.getModel().getElementAt(i));
				return;
			}
		}
		// TODO: Clear right panel if we get here, since nothing is selected
		right.setModel(new ItemArrayListModel(new ArrayElement()));
	}
	
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
		} else {
			right.setModel(new ItemArrayListModel(new ArrayElement()));
		}
	}
	
	private static class ItemArrayListModel implements ListModel<Item> {
		private final ArrayElement items;
		
		public ItemArrayListModel(ArrayElement items) {
			this.items = items;
		}
		
		@Override
		public void addListDataListener(ListDataListener l) {
			
		}

		@Override
		public Item getElementAt(int index) {
			int itemId = items.getPrimitive(index).asInt().orElse(-1);
			return Item.byId(itemId);
		}

		@Override
		public int getSize() {
			return items.size();
		}

		@Override
		public void removeListDataListener(ListDataListener l) {
			
		}
		
	}
	
	
}
