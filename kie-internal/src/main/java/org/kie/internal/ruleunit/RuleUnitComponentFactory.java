package org.kie.internal.ruleunit;

import org.kie.api.definition.KiePackage;
import org.kie.api.internal.utils.KieService;

public interface RuleUnitComponentFactory extends KieService {
    class FactoryHolder {
        private static final RuleUnitComponentFactory factory = KieService.load(RuleUnitComponentFactory.class);
    }

    static RuleUnitComponentFactory get() {
        return FactoryHolder.factory;
    }

    RuleUnitDescription createRuleUnitDescription( KiePackage pkg, Class<?> ruleUnitClass );

    /**
     * Creates a rule unit description from the given qualified name.
     * Optional operation (may be provided by alternative implementations)
     * @return null if not supported or missing.
     */
    RuleUnitDescription createRuleUnitDescription( KiePackage pkg, String ruleUnitSimpleName );

    boolean isRuleUnitClass( Class<?> ruleUnitClass );
    boolean isDataSourceClass( Class<?> ruleUnitClass );
}
