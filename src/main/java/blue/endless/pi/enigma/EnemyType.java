package blue.endless.pi.enigma;

import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.jetbrains.annotations.Nullable;

import blue.endless.jankson.api.document.KeyValuePairElement;
import blue.endless.jankson.api.document.ObjectElement;
import blue.endless.pi.Assets;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;

public class EnemyType {
	public static final int MOTHER_BRAIN_ID = 36; // Hardcoded because MB searches are common.
	
	public static final Map<String, EnemyType> byName = new HashMap<>();
	public static final Int2ObjectOpenHashMap<EnemyType> values = new Int2ObjectOpenHashMap<EnemyType>();
	
	public static void load(ObjectElement obj) {
		for(KeyValuePairElement kvp : obj) {
			if (kvp.getValue() instanceof ObjectElement ob2) {
				String id = kvp.getKey();
				EnemyType monster = new EnemyType(ob2);
				if (monster.id() == -1) {
					System.out.println("Error loading '"+id+"': "+ob2);
					continue;
				}
				
				byName.put(id, monster);
				values.put(monster.id(), monster);
			}
		}
	}
	
	//ZOOMER(0, "spr_Enemy_zoomer_NEW_0"),
	/*
	nova 1
	zeela 2
	viola 3
	zeb 4
	gamet 5
	zebbo 6
	geega 7
	squeept 8
	waver 9
	skree 10
	rio 11
	ripper II 12
	dragon 13
	geruta 14
	side hopper 15
	dessgeega 16
	holtz 17
	ripper 18
	multiviola 19
	flower 20
	vine (short) 21
	vine (long) 22
	mellow 23
	memu 24
	mella 25
	metroid 26
	rinka 27
	turret 28
	polyp 29
	*fake kraid 30 (blue, despawns immediately)
	zebetite 31
	atomic 32
	worker 33
	kraid 34
	ridley 35
	mother brain 36
	spark 37
	*"kabedora" (i.e. gadora, door eye? yellow square, crashes on load if variable "eye" not defined) 38
	spore spawn 39
	*phantoon 40 (crashes on testing)
	*super kraid's head 41 (crashes on testing)
	*super ridley's body 42 (crashes on testing)
	*super mother brain 43 (crashes on testing)
	tangle weed 44
	giant hopper 45
	hive 46
	yakchi 47
	cacti 48
	spore bud 49
	giant dessgeega 50*/
	;
	
	private final int id;
	private final String name;
	private final String spriteResource;
	private final boolean isBoss;
	
	public EnemyType(ObjectElement obj) {
		this.id = obj.getPrimitive("id").asInt().orElse(0);
		this.name = obj.getPrimitive("name").asString().orElse("Unknown");
		this.spriteResource = obj.getPrimitive("sprite").asString().orElse("");
		this.isBoss = obj.getPrimitive("boss").asBoolean().orElse(Boolean.FALSE).booleanValue();
	}
	
	public int id() { return id; }
	public String name() { return name; }
	public String spriteResource() { return spriteResource; }
	public boolean isBoss() { return this.isBoss; }
	
	public BufferedImage getSprite() {
		return Assets.getCachedImage("enemies/"+this.spriteResource+".png").orElseGet(Assets::missingImage);
	}
	
	public BufferedImage getSprite(int[] palette) {
		Optional<BufferedImage> opt = Assets.getPalettedImage("enemies/"+this.spriteResource+".png", palette);
		if (opt.isEmpty()) {
			System.out.println("Error getting sprite for monster "+this.id);
			return null;
		} else {
			return opt.get();
		}
	}
	
	public static @Nullable EnemyType of(int id) {
		return values.getOrDefault(id, null);
	}
}
