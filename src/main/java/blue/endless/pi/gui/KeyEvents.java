package blue.endless.pi.gui;

import java.util.function.IntConsumer;

import blue.endless.tinyevents.Event;
import blue.endless.tinyevents.EventFactories;

public record KeyEvents(
		Event<IntConsumer> keyDown,
		Event<IntConsumer> keyUp,
		Event<IntConsumer> keyPress,
		Event<IntConsumer> character
		) {
	
	public KeyEvents() {
		this(
			EventFactories.intConsumer(),
			EventFactories.intConsumer(),
			EventFactories.intConsumer(),
			EventFactories.intConsumer()
			);
	}
}
