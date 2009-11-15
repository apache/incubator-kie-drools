package org.drools.reteoo.test;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.util.ArrayList;
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
import org.drools.reteoo.test.ReteTester.DslStep;
import org.drools.rule.Declaration;
import org.drools.spi.PropagationContext;

import junit.framework.TestCase;

public class ReteTesterTest extends TestCase {
    public void testIndentPos() {
        ReteTester tester = new ReteTester();
        assertEquals( 5,
                      tester.indentPos( "     asdfasdf" ) );
    }

    public void testReaderAsString() {
        String str = "  line1\n";
        str += "   line2\n";

        List<String> lines = ReteTester.chunkReader( new StringReader( str ) );
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

        List<String> lines = ReteTester.chunkReader( new StringReader( str ) );
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

        List<String> lines = ReteTester.chunkReader( new StringReader( str ) );
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

        List<String> lines = ReteTester.chunkReader( new StringReader( str ) );
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
        DslStep[] step = (DslStep[]) ReteTester.buildDslCommands( new InputStreamReader( stream ) ).toArray( new DslStep[0] );
        //        assertEquals( 10, cmds.length );

        //        assertEquals( 1, step[0].getLine() );
        //        assertEquals( "LeftInputAdapter", step[0].getName() );
        //        assertEquals( "node0", step[0].getCommands().get( 0 ) );
        //        
        //        assertEquals( 3, step[1].getLine() );
        //        assertEquals( "JoinNode", step[1].getName() );
        //        assertEquals( "node0, node1, ==", step[1].getCommands().get( 0 ) ); 
        //        
        //        assertEquals( 5, step[2].getLine() );
        //        assertEquals( "JoinNode", step[2].getName() );
        //        assertEquals( "node1, node2, ==", step[2].getCommands().get( 0 ) );  
        //        
        //        assertEquals( 7, step[3].getLine() );
        //        assertEquals( "Facts", step[3].getName() );
        //        assertEquals( "0, 0, 0, 0", step[3].getCommands().get( 0 ) );  
        //        
        //        assertEquals( 9, step[4].getLine() );
        //        assertEquals( "node1", step[4].getName() );
        //        assertEquals( "assertObject, h0", step[4].getCommands().get( 0 ) );
        //        assertEquals( "assertLeftTuple, h1, h2", step[4].getCommands().get( 1 ) );          
        //        assertEquals( "assertObject, h3, h4", step[4].getCommands().get( 2 ) );          
        //        assertEquals( "leftMemory, 2,[ [h1], [h2] ]", step[4].getCommands().get( 3 ) );          
        //        assertEquals( "rightMemory, 3, [ [h0], [h3], [h4] ]", step[4].getCommands().get( 4 ) );          
        //        
        //        assertEquals( 15, step[5].getLine() );
        //        assertEquals( "node2", step[5].getName() );
        //        assertEquals( "leftMemory, 6, [ [h0, h1], [h0, h2],[h3, h1], [h3, h2],[h4, h1], [h4, h2] ]", step[5].getCommands().get( 0 ) );
        //        assertEquals( "rightMemory, 0, []", step[5].getCommands().get( 1 ) );
        //     
        //        assertEquals( 20, step[6].getLine() );
        //        assertEquals( "node1", step[6].getName() );
        //        assertEquals( "retractRightTuple, h0", step[6].getCommands().get( 0 ) );
        //        assertEquals( "leftMemory, 2, [ [h1], [h2] ]", step[6].getCommands().get( 1 ) );          
        //        assertEquals( "rightMemory, 2, [ [h3], [h4] ]", step[6].getCommands().get( 2 ) );
        //        
        //        assertEquals( 24, step[7].getLine() );
        //        assertEquals( "node2", step[7].getName() );
        //        assertEquals( "leftMemory, 4, [ [h3, h1], [h3, h2], [h4, h1], [h4, h2] ]", step[7].getCommands().get( 0 ) );
        //        
        //        assertEquals( 26, step[8].getLine() );
        //        assertEquals( "node1", step[8].getName() );
        //        assertEquals( "retractLeftTuple, h2", step[8].getCommands().get( 0 ) );
        //        assertEquals( "leftMemory, 1, [ [h1] ]", step[8].getCommands().get( 1 ) );          
        //        assertEquals( "rightMemory, 2, [ [h3], [h4] ]", step[8].getCommands().get( 2 ) );        
        //
        //        assertEquals( 30, step[9].getLine() );
        //        assertEquals( "node2", step[9].getName() );
        //        assertEquals( "leftMemory, 4, [ [h3, h1], [h4, h1] ]", step[9].getCommands().get( 0 ) );        
    }

    public void testObjectTypeNodeStep() {
        String str = "ObjectTypeNode\n";
        str += "    otn1, java.lang.Integer";

        List<DslStep> steps = ReteTester.buildDslCommands( new StringReader( str ) );

        ReteTester tester = new ReteTester();
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

        List<DslStep> steps = ReteTester.buildDslCommands( new StringReader( str ) );

        ReteTester tester = new ReteTester();
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

        List<DslStep> steps = ReteTester.buildDslCommands( new StringReader( str ) );
        //print(steps);

        ReteTester tester = new ReteTester();
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

        List<DslStep> steps = ReteTester.buildDslCommands( new StringReader( str ) );

        ReteTester tester = new ReteTester();
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
                                                            0);
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

        List<DslStep> steps = ReteTester.buildDslCommands( new StringReader( str ) );

        ReteTester tester = new ReteTester();
        Map<String, Object> map = tester.run( steps );

        WorkingMemory wm = (WorkingMemory) map.get( "WorkingMemory" );
        List<InternalFactHandle> handles = (List<InternalFactHandle>) map.get( "Handles" );
        
        assertNotNull( wm );
        assertNotNull( handles );
        
        assertEquals( 5, handles.size() );
        
        assertEquals( 1, handles.get( 0 ).getObject() );
        assertEquals( 2, handles.get( 1 ).getObject() );
        assertEquals( "hello", handles.get( 2 ).getObject() );
        assertEquals( "good bye", handles.get( 3 ).getObject() );
        assertEquals( new ArrayList<FactHandle>(), handles.get( 4 ).getObject() );
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
        
        
        List<DslStep> steps = ReteTester.buildDslCommands( new StringReader( str ) );

        ReteTester tester = new ReteTester();
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
        
        
        List<DslStep> steps = ReteTester.buildDslCommands( new StringReader( str ) );

        ReteTester tester = new ReteTester();
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
        
        
        List<DslStep> steps = ReteTester.buildDslCommands( new StringReader( str ) );

        ReteTester tester = new ReteTester();
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
        
        
        List<DslStep> steps = ReteTester.buildDslCommands( new StringReader( str ) );

        ReteTester tester = new ReteTester();
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

    private void print(DslStep[] steps) {
        for ( DslStep command : steps ) {
            System.out.println( command );
        }
    }
}
