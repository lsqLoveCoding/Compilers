import java.util.HashSet;
import java.util.Set;

public class Token {
	public static int states = 57; // 所有的状态数
	public static Set<String> keywords = new HashSet<String>();

	static {
		keywords.add("while");
		keywords.add("do");
		keywords.add("proc");
		keywords.add("int");
		keywords.add("real");
		keywords.add("if");
		keywords.add("then");
		keywords.add("else");
		keywords.add("call");
		keywords.add("record");
		keywords.add("and");
		keywords.add("or");
		keywords.add("not");
		keywords.add("true");
		keywords.add("false");
	}

	// 根据状态值返回种别码，若非终态返回空
	public static String getToken(int state) {
		switch (state) {
		case 2:
		case 5:
			return "reald";
		case 1:
		case 6:
		case 7:
		case 9:
			return "intd";
		case 10:
			return "id";
		case 11:
			return "not";
		case 12:
			return "!=";
		case 13:
			return "=";
		case 14:
			return "==";
		case 15:
			return "<";
		case 16:
			return "<=";
		case 17:
			return ">";
		case 18:
			return ">=";
		case 19:
			return "+";
		case 20:
			return "+=";
		case 21:
			return "++";
		case 22:
			return "-";
		case 23:
			return "-=";
		case 24:
			return "--";
		case 25:
			return "*";
		case 26:
			return "*=";
		case 27:
			return "%";
		case 28:
			return "%=";
		case 29:
			return "/";
		case 30:
			return "/=";
		case 33:
			return "NOTE";
		case 36:
			return "STRING";
		case 38:
			return "or";
		case 40:
			return "and";
		case 42:
			return ";";
		case 43:
			return "[";
		case 44:
			return "]";
		case 45:
			return "(";
		case 46:
			return ")";
		case 47:
			return "{";
		case 48:
			return "}";
		case 49:
			return ",";
		case 56:
			return "character";
		default:
			return null;
		}
	}
}
