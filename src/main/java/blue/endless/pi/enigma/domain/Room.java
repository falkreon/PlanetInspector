package blue.endless.pi.enigma.domain;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import blue.endless.jankson.api.annotation.Deserializer;
import blue.endless.jankson.api.annotation.SerializedName;
import blue.endless.jankson.api.annotation.Serializer;
import blue.endless.jankson.api.document.ArrayElement;
import blue.endless.jankson.api.document.ObjectElement;
import blue.endless.jankson.api.document.PrimitiveElement;
import blue.endless.jankson.api.document.ValueElement;
import blue.endless.pi.enigma.util.EnigmaFormat;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import it.unimi.dsi.fastutil.ints.IntSet;

public class Room {
	@SerializedName("META")
	public Meta meta = new Meta();
	
	@SerializedName("STATS")
	public Stats stats = new Stats();
	
	@SerializedName("GENERAL")
	public General general = new General();
	
	@SerializedName("PATHING")
	public List<Pathing> pathing = new ArrayList<>();
	
	@SerializedName("HAZARD")
	public Hazard hazard = new Hazard();
	
	@SerializedName("PALETTES")
	public List<PaletteEntry> palettes = new ArrayList<>();
	
	@SerializedName("SCREENS")
	public List<Screen> screens = new ArrayList<>();
	
	
	
	@SerializedName("PARALLAX")
	public ObjectElement parallax;
	
	
	
	@SerializedName("VARIATIONS")
	public ArrayElement variations = new ArrayElement();
	@SerializedName("EVENTS")
	public ArrayElement events = new ArrayElement();
	
	
	
	public static class Meta {
		public long id = 0L;
		public boolean playable = true;
		public int lastPatch = 5;
		public int contentVersion = 1;
		public double lastSave = 0.0;
		
		public ZonedDateTime lastSave() {
			return EnigmaFormat.decodeTimestamp(lastSave);
		}
		
		public void markSaved() {
			lastSave = EnigmaFormat.createTimestamp(ZonedDateTime.now());
		}
	}
	
	public static class Stats {
		public boolean emergencyLinear = false;
		public int screens = 1;
		public IntSet objectTypes = new IntOpenHashSet();
		public ObjectElement region = new ObjectElement();
		public int boss = -1;
		public int alternateLogic = 0;
		public int landsite = 0;
		public int items = 0;
		public IntSet enemyTypes = new IntOpenHashSet();
		public int doorDirections = 0;
		public int combat = 0;
		public boolean potentialMajor = false;
		@SerializedName("class")
		public int roomClass = 2;
		public int pathingItems = 0;
		public int liquidTypes = 0;
		public boolean missilesRequired = false;
		public IntList elevators = new IntArrayList();
		public int doors = 0;
		public boolean farming = false;
		public boolean filler = false;
		public boolean emergencyOpen = true;
		public int blockTypes = 0;
	}
	
	public static class General {
		public String name = "";
		public String designer = "";
		public String bgm = "";
		
		public int bgColor = 64;
		
		public int threat = 0;
		public int spikeLevel = 0;
		public int difficulty = 0;
		
		public int sector = 0;
		public int area = 0;
		public Set<String> areas = new HashSet<>();
		public Set<String> tags = new HashSet<>();
		public int[] focus = new int[] { 0, 0, 0 };
		
		public boolean classicPathing = true;
		public boolean emergencyIntended = true;
		public boolean powered = true;
		public boolean darkness = false;
		public boolean noFloor = false;
		
		public int visionLimit = 0;
		public int miniboss = 0;
		public double gravityMultiplier = 1.0;
		
		public Magnet magnet = new Magnet();
	}
	
	public static class Magnet {
		public boolean usePalettes = false;
		public boolean useShader = false;
		public int onPalette = 21;
		public int offPalette = 0;
	}
	
	public static class PaletteEntry {
		@SerializedName("f")
		public List<PaletteFrame> frames = new ArrayList<>();
		@SerializedName("s")
		public int frameDelay = 0;
	}
	
	
	public record PaletteFrame(int a, int b, int c) {
		public int[] toArray() {
			return new int[] { a, b, c };
		}
		
		@Serializer
		public ArrayElement toJson() {
			ArrayElement arr = new ArrayElement();
			arr.add(PrimitiveElement.of(a));
			arr.add(PrimitiveElement.of(b));
			arr.add(PrimitiveElement.of(c));
			return arr;
		}
		
		@Deserializer
		public static PaletteFrame of(ValueElement value) {
			if (value instanceof ArrayElement arr) {
				int a = arr.getPrimitive(0).asInt().orElse(0);
				int b = arr.getPrimitive(1).asInt().orElse(0);
				int c = arr.getPrimitive(2).asInt().orElse(0);
				return new PaletteFrame(a, b, c);
			} else {
				return new PaletteFrame(0, 0, 0);
			}
		}
	}
	
	//public static class RegionInfo {
		
	//}
	
	public static class Hazard {
		public boolean set = false;
		public int type = 0;
		public int blockType = 1;
		public int tanks = 1;
		public int style = 0;
		public int color = 22;
	}
}
