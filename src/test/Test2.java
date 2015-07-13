package test;

import symjava.symbolic.Expr;
import symjava.symbolic.Symbol;
import ws.raidrush.shunt.Parser;
import ws.raidrush.shunt.Scanner;

public class Test2 {

	public static void test1() {
		try {
			Parser p;
			p = new Parser(new Scanner("a=2*PI;a+1;PI=\"4yy\""));
			//p.reduce(ctx);
			System.out.println(p.opcode()); //This will clear the stack
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void test2() {
		try {
			ShuntingYardParser parser = new ShuntingYardParser();
			//Expr expr = parser.parse("z=x+y; t=z^2+z+1; diff(sin(t),x)");
			//Expr expr = parser.parse("y=x^2+2*x+1; dy=diff(y,x); sin(dy)");
			Expr expr = parser.parse("eq(y,a/(b + x)*x,array(x),array(a,b))");
			
			System.out.println(expr);
			//System.out.println(expr.diff(Symbol.x));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		//test1();
		test2();
	}

}
