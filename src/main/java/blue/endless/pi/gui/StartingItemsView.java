package blue.endless.pi.gui;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import blue.endless.jankson.api.document.ArrayElement;
import blue.endless.jankson.api.document.PrimitiveElement;
import blue.endless.jankson.api.document.ValueElement;
import blue.endless.pi.ItemStack;
import blue.endless.pi.enigma.ItemType;
import blue.endless.pi.enigma.wrapper.WorldInfo;
import blue.endless.pi.gui.layout.Axis;
import blue.endless.pi.gui.layout.CardLayout;
import blue.endless.pi.gui.layout.LinearLayout;
import blue.endless.pi.gui.layout.MultiItemAxisLayout;
import blue.endless.pi.gui.layout.SingleItemAxisLayout;
import blue.endless.pi.gui.view.AbstractView;
import blue.endless.pi.gui.view.ViewContext;
import it.unimi.dsi.fastutil.objects.Object2IntMap;

public class StartingItemsView extends AbstractView {
	private WorldInfo world;
	
	private JPanel startingItemDisplay = new JPanel();
	private ItemPanel lockedItemPanel = new ItemPanel();
	private ItemPanel editableItemPanel = new ItemPanel();
	//private JPanel itemContainer = new JPanel();
	private ItemPanel itemSelector = new ItemPanel();
	
	public StartingItemsView(ViewContext context, WorldInfo world) {
		super(context);
		this.world = world;
		
		/*
		CardLayout layout = new CardLayout();
		layout.setMainLineLayout(MultiItemAxisLayout.FILL_PROPORTIONAL);
		layout.setCrossLineLayout(SingleItemAxisLayout.LEADING);
		layout.setLineAxis(Axis.HORIZONTAL);
		layout.setInterCardSpacing(16);
		layout.setInterLineSpacing(16);*/
		
		LinearLayout layout = new LinearLayout();
		layout.setAxis(Axis.VERTICAL);
		layout.setMainAxisLayout(MultiItemAxisLayout.FILL_PROPORTIONAL);
		layout.setCrossAxisLayout(SingleItemAxisLayout.FILL);
		layout.setSpacing(16);
		startingItemDisplay.setLayout(layout);
		startingItemDisplay.setBorder(new EmptyBorder(16, 16, 16, 16));
		
		
		mainPanel = startingItemDisplay;
		mainPanel.add(new JLabel("Locked"));
		mainPanel.add(lockedItemPanel);
		mainPanel.add(new JLabel("Editable - Click to Remove"));
		mainPanel.add(editableItemPanel);
		
		editableItemPanel.onClick((stack) -> {
			int id = stack.item().id();
			ArrayElement itemsArray = world.json().getObject("SAMUS").getObject("items").getArray("starting");
			Iterator<ValueElement> i = itemsArray.iterator();
			while(i.hasNext()) {
				if (i.next() instanceof PrimitiveElement val) {
					int existingId = val.asInt().orElse(-1);
					if (existingId == id) {
						i.remove();
						break;
					}
				}
			}
			
			updateItems();
			mainPanel.validate();
			mainPanel.repaint();
		});
		
		itemSelector.setItems(ItemType::isReleased);
		itemSelector.onClick((stack) -> {
			ArrayElement itemsArray = world.json().getObject("SAMUS").getObject("items").getArray("starting");
			itemsArray.add(PrimitiveElement.of(stack.item().id()));
			updateItems();
			mainPanel.validate();
			mainPanel.repaint();
		});
		
		/*
		itemSelector.setItemStacks(List.<ItemStack>of(
				new ItemStack(ItemType.byName.get("screw_attack"), 2),
				new ItemStack(ItemType.byName.get("missile_tank"), 3)		
						));*/
		
		rightPanel = itemSelector;
		
		updateItems();
	}
	
	public void updateItems() {
		ArrayElement itemsArray = world.json().getObject("SAMUS").getObject("items").getArray("starting");
		List<ItemStack> stacks = ItemStack.inventoryFromIntArray(itemsArray);
		
		lockedItemPanel.setStacks(stacks, (it) -> !it.item().isReleased());
		editableItemPanel.setStacks(stacks, (it) -> it.item().isReleased());
		
		startingItemDisplay.validate();
	}
}
