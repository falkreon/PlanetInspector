package blue.endless.pi.gui;

import java.util.Map;
import java.util.function.BiConsumer;

import javax.swing.JScrollPane;

import blue.endless.jankson.api.document.ObjectElement;
import blue.endless.pi.SchemaType;
import blue.endless.pi.enigma.wrapper.WorldInfo;
import blue.endless.pi.gui.view.ViewContext;

/**
 * Zoomable and scrollable view
 */
public class ScrollingPlanetView extends JScrollPane {
	private PlanetView planetView;
	
	public ScrollingPlanetView(ViewContext context) {
		planetView = new PlanetView(context);
		this.getViewport().setView(planetView);
		
		this.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
		this.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		
		this.getHorizontalScrollBar().setUnitIncrement(PlanetView.CELL_SIZE);
		this.getVerticalScrollBar().setUnitIncrement(PlanetView.CELL_SIZE);
		
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
