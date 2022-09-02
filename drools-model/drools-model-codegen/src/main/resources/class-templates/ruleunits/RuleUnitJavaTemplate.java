import org.drools.core.common.ReteEvaluator;
import org.drools.core.impl.RuleBase;
import org.drools.modelcompiler.KieBaseBuilder;
import org.drools.ruleunits.api.RuleUnit;
import org.drools.ruleunits.impl.factory.AbstractRuleUnit;
import org.drools.ruleunits.impl.ReteEvaluatorBasedRuleUnitInstance;
import org.drools.ruleunits.impl.sessions.RuleUnitExecutorImpl;

public class CLASS_NAME extends AbstractRuleUnit<RULE_UNIT_CLASS> {

    private static final RuleBase ruleBase = KieBaseBuilder.createKieBaseFromModel( new RULE_UNIT_MODEL() );

    public CLASS_NAME() {
        super(RULE_UNIT_CLASS.class.getCanonicalName());
    }

    @Override
    public RULE_UNIT_INSTANCE_CLASS internalCreateInstance(RULE_UNIT_CLASS data) {
        ReteEvaluator reteEvaluator = new RuleUnitExecutorImpl(ruleBase);
        return new RULE_UNIT_INSTANCE_CLASS(this, data, reteEvaluator);
    }
}