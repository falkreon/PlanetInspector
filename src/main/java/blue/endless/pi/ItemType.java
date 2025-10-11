package blue.endless.pi;

public enum ItemType {
	UNKNOWN         (  0, ItemCategory.PICKUP,       "spr_ITEM_Beam_Amp_1",      new int[] {  0, 16, 32 }),
	LONG_BEAM       (  1, ItemCategory.BEAM_UPGRADE, "spr_ITEM_Beam_Long_0",     new int[] { 22, 26, 40 }),
	CHARGE_BEAM     (  2, ItemCategory.BEAM_UPGRADE, "spr_ITEM_Beam_Charge_0",   new int[] { 22, 16, 32 }),
	ICE_BEAM        (  3, ItemCategory.BEAM,         "spr_ITEM_Beam_Ice_0",      new int[] { 22, 18, 33 }),
	WAVE_BEAM       (  4, ItemCategory.BEAM,         "spr_ITEM_Beam_Wave_0",     new int[] { 22, 20, 52 }),
	SPAZER_BEAM     (  5, ItemCategory.BEAM,         "spr_ITEM_Beam_Spazer_0",   new int[] { 22, 40, 56 }),
	PLASMA_BEAM     (  6, ItemCategory.BEAM,         "spr_ITEM_Beam_Plasma_0"),
	ENERGY_TANK     (  7, ItemCategory.TANK,         "spr_ITEM_Energy_Tank_0"),
	GRAVITY_SUIT    (  8, ItemCategory.SUIT,         "spr_ITEM_Gravity_0"),
	PHAZON_SUIT     (  9, ItemCategory.SUIT),
	MORPH_BALL      ( 10, ItemCategory.BALL,         "spr_ITEM_Morph_0"),
	SPRING_BALL     ( 11, ItemCategory.BALL,         "spr_ITEM_Spring_Ball_0"),
	BOOST_BALL      ( 12, ItemCategory.BALL,         "spr_ITEM_Boost_Ball_0"),
	SPIDER_BALL     ( 13, ItemCategory.BALL,         "spr_ITEM_Spider_Ball_0"),
	BOMBS           ( 14, ItemCategory.BOMB,         "spr_ITEM_Bomb_0"),
	POWER_BOMB_TANK ( 15, ItemCategory.TANK,         "spr_ITEM_POWER_BOMB_0"), // PROBABLY the tank
	MISSILE_TANK_5  ( 16, ItemCategory.TANK,         "spr_ITEM_Missile_0"),    // Experimentally verified
	SUPER_MISSILE_TANK_2 ( 17, ItemCategory.TANK,    "spr_ITEM_Super_Missile_0"),
	HIGH_JUMP       ( 18, ItemCategory.OTHER,        "spr_ITEM_High_Jump_0"),
	SPACE_JUMP      ( 19, ItemCategory.OTHER,        "spr_ITEM_Space_Jump_0"),
	SPEED_BOOSTER   ( 20, ItemCategory.OTHER,        "spr_ITEM_Speed_Booster_0"),
	SCREW_ATTACK    ( 21, ItemCategory.OTHER,        "spr_ITEM_Screw_Attack_0"),
	HAMMER_BALL     ( 22, ItemCategory.BALL,         "spr_ITEM_Hammer_Ball_0"),  // Data mined
	THERMAL_VISOR   ( 23, ItemCategory.VISOR),
	X_RAY_VISOR     ( 24, ItemCategory.VISOR),
	REFILL          ( 25, ItemCategory.OTHER), // 40 Energy, 10 Missiles, placed at item locations in Strike Mode
	POWER_GRIP      ( 26, ItemCategory.OTHER),
	GRAPPLE_BEAM    ( 27, ItemCategory.BEAM),
	FLASH_SHIFT     ( 28, ItemCategory.OTHER), // Data mined
	ENERGY_TANK_HALF( 29, ItemCategory.TANK),  // Data mined
	STUN_CORE       ( 30, ItemCategory.CORE),
	AEGIS_CORE      ( 31, ItemCategory.CORE),
	CRYSTAL_CORE    ( 32, ItemCategory.CORE),
	MAGNET_CORE     ( 33, ItemCategory.CORE),
	PHAZON_CORE     ( 34, ItemCategory.CORE),
	CHRONO_CORE     ( 35, ItemCategory.CORE),
	PHANTOM_CORE    ( 36, ItemCategory.CORE),
	SENSOR_CORE     ( 37, ItemCategory.CORE),
	CORE_DYNAMO     ( 38, ItemCategory.CORE_UPGRADE), // reduces core swap CD
	CORE_CAPACITOR  ( 39, ItemCategory.CORE_UPGRADE), // equip an extra core
	PARALYZER       ( 40, ItemCategory.BEAM),  // Zero suit gun
	POWER_SUIT      ( 41, ItemCategory.SUIT),  // Default suit, always appears in starting items
	VARIA_SUIT      ( 42, ItemCategory.SUIT),  // Experimentally verified
	MISSILE_PLUS    ( 43, ItemCategory.TANK),
	WALL_JUMP       ( 44, ItemCategory.OTHER), // Data mined
	TALON_GRIP      ( 45, ItemCategory.OTHER), // Data mined - negates slippery surfaces
	MISSILE         ( 46, ItemCategory.MISSILE, "spr_ITEM_Missile_Launcher_0"),// Experimentally verified
	SUPER_MISSILE_TANK_0( 47, ItemCategory.TANK),  // Data mined - quantity is zero in item settings
	ZERO_SUIT       ( 48, ItemCategory.SUIT),
	CORE_DRIVER     ( 49, ItemCategory.CORE_UPGRADE), // Data mined - allows use of cores
	ENERGY_TANK_PART( 50, ItemCategory.TANK),  // Data mined - collect 4 to gain a tank
	WARP_DRIVE      ( 51, ItemCategory.OTHER), // Data mined - allows teleport
	RESERVE_TANK    ( 52, ItemCategory.TANK),  // Data mined
	SPIN_BOOST      ( 53, ItemCategory.OTHER), // Data mined - grants an additional spin jump
	CROSS_BOMBS     ( 54, ItemCategory.BOMB),  // Data mined - charge bombs for bomberman shape
	TRANSLATOR      ( 55, ItemCategory.OTHER), // Data mined - allows use of terminals
	AUTO_CHARGER    ( 56, ItemCategory.BEAM),  // Data mined - auto-charges beam weapons
	GRAVITY_BOOST   ( 57, ItemCategory.OTHER), // Data mined - extra jump in liquids
	GLIDE_THRUSTERS ( 58, ItemCategory.OTHER), // Data mined - hold jump to glide
	ROCKET_JUMP     ( 59, ItemCategory.OTHER), // Data mined - hold jump when landing from a jump to activate
	ICE_MISSILE     ( 60, ItemCategory.MISSILE),//Data mined
	DRILL_MISSILE   ( 61, ItemCategory.MISSILE),//Data mined - passes through solids and breaks crumble blocks
	DIFFUSION_MISSILE(62, ItemCategory.MISSILE),//Data mined - Charge missiles for AOE effect
	POWER_BOMB_LAUNCHER(63,ItemCategory.BOMB), // Data mined - "Allows the use of Power Bombs"
	DIFFUSION_BEAM  ( 64, ItemCategory.BEAM),  // Data mined - charged shots do AOE
	POWER_BEAM      ( 65, ItemCategory.BEAM),  // Data mined - default beam equipped in the Power Suit
	ACCEL_CHARGE    ( 66, ItemCategory.BEAM_UPGRADE, "spr_ITEM_Accel_Charge_0"), // Data mined - reduces beam charge time
	BEAM_AMP        ( 67, ItemCategory.BEAM_UPGRADE),
	SUPER_MISSILE   ( 68, ItemCategory.MISSILE), // Data mined - Opens green doors
	HYPER_MISSILE   ( 69, ItemCategory.MISSILE), // Data mined - Opens cyan doors
	STORM_MISSILE   ( 70, ItemCategory.MISSILE), // Data mined - Charge missiles to target
	ION_MISSILE     ( 71, ItemCategory.MISSILE), // Data mined - missiles are now electrical
	TURBO_MISSILE   ( 72, ItemCategory.MISSILE), // Data mined - increase fire rate, may allow auto-fire 
	ARC_BOMB        ( 73, ItemCategory.BOMB),    // Data mined - charge bombs to activate, probably releases ~5 bombs around you
	VOLT_BEAM       ( 74, ItemCategory.BEAM),    // Data mined - beams are now electrical
	MAGMA_BEAM      ( 75, ItemCategory.BEAM),    // Data mined - beams are now lasers
	LIGHT_BEAM      ( 76, ItemCategory.BEAM),    // Data mined - beams are now lasers
	ENERGY_TANK_QUARTER(77,ItemCategory.TANK),   // Data mined
	BARRIER_LAYER   ( 78, ItemCategory.SUIT),    // Data mined - did you know that Varia was originally "Barrier Suit"?
	SLIDE_GEARS     ( 79, ItemCategory.OTHER),   // Data mined - boost+down to slide
	MAGNET_BALL     ( 80, ItemCategory.BALL),    // Data mined - down+grapple to stick to surfaces
	MAGNET_GRIP     ( 81, ItemCategory.OTHER),   // Data mined - grab onto magnetic ledges
	DASH_BOOSTER    ( 82, ItemCategory.OTHER),   // Data mined - dash to build up Speed Boost
	SHINESPARK      ( 83, ItemCategory.OTHER),   // Data mined - press boost during speed to spark
	ARM_BLADES      ( 84, ItemCategory.OTHER),   // Data mined - slide down walls (and wall jump?)
	BEAM_FUSION     ( 85, ItemCategory.BEAM_UPGRADE),// Experimentally verified 
	CORE_OCTOPART   ( 86, ItemCategory.CORE_UPGRADE),// Data mined - equip unlimited cores
	AMMO_TANK_PLUS  ( 87, ItemCategory.TANK),    // Data mined - "Ammo capacity increased by 10"
	DARK_BEAM       ( 88, ItemCategory.BEAM),    // Data mined - Edgier ice beam
	CHARGE_MAGNET   ( 89, ItemCategory.BEAM_UPGRADE), // Data mined - draw in pickups while charging
	PSEUDO_SCREW    ( 90, ItemCategory.OTHER),   // Data mined - deals damage while spinning + charged
	CHARGE_FLARE    ( 91, ItemCategory.BEAM_UPGRADE), // Data mined - firing a charge beam deals player-centered AOE
	PICKUP_MAGNET   ( 92, ItemCategory.OTHER),   // Data mined - Like magnet core but as an innate ability
	SPEED_GEAR      ( 93, ItemCategory.OTHER),   // Data mined - Increases movement speed
	ADVANCED_DRIVER ( 94, ItemCategory.OTHER, "spr_ITEM_Advanced_Driver_0"),   // Data mined - "Allows Power Suit augmentation with advanced technology"
	STAMINA_TANK    ( 95, ItemCategory.OTHER),   // Data mined - "Increases Dashing Stamina by 1 Unit"
	STAMINA_REACTOR ( 96, ItemCategory.OTHER),   // Data mined - Infinite Dashing Stamina
	DASH_THRUSTERS  ( 97, ItemCategory.OTHER),   // Data mined - Hold Boost to dash - costs Stamina
	HORIZON_JUMP    ( 98, ItemCategory.OTHER),   // Data mined - Infinite horizontal spin jumps
	AMMO            ( 99, ItemCategory.TANK),    // Data mined - "Ammo capacity increased by 5"
	
