package org.drools.reteoo.test;

import java.beans.IntrospectionException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.drools.FactHandle;
import org.drools.RuleBaseConfiguration;
import org.drools.WorkingMemory;
import org.drools.base.ClassFieldAccessorCache;
import org.drools.base.ClassFieldAccessorStore;
import org.drools.base.ClassObjectType;
import org.drools.base.ValueType;
import org.drools.base.evaluators.EvaluatorRegistry;
import org.drools.base.evaluators.Operator;
import org.drools.command.Context;
import org.drools.common.DefaultFactHandle;
import org.drools.common.InternalFactHandle;
import org.drools.common.InternalWorkingMemory;
import org.drools.common.PropagationContextImpl;
import org.drools.common.SingleBetaConstraints;
import org.drools.reteoo.BetaMemory;
import org.drools.reteoo.BetaNode;
import org.drools.reteoo.EntryPointNode;
import org.drools.reteoo.JoinNode;
import org.drools.reteoo.LeftInputAdapterNode;
import org.drools.reteoo.LeftTuple;
import org.drools.reteoo.LeftTupleMemory;
import org.drools.reteoo.LeftTupleSource;
import org.drools.reteoo.MockLeftTupleSink;
import org.drools.reteoo.MockObjectSource;
import org.drools.reteoo.MockTupleSource;
import org.drools.reteoo.ObjectSink;
import org.drools.reteoo.ObjectSource;
import org.drools.reteoo.ObjectTypeNode;
import org.drools.reteoo.ReteooRuleBase;
import org.drools.reteoo.RightTuple;
import org.drools.reteoo.RightTupleMemory;
import org.drools.reteoo.TupleSourceTest;
import org.drools.reteoo.ReteooBuilder.IdGenerator;
import org.drools.reteoo.builder.BuildContext;
import org.drools.rule.BehaviorManager;
import org.drools.rule.Declaration;
import org.drools.rule.Package;
import org.drools.rule.Pattern;
import org.drools.rule.VariableConstraint;
import org.drools.spi.BetaNodeFieldConstraint;
import org.drools.spi.Evaluator;
import org.drools.spi.InternalReadAccessor;
import org.drools.spi.PropagationContext;
import org.drools.util.StringUtils;
import org.mvel2.MVEL;

public class ReteDslTestEngine {

    private ReteTesterHelper    reteTesterHelper;
    private Map<String, Object> steps;

    public ReteDslTestEngine() {

        this.reteTesterHelper = new ReteTesterHelper();

        this.steps = new HashMap<String, Object>();

        this.steps.put( "ObjectTypeNode",
                        new ObjectTypeNodeStep( this.reteTesterHelper ) );
        this.steps.put( "LeftInputAdapterNode",
                        new LeftInputAdapterNodeStep( this.reteTesterHelper ) );
        this.steps.put( "Binding",
                        new BindingStep( this.reteTesterHelper ) );
        this.steps.put( "JoinNode",
                        new JoinNodeStep( this.reteTesterHelper ) );
        this.steps.put( "Facts",
                        new FactsStep( this.reteTesterHelper ) );
    }

    public Map run(List<DslStep> steps) {
        Map context = new HashMap();

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

        for ( DslStep step : steps ) {
            String name = step.getName();
            Object object = this.steps.get( name );
            if ( object != null && object instanceof Step ) {
                Step stepImpl = (Step) object;
                try {
                    stepImpl.execute( context,
                                      step.getCommands().toArray( new String[0] ) );
                } catch ( Exception e ) {
                    throw new IllegalArgumentException( "line " + step.getLine() + ": unable to execute step " + step,
                                                        e );
                }
            } else if ( "assert".equals( name.trim() ) ) {
                assertObject(step, context, wm);
            }  else if ( "retract".equals( name.trim() ) ) {
                retractObject(step, context, wm);
            } else {
                Object node = context.get( name.trim() );
                if ( name == null ) {
                    throw new IllegalArgumentException( "line " + step.getLine() + ": step " + name + " does not exist" );    
                }
                
                if ( node instanceof BetaNode ) {
                    BetaNode betaNode = (BetaNode) node;
                    betaNode( step, betaNode, context, wm );
                } else {
                    throw new IllegalArgumentException( "line " + step.getLine() + ": unknown node " + node );
                }                                
            }
        }

        return context;
    }
    
