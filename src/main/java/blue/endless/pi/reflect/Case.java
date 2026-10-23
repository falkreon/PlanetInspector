package blue.endless.pi.reflect;

import java.util.Locale;
import java.util.PrimitiveIterator;

public class Case {

	public static String snakeToCamel(String snaked) {
		StringBuilder sb = new StringBuilder();
		PrimitiveIterator.OfInt iter = snaked.codePoints().iterator();
		boolean camel = false;
		while(iter.hasNext()) {
			int codePoint = iter.nextInt();
			if (codePoint == '_') {
				camel = true;
			} else {
				if (camel) {
					sb.appendCodePoint(Character.toUpperCase(codePoint));
					camel = false;
				} else {
					sb.appendCodePoint(codePoint);
				}
			}
		}
		
		return sb.toString();
	}

	public static String camelToSnake(String camel) {
		StringBuilder sb = new StringBuilder();
		PrimitiveIterator.OfInt iter = camel.codePoints().iterator();
		while(iter.hasNext()) {
			int codePoint = iter.nextInt();
			if (Character.isUpperCase(codePoint)) {
				sb.append('_');
				sb.append(Character.toLowerCase(codePoint));
			} else {
				sb.append(codePoint);
			}
		}
		
		return sb.toString();
	}
	
	public static String upperSnakeToSnake(String upperSnake) {
		return upperSnake.toLowerCase(Locale.ENGLISH);
	}
	
	public static String snakeToUpperSnake(String snake) {
		return snake.toUpperCase(Locale.ENGLISH);
	}
}
