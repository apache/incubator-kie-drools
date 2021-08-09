package org.optaplanner.core.impl.score.director.stream;

import java.util.Map;
import java.util.Objects;

import org.drools.model.Global;
import org.kie.api.KieBase;
import org.optaplanner.core.impl.score.inliner.WeightedScoreImpacter;
import org.optaplanner.core.impl.score.stream.drools.DroolsConstraint;

final class KieBaseDescriptor<Solution_> {

    private final Map<DroolsConstraint<Solution_>, Global<WeightedScoreImpacter>> constraintToGlobalMap;
    private final KieBase kieBase;

    public KieBaseDescriptor(Map<DroolsConstraint<Solution_>, Global<WeightedScoreImpacter>> constraintToGlobalMap,
            KieBase kieBase) {
        this.constraintToGlobalMap = Objects.requireNonNull(constraintToGlobalMap);
        this.kieBase = Objects.requireNonNull(kieBase);
    }

    public Map<DroolsConstraint<Solution_>, Global<WeightedScoreImpacter>> getConstraintToGlobalMap() {
        return constraintToGlobalMap;
    }

    public KieBase getKieBase() {
        return kieBase;
    }

}
