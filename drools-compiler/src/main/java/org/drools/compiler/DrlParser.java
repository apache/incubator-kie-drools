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

public class DrlParser {
    
    private List results = new ArrayList();
	
	public DrlParser() {
		
	}
	
	public PackageDescr parse(String text) throws DroolsParserException {
        RuleParser parser = new RuleParser( new CommonTokenStream( new RuleParserLexer( new ANTLRStringStream( text ) ) ) );
                
        try {
            parser.compilation_unit();
        } catch ( RecognitionException e ) {
            throw new DroolsParserException( e );
        }

        return parser.getPackageDescr();			
		
	}

	public PackageDescr parse(Reader reader) throws IOException, DroolsParserException {
	       StringBuffer text = new StringBuffer();

	        char[] buf = new char[1024];
	        int len = 0;

	        while ( (len = reader.read( buf )) >= 0 ) {
	            text.append( buf,
	                         0,
	                         len );
	        }

	        return parse( text.toString() );
	}
    
    public ParserError[] getErrors() {
        return ( ParserError[] ) this.results.toArray( new ParserError[ this.results.size() ] );
    }
}
