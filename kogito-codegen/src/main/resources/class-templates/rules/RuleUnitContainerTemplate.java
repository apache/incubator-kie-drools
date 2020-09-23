package $Package$;

public class RuleUnits extends org.kie.kogito.rules.units.impl.AbstractRuleUnits {

    private final Application application;

    private final org.kie.kogito.rules.KieRuntimeBuilder ruleRuntimeBuilder = new org.drools.project.model.ProjectRuntime();

    public RuleUnits(Application application) {
        this.application = application;
    }

    public org.kie.kogito.rules.KieRuntimeBuilder ruleRuntimeBuilder() {
        return this.ruleRuntimeBuilder;
    }

    protected org.kie.kogito.rules.RuleUnit<?> create(String fqcn) {
        switch(fqcn) {
            case "$RuleUnit$":
                return new $RuleUnit$RuleUnit(application);
            default:
                throw new java.lang.UnsupportedOperationException();
        }
    }
}
