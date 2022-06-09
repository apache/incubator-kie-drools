package org.optaplanner.core.impl.domain.variable.inverserelation;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalStateException;
import static org.mockito.Mockito.mock;

import org.junit.jupiter.api.Test;
import org.optaplanner.core.api.score.director.ScoreDirector;
import org.optaplanner.core.impl.score.director.InnerScoreDirector;
import org.optaplanner.core.impl.testdata.domain.list.TestdataListEntity;
import org.optaplanner.core.impl.testdata.domain.list.TestdataListSolution;
import org.optaplanner.core.impl.testdata.domain.list.TestdataListValue;

class SingletonListInverseVariableListenerTest {

    private final ScoreDirector<TestdataListSolution> scoreDirector = mock(InnerScoreDirector.class);

    private final SingletonListInverseVariableListener<TestdataListSolution> inverseVariableListener =
            new SingletonListInverseVariableListener<>(
                    TestdataListValue.buildVariableDescriptorForEntity(),
                    TestdataListEntity.buildVariableDescriptorForValueList());

    @Test
    void inverseRelation() {
        TestdataListValue v1 = new TestdataListValue("1");
        TestdataListValue v2 = new TestdataListValue("2");
        TestdataListValue v3 = new TestdataListValue("3");
        TestdataListEntity e1 = new TestdataListEntity("a", v1, v2);
        TestdataListEntity e2 = new TestdataListEntity("b", v3);

        assertThat(v1.getEntity()).isNull();
        assertThat(v2.getEntity()).isNull();
        assertThat(v3.getEntity()).isNull();

        inverseVariableListener.beforeEntityAdded(scoreDirector, e1);
        inverseVariableListener.afterEntityAdded(scoreDirector, e1);
        inverseVariableListener.beforeEntityAdded(scoreDirector, e2);
        inverseVariableListener.afterEntityAdded(scoreDirector, e2);

        assertThat(v1.getEntity()).isEqualTo(e1);
        assertThat(v2.getEntity()).isEqualTo(e1);
        assertThat(v3.getEntity()).isEqualTo(e2);
        assertThat(inverseVariableListener.getInverseSingleton(v1)).isEqualTo(e1);
        assertThat(inverseVariableListener.getInverseSingleton(v2)).isEqualTo(e1);
        assertThat(inverseVariableListener.getInverseSingleton(v3)).isEqualTo(e2);

        inverseVariableListener.beforeElementMoved(scoreDirector, e1, 0, e2, 1);
        e1.getValueList().remove(v1);
        e2.getValueList().add(v1);
        inverseVariableListener.afterElementMoved(scoreDirector, e1, 0, e2, 1);

        assertThat(v1.getEntity()).isEqualTo(e2);
        assertThat(inverseVariableListener.getInverseSingleton(v1)).isEqualTo(e2);
    }

    @Test
    void removeEntity() {
        TestdataListValue v1 = new TestdataListValue("1");
        TestdataListValue v2 = new TestdataListValue("2");
        TestdataListValue v3 = new TestdataListValue("3");
        TestdataListEntity e1 = TestdataListEntity.createWithValues("a", v1, v2);
        TestdataListEntity e2 = TestdataListEntity.createWithValues("b", v3);

        assertThat(v1.getEntity()).isEqualTo(e1);
        assertThat(v2.getEntity()).isEqualTo(e1);
        assertThat(v3.getEntity()).isEqualTo(e2);

        inverseVariableListener.beforeEntityRemoved(scoreDirector, e1);
        inverseVariableListener.afterEntityRemoved(scoreDirector, e1);

        assertThat(v1.getEntity()).isNull();
        assertThat(v2.getEntity()).isNull();
        assertThat(v3.getEntity()).isEqualTo(e2);
        assertThat(inverseVariableListener.getInverseSingleton(v1)).isNull();
        assertThat(inverseVariableListener.getInverseSingleton(v2)).isNull();
        assertThat(inverseVariableListener.getInverseSingleton(v3)).isEqualTo(e2);
    }

    @Test
    void inverseShouldBeNullBeforeElementAdded() {
        TestdataListValue a = new TestdataListValue("A");
        TestdataListEntity ann = TestdataListEntity.createWithValues("Ann", a);
        TestdataListEntity bob = new TestdataListEntity("Bob");

        inverseVariableListener.beforeElementAdded(scoreDirector, bob, 0);
        bob.getValueList().add(a);
        assertThatIllegalStateException()
                .isThrownBy(() -> inverseVariableListener.afterElementAdded(scoreDirector, bob, 0))
                .withMessageContaining("oldInverseEntity (Ann)");

    }

    @Test
    void inverseShouldBeSourceEntityBeforeElementRemoved() {
        TestdataListValue a = new TestdataListValue("A");
        TestdataListEntity ann = TestdataListEntity.createWithValues("Ann", a);
        TestdataListEntity bob = new TestdataListEntity("Bob");

        bob.getValueList().add(a);

        assertThatIllegalStateException().isThrownBy(() -> {
            inverseVariableListener.beforeElementRemoved(scoreDirector, bob, 0);
            bob.getValueList().remove(0);
            inverseVariableListener.afterElementRemoved(scoreDirector, bob, 0);
        }).withMessageContaining("oldInverseEntity (Ann)");
    }

    @Test
    void inverseShouldBeSourceEntityBeforeElementMoved() {
        TestdataListValue a = new TestdataListValue("A");
        TestdataListEntity ann = TestdataListEntity.createWithValues("Ann", a);
        TestdataListEntity bob = new TestdataListEntity("Bob");
        TestdataListEntity carl = new TestdataListEntity("Carl");

        a.setEntity(carl);

        assertThatIllegalStateException()
                .isThrownBy(() -> {
                    inverseVariableListener.beforeElementMoved(scoreDirector, ann, 0, bob, 0);
                    ann.getValueList().remove(0);
                    bob.getValueList().add(a);
                    inverseVariableListener.afterElementMoved(scoreDirector, ann, 0, bob, 0);
                })
                .withMessageContaining("oldInverseEntity (Carl)");
    }
}
