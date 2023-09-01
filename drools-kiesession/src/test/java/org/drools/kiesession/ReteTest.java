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
package org.drools.kiesession;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Properties;

import org.drools.core.RuleBaseConfiguration;
import org.drools.base.base.ClassObjectType;
import org.drools.core.common.DefaultFactHandle;
import org.drools.core.common.PhreakPropagationContextFactory;
import org.drools.core.common.PropagationContextFactory;
import org.drools.kiesession.rulebase.InternalKnowledgeBase;
import org.drools.core.impl.RuleBaseFactory;
import org.drools.core.reteoo.ClassObjectTypeConf;
import org.drools.core.reteoo.EntryPointNode;
import org.drools.core.reteoo.MockObjectSink;
import org.drools.core.reteoo.ObjectTypeNode;
import org.drools.core.reteoo.Rete;
import org.drools.core.reteoo.ReteooBuilder.IdGenerator;
import org.drools.core.reteoo.builder.BuildContext;
import org.drools.base.rule.EntryPointId;
import org.drools.core.common.PropagationContext;
import org.drools.core.test.model.Cheese;
import org.drools.kiesession.rulebase.KnowledgeBaseFactory;
import org.drools.kiesession.session.StatefulKnowledgeSessionImpl;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.kie.api.runtime.rule.FactHandle;
import org.kie.internal.conf.CompositeConfiguration;
import org.kie.internal.utils.ChainedProperties;

import static org.assertj.core.api.Assertions.assertThat;

public class ReteTest {
    private PropagationContextFactory pctxFactory;
    private InternalKnowledgeBase kBase;
    private BuildContext   buildContext;
    private EntryPointNode entryPoint;

    @Before
    public void setUp() throws Exception {
        this.kBase = KnowledgeBaseFactory.newKnowledgeBase();

        this.pctxFactory = new PhreakPropagationContextFactory();
        this.buildContext = new BuildContext(kBase, Collections.emptyList());

        this.entryPoint = buildContext.getRuleBase().getRete().getEntryPointNodes().values().iterator().next();;
    }

    /**
     * Tests ObjectTypeNodes are correctly added to the Rete object
     *
     * @throws Exception
     */
    @Test
    public void testObjectTypeNodes() throws Exception {
        final Rete rete = kBase.getRete();

        final ObjectTypeNode objectTypeNode = new ObjectTypeNode(1,
                                                                 this.entryPoint,
                                                                 new ClassObjectType(Object.class),
                                                                 buildContext);
        objectTypeNode.attach(buildContext);

        final ObjectTypeNode stringTypeNode = new ObjectTypeNode(2,
                                                                 this.entryPoint,
                                                                 new ClassObjectType(String.class),
                                                                 buildContext);
        stringTypeNode.attach(buildContext);

        final List<ObjectTypeNode> list = rete.getObjectTypeNodes();

        // Check the ObjectTypeNodes are correctly added to Rete
        assertThat(list.size()).isEqualTo(3);

        assertThat(list.contains(objectTypeNode)).isTrue();
        assertThat(list.contains(stringTypeNode)).isTrue();
    }

