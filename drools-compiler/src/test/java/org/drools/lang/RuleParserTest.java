package org.drools.lang;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

import junit.framework.TestCase;

import org.antlr.runtime.ANTLRStringStream;
import org.antlr.runtime.CharStream;
import org.antlr.runtime.CommonTokenStream;
import org.antlr.runtime.Lexer;
import org.antlr.runtime.TokenStream;
import org.drools.lang.descr.AndDescr;
import org.drools.lang.descr.BoundVariableDescr;
import org.drools.lang.descr.ColumnDescr;
import org.drools.lang.descr.EvalDescr;
import org.drools.lang.descr.FieldBindingDescr;
import org.drools.lang.descr.FunctionDescr;
import org.drools.lang.descr.LiteralDescr;
import org.drools.lang.descr.NotDescr;
import org.drools.lang.descr.OrDescr;
import org.drools.lang.descr.PackageDescr;
import org.drools.lang.descr.PredicateDescr;
import org.drools.lang.descr.QueryDescr;
import org.drools.lang.descr.ReturnValueDescr;
import org.drools.lang.descr.RuleDescr;

public class RuleParserTest extends TestCase {
	
	private RuleParser parser;
	
	protected void setUp() throws Exception {
		super.setUp();
	}
	
	protected void tearDown() throws Exception {
		super.tearDown();
	}
	
	public void testPackage_OneSegment() throws Exception {
		String packageName = parse( "package foo" ).package_statement();
		assertEquals( "foo", packageName );
	}
	
	public void testPackage_MultipleSegments() throws Exception {
		String packageName = parse( "package foo.bar.baz;" ).package_statement();
		assertEquals( "foo.bar.baz", packageName );
	}
	
	public void testProlog() throws Exception {
		parse( "package foo; import com.foo.Bar; import com.foo.Baz;" ).prolog();
		assertEquals( "foo", parser.getPackageDescr().getName() );
		assertEquals( 2, parser.getPackageDescr().getImports().size() );
		assertEquals( "com.foo.Bar", parser.getPackageDescr().getImports().get( 0 ) );
		assertEquals( "com.foo.Baz", parser.getPackageDescr().getImports().get( 1 ) );
	}
	
	public void testEmptyRule() throws Exception {
		RuleDescr rule = parseResource( "empty_rule.drl" ).rule();
		
		assertNotNull( rule );
		
		assertEquals( "empty", rule.getName() );
		assertNull( rule.getLhs() );
		assertNull( rule.getConsequence() );
	}
	
	public void testAlmostEmptyRule() throws Exception {
		RuleDescr rule = parseResource( "almost_empty_rule.drl" ).rule();
		
		assertNotNull( rule );
		
		assertEquals( "almost_empty", rule.getName() );
		assertNotNull( rule.getLhs() );
		assertEquals( "", rule.getConsequence().trim() );
	}
	
	public void testQuotedStringNameRule() throws Exception {
		RuleDescr rule = parseResource( "quoted_string_name_rule.drl" ).rule();
		
		assertNotNull( rule );
		
		assertEquals( "quoted string name", rule.getName() );
		assertNotNull( rule.getLhs() );
		assertEquals( "", rule.getConsequence().trim() );
	}
	
	public void testChunkWithoutParens() throws Exception {
		String chunk = parse( "foo" ).paren_chunk();
		
		assertEquals( "foo", chunk );
	}
	
	public void testChunkWithParens() throws Exception {
		String chunk = parse( "fnord()" ).paren_chunk();
		
		assertEqualsIgnoreWhitespace( "fnord()", chunk );
	}
	
	public void testChunkWithParensAndQuotedString() throws Exception {
		String chunk = parse( "fnord(\"cheese\")" ).paren_chunk();
		
		assertEqualsIgnoreWhitespace( "fnord(\"cheese\")", chunk );
	}
	
	public void testChunkWithRandomCharac5ters() throws Exception {
		String chunk = parse( "%*9dkj" ).paren_chunk();
		
		assertEqualsIgnoreWhitespace( "%*9dkj", chunk );
	}
	
