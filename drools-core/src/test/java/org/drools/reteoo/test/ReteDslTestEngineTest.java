package org.drools.reteoo.test;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.drools.FactHandle;
import org.drools.RuleBaseFactory;
import org.drools.WorkingMemory;
import org.drools.base.ClassObjectType;
import org.drools.common.DefaultFactHandle;
import org.drools.common.InternalFactHandle;
import org.drools.common.InternalWorkingMemory;
import org.drools.common.PropagationContextImpl;
import org.drools.reteoo.BetaMemory;
import org.drools.reteoo.BetaNode;
import org.drools.reteoo.JoinNode;
import org.drools.reteoo.LeftInputAdapterNode;
import org.drools.reteoo.LeftTuple;
import org.drools.reteoo.MockLeftTupleSink;
import org.drools.reteoo.MockObjectSource;
import org.drools.reteoo.MockTupleSource;
import org.drools.reteoo.ObjectTypeNode;
import org.drools.reteoo.ReteooRuleBase;
import org.drools.reteoo.ReteooWorkingMemory;
import org.drools.reteoo.test.ReteDslTestEngine.DslStep;
import org.drools.rule.Declaration;
import org.drools.spi.PropagationContext;

import junit.framework.TestCase;

public class ReteDslTestEngineTest extends TestCase {
    public void testIndentPos() {
        ReteDslTestEngine tester = new ReteDslTestEngine();
        assertEquals( 5,
                      tester.indentPos( "     asdfasdf" ) );
    }

    public void testReaderAsString() {
        String str = "  line1\n";
        str += "   line2\n";

        List<String> lines = ReteDslTestEngine.chunkReader( new StringReader( str ) );
        assertEquals( 2,
                      lines.size() );

        assertEquals( "  line1",
                      lines.get( 0 ) );
        assertEquals( "   line2",
                      lines.get( 1 ) );
    }

    public void testReaderAsStringRemoveLineQuotes() {
        String str = "  line1\n";
        str += "X// some comments\n";
        str += "   line2\n";
        str += "   line3 //some other comments\n";

        List<String> lines = ReteDslTestEngine.chunkReader( new StringReader( str ) );
        assertEquals( 4,
                      lines.size() );

        assertEquals( "  line1",
                      lines.get( 0 ) );
        assertEquals( "X                ",
                      lines.get( 1 ) );
        assertEquals( "   line2",
                      lines.get( 2 ) );
        assertEquals( "   line3                      ",
                      lines.get( 3 ) );
    }

    public void testReaderAsStringRemoveBlockQuotes1() {
        String str = "  line1\n";
        str += " /* some comments*/\n";
        str += "   line2\n";
        str += "   line3 //some other comments\n";

        List<String> lines = ReteDslTestEngine.chunkReader( new StringReader( str ) );
        assertEquals( 4,
                      lines.size() );

        assertEquals( "  line1",
                      lines.get( 0 ) );
        assertEquals( "                   ",
                      lines.get( 1 ) );
        assertEquals( "   line2",
                      lines.get( 2 ) );
        assertEquals( "   line3                      ",
                      lines.get( 3 ) );
    }

    public void testReaderAsStringRemoveBlockQuotes2() {
        String str = "  line1\n";
        str += " /* some comments\n";
        str += "   line2 // nested line comment\n";
        str += "   lin*/e3\n";
        str += "   line4 //some other comments\n";

        List<String> lines = ReteDslTestEngine.chunkReader( new StringReader( str ) );
        assertEquals( 5,
                      lines.size() );

        assertEquals( "  line1",
                      lines.get( 0 ) );
        assertEquals( "                 ",
                      lines.get( 1 ) );
        assertEquals( "                               ",
                      lines.get( 2 ) );
        assertEquals( "        e3",
                      lines.get( 3 ) );
        assertEquals( "   line4                      ",
                      lines.get( 4 ) );
    }

