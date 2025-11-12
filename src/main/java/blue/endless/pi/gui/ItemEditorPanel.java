package blue.endless.pi.gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import blue.endless.pi.enigma.ItemCategory;
import blue.endless.pi.enigma.ItemType;
import blue.endless.pi.enigma.wrapper.MapObjectInfo;
import blue.endless.pi.gui.layout.Axis;
import blue.endless.pi.gui.layout.LinearLayout;

public class ItemEditorPanel extends JPanel {
	private MapObjectInfo.ItemInfo itemInfo = null;
	private JPanel selectedItemView = new JPanel();
	private ItemTile selectedItemTile = new ItemTile();
	private JLabel selectedItemLabel = new JLabel("");
	private JComboBox<ItemCategory> categoriesBox;
	private Map<ItemCategory, List<ItemType>> releasedItems = new HashMap<>();
	private  ItemPanel itemsPanel = new ItemPanel();
	
	private Runnable editCallback = () -> {};
	
	public ItemEditorPanel() {
		this.setMinimumSize(new Dimension(300, -1));
		this.setPreferredSize(new Dimension(300, -1));
		this.setMaximumSize(new Dimension(500, -1));
		
		this.setLayout(new LinearLayout());
		
		// Preprocess ItemTypes
		for(int i=0; i<1000; i++) {
			ItemType item = ItemType.values.get(i);
			if (item == null) continue;
			if (item.isReleased()) {
				List<ItemType> releasedOfThisCategory = releasedItems.computeIfAbsent(item.category(), (it) -> new ArrayList<>());
				releasedOfThisCategory.add(item);
			}
		}
		
		List<ItemCategory> selectableCategories = new ArrayList<>();
		selectableCategories.add(ItemCategory.ALL);
		for(ItemCategory category : ItemCategory.values()) {
			//List<ItemType> released = releasedItems.get(category);
			if (releasedItems.get(category) != null) selectableCategories.add(category);
		}
		
		
		
		categoriesBox = new JComboBox<ItemCategory>(selectableCategories.toArray(new ItemCategory[selectableCategories.size()]));
		categoriesBox.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				selectCategory((ItemCategory) categoriesBox.getSelectedItem());
			}
		});
		
		// TODO: Replace this with a card
		selectedItemView.setBorder(new EmptyBorder(8, 8, 8, 8));
		selectedItemView.setBackground(Color.BLACK);
		LinearLayout selectedItemLayout = new LinearLayout();
		selectedItemLayout.setSpacing(16);
		selectedItemLayout.setAxis(Axis.HORIZONTAL);
		selectedItemView.setLayout(selectedItemLayout);
		selectedItemView.setPreferredSize(new Dimension(-1, 64));
		this.add(selectedItemView);
		selectedItemTile.setEnabled(false);
		selectedItemLabel.setForeground(Color.WHITE);
		selectedItemView.add(selectedItemTile);
		selectedItemView.add(selectedItemLabel);
		
		this.add(categoriesBox);
		
		itemsPanel.setBorder(new EmptyBorder(8, 8, 8, 8));
		this.add(itemsPanel);
		itemsPanel.onClick((stack) -> {
			setItem(stack.item());
		});
		
		selectCategory(ItemCategory.ALL);
		this.selectItem(null);
	}
	
	private void selectCategory(ItemCategory category) {
		categoriesBox.setSelectedItem(category);
		if (category == ItemCategory.ALL) {
			itemsPanel.setItems(ItemType::isReleased);
		} else {
			itemsPanel.setItems((it) -> it.isReleased() && it.category().equals(category));
		}
		
		this.repaint();
	}
	
	public void selectItem(MapObjectInfo.ItemInfo item) {
		itemInfo = item;
		if (item != null) {
			selectedItemTile.setItem(item.item());
			selectedItemLabel.setText(item.item().name());
		} else {
			selectedItemLabel.setText("");
		}
	}
	
	public void setItem(ItemType item) {
		if (item != null) {
			selectedItemTile.setItem(item);
			selectedItemLabel.setText(item.name());
			if (itemInfo != null) {
				itemInfo.setItem(item);
				editCallback.run();
			}
		} else {
			selectedItemLabel.setText("");
		}
	}
	
	public void setEditCallback(Runnable callback) {
		this.editCallback = callback;
	}
	
}
