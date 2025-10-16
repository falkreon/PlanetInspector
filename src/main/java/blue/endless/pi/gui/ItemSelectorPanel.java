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
import javax.swing.JPanel;

import blue.endless.pi.ItemCategory;
import blue.endless.pi.ItemType;
import blue.endless.pi.gui.layout.CardLayout;
import blue.endless.pi.gui.layout.LinearLayout;

public class ItemSelectorPanel extends JPanel {
	private TestCard selectedItemView = new TestCard();
	private JComboBox<ItemCategory> categoriesBox;
	private Map<ItemCategory, List<ItemType>> releasedItems = new HashMap<>();
	private JPanel itemsPanel = new JPanel();
	
	public ItemSelectorPanel() {
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
			List<ItemType> released = releasedItems.get(category);
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
		selectedItemView.setPreferredSize(new Dimension(-1, 64));
		this.add(selectedItemView);
		
		this.add(categoriesBox);
		
		itemsPanel.setLayout(new CardLayout());
		itemsPanel.setBackground(Color.BLACK);
		this.add(itemsPanel);
		
		
	}
	
	private void selectCategory(ItemCategory category) {
		categoriesBox.setSelectedItem(category);
		itemsPanel.removeAll();
		List<ItemType> items = releasedItems.get(category);
		if (items == null) {
			for(int i=0; i<1000; i++) {
				ItemType item = ItemType.values.get(i);
				if (item != null) {
					if (!item.isReleased()) continue;
					ItemTile tile = new ItemTile();
					tile.setItem(item);
					itemsPanel.add(tile);
				}
			}
		} else {
			for(ItemType item : items) {
				ItemTile tile = new ItemTile();
				tile.setItem(item);
				itemsPanel.add(tile);
			}
		}
		
		
		itemsPanel.validate();
		this.repaint();
	}
	
}
