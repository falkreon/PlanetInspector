package blue.endless.pi.enigma;

import blue.endless.jankson.api.document.ValueElement;

public record Sector(String name, int x, int y, int width, int height, ValueElement[] elevatorScreens, int[] connections) {
	
}
