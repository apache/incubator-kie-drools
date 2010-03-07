package org.drools.reteoo.test;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.antlr.runtime.ANTLRInputStream;
import org.antlr.runtime.ANTLRReaderStream;
import org.antlr.runtime.ANTLRStringStream;
import org.antlr.runtime.CommonTokenStream;
import org.antlr.runtime.RecognitionException;
import org.antlr.runtime.TokenStream;
import org.antlr.runtime.tree.CommonTree;
import org.antlr.runtime.tree.CommonTreeNodeStream;
import org.antlr.runtime.tree.Tree;
import org.drools.RuleBaseConfiguration;
import org.drools.common.InternalFactHandle;
import org.drools.common.InternalWorkingMemory;
import org.drools.common.PropagationContextImpl;
import org.drools.core.util.Iterator;
import org.drools.core.util.ObjectHashMap;
import org.drools.core.util.ObjectHashMap.ObjectEntry;
import org.drools.reteoo.AccumulateNode;
import org.drools.reteoo.BetaMemory;
import org.drools.reteoo.BetaNode;
import org.drools.reteoo.CollectNode;
import org.drools.reteoo.LeftTuple;
import org.drools.reteoo.LeftTupleMemory;
import org.drools.reteoo.LeftTupleSink;
import org.drools.reteoo.ModifyPreviousTuples;
import org.drools.reteoo.ObjectSink;
import org.drools.reteoo.ObjectTypeNode;
import org.drools.reteoo.ReteooRuleBase;
import org.drools.reteoo.RightInputAdapterNode;
import org.drools.reteoo.RightTuple;
import org.drools.reteoo.RightTupleMemory;
import org.drools.reteoo.RuleTerminalNode;
import org.drools.reteoo.Sink;
import org.drools.reteoo.AccumulateNode.AccumulateMemory;
import org.drools.reteoo.CollectNode.CollectMemory;
import org.drools.reteoo.builder.BuildContext;
import org.drools.reteoo.test.dsl.AccumulateNodeStep;
import org.drools.reteoo.test.dsl.BetaNodeStep;
import org.drools.reteoo.test.dsl.BindingStep;
import org.drools.reteoo.test.dsl.CollectNodeStep;
import org.drools.reteoo.test.dsl.DSLMock;
import org.drools.reteoo.test.dsl.DslStep;
import org.drools.reteoo.test.dsl.EvalNodeStep;
import org.drools.reteoo.test.dsl.ExistsNodeStep;
import org.drools.reteoo.test.dsl.FactsStep;
import org.drools.reteoo.test.dsl.JoinNodeStep;
import org.drools.reteoo.test.dsl.LeftInputAdapterNodeStep;
import org.drools.reteoo.test.dsl.LeftTupleSinkStep;
import org.drools.reteoo.test.dsl.MockitoHelper;
import org.drools.reteoo.test.dsl.NodeTestCase;
import org.drools.reteoo.test.dsl.NodeTestCaseResult;
import org.drools.reteoo.test.dsl.NodeTestDef;
import org.drools.reteoo.test.dsl.NotNodeStep;
import org.drools.reteoo.test.dsl.ObjectTypeNodeStep;
import org.drools.reteoo.test.dsl.RIANodeStep;
import org.drools.reteoo.test.dsl.ReteTesterHelper;
import org.drools.reteoo.test.dsl.RuleTerminalNodeStep;
import org.drools.reteoo.test.dsl.Step;
import org.drools.reteoo.test.dsl.WithStep;
import org.drools.reteoo.test.dsl.NodeTestCaseResult.NodeTestResult;
import org.drools.reteoo.test.dsl.NodeTestCaseResult.Result;
import org.drools.reteoo.test.parser.NodeTestDSLLexer;
import org.drools.reteoo.test.parser.NodeTestDSLParser;
import org.drools.reteoo.test.parser.NodeTestDSLTree;
import org.drools.reteoo.test.parser.NodeTestDSLParser.compilation_unit_return;
import org.drools.spi.PropagationContext;
import org.junit.runner.Description;
import org.junit.runner.notification.Failure;
import org.junit.runner.notification.RunNotifier;
import org.junit.runner.notification.StoppedByUserException;
import org.mvel2.MVEL;

public class ReteDslTestEngine {

