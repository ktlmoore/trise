package com.tlear.trise.functions;

import java.util.List;
import java.util.function.BiFunction;

import com.badlogic.gdx.math.Vector2;
import com.tlear.trise.environment.Environment;
import com.tlear.trise.graph.Node;
import com.tlear.trise.utils.Utils;

public class UserDefinedHeuristicFunction implements HeuristicFunction {

	private Environment env;
	private List<String> expr;

	public UserDefinedHeuristicFunction(List<String> functionAsList,
			Environment env) {
		this.env = env;
		expr = functionAsList;
	}

	private double parseFunction(Environment env, Node<Vector2> t) {
		// Then we go through for each operator.
		String[] ops = { "^", "/", "*", "+", "-" };
		for (int i = 0; i < ops.length; i++) {
			System.out.println(ops[i]);
			for (int j = 0; j < expr.size(); j++) {
				System.out.println(String.format(" > %s", expr.get(j)));
				if (expr.get(j).equals(ops[i])) {
					// If we find the op we're looking for, we replace the
					// operator with the op of its left and right operators
					BiFunction<Double, Double, Double> f;
					switch (ops[i]) {
					case "^":
						f = (x, y) -> Math.pow(x, y);
						break;
					case "/":
						f = (x, y) -> x / y;
						break;
					case "*":
						f = (x, y) -> x * y;
						break;
					case "+":
						f = (x, y) -> x + y;
						break;
					case "-":
						f = (x, y) -> x - y;
						break;
					default:
						throw new RuntimeException(
								"Problem with operator: operator is undefined");
					}

					if (j + 1 >= expr.size()) {
						throw new RuntimeException(
								"Expected right hand of expression but could not find this.");
					}
					if (j - 1 < 0) {
						throw new RuntimeException(
								"Expected left hand of expression but could not find this.");
					}
					String x = expr.get(j - 1);
					String y = expr.get(j + 1);
					double result = f.apply(eval(x, env, t), eval(y, env, t));

					expr.set(j, Double.toString(result));
					expr.remove(j - 1);
					expr.remove(j);

					System.out.println(expr.toString());
				}
			}
		}
		return Double.parseDouble(expr.get(0));
	}

	/**
	 * Evaluate a given expression for its value
	 * 
	 * @param expr
	 * @return
	 */
	private double eval(String expr, Environment env, Node<Vector2> t) {
		System.out.println(String.format(" >> %s", expr));
		if (Utils.isNumeric(expr)) {
			return Double.parseDouble(expr);
		}
		if (expr.length() < 1) {
			throw new RuntimeException("Null operand");
		}
		if (expr.length() == 1) {
			throw new RuntimeException("Operand cannot be an operator");
		}
		switch (expr) {
		case "pos":
			return 0.0;
		case "goalPos":
			return t.getValue().dst(env.goals.getFirst().pos);
		case "g(x)":
			return t.getDistanceTo();
		default:
			throw new RuntimeException("Operand is an unrecognised expression");
		}
	}

	@Override
	public Float apply(Node<Vector2> t) {
		float result = (float) parseFunction(env, t);
		t.updateDistanceTo(result);
		return result;
	}
}
