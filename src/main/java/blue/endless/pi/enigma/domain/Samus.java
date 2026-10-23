package blue.endless.pi.enigma.domain;

import java.util.ArrayList;
import java.util.List;

import blue.endless.jankson.api.annotation.SerializedName;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import it.unimi.dsi.fastutil.ints.IntSet;

public class Samus {
	
	public int loadState = 3;
	public int oxygenBaseCapacity = 30;
	public int invincibilityFrames = 64;
	
	public Ammo ammo = new Ammo();
	public Weapons weapons = new Weapons();
	public Energy energy = new Energy();
	public Movement movement = new Movement();
	public List<Missiles> missiles = new ArrayList<>();
	public Items items = new Items();
	
	public static class Ammo {
		@SerializedName("tanks_grant_capacity")
		boolean tanksGrantCapacity = true;
		
		// Use Universal Ammunition (from Metroid Prime Hunters)?
		boolean universal = false;
	}
	
	public static class Weapons {
		// Beam stuff
		int beamDistance = 56;
		int beamSpeed = 5;
		int beamBaseCooldown = 9;
		boolean inheritMomentum = true;
		boolean beamAutofire = true;
		int autofireCooldown = 3;
		int chargeTime = 90;
		int chargeComboAmmo = 1;
		
		// Missile Stuff
		boolean missileAutofire = false;
		boolean missileChargeReplaceAmmo = false;
		int missileDamageFrozenMultiplier = 1;
		
		// Stuns
		boolean stunMooks = false;
		int stunTimeMooks = 10;
		boolean stunBosses = false;
		int stunTimeBosses = 10;
		
		// Bombs & Power Bombs
		int bombBaseDamage = 3;
		int bombScaleDamageFromBeams = 1;
		int powerBombBaseDamage = 12;
		
		// Speedboost & Shinespark
		int speedboostDamage = 30;
		int shinesparkDamage = 30;
		int shinesparkDamageFromSuits = 0;
		
		// Bash? Maybe spider+spring+boost jumps?
		int bashDamage = 10;
	}
	
	public static class Energy {
		int hudType = 0;
		boolean tanksGrantCapacity = true;
		boolean lastChance = true;
	}
	
	public static class Movement {
		public double walkSpeed = 0.75;
		public double walkSpeedMax = 1.5;
		public double airSpeed = 0.75;
		public double airSpeedMax = 1.5;
		public double jumpPower = 4.0;
		public double gravity = 0.1;
		public double gravityMax = 3.5;
		public double morphSpeed = 0.75;
		public double morphSpeedMax = 1.5;
		
		public double liquidSlowdown = 0.6;
		public double speedBoostDistance = 20;
		public int spinJumpTiming = 0;
		
		public boolean enableDownwardAiming = true;
		public boolean enableSpiderBoost = true;
		public boolean enableUnmorphJump = true;
		public boolean enableRespin = false;
		public boolean enableMidairMorph = false;
		
		public boolean speedAutoRun = true;
		public boolean speedKeep = true;
		public boolean speedKeepAirborne = true;
		public boolean speedKeepSpider = true;
		public boolean speedBoostWalkOnLiquids = true;
		public boolean speedBoostBounceOnLiquids = true;
		
		public boolean speedBoostShotBlocks = false;
		public boolean speedBoostBombBlocks = false;
		public boolean screwShotBlocks = false;
		public boolean screwBombBlocks = false;
		public boolean bashBombBlocks = false;
		public boolean bashShotBlocks = false;
		
		public boolean forceWalljumpMomentum = true;
		public boolean allowExtraJumpsInLiquids = false;
		
		public Knockback knockback = new Knockback();
		
	}
	
	public static class Knockback {
		public boolean boostingAllowed = true;
		public double boostingForce = 4.0;
		public int boostingTime = 30;
		public int style = 0;
		public int baseTime = 12;
		public double baseHForce = 4.0;
		public double baseVForce = 2.5;
	}
	
	public static class Missiles {
		public int damage = 8;
		public int ammoUse = 1;
		public int screenLimit = 10;
		public int cooldown = 18;
		public double speedStart = 0.5;
		public double speedAcceleration = 0.3;
		public double speedMax = 3.5;
	}
	
	public static class Items {
		public IntSet starting = IntOpenHashSet.of();
	}
}