    private static final String OBJECT_TYPE_NODE         = "ObjectTypeNode";
    private static final String LEFT_INPUT_ADAPTER_NODE  = "LeftInputAdapterNode";
    private static final String BINDING                  = "Binding";
    private static final String JOIN_NODE                = "JoinNode";
    private static final String NOT_NODE                 = "NotNode";
    private static final String EXISTS_NODE              = "ExistsNode";
    private static final String COLLECT_NODE             = "CollectNode";
    private static final String ACCUMULATE_NODE          = "AccumulateNode";
    private static final String RULE_TERMINAL_NODE       = "RuleTerminalNode";
    private static final String EVAL_NODE                = "EvalNode";
    private static final String WITH                     = "With";
    private static final String FACTS                    = "Facts";
    private static final String RIGHT_INPUT_ADAPTER_NODE = "RightInputAdapterNode";
    private static final String LEFT_TUPLE_SINK_STEP     = "LeftTupleSink";
    private static final String BETA_NODE_STEP           = "BetaNodeStep";

    private ReteTesterHelper    reteTesterHelper;
    private Map<String, Object> steps;

    public ReteDslTestEngine() {

        this.reteTesterHelper = new ReteTesterHelper();

        this.steps = new HashMap<String, Object>();

        this.steps.put( OBJECT_TYPE_NODE,
                        new ObjectTypeNodeStep( this.reteTesterHelper ) );
        this.steps.put( LEFT_INPUT_ADAPTER_NODE,
                        new LeftInputAdapterNodeStep( this.reteTesterHelper ) );
        this.steps.put( BINDING,
                        new BindingStep( this.reteTesterHelper ) );
        this.steps.put( JOIN_NODE,
                        new JoinNodeStep( this.reteTesterHelper ) );
        this.steps.put( NOT_NODE,
                        new NotNodeStep( this.reteTesterHelper ) );
        this.steps.put( EXISTS_NODE,
                        new ExistsNodeStep( this.reteTesterHelper ) );
        this.steps.put( COLLECT_NODE,
                        new CollectNodeStep( this.reteTesterHelper ) );
        this.steps.put( ACCUMULATE_NODE,
                        new AccumulateNodeStep( this.reteTesterHelper ) );
        this.steps.put( RULE_TERMINAL_NODE,
                        new RuleTerminalNodeStep( this.reteTesterHelper ) );
        this.steps.put( EVAL_NODE,
                        new EvalNodeStep( this.reteTesterHelper ) );
        this.steps.put( RIGHT_INPUT_ADAPTER_NODE,
                        new RIANodeStep( this.reteTesterHelper ) );
        this.steps.put( FACTS,
                        new FactsStep( this.reteTesterHelper ) );
        this.steps.put( WITH,
                        new WithStep( this.reteTesterHelper ) );
        this.steps.put( LEFT_TUPLE_SINK_STEP,
                        new LeftTupleSinkStep( this.reteTesterHelper ) );
        this.steps.put( BETA_NODE_STEP,
                        new BetaNodeStep( this.reteTesterHelper ) );
    }

    public NodeTestCaseResult run(NodeTestCase testCase,
                                  RunNotifier notifier) {
        if ( testCase == null || testCase.hasErrors() ) {
            throw new IllegalArgumentException( "Impossible to execute test case due to existing errors: " + testCase.getErrors() );
        }
        if ( notifier == null ) {
            notifier = EmptyNotifier.INSTANCE;
        }
        this.reteTesterHelper.addImports( testCase.getImports() );
        NodeTestCaseResult result = new NodeTestCaseResult( testCase );
        for ( NodeTestDef test : testCase.getTests() ) {
            notifier.fireTestStarted( test.getDescription() );
            NodeTestResult testResult = run( testCase,
                                             test );
            switch ( testResult.result ) {
                case SUCCESS :
                    notifier.fireTestFinished( test.getDescription() );
                    break;
                case ERROR :
                case FAILURE :
                    notifier.fireTestFailure( new Failure( test.getDescription(),
                                                           new AssertionError( testResult.errorMsgs ) ) );
                    break;
            }
            result.add( testResult );
        }
        return result;
    }