    public void testDslCommandBuilder() {
        InputStream stream = getClass().getResourceAsStream( "JoinNode.data" );
        assertNotNull( stream );
        DslStep[] steps = (DslStep[]) ReteDslTestEngine.buildDslCommands( new InputStreamReader( stream ) ).toArray( new DslStep[0] );
        assertEquals( 14,
                      steps.length );

        assertEquals( 2,
                      steps[0].getLine() );
        assertEquals( "ObjectTypeNode",
                      steps[0].getName() );
        assertEquals( "otn1, java.lang.Integer",
                      steps[0].getCommands().get( 0 ) );

        assertEquals( 4,
                      steps[1].getLine() );
        assertEquals( "LeftInputAdapterNode",
                      steps[1].getName() );
        assertEquals( "lian0, otn1",
                      steps[1].getCommands().get( 0 ) );

        assertEquals( 6,
                      steps[2].getLine() );
        assertEquals( "ObjectTypeNode",
                      steps[2].getName() );
        assertEquals( "otn2, java.lang.Integer",
                      steps[2].getCommands().get( 0 ) );
        assertEquals( 8,
                      steps[3].getLine() );
        assertEquals( "ObjectTypeNode",
                      steps[3].getName() );
        assertEquals( "otn3, java.lang.Integer",
                      steps[3].getCommands().get( 0 ) );

        assertEquals( 12,
                      steps[4].getLine() );
        assertEquals( "Binding",
                      steps[4].getName() );
        assertEquals( "p1, 0, java.lang.Integer, intValue",
                      steps[4].getCommands().get( 0 ) );

        assertEquals( 15,
                      steps[5].getLine() );
        assertEquals( "JoinNode",
                      steps[5].getName() );
        assertEquals( "join1, lian0, otn2",
                      steps[5].getCommands().get( 0 ) );
        assertEquals( "intValue, !=, p1",
                      steps[5].getCommands().get( 1 ) );

        assertEquals( 18,
                      steps[6].getLine() );
        assertEquals( "JoinNode",
                      steps[6].getName() );
        assertEquals( "join2, join1, otn3",
                      steps[6].getCommands().get( 0 ) );
        assertEquals( "intValue, !=, p1",
                      steps[6].getCommands().get( 1 ) );

        assertEquals( 23,
                      steps[7].getLine() );
        assertEquals( "Facts",
                      steps[7].getName() );
        assertEquals( "0, 1, 2, 3, 4",
                      steps[7].getCommands().get( 0 ) );

        assertEquals( 28,
                      steps[8].getLine() );
        assertEquals( "assert",
                      steps[8].getName() );
        assertEquals( "otn1 [h1, h3]",
                      steps[8].getCommands().get( 0 ) );   
        assertEquals( "otn2 [h0, h2]",
                      steps[8].getCommands().get( 1 ) ); 
        assertEquals( "otn3 [h4]",
                      steps[8].getCommands().get( 2 ) );

        assertEquals( 34,
                      steps[9].getLine() );
        assertEquals( "join1",
                      steps[9].getName() );
        assertEquals( "leftMemory [[h1], [h3]]",
                      steps[9].getCommands().get( 0 ) );   
        assertEquals( "rightMemory [h0, h2]",
                      steps[9].getCommands().get( 1 ) );              
        
        assertEquals( 37,
                      steps[10].getLine() );
        assertEquals( "join2",
                      steps[10].getName() );
        assertEquals( "leftMemory [[h1, h0], [h3, h0],[h1, h2], [h3, h2]]",
                      steps[10].getCommands().get( 0 ) );   
        assertEquals( "rightMemory [h4]",
                      steps[10].getCommands().get( 1 ) );       
 
        assertEquals( 41,
                      steps[11].getLine() );
        assertEquals( "retract",
                      steps[11].getName() );
        assertEquals( "otn1 [h1]",
                      steps[11].getCommands().get( 0 ) );   
        assertEquals( "otn2 [h2]",
                      steps[11].getCommands().get( 1 ) );        
        
        assertEquals( 44,
                      steps[12].getLine() );
        assertEquals( "join1",
                      steps[12].getName() );
        assertEquals( "leftMemory [ [h3] ]",
                      steps[12].getCommands().get( 0 ) );   
        assertEquals( "rightMemory [h0]",
                      steps[12].getCommands().get( 1 ) );         
        
        assertEquals( 47,
                      steps[13].getLine() );
        assertEquals( "join2",
                      steps[13].getName() );
        assertEquals( "leftMemory  [[h3, h0]]",
                      steps[13].getCommands().get( 0 ) );   
        assertEquals( "rightMemory [h4]",
                      steps[13].getCommands().get( 1 ) );                  
    }

