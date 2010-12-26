/**
 * Copyright 2010 JBoss Inc
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

package org.drools.reteoo.test;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

import org.drools.FactHandle;
import org.drools.Person;
import org.drools.RuleBaseConfiguration;
import org.drools.RuleBaseFactory;
import org.drools.WorkingMemory;
import org.drools.base.ClassObjectType;
import org.drools.common.DefaultFactHandle;
import org.drools.common.InternalFactHandle;
import org.drools.common.InternalWorkingMemory;
import org.drools.common.NodeMemory;
import org.drools.common.PropagationContextImpl;
import org.drools.reteoo.BetaMemory;
import org.drools.reteoo.JoinNode;
import org.drools.reteoo.LeftInputAdapterNode;
import org.drools.reteoo.LeftTuple;
import org.drools.reteoo.ObjectTypeNode;
import org.drools.reteoo.ReteooRuleBase;
import org.drools.reteoo.ReteooWorkingMemory;
import org.drools.reteoo.RuleTerminalNode;
import org.drools.reteoo.test.dsl.DslStep;
import org.drools.reteoo.test.dsl.NodeTestDef;
import org.drools.reteoo.test.dsl.NodeTestCase;
import org.drools.reteoo.test.dsl.NodeTestCaseResult;
import org.drools.reteoo.test.dsl.NodeTestCaseResult.NodeTestResult;
import org.drools.reteoo.test.dsl.NodeTestCaseResult.Result;
import org.drools.rule.Declaration;
import org.drools.spi.PropagationContext;

public class ReteDslTestEngineTest {

    @Test
    public void testDslCommandBuilder() {
        InputStream stream = getClass().getResourceAsStream( "DslTestBuilder.testCase" );
        assertNotNull( stream );
        NodeTestCase test = null;
        try {
            test = ReteDslTestEngine.compile( stream );
        } catch ( Exception e ) {
            e.printStackTrace();
            fail( "Unexpected Exception: " + e.getMessage() );
        }
        assertEquals( "Test Case Name",
                      test.getName() );

        checkSetup( test );

        assertEquals( 0,
                      test.getTearDown().size() );

        List<NodeTestDef> tests = test.getTests();
        assertEquals( 2,
                      tests.size() );

        NodeTestDef test1 = tests.get( 0 );
        assertEquals( "test 1",
                      test1.getName() );
        assertEquals( 19,
                      test1.getLine() );
        DslStep[] steps = test1.getSteps().toArray( new DslStep[0] ); 
        assertEquals( 6, 
                      steps.length );
        
        // step 0
        int step = 0;
        assertEquals( "assert", 
                      steps[step].getName() );
        assertEquals( 1, steps[step].getCommands().size() );
        checkCommand( new String[] { "left", "[h0]" },
                   steps[step].getCommands().get( 0 ) );
        
        // step 1
        step = 1;
        assertEquals( "col", 
                      steps[step].getName() );
        assertEquals( 1, steps[step].getCommands().size() );
        checkCommand( new String[] { "leftMemory", "[[h0]]" },
                   steps[step].getCommands().get( 0 ) );
        
        // step 2
        step = 2;
        assertEquals( "sink", 
                      steps[step].getName() );
        assertEquals( 2, steps[step].getCommands().size() );
        checkCommand( new String[] { "verify", "assert", "count", "1" },
                   steps[step].getCommands().get( 0 ) );
        checkCommand( new String[] { "verify", "assert", "tuple0[1]", "is(empty())" },
                   steps[step].getCommands().get( 1 ) );

        // Another test
        test1 = tests.get( 1 );
        assertEquals( "another test",
                      test1.getName() );
        assertEquals( 35,
                      test1.getLine() );
        steps = test1.getSteps().toArray( new DslStep[0] ); 
        assertEquals( 3, 
                      steps.length );
        
        // step 0
        step = 0;
        assertEquals( "assert", 
                      steps[step].getName() );
        assertEquals( 1, steps[step].getCommands().size() );
        checkCommand( new String[] { "left", "[h2]" },
                   steps[step].getCommands().get( 0 ) );
        
        // step 1
        step = 1;
        assertEquals( "col", 
                      steps[step].getName() );
        assertEquals( 1, steps[step].getCommands().size() );
        checkCommand( new String[] { "leftMemory", "[[h2]]" },
                   steps[step].getCommands().get( 0 ) );
        
        
        
    }

    private void checkCommand(String[] expected,
                           String[] actual) {
        assertEquals( expected.length, actual.length );
        for( int i = 0; i < expected.length; i++ ) {
            assertEquals( expected[i], actual[i] );
        }
    }

    private void checkSetup(NodeTestCase test) {
        /**
         * SETUP
         */
        DslStep[] steps = test.getSetup().toArray( new DslStep[0] );
        assertEquals( 6,
                      steps.length );

        // step 0
        int step = 0;
        assertEquals( "LeftTupleSource",
                      steps[step].getName() );
        assertEquals( 4,
                      steps[step].getLine() );
        assertEquals( 1,
                      steps[step].getCommands().size() );
        assertEquals( 1,
                      steps[step].getCommands().get( 0 ).length );
        assertEquals( "left",
                      steps[step].getCommands().get( 0 )[0] );
        // step 1
        step = 1;
        assertEquals( "ObjectSource",
                      steps[step].getName() );
        assertEquals( 5,
                      steps[step].getLine() );
        assertEquals( 1,
                      steps[step].getCommands().size() );
        assertEquals( 1,
                      steps[step].getCommands().get( 0 ).length );
        assertEquals( "right",
                      steps[step].getCommands().get( 0 )[0] );
        // step 3
        step = 2;
        assertEquals( "LeftTupleSink",
                      steps[step].getName() );
        assertEquals( 6,
                      steps[step].getLine() );
        assertEquals( 1,
                      steps[step].getCommands().size() );
        assertEquals( 1,
                      steps[step].getCommands().get( 0 ).length );
        assertEquals( "sink",
                      steps[step].getCommands().get( 0 )[0] );

        // step 3
        step = 3;
        assertEquals( "CollectNode",
                      steps[step].getName() );
        assertEquals( 7,
                      steps[step].getLine() );
        assertEquals( 2,
                      steps[step].getCommands().size() );
        assertEquals( 4,
                      steps[step].getCommands().get( 0 ).length );
        assertEquals( "col",
                      steps[step].getCommands().get( 0 )[0] );
        assertEquals( "left",
                      steps[step].getCommands().get( 0 )[1] );
        assertEquals( "right",
                      steps[step].getCommands().get( 0 )[2] );
        assertEquals( "java.util.ArrayList",
                      steps[step].getCommands().get( 0 )[3] );
        assertEquals( 4,
                      steps[step].getCommands().get( 1 ).length );
        assertEquals( "source",
                      steps[step].getCommands().get( 1 )[0] );
        assertEquals( "type",
                      steps[step].getCommands().get( 1 )[1] );
        assertEquals( "==",
                      steps[step].getCommands().get( 1 )[2] );
        assertEquals( "l1",
                      steps[step].getCommands().get( 1 )[3] );

        // step 4 
        step = 4;
        assertEquals( "Binding",
                      steps[step].getName() );
        assertEquals( 10,
                      steps[step].getLine() );
        assertEquals( 1,
                      steps[step].getCommands().size() );
        assertEquals( 4,
                      steps[step].getCommands().get( 0 ).length );
        assertEquals( "l1",
                      steps[step].getCommands().get( 0 )[0] );
        assertEquals( "0",
                      steps[step].getCommands().get( 0 )[1] );
        assertEquals( "org.drools.Person",
                      steps[step].getCommands().get( 0 )[2] );
        assertEquals( "likes",
                      steps[step].getCommands().get( 0 )[3] );

        // step 5 
        step = 5;
        assertEquals( "Facts",
                      steps[step].getName() );
        assertEquals( 12,
                      steps[step].getLine() );
        assertEquals( 1,
                      steps[step].getCommands().size() );
        assertEquals( 6,
                      steps[step].getCommands().get( 0 ).length );
        assertEquals( "org.drools.Person('darth', 35, \"brie\")",
                      steps[step].getCommands().get( 0 )[0] );
        assertEquals( "org.drools.Cheese('brie', 12)",
                      steps[step].getCommands().get( 0 )[3] );
    }

    
    @Test
    public void testObjectTypeNodeStep() throws Exception {
        String str = "TestCase 'testOTN'\nTest 'dummy'\n";
        str += "ObjectTypeNode :\n";
        str += "otn1, java.lang.Integer;\n";

        NodeTestResult result = executeTest( str );

        ObjectTypeNode otn1 = (ObjectTypeNode) result.context.get( "otn1" );
        assertNotNull( otn1 );

        assertEquals( new ClassObjectType( Integer.class ),
                      otn1.getObjectType() );
    }

    @Test
    public void testLeftInputAdapterNodeStep() throws Exception {
        String str = "TestCase 'testOTN'\nTest 'dummy'\n";
        str += "ObjectTypeNode:\n";
        str += "    otn1, java.lang.Integer;\n";
        str += "LeftInputAdapterNode:\n";
        str += "    lian0, otn1;";

        NodeTestResult result = executeTest( str );

        ObjectTypeNode otn1 = (ObjectTypeNode) result.context.get( "otn1" );
        LeftInputAdapterNode lian0 = (LeftInputAdapterNode) result.context.get( "lian0" );
        assertNotNull( lian0 );

        assertSame( lian0,
                    otn1.getSinkPropagator().getSinks()[0] );
    }

    @Test
    public void testBindingStep() throws Exception {
        String str = "TestCase 'testOTN'\nTest 'dummy'\n";
        str += "Binding:\n";
        str += "     p1, 0, java.lang.Integer, intValue;\n";

        NodeTestResult result = executeTest( str );
        Declaration p1 = (Declaration) result.context.get( "p1" );
        assertNotNull( p1 );
    }

    @Test
    public void testJoinNodeStep() throws Exception {
        String str = "TestCase 'testOTN'\nTest 'dummy'\n";
        str += "ObjectTypeNode:\n";
        str += "    otn1, java.lang.Integer;\n";
        str += "LeftInputAdapterNode:\n";
        str += "    lian0, otn1;\n";
        str += "ObjectTypeNode:\n";
        str += "    otn2, java.lang.Integer;\n";
        str += "Binding:\n";
        str += "     p1, 0, java.lang.Integer, intValue;\n";
        str += "JoinNode:\n";
        str += "    join1, lian0, otn2;\n";
        str += "    intValue, !=, p1;\n";

        NodeTestResult result = executeTest( str );
        Map<String, Object> map = result.context;

        JoinNode join1 = (JoinNode) map.get( "join1" );
        assertNotNull( join1 );

        PropagationContext context = new PropagationContextImpl( 0,
                                                                 PropagationContext.ASSERTION,
                                                                 null,
                                                                 null,
                                                                 null );
        ReteooWorkingMemory workingMemory = new ReteooWorkingMemory( 1,
                                                                     (ReteooRuleBase) RuleBaseFactory.newRuleBase() );

        BetaMemory memory = (BetaMemory) workingMemory.getNodeMemory( join1 );

        final DefaultFactHandle f0 = new DefaultFactHandle( 0,
                                                            0 );
        final LeftTuple tuple0 = new LeftTuple( f0,
                                                join1,
                                                true );

        // assert tuple, should add one to left memory
        join1.assertLeftTuple( tuple0,
                               context,
                               workingMemory );
        // check memories, left memory is populated, right memory is emptys
        assertEquals( 1,
                      memory.getLeftTupleMemory().size() );
        assertEquals( 0,
                      memory.getRightTupleMemory().size() );

        // assert tuple, should add left memory should be 2
        final DefaultFactHandle f1 = new DefaultFactHandle( 1,
                                                            0 );
        final LeftTuple tuple1 = new LeftTuple( f1,
                                                join1,
                                                true );
        join1.assertLeftTuple( tuple1,
                               context,
                               workingMemory );
        assertEquals( 2,
                      memory.getLeftTupleMemory().size() );

        LeftTuple leftTuple = memory.getLeftTupleMemory().getFirst( (LeftTuple) null );
        assertEquals( tuple0,
                      leftTuple );
        assertEquals( tuple1,
                      leftTuple.getNext() );
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testFactsStep() throws Exception {
        String str = "TestCase 'testOTN'\nTest 'dummy'\n";
        str += "Facts:\n";
        str += "    1, 2, 'hello',\n";
        str += "    'good bye', new java.util.ArrayList();\n";

        NodeTestResult result = executeTest( str );
        Map<String, Object> map = result.context;

        WorkingMemory wm = (WorkingMemory) map.get( "WorkingMemory" );
        List<InternalFactHandle> handles = (List<InternalFactHandle>) map.get( "Handles" );

        assertNotNull( wm );
        assertNotNull( handles );

        assertEquals( 5,
                      handles.size() );

        assertEquals( 1,
                      handles.get( 0 ).getObject() );
        assertEquals( 2,
                      handles.get( 1 ).getObject() );
        assertEquals( "hello",
                      handles.get( 2 ).getObject() );
        assertEquals( "good bye",
                      handles.get( 3 ).getObject() );
        assertEquals( new ArrayList<FactHandle>(),
                      handles.get( 4 ).getObject() );
    }
    
    @SuppressWarnings("unchecked")
    @Test
    public void testWithStep() throws IOException {
        String str = "TestCase 'testOTN'\nTest 'dummy'\n";
        str += "Facts:\n";
        str += "    1, 2, new org.drools.Person('darth', 35),\n";
        str += "    'good bye', new java.util.ArrayList();\n";
        str += "With:\n";
        str += "    h2, age = 36, city = 'la',\n";
        str += "       state = 'ca';\n";
        str += "    h4, add( 2 );\n";

        NodeTestResult result = executeTest( str );
        Map<String, Object> map = result.context;

        WorkingMemory wm = (WorkingMemory) map.get( "WorkingMemory" );
        List<InternalFactHandle> handles = (List<InternalFactHandle>) map.get( "Handles" );

        assertNotNull( wm );
        assertNotNull( handles );

        assertEquals( 5,
                      handles.size() );
        Person p = new Person( "darth", 36);
        p.setAge( 36 );
        p.setCity( "la" );
        p.setState( "ca" );
        
        List<Integer> list = new ArrayList<Integer>();
        list.add( 2 );

        assertEquals( 1,
                      handles.get( 0 ).getObject() );
        assertEquals( 2,
                      handles.get( 1 ).getObject() );        
        assertEquals( p,
                      handles.get( 2 ).getObject() );
        assertEquals( "good bye",
                      handles.get( 3 ).getObject() );
        assertEquals( list,
                      handles.get( 4 ).getObject() );
    }    

    @Test
    public void testBetaNodeAssertOperations() throws IOException {
        String str = "TestCase 'testOTN'\nTest 'dummy'\n";
        str += "ObjectTypeNode:\n";
        str += "    otn1, java.lang.Integer;\n";
        str += "LeftInputAdapterNode:\n";
        str += "    lian0, otn1;\n";
        str += "ObjectTypeNode:\n";
        str += "    otn2, java.lang.Integer;\n";
        str += "Binding:\n";
        str += "     p1, 0, java.lang.Integer, intValue;\n";
        str += "JoinNode:\n";
        str += "    join1, lian0, otn2;\n";
        str += "    intValue, !=, p1;\n";
        str += "Facts:\n";
        str += "    0, 1, 2, 3;\n";
        str += "assert:\n";
        str += "    otn2,[h0, h2];\n";
        str += "    otn1,[h1, h3];\n";

        NodeTestResult result = executeTest( str );
        Map<String, Object> map = result.context;

        InternalWorkingMemory wm = (InternalWorkingMemory) map.get( "WorkingMemory" );

        JoinNode join1 = (JoinNode) map.get( "join1" );

        BetaMemory memory = (BetaMemory) wm.getNodeMemory( join1 );
        assertEquals( 2,
                      memory.getRightTupleMemory().size() );

        assertEquals( 2,
                      memory.getLeftTupleMemory().size() );
    }

    @Test
    public void testBetaNodeRetractOperations() throws IOException {
        String str = "TestCase 'testOTN'\nTest 'dummy'\n";
        str += "ObjectTypeNode:\n";
        str += "    otn1, java.lang.Integer;\n";
        str += "LeftInputAdapterNode:\n";
        str += "    lian0, otn1;\n";
        str += "ObjectTypeNode:\n";
        str += "    otn2, java.lang.Integer;\n";
        str += "Binding:\n";
        str += "     p1, 0, java.lang.Integer, intValue;\n";
        str += "JoinNode:\n";
        str += "    join1, lian0, otn2;\n";
        str += "    intValue, !=, p1;\n";
        str += "Facts:\n";
        str += "    0, 1, 2, 3;\n";
        str += "assert:\n";
        str += "    otn1,[h1, h3];\n";
        str += "    otn2,[h0, h2];\n";
        str += "retract:\n";
        str += "    otn1,[h1];\n";
        str += "    otn2,[h2];\n";

        NodeTestResult result = executeTest( str );
        Map<String, Object> map = result.context;

        InternalWorkingMemory wm = (InternalWorkingMemory) map.get( "WorkingMemory" );
        List<InternalFactHandle> handles = (List<InternalFactHandle>) map.get( "Handles" );

        JoinNode join1 = (JoinNode) map.get( "join1" );

        BetaMemory memory = (BetaMemory) wm.getNodeMemory( join1 );
        assertEquals( 1,
                      memory.getRightTupleMemory().size() );

        assertEquals( 1,
                      memory.getLeftTupleMemory().size() );
    }

    @Test
    public void testBetaNodeSimpleMemoryChecks() throws IOException {
        String str = "TestCase 'testOTN'\nTest 'dummy'\n";
        str += "ObjectTypeNode:\n";
        str += "    otn1, java.lang.Integer;\n";
        str += "LeftInputAdapterNode:\n";
        str += "    lian0, otn1;\n";
        str += "ObjectTypeNode:\n";
        str += "    otn2, java.lang.Integer;\n";
        str += "Binding:\n";
        str += "     p1, 0, java.lang.Integer, intValue;\n";
        str += "JoinNode:\n";
        str += "    join1, lian0, otn2;\n";
        str += "    intValue, !=, p1;\n";
        str += "Facts:\n";
        str += "    0, 1, 2, 3;\n";
        str += "assert:\n";
        str += "    otn1,[h1, h3];\n";
        str += "    otn2,[h0, h2];\n";
        str += "join1:\n";
        str += "    leftMemory,[[h1], [h3]];\n";
        str += "    rightMemory,[h0, h2];\n";
        str += "retract:\n";
        str += "    otn1,[h1];\n";
        str += "    otn2,[h2];\n";;
        str += "join1:\n";
        str += "    leftMemory,[ [h3] ];\n";
        str += "    rightMemory,[h0];\n";

        NodeTestResult result = executeTest( str );
        Map<String, Object> map = result.context;

        InternalWorkingMemory wm = (InternalWorkingMemory) map.get( "WorkingMemory" );
        List<InternalFactHandle> handles = (List<InternalFactHandle>) map.get( "Handles" );

        JoinNode join1 = (JoinNode) map.get( "join1" );

        BetaMemory memory = (BetaMemory) wm.getNodeMemory( join1 );
        assertEquals( 1,
                      memory.getRightTupleMemory().size() );

        assertEquals( 1,
                      memory.getLeftTupleMemory().size() );
    }

    @Test
    public void testBetaNodeChainedMemoryChecks() throws IOException {
        String str = "TestCase 'testOTN'\nTest 'dummy'\n";
        str += "ObjectTypeNode:\n";
        str += "    otn1, java.lang.Integer;\n";
        str += "LeftInputAdapterNode:\n";
        str += "    lian0, otn1;\n";
        str += "ObjectTypeNode:\n";
        str += "    otn2, java.lang.Integer;\n";
        str += "ObjectTypeNode:\n";
        str += "    otn3, java.lang.Integer;\n";
        str += "Binding:\n";
        str += "     p1, 0, java.lang.Integer, intValue;\n";
        str += "JoinNode:\n";
        str += "    join1, lian0, otn2;\n";
        str += "    intValue, !=, p1;\n";
        str += "JoinNode:\n";
        str += "    join2, join1, otn3;\n";
        str += "    intValue, !=, p1;\n";
        str += "Facts:\n";
        str += "    0, 1, 2, 3, 4;\n";
        str += "assert:\n";
        str += "    otn1, [h1, h3];\n";
        str += "    otn2, [h0, h2];\n";
        str += "    otn3, [h4];\n";
        str += "join1:\n";
        str += "    leftMemory, [[h1], [h3]];\n";
        str += "    rightMemory, [h0, h2];\n";
        str += "join2:\n";
        str += "    leftMemory, [[h1, h0], [h3, h0],\n";
        str += "                [h1, h2], [h3, h2]];\n";
        str += "    rightMemory, [h4];\n";
        str += "retract:\n";
        str += "    otn1, [h1];\n";
        str += "    otn2, [h2];\n";;
        str += "join1:\n";
        str += "    leftMemory, [ [h3] ];\n";
        str += "    rightMemory, [h0];\n";
        str += "join2:\n";
        str += "    leftMemory,  [[h3, h0]];\n";
        str += "    rightMemory, [h4];\n";

        NodeTestResult result = executeTest( str );
        Map<String, Object> map = result.context;

        InternalWorkingMemory wm = (InternalWorkingMemory) map.get( "WorkingMemory" );
        List<InternalFactHandle> handles = (List<InternalFactHandle>) map.get( "Handles" );

        JoinNode join1 = (JoinNode) map.get( "join1" );

        BetaMemory memory = (BetaMemory) wm.getNodeMemory( join1 );
        assertEquals( 1,
                      memory.getRightTupleMemory().size() );

        assertEquals( 1,
                      memory.getLeftTupleMemory().size() );
    }
    
    @Test
    public void testBetaNodeChainedMemoryWithIndexChecks() throws IOException {
        String str = "TestCase 'testOTN'\nTest 'dummy'\n";
        str += "ObjectTypeNode:\n";
        str += "    otn1, java.lang.Integer;\n";
        str += "LeftInputAdapterNode:\n";
        str += "    lian0, otn1;\n";
        str += "ObjectTypeNode:\n";
        str += "    otn2, java.lang.Integer;\n";
        str += "ObjectTypeNode:\n";
        str += "    otn3, java.lang.Integer;\n";
        str += "Binding:\n";
        str += "     p1, 0, java.lang.Integer, intValue;\n";
        str += "JoinNode:\n";
        str += "    join1, lian0, otn2;\n";
        str += "    intValue, ==, p1;\n";
        str += "JoinNode:\n";
        str += "    join2, join1, otn3;\n";
        str += "    intValue, ==, p1;\n";
        str += "Facts:\n";
        str += "    new Integer(0), new Integer(0), new Integer(0), 2, 4;\n";
        str += "assert:\n";
        str += "    otn1, [h1, h3];\n";
        str += "    otn2, [h0, h2];\n";
        str += "    otn3, [h4];\n";
        str += "join1:\n";
        str += "    leftMemory, [[h1]];\n";
        str += "    leftMemory, [[h3]];\n";        
        str += "    rightMemory, [h0, h2];\n";
        str += "join2:\n";
        str += "    leftMemory, [[h1, h0],\n";
        str += "                [h1, h2]];\n";
        str += "    rightMemory, [h4];\n";
        str += "retract:\n";
        str += "    otn1, [h2];\n";
        str += "    otn2, [h3];\n";
        str += "join1:\n";
        str += "    leftMemory, [ [h1] ];\n";
        str += "    rightMemory, [h0];\n";
        str += "join2:\n";
        str += "    leftMemory,  [[h1, h0]];\n";
        str += "    rightMemory, [h4];\n";

        NodeTestResult result = executeTest( str );
        Map<String, Object> map = result.context;

        InternalWorkingMemory wm = (InternalWorkingMemory) map.get( "WorkingMemory" );
        List<InternalFactHandle> handles = (List<InternalFactHandle>) map.get( "Handles" );

        JoinNode join1 = (JoinNode) map.get( "join1" );

        BetaMemory memory = (BetaMemory) wm.getNodeMemory( join1 );
        assertEquals( 1,
                      memory.getRightTupleMemory().size() );

        assertEquals( 1,
                      memory.getLeftTupleMemory().size() );
    }    
    
    @Test
    public void testBetaNodeModifyOperations() throws IOException {
        String str = "TestCase 'testOTN'\nTest 'dummy'\n";
        str += "ObjectTypeNode:\n";
        str += "    otn1, java.lang.Integer;\n";
        str += "LeftInputAdapterNode:\n";
        str += "    lian0, otn1;\n";
        str += "ObjectTypeNode:\n";
        str += "    otn2, java.lang.Integer;\n";
        str += "ObjectTypeNode:\n";
        str += "    otn3, java.lang.Integer;\n";
        str += "Binding:\n";
        str += "     p1, 0, org.drools.Person, age;\n";
        str += "JoinNode:\n";
        str += "    join1, lian0, otn2;\n";
        str += "    age, ==, p1;\n";
        str += "JoinNode:\n";
        str += "    join2, join1, otn3;\n";
        str += "    age, ==, p1;\n";
        str += "Facts:\n";
        str += "    new org.drools.Person('darth', 35), new org.drools.Person('bobba', 35),\n";
        str += "    new org.drools.Person('yoda', 35), new org.drools.Person('luke', 35),\n";
        str += "    new org.drools.Person('dave', 36);\n";        
        str += "assert:\n";
        str += "    otn1, [h1, h3, h4];\n";
        str += "    otn2, [h0, h2];\n";
        str += "join1:\n";
        str += "    leftMemory, [[h1], [h3]];\n"; // check leftMemory twice, as we have two index buckets
        str += "    leftMemory, [[h4]];\n";
        str += "    rightMemory, [h0, h2];\n";
        str += "join2:\n";
        str += "    leftMemory, [[h1, h0], [h3, h0],\n";
        str += "                [h1, h2], [h3, h2]];\n";
        str += "    rightMemory, [];\n";  
        str += "With:\n";
        str += "    h1, age = 36;\n";       
        str += "modify:\n";
        str += "    otn1, [h1];\n";
        str += "join1:\n";
        str += "    leftMemory, [[h3]];\n";
        str += "    leftMemory, [[h4], [h1]];\n"; // notice it's moved to the new bucket   
        str += "    rightMemory, [h0, h2];\n";
        str += "join2:\n";
        str += "    leftMemory, [[h3, h0],\n";
        str += "                [h3, h2]];\n";
        str += "    rightMemory, [];\n";
        
        NodeTestResult result = executeTest( str );
        Map<String, Object> map = result.context;

        InternalWorkingMemory wm = (InternalWorkingMemory) map.get( "WorkingMemory" );
        List<InternalFactHandle> handles = (List<InternalFactHandle>) map.get( "Handles" );

        JoinNode join1 = (JoinNode) map.get( "join1" );

        BetaMemory memory = (BetaMemory) wm.getNodeMemory( join1 );
        assertEquals( 2,
                      memory.getRightTupleMemory().size() );

        assertEquals( 3,
                      memory.getLeftTupleMemory().size() );  
        
        JoinNode join2 = (JoinNode) map.get( "join2" );

        memory = (BetaMemory) wm.getNodeMemory( join2 );
        assertEquals( 0,
                      memory.getRightTupleMemory().size() );

        assertEquals( 2,
                      memory.getLeftTupleMemory().size() );         
    }
    
    @Test
    public void testNotNodeStep() throws IOException {
        String str = "TestCase 'testOTN'\nTest 'dummy'\n";
        str += "ObjectTypeNode:\n";
        str += "    otn0, org.drools.Person;\n";
        str += "LeftInputAdapterNode:\n";
        str += "    lian0, otn0;\n";
        str += "ObjectTypeNode:\n";
        str += "    otn1, org.drools.Person;\n";
        str += "Binding:\n";
        str += "     p1, 0, org.drools.Person, age;\n";
        str += "NotNode:\n";
        str += "    not0, lian0, otn1;\n";
        str += "    age, !=, p1;\n";
        str += "LeftTupleSink:\n";
        str += "    sink, not0;\n";  
        str += "Facts:\n";
        str += "    new org.drools.Person('darth', 35), new org.drools.Person('bobba', 35);\n";
        str += "assert:\n";
        str += "    otn0, [h0];\n";
        str += "    otn1, [h1];\n";
        str += "sink:\n";
        str += "    verify, assertLeft, count, 1;\n";    
        str += "With:\n";
        str += "    h1, age = 36;\n";
        str += "modify:\n";
        str += "    otn1, [h1];\n";
        str += "sink:\n";
        str += "    verify, retractLeft, count, 1;\n";         

        NodeTestResult result = executeTest( str );
        Map<String, Object> map = result.context;
    }    

    private void print(DslStep[] steps) {
        for ( DslStep command : steps ) {
            System.out.println( command );
        }
    }

    private NodeTestResult executeTest(String str) throws IOException {
        NodeTestCase testCase = ReteDslTestEngine.compile( str );
        if( testCase.hasErrors() ) {
            fail( testCase.getErrors().toString() );
        }

        ReteDslTestEngine tester = new ReteDslTestEngine();
        NodeTestCaseResult testCaseResult = tester.run( testCase, null );
        
        NodeTestResult result = testCaseResult.getResults().get( 0 );
        if( result.result != Result.SUCCESS ) {
            fail( result.getMessages() );
        }
        return result;
    }

    
}
