package blue.endless.pi.gui.layout;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.LayoutManager;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import blue.endless.pi.datastruct.Vec2;

public class LinearLayout implements LayoutManager {
	MultiItemAxisLayout mainAxis = MultiItemAxisLayout.FILL_PROPORTIONAL;
	SingleItemAxisLayout crossAxis = SingleItemAxisLayout.FILL;
	private Axis axis = Axis.VERTICAL;
	private int spacing = 4;
	
	public void setMainAxisLayout(MultiItemAxisLayout layout) {
		this.mainAxis = layout;
	}
	
	public void setCrossAxisLayout(SingleItemAxisLayout layout) {
		this.crossAxis = layout;
	}
	
	public void setAxis(Axis axis) {
		this.axis = axis;
	}
	
	public void setSpacing(int spacing) {
		this.spacing = spacing;
	}
	
	@Override
	public void addLayoutComponent(String name, Component component) {}
	
	@Override
	public void removeLayoutComponent(Component container) {}
	
	@Override
	public void layoutContainer(Container container) {
		Component[] components = container.getComponents();
		if (components.length == 0) return;
		
		int layoutWidth = container.getWidth() - container.getInsets().left - container.getInsets().right;
		int layoutHeight = container.getHeight() - container.getInsets().top - container.getInsets().bottom;
		
		int mainAxisSpace = axis.select(layoutWidth, layoutHeight);
		int crossAxisSpace = axis.opposite().select(layoutWidth, layoutHeight);
		
		List<MultiItemAxisLayout.ComponentAxisInfo> componentMetrics = new ArrayList<>();
		for(Component component : components) {
			componentMetrics.add(new MultiItemAxisLayout.ComponentAxisInfo(
					axis.select(component.getMinimumSize()),
					axis.select(component.getPreferredSize())
					));
		}
		List<SingleAxisLayout> layout = mainAxis.layout(
				mainAxisSpace,
				spacing,
				componentMetrics
				);
		
		Iterator<SingleAxisLayout> mainLayouts = layout.iterator();
		for(Component component : components) {
			if (!mainLayouts.hasNext()) break;
			SingleAxisLayout mainLayout = mainLayouts.next();
			SingleAxisLayout crossLayout = crossAxis.layout(crossAxisSpace, axis.opposite().select(component.getPreferredSize()));
			Vec2 componentPosition = axis.arrange(mainLayout.start(), crossLayout.start());
			Vec2 componentSize = axis.arrange(mainLayout.size(), crossLayout.size());
			component.setSize(componentSize.x(), componentSize.y());
			component.setLocation(componentPosition.x() + container.getInsets().left, componentPosition.y() + container.getInsets().top);
		}
	}

	@Override
	public Dimension minimumLayoutSize(Container container) {
		if (container.getComponents().length == 0) return new Dimension(0, 0);
		int mainAxisSize = 0;
		int crossAxisSize = 0;
		
		boolean first = true;
		for(Component c : container.getComponents()) {
			Dimension d = c.getMinimumSize();
			mainAxisSize += axis.select(d);
			if (first) {
				first = false;
			} else {
				mainAxisSize += spacing;
			}
			
			int curCrossAxis = axis.opposite().select(d);
			crossAxisSize = Math.max(crossAxisSize, curCrossAxis);
		}
		
		Vec2 result = axis.arrange(mainAxisSize, crossAxisSize);
		return new Dimension(result.x(), result.y());
	}

	@Override
	public Dimension preferredLayoutSize(Container container) {
		return minimumLayoutSize(container);
	}
	
}
