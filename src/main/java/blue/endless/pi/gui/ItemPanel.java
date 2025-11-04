package blue.endless.pi.gui;

import java.awt.Color;
import java.awt.Dimension;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;

import javax.swing.JPanel;
import javax.swing.TransferHandler;
import javax.swing.border.EmptyBorder;

import blue.endless.pi.ItemStack;
import blue.endless.pi.enigma.ItemType;
import blue.endless.pi.gui.layout.CardLayout;

public class ItemPanel extends JPanel {
	private List<ItemStack> items = new ArrayList<>();
	private Consumer<ItemStack> onClick = (it) -> {};
	
	public ItemPanel() {
		CardLayout layout = new CardLayout();
		layout.setInterCardSpacing(16);
		layout.setInterLineSpacing(16);
		this.setLayout(layout);
		this.setBorder(new EmptyBorder(8, 8, 8, 8));
		this.setPreferredSize(new Dimension(8 + 8 + 32 + 32 + 16, -1));
		
		this.setBackground(Color.BLACK);
	}
	
	public void onClick(Consumer<ItemStack> callback) {
		this.onClick = callback;
		updateItems();
	}
	
	public void setItems(Iterable<ItemType> items) {
		this.items.clear();
		for(ItemType item : items) {
			this.items.add(new ItemStack(item));
		}
		updateItems();
	}
	
	public void setItemStacks(Iterable<ItemStack> stacks) {
		this.items.clear();
		for(ItemStack stack : items) {
			this.items.add(stack);
		}
		updateItems();
	}
	
	public void setItems(Predicate<ItemType> predicate) {
		this.items.clear();
		for(ItemType item : ItemType.values.values()) {
			if (predicate.test(item)) {
				this.items.add(new ItemStack(item));
			}
		}
		updateItems();
	}
	
	public void setItems(Iterable<ItemType> items, Predicate<ItemType> predicate) {
		this.items.clear();
		for(ItemType item : items) {
			if (predicate.test(item)) this.items.add(new ItemStack(item));
		}
		updateItems();
	}
	
	public void setStacks(Iterable<ItemStack> stacks, Predicate<ItemStack> predicate) {
		this.items.clear();
		for(ItemStack stack : stacks) {
			if (predicate.test(stack)) this.items.add(stack);
		}
		updateItems();
	}
	
	public void updateItems() {
		this.removeAll();
		
		for(ItemStack item : items) {
			ItemTile tile = new ItemTile();
			tile.setStack(item);
			tile.onClick(() -> this.onClick.accept(item));
			
			this.add(tile);
		}
		
		validate();
	}
}


