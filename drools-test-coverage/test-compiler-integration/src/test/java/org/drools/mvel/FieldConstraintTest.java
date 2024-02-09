/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.drools.mvel;

import org.drools.base.rule.Declaration;
import org.drools.base.rule.Pattern;
import org.drools.base.rule.accessor.ReadAccessor;
import org.drools.base.rule.constraint.AlphaNodeFieldConstraint;
import org.drools.core.base.ClassFieldAccessorCache;
import org.drools.core.common.InternalFactHandle;
import org.drools.kiesession.rulebase.InternalKnowledgeBase;
import org.drools.kiesession.rulebase.KnowledgeBaseFactory;
import org.drools.kiesession.session.StatefulKnowledgeSessionImpl;
import org.drools.mvel.accessors.ClassFieldAccessorStore;
import org.drools.mvel.accessors.ClassFieldReader;
import org.drools.mvel.model.Cheese;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.ArrayList;
import java.util.Collection;

import static org.assertj.core.api.Assertions.assertThat;
import static org.drools.base.base.AccessorKey.AccessorType.ClassObjectType;

@RunWith(Parameterized.class)
public class FieldConstraintTest {

    ClassFieldAccessorStore store = new ClassFieldAccessorStore();

    private final boolean useLambdaConstraint;

    public FieldConstraintTest(boolean useLambdaConstraint) {
        this.useLambdaConstraint = useLambdaConstraint;
    }

    @Parameterized.Parameters(name = "useLambdaConstraint={0}")
    public static Collection<Object[]> getParameters() {
        Collection<Object[]> parameters = new ArrayList<>();
        parameters.add(new Object[]{false});
        parameters.add(new Object[]{true});
        return parameters;
    }

    @Before
    public void setUp() throws Exception {
        store.setClassFieldAccessorCache( new ClassFieldAccessorCache( Thread.currentThread().getContextClassLoader() ) );
        store.setEagerWire( true );
    }

    /**
     * <pre>
     *
     *
     *                ( Cheese (type &quot;cheddar&quot;) )
     *
     *
     * </pre>
     *
     * This is currently the same as using a ReturnValueConstraint just that it
     * doesn't need any requiredDeclarations
     */
    @Test
    public void testLiteralConstraint() {
        InternalKnowledgeBase kBase = KnowledgeBaseFactory.newKnowledgeBase();;
        StatefulKnowledgeSessionImpl ksession = (StatefulKnowledgeSessionImpl)kBase.newKieSession();

        final ClassFieldReader extractor = store.getReader( Cheese.class,
                "type");

        AlphaNodeFieldConstraint constraint = ConstraintTestUtil.createCheeseTypeEqualsConstraint(extractor, "cheddar", useLambdaConstraint);

        final Cheese cheddar = new Cheese( "cheddar",
                                           5 );

        final InternalFactHandle cheddarHandle = (InternalFactHandle) ksession.insert( cheddar );

        // check constraint
        assertThat(constraint.isAllowed(cheddarHandle,
                ksession)).isTrue();

        final Cheese stilton = new Cheese( "stilton",
                                           5 );

        final InternalFactHandle stiltonHandle = (InternalFactHandle) ksession.insert( stilton );

        // check constraint
        assertThat(constraint.isAllowed(stiltonHandle,
                ksession)).isFalse();
    }

    /**
     * <pre>
     *
     *
     *                Cheese( price == 5 )
     *
     *
     * </pre>
     */
    @Test
    public void testPrimitiveLiteralConstraint() {
        InternalKnowledgeBase kBase = KnowledgeBaseFactory.newKnowledgeBase();;
        StatefulKnowledgeSessionImpl ksession = (StatefulKnowledgeSessionImpl)kBase.newKieSession();

        final ClassFieldReader extractor = store.getReader(Cheese.class,
                "price");

        AlphaNodeFieldConstraint constraint = ConstraintTestUtil.createCheesePriceEqualsConstraint(extractor, 5, useLambdaConstraint);

        final Cheese cheddar = new Cheese( "cheddar",
                                           5 );

        final InternalFactHandle cheddarHandle = (InternalFactHandle) ksession.insert( cheddar );

        // check constraint
        assertThat(constraint.isAllowed(cheddarHandle,
                ksession)).isTrue();

        final Cheese stilton = new Cheese( "stilton",
                                           10 );

        final InternalFactHandle stiltonHandle = (InternalFactHandle) ksession.insert( stilton );

        // check constraint
        assertThat(constraint.isAllowed(stiltonHandle,
                ksession)).isFalse();
    }
}
