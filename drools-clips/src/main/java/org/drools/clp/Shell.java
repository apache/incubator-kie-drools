package org.drools.clp;

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
import org.drools.clp.valuehandlers.NamedShellVariableValue;
import org.drools.compiler.PackageBuilder;
import org.drools.lang.DRLLexer;
import org.drools.lang.descr.AttributeDescr;
import org.drools.lang.descr.PackageDescr;
import org.drools.lang.descr.RuleDescr;
import org.drools.spi.GlobalResolver;

public class  Shell implements ParserHandler, GlobalResolver, BuildContext {
	private static final long serialVersionUID = 1L;
	private FunctionRegistry registry;
    private Map variables;
    private Map              properties = Collections.EMPTY_MAP;
    
    private RuleBase ruleBase;
    private StatefulSession session;
    
    public Shell() {
        this.ruleBase = RuleBaseFactory.newRuleBase();
        this.session = ruleBase.newStatefulSession();
        this.session.setGlobalResolver( this );
        this.variables = new HashMap();
    }
    
    public void evalReader(Reader reader)  {
        try {
            CLPParser parser = new CLPParser( new CommonTokenStream( new DRLLexer( new ANTLRReaderStream( reader ) ) ) );
            evalParser( parser );    
        } catch (Exception e) {
            throw new RuntimeException( "Unable to parser Reader", e);
        }      
    }
    
    public void evalString(String text) {
        CLPParser parser =  new CLPParser( newTokenStream( newLexer( newCharStream( text ) ) ) ) ;
        evalParser( parser );
    }
    
    private void evalParser(CLPParser parser) {
        parser.setFunctionRegistry( this.registry );
        //parser.setF
    }
       
//    public void functionHandler(FunctionDescr ruleDescr) {
//        throw new RuntimeException( "Drools Clips does not support FunctionDescr" );
//    }    
    

//    public void functionHandler(FunctionCaller functionCaller) {
//        
//    }
    
    public void lispFormHandler(ExecutionEngine engine) {
        //engine.
        
    }

    public void ruleDescrHandler(RuleDescr ruleDescr) {
        String module = getModuleName( ruleDescr.getAttributes() );
        
        PackageDescr pkg = new PackageDescr(module);
        
        PackageBuilder builder = new PackageBuilder();
        builder.addPackage( pkg );     
    }  
    
    public String getModuleName(List list) {
        for ( Iterator it = list.iterator(); it.hasNext(); ) {
            AttributeDescr attr = ( AttributeDescr ) it.next();
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

    public Object resolve(String name) {
        return this.variables.get( name );
    }

    public void addFunction(FunctionCaller function) {
        //function.getValue( this );
        
    }

    public ValueHandler createLocalVariable(String identifier) {
        ValueHandler var = (ValueHandler) this.variables.get( identifier );
        if ( var == null ) {
            var = new NamedShellVariableValue( identifier );
            this.variables.put( identifier,
                                var );
        }
        return var;
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

    public ValueHandler getVariableValueHandler(String identifier) {
        return ( ValueHandler ) this.variables.get( identifier );      
    }

    public void addVariable(VariableValueHandler var) {
        this.variables.put( var.getIdentifier(), var );
    }

	public Object resolveGlobal(String identifier) {
		// TODO Auto-generated method stub
		return null;
	}

	public void setGlobal(String identifier, Object value) {
		// TODO Auto-generated method stub
		
	}

}
