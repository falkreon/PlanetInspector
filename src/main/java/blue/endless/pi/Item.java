package blue.endless.pi;

public enum Item {
	ENERGY_PICKUP   (  0, ItemType.PICKUP),
	LONG_BEAM       (  1, ItemType.BEAM_UPGRADE),
	CHARGE_BEAM     (  2, ItemType.BEAM_UPGRADE),
	ICE_BEAM        (  3, ItemType.BEAM),
	WAVE_BEAM       (  4, ItemType.BEAM),
	SPAZER_BEAM     (  5, ItemType.BEAM),
	PLASMA_BEAM     (  6, ItemType.BEAM),
	ENERGY_TANK     (  7, ItemType.TANK),
	GRAVITY_SUIT    (  8, ItemType.SUIT),
	PHAZON_SUIT     (  9, ItemType.SUIT),
	MORPH_BALL      ( 10, ItemType.BALL),
	SPRING_BALL     ( 11, ItemType.BALL),
	BOOST_BALL      ( 12, ItemType.BALL),
	SPIDER_BALL     ( 13, ItemType.BALL),
	BOMBS           ( 14, ItemType.BOMB),
	POWER_BOMBS     ( 15, ItemType.BOMB),
	MISSILE_TANK_5  ( 16, ItemType.TANK), // Experimentally verified
	SUPER_MISSILE_TANK_2 ( 17, ItemType.TANK),
	HIGH_JUMP       ( 18, ItemType.OTHER),
	SPACE_JUMP      ( 19, ItemType.OTHER),
	SPEED_BOOSTER   ( 20, ItemType.OTHER),
	SCREW_ATTACK    ( 21, ItemType.OTHER),
	HAMMER_BALL     ( 22, ItemType.BALL),  // Data mined
	THERMAL_VISOR   ( 23, ItemType.VISOR),
	X_RAY_VISOR     ( 24, ItemType.VISOR),
	REFILL          ( 25, ItemType.OTHER), // 40 Energy, 10 Missiles, placed at item locations in Strike Mode
	POWER_GRIP      ( 26, ItemType.OTHER),
	GRAPPLE_BEAM    ( 27, ItemType.BEAM),
	FLASH_SHIFT     ( 28, ItemType.OTHER), // Data mined
	ENERGY_TANK_HALF( 29, ItemType.TANK),  // Data mined
	STUN_CORE       ( 30, ItemType.CORE),
	AEGIS_CORE      ( 31, ItemType.CORE),
	CRYSTAL_CORE    ( 32, ItemType.CORE),
	MAGNET_CORE     ( 33, ItemType.CORE),
	PHASON_CORE     ( 34, ItemType.CORE),
	CHRONO_CORE     ( 35, ItemType.CORE),
	PHANTOM_CORE    ( 36, ItemType.CORE),
	SENSOR_CORE     ( 37, ItemType.CORE),
	CORE_DYNAMO     ( 38, ItemType.CORE_UPGRADE), // reduces core swap CD
	CORE_CAPACITOR  ( 39, ItemType.CORE_UPGRADE), // equip an extra core
	PARALYZER       ( 40, ItemType.BEAM),  // Zero suit gun
	POWER_SUIT      ( 41, ItemType.SUIT),  // Default suit, always appears in starting items
	VARIA_SUIT      ( 42, ItemType.SUIT),  // Experimentally verified
	MISSILE_PLUS    ( 43, ItemType.TANK),
	WALL_JUMP       ( 44, ItemType.OTHER), // Data mined
	TALON_GRIP      ( 45, ItemType.OTHER), // Data mined - negates slippery surfaces
	MISSILE         ( 46, ItemType.MISSILE),// Experimentally verified
	SUPER_MISSILE_TANK_0( 47, ItemType.TANK),  // Data mined - quantity is zero in item settings
	ZERO_SUIT       ( 48, ItemType.SUIT),
	CORE_DRIVER     ( 49, ItemType.CORE_UPGRADE), // Data mined - allows use of cores
	ENERGY_TANK_PART( 50, ItemType.TANK),  // Data mined - collect 4 to gain a tank
	WARP_DRIVE      ( 51, ItemType.OTHER), // Data mined - allows teleport
	RESERVE_TANK    ( 52, ItemType.TANK),  // Data mined
	SPIN_BOOST      ( 53, ItemType.OTHER), // Data mined - grants an additional spin jump
	CROSS_BOMBS     ( 54, ItemType.BOMB),  // Data mined - charge bombs for bomberman shape
	TRANSLATOR      ( 55, ItemType.OTHER), // Data mined - allows use of terminals
	AUTO_CHARGER    ( 56, ItemType.BEAM),  // Data mined - auto-charges beam weapons
	GRAVITY_BOOST   ( 57, ItemType.OTHER), // Data mined - extra jump in liquids
	GLIDE_THRUSTERS ( 58, ItemType.OTHER), // Data mined - hold jump to glide
	ROCKET_JUMP     ( 59, ItemType.OTHER), // Data mined - hold jump when landing from a jump to activate
	ICE_MISSILE     ( 60, ItemType.MISSILE),//Data mined
	DRILL_MISSILE   ( 61, ItemType.MISSILE),//Data mined - passes through solids and breaks crumble blocks
	DIFFUSION_MISSILE(62, ItemType.MISSILE),//Data mined - Charge missiles for AOE effect
	POWER_BOMB_LAUNCHER(63,ItemType.BOMB), // Data mined - "Allows the use of Power Bombs"
	DIFFUSION_BEAM  ( 64, ItemType.BEAM),  // Data mined - charged shots do AOE
	POWER_BEAM      ( 65, ItemType.BEAM),  // Data mined - default beam equipped in the Power Suit
	ACCEL_CHARGE    ( 66, ItemType.BEAM_UPGRADE), // Data mined - reduces beam charge time
	BEAM_AMP        ( 67, ItemType.BEAM_UPGRADE),
	SUPER_MISSILE   ( 68, ItemType.MISSILE), // Data mined - Opens green doors
	HYPER_MISSILE   ( 69, ItemType.MISSILE), // Data mined - Opens cyan doors
	STORM_MISSILE   ( 70, ItemType.MISSILE), // Data mined - Charge missiles to target
	ION_MISSILE     ( 71, ItemType.MISSILE), // Data mined - missiles are now electrical
	TURBO_MISSILE   ( 72, ItemType.MISSILE), // Data mined - increase fire rate, may allow auto-fire 
	ARC_BOMB        ( 73, ItemType.BOMB),    // Data mined - charge bombs to activate, probably releases ~5 bombs around you
	VOLT_BEAM       ( 74, ItemType.BEAM),    // Data mined - beams are now electrical
	MAGMA_BEAM      ( 75, ItemType.BEAM),    // Data mined - beams are now lasers
	LIGHT_BEAM      ( 76, ItemType.BEAM),    // Data mined - beams are now lasers
	ENERGY_TANK_QUARTER(77,ItemType.TANK),   // Data mined
	BARRIER_LAYER   ( 78, ItemType.SUIT),    // Data mined - did you know that Varia was originally "Barrier Suit"?
	SLIDE_GEARS     ( 79, ItemType.OTHER),   // Data mined - boost+down to slide
	MAGNET_BALL     ( 80, ItemType.BALL),    // Data mined - down+grapple to stick to surfaces
	MAGNET_GRIP     ( 81, ItemType.OTHER),   // Data mined - grab onto magnetic ledges
	DASH_BOOSTER    ( 82, ItemType.OTHER),   // Data mined - dash to build up Speed Boost
	SHINESPARK      ( 83, ItemType.OTHER),   // Data mined - press boost during speed to spark
	ARM_BLADES      ( 84, ItemType.OTHER),   // Data mined - slide down walls (and wall jump?)
	BEAM_FUSION     ( 85, ItemType.BEAM_UPGRADE),// Experimentally verified 
	CORE_OCTOPART   ( 86, ItemType.CORE_UPGRADE),// Data mined - equip unlimited cores
	AMMO_TANK_PLUS  ( 87, ItemType.TANK),    // Data mined - "Ammo capacity increased by 10"
	DARK_BEAM       ( 88, ItemType.BEAM),    // Data mined - Edgier ice beam
	CHARGE_MAGNET   ( 89, ItemType.BEAM_UPGRADE), // Data mined - draw in pickups while charging
	PSEUDO_SCREW    ( 90, ItemType.OTHER),   // Data mined - deals damage while spinning + charged
	CHARGE_FLARE    ( 91, ItemType.BEAM_UPGRADE), // Data mined - firing a charge beam deals player-centered AOE
	PICKUP_MAGNET   ( 92, ItemType.OTHER),   // Data mined - Like magnet core but as an innate ability
	SPEED_GEAR      ( 93, ItemType.OTHER),   // Data mined - Increases movement speed
	ADVANCED_DRIVER ( 94, ItemType.OTHER),   // Data mined - "Allows Power Suit augmentation with advanced technology"
	STAMINA_TANK    ( 95, ItemType.OTHER),   // Data mined - "Increases Dashing Stamina by 1 Unit"
	STAMINA_REACTOR ( 96, ItemType.OTHER),   // Data mined - Infinite Dashing Stamina
	DASH_THRUSTERS  ( 97, ItemType.OTHER),   // Data mined - Hold Boost to dash - costs Stamina
	HORIZON_JUMP    ( 98, ItemType.OTHER),   // Data mined - Infinite horizontal spin jumps
	AMMO            ( 99, ItemType.TANK),    // Data mined - "Ammo capacity increased by 5"
	
