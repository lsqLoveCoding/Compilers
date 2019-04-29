import java.io.*;
import java.util.*;

public class DFA {
	public Map<Character, Integer> map; // ���������ַ���DFA�ļ���һ���е��±�
	public int[][] table; // DFA��
	// ���ļ��ж�ȡDFA��

	DFA(String file) {
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new FileReader(file));
			String firstLine = reader.readLine();
			String[] arr = firstLine.trim().split("\\s+");
			map = new HashMap<Character, Integer>();
			for (int i = 0; i < arr.length; i++) {
				map.put(arr[i].charAt(0), i);
			}
			table = new int[Token.states][arr.length];
			for (int i = 0; i < Token.states; i++) {
				String line = reader.readLine();
				String[] items = line.trim().split("\\s+");
				for (int j = 0; j < arr.length; j++) {
					table[i][j] = Integer.parseInt(items[j]);
				}
			}
			reader.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// ����DFA��������룬����token����
	public List<String[]> doNFA(String code) {
		List<String[]> tokens = new ArrayList<String[]>();
		int state = 0; // ��ǰ״̬
		int lastFinalState = 0; // ��һ����̬�����ڴ�����
		int finalStateIndex = 0; // �����ϸ���̬ʱ���ַ�λ��
		int beginIndex = 0; // ������ʼ�ַ�λ��

		for (int i = 0;; i++) {
			// ��β������
			if (i >= code.length()) {
				// һ�����ʽ���
				if (lastFinalState != 0) { // ֮ǰ������ںϷ���̬������
					tokens.add(getTokenDiscrible(code, beginIndex, finalStateIndex + 1, lastFinalState));
					state = 0;
					i = finalStateIndex; // ���ϸ���̬����ʼ����
					lastFinalState = 0; // �����ϸ���̬��ֵ
					beginIndex = i + 1;
					if (beginIndex >= code.length())
						break;
				} else { // ֮ǰ��������ںϷ���̬������
					if (!code.substring(beginIndex, i).matches("\\s+") && beginIndex < i) // ���������������հ��ַ�
						tokens.add(getTokenDiscrible(code, beginIndex, i, -1));
					break;
				}
				continue;
			}
			char c = code.charAt(i);
			if (map.containsKey(c)) { // c�ǺϷ��ǿհ��ַ�
				if (table[state][map.get(c)] == 0) { // ��һ��״̬Ϊ��
					if (lastFinalState != 0) { // ֮ǰ������ںϷ���̬
						tokens.add(getTokenDiscrible(code, beginIndex, finalStateIndex + 1, lastFinalState));
						state = 0;
						i = finalStateIndex; // ���ϸ���̬����ʼ����
						lastFinalState = 0; // �����ϸ���̬��ֵ
						beginIndex = i + 1;
					} else { // ֮ǰ���벻���ںϷ���̬�����д�����
						while (i < code.length()
								&& (!map.containsKey(code.charAt(i)) || table[0][map.get(code.charAt(i))] == 0)) // ����ȷ���ַ�
							i++; // ����
						tokens.add(getTokenDiscrible(code, beginIndex, i, -1));
						state = 0;
						lastFinalState = 0;
						beginIndex = i;
						i--;
					}
				} else // ��һ��״̬�ǿ�
				{
					state = table[state][map.get(c)]; // ת�Ƶ���һ��״̬
					if (Token.getToken(state) != null) // ��һ����̬
					{
						lastFinalState = state;
						finalStateIndex = i;
					}
				}
			} else if (Character.isWhitespace(c)) // c�ǿհ��ַ�
			{
				if (state == 34 || state == 35 || state == 31 || state == 32) // �ַ�����ע���еĿհ��ַ������ضϴ���
					continue;

				// һ�����ʽ���
				if (lastFinalState != 0) { // ֮ǰ������ںϷ���̬
					tokens.add(getTokenDiscrible(code, beginIndex, finalStateIndex + 1, lastFinalState));
					state = 0;
					i = finalStateIndex; // ���ϸ���̬����ʼ����
					lastFinalState = 0; // �����ϸ���̬��ֵ
					beginIndex = i + 1;
				} else { // ֮ǰ��������ںϷ���̬
					while (i < code.length()
							&& (!map.containsKey(code.charAt(i)) || table[0][map.get(code.charAt(i))] == 0)) // �հ��ַ�����ȷ���ַ�
						i++; // ����
					if (!code.substring(beginIndex, i).matches("\\s+") && beginIndex < i) // ���������������հ��ַ�
						tokens.add(getTokenDiscrible(code, beginIndex, i, -1));
					state = 0;
					lastFinalState = 0;
					beginIndex = i;
					i--;
				}
			} else // c�ǷǷ��ַ�
			{
				if (state != 34 && state != 35 && state != 31 && state != 32) // c�����ַ�����ע����
					tokens.add(getTokenDiscrible(code, i, i + 1, -2)); // �Ƿ��ַ�
			}
		}
		return tokens;
	}

	private String[] getTokenDiscrible(String code, int beginIndex, int finalIndex, int state) {
		String token = Token.getToken(state); // �ֱ���
		String text = code.substring(beginIndex, finalIndex);
		String str[] = new String[3];
		int line = 1;
		for (int i = 0; i < beginIndex; i++)
			if (code.charAt(i) == '\n')
				line++;
		str[0] = line + "";
		if (state == -1) {
			str[1] = "�ʷ�����";
			str[2] = text;
			return str;
		}
		if (state == -2) {
			str[1] = "�Ƿ��ַ�";
			str[2] = text;
			return str;
		}
		if (token.equals("id") && Token.keywords.contains(text)) // �ǹؼ��ֶ����Ǳ�ʶ��
			token = text;
		str[1] = token;
		if (token.equals("intd") || token.equals("reald") || token.equals("id") || token.equals("STRING"))
			str[2] = text;
		else
			str[2] = "";
		return str;
	}
}
