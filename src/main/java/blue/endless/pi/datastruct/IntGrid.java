package blue.endless.pi.datastruct;

import blue.endless.jankson.api.annotation.Deserializer;
import blue.endless.jankson.api.annotation.Serializer;
import blue.endless.jankson.api.document.ArrayElement;
import blue.endless.jankson.api.document.PrimitiveElement;
import blue.endless.jankson.api.document.ValueElement;

/**
 * Represents a 2D 
 */
public class IntGrid {
	private final int width;
	private final int height;
	private final int[] data;
	private final int moat;
	
	public IntGrid(int width, int height) {
		this.width = width;
		this.height = height;
		this.data = new int[width * height];
		this.moat = 0;
	}
	
	public IntGrid(int width, int height, int moat) {
		this.width = width;
		this.height = height;
		this.data = new int[width * height];
		this.moat = moat;
	}
	
	/**
	 * Unpack this IntGrid from a Planets json array-array. Data is top-to-bottom, then left-to-right.
	 * @param arr The array of arrays
	 * @return an IntGrid representing this data plane.
	 */
	@Deserializer
	public static IntGrid deserializeColumnMajor(ArrayElement arr) {
		if (arr.size() == 0) return new IntGrid(0, 0);
		int height = 0;
		int width = arr.size();
		for(ValueElement val : arr) {
			if (val instanceof ArrayElement arr2) height = Math.max(height, arr2.size());
		}
		
		IntGrid result = new IntGrid(width, height);
		int x = 0;
		for(ValueElement val : arr) {
			if (val instanceof ArrayElement arr2) {
				int y = 0;
				for(ValueElement val2 : arr2) {
					if (val2 instanceof PrimitiveElement prim) {
						result.set(x, y, prim.asInt().orElse((int) prim.asDouble().orElse(0)));
					}
					y++;
				}
			}
			x++;
		}
		
		return result;
	}
	
	public static IntGrid unpackArray(int width, int height, long[][] arr) {
		IntGrid result = new IntGrid(width, height);
		if (arr.length == 0) return result;
		
		for(int x=0; x<width; x++) {
			if (arr[x].length == 0) continue;
			for(int y=0; y<height; y++) {
				if (arr[x].length <= y) break;
				
				result.set(x, y, (int) arr[x][y]);
			}
		}
		
		return result;
	}
	
	@Serializer
	public ValueElement serializeColumnMajor() {
		ArrayElement columns = new ArrayElement();
		for(int x=0; x<width; x++) {
			ArrayElement column = new ArrayElement();
			for(int y=0; y<height; y++) {
				column.add(PrimitiveElement.of(get(x, y)));
			}
		}
		
		return columns;
	}
	
	public int get(int x, int y) {
		if (x<0 || x>=width || y<0 || y>=height) return moat;
		return data[y * width + x];
	}
	
	public void set(int x, int y, int value) {
		if (x<0 || x>=width || y<0 || y>=height) return;
		data[y * width + x] = value;
	}
}
