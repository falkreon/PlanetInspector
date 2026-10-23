package blue.endless.pi.enigma.domain;

import blue.endless.jankson.api.annotation.Deserializer;
import blue.endless.jankson.api.annotation.Serializer;
import blue.endless.jankson.api.document.ArrayElement;
import blue.endless.jankson.api.document.ValueElement;
import blue.endless.pi.enigma.util.Direction;

public class DirectionMap<T> {
	private T c;
	private T e;
	private T n;
	private T w;
	private T s;
	
	public T get(Direction d) {
		return switch(d) {
			case INVALID -> c;
			case EAST -> e;
			case NORTH -> n;
			case WEST -> w;
			case SOUTH -> s;
		};
	}
	
	public void put(Direction d, T t) {
		switch(d) {
			case INVALID -> c = t;
			case EAST -> e = t;
			case NORTH -> n = t;
			case WEST -> w = t;
			case SOUTH -> s = t;
		}
	}
	
	public T center() { return c; }
	public T east() { return e; }
	public T north() { return n; }
	public T west() { return w; }
	public T south() { return s; }
	
	public void setCenter(T value) { c = value; }
	public void setEast(T value) { e = value; }
	public void setNorth(T value) { n = value; }
	public void setWest(T value) { w = value; }
	public void setSouth(T value) { s = value; }
	
	@Serializer
	public ArrayElement toJson() {
		ArrayElement result = new ArrayElement();
		// TODO: EnigmaSerializer
		return result;
	}
	
}