	public void testSimpleRule() throws Exception {
		RuleDescr rule = parseResource( "simple_rule.drl" ).rule();
		
		assertNotNull( rule );
		
		assertEquals( "simple_rule", rule.getName() );
		
		AndDescr lhs = rule.getLhs();
		
		assertNotNull( lhs );
		
		assertEquals( 3, lhs.getDescrs().size() );
		
		System.err.println( lhs.getDescrs() );
		
        // Check first column
		ColumnDescr first = (ColumnDescr) lhs.getDescrs().get( 0 );		
		assertEquals( "foo3", first.getIdentifier() );
		assertEquals( "Bar", first.getObjectType() );
		
		assertEquals( 1, first.getDescrs().size() );
		
		LiteralDescr constraint = (LiteralDescr) first.getDescrs().get( 0 );
		
		assertNotNull( constraint );
		
		assertEquals( "a", constraint.getFieldName() );
		assertEquals( "==", constraint.getEvaluator() );
		assertEquals( "3", constraint.getText() );
		
        // Check second column
        ColumnDescr second = (ColumnDescr) lhs.getDescrs().get( 1 );     
        assertEquals( "foo4", second.getIdentifier() );
        assertEquals( "Bar", second.getObjectType() );
        
        assertEquals( 2, second.getDescrs().size() );
        
        System.err.println( second.getDescrs() );
        
        FieldBindingDescr fieldBindingDescr = (FieldBindingDescr) second.getDescrs().get( 0 );
        assertEquals( "a", fieldBindingDescr.getFieldName() );
        assertEquals( "a4", fieldBindingDescr.getIdentifier() );
        
        constraint = (LiteralDescr) second.getDescrs().get( 1 );
        
        assertNotNull( constraint );
        
        assertEquals( "a", constraint.getFieldName() );
        assertEquals( "==", constraint.getEvaluator() );
        assertEquals( "4", constraint.getText() );
                
                
        // Check third column
        ColumnDescr third = (ColumnDescr) lhs.getDescrs().get( 2 );
		assertNull( third.getIdentifier() );
		assertEquals( "Baz", third.getObjectType() );
		
		assertEqualsIgnoreWhitespace( 
				"if ( a == b ) { " +
				"  assert( foo3 );" +
				"} else {" +
				"  retract( foo4 );" +
				"}" +
                "  System.out.println( a4 );", 
				rule.getConsequence() );
	}
    
    public void testSimpleQuery() throws Exception {
        QueryDescr query = parseResource( "simple_query.drl" ).query();
        
        assertNotNull( query );
        
        assertEquals( "simple_query", query.getName() );
        
        AndDescr lhs = query.getLhs();
        
        assertNotNull( lhs );
        
        assertEquals( 3, lhs.getDescrs().size() );
        
        System.err.println( lhs.getDescrs() );
        
        // Check first column
        ColumnDescr first = (ColumnDescr) lhs.getDescrs().get( 0 );     
        assertEquals( "foo3", first.getIdentifier() );
        assertEquals( "Bar", first.getObjectType() );
        
        assertEquals( 1, first.getDescrs().size() );
        
        LiteralDescr constraint = (LiteralDescr) first.getDescrs().get( 0 );
        
        assertNotNull( constraint );
        
        assertEquals( "a", constraint.getFieldName() );
        assertEquals( "==", constraint.getEvaluator() );
        assertEquals( "3", constraint.getText() );
        
        // Check second column
        ColumnDescr second = (ColumnDescr) lhs.getDescrs().get( 1 );     
        assertEquals( "foo4", second.getIdentifier() );
        assertEquals( "Bar", second.getObjectType() );
        
        assertEquals( 2, second.getDescrs().size() );
        //check it has field bindings.
        FieldBindingDescr fieldBindingDescr = (FieldBindingDescr) second.getDescrs().get( 0 );
        assertEquals( "a", fieldBindingDescr.getFieldName() );
        assertEquals( "a4", fieldBindingDescr.getIdentifier() );
        
        constraint = (LiteralDescr) second.getDescrs().get( 1 );
        
        assertNotNull( constraint );
        
        assertEquals( "a", constraint.getFieldName() );
        assertEquals( "==", constraint.getEvaluator() );
        assertEquals( "4", constraint.getText() );        
        
    }

    
    public void testQueryRuleMixed() throws Exception {
        RuleParser parser = parseResource( "query_and_rule.drl" );
        parser.compilation_unit();
        
        PackageDescr pack = parser.getPackageDescr();
        assertEquals(4, pack.getRules().size()); //as queries are rules
        RuleDescr rule = (RuleDescr) pack.getRules().get( 0 );
        assertEquals( "bar", rule.getName() );

        QueryDescr query = (QueryDescr) pack.getRules().get( 1 );
        assertEquals( "simple_query", query.getName() );
        
        rule = (RuleDescr) pack.getRules().get( 2 );
        assertEquals( "bar2", rule.getName() );
        
        query = (QueryDescr) pack.getRules().get( 3 );
        assertEquals( "simple_query2", query.getName() );
        
        
    }
    