	PLACE_IMPASSABLE   (100, ItemType.PLACEHOLDER), // Data mined
	PLACE_MAJOR_ITEM   (101, ItemType.PLACEHOLDER), // Enigma replaces these with majors on map gen
	PLACE_MINOR_ITEM   (102, ItemType.PLACEHOLDER), // Enigma replaces these with minors on map gen
	PLACE_HAZARD_RUN   (103, ItemType.PLACEHOLDER), // Data mined 
	PLACE_CORE         (104, ItemType.PLACEHOLDER), // Data mined - Probably replaced with any core
	PLACE_MISSILE      (105, ItemType.PLACEHOLDER), // Data mined - Probably replaced with any regular missile/tank
	PLACE_SUPER_MISSILE(106, ItemType.PLACEHOLDER), // Data mined - Probably replaced with any super missile/tank
	PLACE_HYPER_MISSILE(107, ItemType.PLACEHOLDER), // Data mined - Probably replaced with any hyper missile/tank
	PLACE_POWER_BOMB   (108, ItemType.PLACEHOLDER), // Data mined - Probably replaced with any power bomb/tank
	PLACE_UNKNOWN      (109, ItemType.PLACEHOLDER), // Data mined
	
	ANNHIHILATOR_BEAM  (110, ItemType.BEAM),  // Data mined - homing beam
	NOVA_BEAM          (111, ItemType.BEAM),  // Data mined - piercing beam
	HYPER_BEAM         (112, ItemType.BEAM),  // Data mined - big metroid beam
	PHAZON_BEAM        (113, ItemType.BEAM),  // Data mined - beam is now radioactive
	
