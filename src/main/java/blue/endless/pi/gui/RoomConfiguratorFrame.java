package blue.endless.pi.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Map;

import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;

import blue.endless.jankson.api.document.ObjectElement;
import blue.endless.pi.datastruct.Rect;
import blue.endless.pi.enigma.ItemCategory;
import blue.endless.pi.enigma.ItemType;
import blue.endless.pi.enigma.wrapper.RoomInfo;
import blue.endless.pi.enigma.wrapper.WorldInfo;
import blue.endless.pi.gui.layout.CardLayout;
import blue.endless.pi.gui.layout.LinearLayout;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;

public class RoomConfiguratorFrame extends JFrame {
	private final WorldInfo world;
	private int roomId;
	private final RoomInfo room;
	private final RoomDisplayPanel roomDisplay;
	//private int selectedItem = -1;
	//private int selectedEnemy = -1;
	JSplitPane splitPane;
	private ItemSelectorPanel itemSelector = new ItemSelectorPanel();
	//private final PropertyEditor properties;
	private Object2IntMap<Rect> selectableItems = new Object2IntOpenHashMap<Rect>();
	
	public RoomConfiguratorFrame(WorldInfo world, int roomId) {
		super("Configuring "+world.rooms().get(roomId).name());
		this.room = world.rooms().get(roomId);
		this.world = world;
		this.roomId = roomId;
		
		roomDisplay = new RoomDisplayPanel(world, roomId);
		roomDisplay.setItemSelectCallback((item) -> {
			itemSelector.selectItem(item);
			itemSelector.setEditCallback(this::repaint);
			splitPane.setRightComponent(itemSelector);
		});
		this.getContentPane().setLayout(new BorderLayout());
		JScrollPane roomScroll = new JScrollPane(roomDisplay, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		roomScroll.setMinimumSize(new Dimension(400,400));
		
		//this.getContentPane().add(new JScrollPane(roomDisplay, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED), BorderLayout.CENTER);
		
		/*
		properties = new PropertyEditor();
		this.getContentPane().add(properties, BorderLayout.EAST);
		
		//ScreenInfo representative = null;
		for(ScreenInfo s : room.screens()) {
			if (s.objectCount() > 0) {
				//representative = s;
				ObjectElement o = s.object(0);
				properties.setObject(o, null);
				
				break;
			}
		}*/
		JPanel rightPanel = new JPanel();
		//ItemSelectorPanel rightPanel = new ItemSelectorPanel();
		/*
		JPanel rightPanel = new JPanel();
		rightPanel.setMaximumSize(new Dimension(300, -1));
		rightPanel.setMinimumSize(new Dimension(100, -1));
		rightPanel.setPreferredSize(new Dimension(300, -1));
		LinearLayout layout = new LinearLayout();
		rightPanel.setLayout(layout);
		TestCard selectedTile = new TestCard();
		selectedTile.setPreferredSize(new Dimension(-1, 64));
		rightPanel.add(selectedTile);
		JComboBox<ItemCategory> dropdown = new JComboBox<>(ItemCategory.values());
		//TestCard dropdown = new TestCard();
		//dropdown.setPreferredSize(new Dimension(-1, 16));
		rightPanel.add(dropdown);
		JPanel tilesView = new JPanel();
		tilesView.setLayout(new CardLayout());
		for(ItemType type : ItemType.values.values()) {
			ItemTile tile = new ItemTile();
			tile.setItem(type);
			tilesView.add(tile);
		}
		//TestCard tilesView = new TestCard();
		tilesView.setPreferredSize(new Dimension(-1, -1));
		tilesView.setBackground(Color.DARK_GRAY);
		rightPanel.add(tilesView);*/
		
		
		/*for(int i=0; i<12; i++) {
			TestCard t = new TestCard();
			rightPanel.add(t);
			//layout.addLayoutComponent("", t);
		}*/
		//this.getContentPane().add(rightPanel, BorderLayout.EAST);
		
		splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, roomScroll, rightPanel);
		this.getContentPane().add(splitPane, BorderLayout.CENTER);
		
		
		this.setMinimumSize(new Dimension(640, 480));
		this.setPreferredSize(new Dimension(640, 480));
	}
	
	
}
