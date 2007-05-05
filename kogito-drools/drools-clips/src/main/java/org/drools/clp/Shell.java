package org.drools.clp;

import java.io.Reader;
import java.util.Iterator;
import java.util.List;

import org.antlr.runtime.ANTLRReaderStream;
import org.antlr.runtime.ANTLRStringStream;
import org.antlr.runtime.CharStream;
import org.antlr.runtime.Lexer;
import org.antlr.runtime.TokenStream;
import org.drools.RuleBase;
import org.drools.RuleBaseFactory;
import org.drools.WorkingMemory;
import org.drools.compiler.PackageBuilder;
import org.drools.compiler.SwitchingCommonTokenStream;
import org.drools.lang.DRLLexer;
import org.drools.lang.descr.AttributeDescr;
import org.drools.lang.descr.PackageDescr;
import org.drools.lang.descr.RuleDescr;

public class Shell implements ParserHandler{
    private FunctionRegistry registry;
    //private Map packageBulders;
    
    private RuleBase ruleBase;
    private WorkingMemory workingMemory;
    
    public Shell() {
        this.ruleBase = RuleBaseFactory.newRuleBase();
        this.workingMemory = ruleBase.newWorkingMemory();
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

    public void lispFormHandler(LispForm lispForm) {
        // TODO Auto-generated method stub
        
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

}
