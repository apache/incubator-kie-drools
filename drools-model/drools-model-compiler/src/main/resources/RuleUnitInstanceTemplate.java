import org.drools.core.common.ReteEvaluator;
import org.drools.ruleunits.api.RuleUnit;
import org.drools.ruleunits.impl.ReteEvaluatorBasedRuleUnitInstance;

public class CLASS_NAME extends ReteEvaluatorBasedRuleUnitInstance<RULE_UNIT_CLASS> {

    public CLASS_NAME(RuleUnit<RULE_UNIT_CLASS> unit, RULE_UNIT_CLASS workingMemory, ReteEvaluator reteEvaluator) {
        super(unit, workingMemory, reteEvaluator);
    }

    @Override
    protected void bind(ReteEvaluator evaluator, RULE_UNIT_CLASS ruleUnit) {
    }
}