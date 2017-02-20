/*
 * Copyright 2005 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.core.reteoo;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;

import org.drools.core.RuleBaseConfiguration;
import org.drools.core.base.ClassObjectType;
import org.drools.core.common.DefaultFactHandle;
import org.drools.core.common.PropagationContextFactory;
import org.drools.core.impl.InternalKnowledgeBase;
import org.drools.core.impl.StatefulKnowledgeSessionImpl;
import org.drools.core.reteoo.ReteooBuilder.IdGenerator;
import org.drools.core.reteoo.builder.BuildContext;
import org.drools.core.rule.EntryPointId;
import org.drools.core.spi.PropagationContext;
import org.drools.core.test.model.Cheese;
import org.drools.core.test.model.DroolsTestCase;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.kie.api.runtime.rule.FactHandle;
import org.kie.internal.KnowledgeBaseFactory;

import static org.junit.Assert.*;

public class ReteTest extends DroolsTestCase {
    private PropagationContextFactory pctxFactory;
    private InternalKnowledgeBase kBase;
    private BuildContext   buildContext;
    private EntryPointNode entryPoint;

    @Before
    public void setUp() throws Exception {
        this.kBase = (InternalKnowledgeBase) KnowledgeBaseFactory.newKnowledgeBase();

        this.pctxFactory = kBase.getConfiguration().getComponentFactory().getPropagationContextFactory();
        this.buildContext = new BuildContext(kBase);

        this.entryPoint = buildContext.getKnowledgeBase().getRete().getEntryPointNodes().values().iterator().next();;
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
        assertEquals(3,
                     list.size());

        assertTrue(list.contains(objectTypeNode));
        assertTrue(list.contains(stringTypeNode));
    }

    /**
     * Tests that interfaces and parent classes for an asserted  class are  cached, for  quick future iterations
     */
    @Test
    public void testCache() {
        StatefulKnowledgeSessionImpl ksession = (StatefulKnowledgeSessionImpl)kBase.newStatefulKnowledgeSession();

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

        // LinkedList matches two ObjectTypeNodes        
        h1.setObject(new LinkedList());
        rete.assertObject(h1,
                          pctxFactory.createPropagationContext(0,
                                                               PropagationContext.Type.INSERTION,
                                                               null,
                                                               null,
                                                               null),
                          ksession);

        ClassObjectTypeConf conf = (ClassObjectTypeConf) ksession.getObjectTypeConfigurationRegistry().getObjectTypeConf(this.entryPoint.getEntryPoint(), new ArrayList());
        assertLength(3,
                     conf.getObjectTypeNodes());

        conf = (ClassObjectTypeConf) ksession.getObjectTypeConfigurationRegistry().getObjectTypeConf(this.entryPoint.getEntryPoint(), new ArrayList());
        assertLength(3,
                     conf.getObjectTypeNodes());

    }

    /**
     * Test asserts correctly propagate
     *
     * @throws Exception
     */
    @Test
    public void testAssertObject() throws Exception {
        StatefulKnowledgeSessionImpl ksession = (StatefulKnowledgeSessionImpl)kBase.newStatefulKnowledgeSession();

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

        assertLength(0,
                     sink1.getAsserted());

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
        assertLength(1,
                     asserted);

        final Object[] results = (Object[]) asserted.get(0);
        assertSame(list,
                   ((DefaultFactHandle) results[0]).getObject());
    }

    @Test
    public void testAssertObjectWithNoMatchingObjectTypeNode() {
        StatefulKnowledgeSessionImpl ksession = (StatefulKnowledgeSessionImpl)kBase.newStatefulKnowledgeSession();

        final Rete rete = kBase.getRete();
        assertEquals(1,
                     rete.getObjectTypeNodes().size());

        List list = new ArrayList();

        ksession.insert(list);
        ksession.fireAllRules();

        assertEquals(1,
                     rete.getObjectTypeNodes().size());
    }

    @Test
    @Ignore
    public void testHierarchy() {
        StatefulKnowledgeSessionImpl ksession = (StatefulKnowledgeSessionImpl)kBase.newStatefulKnowledgeSession();

        final Rete rete = kBase.getRete();
        final IdGenerator idGenerator = kBase.getReteooBuilder().getIdGenerator();

        // Attach a List ObjectTypeNode
        final ObjectTypeNode listOtn = new ObjectTypeNode(idGenerator.getNextId(),
                                                          this.entryPoint,
                                                          new ClassObjectType(List.class),
                                                          buildContext);
        listOtn.attach(buildContext);

        // Will automatically create an ArrayList ObjectTypeNode
        FactHandle handle = ksession.insert(new ArrayList());

        // Check we have three ObjectTypeNodes, List, ArrayList and InitialFactImpl
        assertEquals(3,
                     rete.getObjectTypeNodes().size());

        // double check that the List reference is the same as the one we created, i.e. engine should try and recreate it
        assertSame(listOtn,
                   rete.getObjectTypeNodes(EntryPointId.DEFAULT).get(new ClassObjectType(List.class)));

        // ArrayConf should match two ObjectTypenodes for List and ArrayList
        ClassObjectTypeConf arrayConf = (ClassObjectTypeConf) ksession.getObjectTypeConfigurationRegistry().getObjectTypeConf(this.entryPoint.getEntryPoint(), new ArrayList());
        final ObjectTypeNode arrayOtn = arrayConf.getConcreteObjectTypeNode();
        assertEquals(2,
                     arrayConf.getObjectTypeNodes().length);

        // Check it contains List and ArrayList
        List nodes = Arrays.asList(arrayConf.getObjectTypeNodes());
        assertEquals(2,
                     nodes.size());
        assertTrue(nodes.contains(arrayOtn));
        assertTrue(nodes.contains(listOtn));

        // Nodes are there, retract the fact so we can check both nodes are populated
        ksession.retract(handle);

        // Add MockSinks so we can track assertions
        final MockObjectSink listSink = new MockObjectSink();
        listOtn.addObjectSink(listSink);

        final MockObjectSink arraySink = new MockObjectSink();
        listOtn.addObjectSink(arraySink);

        ksession.insert(new ArrayList());
        assertEquals(1,
                     listSink.getAsserted().size());
        assertEquals(1,
                     arraySink.getAsserted().size());

        // Add a Collection ObjectTypeNode, so that we can check that the data from ArrayList is sent to it
        final ObjectTypeNode collectionOtn = new ObjectTypeNode(idGenerator.getNextId(),
                                                                this.entryPoint,
                                                                new ClassObjectType(Collection.class),
                                                                buildContext);
        final MockObjectSink collectionSink = new MockObjectSink();
        collectionOtn.addObjectSink(collectionSink);

        collectionOtn.attach(new TestBuildContext(kBase));

        assertEquals(1,
                     collectionSink.getAsserted().size());

        // check that ArrayListConf was updated with the new ObjectTypeNode
        nodes = Arrays.asList(arrayConf.getObjectTypeNodes());
        assertEquals(3,
                     nodes.size());
        assertTrue(nodes.contains(arrayOtn));
        assertTrue(nodes.contains(listOtn));
        assertTrue(nodes.contains(collectionOtn));
    }

    /**
     * All objects deleted from a RootNode must be propagated to all children
     * ObjectTypeNodes.
     */
    @Test
    public void testRetractObject() throws Exception {
        StatefulKnowledgeSessionImpl ksession = (StatefulKnowledgeSessionImpl)kBase.newStatefulKnowledgeSession();

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
        assertLength(0,
                     sink1.getAsserted());
        assertLength(0,
                     sink1.getRetracted());

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
        assertLength(1,
                     retracted);

        final Object[] results = (Object[]) retracted.get(0);
        assertSame(list,
                   ((DefaultFactHandle) results[0]).getObject());
    }

    @Test
    public void testIsShadowed() {
        StatefulKnowledgeSessionImpl ksession = (StatefulKnowledgeSessionImpl)kBase.newStatefulKnowledgeSession();

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
        RuleBaseConfiguration conf = new RuleBaseConfiguration(properties);
        InternalKnowledgeBase kBase = (InternalKnowledgeBase) KnowledgeBaseFactory.newKnowledgeBase(conf);
        buildContext = new BuildContext(kBase);
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
            super(kBase);
            this.kBase = kBase;
        }
    }
}
