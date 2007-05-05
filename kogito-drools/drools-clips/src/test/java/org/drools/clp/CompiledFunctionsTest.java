package org.drools.clp;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.io.Reader;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import org.antlr.runtime.ANTLRStringStream;
import org.antlr.runtime.CharStream;
import org.antlr.runtime.Lexer;
import org.antlr.runtime.TokenStream;
import org.drools.Person;
import org.drools.clp.valuehandlers.ListValueHandler;
import org.drools.clp.valuehandlers.LocalVariableValue;
import org.drools.clp.valuehandlers.LongValueHandler;
import org.drools.clp.valuehandlers.ObjectValueHandler;
import org.drools.compiler.SwitchingCommonTokenStream;

import junit.framework.TestCase;

public class CompiledFunctionsTest extends TestCase {
    private CLPParser parser;

    public void testPrintout() throws Exception {        
        BlockExecutionEngine engine = (BlockExecutionEngine) parse( "(printout d xx (eq 1 1) ?c (create$ (+ 1 1) x y) zzz)" ).rhs();
        ExecutionContext context = new ExecutionContext( null,
                                                         null,
                                                         1 );

        Map vars = new HashMap();
        vars.put( "?c",
                  new ObjectValueHandler( "brie" ) );
        engine.replaceTempTokens( vars );

        ByteArrayOutputStream bais = new ByteArrayOutputStream();                
        context.addPrintoutRouter( "d", new PrintStream(bais) );
        
        engine.execute( context );
        
        assertEquals( "xxtruebrie2xyzzz", new String( bais.toByteArray() ) );
    }
    
    public void testBindAndModify() throws Exception {
        BlockExecutionEngine engine = (BlockExecutionEngine) parse( "(bind ?x (+ 20 11) ) (modify ?p (age ?x) )" ).rhs();
        ExecutionContext context = new ExecutionContext( null,
                                                         null,
                                                         2 );

        Map vars = new HashMap();
        Person p = new Person( "mark" );
        vars.put( "?p",
                  new ObjectValueHandler( p ) );
        engine.replaceTempTokens( vars );

        engine.execute( context );

        assertEquals( 31,
                      p.getAge() );
    }

    public void testSimpleCreate$() throws Exception {
        BlockExecutionEngine engine = (BlockExecutionEngine) parse( "(bind ?x (create$ 1 2 3) )" ).rhs();
        ExecutionContext context = new ExecutionContext( null,
                                                         null,
                                                         1 );
        engine.execute( context );

        ListValueHandler list = (ListValueHandler) context.getLocalVariable( 0 );

        assertEquals( 3,
                      list.size() );

        assertEquals( 1,
                      list.getList()[0].getIntValue( context ) );
        assertEquals( 2,
                      list.getList()[1].getIntValue( context ) );
        assertEquals( 3,
                      list.getList()[2].getIntValue( context ) );
    }

    public void testNestedCreate$() throws Exception {
        BlockExecutionEngine engine = (BlockExecutionEngine) parse( "(bind ?x (create$ 1 2 (+ 1 2) ) ) (bind ?y (create$ (+ 1 0) ?x (create$ a b ?x (+ 1 1) ) 3) )" ).rhs();
        ExecutionContext context = new ExecutionContext( null,
                                                         null,
                                                         2 );
        engine.execute( context );

        // check ?x
        ListValueHandler list = (ListValueHandler) context.getLocalVariable( 0 );
        assertEquals( 3,
                      list.size() );
        assertEquals( 1,
                      list.getList()[0].getIntValue( context ) );
        assertEquals( 2,
                      list.getList()[1].getIntValue( context ) );
        assertEquals( new BigDecimal( 3 ),
                      list.getList()[2].getBigDecimalValue( context ) );

        // check ?y
        list = (ListValueHandler) context.getLocalVariable( 1 );
        assertEquals( 11,
                      list.size() );

        assertEquals( new BigDecimal( 1 ),
                      list.getList()[0].getBigDecimalValue( context ) );
        assertEquals( 1,
                      list.getList()[1].getIntValue( context ) );
        assertEquals( 2,
                      list.getList()[2].getIntValue( context ) );
        assertEquals( new BigDecimal( 3 ),
                      list.getList()[3].getBigDecimalValue( context ) );
        assertEquals( "a",
                      list.getList()[4].getStringValue( context ) );
        assertEquals( "b",
                      list.getList()[5].getStringValue( context ) );
        assertEquals( 1,
                      list.getList()[6].getIntValue( context ) );
        assertEquals( 2,
                      list.getList()[7].getIntValue( context ) );
        assertEquals( new BigDecimal( 3 ),
                      list.getList()[8].getBigDecimalValue( context ) );
        assertEquals( new BigDecimal( 2 ),
                      list.getList()[9].getBigDecimalValue( context ) );
        assertEquals( 3,
                      list.getList()[10].getIntValue( context ) );
    }

