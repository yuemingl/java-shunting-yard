package test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import symjava.symbolic.Expr;

public class Test3 {

	public static void main(String[] args) {

		try {
			ShuntingYardParser parser = new ShuntingYardParser();
			BufferedReader br = new BufferedReader(new InputStreamReader(
					System.in));

			String input;

			while ((input = br.readLine()) != null) {
				Expr expr = parser.parse(input);
				System.out.println(">>"+expr);
			}

		} catch (IOException io) {
			io.printStackTrace();
		}
	}

}
