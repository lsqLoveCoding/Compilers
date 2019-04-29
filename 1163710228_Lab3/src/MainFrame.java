
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class MainFrame extends JFrame {
	JButton button;

	// ����ui��ʽ
	static {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e) {
		}
	}

	MainFrame() {
		super("���������");
		CodeView codeView = new CodeView(); // ������ʾ����
		DFAView dfaView = new DFAView(); // DFA��ӡ����
		SyntaxView syntaxView = new SyntaxView();
		OutputView outputView = new OutputView(); // �����ʾ����
		button = new JButton("�������");
		button.setPreferredSize(new Dimension(1000, 30));
		button.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				DFA dfa = dfaView.getDFA(); // ���ļ���ȡdfa
				LL ll = syntaxView.getSyntax();
				if (dfa != null && ll != null) {
					// �ʷ�����
					java.util.List<String[]> tokens = dfa.doNFA(codeView.getCode());
					String dfaOutput = "";
					for (String[] s : tokens)
						dfaOutput += s[0] + ":\t" + s[1] + "\t\t" + s[2] + "\n";
					dfaView.setDFAOutput(dfaOutput);
					// �﷨����
					Object[] result = ll.getTree(tokens);
					TreeNode tree = (TreeNode) result[1];
					String error = (String) result[0];
					// System.out.println("�﷨��:\n" + tree.printTree() + "\nError:\n" + error);
					syntaxView.addSyntaxOutput("�﷨��:\n" + tree.printTree() + "\nError:\n" + error);

					// ���岿��
					MediumCode.analysis(tree);
					String output = "���ű�\n" + MediumCode.printSymbolTable() + "\n��Ԫʽ������ַ��\n"
							+ MediumCode.printResult() + "\n" + "Error:\n" + MediumCode.printError();
					outputView.setLabelText(output);
				}
			}
		});
		setLayout(new FlowLayout());
		add(codeView);
		add(dfaView);
		add(syntaxView);
		add(outputView);
		add(button);
		setPreferredSize(new Dimension(1050, 1200));
		pack();
		setResizable(false);
		setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		setVisible(true);
	}
}