    private void betaNode(DslStep step, BetaNode node, Map context, InternalWorkingMemory wm) {
        try {            
            String[] cmds = step.getCommands().toArray( new String[0] );
            List<InternalFactHandle> handles = (List<InternalFactHandle>) context.get( "Handles" );
            
            
            BetaMemory memory = (BetaMemory) wm.getNodeMemory( node );
            for ( String cmd : cmds ) {
                if ( cmd.trim().startsWith( "leftMemory" ) ) {                    
                    int pos = cmd.indexOf( "[" );
                    String nodeName = cmd.substring( 0,
                                                     pos ).trim();
                    String args = cmd.substring( pos ).trim();
                    String listString = args.replaceAll( "h(\\d+)",
                                                         "h[$1]" );
                    Map vars = new HashMap();
                    vars.put( "h",
                              handles );
                    List expectedLeftTuples = (List) MVEL.eval( listString,
                                                  vars );                    
                    
                    LeftTupleMemory leftMemory = memory.getLeftTupleMemory();

                    List actualLeftTuples = null;
                    
                    // we always lookup from the first element, in case it's indexed
                    List<InternalFactHandle> first = (List<InternalFactHandle>) expectedLeftTuples.get( 0 );
                    LeftTuple firstTuple = new LeftTuple(first.get( 0 ), null, false);
                    for ( int i = 1; i < first.size(); i++ ) {
                        firstTuple = new LeftTuple(firstTuple, null, false);
                    }
                    
                    List<LeftTuple> leftTuples = new ArrayList<LeftTuple>();
                    
                    for ( LeftTuple leftTuple = memory.getLeftTupleMemory().getFirst( firstTuple ); leftTuple != null; leftTuple = (LeftTuple) leftTuple.getNext() ) {
                        leftTuples.add( leftTuple );
                    }
                    actualLeftTuples = new ArrayList( leftTuples.size() );
                    for ( LeftTuple leftTuple : leftTuples ) {
                        List<InternalFactHandle> tupleHandles = Arrays.asList( leftTuple.toFactHandles() );
                        actualLeftTuples.add( tupleHandles );                        
                    }  
                    
                    if ( !expectedLeftTuples.equals( actualLeftTuples ) ) {
                        throw new AssertionError( "line " + step.getLine() + ": left Memory expected " + expectedLeftTuples + " actually " + actualLeftTuples);
                    }
                                           
                } else if ( cmd.trim().startsWith( "rightMemory" ) ) {
                    int pos = cmd.indexOf( "[" );
                    String nodeName = cmd.substring( 0,
                                                     pos ).trim();
                    String args = cmd.substring( pos ).trim();
                    String listString = args.replaceAll( "h(\\d+)",
                                                         "h[$1]" );
                    Map vars = new HashMap();
                    vars.put( "h",
                              handles );
                    List expectedFactHandles = (List) MVEL.eval( listString,
                                                  vars );                    
                    
                    RightTupleMemory rightMemory = memory.getRightTupleMemory();  
                    
                    InternalFactHandle first = ( InternalFactHandle ) expectedFactHandles.get( 0 );
                    List<RightTuple> actualRightTuples = new ArrayList();
                    for ( RightTuple rightTuple = memory.getRightTupleMemory().getFirst( first.getFirstRightTuple() ); rightTuple != null; rightTuple = (RightTuple) rightTuple.getNext() ) {
                        actualRightTuples.add( rightTuple );
                    }
                    
                    if ( expectedFactHandles.size() != actualRightTuples.size() ) {
                        throw new AssertionError( "line " + step.getLine() + ": right Memory expected " + actualRightTuples + " actually " + actualRightTuples);
                    }
                    
                    for ( int i = 0, length = actualRightTuples.size(); i < length; i++ ) {
                        if ( expectedFactHandles.get(i) != actualRightTuples.get( i ).getFactHandle() ) {
                            throw new AssertionError( "line " + step.getLine() + ": right Memory expected " + actualRightTuples + " actually " + actualRightTuples);
                        }
                    }
                    
                } else {
                    throw new IllegalArgumentException( "line " + step.getLine() + ": command does not exist " + cmd.trim() );                    
                }
            }
        } catch ( Exception e ) {
            throw new IllegalArgumentException( "line " + step.getLine() + ": unable to execute step " + step,
                                                e );
        }            
    }
    