    /**
     * Tests that interfaces and parent classes for an asserted  class are  cached, for  quick future iterations
     */
    @Test
    public void testCache() {
        StatefulKnowledgeSessionImpl ksession = (StatefulKnowledgeSessionImpl)kBase.newKieSession();

        // Create a Rete network with ObjectTypeNodes for List, Collection and ArrayList
        final Rete rete = kBase.getRete();
        ObjectTypeNode objectTypeNode = new ObjectTypeNode(1,
                                                           this.entryPoint,
                                                           new ClassObjectType(List.class),
                                                           buildContext);
        objectTypeNode.attach(buildContext);
        MockObjectSink sink = new MockObjectSink();
        objectTypeNode.addObjectSink(sink);

        objectTypeNode = new ObjectTypeNode(1,
                                            this.entryPoint,
                                            new ClassObjectType(Collection.class),
                                            buildContext);
        objectTypeNode.attach(buildContext);
        sink = new MockObjectSink();
        objectTypeNode.addObjectSink(sink);

        objectTypeNode = new ObjectTypeNode(1,
                                            this.entryPoint,
                                            new ClassObjectType(ArrayList.class),
                                            buildContext);
        objectTypeNode.attach(buildContext);
        sink = new MockObjectSink();
        objectTypeNode.addObjectSink(sink);

        // ArrayList matches all three ObjectTypeNodes
        final DefaultFactHandle h1 = new DefaultFactHandle(1,
                                                           new ArrayList());
        rete.assertObject(h1,
                          pctxFactory.createPropagationContext(0,
                                                               PropagationContext.Type.INSERTION,
                                                               null,
                                                               null,
                                                               null),
                          ksession);

        // ArrayList matches two ObjectTypeNodes        
        h1.setObject(new ArrayList());
        rete.assertObject(h1,
                          pctxFactory.createPropagationContext(0,
                                                               PropagationContext.Type.INSERTION,
                                                               null,
                                                               null,
                                                               null),
                          ksession);

        ClassObjectTypeConf conf = (ClassObjectTypeConf) ksession.getObjectTypeConfigurationRegistry().getOrCreateObjectTypeConf(this.entryPoint.getEntryPoint(), new ArrayList());
        assertThat(conf.getObjectTypeNodes()).hasSize(3);

        conf = (ClassObjectTypeConf) ksession.getObjectTypeConfigurationRegistry().getOrCreateObjectTypeConf(this.entryPoint.getEntryPoint(), new ArrayList());
        assertThat(conf.getObjectTypeNodes()).hasSize(3);

    }

    /**
     * Test asserts correctly propagate
     *
     * @throws Exception
     */
    @Test
    public void testAssertObject() throws Exception {
        StatefulKnowledgeSessionImpl ksession = (StatefulKnowledgeSessionImpl)kBase.newKieSession();

        // Create a Rete network with ObjectTypeNodes for List, Collection and ArrayList
        final Rete rete = kBase.getRete();
        final ObjectTypeNode objectTypeNode = new ObjectTypeNode(1,
                                                                 this.entryPoint,
                                                                 new ClassObjectType(List.class),
                                                                 buildContext);
        objectTypeNode.attach(buildContext);
        final MockObjectSink sink1 = new MockObjectSink();
        objectTypeNode.addObjectSink(sink1);

        // There are no String ObjectTypeNodes, make sure its not propagated

        final String string = "String";
        final DefaultFactHandle h1 = new DefaultFactHandle(1,
                                                           string);

        rete.assertObject(h1,
                          pctxFactory.createPropagationContext(0,
                                                               PropagationContext.Type.INSERTION,
                                                               null,
                                                               null,
                                                               null),
                          ksession);

        assertThat((Collection) sink1.getAsserted()).hasSize(0);

        // There is a List ObjectTypeNode, make sure it was propagated
        final List list = new ArrayList();
        final DefaultFactHandle h2 = new DefaultFactHandle(1,
                                                           list);

        rete.assertObject(h2,
                          pctxFactory.createPropagationContext(0,
                                                               PropagationContext.Type.INSERTION,
                                                               null,
                                                               null,
                                                               null),
                          ksession);

        ksession.fireAllRules();

        final List asserted = sink1.getAsserted();
        assertThat((Collection) asserted).hasSize(1);

        final Object[] results = (Object[]) asserted.get(0);
        assertThat(((DefaultFactHandle) results[0]).getObject()).isSameAs(list);
    }

    @Test
    public void testAssertObjectWithNoMatchingObjectTypeNode() {
        StatefulKnowledgeSessionImpl ksession = (StatefulKnowledgeSessionImpl)kBase.newKieSession();

        final Rete rete = kBase.getRete();
        assertThat(rete.getObjectTypeNodes().size()).isEqualTo(1);

        List list = new ArrayList();

        ksession.insert(list);
        ksession.fireAllRules();

        assertThat(rete.getObjectTypeNodes().size()).isEqualTo(1);
    }

