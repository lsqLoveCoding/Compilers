package com.analyze;

import java.util.ArrayList;

public class Production {
	private String left;
	private String[] right;
	// ��ʼ��select��
	ArrayList<String> select = new ArrayList<String>();

	public Production(String left, String[] right) {
		this.left = left;
		this.right = right;
	}

	public String[] returnRights() {
		return right;
	}

	public String returnLeft() {
		return left;
	}
}
