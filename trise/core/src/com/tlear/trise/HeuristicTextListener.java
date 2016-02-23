package com.tlear.trise;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.function.BiFunction;

import com.badlogic.gdx.Input.TextInputListener;
import com.tlear.trise.objects.Agent;
import com.tlear.trise.utils.Utils;

public class HeuristicTextListener implements TextInputListener {

	private Agent agent;

	public HeuristicTextListener(Agent a) {
		agent = a;
		System.out.println("===========================================");
		System.out.println("Agent for Heuristic Function");
		System.out.println(agent);
		System.out.println("===========================================");
	}

	@Override
	public void input(String text) {
		// TODO Auto-generated method stub
		Optional<List<String>> heuristicFunction = parseHeuristic(text);

		heuristicFunction.ifPresent(x -> agent.setHeuristic(x));

		if (heuristicFunction.isPresent()) {
			System.out.println("Parsed heuristic to "
					+ heuristicFunction.get().toString());
		} else {
			System.out.println("Could not parse heuristic function");
		}
	}

	@Override
	public void canceled() {
		// TODO Auto-generated method stub

	}

	private Optional<List<String>> parseHeuristic(String raw) {
		// There are three variables we will want our heuristic to have:
		// * Position of node = pos
		// * Goal position = goalPos
		// * Distance to node = g(x)
		//
		// We want to determine h(x) = some combination of the above
		// using +,-,*,/,^ and numbers

		// Syntax -- we split around whitespace, because whitespace is useful.
		String[] raw_split = raw.split(" ");
		// We then check syntactic validity of the input
		if (!checkSyntax(raw_split)) {
			return Optional.empty();
		}

		// Semantics -- we have the expression as a list and replace by the
		// order
		// ^, /, *, +, - until we have a single value.
		LinkedList<String> expr = new LinkedList<>();
		for (int i = 0; i < raw_split.length; i++) {
			expr.add(raw_split[i]);
		}
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
					double result = f.apply(eval(x), eval(y));
					if (Utils.isNumeric(x) && Utils.isNumeric(y)) {
						// Simplify if numerics
						expr.set(j, Double.toString(result));
						expr.remove(j - 1);
						expr.remove(j);
					}

					System.out.println(expr.toString());
				}
			}
		}

		return Optional.of(expr);
	}

	private double eval(String expr) {
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
			return 1.0;
		case "goalPos":
			return 1.0;
		case "g(x)":
			return 1.0;
		default:
			throw new RuntimeException("Operand is an unrecognised expression");
		}
	}

	/**
	 * Checks whether the given array is full of valid parts.
	 * 
	 * @param raw_split
	 * @return
	 */
	private boolean checkSyntax(String[] raw_split) {
		boolean valid = true;
		for (int i = 0; i < raw_split.length && valid; i++) {
			String part = raw_split[i];
			if (Utils.isNumeric(part)) {
				// This is fine
			} else {
				// We check this thing's length. If it's 1 then it's a maths op
				if (part.length() == 1) {
					// It should be a maths op. Identify which.
					switch (part) {
					case "+":
						// addition!
						break;
					case "-":
						// subtraction!
						break;
					case "*":
						// multiplication!
						break;
					case "/":
						// division!
						break;
					case "%":
						// modulo!
						break;
					case "^":
						// exponentiation!
						break;
					default:
						valid = false;
					}
				} else {
					// The part is longer than 1, so we expect it to be a
					// variable
					switch (part) {
					case "pos":
						// pos
						break;
					case "goalPos":
						// goalPos
						break;
					case "g(x)":
						// g(x)
						break;
					default:
						valid = false;
					}
				}
			}
		}
		return valid;
	}

}
