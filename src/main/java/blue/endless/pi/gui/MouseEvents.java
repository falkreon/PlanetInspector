package blue.endless.pi.gui;

import java.util.function.Consumer;

import blue.endless.pi.datastruct.Vec2;
import blue.endless.tinyevents.Event;
import blue.endless.tinyevents.EventFactories;

public record MouseEvents(
		Event<Consumer<Vec2>> leftClick,
		Event<Consumer<Vec2>> rightClick,
		Event<Consumer<Vec2>> doubleClick,
		Event<Consumer<Vec2>> move,
		Event<Consumer<Vec2>> drag
		) {
	
	public MouseEvents() {
		this(
			EventFactories.consumer(),
			EventFactories.consumer(),
			EventFactories.consumer(),
			EventFactories.consumer(),
			EventFactories.consumer()
			);
	}
}