    @Test
    @Ignore
    public void testHierarchy() {
        StatefulKnowledgeSessionImpl ksession = (StatefulKnowledgeSessionImpl)kBase.newKieSession();

        final Rete rete = kBase.getRete();
        final IdGenerator idGenerator = kBase.getReteooBuilder().getNodeIdsGenerator();

        // Attach a List ObjectTypeNode
        final ObjectTypeNode listOtn = new ObjectTypeNode(idGenerator.getNextId(),
                                                          this.entryPoint,
                                                          new ClassObjectType(List.class),
                                                          buildContext);
        listOtn.attach(buildContext);

        // Will automatically create an ArrayList ObjectTypeNode
        FactHandle handle = ksession.insert(new ArrayList());

        // Check we have three ObjectTypeNodes, List, ArrayList and InitialFactImpl
        assertThat(rete.getObjectTypeNodes().size()).isEqualTo(3);

        // double check that the List reference is the same as the one we created, i.e. engine should try and recreate it
        assertThat(rete.getObjectTypeNodes(EntryPointId.DEFAULT).get(new ClassObjectType(List.class))).isSameAs(listOtn);

        // ArrayConf should match two ObjectTypenodes for List and ArrayList
        ClassObjectTypeConf arrayConf = (ClassObjectTypeConf) ksession.getObjectTypeConfigurationRegistry().getOrCreateObjectTypeConf(this.entryPoint.getEntryPoint(), new ArrayList());
        final ObjectTypeNode arrayOtn = arrayConf.getConcreteObjectTypeNode();
        assertThat(arrayConf.getObjectTypeNodes().length).isEqualTo(2);

        // Check it contains List and ArrayList
        List nodes = Arrays.asList(arrayConf.getObjectTypeNodes());
        assertThat(nodes.size()).isEqualTo(2);
        assertThat(nodes.contains(arrayOtn)).isTrue();
        assertThat(nodes.contains(listOtn)).isTrue();

        // Nodes are there, retract the fact so we can check both nodes are populated
        ksession.retract(handle);

        // Add MockSinks so we can track assertions
        final MockObjectSink listSink = new MockObjectSink();
        listOtn.addObjectSink(listSink);

        final MockObjectSink arraySink = new MockObjectSink();
        listOtn.addObjectSink(arraySink);

        ksession.insert(new ArrayList());
        assertThat(listSink.getAsserted().size()).isEqualTo(1);
        assertThat(arraySink.getAsserted().size()).isEqualTo(1);

        // Add a Collection ObjectTypeNode, so that we can check that the data from ArrayList is sent to it
        final ObjectTypeNode collectionOtn = new ObjectTypeNode(idGenerator.getNextId(),
                                                                this.entryPoint,
                                                                new ClassObjectType(Collection.class),
                                                                buildContext);
        final MockObjectSink collectionSink = new MockObjectSink();
        collectionOtn.addObjectSink(collectionSink);

        collectionOtn.attach(new TestBuildContext(kBase));

        assertThat(collectionSink.getAsserted().size()).isEqualTo(1);

        // check that ArrayListConf was updated with the new ObjectTypeNode
        nodes = Arrays.asList(arrayConf.getObjectTypeNodes());
        assertThat(nodes.size()).isEqualTo(3);
        assertThat(nodes.contains(arrayOtn)).isTrue();
        assertThat(nodes.contains(listOtn)).isTrue();
        assertThat(nodes.contains(collectionOtn)).isTrue();
    }

    /**
     * All objects deleted from a RootNode must be propagated to all children
     * ObjectTypeNodes.
     */
    @Test
    public void testRetractObject() throws Exception {
        StatefulKnowledgeSessionImpl ksession = (StatefulKnowledgeSessionImpl)kBase.newKieSession();

        // Create a Rete network with ObjectTypeNodes for List, Collection and ArrayList
        final Rete rete = kBase.getRete();
        final ObjectTypeNode objectTypeNode = new ObjectTypeNode(1,
                                                                 this.entryPoint,
                                                                 new ClassObjectType(List.class),
                                                                 buildContext);
        objectTypeNode.attach(buildContext);
        final MockObjectSink sink1 = new MockObjectSink();
        objectTypeNode.addObjectSink(sink1);

        // There are no String ObjectTypeNodes, make sure its not propagated
        final String string = "String";
        final DefaultFactHandle h1 = new DefaultFactHandle(1,
                                                           string);

        rete.assertObject(h1,
                          pctxFactory.createPropagationContext(0,
                                                               PropagationContext.Type.INSERTION,
                                                               null,
                                                               null,
                                                               null),
                          ksession);
        assertThat((Collection) sink1.getAsserted()).hasSize(0);
        assertThat((Collection) sink1.getRetracted()).hasSize(0);

        // There is a List ObjectTypeNode, make sure it was propagated
        final List list = new ArrayList();
        final DefaultFactHandle h2 = new DefaultFactHandle(1,
                                                           list);

        // need  to assert first, to force it to build  up the cache
        rete.assertObject(h2,
                          pctxFactory.createPropagationContext(0,
                                                               PropagationContext.Type.INSERTION,
                                                               null,
                                                               null,
                                                               null),
                          ksession);

        rete.retractObject(h2,
                           pctxFactory.createPropagationContext(0,
                                                                PropagationContext.Type.INSERTION,
                                                                null,
                                                                null,
                                                                null),
                           ksession);

        ksession.fireAllRules();

        final List retracted = sink1.getRetracted();
        assertThat((Collection) retracted).hasSize(1);

        final Object[] results = (Object[]) retracted.get(0);
        assertThat(((DefaultFactHandle) results[0]).getObject()).isSameAs(list);
    }

