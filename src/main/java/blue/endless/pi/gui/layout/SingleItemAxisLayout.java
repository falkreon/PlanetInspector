package blue.endless.pi.gui.layout;

@FunctionalInterface
public interface SingleItemAxisLayout {
	
	public abstract SingleAxisLayout layout(int availableSpace, int naturalSize);
	
	public static final SingleItemAxisLayout LEADING = (availableSpace, naturalSize) -> {
		if (naturalSize > availableSpace) {
			return new SingleAxisLayout(0, availableSpace);
		} else {
			return new SingleAxisLayout(0, naturalSize);
		}
	};
	
	public static final SingleItemAxisLayout TRAILING = (availableSpace, naturalSize) -> {
		if (naturalSize > availableSpace) {
			return new SingleAxisLayout(0, availableSpace);
		} else {
			return new SingleAxisLayout(availableSpace - naturalSize, naturalSize);
			
		}
	};
	
	public static final SingleItemAxisLayout CENTER = (availableSpace, naturalSize) -> {
		if (naturalSize > availableSpace) {
			return new SingleAxisLayout(0, availableSpace);
		} else {
			return new SingleAxisLayout((availableSpace - naturalSize) / 2, naturalSize);
		}
	};
	
	public static final SingleItemAxisLayout FILL = (availableSpace, naturalSize) -> {
		return new SingleAxisLayout(0, availableSpace);
	};
}
