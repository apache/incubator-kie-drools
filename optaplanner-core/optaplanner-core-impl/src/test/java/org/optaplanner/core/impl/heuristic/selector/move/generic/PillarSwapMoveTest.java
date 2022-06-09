package org.optaplanner.core.impl.heuristic.selector.move.generic;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.optaplanner.core.impl.testdata.util.PlannerAssert.assertAllCodesOfCollection;
import static org.optaplanner.core.impl.testdata.util.PlannerTestUtils.mockRebasingScoreDirector;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.optaplanner.core.api.score.buildin.simple.SimpleScore;
import org.optaplanner.core.api.score.director.ScoreDirector;
import org.optaplanner.core.impl.domain.entity.descriptor.EntityDescriptor;
import org.optaplanner.core.impl.domain.variable.descriptor.GenuineVariableDescriptor;
import org.optaplanner.core.impl.score.director.ScoreDirectorFactory;
import org.optaplanner.core.impl.score.director.easy.EasyScoreDirectorFactory;
import org.optaplanner.core.impl.testdata.domain.TestdataEntity;
import org.optaplanner.core.impl.testdata.domain.TestdataSolution;
import org.optaplanner.core.impl.testdata.domain.TestdataValue;
import org.optaplanner.core.impl.testdata.domain.multivar.TestdataMultiVarEntity;
import org.optaplanner.core.impl.testdata.domain.multivar.TestdataMultiVarSolution;
import org.optaplanner.core.impl.testdata.domain.valuerange.entityproviding.TestdataEntityProvidingEntity;
import org.optaplanner.core.impl.testdata.domain.valuerange.entityproviding.TestdataEntityProvidingSolution;

class PillarSwapMoveTest {

    @Test
    void isMoveDoableValueRangeProviderOnEntity() {
        TestdataValue v1 = new TestdataValue("1");
        TestdataValue v2 = new TestdataValue("2");
        TestdataValue v3 = new TestdataValue("3");
        TestdataValue v4 = new TestdataValue("4");
        TestdataValue v5 = new TestdataValue("5");

        TestdataEntityProvidingEntity a = new TestdataEntityProvidingEntity("a", Arrays.asList(v1, v2, v3), null);
        TestdataEntityProvidingEntity b = new TestdataEntityProvidingEntity("b", Arrays.asList(v2, v3, v4, v5), null);
        TestdataEntityProvidingEntity c = new TestdataEntityProvidingEntity("c", Arrays.asList(v4, v5), null);
        TestdataEntityProvidingEntity z = new TestdataEntityProvidingEntity("z", Arrays.asList(v1, v2, v3, v4, v5), null);

        ScoreDirector<TestdataEntityProvidingSolution> scoreDirector = mock(ScoreDirector.class);
        List<GenuineVariableDescriptor<TestdataEntityProvidingSolution>> variableDescriptorList = TestdataEntityProvidingEntity
                .buildEntityDescriptor().getGenuineVariableDescriptorList();

        PillarSwapMove<TestdataEntityProvidingSolution> abMove = new PillarSwapMove<>(variableDescriptorList,
                Arrays.asList(a), Arrays.asList(b));
        a.setValue(v1);
        b.setValue(v2);
        assertThat(abMove.isMoveDoable(scoreDirector)).isFalse();
        a.setValue(v2);
        b.setValue(v2);
        assertThat(abMove.isMoveDoable(scoreDirector)).isFalse();
        a.setValue(v2);
        b.setValue(v3);
        assertThat(abMove.isMoveDoable(scoreDirector)).isTrue();
        a.setValue(v3);
        b.setValue(v2);
        assertThat(abMove.isMoveDoable(scoreDirector)).isTrue();
        a.setValue(v3);
        b.setValue(v3);
        assertThat(abMove.isMoveDoable(scoreDirector)).isFalse();
        a.setValue(v2);
        b.setValue(v4);
        assertThat(abMove.isMoveDoable(scoreDirector)).isFalse();

        PillarSwapMove<TestdataEntityProvidingSolution> acMove = new PillarSwapMove<>(variableDescriptorList,
                Arrays.asList(a), Arrays.asList(c));
        a.setValue(v1);
        c.setValue(v4);
        assertThat(acMove.isMoveDoable(scoreDirector)).isFalse();
        a.setValue(v2);
        c.setValue(v5);
        assertThat(acMove.isMoveDoable(scoreDirector)).isFalse();

        PillarSwapMove<TestdataEntityProvidingSolution> bcMove = new PillarSwapMove<>(variableDescriptorList,
                Arrays.asList(b), Arrays.asList(c));
        b.setValue(v2);
        c.setValue(v4);
        assertThat(bcMove.isMoveDoable(scoreDirector)).isFalse();
        b.setValue(v4);
        c.setValue(v5);
        assertThat(bcMove.isMoveDoable(scoreDirector)).isTrue();
        b.setValue(v5);
        c.setValue(v4);
        assertThat(bcMove.isMoveDoable(scoreDirector)).isTrue();
        b.setValue(v5);
        c.setValue(v5);
        assertThat(bcMove.isMoveDoable(scoreDirector)).isFalse();

        PillarSwapMove<TestdataEntityProvidingSolution> abzMove = new PillarSwapMove<>(variableDescriptorList,
                Arrays.asList(a, b), Arrays.asList(z));
        a.setValue(v2);
        b.setValue(v2);
        z.setValue(v4);
        assertThat(abzMove.isMoveDoable(scoreDirector)).isFalse();
        a.setValue(v2);
        b.setValue(v2);
        z.setValue(v1);
        assertThat(abzMove.isMoveDoable(scoreDirector)).isFalse();
        a.setValue(v2);
        b.setValue(v2);
        z.setValue(v3);
        assertThat(abzMove.isMoveDoable(scoreDirector)).isTrue();
        a.setValue(v3);
        b.setValue(v3);
        z.setValue(v2);
        assertThat(abzMove.isMoveDoable(scoreDirector)).isTrue();
        a.setValue(v2);
        b.setValue(v2);
        z.setValue(v2);
        assertThat(abzMove.isMoveDoable(scoreDirector)).isFalse();
    }

