package org.drools.reteoo;

import java.util.List;

import org.drools.DroolsTestCase;
import org.drools.FactException;
import org.drools.rule.Rule;
import org.drools.spi.ClassObjectType;
import org.drools.spi.ObjectType;
import org.drools.spi.PropagationContext;
import org.drools.util.PrimitiveLongMap;

public class ObjectTypeNodeTest extends DroolsTestCase {

    public void testAttach() throws Exception{
        Rete source = new Rete();

        ObjectType objectType = new ClassObjectType( String.class );

        ObjectTypeNode objectTypeNode = new ObjectTypeNode( 1,
                                                            objectType,
                                                            source );

        assertEquals( 1,
                      objectTypeNode.getId() );

        assertLength( 0,
                      source.getObjectTypeNodes() );

        objectTypeNode.attach();

        assertLength( 1,
                      source.getObjectTypeNodes() );

        assertSame( objectTypeNode,
                    source.getObjectTypeNode( objectType ) );
    }

    public void testAssertObject() throws Exception{
        Rule rule = new Rule( "test-rule" );
        PropagationContext context = new PropagationContextImpl( PropagationContext.ASSERTION,
                                                                 null,
                                                                 null );

        WorkingMemoryImpl workingMemory = new WorkingMemoryImpl( new RuleBaseImpl() );

        Rete source = new Rete();

        ObjectTypeNode objectTypeNode = new ObjectTypeNode( 1,
                                                            new ClassObjectType( String.class ),
                                                            source );

        MockObjectSink sink = new MockObjectSink();
        objectTypeNode.addObjectSink( sink );

        Object string1 = "cheese";

        Object object1 = new Object();

        FactHandleImpl handle1 = new FactHandleImpl( 1 );
        FactHandleImpl handle2 = new FactHandleImpl( 2 );

        workingMemory.putObject( handle1,
                                 string1 );

        workingMemory.putObject( handle2,
                                 object1 );

        /* should assert as ObjectType matches */
        objectTypeNode.assertObject( string1,
                                     handle1,
                                     context,
                                     workingMemory );

        /* shouldn't assert as ObjectType does not match */
        objectTypeNode.assertObject( object1,
                                     handle2,
                                     context,
                                     workingMemory );

        /* make sure just string1 was asserted */
        List asserted = sink.getAsserted();
        assertLength( 1,
                      asserted );
        assertSame( string1,
                    ((Object[]) asserted.get( 0 ))[0] );

        /* check asserted object was added to memory */
        PrimitiveLongMap memory = (PrimitiveLongMap) workingMemory.getNodeMemory( objectTypeNode );
        assertEquals( 1,
                      memory.size() );
        assertSame( handle1,
                    memory.get( handle1.getId() ) );
    }

    public void testMemory(){
        WorkingMemoryImpl workingMemory = new WorkingMemoryImpl( new RuleBaseImpl() );

        ObjectTypeNode objectTypeNode = new ObjectTypeNode( 1,
                                                            new ClassObjectType( String.class ),
                                                            new Rete() );

        PrimitiveLongMap memory = (PrimitiveLongMap) workingMemory.getNodeMemory( objectTypeNode );

        assertNotNull( memory );
    }

    public void testRetractObject() throws Exception{
        Rule rule = new Rule( "test-rule" );
        PropagationContext context = new PropagationContextImpl( PropagationContext.ASSERTION,
                                                                 null,
                                                                 null );

        WorkingMemoryImpl workingMemory = new WorkingMemoryImpl( new RuleBaseImpl() );

        Rete source = new Rete();

        ObjectTypeNode objectTypeNode = new ObjectTypeNode( 1,
                                                            new ClassObjectType( String.class ),
                                                            source );

        MockObjectSink sink = new MockObjectSink();
        objectTypeNode.addObjectSink( sink );

        Object string1 = "cheese";

        Object object1 = new Object();

        FactHandleImpl handle1 = new FactHandleImpl( 1 );
        FactHandleImpl handle2 = new FactHandleImpl( 2 );

        workingMemory.putObject( handle1,
                                 string1 );

        workingMemory.putObject( handle2,
                                 object1 );

        /* should assert as ObjectType matches */
        objectTypeNode.assertObject( string1,
                                     handle1,
                                     context,
                                     workingMemory );
        /* check asserted object was added to memory */
        PrimitiveLongMap memory = (PrimitiveLongMap) workingMemory.getNodeMemory( objectTypeNode );
        assertEquals( 1,
                      memory.size() );

        /* shouldn't retract as ObjectType does not match */
        objectTypeNode.retractObject( handle2,
                                      context,
                                      workingMemory );
        /* check asserted object was not removed from memory */
        assertEquals( 1,
                      memory.size() );

        /* should retract as ObjectType matches */
        objectTypeNode.retractObject( handle1,
                                      context,
                                      workingMemory );
        /* check asserted object was removed from memory */
        assertEquals( 0,
                      memory.size() );

        /* make sure its just the handle1 for string1 that was propagated */
        List retracted = sink.getRetracted();
        assertLength( 1,
                      retracted );
        assertSame( handle1,
                    ((Object[]) retracted.get( 0 ))[0] );
    }

    public void testUpdateNewNode() throws FactException{
        // Tests that when new child is added only the last added child is
        // updated
        // When the attachingNewNode flag is set
        PropagationContext context = new PropagationContextImpl( PropagationContext.ASSERTION,
                                                                 null,
                                                                 null );

        WorkingMemoryImpl workingMemory = new WorkingMemoryImpl( new RuleBaseImpl() );

        Rete source = new Rete();

        ObjectTypeNode objectTypeNode = new ObjectTypeNode( 1,
                                                            new ClassObjectType( String.class ),
                                                            source );

        MockObjectSink sink1 = new MockObjectSink();
        objectTypeNode.addObjectSink( sink1 );

        Object string1 = "cheese";

        Object string2 = "bread";

        FactHandleImpl handle1 = new FactHandleImpl( 1 );
        FactHandleImpl handle2 = new FactHandleImpl( 2 );

        workingMemory.putObject( handle1,
                                 string1 );

        workingMemory.putObject( handle2,
                                 string2 );

        objectTypeNode.assertObject( string1,
                                     handle1,
                                     context,
                                     workingMemory );

        objectTypeNode.assertObject( string2,
                                     handle2,
                                     context,
                                     workingMemory );

        assertLength( 2,
                      sink1.getAsserted() );

        objectTypeNode.attachingNewNode = true;

        MockObjectSink sink2 = new MockObjectSink();
        objectTypeNode.addObjectSink( sink2 );

        assertLength( 0,
                      sink2.getAsserted() );

        objectTypeNode.updateNewNode( workingMemory,
                                      null );

        objectTypeNode.attachingNewNode = false;

        assertLength( 2,
                      sink1.getAsserted() );
        assertLength( 2,
                      sink2.getAsserted() );

        Object string3 = "water";

        FactHandleImpl handle3 = new FactHandleImpl( 3 );
        workingMemory.putObject( handle3,
                                 string3 );

        objectTypeNode.assertObject( string3,
                                     handle3,
                                     context,
                                     workingMemory );

        assertLength( 3,
                      sink1.getAsserted() );

        assertLength( 3,
                      sink2.getAsserted() );

    }

}