    public void testMultipleRules() throws Exception {
        RuleParser parser = parseResource( "multiple_rules.drl" );
        parser.compilation_unit();
        
        PackageDescr pkg = parser.getPackageDescr();
        List rules = pkg.getRules();
        
        assertEquals( 2, rules.size() );
        
        RuleDescr rule0  = (RuleDescr) rules.get( 0 );
        assertEquals( "Like Stilton", rule0.getName() );
        
        RuleDescr rule1  = (RuleDescr) rules.get( 1 );
        assertEquals( "Like Cheddar", rule1.getName() );

        //checkout the first rule
        AndDescr lhs = rule1.getLhs();
        assertNotNull( lhs );        
        assertEquals( 1, lhs.getDescrs().size() );
        assertEqualsIgnoreWhitespace( "System.out.println(\"I like \" + t);", rule0.getConsequence());
        
        // Check first column
        ColumnDescr first = (ColumnDescr) lhs.getDescrs().get( 0 );     
        assertEquals( "Cheese", first.getObjectType() );   
        
        
        
        //checkout the second rule
        lhs = rule1.getLhs();
        assertNotNull( lhs );        
        assertEquals( 1, lhs.getDescrs().size() );
        assertEqualsIgnoreWhitespace( "System.out.println(\"I like \" + t);", rule1.getConsequence()); 
        
        // Check first column
        first = (ColumnDescr) lhs.getDescrs().get( 0 );     
        assertEquals( "Cheese", first.getObjectType() );         
        
    }
    
    public void testSimpleExpander() throws Exception {
        RuleParser parser = parseResource( "simple_expander.drl" );
        MockExpanderResolver mockExpanderResolver = new MockExpanderResolver();
        parser.setExpanderResolver( mockExpanderResolver );
        parser.compilation_unit();
        PackageDescr pack = parser.getPackageDescr();
        assertNotNull(pack);
        assertEquals(1, pack.getRules().size());
        
        assertTrue(mockExpanderResolver.checkCalled( "foo.dsl"));
        RuleDescr rule = (RuleDescr) pack.getRules().get( 0 );
        assertEquals("simple_rule", rule.getName());
        
        
        //now check out the LHS
        assertEquals(4, rule.getLhs().getDescrs().size());
        
        //The rain in spain ... ----> foo : Bar(a==3) (via MockExpander)
        ColumnDescr col = (ColumnDescr) rule.getLhs().getDescrs().get( 0 );
        assertEquals( "Bar", col.getObjectType() );
        assertEquals( "foo", col.getIdentifier() );
        assertEquals(1, col.getDescrs().size());
        LiteralDescr lit = (LiteralDescr) col.getDescrs().get( 0 );
        assertEquals("==", lit.getEvaluator());
        assertEquals("a", lit.getFieldName());
        assertEquals("3", lit.getText());
        
        //>Baz() --> not expanded, as it has the magical escape character '>' !!
        col = (ColumnDescr) rule.getLhs().getDescrs().get( 1 );
        assertEquals("Baz", col.getObjectType());
        
        //The rain in spain ... ----> foo : Bar(a==3) (via MockExpander), again...
        col = (ColumnDescr) rule.getLhs().getDescrs().get( 2 );
        assertEquals( "Bar", col.getObjectType() );
        assertEquals( "foo", col.getIdentifier() );
        assertEquals(1, col.getDescrs().size());
        lit = (LiteralDescr) col.getDescrs().get( 0 );
        assertEquals("==", lit.getEvaluator());
        assertEquals("a", lit.getFieldName());
        assertEquals("3", lit.getText());        

        //>Bar() --> not expanded, as it has the magical escape character '>' !!
        col = (ColumnDescr) rule.getLhs().getDescrs().get( 3 );
        assertEquals("Bar", col.getObjectType());
        
        
    }
    
