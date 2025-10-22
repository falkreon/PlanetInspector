package blue.endless.pi.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

import blue.endless.jankson.api.document.ArrayElement;
import blue.endless.jankson.api.document.ObjectElement;
import blue.endless.jankson.api.document.PrimitiveElement;
import blue.endless.jankson.api.document.ValueElement;
import blue.endless.pi.SchemaType;
import blue.endless.pi.gui.layout.Axis;
import blue.endless.pi.gui.layout.LinearLayout;
import blue.endless.pi.gui.layout.MultiItemAxisLayout;
import blue.endless.pi.gui.layout.SingleItemAxisLayout;

public class PropertyEditor extends JPanel {
	private JLabel title = new JLabel("Properties");
	private JScrollPane scrollPane = new JScrollPane();
	private JPanel editorPanel = null;
	private ObjectElement value = null;
	
	public PropertyEditor() {
		this.setLayout(new BorderLayout());
		this.add(title, BorderLayout.NORTH);
		this.add(scrollPane, BorderLayout.CENTER);
		this.setMinimumSize(new Dimension(200, 200));
		this.setMaximumSize(new Dimension(300, Integer.MAX_VALUE));
		this.setPreferredSize(new Dimension(300, 600));
		
		scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
	}
	
	public void setObject(ObjectElement obj, Map<String, SchemaType<?>> knownKeys) {
		
		this.value = obj;
		editorPanel = new JPanel();
		editorPanel.setBorder(new EmptyBorder(8, 8, 8, 8));
		LinearLayout layout = new LinearLayout();
		layout.setAxis(Axis.VERTICAL);
		layout.setMainAxisLayout(MultiItemAxisLayout.FILL_PROPORTIONAL);
		layout.setCrossAxisLayout(SingleItemAxisLayout.FILL);
		editorPanel.setLayout(layout);
		
		if (obj == null) {
			scrollPane.getViewport().setView(editorPanel);
			return;
		}
		
		List<String> remainingKeys = new ArrayList<>(obj.keySet());
		
		
		if (knownKeys != null) {
			for(Map.Entry<String, SchemaType<?>> entry : knownKeys.entrySet()) {
				//System.out.println("Adding schema line for "+entry.getKey());
				addLine(entry.getKey(), obj.get(entry.getKey()), entry.getValue());
				remainingKeys.remove(entry.getKey());
			}
		}
		
		for(String s : remainingKeys) {
			//System.out.println("Adding non-schema line for "+s);
			addLine(s, obj.get(s), null);
		}
		
		scrollPane.getViewport().setView(editorPanel);
	}
	
	private String represent(ValueElement value) {
		if (value == null) return "null";
		
		switch (value) {
			case ObjectElement obj -> {
				return "{ }";
			}
			case ArrayElement arr -> {
				return "[ ]";
			}
			case PrimitiveElement prim -> {
				return prim.asString().get();
			}
			default -> {
				return value.toString();
			}
		}
	}
	
	private JComponent createEditorComponent(ValueElement value, SchemaType<?> schema) {
		
		// Misc values that have *no* schemaType
		
		if (schema == null) {
			JTextField field = new JTextField();
			field.setText(represent(value));
			field.setDisabledTextColor(new Color(128, 64, 0));
			field.setFont(field.getFont().deriveFont(Font.BOLD));
			field.setEnabled(false);
			return field;
		}
		
		JTextField field = new JTextField();
		field.setText(represent(value));
		field.setDisabledTextColor(Color.RED);
		field.setFont(field.getFont().deriveFont(Font.BOLD));
		field.setEnabled(false);
		return field;
	}
	
	private void addLine(String key, ValueElement value, SchemaType<?> schema) {
		JComponent editorComponent = (schema != null) ?
			schema.createEditor(this.value, key) :
			createEditorComponent(value, schema);
		
		addLine(key, editorComponent);
	}
	
	public void addLine(String key, JComponent editor) {
		JPanel panel = new JPanel();
		LinearLayout layout = new LinearLayout();
		layout.setAxis(Axis.HORIZONTAL);
		layout.setMainAxisLayout(MultiItemAxisLayout.FILL_UNIFORM);
		layout.setCrossAxisLayout(SingleItemAxisLayout.FILL);
		panel.setLayout(layout);
		JLabel label = new JLabel(key);
		panel.add(label);
		panel.add(editor);
		editorPanel.add(panel);
	}
	
	public void addExternalLine(String label, ObjectElement sourceObject, String sourceKey, SchemaType<?> schema) {
		JComponent editor = (schema != null) ?
				schema.createEditor(sourceObject, sourceKey) :
				SchemaType.IMMUTABLE.createEditor(sourceObject, sourceKey);
		addLine(label, editor);
		editorPanel.validate();
	}
	
	public void addExternalLine(String label, ArrayElement sourceArray, int sourceIndex, SchemaType<?> schema) {
		JComponent editor = (schema != null) ?
				schema.createEditor(sourceArray, sourceIndex) :
				SchemaType.IMMUTABLE.createEditor(sourceArray, sourceIndex);
		addLine(label, editor);
		editorPanel.validate();
	}
}
