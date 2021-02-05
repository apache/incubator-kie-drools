package $Package$;

public class RuleUnits extends org.kie.kogito.rules.units.impl.AbstractRuleUnits {

    private final Application application;

    public RuleUnits(Application application) {
        this.application = application;
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
