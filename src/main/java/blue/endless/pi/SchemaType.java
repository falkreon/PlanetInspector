package blue.endless.pi;

import blue.endless.jankson.api.document.ArrayElement;
import blue.endless.jankson.api.document.ObjectElement;

public enum SchemaType {
	// Editable
	STRING(String.class),
	FLOAT(Double.class),
	INT(Long.class),
	BOOLEAN(Boolean.class),
	
	// Not editable, limited display
	ARRAY(ArrayElement.class),
	OBJECT(ObjectElement.class)
	;
	
	private final Class<?> valueClass;
	
	SchemaType(Class<?> clazz) {
		this.valueClass = clazz;
	}
	
	public Class<?> valueClass() {
		return valueClass;
	}
	
	
}
