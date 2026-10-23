package blue.endless.pi.enigma.domain;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import blue.endless.jankson.api.annotation.SerializedName;
import blue.endless.jankson.api.document.ArrayElement;
import blue.endless.jankson.api.document.ObjectElement;
import blue.endless.jankson.api.document.ValueElement;
import blue.endless.pi.reflect.PlanetsDeserializer;

public class World {
	// Not part of the world json itself
	public transient WorldMeta metadata = new WorldMeta();
	
	@SerializedName("GENERAL")
	public WorldGeneral general = new WorldGeneral();
	
	@SerializedName("GUNSHIP")
	public GunshipSettings gunship = new GunshipSettings();
	
	@SerializedName("SAMUS")
	public Samus samus = new Samus();
	
	@SerializedName("RULES")
	public Rules rules = new Rules();
	
	@SerializedName("OBJECTIVES")
	public Objectives objectives = new Objectives();
	
	@SerializedName("GENERATION_DEBUG_LOG")
	public ObjectElement generationDebugLog = new ObjectElement();
	
	@SerializedName("LIQUIDS_DAMAGE")
	public int[] liquidsDamage = {
		0, 1, 0, 2, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0
	};
	
	@SerializedName("SECTORS")
	public List<Sector> sectors = new ArrayList<>();
	
	@SerializedName("AREAS")
	public List<Area> areas = new ArrayList<>();
	
	@SerializedName("ROOMS")
	public List<ObjectElement> rooms = new ArrayList<>();
	
	@SerializedName("HAZARDS")
	public List<Hazard> hazards = new ArrayList<>();
	
	@SerializedName("ITEM_DATA")
	public List<ItemData> itemData = new ArrayList<>();
	/**
	 * Each EnemyData is info for all variants of the enemy with the index's Id.
	 * Each entry within the EnemyData is one Level for that enemy
	 */
	@SerializedName("ENEMY_DATA")
	public List<EnemyData> enemyData = new ArrayList<>();
	@SerializedName("PROGRESSION_LOG")
	public List<ProgressionItem> progressionLog = new ArrayList<>();
	
	public World() {
		// TODO: Fill in elements that MUST be present!
		
		
	}
	
	public World(ObjectElement meta, ObjectElement main) throws IOException {
		metadata = PlanetsDeserializer.toType(meta, WorldMeta.class);
		general = PlanetsDeserializer.toType(main.getObject("GENERAL"), WorldGeneral.class);
		gunship = PlanetsDeserializer.toType(main.getObject("GUNSHIP"), GunshipSettings.class);
		samus = PlanetsDeserializer.toType(main.getObject("SAMUS"), Samus.class);
		rules = PlanetsDeserializer.toType(main.getObject("RULES"), Rules.class);
		objectives = PlanetsDeserializer.toType(main.getObject("OBJECTIVES"), Objectives.class);
		generationDebugLog = main.getObject("GENERATION_DEBUG_LOG");
		liquidsDamage = main.getArray("LIQUIDS_DAMAGE").asIntArray().orElseGet(() -> new int[]{ 0, 1, 0, 2, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 });
		
		sectors = PlanetsDeserializer.toList(main.getArray("SECTORS"), Sector.class);
		areas = PlanetsDeserializer.toList(main.getArray("AREAS"), Area.class);
		rooms = PlanetsDeserializer.toList(main.getArray("ROOMS"), ObjectElement.class);
		hazards = PlanetsDeserializer.toList(main.getArray("HAZARDS"), Hazard.class);
		itemData = PlanetsDeserializer.toList(main.getArray("ITEM_DATA"), ArrayElement.class);
		
		// Kind of an annoying process here because ENEMY_DATA is an array-of-array-of-object
		enemyData.clear();
		for(ValueElement elem : main.getArray("ENEMY_DATA")) {
			if (elem instanceof ArrayElement arr) {
				EnemyData enemy = new EnemyData();
				enemy.clear();
				for(ValueElement cur : arr) {
					EnemyData.Level level = PlanetsDeserializer.toType(cur, EnemyData.Level.class);
					enemy.add(level);
				}
				enemyData.add(enemy);
			}
		}
		//enemyData = PlanetsDeserializer.marshallList(main.getArray("ENEMY_DATA"), ObjectElement.class);
		progressionLog = PlanetsDeserializer.toList(main.getArray("PROGRESSION_LOG"), ProgressionItem.class);
	}
}
