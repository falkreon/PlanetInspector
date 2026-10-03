package blue.endless.pi.reflect;

import java.io.IOException;
import java.lang.reflect.AccessFlag;
import java.lang.reflect.Field;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.lang.reflect.WildcardType;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.OptionalDouble;
import java.util.OptionalInt;
import java.util.OptionalLong;
import java.util.Set;

import blue.endless.jankson.api.annotation.Deserializer;
import blue.endless.jankson.api.annotation.SerializedName;
import blue.endless.jankson.api.document.ArrayElement;
import blue.endless.jankson.api.document.DoubleElement;
import blue.endless.jankson.api.document.KeyValuePairElement;
import blue.endless.jankson.api.document.ObjectElement;
import blue.endless.jankson.api.document.PrimitiveElement;
import blue.endless.jankson.api.document.ValueElement;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;

public class PlanetsDeserializer {
	public static <T> T decode(ValueElement v, Class<T> clazz) throws IOException {
		if (v instanceof ObjectElement o) {
			T result = null;
			try {
				result = clazz.getConstructor(new Class<?>[] {}).newInstance();
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
			
			// Build a SerializedName -> Field table
			HashMap<String, Field> fields = new HashMap<>();
			for(Field f: clazz.getDeclaredFields()) {
				SerializedName sn = f.getAnnotation(SerializedName.class);
				if (sn != null) {
					fields.put(sn.value(), f);
				} else {
					fields.put(f.getName(), f);
				}
			}
			
			// Map and marshall
			for(KeyValuePairElement kvp: o) {
				Field field = fields.get(kvp.getKey());
				if (field == null) {
					System.out.println(kvp.getKey() + ": No Mapping");
				} else {
					System.out.println("Inspecting " + kvp.getKey() + ": " + kvp.getValue().getClass().getSimpleName()+" -> "+field.getType().getSimpleName());
					try {
						Object marshalledValue = marshallUnknown(kvp.getValue(), field.getGenericType());
						
						if (marshalledValue == null) {
							System.out.println("  Field SKIPPED");
						} else {
							field.set(result, marshalledValue);
							System.out.println("  Set SUCCESS");
						}
					} catch (Exception setEx) {
						throw new RuntimeException(setEx);
					}
				}
			}
		
			return result;
		} else {
			throw new IOException("Source data is not an Object");
		}
	}
	
	public static Class<?> toClass(Type t) {
		return switch(t) {
			case Class<?> c -> c;
			
			case ParameterizedType p -> {
				Type rawType = p.getRawType();
				if (rawType instanceof Class c) {
					yield c;
				} else {
					yield null;
				}
			}
			
			case GenericArrayType gat -> null; // There SHOULD be a unique array class for each of these but it's somewhat unavailable.
			case WildcardType wct -> null; // There is no concrete class for this
			case TypeVariable<?> tv -> null; // There may be a concrete class for this but we'd have to walk the class hierarchy tree to find out
			
			default -> null;
		};
	}
	
	private static Object marshallUnknown(ValueElement input, Type destType) {
		Class<?> baseClass = toClass(destType);
		if (baseClass == null) return null;
		if (baseClass.isAssignableFrom(input.getClass())) return input;
		
		return switch(baseClass.getSimpleName()) {
			case "Boolean", "boolean" -> marshallBoolean(input);
			case "Integer", "int" -> marshallInt(input);
			case "Long", "long" -> marshallLong(input);
			case "Double", "double" -> marshallDouble(input);
			case "String" -> marshallString(input);
			
			case "Set" -> {
				if (destType instanceof ParameterizedType p && p.getActualTypeArguments().length == 1) {
					Type elementType = p.getActualTypeArguments()[0];
					yield marshallSet(input, elementType);
				} else {
					yield null;
				}
			}
			
			case "IntSet" -> {
				if (input instanceof ArrayElement arr) {
					IntOpenHashSet result = new IntOpenHashSet();
					for(ValueElement v : arr) {
						Integer i = marshallInt(v);
						if (i != null) result.add(i.intValue());
					}
					yield result;
				} else {
					yield null;
				}
			}
			
			case "List" -> {
				if (destType instanceof ParameterizedType p && p.getActualTypeArguments().length == 1) {
					Type elementType = p.getActualTypeArguments()[0];
					yield marshallList(input, elementType);
				} else {
					yield null;
				}
			}
			
			// Look for a Deserializer factory method
			default -> marshallObject(baseClass, input);
		};
	}
	
	private static Boolean marshallBoolean(ValueElement input) {
		if (input instanceof PrimitiveElement prim) {
			if (prim instanceof DoubleElement d) {
				return d.asDouble().orElse(0) != 0;
			}
			return prim.asBoolean().orElse(null);
		} else {
			return null;
		}
	}
	
	private static Integer marshallInt(ValueElement input) {
		if (input instanceof PrimitiveElement prim) {
			OptionalInt result = prim.asInt();
			if (result.isPresent()) return result.getAsInt();
		}
		return null;
	}
	
	private static Long marshallLong(ValueElement input) {
		if (input instanceof PrimitiveElement prim) {
			OptionalLong result = prim.asLong();
			if (result.isPresent()) return result.getAsLong();
		}
		return null;
	}
	
	private static Double marshallDouble(ValueElement input) {
		if (input instanceof PrimitiveElement prim) {
			OptionalDouble result = prim.asDouble();
			if (result.isPresent()) return result.getAsDouble();
		}
		return null;
	}
	
	private static String marshallString(ValueElement input) {
		if (input instanceof PrimitiveElement prim) {
			return prim.asString().orElse(null);
		} else {
			return null;
		}
	}
	
	private static <T> Set<T> marshallSet(ValueElement input, Type elementType) {
		if (input instanceof ArrayElement arr) {
			HashSet<T> result = new HashSet<>();
			
			for(ValueElement elem: arr) {
				try {
					@SuppressWarnings("unchecked")
					T t = (T) marshallUnknown(elem, elementType);
					result.add(t);
				} catch (Throwable t) {
					// Skip this item
					System.out.println("  SKIPPING '"+input.toString()+"'");
				}
			}
			
			return result;
		}
		
		return null;
	}
	
	public static <T> List<T> marshallList(ValueElement input, Type elementType) {
		ArrayList<T> result = new ArrayList<>();
		
		if (input instanceof ArrayElement arr) {
			for(ValueElement elem: arr) {
				try {
					@SuppressWarnings("unchecked")
					T t = (T) marshallUnknown(elem, elementType);
					result.add(t);
				} catch (Throwable t) {
					// Skip this item
					System.out.println("  SKIPPING '"+input.toString()+"'");
				}
			}
		}
		
		return result;
	}
	
	@SuppressWarnings("unchecked")
	private static <T> T marshallObject(Class<T> outputType, ValueElement input) {
		for(Method m : outputType.getDeclaredMethods()) {
			if (!m.isAnnotationPresent(Deserializer.class)) continue;
			if (!m.accessFlags().contains(AccessFlag.STATIC)) continue;
			if (m.getParameterCount() != 1) continue;
			if (!outputType.isAssignableFrom(m.getReturnType())) continue;
			Class<?> argumentType = m.getParameters()[0].getType();
			Class<?> inputType = input.getClass();
			if (!argumentType.isAssignableFrom(inputType)) continue;
			
			System.out.println("  ObjectEXPLICIT " + input.getClass().getSimpleName()+" -> "+outputType.getSimpleName() + ", using "+m.toString());
			
			try {
				return (T) m.invoke(null, new Object[] { input });
			} catch (Throwable t) {
				System.out.println("  ObjectTHROW: "+t.getLocalizedMessage());
				return null;
			}
		}
		
		try {
			System.out.println("  ObjectDEFER...");
			T result = decode(input, outputType);
			if (result != null) {
				System.out.println("  ObjectDEFER COMPLETE");
				return result;
			} else {
				System.out.println("  ObjectDEFER FAIL " + input.getClass().getSimpleName()+" -> "+outputType.getSimpleName());
				return null;
			}
		} catch (Throwable t) {
			System.out.println("  ObjectTHROW: "+t.getLocalizedMessage());
			return null;
		}
	}
}




