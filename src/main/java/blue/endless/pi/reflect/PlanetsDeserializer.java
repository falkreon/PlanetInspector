package blue.endless.pi.reflect;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.OptionalInt;

import blue.endless.jankson.api.annotation.SerializedName;
import blue.endless.jankson.api.document.BooleanElement;
import blue.endless.jankson.api.document.KeyValuePairElement;
import blue.endless.jankson.api.document.LongElement;
import blue.endless.jankson.api.document.ObjectElement;
import blue.endless.jankson.api.document.ValueElement;

public class PlanetsDeserializer {
	public static <T> T deserializePlanetsDomainObject(ValueElement v, Class<T> clazz) throws IOException {
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
					try {
						switch(field.getType().getSimpleName()) {
							case "Boolean" -> {
								switch(kvp.getValue()) {
									case BooleanElement b -> field.set(result, b.asBoolean().get());
									case LongElement l -> field.set(result, l.asBoolean().get());
									default -> {}
								}
							}
							
							case "Integer" -> {
								if (kvp.getValue() instanceof LongElement l) {
									OptionalInt maybe = l.asInt();
									if (maybe.isPresent()) field.set(result, maybe.getAsInt());
								}
							}
							
							
							default -> throw new RuntimeException("Cannot determine marshalling for this type: "+field.getType().getSimpleName());
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
}