    public void testBoundVariables() throws Exception {
        RuleParser parser = parseResource( "bindings.drl" );
        parser.compilation_unit();
        
        PackageDescr pkg = parser.getPackageDescr();
        RuleDescr ruleDescr = ( RuleDescr ) pkg.getRules().get( 0 );
        
        AndDescr lhs = ruleDescr.getLhs();
        assertEquals(2, lhs.getDescrs().size() );
        ColumnDescr cheese = (ColumnDescr) lhs.getDescrs().get( 0 );
        assertEquals("Cheese", cheese.getObjectType() );
        assertEquals(2, lhs.getDescrs().size() );
        FieldBindingDescr fieldBinding = ( FieldBindingDescr) cheese.getDescrs().get( 0 );
        assertEquals( "type", fieldBinding.getFieldName());        
        LiteralDescr literalDescr = ( LiteralDescr) cheese.getDescrs().get( 1 );
        assertEquals( "type", literalDescr.getFieldName() );
        assertEquals( "==", literalDescr.getEvaluator() );
        assertEquals( "stilton", literalDescr.getText() );
        
        ColumnDescr person = (ColumnDescr) lhs.getDescrs().get( 1 );
        fieldBinding = ( FieldBindingDescr) person.getDescrs().get( 0 );
        assertEquals( "name", fieldBinding.getFieldName());        
        literalDescr = ( LiteralDescr) person.getDescrs().get( 1 );
        assertEquals( "name", literalDescr.getFieldName() );
        assertEquals( "==", literalDescr.getEvaluator() );
        assertEquals( "bob", literalDescr.getText() );        
        
        System.err.println( person.getDescrs() );
        
        BoundVariableDescr variableDescr = ( BoundVariableDescr ) person.getDescrs().get( 2 );
        assertEquals( "likes", variableDescr.getFieldName() );
        assertEquals( "==", variableDescr.getEvaluator() );
        assertEquals( "$type", variableDescr.getIdentifier() );                   
    }
    
