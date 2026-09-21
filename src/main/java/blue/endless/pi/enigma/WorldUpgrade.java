package blue.endless.pi.enigma;

import java.util.function.Consumer;

import blue.endless.pi.enigma.wrapper.WorldInfo;

public record WorldUpgrade(
		int sourceVersion,
		int destVersion,
		Consumer<WorldInfo> function
		) {
}
