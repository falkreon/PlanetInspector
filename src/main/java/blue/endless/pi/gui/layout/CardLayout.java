package blue.endless.pi.gui.layout;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import blue.endless.pi.datastruct.Vec2;

public class CardLayout implements LayoutManager {
	/** Space between cards on the same "line" */
	private int interCardSpacing = 4;
	/** Space between "lines" in the layout */
	private int interLineSpacing = 4;
	
	LinearLayout.Axis mainLineAxis = LinearLayout.Axis.HORIZONTAL;
	LinearLayout.Axis crossLineAxis = LinearLayout.Axis.VERTICAL;
	private MultiItemAxisLayout mainAxisLineLayout  = MultiItemAxisLayout.FILL_PROPORTIONAL;
	private SingleItemAxisLayout crossAxisLineLayout = SingleItemAxisLayout.FILL;
	
	
	@Override
	public void layoutContainer(Container container) {
		Insets insets = container.getInsets();
		Dimension layoutSize = container.getSize();
		int layoutWidth = layoutSize.width - insets.left - insets.right;
		//System.out.println("Laying out for "+layoutSize.width+"px wide...");
		
		ArrayList<Component> line = new ArrayList<>();
		
		int advanceX = 0;
		int advanceY = 0;
		ArrayList<Component> remaining = new ArrayList<>();
		for(Component c : container.getComponents()) remaining.add(c);
		int iters = remaining.size() * 2;
		while(!remaining.isEmpty()) {
			Component first = remaining.get(0);
			Dimension firstSize = first.getMinimumSize();
			
			if (firstSize.width > layoutWidth && line.isEmpty()) {
				// Lay this component out right here and advance the line
				//System.out.println("Component requires entire line");
				line.add(first);
				remaining.remove(0);
				advanceX = 0;
				advanceY += layoutLine(line, layoutWidth, insets.left, insets.top + advanceY);
				line.clear();
			} else {
				int proposedWidth = advanceX + firstSize.width;
				if (!line.isEmpty()) proposedWidth += interCardSpacing;
				
				if (proposedWidth <= layoutWidth) {
					// Add the element to the line and keep going
					//System.out.println("Add and keep going: Proposed width of "+proposedWidth+" is less than available "+layoutWidth);
					line.add(first);
					remaining.remove(0);
					advanceX += firstSize.width + interCardSpacing;
					
				} else {
					// Add nothing - lay out the line and come back to lay out the components
					//System.out.println("Advance Line; proposed width of "+proposedWidth+" is greater than available "+layoutWidth);
					advanceX = 0;
					advanceY += layoutLine(line, layoutWidth, insets.left, insets.top + advanceY);
					line.clear();
				}
			}
			iters--;
			if (iters == 0) break;
		}
		if (!line.isEmpty()) {
			layoutLine(line, layoutWidth, insets.left, insets.top + advanceY);
			line.clear();
		}
	}
	
	/**
	 * Lays out one line of components 
	 * @param line
	 * @param layoutWidth
	 * @param baseX
	 * @param baseY
	 * @return
	 */
	private int layoutLine(ArrayList<Component> line, int layoutWidth, int baseX, int baseY) {
		if (line.isEmpty()) {
			return 0;
		}
		
		//System.out.println("Laying out "+line.size()+" components on a line...");
		
		// Capture line metrics
		ArrayList<MultiItemAxisLayout.ComponentAxisInfo> metrics = new ArrayList<>();
		int intrinsicLineHeight = 0;
		for(Component component : line) {
			int minMainSize = mainLineAxis.select(component.getMinimumSize());
			int intrinsicMainSize = mainLineAxis.select(component.getPreferredSize());
			int curCrossSize = crossLineAxis.select(component.getPreferredSize());
			if (curCrossSize != -1) intrinsicLineHeight = Math.max(intrinsicLineHeight, curCrossSize);
			
			metrics.add(new MultiItemAxisLayout.ComponentAxisInfo(minMainSize, intrinsicMainSize));
		}
		
		List<SingleAxisLayout> lineLayout = mainAxisLineLayout.layout(layoutWidth, interCardSpacing, metrics);
		Iterator<SingleAxisLayout> lineLayoutIter = lineLayout.iterator();
		for(Component component : line) {
			if (!lineLayoutIter.hasNext()) break;
			SingleAxisLayout mainLayout = lineLayoutIter.next();
			
			int curCrossSize = crossLineAxis.select(component.getPreferredSize());
			SingleAxisLayout crossLayout = crossAxisLineLayout.layout(intrinsicLineHeight, curCrossSize);
			
			Vec2 componentPosition = mainLineAxis.arrange(mainLayout.start(), crossLayout.start());
			Vec2 componentSize = mainLineAxis.arrange(mainLayout.size(), crossLayout.size());
			component.setSize(componentSize.x(), componentSize.y());
			component.setLocation(componentPosition.x() + baseX, componentPosition.y() + baseY);
		}
		
		return intrinsicLineHeight + interLineSpacing;
	}
	
	@Override
	public Dimension minimumLayoutSize(Container container) {
		int minWidth = Integer.MAX_VALUE;
		int minHeight = Integer.MAX_VALUE;
		for(Component component : container.getComponents()) {
			minWidth = Math.min(component.getWidth(), minWidth);
			minHeight = Math.min(component.getHeight(), minHeight);
		}
		if (minWidth == Integer.MAX_VALUE) minWidth = 0;
		if (minHeight == Integer.MAX_VALUE) minHeight = 0;
		return new Dimension(minWidth, minHeight);
	}

	@Override
	public Dimension preferredLayoutSize(Container container) {
		// Return the largest preferred width of any component
		int maxWidth = 0;
		for(Component comp : container.getComponents()) {
			maxWidth = Math.max(maxWidth, comp.getPreferredSize().width);
		}
		return new Dimension(maxWidth, Integer.MAX_VALUE);
	}

	@Override
	public void addLayoutComponent(String name, Component component) {}
	
	@Override
	public void removeLayoutComponent(Component component) {}
	
}