    private NodeTestResult run(NodeTestCase testCase,
                               NodeTestDef test) {
        Map<String, Object> context = createContext( testCase );
        NodeTestResult result = new NodeTestResult( test,
                                                    Result.NOT_EXECUTED,
                                                    context,
                                                    new LinkedList<String>() );
        try {
            // run setup
            run( context,
                 testCase.getSetup(),
                 result );
            // run test
            run( context,
                 test.getSteps(),
                 result );
            // run tearDown
            run( context,
                 testCase.getTearDown(),
                 result );
            result.result = Result.SUCCESS;
        } catch ( Exception e ) {
            result.result = Result.ERROR;
            result.errorMsgs.add( e.getMessage() );
        }
        return result;
    }

    private Map<String, Object> createContext(NodeTestCase testCase) {
        Map<String, Object> context = new HashMap<String, Object>();
        context.put( "TestCase",
                     testCase );

        RuleBaseConfiguration conf = new RuleBaseConfiguration();

        ReteooRuleBase rbase = new ReteooRuleBase( "ID",
                                                   conf );
        BuildContext buildContext = new BuildContext( rbase,
                                                      rbase.getReteooBuilder().getIdGenerator() );
        context.put( "BuildContext",
                     buildContext );
        context.put( "ClassFieldAccessorStore",
                     this.reteTesterHelper.getStore() );

        InternalWorkingMemory wm = (InternalWorkingMemory) rbase.newStatefulSession( true );
        context.put( "WorkingMemory",
                     wm );
        return context;
    }

    public Map<String, Object> run(Map<String, Object> context,
                                   List<DslStep> steps,
                                   NodeTestResult result) {
        InternalWorkingMemory wm = (InternalWorkingMemory) context.get( "WorkingMemory" );
        for ( DslStep step : steps ) {
            String name = step.getName();
            Object object = this.steps.get( name );
            if ( object != null && object instanceof Step ) {
                Step stepImpl = (Step) object;
                try {
                    stepImpl.execute( context,
                                      step.getCommands() );
                } catch ( Exception e ) {
                    throw new IllegalArgumentException( "line " + step.getLine() + ": unable to execute step " + step,
                                                        e );
                }
            } else if ( "assert".equals( name.trim() ) ) {
                assertObject( step,
                              context,
                              wm );
            } else if ( "retract".equals( name.trim() ) ) {
                retractObject( step,
                               context,
                               wm );
            } else if ( "modify".equals( name.trim() ) ) {
                modifyObject( step,
                              context,
                              wm );
            } else {
                Object node = context.get( name.trim() );
                if ( node == null ) {
                    throw new IllegalArgumentException( "line " + step.getLine() + ": step " + name + " does not exist" );
                }

                if ( node instanceof DSLMock ) {
                    // it is a mock
                    MockitoHelper.process( step,
                                           (LeftTupleSink) node,
                                           context,
                                           wm );
                } else if ( node instanceof BetaNode ) {
                    betaNode( step,
                              (BetaNode) node,
                              context,
                              wm );
                } else if ( node instanceof RightInputAdapterNode ) {
                    riaNode( step,
                             (RightInputAdapterNode) node,
                             context,
                             wm );
                } else if ( node instanceof RuleTerminalNode ) {
                    ruleTerminalNode( step,
                                      (RuleTerminalNode) node,
                                      context,
                                      wm );
                } else {
                    throw new IllegalArgumentException( "line " + step.getLine() + ": unknown node " + node );
                }
            }
        }

        return context;
    }