    @Test
    void doMove() {
        TestdataValue v1 = new TestdataValue("1");
        TestdataValue v2 = new TestdataValue("2");
        TestdataValue v3 = new TestdataValue("3");
        TestdataValue v4 = new TestdataValue("4");
        TestdataValue v5 = new TestdataValue("5");

        TestdataEntityProvidingEntity a = new TestdataEntityProvidingEntity("a", Arrays.asList(v1, v2, v3, v4), null);
        TestdataEntityProvidingEntity b = new TestdataEntityProvidingEntity("b", Arrays.asList(v2, v3, v4, v5), null);
        TestdataEntityProvidingEntity c = new TestdataEntityProvidingEntity("c", Arrays.asList(v4, v5), null);
        TestdataEntityProvidingEntity z = new TestdataEntityProvidingEntity("z", Arrays.asList(v1, v2, v3, v4, v5), null);

        ScoreDirectorFactory<TestdataEntityProvidingSolution> scoreDirectorFactory =
                new EasyScoreDirectorFactory<>(TestdataEntityProvidingSolution.buildSolutionDescriptor(),
                        solution -> SimpleScore.ZERO);
        ScoreDirector<TestdataEntityProvidingSolution> scoreDirector = scoreDirectorFactory.buildScoreDirector();
        List<GenuineVariableDescriptor<TestdataEntityProvidingSolution>> variableDescriptorList = TestdataEntityProvidingEntity
                .buildEntityDescriptor().getGenuineVariableDescriptorList();

        PillarSwapMove<TestdataEntityProvidingSolution> abMove = new PillarSwapMove<>(variableDescriptorList,
                Arrays.asList(a), Arrays.asList(b));

        a.setValue(v1);
        b.setValue(v1);
        abMove.doMove(scoreDirector);
        assertThat(a.getValue()).isEqualTo(v1);
        assertThat(b.getValue()).isEqualTo(v1);

        a.setValue(v2);
        b.setValue(v1);
        abMove.doMove(scoreDirector);
        assertThat(a.getValue()).isEqualTo(v1);
        assertThat(b.getValue()).isEqualTo(v2);

        a.setValue(v3);
        b.setValue(v2);
        abMove.doMove(scoreDirector);
        assertThat(a.getValue()).isEqualTo(v2);
        assertThat(b.getValue()).isEqualTo(v3);
        abMove.doMove(scoreDirector);
        assertThat(a.getValue()).isEqualTo(v3);
        assertThat(b.getValue()).isEqualTo(v2);

        PillarSwapMove<TestdataEntityProvidingSolution> abzMove = new PillarSwapMove<>(variableDescriptorList,
                Arrays.asList(a, b), Arrays.asList(z));

        a.setValue(v3);
        b.setValue(v3);
        z.setValue(v2);
        abzMove.doMove(scoreDirector);
        assertThat(a.getValue()).isEqualTo(v2);
        assertThat(b.getValue()).isEqualTo(v2);
        assertThat(z.getValue()).isEqualTo(v3);
        abzMove.doMove(scoreDirector);
        assertThat(a.getValue()).isEqualTo(v3);
        assertThat(b.getValue()).isEqualTo(v3);
        assertThat(z.getValue()).isEqualTo(v2);

        a.setValue(v3);
        b.setValue(v3);
        z.setValue(v4);
        abzMove.doMove(scoreDirector);
        assertThat(a.getValue()).isEqualTo(v4);
        assertThat(b.getValue()).isEqualTo(v4);
        assertThat(z.getValue()).isEqualTo(v3);
        abzMove.doMove(scoreDirector);
        assertThat(a.getValue()).isEqualTo(v3);
        assertThat(b.getValue()).isEqualTo(v3);
        assertThat(z.getValue()).isEqualTo(v4);

        PillarSwapMove<TestdataEntityProvidingSolution> abczMove = new PillarSwapMove<>(variableDescriptorList,
                Arrays.asList(a), Arrays.asList(b, c, z));

        a.setValue(v2);
        b.setValue(v3);
        c.setValue(v3);
        z.setValue(v3);
        abczMove.doMove(scoreDirector);
        assertThat(a.getValue()).isEqualTo(v3);
        assertThat(b.getValue()).isEqualTo(v2);
        assertThat(c.getValue()).isEqualTo(v2);
        assertThat(z.getValue()).isEqualTo(v2);
        abczMove.doMove(scoreDirector);
        assertThat(a.getValue()).isEqualTo(v2);
        assertThat(b.getValue()).isEqualTo(v3);
        assertThat(c.getValue()).isEqualTo(v3);
        assertThat(z.getValue()).isEqualTo(v3);

        PillarSwapMove<TestdataEntityProvidingSolution> abczMove2 = new PillarSwapMove<>(variableDescriptorList,
                Arrays.asList(a, b), Arrays.asList(c, z));

        a.setValue(v4);
        b.setValue(v4);
        c.setValue(v3);
        z.setValue(v3);
        abczMove2.doMove(scoreDirector);
        assertThat(a.getValue()).isEqualTo(v3);
        assertThat(b.getValue()).isEqualTo(v3);
        assertThat(c.getValue()).isEqualTo(v4);
        assertThat(z.getValue()).isEqualTo(v4);
        abczMove2.doMove(scoreDirector);
        assertThat(a.getValue()).isEqualTo(v4);
        assertThat(b.getValue()).isEqualTo(v4);
        assertThat(c.getValue()).isEqualTo(v3);
        assertThat(z.getValue()).isEqualTo(v3);
    }

