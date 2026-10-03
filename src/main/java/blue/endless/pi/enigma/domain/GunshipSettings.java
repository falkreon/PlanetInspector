package blue.endless.pi.enigma.domain;

import blue.endless.jankson.api.annotation.SerializedName;
import blue.endless.jankson.api.document.ArrayElement;
import it.unimi.dsi.fastutil.ints.IntSet;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;

/**
 * Represents the GUNSHIP key in the root world json
 */
public class GunshipSettings {
	@SerializedName("starting_landsites")
	public ArrayElement startingLandsites = new ArrayElement();
	
	@SerializedName("recharge_enabled")
	public boolean rechargeEnabled = true;
	
	@SerializedName("starting_position")
	public int startingPosition;
	
	@SerializedName("starting_inventory")
	public IntSet startingInventory = new IntOpenHashSet();
	
	@SerializedName("save_available")
	public boolean saveAvailable = true;
	
	@SerializedName("map_scanner_link")
	public boolean mapScannerLink = false;
}
