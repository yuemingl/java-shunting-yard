package test;

import static symjava.math.SymMath.exp;
import static symjava.math.SymMath.log;
import static symjava.math.SymMath.pow;
import static symjava.math.SymMath.sqrt;
import static symjava.symbolic.Symbol.C0;
import static symjava.symbolic.Symbol.z;
import symjava.domains.Domain;
import symjava.domains.Interval;
import symjava.examples.Newton;
import symjava.relational.Eq;
import symjava.symbolic.Expr;
import symjava.symbolic.Integrate;
import symjava.symbolic.SymConst;
import symjava.symbolic.Symbol;

public class Test {
	public static void main(String[] args) {
		ShuntingYardParser parser = new ShuntingYardParser();
		
		Expr expr = parser.parse("e");
		//Expr expr = parse("1-(x+sin(x))^2");
		//Expr expr = parse("1-(x+integrate(2*y,interval(y,0,1,100)))^(2+x)");
		System.out.println(expr);
		System.out.println(expr.diff(new symjava.symbolic.Symbol("x")));
		
		Expr res = getBlackScholes();
		System.out.println(res);
		
		String strRes = res.toString();
		Expr exprRes = parser.parse(strRes);
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
		Expr exprEq = parser.parse(eq[0].toString());
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
	

}