	PLACE_IMPASSABLE   (100, ItemCategory.PLACEHOLDER), // Data mined
	PLACE_MAJOR_ITEM   (101, ItemCategory.PLACEHOLDER), // Enigma replaces these with majors on map gen
	PLACE_MINOR_ITEM   (102, ItemCategory.PLACEHOLDER), // Enigma replaces these with minors on map gen
	PLACE_HAZARD_RUN   (103, ItemCategory.PLACEHOLDER), // Data mined 
	PLACE_CORE         (104, ItemCategory.PLACEHOLDER), // Data mined - Probably replaced with any core
	PLACE_MISSILE      (105, ItemCategory.PLACEHOLDER), // Data mined - Probably replaced with any regular missile/tank
	PLACE_SUPER_MISSILE(106, ItemCategory.PLACEHOLDER), // Data mined - Probably replaced with any super missile/tank
	PLACE_HYPER_MISSILE(107, ItemCategory.PLACEHOLDER), // Data mined - Probably replaced with any hyper missile/tank
	PLACE_POWER_BOMB   (108, ItemCategory.PLACEHOLDER), // Data mined - Probably replaced with any power bomb/tank
	PLACE_UNKNOWN      (109, ItemCategory.PLACEHOLDER), // Data mined
	
	ANNHIHILATOR_BEAM  (110, ItemCategory.BEAM),  // Data mined - homing beam
	NOVA_BEAM          (111, ItemCategory.BEAM),  // Data mined - piercing beam
	HYPER_BEAM         (112, ItemCategory.BEAM),  // Data mined - big metroid beam
	PHAZON_BEAM        (113, ItemCategory.BEAM),  // Data mined - beam is now radioactive
	