    public void testIf() throws Exception {
        BlockExecutionEngine engine = (BlockExecutionEngine) parse( "(if (< ?x ?y ) then (modify ?p (age 15) ) (printout d 15) else (modify ?p (age 5)) (printout d 5) )" ).rhs();
        ExecutionContext context = new ExecutionContext( null,
                                                         null,
                                                         2 );

        Person p = new Person( "mark" );
        Map vars = new HashMap();

        vars.put( "?x",
                  new LongValueHandler( 10 ) );
        vars.put( "?y",
                  new LocalVariableValue( "?y",
                                          0 ) );
        vars.put( "?p",
                  new ObjectValueHandler( p ) );
        engine.replaceTempTokens( vars );

        context.setLocalVariable( 0,
                                  new LongValueHandler( 20 ) );        
        ByteArrayOutputStream bais = new ByteArrayOutputStream();                
        context.addPrintoutRouter( "d", new PrintStream(bais) );        
        engine.execute( context );
        assertEquals( 15,
                      p.getAge() );
        assertEquals( "15", new String( bais.toByteArray() ) );
        
        
        context.setLocalVariable( 0,
                                  new LongValueHandler( 7 ) );
        bais = new ByteArrayOutputStream();                
        context.addPrintoutRouter( "d", new PrintStream(bais) );          
        engine.execute( context );
        assertEquals( 5,
                      p.getAge() );
        assertEquals( "5", new String( bais.toByteArray() ) );
    }

    public void testWhile() throws Exception {               
        BlockExecutionEngine engine = (BlockExecutionEngine) parse( "(while (< ?x ?y) do (bind ?x (+ ?x 1)) (printout d ?x \" \") )" ).rhs();
        ExecutionContext context = new ExecutionContext( null,
                                                         null,
                                                         2 );

        Map vars = new HashMap();

        vars.put( "?x",
                  new LocalVariableValue( "?x",
                                          0 ) );
        vars.put( "?y",
                  new LocalVariableValue( "?y",
                                          1 ) );
        engine.replaceTempTokens( vars );

        context.setLocalVariable( 0,
                                  new LongValueHandler( 0 ) );
        context.setLocalVariable( 1,
                                  new LongValueHandler( 10 ) );
        
        ByteArrayOutputStream bais = new ByteArrayOutputStream();              
        context.addPrintoutRouter( "d", new PrintStream(bais) );                                

        engine.execute( context );
        assertEquals( new BigDecimal( 10 ),
                      context.getLocalVariable( 0 ).getBigDecimalValue( context ) );
        
        assertEquals( "1 2 3 4 5 6 7 8 9 10 ", new String( bais.toByteArray() ) );        
    }

    public void testForeach() throws Exception {
        BlockExecutionEngine engine = (BlockExecutionEngine) parse( "(bind ?x 0) (foreach ?e (create$ 1 2 3) (bind ?x (+ ?x ?e) ) (printout d ?x \" \") )" ).rhs();
        ExecutionContext context = new ExecutionContext( null,
                                                         null,
                                                         2 );
        
        ByteArrayOutputStream bais = new ByteArrayOutputStream();              
        context.addPrintoutRouter( "d", new PrintStream(bais) );         
        
        engine.execute( context );
        assertEquals( new BigDecimal( 6 ),
                      context.getLocalVariable( 0 ).getBigDecimalValue( context ) );
        
        assertEquals( "1 3 6 ", new String( bais.toByteArray() ) );          
    }
    