    private void betaNode(DslStep step,
                          BetaNode node,
                          Map<String, Object> context,
                          InternalWorkingMemory wm) {
        try {
            List<String[]> cmds = step.getCommands();
            List<InternalFactHandle> handles = (List<InternalFactHandle>) context.get( "Handles" );

            BetaMemory memory = null;
            if ( node instanceof CollectNode ) {
                CollectMemory colmem = (CollectMemory) wm.getNodeMemory( node );
                memory = colmem.betaMemory;
            } else if ( node instanceof AccumulateNode ) {
                AccumulateMemory accmem = (AccumulateMemory) wm.getNodeMemory( node );
                memory = accmem.betaMemory;
            } else {
                memory = (BetaMemory) wm.getNodeMemory( node );
            }
            for ( String[] cmd : cmds ) {
                if ( cmd[0].equals( "leftMemory" ) ) {
                    String args = cmd[1];
                    String listString = args.replaceAll( "h(\\d+)",
                                                         "h[$1]" );
                    Map<String, Object> vars = new HashMap<String, Object>();
                    vars.put( "h",
                              handles );
                    List< ? > expectedLeftTuples = (List< ? >) MVEL.eval( listString,
                                                                          vars );

                    LeftTupleMemory leftMemory = memory.getLeftTupleMemory();

                    if ( expectedLeftTuples.isEmpty() && leftMemory.size() != 0 ) {
                        throw new AssertionError( "line " + step.getLine() + ": left Memory expected [] actually " + leftMemory );
                    } else if ( expectedLeftTuples.isEmpty() && leftMemory.size() == 0 ) {
                        return;
                    }

                    // we always lookup from the first element, in case it's indexed
                    List<InternalFactHandle> first = (List<InternalFactHandle>) expectedLeftTuples.get( 0 );
                    LeftTuple firstTuple = new LeftTuple( first.get( 0 ),
                                                          null,
                                                          false );
                    for ( int i = 1; i < first.size(); i++ ) {
                        firstTuple = new LeftTuple( firstTuple,
                                                    null,
                                                    false );
                    }

                    List<LeftTuple> leftTuples = new ArrayList<LeftTuple>();

                    for ( LeftTuple leftTuple = memory.getLeftTupleMemory().getFirst( firstTuple ); leftTuple != null; leftTuple = (LeftTuple) leftTuple.getNext() ) {
                        leftTuples.add( leftTuple );
                    }
                    List<List<InternalFactHandle>> actualLeftTuples = new ArrayList<List<InternalFactHandle>>( leftTuples.size() );
                    for ( LeftTuple leftTuple : leftTuples ) {
                        List<InternalFactHandle> tupleHandles = Arrays.asList( leftTuple.toFactHandles() );
                        actualLeftTuples.add( tupleHandles );
                    }

                    if ( !expectedLeftTuples.equals( actualLeftTuples ) ) {
                        throw new AssertionError( "line " + step.getLine() + ": left Memory expected " + expectedLeftTuples + " actually " + actualLeftTuples );
                    }

                } else if ( cmd[0].equals( "rightMemory" ) ) {
                    String args = cmd[1];
                    String listString = args.replaceAll( "h(\\d+)",
                                                         "h[$1]" );
                    Map<String, Object> vars = new HashMap<String, Object>();
                    vars.put( "h",
                              handles );
                    List< ? > expectedFactHandles = (List< ? >) MVEL.eval( listString,
                                                                           vars );

                    RightTupleMemory rightMemory = memory.getRightTupleMemory();

                    if ( expectedFactHandles.isEmpty() && rightMemory.size() != 0 ) {
                        throw new AssertionError( "line " + step.getLine() + ": right Memory expected [] actually " + rightMemory );
                    } else if ( expectedFactHandles.isEmpty() && rightMemory.size() == 0 ) {
                        return;
                    }

                    RightTuple first = new RightTuple( (InternalFactHandle) expectedFactHandles.get( 0 ) );
                    List<RightTuple> actualRightTuples = new ArrayList<RightTuple>();
                    for ( RightTuple rightTuple = memory.getRightTupleMemory().getFirst( first ); rightTuple != null; rightTuple = (RightTuple) rightTuple.getNext() ) {
                        actualRightTuples.add( rightTuple );
                    }

                    if ( expectedFactHandles.size() != actualRightTuples.size() ) {
                        throw new AssertionError( "line " + step.getLine() + ": right Memory expected " + expectedFactHandles + " actually " + actualRightTuples );
                    }

                    for ( int i = 0, length = actualRightTuples.size(); i < length; i++ ) {
                        if ( expectedFactHandles.get( i ) != actualRightTuples.get( i ).getFactHandle() ) {
                            throw new AssertionError( "line " + step.getLine() + ": right Memory expected " + expectedFactHandles + " actually " + actualRightTuples );
                        }
                    }

                } else {
                    throw new IllegalArgumentException( "line " + step.getLine() + ": command does not exist " + Arrays.toString( cmd ) );
                }
            }
        } catch ( Exception e ) {
            throw new IllegalArgumentException( "line " + step.getLine() + ": unable to execute step " + step,
                                                e );
        }
    }

