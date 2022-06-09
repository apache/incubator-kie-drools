package org.optaplanner.quarkus.deployment;

import java.util.Arrays;
import java.util.List;

import org.jboss.jandex.DotName;
import org.optaplanner.core.api.domain.constraintweight.ConstraintConfigurationProvider;
import org.optaplanner.core.api.domain.constraintweight.ConstraintWeight;
import org.optaplanner.core.api.domain.entity.PlanningEntity;
import org.optaplanner.core.api.domain.entity.PlanningPin;
import org.optaplanner.core.api.domain.lookup.PlanningId;
import org.optaplanner.core.api.domain.solution.PlanningEntityCollectionProperty;
import org.optaplanner.core.api.domain.solution.PlanningEntityProperty;
import org.optaplanner.core.api.domain.solution.PlanningScore;
import org.optaplanner.core.api.domain.solution.PlanningSolution;
import org.optaplanner.core.api.domain.solution.ProblemFactCollectionProperty;
import org.optaplanner.core.api.domain.solution.ProblemFactProperty;
import org.optaplanner.core.api.domain.valuerange.ValueRangeProvider;
import org.optaplanner.core.api.domain.variable.AnchorShadowVariable;
import org.optaplanner.core.api.domain.variable.CustomShadowVariable;
import org.optaplanner.core.api.domain.variable.IndexShadowVariable;
import org.optaplanner.core.api.domain.variable.InverseRelationShadowVariable;
import org.optaplanner.core.api.domain.variable.PlanningListVariable;
import org.optaplanner.core.api.domain.variable.PlanningVariable;
import org.optaplanner.core.api.domain.variable.PlanningVariableReference;
import org.optaplanner.core.api.score.calculator.EasyScoreCalculator;
import org.optaplanner.core.api.score.calculator.IncrementalScoreCalculator;
import org.optaplanner.core.api.score.stream.ConstraintProvider;

public final class DotNames {

    static final DotName PLANNING_SOLUTION = DotName.createSimple(PlanningSolution.class.getName());
    static final DotName PLANNING_ENTITY_COLLECTION_PROPERTY =
            DotName.createSimple(PlanningEntityCollectionProperty.class.getName());
    static final DotName PLANNING_ENTITY_PROPERTY = DotName.createSimple(PlanningEntityProperty.class.getName());
    static final DotName PLANNING_SCORE = DotName.createSimple(PlanningScore.class.getName());
    static final DotName PROBLEM_FACT_COLLECTION_PROPERTY = DotName.createSimple(ProblemFactCollectionProperty.class.getName());
    static final DotName PROBLEM_FACT_PROPERTY = DotName.createSimple(ProblemFactProperty.class.getName());

    static final DotName EASY_SCORE_CALCULATOR = DotName.createSimple(EasyScoreCalculator.class.getName());
    static final DotName CONSTRAINT_PROVIDER = DotName.createSimple(ConstraintProvider.class.getName());
    static final DotName INCREMENTAL_SCORE_CALCULATOR =
            DotName.createSimple(IncrementalScoreCalculator.class.getName());
    static final DotName CONSTRAINT_CONFIGURATION_PROVIDER =
            DotName.createSimple(ConstraintConfigurationProvider.class.getName());
    static final DotName CONSTRAINT_WEIGHT = DotName.createSimple(ConstraintWeight.class.getName());

    static final DotName PLANNING_ENTITY = DotName.createSimple(PlanningEntity.class.getName());
    static final DotName PLANNING_PIN = DotName.createSimple(PlanningPin.class.getName());
    static final DotName PLANNING_ID = DotName.createSimple(PlanningId.class.getName());

    static final DotName PLANNING_VARIABLE = DotName.createSimple(PlanningVariable.class.getName());
    static final DotName PLANNING_LIST_VARIABLE = DotName.createSimple(PlanningListVariable.class.getName());
    static final DotName PLANNING_VARIABLE_REFERENCE = DotName.createSimple(PlanningVariableReference.class.getName());
    static final DotName VALUE_RANGE_PROVIDER = DotName.createSimple(ValueRangeProvider.class.getName());

    static final DotName ANCHOR_SHADOW_VARIABLE = DotName.createSimple(AnchorShadowVariable.class.getName());
    static final DotName CUSTOM_SHADOW_VARIABLE = DotName.createSimple(CustomShadowVariable.class.getName());
    static final DotName INDEX_SHADOW_VARIABLE = DotName.createSimple(IndexShadowVariable.class.getName());
    static final DotName INVERSE_RELATION_SHADOW_VARIABLE = DotName.createSimple(InverseRelationShadowVariable.class.getName());

    // Need to use String since optaplanner-test is not on the compile classpath
    static final DotName CONSTRAINT_VERIFIER = DotName.createSimple("org.optaplanner.test.api.score.stream.ConstraintVerifier");

    static final DotName[] PLANNING_ENTITY_FIELD_ANNOTATIONS = {
            PLANNING_PIN,
            PLANNING_VARIABLE,
            PLANNING_LIST_VARIABLE,
            ANCHOR_SHADOW_VARIABLE,
            CUSTOM_SHADOW_VARIABLE,
            INDEX_SHADOW_VARIABLE,
            INVERSE_RELATION_SHADOW_VARIABLE,
    };

    static final DotName[] GIZMO_MEMBER_ACCESSOR_ANNOTATIONS = {
            PLANNING_ENTITY_COLLECTION_PROPERTY,
            PLANNING_ENTITY_PROPERTY,
            PLANNING_SCORE,
            PROBLEM_FACT_COLLECTION_PROPERTY,
            PROBLEM_FACT_PROPERTY,
            CONSTRAINT_CONFIGURATION_PROVIDER,
            CONSTRAINT_WEIGHT,
            PLANNING_PIN,
            PLANNING_ID,
            PLANNING_VARIABLE,
            PLANNING_LIST_VARIABLE,
            PLANNING_VARIABLE_REFERENCE,
            VALUE_RANGE_PROVIDER,
            ANCHOR_SHADOW_VARIABLE,
            CUSTOM_SHADOW_VARIABLE,
            INDEX_SHADOW_VARIABLE,
            INVERSE_RELATION_SHADOW_VARIABLE,
    };

    public enum BeanDefiningAnnotations {
        PLANNING_SCORE(DotNames.PLANNING_SCORE, "scoreDefinitionClass"),
        PLANNING_SOLUTION(DotNames.PLANNING_SOLUTION, "solutionCloner"),
        PLANNING_ENTITY(DotNames.PLANNING_ENTITY, "pinningFilter", "difficultyComparatorClass",
                "difficultyWeightFactoryClass"),
        PLANNING_VARIABLE(DotNames.PLANNING_VARIABLE, "strengthComparatorClass",
                "strengthWeightFactoryClass"),
        CUSTOM_SHADOW_VARIABLE(DotNames.CUSTOM_SHADOW_VARIABLE, "variableListenerClass");

        private final DotName annotationDotName;
        private final List<String> parameterNames;

        BeanDefiningAnnotations(DotName annotationDotName, String... parameterNames) {
            this.annotationDotName = annotationDotName;
            this.parameterNames = Arrays.asList(parameterNames);
        }

        public DotName getAnnotationDotName() {
            return annotationDotName;
        }

        public List<String> getParameterNames() {
            return parameterNames;
        }
    }

    private DotNames() {
    }

}