    public void testObjectTypeNodeStep() {
        String str = "ObjectTypeNode\n";
        str += "    otn1, java.lang.Integer";

        List<DslStep> steps = ReteDslTestEngine.buildDslCommands( new StringReader( str ) );

        ReteDslTestEngine tester = new ReteDslTestEngine();
        Map<String, Object> map = tester.run( steps );
        ObjectTypeNode otn1 = (ObjectTypeNode) map.get( "otn1" );
        assertNotNull( otn1 );

        assertEquals( new ClassObjectType( Integer.class ),
                      otn1.getObjectType() );
    }

    public void testLeftInputAdapterNodeStep() {
        String str = "ObjectTypeNode\n";
        str += "    otn1, java.lang.Integer\n";
        str += "LeftInputAdapterNode\n";
        str += "    lian0, otn1";

        List<DslStep> steps = ReteDslTestEngine.buildDslCommands( new StringReader( str ) );

        ReteDslTestEngine tester = new ReteDslTestEngine();
        Map<String, Object> map = tester.run( steps );
        ObjectTypeNode otn1 = (ObjectTypeNode) map.get( "otn1" );

        LeftInputAdapterNode lian0 = (LeftInputAdapterNode) map.get( "lian0" );
        assertNotNull( lian0 );

        assertSame( lian0,
                    otn1.getSinkPropagator().getSinks()[0] );
    }

    public void testBindingStep() {
        String str = "Binding\n";
        str += "     p1, 0, java.lang.Integer, intValue\n";

        List<DslStep> steps = ReteDslTestEngine.buildDslCommands( new StringReader( str ) );
        //print(steps);

        ReteDslTestEngine tester = new ReteDslTestEngine();
        Map<String, Object> map = tester.run( steps );
        Declaration p1 = (Declaration) map.get( "p1" );
        assertNotNull( p1 );
    }

    public void testJoinNodeStep() {
        String str = "ObjectTypeNode\n";
        str += "    otn1, java.lang.Integer\n";
        str += "LeftInputAdapterNode\n";
        str += "    lian0, otn1\n";
        str += "ObjectTypeNode\n";
        str += "    otn2, java.lang.Integer\n";
        str += "Binding\n";
        str += "     p1, 0, java.lang.Integer, intValue\n";
        str += "JoinNode\n";
        str += "    join1, lian0, otn2\n";
        str += "    intValue, ==, p1\n";

        List<DslStep> steps = ReteDslTestEngine.buildDslCommands( new StringReader( str ) );

        ReteDslTestEngine tester = new ReteDslTestEngine();
        Map<String, Object> map = tester.run( steps );

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

        LeftTuple leftTuple = memory.getLeftTupleMemory().getFirst( null );
        assertEquals( tuple0,
                      leftTuple );
        assertEquals( tuple1,
                      leftTuple.getNext() );
    }

