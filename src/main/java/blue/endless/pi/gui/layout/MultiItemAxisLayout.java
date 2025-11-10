package blue.endless.pi.gui.layout;

import java.util.ArrayList;
import java.util.List;

public interface MultiItemAxisLayout {
	public static record ComponentAxisInfo(int minSize, int naturalSize) {}
	
	public List<SingleAxisLayout> layout(int availableSpace, int spacing, List<ComponentAxisInfo> components);
	
	public static MultiItemAxisLayout FILL_UNIFORM = (availableSpace, spacing, components) -> {
		List<SingleAxisLayout> result = new ArrayList<>();
		int availableMinusSpacing = (components.isEmpty()) ? availableSpace : availableSpace - spacing * (components.size() - 1);
		
		int perComponent = availableMinusSpacing / components.size();
		int advance = 0;
		
		// TODO: Update to Java 25 to use this instead
		//for(var _ : components) {
		for(int i=0; i<components.size(); i++) {
			result.add(new SingleAxisLayout(advance, perComponent));
			advance += perComponent + spacing;
		}
		
		return result;
	};
	
	public MultiItemAxisLayout FILL_PROPORTIONAL = (availableSpace, spacing, components) -> {
		List<SingleAxisLayout> result = new ArrayList<>();
		int availableMinusSpacing = (components.isEmpty()) ? availableSpace : availableSpace - spacing * (components.size() - 1);
		
		int naturalHeight = 0;
		int proportionalCount = 0;
		
		for(ComponentAxisInfo component : components) {
			int nat = component.naturalSize();
			if (nat != -1) {
				naturalHeight += nat;
			} else {
				proportionalCount++;
			}
		}
		
		int proportionalSpace = availableMinusSpacing - naturalHeight;
		if (proportionalSpace < 0) proportionalSpace = 0;
		int proportionalPerComponent = (proportionalCount > 0) ? proportionalSpace / proportionalCount : 0;
		
		int advance = 0;
		for(ComponentAxisInfo component : components) {
			int nat = component.naturalSize();
			if (nat == -1) {
				result.add(new SingleAxisLayout(advance, proportionalPerComponent));
				advance += proportionalPerComponent + spacing;
			} else {
				result.add(new SingleAxisLayout(advance, nat));
				advance += nat + spacing;
			}
		}
		
		return result;
	};
}
