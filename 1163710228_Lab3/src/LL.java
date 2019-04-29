import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.*;

public class LL {
	String[] terminator = new String[] { "id", "intd", "reald", "proc", "int", "real", "if", "then", "else", "call",
			"record", "while", "do", "=", "(", ")", "[", "]", "+", "-", "*", "<", "<=", "==", "!=", ">=", ">", "and",
			"or", "not", "true", "false", ",", ";" };
	Map<String, Set<String>> original, first, follow;
	Map<String, Map<String, String[]>> table;

	LL(File file) {
		original = new HashMap<String, Set<String>>();
		first = new HashMap<String, Set<String>>();
		follow = new HashMap<String, Set<String>>();
		table = new HashMap<String, Map<String, String[]>>();
		try {
			BufferedReader reader = new BufferedReader(new FileReader(file));
			String line;
			while ((line = reader.readLine()) != null) {
				if (!line.contains("->"))
					continue;
				String key = line.substring(0, line.indexOf("->")).trim();
				String[] value = line.substring(line.indexOf("->") + 2).split("\\u007C");
				Set<String> valueList = new LinkedHashSet<String>();
				for (String v : value)
					valueList.add(v.trim());
				original.put(key, valueList);
			}
			getFirst();
			getFollow();
			getTable();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void getFirst() {
		first.put("��", new HashSet<String>());
		first.get("��").add("��");
		// �����ս��first��Ϊ����
		for (String x : terminator) {
			Set<String> value = new HashSet<String>();
			value.add(x);
			first.put(x, value);
		}
		// X->����first������
		for (String x : original.keySet()) {
			Set<String> value = new HashSet<String>();
			if (hasEmpty(original.get(x))) {
				value.add("��");
			}
			first.put(x, value);
		}
		boolean change;
		do {
			change = false;
			for (String x : original.keySet()) {
				// ���ս��
				if (!isTerminator(x)) {
					for (String str : original.get(x)) {
						String[] temp = str.split(" ");
						// X->Y1Y2...�ҵ��׸������ŵ�Yi
						int i = 0;
						while (i < temp.length && (temp[i].equals("��") || hasEmpty(original.get(temp[i]))))
							i++;
						// ���ǰ�Ĵ�С
						int count = first.get(x).size();
						// X��first��
						Set<String> value = first.get(x);
						if (i < temp.length) // ����Yi
						{
							// X��first�����Yi��Yiǰ��ÿ��Yj��first��
							for (int j = 0; j <= i; j++) {
								for (String t : first.get(temp[j]))
									if (!t.equals("��"))
										value.add(t);
							}
						} else // ������Yi����ӿ�
						{
							value.add("��");
						}
						if (value.size() != count)
							change = true;
					}
				}
			}
		} while (change);
	}

	private void getFollow() {
		// �����ս��follow��Ϊ��
		for (String x : terminator)
			follow.put(x, new HashSet<String>());
		for (String x : original.keySet())
			follow.put(x, new HashSet<String>());
		follow.get("P").add("#");
		boolean change;
		do {
			change = false;
			for (String x : original.keySet()) {
				// ���ս��
				if (!isTerminator(x)) {
					for (String str : original.get(x)) {
						// ÿһ���ķ�
						String[] temp = str.split(" ");
						// first������follow��
						for (int i = 0; i < temp.length - 1; i++) {
							if (!temp[i].equals("��") && !isTerminator(temp[i])) {
								Set<String> tempSet = follow.get(temp[i]);
								int c = tempSet.size();
								tempSet.addAll(first.get(temp[i + 1]));
								tempSet.remove("��");
								if (tempSet.size() != c)
									change = true;
							}
						}
						// follow������follow��
						for (int i = temp.length - 1; i >= 0; i--) {
							if (i + 1 >= temp.length || hasEmpty(first.get(temp[i + 1]))) {
								if (temp[i].equals("��"))
									continue;
								if (isTerminator(temp[i]))
									break;
								Set<String> tempSet = follow.get(temp[i]);
								int c = tempSet.size();
								tempSet.addAll(follow.get(x));
								if (tempSet.size() != c)
									change = true;
							} else
								break;
						}
					}
				}
			}
		} while (change);
	}

	private void getTable() {
		for (String x : original.keySet()) {
			// ���ս��
			if (!isTerminator(x)) {
				table.put(x, new HashMap<String, String[]>());
				Map<String, String[]> tempMap = table.get(x);
				// ����������ͬ����
				for (String t : follow.get(x))
					tempMap.put(t, new String[0]);
				// ����ÿ���﷨����
				for (String str : original.get(x)) {
					// ÿһ���ķ�
					String[] temp = str.split(" ");
					if (hasEmpty(first.get(temp[0]))) {
						for (String t : follow.get(x))
							tempMap.put(t, temp);
					}
					for (String t : first.get(temp[0]))
						tempMap.put(t, temp);
					tempMap.remove("��");
				}
			}
		}
	}

	public boolean hasEmpty(Collection<String> collection) {
		if (collection == null)
			return false;
		for (String s : collection) {
			if (s.equals("��"))
				return true;
		}
		return false;
	}

	public boolean isTerminator(String s) {
		switch (s) {
		case "id":
		case "intd":
		case "reald":
		case "proc":
		case "int":
		case "real":
		case "if":
		case "then":
		case "else":
		case "call":
		case "record":
		case "while":
		case "do":
		case "=":
		case "(":
		case ")":
		case "[":
		case "]":
		case "+":
		case "-":
		case "*":
		case "<":
		case "<=":
		case "==":
		case "!=":
		case ">=":
		case ">":
		case "and":
		case "or":
		case "not":
		case "true":
		case "false":
		case ",":
		case ";":
			return true;
		}
		return false;
	}

	public Object[] getTree(List<String[]> data) {
		data.add(new String[] { "", "#", "" });
		String error = "";
		TreeNode tree = new TreeNode(null, new String[] { "", "P", "" });
		Stack<TreeNode> stack = new Stack<TreeNode>();
		stack.push(tree);
		int index = 0;
		while (!stack.empty() && index < data.size()) {
			// �������ȡ�ַ�
			String ch = data.get(index)[1];
			if (ch.equals("�ʷ�����") || ch.equals("�Ƿ��ַ�")) {
				error += "Error at Line " + data.get(index)[0] + "��" + data.get(index)[1] + "��" + data.get(index)[2]
						+ "\n";
				index++;
				continue;
			}
			TreeNode top = stack.peek();
			// ջ���ַ���������ͬ����Լ
			if (top.getChar().equals(ch)) {
				top.info = data.get(index);
				stack.pop();
				index++;
				continue;
			}
			// ���ֶ����ַ�
			if (!table.containsKey(top.getChar())) {
				if (ch.equals("intd") || ch.equals("reald") || ch.equals("id") || ch.equals("STRING"))
					ch = data.get(index)[2];
				error += "Error at Line " + data.get(index)[0] + "���﷨��������" + ch + "\n";
				index++;
				continue;
			}
			String[] rule = table.get(top.getChar()).get(ch);
			if (rule != null) // �޴���
			{
				if (rule.length != 0) // ����ͬ����
				{
					stack.pop();
					for (int i = rule.length - 1; i >= 0; i--) {
						TreeNode node = new TreeNode(top, new String[] { "", rule[i], "" });
						top.addChild(node);
						if (!node.getChar().equals("��"))
							stack.push(node);
					}
				} else // ��ͬ���㣬ɾȥջ��Ԫ��
				{
					error += "Error at Line " + data.get(index)[0] + "���﷨����M[" + top.getChar() + "," + ch
							+ "]=synch\n";
					stack.pop();
				}
			} else // �д���
			{
				if (ch.equals("intd") || ch.equals("reald") || ch.equals("id") || ch.equals("STRING"))
					ch = data.get(index)[2];
				error += "Error at Line " + data.get(index)[0] + "���﷨��������" + ch + "\n";
				index++;
			}
		}
		return new Object[] { error, tree };
	}

	public String printFirst() {
		StringBuilder sb = new StringBuilder();
		for (String str : first.keySet()) {
			sb.append(str).append(":\t").append(first.get(str).toString()).append("\n");
		}
		return sb.append("\n").toString();
	}

	public String printfollow() {
		StringBuilder sb = new StringBuilder();
		for (String str : follow.keySet()) {
			sb.append(str).append(":\t").append(follow.get(str).toString()).append("\n");
		}
		return sb.append("\n").toString();
	}

	public String printTable() {
		StringBuilder sb = new StringBuilder();
		for (String str : terminator)
			sb.append("\t\t\t").append(str);
		sb.append("\n");
		for (String str : table.keySet()) {
			sb.append(str).append("\t\t\t");
			for (String str2 : terminator) {
				if (table.get(str).get(str2) == null)
					sb.append("error").append("\t\t\t");
				else if (table.get(str).get(str2).length == 0)
					sb.append("synch").append("\t\t\t");
				else {
					String re = "";
					for (String it : table.get(str).get(str2))
						re += it + " ";
					re = str + "->" + re.trim();
					sb.append(re).append("\t");
					if (re.length() < 16)
						sb.append("\t");
					if (re.length() < 8)
						sb.append("\t");
				}
			}
			sb.append("\n");
		}
		return sb.append("\n").toString();
	}
}
