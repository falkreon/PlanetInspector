package blue.endless.pi;


import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Optional;

import javax.swing.JComponent;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import blue.endless.jankson.api.document.ArrayElement;
import blue.endless.jankson.api.document.ObjectElement;
import blue.endless.jankson.api.document.PrimitiveElement;
import blue.endless.jankson.api.document.ValueElement;
import blue.endless.jankson.impl.document.NullElementImpl;

public sealed interface SchemaType<T> permits SchemaType.Editable, SchemaType.NonEditable {
	// Editable
	public static final SchemaType<String> NON_EDITABLE = new NonEditable();
	
	public static final SchemaType<String> STRING = new EditableString();
	public static final SchemaType<Double> DOUBLE = new EditableDouble();
	public static final SchemaType<Integer> INT = new EditableInt();
	//public static final SchemaType<Boolean> BOOLEAN = new SchemaType<>();
	
	// Not editable, limited display
	//public static final SchemaType<ArrayElement> ARRAY = new SchemaType<>();
	//public static final SchemaType<ObjectElement> OBJECT = new SchemaType<>();
	
	default T get(ObjectElement parent, String key) {
		return deserialize(parent.get(key));
	}
	
	default T get(ArrayElement parent, int index) {
		return deserialize(parent.get(index));
	}
	
	T deserialize(ValueElement v);
	
	default JComponent createEditor(ObjectElement parent, String key) {
		JTextField result = new JTextField();
		result.setBackground(Color.WHITE);
		T value = get(parent, key);
		if (value == null) {
			result.setText("null");
		} else {
			result.setText(value.toString());
		}
		if (this instanceof Editable<T> editable) {
			result.getDocument().addDocumentListener(new DocumentListener() {

				@Override
				public void changedUpdate(DocumentEvent evt) {
					Optional<T> value = editable.convert(result.getText());
					if (value.isPresent()) {
						editable.put(parent, key, value.get());
						
						result.setForeground(Color.BLACK);
					} else {
						result.setForeground(Color.RED);
					}
				}

				@Override
				public void insertUpdate(DocumentEvent evt) {
					changedUpdate(evt);
				}

				@Override
				public void removeUpdate(DocumentEvent evt) {
					changedUpdate(evt);
				}
				
			});
			
		} else {
			result.setEditable(false);
			result.setFont(result.getFont().deriveFont(Font.BOLD));
		}
		return result;
	}
	
	default JComponent createEditor(ArrayElement parent, int index) {
		JTextField result = new JTextField();
		result.setBackground(Color.WHITE);
		T value = get(parent, index);
		if (value == null) {
			result.setText("null");
		} else {
			result.setText(value.toString());
		}
		if (this instanceof Editable<T> editable) {
			result.getDocument().addDocumentListener(new DocumentListener() {

				@Override
				public void changedUpdate(DocumentEvent evt) {
					Optional<T> value = editable.convert(result.getText());
					if (value.isPresent()) {
						editable.put(parent, index, value.get());
						result.setSelectedTextColor(null);
						result.setForeground(Color.BLACK);
					} else {
						result.setForeground(Color.RED);
					}
				}

				@Override
				public void insertUpdate(DocumentEvent evt) {
					changedUpdate(evt);
				}

				@Override
				public void removeUpdate(DocumentEvent evt) {
					changedUpdate(evt);
				}
				
			});
		} else {
			result.setEditable(false);
			result.setFont(result.getFont().deriveFont(Font.BOLD));
		}
		return result;
	}
	
	public static final class NonEditable implements SchemaType<String> {
		
		public String get(ObjectElement parent, String key) {
			return express(parent.get(key));
		}
		
		public String get(ArrayElement parent, int index) {
			return express(parent.get(index));
		}
		
		private String express(ValueElement val) {
			return switch(val) {
				case NullElementImpl n -> "null";
				case PrimitiveElement prim -> prim.asString().orElse("");
				case ObjectElement obj -> "{ }";
				case ArrayElement arr -> "[ ]";
				default -> val.toString();
			};
		}

		@Override
		public String deserialize(ValueElement v) {
			return switch(v) {
				case PrimitiveElement prim -> prim.asString().orElse("");
				default -> "";
			};
		}
	}
	
	public static non-sealed interface Editable<T> extends SchemaType<T> {
		default void put(ObjectElement parent, String key, T value) {
			parent.put(key, serialize(value));
		}
		
		default void put(ArrayElement parent, int index, T value) {
			parent.set(index, serialize(value));
		}
		
		default ValueElement serialize(T value) {
			return PrimitiveElement.box(value);
		}
		
		Optional<T> convert(String value);
	}
	
	public static class EditableDouble implements Editable<Double> {

		@Override
		public Double deserialize(ValueElement v) {
			return switch(v) {
				case PrimitiveElement prim -> prim.asDouble().orElse(0.0);
				default -> 0.0;
			};
		}
		
		@Override
		public ValueElement serialize(Double value) {
			return PrimitiveElement.of(value.doubleValue());
		};

		@Override
		public Optional<Double> convert(String value) {
			try {
				return Optional.of(Double.parseDouble(value));
			} catch (NumberFormatException ex) {
				return Optional.empty();
			}
		}
	}
	
	public static class EditableInt implements Editable<Integer> {
		@Override
		public Integer deserialize(ValueElement v) {
			return switch(v) {
				case PrimitiveElement prim -> prim.asInt().orElse(0);
				default -> 0;
			};
		}
		
		public ValueElement serialize(Integer value) {
			return PrimitiveElement.of(value.intValue());
		};

		@Override
		public Optional<Integer> convert(String value) {
			try {
				return Optional.of((int) Double.parseDouble(value));
			} catch (NumberFormatException ex) {
				return Optional.empty();
			}
		}
	}
	
	public static class EditableString implements Editable<String> {

		@Override
		public String deserialize(ValueElement v) {
			return switch(v) {
				case PrimitiveElement prim -> prim.asString().orElse("");
				default -> "";
			};
		}
		
		@Override
		public ValueElement serialize(String value) {
			return PrimitiveElement.of(value);
		};

		@Override
		public Optional<String> convert(String value) {
			return Optional.of(value);
		}
		
	}
	
}