    private void riaNode(DslStep step,
                         RightInputAdapterNode node,
                         Map<String, Object> context,
                         InternalWorkingMemory wm) {
        try {
            List<String[]> cmds = step.getCommands();
            List<InternalFactHandle> handles = (List<InternalFactHandle>) context.get( "Handles" );

            final ObjectHashMap memory = (ObjectHashMap) wm.getNodeMemory( node );
            for ( String[] cmd : cmds ) {
                if ( cmd[0].equals( "leftMemory" ) ) {
                    String args = cmd[1];
                    String listString = args.replaceAll( "h(\\d+)",
                                                         "h[$1]" );
                    Map<String, Object> vars = new HashMap<String, Object>();
                    vars.put( "h",
                              handles );
                    List< ? > expectedLeftTuples = (List< ? >) MVEL.eval( listString,
                                                                          vars );

                    if ( expectedLeftTuples.isEmpty() && memory.size() != 0 ) {
                        throw new AssertionError( "line " + step.getLine() + ": left Memory expected [] actually " + memory );
                    } else if ( expectedLeftTuples.isEmpty() && memory.size() == 0 ) {
                        return;
                    }

                    // create expected tuples
                    List<LeftTuple> leftTuples = new ArrayList<LeftTuple>();
                    for ( List<InternalFactHandle> tlist : (List<List<InternalFactHandle>>) expectedLeftTuples ) {
                        LeftTuple tuple = new LeftTuple( tlist.get( 0 ),
                                                         null,
                                                         false );
                        for ( int i = 1; i < tlist.size(); i++ ) {
                            tuple = new LeftTuple( tuple,
                                                   new RightTuple( tlist.get( i ) ),
                                                   null,
                                                   false );
                        }
                        leftTuples.add( tuple );

                    }

                    // get actual tuples
                    final List<LeftTuple> actualTuples = new ArrayList<LeftTuple>();
                    final Iterator it = memory.iterator();
                    for ( ObjectEntry entry = (ObjectEntry) it.next(); entry != null; entry = (ObjectEntry) it.next() ) {
                        actualTuples.add( (LeftTuple) entry.getKey() );
                    }

                    // iterate over expected tuples and compare with actual tuples 
                    for ( LeftTuple tuple : leftTuples ) {
                        if ( !actualTuples.remove( tuple ) ) {
                            throw new AssertionError( "line " + step.getLine() + ": left Memory expected " + tuple + " not found in memory." );
                        }
                    }
                    if ( !actualTuples.isEmpty() ) {
                        throw new AssertionError( "line " + step.getLine() + ": left Memory unexpected tuples in the node memory " + actualTuples );
                    }
                } else {
                    throw new IllegalArgumentException( "line " + step.getLine() + ": command does not exist " + Arrays.toString( cmd ) );
                }
            }
        } catch ( Exception e ) {
            throw new IllegalArgumentException( "line " + step.getLine() + ": unable to execute step " + step,
                                                e );
        }
    }

    private void ruleTerminalNode(DslStep step,
                                  RuleTerminalNode node,
                                  Map<String, Object> context,
                                  InternalWorkingMemory wm) {
        try {
            List<String[]> cmds = step.getCommands();
            //List<InternalFactHandle> handles = (List<InternalFactHandle>) context.get( "Handles" );

            for ( String[] cmd : cmds ) {
                throw new IllegalArgumentException( "line " + step.getLine() + ": command does not exist " + Arrays.toString( cmd ) );
            }
        } catch ( Exception e ) {
            throw new IllegalArgumentException( "line " + step.getLine() + ": unable to execute step " + step,
                                                e );
        }
    }

