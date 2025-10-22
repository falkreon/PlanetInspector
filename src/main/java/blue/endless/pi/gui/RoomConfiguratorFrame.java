package blue.endless.pi.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;

import blue.endless.pi.enigma.wrapper.WorldInfo;

public class RoomConfiguratorFrame extends JFrame {
	private final RoomDisplayPanel roomDisplay;
	JSplitPane splitPane;
	private ItemSelectorPanel itemSelector = new ItemSelectorPanel();
	
	public RoomConfiguratorFrame(WorldInfo world, int roomId) {
		super("Configuring "+world.rooms().get(roomId).name());
		
		roomDisplay = new RoomDisplayPanel(world, roomId);
		roomDisplay.setItemSelectCallback((item) -> {
			itemSelector.selectItem(item);
			itemSelector.setEditCallback(this::repaint);
			splitPane.setRightComponent(itemSelector);
		});
		this.getContentPane().setLayout(new BorderLayout());
		JScrollPane roomScroll = new JScrollPane(roomDisplay, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		roomScroll.setMinimumSize(new Dimension(400,400));
		
		splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, roomScroll, itemSelector);
		this.getContentPane().add(splitPane, BorderLayout.CENTER);
		
		
		this.setMinimumSize(new Dimension(640, 480));
		this.setPreferredSize(new Dimension(640, 480));
	}
	
	
}
