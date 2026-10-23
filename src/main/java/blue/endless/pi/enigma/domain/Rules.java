package blue.endless.pi.enigma.domain;

import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;

public class Rules {
	
	public int saveStyle = 0;
	
	public boolean energyRefills = true;
	public boolean ammoRefills = true;
	
	public boolean roomInfoShowPopup = true;
	public boolean roomInfoShowDesigners = true;
	public boolean roomInfoShowOnMap = true;
	public int roomInfoIndicateMajor = 0;
	
	public boolean enemyRandomSpawning = true;
	public int enemyRandomSpawnRate = 2;
	public int enemyDeathMemory = 0;
	public int enemyDeathMemoryTime = 3;
	public int enemyDeathMemoryDistance = 2;
	
	public boolean showBossEnergy = true;
	public boolean bossMissiles = false;
	
	public int doorRedHealth = 5;
	public int doorPurpleHealth = 10;
	
	public int magnetEnergizeTime = 600;
	
	public int freezeEffectTime = 360;
	public boolean freezeEffectTimeByHazard = false;
	
	public int blockCrumbleTime = 24;
	public int blockReformTime = 240;
	
	public boolean mapRevealed = false;
	public boolean autoMap = true;
	public int mapScannerTime = 15;
	public int mapScannerSize = 9;
	
	// TODO: Figure out what this is for and wrap the type tighter around that purpose.
	public IntList mapMarkerIcons = new IntArrayList();
	
	public boolean routeReplay = true;
	public boolean routeReplayRevealAll = false;
	
}