    @Test
    public void testIsShadowed() {
        StatefulKnowledgeSessionImpl ksession = (StatefulKnowledgeSessionImpl)kBase.newKieSession();

        // Create a Rete network with ObjectTypeNodes for List, Collection and ArrayList
        final Rete rete = kBase.getRete();
        final ObjectTypeNode objectTypeNode = new ObjectTypeNode(1,
                                                                 this.entryPoint,
                                                                 new ClassObjectType(Cheese.class),
                                                                 buildContext);
        objectTypeNode.attach(buildContext);
        final MockObjectSink sink1 = new MockObjectSink();
        objectTypeNode.addObjectSink(sink1);

        // There are no String ObjectTypeNodes, make sure its not propagated

        final Cheese cheese = new Cheese("brie",
                                         15);
        final DefaultFactHandle h1 = new DefaultFactHandle(1,
                                                           cheese);

        rete.assertObject(h1,
                          pctxFactory.createPropagationContext(0,
                                                               PropagationContext.Type.INSERTION,
                                                               null,
                                                               null,
                                                               null),
                          ksession);

        ksession.fireAllRules();

        final Object[] results = (Object[]) sink1.getAsserted().get(0);
    }

    @Test @Ignore
    public void testNotShadowed() {

        Properties properties = new Properties();
        properties.setProperty("drools.shadowProxyExcludes",
                               "org.drools.core.test.model.Cheese");
        RuleBaseConfiguration conf = new RuleBaseConfiguration(new CompositeConfiguration<>(ChainedProperties.getChainedProperties(null).addProperties(properties), null));
        InternalKnowledgeBase kBase = (InternalKnowledgeBase) RuleBaseFactory.newRuleBase(conf);
        buildContext = new BuildContext(kBase, Collections.emptyList());
        final StatefulKnowledgeSessionImpl ksession = new StatefulKnowledgeSessionImpl(1L, kBase);

        // Create a Rete network with ObjectTypeNodes for List, Collection and ArrayList
        final Rete rete = kBase.getRete();
        final EntryPointNode entryPoint = new EntryPointNode(0,
                                                             rete,
                                                             buildContext);
        entryPoint.attach(buildContext);

        final ObjectTypeNode objectTypeNode = new ObjectTypeNode(1,
                                                                 entryPoint,
                                                                 new ClassObjectType(Cheese.class),
                                                                 buildContext);
        objectTypeNode.attach(buildContext);
        final MockObjectSink sink1 = new MockObjectSink();
        objectTypeNode.addObjectSink(sink1);

        // There are no String ObjectTypeNodes, make sure its not propagated

        final Cheese cheese = new Cheese("brie",
                                         15);
        final DefaultFactHandle h1 = new DefaultFactHandle(1,
                                                           cheese);

        rete.assertObject(h1,
                          pctxFactory.createPropagationContext(0,
                                                               PropagationContext.Type.INSERTION,
                                                               null,
                                                               null,
                                                               null),
                          ksession);

        ksession.fireAllRules();

        final Object[] results = (Object[]) sink1.getAsserted().get(0);
    }

    public static class TestBuildContext extends BuildContext {
        InternalKnowledgeBase kBase;

        TestBuildContext(InternalKnowledgeBase kBase) {
            super(kBase, Collections.emptyList());
            this.kBase = kBase;
        }
    }
}
