package org.drools.core.util.index;

import org.drools.core.test.model.Person;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class RangeIndexTest {

    // This DRL is not actually used but test() simulates this DRL use case.
    final String drl =
            "package org.drools.compiler.test\n" +
                       "import " + Person.class.getCanonicalName() + "\n" +
                       "rule A when\n" +
                       "   Person( age >= 18 )\n" +
                       "then\n" +
                       "end\n" +
                       "rule B when\n" +
                       "   Person( age < 25 )\n" +
                       "then\n" +
                       "end\n" +
                       "rule C when\n" +
                       "   Person( age > 8 )\n" +
                       "then\n" +
                       "end\n" +
                       "rule D when\n" +
                       "   Person( age < 60 )\n" +
                       "then\n" +
                       "end\n" +
                       "rule E when\n" +
                       "   Person( age > 12 )\n" +
                       "then\n" +
                       "end\n" +
                       "rule F when\n" +
                       "   Person( age <= 4 )\n" +
                       "then\n" +
                       "end\n";

    @Test
    public void test() {
        RangeIndex<Integer, String> index = new RangeIndex<>();
        index.addIndex(RangeIndex.IndexType.GE, 18, "A");
        index.addIndex(RangeIndex.IndexType.LT, 25, "B");
        index.addIndex(RangeIndex.IndexType.GT, 8, "C");
        index.addIndex(RangeIndex.IndexType.LT, 60, "D");
        index.addIndex(RangeIndex.IndexType.GT, 12, "E");
        index.addIndex(RangeIndex.IndexType.LE, 4, "F");

        assertThat(index.getValues(18)).containsExactlyInAnyOrder("A", "B", "C", "D", "E");
        assertThat(index.getValues(60)).containsExactlyInAnyOrder("A", "C", "E");
        assertThat(index.getValues(59)).containsExactlyInAnyOrder("A", "C", "D", "E");
        assertThat(index.getValues(4)).containsExactlyInAnyOrder("B", "D", "F");
        assertThat(index.getAllValues()).containsExactlyInAnyOrder("A", "B", "C", "D", "E", "F");

        index.removeIndex(RangeIndex.IndexType.GT, 8); // "C"
        index.removeIndex(RangeIndex.IndexType.LT, 60); // "D"

        assertThat(index.getValues(18)).containsExactlyInAnyOrder("A", "B", "E");
        assertThat(index.getValues(60)).containsExactlyInAnyOrder("A", "E");
        assertThat(index.getValues(59)).containsExactlyInAnyOrder("A", "E");
        assertThat(index.getValues(4)).containsExactlyInAnyOrder("B", "F");
        assertThat(index.getAllValues()).containsExactlyInAnyOrder("A", "B", "E", "F");
    }

    @Test
    public void testBoundary() {
        RangeIndex<Integer, String> index = new RangeIndex<>();
        index.addIndex(RangeIndex.IndexType.LT, 20, "A");
        index.addIndex(RangeIndex.IndexType.LE, 25, "B");
        index.addIndex(RangeIndex.IndexType.GE, 40, "C");
        index.addIndex(RangeIndex.IndexType.GT, 60, "D");

        assertThat(index.getValues(25)).containsExactlyInAnyOrder("B");
        assertThat(index.getValues(26)).isEmpty();
        assertThat(index.getValues(39)).isEmpty();
        assertThat(index.getValues(40)).containsExactlyInAnyOrder("C");
    }
}
