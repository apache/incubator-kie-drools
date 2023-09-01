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

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.ArrayList;
import java.util.Collection;

import org.drools.base.base.ValueResolver;
import org.drools.core.base.ClassFieldAccessorCache;
import org.drools.mvel.accessors.ClassFieldAccessorStore;
import org.drools.base.base.ClassObjectType;
import org.drools.base.definitions.InternalKnowledgePackage;
import org.drools.base.definitions.rule.impl.RuleImpl;
import org.drools.core.reteoo.CoreComponentFactory;
import org.drools.base.rule.Pattern;
import org.drools.base.rule.constraint.AlphaNodeFieldConstraint;
import org.drools.base.rule.consequence.Consequence;
import org.drools.core.rule.consequence.KnowledgeHelper;
import org.drools.core.test.model.Cheese;
import org.drools.kiesession.rulebase.InternalKnowledgeBase;
import org.drools.kiesession.rulebase.KnowledgeBaseFactory;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.kie.api.event.kiebase.AfterFunctionRemovedEvent;
import org.kie.api.event.kiebase.AfterKieBaseLockedEvent;
import org.kie.api.event.kiebase.AfterKieBaseUnlockedEvent;
import org.kie.api.event.kiebase.AfterKiePackageAddedEvent;
import org.kie.api.event.kiebase.AfterKiePackageRemovedEvent;
import org.kie.api.event.kiebase.AfterProcessAddedEvent;
import org.kie.api.event.kiebase.AfterProcessRemovedEvent;
import org.kie.api.event.kiebase.AfterRuleAddedEvent;
import org.kie.api.event.kiebase.AfterRuleRemovedEvent;
import org.kie.api.event.kiebase.BeforeFunctionRemovedEvent;
import org.kie.api.event.kiebase.BeforeKieBaseLockedEvent;
import org.kie.api.event.kiebase.BeforeKieBaseUnlockedEvent;
import org.kie.api.event.kiebase.BeforeKiePackageAddedEvent;
import org.kie.api.event.kiebase.BeforeKiePackageRemovedEvent;
import org.kie.api.event.kiebase.BeforeProcessAddedEvent;
import org.kie.api.event.kiebase.BeforeProcessRemovedEvent;
import org.kie.api.event.kiebase.BeforeRuleAddedEvent;
import org.kie.api.event.kiebase.BeforeRuleRemovedEvent;
import org.kie.api.event.kiebase.KieBaseEventListener;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(Parameterized.class)
public class InternalRuleBaseEventSupportTest {

    private InternalKnowledgeBase kBase;
    private TestRuleBaseListener listener1;
    private TestRuleBaseListener listener2;
    private InternalKnowledgePackage pkg;

    private final boolean useLambdaConstraint;

    public InternalRuleBaseEventSupportTest(boolean useLambdaConstraint) {
        this.useLambdaConstraint = useLambdaConstraint;
    }

    @Parameterized.Parameters(name = "useLambdaConstraint={0}")
    public static Collection<Object[]> getParameters() {
        Collection<Object[]> parameters = new ArrayList<>();
        parameters.add(new Object[]{false});
        parameters.add(new Object[]{true});
        return parameters;
    }

    /* (non-Javadoc)
     * @see junit.framework.TestCase#setUp()
     */
    @Before
    public void setUp() throws Exception {
        kBase = KnowledgeBaseFactory.newKnowledgeBase();;
        listener1 = new TestRuleBaseListener( "(listener-1) " );
        listener2 = new TestRuleBaseListener( "(listener-2) " );
        kBase.addEventListener( listener1 );
        kBase.addEventListener( listener2 );

        final RuleImpl rule1 = new RuleImpl( "test1" );
        final ClassObjectType cheeseObjectType = new ClassObjectType( Cheese.class );
        final Pattern pattern = new Pattern( 0,
                                             cheeseObjectType );

        ClassFieldAccessorStore store = new ClassFieldAccessorStore();
        store.setClassFieldAccessorCache( new ClassFieldAccessorCache( Thread.currentThread().getContextClassLoader() ) );
        store.setEagerWire( true );

        AlphaNodeFieldConstraint constraint = ConstraintTestUtil.createCheeseTypeEqualsConstraint(store, "cheddar", useLambdaConstraint);

        pattern.addConstraint( constraint );
        rule1.addPattern( pattern );

        rule1.setConsequence( new Consequence<KnowledgeHelper>() {
            private static final long serialVersionUID = 510l;

            public void evaluate(final KnowledgeHelper knowledgeHelper,
                                 final ValueResolver valueResolver) throws Exception {
            }

            public void readExternal(ObjectInput in) throws IOException,
                                                    ClassNotFoundException {

            }

            public void writeExternal(ObjectOutput out) throws IOException {

            }
            
            public String getName() {
                return "default";
            }
        } );

        final RuleImpl rule2 = new RuleImpl( "test2" );
        final ClassObjectType cheeseObjectType2 = new ClassObjectType( Cheese.class );
        final Pattern pattern2 = new Pattern( 0,
                                              cheeseObjectType2 );

        AlphaNodeFieldConstraint constraint2 = ConstraintTestUtil.createCheeseTypeEqualsConstraint(store, "stilton", useLambdaConstraint);

        pattern2.addConstraint( constraint2 );
        rule2.addPattern( pattern2 );

        rule2.setConsequence( new Consequence<KnowledgeHelper>() {
            private static final long serialVersionUID = 510l;

            public void evaluate(final KnowledgeHelper knowledgeHelper,
                                 final ValueResolver valueResolver) throws Exception {
            }

            public void readExternal(ObjectInput in) throws IOException,
                                                    ClassNotFoundException {

            }

            public void writeExternal(ObjectOutput out) throws IOException {

            }
            
            public String getName() {
                return "default";
            }
        } );

        pkg = CoreComponentFactory.get().createKnowledgePackage( "org.drools.test1" );
        pkg.addRule( rule1 );
        pkg.addRule( rule2 );

    }

