import java.util.function.Function;

import org.drools.core.SessionConfiguration;
import org.drools.core.common.ReteEvaluator;
import org.drools.core.impl.RuleBase;
import org.drools.modelcompiler.KieBaseBuilder;
import org.drools.ruleunits.api.RuleUnit;
import org.drools.ruleunits.api.conf.RuleConfig;
import org.drools.ruleunits.api.RuleUnits;
import org.drools.ruleunits.impl.factory.AbstractRuleUnit;
import org.drools.ruleunits.impl.factory.AbstractRuleUnits;
import org.drools.ruleunits.impl.ReteEvaluatorBasedRuleUnitInstance;
import org.drools.ruleunits.impl.sessions.RuleUnitExecutorImpl;

@javax.enterprise.context.ApplicationScoped
public class CLASS_NAME extends AbstractRuleUnit<RULE_UNIT_CLASS> {

    private static final RuleBase ruleBase = KieBaseBuilder.createKieBaseFromModel( new RULE_UNIT_MODEL() );

    private static final SessionConfiguration sessionConfiguration = ruleBase.getSessionConfiguration();

    static {
        sessionConfiguration.setClockType($ClockType$);
    }

    public CLASS_NAME() {
        this((RuleUnits) null);
    }

    @javax.inject.Inject
    public CLASS_NAME(javax.enterprise.inject.Instance<RuleUnits> ruleUnits) {
        this(ruleUnits == null || ruleUnits.isUnsatisfied() ? AbstractRuleUnits.DummyRuleUnits.INSTANCE : ruleUnits.get());
    }

    public CLASS_NAME(RuleUnits ruleUnits) {
        super(RULE_UNIT_CLASS.class, ruleUnits);
        this.ruleUnits.register(this);
    }

    @Override
    public RULE_UNIT_INSTANCE_CLASS internalCreateInstance(RULE_UNIT_CLASS data, RuleConfig ruleConfig) {
        ReteEvaluator reteEvaluator = evaluatorConfigurator.apply(new RuleUnitExecutorImpl(ruleBase, sessionConfiguration));
        return new RULE_UNIT_INSTANCE_CLASS(this, data, reteEvaluator, ruleConfig);
    }
}