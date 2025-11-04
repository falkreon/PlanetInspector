package blue.endless.pi;

import java.util.ArrayList;
import java.util.List;

import blue.endless.jankson.api.document.ArrayElement;
import blue.endless.jankson.api.document.PrimitiveElement;
import blue.endless.jankson.api.document.ValueElement;
import blue.endless.pi.enigma.ItemType;

public record ItemStack(ItemType item, int count) {
	public ItemStack(ItemType item) {
		this(item, 1);
	}
	
	public static List<ItemStack> inventoryFromIntArray(ArrayElement array) {
		ArrayList<ItemStack> result = new ArrayList<>();
		
		for(ValueElement val : array) {
			if (val instanceof PrimitiveElement prim) {
				int itemId = prim.asInt().orElse(-1);
				if (itemId < 0) continue;
				
				ItemType item = ItemType.of(itemId);
				if (item != null) {
					boolean found = false;
					for(int i=0; i<result.size(); i++) {
						ItemStack stack = result.get(i);
						if (stack.item() == item) {
							found = true;
							result.set(i, new ItemStack(stack.item(), stack.count() + 1));
							break;
						}
					}
					if (!found) result.add(new ItemStack(item));
				}
			}
		}
		
		return result;
	}
}