    @Test
    void rebase() {
        EntityDescriptor<TestdataSolution> entityDescriptor = TestdataEntity.buildEntityDescriptor();
        List<GenuineVariableDescriptor<TestdataSolution>> variableDescriptorList = entityDescriptor
                .getGenuineVariableDescriptorList();

        TestdataValue v1 = new TestdataValue("v1");
        TestdataValue v2 = new TestdataValue("v2");
        TestdataValue v3 = new TestdataValue("v3");
        TestdataEntity e1 = new TestdataEntity("e1", v1);
        TestdataEntity e2 = new TestdataEntity("e2", null);
        TestdataEntity e3 = new TestdataEntity("e3", v1);
        TestdataEntity e4 = new TestdataEntity("e4", v3);

        TestdataValue destinationV1 = new TestdataValue("v1");
        TestdataValue destinationV2 = new TestdataValue("v2");
        TestdataValue destinationV3 = new TestdataValue("v3");
        TestdataEntity destinationE1 = new TestdataEntity("e1", destinationV1);
        TestdataEntity destinationE2 = new TestdataEntity("e2", null);
        TestdataEntity destinationE3 = new TestdataEntity("e3", destinationV1);
        TestdataEntity destinationE4 = new TestdataEntity("e4", destinationV3);

        ScoreDirector<TestdataSolution> destinationScoreDirector = mockRebasingScoreDirector(
                entityDescriptor.getSolutionDescriptor(), new Object[][] {
                        { v1, destinationV1 },
                        { v2, destinationV2 },
                        { v3, destinationV3 },
                        { e1, destinationE1 },
                        { e2, destinationE2 },
                        { e3, destinationE3 },
                        { e4, destinationE4 },
                });

        assertSameProperties(Arrays.asList(destinationE1, destinationE3), Arrays.asList(destinationE2),
                new PillarSwapMove<>(variableDescriptorList, Arrays.asList(e1, e3), Arrays.asList(e2))
                        .rebase(destinationScoreDirector));
        assertSameProperties(Arrays.asList(destinationE4), Arrays.asList(destinationE1, destinationE3),
                new PillarSwapMove<>(variableDescriptorList, Arrays.asList(e4), Arrays.asList(e1, e3))
                        .rebase(destinationScoreDirector));
    }

