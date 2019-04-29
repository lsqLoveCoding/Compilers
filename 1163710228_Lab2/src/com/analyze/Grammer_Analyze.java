package com.analyze;

import com.analyze.Production;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class Grammer_Analyze {

	private ArrayList<Production> productions; // ����ʽ����
	private ArrayList<String> terminals; // �ս������
	private ArrayList<String> nonterminals; // ���ս������
	private HashMap<String, ArrayList<String>> firsts; // FIRST����
	private HashMap<String, ArrayList<String>> follows; // FOLLOW����

	// ���췽��
	public Grammer_Analyze() {
		productions = new ArrayList<Production>();
		terminals = new ArrayList<String>();
		nonterminals = new ArrayList<String>();
		firsts = new HashMap<String, ArrayList<String>>();
		follows = new HashMap<String, ArrayList<String>>();
		
		// ���ļ��ж�ȡ����ʽ
		readProductions();
		// ��÷��ս����
		setNonTerminals();
		// ����ս����
		setTerminals();
		
		// ��ӡ����ʽ
		// outputProductions();
		// ��ӡ���ս������
		// outputNonterminals();
		// ��ӡ�ս������
		// outputTerminals();
		
		// ���First��
		getFirst();
		// ���Follow��
		getFollow();
		// ���Select��
		getSelect();

		System.out.println("First����������ļ�First.txt��");
		outputFirst();
		System.out.println("Follow����������ļ�Follow.txt��");
		outputFollow();
		// outputSelect();

		createPredict();
	}

	// ��������ʽSelect
	public void outputSelect() {
		for (int i = 0; i < productions.size(); i++) {
			System.out.println(productions.get(i).select);
		}
	}

	// ����Follow��
	public void outputFollow() {
		try {
			String line = System.getProperty("line.separator");
			StringBuffer str = new StringBuffer();
			FileWriter fw = new FileWriter("src/Follow.txt", true);
			Set set = follows.entrySet();
			Iterator iter = set.iterator();
			while (iter.hasNext()) {
				Map.Entry entry = (Map.Entry) iter.next();
				str.append(entry.getKey() + " -> " + entry.getValue()).append(line);
			}
			fw.write(str.toString());
			fw.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		/*
		 * for (Entry<String, ArrayList<String>> entry : follows.entrySet()) {
		 * entry.getKey(); entry.getValue(); System.out.print(entry.getKey());
		 * System.out.print(" -> "); System.out.print(entry.getValue());
		 * System.out.println(); }
		 */
	}

	// ����First��
	public void outputFirst() {
		try {
			String line = System.getProperty("line.separator");
			StringBuffer str = new StringBuffer();
			FileWriter fw = new FileWriter("src/First.txt", true);
			Set set = firsts.entrySet();
			Iterator iter = set.iterator();
			while (iter.hasNext()) {
				Map.Entry entry = (Map.Entry) iter.next();
				str.append(entry.getKey() + " -> " + entry.getValue()).append(line);
			}
			fw.write(str.toString());
			fw.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		/*for (Entry<String, ArrayList<String>> entry : firsts.entrySet()) {
			entry.getKey();
			entry.getValue();
			System.out.print(entry.getKey());
			System.out.print(" -> ");
			System.out.print(entry.getValue());
			System.out.println();
		}*/
	}

	// ��������ʽProduction
	public void outputProductions() {
		for (int i = 0; i < productions.size(); i++) {
			System.out.printf("%s -> ", productions.get(i).returnLeft());
			for (int j = 0; j < productions.get(i).returnRights().length; j++) {
				System.out.printf("%s ", productions.get(i).returnRights()[j]);
			}
			System.out.println();
		}
	}

	// �������ս����
	public void outputNonterminals() {
		for (int i = 0; i < nonterminals.size(); i++) {
			System.out.println(nonterminals.get(i));
		}
	}

	// �����ս����
	public void outputTerminals() {
		for (int i = 0; i < terminals.size(); i++) {
			System.out.println(terminals.get(i));
		}
	}

	// ���ļ��ж�ȡ����ʽ
	public void readProductions() {
		try {
			File file = new File("src/grammar.txt");
			BufferedReader reader = new BufferedReader(new FileReader(file));
			String line;
			String left;
			String right;
			Production production;
			while ((line = reader.readLine()) != null) {
				left = line.split("->")[0].trim();
				right = line.split("->")[1].trim();
				production = new Production(left, right.split(" "));
				productions.add(production);
			}
			reader.close();
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}

	// ��÷��ս����
	public void setNonTerminals() {
		try {
			File file = new File("src/grammar.txt");
			BufferedReader reader = new BufferedReader(new FileReader(file));
			String line;
			String left;
			while ((line = reader.readLine()) != null) {
				left = line.split("->")[0].trim();
				if (nonterminals.contains(left))
					continue;
				else
					nonterminals.add(left);

			}
			reader.close();
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}

	// ����ս����,�����ڻ�ò���ʽ����
	public void setTerminals() {
		// �������еĲ���ʽ
		String[] rights;
		for (int i = 0; i < productions.size(); i++) {
			rights = productions.get(i).returnRights();
			// ���Ҳ�Ѱ���ս��
			for (int j = 0; j < rights.length; j++) {
				if (nonterminals.contains(rights[j]) || rights[j].equals("$"))
					continue;
				else
					terminals.add(rights[j]);
			}
		}
	}

	// ʹ���Ѿ�������ݹ���ķ���ָ�����ϵ��ķ�ʵ�ڲ���Ū��
	// ��ȡFirst��
	public void getFirst() {
		// �ս��ȫ�����first��
		ArrayList<String> first;
		// ��������ս����First��
		for (int i = 0; i < terminals.size(); i++) {
			first = new ArrayList<String>();
			first.add(terminals.get(i));
			firsts.put(terminals.get(i), first);
		}

		// �����з��ս��ע��һ��
		for (int i = 0; i < nonterminals.size(); i++) {
			first = new ArrayList<String>();
			firsts.put(nonterminals.get(i), first);
		}

		boolean flag;
		while (true) {
			flag = true;
			String left;
			String right;
			String[] rights;
			for (int i = 0; i < productions.size(); i++) {
				// ����ÿһ������ʽ
				left = productions.get(i).returnLeft();
				rights = productions.get(i).returnRights();
				for (int j = 0; j < rights.length; j++) {
					// ����ÿ������ʽ�Ҳ���ÿ��Ԫ��
					right = rights[j];
					// right�Ƿ���ڣ���������ô��
					// ���right��Ϊ��
					if (!right.equals("$")) {
						for (int l = 0; l < firsts.get(right).size(); l++) {
							if (firsts.get(left).contains(firsts.get(right).get(l))) {
								continue;
							} else {
								firsts.get(left).add(firsts.get(right).get(l));
								flag = false;
							}
						}
					}
					// ����Ҳ�����Ϊ��
					if (isCanBeNull(right)) {
						continue;
					} else {
						break;
					}
				}
			}
			if (flag == true) {
				break;
			}
		}
		// ���ս����first��
	}

	// �ж��Ƿ����$
	public boolean isCanBeNull(String symbol) {
		String[] rights;
		for (int i = 0; i < productions.size(); i++) {
			// �ҵ�����ʽ
			if (productions.get(i).returnLeft().equals(symbol)) {
				rights = productions.get(i).returnRights();
				if (rights[0].equals("$")) {
					return true;
				}
			}
		}
		return false;
	}

	// ���Follow��
	public void getFollow() {
		// ���з��ս����follow����ʼ��һ��
		ArrayList<String> follow;
		for (int i = 0; i < nonterminals.size(); i++) {
			follow = new ArrayList<String>();
			follows.put(nonterminals.get(i), follow);
		}
		// ��#���뵽follow(S)��
		follows.get("S").add("#");

		boolean flag;
		boolean fab;
		while (true) {
			flag = true;
			// ѭ�����������в���ʽ
			for (int i = 0; i < productions.size(); i++) {
				String left;
				String right;
				String[] rights;
				rights = productions.get(i).returnRights();
				// ��������ʽ�Ҳ�
				for (int j = 0; j < rights.length; j++) {
					right = rights[j];

					// ���ս��
					if (nonterminals.contains(right)) {
						fab = true;
						for (int k = j + 1; k < rights.length; k++) {
							// ����first��
							for (int v = 0; v < firsts.get(rights[k]).size(); v++) {
								// ����һ��Ԫ�ص�first�����뵽ǰһ��Ԫ�ص�follow����
								if (follows.get(right).contains(firsts.get(rights[k]).get(v))) {
									continue;
								} else {
									follows.get(right).add(firsts.get(rights[k]).get(v));
									flag = false;
								}
							}
							if (isCanBeNull(rights[k])) {
								continue;
							} else {
								fab = false;
								break;
							}
						}
						// �������һ������ʽA����B������ڲ���ʽA����B����FIRST(��) �����ţ�
						// ��ô FOLLOW(A)�е����з��Ŷ���FOLLOW(B)��
						if (fab) {
							left = productions.get(i).returnLeft();
							for (int p = 0; p < follows.get(left).size(); p++) {
								if (follows.get(right).contains(follows.get(left).get(p))) {
									continue;
								} else {
									follows.get(right).add(follows.get(left).get(p));
									flag = false;
								}
							}
						}
					}
				}
			}
			// ȫ�����������ѭ��
			if (flag == true) {
				break;
			}
		}
		// ���follow���е�#
		String left;
		for (int j = 0; j < nonterminals.size(); j++) {
			left = nonterminals.get(j);
			for (int v = 0; v < follows.get(left).size(); v++) {
				if (follows.get(left).get(v).equals("#"))
					follows.get(left).remove(v);
			}
		}
	}

	// ��ȡSelect��
	public void getSelect() {
		String left;
		String right;
		String[] rights;
		ArrayList<String> follow = new ArrayList<String>();
		ArrayList<String> first = new ArrayList<String>();

		for (int i = 0; i < productions.size(); i++) {
			left = productions.get(i).returnLeft();
			rights = productions.get(i).returnRights();
			if (rights[0].equals("$")) {
				// select(i) = follow(A)
				follow = follows.get(left);
				for (int j = 0; j < follow.size(); j++) {
					if (productions.get(i).select.contains(follow.get(j))) {
						continue;
					} else {
						productions.get(i).select.add(follow.get(j));
					}
				}
			}
			// ����ķ�G�ĵ�i������ʽΪA��a�£�����
			// SELECT(i)={a}
			else {
				boolean flag = true;
				for (int j = 0; j < rights.length; j++) {
					right = rights[j];
					first = firsts.get(right);
					for (int v = 0; v < first.size(); v++) {
						if (productions.get(i).select.contains(first.get(v))) {
							continue;
						} else {
							productions.get(i).select.add(first.get(v));
						}
					}
					if (isCanBeNull(right)) {
						continue;
					} else {
						flag = false;
						break;
					}
				}
				// First�����п�
				if (flag) {
					follow = follows.get(left);
					for (int j = 0; j < follow.size(); j++) {
						if (productions.get(i).select.contains(follow.get(j))) {
							continue;
						} else {
							productions.get(i).select.add(follow.get(j));
						}
					}
				}
			}
		}
	}

	// ����Ԥ�������
	public void createPredict() {
		Production production;
		String line;
		String[] rights;
		try {
			File file = new File("src/AnalysisTable.txt");
			// if file doesn't exists, then create it
			if (!file.exists()) {
				file.createNewFile();
			}

			FileWriter fw = new FileWriter(file.getAbsoluteFile());
			BufferedWriter bw = new BufferedWriter(fw);

			for (int i = 0; i < productions.size(); i++) {
				production = productions.get(i);
				for (int j = 0; j < production.select.size(); j++) {
					line = production.returnLeft() + "#" + production.select.get(j) + " ->";
					rights = production.returnRights();
					for (int v = 0; v < rights.length; v++) {
						line = line + " " + rights[v];
					}
					line = line + "\n";
					// д���ļ�
					bw.write(line);
				}
			}
			bw.close();
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		System.out.println("��������������ļ�AnalysisTable.txt��");
	}
}
