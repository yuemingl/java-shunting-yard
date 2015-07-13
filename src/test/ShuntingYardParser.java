package test;

import static symjava.math.SymMath.abs;
import static symjava.math.SymMath.cos;
import static symjava.math.SymMath.log;
import static symjava.math.SymMath.log10;
import static symjava.math.SymMath.log2;
import static symjava.math.SymMath.pow;
import static symjava.math.SymMath.sin;
import static symjava.math.SymMath.sqrt;
import static symjava.math.SymMath.tan;

import java.util.HashMap;
import java.util.Stack;

import lc.bytecode.ShuntingYardParser.ExprHolder;
import symjava.domains.Domain;
import symjava.domains.Interval;
import symjava.math.SymMath;
import symjava.relational.Eq;
import symjava.symbolic.Expr;
import symjava.symbolic.Integrate;
import symjava.symbolic.SymReal;
import symjava.symbolic.Symbol;
import ws.raidrush.shunt.Context;
import ws.raidrush.shunt.Function;
import ws.raidrush.shunt.Ident;
import ws.raidrush.shunt.Parser;
import ws.raidrush.shunt.RNumber;
import ws.raidrush.shunt.RSymbol;
import ws.raidrush.shunt.Scanner;
import ws.raidrush.shunt.Token;
import ws.raidrush.shunt.TokenStack;

public class ShuntingYardParser {
	// Contain defined functions 
	//protected Context ctx;
	protected HashMap<String, Expr> ctx = new HashMap<String, Expr>();
	
	public ShuntingYardParser() {
//		ctx = new Context();
//		
//		//See class Context for pi, e
//		
//		ctx.setFunction("sqrt", new DummyFunction());
//		ctx.setFunction("sin", new DummyFunction());
//		ctx.setFunction("cos", new DummyFunction());
//		ctx.setFunction("tan", new DummyFunction());
//		ctx.setFunction("log", new DummyFunction());
//		ctx.setFunction("log2", new DummyFunction());
//		ctx.setFunction("log10", new DummyFunction());
//		ctx.setFunction("pow", new DummyFunction());
//		ctx.setFunction("abs", new DummyFunction());
//		ctx.setFunction("diff", new DummyFunction());
//		ctx.setFunction("integrate", new DummyFunction());
//		ctx.setFunction("domain", new DummyFunction());
//		ctx.setFunction("interval", new DummyFunction());
//		ctx.setFunction("eq", new DummyFunction());
//		ctx.setFunction("array", new DummyFunction());
	}
	
	public static class DummyFunction implements Function {
		public RSymbol call(RSymbol[] args) {
			return null;
		}
	}
	
	public static class ExprHolder extends Expr {
		public Object obj;
		public ExprHolder(Object obj) {
			this.obj = obj;
		}
		
		@Override
		public Expr simplify() { return null; }
		@Override
		public boolean symEquals(Expr other) { return false; }
		@Override
		public Expr[] args() { return null; }
		@Override
		public Expr diff(Expr x) { return null; }
		@Override
		public TYPE getType() { return null; }
	}
	
//	public Context getContext() {
//		return ctx;
//	}
	

