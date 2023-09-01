package org.drools.base.ruleunit;

import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.drools.base.definitions.rule.impl.RuleImpl;
import org.drools.base.definitions.InternalKnowledgePackage;
import org.kie.internal.ruleunit.RuleUnitComponentFactory;
import org.kie.internal.ruleunit.RuleUnitDescription;

public class RuleUnitDescriptionLoader {

    private RuleUnitDescriptionRegistry.State state = RuleUnitDescriptionRegistry.State.UNKNOWN;

    private transient final InternalKnowledgePackage pkg;
    private final Map<String, RuleUnitDescription> ruleUnitDescriptionsCache = new ConcurrentHashMap<>();
    private final Set<String> nonExistingUnits = new HashSet<>();

    public RuleUnitDescriptionLoader(InternalKnowledgePackage pkg) {
        this.pkg = pkg;
    }

    public RuleUnitDescriptionRegistry.State getState() {
        return state;
    }

    public Map<String, RuleUnitDescription> getDescriptions() {
        return ruleUnitDescriptionsCache;
    }

    public Optional<RuleUnitDescription> getDescription(final RuleImpl rule) {
        return getDescription(rule.getRuleUnitClassName());
    }

    public Optional<RuleUnitDescription> getDescription(final String unitClassName) {
        final Optional<RuleUnitDescription> result = Optional.ofNullable(unitClassName)
                .map(name -> ruleUnitDescriptionsCache.computeIfAbsent(name, this::findDescription));
        state = state.hasUnit(result.isPresent());
        return result;
    }

    private RuleUnitDescription findDescription(final String ruleUnit) {
        if (nonExistingUnits.contains(ruleUnit)) {
            return null;
        }
        RuleUnitComponentFactory ruleUnitComponentFactory = RuleUnitComponentFactory.get();
        // short-circuit if there is no support for units
        if (ruleUnitComponentFactory == null) {
            return null;
        }
        try {
            return ruleUnitComponentFactory.createRuleUnitDescription(pkg, pkg.getTypeResolver().resolveType(ruleUnit) );
        } catch (final ClassNotFoundException e) {
            RuleUnitDescription ruleUnitDescription = ruleUnitComponentFactory.createRuleUnitDescription(pkg, ruleUnit);
            if (ruleUnitDescription == null) {
                nonExistingUnits.add(ruleUnit);
                return null;
            } else {
                return ruleUnitDescription;
            }
        }
    }
}
