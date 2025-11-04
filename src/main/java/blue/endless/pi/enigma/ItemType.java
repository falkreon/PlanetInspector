package blue.endless.pi.enigma;

import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import org.jetbrains.annotations.Nullable;

import blue.endless.jankson.api.document.ArrayElement;
import blue.endless.jankson.api.document.KeyValuePairElement;
import blue.endless.jankson.api.document.ObjectElement;
import blue.endless.pi.Assets;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;

public class ItemType {
	public static final Map<String, ItemType> byName = new HashMap<>();
	public static final Int2ObjectOpenHashMap<ItemType> values = new Int2ObjectOpenHashMap<ItemType>();
	
	public static final Set<String> LOCKED = Set.of(
			"zero_suit", "stun_pistol"
			);
	
	public static void load(ObjectElement obj) {
		for(KeyValuePairElement kvp : obj) {
			if (kvp.getValue() instanceof ObjectElement ob2) {
				String id = kvp.getKey();
				ItemType item = new ItemType(ob2);
				if (item.id() == -1 || item.category() == null) {
					System.out.println("Error loading '"+id+"': "+ob2);
					continue;
				}
				
				byName.put(id, item);
				values.put(item.id(), item);
			}
		}
	}
	
	private final int id;
	private final String name;
	private final ItemCategory type;
	/**
	 * If an item is normally obtainable as a collectable in-world item in a randomly-generated Enigma world, or it's
	 * selectable as a starting item in Enigma, it's considered released.
	 */
	private final boolean released;
	private final String spriteResource;
	private final int[] palette;
	private final ObjectElement defaultParams;
	
	public ItemType(ObjectElement obj) {
		Optional<String> category = obj.getPrimitive("category").asString();
		if (category.isEmpty()) {
			this.id = -1;
			this.type = ItemCategory.OTHER;
		} else {
			this.id = obj.getPrimitive("id").asInt().orElse(-1);
			this.type = ItemCategory.valueOf(category.get());
		}
		this.name = obj.getPrimitive("name").asString().orElse("UNKNOWN");
		this.released = obj.getPrimitive("released").asBoolean().orElse(false);
		this.spriteResource = obj.getPrimitive("sprite").asString().orElse("");
		ArrayElement paletteArr = obj.getArray("sprite_palette");
		palette = new int[3];
		for(int i=0; i<3; i++) {
			if (i >= paletteArr.size()) break;
			palette[i] = paletteArr.getPrimitive(i).asInt().orElse(0);
		}
		defaultParams = obj.getObject("parameters");
	}
	
	/*
	ItemType(int id, ItemCategory type) {
		this.id = id;
		this.type = type;
		this.spriteResource = "spr_ITEM_Beam_Amp_1";
		this.palette = new int[] { 0, 16, 32 };
	}

	ItemType(int id, ItemCategory type, String spriteResource) {
		this.id = id;
		this.type = type;
		this.spriteResource = spriteResource;
		this.palette = new int[] { 0, 16, 32 };
	}
	
	ItemType(int id, ItemCategory type, String spriteResource, int[] palette) {
		this.id = id;
		this.type = type;
		this.spriteResource = spriteResource;
		this.palette = palette;
	}*/
	
	public int id() { return this.id; }
	public String name() { return this.name; }
	public ItemCategory category() { return this.type; }
	public boolean isReleased() { return this.released; }
	public String spriteResource() { return this.spriteResource; }
	public BufferedImage getSprite() {
		Optional<BufferedImage> opt = Assets.getPalettedImage("items/"+this.spriteResource+".png", this.palette);
		if (opt.isEmpty()) {
			System.out.println("Error getting sprite for item "+this.id);
			return null;
		} else {
			return opt.get();
		}
	}
	public int[] palette() { return this.palette; }
	public ObjectElement defaultParams() { return this.defaultParams; }
	
	@Override
	public String toString() {
		return this.name;
	}
	
	public static @Nullable ItemType of(int id) {
		return values.getOrDefault(id, null);
	}
}