    @SuppressWarnings("unchecked")
    private void assertObject(DslStep step,
                              Map<String, Object> context,
                              InternalWorkingMemory wm) {
        try {
            List<String[]> cmds = step.getCommands();
            List<InternalFactHandle> handles = (List<InternalFactHandle>) context.get( "Handles" );
            for ( String[] cmd : cmds ) {
                try {
                    String nodeName = cmd[0];
                    Sink sink = (Sink) context.get( nodeName );
                    if ( sink == null ) {
                        throw new IllegalArgumentException( "line " + step.getLine() + ": node " + nodeName + " does not exist" );
                    }

                    Map<String, Object> vars = new HashMap<String, Object>();
                    vars.put( "h",
                              handles );
                    String args = cmd[1];
                    String listString = args.replaceAll( "h(\\d+)",
                                                         "h[$1]" );
                    List< ? > list = (List< ? >) MVEL.eval( listString,
                                                            vars );
                    if ( list == null ) {
                        throw new IllegalArgumentException( cmd + " does not specify an existing fact handle" );
                    }

                    for ( Object element : list ) {
                        if ( element == null ) {
                            throw new IllegalArgumentException( cmd + " does not specify an existing fact handle" );
                        }

                        if ( element instanceof InternalFactHandle ) {
                            InternalFactHandle handle = (InternalFactHandle) element;
                            PropagationContext pContext = new PropagationContextImpl( wm.getNextPropagationIdCounter(),
                                                                                      PropagationContext.ASSERTION,
                                                                                      null,
                                                                                      null,
                                                                                      handle );
                            ((ObjectSink) sink).assertObject( handle,
                                                              pContext,
                                                              wm );
                        } else {
                            List<InternalFactHandle> tlist = (List<InternalFactHandle>) element;
                            LeftTuple tuple = createTuple( context,
                                                           tlist );
                            PropagationContext pContext = new PropagationContextImpl( wm.getNextPropagationIdCounter(),
                                                                                      PropagationContext.ASSERTION,
                                                                                      null,
                                                                                      tuple,
                                                                                      null );
                            ((LeftTupleSink) sink).assertLeftTuple( tuple,
                                                                    pContext,
                                                                    wm );
                        }

                    }
                } catch ( Exception e ) {
                    throw new IllegalArgumentException( "line " + step.getLine() + ": unable to execute command " + cmd,
                                                        e );

                }
            }
        } catch ( Exception e ) {
            throw new IllegalArgumentException( "line " + step.getLine() + ": unable to execute step " + step,
                                                e );
        }
    }

    private LeftTuple createTuple(Map<String, Object> context,
                                  List<InternalFactHandle> tlist) {
        LeftTuple tuple = null;
        String id = getTupleId( tlist );
        for ( InternalFactHandle handle : tlist ) {
            if ( tuple == null ) {
                tuple = new LeftTuple( handle,
                                       null,
                                       false ); // do not keep generated tuples on the handle list
            } else {
                tuple = new LeftTuple( tuple,
                                       new RightTuple( handle ),
                                       null,
                                       true );
            }
        }
        context.put( id,
                     tuple );
        return tuple;
    }

    private String getTupleId(List<InternalFactHandle> tlist) {
        StringBuilder id = new StringBuilder();
        id.append( "T." );
        for ( InternalFactHandle handle : tlist ) {
            id.append( handle.getId() );
            id.append( "." );
        }
        return id.toString();
    }

