package org.drools.compiler;

import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

import org.antlr.runtime.ANTLRStringStream;
import org.antlr.runtime.CommonTokenStream;
import org.antlr.runtime.RecognitionException;
import org.drools.lang.RuleParser;
import org.drools.lang.RuleParserLexer;
import org.drools.lang.descr.PackageDescr;
import org.drools.lang.dsl.DefaultExpanderResolver;

public class DrlParser {
    
    private List results = new ArrayList();
	
	public DrlParser() {
		
	}
	
	public PackageDescr parse(String text) throws DroolsParserException {
        RuleParser parser = getParser( text );
        compile( parser );
        return parser.getPackageDescr();			
		
	}

    private void compile(RuleParser parser) throws DroolsParserException {
        try {
            parser.compilation_unit();
        } catch ( RecognitionException e ) {
            throw new DroolsParserException( e );
        }
    }

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
        DefaultExpanderResolver resolver = new DefaultExpanderResolver(dsl);
        RuleParser parser = getParser( text.toString() );
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
    
    public ParserError[] getErrors() {
        return ( ParserError[] ) this.results.toArray( new ParserError[ this.results.size() ] );
    }
}
