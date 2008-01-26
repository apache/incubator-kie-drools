package org.drools.clp.mvel;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.io.Reader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import junit.framework.TestCase;

import org.antlr.runtime.ANTLRReaderStream;
import org.antlr.runtime.ANTLRStringStream;
import org.antlr.runtime.CharStream;
import org.antlr.runtime.CommonTokenStream;
import org.antlr.runtime.Lexer;
import org.antlr.runtime.TokenStream;
import org.drools.clp.CLPMVELLexer;
import org.drools.clp.CLPMVELParser;
import org.mvel.MVEL;

public class TestMVEL extends TestCase {
    private Map vars;
    private ByteArrayOutputStream baos;
    private MVELClipsContext context;
    
    public void setUp() {
        FunctionHandlers handlers = FunctionHandlers.getInstance();
        handlers.registerFunction( new PlusFunction() );
        handlers.registerFunction( new MultiplyFunction() );
        handlers.registerFunction( new ModifyFunction() );
        handlers.registerFunction( new CreateListFunction() );
        handlers.registerFunction( new PrintoutFunction() );
        handlers.registerFunction( new PrognFunction() );
        handlers.registerFunction( new IfFunction() );
        handlers.registerFunction( new LessThanFunction() );
        handlers.registerFunction( new MoreThanFunction() );
        handlers.registerFunction( new EqFunction() );
        handlers.registerFunction( new SwitchFunction() );
        handlers.registerFunction( new DeffunctionFunction() );    
        handlers.registerFunction( new ReturnFunction() );
        
        baos = new ByteArrayOutputStream();                
        vars = new HashMap();
        Map routers = new HashMap();
        routers.put( "t",  new PrintStream( baos ) );
        vars.put( "routers", routers );
        
        context = new MVELClipsContext();
    }

    public void test1() {
        String expr = "(* (+ 4 4 ) 2) (create$ 10 20 (+ 10 10) a) (modify ?p (name mark) (location \"london\")(age (+ 16 16) ) ) (printout t a b c (+ 4 4) )";
        
        SExpression[] lisplists = evalString( expr );

        StringBuilderAppendable appendable = new StringBuilderAppendable();        
        MVELClipsContext context = new MVELClipsContext();         
        for ( SExpression sExpression : lisplists ) {
            FunctionHandlers.dump( sExpression, appendable, context );
        }
        
        System.out.println( appendable );
    }
    
    public void testProgn() {
        String expr = "(progn (?x (create$ 10 20 30) ) (printout t ?x) ) )";
        
        SExpression[] lisplists = evalString( expr );

        StringBuilderAppendable appendable = new StringBuilderAppendable();                 
        for ( SExpression sExpression : lisplists ) {
            FunctionHandlers.dump( sExpression, appendable, context );
        }
        
        eval( appendable.toString() );        
        assertEquals( "102030", new String( baos.toByteArray() ));
    }    
    
    public void testIf() {
        String expr = "(if (< 1 3) then (printout t hello) (printout t hello) )";
        
        SExpression[] lisplists = evalString( expr );

        StringBuilderAppendable appendable = new StringBuilderAppendable();                 
        for ( SExpression sExpression : lisplists ) {
            FunctionHandlers.dump( sExpression, appendable, context );
        }
        
        eval( appendable.toString() );      
        assertEquals( "hellohello", new String( baos.toByteArray() ));        
    }
    
    public void testIfElse() {
        String expr = "(if (eq 1 3) then (printout t hello)  (printout t 1) else (printout t hello)  (printout t 2))";
        
        SExpression[] lisplists = evalString( expr );

        StringBuilderAppendable appendable = new StringBuilderAppendable();        
        for ( SExpression sExpression : lisplists ) {
            FunctionHandlers.dump( sExpression, appendable, context );
        }
        
        eval( appendable.toString() );              
        assertEquals( "hello2", new String( baos.toByteArray() ) );               
    }  
    
    public void testSwitch() throws IOException {
        String expr = "(switch (?x) (case a then (printout t hello)(printout t 1)) (case b then (printout t hello)(printout t 2)) (default (printout t hello)(printout t 3)) )";
        
        SExpression[] lisplists = evalString( expr );

        StringBuilderAppendable appendable = new StringBuilderAppendable();                 
        for ( SExpression sExpression : lisplists ) {
            FunctionHandlers.dump( sExpression, appendable, context );
        }          
        
        // check case a
        vars.put("_Q_x", "a" );        
        MVEL.eval( appendable.toString(),  vars);        
        assertEquals( "hello1", new String( baos.toByteArray() ) );
        
        // check default
        vars.put("_Q_x", "M" );        
        MVEL.eval( appendable.toString(),  vars);        
        assertEquals( "hello1hello3", new String( baos.toByteArray() ) );    
        
        // check case b
        vars.put("_Q_x", "b" );        
        eval( appendable.toString() );        
        assertEquals( "hello1hello3hello2", new String( baos.toByteArray() ) );         
    }
    
    
    
    public  void testDeffunction() {
        String function = "(deffunction max (?a ?b) (if (> ?a ?b) then (return ?a) else (return ?b) ) )";
        
        
        SExpression[] lisplists = evalString( function );
        StringBuilderAppendable appendable = new StringBuilderAppendable();        
        for ( SExpression sExpression : lisplists ) {
            FunctionHandlers.dump( sExpression, appendable, context );                       
        }                  
        eval( appendable.toString() );        
        
        String expr = "(if (eq (max 3 5) 5) then (printout t hello) )";
        lisplists = evalString( expr );
        appendable = new StringBuilderAppendable();        
        for ( SExpression sExpression : lisplists ) {
            FunctionHandlers.dump( sExpression, appendable, context );                       
        }                  
        eval( appendable.toString() );        
        
        // check case a
        vars.put("_Q_a", "10" );
        vars.put("_Q_b", "20" );
        assertEquals( "hello", new String( baos.toByteArray() ) );        
    }
    
    public void eval(String string) {
        for ( org.mvel.ast.Function function : context.getFunctions().values() ) {
            this.vars.put( function.getAbsoluteName(), function );
        }
        MVEL.eval( string,  vars);
    }

    public SExpression[] evalReader(Reader reader) {
        try {
            CLPMVELParser parser = new CLPMVELParser( new CommonTokenStream( new CLPMVELLexer( new ANTLRReaderStream( reader ) ) ) );
            return evalParser( parser );
        } catch ( Exception e ) {
            throw new RuntimeException( "Unable to parser Reader",
                                        e );
        }
    }

    public SExpression[] evalString(String text) {
        CLPMVELParser parser = new CLPMVELParser( newTokenStream( newLexer( newCharStream( text ) ) ) );
        return evalParser( parser );
    }

    private SExpression[] evalParser(CLPMVELParser parser) {
        try {
            List list = parser.eval_sExpressions();
            return (SExpression[]) list.toArray( new SExpression[list.size()] );
        } catch ( Exception e ) {
            throw new RuntimeException( e );
        }

        //parser.setF
    }

    private CharStream newCharStream(final String text) {
        return new ANTLRStringStream( text );
    }

    private CLPMVELLexer newLexer(final CharStream charStream) {
        return new CLPMVELLexer( charStream );
    }

    private TokenStream newTokenStream(final Lexer lexer) {
        return new CommonTokenStream( lexer );
    }
}
