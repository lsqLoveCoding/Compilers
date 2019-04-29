package com.analyze;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;

import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

public class Parse {
	private HashMap<String, String> predictMap;
	private ArrayList<String> inputCache;
	private ArrayList<String> stack;
	private JTable jtable2;
	private JTable jtable4;

	public Parse(ArrayList<String> input_cache, JTable jtable2, JTable jtable4) {
		predictMap = new HashMap<String, String>();
		this.inputCache = input_cache;
		stack = new ArrayList<String>();
		this.jtable2 = jtable2;
		this.jtable4 = jtable4;
		getPredictMap();
	}
	
	// ִ�з������̣���ջ���滻����ջ��
	public void Parsing() {
		// ��ʼ����ѹ��ջ
		stack.add("S");
		String right;
		String leftandinput;
		String process = "";
		// ��ջ�ǿգ����뻺��������
		while (stack.size() > 0 && inputCache.size() > 0) {
			// ���뻺�������Ƶ����Ŵ���һ���ַ���ȵĻ���ɾ��
			try {
				if (inputCache.get(0).equals(stack.get(stack.size() - 1))) {
					inputCache.remove(0);
					stack.remove(stack.size() - 1);
					continue;
				}
			} catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
			}

			// ƥ���ַ�
			leftandinput = stack.get(stack.size() - 1) + "-" + inputCache.get(0);
			// �ܹ��ҵ�ƥ���
			if ((right = predictMap.get(leftandinput)) != null) {
				// �������ʽ���Ƶ�����
				process = "";
				for (int i = stack.size() - 1; i > -1; i--) {
					process = process + stack.get(i) + " ";
				}
				// ���
				DefaultTableModel tableModel = (DefaultTableModel) jtable4.getModel();
				tableModel.addRow(new Object[] { stack.get(stack.size() - 1) + " -> " + right, process });
				jtable4.invalidate();
				// ɾ���������ַ���ѹ���ջ
				stack.remove(stack.size() - 1);
				if (right.equals("$")) {
					// ֻ����ѹ
				}
				// ѹ������ַ�
				else {
					String[] arg = right.split(" ");
					for (int i = arg.length - 1; i > -1; i--) {
						// ����ѹ���ջ
						stack.add(arg[i]);
					}
				}
			}
			// ����Ļ�����
			else {
				// ������дprocess
				process = "";
				for (int i = stack.size() - 1; i > -1; i--) {
					process = process + stack.get(i) + " ";
				}
				DefaultTableModel tableModel = (DefaultTableModel) jtable2.getModel();
				tableModel.addRow(new Object[] { "�޷�ʶ����ַ�:" + inputCache.get(0), "����ʽ:" + leftandinput });
				jtable4.invalidate();
				inputCache.remove(0);
			}
		}
	}

	// ���Ԥ��������еĲ���ʽ�Լ���Ӧ��select��
	// �洢��ʽΪ��ֵ�Ե���ʽ
	public void getPredictMap() {
		String text_line;
		String left;
		String symbol;
		String right;
		try {
			// ��ʼ��
			predictMap = new HashMap<String, String>();
			File file = new File("src/AnalysisTable.txt");
			BufferedReader reader = new BufferedReader(new FileReader(file));
			while ((text_line = reader.readLine()) != null) {
				left = text_line.split("#")[0];
				symbol = (text_line.split("#")[1]).split("->")[0].trim();
				right = (text_line.split("#")[1]).split("->")[1].trim();
				predictMap.put(left + "-" + symbol, right);
			}
			reader.close();
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}
}
