package blue.endless.pi.gui.view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayDeque;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSplitPane;

public class ViewerFrame extends JFrame implements ViewContext {
	JSplitPane splitPane;
	JButton backButton = new JButton("Back");
	JPanel statusBar;
	View currentView;
	boolean unsaved;
	
	public ArrayDeque<View> stack = new ArrayDeque<>();
	
	public ViewerFrame(String title) {
		this();
		this.setTitle(title);
	}
	
	public ViewerFrame() {
		this.setMinimumSize(new Dimension(640, 480));
		
		splitPane = new JSplitPane();
		splitPane.setResizeWeight(1.0);
		this.getContentPane().setLayout(new BorderLayout());
		this.add(splitPane, BorderLayout.CENTER);
		
		statusBar = new JPanel();
		statusBar.setLayout(new BorderLayout());
		Dimension buttonSize = new Dimension(200, -1);
		backButton.setMinimumSize(buttonSize);
		backButton.setPreferredSize(buttonSize);
		backButton.setMaximumSize(buttonSize);
		backButton.setAction(new AbstractAction("Back") {
			@Override
			public void actionPerformed(ActionEvent e) {
				goBack();
				repaint();
			}
		});
		
		Dimension barSize = new Dimension(-1, 24);
		statusBar.setMinimumSize(barSize);
		statusBar.setPreferredSize(barSize);
		statusBar.setMaximumSize(barSize);
		statusBar.setBackground(new Color(10, 10, 10));
		this.getContentPane().add(statusBar, BorderLayout.NORTH);
		
		clearView();
		
		this.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		this.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				attemptClose();
				
			}
		});
	}

	@Override
	public void push() {
		
		if (currentView != null) {
			System.out.println("Pushing UI '"+currentView.getClass().getSimpleName()+"'.");
			stack.push(currentView);
		} else {
			System.out.println("Nothing to push");
		}
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
		
		currentView = ui;
		
		splitPane.setLeftComponent(ui.mainPanel());
		splitPane.setRightComponent(ui.rightPanel());
		
		setJMenuBar(ui.menuBar());
		
		statusBar.removeAll();
		if (!stack.isEmpty()) {
			statusBar.add(backButton, BorderLayout.WEST);
		}
		
		JComponent statusLine = ui.statusLine();
		if (statusLine != null) {
			statusBar.add(statusLine, BorderLayout.CENTER);
		} else {
			statusBar.add(new JLabel(), BorderLayout.CENTER);
		}
		this.validate();
		this.repaint();
	}
	
	public void clearView() {
		currentView = null;
		
		JPanel mainPanel = new JPanel();
		mainPanel.setBackground(new Color(80, 80, 80));
		splitPane.setLeftComponent(mainPanel);
		
		JPanel rightPanel = new JPanel();
		splitPane.setRightComponent(rightPanel);
		statusBar.removeAll();
		
		setJMenuBar(null);
		this.repaint();
	}
	
	@Override
	public void setRightPanel(JComponent component) {
		splitPane.setRightComponent(component);
		this.repaint();
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
	public void markUnsaved() {
		this.unsaved = true;
	}
	
	@Override
	public void clearUnsaved() {
		this.unsaved = false;
	}
	
	@Override
	public boolean isUnsaved() {
		return this.unsaved;
	}
	
}