    private void assertObject(DslStep step, Map context, InternalWorkingMemory wm) {
        try {
            String[] cmds = step.getCommands().toArray( new String[0] );
            List<InternalFactHandle> handles = (List<InternalFactHandle>) context.get( "Handles" );
            for ( String cmd : cmds ) {
                try {
                    int pos = cmd.indexOf( "[" );
                    String nodeName = cmd.substring( 0,
                                                     pos ).trim();
                    ObjectTypeNode sink = (ObjectTypeNode) context.get( nodeName );
                    if ( sink == null ) {
                        throw new IllegalArgumentException( "line " + step.getLine() + ": node " + nodeName + " does not exist" );
                    }

                    String args = cmd.substring( pos ).trim();
                    String listString = args.replaceAll( "h(\\d+)",
                                                         "h[$1]" );
                    Map vars = new HashMap();
                    vars.put( "h",
                              handles );
                    List<InternalFactHandle> list = (List<InternalFactHandle>) MVEL.eval( listString,
                                                                                          vars );
                    if ( list == null ) {
                        throw new IllegalArgumentException( cmd.trim() + " does not specify an existing fact handle" );
                    }

                    for ( InternalFactHandle handle : list ) {
                        if ( handle == null ) {
                            throw new IllegalArgumentException( cmd.trim() + " does not specify an existing fact handle" );
                        }

                        PropagationContext pContext = new PropagationContextImpl( wm.getNextPropagationIdCounter(),
                                                                                  PropagationContext.ASSERTION,
                                                                                  null,
                                                                                  null,
                                                                                  handle );
                        sink.assertObject( handle,
                                           pContext,
                                           wm );
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
    
    private void retractObject(DslStep step, Map context, InternalWorkingMemory wm) {
        try {
            String[] cmds = step.getCommands().toArray( new String[0] );
            List<InternalFactHandle> handles = (List<InternalFactHandle>) context.get( "Handles" );
            for ( String cmd : cmds ) {
                try {
                    int pos = cmd.indexOf( "[" );
                    String nodeName = cmd.substring( 0,
                                                     pos ).trim();
                    ObjectTypeNode sink = (ObjectTypeNode) context.get( nodeName );
                    if ( sink == null ) {
                        throw new IllegalArgumentException( "line " + step.getLine() + ": node " + nodeName + " does not exist" );
                    }

                    String args = cmd.substring( pos ).trim();
                    String listString = args.replaceAll( "h(\\d+)",
                                                         "h[$1]" );
                    Map vars = new HashMap();
                    vars.put( "h",
                              handles );
                    List<InternalFactHandle> list = (List<InternalFactHandle>) MVEL.eval( listString,
                                                                                          vars );
                    if ( list == null ) {
                        throw new IllegalArgumentException( cmd.trim() + " does not specify an existing fact handle" );
                    }

                    for ( InternalFactHandle handle : list ) {
                        if ( handle == null ) {
                            throw new IllegalArgumentException( cmd.trim() + " does not specify an existing fact handle" );
                        }

                        PropagationContext pContext = new PropagationContextImpl( wm.getNextPropagationIdCounter(),
                                                                                  PropagationContext.RETRACTION,
                                                                                  null,
                                                                                  null,
                                                                                  handle );
                        sink.retractObject( handle, pContext, wm );
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

    public static List<DslStep> buildDslCommands(Reader reader) {
        try {
            int commandIndentPos = 0;

            DslStep dslCommand = null;
            List<DslStep> commands = new ArrayList<DslStep>();
            StringBuilder cmdContent = null;
            List<String> cmds = null;

            List<String> lines = chunkReader( reader );
            int lineCount = 1;

            for ( String line : lines ) {
                if ( StringUtils.isEmpty( line ) ) {
                    lineCount++;
                    continue;
                }

                if ( line.charAt( 0 ) != ' ' ) {
                    // finish of the last command
                    if ( dslCommand != null ) {
                        // existing dslCommand, so add arguments before starting again
                        cmds.add( cmdContent.toString() );
                    }

                    // start of new command
                    cmds = new ArrayList<String>();
                    dslCommand = new DslStep( lineCount,
                                              line.trim(),
                                              cmds );
                    commands.add( dslCommand );
                    commandIndentPos = 0;
                } else {
                    if ( commandIndentPos == 0 ) {
                        commandIndentPos = indentPos( line );
                        cmdContent = new StringBuilder();
                        cmdContent.append( line.trim() );
                    } else {
                        if ( indentPos( line ) > commandIndentPos ) {
                            cmdContent.append( line.trim() );
                        } else {
                            cmds.add( cmdContent.toString() );

                            cmdContent = new StringBuilder();
                            cmdContent.append( line.trim() );
                            commandIndentPos = indentPos( line );
                        }
                    }
                }
                lineCount++;
            }

            // finish of the last command
            if ( dslCommand != null ) {
                // existing dslCommand, so add arguments before starting again
                cmds.add( cmdContent.toString() );
            }

            return commands;
        } catch ( Exception e ) {
            throw new RuntimeException( e );
        }
    }

    public static int indentPos(String line) {
        int i;
        for ( i = 0; i < line.length() && line.charAt( i ) == ' '; i++ ) {
            // iterate to first char
        }

        return i;
    }

    /**
     * This chunks the reader into a List<String> it removes single line and block comements
     * while preserving spacing.
     */
    public static List<String> chunkReader(Reader reader) {
        List<String> lines = new ArrayList<String>();
        BufferedReader bReader = null;
        if ( !(reader instanceof BufferedReader) ) {
            bReader = new BufferedReader( reader );
        }
        String line;
        int pos;
        boolean blockComment = false;
        try {
            while ( (line = bReader.readLine()) != null ) {
                if ( !blockComment ) {
                    pos = line.indexOf( "/*" );
                    if ( pos != -1 ) {
                        int endPos = line.indexOf( "*/" );
                        if ( endPos != -1 ) {
                            // we end the block commend on the same time
                            blockComment = false;
                        } else {
                            // replace line till end
                            blockComment = true;
                        }

                        line = line.substring( 0,
                                               pos ).concat( StringUtils.repeat( " ",
                                                                                 line.length() - pos ) );
                    } else {
                        // no block comment, so see if single line comment
                        pos = line.indexOf( "//" );
                        if ( pos != -1 ) {
                            // we have a single line comment
                            line = line.substring( 0,
                                                   pos ).concat( StringUtils.repeat( " ",
                                                                                     line.length() - pos ) );
                        }
                    }
                } else {
                    // we are in a block comment, replace all text until end of block
                    pos = line.indexOf( "*/" );
                    if ( pos != -1 ) {
                        line = StringUtils.repeat( " ",
                                                   pos + 2 ).concat( line.substring( pos + 2,
                                                                                     line.length() ) );
                        blockComment = false;
                    } else {
                        line = StringUtils.repeat( " ",
                                                   line.length() );
                    }
                }
                lines.add( line );
            }
        } catch ( IOException e ) {
            throw new RuntimeException( e );
        }

        return lines;
    }

    public static class DslStep {
        private int          line;

        private String       name;
        private List<String> commands;

        public DslStep(int line,
                       String name,
                       List<String> commands) {
            this.line = line;
            this.name = name;
            this.commands = commands;
        }

        public String getName() {
            return name;
        }

        public int getLine() {
            return line;
        }

        public List<String> getCommands() {
            return commands;
        }

        public String toString() {
            return line + " : " + name + " : " + commands;
        }

    }

    public static interface Step {
        public void execute(Map context,
                            String[] args);
    }

    public static class ObjectTypeNodeStep
        implements
        Step {

        private ReteTesterHelper reteTesterHelper;

        public ObjectTypeNodeStep(ReteTesterHelper reteTesterHelper) {
            this.reteTesterHelper = reteTesterHelper;
        }

        public void execute(Map context,
                            String[] args) {
            BuildContext buildContext = (BuildContext) context.get( "BuildContext" );
            String name;
            String type;

            if ( args.length == 1 ) {
                String[] c = args[0].split( "," );
                name = c[0].trim();
                type = c[1].trim();
            } else {
                throw new IllegalArgumentException( "Cannot arguments " + Arrays.asList( args ) );
            }
            ObjectTypeNode otn;
            try {
                EntryPointNode epn = new EntryPointNode( buildContext.getNextId(),
                                                         buildContext.getRuleBase().getRete(),
                                                         buildContext );
                epn.attach();

                otn = new ObjectTypeNode( buildContext.getNextId(),
                                          epn,
                                          new ClassObjectType( Class.forName( type ) ),
                                          buildContext );
                // we don't attach, as we want to manually propagate and not
                // have the working memory propagate
                //otn.attach();
            } catch ( ClassNotFoundException e ) {
                throw new IllegalArgumentException( "Cannot create OTN " + Arrays.asList( args,
                                                                                          e ) );
            }
            context.put( name,
                         otn );
        }

    }

    public static class LeftInputAdapterNodeStep
        implements
        Step {

        private ReteTesterHelper reteTesterHelper;

        public LeftInputAdapterNodeStep(ReteTesterHelper reteTesterHelper) {
            this.reteTesterHelper = reteTesterHelper;
        }

        public void execute(Map context,
                            String[] args) {
            BuildContext buildContext = (BuildContext) context.get( "BuildContext" );
            String name;
            String source;

            if ( args.length == 1 ) {
                String[] c = args[0].split( "," );
                name = c[0].trim();
                source = c[1].trim();
            } else {
                throw new IllegalArgumentException( "Cannot arguments " + Arrays.asList( args ) );
            }
            ObjectTypeNode otn = (ObjectTypeNode) context.get( source );

            LeftInputAdapterNode liaNode = new LeftInputAdapterNode( buildContext.getNextId(),
                                                                     otn,
                                                                     buildContext );
            liaNode.attach();
            context.put( name,
                         liaNode );
        }
    }

    public static class BindingStep
        implements
        Step {

        private ReteTesterHelper reteTesterHelper;

        public BindingStep(ReteTesterHelper reteTesterHelper) {
            this.reteTesterHelper = reteTesterHelper;
        }

        public void execute(Map context,
                            String[] args) {
            BuildContext buildContext = (BuildContext) context.get( "BuildContext" );
            String name;
            String index;
            String type;
            String field;

            if ( args.length != 0 ) {
                String[] c = args[0].split( "," );
                if ( c.length == 3 ) {
                    // TODO
                    throw new IllegalArgumentException( "Cannot create Binding " + Arrays.asList( args ) );
                } else {
                    name = c[0].trim();
                    index = c[1].trim();
                    type = c[2].trim();
                    field = c[3].trim();

                    try {
                        Pattern pattern = new Pattern( Integer.parseInt( index ),
                                                       new ClassObjectType( Class.forName( type ) ) );

                        final Class clazz = ((ClassObjectType) pattern.getObjectType()).getClassType();
                        ClassFieldAccessorStore store = (ClassFieldAccessorStore) context.get( "ClassFieldAccessorStore" );

                        final InternalReadAccessor extractor = store.getReader( clazz,
                                                                                field,
                                                                                getClass().getClassLoader() );

                        Declaration declr = new Declaration( name,
                                                             extractor,
                                                             pattern );
                        context.put( name,
                                     declr );
                    } catch ( Exception e ) {
                        throw new IllegalArgumentException( "Cannot create Binding " + Arrays.asList( args,
                                                                                                      e ) );
                    }
                }

            } else {
                throw new IllegalArgumentException( "Cannot arguments " + Arrays.asList( args ) );
            }
        }
    }

    public static class JoinNodeStep
        implements
        Step {

        private ReteTesterHelper reteTesterHelper;

        public JoinNodeStep(ReteTesterHelper reteTesterHelper) {
            this.reteTesterHelper = reteTesterHelper;
        }

        public void execute(Map context,
                            String[] args) {
            BuildContext buildContext = (BuildContext) context.get( "BuildContext" );

            if ( args.length != 0 ) {
                String[] a = args[0].split( "," );
                String name = a[0].trim();
                String leftInput = a[1].trim();
                String rightInput = a[2].trim();

                LeftTupleSource leftTupleSource;
                if ( "mock".equals( leftInput ) ) {
                    leftTupleSource = new MockTupleSource( buildContext.getNextId() );
                } else {
                    leftTupleSource = (LeftTupleSource) context.get( leftInput );
                }

                ObjectSource rightObjectSource;
                if ( "mock".equals( rightInput ) ) {
                    rightObjectSource = new MockObjectSource( buildContext.getNextId() );
                } else {
                    rightObjectSource = (ObjectSource) context.get( rightInput );
                }

                a = args[1].split( "," );
                String fieldName = a[0].trim();
                String operator = a[1].trim();
                String var = a[2].trim();

                Declaration declr = (Declaration) context.get( var );

                //                Pattern pattern = new Pattern(Integer.parseInt( index )) ;
                //                ObjectType objectType = new ClassObjectType( )
                //                

                BetaNodeFieldConstraint betaConstraint;
                try {
                    betaConstraint = this.reteTesterHelper.getBoundVariableConstraint( declr.getPattern(),
                                                                                       fieldName,
                                                                                       declr,
                                                                                       operator );
                } catch ( IntrospectionException e ) {
                    throw new IllegalArgumentException();
                }

                SingleBetaConstraints constraints = new SingleBetaConstraints( betaConstraint,
                                                                               buildContext.getRuleBase().getConfiguration() );

                JoinNode joinNode = new JoinNode( buildContext.getNextId(),
                                                  leftTupleSource,
                                                  rightObjectSource,
                                                  constraints,
                                                  BehaviorManager.NO_BEHAVIORS,
                                                  buildContext );
                joinNode.attach();
                context.put( name,
                             joinNode );

            } else {
                throw new IllegalArgumentException( "Cannot arguments " + args );

            }
        }
    }

    public static class FactsStep
        implements
        Step {

        private ReteTesterHelper reteTesterHelper;

        public FactsStep(ReteTesterHelper reteTesterHelper) {
            this.reteTesterHelper = reteTesterHelper;
        }

        public void execute(Map context,
                            String[] args) {
            BuildContext buildContext = (BuildContext) context.get( "BuildContext" );

            if ( args.length >= 1 ) {

                WorkingMemory wm = (WorkingMemory) context.get( "WorkingMemory" );
                List handles = (List) context.get( "Handles" );
                if ( handles == null ) {
                    handles = new ArrayList();
                    context.put( "Handles",
                                 handles );
                }

                for ( String arg : args ) {
                    String[] elms = arg.split( "," );
                    for ( String elm : elms ) {
                        FactHandle handle = wm.insert( MVEL.eval( elm ) );
                        handles.add( handle );
                    }
                }

            } else {
                throw new IllegalArgumentException( "Cannot arguments " + Arrays.asList( args ) );
            }
        }
    }

    public static class ReteTesterHelper {

        private Package                 pkg;
        private ClassFieldAccessorStore store;
        private EvaluatorRegistry       registry = new EvaluatorRegistry();

        public ReteTesterHelper() {
            this.pkg = new Package( "org.drools.examples.manners" );
            this.pkg.setClassFieldAccessorCache( new ClassFieldAccessorCache( Thread.currentThread().getContextClassLoader() ) );
            this.store = this.pkg.getClassFieldAccessorStore();
            this.store.setEagerWire( true );
        }

        public Package getPkg() {
            return pkg;
        }

        public ClassFieldAccessorStore getStore() {
            return store;
        }

        public EvaluatorRegistry getRegistry() {
            return registry;
        }

        public BetaNodeFieldConstraint getBoundVariableConstraint(final Pattern pattern,
                                                                  final String fieldName,
                                                                  final Declaration declaration,
                                                                  final String evaluatorString) throws IntrospectionException {
            final Class clazz = ((ClassObjectType) pattern.getObjectType()).getClassType();

            final InternalReadAccessor extractor = store.getReader( clazz,
                                                                    fieldName,
                                                                    getClass().getClassLoader() );

            Evaluator evaluator = getEvaluator( clazz,
                                                evaluatorString );

            return new VariableConstraint( extractor,
                                           declaration,
                                           evaluator );
        }

        public Evaluator getEvaluator(Class cls,
                                      String operator) {
            return registry.getEvaluator( ValueType.determineValueType( cls ),
                                          Operator.determineOperator( operator,
                                                                      false ) );
        }
    }

}