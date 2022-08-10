package org.optaplanner.core.impl.domain.lookup;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;
import static org.assertj.core.api.Assertions.assertThatIllegalStateException;

import org.junit.jupiter.api.Test;
import org.optaplanner.core.api.domain.lookup.LookUpStrategyType;
import org.optaplanner.core.impl.testdata.domain.clone.lookup.TestdataObjectEquals;
import org.optaplanner.core.impl.testdata.domain.clone.lookup.TestdataObjectEqualsNoHashCode;
import org.optaplanner.core.impl.testdata.domain.clone.lookup.TestdataObjectEqualsSubclass;
import org.optaplanner.core.impl.testdata.domain.clone.lookup.TestdataObjectNoId;

class LookUpStrategyEqualityTest extends AbstractLookupTest {

    public LookUpStrategyEqualityTest() {
        super(LookUpStrategyType.EQUALITY);
    }

    @Test
    void addRemoveWithEquals() {
        TestdataObjectEquals object = new TestdataObjectEquals(0);
        lookUpManager.addWorkingObject(object);
        lookUpManager.removeWorkingObject(object);
        // The removed object cannot be looked up
        assertThat(lookUpManager.lookUpWorkingObjectOrReturnNull(object)).isNull();
    }

    @Test
    void addWithEqualsInSuperclass() {
        TestdataObjectEqualsSubclass object = new TestdataObjectEqualsSubclass(0);
        lookUpManager.addWorkingObject(object);
        assertThat(lookUpManager.lookUpWorkingObject(object)).isSameAs(object);
    }

    @Test
    void addWithoutEquals() {
        TestdataObjectNoId object = new TestdataObjectNoId();
        assertThatIllegalArgumentException()
                .isThrownBy(() -> lookUpManager.addWorkingObject(object))
                .withMessageContaining("override the equals() method")
                .withMessageContaining(TestdataObjectNoId.class.getSimpleName());
    }

    @Test
    void addWithoutHashCode() {
        TestdataObjectEqualsNoHashCode object = new TestdataObjectEqualsNoHashCode(0);
        assertThatIllegalArgumentException()
                .isThrownBy(() -> lookUpManager.addWorkingObject(object))
                .withMessageContaining("overrides the hashCode() method")
                .withMessageContaining(TestdataObjectEqualsNoHashCode.class.getSimpleName());
    }

    @Test
    void removeWithoutEquals() {
        TestdataObjectNoId object = new TestdataObjectNoId();
        assertThatIllegalArgumentException()
                .isThrownBy(() -> lookUpManager.removeWorkingObject(object))
                .withMessageContaining("override the equals() method")
                .withMessageContaining(TestdataObjectNoId.class.getSimpleName());
    }

    @Test
    void addEqualObjects() {
        TestdataObjectEquals object = new TestdataObjectEquals(2);
        lookUpManager.addWorkingObject(object);
        assertThatIllegalStateException()
                .isThrownBy(() -> lookUpManager.addWorkingObject(new TestdataObjectEquals(2)))
                .withMessageContaining(object.toString());
    }

    @Test
    void removeWithoutAdding() {
        TestdataObjectEquals object = new TestdataObjectEquals(0);
        assertThatIllegalStateException()
                .isThrownBy(() -> lookUpManager.removeWorkingObject(object))
                .withMessageContaining("differ");
    }

    @Test
    void lookUpWithEquals() {
        TestdataObjectEquals object = new TestdataObjectEquals(1);
        lookUpManager.addWorkingObject(object);
        assertThat(lookUpManager.lookUpWorkingObject(new TestdataObjectEquals(1))).isSameAs(object);
    }

    @Test
    void lookUpWithoutEquals() {
        TestdataObjectNoId object = new TestdataObjectNoId();
        assertThatIllegalArgumentException()
                .isThrownBy(() -> lookUpManager.lookUpWorkingObject(object))
                .withMessageContaining("override the equals() method");
    }

    @Test
    void lookUpWithoutAdding() {
        TestdataObjectEquals object = new TestdataObjectEquals(0);
        assertThat(lookUpManager.lookUpWorkingObjectOrReturnNull(object)).isNull();
    }
}