    public void testFactsStep() {
        String str = "Facts\n";
        str += "    1, 2, 'hello'\n";
        str += "    'good bye', new java.util.ArrayList()\n";

        List<DslStep> steps = ReteDslTestEngine.buildDslCommands( new StringReader( str ) );

        ReteDslTestEngine tester = new ReteDslTestEngine();
        Map<String, Object> map = tester.run( steps );

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

    public void testBetaNodeAssertOperations() {
        String str = "ObjectTypeNode\n";
        str += "    otn1, java.lang.Integer\n";
        str += "LeftInputAdapterNode\n";
        str += "    lian0, otn1\n";
        str += "ObjectTypeNode\n";
        str += "    otn2, java.lang.Integer\n";
        str += "Binding\n";
        str += "     p1, 0, java.lang.Integer, intValue\n";
        str += "JoinNode\n";
        str += "    join1, lian0, otn2\n";
        str += "    intValue, !=, p1\n";
        str += "Facts\n";
        str += "    0, 1, 2, 3\n";
        str += "assert\n";
        str += "    otn2 [h0, h2]\n";
        str += "    otn1 [h1, h3]\n";

        List<DslStep> steps = ReteDslTestEngine.buildDslCommands( new StringReader( str ) );

        ReteDslTestEngine tester = new ReteDslTestEngine();
        Map<String, Object> map = tester.run( steps );

        InternalWorkingMemory wm = (InternalWorkingMemory) map.get( "WorkingMemory" );
        List<InternalFactHandle> handles = (List<InternalFactHandle>) map.get( "Handles" );

        JoinNode join1 = (JoinNode) map.get( "join1" );

        BetaMemory memory = (BetaMemory) wm.getNodeMemory( join1 );
        assertEquals( 2,
                      memory.getRightTupleMemory().size() );

        assertEquals( 2,
                      memory.getLeftTupleMemory().size() );
    }

    public void testBetaNodeRetractOperations() {
        String str = "ObjectTypeNode\n";
        str += "    otn1, java.lang.Integer\n";
        str += "LeftInputAdapterNode\n";
        str += "    lian0, otn1\n";
        str += "ObjectTypeNode\n";
        str += "    otn2, java.lang.Integer\n";
        str += "Binding\n";
        str += "     p1, 0, java.lang.Integer, intValue\n";
        str += "JoinNode\n";
        str += "    join1, lian0, otn2\n";
        str += "    intValue, !=, p1\n";
        str += "Facts\n";
        str += "    0, 1, 2, 3\n";
        str += "assert\n";
        str += "    otn1 [h1, h3]\n";
        str += "    otn2 [h0, h2]\n";
        str += "retract\n";
        str += "    otn1 [h1]\n";
        str += "    otn2 [h2]\n";

        List<DslStep> steps = ReteDslTestEngine.buildDslCommands( new StringReader( str ) );

        ReteDslTestEngine tester = new ReteDslTestEngine();
        Map<String, Object> map = tester.run( steps );

        InternalWorkingMemory wm = (InternalWorkingMemory) map.get( "WorkingMemory" );
        List<InternalFactHandle> handles = (List<InternalFactHandle>) map.get( "Handles" );

        JoinNode join1 = (JoinNode) map.get( "join1" );

        BetaMemory memory = (BetaMemory) wm.getNodeMemory( join1 );
        assertEquals( 1,
                      memory.getRightTupleMemory().size() );

        assertEquals( 1,
                      memory.getLeftTupleMemory().size() );
    }

    public void testBetaNodeSimpleMemoryChecks() {
        String str = "ObjectTypeNode\n";
        str += "    otn1, java.lang.Integer\n";
        str += "LeftInputAdapterNode\n";
        str += "    lian0, otn1\n";
        str += "ObjectTypeNode\n";
        str += "    otn2, java.lang.Integer\n";
        str += "Binding\n";
        str += "     p1, 0, java.lang.Integer, intValue\n";
        str += "JoinNode\n";
        str += "    join1, lian0, otn2\n";
        str += "    intValue, !=, p1\n";
        str += "Facts\n";
        str += "    0, 1, 2, 3\n";
        str += "assert\n";
        str += "    otn1 [h1, h3]\n";
        str += "    otn2 [h0, h2]\n";
        str += "join1\n";
        str += "    leftMemory [[h1], [h3]]\n";
        str += "    rightMemory [h0, h2]\n";
        str += "retract\n";
        str += "    otn1 [h1]\n";
        str += "    otn2 [h2]\n";;
        str += "join1\n";
        str += "    leftMemory [ [h3] ]\n";
        str += "    rightMemory [h0]\n";

        List<DslStep> steps = ReteDslTestEngine.buildDslCommands( new StringReader( str ) );

        ReteDslTestEngine tester = new ReteDslTestEngine();
        Map<String, Object> map = tester.run( steps );

        InternalWorkingMemory wm = (InternalWorkingMemory) map.get( "WorkingMemory" );
        List<InternalFactHandle> handles = (List<InternalFactHandle>) map.get( "Handles" );

        JoinNode join1 = (JoinNode) map.get( "join1" );

        BetaMemory memory = (BetaMemory) wm.getNodeMemory( join1 );
        assertEquals( 1,
                      memory.getRightTupleMemory().size() );

        assertEquals( 1,
                      memory.getLeftTupleMemory().size() );
    }

    public void testBetaNodeChainedMemoryChecks() {
        String str = "ObjectTypeNode\n";
        str += "    otn1, java.lang.Integer\n";
        str += "LeftInputAdapterNode\n";
        str += "    lian0, otn1\n";
        str += "ObjectTypeNode\n";
        str += "    otn2, java.lang.Integer\n";
        str += "ObjectTypeNode\n";
        str += "    otn3, java.lang.Integer\n";
        str += "Binding\n";
        str += "     p1, 0, java.lang.Integer, intValue\n";
        str += "JoinNode\n";
        str += "    join1, lian0, otn2\n";
        str += "    intValue, !=, p1\n";
        str += "JoinNode\n";
        str += "    join2, join1, otn3\n";
        str += "    intValue, !=, p1\n";
        str += "Facts\n";
        str += "    0, 1, 2, 3, 4\n";
        str += "assert\n";
        str += "    otn1 [h1, h3]\n";
        str += "    otn2 [h0, h2]\n";
        str += "    otn3 [h4]\n";
        str += "join1\n";
        str += "    leftMemory [[h1], [h3]]\n";
        str += "    rightMemory [h0, h2]\n";
        str += "join2\n";
        str += "    leftMemory [[h1, h0], [h3, h0],\n";
        str += "                [h1, h2], [h3, h2]]\n";
        str += "    rightMemory [h4]\n";
        str += "retract\n";
        str += "    otn1 [h1]\n";
        str += "    otn2 [h2]\n";;
        str += "join1\n";
        str += "    leftMemory [ [h3] ]\n";
        str += "    rightMemory [h0]\n";
        str += "join2\n";
        str += "    leftMemory  [[h3, h0]]\n";
        str += "    rightMemory [h4]\n";

        List<DslStep> steps = ReteDslTestEngine.buildDslCommands( new StringReader( str ) );

        ReteDslTestEngine tester = new ReteDslTestEngine();
        Map<String, Object> map = tester.run( steps );

        InternalWorkingMemory wm = (InternalWorkingMemory) map.get( "WorkingMemory" );
        List<InternalFactHandle> handles = (List<InternalFactHandle>) map.get( "Handles" );

        JoinNode join1 = (JoinNode) map.get( "join1" );

        BetaMemory memory = (BetaMemory) wm.getNodeMemory( join1 );
        assertEquals( 1,
                      memory.getRightTupleMemory().size() );

        assertEquals( 1,
                      memory.getLeftTupleMemory().size() );
    }
    
    public void testDslEndToEnd() {
        InputStream stream = getClass().getResourceAsStream( "JoinNode.data" );
        assertNotNull( stream );
        DslStep[] steps = (DslStep[]) ReteDslTestEngine.buildDslCommands( new InputStreamReader( stream ) ).toArray( new DslStep[0] );
        assertEquals( 14,
                      steps.length );

        ReteDslTestEngine tester = new ReteDslTestEngine();
        Map<String, Object> map = tester.run( Arrays.asList( steps ) );         
    }    

    private void print(DslStep[] steps) {
        for ( DslStep command : steps ) {
            System.out.println( command );
        }
    }
}
