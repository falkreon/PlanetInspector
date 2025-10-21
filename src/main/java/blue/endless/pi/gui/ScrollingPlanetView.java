package blue.endless.pi.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.util.Map;
import java.util.function.BiConsumer;

import javax.swing.JScrollPane;

import blue.endless.jankson.api.document.ObjectElement;
import blue.endless.pi.SchemaType;
import blue.endless.pi.enigma.wrapper.RoomInfo;
import blue.endless.pi.enigma.wrapper.WorldInfo;

/**
 * Zoomable and scrollable view
 */
public class ScrollingPlanetView extends JScrollPane {
	private PlanetView planetView = new PlanetView();
	
	public ScrollingPlanetView() {
		this.getViewport().setView(planetView);
		
		this.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
		this.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		
		this.getHorizontalScrollBar().setUnitIncrement(PlanetView.CELL_SIZE / 2);
		this.getVerticalScrollBar().setUnitIncrement(PlanetView.CELL_SIZE / 2);
		
		planetView.setOpaque(true);
	}

	public void setWorld(WorldInfo world) {
		planetView.setWorld(world);
		this.setViewportView(planetView);
	}
	
	public void setPropertiesConsumer(BiConsumer<ObjectElement, Map<String, SchemaType<?>>> propertiesConsumer) {
		planetView.setPropertiesConsumer(propertiesConsumer);
	}
	
	public PlanetView getView() {
		return this.planetView;
	}
}
