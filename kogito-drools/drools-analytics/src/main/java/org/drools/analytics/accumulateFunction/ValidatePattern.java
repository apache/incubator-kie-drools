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

	private static List<LiteralRestriction> restrictions;

	public Object createContext() {
		return restrictions;
	}

	public void init(Object context) throws Exception {
		restrictions = new ArrayList<LiteralRestriction>();
	}

	public void accumulate(Object context, Object value) {
		List<LiteralRestriction> list = (List<LiteralRestriction>) context;

		list.add((LiteralRestriction) value);
	}

	public Object getResult(Object context) throws Exception {
		List<LiteralRestriction> list = (List<LiteralRestriction>) context;
		ValidatePatternResult result = new ValidatePatternResult();

		result.setValue(FindMissingNumber.testForPattern(list));

		return result;
	}

	public void reverse(Object context, Object value) throws Exception {
		// TODO Auto-generated method stub

	}

	public boolean supportsReverse() {
		return false;
	}
}
