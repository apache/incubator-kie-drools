package org.drools.clp;

import java.io.PrintStream;
import java.io.Reader;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.antlr.runtime.ANTLRReaderStream;
import org.antlr.runtime.ANTLRStringStream;
import org.antlr.runtime.CharStream;
import org.antlr.runtime.CommonTokenStream;
import org.antlr.runtime.Lexer;
import org.antlr.runtime.TokenStream;
import org.drools.RuleBase;
import org.drools.RuleBaseFactory;
import org.drools.StatefulSession;
import org.drools.clp.valuehandlers.FunctionCaller;
import org.drools.clp.valuehandlers.IndexedLocalVariableValue;
import org.drools.clp.valuehandlers.TempTokenVariable;
import org.drools.common.InternalWorkingMemory;
import org.drools.compiler.PackageBuilder;
import org.drools.lang.DRLLexer;
import org.drools.lang.descr.AttributeDescr;
import org.drools.lang.descr.PackageDescr;
import org.drools.lang.descr.RuleDescr;
import org.drools.reteoo.ReteTuple;
import org.drools.spi.GlobalResolver;

public class Shell
    implements
    ParserHandler,
    //GlobalResolver,
    BuildContext,
    ExecutionContext {
    private static final long serialVersionUID = 1L;
    private FunctionRegistry registry;

    private ValueHandler[]   variables;
    private Map              vars       = new HashMap();

    private Map              properties = Collections.EMPTY_MAP;

    private RuleBase         ruleBase;
    private StatefulSession  session;

    private Map              printoutRouters;
    
    private int              index;

    public Shell() {
        this.ruleBase = RuleBaseFactory.newRuleBase();
        this.session = this.ruleBase.newStatefulSession();
        //this.session.setGlobalResolver( this );
        this.variables = new ValueHandler[50];

        this.registry = new FunctionRegistry( BuiltinFunctions.getInstance() );
    }

    public void evalReader(Reader reader) {
        try {
            CLPParser parser = new CLPParser( new CommonTokenStream( new DRLLexer( new ANTLRReaderStream( reader ) ) ) );
            evalParser( parser );
        } catch ( Exception e ) {
            throw new RuntimeException( "Unable to parser Reader",
                                        e );
        }
    }

    public void evalString(String text) {
        CLPParser parser = new CLPParser( newTokenStream( newLexer( newCharStream( text ) ) ) );
        evalParser( parser );
    }

    private void evalParser(CLPParser parser) {
        parser.setFunctionRegistry( this.registry );
        try {
            parser.eval_script( this );
        } catch ( Exception e ) {
            throw new RuntimeException( e );
        }
        //parser.setF
    }

    //    public void functionHandler(FunctionDescr ruleDescr) {
    //        throw new RuntimeException( "Drools Clips does not support FunctionDescr" );
    //    }    

    //    public void functionHandler(FunctionCaller functionCaller) {
    //        
    //    }

    public void lispFormHandler(ValueHandler valueHandler) {
        ExecutionContext context = new ExecutionContextImpl( null,
                                                          null,
                                                          0 );
        valueHandler.getValue( context );
    }

    public void ruleDescrHandler(RuleDescr ruleDescr) {
        String module = getModuleName( ruleDescr.getAttributes() );

        PackageDescr pkg = new PackageDescr( module );
        pkg.addRule( ruleDescr );

        PackageBuilder builder = new PackageBuilder();
        builder.addPackage( pkg );
    }

    public String getModuleName(List list) {
        for ( Iterator it = list.iterator(); it.hasNext(); ) {
            AttributeDescr attr = (AttributeDescr) it.next();
            if ( attr.getName().equals( "agenda-group" ) ) {
                return attr.getValue();
            }
        }
        return "MAIN";
    }

    //    public PackageBuilder getBuilder(String namespace) {
    //        PackageBuilder builder = (PackageBuilder) this.packageBulders.get( namespace );
    //        if ( builder == null ) {
    //            builder = new PackageBuilder();
    //            this.packageBulders.put( namespace, builder );
    //        }
    //        return builder;
    //        
    //    }

    private CharStream newCharStream(final String text) {
        return new ANTLRStringStream( text );
    }

    private CLPLexer newLexer(final CharStream charStream) {
        return new CLPLexer( charStream );
    }

    private TokenStream newTokenStream(final Lexer lexer) {
        return new CommonTokenStream( lexer );
    }

    public void addFunction(FunctionCaller function) {
        //function.getValue( this );

    }

    /* (non-Javadoc)
     * @see org.drools.clp.ExecutionContext#getLocalVariable(int)
     */
    public ValueHandler getLocalVariable(int index) {
        return variables[index];
    }

    /* (non-Javadoc)
     * @see org.drools.clp.ExecutionContext#setLocalVariable(int, org.drools.clp.ValueHandler)
     */
    public void setLocalVariable(int index,
                                 ValueHandler valueHandler) {
        this.variables[index] = valueHandler;
    }

    public FunctionRegistry getFunctionRegistry() {
        return this.registry;
    }

    /* (non-Javadoc)
     * @see org.drools.clp.BuildContext#setProperty(java.lang.Object, java.lang.Object)
     */
    public Object setProperty(Object key,
                              Object value) {
        if ( this.properties == Collections.EMPTY_MAP ) {
            this.properties = new HashMap();
        }
        return this.properties.put( key,
                                    value );
    }

    /* (non-Javadoc)
     * @see org.drools.clp.BuildContext#getProperty(java.lang.Object)
     */
    public Object getProperty(Object key) {
        return this.properties.get( key );
    }

    public Object getObject() {
        // TODO Auto-generated method stub
        return null;
    }

    public InternalWorkingMemory getWorkingMemory() {
        return ( InternalWorkingMemory) this.session;
    }

    /* (non-Javadoc)
     * @see org.drools.clp.ExecutionContext#setPrintoutRouters(java.util.Map)
     */
    public void setPrintoutRouters(Map printoutRouters) {
        this.printoutRouters = printoutRouters;
    }

    /* (non-Javadoc)
     * @see org.drools.clp.ExecutionContext#addPrintoutRouter(java.lang.String, java.io.PrintStream)
     */
    public void addPrintoutRouter(String identifier,
                                  PrintStream stream) {
        this.printoutRouters.put( identifier,
                                  stream );
    }

    /* (non-Javadoc)
     * @see org.drools.clp.ExecutionContext#getPrintoutRouters(java.lang.String)
     */
    public PrintStream getPrintoutRouters(String identifier) {
        return (PrintStream) this.printoutRouters.get( identifier );
    }
    
    public ValueHandler createLocalVariable(String identifier) {
        return new IndexedLocalVariableValue( identifier,
                                              this.index++ );
    }
    
    public void addVariable(VariableValueHandler var) {
        this.vars.put( var.getIdentifier(), var);
    }

    /* (non-Javadoc)
     * @see org.drools.clp.BuildContext#getVariableValueHandler(java.lang.String)
     */
    public ValueHandler getVariableValueHandler(String identifier) {
        ValueHandler var = (ValueHandler) this.vars.get( identifier );
        if ( var == null ) {
            var = new TempTokenVariable( identifier );
        }
        return var;
    }

    public Object resolve(String identifier) {
        ValueHandler var = (ValueHandler) this.vars.get( identifier );
        return var.getObject( this );
    }

//    public Object resolveGlobal(String arg0) {
//        // TODO Auto-generated method stub
//        return null;
//    }
//
//    public void setGlobal(String arg0,
//                          Object arg1) {
//        // TODO Auto-generated method stub
//        
//    }

    public ReteTuple getTuple() {
        return null;
    }    

}