    private void retractObject(DslStep step,
                               Map<String, Object> context,
                               InternalWorkingMemory wm) {
        try {
            List<String[]> cmds = step.getCommands();
            List<InternalFactHandle> handles = (List<InternalFactHandle>) context.get( "Handles" );
            for ( String[] cmd : cmds ) {
                try {
                    String nodeName = cmd[0];
                    Sink sink = (Sink) context.get( nodeName );
                    if ( sink == null ) {
                        throw new IllegalArgumentException( "line " + step.getLine() + ": node " + nodeName + " does not exist" );
                    }

                    String args = cmd[1];
                    String listString = args.replaceAll( "h(\\d+)",
                                                         "h[$1]" );
                    Map<String, Object> vars = new HashMap<String, Object>();
                    vars.put( "h",
                              handles );
                    List< ? > list = (List< ? >) MVEL.eval( listString,
                                                            vars );
                    if ( list == null ) {
                        throw new IllegalArgumentException( Arrays.toString( cmd ) + " does not specify an existing fact handle" );
                    }

                    for ( Object element : list ) {
                        if ( element == null ) {
                            throw new IllegalArgumentException( Arrays.toString( cmd ) + " does not specify an existing fact handle" );
                        }

                        if ( element instanceof InternalFactHandle ) {
                            InternalFactHandle handle = (InternalFactHandle) element;
                            PropagationContext pContext = new PropagationContextImpl( wm.getNextPropagationIdCounter(),
                                                                                      PropagationContext.RETRACTION,
                                                                                      null,
                                                                                      null,
                                                                                      handle );
                            if ( sink instanceof ObjectTypeNode ) {
                                ((ObjectTypeNode) sink).retractObject( handle,
                                                                       pContext,
                                                                       wm );
                            } else {
                                for ( RightTuple rightTuple = handle.getFirstRightTuple(); rightTuple != null; rightTuple = (RightTuple) rightTuple.getHandleNext() ) {
                                    rightTuple.getRightTupleSink().retractRightTuple( rightTuple,
                                                                                      pContext,
                                                                                      wm );
                                }
                                handle.setFirstRightTuple( null );
                                handle.setLastRightTuple( null );
                                for ( LeftTuple leftTuple = handle.getFirstLeftTuple(); leftTuple != null; leftTuple = (LeftTuple) leftTuple.getLeftParentNext() ) {
                                    leftTuple.getLeftTupleSink().retractLeftTuple( leftTuple,
                                                                                   pContext,
                                                                                   wm );
                                }
                                handle.setFirstLeftTuple( null );
                                handle.setLastLeftTuple( null );
                            }
                        } else {
                            List<InternalFactHandle> tlist = (List<InternalFactHandle>) element;
                            String id = getTupleId( tlist );
                            LeftTuple tuple = (LeftTuple) context.remove( id );
                            if ( tuple == null ) {
                                throw new IllegalArgumentException( "Tuple not found: " + id + " : " + tlist.toString() );
                            }
                            PropagationContext pContext = new PropagationContextImpl( wm.getNextPropagationIdCounter(),
                                                                                      PropagationContext.RETRACTION,
                                                                                      null,
                                                                                      tuple,
                                                                                      null );
                            ((LeftTupleSink) sink).retractLeftTuple( tuple,
                                                                     pContext,
                                                                     wm );
                        }

                    }
                } catch ( Exception e ) {
                    throw new IllegalArgumentException( "line " + step.getLine() + ": unable to execute command " + Arrays.toString( cmd ),
                                                        e );

                }
            }
        } catch ( Exception e ) {
            throw new IllegalArgumentException( "line " + step.getLine() + ": unable to execute step " + step,
                                                e );
        }
    }

    private void modifyObject(DslStep step,
                              Map<String, Object> context,
                              InternalWorkingMemory wm) {
        try {
            List<String[]> cmds = step.getCommands();
            List<InternalFactHandle> handles = (List<InternalFactHandle>) context.get( "Handles" );
            for ( String[] cmd : cmds ) {
                try {
                    String nodeName = cmd[0];
                    Sink sink = (Sink) context.get( nodeName );
                    if ( sink == null ) {
                        throw new IllegalArgumentException( "line " + step.getLine() + ": node " + nodeName + " does not exist" );
                    }

                    String args = cmd[1];
                    String listString = args.replaceAll( "h(\\d+)",
                                                         "h[$1]" );
                    Map<String, Object> vars = new HashMap<String, Object>();
                    vars.put( "h",
                              handles );
                    List< ? > list = (List< ? >) MVEL.eval( listString,
                                                            vars );
                    if ( list == null ) {
                        throw new IllegalArgumentException( Arrays.toString( cmd ) + " does not specify an existing fact handle" );
                    }

                    for ( Object element : list ) {
                        if ( element == null ) {
                            throw new IllegalArgumentException( Arrays.toString( cmd ) + " does not specify an existing fact handle" );
                        }

                        if ( element instanceof InternalFactHandle ) {
                            InternalFactHandle handle = (InternalFactHandle) element;
                            PropagationContext pContext = new PropagationContextImpl( wm.getNextPropagationIdCounter(),
                                                                                      PropagationContext.MODIFICATION,
                                                                                      null,
                                                                                      null,
                                                                                      handle );
                            ModifyPreviousTuples modifyPreviousTuples = new ModifyPreviousTuples( handle.getFirstLeftTuple(),
                                                                                                  handle.getFirstRightTuple() );
                            handle.setFirstLeftTuple( null );
                            handle.setFirstRightTuple( null );
                            handle.setLastLeftTuple( null );
                            handle.setLastRightTuple( null );
                            ((ObjectSink) sink).modifyObject( handle,
                                                              modifyPreviousTuples,
                                                              pContext,
                                                              wm );
                            modifyPreviousTuples.retractTuples( pContext,
                                                                wm );
                        } else {
                            List<InternalFactHandle> tlist = (List<InternalFactHandle>) element;
                            String id = getTupleId( tlist );
                            LeftTuple tuple = (LeftTuple) context.get( id );
                            if ( tuple == null ) {
                                throw new IllegalArgumentException( "Tuple not found: " + id + " : " + tlist.toString() );
                            }
                            PropagationContext pContext = new PropagationContextImpl( wm.getNextPropagationIdCounter(),
                                                                                      PropagationContext.MODIFICATION,
                                                                                      null,
                                                                                      tuple,
                                                                                      null );
                            ((LeftTupleSink) sink).modifyLeftTuple( tuple,
                                                                    pContext,
                                                                    wm );
                        }
                    }
                } catch ( Exception e ) {
                    throw new IllegalArgumentException( "line " + step.getLine() + ": unable to execute command " + cmd,
                                                        e );
                }
            }
        } catch ( Exception e ) {
            throw new IllegalArgumentException( "line " + step.getLine() + ": unable to execute step " + step,
                                                e );
        }
    }