    public void testSwitch() throws Exception {
        BlockExecutionEngine engine = (BlockExecutionEngine) parse( "(bind ?cheese ?var) (switch ?cheese (case stilton then (bind ?x ?cheese ) (break) ) (case cheddar then (bind ?x ?cheese ) (break) ) (default (bind ?x \"default\" ) ) )" ).rhs();
        ExecutionContext context = new ExecutionContext( null,
                                                         null,
                                                         3 );

        Map vars = new HashMap();

        vars.put( "?var",
                  new LocalVariableValue( "?var",
                                          2 ) );        
        engine.replaceTempTokens( vars );

        // try it with stilton
        context.setLocalVariable( 2,
                                  new ObjectValueHandler( "stilton" ) );
        engine.execute( context );        
        assertEquals( "stilton" ,
                      context.getLocalVariable( 1 ).getObject( context ) );

        // try it with cheddar        
        context.setLocalVariable( 2,
                                  new ObjectValueHandler( "cheddar" ) );
        engine.execute( context );        
        assertEquals( "cheddar" ,
                      context.getLocalVariable( 1 ).getObject( context ) );        
        
        // try it with a brie, which has no matching case        
        context.setLocalVariable( 2,
                                  new ObjectValueHandler( "brie" ) );
        engine.execute( context );        
        assertEquals( "default" ,
                      context.getLocalVariable( 1 ).getObject( context ) );                          
    }    
    
    public void testProgn() throws Exception {
        BlockExecutionEngine engine = (BlockExecutionEngine) parse( "(bind ?n 2) (while (progn (bind ?n (* ?n ?n)) (< ?n 1000)) do (printout d ?n) )" ).rhs();
        ExecutionContext context = new ExecutionContext( null,
                                                         null,
                                                         2 );
        
        ByteArrayOutputStream bais = new ByteArrayOutputStream();              
        context.addPrintoutRouter( "d", new PrintStream(bais) );                                

        engine.execute( context );
        
        assertEquals( "416256", new String( bais.toByteArray() ) );          
    }
    
    public void testFactorial() throws Exception {
        BlockExecutionEngine engine = (BlockExecutionEngine) parse( "(deffunction factorial (?n) (if (>= ?n 1) then (* ?n (factorial (- ?n 1))) else 1))" ).rhs();
        //engine.ex
    }

    private CLPParser parse(final String text) throws Exception {
        this.parser = newParser( newTokenStream( newLexer( newCharStream( text ) ) ) );
        return this.parser;
    }

    private CLPParser parse(final String source,
                            final String text) throws Exception {
        this.parser = newParser( newTokenStream( newLexer( newCharStream( text ) ) ) );
        this.parser.setSource( source );
        return this.parser;
    }

    private Reader getReader(final String name) throws Exception {
        final InputStream in = getClass().getResourceAsStream( name );

        return new InputStreamReader( in );
    }

    private CLPParser parseResource(final String name) throws Exception {
        Reader reader = getReader( name );

        final StringBuffer text = new StringBuffer();

        final char[] buf = new char[1024];
        int len = 0;

        while ( (len = reader.read( buf )) >= 0 ) {
            text.append( buf,
                         0,
                         len );
        }

        return parse( name,
                      text.toString() );
    }

    private CharStream newCharStream(final String text) {
        return new ANTLRStringStream( text );
    }

    private CLPLexer newLexer(final CharStream charStream) {
        return new CLPLexer( charStream );
    }

    private TokenStream newTokenStream(final Lexer lexer) {
        return new SwitchingCommonTokenStream( lexer );
    }

    private CLPParser newParser(final TokenStream tokenStream) {
        final CLPParser p = new CLPParser( tokenStream );
        p.setFunctionRegistry( new FunctionRegistry( BuiltinFunctions.getInstance() ) );
        //p.setParserDebug( true );
        return p;
    }

    private void assertEqualsIgnoreWhitespace(final String expected,
                                              final String actual) {
        final String cleanExpected = expected.replaceAll( "\\s+",
                                                          "" );
        final String cleanActual = actual.replaceAll( "\\s+",
                                                      "" );

        assertEquals( cleanExpected,
                      cleanActual );
    }
}
