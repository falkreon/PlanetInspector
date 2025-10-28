package blue.endless.pi.gui;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JMenuBar;
import javax.swing.JPanel;

public class AbstractView implements View {
	protected ViewContext context;
	protected JComponent mainPanel = new JPanel();
	protected JComponent rightPanel = new JPanel();
	protected JMenuBar menuBar = null;
	protected JComponent statusLine = new JLabel();
	
	public AbstractView(ViewContext context) {
		this.context = context;
	}
	
	@Override
	public JComponent mainPanel() {
		return mainPanel;
	}

	@Override
	public JComponent rightPanel() {
		return rightPanel;
	}

	@Override
	public JMenuBar menuBar() {
		return menuBar;
	}

	@Override
	public JComponent statusLine() {
		return statusLine;
	}
	
}