    /** Test that explicit "&&", "||" works as expected */
    public void testAndOrRules() throws Exception {
        RuleParser parser = parseResource( "and_or_rule.drl");
        parser.compilation_unit();
        
        PackageDescr pack = parser.getPackageDescr();
        assertNotNull(pack);
        assertEquals(1, pack.getRules().size());
        RuleDescr rule = (RuleDescr) pack.getRules().get( 0 );
        assertEquals( "simple_rule", rule.getName() );
        
        //we will have 2 children under the main And node
        AndDescr and = rule.getLhs();
        assertEquals(2, and.getDescrs().size());
        
        //check the "&&" part
        AndDescr join = (AndDescr) and.getDescrs().get( 0 );
        assertEquals(2, join.getDescrs().size());
        
        ColumnDescr left = (ColumnDescr) join.getDescrs().get( 0 );
        ColumnDescr right = (ColumnDescr) join.getDescrs().get( 1 );
        assertEquals("Person", left.getObjectType());
        assertEquals("Cheese", right.getObjectType());
       
        assertEquals(1, left.getDescrs().size());
        LiteralDescr literal = (LiteralDescr) left.getDescrs().get( 0 );
        assertEquals( "==", literal.getEvaluator());
        assertEquals( "name", literal.getFieldName() );
        assertEquals( "mark", literal.getText() );

        assertEquals(1, right.getDescrs().size());
        literal = (LiteralDescr) right.getDescrs().get( 0 );
        assertEquals( "==", literal.getEvaluator());
        assertEquals( "type", literal.getFieldName() );
        assertEquals( "stilton", literal.getText() );
        
        //now the "||" part
        OrDescr or = (OrDescr) and.getDescrs().get( 1 );
        assertEquals(2, or.getDescrs().size());
        left = (ColumnDescr) or.getDescrs().get( 0 );
        right = (ColumnDescr) or.getDescrs().get( 1 );
        assertEquals("Person", left.getObjectType());
        assertEquals("Cheese", right.getObjectType());        
        assertEquals(1, left.getDescrs().size());
        literal = (LiteralDescr) left.getDescrs().get( 0 );
        assertEquals( "==", literal.getEvaluator());
        assertEquals( "name", literal.getFieldName() );
        assertEquals( "mark", literal.getText() );

        assertEquals(1, right.getDescrs().size());
        literal = (LiteralDescr) right.getDescrs().get( 0 );
        assertEquals( "==", literal.getEvaluator());
        assertEquals( "type", literal.getFieldName() );
        assertEquals( "stilton", literal.getText() );        
        
        assertEqualsIgnoreWhitespace( "System.out.println( \"Mark and Michael\" );", rule.getConsequence());
        
    }
    
    /** test basic foo : Fact() || Fact() stuff */
    public void testOrWithBinding() throws Exception {
        RuleParser parser = parseResource( "or_binding.drl" );
        parser.compilation_unit();
        
        PackageDescr pack = parser.getPackageDescr();
        assertEquals(1, pack.getRules().size());
        RuleDescr rule = (RuleDescr) pack.getRules().get( 0 );
        assertEquals(1, rule.getLhs().getDescrs().size());
        
        OrDescr or = (OrDescr) rule.getLhs().getDescrs().get( 0 );
        assertEquals(2, or.getDescrs().size());
        
        ColumnDescr leftCol = (ColumnDescr) or.getDescrs().get( 0 );
        assertEquals("Person", leftCol.getObjectType());
        assertEquals("foo", leftCol.getIdentifier());
        
        AndDescr right = (AndDescr) or.getDescrs().get( 1 );
        assertEquals(2, right.getDescrs().size());
        ColumnDescr secondFact = (ColumnDescr) right.getDescrs().get( 0 );
        assertEquals("Person", secondFact.getObjectType());
        assertEquals( "foo", secondFact.getIdentifier() );
        
        ColumnDescr thirdFact = (ColumnDescr) right.getDescrs().get( 1 );
        assertEquals("Cheese", thirdFact.getObjectType());
        
        assertEqualsIgnoreWhitespace( "System.out.println( \"Mark and Michael\" + bar );", rule.getConsequence());
    }
    
    /** test basic foo : Fact() || Fact() stuff binding to an "or"*/
    public void testOrBindingComplex() throws Exception {
        RuleParser parser = parseResource( "or_binding_complex.drl" );
        parser.compilation_unit();
        
        PackageDescr pack = parser.getPackageDescr();
        assertEquals(1, pack.getRules().size());
        RuleDescr rule = (RuleDescr) pack.getRules().get( 0 );
        assertEquals(1, rule.getLhs().getDescrs().size());
        
        assertEquals(1, rule.getLhs().getDescrs().size());

        OrDescr or = (OrDescr) rule.getLhs().getDescrs().get( 0 );
        assertEquals( 2, or.getDescrs().size() );
        
        //first fact
        ColumnDescr firstFact = (ColumnDescr) or.getDescrs().get( 0 );
        assertEquals("Person", firstFact.getObjectType());
        assertEquals("foo", firstFact.getIdentifier());
        
        //second "option"
        ColumnDescr secondFact = (ColumnDescr) or.getDescrs().get( 0 );
        assertEquals("Person", secondFact.getObjectType());
        assertEquals( "foo", secondFact.getIdentifier() );
        
        assertEqualsIgnoreWhitespace( "System.out.println( \"Mark and Michael\" + bar );", rule.getConsequence());
    }   
    
