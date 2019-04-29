package com.analyze;

import java.util.*;

import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

public class lexical_analysis {

	private String text;
	private JTable jtable1;
	private JTable jtable2;
	private JTable jtable3;
	public static int symbol_pos = 0; // ��¼���ű�λ��
	public static Map<String, Integer> symbol = new HashMap<String, Integer>(); // ���ű�HashMap

	public lexical_analysis(String text, JTable jtable1, JTable jtable2, JTable jtable3) {
		this.text = text;
		this.jtable1 = jtable1;
		this.jtable2 = jtable2;
		this.jtable3 = jtable3;
	}

	public void analyze() {
		String[] texts = text.split("\n");
		symbol.clear();
		symbol_pos = 0;
		for (int m = 0; m < texts.length; m++) {
			String str = texts[m];
			if (str.equals("")) {
				continue;
			} else {
				// ���ַ���ת��Ϊ�ַ�������
				char[] strline = str.toCharArray();

				for (int i = 0; i < strline.length; i++) {
					// ����strline�е�ÿ���ַ�
					char ch = strline[i];
					// ��ʼ��token�ַ���Ϊ��
					String token = "";

					// �жϱ�ʶ�͹ؼ���
					if (isAlpha(ch)) // �жϹؼ��ֺͱ�ʶ��
					{
						do {
							token += ch;
							i++;
							if (i >= strline.length)
								break;
							ch = strline[i];
						} while (ch != '\0' && (isAlpha(ch) || isDigit(ch)));

						--i; // ����ָ���1,ָ�����
						// �ǹؼ���
						if (isMatchKeyword(token.toString())) {
							DefaultTableModel tableModel = (DefaultTableModel) jtable1.getModel();
							tableModel.addRow(new Object[] { token, "�ؼ���", "100", m + 1 });
							jtable1.invalidate();
						}
						// �Ǳ�ʶ��
						else {
							// ������ű�Ϊ�ջ���ű��в�������ǰtoken�������
							if (symbol.isEmpty() || (!symbol.isEmpty() && !symbol.containsKey(token))) {
								symbol.put(token, symbol_pos);
								DefaultTableModel tableModel3 = (DefaultTableModel) jtable3.getModel();
								tableModel3.addRow(new Object[] { token, symbol_pos });
								jtable3.invalidate();
								symbol_pos++;
							}
							DefaultTableModel tableModel1 = (DefaultTableModel) jtable1.getModel();
							tableModel1.addRow(new Object[] { token, "��ʶ��", "200", m + 1 });
							jtable1.invalidate();
						}
						token = "";
					}
					// �ж����ֳ���
					else if (isDigit(ch)) {
						// ��ʼ������1״̬
						int state = 1;
						// ������������
						int k;
						Boolean isfloat = false;
						while ((ch != '\0') && (isDigit(ch) || ch == '.' || ch == 'e' || ch == '-')) {
							if (ch == '.' || ch == 'e')
								isfloat = true;

							for (k = 0; k <= 6; k++) {
								char tmpstr[] = digitDFA[state].toCharArray();
								if (ch != '#' && 1 == in_digitDFA(ch, tmpstr[k])) {
									token += ch;
									state = k;
									break;
								}
							}
							if (k > 6)
								break;
							// ����������ǰ�ƶ�
							i++;
							if (i >= strline.length)
								break;
							ch = strline[i];
						}
						Boolean haveMistake = false;

						if (state == 2 || state == 4 || state == 5) {
							haveMistake = true;
						}

						else// 1,3,6
						{
							if ((!isOp(ch) || ch == '.') && !isDigit(ch))
								haveMistake = true;
						}

						// ������
						if (haveMistake) {
							// һֱ�����ɷָ���ַ�����
							while (ch != '\0' && ch != ',' && ch != ';' && ch != ' ') {
								token += ch;
								i++;
								if (i >= strline.length)
									break;
								ch = strline[i];
							}
							DefaultTableModel tableModel2 = (DefaultTableModel) jtable2.getModel();
							tableModel2.addRow(new Object[] { m + 1, token + " ȷ���޷��ų���������ȷ" });
							jtable2.invalidate();
						} else {
							if (isfloat) {
								DefaultTableModel tableModel1 = (DefaultTableModel) jtable1.getModel();
								tableModel1.addRow(new Object[] { token, "�����ͳ���", "300", m + 1 });
								jtable1.invalidate();
							} else {
								DefaultTableModel tableModel1 = (DefaultTableModel) jtable1.getModel();
								tableModel1.addRow(new Object[] { token, "���ͳ���", "301", m + 1 });
								jtable1.invalidate();
							}
						}
						i--;
						token = "";
						// �ж����ֳ���
					}
					// ʶ���ַ�����
					else if (ch == '\'') {
						// ��ʼ��״̬Ϊ0
						int state = 0;
						// token���ϡ�
						token += ch;

						while (state != 3) {
							i++;
							if (i >= strline.length)
								break;
							ch = strline[i];
							for (int k = 0; k < 4; k++) {
								char tmpstr[] = charDFA[state].toCharArray();
								if (in_charDFA(ch, tmpstr[k])) {
									token += ch;
									state = k;
									break;
								}
							}
						}
						if (state != 3) {
							DefaultTableModel tableModel2 = (DefaultTableModel) jtable2.getModel();
							tableModel2.addRow(new Object[] { m + 1, token + " �ַ���������δ���" });
							jtable2.invalidate();
							i--;
						} else {
							DefaultTableModel tableModel1 = (DefaultTableModel) jtable1.getModel();
							tableModel1.addRow(new Object[] { token, "�ַ�����", "302", m + 1 });
							jtable1.invalidate();
						}
						token = "";
						// ʶ���ַ�����
					}
					// ʶ���ַ�������
					else if (ch == '"') {
						String string = "";
						string += ch;

						int state = 0;
						Boolean haveMistake = false;

						while (state != 3) {
							i++;

							if (i >= strline.length - 1) {
								haveMistake = true;
								break;
							}

							ch = strline[i];

							if (ch == '\0') {
								haveMistake = true;
								break;
							}

							for (int k = 0; k < 4; k++) {
								char tmpstr[] = stringDFA[state].toCharArray();
								if (in_stringDFA(ch, tmpstr[k])) {
									string += ch;
									if (k == 2 && state == 1) {
										if (isEsSt(ch)) // ��ת���ַ�
											token = token + '\\' + ch;
										else
											token += ch;
									} else if (k != 3 && k != 1)
										token += ch;
									state = k;
									break;
								}
							}
						}
						if (haveMistake) {
							DefaultTableModel tableModel2 = (DefaultTableModel) jtable2.getModel();
							tableModel2.addRow(new Object[] { m + 1, string + " �ַ�����������δ���" });
							jtable2.invalidate();
							--i;
						} else {
							DefaultTableModel tableModel1 = (DefaultTableModel) jtable1.getModel();
							tableModel1.addRow(new Object[] { token, "�ַ�������", "303", m + 1 });
							jtable1.invalidate();
						}
						token = "";
						// ʶ���ַ�������
					}
					// ������ͽ��
					else if (isOp(ch)) {
						token += ch;
						// ���������һ��"="
						if (isPlusEqu(ch)) {
							i++;
							if (i >= strline.length)
								break;
							ch = strline[i];
							if (ch == '=')
								token += ch;
							else {
								// ���������һ�����Լ�һ����
								if (isPlusSame(strline[i - 1]) && ch == strline[i - 1])
									token += ch;
								else
									--i;
							}
						}
						// �ж��Ƿ�Ϊ���
						if (token.length() == 1) {
							char signal = token.charAt(0);
							boolean isbound = false;
							for (int bound = 0; bound < boundary.length; bound++) {
								if (signal == boundary[bound]) {
									DefaultTableModel tableModel1 = (DefaultTableModel) jtable1.getModel();
									tableModel1.addRow(new Object[] { token, "���", "304", m + 1 });
									jtable1.invalidate();
									isbound = true;
									break;
								}
							}
							if (!isbound) {
								DefaultTableModel tableModel1 = (DefaultTableModel) jtable1.getModel();
								tableModel1.addRow(new Object[] { token, "�����", "305", m + 1 });
								jtable1.invalidate();
							}
						} else {
							DefaultTableModel tableModel1 = (DefaultTableModel) jtable1.getModel();
							tableModel1.addRow(new Object[] { token, "�����", "305", m + 1 });
							jtable1.invalidate();
						}

						token = "";
						// ʶ�������
					}
					// ʶ��ע��//
					else if (ch == '/') {
						token += ch;
						i++;
						if (i >= strline.length)
							break;
						ch = strline[i];

						// ���Ƕ���ע�ͼ�����ע��
						if (ch != '*' && ch != '/') {
							if (ch == '=')
								token += ch; // /=
							else {
								--i; // ָ����� // /
							}
							DefaultTableModel tableModel1 = (DefaultTableModel) jtable1.getModel();
							tableModel1.addRow(new Object[] { token, "�����", "305", m + 1 });
							jtable1.invalidate();
							token = "";
						}
						// ע�Ϳ����ǡ�//��Ҳ�����ǡ�/*��
						else {
							Boolean haveMistake = false;
							int State = 0;
							if (ch == '*') {
								// ch == '*'
								token += ch;
								int state = 2;

								while (state != 4) {
									i++;
									if (i >= strline.length)
										break;
									ch = strline[i];

									if (ch == '\0') {
										haveMistake = true;
										break;
									}
									for (int k = 2; k <= 4; k++) {
										char tmpstr[] = noteDFA[state].toCharArray();
										if (in_noteDFA(ch, tmpstr[k], state)) {
											token += ch;
											state = k;
											break;
										}
									}
								}
								State = state;
								// if '*'
							} else if (ch == '/') {
								// ����ע�Ͷ�ȡ�����ַ�
								int index = str.lastIndexOf("//");

								String tmpstr = str.substring(index);
								int tmpint = tmpstr.length();
								for (int k = 0; k < tmpint; k++)
									i++;
								token = tmpstr;
							}
							if (haveMistake || State != 4) {
								if (ch != '/') {
									DefaultTableModel tableModel2 = (DefaultTableModel) jtable2.getModel();
									tableModel2.addRow(new Object[] { m + 1, "ע��δ���" });
									jtable2.invalidate();
									--i;
								}
							} else {
								DefaultTableModel tableModel1 = (DefaultTableModel) jtable1.getModel();
								tableModel1.addRow(new Object[] { token, "ע��", "306", m + 1 });
								jtable1.invalidate();
							}
							token = "";
							// Ϊע��
						}
						// ʶ��ע��
					}
					// ���Ϸ��ַ�
					else {
						if (ch != ' ' && ch != '\t' && ch != '\0' && ch != '\n' && ch != '\r') {
							DefaultTableModel tableModel2 = (DefaultTableModel) jtable2.getModel();
							tableModel2.addRow(new Object[] { m + 1, "���ڲ��Ϸ��ַ�" });
							jtable2.invalidate();
							System.out.println(ch);
						}
					}
					// ����strline�е�ÿ���ַ�
				}
				// �����ı���Ϊ��
			}
			// ����ÿ���ı�
		}
		// analyze()
	}

