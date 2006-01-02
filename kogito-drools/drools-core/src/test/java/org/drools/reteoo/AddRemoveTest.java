package org.drools.reteoo;

import org.drools.DroolsTestCase;
import org.drools.RuleBase;
import org.drools.spi.ClassObjectType;

public class AddRemoveTest extends DroolsTestCase {
    public void testAdd() {
        /*
         * create a RuleBase with a single ObjectTypeNode we attach a
         * MockObjectSink so we can detect assertions and retractions
         */
        Rete rete = new Rete();
        ObjectTypeNode objectTypeNode = new ObjectTypeNode( 0,
                                                            new ClassObjectType( Object.class ),
                                                            rete );
        objectTypeNode.attach();

        MockObjectSink sink = new MockObjectSink();
        objectTypeNode.addObjectSink( sink );
        RuleBase ruleBase = new RuleBaseImpl();
        WorkingMemoryImpl workingMemory = (WorkingMemoryImpl) ruleBase.newWorkingMemory();

        // objectTypeNode.
    }

}
