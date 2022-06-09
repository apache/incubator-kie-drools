package org.optaplanner.core.impl.testdata.domain.shadow.order;

import org.optaplanner.core.api.domain.entity.PlanningEntity;
import org.optaplanner.core.api.domain.variable.CustomShadowVariable;
import org.optaplanner.core.api.domain.variable.PlanningVariable;
import org.optaplanner.core.api.domain.variable.PlanningVariableReference;
import org.optaplanner.core.impl.domain.entity.descriptor.EntityDescriptor;
import org.optaplanner.core.impl.testdata.domain.DummyVariableListener;
import org.optaplanner.core.impl.testdata.domain.TestdataObject;
import org.optaplanner.core.impl.testdata.domain.TestdataValue;

@PlanningEntity
public class TestdataShadowVariableOrderEntity extends TestdataObject {

    public static EntityDescriptor<TestdataShadowVariableOrderSolution> buildEntityDescriptor() {
        return TestdataShadowVariableOrderSolution.buildSolutionDescriptor()
                .findEntityDescriptorOrFail(TestdataShadowVariableOrderEntity.class);
    }

    /*
     * The variables correspond to the scenario described in shadowVariableOrder.png. The last letter matches the variable name
     * on the diagram. 'xN' is an artificial prefix to force an order in which the fields are iterated that is different from
     * the alphabetical order of the original variable names (which, coincidentally, is the expected order of variable
     * listeners).
     */

    /**
     * G -> F
     */
    @CustomShadowVariable(variableListenerRef = @PlanningVariableReference(variableName = "x4F"))
    private String x0G;

    /**
     * D -> C
     */
    @CustomShadowVariable(variableListenerClass = DVariableListener.class,
            sources = @PlanningVariableReference(variableName = "x3C"))
    private String x1D;

    /**
     * E -> {B, C}
     */
    @CustomShadowVariable(variableListenerClass = EVariableListener.class,
            sources = { @PlanningVariableReference(variableName = "x5B"), @PlanningVariableReference(variableName = "x3C") })
    private String x2E;

    /**
     * C -> A
     */
    @CustomShadowVariable(variableListenerClass = CVariableListener.class,
            sources = @PlanningVariableReference(variableName = "x6A"))
    private String x3C;

    /**
     * F -> E
     */
    @CustomShadowVariable(variableListenerClass = FGVariableListener.class,
            sources = @PlanningVariableReference(variableName = "x2E"))
    private String x4F;

    @PlanningVariable(valueRangeProviderRefs = "valueRange")
    private TestdataValue x5B;
    @PlanningVariable(valueRangeProviderRefs = "valueRange")
    private TestdataValue x6A;

    public TestdataShadowVariableOrderEntity() {
    }

    public TestdataShadowVariableOrderEntity(String code) {
        super(code);
    }

    // ************************************************************************
    // Static inner classes
    // ************************************************************************

    abstract static class VariableListenerWithToString
            extends DummyVariableListener<TestdataShadowVariableOrderSolution, TestdataShadowVariableOrderEntity> {

        @Override
        public String toString() {
            return getClass().getSimpleName().replace("VariableListener", "");
        }
    }

    public static class CVariableListener extends VariableListenerWithToString {
    }

    public static class DVariableListener extends VariableListenerWithToString {
    }

    public static class EVariableListener extends VariableListenerWithToString {
    }

    public static class FGVariableListener extends VariableListenerWithToString {
    }
}
