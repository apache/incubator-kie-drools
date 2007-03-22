package org.drools.clp;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.HashMap;
import java.util.Map;

import org.antlr.runtime.ANTLRStringStream;
import org.antlr.runtime.CharStream;
import org.antlr.runtime.Lexer;
import org.antlr.runtime.TokenStream;
import org.drools.Person;
import org.drools.compiler.SwitchingCommonTokenStream;

import junit.framework.TestCase;

public class CompiledParserTest extends TestCase {
   private CLPParser parser;
   
    public void test1() throws Exception {
        BlockExecutionEngine engine = ( BlockExecutionEngine ) parse("(bind ?x (+ 20 11) )(modify ?p (age ?x) )").rhs();
        ExecutionContext context = new ExecutionContext(null, null, 2);
        
        Person p = new Person("mark");        
        Map vars = new HashMap();
        vars.put( "?p", p );
        engine.replaceTempTokens( vars );
        
        engine.execute( context );
        
        assertEquals( 31, p.getAge() );
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

        //        System.err.println( getClass().getResource( name ) );
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
        p.setFunctionRegistry( new FunctionRegistry( BuiltinFunctions.getInstance() )  );
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
