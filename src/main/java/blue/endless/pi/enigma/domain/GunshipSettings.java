package blue.endless.pi.enigma.domain;

import blue.endless.jankson.api.document.ArrayElement;
import it.unimi.dsi.fastutil.ints.IntSet;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;

/**
 * Represents the GUNSHIP key in the root world json
 */
public class GunshipSettings {
	// TODO: Is this an IntSet or an array of domain objects?
	public ArrayElement startingLandsites = new ArrayElement();
	public boolean rechargeEnabled = true;
	public int startingPosition;
	public IntSet startingInventory = new IntOpenHashSet();
	public boolean saveAvailable = true;
	public boolean mapScannerLink = false;
}