	BEAM_AMMO          (114, ItemType.TANK),  // Data mined - beam ammo +20
	
	CRYO_SUIT          (115, ItemType.SUIT),  // Data mined
	DARK_SUIT          (116, ItemType.SUIT),  // Data mined
	LIGHT_SUIT         (117, ItemType.SUIT),  // Data mined
	PED_SUIT           (118, ItemType.SUIT),  // Data mined
	HAZARD_SHIELD      (119, ItemType.SUIT),  // Data mined - nullifies damage from a specific hazard
	METROID_SUIT       (120, ItemType.SUIT),  // Data mined
	ACID_SUIT          (121, ItemType.SUIT),  // Data mined
	VERDURE_SUIT       (122, ItemType.SUIT),  // Data mined
	CUSTOM_SUIT_1      (123, ItemType.SUIT),  // Data mined
	CUSTOM_SUIT_2      (124, ItemType.SUIT),  // Data mined
	CUSTOM_SUIT_3      (125, ItemType.SUIT),  // Data mined
	
	SPREAD_BEAM        (126, ItemType.BEAM),  // Data mined - increases the number of shots
	
	OXYGEN_TANK        (127, ItemType.TANK),  // Data mined - increases oxygen capacity
	OXYGEN_SYNTHESIZER (128, ItemType.OTHER), // Data mined - infinite oxygen. On by default in Enigma
	
