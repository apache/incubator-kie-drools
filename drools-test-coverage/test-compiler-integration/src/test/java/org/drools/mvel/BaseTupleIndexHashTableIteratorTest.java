/*
 * Copyright (c) 2021. Red Hat, Inc. and/or its affiliates.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.drools.mvel;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.drools.core.base.ClassFieldAccessorCache;
import org.drools.core.base.ClassFieldAccessorStore;
import org.drools.core.base.ClassObjectType;
import org.drools.core.base.evaluators.EvaluatorRegistry;
import org.drools.core.base.evaluators.Operator;
import org.drools.core.rule.Declaration;
import org.drools.core.rule.Pattern;
import org.drools.core.spi.BetaNodeFieldConstraint;
import org.drools.core.spi.InternalReadAccessor;
import org.drools.core.util.Entry;
import org.drools.core.util.index.TupleIndexHashTable;
import org.drools.model.functions.Predicate2;
import org.drools.model.index.BetaIndexImpl;
import org.drools.modelcompiler.util.EvaluationUtil;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import static org.junit.Assert.assertEquals;

@RunWith(Parameterized.class)
public abstract class BaseTupleIndexHashTableIteratorTest {

    public static EvaluatorRegistry registry = new EvaluatorRegistry();

    protected boolean useLambdaConstraint;

    @Parameterized.Parameters(name = "useLambdaConstraint={0}")
    public static Collection<Object[]> getParameters() {
        Collection<Object[]> parameters = new ArrayList<>();
        parameters.add(new Object[]{false});
        parameters.add(new Object[]{true});
        return parameters;
    }

    protected static BetaNodeFieldConstraint createFooThisEqualsDBetaConstraint(boolean useLambdaConstraint) {
        if (useLambdaConstraint) {
            return createFooThisEqualsDBetaConstraintWithLambdaConstraint();
        } else {
            return createFooThisEqualsDBetaConstraintWithMvelConstraint();
        }
    }

    private static BetaNodeFieldConstraint createFooThisEqualsDBetaConstraintWithLambdaConstraint() {
        Pattern pattern = new Pattern(0, new ClassObjectType(Foo.class));
        Pattern varPattern = new Pattern(1, new ClassObjectType(Foo.class));
        Predicate2<Foo, Foo> predicate = new Predicate2.Impl<Foo, Foo>((_this, d) -> EvaluationUtil.areNullSafeEquals(_this, d));
        BetaIndexImpl<Foo, Foo, Foo> index = new BetaIndexImpl<Foo, Foo, Foo>(Foo.class, org.drools.model.Index.ConstraintType.EQUAL, 1, _this -> _this, d -> d, Foo.class);
        return LambdaConstraintTestUtil.createLambdaConstraint2(Foo.class, Foo.class, pattern, varPattern, "d", predicate, index);
    }

    private static BetaNodeFieldConstraint createFooThisEqualsDBetaConstraintWithMvelConstraint() {
        ClassFieldAccessorStore store = new ClassFieldAccessorStore();
        store.setClassFieldAccessorCache(new ClassFieldAccessorCache(Thread.currentThread().getContextClassLoader()));
        store.setEagerWire(true);
        InternalReadAccessor extractor = store.getReader(Foo.class, "this");
        Declaration declaration = new Declaration("d", extractor, new Pattern(0, new ClassObjectType(Foo.class)));
        String expression = "this " + Operator.EQUAL.getOperatorString() + " d";
        return new MVELConstraintTestUtil(expression, declaration, extractor);
    }

    protected List createTableIndexListForAssertion(TupleIndexHashTable hashTable) {
        Entry[] table = hashTable.getTable();
        List list = new ArrayList();
        for (int i = 0; i < table.length; i++) {
            if (table[i] != null) {
                List entries = new ArrayList();
                entries.add(i);
                Entry entry = table[i];
                while (entry != null) {
                    entries.add(entry);
                    entry = entry.getNext();
                }
                list.add(entries.toArray());
            }
        }
        return list;
    }

    protected void assertTableIndex(List list, int index, int expectedTableIndex, int expectedSizeOfEntries) {
        Object[] entries = (Object[]) list.get(index);
        assertEquals(expectedTableIndex, entries[0]);
        assertEquals(expectedSizeOfEntries, entries.length);
    }

    public static class Foo {

        private String val;
        private int hashCode;

        public Foo(String val, int hashCode) {
            this.val = val;
            this.hashCode = hashCode;
        }

        public String getVal() {
            return val;
        }

        @Override
        public int hashCode() {
            return hashCode;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj)
                return true;
            if (obj == null)
                return false;
            if (getClass() != obj.getClass())
                return false;
            Foo other = (Foo) obj;
            if (hashCode != other.hashCode)
                return false;
            if (val == null) {
                if (other.val != null)
                    return false;
            } else if (!val.equals(other.val))
                return false;
            return true;
        }

    }

}
