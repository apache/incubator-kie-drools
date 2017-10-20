package org.drools.pmml.pmml_4_2.model.mining;

import java.util.List;

public interface PredicateRuleProducer {
	public String getPredicateRule();
	public List<String> getPredicateFieldNames();
	public List<String> getFieldMissingFieldNames();
}
