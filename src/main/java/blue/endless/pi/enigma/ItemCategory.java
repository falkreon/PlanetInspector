package blue.endless.pi.enigma;

public enum ItemCategory {
	ALL,
	PICKUP,
	
	/**
	 * BEAM is anything on your primary fire button that is affected by Beam Fusion. So, for example yes to Ice beam or
	 * Spazer, no to Long Beam or Charge Beam.
	 */
	BEAM,
	
	/**
	 * Anything that affects your (non-missile) primary fire, but isn't affected by Beam Fusion. For example, Long,
	 * Charge, BAmp
	 */
	BEAM_UPGRADE,
	MISSILE,
	TANK,
	SUIT,
	BALL,
	BOMB,
	VISOR,
	CORE,
	CORE_UPGRADE,
	PLACEHOLDER,
	OTHER;
	
}