import java.util.function.Function;

import org.drools.core.SessionConfiguration;
import org.drools.core.common.ReteEvaluator;
import org.drools.core.impl.RuleBase;
import org.drools.modelcompiler.KieBaseBuilder;
import org.drools.ruleunits.api.RuleUnit;
import org.drools.ruleunits.impl.RuleUnits;
import org.drools.ruleunits.impl.factory.AbstractRuleUnit;
import org.drools.ruleunits.impl.ReteEvaluatorBasedRuleUnitInstance;
import org.drools.ruleunits.impl.sessions.RuleUnitExecutorImpl;

public class CLASS_NAME extends AbstractRuleUnit<RULE_UNIT_CLASS> {

    private static final RuleBase ruleBase = KieBaseBuilder.createKieBaseFromModel( new RULE_UNIT_MODEL() );

    private static final SessionConfiguration sessionConfiguration = ruleBase.getSessionConfiguration();

    static {
        sessionConfiguration.setClockType($ClockType$);
    }

    private final Function<ReteEvaluator, ReteEvaluator> evaluatorConfigurator;

    public CLASS_NAME() {
        this(null, Function.identity());
    }

    public CLASS_NAME(RuleUnits ruleUnits, Function<ReteEvaluator, ReteEvaluator> evaluatorConfigurator) {
        super(RULE_UNIT_CLASS.class, ruleUnits);
        this.evaluatorConfigurator = evaluatorConfigurator;
    }

    @Override
    public RULE_UNIT_INSTANCE_CLASS internalCreateInstance(RULE_UNIT_CLASS data) {
        ReteEvaluator reteEvaluator = evaluatorConfigurator.apply(new RuleUnitExecutorImpl(ruleBase, sessionConfiguration));
        return new RULE_UNIT_INSTANCE_CLASS(this, data, reteEvaluator);
    }
}