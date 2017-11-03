package org.drools.pmml.pmml_4_2.model.mining;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.dmg.pmml.pmml_4_2.descr.CompoundPredicate;
import org.dmg.pmml.pmml_4_2.descr.SimplePredicate;
import org.dmg.pmml.pmml_4_2.descr.SimpleSetPredicate;

public class CompoundSegmentPredicate implements PredicateRuleProducer {
	private String booleanOperator;
	private List<PredicateRuleProducer> subpredicates;

	public CompoundSegmentPredicate() {
		subpredicates = new ArrayList<>();
	}
	
	public CompoundSegmentPredicate(CompoundPredicate predicate) {
		subpredicates = new ArrayList<>();
		for (Serializable serializable : predicate.getSimplePredicatesAndCompoundPredicatesAndSimpleSetPredicates()) {
			if (serializable instanceof SimplePredicate) {
				subpredicates.add(new SimpleSegmentPredicate((SimplePredicate)serializable));
			} else if (serializable instanceof SimpleSetPredicate) {
				subpredicates.add(new SimpleSetSegmentPredicate((SimpleSetPredicate)serializable));
			} else if (serializable instanceof CompoundPredicate) {
				subpredicates.add(new CompoundSegmentPredicate((CompoundPredicate)serializable));
			}
		}
		booleanOperator = predicate.getBooleanOperator();
		if (booleanOperator == null) {
			throw new IllegalStateException("PMML-CompoundPredicate: Missing the booleanOperator attribute");
		}
		if (!booleanOperator.equalsIgnoreCase("and") &&
			!booleanOperator.equalsIgnoreCase("or") &&
			!booleanOperator.equalsIgnoreCase("xor") &&
			!booleanOperator.equalsIgnoreCase("surrogate")) {
			throw new IllegalStateException("PMML-CompoundPredicate: Invalid value ("+booleanOperator+") for the booleanOperator attribute");
		}
	}
	
	private String buildAndPredicate() {
		StringBuilder bldr = new StringBuilder("(");
		boolean firstPredicate = true;
		for (PredicateRuleProducer ruleProducer : subpredicates) {
			if (!firstPredicate) {
				bldr.append(" && ");
			} else {
				firstPredicate = false;
			}
			bldr.append("(").append(ruleProducer.getPredicateRule()).append(")");
		}
		bldr.append(")");
		return bldr.toString();
	}
	
	private String buildXorPredicate() {
		StringBuilder bldr = new StringBuilder("(");
		boolean firstPredicate = true;
		for (PredicateRuleProducer ruleProducer : subpredicates) {
			if (!firstPredicate) {
				bldr.append(" ^ ");
			} else {
				firstPredicate = false;
			}
			bldr.append("(").append(ruleProducer.getPredicateRule()).append(")");
		}
		bldr.append(")");
		return bldr.toString();
	}
	
	private String buildOrPredicate() {
		StringBuilder bldr = new StringBuilder("(");
		boolean firstPredicate = true;
		for (PredicateRuleProducer ruleProducer : subpredicates) {
			if (!firstPredicate) {
				bldr.append(" || ");
			} else {
				firstPredicate = false;
			}
			bldr.append("(").append(ruleProducer.getPredicateRule()).append(")");
		}
		bldr.append(")");
		return bldr.toString();
	}
	
	public List<String> getPredicateFieldNames() {
		List<String> fieldNames = new ArrayList<>();
		for (PredicateRuleProducer ruleProducer : subpredicates) {
			fieldNames.addAll(ruleProducer.getPredicateFieldNames());
		}
		return fieldNames;
	}
	
	public List<String> getFieldMissingFieldNames() {
		List<String> fieldNames = new ArrayList<>();
		for (PredicateRuleProducer ruleProducer: subpredicates) {
			fieldNames.addAll(ruleProducer.getFieldMissingFieldNames());
		}
		return fieldNames;
	}
	
	public String getPrimaryPredicateRule() {
		if (this.booleanOperator.equalsIgnoreCase("surrogate")) {
			PredicateRuleProducer ruleProducer = subpredicates.get(0);
			if (ruleProducer != null) {
				return ruleProducer.getPredicateRule();
			}
		} else {
			throw new IllegalStateException("PMML-CompoundPredicate: Primary predicate is only available when operator is \"surrogate\"");
		}
		return null;
	}
	
	public String getNextPredicateRule(int lastPredicate) {
		if (booleanOperator.equalsIgnoreCase("surrogate")) {
			int index = lastPredicate + 1;
			List<String> fieldNames = new ArrayList<>();
			for (int counter = 0; counter < index; counter++) {
				List<String> fn = subpredicates.get(counter).getFieldMissingFieldNames();
				if (fn != null && !fn.isEmpty()) {
					fieldNames.addAll(fn);
				}
			}
			if (!fieldNames.isEmpty()) {
				StringBuilder bldr = new StringBuilder("( (");
				boolean firstFieldName = true;
				for (String fldName : fieldNames) {
					if (!firstFieldName) {
						bldr.append(", ");
					} else {
						firstFieldName = false;
					}
					bldr.append(fldName).append("==true");
				}
				bldr.append(") && ");
				PredicateRuleProducer ruleProducer = subpredicates.get(index);
				if (ruleProducer != null) {
					bldr.append("( ").append(ruleProducer.getPredicateRule()).append(" )");
				}
				bldr.append(" )");
				return bldr.toString();
			}
		} else {
			throw new IllegalStateException("PMML-CompoundPredicate: Sub-predicates are only available when operator is \"surrogate\"");
		}
		return null;
	}
	
	public int getSubpredicateCount() {
		return subpredicates.size()-1;
	}
	
	public boolean hasSurrogation() {
		return this.booleanOperator != null && this.booleanOperator.equalsIgnoreCase("surrogate");
	}
	
	private String buildSurrogationPredicate() {
		StringBuilder builder = new StringBuilder();
		String predicate = this.getPrimaryPredicateRule();
		builder.append("( ").append(predicate).append(" )");
		for (int lastPred = 0; lastPred < getSubpredicateCount(); lastPred++) {
			predicate = this.getNextPredicateRule(lastPred);
			if (predicate != null) {
				builder.append(" || ( ").append(predicate).append(" )");
			}
		}
		
		return builder.toString();
	}
	
	@Override
	public String getPredicateRule() {
		if (booleanOperator.equalsIgnoreCase("and")) {
			return buildAndPredicate();
		} else if (booleanOperator.equalsIgnoreCase("or")) {
			return buildOrPredicate();
		} else if (booleanOperator.equalsIgnoreCase("xor")) {
			return buildXorPredicate();
		} else if (hasSurrogation()) {
			return buildSurrogationPredicate();
		}
		return null;
	}

}
