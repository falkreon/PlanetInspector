package blue.endless.pi.enigma.domain;

import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;

/**
 * From world HAZARDS[*], not to be confused with the room-specific one!
 */
public class Hazard {
	public String name = "ATMOSPHERIC";
	
	public int visual_style = 0;
	public int visualColor = 22;
	
	public boolean touch = false;
	public boolean suffocating = false;
	public boolean corrupting = false;
	public boolean air = true;
	
	public int damage = 2;
	public IntList disabling_flags = new IntArrayList();
}
