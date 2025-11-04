package blue.endless.pi.gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.util.Map;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.EmptyBorder;

import blue.endless.jankson.api.document.ArrayElement;
import blue.endless.jankson.api.document.ObjectElement;
import blue.endless.jankson.api.document.PrimitiveElement;
import blue.endless.jankson.api.document.ValueElement;
import blue.endless.pi.BGM;
import blue.endless.pi.SchemaType;
import blue.endless.pi.enigma.wrapper.WorldInfo;
import blue.endless.pi.gui.layout.Axis;
import blue.endless.pi.gui.layout.LinearLayout;
import blue.endless.pi.gui.layout.MultiItemAxisLayout;
import blue.endless.pi.gui.layout.SingleItemAxisLayout;
import blue.endless.pi.gui.view.AbstractView;
import blue.endless.pi.gui.view.ViewContext;

public class AreaView extends AbstractView {
	private static final Map<String, SchemaType<?>> AREA_SCHEMA = Map.of(
			"name", SchemaType.STRING,
			"bgm", BGM.SCHEMA,
			"color", SchemaType.COLOR
			);
	private WorldInfo world;
	private JPanel areaPanel = new JPanel();
	private JScrollPane scroll = new JScrollPane(areaPanel, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
	
	private PropertyEditor properties = new PropertyEditor();
	
	public AreaView(ViewContext context, WorldInfo world) {
		super(context);
		this.world = world;
		
		
		areaPanel.setBorder(new EmptyBorder(16,16,16,16));
		
		LinearLayout layout = new LinearLayout();
		layout.setAxis(Axis.VERTICAL);
		layout.setSpacing(16);
		layout.setMainAxisLayout(MultiItemAxisLayout.FILL_PROPORTIONAL);
		layout.setCrossAxisLayout(SingleItemAxisLayout.CENTER);
		areaPanel.setLayout(layout);
		
		properties.setEditCallback(areaPanel::repaint);
		
		//JScrollPane scroll = new JScrollPane(areaPanel, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		
		mainPanel = scroll;
		rightPanel = properties;
		
		properties.setMinimumSize(new Dimension(400, -1));
		properties.setPreferredSize(new Dimension(400, -1));
		
		refreshAreas();
		scroll.setViewportView(areaPanel);
		scroll.validate();
	}
	
	public void refreshAreas() {
		areaPanel.removeAll();
		
		ArrayElement areasArray = world.json().getArray("AREAS");
		for(int i=0; i<areasArray.size(); i++) {
			ValueElement val = areasArray.get(i);
			if (val instanceof ObjectElement obj) {
				areaPanel.add(new AreaCard(obj, i, properties));
			}
		}
		
		JButton plus = new JButton("+");
		plus.setFont(plus.getFont().deriveFont(Font.BOLD));
		plus.setAction(new AbstractAction("+") {
			@Override
			public void actionPerformed(ActionEvent e) {
				ObjectElement newArea = new ObjectElement();
				newArea.put("name", PrimitiveElement.of("NEW AREA"));
				newArea.put("bgm", PrimitiveElement.of("bgm_Crateria_Arrival_SM"));
				newArea.put("color", PrimitiveElement.of(0x808080));
				areasArray.add(newArea);
				refreshAreas();
			}
		});
		areaPanel.add(plus);
		
		areaPanel.validate();
		scroll.setViewportView(areaPanel);
	}
	
	public static class AreaCard extends JButton {
		private ObjectElement json;
		
		public AreaCard(ObjectElement areaObj, int index, PropertyEditor properties) {
			this.json = areaObj;
			updateCard();
			
			Dimension cardSize = new Dimension(400, 100);
			this.setMinimumSize(cardSize);
			this.setPreferredSize(cardSize);
			this.setMaximumSize(cardSize);
			
			
			String buttonName = areaObj.getPrimitive("name").asString().orElse("UNKNOWN");
			this.setText(buttonName);
			this.setFont(this.getFont().deriveFont(Font.BOLD));
			
			this.setAction(new AbstractAction(buttonName) {
				@Override
				public void actionPerformed(ActionEvent e) {
					properties.setObject(areaObj, AREA_SCHEMA);
				}
			});
		}
		
		private void updateCard() {
			int bgr = json.getPrimitive("color").asInt().orElse(0xFFFFFF);
			int r = bgr & 0xFF;
			int g = (bgr >> 8) & 0xFF;
			int b = (bgr >> 16) & 0xFF;
			Color c = new Color(r, g, b);
			this.setBackground(c);
			
			int avg = (r+g+b) / 3;
			this.setForeground((avg>128) ? Color.BLACK : Color.WHITE);
			
			String buttonName = json.getPrimitive("name").asString().orElse("UNKNOWN");
			this.setText(buttonName);
		}
		
		@Override
		public void paint(Graphics g) {
			updateCard();
			
			super.paint(g);
		}
	}
}