    public static NodeTestCase compile(final Reader reader) throws IOException {
        NodeTestDSLParser parser = getParser( reader );
        return compile( parser );
    }

    public static NodeTestCase compile(final InputStream is) throws IOException {
        NodeTestDSLParser parser = getParser( is );
        return compile( parser );
    }

    public static NodeTestCase compile(final String source) throws IOException {
        NodeTestDSLParser parser = getParser( source );
        return compile( parser );
    }

    private static NodeTestCase compile(final NodeTestDSLParser parser) {
        try {
            compilation_unit_return cur = parser.compilation_unit();
            if ( parser.hasErrors() ) {
                NodeTestCase result = new NodeTestCase();
                result.setErrors( parser.getErrorMessages() );
                return result;
            }
            NodeTestCase testCase = walk( parser.getTokenStream(),
                                          (CommonTree) cur.getTree() );
            return testCase;
        } catch ( RecognitionException e ) {
            NodeTestCase result = new NodeTestCase();
            result.setErrors( Collections.singletonList( e.getMessage() ) );
            return result;
        }
    }

    private static NodeTestCase walk(TokenStream tokenStream,
                                     Tree resultTree) throws RecognitionException {
        CommonTreeNodeStream nodes = new CommonTreeNodeStream( resultTree );
        // AST nodes have payload that point into token stream
        nodes.setTokenStream( tokenStream );
        // Create a tree walker attached to the nodes stream
        NodeTestDSLTree walker = new NodeTestDSLTree( nodes );
        walker.compilation_unit();
        return walker.getTestCase();
    }

    private static NodeTestDSLParser getParser(final Reader reader) throws IOException {
        NodeTestDSLLexer lexer = new NodeTestDSLLexer( new ANTLRReaderStream( reader ) );
        NodeTestDSLParser parser = new NodeTestDSLParser( new CommonTokenStream( lexer ) );
        return parser;
    }

    private static NodeTestDSLParser getParser(final InputStream is) throws IOException {
        NodeTestDSLLexer lexer = new NodeTestDSLLexer( new ANTLRInputStream( is ) );
        NodeTestDSLParser parser = new NodeTestDSLParser( new CommonTokenStream( lexer ) );
        return parser;
    }

    private static NodeTestDSLParser getParser(final String source) throws IOException {
        NodeTestDSLLexer lexer = new NodeTestDSLLexer( new ANTLRStringStream( source ) );
        NodeTestDSLParser parser = new NodeTestDSLParser( new CommonTokenStream( lexer ) );
        return parser;
    }

    public static class EmptyNotifier extends RunNotifier {
        public static final EmptyNotifier INSTANCE = new EmptyNotifier();

        @Override
        public void fireTestAssumptionFailed(Failure failure) {
        }

        @Override
        public void fireTestFailure(Failure failure) {
        }

        @Override
        public void fireTestFinished(Description description) {
        }

        @Override
        public void fireTestIgnored(Description description) {
        }

        @Override
        public void fireTestRunFinished(org.junit.runner.Result result) {
        }

        @Override
        public void fireTestRunStarted(Description description) {
        }

        @Override
        public void fireTestStarted(Description description) throws StoppedByUserException {
        }
    }
}