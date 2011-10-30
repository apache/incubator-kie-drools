package ruleml.translator.ruleml2drl;

import java.util.List;

import reactionruleml.QueryType;
import ruleml.translator.ruleml2drl.DroolsBuilder.Query;

public class QueryProcessor extends RuleMLGenericProcessor {

	public void processQuery(QueryType queryType) {
		List<Object> formulaOrRulebaseOrAtom = queryType
				.getFormulaOrRulebaseOrAtom();

		Query query = new Query();

		for (Object o : formulaOrRulebaseOrAtom) {
			// forward
			translator.dispatchType(o);

			// add the current rule to the list with rules
			query.setRuleName("query" + ruleNumber++);
			if (translator.getWhenPatterns().isEmpty()) {
				throw new IllegalArgumentException(
						"Restrict condition can not be empty");
			} else {
				query.setWhenPart(translator.getWhenPatterns().toArray());
				translator.getDrl().addQuery(query);

				// reset the patterns
				translator.getWhenPatterns().clear();
				translator.getThenPatterns().clear();
			}
		}
	}
}
