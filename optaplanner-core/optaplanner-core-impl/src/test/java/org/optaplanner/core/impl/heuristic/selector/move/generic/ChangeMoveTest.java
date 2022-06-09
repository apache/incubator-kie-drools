package org.optaplanner.core.impl.heuristic.selector.move.generic;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.optaplanner.core.impl.testdata.util.PlannerAssert.assertCode;
import static org.optaplanner.core.impl.testdata.util.PlannerTestUtils.mockRebasingScoreDirector;

import java.util.Arrays;

import org.junit.jupiter.api.Test;
import org.optaplanner.core.api.score.buildin.simple.SimpleScore;
import org.optaplanner.core.api.score.director.ScoreDirector;
import org.optaplanner.core.impl.domain.variable.descriptor.GenuineVariableDescriptor;
import org.optaplanner.core.impl.score.director.ScoreDirectorFactory;
import org.optaplanner.core.impl.score.director.easy.EasyScoreDirectorFactory;
import org.optaplanner.core.impl.testdata.domain.TestdataEntity;
import org.optaplanner.core.impl.testdata.domain.TestdataSolution;
import org.optaplanner.core.impl.testdata.domain.TestdataValue;
import org.optaplanner.core.impl.testdata.domain.multivar.TestdataMultiVarEntity;
import org.optaplanner.core.impl.testdata.domain.multivar.TestdataMultiVarSolution;
import org.optaplanner.core.impl.testdata.domain.multivar.TestdataOtherValue;
import org.optaplanner.core.impl.testdata.domain.valuerange.entityproviding.TestdataEntityProvidingEntity;
import org.optaplanner.core.impl.testdata.domain.valuerange.entityproviding.TestdataEntityProvidingSolution;

class ChangeMoveTest {

    @Test
    void isMoveDoable() {
        TestdataValue v1 = new TestdataValue("1");
        TestdataValue v2 = new TestdataValue("2");
        TestdataValue v3 = new TestdataValue("3");

        TestdataEntityProvidingEntity a = new TestdataEntityProvidingEntity("a", Arrays.asList(v1, v2, v3), null);

        ScoreDirector<TestdataEntityProvidingSolution> scoreDirector = mock(ScoreDirector.class);

        GenuineVariableDescriptor<TestdataEntityProvidingSolution> variableDescriptor =
                TestdataEntityProvidingEntity.buildVariableDescriptorForValue();

        ChangeMove<TestdataEntityProvidingSolution> aMove = new ChangeMove<>(variableDescriptor, a, v2);
        a.setValue(v1);
        assertThat(aMove.isMoveDoable(scoreDirector)).isTrue();

        a.setValue(v2);
        assertThat(aMove.isMoveDoable(scoreDirector)).isFalse();

        a.setValue(v3);
        assertThat(aMove.isMoveDoable(scoreDirector)).isTrue();
    }

    @Test
    void doMove() {
        TestdataValue v1 = new TestdataValue("1");
        TestdataValue v2 = new TestdataValue("2");
        TestdataValue v3 = new TestdataValue("3");

        TestdataEntityProvidingEntity a = new TestdataEntityProvidingEntity("a", Arrays.asList(v1, v2, v3), null);

        ScoreDirectorFactory<TestdataEntityProvidingSolution> scoreDirectorFactory =
                new EasyScoreDirectorFactory<>(TestdataEntityProvidingSolution.buildSolutionDescriptor(),
                        solution -> SimpleScore.ZERO);
        ScoreDirector<TestdataEntityProvidingSolution> scoreDirector = scoreDirectorFactory.buildScoreDirector();

        GenuineVariableDescriptor<TestdataEntityProvidingSolution> variableDescriptor =
                TestdataEntityProvidingEntity.buildVariableDescriptorForValue();

        ChangeMove<TestdataEntityProvidingSolution> aMove = new ChangeMove<>(variableDescriptor, a, v2);
        a.setValue(v1);
        aMove.doMove(scoreDirector);
        assertThat(a.getValue()).isEqualTo(v2);

        a.setValue(v2);
        aMove.doMove(scoreDirector);
        assertThat(a.getValue()).isEqualTo(v2);

        a.setValue(v3);
        aMove.doMove(scoreDirector);
        assertThat(a.getValue()).isEqualTo(v2);
    }

    @Test
    void rebase() {
        GenuineVariableDescriptor<TestdataSolution> variableDescriptor = TestdataEntity.buildVariableDescriptorForValue();

        TestdataValue v1 = new TestdataValue("v1");
        TestdataValue v2 = new TestdataValue("v2");
        TestdataEntity e1 = new TestdataEntity("e1", v1);
        TestdataEntity e2 = new TestdataEntity("e2", null);
        TestdataEntity e3 = new TestdataEntity("e3", v1);

        TestdataValue destinationV1 = new TestdataValue("v1");
        TestdataValue destinationV2 = new TestdataValue("v2");
        TestdataEntity destinationE1 = new TestdataEntity("e1", destinationV1);
        TestdataEntity destinationE2 = new TestdataEntity("e2", null);
        TestdataEntity destinationE3 = new TestdataEntity("e3", destinationV1);

        ScoreDirector<TestdataSolution> destinationScoreDirector = mockRebasingScoreDirector(
                variableDescriptor.getEntityDescriptor().getSolutionDescriptor(), new Object[][] {
                        { v1, destinationV1 },
                        { v2, destinationV2 },
                        { e1, destinationE1 },
                        { e2, destinationE2 },
                        { e3, destinationE3 },
                });

        assertSameProperties(destinationE1, null,
                new ChangeMove<>(variableDescriptor, e1, null).rebase(destinationScoreDirector));
        assertSameProperties(destinationE1, destinationV1,
                new ChangeMove<>(variableDescriptor, e1, v1).rebase(destinationScoreDirector));
        assertSameProperties(destinationE2, null,
                new ChangeMove<>(variableDescriptor, e2, null).rebase(destinationScoreDirector));
        assertSameProperties(destinationE3, destinationV2,
                new ChangeMove<>(variableDescriptor, e3, v2).rebase(destinationScoreDirector));
    }

