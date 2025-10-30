package blue.endless.pi;


import java.awt.Color;
import java.awt.Font;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;

import javax.swing.JColorChooser;
import javax.swing.JComponent;
import javax.swing.JTextField;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import blue.endless.jankson.api.document.ArrayElement;
import blue.endless.jankson.api.document.ObjectElement;
import blue.endless.jankson.api.document.PrimitiveElement;
import blue.endless.jankson.api.document.ValueElement;
import blue.endless.jankson.impl.document.DoubleElementImpl;
import blue.endless.jankson.impl.document.NullElementImpl;

public sealed interface SchemaType<T> permits SchemaType.Editable, SchemaType.NonEditable {
	// Editable
	public static final SchemaType<String> IMMUTABLE = new NonEditableGeneral();
	public static final SchemaType<Long> IMMUTABLE_INT = new NonEditableInt();
	
	public static final SchemaType<String> STRING = new EditableString();
	public static final SchemaType<Double> DOUBLE = new EditableDouble();
	public static final SchemaType<Integer> INT = new EditableInt();
	public static final SchemaType<List<String>> STRING_LIST = new EditableStringList();
	//public static final SchemaType<Boolean> BOOLEAN = new SchemaType<>();
	public static final SchemaType<Color> COLOR = new EditableColor();
	
	// Not editable, limited display
	//public static final SchemaType<ArrayElement> ARRAY = new SchemaType<>();
	//public static final SchemaType<ObjectElement> OBJECT = new SchemaType<>();
	
	default T get(ObjectElement parent, String key) {
		ValueElement v = parent.get(key);
		if (v == null) v = PrimitiveElement.ofNull();
		return deserialize(v);
	}
	
	default T get(ArrayElement parent, int index) {
		return deserialize(parent.get(index));
	}
	
	T deserialize(ValueElement v);
	
	default String express(T value) {
		return value.toString();
	}
	
	default JComponent createEditor(ObjectElement parent, String key) {
		JTextField result = new JTextField();
		T value = get(parent, key);
		
		if (value == null) {
			result.setText("null");
		} else {
			result.setText(express(value));
		}
		if (this instanceof Editable<T> editable) {
			result.getDocument().addDocumentListener(new DocumentListener() {

				@Override
				public void changedUpdate(DocumentEvent evt) {
					Optional<T> value = editable.convert(result.getText());
					if (value.isPresent()) {
						editable.put(parent, key, value.get());
						
						result.setForeground(null);
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
			result.setEnabled(false);
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
	
	public static non-sealed interface NonEditable<T> extends SchemaType<T> {
		
	}
	
	public static class NonEditableGeneral implements NonEditable<String> {
		
		@Override
		public String get(ObjectElement parent, String key) {
			return express(parent.get(key));
		}
		
		@Override
		public String get(ArrayElement parent, int index) {
			return express(parent.get(index));
		}
		
		public String express(ValueElement val) {
			return switch(val) {
				case NullElementImpl n -> "null";
				case PrimitiveElement prim -> prim.asString().orElse("");
				case ObjectElement obj -> "{ }";
				case ArrayElement arr -> "[ ]";
				case null -> "null";
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
	
	public static class NonEditableInt implements NonEditable<Long> {
		@Override
		public String express(Long value) {
			return Long.toString(value);
		}

		@Override
		public Long deserialize(ValueElement v) {
			return switch(v) {
				case DoubleElementImpl d -> (long) d.asDouble().getAsDouble();
				case PrimitiveElement prim -> prim.asLong().orElse(0L);
				default -> 0L;
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
	
	public static class EditableStringList implements Editable<List<String>> {
		@Override
		public List<String> deserialize(ValueElement value) {
			return switch(value) {
				case PrimitiveElement prim ->  prim.asString().map(List::of).orElseGet(List::of);
				
				case ArrayElement arr -> {
					ArrayList<String> result = new ArrayList<>();
					for(ValueElement val : arr) {
						if (val instanceof PrimitiveElement prim) {
							Optional<String> str = prim.asString();
							if (str.isPresent()) result.add(str.get());
						}
					}
					yield result;
				}
				
				case null, default -> List.of();
			};
		}
		
		@Override
		public Optional<List<String>> convert(String value) {
			String[] parts = value.split(Pattern.quote(","));
			ArrayList<String> result = new ArrayList<>();
			for(String s : parts) {
				String clean = s.trim();
				if (!clean.isBlank()) result.add(clean);
			}
			return (result.size() > 0) ? Optional.of(result) : Optional.empty();
		}
		
		@Override
		public ValueElement serialize(List<String> value) {
			ArrayElement result = new ArrayElement();
			for(String s : value) {
				result.add(PrimitiveElement.of(s));
			}
			return result;
		}
		
		public String express(List<String> value) {
			StringBuilder result = new StringBuilder();
			for(int i=0; i<value.size(); i++) {
				if (i != 0) result.append(", ");
				result.append(value.get(i));
			}
			
			return result.toString();
		}
	}
	
	public static class EditableColor implements Editable<Color> {
		@Override
		public JComponent createEditor(ObjectElement parent, String key) {
			JColorChooser chooser = new JColorChooser();
			
			
			chooser.getSelectionModel().addChangeListener(new ChangeListener() {
				@Override
				public void stateChanged(ChangeEvent e) {
					Color c = chooser.getSelectionModel().getSelectedColor();
					//System.out.println("StateChanged to "+c);
					put(parent, key, c);
				}
			});
			
			Color c = get(parent, key);
			//System.out.println("Initializing to "+c);
			Color check = chooser.getSelectionModel().getSelectedColor();
			//System.out.println("Checked as "+check);
			
			
			return chooser;
		}
		
		@Override
		public JComponent createEditor(ArrayElement parent, int index) {
			JColorChooser chooser = new JColorChooser();
			
			chooser.getSelectionModel().setSelectedColor(get(parent, index));
			chooser.getSelectionModel().addChangeListener(new ChangeListener() {
				@Override
				public void stateChanged(ChangeEvent e) {
					//System.out.println("StateChanged to "+chooser.getColor());
					put(parent, index, chooser.getColor());
				}
			});
			
			return chooser;
		}
		
		@Override
		public ValueElement serialize(Color value) {
			int bgr =
					value.getRed() |
					(value.getGreen() << 8) |
					(value.getBlue() << 16);
			return PrimitiveElement.of(bgr);
		}

		@Override
		public Color deserialize(ValueElement val) {
			return switch(val) {
				case PrimitiveElement prim -> {
					int bgr = prim.asInt().orElse(0);
					System.out.println("BGR: "+bgr);
					int r = bgr & 0xFF;
					int g = (bgr >> 8) & 0xFF;
					int b = (bgr >> 16) & 0xFF;
					System.out.println("Converted to "+r+","+g+","+b);
					yield new Color(r, g, b);
				}
				default -> Color.BLUE;
			};
		}

		@Override
		public Optional<Color> convert(String value) {
			return Optional.empty();
		}

	}
}
