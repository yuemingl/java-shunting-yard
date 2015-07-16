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

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Stack;

import symjava.domains.Domain;
import symjava.domains.Interval;
import symjava.math.SymMath;
import symjava.relational.Eq;
import symjava.symbolic.Expr;
import symjava.symbolic.Integrate;
import symjava.symbolic.SymDouble;
import symjava.symbolic.SymInteger;
import symjava.symbolic.SymReal;
import symjava.symbolic.Symbol;
import symjava.symbolic.utils.Utils;
import ws.raidrush.shunt.Function;
import ws.raidrush.shunt.Ident;
import ws.raidrush.shunt.Parser;
import ws.raidrush.shunt.RDouble;
import ws.raidrush.shunt.RInteger;
import ws.raidrush.shunt.RSymbol;
import ws.raidrush.shunt.Scanner;
import ws.raidrush.shunt.Token;
import ws.raidrush.shunt.TokenStack;

public class ShuntingYardParser {
	// Contain defined symbols, functions 
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
	
	public static class ObjHolder{
		public Object obj;
		public String type;
		public ObjHolder(Object obj) {
			this.obj = obj;
			if(obj instanceof SymInteger) {
				type = "I";
			} else if(obj instanceof SymDouble) {
				type = "D";
			} else 
				type = "E"; //expr
		}
		public ObjHolder(String type, Object obj) {
			this.type = type;
			this.obj = obj;
		}
		public Expr expr() {
			return (Expr)obj;
		}
		public Object getJavaObj() {
			if(type.equals("I")) {
				SymInteger a = (SymInteger)obj;
				return a.getIntValue();
			} else if(type.equals("D")) {
				SymDouble a = (SymDouble)obj;
				return a.getDoubleValue();
			} else if(type.equals("[D")) {
				Expr[] exprs = (Expr[])obj;
				double[] rlt = new double[exprs.length];
				for(int i=0; i<rlt.length; i++) {
					SymReal<?> a = (SymReal<?>)exprs[i];
					rlt[i] = a.getDoubleValue();
				}
				return rlt;
			} else if(type.equals("E") || type.equals("[E")) {
				return obj;
			} else {
				throw new RuntimeException(type+": "+obj);
			}
		}
	}
	
//	public Context getContext() {
//		return ctx;
//	}
	

