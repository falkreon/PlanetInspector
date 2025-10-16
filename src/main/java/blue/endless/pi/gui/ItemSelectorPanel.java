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

import blue.endless.pi.ItemCategory;
import blue.endless.pi.ItemType;
import blue.endless.pi.gui.layout.CardLayout;
import blue.endless.pi.gui.layout.LinearLayout;

public class ItemSelectorPanel extends JPanel {
	private MapObjectInfo.ItemInfo itemInfo = null;
	private JPanel selectedItemView = new JPanel();
	private ItemTile selectedItemTile = new ItemTile();
	private JLabel selectedItemLabel = new JLabel("");
	private JComboBox<ItemCategory> categoriesBox;
	private Map<ItemCategory, List<ItemType>> releasedItems = new HashMap<>();
	private JPanel itemsPanel = new JPanel();
	
	private Runnable editCallback = () -> {};
	
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
		selectedItemView.setBorder(new EmptyBorder(8, 8, 8, 8));
		selectedItemView.setBackground(Color.BLACK);
		LinearLayout selectedItemLayout = new LinearLayout();
		selectedItemLayout.setSpacing(16);
		selectedItemLayout.setAxis(LinearLayout.Axis.HORIZONTAL);
		selectedItemView.setLayout(selectedItemLayout);
		selectedItemView.setPreferredSize(new Dimension(-1, 64));
		this.add(selectedItemView);
		selectedItemTile.setEnabled(false);
		selectedItemLabel.setForeground(Color.WHITE);
		selectedItemView.add(selectedItemTile);
		selectedItemView.add(selectedItemLabel);
		
		this.add(categoriesBox);
		
		itemsPanel.setBorder(new EmptyBorder(8, 8, 8, 8));
		CardLayout cardLayout = new CardLayout();
		cardLayout.setInterCardSpacing(8);
		cardLayout.setInterLineSpacing(8);
		itemsPanel.setLayout(cardLayout);
		itemsPanel.setBackground(Color.BLACK);
		this.add(itemsPanel);
		
		this.selectItem(null);
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
					tile.setOnClick(()->setItem(item));
					itemsPanel.add(tile);
				}
			}
		} else {
			for(ItemType item : items) {
				ItemTile tile = new ItemTile();
				tile.setItem(item);
				tile.setOnClick(()->setItem(item));
				itemsPanel.add(tile);
			}
		}
		
		
		itemsPanel.validate();
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