    /** */
    public void testBracketsPrecedence() throws Exception {
        RuleParser parser = parseResource( "brackets_precedence.drl" );
        parser.compilation_unit();
        
        PackageDescr pack = parser.getPackageDescr();
        assertEquals(1, pack.getRules().size());
        RuleDescr rule = (RuleDescr) pack.getRules().get( 0 );
        
        assertEquals(1, rule.getLhs().getDescrs().size());
        
        AndDescr rootAnd = (AndDescr) rule.getLhs().getDescrs().get( 0 );
        
        assertEquals( 2, rootAnd.getDescrs().size() );
        
        OrDescr leftOr = (OrDescr) rootAnd.getDescrs().get( 0 );
        
        assertEquals(2, leftOr.getDescrs().size());
        NotDescr not = (NotDescr) leftOr.getDescrs().get( 0 );
        ColumnDescr foo1 = (ColumnDescr) not.getDescrs().get( 0 );
        assertEquals("Foo", foo1.getObjectType());
        ColumnDescr foo2 = (ColumnDescr) leftOr.getDescrs().get( 1 );
        assertEquals("Foo", foo2.getObjectType());
        
        OrDescr rightOr = (OrDescr) rootAnd.getDescrs().get( 1 );
        
        assertEquals(2, rightOr.getDescrs().size());
        ColumnDescr shoes = (ColumnDescr) rightOr.getDescrs().get( 0 );
        assertEquals("Shoes", shoes.getObjectType());
        ColumnDescr butt = (ColumnDescr) rightOr.getDescrs().get( 1 );
        assertEquals("Butt", butt.getObjectType());
        

    }     
    
    public void testWithEval() throws Exception {
        RuleParser parser = parseResource( "with_eval.drl" );
        parser.compilation_unit();
        
        PackageDescr pack = parser.getPackageDescr();
        assertEquals(1, pack.getRules().size());
        RuleDescr rule = (RuleDescr) pack.getRules().get( 0 );
        assertEquals(3, rule.getLhs().getDescrs().size());
        EvalDescr eval = (EvalDescr) rule.getLhs().getDescrs().get( 2 );
        assertEqualsIgnoreWhitespace( "abc(\"foo\");", eval.getText());
    }
    
    public void testWithRetval() throws Exception {
        RuleParser parser = parseResource( "with_retval.drl" );
        parser.compilation_unit();
        
        PackageDescr pack = parser.getPackageDescr();
        assertEquals(1, pack.getRules().size());
        
        RuleDescr rule = (RuleDescr) pack.getRules().get( 0 );
        assertEquals(1, rule.getLhs().getDescrs().size());
        ColumnDescr col = (ColumnDescr) rule.getLhs().getDescrs().get( 0 );
        assertEquals(1, col.getDescrs().size());
        assertEquals("Foo", col.getObjectType());
        ReturnValueDescr retval = (ReturnValueDescr) col.getDescrs().get( 0 );
        assertEquals("a + b", retval.getText());
        assertEquals( "name", retval.getFieldName());
        assertEquals("==", retval.getEvaluator());
    }    
    
