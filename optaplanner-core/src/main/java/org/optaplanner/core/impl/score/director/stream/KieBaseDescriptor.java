package org.optaplanner.core.impl.score.director.stream;

import java.util.Map;
import java.util.Objects;
import java.util.function.Supplier;

import org.drools.model.Global;
import org.kie.api.KieBase;
import org.optaplanner.core.impl.score.inliner.WeightedScoreImpacter;
import org.optaplanner.core.impl.score.stream.drools.DroolsConstraint;

public final class KieBaseDescriptor<Solution_> implements Supplier<KieBase> {

    private final Map<DroolsConstraint<Solution_>, Global<WeightedScoreImpacter>> constraintToGlobalMap;
    private final KieBase kieBase;

    KieBaseDescriptor(Map<DroolsConstraint<Solution_>, Global<WeightedScoreImpacter>> constraintToGlobalMap,
            KieBase kieBase) {
        this.constraintToGlobalMap = Objects.requireNonNull(constraintToGlobalMap);
        this.kieBase = Objects.requireNonNull(kieBase);
    }

    public Map<DroolsConstraint<Solution_>, Global<WeightedScoreImpacter>> getConstraintToGlobalMap() {
        return constraintToGlobalMap;
    }

    @Override
    public KieBase get() {
        return kieBase;
    }

}