	BEAM_AMMO          (114, ItemCategory.TANK),  // Data mined - beam ammo +20
	
	CRYO_SUIT          (115, ItemCategory.SUIT),  // Data mined
	DARK_SUIT          (116, ItemCategory.SUIT),  // Data mined
	LIGHT_SUIT         (117, ItemCategory.SUIT),  // Data mined
	PED_SUIT           (118, ItemCategory.SUIT),  // Data mined
	HAZARD_SHIELD      (119, ItemCategory.SUIT),  // Data mined - nullifies damage from a specific hazard
	METROID_SUIT       (120, ItemCategory.SUIT),  // Data mined
	ACID_SUIT          (121, ItemCategory.SUIT),  // Data mined
	VERDURE_SUIT       (122, ItemCategory.SUIT),  // Data mined
	CUSTOM_SUIT_1      (123, ItemCategory.SUIT),  // Data mined
	CUSTOM_SUIT_2      (124, ItemCategory.SUIT),  // Data mined
	CUSTOM_SUIT_3      (125, ItemCategory.SUIT),  // Data mined
	
	SPREAD_BEAM        (126, ItemCategory.BEAM),  // Data mined - increases the number of shots
	
	OXYGEN_TANK        (127, ItemCategory.TANK),  // Data mined - increases oxygen capacity
	OXYGEN_SYNTHESIZER (128, ItemCategory.OTHER), // Data mined - infinite oxygen. On by default in Enigma
	
