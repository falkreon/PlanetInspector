package blue.endless.pi.enigma;

import blue.endless.jankson.api.annotation.Deserializer;
import blue.endless.jankson.api.annotation.Serializer;
import blue.endless.jankson.api.document.DoubleElement;
import blue.endless.jankson.api.document.LongElement;
import blue.endless.jankson.api.document.PrimitiveElement;
import blue.endless.jankson.api.document.ValueElement;

public record Version(long major, int minor) implements Comparable<Version> {
	private static final long DECIMAL_RESOLUTION = 1000;
	public Version(double classicVersion) {
		this((long) classicVersion, (int) (classicVersion * DECIMAL_RESOLUTION));
	}
	
	public Version(long modernVersion) {
		this(modernVersion, 0);
	}
	
	public double toDouble() {
		return major + (minor / (double) DECIMAL_RESOLUTION);
	}
	
	@Serializer
	public PrimitiveElement toJson() {
		if (minor == 0) {
			return PrimitiveElement.of(major);
		} else {
			return PrimitiveElement.of(toDouble());
		}
	}

	@Override
	public int compareTo(Version other) {
		int majors = Long.compare(this.major, other.major);
		if (majors != 0) return majors;
		
		return Integer.compare(this.minor, other.minor);
	}
	
	@Deserializer
	public static Version of(ValueElement value) {
		return switch(value) {
			case LongElement l -> new Version(l.asLong().orElse(0));
			case DoubleElement d -> new Version(d.asDouble().orElse(0));
			default -> new Version(-1, -1);
		};
	}
}
