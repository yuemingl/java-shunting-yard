package test;

import static symjava.math.SymMath.abs;
import static symjava.math.SymMath.cos;
import static symjava.math.SymMath.exp;
import static symjava.math.SymMath.log;
import static symjava.math.SymMath.log10;
import static symjava.math.SymMath.log2;
import static symjava.math.SymMath.pow;
import static symjava.math.SymMath.sin;
import static symjava.math.SymMath.sqrt;
import static symjava.math.SymMath.tan;
import static symjava.symbolic.Symbol.C0;
import static symjava.symbolic.Symbol.z;

import java.util.Stack;

import symjava.domains.Domain;
import symjava.domains.Interval;
import symjava.examples.Newton;
import symjava.math.SymMath;
import symjava.relational.Eq;
import symjava.symbolic.Expr;
import symjava.symbolic.Integrate;
import symjava.symbolic.SymConst;
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

public class Test {
	public static void Test() {
		try {
			Context ctx = new Context();
			Parser p;
			p = new Parser(new Scanner("2*PI"));
			//p.reduce(ctx);
			System.out.println(p.opcode()); //This will clear the stack
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	public static void main(String[] args) {
		//Test();
		Expr expr = parse("e");
		//Expr expr = parse("1-(x+sin(x))^2");
		//Expr expr = parse("1-(x+integrate(2*y,interval(y,0,1,100)))^(2+x)");
		System.out.println(expr);
		System.out.println(expr.diff(new symjava.symbolic.Symbol("x")));
		
		Expr res = getBlackScholes();
		System.out.println(res);
		
		String strRes = res.toString();
		Expr exprRes = parse(strRes);
		System.out.println(exprRes);
		
		
		Symbol spot = new Symbol("spot"); //spot price
		Symbol strike = new Symbol("strike"); //strike price
		Symbol rd = new Symbol("rd");
		Symbol rf = new Symbol("rf");
		Symbol vol = new Symbol("sigma"); //volatility
		Symbol tau = new Symbol("tau");
		Symbol phi = new Symbol("phi");
		Expr[] freeVars = {vol};
		Expr[] params = {spot, strike, rd, rf, tau, phi};
		Eq[] eq = new Eq[] { new Eq(exprRes-0.897865, C0, freeVars, params) };

		System.out.println(">>>"+eq[0].toString());
		Expr exprEq = parse(eq[0].toString());
		System.out.println(">>>"+exprEq);

		// Use Newton's method to find the root
		double[] guess = new double[]{ 0.10 };
		double[] constParams = new double[] {100.0, 110.0, 0.002, 0.01, 0.5, 1};
		Newton.solve(eq, guess, constParams, 100, 1e-5);
		
		Newton.solve(new Expr[]{exprEq}, guess, constParams, 100, 1e-5);
	}

	public static Expr getBlackScholes() {
		// Define symbols to construct the Black-Scholes formula
		Symbol spot = new Symbol("spot"); //spot price
		Symbol strike = new Symbol("strike"); //strike price
		Symbol rd = new Symbol("rd");
		Symbol rf = new Symbol("rf");
		Symbol vol = new Symbol("sigma"); //volatility
		Symbol tau = new Symbol("tau");
		Symbol phi = new Symbol("phi");
		SymConst PI2 = new SymConst("2*pi", 2*Math.PI);
		
		Expr domDf = exp(-rd*tau); 
		Expr forDf = exp(-rf*tau);
		Expr fwd=spot*forDf/domDf;
		Expr stdDev=vol*sqrt(tau);
		//We use -10 instead of -oo for numerical computation
		double step = 1e-3;
		Domain I1 = Interval.apply(-10, phi*(log(fwd/strike)+0.5*pow(stdDev,2))/stdDev, z)
				.setStepSize(step); 
		Domain I2 = Interval.apply(-10, phi*(log(fwd/strike)-0.5*pow(stdDev,2))/stdDev, z)
				.setStepSize(step); 
		Expr cdf1 = Integrate.apply(exp(-0.5*pow(z,2)), I1)/sqrt(PI2);
		Expr cdf2 = Integrate.apply(exp(-0.5*pow(z,2)), I2)/sqrt(PI2);
		Expr res = phi*domDf*(fwd*cdf1-strike*cdf2);
		return res;
	}
	
	public static class DummyFunction implements Function {
		public RSymbol call(RSymbol[] args) {
			return args[0];
		}		
	}
	
	public static class ExprHolder extends Expr {
		public Object obj;
		public ExprHolder(Object obj) {
			this.obj = obj;
		}
		
		@Override
		public Expr simplify() {
			return null;
		}
		@Override
		public boolean symEquals(Expr other) {
			return false;
		}
		@Override
		public Expr[] args() {
			return null;
		}
		@Override
		public Expr diff(Expr x) {
			return null;
		}

		@Override
		public TYPE getType() {
			return null;
		}
	}
	
	public static Expr parse(String expr) {
		try {
			Context ctx = new Context();
			
			//See class Context for pi, e
			
			ctx.setFunction("sqrt", new DummyFunction());
			ctx.setFunction("sin", new DummyFunction());
			ctx.setFunction("cos", new DummyFunction());
			ctx.setFunction("tan", new DummyFunction());
			ctx.setFunction("log", new DummyFunction());
			ctx.setFunction("log2", new DummyFunction());
			ctx.setFunction("log10", new DummyFunction());
			ctx.setFunction("pow", new DummyFunction());
			ctx.setFunction("abs", new DummyFunction());
			ctx.setFunction("diff", new DummyFunction());
			ctx.setFunction("integrate", new DummyFunction());
			ctx.setFunction("domain", new DummyFunction());
			ctx.setFunction("interval", new DummyFunction());
			ctx.setFunction("eq", new DummyFunction());
			ctx.setFunction("array", new DummyFunction());
			
			Parser p = new Parser(new Scanner(expr));
			//System.out.println(p.opcode()); //This will clear the stack
			Stack<TokenStack> st = p.getTokenStacks();
			while(!st.empty()) {
				TokenStack ts = st.pop();
				Token t = null;
				Stack<Expr> symStack = new Stack<Expr>();
				Expr l,r;
				while(ts.peek() != null) {
					t = ts.next();
					switch(t.type) {
						case Token.T_NUMBER:     // eine nummer (integer / double)
							if(t instanceof RNumber) {
								RNumber n = (RNumber)t;
								symStack.push(Expr.valueOf(n.value));
							}
							break;
						case Token.T_IDENT:      // konstante
							if(t instanceof Ident) {
								Ident n = (Ident)t;
								if(n.value.equalsIgnoreCase("e")) {
									symStack.push(SymMath.E);
								} else if(n.value.equalsIgnoreCase("PI")) {
									symStack.push(SymMath.PI);
								} else {
									symStack.push(new Symbol(n.value));
								}
							}
							break;
						case Token.T_FUNCTION:   // funktion
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
							//else if(n.value.equalsIgnoreCase("domain")) { l = es.pop(); es.push(sin(l)); }
							else if(n.value.equalsIgnoreCase("interval")) {
								SymReal step = (SymReal)symStack.pop();
								Expr end = symStack.pop();
								Expr start = symStack.pop();
								Expr coordVar = symStack.pop();
								Domain domain = Interval.apply(start, end, coordVar).
										setStepSize(coordVar, step.getDoubleValue());
								symStack.push(new ExprHolder(domain)); 
								}
							else if(n.value.equalsIgnoreCase("eq")) {
								ExprHolder paramsHoleder = (ExprHolder)symStack.pop();
								ExprHolder freeVarsHolder = (ExprHolder)symStack.pop();
								r = symStack.pop();
								l = symStack.pop();
								Eq eq = new Eq(l,r,(Expr[])(freeVarsHolder.obj), (Expr[])(paramsHoleder.obj));
								symStack.push(eq); 
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
						case Token.T_RIDENT:     // ident vor = (R -> right)
							break;
						case Token.T_STRING:     // string
							break;
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
						case Token.T_UNARY_PLUS: // + als vorzeichen (zur �bersetzungszeit ermittelt)
							break;
						case Token.T_UNARY_MINUS:// - als vorzeichen (zur �bersetzungszeit ermittelt)
							l = symStack.pop();
							symStack.push(-l);
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
				Expr rlt = symStack.pop();
				return rlt;
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

}