	// �ж���ĸ���»���
	public static Boolean isAlpha(char ch) {
		return ((ch >= 'a' && ch <= 'z') || (ch >= 'A' && ch <= 'Z') || ch == '_');
	}

	// �ж�����
	public static Boolean isDigit(char ch) {
		return (ch >= '0' && ch <= '9');
	}

	// �ж��Ƿ��������
	public static Boolean isOp(char ch) {
		for (int i = 0; i < operator.length; i++)
			if (ch == operator[i]) {
				return true;
			}
		return false;
	}

	public static Boolean isMatchKeyword(String str) {
		Boolean flag = false;
		for (int i = 0; i < keywords.length; i++) {
			if (str.equals(keywords[i])) {
				flag = true;
				break;
			}
		}
		return flag;
	}

	// �ؼ���
	public static String keywords[] = { "auto", "double", "int", "struct", "break", "else", "long", "switch", "case",
			"enum", "register", "typedef", "char", "extern", "return", "union", "const", "float", "short", "unsigned",
			"continue", "for", "signed", "void", "default", "goto", "sizeof", "volatile", "do", "if", "while", "static",
			"main", "String" };
	public static char operator[] = { '+', '-', '*', '=', '<', '>', '&', '|', '~', '^', '!', '(', ')', '[', ']', '{',
			'}', '%', ';', ',', '#', '.' };
	public static char boundary[] = { ',', ';', '[', ']', '(', ')', '.', '{', '}' };