    public void testWithPredicate() throws Exception {
        RuleParser parser = parseResource( "with_predicate.drl" );
        parser.compilation_unit();
        
        PackageDescr pack = parser.getPackageDescr();
        assertEquals(1, pack.getRules().size());
        
        RuleDescr rule = (RuleDescr) pack.getRules().get( 0 );
        assertEquals(1, rule.getLhs().getDescrs().size());
        ColumnDescr col = (ColumnDescr) rule.getLhs().getDescrs().get( 0 );
        assertEquals(1, col.getDescrs().size());
        
        PredicateDescr pred = (PredicateDescr) col.getDescrs().get( 0 );
        assertEquals("age", pred.getFieldName());
        assertEquals("$age2", pred.getDeclaration());
        assertEqualsIgnoreWhitespace( "$age2 == $age1+2", pred.getText());
        
    }    
    
    public void testGlobal() throws Exception {
        RuleParser parser = parseResource( "globals.drl" );
        parser.compilation_unit();
        
        PackageDescr pack = parser.getPackageDescr();
        assertEquals(1, pack.getRules().size());
        
        RuleDescr rule = (RuleDescr) pack.getRules().get( 0 );
        assertEquals(1, rule.getLhs().getDescrs().size());
        
        assertEquals(1, pack.getImports().size());
        assertEquals(2, pack.getGlobals().values().size());
        
        System.err.println( "G: " + pack.getGlobals() );
        
        assertEquals("java.lang.String", pack.getGlobals().get( "foo" ));
        assertEquals("java.lang.Integer", pack.getGlobals().get( "bar" ));
        
    }   

    public void testFunctions() throws Exception {
        RuleParser parser = parseResource( "functions.drl" );
        parser.compilation_unit();
        
        PackageDescr pack = parser.getPackageDescr();
        assertEquals(2, pack.getRules().size());

        List functions = pack.getFunctions();
        assertEquals(2, functions.size());
        
        FunctionDescr func = (FunctionDescr) functions.get( 0 );
        assertEquals("functionA", func.getName());
        assertEquals("String", func.getReturnType());
        assertEquals(2, func.getParameterNames().size());
        assertEquals(2, func.getParameterTypes().size());
        
        assertEquals("String", func.getParameterTypes().get( 0 ));
        assertEquals("s", func.getParameterNames().get( 0 ));

        assertEquals("Integer", func.getParameterTypes().get( 1 ));
        assertEquals("i", func.getParameterNames().get( 1 ));

        assertEqualsIgnoreWhitespace( "foo();", func.getText());
        
        func = (FunctionDescr) functions.get( 1 );
        assertEquals("functionB", func.getName());
        assertEqualsIgnoreWhitespace( "bar();", func.getText());

        
    }   
    
    public void testComment() throws Exception {
        RuleParser parser = parseResource( "comment.drl" );
        parser.compilation_unit();
        
        PackageDescr pack = parser.getPackageDescr();
        assertNotNull(pack);
        
        assertEquals( "foo.bar", pack.getName() );
    }
    
    
	
	private RuleParser parse(String text) throws Exception {
		parser = newParser( newTokenStream( newLexer( newCharStream( text ) ) ) );
		return parser;
	}
	
	private RuleParser parseResource(String name) throws Exception {
		
		InputStream in = getClass().getResourceAsStream( name );
		
		InputStreamReader reader = new InputStreamReader( in );
		
		StringBuffer text = new StringBuffer();
		
		char[] buf = new char[1024];
		int len = 0;
		
		while ( ( len = reader.read( buf ) ) >= 0 ) {
			text.append( buf, 0, len );
		}
		
		return parse( text.toString() );
	}
	
	private CharStream newCharStream(String text) {
		return new ANTLRStringStream( text );
	}
	
	private RuleParserLexer newLexer(CharStream charStream) {
		return new RuleParserLexer( charStream );
	}
	
	private TokenStream newTokenStream(Lexer lexer) {
		return new CommonTokenStream( lexer );
	}
	
	private RuleParser newParser(TokenStream tokenStream) {
		return new RuleParser( tokenStream );
	}
	
	private void assertEqualsIgnoreWhitespace(String expected, String actual) {
		String cleanExpected = expected.replaceAll( "\\s+", "" );
		String cleanActual   = actual.replaceAll( "\\s+", "" );
		
		assertEquals( cleanExpected, cleanActual );
	}

}
