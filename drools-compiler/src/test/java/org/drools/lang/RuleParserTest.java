package org.drools.lang;

/*
 * Copyright 2005 JBoss Inc
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import junit.framework.TestCase;

import org.antlr.runtime.ANTLRStringStream;
import org.antlr.runtime.CharStream;
import org.antlr.runtime.CommonTokenStream;
import org.antlr.runtime.Lexer;
import org.antlr.runtime.RecognitionException;
import org.antlr.runtime.TokenStream;
import org.drools.compiler.DrlParser;
import org.drools.compiler.SwitchingCommonTokenStream;
import org.drools.lang.descr.AccessorDescr;
import org.drools.lang.descr.AccumulateDescr;
import org.drools.lang.descr.AndDescr;
import org.drools.lang.descr.AttributeDescr;
import org.drools.lang.descr.CollectDescr;
import org.drools.lang.descr.ColumnDescr;
import org.drools.lang.descr.EvalDescr;
import org.drools.lang.descr.ExistsDescr;
import org.drools.lang.descr.FactTemplateDescr;
import org.drools.lang.descr.FieldAccessDescr;
import org.drools.lang.descr.FieldBindingDescr;
import org.drools.lang.descr.FieldConstraintDescr;
import org.drools.lang.descr.FieldTemplateDescr;
import org.drools.lang.descr.FromDescr;
import org.drools.lang.descr.FunctionDescr;
import org.drools.lang.descr.LiteralRestrictionDescr;
import org.drools.lang.descr.NotDescr;
import org.drools.lang.descr.OrDescr;
import org.drools.lang.descr.PackageDescr;
import org.drools.lang.descr.PredicateDescr;
import org.drools.lang.descr.QueryDescr;
import org.drools.lang.descr.RestrictionConnectiveDescr;
import org.drools.lang.descr.ReturnValueRestrictionDescr;
import org.drools.lang.descr.RuleDescr;
import org.drools.lang.descr.VariableRestrictionDescr;
import org.drools.lang.dsl.DefaultExpanderResolver;

public class RuleParserTest extends TestCase {

    private DRLParser parser;

    protected void setUp() throws Exception {
        super.setUp();
        this.parser = null;
    }

    protected void tearDown() throws Exception {
        this.parser = null;
        super.tearDown();
    }

    public void testPackage_OneSegment() throws Exception {
        final String packageName = parse( "package foo" ).package_statement();
        assertEquals( "foo",
                      packageName );
        assertFalse( this.parser.hasErrors() );
    }

    public void testPackage_MultipleSegments() throws Exception {
        final String packageName = parse( "package foo.bar.baz;" ).package_statement();
        assertEquals( "foo.bar.baz",
                      packageName );
        assertFalse( this.parser.hasErrors() );
    }

    public void testCompilationUnit() throws Exception {
        parse( "package foo; import com.foo.Bar; import com.foo.Baz;" ).compilation_unit();
        assertEquals( "foo",
                      this.parser.getPackageDescr().getName() );
        assertEquals( 2,
                      this.parser.getPackageDescr().getImports().size() );
        assertEquals( "com.foo.Bar",
                      this.parser.getPackageDescr().getImports().get( 0 ) );
        assertEquals( "com.foo.Baz",
                      this.parser.getPackageDescr().getImports().get( 1 ) );
        assertFalse( this.parser.hasErrors() );
    }

    public void testEmptyRule() throws Exception {
        final RuleDescr rule = parseResource( "empty_rule.drl" ).rule();

        assertNotNull( rule );

        assertEquals( "empty",
                      rule.getName() );
        assertNull( rule.getLhs() );
        assertNotNull( rule.getConsequence() );

        assertFalse( this.parser.hasErrors() );
    }

    public void testKeywordCollisions() throws Exception {
        //MN: this really needs the multiphase parser for it to work properly
        final DRLParser parser = parseResource( "eol_funny_business.drl" );

        parser.compilation_unit();
        final PackageDescr pkg = parser.getPackageDescr();

        
        
        assertEquals( 1,
                      pkg.getRules().size() );

        assertFalse( parser.getErrors().toString(), parser.hasErrors() );
        
    }
    
    public void testPartialAST() throws Exception {
        parseResource( "column_partial.drl" );
        
        parser.compilation_unit();
        
        assertTrue(parser.hasErrors());
        
        PackageDescr pkg = parser.getPackageDescr();
        assertEquals(1, pkg.getRules().size());
        RuleDescr rule = (RuleDescr) pkg.getRules().get( 0 );
        
        assertEquals(1, rule.getLhs().getDescrs().size());
        ColumnDescr col = (ColumnDescr) rule.getLhs().getDescrs().get(0);
        
        assertNotNull(col);
        assertEquals("Bar", col.getObjectType());
        assertEquals("foo3", col.getIdentifier());
                
    }
    
    public void testTemplates() throws Exception {
        
        final DRLParser parser = parseResource( "test_Templates.drl" );

        parser.compilation_unit();
        final PackageDescr pkg = parser.getPackageDescr();

        if (parser.hasErrors()) {
        	System.err.println("FACT TEMPLATES FAILED: " + parser.getErrorMessages());
        }
        assertFalse( parser.hasErrors() );
        
        assertEquals( 1, pkg.getRules().size() );
        assertEquals(2, pkg.getFactTemplates().size());
        
        FactTemplateDescr fact1 = (FactTemplateDescr) pkg.getFactTemplates().get(0);
        assertEquals("Cheese", fact1.getName());
        assertEquals(2, fact1.getFields().size());
        
        assertEquals("name", ((FieldTemplateDescr)fact1.getFields().get(0)).getName());
        assertEquals("String", ((FieldTemplateDescr)fact1.getFields().get(0)).getClassType());
        
        assertEquals("age", ((FieldTemplateDescr)fact1.getFields().get(1)).getName());
        assertEquals("Integer", ((FieldTemplateDescr)fact1.getFields().get(1)).getClassType());
        
        fact1 = null;
        
        FactTemplateDescr fact2 = (FactTemplateDescr) pkg.getFactTemplates().get(1);
        assertEquals("Wine", fact2.getName());
        assertEquals(3, fact2.getFields().size());
        
        assertEquals("name", ((FieldTemplateDescr)fact2.getFields().get(0)).getName());
        assertEquals("String", ((FieldTemplateDescr)fact2.getFields().get(0)).getClassType());

        assertEquals("year", ((FieldTemplateDescr)fact2.getFields().get(1)).getName());
        assertEquals("String", ((FieldTemplateDescr)fact2.getFields().get(1)).getClassType());
        
        assertEquals("accolades", ((FieldTemplateDescr)fact2.getFields().get(2)).getName());
        assertEquals("String[]", ((FieldTemplateDescr)fact2.getFields().get(2)).getClassType());
    }    
    
    public void testTernaryExpression() throws Exception {

        final DRLParser parser = parseResource( "ternary_expression.drl" );

        parser.compilation_unit();
        final PackageDescr pkg = parser.getPackageDescr();
        RuleDescr rule = (RuleDescr) pkg.getRules().get(0);
        assertEquals( 1,
                      pkg.getRules().size() );

        assertFalse( parser.hasErrors() );
        assertEqualsIgnoreWhitespace("if (speed > speedLimit ? true : false;) pullEmOver();", rule.getConsequence());
    }    
    

    public void FIX_ME_testLatinChars() throws Exception {
        final DrlParser parser = new DrlParser();
        final Reader drl = new InputStreamReader( this.getClass().getResourceAsStream( "latin-sample.drl" ) );
        final Reader dsl = new InputStreamReader( this.getClass().getResourceAsStream( "latin.dsl" ) );

        final PackageDescr pkg = parser.parse( drl,
                                         dsl );

        
        //MN: will get some errors due to the char encoding on my FC5 install
        //others who use the right encoding may not see this, feel free to uncomment
        //the following assertion.
        assertFalse( parser.hasErrors() );
        
        
        assertEquals( "br.com.auster.drools.sample",
                      pkg.getName() );
        assertEquals( 1,
                      pkg.getRules().size() );

    }

    public void testFunctionWithArrays() throws Exception {
        DRLParser parser = parseResource( "function_arrays.drl" );

        parser.compilation_unit();
        PackageDescr pkg = parser.getPackageDescr();
        
        if (parser.hasErrors()) {
        	System.err.println(parser.getErrorMessages());
        }
        assertFalse( parser.hasErrors() );
        assertEquals( "foo",
                      pkg.getName() );
        assertEquals( 1,
                      pkg.getRules().size() );

        final RuleDescr rule = (RuleDescr) pkg.getRules().get( 0 );

        assertEqualsIgnoreWhitespace( "yourFunction(new String[3] {\"a\",\"b\",\"c\"});",
                                      rule.getConsequence() );
        final FunctionDescr func = (FunctionDescr) pkg.getFunctions().get( 0 );

        assertEquals( "String[]",
                      func.getReturnType() );
        assertEquals( "args[]",
                      func.getParameterNames().get( 0 ) );
        assertEquals( "String",
                      func.getParameterTypes().get( 0 ) );
    }

    public void testAlmostEmptyRule() throws Exception {
        final RuleDescr rule = parseResource( "almost_empty_rule.drl" ).rule();

        assertNotNull( rule );

        assertEquals( "almost_empty",
                      rule.getName() );
        assertNotNull( rule.getLhs() );
        assertEquals( "",
                      rule.getConsequence().trim() );
        assertFalse( this.parser.hasErrors() );
    }

    public void testQuotedStringNameRule() throws Exception {
        final RuleDescr rule = parseResource( "quoted_string_name_rule.drl" ).rule();

        assertNotNull( rule );

        assertEquals( "quoted string name",
                      rule.getName() );
        assertNotNull( rule.getLhs() );
        assertEquals( "",
                      rule.getConsequence().trim() );
        assertFalse( this.parser.hasErrors() );
    }

    public void testNoLoop() throws Exception {
        final RuleDescr rule = parseResource( "no-loop.drl" ).rule();

        assertNotNull( rule );

        assertEquals( "rule1",
                      rule.getName() );
        final AttributeDescr att = (AttributeDescr) rule.getAttributes().get( 0 );
        assertEquals( "false",
                      att.getValue() );
        assertEquals( "no-loop",
                      att.getName() );
        assertFalse( this.parser.hasErrors() );

    }

    public void testAutofocus() throws Exception {
        final RuleDescr rule = parseResource( "autofocus.drl" ).rule();

        assertNotNull( rule );

        assertEquals( "rule1",
                      rule.getName() );
        final AttributeDescr att = (AttributeDescr) rule.getAttributes().get( 0 );
        assertEquals( "true",
                      att.getValue() );
        assertEquals( "auto-focus",
                      att.getName() );

        assertFalse( this.parser.hasErrors() );
    }

    public void testConsequenceWithDeclaration() throws Exception {
        final RuleDescr rule = parseResource( "declaration-in-consequence.drl" ).rule();

        assertNotNull( rule );

        assertEquals( "myrule",
                      rule.getName() );

        final String expected = "int i = 0; i = 1; i / 1; i == 1; i(i); i = 'i'; i.i.i; i\\i; i<i; i>i; i=\"i\";  ++i;" + "i++; --i; i--; i += i; i -= i; i *= i; i /= i;" + "int i = 5;" + "for(int j; j<i; ++j) {" + "System.out.println(j);}"
                          + "Object o = new String(\"Hello\");" + "String s = (String) o;";

        assertEqualsIgnoreWhitespace( expected,
                                      rule.getConsequence() );
        assertTrue( rule.getConsequence().indexOf( "++" ) > 0 );
        assertTrue( rule.getConsequence().indexOf( "--" ) > 0 );
        assertTrue( rule.getConsequence().indexOf( "+=" ) > 0 );
        assertTrue( rule.getConsequence().indexOf( "==" ) > 0 );

        //System.out.println(rule.getConsequence());
        //note, need to assert that "i++" is preserved as is, no extra spaces.

        assertFalse( this.parser.hasErrors() );
    }
    
    
    public void testRuleParseLhs() throws Exception {
    	String text = "Person(age < 42, location==\"atlanta\") \nor\nPerson(name==\"bob\") \n";
    	AndDescr descrs = new AndDescr();
		CharStream charStream = new ANTLRStringStream( text );
		DRLLexer lexer = new DRLLexer( charStream );
		TokenStream tokenStream = new CommonTokenStream( lexer );
        DRLParser parser = new DRLParser( tokenStream );
		parser.setLineOffset( descrs.getLine() );
		parser.normal_lhs_block(descrs);
        if(parser.hasErrors()) {
            System.err.println(parser.getErrorMessages());
        }
		assertFalse(parser.hasErrors());
    	
    }

    public void testLiteralBoolAndNegativeNumbersRule() throws Exception {
        final DRLParser parser = parseResource( "literal_bool_and_negative.drl" );
        final RuleDescr rule = parser.rule();
        assertFalse( parser.hasErrors() );

        assertNotNull( rule );

        assertEquals( "simple_rule",
                      rule.getName() );
        assertNotNull( rule.getLhs() );
        assertEqualsIgnoreWhitespace( "cons();",
                                      rule.getConsequence() );

        final AndDescr lhs = rule.getLhs();
        assertEquals( 3,
                      lhs.getDescrs().size() );

        ColumnDescr col = (ColumnDescr) lhs.getDescrs().get( 0 );
        assertEquals( 1,
                      col.getDescrs().size() );
        FieldConstraintDescr fld = (FieldConstraintDescr) col.getDescrs().get( 0 );
        LiteralRestrictionDescr lit = (LiteralRestrictionDescr) fld.getRestrictions().get(0);
        
        assertEquals( "==",
                      lit.getEvaluator() );
        assertEquals( "false",
                      lit.getText() );
        assertEquals( "bar",
        			  fld.getFieldName() );
        assertEquals( false,
                      lit.isStaticFieldValue() );

        col = (ColumnDescr) lhs.getDescrs().get( 1 );
        assertEquals( 1,
                      col.getDescrs().size() );
        
        
        fld = (FieldConstraintDescr) col.getDescrs().get( 0 );
        lit = (LiteralRestrictionDescr) fld.getRestrictions().get(0);
        
        
        assertEquals( ">",
                      lit.getEvaluator() );
        assertEquals( "-42",
                      lit.getText() );
        assertEquals( "boo",
                      fld.getFieldName() );

        col = (ColumnDescr) lhs.getDescrs().get( 2 );
        assertEquals( 1,
                      col.getDescrs().size() );
        
        
        //lit = (LiteralDescr) col.getDescrs().get( 0 );
        
        fld = (FieldConstraintDescr) col.getDescrs().get( 0 );
        lit = (LiteralRestrictionDescr) fld.getRestrictions().get(0);
        assertEquals( ">",
                      lit.getEvaluator() );
        assertEquals( "-42.42",
                      lit.getText() );
        assertEquals( "boo",
                      fld.getFieldName() );

        assertFalse( parser.hasErrors() );
    }

    public void testChunkWithoutParens() throws Exception {
        final String chunk = parse( "( foo )" ).paren_chunk();

        assertEquals( "( foo )",
                      chunk );

        assertFalse( this.parser.hasErrors() );
    }

    public void testChunkWithParens() throws Exception {
        final String chunk = parse( "(fnord())" ).paren_chunk();

        assertEqualsIgnoreWhitespace( "(fnord())",
                                      chunk );

        assertFalse( this.parser.hasErrors() );
    }

    public void testChunkWithParensAndQuotedString() throws Exception {
        final String chunk = parse( "( fnord( \"cheese\" ) )" ).paren_chunk();

        assertEqualsIgnoreWhitespace( "( fnord( \"cheese\" ) )",
                                      chunk );

        assertFalse( this.parser.hasErrors() );
    }

    public void testChunkWithRandomCharac5ters() throws Exception {
        final String chunk = parse( "( %*9dkj)" ).paren_chunk();

        assertEqualsIgnoreWhitespace( "( %*9dkj)",
                                      chunk );

        assertFalse( this.parser.hasErrors() );
    }

    public void testEmptyColumn() throws Exception {
        parseResource( "test_EmptyColumn.drl" );
        this.parser.compilation_unit();
        final PackageDescr packageDescr = this.parser.getPackageDescr();
        assertEquals( 1,
                      packageDescr.getRules().size() );
        final RuleDescr ruleDescr = (RuleDescr) packageDescr.getRules().get( 0 );
        assertEquals( "simple rule",
                      ruleDescr.getName() );
        assertNotNull( ruleDescr.getLhs() );
        assertEquals( 1,
                      ruleDescr.getLhs().getDescrs().size() );
        final ColumnDescr columnDescr = (ColumnDescr) ruleDescr.getLhs().getDescrs().get( 0 );
        assertEquals( 0,
                      columnDescr.getDescrs().size() ); //this may be null, not sure as the test doesn't get this far...
        assertEquals( "Cheese",
                      columnDescr.getObjectType() );

    }
    
    public void testSimpleMethodCallWithFrom() throws Exception {
        
        final RuleDescr rule = parseResource( "test_SimpleMethodCallWithFrom.drl" ).rule();
        FromDescr from = (FromDescr) rule.getLhs().getDescrs().get( 0 );
        AccessorDescr method = (AccessorDescr) from.getDataSource();
        
        assertFalse(parser.getErrorMessages().toString(), parser.hasErrors());    
        
        assertEquals( "something.doIt( foo,bar,42,\"hello\",{ a => \"b\", \"something\" => 42, \"a\" => foo, x => {x=>y}},\"end\", [a, \"b\", 42] )", 
                      method.toString());
    }
    
    public void testSimpleFunctionCallWithFrom() throws Exception {
        
        final RuleDescr rule = parseResource( "test_SimpleFunctionCallWithFrom.drl" ).rule();
        FromDescr from = (FromDescr) rule.getLhs().getDescrs().get( 0 );
        AccessorDescr func = (AccessorDescr) from.getDataSource();
        
        assertFalse(parser.getErrorMessages().toString(), parser.hasErrors());    
        
        assertEquals( "doIt( foo,bar,42,\"hello\",{ a => \"b\", \"something\" => 42, \"a\" => foo, x => {x=>y}},\"end\", [a, \"b\", 42] )", 
                      func.toString() );
    }    
    
    
    public void testSimpleAccessorWithFrom() throws Exception {
        
        final RuleDescr rule = parseResource( "test_SimpleAccessorWithFrom.drl" ).rule();
        FromDescr from = (FromDescr) rule.getLhs().getDescrs().get( 0 );
        AccessorDescr accessor = (AccessorDescr) from.getDataSource();
        
        assertFalse(parser.getErrorMessages().toString(), parser.hasErrors());    
        
        assertNull( ((FieldAccessDescr) accessor.getInvokers().get( 0 )).getArgument() );
        
        assertEquals( "something.doIt", accessor.toString() );
    }          
    
    public void testSimpleAccessorAndArgWithFrom() throws Exception {
        
        final RuleDescr rule = parseResource( "test_SimpleAccessorArgWithFrom.drl" ).rule();
        FromDescr from = (FromDescr) rule.getLhs().getDescrs().get( 0 );
        AccessorDescr accessor = (AccessorDescr) from.getDataSource();
        
        assertFalse(parser.getErrorMessages().toString(), parser.hasErrors());    
        
        assertNotNull( ((FieldAccessDescr) accessor.getInvokers().get( 0 )).getArgument() );
        
        assertEquals( "something.doIt[\"key\"]", accessor.toString() );
    }      
    
    public void testComplexChainedAcessor() throws Exception {
        final RuleDescr rule = parseResource( "test_ComplexChainedCallWithFrom.drl" ).rule();
        FromDescr from = (FromDescr) rule.getLhs().getDescrs().get( 0 );
        AccessorDescr accessor = (AccessorDescr) from.getDataSource();
 
        assertFalse(parser.getErrorMessages().toString(), parser.hasErrors());    
 
        assertEquals( "doIt1( foo,bar,42,\"hello\",{ a => \"b\"}, [a, \"b\", 42] ).doIt2(bar, [a, \"b\", 42]).field[\"key\"]", 
                      accessor.toString() );        
    }    
    
//    public void testFrom() throws Exception {
//        final RuleDescr rule = parseResource( "from.drl" ).rule();
//
//        if(parser.hasErrors()) {
//            System.err.println(parser.getErrorMessages());
//        }
//        assertFalse(parser.hasErrors());
//        
//        assertNotNull( rule );
//
//        assertEquals( "using_from",
//                      rule.getName() );
//
//        assertEquals(9, rule.getLhs().getDescrs().size());
//        
//        FromDescr from = (FromDescr) rule.getLhs().getDescrs().get(0);
//        
//        assertEquals(3, from.getLine());
//        
//        assertEquals("Foo", from.getReturnedColumn().getObjectType());
//        assertTrue(from.getDataSource() instanceof FieldAccessDescr);
//        assertEquals("baz", ((FieldAccessDescr) from.getDataSource()).getFieldName());        
//        assertEquals("bar", ((FieldAccessDescr) from.getDataSource()).getVariableName());
//        
//        
//        ArgumentValueDescr arg = null;
//        
//        from = (FromDescr) rule.getLhs().getDescrs().get(1);
//        assertEquals("Foo", from.getReturnedColumn().getObjectType());
//        assertEquals(0, from.getReturnedColumn().getDescrs().size());
//        FieldAccessDescr fieldAccess = ( FieldAccessDescr ) from.getDataSource();
//        arg = ( ArgumentValueDescr ) fieldAccess.getArgument();
//        assertEquals(ArgumentValueDescr.STRING,  arg.getType() );
//        
//        from = (FromDescr) rule.getLhs().getDescrs().get(2);
//        fieldAccess = ( FieldAccessDescr ) from.getDataSource();
//        arg = ( ArgumentValueDescr ) fieldAccess.getArgument();
//        assertEquals(ArgumentValueDescr.VARIABLE,  arg.getType() );        
//        
//        from = (FromDescr) rule.getLhs().getDescrs().get(3);
//        fieldAccess = ( FieldAccessDescr ) from.getDataSource();
//        arg = ( ArgumentValueDescr ) fieldAccess.getArgument();
//        assertEquals(ArgumentValueDescr.INTEGRAL,  arg.getType() );
//        
//        from = (FromDescr) rule.getLhs().getDescrs().get(4);
//        assertEquals("Whee", from.getReturnedColumn().getObjectType());
//        assertEquals(1, from.getReturnedColumn().getDescrs().size());
//        assertTrue(from.getDataSource() instanceof FunctionCallDescr);
//        assertEquals("whee", ((FunctionCallDescr) from.getDataSource()).getName());        
//        assertEquals(1, ((FunctionCallDescr) from.getDataSource()).getArguments().size());
//        arg = ( (ArgumentValueDescr )((FunctionCallDescr) from.getDataSource()).getArguments().get(0));
//        assertEquals("y", arg.getValue());
//        assertEquals(ArgumentValueDescr.STRING, arg.getType());
//
//        assertEquals(7, from.getLine());
//        assertEquals(7, from.getReturnedColumn().getLine());
//        
//        from = (FromDescr) rule.getLhs().getDescrs().get(5);
//        assertEquals("Foo", from.getReturnedColumn().getObjectType());
//        assertEquals(1, from.getReturnedColumn().getDescrs().size());
//        assertEquals("f", from.getReturnedColumn().getIdentifier());
//        assertTrue(from.getDataSource() instanceof MethodAccessDescr);
//        assertEquals("bar", ((MethodAccessDescr) from.getDataSource()).getVariableName());        
//        assertEquals("la", ((MethodAccessDescr) from.getDataSource()).getMethodName());
//        assertEquals(1, ((MethodAccessDescr) from.getDataSource()).getArguments().size());
//        arg = (ArgumentValueDescr) ((MethodAccessDescr) from.getDataSource()).getArguments().get(0);
//        
//        
//        assertEquals("x", arg.getValue());
//        assertEquals(ArgumentValueDescr.VARIABLE, arg.getType());
//
//        assertEqualsIgnoreWhitespace("whee();", rule.getConsequence());
//        
//        from = (FromDescr) rule.getLhs().getDescrs().get(6);
//        assertEquals("wa", ((FunctionCallDescr)from.getDataSource()).getName());
//
//        from = (FromDescr) rule.getLhs().getDescrs().get(7);
//        MethodAccessDescr meth = (MethodAccessDescr)from.getDataSource();
//        assertEquals("wa", meth.getMethodName());
//        assertEquals("la", meth.getVariableName());
//        
//        arg = (ArgumentValueDescr) meth.getArguments().get(0);
//        assertEquals("42", arg.getValue());
//        assertEquals(ArgumentValueDescr.INTEGRAL, arg.getType());
//        
//        arg = (ArgumentValueDescr) meth.getArguments().get(1);
//        assertEquals("42.42", arg.getValue());
//        assertEquals(ArgumentValueDescr.DECIMAL, arg.getType());
//
//        arg = (ArgumentValueDescr) meth.getArguments().get(2);
//        assertEquals("false", arg.getValue());
//        assertEquals(ArgumentValueDescr.BOOLEAN, arg.getType());
//        
//        arg = (ArgumentValueDescr) meth.getArguments().get(3);
//        assertEquals("null", arg.getValue());
//        assertEquals(ArgumentValueDescr.NULL, arg.getType());
//        
//                
//        
//        assertEquals("Bam", ((ColumnDescr)rule.getLhs().getDescrs().get(8)).getObjectType());
//    }
    
    public void testSimpleRule() throws Exception {
        final RuleDescr rule = parseResource( "simple_rule.drl" ).rule();

        assertNotNull( rule );

        assertEquals( "simple_rule",
                      rule.getName() );

        assertEquals( 7,
                      rule.getConsequenceLine() );
        assertEquals( 2,
                      rule.getConsequenceColumn() );

        final AndDescr lhs = rule.getLhs();

        assertNotNull( lhs );

        assertEquals( 3,
                      lhs.getDescrs().size() );

        //System.err.println( lhs.getDescrs() );

        // Check first column
        final ColumnDescr first = (ColumnDescr) lhs.getDescrs().get( 0 );
        assertEquals( "foo3",
                      first.getIdentifier() );
        assertEquals( "Bar",
                      first.getObjectType() );

        assertEquals( 1,
                      first.getDescrs().size() );
        
        
        FieldConstraintDescr fld = (FieldConstraintDescr) first.getDescrs().get( 0 );
        LiteralRestrictionDescr constraint = (LiteralRestrictionDescr) fld.getRestrictions().get(0);
        
        
        
        assertNotNull( constraint );

        assertEquals( "a",
                      fld.getFieldName() );
        assertEquals( "==",
                      constraint.getEvaluator() );
        assertEquals( "3",
                      constraint.getText() );

        // Check second column
        final ColumnDescr second = (ColumnDescr) lhs.getDescrs().get( 1 );
        assertEquals( "foo4",
                      second.getIdentifier() );
        assertEquals( "Bar",
                      second.getObjectType() );

        assertEquals( 2,
                      second.getDescrs().size() );

        //System.err.println( second.getDescrs() );

        final FieldBindingDescr fieldBindingDescr = (FieldBindingDescr) second.getDescrs().get( 0 );
        assertEquals( "a",
                      fieldBindingDescr.getFieldName() );
        assertEquals( "a4",
                      fieldBindingDescr.getIdentifier() );

        
        fld = (FieldConstraintDescr) second.getDescrs().get( 1 );
        constraint = (LiteralRestrictionDescr) fld.getRestrictions().get( 0 );
        
        
        
        assertNotNull( constraint );

        assertEquals( "a",
                      fld.getFieldName() );
        assertEquals( "==",
                      constraint.getEvaluator() );
        assertEquals( "4",
                      constraint.getText() );

        // Check third column
        final ColumnDescr third = (ColumnDescr) lhs.getDescrs().get( 2 );
        assertNull( third.getIdentifier() );
        assertEquals( "Baz",
                      third.getObjectType() );

        assertEqualsIgnoreWhitespace( "if ( a == b ) { " + "  assert( foo3 );" + "} else {" + "  retract( foo4 );" + "}" + "  System.out.println( a4 );",
                                      rule.getConsequence() );

        assertFalse( this.parser.hasErrors() );
    }
    
    
    public void testRestrictionsMultiple() throws Exception {
        final RuleDescr rule = parseResource( "restrictions_test.drl" ).rule();
        
        assertFalse(this.parser.getErrors().toString(),this.parser.hasErrors());        
        assertNotNull( rule );

        assertEqualsIgnoreWhitespace("consequence();", rule.getConsequence());
        assertEquals( "simple_rule", rule.getName() );
        assertEquals(2, rule.getLhs().getDescrs().size());        
        
        //The first column, with 2 restrictions on a single field (plus a connective)
        ColumnDescr col = (ColumnDescr) rule.getLhs().getDescrs().get(0);
        assertEquals("Person", col.getObjectType());
        assertEquals(1, col.getDescrs().size());
        
        FieldConstraintDescr fld = (FieldConstraintDescr) col.getDescrs().get(0);
        assertEquals(3, fld.getRestrictions().size());
        assertEquals("age", fld.getFieldName());
        
        LiteralRestrictionDescr lit = (LiteralRestrictionDescr) fld.getRestrictions().get(0);
        assertEquals(">", lit.getEvaluator());
        assertEquals("30", lit.getText());
        
        RestrictionConnectiveDescr con = (RestrictionConnectiveDescr) fld.getRestrictions().get(1);
        assertEquals(RestrictionConnectiveDescr.AND, con.getConnective());
        
        lit = (LiteralRestrictionDescr) fld.getRestrictions().get(2);
        assertEquals("<", lit.getEvaluator());
        assertEquals("40", lit.getText());
        
        //the second col, with 2 fields, the first with 2 restrictions, the second field with one
        col = (ColumnDescr) rule.getLhs().getDescrs().get(1);
        assertEquals("Vehicle", col.getObjectType());
        assertEquals(2, col.getDescrs().size());
        
        fld = (FieldConstraintDescr) col.getDescrs().get(0);
        assertEquals(3, fld.getRestrictions().size());
        lit = (LiteralRestrictionDescr) fld.getRestrictions().get(0);
        assertEquals("type", fld.getFieldName());
        assertEquals("==", lit.getEvaluator());
        assertEquals("sedan", lit.getText());
        con = (RestrictionConnectiveDescr) fld.getRestrictions().get(1);
        assertEquals(RestrictionConnectiveDescr.OR, con.getConnective());
        
        lit = (LiteralRestrictionDescr) fld.getRestrictions().get(2);
        assertEquals("==", lit.getEvaluator());
        assertEquals("wagon", lit.getText());
        
        
        	//now the second field
        fld = (FieldConstraintDescr) col.getDescrs().get(1);
        assertEquals(1, fld.getRestrictions().size());
        lit = (LiteralRestrictionDescr) fld.getRestrictions().get(0);
        assertEquals("<", lit.getEvaluator());
        assertEquals("3", lit.getText());

    }

    public void testLineNumberInAST() throws Exception {
        //also see testSimpleExpander to see how this works with an expander (should be the same). 

        final RuleDescr rule = parseResource( "simple_rule.drl" ).rule();

        assertNotNull( rule );

        assertEquals( "simple_rule",
                      rule.getName() );

        assertEquals( 7,
                      rule.getConsequenceLine() );
        assertEquals( 2,
                      rule.getConsequenceColumn() );

        final AndDescr lhs = rule.getLhs();

        assertNotNull( lhs );

        assertEquals( 3,
                      lhs.getDescrs().size() );

        // Check first column
        final ColumnDescr first = (ColumnDescr) lhs.getDescrs().get( 0 );
        assertEquals( "foo3",
                      first.getIdentifier() );
        assertEquals( "Bar",
                      first.getObjectType() );
        assertEquals( 1,
                      first.getDescrs().size() );

        // Check second column
        final ColumnDescr second = (ColumnDescr) lhs.getDescrs().get( 1 );
        assertEquals( "foo4",
                      second.getIdentifier() );
        assertEquals( "Bar",
                      second.getObjectType() );

        final ColumnDescr third = (ColumnDescr) lhs.getDescrs().get( 2 );
        assertEquals( "Baz",
                      third.getObjectType() );

        assertEquals( 4,
                      first.getLine() );
        assertEquals( 5,
                      second.getLine() );
        assertEquals( 6,
                      third.getLine() );
        assertFalse( this.parser.hasErrors() );
    }
    
    public void FIXME_testLineNumberIncludingCommentsInRHS() throws Exception {
        parseResource( "test_CommentLineNumbersInConsequence.drl" ).compilation_unit();
        
        assertFalse(parser.hasErrors());        
        String rhs = ((RuleDescr) parser.getPackageDescr().getRules().get( 0 )).getConsequence();
        //System.out.println(rhs);
        assertEquals("\n first\n\n\n\n\n\n\n second", rhs);
    }

    public void testMultiBindings() throws Exception {
        final RuleDescr rule = parseResource( "multiple_bindings.drl" ).rule();
        assertNotNull( rule );
        assertEquals( "simple_rule",
                      rule.getName() );

        assertEquals( 2,
                      rule.getLhs().getDescrs().size() );
        assertEquals( "foo",
                      ((ColumnDescr) rule.getLhs().getDescrs().get( 0 )).getIdentifier() );
        assertEquals( "baz",
                      ((ColumnDescr) rule.getLhs().getDescrs().get( 1 )).getIdentifier() );

    }

    public void testMultiBindingsMore() throws Exception {
        final RuleDescr rule = parseResource( "multiple_bindings_more.drl" ).rule();
        assertNotNull( rule );
        assertEquals( "simple_rule",
                      rule.getName() );

        assertEquals( 3,
                      rule.getLhs().getDescrs().size() );
        assertEquals( "foo",
                      ((ColumnDescr) rule.getLhs().getDescrs().get( 0 )).getIdentifier() );
        assertEquals( "something foo",
                      ((EvalDescr) rule.getLhs().getDescrs().get( 1 )).getText() );
        assertEquals( "another foo",
                      ((EvalDescr) rule.getLhs().getDescrs().get( 2 )).getText() );

    }

    public void testLhsSemicolonDelim() throws Exception {
        final RuleDescr rule = parseResource( "lhs_semicolon_delim.drl" ).rule();

        assertNotNull( rule );

        assertEquals( "simple_rule",
                      rule.getName() );

        final AndDescr lhs = rule.getLhs();

        assertNotNull( lhs );

        assertEquals( 3,
                      lhs.getDescrs().size() );

        //System.err.println( lhs.getDescrs() );

        // Check first column
        final ColumnDescr first = (ColumnDescr) lhs.getDescrs().get( 0 );
        assertEquals( "foo3",
                      first.getIdentifier() );
        assertEquals( "Bar",
                      first.getObjectType() );

        assertEquals( 1,
                      first.getDescrs().size() );

        //LiteralDescr constraint = (LiteralDescr) first.getDescrs().get( 0 );

        FieldConstraintDescr fld = (FieldConstraintDescr) first.getDescrs().get(0);
        LiteralRestrictionDescr constraint = (LiteralRestrictionDescr) fld.getRestrictions().get(0);
        
        assertNotNull( constraint );

        assertEquals( "a",
                      fld.getFieldName() );
        assertEquals( "==",
                      constraint.getEvaluator() );
        assertEquals( "3",
                      constraint.getText() );

        // Check second column
        final ColumnDescr second = (ColumnDescr) lhs.getDescrs().get( 1 );
        assertEquals( "foo4",
                      second.getIdentifier() );
        assertEquals( "Bar",
                      second.getObjectType() );

        assertEquals( 2,
                      second.getDescrs().size() );

        //System.err.println( second.getDescrs() );

        final FieldBindingDescr fieldBindingDescr = (FieldBindingDescr) second.getDescrs().get( 0 );
        assertEquals( "a",
                      fieldBindingDescr.getFieldName() );
        assertEquals( "a4",
                      fieldBindingDescr.getIdentifier() );

        
        fld = (FieldConstraintDescr) second.getDescrs().get( 1 );
        
        constraint = (LiteralRestrictionDescr) fld.getRestrictions().get(0);
        
        
        assertNotNull( constraint );

        assertEquals( "a",
                      fld.getFieldName() );
        assertEquals( "==",
                      constraint.getEvaluator() );
        assertEquals( "4",
                      constraint.getText() );

        // Check third column
        final ColumnDescr third = (ColumnDescr) lhs.getDescrs().get( 2 );
        assertNull( third.getIdentifier() );
        assertEquals( "Baz",
                      third.getObjectType() );

        assertEqualsIgnoreWhitespace( "if ( a == b ) { " + "  assert( foo3 );" + "} else {" + "  retract( foo4 );" + "}" + "  System.out.println( a4 );",
                                      rule.getConsequence() );

        assertFalse( this.parser.hasErrors() );
    }

    public void testNotNode() throws Exception {
        final RuleDescr rule = parseResource( "rule_not.drl" ).rule();

        assertNotNull( rule );
        assertEquals( "simple_rule",
                      rule.getName() );

        final AndDescr lhs = rule.getLhs();
        assertEquals( 1,
                      lhs.getDescrs().size() );
        final NotDescr not = (NotDescr) lhs.getDescrs().get( 0 );
        assertEquals( 1,
                      not.getDescrs().size() );
        final ColumnDescr col = (ColumnDescr) not.getDescrs().get( 0 );

        assertEquals( "Cheese",
                      col.getObjectType() );
        assertEquals( 1,
                      col.getDescrs().size() );
        
        
        FieldConstraintDescr fld = (FieldConstraintDescr) col.getDescrs().get( 0 );
        final LiteralRestrictionDescr lit = (LiteralRestrictionDescr) fld.getRestrictions().get(0);
        
        assertEquals( "==",
                      lit.getEvaluator() );
        assertEquals( "stilton",
                      lit.getText() );
        assertEquals( "type",
                      fld.getFieldName() );

        assertFalse( this.parser.hasErrors() );
    }
    
    public void testFunctionImport() throws Exception {
        final DRLParser parser = parseResource( "test_FunctionImport.drl" );
        parser.compilation_unit();
        assertFalse(parser.hasErrors());
        
        PackageDescr pkg = parser.getPackageDescr();
        assertEquals(2, pkg.getFunctionImports().size());
        
        assertEquals("abd.def.x", pkg.getFunctionImports().get( 0 ));
        assertEquals("qed.wah.*", pkg.getFunctionImports().get( 1 ));
        
        
    }

    public void testNotExistWithBrackets() throws Exception {

        final DRLParser parser = parseResource( "not_exist_with_brackets.drl" );

        parser.compilation_unit();
        final PackageDescr pkg = parser.getPackageDescr();

        final RuleDescr rule = (RuleDescr) pkg.getRules().get( 0 );

        assertNotNull( rule );
        assertEquals( "simple_rule",
                      rule.getName() );

        final AndDescr lhs = rule.getLhs();
        assertEquals( 2,
                      lhs.getDescrs().size() );
        final NotDescr not = (NotDescr) lhs.getDescrs().get( 0 );
        assertEquals( 1,
                      not.getDescrs().size() );
        final ColumnDescr col = (ColumnDescr) not.getDescrs().get( 0 );

        assertEquals( "Cheese",
                      col.getObjectType() );

        final ExistsDescr ex = (ExistsDescr) lhs.getDescrs().get( 1 );
        assertEquals( 1,
                      ex.getDescrs().size() );
        final ColumnDescr exCol = (ColumnDescr) ex.getDescrs().get( 0 );
        assertEquals( "Foo",
                      exCol.getObjectType() );

        assertFalse( parser.hasErrors() );
    }

    public void testNotBindindShouldBarf() throws Exception {
        final DRLParser parser = parseResource( "not_with_binding_error.drl" );
        parser.compilation_unit();
        assertTrue( parser.hasErrors() );
    }

    public void testSimpleQuery() throws Exception {
        final QueryDescr query = parseResource( "simple_query.drl" ).query();

        assertNotNull( query );

        assertEquals( "simple_query",
                      query.getName() );

        final AndDescr lhs = query.getLhs();

        assertNotNull( lhs );

        assertEquals( 3,
                      lhs.getDescrs().size() );

        // Check first column
        final ColumnDescr first = (ColumnDescr) lhs.getDescrs().get( 0 );
        assertEquals( "foo3",
                      first.getIdentifier() );
        assertEquals( "Bar",
                      first.getObjectType() );

        assertEquals( 1,
                      first.getDescrs().size() );

        FieldConstraintDescr fld = (FieldConstraintDescr) first.getDescrs().get( 0 );
        LiteralRestrictionDescr constraint = (LiteralRestrictionDescr) fld.getRestrictions().get(0);
        //LiteralDescr constraint = (LiteralDescr) first.getDescrs().get( 0 );

        assertNotNull( constraint );

        assertEquals( "a",
                      fld.getFieldName() );
        assertEquals( "==",
                      constraint.getEvaluator() );
        assertEquals( "3",
                      constraint.getText() );

        // Check second column
        final ColumnDescr second = (ColumnDescr) lhs.getDescrs().get( 1 );
        assertEquals( "foo4",
                      second.getIdentifier() );
        assertEquals( "Bar",
                      second.getObjectType() );

        assertEquals( 2,
                      second.getDescrs().size() );
        //check it has field bindings.
        final FieldBindingDescr fieldBindingDescr = (FieldBindingDescr) second.getDescrs().get( 0 );
        assertEquals( "a",
                      fieldBindingDescr.getFieldName() );
        assertEquals( "a4",
                      fieldBindingDescr.getIdentifier() );

        fld = (FieldConstraintDescr) second.getDescrs().get( 1 );
        
        constraint = (LiteralRestrictionDescr) fld.getRestrictions().get(0);

        assertNotNull( constraint );

        assertEquals( "a",
                      fld.getFieldName() );
        assertEquals( "==",
                      constraint.getEvaluator() );
        assertEquals( "4",
                      constraint.getText() );

        assertFalse( this.parser.hasErrors() );
    }

    public void testQueryRuleMixed() throws Exception {
        final DRLParser parser = parseResource( "query_and_rule.drl" );
        parser.compilation_unit();

        final PackageDescr pack = parser.getPackageDescr();
        assertEquals( 4,
                      pack.getRules().size() ); //as queries are rules
        RuleDescr rule = (RuleDescr) pack.getRules().get( 0 );
        assertEquals( "bar",
                      rule.getName() );

        QueryDescr query = (QueryDescr) pack.getRules().get( 1 );
        assertEquals( "simple_query",
                      query.getName() );

        rule = (RuleDescr) pack.getRules().get( 2 );
        assertEquals( "bar2",
                      rule.getName() );

        query = (QueryDescr) pack.getRules().get( 3 );
        assertEquals( "simple_query2",
                      query.getName() );

        assertFalse( parser.hasErrors() );
    }

    public void testMultipleRules() throws Exception {
        final DRLParser parser = parseResource( "multiple_rules.drl" );
        parser.compilation_unit();

        final PackageDescr pkg = parser.getPackageDescr();
        final List rules = pkg.getRules();

        assertEquals( 2,
                      rules.size() );

        final RuleDescr rule0 = (RuleDescr) rules.get( 0 );
        assertEquals( "Like Stilton",
                      rule0.getName() );

        final RuleDescr rule1 = (RuleDescr) rules.get( 1 );
        assertEquals( "Like Cheddar",
                      rule1.getName() );

        //checkout the first rule
        AndDescr lhs = rule1.getLhs();
        assertNotNull( lhs );
        assertEquals( 1,
                      lhs.getDescrs().size() );
        assertEqualsIgnoreWhitespace( "System.out.println(\"I like \" + t);",
                                      rule0.getConsequence() );

        // Check first column
        ColumnDescr first = (ColumnDescr) lhs.getDescrs().get( 0 );
        assertEquals( "Cheese",
                      first.getObjectType() );

        //checkout the second rule
        lhs = rule1.getLhs();
        assertNotNull( lhs );
        assertEquals( 1,
                      lhs.getDescrs().size() );
        assertEqualsIgnoreWhitespace( "System.out.println(\"I like \" + t);",
                                      rule1.getConsequence() );

        // Check first column
        first = (ColumnDescr) lhs.getDescrs().get( 0 );
        assertEquals( "Cheese",
                      first.getObjectType() );

        assertFalse( parser.hasErrors() );
    }

    public void FIXME_testSimpleExpander() throws Exception {
        final DRLParser parser = parseResource( "simple_expander.drl" );
        final MockExpanderResolver mockExpanderResolver = new MockExpanderResolver();
        parser.setExpanderResolver( mockExpanderResolver );
        parser.compilation_unit();
        final PackageDescr pack = parser.getPackageDescr();
        if(parser.hasErrors()) {
            System.err.println(parser.getErrorMessages());
        }
        assertNotNull( pack );
        assertEquals( 1,
                      pack.getRules().size() );

        assertTrue( mockExpanderResolver.checkCalled( "foo.dsl" ) );
        final RuleDescr rule = (RuleDescr) pack.getRules().get( 0 );
        assertEquals( "simple_rule",
                      rule.getName() );

        //now check out the LHS
        assertEquals( 4,
                      rule.getLhs().getDescrs().size() );

        //The rain in spain ... ----> foo : Bar(a==3) (via MockExpander)
        ColumnDescr col = (ColumnDescr) rule.getLhs().getDescrs().get( 0 );
        assertEquals( "Bar",
                      col.getObjectType() );
        assertEquals( "foo1",
                      col.getIdentifier() );
        assertEquals( 1,
                      col.getDescrs().size() );
        assertEquals( 6,
                      col.getLine() );

        FieldConstraintDescr fld = (FieldConstraintDescr) col.getDescrs().get( 0 );
        LiteralRestrictionDescr lit = (LiteralRestrictionDescr) fld.getRestrictions().get(0);
        
        assertEquals( "==",
                      lit.getEvaluator() );
        assertEquals( "a",
                      fld.getFieldName() );
        assertEquals( "1",
                      lit.getText() );

        //>Baz() --> not expanded, as it has the magical escape character '>' !!
        col = (ColumnDescr) rule.getLhs().getDescrs().get( 1 );
        assertEquals( "Baz",
                      col.getObjectType() );
        assertEquals( 7,
                      col.getLine() );

        //The rain in spain ... ----> foo : Bar(a==3) (via MockExpander), again...
        col = (ColumnDescr) rule.getLhs().getDescrs().get( 2 );
        assertEquals( "Bar",
                      col.getObjectType() );
        assertEquals( "foo2",
                      col.getIdentifier() );
        assertEquals( 1,
                      col.getDescrs().size() );
        assertEquals( 8,
                      col.getLine() );
        
        fld = (FieldConstraintDescr) col.getDescrs().get( 0 );
        lit = (LiteralRestrictionDescr) fld.getRestrictions().get(0);
        
        assertEquals( "==",
                      lit.getEvaluator() );
        assertEquals( "a",
                      fld.getFieldName() );
        assertEquals( "2",
                      lit.getText() );

        //>Bar() --> not expanded, as it has the magical escape character '>' !!
        col = (ColumnDescr) rule.getLhs().getDescrs().get( 3 );
        assertEquals( "Bar",
                      col.getObjectType() );
        assertEquals( 9,
                      col.getLine() );

        assertEqualsIgnoreWhitespace( "bar foo3 : Bar(a==3) baz foo4 : Bar(a==4)",
                                      rule.getConsequence() );
        assertTrue( mockExpanderResolver.checkExpanded( "when,The rain in spain falls mainly" ) );
        assertTrue( mockExpanderResolver.checkExpanded( "then,Something else" ) );
        assertTrue( mockExpanderResolver.checkExpanded( "then,Hey dude" ) );

        assertFalse( parser.hasErrors() );
    }

    public void FIXME_testExpanderErrorsAfterExpansion() throws Exception {

        final ExpanderResolver res = new ExpanderResolver() {
            public Expander get(String name,
                                String config) {
                return new Expander() {
                    public String expand(String scope,
                                         String pattern) {
                        return pattern;
                    }
                };
            }
        };

        final DRLParser parser = parseResource( "expander_post_errors.drl" );
        parser.setExpanderResolver( res );
        parser.compilation_unit();
        assertTrue( parser.hasErrors() );
        
        RecognitionException err = (RecognitionException) parser.getErrors().get( 0 );
        //System.err.println(parser.getErrorMessages());
        assertEquals(2, parser.getErrors().size());
        
        assertEquals( 6,
                      err.line );
        err = (RecognitionException) parser.getErrors().get(1);
        assertEquals( 9,
                err.line );
        
    }
    
    public void FIXME_testExpanderLineSpread() throws Exception {

        final DRLParser parser = parseResource( "expander_spread_lines.drl" );
        final DefaultExpanderResolver res = new DefaultExpanderResolver( new InputStreamReader( this.getClass().getResourceAsStream( "complex.dsl" ) ) );
        parser.setExpanderResolver( res );
        parser.setExpanderDebug( true );
        parser.compilation_unit();
        if(parser.hasErrors()) {
            System.err.println(parser.getErrorMessages());
        }
        
        //        List errorMessages = parser.getErrorMessages();
        //        for ( Iterator iter = errorMessages.iterator(); iter.hasNext(); ) {
        //            String element = (String) iter.next();
        //            System.out.println(element);
        //            
        //        }        

        assertFalse( parser.hasErrors() );

        final PackageDescr pkg = parser.getPackageDescr();
        final RuleDescr rule = (RuleDescr) pkg.getRules().get( 0 );
        assertEquals( 1,
                      rule.getLhs().getDescrs().size() );

        final OrDescr or = (OrDescr) rule.getLhs().getDescrs().get( 0 );
        assertEquals( 2,
                      or.getDescrs().size() );
        assertNotNull( rule.getConsequence() );

    }
    
    public void FIXME_testExpanderMultipleConstraints() throws Exception {

        final DRLParser parser = parseResource( "expander_multiple_constraints.drl" );
        final DefaultExpanderResolver res = new DefaultExpanderResolver( new InputStreamReader( 
        		this.getClass().getResourceAsStream( "multiple_constraints.dsl" ) ) );
        parser.setExpanderResolver( res );
        parser.setExpanderDebug( true );
        parser.compilation_unit();
      
        if(parser.hasErrors()) {
            System.err.println(parser.getErrorMessages());
        }

        assertFalse( parser.hasErrors() );
        
        final PackageDescr pkg = parser.getPackageDescr();
        final RuleDescr rule = (RuleDescr) pkg.getRules().get( 0 );
        assertEquals(2, rule.getLhs().getDescrs().size());

        ColumnDescr col = (ColumnDescr) rule.getLhs().getDescrs().get(0);
        assertEquals("Person", col.getObjectType());
        
        
        assertEquals(2, col.getDescrs().size());
        assertEquals("age", ((FieldConstraintDescr) col.getDescrs().get(0)).getFieldName());                
        assertEquals("location", ((FieldConstraintDescr) col.getDescrs().get(1)).getFieldName());
        
        col = (ColumnDescr) rule.getLhs().getDescrs().get(1);
        assertEquals("Bar", col.getObjectType());
        
        assertNotNull( rule.getConsequence() );

    }    
    
    public void FIXME_testExpanderMultipleConstraintsFlush() throws Exception {
    	//this is similar to the other test, but it requires a flush to add the constraints
        final DRLParser parser = parseResource( "expander_multiple_constraints_flush.drl" );
        final DefaultExpanderResolver res = new DefaultExpanderResolver( new InputStreamReader( 
        		this.getClass().getResourceAsStream( "multiple_constraints.dsl" ) ) );
        parser.setExpanderResolver( res );
        parser.setExpanderDebug( true );
        parser.compilation_unit();
      

        assertFalse( parser.hasErrors() );
        
        final PackageDescr pkg = parser.getPackageDescr();
        final RuleDescr rule = (RuleDescr) pkg.getRules().get( 0 );
        assertEquals(1, rule.getLhs().getDescrs().size());

        ColumnDescr col = (ColumnDescr) rule.getLhs().getDescrs().get(0);
        assertEquals("Person", col.getObjectType());        
        
        assertEquals(2, col.getDescrs().size());
        assertEquals("age", ((FieldConstraintDescr) col.getDescrs().get(0)).getFieldName());                
        assertEquals("location", ((FieldConstraintDescr) col.getDescrs().get(1)).getFieldName());
        
        assertNotNull( rule.getConsequence() );

    }      

    public void FIXME_testExpanderUnExpandableErrorLines() throws Exception {

        //stubb expander
        final ExpanderResolver res = new ExpanderResolver() {
            public Expander get(String name,
                                String config) {
                return new Expander() {
                    public String expand(String scope,
                                         String pattern) {
                        if ( pattern.startsWith( "Good" ) ) {
                            return pattern;
                        } else {
                            throw new IllegalArgumentException( "whoops" );
                        }

                    }
                };
            }
        };

        final DRLParser parser = parseResource( "expander_line_errors.drl" );
        parser.setExpanderResolver( res );
        parser.compilation_unit();
        assertTrue( parser.hasErrors() );

        final List messages = parser.getErrorMessages();
        assertEquals( messages.size(),
                      parser.getErrors().size() );

        assertEquals( 4,
                      parser.getErrors().size() );
        assertEquals( ExpanderException.class,
                      parser.getErrors().get( 0 ).getClass() );
        assertEquals( 8,
                      ((RecognitionException) parser.getErrors().get( 0 )).line );
        assertEquals( 10,
                      ((RecognitionException) parser.getErrors().get( 1 )).line );
        assertEquals( 12,
                      ((RecognitionException) parser.getErrors().get( 2 )).line );
        assertEquals( 13,
                      ((RecognitionException) parser.getErrors().get( 3 )).line );

        final PackageDescr pack = parser.getPackageDescr();
        assertNotNull( pack );

        final ExpanderException ex = (ExpanderException) parser.getErrors().get( 0 );
        assertTrue( ex.getMessage().indexOf( "whoops" ) > -1 );

    }

    public void testBasicBinding() throws Exception {
        final DRLParser parser = parseResource( "basic_binding.drl" );
        parser.compilation_unit();

        final PackageDescr pkg = parser.getPackageDescr();
        final RuleDescr ruleDescr = (RuleDescr) pkg.getRules().get( 0 );

        final AndDescr lhs = ruleDescr.getLhs();
        assertEquals( 1,
                      lhs.getDescrs().size() );
        final ColumnDescr cheese = (ColumnDescr) lhs.getDescrs().get( 0 );
        assertEquals( 1,
                      cheese.getDescrs().size() );
        assertEquals( "Cheese",
                      cheese.getObjectType() );
        assertEquals( 1,
                      lhs.getDescrs().size() );
        final FieldBindingDescr fieldBinding = (FieldBindingDescr) cheese.getDescrs().get( 0 );
        assertEquals( "type",
                      fieldBinding.getFieldName() );

        assertFalse( parser.hasErrors() );
    }

    public void testBoundVariables() throws Exception {
        final DRLParser parser = parseResource( "bindings.drl" );
        parser.compilation_unit();

        final PackageDescr pkg = parser.getPackageDescr();
        final RuleDescr ruleDescr = (RuleDescr) pkg.getRules().get( 0 );

        final AndDescr lhs = ruleDescr.getLhs();
        assertEquals( 2,
                      lhs.getDescrs().size() );
        final ColumnDescr cheese = (ColumnDescr) lhs.getDescrs().get( 0 );
        assertEquals( "Cheese",
                      cheese.getObjectType() );
        assertEquals( 2,
                      lhs.getDescrs().size() );
        FieldBindingDescr fieldBinding = (FieldBindingDescr) cheese.getDescrs().get( 0 );
        assertEquals( "type",
                      fieldBinding.getFieldName() );
        
        FieldConstraintDescr fld = (FieldConstraintDescr) cheese.getDescrs().get( 1 );
        LiteralRestrictionDescr literalDescr = (LiteralRestrictionDescr) fld.getRestrictions().get(0);
        //LiteralDescr literalDescr = (LiteralDescr) cheese.getDescrs().get( 1 );
        assertEquals( "type",
                      fld.getFieldName() );
        assertEquals( "==",
                      literalDescr.getEvaluator() );
        assertEquals( "stilton",
                      literalDescr.getText() );

        final ColumnDescr person = (ColumnDescr) lhs.getDescrs().get( 1 );
        fieldBinding = (FieldBindingDescr) person.getDescrs().get( 0 );
        assertEquals( "name",
                      fieldBinding.getFieldName() );
        
        fld = (FieldConstraintDescr) person.getDescrs().get( 1 );
        literalDescr = (LiteralRestrictionDescr) fld.getRestrictions().get(0);
        
        assertEquals( "name",
                      fld.getFieldName() );
        assertEquals( "==",
                      literalDescr.getEvaluator() );
        assertEquals( "bob",
                      literalDescr.getText() );

        fld = (FieldConstraintDescr) person.getDescrs().get( 2 );
        final VariableRestrictionDescr variableDescr = (VariableRestrictionDescr) fld.getRestrictions().get(0);
        
        assertEquals( "likes",
                      fld.getFieldName() );
        assertEquals( "==",
                      variableDescr.getEvaluator() );
        assertEquals( "$type",
                      variableDescr.getIdentifier() );

        assertFalse( parser.hasErrors() );
    }
    
    public void testOrNesting() throws Exception {
        final DRLParser parser = parseResource( "or_nesting.drl" );
        parser.compilation_unit();

        final PackageDescr pack = parser.getPackageDescr();
        assertNotNull( pack );
        assertEquals( 1,
                      pack.getRules().size() );
        final RuleDescr rule = (RuleDescr) pack.getRules().get( 0 );
        assertEquals( "simple_rule",
                      rule.getName() );

        assertEquals(1, rule.getLhs().getDescrs().size());
        
        OrDescr or = (OrDescr) rule.getLhs().getDescrs().get( 0 );
        assertEquals(2, or.getDescrs().size());
        
        ColumnDescr first = (ColumnDescr) or.getDescrs().get( 0 );
        assertEquals("Person", first.getObjectType());
        
        AndDescr and = (AndDescr) or.getDescrs().get( 1 );
        assertEquals(2, and.getDescrs().size());
        
        ColumnDescr left = (ColumnDescr) and.getDescrs().get( 0 );
        assertEquals("Person", left.getObjectType());
        
        ColumnDescr right = (ColumnDescr) and.getDescrs().get( 1 );
        assertEquals("Cheese", right.getObjectType());
    }

    /** Test that explicit "&&", "||" works as expected */
    public void testAndOrRules() throws Exception {
        final DRLParser parser = parseResource( "and_or_rule.drl" );
        parser.compilation_unit();

        final PackageDescr pack = parser.getPackageDescr();
        assertNotNull( pack );
        assertEquals( 1,
                      pack.getRules().size() );
        final RuleDescr rule = (RuleDescr) pack.getRules().get( 0 );
        assertEquals( "simple_rule",
                      rule.getName() );

        //we will have 2 children under the main And node
        final AndDescr and = rule.getLhs();
        assertEquals( 2,
                      and.getDescrs().size() );

        //check the "&&" part
        final AndDescr join = (AndDescr) and.getDescrs().get( 0 );
        assertEquals( 2,
                      join.getDescrs().size() );

        ColumnDescr left = (ColumnDescr) join.getDescrs().get( 0 );
        ColumnDescr right = (ColumnDescr) join.getDescrs().get( 1 );
        assertEquals( "Person",
                      left.getObjectType() );
        assertEquals( "Cheese",
                      right.getObjectType() );

        assertEquals( 1,
                      left.getDescrs().size() );
        
        FieldConstraintDescr fld = (FieldConstraintDescr) left.getDescrs().get( 0 );
        LiteralRestrictionDescr literal = (LiteralRestrictionDescr) fld.getRestrictions().get(0);
        
        assertEquals( "==",
                      literal.getEvaluator() );
        assertEquals( "name",
                      fld.getFieldName() );
        assertEquals( "mark",
                      literal.getText() );

        assertEquals( 1,
                      right.getDescrs().size() );
        
        fld = (FieldConstraintDescr) right.getDescrs().get( 0 );
        literal = (LiteralRestrictionDescr) fld.getRestrictions().get(0);
        
        assertEquals( "==",
                      literal.getEvaluator() );
        assertEquals( "type",
                      fld.getFieldName() );
        assertEquals( "stilton",
                      literal.getText() );

        //now the "||" part
        final OrDescr or = (OrDescr) and.getDescrs().get( 1 );
        assertEquals( 2,
                      or.getDescrs().size() );
        left = (ColumnDescr) or.getDescrs().get( 0 );
        right = (ColumnDescr) or.getDescrs().get( 1 );
        assertEquals( "Person",
                      left.getObjectType() );
        assertEquals( "Cheese",
                      right.getObjectType() );
        assertEquals( 1,
                      left.getDescrs().size() );
        
        fld = (FieldConstraintDescr) left.getDescrs().get( 0 );
        literal = (LiteralRestrictionDescr) fld.getRestrictions().get(0);
        
        
        assertEquals( "==",
                      literal.getEvaluator() );
        assertEquals( "name",
                      fld.getFieldName() );
        assertEquals( "mark",
                      literal.getText() );

        assertEquals( 1,
                      right.getDescrs().size() );
        
        fld = (FieldConstraintDescr) right.getDescrs().get( 0 );
        literal = (LiteralRestrictionDescr) fld.getRestrictions().get(0);
        
        assertEquals( "==",
                      literal.getEvaluator() );
        assertEquals( "type",
                      fld.getFieldName() );
        assertEquals( "stilton",
                      literal.getText() );

        assertEqualsIgnoreWhitespace( "System.out.println( \"Mark and Michael\" );",
                                      rule.getConsequence() );

        assertFalse( parser.hasErrors() );
    }

    /** test basic foo : Fact() || Fact() stuff */
    public void testOrWithBinding() throws Exception {
        final DRLParser parser = parseResource( "or_binding.drl" );
        parser.compilation_unit();

        final PackageDescr pack = parser.getPackageDescr();
        assertEquals( 1,
                      pack.getRules().size() );
        final RuleDescr rule = (RuleDescr) pack.getRules().get( 0 );
        assertEquals( 2,
                      rule.getLhs().getDescrs().size() );

        final OrDescr or = (OrDescr) rule.getLhs().getDescrs().get( 0 );
        assertEquals( 2,
                      or.getDescrs().size() );

        final ColumnDescr leftCol = (ColumnDescr) or.getDescrs().get( 0 );
        assertEquals( "Person",
                      leftCol.getObjectType() );
        assertEquals( "foo",
                      leftCol.getIdentifier() );
        
        final ColumnDescr rightCol = (ColumnDescr) or.getDescrs().get( 1 );
        assertEquals( "Person",
                      rightCol.getObjectType() );
        assertEquals( "foo",
                      rightCol.getIdentifier() );
        
        final ColumnDescr cheeseDescr = (ColumnDescr) rule.getLhs().getDescrs().get( 1 );
        assertEquals("Cheese", cheeseDescr.getObjectType());
        assertEquals(null, cheeseDescr.getIdentifier());


        assertEqualsIgnoreWhitespace( "System.out.println( \"Mark and Michael\" + bar );",
                                      rule.getConsequence() );

        assertFalse( parser.hasErrors() );
    }

    /** test basic foo : Fact() || Fact() stuff binding to an "or"*/
    public void testOrBindingComplex() throws Exception {
        final DRLParser parser = parseResource( "or_binding_complex.drl" );
        parser.compilation_unit();

        final PackageDescr pack = parser.getPackageDescr();
        assertEquals( 1,
                      pack.getRules().size() );
        final RuleDescr rule = (RuleDescr) pack.getRules().get( 0 );
        assertEquals( 1,
                      rule.getLhs().getDescrs().size() );

        assertEquals( 1,
                      rule.getLhs().getDescrs().size() );

        final OrDescr or = (OrDescr) rule.getLhs().getDescrs().get( 0 );
        assertEquals( 2,
                      or.getDescrs().size() );

        //first fact
        final ColumnDescr firstFact = (ColumnDescr) or.getDescrs().get( 0 );
        assertEquals( "Person",
                      firstFact.getObjectType() );
        assertEquals( "foo",
                      firstFact.getIdentifier() );

        //second "option"
        final ColumnDescr secondFact = (ColumnDescr) or.getDescrs().get( 1 );
        assertEquals( "Person",
                      secondFact.getObjectType() );
        assertEquals(1, secondFact.getDescrs().size());
        assertEquals("foo", secondFact.getIdentifier());
        
        assertEqualsIgnoreWhitespace( "System.out.println( \"Mark and Michael\" + bar );",
                                      rule.getConsequence() );

        assertFalse( parser.hasErrors() );
    }

    public void testOrBindingWithBrackets() throws Exception {
        final DRLParser parser = parseResource( "or_binding_with_brackets.drl" );
        parser.compilation_unit();

        final PackageDescr pack = parser.getPackageDescr();
        assertEquals( 1,
                      pack.getRules().size() );
        final RuleDescr rule = (RuleDescr) pack.getRules().get( 0 );
        assertEquals( 1,
                      rule.getLhs().getDescrs().size() );

        assertEquals( 1,
                      rule.getLhs().getDescrs().size() );

        final OrDescr or = (OrDescr) rule.getLhs().getDescrs().get( 0 );
        assertEquals( 2,
                      or.getDescrs().size() );

        //first fact
        final ColumnDescr firstFact = (ColumnDescr) or.getDescrs().get( 0 );
        assertEquals( "Person",
                      firstFact.getObjectType() );
        assertEquals( "foo",
                      firstFact.getIdentifier() );

        //second "option"
        final ColumnDescr secondFact = (ColumnDescr) or.getDescrs().get( 0 );
        assertEquals( "Person",
                      secondFact.getObjectType() );
        assertEquals( "foo",
                      secondFact.getIdentifier() );

        assertEqualsIgnoreWhitespace( "System.out.println( \"Mark and Michael\" + bar );",
                                      rule.getConsequence() );

        assertFalse( parser.hasErrors() );
    }

    /** */
    public void testBracketsPrecedence() throws Exception {
        final DRLParser parser = parseResource( "brackets_precedence.drl" );
        parser.compilation_unit();

        final PackageDescr pack = parser.getPackageDescr();
        assertEquals( 1,
                      pack.getRules().size() );
        final RuleDescr rule = (RuleDescr) pack.getRules().get( 0 );

        assertEquals( 1,
                      rule.getLhs().getDescrs().size() );

        final AndDescr rootAnd = (AndDescr) rule.getLhs().getDescrs().get( 0 );

        assertEquals( 2,
                      rootAnd.getDescrs().size() );

        final OrDescr leftOr = (OrDescr) rootAnd.getDescrs().get( 0 );

        assertEquals( 2,
                      leftOr.getDescrs().size() );
        final NotDescr not = (NotDescr) leftOr.getDescrs().get( 0 );
        final ColumnDescr foo1 = (ColumnDescr) not.getDescrs().get( 0 );
        assertEquals( "Foo",
                      foo1.getObjectType() );
        final ColumnDescr foo2 = (ColumnDescr) leftOr.getDescrs().get( 1 );
        assertEquals( "Foo",
                      foo2.getObjectType() );

        final OrDescr rightOr = (OrDescr) rootAnd.getDescrs().get( 1 );

        assertEquals( 2,
                      rightOr.getDescrs().size() );
        final ColumnDescr shoes = (ColumnDescr) rightOr.getDescrs().get( 0 );
        assertEquals( "Shoes",
                      shoes.getObjectType() );
        final ColumnDescr butt = (ColumnDescr) rightOr.getDescrs().get( 1 );
        assertEquals( "Butt",
                      butt.getObjectType() );

        assertFalse( parser.hasErrors() );
    }

    public void testEvalMultiple() throws Exception {
        final DRLParser parser = parseResource( "eval_multiple.drl" );
        parser.compilation_unit();

        assertFalse( parser.getErrorMessages().toString(), parser.hasErrors() );

        final PackageDescr pack = parser.getPackageDescr();
        assertEquals( 1,
                      pack.getRules().size() );
        final RuleDescr rule = (RuleDescr) pack.getRules().get( 0 );
        assertEquals( 4,
                      rule.getLhs().getDescrs().size() );

        final EvalDescr eval = (EvalDescr) rule.getLhs().getDescrs().get( 0 );
        assertEqualsIgnoreWhitespace( "abc(\"foo\") + 5",
                                      eval.getText() );

        final ColumnDescr col = (ColumnDescr) rule.getLhs().getDescrs().get( 1 );
        assertEquals( "Foo",
                      col.getObjectType() );

    }

    public void testWithEval() throws Exception {
        final DRLParser parser = parseResource( "with_eval.drl" );
        parser.compilation_unit();

        final PackageDescr pack = parser.getPackageDescr();
        assertEquals( 1,
                      pack.getRules().size() );
        final RuleDescr rule = (RuleDescr) pack.getRules().get( 0 );
        assertEquals( 3,
                      rule.getLhs().getDescrs().size() );
        ColumnDescr col = (ColumnDescr) rule.getLhs().getDescrs().get( 0 );
        assertEquals( "Foo",
                      col.getObjectType() );
        col = (ColumnDescr) rule.getLhs().getDescrs().get( 1 );
        assertEquals( "Bar",
                      col.getObjectType() );

        final EvalDescr eval = (EvalDescr) rule.getLhs().getDescrs().get( 2 );
        assertEqualsIgnoreWhitespace( "abc(\"foo\")",
                                      eval.getText() );
        assertEqualsIgnoreWhitespace( "Kapow",
                                      rule.getConsequence() );

        assertFalse( parser.hasErrors() );
    }

    public void testWithRetval() throws Exception {
        final DRLParser parser = parseResource( "with_retval.drl" );
        parser.compilation_unit();

        final PackageDescr pack = parser.getPackageDescr();
        assertEquals( 1,
                      pack.getRules().size() );

        final RuleDescr rule = (RuleDescr) pack.getRules().get( 0 );
        assertEquals( 1,
                      rule.getLhs().getDescrs().size() );
        final ColumnDescr col = (ColumnDescr) rule.getLhs().getDescrs().get( 0 );
        assertEquals( 1,
                      col.getDescrs().size() );
        assertEquals( "Foo",
                      col.getObjectType() );
        FieldConstraintDescr fld = (FieldConstraintDescr) col.getDescrs().get( 0 );
        ReturnValueRestrictionDescr retval = (ReturnValueRestrictionDescr) fld.getRestrictions().get(0);
        
        
        assertEquals( "a + b",
                      retval.getText() );
        assertEquals( "name",
                      fld.getFieldName() );
        assertEquals( "==",
                      retval.getEvaluator() );

        assertFalse( parser.hasErrors() );
    }

    public void testWithPredicate() throws Exception {
        final DRLParser parser = parseResource( "with_predicate.drl" );
        parser.compilation_unit();

        final PackageDescr pack = parser.getPackageDescr();
        assertEquals( 1,
                      pack.getRules().size() );

        final RuleDescr rule = (RuleDescr) pack.getRules().get( 0 );
        assertEquals( 1,
                      rule.getLhs().getDescrs().size() );
        final ColumnDescr col = (ColumnDescr) rule.getLhs().getDescrs().get( 0 );
        assertEquals( 1,
                      col.getDescrs().size() );

        final PredicateDescr pred = (PredicateDescr) col.getDescrs().get( 0 );
        assertEquals( "age",
                      pred.getFieldName() );
        assertEquals( "$age2",
                      pred.getDeclaration() );
        assertEqualsIgnoreWhitespace( "$age2 == $age1+2",
                                      pred.getText() );

        assertFalse( parser.getErrorMessages().toString(), parser.hasErrors() );
    }

    public void testNotWithConstraint() throws Exception {
        final DRLParser parser = parseResource( "not_with_constraint.drl" );
        parser.compilation_unit();

        final PackageDescr pack = parser.getPackageDescr();
        assertEquals( 1,
                      pack.getRules().size() );

        final RuleDescr rule = (RuleDescr) pack.getRules().get( 0 );
        assertEquals( 2,
                      rule.getLhs().getDescrs().size() );

        ColumnDescr column = (ColumnDescr) rule.getLhs().getDescrs().get( 0 );
        final FieldBindingDescr fieldBinding = (FieldBindingDescr) column.getDescrs().get( 0 );
        assertEquals( "$likes",
                      fieldBinding.getIdentifier() );

        final NotDescr not = (NotDescr) rule.getLhs().getDescrs().get( 1 );
        column = (ColumnDescr) not.getDescrs().get( 0 );
        
        FieldConstraintDescr fld = (FieldConstraintDescr) column.getDescrs().get( 0 );
        final VariableRestrictionDescr boundVariable = (VariableRestrictionDescr) fld.getRestrictions().get(0);
        
        assertEquals( "type",
                      fld.getFieldName() );
        assertEquals( "==",
                      boundVariable.getEvaluator() );
        assertEquals( "$likes",
                      boundVariable.getIdentifier() );

        assertFalse( parser.hasErrors() );
    }

    public void testGlobal() throws Exception {
        final DRLParser parser = parseResource( "globals.drl" );
        parser.compilation_unit();

        final PackageDescr pack = parser.getPackageDescr();
        assertEquals( 1,
                      pack.getRules().size() );

        final RuleDescr rule = (RuleDescr) pack.getRules().get( 0 );
        assertEquals( 1,
                      rule.getLhs().getDescrs().size() );

        assertEquals( 1,
                      pack.getImports().size() );
        assertEquals( 2,
                      pack.getGlobals().values().size() );

        assertEquals( "java.lang.String",
                      pack.getGlobals().get( "foo" ) );
        assertEquals( "java.lang.Integer",
                      pack.getGlobals().get( "bar" ) );

        assertFalse( parser.hasErrors() );
    }

    public void testFunctions() throws Exception {
        final DRLParser parser = parseResource( "functions.drl" );
        parser.compilation_unit();

        final PackageDescr pack = parser.getPackageDescr();
        assertEquals( 2,
                      pack.getRules().size() );

        final List functions = pack.getFunctions();
        assertEquals( 2,
                      functions.size() );

        FunctionDescr func = (FunctionDescr) functions.get( 0 );
        assertEquals( "functionA",
                      func.getName() );
        assertEquals( "String",
                      func.getReturnType() );
        assertEquals( 2,
                      func.getParameterNames().size() );
        assertEquals( 2,
                      func.getParameterTypes().size() );
        assertEquals( 4,
                      func.getLine());
        assertEquals( 0,
                      func.getColumn());

        assertEquals( "String",
                      func.getParameterTypes().get( 0 ) );
        assertEquals( "s",
                      func.getParameterNames().get( 0 ) );

        assertEquals( "Integer",
                      func.getParameterTypes().get( 1 ) );
        assertEquals( "i",
                      func.getParameterNames().get( 1 ) );

        assertEqualsIgnoreWhitespace( "foo();",
                                      func.getText() );

        func = (FunctionDescr) functions.get( 1 );
        assertEquals( "functionB",
                      func.getName() );
        assertEqualsIgnoreWhitespace( "bar();",
                                      func.getText() );

        assertFalse( parser.hasErrors() );
    }

    public void testComment() throws Exception {
        final DRLParser parser = parseResource( "comment.drl" );
        parser.compilation_unit();

        final PackageDescr pack = parser.getPackageDescr();
        assertNotNull( pack );

        assertEquals( "foo.bar",
                      pack.getName() );

        assertFalse( parser.getErrorMessages().toString(), parser.hasErrors() );
    }

    public void testAttributes() throws Exception {
        final RuleDescr rule = parseResource( "rule_attributes.drl" ).rule();
        assertEquals( "simple_rule",
                      rule.getName() );
        assertEqualsIgnoreWhitespace( "bar();",
                                      rule.getConsequence() );

        final List attrs = rule.getAttributes();
        assertEquals( 5,
                      attrs.size() );

        AttributeDescr at = (AttributeDescr) attrs.get( 0 );
        assertEquals( "salience",
                      at.getName() );
        assertEquals( "42",
                      at.getValue() );

        at = (AttributeDescr) attrs.get( 1 );
        assertEquals( "agenda-group",
                      at.getName() );
        assertEquals( "my_group",
                      at.getValue() );

        at = (AttributeDescr) attrs.get( 2 );
        assertEquals( "no-loop",
                      at.getName() );
        assertEquals( "true",
                      at.getValue() );

        at = (AttributeDescr) attrs.get( 3 );
        assertEquals( "duration",
                      at.getName() );
        assertEquals( "42",
                      at.getValue() );

        at = (AttributeDescr) attrs.get( 4 );
        assertEquals( "activation-group",
                      at.getName() );
        assertEquals( "my_activation_group",
                      at.getValue() );

        assertFalse( this.parser.hasErrors() );
    }

    public void testAttributes_alternateSyntax() throws Exception {
        final RuleDescr rule = parseResource( "rule_attributes_alt.drl" ).rule();
        assertEquals( "simple_rule",
                      rule.getName() );
        assertEqualsIgnoreWhitespace( "bar();",
                                      rule.getConsequence() );

        final List attrs = rule.getAttributes();
        assertEquals( 5,
                      attrs.size() );

        AttributeDescr at = (AttributeDescr) attrs.get( 0 );
        assertEquals( "salience",
                      at.getName() );
        assertEquals( "42",
                      at.getValue() );

        at = (AttributeDescr) attrs.get( 1 );
        assertEquals( "agenda-group",
                      at.getName() );
        assertEquals( "my_group",
                      at.getValue() );

        at = (AttributeDescr) attrs.get( 2 );
        assertEquals( "no-loop",
                      at.getName() );
        assertEquals( "true",
                      at.getValue() );

        at = (AttributeDescr) attrs.get( 3 );
        assertEquals( "duration",
                      at.getName() );
        assertEquals( "42",
                      at.getValue() );

        at = (AttributeDescr) attrs.get( 4 );
        assertEquals( "activation-group",
                      at.getName() );
        assertEquals( "my_activation_group",
                      at.getValue() );

        assertFalse( this.parser.hasErrors() );
    }

    public void testEnumeration() throws Exception {
        final RuleDescr rule = parseResource( "enumeration.drl" ).rule();
        assertEquals( "simple_rule",
                      rule.getName() );
        assertEquals( 1,
                      rule.getLhs().getDescrs().size() );
        final ColumnDescr col = (ColumnDescr) rule.getLhs().getDescrs().get( 0 );
        assertEquals( "Foo",
                      col.getObjectType() );
        assertEquals( 1,
                      col.getDescrs().size() );
        FieldConstraintDescr fld = (FieldConstraintDescr) col.getDescrs().get( 0 );
        final LiteralRestrictionDescr lit = (LiteralRestrictionDescr) fld.getRestrictions().get(0);
        
        assertEquals( "bar",
                      fld.getFieldName() );
        assertEquals( "==",
                      lit.getEvaluator() );
        assertEquals( "Foo.BAR",
                      lit.getText() );
        assertTrue( lit.isStaticFieldValue() );

        assertFalse( this.parser.hasErrors() );
    }

    public void FIXME_testExpanderBad() throws Exception {
        final DRLParser parser = parseResource( "bad_expander.drl" );
        try {
            parser.compilation_unit();
            fail( "Should have thrown a wobbly." );
        } catch ( final IllegalArgumentException e ) {
            assertNotNull( e.getMessage() );
        }

        assertFalse( parser.hasErrors() );
    }

    public void testInvalidSyntax_Catches() throws Exception {
        parseResource( "invalid_syntax.drl" ).compilation_unit();
        assertTrue( this.parser.hasErrors() );
    }

    public void testMultipleErrors() throws Exception {
        parseResource( "multiple_errors.drl" ).compilation_unit();
        assertTrue( this.parser.hasErrors() );
        assertEquals( 2,
                      this.parser.getErrors().size() );
    }

    public void testExtraLhsNewline() throws Exception {
        parseResource( "extra_lhs_newline.drl" ).compilation_unit();
        assertFalse( this.parser.hasErrors() );
    }

    public void testStatementOrdering1() throws Exception {
        parseResource( "statement_ordering_1.drl" );
        final MockExpanderResolver mockExpanderResolver = new MockExpanderResolver();
        this.parser.setExpanderResolver( mockExpanderResolver );
        this.parser.compilation_unit();

        final PackageDescr pkg = this.parser.getPackageDescr();

        assertEquals( 2,
                      pkg.getRules().size() );

        assertEquals( "foo",
                      ((RuleDescr) pkg.getRules().get( 0 )).getName() );
        assertEquals( "bar",
                      ((RuleDescr) pkg.getRules().get( 1 )).getName() );

        assertEquals( 2,
                      pkg.getFunctions().size() );

        assertEquals( "cheeseIt",
                      ((FunctionDescr) pkg.getFunctions().get( 0 )).getName() );
        assertEquals( "uncheeseIt",
                      ((FunctionDescr) pkg.getFunctions().get( 1 )).getName() );

        assertEquals( 4,
                      pkg.getImports().size() );
        assertEquals( "im.one",
                      pkg.getImports().get( 0 ) );
        assertEquals( "im.two",
                      pkg.getImports().get( 1 ) );
        assertEquals( "im.three",
                      pkg.getImports().get( 2 ) );
        assertEquals( "im.four",
                      pkg.getImports().get( 3 ) );

        assertFalse( this.parser.hasErrors() );
    }

    public void testRuleNamesStartingWithNumbers() throws Exception {
        parseResource( "rule_names_number_prefix.drl" ).compilation_unit();

        assertFalse( this.parser.getErrors().toString(), this.parser.hasErrors() );

        final PackageDescr pkg = this.parser.getPackageDescr();

        assertEquals( 2,
                      pkg.getRules().size() );

        assertEquals( "1. Do Stuff!",
                      ((RuleDescr) pkg.getRules().get( 0 )).getName() );
        assertEquals( "2. Do More Stuff!",
                      ((RuleDescr) pkg.getRules().get( 1 )).getName() );
    }

    public void testEvalWithNewline() throws Exception {
        parseResource( "eval_with_newline.drl" ).compilation_unit();

        if(parser.hasErrors()) {
            System.err.println( parser.getErrorMessages() );
        }
        assertFalse( this.parser.hasErrors() );
    }

    public void testEvalWithSemicolon() throws Exception {
        parseResource( "eval_with_semicolon.drl" ).compilation_unit();

        assertTrue( this.parser.hasErrors() );
        assertEquals( 1,
                      this.parser.getErrorMessages().size() );
        assertTrue( ((String) this.parser.getErrorMessages().get( 0 )).indexOf( "Trailing semi-colon not allowed" ) >= 0 );
    }
    
    public void testEndPosition() throws Exception {
        parseResource( "test_EndPosition.drl" ).compilation_unit();
        RuleDescr rule = (RuleDescr) parser.getPackageDescr().getRules().get( 0 );
        ColumnDescr col = (ColumnDescr) rule.getLhs().getDescrs().get( 0 );
        assertEquals(6, col.getLine());

        
        assertEquals(8, col.getEndLine());
        
    }

    public void testQualifiedClassname() throws Exception {
        parseResource( "qualified_classname.drl" ).compilation_unit();

        assertFalse( this.parser.hasErrors() );

        final PackageDescr pkg = this.parser.getPackageDescr();
        final RuleDescr rule = (RuleDescr) pkg.getRules().get( 0 );

        final ColumnDescr c = (ColumnDescr) rule.getLhs().getDescrs().get( 0 );

        assertEquals( "com.cheeseco.Cheese",
                      c.getObjectType() );
    }

    public void testAccumulate() throws Exception {
        final DRLParser parser = parseResource( "accumulate.drl" );
        parser.compilation_unit();

        if(parser.hasErrors()) {
            System.err.println( parser.getErrorMessages() );
        }

        assertFalse( parser.getErrorMessages().toString(), parser.hasErrors() );

        final PackageDescr pack = parser.getPackageDescr();
        assertEquals( 1,
                      pack.getRules().size() );
        final RuleDescr rule = (RuleDescr) pack.getRules().get( 0 );
        assertEquals( 1,
                      rule.getLhs().getDescrs().size() );

        final AccumulateDescr accum = (AccumulateDescr) rule.getLhs().getDescrs().get( 0 );
        assertEqualsIgnoreWhitespace( "int x = 0 ;",
                                      accum.getInitCode() );
        assertEqualsIgnoreWhitespace( "x++;",
                                      accum.getActionCode() );
        assertEqualsIgnoreWhitespace( "new Integer(x)",
                                      accum.getResultCode() );

        final ColumnDescr col = (ColumnDescr) accum.getSourceColumn();
        assertEquals( "Person",
                      col.getObjectType() );
    }

    public void testAccumulateWithBindings() throws Exception {
        final DRLParser parser = parseResource( "accumulate_with_bindings.drl" );
        parser.compilation_unit();

        assertFalse( parser.getErrorMessages().toString(), parser.hasErrors() );

        final PackageDescr pack = parser.getPackageDescr();
        assertEquals( 1,
                      pack.getRules().size() );
        final RuleDescr rule = (RuleDescr) pack.getRules().get( 0 );
        assertEquals( 1,
                      rule.getLhs().getDescrs().size() );

        final AccumulateDescr accum = (AccumulateDescr) rule.getLhs().getDescrs().get( 0 );
        assertEqualsIgnoreWhitespace( "$counter",
                                      accum.getResultColumn().getIdentifier());
        assertEqualsIgnoreWhitespace( "int x = 0 ;",
                                      accum.getInitCode() );
        assertEqualsIgnoreWhitespace( "x++;",
                                      accum.getActionCode() );
        assertEqualsIgnoreWhitespace( "new Integer(x)",
                                      accum.getResultCode() );

        final ColumnDescr col = (ColumnDescr) accum.getSourceColumn();
        assertEquals( "Person",
                      col.getObjectType() );
    }

    public void testCollect() throws Exception {
        final DRLParser parser = parseResource( "collect.drl" );
        parser.compilation_unit();

        if(parser.hasErrors()) {
            System.err.println( parser.getErrorMessages() );
        }

        assertFalse( parser.hasErrors() );

        final PackageDescr pack = parser.getPackageDescr();
        assertEquals( 1,
                      pack.getRules().size() );
        final RuleDescr rule = (RuleDescr) pack.getRules().get( 0 );
        assertEquals( 1,
                      rule.getLhs().getDescrs().size() );

        final CollectDescr collect = (CollectDescr) rule.getLhs().getDescrs().get( 0 );

        final ColumnDescr col = (ColumnDescr) collect.getSourceColumn();
        assertEquals( "Person",
                      col.getObjectType() );
    }

    public void testPredicate() throws Exception {
        List constraints = new ArrayList();
        parse( "$var : attr -> ( $var.equals(\"xyz\") )" ).predicate(constraints);
        
        assertFalse( this.parser.hasErrors() );

        assertEquals(1, constraints.size());

        PredicateDescr predicate = (PredicateDescr) constraints.get( 0 );
        assertEquals("$var", predicate.getDeclaration());
        assertEquals("attr", predicate.getFieldName());
        assertEquals(" $var.equals(\"xyz\") ", predicate.getText());
        
    }

    private DRLParser parse(final String text) throws Exception {
        this.parser = newParser( newTokenStream( newLexer( newCharStream( text ) ) ) );
        return this.parser;
    }

    private DRLParser parse(final String source,
                             final String text) throws Exception {
        this.parser = newParser( newTokenStream( newLexer( newCharStream( text ) ) ) );
        this.parser.setSource( source );
        return this.parser;
    }

    private DRLParser parseResource(final String name) throws Exception {

//        System.err.println( getClass().getResource( name ) );
        final InputStream in = getClass().getResourceAsStream( name );

        final InputStreamReader reader = new InputStreamReader( in );

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

    private DRLLexer newLexer(final CharStream charStream) {
        return new DRLLexer( charStream );
    }

    private TokenStream newTokenStream(final Lexer lexer) {
        return new SwitchingCommonTokenStream( lexer );
    }

    private DRLParser newParser(final TokenStream tokenStream) {
        final DRLParser p = new DRLParser( tokenStream );
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
    
    private void prettyPrintErrors() {
    	List msgs = this.parser.getErrorMessages();
    	for (Iterator iter = msgs.iterator(); iter.hasNext();) {
			String err = (String) iter.next();
			System.out.println(err);
			
		}
    }
    
}