	// Data mined - allows you to use corresponding color terminals
	TRANSLATOR_VIOLET  (129, ItemCategory.VISOR),
	TRANSLATOR_AMBER   (130, ItemCategory.VISOR),
	TRANSLATOR_EMERALD (131, ItemCategory.VISOR),
	TRANSLATOR_COBALT  (132, ItemCategory.VISOR),
	
	GRAND_BOMB         (133, ItemCategory.BOMB),  // Data mined - increases normal bomb radius
	CHOZO_SPIRIT       (134, ItemCategory.OTHER), // Data mined - increases damage at <= 30 energy
	HYPER_MISSILE_TANK (135, ItemCategory.TANK),  // Data mined - +2 hyper missiles
	SEEKER_MISSILE     (136, ItemCategory.MISSILE),//Data mined - "Hold shoot to charge targeting missiles"
	CRYSTAL_FLASH      (137, ItemCategory.OTHER), // Data mined - secret technique to convert ammo into energy
	SCAN_VISOR         (138, ItemCategory.VISOR), // Data mined - lets you scan surroundings
	COMBAT_VISOR       (139, ItemCategory.VISOR), // Data mined - basic combat visor, default in Enigma
	
	SUPER_CHARGE       (140, ItemCategory.BEAM_UPGRADE), // Data mined - launch a super missile using a charged beam instead of ammo
	DIAMOND_DUST       (141, ItemCategory.BEAM_UPGRADE), // Data mined - secret tech from charged ice beam
	ICE_SPREADER       (142, ItemCategory.BEAM_UPGRADE), // Data mined - secret tech from charged ice beam
	ASTEROID_BELT      (143, ItemCategory.BEAM_UPGRADE), // Data mined - secret tech from charged wave beam
	STARDUST_SHOWER    (144, ItemCategory.BEAM_UPGRADE), // Data mined - secret tech from charged spazer
	EMERALD_SPLASH     (145, ItemCategory.BEAM_UPGRADE), // Data mined - secret tech from charged plasma
	VOLTBUSTER         (146, ItemCategory.BEAM_UPGRADE), // Data mined - secret tech from charged volt beam
	FLAMETHROWER       (147, ItemCategory.BEAM_UPGRADE), // Data mined - secret tech from charged magma beam
	SUNBURST           (148, ItemCategory.BEAM_UPGRADE), // Data mined - secret tech from charged light beam
	DARKBURST          (149, ItemCategory.BEAM_UPGRADE), // Data mined - secret tech from charged dark beam
	SONIC_BOOM         (150, ItemCategory.BEAM_UPGRADE), // Data mined - secret tech from charged annihilator beam
	
	// Lock and key objects
	CHOZO_ARTIFACT     (151, ItemCategory.OTHER),        // Data mined - "A relic left behind by the chozo"
	DARK_TEMPLE_KEY    (152, ItemCategory.OTHER),        // Data mined
	SKY_TEMPLE_KEY     (153, ItemCategory.OTHER),        // Data mined
	ENERGY_CELL        (154, ItemCategory.OTHER),        // Data mined - probably for powering unpowered Rooms
	
	X_RAY_SCOPE        (155, ItemCategory.VISOR),        // Data mined - the classic cone of shame
	DARK_VISOR         (156, ItemCategory.VISOR),        // Data mined
	ECHO_VISOR         (157, ItemCategory.VISOR),        // Data mined
	SCREW_SHIELD       (158, ItemCategory.OTHER),        // Data mined - invulnerability while spinning, may break bomb/shot blocks or enable horizon/space
	
	// There are slots 159-199 but they are unused
	
	INVALID(-1, ItemCategory.OTHER)
	;
	
	private final int id;
	private final ItemCategory type;
	private final String spriteResource;
	private final int[] palette;
	
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
	}
	
	public int id() { return this.id; }
	public ItemCategory type() { return this.type; }
	public String spriteResource() { return this.spriteResource; }
	public int[] palette() { return this.palette; }
	
	public static ItemType byId(int id) {
		for(ItemType item : values()) {
			if (item.id == id) return item;
		}
		
		return INVALID;
	}
}