	// Data mined - allows you to use corresponding color terminals
	TRANSLATOR_VIOLET  (129, ItemType.VISOR),
	TRANSLATOR_AMBER   (130, ItemType.VISOR),
	TRANSLATOR_EMERALD (131, ItemType.VISOR),
	TRANSLATOR_COBALT  (132, ItemType.VISOR),
	
	GRAND_BOMB         (133, ItemType.BOMB),  // Data mined - increases normal bomb radius
	CHOZO_SPIRIT       (134, ItemType.OTHER), // Data mined - increases damage at <= 30 energy
	HYPER_MISSILE_TANK (135, ItemType.TANK),  // Data mined - +2 hyper missiles
	SEEKER_MISSILE     (136, ItemType.MISSILE),//Data mined - "Hold shoot to charge targeting missiles"
	CRYSTAL_FLASH      (137, ItemType.OTHER), // Data mined - secret technique to convert ammo into energy
	SCAN_VISOR         (138, ItemType.VISOR), // Data mined - lets you scan surroundings
	COMBAT_VISOR       (139, ItemType.VISOR), // Data mined - basic combat visor, default in Enigma
	
	SUPER_CHARGE       (140, ItemType.BEAM_UPGRADE), // Data mined - launch a super missile using a charged beam instead of ammo
	DIAMOND_DUST       (141, ItemType.BEAM_UPGRADE), // Data mined - secret tech from charged ice beam
	ICE_SPREADER       (142, ItemType.BEAM_UPGRADE), // Data mined - secret tech from charged ice beam
	ASTEROID_BELT      (143, ItemType.BEAM_UPGRADE), // Data mined - secret tech from charged wave beam
	STARDUST_SHOWER    (144, ItemType.BEAM_UPGRADE), // Data mined - secret tech from charged spazer
	EMERALD_SPLASH     (145, ItemType.BEAM_UPGRADE), // Data mined - secret tech from charged plasma
	VOLTBUSTER         (146, ItemType.BEAM_UPGRADE), // Data mined - secret tech from charged volt beam
	FLAMETHROWER       (147, ItemType.BEAM_UPGRADE), // Data mined - secret tech from charged magma beam
	SUNBURST           (148, ItemType.BEAM_UPGRADE), // Data mined - secret tech from charged light beam
	DARKBURST          (149, ItemType.BEAM_UPGRADE), // Data mined - secret tech from charged dark beam
	SONIC_BOOM         (150, ItemType.BEAM_UPGRADE), // Data mined - secret tech from charged annihilator beam
	
	// Lock and key objects
	CHOZO_ARTIFACT     (151, ItemType.OTHER),        // Data mined - "A relic left behind by the chozo"
	DARK_TEMPLE_KEY    (152, ItemType.OTHER),        // Data mined
	SKY_TEMPLE_KEY     (153, ItemType.OTHER),        // Data mined
	ENERGY_CELL        (154, ItemType.OTHER),        // Data mined - probably for powering unpowered Rooms
	
	X_RAY_SCOPE        (155, ItemType.VISOR),        // Data mined - the classic cone of shame
	DARK_VISOR         (156, ItemType.VISOR),        // Data mined
	ECHO_VISOR         (157, ItemType.VISOR),        // Data mined
	SCREW_SHIELD       (158, ItemType.OTHER),        // Data mined - invulnerability while spinning, may break bomb/shot blocks or enable horizon/space
	
	// There are slots 159-199 but they are unused
	
	INVALID(-1, ItemType.OTHER)
	;
	
	private final int id;
	private final ItemType type;
	
	Item(int id, ItemType type) {
		this.id = id;
		this.type = type;
	}
	
	public int id() { return this.id; }
	public ItemType type() { return this.type; }
	
	public static Item byId(int id) {
		for(Item item : values()) {
			if (item.id == id) return item;
		}
		
		return INVALID;
	}
}
