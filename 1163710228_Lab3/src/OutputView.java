import javax.swing.*;
import java.awt.*;

public class OutputView extends JPanel {
	JTextArea label;

	OutputView() {
		label = new JTextArea();
		JScrollPane pane = new JScrollPane(label);
		setLayout(new BorderLayout());
		add(pane, "Center");
		setPreferredSize(new Dimension(800, 400));
		updateUI();
	}

	public void setLabelText(String text) {
		label.setText(text);
	}
}
