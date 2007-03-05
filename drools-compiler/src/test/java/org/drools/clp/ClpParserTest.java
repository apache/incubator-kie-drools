package org.drools.clp;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.List;

import junit.framework.TestCase;

import org.antlr.runtime.ANTLRStringStream;
import org.antlr.runtime.CharStream;
import org.antlr.runtime.Lexer;
import org.antlr.runtime.TokenStream;
import org.drools.compiler.SwitchingCommonTokenStream;
import org.drools.lang.DRLLexer;
import org.drools.lang.DRLParser;
import org.drools.lang.descr.AndDescr;
import org.drools.lang.descr.ColumnDescr;
import org.drools.lang.descr.FieldConstraintDescr;
import org.drools.lang.descr.LiteralRestrictionDescr;
import org.drools.lang.descr.PredicateDescr;
import org.drools.lang.descr.RestrictionConnectiveDescr;
import org.drools.lang.descr.ReturnValueRestrictionDescr;
import org.drools.lang.descr.RuleDescr;
import org.drools.lang.descr.VariableRestrictionDescr;

public class ClpParserTest extends TestCase {
    
    private CLPParser parser;
    
    public void testRule() throws Exception {
        RuleDescr rule = parse("(defrule xxx ?b <- (person (name \"yyy\"&?bf|~\"zzz\"|~=(ppp)&:(ooo)) ) ?c <- (hobby (type ?bf2&~iii) (rating fivestar) )").rule();
        
        assertEquals( "xxx", rule.getName() );
        
        AndDescr lhs = rule.getLhs();
        List lhsList = lhs.getDescrs();
        assertEquals(2, lhsList.size());
        
        // Parse the first column
        ColumnDescr col = ( ColumnDescr ) lhsList.get( 0 );
        assertEquals("?b", col.getIdentifier() );
        assertEquals("person", col.getObjectType() );
        
        List colList = col.getDescrs();
        assertEquals(2, colList.size());
        FieldConstraintDescr fieldConstraintDescr = ( FieldConstraintDescr ) colList.get( 0 );
        List restrictionList = fieldConstraintDescr.getRestrictions();
        
        assertEquals("name", fieldConstraintDescr.getFieldName() );        
        // @todo the 7th one has no constraint, as its a predicate, have to figure out how to handle this
        assertEquals(8, restrictionList.size());
                
        
        LiteralRestrictionDescr litDescr = ( LiteralRestrictionDescr ) restrictionList.get( 0 );
        assertEquals("==", litDescr.getEvaluator() );
        assertEquals("yyy", litDescr.getText() );

        RestrictionConnectiveDescr connDescr = ( RestrictionConnectiveDescr ) restrictionList.get( 1 );
        assertEquals(RestrictionConnectiveDescr.AND, connDescr.getConnective() );
        
        VariableRestrictionDescr varDescr = ( VariableRestrictionDescr ) restrictionList.get( 2 );
        assertEquals("==", varDescr.getEvaluator() );
        assertEquals("?bf", varDescr.getIdentifier() );        
        
        connDescr = ( RestrictionConnectiveDescr ) restrictionList.get( 3 );
        assertEquals(RestrictionConnectiveDescr.OR, connDescr.getConnective() );
        
        litDescr = ( LiteralRestrictionDescr ) restrictionList.get( 4 );
        assertEquals("!=", litDescr.getEvaluator() );
        assertEquals("zzz", litDescr.getText() );
        
        connDescr = ( RestrictionConnectiveDescr ) restrictionList.get( 5 );
        assertEquals(RestrictionConnectiveDescr.OR, connDescr.getConnective() );
        
        ReturnValueRestrictionDescr retDescr = ( ReturnValueRestrictionDescr ) restrictionList.get( 6 );
        assertEquals("!=", retDescr.getEvaluator() );
        assertEquals("ppp", retDescr.getText() );
        
        PredicateDescr predicateDescr = ( PredicateDescr ) colList.get( 1 );
        assertEquals( "ooo", predicateDescr.getText() );
        
        
        // Parse the second column
        col = ( ColumnDescr ) lhsList.get( 1 );
        assertEquals("?c", col.getIdentifier() );
        assertEquals("hobby", col.getObjectType() );   

        colList = col.getDescrs();
        assertEquals(2, colList.size());
        fieldConstraintDescr = ( FieldConstraintDescr ) colList.get( 0 );
        restrictionList = fieldConstraintDescr.getRestrictions();
        
        assertEquals("type", fieldConstraintDescr.getFieldName() );        
        
        varDescr = ( VariableRestrictionDescr ) restrictionList.get( 0 );
        assertEquals("==", varDescr.getEvaluator() );
        assertEquals("?bf2", varDescr.getIdentifier() );  
        
        connDescr = ( RestrictionConnectiveDescr ) restrictionList.get( 1 );
        assertEquals(RestrictionConnectiveDescr.AND, connDescr.getConnective() );
        
        litDescr = ( LiteralRestrictionDescr ) restrictionList.get( 2 );
        assertEquals("!=", litDescr.getEvaluator() );
        assertEquals("iii", litDescr.getText() );
        
        fieldConstraintDescr = ( FieldConstraintDescr ) colList.get( 1 );
        restrictionList = fieldConstraintDescr.getRestrictions();        
        
        assertEquals("rating", fieldConstraintDescr.getFieldName() );
        
        litDescr = ( LiteralRestrictionDescr ) restrictionList.get( 0 );
        assertEquals("==", litDescr.getEvaluator() );
        assertEquals("fivestar", litDescr.getText() );        
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
