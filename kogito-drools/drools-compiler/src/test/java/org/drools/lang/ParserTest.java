package org.drools.lang;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Map;

import junit.framework.TestCase;

import org.drools.rule.Rule;

public class ParserTest extends TestCase {
	
	protected void setUp() throws Exception {
		super.setUp();
	}
	
	protected void tearDown() throws Exception {
		super.tearDown();
	}
	
	public void test_package() throws Exception {
		Parser parser = parser( "package.drl" );
		
		parser.parse();
		
		assertEquals( "org.drools.lang", parser.getPackageDeclaration() );
	}
	
	public void test_package_imports() throws Exception {
		Parser parser = parser( "package-imports.drl" );
		
		parser.parse();
		
		assertEquals( "org.drools.lang", parser.getPackageDeclaration() );
		assertEquals( 2, parser.getImports().size() );
		assertEquals( "java.util.List", parser.getImports().get( 0 ) );
		assertEquals( "java.util.ArrayList", parser.getImports().get( 1 ) );
	}
	
	public void test_one_rule() throws Exception {
		Parser parser = parser( "one-rule.drl" );
		
		parser.parse();
		
		assertEquals( "org.drools.lang", parser.getPackageDeclaration() );
		assertEquals( 2, parser.getImports().size() );
		assertEquals( "java.util.List", parser.getImports().get( 0 ) );
		assertEquals( "java.util.ArrayList", parser.getImports().get( 1 ) );
		
		assertEquals( 1, parser.getRules().size() );
        Rule rule = (Rule)parser.getRules().get(0);
		assertEquals( "find seating", rule.getName() );
        assertEquals( 30, rule.getSalience());
        assertEquals( true, rule.getNoLoop());
        assertEquals(null, parser.getExpander());
	}
    
    public void test_with_expander() throws Exception {
        Parser parser = parser( "cheese-rules.drl" );
        parser.parse();
        assertEquals("cheese_rules", ((Rule)parser.getRules().get(0)).getName() );
        
    }
    
    public void test_with_appdata() throws Exception  {
        Parser parser = parser ( "application-data.drl" );
        parser.parse();
        Map appData = parser.getGlobalDeclarations();
        assertEquals(2, appData.size());
        assertEquals("MyObject", appData.get("me"));
        assertEquals("YourObject", appData.get("you"));
    }
    
    public void test_with_functions() throws Exception {
        Parser parser = parser ( "functions.drl" );
        parser.parse();
        assertNotNull(parser.getFunctions());
        assertTrue(parser.getFunctions().indexOf("System.out.println(string);") > -1);
        
    }
	
	protected Parser parser(String name) {
		InputStream in = getClass().getResourceAsStream( name );
		InputStreamReader reader = new InputStreamReader( in );
		
		return new Parser( reader );
	}

}
