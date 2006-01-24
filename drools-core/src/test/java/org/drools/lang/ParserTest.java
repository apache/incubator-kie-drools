package org.drools.lang;

import java.io.InputStream;
import java.io.InputStreamReader;

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
		assertEquals( "find_seating", ((Rule)parser.getRules().get(0)).getName() );
        
        assertEquals(null, parser.getExpanderName());
	}
	
	protected Parser parser(String name) {
		InputStream in = getClass().getResourceAsStream( name );
		InputStreamReader reader = new InputStreamReader( in );
		
		return new Parser( reader );
	}

}
