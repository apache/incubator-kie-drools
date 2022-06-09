package org.optaplanner.constraint.drl;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Function;

import org.kie.api.KieBase;
import org.kie.api.definition.KiePackage;
import org.kie.api.definition.rule.Global;
import org.kie.api.definition.rule.Rule;
import org.kie.api.runtime.KieSession;
import org.optaplanner.core.api.domain.constraintweight.ConstraintWeight;
import org.optaplanner.core.api.domain.solution.PlanningSolution;
import org.optaplanner.core.api.score.Score;
import org.optaplanner.core.impl.domain.constraintweight.descriptor.ConstraintConfigurationDescriptor;
import org.optaplanner.core.impl.domain.constraintweight.descriptor.ConstraintWeightDescriptor;
import org.optaplanner.core.impl.domain.solution.descriptor.SolutionDescriptor;
import org.optaplanner.core.impl.score.director.AbstractScoreDirectorFactory;
import org.optaplanner.core.impl.score.director.ScoreDirectorFactory;

/**
 * Drools implementation of {@link ScoreDirectorFactory}.
 *
 * @param <Solution_> the solution type, the class with the {@link PlanningSolution} annotation
 * @param <Score_> the score type to go with the solution
 * @see DrlScoreDirector
 * @see ScoreDirectorFactory
 */
public class DrlScoreDirectorFactory<Solution_, Score_ extends Score<Score_>>
        extends AbstractScoreDirectorFactory<Solution_, Score_> {

    private final KieBase kieBase;

    protected Map<Rule, Function<Solution_, Score_>> ruleToConstraintWeightExtractorMap;

    /**
     * @param solutionDescriptor never null
     * @param kieBase never null
     */
    public DrlScoreDirectorFactory(SolutionDescriptor<Solution_> solutionDescriptor, KieBase kieBase) {
        super(solutionDescriptor);
        this.kieBase = kieBase;
        assertGlobalScoreHolderExists(kieBase);
        createRuleToConstraintWeightExtractorMap(kieBase);
        solutionDescriptor.assertProblemFactsExist();
    }

    protected void assertGlobalScoreHolderExists(KieBase kieBase) {
        boolean hasGlobalScoreHolder = false;
        for (KiePackage kiePackage : kieBase.getKiePackages()) {
            for (Global global : kiePackage.getGlobalVariables()) {
                if (DrlScoreDirector.GLOBAL_SCORE_HOLDER_KEY.equals(global.getName())) {
                    hasGlobalScoreHolder = true;
                    // TODO Fail fast once global.getType() can be turned into a Class instead of a String
                    //                    if (!ScoreHolder.class.isAssignableFrom(global.getType())) {
                    //                        throw new IllegalStateException("The global with name (" + global.getName()
                    //                                + ") has a type (" + global.getType()
                    //                                + ") that does not implement " + ScoreHolder.class.getSimpleName() + ".");
                    //                    }
                    break;
                }
            }
        }
        if (!hasGlobalScoreHolder) {
            throw new IllegalArgumentException("The kieBase with kiePackages (" + kieBase.getKiePackages()
                    + ") has no global field called " + DrlScoreDirector.GLOBAL_SCORE_HOLDER_KEY + ".\n"
                    + "Check if the rule files are found and if the global field is spelled correctly.");
        }
    }

    protected void createRuleToConstraintWeightExtractorMap(KieBase kieBase) {
        ConstraintConfigurationDescriptor<Solution_> constraintConfigurationDescriptor = solutionDescriptor
                .getConstraintConfigurationDescriptor();
        if (constraintConfigurationDescriptor == null) {
            ruleToConstraintWeightExtractorMap = new LinkedHashMap<>(0);
            return;
        }
        Collection<ConstraintWeightDescriptor<Solution_>> constraintWeightDescriptors = constraintConfigurationDescriptor
                .getConstraintWeightDescriptors();
        ruleToConstraintWeightExtractorMap = new LinkedHashMap<>(constraintWeightDescriptors.size());
        for (ConstraintWeightDescriptor<Solution_> constraintWeightDescriptor : constraintWeightDescriptors) {
            String constraintPackage = constraintWeightDescriptor.getConstraintPackage();
            String constraintName = constraintWeightDescriptor.getConstraintName();
            Rule rule = kieBase.getRule(constraintPackage, constraintName);
            if (rule == null) {
                Rule potentialRule = kieBase.getKiePackages().stream().flatMap(kiePackage -> kiePackage.getRules().stream())
                        .filter(selectedRule -> selectedRule.getName().equals(constraintName)).findFirst().orElse(null);
                throw new IllegalStateException("The constraintConfigurationClass ("
                        + constraintConfigurationDescriptor.getConstraintConfigurationClass()
                        + ") has a @" + ConstraintWeight.class.getSimpleName()
                        + " annotated member (" + constraintWeightDescriptor.getMemberAccessor()
                        + ") with constraintPackage/rulePackage (" + constraintPackage
                        + ") and constraintName/ruleName (" + constraintName
                        + ") for which no Drools rule exist in the DRL.\n"
                        + (potentialRule != null ? "Maybe the constraintPackage (" + constraintPackage + ") is wrong,"
                                + " because there is a rule with the same ruleName (" + constraintName
                                + "), but in a different rulePackage (" + potentialRule.getPackageName() + ")."
                                : "Maybe there is a typo in the constraintName (" + constraintName
                                        + ") so it not identical to the constraint's ruleName."));
            }
            Function<Solution_, Score_> constraintWeightExtractor =
                    (Function<Solution_, Score_>) constraintWeightDescriptor.createExtractor();
            ruleToConstraintWeightExtractorMap.put(rule, constraintWeightExtractor);
        }
    }

    public Map<Rule, Function<Solution_, Score_>> getRuleToConstraintWeightExtractorMap() {
        return ruleToConstraintWeightExtractorMap;
    }

    // ************************************************************************
    // Complex methods
    // ************************************************************************

    @Override
    public DrlScoreDirector<Solution_, Score_> buildScoreDirector(
            boolean lookUpEnabled, boolean constraintMatchEnabledPreference) {
        return new DrlScoreDirector<>(this, lookUpEnabled, constraintMatchEnabledPreference);
    }

    public KieSession newKieSession() {
        return kieBase.newKieSession();
    }

}
