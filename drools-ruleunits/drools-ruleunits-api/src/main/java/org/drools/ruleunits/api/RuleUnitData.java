package org.drools.ruleunits.api;

/**
 * A marker interface that has to be implemented by a POJO defining the set of data belonging to and used by a {@link RuleUnit}.
 * All fields of the implementing POJO that are instance of {@link DataSource} are equivalent to typed {@link org.kie.api.runtime.rule.EntryPoint}s
 * through which inserting (and update and remove when allowed) the facts on which the rule engine will attempt a pattern matching.
 * All other fields are equivalent to {@link org.kie.api.definition.rule.Global} for this rule unit.
 */
public interface RuleUnitData {

}
