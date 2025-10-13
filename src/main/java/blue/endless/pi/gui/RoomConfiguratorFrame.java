package blue.endless.pi.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.util.ArrayList;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;

import blue.endless.jankson.api.document.ObjectElement;

public class RoomConfiguratorFrame extends JFrame {
	private final WorldInfo world;
	private int roomId;
	private final RoomInfo room;
	private final RoomDisplayPanel roomDisplay;
	//private final PropertyEditor properties;
	
	public RoomConfiguratorFrame(WorldInfo world, int roomId) {
		super("Configuring "+world.rooms().get(roomId).name());
		this.room = world.rooms().get(roomId);
		this.world = world;
		this.roomId = roomId;
		
		roomDisplay = new RoomDisplayPanel(world, roomId);
		this.getContentPane().setLayout(new BorderLayout());
		
		
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
		rightPanel.setMaximumSize(new Dimension(400, -1));
		rightPanel.setMinimumSize(new Dimension(100, -1));
		rightPanel.setPreferredSize(new Dimension(500, -1));
		CardLayout layout = new CardLayout();
		rightPanel.setLayout(layout);
		for(int i=0; i<12; i++) {
			TestCard t = new TestCard();
			rightPanel.add(t);
			//layout.addLayoutComponent("", t);
		}
		//this.getContentPane().add(rightPanel, BorderLayout.EAST);
		
		JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, new JScrollPane(roomDisplay, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED), rightPanel);
		this.getContentPane().add(splitPane, BorderLayout.CENTER);
		
		
		this.setMinimumSize(new Dimension(640, 480));
		this.setPreferredSize(new Dimension(640, 480));
	}
	
	
}
