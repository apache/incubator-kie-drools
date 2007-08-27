package org.drools.analytics.accumulateFunction;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.drools.analytics.components.LiteralRestriction;
import org.drools.base.accumulators.AccumulateFunction;



/**
 * 
 * @author Toni Rikkola
 */
public class ValidatePattern implements AccumulateFunction {

	private static final int MIN_NUMBER_OF_CONSTRAINTS = 4;
	private static List<LiteralRestriction> restrictions = new ArrayList<LiteralRestriction>();

	public Object createContext() {
		return restrictions;
	}

	public void init(Object context) throws Exception {
		// constraints = new ArrayList<Constraint>();
	}

	public void accumulate(Object context, Object value) {
		List<LiteralRestriction> list = (List<LiteralRestriction>) context;

		list.add((LiteralRestriction) value);
	}

	public Object getResult(Object context) throws Exception {
		List<LiteralRestriction> list = (List<LiteralRestriction>) context;
		ValidatePatternResult result = new ValidatePatternResult();

		if (list != null && list.size() >= MIN_NUMBER_OF_CONSTRAINTS) {
			result.setValue(testForPattern(list));
		}

		return result;
	}

	public void reverse(Object context, Object value) throws Exception {
		// TODO Auto-generated method stub

	}

	public boolean supportsReverse() {
		return false;
	}

	/**
	 * Test if the values in constraints are in pattern.
	 * 
	 * @param constraints
	 * @return false if can't find a pattern or constraints list is null or size
	 *         of the list is under 3.
	 */
	protected static Number testForPattern(List<LiteralRestriction> constraints) {

		if (constraints == null || constraints.size() < 4) {
			return null;
		}

		BigDecimal[] numbers = new BigDecimal[constraints.size()];

		int index = 0;
		for (LiteralRestriction restriction : restrictions) {
			numbers[index++] = BigDecimal.valueOf(restriction.getDoubleValue());
		}

		Arrays.sort(numbers);

		Number missingNumber = findSumPattern(numbers);
		if (missingNumber != null) {
			return missingNumber;
		} else {
			missingNumber = findMultiplicationPattern(numbers);
			if (missingNumber != null) {
				return missingNumber;
			}
		}

		return null;
	}

	/**
	 * Looks for sum pattern, on each step x is added or removed. -x is the same
	 * as +(-x) so this works for both.
	 * 
	 * @param numbers
	 * @return true if pattern is found.
	 */
	protected static Number findSumPattern(BigDecimal[] numbers) {
		if (numbers == null || numbers.length < MIN_NUMBER_OF_CONSTRAINTS) {
			return null;
		}
		BigDecimal gap = null;
		Number missingNumber = null;

		BigDecimal a = numbers[0];
		BigDecimal b = numbers[1];
		BigDecimal c = numbers[2];
		BigDecimal d = numbers[3];

		// Uses first four numbers to check if there is a pattern and to
		// calculate the gap between them. One missing value is allowed.
		if (b.subtract(a).equals(c.subtract(b))) {
			gap = b.subtract(a);
		} else if (c.subtract(b).equals(d.subtract(c))) {
			gap = c.subtract(b);
		} else if (b.subtract(a).equals(d.subtract(c))) {
			gap = b.subtract(a);
		} else {
			// No pattern found.
			return null;
		}

		for (int i = 0; i < (numbers.length - 1); i++) {
			BigDecimal first = numbers[i];
			BigDecimal second = numbers[i + 1];

			if (missingNumber == null && !second.subtract(first).equals(gap)) {
				missingNumber = second.subtract(gap);
			} else if (!second.subtract(first).equals(gap)
					&& missingNumber != null) {
				// Happends if there is no pattern found, or more than 1
				// missing number.
				return null;
			}
		}

		return missingNumber;
	}

	/**
	 * Looks for multiplication pattern, on each step x multiplied or divided.
	 * *x is the same as *(1/x) so this works for both.
	 * 
	 * @param numbers
	 * @return true if pattern is found.
	 */
	protected static Number findMultiplicationPattern(BigDecimal[] numbers) {
		if (numbers == null || numbers.length < MIN_NUMBER_OF_CONSTRAINTS) {
			return null;
		}
		try {

			BigDecimal gap = null;
			Number missingNumber = null;

			BigDecimal a = numbers[0];
			BigDecimal b = numbers[1];
			BigDecimal c = numbers[2];
			BigDecimal d = numbers[3];

			// Uses first four numbers to check if there is a pattern and to
			// calculate the gap between them. One missing value is allowed.
			if (b.divide(a).equals(c.divide(b))) {
				gap = b.divide(a);
			} else if (c.divide(b).equals(d.divide(c))) {
				gap = c.divide(b);
			} else if (b.divide(a).equals(d.divide(c))) {
				gap = b.divide(a);
			} else {
				// No pattern found.
				return null;
			}

			BigDecimal first = null;
			BigDecimal second = null;
			for (int i = 0; i < (numbers.length - 1); i++) {
				first = numbers[i];
				second = numbers[i + 1];

				if (missingNumber == null && !second.divide(first).equals(gap)) {
					missingNumber = first.multiply(gap);
				} else if (!second.divide(first).equals(gap)
						&& missingNumber != null) {
					// Happends if there is no pattern found, or more than 1
					// missing number.
					return null;
				}
			}
			return missingNumber;
		} catch (Exception e) {
			return null;
		}
	}
}
