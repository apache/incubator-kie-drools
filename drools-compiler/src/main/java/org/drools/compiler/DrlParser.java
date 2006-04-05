package org.drools.compiler;

import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.antlr.runtime.ANTLRStringStream;
import org.antlr.runtime.CommonTokenStream;
import org.antlr.runtime.RecognitionException;
import org.drools.lang.RuleParser;
import org.drools.lang.RuleParserLexer;
import org.drools.lang.descr.PackageDescr;
import org.drools.lang.dsl.DefaultExpanderResolver;

/**
 * This is a low level parser API. This will return textual AST representations
 * of the DRL source, including with DSL expanders if appropriate.
 */
public class DrlParser {
    
    private List results = new ArrayList();
	
	public DrlParser() {
		
	}
	
    /** Parse a rule from text */
	public PackageDescr parse(String text) throws DroolsParserException {
        RuleParser parser = getParser( text );
        compile( parser );
        return parser.getPackageDescr();			
		
	}
    
    private void compile(RuleParser parser) throws DroolsParserException {
        try {
            parser.compilation_unit();
            
            makeErrorList( parser );
        } catch ( RecognitionException e ) {
            throw new DroolsParserException( e );
        }
    }

    /** Convert the antlr exceptions to drools parser exceptions */
    private void makeErrorList(RuleParser parser) {
        for ( Iterator iter = parser.getErrors().iterator(); iter.hasNext(); ) {
            RecognitionException recogErr = (RecognitionException) iter.next();
            ParserError err = new ParserError(parser.createErrorMessage( recogErr ), recogErr.line, recogErr.charPositionInLine);
            this.results.add( err );
        }
    }

    /**
     * @return An instance of a RuleParser should you need one (most folks will not).
     */
    private RuleParser getParser(String text) {
        return new RuleParser( new CommonTokenStream( new RuleParserLexer( new ANTLRStringStream( text ) ) ) );
    }

    
    
    /** Parse and build a rule package from a DRL source */
	public PackageDescr parse(Reader reader) throws IOException, DroolsParserException {
	       StringBuffer text = getDRLText( reader );
	       return parse( text.toString() );
	}
    
    /** 
     * Parse and build a rule package from a DRL source with a 
     * domain specific language.
     */
    public PackageDescr parse(Reader drl, Reader dsl) throws DroolsParserException, IOException {
        StringBuffer text = getDRLText( drl );
        return parse( text.toString(), dsl );
    }

    /**
     * Parse and build a rule package from a DRL source with a domain specific language.
     * @param source As Text.
     * @param dsl 
     * @return
     * @throws DroolsParserException
     */
    public PackageDescr parse(String source, Reader dsl) throws DroolsParserException {
        DefaultExpanderResolver resolver = new DefaultExpanderResolver(dsl);
        RuleParser parser = getParser( source );
        parser.setExpanderResolver( resolver );
        compile( parser );
        return parser.getPackageDescr();
    }

    private StringBuffer getDRLText(Reader reader) throws IOException {
        StringBuffer text = new StringBuffer();

	        char[] buf = new char[1024];
	        int len = 0;

	        while ( (len = reader.read( buf )) >= 0 ) {
	            text.append( buf,
	                         0,
	                         len );
	        }
        return text;
    }
    
    /**
     * @return true if there were parser errors.
     */
    public boolean hasErrors() {
        return this.results.size() > 0;
    }
    
    /**
     * @return a list of ParserError's.
     */
    public List getErrors() {
        return this.results;
    }
}