    public void assertSameProperties(List<Object> leftPillar, List<Object> rightPillar, PillarSwapMove<?> move) {
        assertThat(move.getLeftPillar()).hasSameElementsAs(leftPillar);
        assertThat(move.getRightPillar()).hasSameElementsAs(rightPillar);
    }

    @Test
    void getters() {
        GenuineVariableDescriptor<TestdataMultiVarSolution> primaryDescriptor = TestdataMultiVarEntity
                .buildVariableDescriptorForPrimaryValue();
        GenuineVariableDescriptor<TestdataMultiVarSolution> secondaryDescriptor = TestdataMultiVarEntity
                .buildVariableDescriptorForSecondaryValue();
        PillarSwapMove<TestdataMultiVarSolution> move = new PillarSwapMove<>(Arrays.asList(primaryDescriptor),
                Arrays.asList(new TestdataMultiVarEntity("a"), new TestdataMultiVarEntity("b")),
                Arrays.asList(new TestdataMultiVarEntity("c"), new TestdataMultiVarEntity("d")));
        assertThat(move.getVariableNameList()).containsExactly("primaryValue");
        assertAllCodesOfCollection(move.getLeftPillar(), "a", "b");
        assertAllCodesOfCollection(move.getRightPillar(), "c", "d");

        move = new PillarSwapMove<>(Arrays.asList(primaryDescriptor, secondaryDescriptor),
                Arrays.asList(new TestdataMultiVarEntity("e"), new TestdataMultiVarEntity("f")),
                Arrays.asList(new TestdataMultiVarEntity("g"), new TestdataMultiVarEntity("h")));
        assertThat(move.getVariableNameList()).containsExactly("primaryValue", "secondaryValue");
        assertAllCodesOfCollection(move.getLeftPillar(), "e", "f");
        assertAllCodesOfCollection(move.getRightPillar(), "g", "h");
    }

    @Test
    void toStringTest() {
        TestdataValue v1 = new TestdataValue("v1");
        TestdataValue v2 = new TestdataValue("v2");
        TestdataEntity a = new TestdataEntity("a", null);
        TestdataEntity b = new TestdataEntity("b", null);
        TestdataEntity c = new TestdataEntity("c", v1);
        TestdataEntity d = new TestdataEntity("d", v1);
        TestdataEntity e = new TestdataEntity("e", v1);
        TestdataEntity f = new TestdataEntity("f", v2);
        TestdataEntity g = new TestdataEntity("g", v2);
        List<GenuineVariableDescriptor<TestdataSolution>> variableDescriptorList = TestdataEntity.buildEntityDescriptor()
                .getGenuineVariableDescriptorList();

        assertThat(new PillarSwapMove<>(variableDescriptorList,
                Arrays.asList(a, b), Arrays.asList(c, d, e)).toString()).isEqualTo("[a, b] {null} <-> [c, d, e] {v1}");
        assertThat(new PillarSwapMove<>(variableDescriptorList,
                Arrays.asList(b), Arrays.asList(c)).toString()).isEqualTo("[b] {null} <-> [c] {v1}");
        assertThat(new PillarSwapMove<>(variableDescriptorList,
                Arrays.asList(f, g), Arrays.asList(c, d, e)).toString()).isEqualTo("[f, g] {v2} <-> [c, d, e] {v1}");
    }

}
