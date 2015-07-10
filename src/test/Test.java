package test;

import java.util.Stack;

import symjava.symbolic.Expr;
import ws.raidrush.shunt.Context;
import ws.raidrush.shunt.Parser;
import ws.raidrush.shunt.Scanner;
import ws.raidrush.shunt.Symbol;
import ws.raidrush.shunt.Token;
import ws.raidrush.shunt.TokenStack;
import static symjava.math.SymMath.*;

public class Test {
	public static void main(String[] args) {
		Expr expr = parse("1-x",new String[]{"x"});
		System.out.println(expr);
		System.out.println(expr.diff(new symjava.symbolic.Symbol("x")));
	}

	public static Expr parse(String expr, String[] args) {
		try {
			Context ctx = new Context();
			for(int i=0; i<args.length; i++)
				ctx.setConstant(args[i], new Symbol());
			
			Parser p = new Parser(new Scanner(expr));
			Stack<TokenStack> st = p.getTokenStacks();
			while(!st.empty()) {
				TokenStack ts = st.pop();
				Token t = null;
				Stack<Expr> es = new Stack<Expr>();
				Expr l,r;
				while(ts.peek() != null) {
					t = ts.next();
					switch(t.type) {
						case Token.T_NUMBER:     // eine nummer (integer / double)
							if(t instanceof ws.raidrush.shunt.Number) {
								ws.raidrush.shunt.Number n = (ws.raidrush.shunt.Number)t;
								es.push(Expr.valueOf(n.value));
							}
							break;
						case Token.T_IDENT:      // konstante
							if(t instanceof ws.raidrush.shunt.Ident) {
								ws.raidrush.shunt.Ident n = (ws.raidrush.shunt.Ident)t;
								es.push(new symjava.symbolic.Symbol(n.value));
							}
							break;
						case Token.T_FUNCTION:   // funktion
							break;
						case Token.T_POPEN:      // (
							break;
						case Token.T_PCLOSE:     // )
							break;
						case Token.T_RIDENT:     // ident vor = (R -> right)
							break;
						case Token.T_STRING:     // string
							break;
						case Token.T_OPERATOR:   // operator
							break;
						case Token.T_PLUS:       // +
							r = es.pop();
							l = es.pop();
							es.push(l+r);
							break;
						case Token.T_MINUS:      // -
							r = es.pop();
							l = es.pop();
							es.push(l-r);
							break;
						case Token.T_TIMES:      // * 
							r = es.pop();
							l = es.pop();
							es.push(l*r);
							break;
						case Token.T_DIV:        // /
							r = es.pop();
							l = es.pop();
							es.push(l/r);
							break;
						case Token.T_MOD:        // %
							r = es.pop();
							l = es.pop();
							es.push(l%r);
							break;
						case Token.T_POW:        // ^
							r = es.pop();
							l = es.pop();
							es.push(pow(l,r));
							break;
						case Token.T_UNARY_PLUS: // + als vorzeichen (zur �bersetzungszeit ermittelt)
							break;
						case Token.T_UNARY_MINUS:// - als vorzeichen (zur �bersetzungszeit ermittelt)
							break;
						case Token.T_NOT:        // ! als vorzeichen
							break;
						case Token.T_SEMI:       // ;
							break;
						case Token.T_COMMA:      // ,
							break;
						case Token.T_ASSIGN:     // =
							break;
						default:
							break;
					};
				}
				Expr rlt = es.pop();
				return rlt;
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

}
