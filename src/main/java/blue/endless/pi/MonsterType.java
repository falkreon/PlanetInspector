package blue.endless.pi;

public enum MonsterType {
	ZOOMER(0, "spr_Enemy_zoomer_NEW_0"),
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
	
	private final int value;
	private final String spriteResource;
	
	MonsterType(int value, String spriteResource) {
		this.value = value;
		this.spriteResource = spriteResource;
	}
}
