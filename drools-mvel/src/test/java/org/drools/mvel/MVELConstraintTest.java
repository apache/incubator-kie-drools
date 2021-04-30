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

import org.drools.core.base.ClassFieldAccessorCache;
import org.drools.core.base.ClassFieldAccessorStore;
import org.drools.core.base.ClassFieldReader;
import org.drools.core.base.FieldFactory;
import org.drools.core.spi.Constraint.ConstraintType;
import org.drools.core.spi.FieldValue;
import org.drools.core.util.index.IndexUtil;
import org.drools.mvel.compiler.Cheese;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class MVELConstraintTest {

    private ClassFieldAccessorStore store = new ClassFieldAccessorStore();

    @Before
    public void setUp() throws Exception {
        store.setClassFieldAccessorCache(new ClassFieldAccessorCache(Thread.currentThread().getContextClassLoader()));
        store.setEagerWire(true);
    }

    @Test
    public void testAlphaHashable() {
        final ClassFieldReader extractor1 = store.getReader(Cheese.class, "doublePrice");
        final FieldValue field1 = FieldFactory.getInstance().getFieldValue(5.0d);
        MVELConstraint constraint1 = new MVELConstraint("myPkg", "doublePrice == 5.00", null, IndexUtil.ConstraintType.EQUAL, field1, extractor1, null);
        constraint1.setType(ConstraintType.ALPHA);

        final ClassFieldReader extractor2 = store.getReader(Cheese.class, "doublePrice");
        final FieldValue field2 = FieldFactory.getInstance().getFieldValue(5.0d);
        MVELConstraint constraint2 = new MVELConstraint("myPkg", "doublePrice == 5.0", null, IndexUtil.ConstraintType.EQUAL, field2, extractor2, null);
        constraint2.setType(ConstraintType.ALPHA);

        assertTrue(constraint1.equals(constraint2));
        assertEquals(constraint1.hashCode(), constraint2.hashCode());
    }

    @Test
    public void testAlphaRangeIndexable() {
        final ClassFieldReader extractor1 = store.getReader(Cheese.class, "doublePrice");
        final FieldValue field1 = FieldFactory.getInstance().getFieldValue(5.0d);
        MVELConstraint constraint1 = new MVELConstraint("myPkg", "doublePrice > 5.00", null, IndexUtil.ConstraintType.GREATER_THAN, field1, extractor1, null);
        constraint1.setType(ConstraintType.ALPHA);

        final ClassFieldReader extractor2 = store.getReader(Cheese.class, "doublePrice");
        final FieldValue field2 = FieldFactory.getInstance().getFieldValue(5.0d);
        MVELConstraint constraint2 = new MVELConstraint("myPkg", "doublePrice > 5.0", null, IndexUtil.ConstraintType.GREATER_THAN, field2, extractor2, null);
        constraint2.setType(ConstraintType.ALPHA);

        assertTrue(constraint1.equals(constraint2));
        assertEquals(constraint1.hashCode(), constraint2.hashCode());
    }
}
