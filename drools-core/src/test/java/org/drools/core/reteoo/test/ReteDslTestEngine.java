/*
 * Copyright 2010 Red Hat, Inc. and/or its affiliates.
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

package org.drools.core.reteoo.test;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import junit.framework.AssertionFailedError;
import org.antlr.runtime.ANTLRInputStream;
import org.antlr.runtime.ANTLRReaderStream;
import org.antlr.runtime.ANTLRStringStream;
import org.antlr.runtime.CommonTokenStream;
import org.antlr.runtime.RecognitionException;
import org.antlr.runtime.TokenStream;
import org.antlr.runtime.tree.CommonTree;
import org.antlr.runtime.tree.CommonTreeNodeStream;
import org.antlr.runtime.tree.Tree;
import org.drools.core.RuleBaseConfiguration;
import org.drools.core.common.DefaultFactHandle;
import org.drools.core.common.InternalFactHandle;
import org.drools.core.common.InternalWorkingMemory;
import org.drools.core.common.Memory;
import org.drools.core.common.PropagationContextFactory;
import org.drools.core.definitions.InternalKnowledgePackage;
import org.drools.core.definitions.impl.KnowledgePackageImpl;
import org.drools.core.definitions.rule.impl.RuleImpl;
import org.drools.core.impl.KnowledgeBaseImpl;
import org.drools.core.reteoo.AccumulateNode;
import org.drools.core.reteoo.AccumulateNode.AccumulateMemory;
import org.drools.core.reteoo.BetaMemory;
import org.drools.core.reteoo.BetaNode;
import org.drools.core.reteoo.LeftTuple;
import org.drools.core.reteoo.LeftTupleImpl;
import org.drools.core.reteoo.LeftTupleSink;
import org.drools.core.reteoo.ModifyPreviousTuples;
import org.drools.core.reteoo.NodeTypeEnums;
import org.drools.core.reteoo.ObjectSink;
import org.drools.core.reteoo.ObjectTypeNode;
import org.drools.core.reteoo.RightInputAdapterNode;
import org.drools.core.reteoo.RightTuple;
import org.drools.core.reteoo.RightTupleImpl;
import org.drools.core.reteoo.RuleTerminalNode;
import org.drools.core.reteoo.Sink;
import org.drools.core.reteoo.TupleMemory;
import org.drools.core.reteoo.builder.BuildContext;
import org.drools.core.reteoo.test.dsl.AccumulateNodeStep;
import org.drools.core.reteoo.test.dsl.BetaNodeStep;
import org.drools.core.reteoo.test.dsl.BindingStep;
import org.drools.core.reteoo.test.dsl.ConfigStep;
import org.drools.core.reteoo.test.dsl.DSLMock;
import org.drools.core.reteoo.test.dsl.DslStep;
import org.drools.core.reteoo.test.dsl.EvalNodeStep;
import org.drools.core.reteoo.test.dsl.ExistsNodeStep;
import org.drools.core.reteoo.test.dsl.FactsStep;
import org.drools.core.reteoo.test.dsl.JoinNodeStep;
import org.drools.core.reteoo.test.dsl.LeftInputAdapterNodeStep;
import org.drools.core.reteoo.test.dsl.LeftTupleSinkStep;
import org.drools.core.reteoo.test.dsl.MockitoHelper;
import org.drools.core.reteoo.test.dsl.NodeTestCase;
import org.drools.core.reteoo.test.dsl.NodeTestCaseResult;
import org.drools.core.reteoo.test.dsl.NodeTestCaseResult.NodeTestResult;
import org.drools.core.reteoo.test.dsl.NodeTestCaseResult.Result;
import org.drools.core.reteoo.test.dsl.NodeTestDef;
import org.drools.core.reteoo.test.dsl.NotNodeStep;
import org.drools.core.reteoo.test.dsl.ObjectTypeNodeStep;
import org.drools.core.reteoo.test.dsl.RIANodeStep;
import org.drools.core.reteoo.test.dsl.ReteTesterHelper;
import org.drools.core.reteoo.test.dsl.RuleTerminalNodeStep;
import org.drools.core.reteoo.test.dsl.Step;
import org.drools.core.reteoo.test.dsl.WithStep;
import org.drools.core.reteoo.test.parser.NodeTestDSLLexer;
import org.drools.core.reteoo.test.parser.NodeTestDSLParser;
import org.drools.core.reteoo.test.parser.NodeTestDSLParser.compilation_unit_return;
import org.drools.core.reteoo.test.parser.NodeTestDSLTree;
import org.drools.core.rule.MVELDialectRuntimeData;
import org.drools.core.spi.PropagationContext;
import org.drools.core.spi.Tuple;
import org.drools.core.util.Iterator;
import org.junit.runner.Description;
import org.junit.runner.notification.Failure;
import org.junit.runner.notification.RunNotifier;
import org.junit.runner.notification.StoppedByUserException;
import org.mvel2.MVEL;

public class ReteDslTestEngine {

    public static final String  WORKING_MEMORY           = "WorkingMemory";
    public static final String  BUILD_CONTEXT            = "BuildContext";
    private static final String CONFIG                   = "Config";
    private static final String OBJECT_TYPE_NODE         = "ObjectTypeNode";
    private static final String LEFT_INPUT_ADAPTER_NODE  = "LeftInputAdapterNode";
    private static final String BINDING                  = "Binding";
    private static final String JOIN_NODE                = "JoinNode";
    private static final String NOT_NODE                 = "NotNode";
    private static final String EXISTS_NODE              = "ExistsNode";
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


    PropagationContextFactory pctxFactory;

    public ReteDslTestEngine() {

        this.reteTesterHelper = new ReteTesterHelper();

        this.steps = new HashMap<String, Object>();

        this.steps.put( CONFIG,
                        new ConfigStep() );
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
            NodeTestResult testResult = createTestResult( test,
                                                          null );

            try {
                testResult = run( testCase,
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

            } catch ( Throwable e ) {
                notifier.fireTestFailure( new Failure( test.getDescription(),
                                                       e ) );
            }
 
            result.add( testResult );
        }
    
        return result;
    }

    private NodeTestResult run(NodeTestCase testCase,
                               NodeTestDef test) {
        Map<String, Object> context = createContext( testCase );
        NodeTestResult result = createTestResult( test,
                                                  context );

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
        } catch ( Throwable e ) {
            result.result = Result.ERROR;
            result.errorMsgs.add( e.toString() );
        }
        return result;
    }
    
    private NodeTestResult createTestResult(NodeTestDef test,
                                            Map<String, Object> context) {

        return new NodeTestResult( test,
                                   Result.NOT_EXECUTED,
                                   context,
                                   new LinkedList<String>() );
    }
    

    private Map<String, Object> createContext(NodeTestCase testCase) {
        Map<String, Object> context = new HashMap<String, Object>();
        context.put( "TestCase",
                     testCase );

        RuleBaseConfiguration conf = new RuleBaseConfiguration();

        KnowledgeBaseImpl rbase = new KnowledgeBaseImpl( "ID",
                                                         conf );
        BuildContext buildContext = new BuildContext( rbase );

        RuleImpl rule = new RuleImpl("rule1").setPackage( "org.pkg1");
        InternalKnowledgePackage pkg = new KnowledgePackageImpl( "org.pkg1" );
        pkg.getDialectRuntimeRegistry().setDialectData( "mvel", new MVELDialectRuntimeData() );
        pkg.addRule( rule );
        
        buildContext.setRule( rule );

        pctxFactory = conf.getComponentFactory().getPropagationContextFactory();
        
        rbase.addPackage( pkg );
        context.put( BUILD_CONTEXT,
                     buildContext );
        context.put( "ClassFieldAccessorStore",
                     this.reteTesterHelper.getStore() );

        InternalWorkingMemory wm = rbase.newStatefulSession( true );
        context.put( WORKING_MEMORY,
                     wm );
        return context;
    }

    public Map<String, Object> run(Map<String, Object> context,
                                   List<DslStep> steps,
                                   NodeTestResult result) {
        InternalWorkingMemory wm = (InternalWorkingMemory) context.get( WORKING_MEMORY );
        for ( DslStep step : steps ) {
            String name = step.getName();
            Object object = this.steps.get( name );
            if ( object != null && object instanceof Step ) {
                Step stepImpl = (Step) object;
                try {
                    stepImpl.execute( context,
                                      step.getCommands() );
                } catch ( Exception e ) {
                    e.printStackTrace();
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
            if ( node instanceof AccumulateNode ) {
                AccumulateMemory accmem = (AccumulateMemory) wm.getNodeMemory( node );
                memory = accmem.getBetaMemory();
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

                    TupleMemory leftMemory = memory.getLeftTupleMemory();

                    if ( expectedLeftTuples.isEmpty() && leftMemory.size() != 0 ) {
                        throw new AssertionFailedError( "line " + step.getLine()
                                                        + ": left Memory expected [] actually "
                                                        + print( leftMemory,
                                                                 true ) );
                    } else if ( expectedLeftTuples.isEmpty()
                                && leftMemory.size() == 0 ) {
                        continue;
                        
                    }

                    // we always lookup from the first element, in case it's indexed
                    List<InternalFactHandle> first = (List<InternalFactHandle>) expectedLeftTuples.get( 0 );
                    LeftTuple firstTuple = new LeftTupleImpl( first.get( 0 ),
                                                              null,
                                                              false);
                    for ( int i = 1; i < first.size(); i++ ) {
                        firstTuple = new LeftTupleImpl( firstTuple,
                                                        new RightTupleImpl( first.get( i )),
                                                        null,
                                                        false );
                    }

                    List<Tuple> leftTuples = new ArrayList<Tuple>();

                    for ( Tuple leftTuple = getFirst(memory.getLeftTupleMemory(), firstTuple); leftTuple != null; leftTuple = (LeftTuple) leftTuple.getNext() ) {
                        leftTuples.add( leftTuple );
                    }
                    
                    // When L&R Unlinking is active, we need to sort the
                    // tuples here,
                    // because we might have asserted things in the wrong
                    // order,
                    // since linking a node's side means populating its
                    // memory
                    // from the OTN which stores things in a hash-set, so
                    // insertion order is not kept.
                    Collections.sort( leftTuples,
                                      new TupleComparator() );

                    List<List<InternalFactHandle>> actualLeftTuples = getHandlesList( leftTuples );


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

                    TupleMemory rightMemory = memory.getRightTupleMemory();

                    if ( expectedFactHandles.isEmpty() && rightMemory.size() != 0 ) {
                        throw new AssertionError( "line " + step.getLine() + ": right Memory expected [] actually " + print( rightMemory ));
                    } else if ( expectedFactHandles.isEmpty() && rightMemory.size() == 0 ) {
                        continue;
                    }

                    RightTuple first = new RightTupleImpl( (InternalFactHandle) expectedFactHandles.get( 0 ) );
                    List<Tuple> actualRightTuples = new ArrayList<Tuple>();
                    for ( Tuple rightTuple = getFirst(memory.getRightTupleMemory(), first); rightTuple != null; rightTuple = (RightTuple) rightTuple.getNext() ) {
                        actualRightTuples.add( rightTuple );
                    }

                    if ( expectedFactHandles.size() != actualRightTuples.size() ) {
                        throw new AssertionError( "line " + step.getLine() + ": right Memory expected " + print( expectedFactHandles ) 
                                                  + " actually " + print( actualRightTuples ));
                    }

                    for ( int i = 0, length = actualRightTuples.size(); i < length; i++ ) {
                        if ( expectedFactHandles.get( i ) != actualRightTuples.get( i ).getFactHandle() ) {
                            throw new AssertionError( "line " + step.getLine() + ": right Memory expected " + print( expectedFactHandles )
                                                      + " actually " + print( actualRightTuples ));
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
    
    private Tuple getFirst(TupleMemory memory, Tuple leftTuple) {
        Iterator<LeftTuple> it = memory.iterator();
        for ( LeftTuple next = it.next(); next != null; next = it.next() ) {
          if (next.equals( leftTuple ) ) {
              return next.getMemory().getFirst();
          }            
        }
        
        return null;
    }    
    
    private List<List<InternalFactHandle>> getHandlesList(
                                                          List<Tuple> leftTuples) {
        List<List<InternalFactHandle>> actualLeftTuples = new ArrayList<List<InternalFactHandle>>( leftTuples.size() );
        for ( Tuple leftTuple : leftTuples ) {
            List<InternalFactHandle> tupleHandles = Arrays.asList( leftTuple
                    .toFactHandles() );
            actualLeftTuples.add( tupleHandles );
        }
        return actualLeftTuples;
    }

    private String print(TupleMemory leftMemory,
                         boolean lrUnlinkingEnabled) {

        List<Tuple> tuples = new ArrayList<Tuple>();
        Iterator<LeftTuple> it = leftMemory.iterator();
        for ( LeftTuple tuple = it.next(); tuple != null; tuple = it.next() ) {
            tuples.add( tuple );
        }

        if ( lrUnlinkingEnabled ) {
            // Necessary only when L&R unlinking are active.
            Collections.sort( tuples,
                              new TupleComparator() );
        }

        return print( getHandlesList( tuples ) );
    }

    private String print(TupleMemory memory) {

        List<RightTuple> tuples = new ArrayList<RightTuple>();
        Iterator it = memory.iterator();

        for ( RightTuple tuple = (RightTuple) it.next(); tuple != null; tuple = (RightTuple) it
                .next() ) {
            tuples.add( tuple );
        }

        return "[" + print( tuples ) + "]";
    }

    /** Provides better error messages. */
    protected String print(List< ? > tuples) {

        StringBuilder b = new StringBuilder();

        for ( java.util.Iterator iterator = tuples.iterator(); iterator
                .hasNext(); ) {

            Object tuple = iterator.next();

            if ( tuple instanceof List< ? > ) {
                b.append( "[" );
                b.append( print( (List< ? >) tuple ) );
                b.append( "]" );
            } else if ( tuple instanceof InternalFactHandle ) {
                InternalFactHandle h = (InternalFactHandle) tuple;
                b.append( "h" ).append( h.getId() - 1 );
            } else if ( tuple instanceof RightTuple ) {
                InternalFactHandle h = ((RightTuple) tuple)
                        .getFactHandle();
                b.append( "h" ).append( h.getId() - 1 );
            }

            if ( iterator.hasNext() ) b.append( ", " );
        }

        if ( b.length() == 0 ) return "[]";

        return b.toString();
    }


    private void riaNode(DslStep step,
                         RightInputAdapterNode node,
                         Map<String, Object> context,
                         InternalWorkingMemory wm) {
        try {
            List<String[]> cmds = step.getCommands();
            List<InternalFactHandle> handles = (List<InternalFactHandle>) context.get( "Handles" );

            BetaMemory bm;
            Memory childMemory = wm.getNodeMemory( node );
            if ( node.getType() == NodeTypeEnums.AccumulateNode ) {
                bm =  ((AccumulateMemory) childMemory).getBetaMemory();
            } else {
                bm =  (BetaMemory) childMemory;
            }

            int memorySize = bm.getRightTupleMemory().size();
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

                    if ( expectedLeftTuples.isEmpty() && memorySize != 0 ) {
                        throw new AssertionError( "line " + step.getLine() + ": left tuples expected "+expectedLeftTuples.isEmpty()+" actually " + memorySize );
                    } else if ( expectedLeftTuples.isEmpty() && memorySize == 0 ) {
                        return;
                    }

                    // create expected tuples
                    List<LeftTuple> leftTuples = new ArrayList<LeftTuple>();
                    for ( List<InternalFactHandle> tlist : (List<List<InternalFactHandle>>) expectedLeftTuples ) {
                        LeftTuple tuple = new LeftTupleImpl( tlist.get( 0 ),
                                                         null,
                                                         false );
                        for ( int i = 1; i < tlist.size(); i++ ) {
                            tuple = new LeftTupleImpl( tuple,
                                                   new RightTupleImpl( tlist.get( i ) ),
                                                   null,
                                                   false );
                        }
                        leftTuples.add( tuple );

                    }

                    // get actual tuples
                    final List<LeftTuple> actualTuples = new ArrayList<LeftTuple>();
                    final Iterator it = bm.getRightTupleMemory().iterator();
                    for ( RightTuple entry = (RightTuple) it.next(); entry != null; entry = (RightTuple) it.next() ) {
                        LeftTuple leftTuple = (LeftTuple) entry.getFactHandle().getObject();
                        actualTuples.add( leftTuple );
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
                            PropagationContext pContext = pctxFactory.createPropagationContext(wm.getNextPropagationIdCounter(), PropagationContext.Type.INSERTION,
                                                                                               null, null, handle);
                            ((ObjectSink) sink).assertObject( handle,
                                                              pContext,
                                                              wm );
                            pContext.evaluateActionQueue( wm );
                        } else {
                            List<InternalFactHandle> tlist = (List<InternalFactHandle>) element;
                            LeftTuple tuple = createTuple( context,
                                                           tlist );
                            PropagationContext pContext = pctxFactory.createPropagationContext(wm.getNextPropagationIdCounter(), PropagationContext.Type.INSERTION,
                                                                                               null, tuple, null);
                            ((LeftTupleSink) sink).assertLeftTuple( tuple,
                                                                    pContext,
                                                                    wm );
                            pContext.evaluateActionQueue( wm );
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
                tuple = new LeftTupleImpl( handle,
                                           null,
                                           false ); // do not keep generated tuples on the handle list
            } else {
                tuple = new LeftTupleImpl( tuple,
                                           new RightTupleImpl( handle ),
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
                            PropagationContext pContext = pctxFactory.createPropagationContext(wm.getNextPropagationIdCounter(), PropagationContext.Type.DELETION,
                                                                                               null, null, handle);
                            if ( sink instanceof ObjectTypeNode ) {
                                ((ObjectTypeNode) sink).retractObject( handle,
                                                                       pContext,
                                                                       wm );
                            } else {
                                handle.forEachRightTuple( rt -> rt.retractTuple( pContext, wm ) );
                                handle.clearRightTuples();
                                handle.forEachLeftTuple( lt -> lt.retractTuple( pContext, wm ) );
                                handle.clearLeftTuples();
                            }
                            pContext.evaluateActionQueue( wm );
                        } else {
                            List<InternalFactHandle> tlist = (List<InternalFactHandle>) element;
                            String id = getTupleId( tlist );
                            LeftTuple tuple = (LeftTuple) context.remove( id );
                            if ( tuple == null ) {
                                throw new IllegalArgumentException( "Tuple not found: " + id + " : " + tlist.toString() );
                            }
                            PropagationContext pContext = pctxFactory.createPropagationContext(wm.getNextPropagationIdCounter(), PropagationContext.Type.DELETION,
                                                                                               null, tuple, null);
                            ((LeftTupleSink) sink).retractLeftTuple( tuple,
                                                                     pContext,
                                                                     wm );
                            pContext.evaluateActionQueue( wm );
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
                            PropagationContext pContext = pctxFactory.createPropagationContext(wm.getNextPropagationIdCounter(), PropagationContext.Type.MODIFICATION,
                                                                                               null, null, handle);
                            ModifyPreviousTuples modifyPreviousTuples = new ModifyPreviousTuples( handle.detachLinkedTuples() );
                            ((ObjectSink) sink).modifyObject( handle,
                                                              modifyPreviousTuples,
                                                              pContext,
                                                              wm );
                            modifyPreviousTuples.retractTuples( pContext,
                                                                wm );
                            pContext.evaluateActionQueue( wm );
                        } else {
                            List<InternalFactHandle> tlist = (List<InternalFactHandle>) element;
                            String id = getTupleId( tlist );
                            LeftTuple tuple = (LeftTuple) context.get( id );
                            if ( tuple == null ) {
                                throw new IllegalArgumentException( "Tuple not found: " + id + " : " + tlist.toString() );
                            }
                            PropagationContext pContext = pctxFactory.createPropagationContext(wm.getNextPropagationIdCounter(), PropagationContext.Type.MODIFICATION,
                                                                                               null, tuple, new DefaultFactHandle(1, ""));
                            pContext.evaluateActionQueue( wm );
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
    
    private static final class TupleComparator implements Comparator<Tuple> {
        
        public int compare(Tuple o1,
                           Tuple o2) {

            InternalFactHandle[] h1 = o1.toFactHandles();
            InternalFactHandle[] h2 = o2.toFactHandles();

            // Handles have to be compared in the inverse order.
            for ( int i = (h1.length - 1); i >= 0; i-- ) {

                int diff = h1[i].getId() - h2[i].getId();

                // Will continue comparing handles until
                // a difference is found.
                if ( diff != 0 ) return diff;
            }

            return 0;
        }
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
