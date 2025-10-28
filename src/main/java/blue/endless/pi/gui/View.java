package blue.endless.pi.gui;

import javax.swing.JComponent;
import javax.swing.JMenuBar;

public interface View {
	
	JComponent mainPanel();
	JComponent rightPanel();
	JMenuBar menuBar();
	JComponent statusLine();
}