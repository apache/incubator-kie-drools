package org.drools.clp;

import java.io.Reader;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.antlr.runtime.ANTLRReaderStream;
import org.antlr.runtime.ANTLRStringStream;
import org.antlr.runtime.CharStream;
import org.antlr.runtime.Lexer;
import org.antlr.runtime.TokenStream;
import org.drools.RuleBase;
import org.drools.RuleBaseFactory;
import org.drools.StatefulSession;
import org.drools.WorkingMemory;
import org.drools.clp.valuehandlers.FunctionCaller;
import org.drools.compiler.PackageBuilder;
import org.drools.compiler.SwitchingCommonTokenStream;
import org.drools.lang.DRLLexer;
import org.drools.lang.descr.AttributeDescr;
import org.drools.lang.descr.PackageDescr;
import org.drools.lang.descr.RuleDescr;
import org.drools.spi.GlobalResolver;

public class Shell implements ParserHandler, GlobalResolver, BuildContext {
    private FunctionRegistry registry;
    private Map variables;
    //private Map packageBulders;
    
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
            CLPParser parser = new CLPParser( new SwitchingCommonTokenStream( new DRLLexer( new ANTLRReaderStream( reader ) ) ) );
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
        return new SwitchingCommonTokenStream( lexer );
    }

    public Object resolve(String name) {
        return this.variables.get( name );
    }

    public void addFunction(FunctionCaller function) {
        // TODO Auto-generated method stub
        
    }

    public ValueHandler createLocalVariable(String identifier) {
        // TODO Auto-generated method stub
        return null;
    }

    public FunctionRegistry getFunctionRegistry() {
        // TODO Auto-generated method stub
        return null;
    }

    public Object getProperty(Object key) {
        // TODO Auto-generated method stub
        return null;
    }

    public ValueHandler getVariableValueHandler(String identifier) {
        // TODO Auto-generated method stub
        return null;
    }

    public Object setProperty(Object key,
                              Object value) {
        // TODO Auto-generated method stub
        return null;
    }

}
