package org.drools.lang.dsl;

import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Iterator;

import junit.framework.TestCase;

public class ANTLRDSLTest extends TestCase {

	public ANTLRDSLTest(String name) {
		super(name);
	}

	protected void setUp() throws Exception {
		super.setUp();
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}
	
	public void testMe() throws Exception{
		DSLTokenizedMappingFile tokenizedFile = null;
	    final String filename = "test_antlr.dsl";
        final Reader reader = new InputStreamReader( this.getClass().getResourceAsStream( filename ) );
        tokenizedFile = new DSLTokenizedMappingFile();
        tokenizedFile.parseAndLoad( reader );
        reader.close();
        for (Iterator it = tokenizedFile.getMapping().getEntries().iterator(); it.hasNext();) {
			DSLMappingEntry entry = (DSLMappingEntry) it.next();
//			System.out.println("ENTRY: " + entry.getKeyPattern() + "   :::::   " + entry.getValuePattern());
		}
        
        DefaultExpander ex = new DefaultExpander();
        ex.addDSLMapping( tokenizedFile.getMapping() );
        
        System.err.println(ex.expand( "rule 'x' \n when \n address is present where name is \"foo\" and age is \"32\" \n then \n end" ));
	}
	
	public void testSimple() throws Exception{
		String input = "u : User() and exists (a: Address( where name is \"foo\" and age is \"32\" ) from u.addresses)";
		String pattern = "(\\W|^)where\\s+([\\S]+)\\s+is \"(.*?)\"(\\W|$)";
		java.util.regex.Pattern p = java.util.regex.Pattern.compile(pattern);
		java.util.regex.Matcher m = p.matcher(input);
		System.out.println("SIMPLE MATCHER matches: " + m.matches());
	}

}
