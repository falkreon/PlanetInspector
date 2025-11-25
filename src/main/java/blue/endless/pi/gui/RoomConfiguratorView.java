package blue.endless.pi.gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.OptionalInt;

import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import blue.endless.jankson.api.document.ArrayElement;
import blue.endless.jankson.api.document.PrimitiveElement;
import blue.endless.pi.enigma.DoorType;
import blue.endless.pi.enigma.ObjectType;
import blue.endless.pi.enigma.wrapper.MapObjectInfo;
import blue.endless.pi.enigma.wrapper.WorldInfo;
import blue.endless.pi.gui.view.AbstractView;
import blue.endless.pi.gui.view.ViewContext;

public class RoomConfiguratorView extends AbstractView {
	
	private final RoomDisplayPanel roomDisplay;
	private JPanel emptyPanel = new JPanel();
	private ItemEditorPanel itemSelector = new ItemEditorPanel();
	private PropertyEditor editor = new PropertyEditor();
	
	public RoomConfiguratorView(ViewContext context, WorldInfo world, int roomId) {
		super(context);
		
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
					editor.setObject(null, null);
					editor.addExternalLine("Type", new JLabel(enemy.enemy().name()));
					editor.addExternalLine("Position", new JLabel(
							enemy.x()+", "+enemy.y()
							));
					int enemyTierCount = 0;
					int enemyTypeId = enemy.enemy().id();
					ArrayElement enemyData = world.json().getArray("ENEMY_DATA");
					if (enemyTypeId >= 0 && enemyTypeId < enemyData.size()) {
						ArrayElement enemyTiers = enemyData.getArray(enemyTypeId);
						enemyTierCount = enemyTiers.size();
					}
					
					JSlider tierSlider = new JSlider();
					tierSlider.setMinimum(1);
					tierSlider.setMaximum(enemyTierCount);
					tierSlider.setMajorTickSpacing(1);
					tierSlider.setPaintTicks(true);
					tierSlider.setValue(enemy.json().getPrimitive("level").asInt().orElse(0) + 1);
					tierSlider.addChangeListener(new ChangeListener() {
						@Override
						public void stateChanged(ChangeEvent e) {
							enemy.json().put("level", PrimitiveElement.of(tierSlider.getValue() - 1));
							roomDisplay.repaint();
						}
					});
					editor.addExternalLine("Level", tierSlider);
					
					//editor.setObject(enemy.json(), ENEMY_SCHEMA);
					this.rightPanel = editor;
					context.setRightPanel(editor);
				}
				
				case MapObjectInfo.MapElevatorInfo elevator -> {
					editor.setObject(null, null);
					editor.addExternalLine("Direction", new JLabel(elevator.dir().toString()));
					JCheckBox escapeCheckBox = new JCheckBox();
					escapeCheckBox.setSelected(elevator.isEscape());
					escapeCheckBox.addChangeListener(new ChangeListener() {
						@Override
						public void stateChanged(ChangeEvent arg0) {
							elevator.setEscape(escapeCheckBox.isSelected());
							roomDisplay.repaint();
						}
					});
					editor.addExternalLine("Is Escape?", escapeCheckBox);
					//editor.setObject(elevator.json(), null);
					this.rightPanel = editor;
					context.setRightPanel(editor);
				}
				
				case MapObjectInfo.DoorObjectInfo door -> { // TODO: Something else!
					editor.setObject(null, null);
					editor.addExternalLine("Direction", new JLabel(door.dir().toString()));
					
					JComboBox<DoorType> valuesControl = DoorType.createControl();
					valuesControl.setSelectedItem(door.doorType());
					valuesControl.addItemListener(new ItemListener() {
						@Override
						public void itemStateChanged(ItemEvent e) {
							DoorType doorType = (DoorType) valuesControl.getSelectedItem();
							DoorType actualType = (doorType == DoorType.ZERO) ? DoorType.BLUE : doorType;
							door.json().put("type", PrimitiveElement.of(actualType.value()));
							ArrayElement mapCellDoors = door.screen().json().getObject("MAP").getArray("doors");
							mapCellDoors.set(door.dir().value(), PrimitiveElement.of(doorType.value()));
							mainPanel.repaint();
						}
					});
					
					editor.addExternalLine("Type", valuesControl);
					
					this.rightPanel = editor;
					context.setRightPanel(editor);
				}
				
				case null -> {
					this.rightPanel = emptyPanel;
					context.setRightPanel(emptyPanel);
				}
				
				default -> {
					ObjectType type = mapObject.type();
					if (type != null) {
						switch(type) {
							case SCANNER -> {
								editor.setObject(mapObject.json(), null);
							}
							case GUNSHIP -> {
								editor.setObject(mapObject.json(), null);
							}
							
							default -> {
								editor.setObject(mapObject.json(), null);
							}
						}
						
						this.rightPanel = editor;
						context.setRightPanel(editor);
					} else {
						OptionalInt opt = mapObject.json().getPrimitive("type").asInt();
						if (opt.isPresent()) {
							System.out.println("Selected unknown object of type "+opt.getAsInt());
						}
						
						editor.setObject(mapObject.json(), null);
						this.rightPanel = editor;
						context.setRightPanel(editor);
					}
				}
			}
		});
		
		JScrollPane roomScroll = new JScrollPane(roomDisplay, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		roomScroll.getHorizontalScrollBar().setUnitIncrement(16);
		roomScroll.getVerticalScrollBar().setUnitIncrement(16);
		roomScroll.setMinimumSize(new Dimension(400,400));
		mainPanel = roomScroll;
		rightPanel = editor;
	}
	
	
}