	// �������ɼӵ���
	public static Boolean isPlusEqu(char ch) {
		return ch == '+' || ch == '-' || ch == '*' || ch == '/' || ch == '=' || ch == '>' || ch == '<' || ch == '&'
				|| ch == '|' || ch == '^';
	}

	// �����������������һ��
	public static Boolean isPlusSame(char ch) {
		return ch == '+' || ch == '-' || ch == '&' || ch == '|';
	}

	// a���������ַ���b�����\��'֮����ַ�
	public static String stringDFA[] = { "#\\b#", "##a#", "#\\b\"", "####" };

	public static Boolean in_stringDFA(char ch, char key) {
		if (key == 'a')
			return true;
		if (key == '\\')
			return ch == key;
		if (key == '"')
			return ch == key;
		if (key == 'b')
			return ch != '\\' && ch != '"';
		return false;
	}

	// a���������ַ���b�����\��'֮����ַ�
	public static String charDFA[] = { "#\\b#", "##a#", "###\'", "####" };

	public static Boolean isEsSt(char ch) {
		return ch == 'a' || ch == 'b' || ch == 'f' || ch == 'n' || ch == 'r' || ch == 't' || ch == 'v' || ch == '?'
				|| ch == '0';
	}

	public static Boolean in_charDFA(char ch, char key) {
		if (key == 'a')
			return true;
		if (key == '\\')
			return ch == key;
		if (key == '\'')
			return ch == key;
		if (key == 'b')
			return ch != '\\' && ch != '\'';
		return false;
	}

	// DFA of digit
	public static String digitDFA[] = { "#d#####", "#d.#e##", "###d###", "###de##", "#####-d", "######d", "######d" };

	// �ж���������Ƿ����״̬��
	public static int in_digitDFA(char ch, char test) {
		if (test == 'd') {
			if (isDigit(ch))
				return 1;
			else
				return 0;
		} else {
			if (ch == test)
				return 1;
			else
				return 0;
		}
	}

	// ����ע��DFA
	public static String noteDFA[] = { "#####", "##*##", "##c*#", "##c*/", "#####" };

	public static Boolean in_noteDFA(char ch, char nD, int s) {
		if (s == 2) {
			if (nD == 'c') {
				if (ch != '*')
					return true;
				else
					return false;
			}
		}
		if (s == 3) {
			if (nD == 'c') {
				if (ch != '*' && ch != '/')
					return true;
				else
					return false;
			}
		}
		return (ch == nD) ? true : false;
	}
}