    @Test
    public void testAddPackageEvents() throws Exception {
        assertThat(listener1.getBeforePackageAdded()).isEqualTo(0);
        assertThat(listener1.getAfterPackageAdded()).isEqualTo(0);
        assertThat(listener2.getBeforePackageAdded()).isEqualTo(0);
        assertThat(listener2.getAfterPackageAdded()).isEqualTo(0);
        assertThat(listener1.getBeforeRuleAdded()).isEqualTo(0);
        assertThat(listener1.getAfterRuleAdded()).isEqualTo(0);
        assertThat(listener2.getBeforeRuleAdded()).isEqualTo(0);
        assertThat(listener2.getAfterRuleAdded()).isEqualTo(0);

        this.kBase.addPackage( pkg );

        assertThat(listener1.getBeforePackageAdded()).isEqualTo(1);
        assertThat(listener1.getAfterPackageAdded()).isEqualTo(1);
        assertThat(listener2.getBeforePackageAdded()).isEqualTo(1);
        assertThat(listener2.getAfterPackageAdded()).isEqualTo(1);
        assertThat(listener1.getBeforeRuleAdded()).isEqualTo(2);
        assertThat(listener1.getAfterRuleAdded()).isEqualTo(2);
        assertThat(listener2.getBeforeRuleAdded()).isEqualTo(2);
        assertThat(listener2.getAfterRuleAdded()).isEqualTo(2);
    }

    @Test
    public void testRemovePackageEvents() throws Exception {
        this.kBase.addPackage( pkg );

        assertThat(listener1.getBeforePackageRemoved()).isEqualTo(0);
        assertThat(listener1.getAfterPackageRemoved()).isEqualTo(0);
        assertThat(listener2.getBeforePackageRemoved()).isEqualTo(0);
        assertThat(listener2.getAfterPackageRemoved()).isEqualTo(0);

        assertThat(listener1.getBeforeRuleRemoved()).isEqualTo(0);
        assertThat(listener1.getAfterRuleRemoved()).isEqualTo(0);
        assertThat(listener2.getBeforeRuleRemoved()).isEqualTo(0);
        assertThat(listener2.getAfterRuleRemoved()).isEqualTo(0);

        this.kBase.removeKiePackage( "org.drools.test1" );

        assertThat(listener1.getBeforePackageRemoved()).isEqualTo(1);
        assertThat(listener1.getAfterPackageRemoved()).isEqualTo(1);
        assertThat(listener2.getBeforePackageRemoved()).isEqualTo(1);
        assertThat(listener2.getAfterPackageRemoved()).isEqualTo(1);
        assertThat(listener1.getBeforeRuleRemoved()).isEqualTo(2);
        assertThat(listener1.getAfterRuleRemoved()).isEqualTo(2);
        assertThat(listener2.getBeforeRuleRemoved()).isEqualTo(2);
        assertThat(listener2.getAfterRuleRemoved()).isEqualTo(2);

    }

