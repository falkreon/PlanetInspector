package blue.endless.pi.gui;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.util.ArrayList;

public class CardLayout implements LayoutManager {

	private ArrayList<Component> components = new ArrayList<>();
	private int interCardSpacing = 4;
	
	@Override
	public void addLayoutComponent(String name, Component component) {
		components.add(component);
	}

	@Override
	public void layoutContainer(Container container) {
		Insets insets = container.getInsets();
		Dimension layoutSize = container.getSize();
		int layoutWidth = layoutSize.width - insets.left - insets.right;
		//int layoutHeight = layoutSize.height - insets.left - insets.right;
		
		ArrayList<Component> line = new ArrayList<>();
		
		int advanceX = 0;
		int advanceY = 0;
		ArrayList<Component> remaining = new ArrayList<>();
		for(Component c : container.getComponents()) remaining.add(c);
		System.out.println("Laying out "+remaining.size()+" components.");
		int iters = remaining.size() * 2;
		while(!remaining.isEmpty()) {
			Component first = remaining.get(0);
			Dimension firstSize = first.getMinimumSize();
			
			if (firstSize.width > layoutWidth && line.isEmpty()) {
				// Lay this component out right here and advance the line
				System.out.println("Component requires entire line");
				line.add(first);
				remaining.remove(0);
				advanceX = 0;
				advanceY += layoutLine(line, layoutWidth, insets.left, insets.top + advanceY);
			} else {
				int proposedWidth = advanceX + firstSize.width;
				if (!line.isEmpty()) proposedWidth += interCardSpacing;
				
				if (proposedWidth <= layoutWidth) {
					// Add the element to the line and keep going
					System.out.println("Add and keep going: Proposed width of "+proposedWidth+" is less than available "+layoutWidth);
					line.add(first);
					remaining.remove(0);
					advanceX += firstSize.width + interCardSpacing;
					
				} else {
					// Add nothing - lay out the line and come back to lay out the components
					System.out.println("Advance Line; proposed width of "+proposedWidth+" is greater than available "+layoutWidth);
					advanceX = 0;
					advanceY += layoutLine(line, layoutWidth, insets.left, insets.top + advanceY);
				}
			}
			iters--;
			if (iters == 0) break;
		}
		if (!line.isEmpty()) {
			layoutLine(line, layoutWidth, insets.left, insets.top + advanceY);
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
		
		Dimension min = getMinLineSize(line);
		
		int available = layoutWidth - (interCardSpacing * (line.size() - 1));
		int perComponent = available / line.size();
		int advanceX = baseX;
		for(Component comp : line) {
			System.out.println("Laying out component at "+advanceX+", "+baseY+" at "+perComponent+" x "+min.height);
			comp.setLocation(advanceX, baseY);
			comp.setSize(perComponent, min.height);
			
			advanceX += perComponent + interCardSpacing;
		}
		
		line.clear();
		return min.height;
	}
	
	private Dimension getMinLineSize(ArrayList<Component> line) {
		if (line.isEmpty()) return new Dimension(0, 0);
		int lineMinWidth = 0;
		int lineMinHeight = 0;
		for(Component comp : line) {
			Dimension min = comp.getMinimumSize();
			lineMinWidth += min.width;
			lineMinHeight = Math.max(lineMinHeight, min.height);
		}
		lineMinWidth += interCardSpacing * (line.size() - 1);
		
		return new Dimension(lineMinWidth, lineMinHeight);
	}

	@Override
	public Dimension minimumLayoutSize(Container container) {
		int minWidth = Integer.MAX_VALUE;
		int minHeight = Integer.MAX_VALUE;
		for(Component component : components) {
			minWidth = Math.min(component.getWidth(), minWidth);
			minHeight = Math.min(component.getHeight(), minHeight);
		}
		if (minWidth == Integer.MAX_VALUE) minWidth = 0;
		if (minHeight == Integer.MAX_VALUE) minHeight = 0;
		return new Dimension(minWidth, minHeight);
	}

	@Override
	public Dimension preferredLayoutSize(Container container) {
		return new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE);
	}

	@Override
	public void removeLayoutComponent(Component component) {
		components.remove(component);
	}
	
}
