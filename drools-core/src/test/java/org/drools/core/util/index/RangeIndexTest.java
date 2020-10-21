/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/

package org.drools.core.util.index;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.IntPredicate;

import org.assertj.core.api.Assertions;
import org.drools.core.reteoo.AlphaNode;
import org.drools.core.test.model.Person;
import org.drools.core.util.Entry;
import org.drools.core.util.FastIterator;
import org.drools.core.util.RBTree.Node;
import org.junit.Test;

public class RangeIndexTest {

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

        Assertions.assertThat(getValues(index, 18)).containsExactlyInAnyOrder("A", "B", "C", "D", "E");
        Assertions.assertThat(getValues(index, 60)).containsExactlyInAnyOrder("A", "C", "E");
        Assertions.assertThat(getValues(index, 59)).containsExactlyInAnyOrder("A", "C", "D", "E");
        Assertions.assertThat(getValues(index, 4)).containsExactlyInAnyOrder("B", "D", "F");
        Assertions.assertThat(getAllValues(index)).containsExactlyInAnyOrder("A", "B", "C", "D", "E", "F");

        index.removeIndex(RangeIndex.IndexType.GT, 8); // "C"
        index.removeIndex(RangeIndex.IndexType.LT, 60); // "D"

        Assertions.assertThat(getValues(index, 18)).containsExactlyInAnyOrder("A", "B", "E");
        Assertions.assertThat(getValues(index, 60)).containsExactlyInAnyOrder("A", "E");
        Assertions.assertThat(getValues(index, 59)).containsExactlyInAnyOrder("A", "E");
        Assertions.assertThat(getValues(index, 4)).containsExactlyInAnyOrder("B", "F");
        Assertions.assertThat(getAllValues(index)).containsExactlyInAnyOrder("A", "B", "E", "F");
    }

    @Test
    public void testBoundary() {
        RangeIndex<Integer, String> index = new RangeIndex<>();
        index.addIndex(RangeIndex.IndexType.LT, 20, "A");
        index.addIndex(RangeIndex.IndexType.LE, 25, "B");
        index.addIndex(RangeIndex.IndexType.GE, 40, "C");
        index.addIndex(RangeIndex.IndexType.GT, 60, "D");

        Assertions.assertThat(getValues(index, 25)).containsExactlyInAnyOrder("B");
        Assertions.assertThat(getValues(index, 26)).isEmpty();
        Assertions.assertThat(getValues(index, 39)).isEmpty();
        Assertions.assertThat(getValues(index, 40)).containsExactlyInAnyOrder("C");
    }

    private Collection<String> getValues(RangeIndex<Integer, String> index, Integer key) {
        FastIterator it = index.getValuesIterator(key);
        List<String> result = new ArrayList<>();
        for (Entry entry = it.next(null); entry != null; entry = it.next(entry)) {
            Node<Integer, String> node = (Node<Integer, String>) entry;
            result.add(node.value);
        }
        return result;
    }

    private Collection<String> getAllValues(RangeIndex<Integer, String> index) {
        FastIterator it = index.getAllValuesIterator();
        List<String> result = new ArrayList<>();
        for (Entry entry = it.next(null); entry != null; entry = it.next(entry)) {
            Node<Integer, String> node = (Node<Integer, String>) entry;
            result.add(node.value);
        }
        return result;
    }
}
