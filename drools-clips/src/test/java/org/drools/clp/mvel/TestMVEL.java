package org.drools.clp.mvel;

import java.io.Reader;
import java.util.List;

import junit.framework.TestCase;

import org.antlr.runtime.ANTLRReaderStream;
import org.antlr.runtime.ANTLRStringStream;
import org.antlr.runtime.CharStream;
import org.antlr.runtime.CommonTokenStream;
import org.antlr.runtime.Lexer;
import org.antlr.runtime.TokenStream;
import org.drools.clp.CLPMVELLexer;
import org.drools.clp.CLPMVELParser;

public class TestMVEL extends TestCase {
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
    }

    public void test1() {
        String expr = "(* (+ 4 4 ) 2) (create$ 10 20 (+ 10 10) a) (modify ?p (name mark) (location \"london\")(age (+ 16 16) ) ) (printout t a b c (+ 4 4) )";
        
        SExpression[] lisplists = evalString( expr );

        StringBuilderAppendable appendable = new StringBuilderAppendable();
        
        for ( SExpression sExpression : lisplists ) {
            FunctionHandlers.getInstance().dump( sExpression, appendable );
        }
        
        System.out.println( appendable );
    }
    
    public void testProgn() {
        String expr = "(progn (?x (create$ 10 20 30) ) (printout t ?x) ) )";
        
        SExpression[] lisplists = evalString( expr );

        StringBuilderAppendable appendable = new StringBuilderAppendable();
        
        for ( SExpression sExpression : lisplists ) {
            FunctionHandlers.getInstance().dump( sExpression, appendable );
        }
        
        System.out.println( appendable );
    }    
    
    public void testIf() {
        String expr = "(if (< 1 3) then (printout t x) )";
        
        SExpression[] lisplists = evalString( expr );

        StringBuilderAppendable appendable = new StringBuilderAppendable();
        
        for ( SExpression sExpression : lisplists ) {
            FunctionHandlers.getInstance().dump( sExpression, appendable );
        }
        
        System.out.println( appendable );        
    }
    
    public void testIfElse() {
        String expr = "(if (eq 1 3) then (printout t x) else (printout t y) )";
        
        SExpression[] lisplists = evalString( expr );

        StringBuilderAppendable appendable = new StringBuilderAppendable();
        
        for ( SExpression sExpression : lisplists ) {
            FunctionHandlers.getInstance().dump( sExpression, appendable );
        }
        
        System.out.println( appendable );         
    }  
    
    public void testSwitch() {
        String expr = "(switch (?x) (case a then (printout t a)) (case b then (printout t b)) (default (printout t b)) )";
        
        SExpression[] lisplists = evalString( expr );

        StringBuilderAppendable appendable = new StringBuilderAppendable();
        
        for ( SExpression sExpression : lisplists ) {
            FunctionHandlers.getInstance().dump( sExpression, appendable );
        }
        
        System.out.println( appendable );           
    }
    
    
    
    public  void test3() {
        String function = "(deffunction max (?a ?b) (return ?b) )";
        String expr = "(max (3 5) )";
        
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
