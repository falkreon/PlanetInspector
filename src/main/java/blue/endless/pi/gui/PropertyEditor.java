package blue.endless.pi.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.OptionalDouble;

import javax.swing.Box;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.jetbrains.annotations.Nullable;

import blue.endless.jankson.api.document.ArrayElement;
import blue.endless.jankson.api.document.ObjectElement;
import blue.endless.jankson.api.document.PrimitiveElement;
import blue.endless.jankson.api.document.ValueElement;
import blue.endless.jankson.impl.document.BooleanElementImpl;
import blue.endless.jankson.impl.document.DoubleElementImpl;
import blue.endless.jankson.impl.document.LongElementImpl;
import blue.endless.jankson.impl.document.StringElementImpl;
import blue.endless.pi.SchemaType;

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
	
	public void setObject(ObjectElement obj, Map<String, SchemaType> knownKeys) {
		List<String> remainingKeys = new ArrayList<>(obj.keySet());
		editorPanel = new JPanel();
		editorPanel.setLayout(new GridLayout(0, 2));
		
		for(Map.Entry<String, SchemaType> entry : knownKeys.entrySet()) {
			//System.out.println("Adding schema line for "+entry.getKey());
			addLine(entry.getKey(), obj.get(entry.getKey()), entry.getValue());
			remainingKeys.remove(entry.getKey());
		}
		
		for(String s : remainingKeys) {
			//System.out.println("Adding non-schema line for "+s);
			addLine(s, obj.get(s), null);
		}
		
		//editorPanel.add(Box.createVerticalGlue());
		
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
	
	private JComponent createEditorComponent(ValueElement value, SchemaType schema) {
		// First, booleans.
		if (schema == SchemaType.BOOLEAN) {
			JCheckBox field = new JCheckBox();
			if (value instanceof BooleanElementImpl b) {
				field.setSelected(b.asBoolean().get());
				return field;
			} else if (value instanceof DoubleElementImpl d) {
				field.setSelected(d.asDouble().getAsDouble() != 0);
				return field;
			} else if (value instanceof LongElementImpl l) {
				field.setSelected(l.asLong().getAsLong() != 0);
				return field;
			}
			
			// TODO: wire in the containing object and the key name so we can update the backing data
			field.addChangeListener(new ChangeListener() {
				@Override
				public void stateChanged(ChangeEvent arg0) {
					System.out.println("Value changed to "+field.isSelected());
				}});
			// Otherwise fall through
		}
		
		if (schema == SchemaType.INT) {
			if (value instanceof LongElementImpl l) {
				JTextField field = new JTextField();
				field.setText(represent(value));
				return field;
			} else if (value instanceof DoubleElementImpl d) {
				JTextField field = new JTextField();
				field.setText(Long.toString((long) d.asDouble().getAsDouble()));
				return field;
			}
		}
		
		if (schema == SchemaType.FLOAT) {
			if (value instanceof PrimitiveElement prim) {
				OptionalDouble opt = prim.asDouble();
				if (opt.isPresent()) {
					JTextField field = new JTextField();
					field.setText(Double.toString(opt.getAsDouble()));
					return field;
				}
			}
		}
		
		if (schema == SchemaType.STRING) {
			if (value instanceof StringElementImpl s) {
				JTextField field = new JTextField();
				field.setText(represent(value));
				return field;
			}
		}
		
		if (schema == SchemaType.OBJECT) {
			if (value instanceof ObjectElement) {
				// Report good but not-editable
				JTextField field = new JTextField();
				field.setText(represent(value));
				field.setFont(field.getFont().deriveFont(Font.BOLD));
				field.setEnabled(false);
				return field;
			}
		} else if (schema == SchemaType.ARRAY) {
			if (value instanceof ArrayElement) {
				JTextField field = new JTextField();
				field.setText(represent(value));
				field.setFont(field.getFont().deriveFont(Font.BOLD));
				field.setEnabled(false);
				return field;
			}
		}
		
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
	
	private void addLine(String key, ValueElement value, SchemaType schema) {
		JLabel label = new JLabel(key);
		editorPanel.add(label);
		
		JComponent editorComponent = createEditorComponent(value, schema);
		editorPanel.add(editorComponent);
	}
}
