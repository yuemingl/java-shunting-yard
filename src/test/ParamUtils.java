package test;

import symjava.symbolic.Expr;

public class ParamUtils {
	public static boolean isInteger(String s) {
	    return isInteger(s,10);
	}

	public static boolean isInteger(String s, int radix) {
	    if(s.isEmpty()) return false;
	    for(int i = 0; i < s.length(); i++) {
	        if(i == 0 && s.charAt(i) == '-') {
	            if(s.length() == 1) return false;
	            else continue;
	        }
	        if(Character.digit(s.charAt(i),radix) < 0) return false;
	    }
	    return true;
	}
	
	public static Class<?> getClass(String arg) {
		if(arg.charAt(0) == 'I')
			return int.class;
		else if(arg.charAt(0) == 'L')
			return long.class;
		else if(arg.charAt(0) == 'S')
			return String.class;
		else if(arg.charAt(0) == 'C')
			return char.class;
		else if(arg.charAt(0) == 'F')
			return float.class;
		else if(arg.charAt(0) == 'D')
			return double.class;
		else if(arg.charAt(0) == 'Z')
			return boolean.class;
		else if(arg.charAt(0) == 'A')
			return Object.class;
		else if(arg.charAt(0) == 'E')
			return Expr.class;
		else if(arg.charAt(0) == '[')
			return getClass1(arg);
		return null;
	}
	
	public static Class<?> getClass1(String arg) {
		int pos = 1;
		if(arg.charAt(pos) == 'I')
			return int[].class;
		else if(arg.charAt(pos) == 'L')
			return long[].class;
		else if(arg.charAt(pos) == 'S')
			return String[].class;
		else if(arg.charAt(pos) == 'C')
			return char[].class;
		else if(arg.charAt(pos) == 'F')
			return float[].class;
		else if(arg.charAt(pos) == 'D')
			return double[].class;
		else if(arg.charAt(pos) == 'Z')
			return boolean[].class;
		else if(arg.charAt(pos) == 'A')
			return Object[].class;
		else if(arg.charAt(pos) == 'E')
			return Expr[].class;
		else if(arg.charAt(pos) == '[')
			return getClass2(arg);
		return null;
	}	
	
	public static Class<?> getClass2(String arg) {
		int pos = 2;
		if(arg.charAt(pos) == 'I')
			return int[][].class;
		else if(arg.charAt(pos) == 'L')
			return long[][].class;
		else if(arg.charAt(pos) == 'S')
			return String[][].class;
		else if(arg.charAt(pos) == 'C')
			return char[][].class;
		else if(arg.charAt(pos) == 'F')
			return float[][].class;
		else if(arg.charAt(pos) == 'D')
			return double[][].class;
		else if(arg.charAt(pos) == 'Z')
			return boolean[][].class;
		else if(arg.charAt(pos) == 'A')
			return Object[][].class;
		else if(arg.charAt(pos) == 'E')
			return Expr[][].class;
		else if(arg.charAt(pos) == '[')
			return getClass3(arg);
		return null;
	}	
	
	public static Class<?> getClass3(String arg) {
		int pos = 3;
		if(arg.charAt(pos) == 'I')
			return int[][][].class;
		else if(arg.charAt(pos) == 'L')
			return long[][][].class;
		else if(arg.charAt(pos) == 'S')
			return String[][][].class;
		else if(arg.charAt(pos) == 'C')
			return char[][][].class;
		else if(arg.charAt(pos) == 'F')
			return float[][][].class;
		else if(arg.charAt(pos) == 'D')
			return double[][][].class;
		else if(arg.charAt(pos) == 'Z')
			return boolean[][][].class;
		else if(arg.charAt(pos) == 'A')
			return Object[][][].class;
		else if(arg.charAt(pos) == 'E')
			return Expr[][][].class;
		else if(arg.charAt(pos) == '[')
			return getClass4(arg);
		return null;
	}	

	public static Class<?> getClass4(String arg) {
		int pos = 4;
		if(arg.charAt(pos) == 'I')
			return int[][][][].class;
		else if(arg.charAt(pos) == 'L')
			return long[][][][].class;
		else if(arg.charAt(pos) == 'S')
			return String[][][][].class;
		else if(arg.charAt(pos) == 'C')
			return char[][][][].class;
		else if(arg.charAt(pos) == 'F')
			return float[][][][].class;
		else if(arg.charAt(pos) == 'D')
			return double[][][][].class;
		else if(arg.charAt(pos) == 'Z')
			return boolean[][][][].class;
		else if(arg.charAt(pos) == 'A')
			return Object[][][][].class;
		else if(arg.charAt(pos) == 'E')
			return Expr[][][][].class;
		else if(arg.charAt(pos) == '[')
			throw new RuntimeException("5 dim arrary is not supported!");
		return null;
	}	
	
}
