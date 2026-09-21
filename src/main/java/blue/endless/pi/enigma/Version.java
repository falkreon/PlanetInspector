package blue.endless.pi.enigma;

import blue.endless.jankson.api.document.DoubleElement;
import blue.endless.jankson.api.document.PrimitiveElement;
import blue.endless.jankson.api.document.ValueElement;

public record Version(long major, int minor) implements Comparable<Version> {
	private static final long DECIMAL_RESOLUTION = 1000;
	public Version(double jsonVersion) {
		this((long) jsonVersion, (int) (jsonVersion * DECIMAL_RESOLUTION));
	}
	
	public double toDouble() {
		return major + (minor / (double) DECIMAL_RESOLUTION);
	}
	
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
	
	public static Version of(ValueElement value) {
		return switch(value) {
			case DoubleElement d -> new Version(d.asDouble().orElse(0));
			default -> new Version(-1, -1);
		};
	}
}
