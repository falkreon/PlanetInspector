package blue.endless.pi.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.util.Map;
import java.util.function.BiConsumer;

import javax.swing.JScrollPane;

import blue.endless.jankson.api.document.ObjectElement;
import blue.endless.pi.SchemaType;

/**
 * Zoomable and scrollable view
 */
public class ScrollingPlanetView extends JScrollPane {
	private PlanetView planetView = new PlanetView();
	
	public ScrollingPlanetView() {
		this.getViewport().setView(planetView);
		
		this.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
		this.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		
		planetView.setOpaque(true);
	}

	public void setWorld(WorldInfo world) {
		planetView.setWorld(world);
	}
	
	public void setPropertiesConsumer(BiConsumer<ObjectElement, Map<String, SchemaType>> propertiesConsumer) {
		planetView.setPropertiesConsumer(propertiesConsumer);
	}
}
