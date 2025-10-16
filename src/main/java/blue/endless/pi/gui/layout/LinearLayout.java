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
	//private MainAxisLayout mainAxis = MainAxisLayout.LEADING;
	MultiItemAxisLayout mainAxis = MultiItemAxisLayout.FILL_PROPORTIONAL;
	SingleItemAxisLayout crossAxis = SingleItemAxisLayout.FILL;
	//private CrossAxisLayout crossAxis = CrossAxisLayout.FILL;
	private Axis axis = Axis.VERTICAL;
	private int spacing = 4;
	
	//public void setMainAxisLayout(MainAxisLayout layout) {
	//	this.mainAxis = layout;
	//}
	
	//public void setCrossAxisLayout(CrossAxisLayout layout) {
	//	this.crossAxis = layout;
	//}
	
	public void setAxis(Axis axis) {
		this.axis = axis;
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
		/*
		switch(mainAxis) {
			case FILL_UNIFORM -> {
				int layoutWidth = container.getWidth() - container.getInsets().left - container.getInsets().right;
				int layoutHeight = container.getHeight() - container.getInsets().top - container.getInsets().bottom;
				
				int layoutSize = axis.select(layoutWidth, layoutHeight) - spacing * (components.length - 1);
				int mainAxisSize = layoutSize / components.length;
				int crossAxisSize = axis.opposite().select(layoutWidth, layoutHeight);
				Vec2 componentSize = axis.arrange(mainAxisSize, crossAxisSize);
				
				Vec2 pointer = new Vec2(container.getInsets().left, container.getInsets().top);
				Vec2 advance = axis.arrange(mainAxisSize + spacing, 0);
				
				for(Component comp : components) {
					// TODO: Off-axis layout
					comp.setLocation(pointer.x(), pointer.y());
					comp.setSize(componentSize.x(), componentSize.y());
					
					pointer.add(advance);
				}
			}
			
			case FILL_PROPORTIONAL -> {
				int layoutWidth = container.getWidth() - container.getInsets().left - container.getInsets().right;
				int layoutHeight = container.getHeight() - container.getInsets().top - container.getInsets().bottom;
				
				int maxMainAxisSize = axis.select(layoutWidth, layoutHeight);
				int maxCrossAxisSize = axis.opposite().select(layoutWidth, layoutHeight);
				
				int combinedNaturalSize = 0;
				int proportionalComponents = 0;
				
				for(Component component : components) {
					Dimension curPreferred = component.getPreferredSize();
					int naturalSize = axis.select(curPreferred);
					if (naturalSize > 0) {
						combinedNaturalSize += naturalSize;
					} else if (naturalSize == -1) {
						proportionalComponents++;
					}
				}
				
				int proportionalLayoutSpace = layoutHeight - combinedNaturalSize - ((components.length - 1) * spacing);
				
				int mainSizeForProportional = proportionalLayoutSpace / proportionalComponents;
				
				Vec2 pointer = new Vec2(container.getInsets().left, container.getInsets().top);
				for(Component component : components) {
					int componentOffAxis = Math.min(axis.opposite().select(component.getPreferredSize()), maxCrossAxisSize);
					int offAxisOffset = 0;
					
					if (componentOffAxis < maxCrossAxisSize) {
						switch(crossAxis) {
							case LEADING -> {}
							
							case TRAILING -> {
								offAxisOffset = maxCrossAxisSize - componentOffAxis;
							}
							
							case CENTER -> {
								offAxisOffset = (maxCrossAxisSize - componentOffAxis) / 2;
							}
							
							case FILL -> {
								componentOffAxis = maxCrossAxisSize;
							}
						}
					}
					
					Vec2 offAxisCorrection = axis.arrange(0, offAxisOffset);
					
					
					Dimension curPreferred = component.getPreferredSize();
					int naturalSize = axis.select(curPreferred);
					if (naturalSize >= 0) {
						int mainAxisSize = axis.select(curPreferred);
						
						Vec2 componentSize = axis.arrange(mainAxisSize, componentOffAxis);
						
					} else if (naturalSize == -1) {
						proportionalComponents++;
					}
				}
				
			}
			
			case LEADING -> {
				int layoutWidth = container.getWidth() - container.getInsets().left - container.getInsets().right;
				int layoutHeight = container.getHeight() - container.getInsets().top - container.getInsets().bottom;
				
				//int layoutSize = axis.select(layoutWidth, layoutHeight) - spacing * (components.length - 1);
				//int mainAxisSize = layoutSize / components.length;
				int crossAxisSize = axis.opposite().select(layoutWidth, layoutHeight); // This is a MAX, not universal.
				// Actual cross-axis size will depend on the component.
				
				Vec2 pointer = new Vec2(container.getInsets().left, container.getInsets().top);
				//Vec2 advance = axis.arrange(mainAxisSize + spacing, 0);
				
				for(Component comp : components) {
					int mainAxisSize = axis.select(comp.getMinimumSize());
					Vec2 advance = axis.arrange(mainAxisSize + spacing, 0);
					
					int componentOffAxis = Math.min(axis.opposite().select(comp.getMinimumSize()), crossAxisSize);
					int offAxisOffset = 0;
					
					if (componentOffAxis < crossAxisSize) {
						switch(crossAxis) {
							case LEADING -> {}
							
							case TRAILING -> {
								offAxisOffset = crossAxisSize - componentOffAxis;
							}
							
							case CENTER -> {
								offAxisOffset = (crossAxisSize - componentOffAxis) / 2;
							}
							
							case FILL -> {
								componentOffAxis = crossAxisSize;
							}
						}
					}
					
					Vec2 offAxisCorrection = axis.arrange(0, offAxisOffset);
					Vec2 componentSize = axis.arrange(mainAxisSize, componentOffAxis);
					
					comp.setLocation(pointer.x() + offAxisCorrection.x(), pointer.y() + offAxisCorrection.y());
					comp.setSize(componentSize.x(), componentSize.y());
					
					pointer = pointer.add(advance);
				}
			}*/
		//}
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
	public static enum Axis {
		HORIZONTAL(new Vec2(1, 0)),
		VERTICAL(new Vec2(0, 1));
		
		private final Vec2 direction;
		
		Axis(Vec2 direction) {
			this.direction = direction;
		}
		
		public Axis opposite() {
			if (this == HORIZONTAL) {
				return VERTICAL;
			} else {
				return HORIZONTAL;
			}
		}
		
		public Vec2 direction() { return direction; }
		
		public int select(int x, int y) {
			if (this == HORIZONTAL) {
				return x;
			} else {
				return y;
			}
		}
		
		public int select(Dimension d) {
			return select(d.width, d.height);
		}
		
		public Vec2 arrange(int main, int cross) {
			if (this == HORIZONTAL) {
				return new Vec2(main, cross);
			} else {
				return new Vec2(cross, main);
			}
		}
	}
	
	public static enum MainAxisLayout {
		LEADING,
		FILL_UNIFORM,
		FILL_PROPORTIONAL;
	}
	public static enum CrossAxisLayout {
		LEADING,
		TRAILING,
		CENTER,
		FILL;
	}
}
