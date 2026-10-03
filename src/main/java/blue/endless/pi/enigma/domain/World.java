package blue.endless.pi.enigma.domain;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import blue.endless.jankson.api.annotation.SerializedName;
import blue.endless.jankson.api.document.ArrayElement;
import blue.endless.jankson.api.document.ObjectElement;
import blue.endless.pi.reflect.PlanetsDeserializer;

public class World {
	// Not part of the world json itself
	public transient WorldMeta metadata = new WorldMeta();
	
	@SerializedName("GENERAL")
	public ObjectElement general = new ObjectElement();
	
	@SerializedName("GUNSHIP")
	public GunshipSettings gunship = new GunshipSettings();
	
	@SerializedName("SAMUS")
	public ObjectElement samus = new ObjectElement();
	
	@SerializedName("RULES")
	public ObjectElement rules = new ObjectElement();
	
	@SerializedName("OBJECTIVES")
	public ObjectElement objectives = new ObjectElement();
	
	@SerializedName("GENERATION_DEBUG_LOG")
	public ObjectElement generationDebugLog = new ObjectElement();
	
	@SerializedName("LIQUIDS_DAMAGE")
	public int[] liquidsDamage = {
		0, 1, 0, 2, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0
	};
	
	public List<ObjectElement> sectors = new ArrayList<>();
	public List<Area> areas = new ArrayList<>();
	public List<ObjectElement> rooms = new ArrayList<>();
	public List<ObjectElement> hazards = new ArrayList<>();
	public List<ObjectElement> itemData = new ArrayList<>();
	public List<ArrayElement> enemyData = new ArrayList<>();
	public List<ProgressionItem> progressionLog = new ArrayList<>();
	
	public World() {
		// TODO: Fill in elements that MUST be present!
		
		
	}
	
	public World(ObjectElement meta, ObjectElement main) throws IOException {
		metadata = PlanetsDeserializer.decode(meta, WorldMeta.class);
		
		
		// TODO: Fill out the rest
		general = main.getObject("GENERAL");
		gunship = PlanetsDeserializer.decode(main.getObject("GUNSHIP"), GunshipSettings.class);
		samus = main.getObject("SAMUS");
		rules = main.getObject("RULES");
		objectives = main.getObject("OBJECTIVES");
		generationDebugLog = main.getObject("GENERATION_DEBUG_LOG");
		liquidsDamage = main.getArray("LIQUIDS_DAMAGE").asIntArray().orElseGet(() -> new int[]{});
		
		sectors = PlanetsDeserializer.marshallList(main.getArray("SECTORS"), ObjectElement.class);
		areas = PlanetsDeserializer.marshallList(main.getArray("AREAS"), Area.class);
		rooms = PlanetsDeserializer.marshallList(main.getArray("ROOMS"), ObjectElement.class);
		hazards = PlanetsDeserializer.marshallList(main.getArray("HAZARDS"), ObjectElement.class);
		itemData = PlanetsDeserializer.marshallList(main.getArray("ITEM_DATA"), ArrayElement.class);
		enemyData = PlanetsDeserializer.marshallList(main.getArray("ENEMY_DATA"), ObjectElement.class);
		progressionLog = PlanetsDeserializer.marshallList(main.getArray("PROGRESSION_LOG"), ProgressionItem.class);
	}
}
