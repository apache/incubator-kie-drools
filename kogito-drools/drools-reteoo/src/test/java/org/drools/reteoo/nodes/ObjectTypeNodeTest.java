/*
 * Copyright 2005 JBoss Inc
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

package org.drools.reteoo.nodes;

import org.drools.core.RuleBaseConfiguration;
import org.drools.core.base.ClassObjectType;
import org.drools.core.common.DefaultFactHandle;
import org.drools.core.common.InternalFactHandle;
import org.drools.core.common.PhreakPropagationContext;
import org.drools.core.common.PropagationContextFactory;
import org.drools.core.impl.InternalKnowledgeBase;
import org.drools.core.impl.StatefulKnowledgeSessionImpl;
import org.drools.core.reteoo.EntryPointNode;
import org.drools.core.reteoo.MockObjectSink;
import org.drools.core.reteoo.ObjectTypeNode;
import org.drools.core.reteoo.ObjectTypeNode.ObjectTypeNodeMemory;
import org.drools.core.reteoo.Rete;
import org.drools.core.reteoo.ReteooBuilder.IdGenerator;
import org.drools.core.reteoo.builder.BuildContext;
import org.drools.core.rule.EntryPointId;
import org.drools.core.spi.ObjectType;
import org.drools.core.spi.PropagationContext;
import org.drools.core.test.model.Cheese;
import org.drools.core.test.model.DroolsTestCase;
import org.drools.core.test.model.Person;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.kie.internal.KnowledgeBaseFactory;

import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

@Ignore
public class ObjectTypeNodeTest extends DroolsTestCase {
    private PropagationContextFactory pctxFactory;
    private InternalKnowledgeBase kBase;
    private BuildContext              buildContext;
    private EntryPointNode entryPoint;

    @Before
    public void setUp() throws Exception {
        this.kBase = (InternalKnowledgeBase) KnowledgeBaseFactory.newKnowledgeBase();
        this.buildContext = new BuildContext(kBase, kBase.getReteooBuilder().getIdGenerator());
        this.entryPoint = new EntryPointNode(0,
                                             this.kBase.getRete(),
                                             buildContext);
        this.entryPoint.attach(buildContext);
        pctxFactory = kBase.getConfiguration().getComponentFactory().getPropagationContextFactory();
    }

    @Test
    public void testAttach() throws Exception {
        IdGenerator idGenerator = kBase.getReteooBuilder().getIdGenerator();

        final Rete source = this.kBase.getRete();

        final ObjectType objectType = new ClassObjectType(String.class);

        int id = idGenerator.getNextId();
        final ObjectTypeNode objectTypeNode = new ObjectTypeNode(id,
                                                                 this.entryPoint,
                                                                 objectType,
                                                                 buildContext);

        assertEquals(id,
                     objectTypeNode.getId());

        Map<ObjectType, ObjectTypeNode> map = source.getObjectTypeNodes(EntryPointId.DEFAULT);

        assertEquals(0,
                     map.size());

        objectTypeNode.attach(buildContext);

        assertEquals(1,
                     map.size());

        assertSame(objectTypeNode,
                   map.get(objectType));
    }

    @Test
    public void testAssertObject() throws Exception {
        final PropagationContext context = pctxFactory.createPropagationContext(0,
                                                                                PropagationContext.INSERTION,
                                                                                null,
                                                                                null,
                                                                                null);

        IdGenerator idGenerator = kBase.getReteooBuilder().getIdGenerator();

        StatefulKnowledgeSessionImpl ksession = (StatefulKnowledgeSessionImpl)kBase.newStatefulKnowledgeSession();

        final Rete source = kBase.getRete();

        final EntryPointNode entryPoint = new EntryPointNode(0,
                                                             source,
                                                             buildContext);
        entryPoint.attach(buildContext);

        final ObjectTypeNode objectTypeNode = new ObjectTypeNode(idGenerator.getNextId(),
                                                                 entryPoint,
                                                                 new ClassObjectType(String.class),
                                                                 buildContext);

        final MockObjectSink sink = new MockObjectSink();
        objectTypeNode.addObjectSink(sink);

        final Object string1 = "cheese";

        final InternalFactHandle handle1 = (InternalFactHandle) ksession.insert(string1);

        // should assert as ObjectType matches
        objectTypeNode.assertObject(handle1,
                                    context,
                                    ksession);

        // make sure just string1 was asserted 
        final List asserted = sink.getAsserted();
        assertLength(1,
                     asserted);
        assertSame(string1,
                   ksession.getObject((DefaultFactHandle) ((Object[]) asserted.get(0))[0]));

        // check asserted object was added to memory
        final ObjectTypeNodeMemory memory = (ObjectTypeNodeMemory) ksession.getNodeMemory(objectTypeNode);
        assertEquals(1,
                     memory.memory.size());
        assertTrue(memory.memory.contains(handle1));
    }

    @Test
    public void testAssertObjectSequentialMode() {
        final PropagationContext context = pctxFactory.createPropagationContext(0,
                                                                                PropagationContext.INSERTION,
                                                                                null,
                                                                                null,
                                                                                null);

        RuleBaseConfiguration conf = new RuleBaseConfiguration();
        conf.setPhreakEnabled(false);
        conf.setSequential(true);

        InternalKnowledgeBase kBase = (InternalKnowledgeBase) KnowledgeBaseFactory.newKnowledgeBase(conf);
        buildContext = new BuildContext(kBase, kBase.getReteooBuilder().getIdGenerator());
        buildContext.setObjectTypeNodeMemoryEnabled(false);

        final StatefulKnowledgeSessionImpl workingMemory = new StatefulKnowledgeSessionImpl(1L,
                                                                              kBase);

        final Rete source = kBase.getRete();

        final EntryPointNode entryPoint = new EntryPointNode(0,
                                                             source,
                                                             buildContext);
        entryPoint.attach(buildContext);

        final ObjectTypeNode objectTypeNode = new ObjectTypeNode(1,
                                                                 entryPoint,
                                                                 new ClassObjectType(String.class),
                                                                 buildContext);

        final MockObjectSink sink = new MockObjectSink();
        objectTypeNode.addObjectSink(sink);

        final Object string1 = "cheese";

        final InternalFactHandle handle1 = (InternalFactHandle) workingMemory.insert(string1);

        // should assert as ObjectType matches
        objectTypeNode.assertObject(handle1,
                                    context,
                                    workingMemory);

        // make sure just string1 was asserted 
        final List asserted = sink.getAsserted();
        assertLength(1,
                     asserted);
        assertSame(string1,
                   workingMemory.getObject((DefaultFactHandle) ((Object[]) asserted.get(0))[0]));

        // it's sequential, so check the asserted object was not added to the node memory
        final ObjectTypeNodeMemory memory = (ObjectTypeNodeMemory) workingMemory.getNodeMemory(objectTypeNode);
        assertEquals(0,
                     memory.memory.size());
    }

    @Test
    public void testMemory() {
        InternalKnowledgeBase kBase = (InternalKnowledgeBase) KnowledgeBaseFactory.newKnowledgeBase();
        IdGenerator idGenerator = kBase.getReteooBuilder().getIdGenerator();

        StatefulKnowledgeSessionImpl ksession = (StatefulKnowledgeSessionImpl)kBase.newStatefulKnowledgeSession();

        final ObjectTypeNode objectTypeNode = new ObjectTypeNode(idGenerator.getNextId(),
                                                                 this.entryPoint,
                                                                 new ClassObjectType(String.class),
                                                                 buildContext);

        final ObjectTypeNodeMemory memory = (ObjectTypeNodeMemory) ksession.getNodeMemory(objectTypeNode);

        assertNotNull(memory);
    }

    @Test
    public void testIsAssignableFrom() {
        InternalKnowledgeBase kBase = (InternalKnowledgeBase) KnowledgeBaseFactory.newKnowledgeBase();
        IdGenerator idGenerator = kBase.getReteooBuilder().getIdGenerator();
        final Rete source = new Rete(kBase);

        ObjectTypeNode objectTypeNode = new ObjectTypeNode(idGenerator.getNextId(),
                                                           this.entryPoint,
                                                           new ClassObjectType(String.class),
                                                           buildContext);

        assertFalse(objectTypeNode.isAssignableFrom(new ClassObjectType(new Object().getClass())));
        assertFalse(objectTypeNode.isAssignableFrom(new ClassObjectType(new Integer(5).getClass())));
        assertTrue(objectTypeNode.isAssignableFrom(new ClassObjectType("string".getClass())));

        objectTypeNode = new ObjectTypeNode(idGenerator.getNextId(),
                                            this.entryPoint,
                                            new ClassObjectType(Object.class),
                                            buildContext);

        assertTrue(objectTypeNode.isAssignableFrom(new ClassObjectType(new Object().getClass())));
        assertTrue(objectTypeNode.isAssignableFrom(new ClassObjectType(new Integer(5).getClass())));
        assertTrue(objectTypeNode.isAssignableFrom(new ClassObjectType("string".getClass())));

    }

    @Test
    public void testRetractObject() throws Exception {
        InternalKnowledgeBase kBase = (InternalKnowledgeBase) KnowledgeBaseFactory.newKnowledgeBase();
        IdGenerator idGenerator = kBase.getReteooBuilder().getIdGenerator();
        final PropagationContext context = pctxFactory.createPropagationContext(0,
                                                                                PropagationContext.INSERTION,
                                                                                null,
                                                                                null,
                                                                                null);

        StatefulKnowledgeSessionImpl ksession = (StatefulKnowledgeSessionImpl)kBase.newStatefulKnowledgeSession();

        final Rete source = new Rete(kBase);

        final ObjectTypeNode objectTypeNode = new ObjectTypeNode(idGenerator.getNextId(),
                                                                 this.entryPoint,
                                                                 new ClassObjectType(String.class),
                                                                 buildContext);

        final MockObjectSink sink = new MockObjectSink();
        objectTypeNode.addObjectSink(sink);

        final Object string1 = "cheese";

        final DefaultFactHandle handle1 = new DefaultFactHandle(1,
                                                                string1);

        // should assert as ObjectType matches
        objectTypeNode.assertObject(handle1,
                                    context,
                                    ksession);
        // check asserted object was added to memory
        final ObjectTypeNodeMemory memory = (ObjectTypeNodeMemory) ksession.getNodeMemory(objectTypeNode);
        assertEquals(1,
                     memory.memory.size());

        // should retract as ObjectType matches
        objectTypeNode.retractObject(handle1,
                                     context,
                                     ksession);
        // check asserted object was removed from memory
        assertEquals(0,
                     memory.memory.size());

        // make sure its just the handle1 for string1 that was propagated
        final List retracted = sink.getRetracted();
        assertLength(1,
                     retracted);
        assertSame(handle1,
                   ((Object[]) retracted.get(0))[0]);
    }

    @Test
    public void testUpdateSink() {
        // Tests that when new child is added only the last added child is
        // updated
        // When the attachingNewNode flag is set
        final PropagationContext context = pctxFactory.createPropagationContext(0,
                                                                                PropagationContext.INSERTION,
                                                                                null,
                                                                                null,
                                                                                null);
        InternalKnowledgeBase kBase = (InternalKnowledgeBase) KnowledgeBaseFactory.newKnowledgeBase();
        final StatefulKnowledgeSessionImpl workingMemory = new StatefulKnowledgeSessionImpl(1L, kBase);

        final Rete source = new Rete(kBase);

        final ObjectTypeNode objectTypeNode = new ObjectTypeNode(1,
                                                                 this.entryPoint,
                                                                 new ClassObjectType(String.class),
                                                                 buildContext);

        final MockObjectSink sink1 = new MockObjectSink();
        objectTypeNode.addObjectSink(sink1);

        final Object string1 = "cheese";

        final Object string2 = "bread";

        final DefaultFactHandle handle1 = new DefaultFactHandle(1,
                                                                string1);
        final DefaultFactHandle handle2 = new DefaultFactHandle(2,
                                                                string2);

        objectTypeNode.assertObject(handle1,
                                    context,
                                    workingMemory);

        objectTypeNode.assertObject(handle2,
                                    context,
                                    workingMemory);

        assertEquals(2,
                     sink1.getAsserted().size());

        final MockObjectSink sink2 = new MockObjectSink();
        objectTypeNode.addObjectSink(sink2);

        assertEquals(0,
                     sink2.getAsserted().size());

        objectTypeNode.updateSink(sink2,
                                  new PhreakPropagationContext(),
                                  workingMemory);

        assertEquals(2,
                     sink2.getAsserted().size());

        final Object string3 = "water";

        final DefaultFactHandle handle3 = new DefaultFactHandle(3,
                                                                string3);

        objectTypeNode.assertObject(handle3,
                                    context,
                                    workingMemory);

        assertEquals(3,
                     sink1.getAsserted().size());

        assertEquals(3,
                     sink2.getAsserted().size());

    }

    @Test
    public void testAssertObjectWithShadowEnabled() throws Exception {

        InternalKnowledgeBase kBase = (InternalKnowledgeBase) KnowledgeBaseFactory.newKnowledgeBase();
        final StatefulKnowledgeSessionImpl workingMemory = new StatefulKnowledgeSessionImpl(1L, kBase);

        final Rete source = kBase.getRete();

        final EntryPointNode entryPoint = new EntryPointNode(0,
                                                             source,
                                                             buildContext);
        entryPoint.attach(buildContext);

        final ObjectTypeNode objectTypeNode = new ObjectTypeNode(1,
                                                                 this.entryPoint,
                                                                 new ClassObjectType(Cheese.class),
                                                                 buildContext);

        final MockObjectSink sink = new MockObjectSink();
        objectTypeNode.addObjectSink(sink);
        entryPoint.addObjectSink(objectTypeNode);

        final Object cheese = new Cheese("muzzarela",
                                         5);

        final InternalFactHandle handle1 = (InternalFactHandle) workingMemory.insert(cheese);

        // make sure just string1 was asserted 
        final List asserted = sink.getAsserted();
        assertLength(1,
                     asserted);
        assertEquals(cheese,
                     ((InternalFactHandle) ((Object[]) asserted.get(0))[0]).getObject());

        // check asserted object was added to memory
        final ObjectTypeNodeMemory memory = (ObjectTypeNodeMemory) workingMemory.getNodeMemory(objectTypeNode);
        assertEquals(1,
                     memory.memory.size());
        assertTrue(memory.memory.contains(handle1));
    }

    @Test
    public void testAssertObjectWithShadowEnabledNoDefaultConstr() throws Exception {
        final PropagationContext context = pctxFactory.createPropagationContext(0,
                                                                                PropagationContext.INSERTION,
                                                                                null,
                                                                                null,
                                                                                null);

        InternalKnowledgeBase kBase = (InternalKnowledgeBase) KnowledgeBaseFactory.newKnowledgeBase();
        IdGenerator idGenerator = kBase.getReteooBuilder().getIdGenerator();
        StatefulKnowledgeSessionImpl ksession = (StatefulKnowledgeSessionImpl)kBase.newStatefulKnowledgeSession();

        final Rete source = kBase.getRete();

        final EntryPointNode entryPoint = new EntryPointNode(0,
                                                             source,
                                                             buildContext);
        entryPoint.attach(buildContext);

        final ObjectTypeNode objectTypeNode = new ObjectTypeNode(idGenerator.getNextId(),
                                                                 entryPoint,
                                                                 new ClassObjectType(Person.class),
                                                                 buildContext);

        final MockObjectSink sink = new MockObjectSink();
        objectTypeNode.addObjectSink(sink);

        final Object person = new Person("bob",
                                         25);

        final InternalFactHandle handle1 = (InternalFactHandle) ksession.insert(person);

        // should assert as ObjectType matches
        objectTypeNode.assertObject(handle1,
                                    context,
                                    ksession);

        // make sure just string1 was asserted 
        final List asserted = sink.getAsserted();
        assertLength(1,
                     asserted);
        assertEquals(((InternalFactHandle) ((Object[]) asserted.get(0))[0]).getObject(),
                     person);

        // check asserted object was added to memory
        final ObjectTypeNodeMemory memory = (ObjectTypeNodeMemory) ksession.getNodeMemory(objectTypeNode);
        assertEquals(1,
                     memory.memory.size());
        assertTrue(memory.memory.contains(handle1));
    }

}