    public static class TestRuleBaseListener
        implements
        KieBaseEventListener {
        private String id;
        private int    beforePackageAdded   = 0;
        private int    afterPackageAdded    = 0;
        private int    beforePackageRemoved = 0;
        private int    afterPackageRemoved  = 0;
        private int    beforeRuleAdded      = 0;
        private int    afterRuleAdded       = 0;
        private int    beforeRuleRemoved    = 0;
        private int    afterRuleRemoved     = 0;

        public TestRuleBaseListener() {
        }

        public TestRuleBaseListener(String id) {
            super();
            this.id = id;
        }

        public void readExternal(ObjectInput in) throws IOException,
                                                ClassNotFoundException {
            id = (String) in.readObject();
            beforePackageAdded = in.readInt();
            afterPackageAdded = in.readInt();
            beforePackageRemoved = in.readInt();
            afterPackageRemoved = in.readInt();
            beforeRuleAdded = in.readInt();
            afterRuleAdded = in.readInt();
            beforeRuleRemoved = in.readInt();
            afterRuleRemoved = in.readInt();
        }

        public void writeExternal(ObjectOutput out) throws IOException {
            out.writeObject( id );
            out.writeInt( beforePackageAdded );
            out.writeInt( afterPackageAdded );
            out.writeInt( beforePackageRemoved );
            out.writeInt( afterPackageRemoved );
            out.writeInt( beforeRuleAdded );
            out.writeInt( afterRuleAdded );
            out.writeInt( beforeRuleRemoved );
            out.writeInt( afterRuleRemoved );
        }

        public void afterKiePackageAdded(AfterKiePackageAddedEvent event) {
            //            System.out.println( this.id + event );
            this.afterPackageAdded++;
        }

        public void beforeKiePackageAdded(BeforeKiePackageAddedEvent event) {
            //            System.out.println( this.id + event );
            this.beforePackageAdded++;
        }

        protected int getAfterPackageAdded() {
            return afterPackageAdded;
        }

        protected int getBeforePackageAdded() {
            return beforePackageAdded;
        }

        protected String getId() {
            return id;
        }

        public void afterKiePackageRemoved(AfterKiePackageRemovedEvent event) {
            //            System.out.println( this.id + event );
            this.afterPackageRemoved++;
        }

        public void beforeKiePackageRemoved(BeforeKiePackageRemovedEvent event) {
            //            System.out.println( this.id + event );
            this.beforePackageRemoved++;
        }

        protected int getAfterPackageRemoved() {
            return afterPackageRemoved;
        }

        protected int getBeforePackageRemoved() {
            return beforePackageRemoved;
        }

        public int getAfterRuleAdded() {
            return afterRuleAdded;
        }

        public int getBeforeRuleAdded() {
            return beforeRuleAdded;
        }

        public void afterRuleAdded(AfterRuleAddedEvent event) {
            //            System.out.println( this.id + event );
            this.afterRuleAdded++;
        }

        public void beforeRuleAdded(BeforeRuleAddedEvent event) {
            //            System.out.println( this.id + event );
            this.beforeRuleAdded++;
        }

        public int getAfterRuleRemoved() {
            return afterRuleRemoved;
        }

        public int getBeforeRuleRemoved() {
            return beforeRuleRemoved;
        }

        public void afterRuleRemoved(AfterRuleRemovedEvent event) {
            //            System.out.println( this.id + event );
            this.afterRuleRemoved++;
        }

        public void beforeRuleRemoved(BeforeRuleRemovedEvent event) {
            //            System.out.println( this.id + event );
            this.beforeRuleRemoved++;
        }

        public void afterFunctionRemoved(AfterFunctionRemovedEvent event) {
            // TODO Auto-generated method stub

        }

        public void afterKieBaseLocked(AfterKieBaseLockedEvent event) {
            // TODO Auto-generated method stub

        }

        public void afterKieBaseUnlocked(AfterKieBaseUnlockedEvent event) {
            // TODO Auto-generated method stub

        }

        public void beforeFunctionRemoved(BeforeFunctionRemovedEvent event) {
            // TODO Auto-generated method stub

        }

        public void beforeKieBaseLocked(BeforeKieBaseLockedEvent event) {
            // TODO Auto-generated method stub

        }

        public void beforeKieBaseUnlocked(BeforeKieBaseUnlockedEvent event) {
            // TODO Auto-generated method stub

        }

		public void beforeProcessAdded(BeforeProcessAddedEvent event) {
			// TODO Auto-generated method stub
			
		}

		public void afterProcessAdded(AfterProcessAddedEvent event) {
			// TODO Auto-generated method stub
			
		}

		public void beforeProcessRemoved(BeforeProcessRemovedEvent event) {
			// TODO Auto-generated method stub
			
		}

		public void afterProcessRemoved(AfterProcessRemovedEvent event) {
			// TODO Auto-generated method stub
			
		}

    }

}