	public Expr parse(String expr) {
		try {
			Parser p = new Parser(new Scanner(expr));
			//System.out.println(p.opcode()); //This will clear the stack
			
			Stack<TokenStack> st = p.getTokenStacks();
			Stack<Expr> symStack = null;
			for(TokenStack ts : st) {
				Token t = null;
				Expr l,r;
				symStack = new Stack<Expr>();
				while(ts.peek() != null) {
					t = ts.next();
					switch(t.type) {
						case Token.T_NUMBER:     // number
							if(t instanceof RNumber) {
								RNumber n = (RNumber)t;
								symStack.push(Expr.valueOf(n.value));
							}
							break;
						case Token.T_IDENT:      // identity
							if(t instanceof Ident) {
								Ident n = (Ident)t;
								if(n.value.equalsIgnoreCase("e")) {
									symStack.push(SymMath.E);
								} else if(n.value.equalsIgnoreCase("pi")) {
									symStack.push(SymMath.PI);
								} else {
									Expr local = ctx.get(n.value);
									if(local != null) {
										symStack.push(local);
									} else {
										symStack.push(new Symbol(n.value));
									}
								}
							}
							break;
						case Token.T_FUNCTION:   // function
							Ident n = (Ident)t;
							if(n.value.equalsIgnoreCase("sqrt")) { l = symStack.pop(); symStack.push(sqrt(l)); }
							else if(n.value.equalsIgnoreCase("sin")) { l = symStack.pop(); symStack.push(sin(l)); }
							else if(n.value.equalsIgnoreCase("cos")) { l = symStack.pop(); symStack.push(cos(l)); }
							else if(n.value.equalsIgnoreCase("tan")) { l = symStack.pop(); symStack.push(tan(l)); }
							else if(n.value.equalsIgnoreCase("log")) { l = symStack.pop(); symStack.push(log(l)); }
							else if(n.value.equalsIgnoreCase("log2")) { l = symStack.pop(); symStack.push(log2(l)); }
							else if(n.value.equalsIgnoreCase("log10")) { l = symStack.pop(); symStack.push(log10(l)); }
							else if(n.value.equalsIgnoreCase("pow")) { r = symStack.pop(); l = symStack.pop(); symStack.push(pow(l, r)); }
							else if(n.value.equalsIgnoreCase("abs")) { l = symStack.pop(); symStack.push(abs(l)); }
							else if(n.value.equalsIgnoreCase("diff")) { r = symStack.pop(); l = symStack.pop(); symStack.push(l.diff(r)); }
							else if(n.value.equalsIgnoreCase("integrate")) {
								ExprHolder domainHolder = (ExprHolder)symStack.pop();
								Expr integrand = symStack.pop();
								Expr a = Integrate.apply(integrand, (Domain)(domainHolder.obj));
								symStack.push(a); 
							}
							//TODO
							//else if(n.value.equalsIgnoreCase("domain")) { l = es.pop(); es.push(sin(l)); }
							else if(n.value.equalsIgnoreCase("interval")) {
								SymReal<?> step = (SymReal<?>)symStack.pop();
								Expr end = symStack.pop();
								Expr start = symStack.pop();
								Expr coordVar = symStack.pop();
								Domain domain = Interval.apply(start, end, coordVar).
										setStepSize(coordVar, step.getDoubleValue());
								symStack.push(new ExprHolder(domain)); 
								}
							else if(n.value.equalsIgnoreCase("eq")) {
								n = (Ident)t;
								if(n.argc == 2) {
									r = symStack.pop();
									l = symStack.pop();
									Eq eq = new Eq(l,r);
									symStack.push(eq);
								} else if(n.argc == 3) {
									ExprHolder freeVarsHolder = (ExprHolder)symStack.pop();
									r = symStack.pop();
									l = symStack.pop();
									Eq eq = new Eq(l,r,(Expr[])(freeVarsHolder.obj));
									symStack.push(eq);
									
								} else if(n.argc == 4) {
									ExprHolder paramsHoleder = (ExprHolder)symStack.pop();
									ExprHolder freeVarsHolder = (ExprHolder)symStack.pop();
									r = symStack.pop();
									l = symStack.pop();
									Eq eq = new Eq(l,r,(Expr[])(freeVarsHolder.obj), (Expr[])(paramsHoleder.obj));
									symStack.push(eq);
								} else {
									throw new RuntimeException("Wrong number of arguments of eq()");
								}
							}
							else if(n.value.equalsIgnoreCase("array")) {
								Expr[] exprAry = new Expr[n.argc];
								for(int i=0; i<n.argc; i++) {
									exprAry[n.argc-i-1] = symStack.pop();
								}
								symStack.push(new ExprHolder(exprAry)); 
							}
							break;
						case Token.T_POPEN:      // (
							break;
						case Token.T_PCLOSE:     // )
							break;
						case Token.T_RIDENT:     // right identity
							n = (Ident)t;
							symStack.push(new Symbol(n.value));
							System.out.println("define local: "+n.value);
							break;
						case Token.T_STRING:     // string
							throw new RuntimeException("String is not supported!");
						case Token.T_OPERATOR:   // operator
							break;
						case Token.T_PLUS:       // +
							r = symStack.pop();
							l = symStack.pop();
							symStack.push(l+r);
							break;
						case Token.T_MINUS:      // -
							r = symStack.pop();
							l = symStack.pop();
							symStack.push(l-r);
							break;
						case Token.T_TIMES:      // * 
							r = symStack.pop();
							l = symStack.pop();
							symStack.push(l*r);
							break;
						case Token.T_DIV:        // /
							r = symStack.pop();
							l = symStack.pop();
							symStack.push(l/r);
							break;
						case Token.T_MOD:        // %
							r = symStack.pop();
							l = symStack.pop();
							symStack.push(l%r);
							break;
						case Token.T_POW:        // ^
							r = symStack.pop();
							l = symStack.pop();
							symStack.push(pow(l,r));
							break;
						case Token.T_UNARY_PLUS: // + (determined at compile time) as a sign
							break;
						case Token.T_UNARY_MINUS:// - (determined at compile time) as a sign
							l = symStack.pop();
							symStack.push(-l);
							break;
						case Token.T_NOT:        // ! as a sign
							break;
						case Token.T_SEMI:       // ;
							break;
						case Token.T_COMMA:      // ,
							break;
						case Token.T_ASSIGN:     // =
							r = symStack.pop();
							l = symStack.pop();
							ctx.put(l.toString(), r);
							symStack.push(l);
							break;
						default:
							break;
					}
				}
			}
			return symStack.pop();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

}
