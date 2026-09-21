package blue.endless.pi.gui;

import java.awt.Color;
import java.awt.Container;
import java.util.ArrayDeque;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;

import blue.endless.pi.gui.view.CloseAware;
import blue.endless.pi.gui.view.View;
import blue.endless.pi.gui.view.ViewContext;

public class TestFrame extends JFrame implements ViewContext {
	private View currentView = null;
	private ArrayDeque<View> stack = new ArrayDeque<>();
	private boolean saved = true;
	
	public TestFrame() {
		Container c = this.getContentPane();
	}
	
	@Override
	public void attemptClose() {
		System.out.println("Firing close on "+stack.size()+" views");
		while(!stack.isEmpty()) {
			View view = stack.peek();
			if (view instanceof CloseAware aware) {
				System.out.println("Firing attemptClose on '"+aware.getClass().getSimpleName()+"'.");
				if (!aware.attemptClose()) return;
			}
			stack.pop();
		}
		
		if (currentView instanceof CloseAware aware) {
			if (!aware.attemptClose()) return;
		}
		
		System.exit(0);
	}

	@Override
	public void push() {
		if (currentView != null) stack.push(currentView);
	}

	@Override
	public View pop() {
		if (stack.isEmpty()) return null;
		
		return stack.pop();
	}

	@Override
	public void setView(View ui) {
		if (ui == null) {
			clearView();
			return;
		}
	}
	
	public void clearView() {
		currentView = null;
		
		//JPanel mainPanel = new JPanel();
		//mainPanel.setBackground(new Color(80, 80, 80));
		//splitPane.setLeftComponent(mainPanel);
		
		//JPanel rightPanel = new JPanel();
		//splitPane.setRightComponent(rightPanel);
		//statusBar.removeAll();
		
		//setJMenuBar(null);
		//this.repaint();
	}

	@Override
	public void setMainPanel(JComponent component) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void setRightPanel(JComponent component) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setStatusLine(JComponent component) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean isSaved() {
		return saved;
	}

	@Override
	public void clearSaved() {
		saved = false;
	}

	@Override
	public void markSaved() {
		saved = true;
	}
	
}
