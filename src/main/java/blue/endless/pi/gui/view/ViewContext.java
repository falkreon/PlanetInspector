package blue.endless.pi.gui.view;

import javax.swing.JComponent;

public interface ViewContext {
	public void attemptClose();
	
	/**
	 * Takes the currently displayed UI and menubar and pushes it to the context stack, *without* changing the currently
	 * displayed UI.
	 */
	public void push();
	
	/**
	 * removes and returns the top item of the context stack (not the currently displayed UI!)
	 * @return
	 */
	public View pop();
	
	/**
	 * Sets the currently displayed UI, without preserving the currently displayed UI.
	 * @param ui The UI to display
	 */
	public void setView(View ui);
	
	/**
	 * Pushes the current UI to the context stack, and 
	 * @param ui
	 */
	public default void go(View ui) {
		push();
		setView(ui);
	}
	
	/**
	 * Discards the current UI, pops the context stack and sets the removed item as the currently-displayed UI.
	 */
	public default void goBack() {
		setView(pop());
	}

	public void setRightPanel(JComponent component);

}
