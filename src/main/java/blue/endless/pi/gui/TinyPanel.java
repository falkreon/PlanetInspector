package blue.endless.pi.gui;

import java.util.function.Consumer;
import java.awt.Graphics2D;

import blue.endless.pi.datastruct.Vec2;
import blue.endless.tinyevents.Event;
import blue.endless.tinyevents.EventFactories;

public abstract class TinyPanel {
	private int width;
	private int height;
	private MouseEvents mouseEvents = new MouseEvents();
	private KeyEvents keyEvents = new KeyEvents();
	private Event<Consumer<Graphics2D>> onPaint = EventFactories.consumer();
	
	public MouseEvents mouseEvents() { return mouseEvents; }
	public KeyEvents keyEvents() { return keyEvents; }
	public Event<Consumer<Graphics2D>> onPaint() { return onPaint; }
	
	public Vec2 getSize() { return new Vec2(width, height); }
	public void setSize(Vec2 vec) {
		this.width = vec.x();
		this.height = vec.y();
	}
	public void setSize(int width, int height) {
		this.width = width;
		this.height = height;
	}
}