	public Expr parse(String expr) {
		try {
			Parser p = new Parser(new Scanner(expr));
			//System.out.println(p.opcode()); //This will clear the stack
			
			Stack<TokenStack> tokenStack = p.getTokenStacks();
			Stack<ObjHolder> symStack = null;
			for(TokenStack ts : tokenStack) {
				Token t = null;
				Expr l,r;
				symStack = new Stack<ObjHolder>();
				while(ts.peek() != null) {
					t = ts.next();
					switch(t.type) {
						case Token.T_NUMBER:     // number
							if(t instanceof RDouble) {
								RDouble n = (RDouble)t;
								symStack.push(new ObjHolder(Expr.valueOf(n.value)));
							} else if(t instanceof RInteger) {
								RInteger n = (RInteger)t;
								symStack.push(new ObjHolder(Expr.valueOf(n.value)));
							}
							break;
						case Token.T_IDENT:      // identity
							if(t instanceof Ident) {
								Ident n = (Ident)t;
								if(n.value.equalsIgnoreCase("e")) {
									symStack.push(new ObjHolder(SymMath.E));
								} else if(n.value.equalsIgnoreCase("pi")) {
									symStack.push(new ObjHolder(SymMath.PI));
								} else {
									Expr local = ctx.get(n.value);
									if(local != null) {
										symStack.push(new ObjHolder(local));
									} else {
										symStack.push(new ObjHolder(new Symbol(n.value)));
									}
								}
							}
							break;
						case Token.T_FUNCTION:   // function
							Ident n = (Ident)t;
							if(n.value.equalsIgnoreCase("sqrt")) { l = symStack.pop().expr(); symStack.push(new ObjHolder(sqrt(l))); }
							else if(n.value.equalsIgnoreCase("sin")) { l = symStack.pop().expr(); symStack.push(new ObjHolder(sin(l))); }
							else if(n.value.equalsIgnoreCase("cos")) { l = symStack.pop().expr(); symStack.push(new ObjHolder(cos(l))); }
							else if(n.value.equalsIgnoreCase("tan")) { l = symStack.pop().expr(); symStack.push(new ObjHolder(tan(l))); }
							else if(n.value.equalsIgnoreCase("log")) { l = symStack.pop().expr(); symStack.push(new ObjHolder(log(l))); }
							else if(n.value.equalsIgnoreCase("log2")) { l = symStack.pop().expr(); symStack.push(new ObjHolder(log2(l))); }
							else if(n.value.equalsIgnoreCase("log10")) { l = symStack.pop().expr(); symStack.push(new ObjHolder(log10(l))); }
							else if(n.value.equalsIgnoreCase("pow")) { r = symStack.pop().expr(); l = symStack.pop().expr(); symStack.push(new ObjHolder(pow(l, r))); }
							else if(n.value.equalsIgnoreCase("abs")) { l = symStack.pop().expr(); symStack.push(new ObjHolder(abs(l))); }
							else if(n.value.equalsIgnoreCase("diff")) { r = symStack.pop().expr(); l = symStack.pop().expr(); symStack.push(new ObjHolder(l.diff(r))); }
							else if(n.value.equalsIgnoreCase("integrate")) {
								ObjHolder domainHolder = (ObjHolder)symStack.pop();
								Expr integrand = symStack.pop().expr();
								Expr a = Integrate.apply(integrand, (Domain)(domainHolder.obj));
								symStack.push(new ObjHolder(a)); 
							}
							//TODO
							//else if(n.value.equalsIgnoreCase("domain")) { l = es.pop(); es.push(sin(l)); }
							else if(n.value.equalsIgnoreCase("interval")) {
								SymReal<?> step = (SymReal<?>)symStack.pop().expr();
								Expr end = symStack.pop().expr();
								Expr start = symStack.pop().expr();
								Expr coordVar = symStack.pop().expr();
								Domain domain = Interval.apply(start, end, coordVar).
										setStepSize(coordVar, step.getDoubleValue());
								symStack.push(new ObjHolder(domain)); 
								}
							else if(n.value.equalsIgnoreCase("eq")) {
								n = (Ident)t;
								if(n.argc == 2) {
									r = symStack.pop().expr();
									l = symStack.pop().expr();
									Eq eq = new Eq(l,r);
									symStack.push(new ObjHolder(eq));
								} else if(n.argc == 3) {
									ObjHolder freeVarsHolder = (ObjHolder)symStack.pop();
									r = symStack.pop().expr();
									l = symStack.pop().expr();
									Eq eq = new Eq(l,r,(Expr[])(freeVarsHolder.obj));
									symStack.push(new ObjHolder(eq));
									
								} else if(n.argc == 4) {
									ObjHolder paramsHoleder = (ObjHolder)symStack.pop();
									ObjHolder freeVarsHolder = (ObjHolder)symStack.pop();
									r = symStack.pop().expr();
									l = symStack.pop().expr();
									Eq eq = new Eq(l,r,(Expr[])(freeVarsHolder.obj), (Expr[])(paramsHoleder.obj));
									symStack.push(new ObjHolder(eq));
								} else {
									throw new RuntimeException("Wrong number of arguments of eq()");
								}
							}
							else if(n.value.equalsIgnoreCase("array")) {
								Expr[] args = new Expr[n.argc];
								for(int i=0; i<n.argc; i++) {
									args[n.argc-i-1] = symStack.pop().expr();
								}
								symStack.push(new ObjHolder(getArrayType(args), args)); 
							} else { //
								ObjHolder[] args = new ObjHolder[n.argc];
								for(int i=0; i<n.argc; i++) {
									args[n.argc-i-1] = symStack.pop();
								}
								invokeMethod(n.value, args);
							}
							break;
						case Token.T_POPEN:      // (
							break;
						case Token.T_PCLOSE:     // )
							break;
						case Token.T_RIDENT:     // right identity
							n = (Ident)t;
							symStack.push(new ObjHolder(new Symbol(n.value)));
							System.out.println("define local: "+n.value);
							break;
						case Token.T_STRING:     // string
							throw new RuntimeException("String is not supported!");
						case Token.T_OPERATOR:   // operator
							break;
						case Token.T_PLUS:       // +
							r = symStack.pop().expr();
							l = symStack.pop().expr();
							symStack.push(new ObjHolder(l+r));
							break;
						case Token.T_MINUS:      // -
							r = symStack.pop().expr();
							l = symStack.pop().expr();
							symStack.push(new ObjHolder(l-r));
							break;
						case Token.T_TIMES:      // * 
							r = symStack.pop().expr();
							l = symStack.pop().expr();
							symStack.push(new ObjHolder(l*r));
							break;
						case Token.T_DIV:        // /
							r = symStack.pop().expr();
							l = symStack.pop().expr();
							symStack.push(new ObjHolder(l/r));
							break;
						case Token.T_MOD:        // %
							r = symStack.pop().expr();
							l = symStack.pop().expr();
							symStack.push(new ObjHolder(l%r));
							break;
						case Token.T_POW:        // ^
							r = symStack.pop().expr();
							l = symStack.pop().expr();
							symStack.push(new ObjHolder(pow(l,r)));
							break;
						case Token.T_UNARY_PLUS: // + (determined at compile time) as a sign
							break;
						case Token.T_UNARY_MINUS:// - (determined at compile time) as a sign
							l = symStack.pop().expr();
							symStack.push(new ObjHolder(-l));
							break;
						case Token.T_NOT:        // ! as a sign
							break;
						case Token.T_SEMI:       // ;
							break;
						case Token.T_COMMA:      // ,
							break;
						case Token.T_ASSIGN:     // =
							r = symStack.pop().expr();
							l = symStack.pop().expr();
							ctx.put(l.toString(), r);
							symStack.push(new ObjHolder(l));
							break;
						default:
							break;
					}
				}
			}
			return symStack.pop().expr();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public String getArrayType(Expr[] ary) {
		boolean isIntAry = true;
		boolean isDoubleAry = true;
		for(Expr e : ary) {
			if(!(e instanceof SymInteger)) isIntAry = false;
			if(!(e instanceof SymDouble)) isDoubleAry = false;
		}
		if(isIntAry) return "[I";
		if(isDoubleAry) return "[D";
		return "[E";
	}
	
	protected double[] invokeMethod(String name, ObjHolder[] args) {
		String[] splits = name.split("\\.");
		String className = Utils.joinLabels(splits, 0, splits.length-1, ".");
		String methodName = splits[splits.length-1];
		Method method;
		try {
			Class<?>[] argClass = new Class[args.length];
			for(int i=0; i<argClass.length; i++)
				argClass[i] = ParamUtils.getClass(args[i].type);
			method = this.getClass().getClassLoader().loadClass(className)
					.getMethod(methodName, argClass);
			Object[] invokeArgs = new Object[argClass.length];
			for(int i=0; i<args.length; i++) {
				invokeArgs[i] = args[i].getJavaObj();
			}
			double[] invokeRlt = (double[])method.invoke(null, invokeArgs);
			return invokeRlt;
		} catch(Exception e) {
			e.printStackTrace();
		}
		return null;
	}
}
