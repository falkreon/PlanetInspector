package blue.endless.pi.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.util.Map;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;

import blue.endless.pi.SchemaType;
import blue.endless.pi.enigma.wrapper.MapObjectInfo;
import blue.endless.pi.enigma.wrapper.WorldInfo;
import blue.endless.pi.gui.view.AbstractView;
import blue.endless.pi.gui.view.ViewContext;

public class RoomConfiguratorView extends AbstractView {
	private static final Map<String, SchemaType<?>> ENEMY_SCHEMA = Map.of(
			"id", SchemaType.IMMUTABLE_INT,
			"x", SchemaType.IMMUTABLE_INT,
			"y", SchemaType.IMMUTABLE_INT,
			"type", SchemaType.IMMUTABLE_INT,
			"lock", SchemaType.IMMUTABLE, // TODO: This should be a BOOLEAN
			"rot", SchemaType.IMMUTABLE_INT,
			"level", SchemaType.INT
			);
	
	private final RoomDisplayPanel roomDisplay;
	private JPanel emptyPanel = new JPanel();
	private ItemEditorPanel itemSelector = new ItemEditorPanel();
	private PropertyEditor editor = new PropertyEditor();
	
	public RoomConfiguratorView(ViewContext context, WorldInfo world, int roomId) {
		super(context);
		//super("Configuring "+world.rooms().get(roomId).name());
		
		emptyPanel.setBackground(new Color(80, 80, 80));
		
		roomDisplay = new RoomDisplayPanel(world, roomId);
		roomDisplay.setSelectCallback((mapObject) -> {
			switch (mapObject) {
				case MapObjectInfo.ItemInfo item -> {
					itemSelector.selectItem(item);
					itemSelector.setEditCallback(roomDisplay::repaint);
					this.rightPanel = itemSelector;
					context.setRightPanel(itemSelector);
				}
				case MapObjectInfo.EnemyInfo enemy -> {
					editor.setObject(enemy.json(), ENEMY_SCHEMA);
					this.rightPanel = editor;
					context.setRightPanel(editor);
				}
				
				case null -> {
					this.rightPanel = emptyPanel;
					context.setRightPanel(emptyPanel);
				}
				
				default -> {
					
				}
			}
			//itemSelector.selectItem(item);
			//itemSelector.setEditCallback(this::repaint);
			//splitPane.setRightComponent(itemSelector);
		});
		//this.getContentPane().setLayout(new BorderLayout());
		JScrollPane roomScroll = new JScrollPane(roomDisplay, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		roomScroll.getHorizontalScrollBar().setUnitIncrement(16);
		roomScroll.getVerticalScrollBar().setUnitIncrement(16);
		roomScroll.setMinimumSize(new Dimension(400,400));
		mainPanel = roomScroll;
		rightPanel = editor;
		
		//splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, roomScroll, emptyPanel);
		//splitPane.setResizeWeight(1.0);
		//this.getContentPane().add(splitPane, BorderLayout.CENTER);
		
		
		//this.setMinimumSize(new Dimension(640, 480));
		//this.setPreferredSize(new Dimension(640, 480));
	}
	
	
}