    public void assertSameProperties(Object entity, Object toPlanningVariable, ChangeMove<?> move) {
        assertThat(move.getEntity()).isSameAs(entity);
        assertThat(move.getToPlanningValue()).isSameAs(toPlanningVariable);
    }

    @Test
    void getters() {
        GenuineVariableDescriptor<TestdataMultiVarSolution> primaryVariableDescriptor =
                TestdataMultiVarEntity.buildVariableDescriptorForPrimaryValue();
        ChangeMove<TestdataMultiVarSolution> primaryMove =
                new ChangeMove<>(primaryVariableDescriptor, new TestdataMultiVarEntity("a"), null);
        assertCode("a", primaryMove.getEntity());
        assertThat(primaryMove.getVariableName()).isEqualTo("primaryValue");
        assertCode(null, primaryMove.getToPlanningValue());

        GenuineVariableDescriptor<TestdataMultiVarSolution> secondaryVariableDescriptor =
                TestdataMultiVarEntity.buildVariableDescriptorForSecondaryValue();
        ChangeMove<TestdataMultiVarSolution> secondaryMove =
                new ChangeMove<>(secondaryVariableDescriptor, new TestdataMultiVarEntity("b"), new TestdataValue("1"));
        assertCode("b", secondaryMove.getEntity());
        assertThat(secondaryMove.getVariableName()).isEqualTo("secondaryValue");
        assertCode("1", secondaryMove.getToPlanningValue());
    }

    @Test
    void toStringTest() {
        TestdataValue v1 = new TestdataValue("v1");
        TestdataValue v2 = new TestdataValue("v2");
        TestdataEntity a = new TestdataEntity("a", null);
        TestdataEntity b = new TestdataEntity("b", v1);
        GenuineVariableDescriptor<TestdataSolution> variableDescriptor = TestdataEntity.buildVariableDescriptorForValue();

        assertThat(new ChangeMove<>(variableDescriptor, a, null).toString()).isEqualTo("a {null -> null}");
        assertThat(new ChangeMove<>(variableDescriptor, a, v1).toString()).isEqualTo("a {null -> v1}");
        assertThat(new ChangeMove<>(variableDescriptor, a, v2).toString()).isEqualTo("a {null -> v2}");
        assertThat(new ChangeMove<>(variableDescriptor, b, null).toString()).isEqualTo("b {v1 -> null}");
        assertThat(new ChangeMove<>(variableDescriptor, b, v1).toString()).isEqualTo("b {v1 -> v1}");
        assertThat(new ChangeMove<>(variableDescriptor, b, v2).toString()).isEqualTo("b {v1 -> v2}");
    }

    @Test
    void toStringTestMultiVar() {
        TestdataValue v1 = new TestdataValue("v1");
        TestdataValue v2 = new TestdataValue("v2");
        TestdataValue v3 = new TestdataValue("v3");
        TestdataValue v4 = new TestdataValue("v4");
        TestdataOtherValue w1 = new TestdataOtherValue("w1");
        TestdataOtherValue w2 = new TestdataOtherValue("w2");
        TestdataMultiVarEntity a = new TestdataMultiVarEntity("a", null, null, null);
        TestdataMultiVarEntity b = new TestdataMultiVarEntity("b", v1, v3, w1);
        TestdataMultiVarEntity c = new TestdataMultiVarEntity("c", v2, v4, w2);
        GenuineVariableDescriptor<TestdataMultiVarSolution> variableDescriptor =
                TestdataMultiVarEntity.buildVariableDescriptorForSecondaryValue();

        assertThat(new ChangeMove<>(variableDescriptor, a, null).toString()).isEqualTo("a {null -> null}");
        assertThat(new ChangeMove<>(variableDescriptor, a, v1).toString()).isEqualTo("a {null -> v1}");
        assertThat(new ChangeMove<>(variableDescriptor, a, v2).toString()).isEqualTo("a {null -> v2}");
        assertThat(new ChangeMove<>(variableDescriptor, b, null).toString()).isEqualTo("b {v3 -> null}");
        assertThat(new ChangeMove<>(variableDescriptor, b, v1).toString()).isEqualTo("b {v3 -> v1}");
        assertThat(new ChangeMove<>(variableDescriptor, b, v2).toString()).isEqualTo("b {v3 -> v2}");
        assertThat(new ChangeMove<>(variableDescriptor, c, v3).toString()).isEqualTo("c {v4 -> v3}");
    }

}
