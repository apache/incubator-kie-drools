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
import java.io.StringReader;
import java.util.Iterator;
import java.util.List;

import junit.framework.TestCase;

import org.antlr.runtime.ANTLRStringStream;
import org.antlr.runtime.CharStream;
import org.antlr.runtime.CommonToken;
import org.antlr.runtime.CommonTokenStream;
import org.antlr.runtime.Lexer;
import org.antlr.runtime.MismatchedTokenException;
import org.antlr.runtime.NoViableAltException;
import org.antlr.runtime.RecognitionException;
import org.antlr.runtime.TokenStream;
import org.drools.compiler.DrlParser;
import org.drools.lang.DRLParser.paren_chunk_return;
import org.drools.lang.descr.AccessorDescr;
import org.drools.lang.descr.AccumulateDescr;
import org.drools.lang.descr.AndDescr;
import org.drools.lang.descr.AttributeDescr;
import org.drools.lang.descr.CollectDescr;
import org.drools.lang.descr.EvalDescr;
import org.drools.lang.descr.ExistsDescr;
import org.drools.lang.descr.FactTemplateDescr;
import org.drools.lang.descr.FieldAccessDescr;
import org.drools.lang.descr.FieldBindingDescr;
import org.drools.lang.descr.FieldConstraintDescr;
import org.drools.lang.descr.FieldTemplateDescr;
import org.drools.lang.descr.ForallDescr;
import org.drools.lang.descr.FromDescr;
import org.drools.lang.descr.FunctionDescr;
import org.drools.lang.descr.FunctionImportDescr;
import org.drools.lang.descr.GlobalDescr;
import org.drools.lang.descr.ImportDescr;
import org.drools.lang.descr.LiteralRestrictionDescr;
import org.drools.lang.descr.NotDescr;
import org.drools.lang.descr.OrDescr;
import org.drools.lang.descr.PackageDescr;
import org.drools.lang.descr.PatternDescr;
import org.drools.lang.descr.PredicateDescr;
import org.drools.lang.descr.QualifiedIdentifierRestrictionDescr;
import org.drools.lang.descr.QueryDescr;
import org.drools.lang.descr.RestrictionConnectiveDescr;
import org.drools.lang.descr.ReturnValueRestrictionDescr;
import org.drools.lang.descr.RuleDescr;
import org.drools.lang.descr.VariableRestrictionDescr;
import org.drools.lang.dsl.DefaultExpander;

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

    public void xxxtestPackage_OneSegment() throws Exception {
        final String packageName = parse( "package foo" ).package_statement();
        assertEquals( "foo",
                      packageName );
        assertFalse( this.parser.hasErrors() );
    }

    public void xxxtestPackage_MultipleSegments() throws Exception {
        final String packageName = parse( "package foo.bar.baz;" ).package_statement();
        assertEquals( "foo.bar.baz",
                      packageName );
        assertFalse( this.parser.hasErrors() );
    }

    public void xxxtestEmptyPackage() throws Exception {
        final String source = "package foo.bar.baz";
        final DrlParser parser = new DrlParser();
        final PackageDescr pkg = parser.parse( new StringReader( source ) );
        assertFalse( parser.hasErrors() );
        assertEquals( "foo.bar.baz",
                      pkg.getName() );
    }

    public void xxxtestCompilationUnit() throws Exception {
        final String source = "package foo; import com.foo.Bar; import com.foo.Baz;";
        parse( source ).compilation_unit();
        assertEquals( "foo",
                      this.parser.getPackageDescr().getName() );
        assertEquals( 2,
                      this.parser.getPackageDescr().getImports().size() );
        ImportDescr impdescr = (ImportDescr) this.parser.getPackageDescr().getImports().get( 0 );
        assertEquals( "com.foo.Bar",
                      impdescr.getTarget() );
        assertEquals( source.indexOf( "import " + impdescr.getTarget() ),
                      impdescr.getStartCharacter() );
        assertEquals( source.indexOf( "import " + impdescr.getTarget() ) + ("import " + impdescr.getTarget()).length() - 1,
                      impdescr.getEndCharacter() );

        impdescr = (ImportDescr) this.parser.getPackageDescr().getImports().get( 1 );
        assertEquals( "com.foo.Baz",
                      impdescr.getTarget() );
        assertEquals( source.indexOf( "import " + impdescr.getTarget() ),
                      impdescr.getStartCharacter() );
        assertEquals( source.indexOf( "import " + impdescr.getTarget() ) + ("import " + impdescr.getTarget()).length() - 1,
                      impdescr.getEndCharacter() );

        assertFalse( this.parser.hasErrors() );
    }

    public void xxxtestEmptyRule() throws Exception {
        final RuleDescr rule = parseResource( "empty_rule.drl" ).rule();

        assertNotNull( rule );

        assertEquals( "empty",
                      rule.getName() );
        assertNotNull( rule.getLhs() );
        assertNotNull( rule.getConsequence() );

        assertFalse( this.parser.getErrorMessages().toString(),
                     this.parser.hasErrors() );
    }

    public void xxxtestKeywordCollisions() throws Exception {
        final DRLParser parser = parseResource( "eol_funny_business.drl" );

        parser.compilation_unit();
        final PackageDescr pkg = parser.getPackageDescr();

        assertEquals( 1,
                      pkg.getRules().size() );

        assertFalse( parser.getErrors().toString(),
                     parser.hasErrors() );

    }

    public void xxxtestPartialAST() throws Exception {
        parseResource( "pattern_partial.drl" );

        this.parser.compilation_unit();

        assertTrue( this.parser.hasErrors() );

        final PackageDescr pkg = this.parser.getPackageDescr();
        assertEquals( 1,
                      pkg.getRules().size() );
        final RuleDescr rule = (RuleDescr) pkg.getRules().get( 0 );

        assertEquals( 1,
                      rule.getLhs().getDescrs().size() );
        final PatternDescr pattern = (PatternDescr) rule.getLhs().getDescrs().get( 0 );

        assertNotNull( pattern );
        assertEquals( "Bar",
                      pattern.getObjectType() );
        assertEquals( "foo3",
                      pattern.getIdentifier() );

    }

    public void xxxtestTemplates() throws Exception {

        final DRLParser parser = parseResource( "test_Templates.drl" );

        parser.compilation_unit();
        final PackageDescr pkg = parser.getPackageDescr();

        if ( parser.hasErrors() ) {
            System.err.println( "FACT TEMPLATES FAILED: " + parser.getErrorMessages() );
        }
        assertFalse( parser.hasErrors() );

        assertEquals( 1,
                      pkg.getRules().size() );
        assertEquals( 2,
                      pkg.getFactTemplates().size() );

        FactTemplateDescr fact1 = (FactTemplateDescr) pkg.getFactTemplates().get( 0 );
        assertEquals( "Cheese",
                      fact1.getName() );
        assertEquals( 2,
                      fact1.getFields().size() );

        assertEquals( "name",
                      ((FieldTemplateDescr) fact1.getFields().get( 0 )).getName() );
        assertEquals( "String",
                      ((FieldTemplateDescr) fact1.getFields().get( 0 )).getClassType() );

        assertEquals( "age",
                      ((FieldTemplateDescr) fact1.getFields().get( 1 )).getName() );
        assertEquals( "Integer",
                      ((FieldTemplateDescr) fact1.getFields().get( 1 )).getClassType() );

        fact1 = null;

        final FactTemplateDescr fact2 = (FactTemplateDescr) pkg.getFactTemplates().get( 1 );
        assertEquals( "Wine",
                      fact2.getName() );
        assertEquals( 3,
                      fact2.getFields().size() );

        assertEquals( "name",
                      ((FieldTemplateDescr) fact2.getFields().get( 0 )).getName() );
        assertEquals( "String",
                      ((FieldTemplateDescr) fact2.getFields().get( 0 )).getClassType() );

        assertEquals( "year",
                      ((FieldTemplateDescr) fact2.getFields().get( 1 )).getName() );
        assertEquals( "String",
                      ((FieldTemplateDescr) fact2.getFields().get( 1 )).getClassType() );

        assertEquals( "accolades",
                      ((FieldTemplateDescr) fact2.getFields().get( 2 )).getName() );
        assertEquals( "String[]",
                      ((FieldTemplateDescr) fact2.getFields().get( 2 )).getClassType() );
    }

    public void xxxtestTernaryExpression() throws Exception {

        final DRLParser parser = parseResource( "ternary_expression.drl" );

        parser.compilation_unit();
        final PackageDescr pkg = parser.getPackageDescr();
        final RuleDescr rule = (RuleDescr) pkg.getRules().get( 0 );
        assertEquals( 1,
                      pkg.getRules().size() );

        assertFalse( parser.hasErrors() );
        assertEqualsIgnoreWhitespace( "if (speed > speedLimit ? true : false;) pullEmOver();",
                                      (String) rule.getConsequence() );
    }

    public void FIXME_testLatinChars() throws Exception {
        final DrlParser parser = new DrlParser();
        final Reader drl = new InputStreamReader( this.getClass().getResourceAsStream( "latin-sample.dslr" ) );
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

    public void xxxtestFunctionWithArrays() throws Exception {
        final DRLParser parser = parseResource( "function_arrays.drl" );

        parser.compilation_unit();
        final PackageDescr pkg = parser.getPackageDescr();

        if ( parser.hasErrors() ) {
            System.err.println( parser.getErrorMessages() );
        }
        assertFalse( parser.hasErrors() );
        assertEquals( "foo",
                      pkg.getName() );
        assertEquals( 1,
                      pkg.getRules().size() );

        final RuleDescr rule = (RuleDescr) pkg.getRules().get( 0 );

        assertEqualsIgnoreWhitespace( "yourFunction(new String[3] {\"a\",\"b\",\"c\"});",
                                      (String) rule.getConsequence() );
        final FunctionDescr func = (FunctionDescr) pkg.getFunctions().get( 0 );

        assertEquals( "String[]",
                      func.getReturnType() );
        assertEquals( "args[]",
                      func.getParameterNames().get( 0 ) );
        assertEquals( "String",
                      func.getParameterTypes().get( 0 ) );
    }

    public void xxxtestAlmostEmptyRule() throws Exception {
        final RuleDescr rule = parseResource( "almost_empty_rule.drl" ).rule();

        assertNotNull( rule );

        assertEquals( "almost_empty",
                      rule.getName() );
        assertNotNull( rule.getLhs() );
        assertEquals( "",
                      ((String) rule.getConsequence()).trim() );
        assertFalse( this.parser.hasErrors() );
    }

    public void xxxtestQuotedStringNameRule() throws Exception {
        final RuleDescr rule = parseResource( "quoted_string_name_rule.drl" ).rule();

        assertNotNull( rule );

        assertEquals( "quoted string name",
                      rule.getName() );
        assertNotNull( rule.getLhs() );
        assertEquals( "",
                      ((String) rule.getConsequence()).trim() );
        assertFalse( this.parser.hasErrors() );
    }

    public void xxxtestNoLoop() throws Exception {
        final RuleDescr rule = parseResource( "no-loop.drl" ).rule();

        assertNotNull( rule );

        assertEquals( "rule1",
                      rule.getName() );
        final AttributeDescr att = (AttributeDescr) rule.getAttributes().get( 0 );
        assertEquals( "false",
                      att.getValue() );
        assertEquals( "no-loop",
                      att.getName() );
        assertFalse( this.parser.getErrorMessages().toString(),
                     this.parser.hasErrors() );

    }

    public void xxxtestAutofocus() throws Exception {
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

    public void xxxtestRuleFlowGroup() throws Exception {
        final RuleDescr rule = parseResource( "ruleflowgroup.drl" ).rule();

        assertNotNull( rule );

        assertEquals( "rule1",
                      rule.getName() );
        final AttributeDescr att = (AttributeDescr) rule.getAttributes().get( 0 );
        assertEquals( "a group",
                      att.getValue() );
        assertEquals( "ruleflow-group",
                      att.getName() );

        assertFalse( this.parser.hasErrors() );
    }

    public void xxxtestConsequenceWithDeclaration() throws Exception {
        final RuleDescr rule = parseResource( "declaration-in-consequence.drl" ).rule();

        assertNotNull( rule );

        assertEquals( "myrule",
                      rule.getName() );

        final String expected = "int i = 0; i = 1; i / 1; i == 1; i(i); i = 'i'; i.i.i; i\\i; i<i; i>i; i=\"i\";  ++i;" + "i++; --i; i--; i += i; i -= i; i *= i; i /= i;" + "int i = 5;" + "for(int j; j<i; ++j) {" + "System.out.println(j);}"
                                + "Object o = new String(\"Hello\");" + "String s = (String) o;";

        assertEqualsIgnoreWhitespace( expected,
                                      (String) rule.getConsequence() );
        assertTrue( ((String) rule.getConsequence()).indexOf( "++" ) > 0 );
        assertTrue( ((String) rule.getConsequence()).indexOf( "--" ) > 0 );
        assertTrue( ((String) rule.getConsequence()).indexOf( "+=" ) > 0 );
        assertTrue( ((String) rule.getConsequence()).indexOf( "==" ) > 0 );

        //System.out.println(( String ) rule.getConsequence());
        //note, need to assert that "i++" is preserved as is, no extra spaces.

        assertFalse( this.parser.hasErrors() );
    }

    public void xxxtestRuleParseLhs() throws Exception {
        final String text = "Person(age < 42, location==\"atlanta\") \nor\nPerson(name==\"bob\") \n";
        final AndDescr descrs = new AndDescr();
        final CharStream charStream = new ANTLRStringStream( text );
        final DRLLexer lexer = new DRLLexer( charStream );
        final TokenStream tokenStream = new CommonTokenStream( lexer );
        final DRLParser parser = new DRLParser( tokenStream );
        parser.setLineOffset( descrs.getLine() );
        parser.normal_lhs_block( descrs );
        if ( parser.hasErrors() ) {
            System.err.println( parser.getErrorMessages() );
        }
        assertFalse( parser.hasErrors() );
    }

    public void xxxtestLiteralBoolAndNegativeNumbersRule() throws Exception {
        final DRLParser parser = parseResource( "literal_bool_and_negative.drl" );
        final RuleDescr rule = parser.rule();
        assertFalse( parser.hasErrors() );

        assertNotNull( rule );

        assertEquals( "simple_rule",
                      rule.getName() );
        assertNotNull( rule.getLhs() );
        assertEqualsIgnoreWhitespace( "cons();",
                                      (String) rule.getConsequence() );

        final AndDescr lhs = rule.getLhs();
        assertEquals( 3,
                      lhs.getDescrs().size() );

        PatternDescr pattern = (PatternDescr) lhs.getDescrs().get( 0 );
        assertEquals( 1,
                      pattern.getConstraint().getDescrs().size() );
        AndDescr fieldAnd = (AndDescr) pattern.getConstraint();
        FieldConstraintDescr fld = (FieldConstraintDescr) fieldAnd.getDescrs().get( 0 );
        LiteralRestrictionDescr lit = (LiteralRestrictionDescr) fld.getRestrictions().get( 0 );

        assertEquals( "==",
                      lit.getEvaluator() );
        assertEquals( "false",
                      lit.getText() );
        assertEquals( "bar",
                      fld.getFieldName() );

        pattern = (PatternDescr) lhs.getDescrs().get( 1 );
        assertEquals( 1,
                      pattern.getConstraint().getDescrs().size() );

        fieldAnd = (AndDescr) pattern.getConstraint();
        fld = (FieldConstraintDescr) fieldAnd.getDescrs().get( 0 );
        lit = (LiteralRestrictionDescr) fld.getRestrictions().get( 0 );

        assertEquals( ">",
                      lit.getEvaluator() );
        assertEquals( "-42",
                      lit.getText() );
        assertEquals( "boo",
                      fld.getFieldName() );

        pattern = (PatternDescr) lhs.getDescrs().get( 2 );
        assertEquals( 1,
                      pattern.getConstraint().getDescrs().size() );

        //lit = (LiteralDescr) col.getDescrs().get( 0 );

        fieldAnd = (AndDescr) pattern.getConstraint();
        fld = (FieldConstraintDescr) fieldAnd.getDescrs().get( 0 );
        lit = (LiteralRestrictionDescr) fld.getRestrictions().get( 0 );
        assertEquals( ">",
                      lit.getEvaluator() );
        assertEquals( "-42.42",
                      lit.getText() );
        assertEquals( "boo",
                      fld.getFieldName() );

        assertFalse( parser.hasErrors() );
    }

    public void xxxtestChunkWithoutParens() throws Exception {
        String input = "( foo )";
        paren_chunk_return ret = parse( input ).paren_chunk();
        final String chunk = input.substring( ((CommonToken) ret.start).getStartIndex(),
                                              ((CommonToken) ret.stop).getStopIndex() + 1 );

        assertEquals( "( foo )",
                      chunk );

        assertFalse( this.parser.hasErrors() );
    }

    public void xxxtestChunkWithParens() throws Exception {
        String input = "(fnord())";
        paren_chunk_return ret = parse( input ).paren_chunk();
        final String chunk = input.substring( ((CommonToken) ret.start).getStartIndex(),
                                              ((CommonToken) ret.stop).getStopIndex() + 1 );

        assertEqualsIgnoreWhitespace( "(fnord())",
                                      chunk );

        assertFalse( this.parser.hasErrors() );
    }

    public void xxxtestChunkWithParensAndQuotedString() throws Exception {
        String input = "( fnord( \"cheese\" ) )";
        paren_chunk_return ret = parse( input ).paren_chunk();
        final String chunk = input.substring( ((CommonToken) ret.start).getStartIndex(),
                                              ((CommonToken) ret.stop).getStopIndex() + 1 );

        assertEqualsIgnoreWhitespace( "( fnord( \"cheese\" ) )",
                                      chunk );

        assertFalse( this.parser.hasErrors() );
    }

    public void xxxtestChunkWithRandomCharac5ters() throws Exception {
        String input = "( %*9dkj)";
        paren_chunk_return ret = parse( input ).paren_chunk();
        final String chunk = input.substring( ((CommonToken) ret.start).getStartIndex(),
                                              ((CommonToken) ret.stop).getStopIndex() + 1 );

        assertEqualsIgnoreWhitespace( "( %*9dkj)",
                                      chunk );

        assertFalse( this.parser.hasErrors() );
    }

    public void xxxtestEmptyPattern() throws Exception {
        parseResource( "test_EmptyPattern.drl" );
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
        final PatternDescr patternDescr = (PatternDescr) ruleDescr.getLhs().getDescrs().get( 0 );
        assertEquals( 0,
                      patternDescr.getConstraint().getDescrs().size() ); //this may be null, not sure as the test doesn't get this far...
        assertEquals( "Cheese",
                      patternDescr.getObjectType() );

    }

    public void xxxtestSimpleMethodCallWithFrom() throws Exception {

        final RuleDescr rule = parseResource( "test_SimpleMethodCallWithFrom.drl" ).rule();
        final PatternDescr pattern = (PatternDescr) rule.getLhs().getDescrs().get( 0 );
        final FromDescr from = (FromDescr) pattern.getSource();
        final AccessorDescr method = (AccessorDescr) from.getDataSource();

        assertFalse( this.parser.getErrorMessages().toString(),
                     this.parser.hasErrors() );

        assertEquals( "something.doIt( foo,bar,42,\"hello\",{ a => \"b\", \"something\" => 42, \"a\" => foo, x => {x=>y}},\"end\", [a, \"b\", 42] )",
                      method.toString() );
    }

    public void xxxtestSimpleFunctionCallWithFrom() throws Exception {

        final RuleDescr rule = parseResource( "test_SimpleFunctionCallWithFrom.drl" ).rule();
        final PatternDescr pattern = (PatternDescr) rule.getLhs().getDescrs().get( 0 );
        final FromDescr from = (FromDescr) pattern.getSource();
        final AccessorDescr func = (AccessorDescr) from.getDataSource();

        assertFalse( this.parser.getErrorMessages().toString(),
                     this.parser.hasErrors() );

        assertEquals( "doIt( foo,bar,42,\"hello\",{ a => \"b\", \"something\" => 42, \"a\" => foo, x => {x=>y}},\"end\", [a, \"b\", 42] )",
                      func.toString() );
    }

    public void xxxtestSimpleAccessorWithFrom() throws Exception {

        final RuleDescr rule = parseResource( "test_SimpleAccessorWithFrom.drl" ).rule();
        final PatternDescr pattern = (PatternDescr) rule.getLhs().getDescrs().get( 0 );
        final FromDescr from = (FromDescr) pattern.getSource();
        final AccessorDescr accessor = (AccessorDescr) from.getDataSource();

        assertFalse( this.parser.getErrorMessages().toString(),
                     this.parser.hasErrors() );

        assertNull( ((FieldAccessDescr) accessor.getInvokers().get( 0 )).getArgument() );

        assertEquals( "something.doIt",
                      accessor.toString() );
    }

    public void xxxtestSimpleAccessorAndArgWithFrom() throws Exception {

        final RuleDescr rule = parseResource( "test_SimpleAccessorArgWithFrom.drl" ).rule();
        final PatternDescr pattern = (PatternDescr) rule.getLhs().getDescrs().get( 0 );
        final FromDescr from = (FromDescr) pattern.getSource();
        final AccessorDescr accessor = (AccessorDescr) from.getDataSource();

        assertFalse( this.parser.getErrorMessages().toString(),
                     this.parser.hasErrors() );

        assertNotNull( ((FieldAccessDescr) accessor.getInvokers().get( 0 )).getArgument() );

        assertEquals( "something.doIt[\"key\"]",
                      accessor.toString() );
    }

    public void xxxtestComplexChainedAcessor() throws Exception {
        final RuleDescr rule = parseResource( "test_ComplexChainedCallWithFrom.drl" ).rule();
        final PatternDescr pattern = (PatternDescr) rule.getLhs().getDescrs().get( 0 );
        final FromDescr from = (FromDescr) pattern.getSource();
        final AccessorDescr accessor = (AccessorDescr) from.getDataSource();

        assertFalse( this.parser.getErrorMessages().toString(),
                     this.parser.hasErrors() );

        assertEquals( "doIt1( foo,bar,42,\"hello\",{ a => \"b\"}, [a, \"b\", 42] ).doIt2(bar, [a, \"b\", 42]).field[\"key\"]",
                      accessor.toString() );
    }

    //        public void xxxtestFrom() throws Exception {
    //            final RuleDescr rule = parseResource( "from.drl" ).rule();
    //
    //            if(parser.hasErrors()) {
    //                System.err.println(parser.getErrorMessages());
    //            }
    //            assertFalse(parser.hasErrors());
    //
    //            assertNotNull( rule );
    //
    //            assertEquals( "using_from",
    //                          rule.getName() );
    //
    //            assertEquals(9, rule.getLhs().getDescrs().size());
    //
    //            FromDescr from = (FromDescr) rule.getLhs().getDescrs().get(0);
    //
    //            assertEquals(3, from.getLine());
    //
    //            assertEquals("Foo", from.getReturnedPattern().getObjectType());
    //            assertTrue(from.getDataSource() instanceof FieldAccessDescr);
    //            assertEquals("baz", ((FieldAccessDescr) from.getDataSource()).getFieldName());
    //            assertEquals("bar", ((FieldAccessDescr) from.getDataSource()).getVariableName());
    //
    //
    //            ArgumentValueDescr arg = null;
    //
    //            from = (FromDescr) rule.getLhs().getDescrs().get(1);
    //            assertEquals("Foo", from.getReturnedPattern().getObjectType());
    //            assertEquals(0, from.getReturnedPattern().getDescrs().size());
    //            FieldAccessDescr fieldAccess = ( FieldAccessDescr ) from.getDataSource();
    //            arg = ( ArgumentValueDescr ) fieldAccess.getArgument();
    //            assertEquals(ArgumentValueDescr.STRING,  arg.getType() );
    //
    //            from = (FromDescr) rule.getLhs().getDescrs().get(2);
    //            fieldAccess = ( FieldAccessDescr ) from.getDataSource();
    //            arg = ( ArgumentValueDescr ) fieldAccess.getArgument();
    //            assertEquals(ArgumentValueDescr.VARIABLE,  arg.getType() );
    //
    //            from = (FromDescr) rule.getLhs().getDescrs().get(3);
    //            fieldAccess = ( FieldAccessDescr ) from.getDataSource();
    //            arg = ( ArgumentValueDescr ) fieldAccess.getArgument();
    //            assertEquals(ArgumentValueDescr.INTEGRAL,  arg.getType() );
    //
    //            from = (FromDescr) rule.getLhs().getDescrs().get(4);
    //            assertEquals("Whee", from.getReturnedColumn().getObjectType());
    //            assertEquals(1, from.getReturnedColumn().getDescrs().size());
    //            assertTrue(from.getDataSource() instanceof FunctionCallDescr);
    //            assertEquals("whee", ((FunctionCallDescr) from.getDataSource()).getName());
    //            assertEquals(1, ((FunctionCallDescr) from.getDataSource()).getArguments().size());
    //            arg = ( (ArgumentValueDescr )((FunctionCallDescr) from.getDataSource()).getArguments().get(0));
    //            assertEquals("y", arg.getValue());
    //            assertEquals(ArgumentValueDescr.STRING, arg.getType());
    //
    //            assertEquals(7, from.getLine());
    //            assertEquals(7, from.getReturnedColumn().getLine());
    //
    //            from = (FromDescr) rule.getLhs().getDescrs().get(5);
    //            assertEquals("Foo", from.getReturnedColumn().getObjectType());
    //            assertEquals(1, from.getReturnedColumn().getDescrs().size());
    //            assertEquals("f", from.getReturnedColumn().getIdentifier());
    //            assertTrue(from.getDataSource() instanceof MethodAccessDescr);
    //            assertEquals("bar", ((MethodAccessDescr) from.getDataSource()).getVariableName());
    //            assertEquals("la", ((MethodAccessDescr) from.getDataSource()).getMethodName());
    //            assertEquals(1, ((MethodAccessDescr) from.getDataSource()).getArguments().size());
    //            arg = (ArgumentValueDescr) ((MethodAccessDescr) from.getDataSource()).getArguments().get(0);
    //
    //
    //            assertEquals("x", arg.getValue());
    //            assertEquals(ArgumentValueDescr.VARIABLE, arg.getType());
    //
    //            assertEqualsIgnoreWhitespace("whee();", ( String ) rule.getConsequence());
    //
    //            from = (FromDescr) rule.getLhs().getDescrs().get(6);
    //            assertEquals("wa", ((FunctionCallDescr)from.getDataSource()).getName());
    //
    //            from = (FromDescr) rule.getLhs().getDescrs().get(7);
    //            MethodAccessDescr meth = (MethodAccessDescr)from.getDataSource();
    //            assertEquals("wa", meth.getMethodName());
    //            assertEquals("la", meth.getVariableName());
    //
    //            arg = (ArgumentValueDescr) meth.getArguments().get(0);
    //            assertEquals("42", arg.getValue());
    //            assertEquals(ArgumentValueDescr.INTEGRAL, arg.getType());
    //
    //            arg = (ArgumentValueDescr) meth.getArguments().get(1);
    //            assertEquals("42.42", arg.getValue());
    //            assertEquals(ArgumentValueDescr.DECIMAL, arg.getType());
    //
    //            arg = (ArgumentValueDescr) meth.getArguments().get(2);
    //            assertEquals("false", arg.getValue());
    //            assertEquals(ArgumentValueDescr.BOOLEAN, arg.getType());
    //
    //            arg = (ArgumentValueDescr) meth.getArguments().get(3);
    //            assertEquals("null", arg.getValue());
    //            assertEquals(ArgumentValueDescr.NULL, arg.getType());
    //
    //            assertEquals("Bam", ((PatternDescr)rule.getLhs().getDescrs().get(8)).getObjectType());
    //        }

    public void xxxtestSimpleRule() throws Exception {
        final RuleDescr rule = parseResource( "simple_rule.drl" ).rule();

        assertNotNull( rule );

        assertEquals( "simple_rule",
                      rule.getName() );

        assertEquals( 7,
                      rule.getConsequenceLine() );
        assertEquals( 2,
                      rule.getConsequencePattern() );

        final AndDescr lhs = rule.getLhs();

        assertNotNull( lhs );

        assertEquals( 3,
                      lhs.getDescrs().size() );

        //System.err.println( lhs.getDescrs() );

        // Check first pattern
        final PatternDescr first = (PatternDescr) lhs.getDescrs().get( 0 );
        assertEquals( "foo3",
                      first.getIdentifier() );
        assertEquals( "Bar",
                      first.getObjectType() );

        assertEquals( 1,
                      first.getConstraint().getDescrs().size() );

        AndDescr fieldAnd = (AndDescr) first.getConstraint();
        FieldConstraintDescr fld = (FieldConstraintDescr) fieldAnd.getDescrs().get( 0 );
        LiteralRestrictionDescr constraint = (LiteralRestrictionDescr) fld.getRestrictions().get( 0 );

        assertNotNull( constraint );

        assertEquals( "a",
                      fld.getFieldName() );
        assertEquals( "==",
                      constraint.getEvaluator() );
        assertEquals( "3",
                      constraint.getText() );

        // Check second pattern
        final PatternDescr second = (PatternDescr) lhs.getDescrs().get( 1 );
        assertEquals( "foo4",
                      second.getIdentifier() );
        assertEquals( "Bar",
                      second.getObjectType() );

        //System.err.println( second.getDescrs() );

        fieldAnd = (AndDescr) second.getConstraint();
        assertEquals( 2,
                      fieldAnd.getDescrs().size() );

        final FieldBindingDescr fieldBindingDescr = (FieldBindingDescr) fieldAnd.getDescrs().get( 0 );
        assertEquals( "a",
                      fieldBindingDescr.getFieldName() );
        assertEquals( "a4",
                      fieldBindingDescr.getIdentifier() );

        fld = (FieldConstraintDescr) fieldAnd.getDescrs().get( 1 );
        constraint = (LiteralRestrictionDescr) fld.getRestrictions().get( 0 );

        assertNotNull( constraint );

        assertEquals( "a",
                      fld.getFieldName() );
        assertEquals( "==",
                      constraint.getEvaluator() );
        assertEquals( "4",
                      constraint.getText() );

        // Check third pattern
        final PatternDescr third = (PatternDescr) lhs.getDescrs().get( 2 );
        assertNull( third.getIdentifier() );
        assertEquals( "Baz",
                      third.getObjectType() );

        assertEqualsIgnoreWhitespace( "if ( a == b ) { " + "  assert( foo3 );" + "} else {" + "  retract( foo4 );" + "}" + "  System.out.println( a4 );",
                                      (String) rule.getConsequence() );

        assertFalse( this.parser.hasErrors() );
    }

    public void xxxtestRestrictionsMultiple() throws Exception {
        final RuleDescr rule = parseResource( "restrictions_test.drl" ).rule();

        assertFalse( this.parser.getErrors().toString(),
                     this.parser.hasErrors() );
        assertNotNull( rule );

        assertEqualsIgnoreWhitespace( "consequence();",
                                      (String) rule.getConsequence() );
        assertEquals( "simple_rule",
                      rule.getName() );
        assertEquals( 2,
                      rule.getLhs().getDescrs().size() );

        //The first pattern, with 2 restrictions on a single field (plus a connective)
        PatternDescr pattern = (PatternDescr) rule.getLhs().getDescrs().get( 0 );
        assertEquals( "Person",
                      pattern.getObjectType() );
        assertEquals( 1,
                      pattern.getConstraint().getDescrs().size() );

        AndDescr and = (AndDescr) pattern.getConstraint();
        FieldConstraintDescr fld = (FieldConstraintDescr) and.getDescrs().get( 0 );
        assertEquals( RestrictionConnectiveDescr.AND,
                      ((RestrictionConnectiveDescr) fld.getRestriction()).getConnective() );
        assertEquals( 2,
                      fld.getRestrictions().size() );
        assertEquals( "age",
                      fld.getFieldName() );

        LiteralRestrictionDescr lit = (LiteralRestrictionDescr) fld.getRestrictions().get( 0 );
        assertEquals( ">",
                      lit.getEvaluator() );
        assertEquals( "30",
                      lit.getText() );

        lit = (LiteralRestrictionDescr) fld.getRestrictions().get( 1 );
        assertEquals( "<",
                      lit.getEvaluator() );
        assertEquals( "40",
                      lit.getText() );

        //the second col, with 2 fields, the first with 2 restrictions, the second field with one
        pattern = (PatternDescr) rule.getLhs().getDescrs().get( 1 );
        assertEquals( "Vehicle",
                      pattern.getObjectType() );
        assertEquals( 2,
                      pattern.getConstraint().getDescrs().size() );

        and = (AndDescr) pattern.getConstraint();
        fld = (FieldConstraintDescr) and.getDescrs().get( 0 );
        assertEquals( "type",
                      fld.getFieldName() );
        assertEquals( 1,
                      fld.getRestrictions().size() );
        RestrictionConnectiveDescr or = (RestrictionConnectiveDescr) fld.getRestrictions().get( 0 );
        assertEquals( RestrictionConnectiveDescr.OR,
                      or.getConnective() );
        assertEquals( 2,
                      or.getRestrictions().size() );
        lit = (LiteralRestrictionDescr) or.getRestrictions().get( 0 );
        assertEquals( "==",
                      lit.getEvaluator() );
        assertEquals( "sedan",
                      lit.getText() );

        lit = (LiteralRestrictionDescr) or.getRestrictions().get( 1 );
        assertEquals( "==",
                      lit.getEvaluator() );
        assertEquals( "wagon",
                      lit.getText() );

        //now the second field
        fld = (FieldConstraintDescr) and.getDescrs().get( 1 );
        assertEquals( 1,
                      fld.getRestrictions().size() );
        lit = (LiteralRestrictionDescr) fld.getRestrictions().get( 0 );
        assertEquals( "<",
                      lit.getEvaluator() );
        assertEquals( "3",
                      lit.getText() );

    }

    public void xxxtestLineNumberInAST() throws Exception {
        //also see testSimpleExpander to see how this works with an expander (should be the same).

        final RuleDescr rule = parseResource( "simple_rule.drl" ).rule();

        assertNotNull( rule );

        assertEquals( "simple_rule",
                      rule.getName() );

        assertEquals( 7,
                      rule.getConsequenceLine() );
        assertEquals( 2,
                      rule.getConsequencePattern() );

        final AndDescr lhs = rule.getLhs();

        assertNotNull( lhs );

        assertEquals( 3,
                      lhs.getDescrs().size() );

        // Check first pattern
        final PatternDescr first = (PatternDescr) lhs.getDescrs().get( 0 );
        assertEquals( "foo3",
                      first.getIdentifier() );
        assertEquals( "Bar",
                      first.getObjectType() );
        assertEquals( 1,
                      first.getConstraint().getDescrs().size() );

        // Check second pattern
        final PatternDescr second = (PatternDescr) lhs.getDescrs().get( 1 );
        assertEquals( "foo4",
                      second.getIdentifier() );
        assertEquals( "Bar",
                      second.getObjectType() );

        final PatternDescr third = (PatternDescr) lhs.getDescrs().get( 2 );
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

    public void xxxtestLineNumberIncludingCommentsInRHS() throws Exception {
        parseResource( "test_CommentLineNumbersInConsequence.drl" ).compilation_unit();

        assertFalse( this.parser.hasErrors() );
        final String rhs = (String) ((RuleDescr) this.parser.getPackageDescr().getRules().get( 0 )).getConsequence();
        String expected = "  \t//woot\n  \tfirst\n  \t\n  \t//\n  \t\n  \t/* lala\n  \t\n  \t*/\n  \tsecond  \n";
        assertEquals( expected,
                      rhs );
    }

    public void xxxtestLhsSemicolonDelim() throws Exception {
        final RuleDescr rule = parseResource( "lhs_semicolon_delim.drl" ).rule();

        assertNotNull( rule );

        assertEquals( "simple_rule",
                      rule.getName() );

        final AndDescr lhs = rule.getLhs();

        assertNotNull( lhs );

        assertEquals( 3,
                      lhs.getDescrs().size() );

        //System.err.println( lhs.getDescrs() );

        // Check first pattern
        final PatternDescr first = (PatternDescr) lhs.getDescrs().get( 0 );
        assertEquals( "foo3",
                      first.getIdentifier() );
        assertEquals( "Bar",
                      first.getObjectType() );

        assertEquals( 1,
                      first.getConstraint().getDescrs().size() );

        //LiteralDescr constraint = (LiteralDescr) first.getDescrs().get( 0 );
        AndDescr and = (AndDescr) first.getConstraint();
        FieldConstraintDescr fld = (FieldConstraintDescr) and.getDescrs().get( 0 );
        LiteralRestrictionDescr constraint = (LiteralRestrictionDescr) fld.getRestrictions().get( 0 );

        assertNotNull( constraint );

        assertEquals( "a",
                      fld.getFieldName() );
        assertEquals( "==",
                      constraint.getEvaluator() );
        assertEquals( "3",
                      constraint.getText() );

        // Check second pattern
        final PatternDescr second = (PatternDescr) lhs.getDescrs().get( 1 );
        assertEquals( "foo4",
                      second.getIdentifier() );
        assertEquals( "Bar",
                      second.getObjectType() );

        and = (AndDescr) second.getConstraint();
        assertEquals( 2,
                      and.getDescrs().size() );

        //System.err.println( second.getDescrs() );

        final FieldBindingDescr fieldBindingDescr = (FieldBindingDescr) and.getDescrs().get( 0 );
        assertEquals( "a",
                      fieldBindingDescr.getFieldName() );
        assertEquals( "a4",
                      fieldBindingDescr.getIdentifier() );

        fld = (FieldConstraintDescr) and.getDescrs().get( 1 );

        constraint = (LiteralRestrictionDescr) fld.getRestrictions().get( 0 );

        assertNotNull( constraint );

        assertEquals( "a",
                      fld.getFieldName() );
        assertEquals( "==",
                      constraint.getEvaluator() );
        assertEquals( "4",
                      constraint.getText() );

        // Check third pattern
        final PatternDescr third = (PatternDescr) lhs.getDescrs().get( 2 );
        assertNull( third.getIdentifier() );
        assertEquals( "Baz",
                      third.getObjectType() );

        assertEqualsIgnoreWhitespace( "if ( a == b ) { " + "  assert( foo3 );" + "} else {" + "  retract( foo4 );" + "}" + "  System.out.println( a4 );",
                                      (String) rule.getConsequence() );

        assertFalse( this.parser.hasErrors() );
    }

    public void xxxtestNotNode() throws Exception {
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
        final PatternDescr pattern = (PatternDescr) not.getDescrs().get( 0 );

        assertEquals( "Cheese",
                      pattern.getObjectType() );
        assertEquals( 1,
                      pattern.getConstraint().getDescrs().size() );

        final AndDescr and = (AndDescr) pattern.getConstraint();
        final FieldConstraintDescr fld = (FieldConstraintDescr) and.getDescrs().get( 0 );
        final LiteralRestrictionDescr lit = (LiteralRestrictionDescr) fld.getRestrictions().get( 0 );

        assertEquals( "==",
                      lit.getEvaluator() );
        assertEquals( "stilton",
                      lit.getText() );
        assertEquals( "type",
                      fld.getFieldName() );

        assertFalse( this.parser.hasErrors() );
    }

    public void xxxtestFunctionImport() throws Exception {
        final DRLParser parser = parseResource( "test_FunctionImport.drl" );
        parser.compilation_unit();
        assertFalse( parser.hasErrors() );

        final PackageDescr pkg = parser.getPackageDescr();
        assertEquals( 2,
                      pkg.getFunctionImports().size() );

        assertEquals( "abd.def.x",
                      ((FunctionImportDescr) pkg.getFunctionImports().get( 0 )).getTarget() );
        assertFalse( ((FunctionImportDescr) pkg.getFunctionImports().get( 0 )).getStartCharacter() == -1 );
        assertFalse( ((FunctionImportDescr) pkg.getFunctionImports().get( 0 )).getEndCharacter() == -1 );
        assertEquals( "qed.wah.*",
                      ((FunctionImportDescr) pkg.getFunctionImports().get( 1 )).getTarget() );
        assertFalse( ((FunctionImportDescr) pkg.getFunctionImports().get( 1 )).getStartCharacter() == -1 );
        assertFalse( ((FunctionImportDescr) pkg.getFunctionImports().get( 1 )).getEndCharacter() == -1 );
    }

    public void xxxtestNotExistWithBrackets() throws Exception {

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
        final PatternDescr pattern = (PatternDescr) not.getDescrs().get( 0 );

        assertEquals( "Cheese",
                      pattern.getObjectType() );

        final ExistsDescr ex = (ExistsDescr) lhs.getDescrs().get( 1 );
        assertEquals( 1,
                      ex.getDescrs().size() );
        final PatternDescr exPattern = (PatternDescr) ex.getDescrs().get( 0 );
        assertEquals( "Foo",
                      exPattern.getObjectType() );

        assertFalse( parser.hasErrors() );
    }

    public void xxxtestNotBindindShouldBarf() throws Exception {
        final DRLParser parser = parseResource( "not_with_binding_error.drl" );
        parser.compilation_unit();
        assertTrue( parser.hasErrors() );
    }

    public void xxxtestSimpleQuery() throws Exception {
        final QueryDescr query = parseResource( "simple_query.drl" ).query();

        assertNotNull( query );

        assertEquals( "simple_query",
                      query.getName() );

        final AndDescr lhs = query.getLhs();

        assertNotNull( lhs );

        assertEquals( 3,
                      lhs.getDescrs().size() );

        // Check first pattern
        final PatternDescr first = (PatternDescr) lhs.getDescrs().get( 0 );
        assertEquals( "foo3",
                      first.getIdentifier() );
        assertEquals( "Bar",
                      first.getObjectType() );

        assertEquals( 1,
                      first.getConstraint().getDescrs().size() );

        AndDescr and = (AndDescr) first.getConstraint();
        FieldConstraintDescr fld = (FieldConstraintDescr) and.getDescrs().get( 0 );
        LiteralRestrictionDescr constraint = (LiteralRestrictionDescr) fld.getRestrictions().get( 0 );
        //LiteralDescr constraint = (LiteralDescr) first.getDescrs().get( 0 );

        assertNotNull( constraint );

        assertEquals( "a",
                      fld.getFieldName() );
        assertEquals( "==",
                      constraint.getEvaluator() );
        assertEquals( "3",
                      constraint.getText() );

        // Check second pattern
        final PatternDescr second = (PatternDescr) lhs.getDescrs().get( 1 );
        assertEquals( "foo4",
                      second.getIdentifier() );
        assertEquals( "Bar",
                      second.getObjectType() );

        and = (AndDescr) second.getConstraint();
        assertEquals( 2,
                      and.getDescrs().size() );
        //check it has field bindings.
        final FieldBindingDescr fieldBindingDescr = (FieldBindingDescr) and.getDescrs().get( 0 );
        assertEquals( "a",
                      fieldBindingDescr.getFieldName() );
        assertEquals( "a4",
                      fieldBindingDescr.getIdentifier() );

        fld = (FieldConstraintDescr) and.getDescrs().get( 1 );

        constraint = (LiteralRestrictionDescr) fld.getRestrictions().get( 0 );

        assertNotNull( constraint );

        assertEquals( "a",
                      fld.getFieldName() );
        assertEquals( "==",
                      constraint.getEvaluator() );
        assertEquals( "4",
                      constraint.getText() );

        assertFalse( this.parser.hasErrors() );
    }

    public void xxxtestQueryRuleMixed() throws Exception {
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

    public void xxxtestMultipleRules() throws Exception {
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
                                      (String) rule0.getConsequence() );

        // Check first pattern
        PatternDescr first = (PatternDescr) lhs.getDescrs().get( 0 );
        assertEquals( "Cheese",
                      first.getObjectType() );

        //checkout the second rule
        lhs = rule1.getLhs();
        assertNotNull( lhs );
        assertEquals( 1,
                      lhs.getDescrs().size() );
        assertEqualsIgnoreWhitespace( "System.out.println(\"I like \" + t);",
                                      (String) rule1.getConsequence() );

        // Check first pattern
        first = (PatternDescr) lhs.getDescrs().get( 0 );
        assertEquals( "Cheese",
                      first.getObjectType() );

        assertFalse( parser.hasErrors() );
    }

    public void xxxtestExpanderErrorsAfterExpansion() throws Exception {

        final String name = "expander_post_errors.dslr";
        final Expander expander = new DefaultExpander();
        final String expanded = expander.expand( this.getReader( name ) );

        final DRLParser parser = parse( name,
                                        expanded );
        parser.compilation_unit();
        assertTrue( parser.hasErrors() );

        final RecognitionException err = (RecognitionException) parser.getErrors().get( 0 );
        assertEquals( 1,
                      parser.getErrors().size() );

        assertEquals( 5,
                      err.line );
    }

    public void xxxtestExpanderLineSpread() throws Exception {
        final DrlParser parser = new DrlParser();
        final PackageDescr pkg = parser.parse( this.getReader( "expander_spread_lines.dslr" ),
                                               this.getReader( "complex.dsl" ) );

        assertFalse( parser.getErrors().toString(),
                     parser.hasErrors() );

        final RuleDescr rule = (RuleDescr) pkg.getRules().get( 0 );
        assertEquals( 1,
                      rule.getLhs().getDescrs().size() );

        final OrDescr or = (OrDescr) rule.getLhs().getDescrs().get( 0 );
        assertEquals( 2,
                      or.getDescrs().size() );
        assertNotNull( (String) rule.getConsequence() );

    }

    public void xxxtestExpanderMultipleConstraints() throws Exception {
        final DrlParser parser = new DrlParser();
        final PackageDescr pkg = parser.parse( this.getReader( "expander_multiple_constraints.dslr" ),
                                               this.getReader( "multiple_constraints.dsl" ) );

        assertFalse( parser.getErrors().toString(),
                     parser.hasErrors() );

        final RuleDescr rule = (RuleDescr) pkg.getRules().get( 0 );
        assertEquals( 2,
                      rule.getLhs().getDescrs().size() );

        PatternDescr pattern = (PatternDescr) rule.getLhs().getDescrs().get( 0 );
        assertEquals( "Person",
                      pattern.getObjectType() );

        assertEquals( 2,
                      pattern.getConstraint().getDescrs().size() );
        assertEquals( "age",
                      ((FieldConstraintDescr) pattern.getConstraint().getDescrs().get( 0 )).getFieldName() );
        assertEquals( "location",
                      ((FieldConstraintDescr) pattern.getConstraint().getDescrs().get( 1 )).getFieldName() );

        pattern = (PatternDescr) rule.getLhs().getDescrs().get( 1 );
        assertEquals( "Bar",
                      pattern.getObjectType() );

        assertNotNull( (String) rule.getConsequence() );

    }

    public void xxxtestExpanderMultipleConstraintsFlush() throws Exception {
        final DrlParser parser = new DrlParser();
        //this is similar to the other test, but it requires a flush to add the constraints
        final PackageDescr pkg = parser.parse( this.getReader( "expander_multiple_constraints_flush.dslr" ),
                                               this.getReader( "multiple_constraints.dsl" ) );

        assertFalse( parser.getErrors().toString(),
                     parser.hasErrors() );

        final RuleDescr rule = (RuleDescr) pkg.getRules().get( 0 );
        assertEquals( 1,
                      rule.getLhs().getDescrs().size() );

        final PatternDescr pattern = (PatternDescr) rule.getLhs().getDescrs().get( 0 );
        assertEquals( "Person",
                      pattern.getObjectType() );

        assertEquals( 2,
                      pattern.getConstraint().getDescrs().size() );
        assertEquals( "age",
                      ((FieldConstraintDescr) pattern.getConstraint().getDescrs().get( 0 )).getFieldName() );
        assertEquals( "location",
                      ((FieldConstraintDescr) pattern.getConstraint().getDescrs().get( 1 )).getFieldName() );

        assertNotNull( (String) rule.getConsequence() );

    }

    //    public void xxxtestExpanderUnExpandableErrorLines() throws Exception {
    //
    //        //stubb expander
    //        final ExpanderResolver res = new ExpanderResolver() {
    //            public Expander get(String name,
    //                                String config) {
    //                return new Expander() {
    //                    public String expand(String scope,
    //                                         String pattern) {
    //                        if ( pattern.startsWith( "Good" ) ) {
    //                            return pattern;
    //                        } else {
    //                            throw new IllegalArgumentException( "whoops" );
    //                        }
    //
    //                    }
    //                };
    //            }
    //        };
    //
    //        final DRLParser parser = parseResource( "expander_line_errors.dslr" );
    //        parser.setExpanderResolver( res );
    //        parser.compilation_unit();
    //        assertTrue( parser.hasErrors() );
    //
    //        final List messages = parser.getErrorMessages();
    //        assertEquals( messages.size(),
    //                      parser.getErrors().size() );
    //
    //        assertEquals( 4,
    //                      parser.getErrors().size() );
    //        assertEquals( ExpanderException.class,
    //                      parser.getErrors().get( 0 ).getClass() );
    //        assertEquals( 8,
    //                      ((RecognitionException) parser.getErrors().get( 0 )).line );
    //        assertEquals( 10,
    //                      ((RecognitionException) parser.getErrors().get( 1 )).line );
    //        assertEquals( 12,
    //                      ((RecognitionException) parser.getErrors().get( 2 )).line );
    //        assertEquals( 13,
    //                      ((RecognitionException) parser.getErrors().get( 3 )).line );
    //
    //        final PackageDescr pack = parser.getPackageDescr();
    //        assertNotNull( pack );
    //
    //        final ExpanderException ex = (ExpanderException) parser.getErrors().get( 0 );
    //        assertTrue( ex.getMessage().indexOf( "whoops" ) > -1 );
    //
    //    }

    public void xxxtestBasicBinding() throws Exception {
        final DRLParser parser = parseResource( "basic_binding.drl" );
        parser.compilation_unit();

        final PackageDescr pkg = parser.getPackageDescr();
        final RuleDescr ruleDescr = (RuleDescr) pkg.getRules().get( 0 );

        final AndDescr lhs = ruleDescr.getLhs();
        assertEquals( 1,
                      lhs.getDescrs().size() );
        final PatternDescr cheese = (PatternDescr) lhs.getDescrs().get( 0 );
        assertEquals( 1,
                      cheese.getConstraint().getDescrs().size() );
        assertEquals( "Cheese",
                      cheese.getObjectType() );
        assertEquals( 1,
                      lhs.getDescrs().size() );
        final FieldBindingDescr fieldBinding = (FieldBindingDescr) cheese.getConstraint().getDescrs().get( 0 );
        assertEquals( "type",
                      fieldBinding.getFieldName() );

        assertFalse( parser.hasErrors() );
    }

    public void xxxtestBoundVariables() throws Exception {
        final DRLParser parser = parseResource( "bindings.drl" );
        parser.compilation_unit();

        final PackageDescr pkg = parser.getPackageDescr();
        final RuleDescr ruleDescr = (RuleDescr) pkg.getRules().get( 0 );

        final AndDescr lhs = ruleDescr.getLhs();
        assertEquals( 2,
                      lhs.getDescrs().size() );
        final PatternDescr cheese = (PatternDescr) lhs.getDescrs().get( 0 );
        assertEquals( "Cheese",
                      cheese.getObjectType() );
        assertEquals( 2,
                      lhs.getDescrs().size() );
        FieldBindingDescr fieldBinding = (FieldBindingDescr) cheese.getConstraint().getDescrs().get( 0 );
        assertEquals( "type",
                      fieldBinding.getFieldName() );

        FieldConstraintDescr fld = (FieldConstraintDescr) cheese.getConstraint().getDescrs().get( 1 );
        LiteralRestrictionDescr literalDescr = (LiteralRestrictionDescr) fld.getRestrictions().get( 0 );
        //LiteralDescr literalDescr = (LiteralDescr) cheese.getDescrs().get( 1 );
        assertEquals( "type",
                      fld.getFieldName() );
        assertEquals( "==",
                      literalDescr.getEvaluator() );
        assertEquals( "stilton",
                      literalDescr.getText() );

        final PatternDescr person = (PatternDescr) lhs.getDescrs().get( 1 );
        fieldBinding = (FieldBindingDescr) person.getConstraint().getDescrs().get( 0 );
        assertEquals( "name",
                      fieldBinding.getFieldName() );

        fld = (FieldConstraintDescr) person.getConstraint().getDescrs().get( 1 );
        literalDescr = (LiteralRestrictionDescr) fld.getRestrictions().get( 0 );

        assertEquals( "name",
                      fld.getFieldName() );
        assertEquals( "==",
                      literalDescr.getEvaluator() );
        assertEquals( "bob",
                      literalDescr.getText() );

        fld = (FieldConstraintDescr) person.getConstraint().getDescrs().get( 2 );
        final VariableRestrictionDescr variableDescr = (VariableRestrictionDescr) fld.getRestrictions().get( 0 );

        assertEquals( "likes",
                      fld.getFieldName() );
        assertEquals( "==",
                      variableDescr.getEvaluator() );
        assertEquals( "$type",
                      variableDescr.getIdentifier() );

        assertFalse( parser.hasErrors() );
    }

    public void xxxtestOrNesting() throws Exception {
        final DRLParser parser = parseResource( "or_nesting.drl" );
        parser.compilation_unit();

        final PackageDescr pack = parser.getPackageDescr();
        assertNotNull( pack );
        assertEquals( 1,
                      pack.getRules().size() );
        final RuleDescr rule = (RuleDescr) pack.getRules().get( 0 );
        assertEquals( "simple_rule",
                      rule.getName() );

        assertEquals( 1,
                      rule.getLhs().getDescrs().size() );

        final OrDescr or = (OrDescr) rule.getLhs().getDescrs().get( 0 );
        assertEquals( 2,
                      or.getDescrs().size() );

        final PatternDescr first = (PatternDescr) or.getDescrs().get( 0 );
        assertEquals( "Person",
                      first.getObjectType() );

        final AndDescr and = (AndDescr) or.getDescrs().get( 1 );
        assertEquals( 2,
                      and.getDescrs().size() );

        final PatternDescr left = (PatternDescr) and.getDescrs().get( 0 );
        assertEquals( "Person",
                      left.getObjectType() );

        final PatternDescr right = (PatternDescr) and.getDescrs().get( 1 );
        assertEquals( "Cheese",
                      right.getObjectType() );
    }

    /** Test that explicit "&&", "||" works as expected */
    public void xxxtestAndOrRules() throws Exception {
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

        PatternDescr left = (PatternDescr) join.getDescrs().get( 0 );
        PatternDescr right = (PatternDescr) join.getDescrs().get( 1 );
        assertEquals( "Person",
                      left.getObjectType() );
        assertEquals( "Cheese",
                      right.getObjectType() );

        assertEquals( 1,
                      left.getConstraint().getDescrs().size() );

        FieldConstraintDescr fld = (FieldConstraintDescr) left.getConstraint().getDescrs().get( 0 );
        LiteralRestrictionDescr literal = (LiteralRestrictionDescr) fld.getRestrictions().get( 0 );

        assertEquals( "==",
                      literal.getEvaluator() );
        assertEquals( "name",
                      fld.getFieldName() );
        assertEquals( "mark",
                      literal.getText() );

        assertEquals( 1,
                      right.getConstraint().getDescrs().size() );

        fld = (FieldConstraintDescr) right.getConstraint().getDescrs().get( 0 );
        literal = (LiteralRestrictionDescr) fld.getRestrictions().get( 0 );

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
        left = (PatternDescr) or.getDescrs().get( 0 );
        right = (PatternDescr) or.getDescrs().get( 1 );
        assertEquals( "Person",
                      left.getObjectType() );
        assertEquals( "Cheese",
                      right.getObjectType() );
        assertEquals( 1,
                      left.getConstraint().getDescrs().size() );

        fld = (FieldConstraintDescr) left.getConstraint().getDescrs().get( 0 );
        literal = (LiteralRestrictionDescr) fld.getRestrictions().get( 0 );

        assertEquals( "==",
                      literal.getEvaluator() );
        assertEquals( "name",
                      fld.getFieldName() );
        assertEquals( "mark",
                      literal.getText() );

        assertEquals( 1,
                      right.getConstraint().getDescrs().size() );

        fld = (FieldConstraintDescr) right.getConstraint().getDescrs().get( 0 );
        literal = (LiteralRestrictionDescr) fld.getRestrictions().get( 0 );

        assertEquals( "==",
                      literal.getEvaluator() );
        assertEquals( "type",
                      fld.getFieldName() );
        assertEquals( "stilton",
                      literal.getText() );

        assertEqualsIgnoreWhitespace( "System.out.println( \"Mark and Michael\" );",
                                      (String) rule.getConsequence() );

        assertFalse( parser.hasErrors() );
    }

    /** test basic foo : Fact() || Fact() stuff */
    public void xxxtestOrWithBinding() throws Exception {
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

        final PatternDescr leftPattern = (PatternDescr) or.getDescrs().get( 0 );
        assertEquals( "Person",
                      leftPattern.getObjectType() );
        assertEquals( "foo",
                      leftPattern.getIdentifier() );

        final PatternDescr rightPattern = (PatternDescr) or.getDescrs().get( 1 );
        assertEquals( "Person",
                      rightPattern.getObjectType() );
        assertEquals( "foo",
                      rightPattern.getIdentifier() );

        final PatternDescr cheeseDescr = (PatternDescr) rule.getLhs().getDescrs().get( 1 );
        assertEquals( "Cheese",
                      cheeseDescr.getObjectType() );
        assertEquals( null,
                      cheeseDescr.getIdentifier() );

        assertEqualsIgnoreWhitespace( "System.out.println( \"Mark and Michael\" + bar );",
                                      (String) rule.getConsequence() );

        assertFalse( parser.hasErrors() );
    }

    /** test basic foo : Fact() || Fact() stuff binding to an "or"*/
    public void xxxtestOrBindingComplex() throws Exception {
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
        final PatternDescr firstFact = (PatternDescr) or.getDescrs().get( 0 );
        assertEquals( "Person",
                      firstFact.getObjectType() );
        assertEquals( "foo",
                      firstFact.getIdentifier() );

        //second "option"
        final PatternDescr secondFact = (PatternDescr) or.getDescrs().get( 1 );
        assertEquals( "Person",
                      secondFact.getObjectType() );
        assertEquals( 1,
                      secondFact.getConstraint().getDescrs().size() );
        assertEquals( "foo",
                      secondFact.getIdentifier() );

        assertEqualsIgnoreWhitespace( "System.out.println( \"Mark and Michael\" + bar );",
                                      (String) rule.getConsequence() );

        assertFalse( parser.hasErrors() );
    }

    public void xxxtestOrBindingWithBrackets() throws Exception {
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
        final PatternDescr firstFact = (PatternDescr) or.getDescrs().get( 0 );
        assertEquals( "Person",
                      firstFact.getObjectType() );
        assertEquals( "foo",
                      firstFact.getIdentifier() );

        //second "option"
        final PatternDescr secondFact = (PatternDescr) or.getDescrs().get( 0 );
        assertEquals( "Person",
                      secondFact.getObjectType() );
        assertEquals( "foo",
                      secondFact.getIdentifier() );

        assertEqualsIgnoreWhitespace( "System.out.println( \"Mark and Michael\" + bar );",
                                      (String) rule.getConsequence() );

        assertFalse( parser.hasErrors() );
    }

    /** */
    public void xxxtestBracketsPrecedence() throws Exception {
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
        final PatternDescr foo1 = (PatternDescr) not.getDescrs().get( 0 );
        assertEquals( "Foo",
                      foo1.getObjectType() );
        final PatternDescr foo2 = (PatternDescr) leftOr.getDescrs().get( 1 );
        assertEquals( "Foo",
                      foo2.getObjectType() );

        final OrDescr rightOr = (OrDescr) rootAnd.getDescrs().get( 1 );

        assertEquals( 2,
                      rightOr.getDescrs().size() );
        final PatternDescr shoes = (PatternDescr) rightOr.getDescrs().get( 0 );
        assertEquals( "Shoes",
                      shoes.getObjectType() );
        final PatternDescr butt = (PatternDescr) rightOr.getDescrs().get( 1 );
        assertEquals( "Butt",
                      butt.getObjectType() );

        assertFalse( parser.hasErrors() );
    }

    public void xxxtestEvalMultiple() throws Exception {
        final DRLParser parser = parseResource( "eval_multiple.drl" );
        parser.compilation_unit();

        assertFalse( parser.getErrorMessages().toString(),
                     parser.hasErrors() );

        final PackageDescr pack = parser.getPackageDescr();
        assertEquals( 1,
                      pack.getRules().size() );
        final RuleDescr rule = (RuleDescr) pack.getRules().get( 0 );
        assertEquals( 4,
                      rule.getLhs().getDescrs().size() );

        final EvalDescr eval = (EvalDescr) rule.getLhs().getDescrs().get( 0 );
        assertEqualsIgnoreWhitespace( "abc(\"foo\") + 5",
                                      (String) eval.getContent() );

        final PatternDescr pattern = (PatternDescr) rule.getLhs().getDescrs().get( 1 );
        assertEquals( "Foo",
                      pattern.getObjectType() );

    }

    public void xxxtestWithEval() throws Exception {
        final DRLParser parser = parseResource( "with_eval.drl" );
        parser.compilation_unit();

        final PackageDescr pack = parser.getPackageDescr();
        assertEquals( 1,
                      pack.getRules().size() );
        final RuleDescr rule = (RuleDescr) pack.getRules().get( 0 );
        assertEquals( 3,
                      rule.getLhs().getDescrs().size() );
        PatternDescr pattern = (PatternDescr) rule.getLhs().getDescrs().get( 0 );
        assertEquals( "Foo",
                      pattern.getObjectType() );
        pattern = (PatternDescr) rule.getLhs().getDescrs().get( 1 );
        assertEquals( "Bar",
                      pattern.getObjectType() );

        final EvalDescr eval = (EvalDescr) rule.getLhs().getDescrs().get( 2 );
        assertEqualsIgnoreWhitespace( "abc(\"foo\")",
                                      (String) eval.getContent() );
        assertEqualsIgnoreWhitespace( "Kapow",
                                      (String) rule.getConsequence() );

        assertFalse( parser.hasErrors() );
    }

    public void xxxtestWithRetval() throws Exception {
        final DRLParser parser = parseResource( "with_retval.drl" );
        parser.compilation_unit();

        final PackageDescr pack = parser.getPackageDescr();
        assertEquals( 1,
                      pack.getRules().size() );

        final RuleDescr rule = (RuleDescr) pack.getRules().get( 0 );
        assertEquals( 1,
                      rule.getLhs().getDescrs().size() );
        final PatternDescr col = (PatternDescr) rule.getLhs().getDescrs().get( 0 );
        assertEquals( 1,
                      col.getConstraint().getDescrs().size() );
        assertEquals( "Foo",
                      col.getObjectType() );
        final FieldConstraintDescr fld = (FieldConstraintDescr) col.getConstraint().getDescrs().get( 0 );
        final ReturnValueRestrictionDescr retval = (ReturnValueRestrictionDescr) fld.getRestrictions().get( 0 );

        assertEquals( "a + b",
                      retval.getContent() );
        assertEquals( "name",
                      fld.getFieldName() );
        assertEquals( "==",
                      retval.getEvaluator() );

        assertFalse( parser.hasErrors() );
    }

    public void xxxtestWithPredicate() throws Exception {
        final DRLParser parser = parseResource( "with_predicate.drl" );
        parser.compilation_unit();

        final PackageDescr pack = parser.getPackageDescr();
        assertEquals( 1,
                      pack.getRules().size() );

        final RuleDescr rule = (RuleDescr) pack.getRules().get( 0 );
        assertEquals( 1,
                      rule.getLhs().getDescrs().size() );
        final PatternDescr col = (PatternDescr) rule.getLhs().getDescrs().get( 0 );
        AndDescr and = (AndDescr) col.getConstraint();
        assertEquals( 2,
                      and.getDescrs().size() );

        final FieldBindingDescr field = (FieldBindingDescr) and.getDescrs().get( 0 );
        final PredicateDescr pred = (PredicateDescr) and.getDescrs().get( 1 );
        assertEquals( "age",
                      field.getFieldName() );
        assertEquals( "$age2",
                      field.getIdentifier() );
        assertEqualsIgnoreWhitespace( "$age2 == $age1+2",
                                      (String) pred.getContent() );

        assertFalse( parser.getErrorMessages().toString(),
                     parser.hasErrors() );
    }

    public void xxxtestNotWithConstraint() throws Exception {
        final DRLParser parser = parseResource( "not_with_constraint.drl" );
        parser.compilation_unit();

        final PackageDescr pack = parser.getPackageDescr();
        assertEquals( 1,
                      pack.getRules().size() );

        final RuleDescr rule = (RuleDescr) pack.getRules().get( 0 );
        assertEquals( 2,
                      rule.getLhs().getDescrs().size() );

        PatternDescr pattern = (PatternDescr) rule.getLhs().getDescrs().get( 0 );
        final FieldBindingDescr fieldBinding = (FieldBindingDescr) pattern.getConstraint().getDescrs().get( 0 );
        assertEquals( "$likes",
                      fieldBinding.getIdentifier() );

        final NotDescr not = (NotDescr) rule.getLhs().getDescrs().get( 1 );
        pattern = (PatternDescr) not.getDescrs().get( 0 );

        final FieldConstraintDescr fld = (FieldConstraintDescr) pattern.getConstraint().getDescrs().get( 0 );
        final VariableRestrictionDescr boundVariable = (VariableRestrictionDescr) fld.getRestrictions().get( 0 );

        assertEquals( "type",
                      fld.getFieldName() );
        assertEquals( "==",
                      boundVariable.getEvaluator() );
        assertEquals( "$likes",
                      boundVariable.getIdentifier() );

        assertFalse( parser.hasErrors() );
    }

    public void xxxtestGlobal() throws Exception {
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
                      pack.getGlobals().size() );

        final GlobalDescr foo = (GlobalDescr) pack.getGlobals().get( 0 );
        assertEquals( "java.lang.String",
                      foo.getType() );
        assertEquals( "foo",
                      foo.getIdentifier() );
        final GlobalDescr bar = (GlobalDescr) pack.getGlobals().get( 1 );
        assertEquals( "java.lang.Integer",
                      bar.getType() );
        assertEquals( "bar",
                      bar.getIdentifier() );

        assertFalse( parser.hasErrors() );
    }

    public void xxxtestFunctions() throws Exception {
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
                      func.getLine() );
        assertEquals( 0,
                      func.getColumn() );

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

    public void xxxtestComment() throws Exception {
        final DRLParser parser = parseResource( "comment.drl" );
        parser.compilation_unit();

        final PackageDescr pack = parser.getPackageDescr();
        assertNotNull( pack );

        assertEquals( "foo.bar",
                      pack.getName() );

        assertFalse( parser.getErrorMessages().toString(),
                     parser.hasErrors() );
    }

    public void xxxtestAttributes() throws Exception {
        final RuleDescr rule = parseResource( "rule_attributes.drl" ).rule();
        assertEquals( "simple_rule",
                      rule.getName() );
        assertEqualsIgnoreWhitespace( "bar();",
                                      (String) rule.getConsequence() );

        final List attrs = rule.getAttributes();
        assertEquals( 6,
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

        at = (AttributeDescr) attrs.get( 5 );
        assertEquals( "lock-on-active",
                      at.getName() );
        assertEquals( "true",
                      at.getValue() );

        assertFalse( this.parser.hasErrors() );
    }

    public void xxxtestAttributes_alternateSyntax() throws Exception {
        final RuleDescr rule = parseResource( "rule_attributes_alt.drl" ).rule();
        assertEquals( "simple_rule",
                      rule.getName() );
        assertEqualsIgnoreWhitespace( "bar();",
                                      (String) rule.getConsequence() );

        final List attrs = rule.getAttributes();
        assertEquals( 6,
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
        assertEquals( "lock-on-active",
                      at.getName() );
        assertEquals( "true",
                      at.getValue() );

        at = (AttributeDescr) attrs.get( 4 );
        assertEquals( "duration",
                      at.getName() );
        assertEquals( "42",
                      at.getValue() );

        at = (AttributeDescr) attrs.get( 5 );
        assertEquals( "activation-group",
                      at.getName() );
        assertEquals( "my_activation_group",
                      at.getValue() );

        assertFalse( this.parser.hasErrors() );
    }

    public void xxxtestEnumeration() throws Exception {
        final RuleDescr rule = parseResource( "enumeration.drl" ).rule();
        assertEquals( "simple_rule",
                      rule.getName() );
        assertEquals( 1,
                      rule.getLhs().getDescrs().size() );
        final PatternDescr col = (PatternDescr) rule.getLhs().getDescrs().get( 0 );
        assertEquals( "Foo",
                      col.getObjectType() );
        assertEquals( 1,
                      col.getConstraint().getDescrs().size() );
        final FieldConstraintDescr fld = (FieldConstraintDescr) col.getConstraint().getDescrs().get( 0 );
        final QualifiedIdentifierRestrictionDescr lit = (QualifiedIdentifierRestrictionDescr) fld.getRestrictions().get( 0 );

        assertEquals( "bar",
                      fld.getFieldName() );
        assertEquals( "==",
                      lit.getEvaluator() );
        assertEquals( "Foo.BAR",
                      lit.getText() );

        assertFalse( this.parser.hasErrors() );
    }

    public void xxxtestInvalidSyntax_Catches() throws Exception {
        parseResource( "invalid_syntax.drl" ).compilation_unit();
        assertTrue( this.parser.hasErrors() );
    }

    public void xxxtestMultipleErrors() throws Exception {
        parseResource( "multiple_errors.drl" ).compilation_unit();
        assertTrue( this.parser.hasErrors() );
        assertEquals( 2,
                      this.parser.getErrors().size() );
    }

    public void xxxtestExtraLhsNewline() throws Exception {
        parseResource( "extra_lhs_newline.drl" ).compilation_unit();
        assertFalse( this.parser.hasErrors() );
    }

    public void xxxtestPackageGarbage() throws Exception {

        parseResource( "package_garbage.drl" ).compilation_unit();
        assertTrue( this.parser.hasErrors() );
    }

    public void xxxtestSoundsLike() throws Exception {
        parseResource( "soundslike_operator.drl" ).compilation_unit();
        assertFalse( this.parser.getErrorMessages().toString(),
                     this.parser.hasErrors() );

        RuleDescr rule = (RuleDescr) this.parser.getPackageDescr().getRules().get( 0 );
        PatternDescr pat = (PatternDescr) rule.getLhs().getDescrs().get( 0 );

        pat.getConstraint();
    }

    public void xxxtestPackageAttributes() throws Exception {
        parseResource( "package_attributes.drl" ).compilation_unit();
        assertFalse( this.parser.getErrorMessages().toString(),
                     this.parser.hasErrors() );

        PackageDescr pkg = this.parser.getPackageDescr();
        AttributeDescr at = (AttributeDescr) pkg.getAttributes().get( 0 );
        assertEquals( "agenda-group",
                      at.getName() );
        assertEquals( "x",
                      at.getValue() );
        at = (AttributeDescr) pkg.getAttributes().get( 1 );
        assertEquals( "dialect",
                      at.getName() );
        assertEquals( "java",
                      at.getValue() );

        assertEquals( 2,
                      pkg.getRules().size() );

        assertEquals( 2,
                      pkg.getImports().size() );

        RuleDescr rule = (RuleDescr) pkg.getRules().get( 0 );
        assertEquals( "bar",
                      rule.getName() );
        at = (AttributeDescr) rule.getAttributes().get( 0 );
        assertEquals( "agenda-group",
                      at.getName() );
        assertEquals( "x",
                      at.getValue() );
        at = (AttributeDescr) rule.getAttributes().get( 1 );
        assertEquals( "dialect",
                      at.getName() );
        assertEquals( "java",
                      at.getValue() );

        rule = (RuleDescr) pkg.getRules().get( 1 );
        assertEquals( "baz",
                      rule.getName() );
        at = (AttributeDescr) rule.getAttributes().get( 0 );
        assertEquals( "dialect",
                      at.getName() );
        assertEquals( "mvel",
                      at.getValue() );
        at = (AttributeDescr) rule.getAttributes().get( 1 );
        assertEquals( "agenda-group",
                      at.getName() );
        assertEquals( "x",
                      at.getValue() );

    }

    public void xxxtestStatementOrdering1() throws Exception {
        parseResource( "statement_ordering_1.drl" );
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
                      ((ImportDescr) pkg.getImports().get( 0 )).getTarget() );
        assertEquals( "im.two",
                      ((ImportDescr) pkg.getImports().get( 1 )).getTarget() );
        assertEquals( "im.three",
                      ((ImportDescr) pkg.getImports().get( 2 )).getTarget() );
        assertEquals( "im.four",
                      ((ImportDescr) pkg.getImports().get( 3 )).getTarget() );

        assertFalse( this.parser.hasErrors() );
    }

    public void xxxtestRuleNamesStartingWithNumbers() throws Exception {
        parseResource( "rule_names_number_prefix.drl" ).compilation_unit();

        assertFalse( this.parser.getErrors().toString(),
                     this.parser.hasErrors() );

        final PackageDescr pkg = this.parser.getPackageDescr();

        assertEquals( 2,
                      pkg.getRules().size() );

        assertEquals( "1. Do Stuff!",
                      ((RuleDescr) pkg.getRules().get( 0 )).getName() );
        assertEquals( "2. Do More Stuff!",
                      ((RuleDescr) pkg.getRules().get( 1 )).getName() );
    }

    public void xxxtestEvalWithNewline() throws Exception {
        parseResource( "eval_with_newline.drl" ).compilation_unit();

        if ( this.parser.hasErrors() ) {
            System.err.println( this.parser.getErrorMessages() );
        }
        assertFalse( this.parser.hasErrors() );
    }

    public void xxxtestEvalWithSemicolon() throws Exception {
        parseResource( "eval_with_semicolon.drl" ).compilation_unit();

        assertTrue( this.parser.hasErrors() );
        assertEquals( 1,
                      this.parser.getErrorMessages().size() );
        assertTrue( ((String) this.parser.getErrorMessages().get( 0 )).indexOf( "Trailing semi-colon not allowed" ) >= 0 );
    }

    public void xxxtestEndPosition() throws Exception {
        parseResource( "test_EndPosition.drl" ).compilation_unit();
        final RuleDescr rule = (RuleDescr) this.parser.getPackageDescr().getRules().get( 0 );
        final PatternDescr col = (PatternDescr) rule.getLhs().getDescrs().get( 0 );
        assertEquals( 6,
                      col.getLine() );

        assertEquals( 8,
                      col.getEndLine() );

    }

    public void xxxtestQualifiedClassname() throws Exception {
        parseResource( "qualified_classname.drl" ).compilation_unit();

        assertFalse( this.parser.hasErrors() );

        final PackageDescr pkg = this.parser.getPackageDescr();
        final RuleDescr rule = (RuleDescr) pkg.getRules().get( 0 );

        final PatternDescr p = (PatternDescr) rule.getLhs().getDescrs().get( 0 );

        assertEquals( "com.cheeseco.Cheese",
                      p.getObjectType() );
    }

    public void xxxtestAccumulate() throws Exception {
        final DRLParser parser = parseResource( "accumulate.drl" );
        parser.compilation_unit();

        if ( parser.hasErrors() ) {
            System.err.println( parser.getErrorMessages() );
        }

        assertFalse( parser.getErrorMessages().toString(),
                     parser.hasErrors() );

        final PackageDescr pack = parser.getPackageDescr();
        assertEquals( 1,
                      pack.getRules().size() );
        final RuleDescr rule = (RuleDescr) pack.getRules().get( 0 );
        assertEquals( 1,
                      rule.getLhs().getDescrs().size() );

        final PatternDescr outPattern = (PatternDescr) rule.getLhs().getDescrs().get( 0 );
        final AccumulateDescr accum = (AccumulateDescr) outPattern.getSource();
        assertEqualsIgnoreWhitespace( "int x = 0 ;",
                                      accum.getInitCode() );
        assertEqualsIgnoreWhitespace( "x++;",
                                      accum.getActionCode() );
        assertNull( accum.getReverseCode() );
        assertEqualsIgnoreWhitespace( "new Integer(x)",
                                      accum.getResultCode() );

        assertFalse( accum.isExternalFunction() );

        final PatternDescr pattern = (PatternDescr) accum.getInputPattern();
        assertEquals( "Person",
                      pattern.getObjectType() );
    }

    public void xxxtestAccumulateWithBindings() throws Exception {
        final DRLParser parser = parseResource( "accumulate_with_bindings.drl" );
        parser.compilation_unit();

        assertFalse( parser.getErrorMessages().toString(),
                     parser.hasErrors() );

        final PackageDescr pack = parser.getPackageDescr();
        assertEquals( 1,
                      pack.getRules().size() );
        final RuleDescr rule = (RuleDescr) pack.getRules().get( 0 );
        assertEquals( 1,
                      rule.getLhs().getDescrs().size() );

        final PatternDescr outPattern = (PatternDescr) rule.getLhs().getDescrs().get( 0 );
        final AccumulateDescr accum = (AccumulateDescr) outPattern.getSource();
        assertEqualsIgnoreWhitespace( "$counter",
                                      outPattern.getIdentifier() );
        assertEqualsIgnoreWhitespace( "int x = 0 ;",
                                      accum.getInitCode() );
        assertEqualsIgnoreWhitespace( "x++;",
                                      accum.getActionCode() );
        assertEqualsIgnoreWhitespace( "new Integer(x)",
                                      accum.getResultCode() );

        final PatternDescr pattern = (PatternDescr) accum.getInputPattern();
        assertEquals( "Person",
                      pattern.getObjectType() );
    }

    public void xxxtestCollect() throws Exception {
        final DRLParser parser = parseResource( "collect.drl" );
        parser.compilation_unit();

        if ( parser.hasErrors() ) {
            System.err.println( parser.getErrorMessages() );
        }

        assertFalse( parser.hasErrors() );

        final PackageDescr pack = parser.getPackageDescr();
        assertEquals( 1,
                      pack.getRules().size() );
        final RuleDescr rule = (RuleDescr) pack.getRules().get( 0 );
        assertEquals( 1,
                      rule.getLhs().getDescrs().size() );

        final PatternDescr outPattern = (PatternDescr) rule.getLhs().getDescrs().get( 0 );
        final CollectDescr collect = (CollectDescr) outPattern.getSource();

        final PatternDescr pattern = (PatternDescr) collect.getInputPattern();
        assertEquals( "Person",
                      pattern.getObjectType() );
    }

    public void xxxtestPredicate() throws Exception {
        final PatternDescr pattern = new PatternDescr();
        parse( "$var : attr -> ( $var.equals(\"xyz\") )" ).constraints( pattern );

        assertFalse( this.parser.getErrorMessages().toString(),
                     this.parser.hasErrors() );

        final List constraints = pattern.getConstraint().getDescrs();
        assertEquals( 2,
                      constraints.size() );

        final FieldBindingDescr field = (FieldBindingDescr) constraints.get( 0 );
        final PredicateDescr predicate = (PredicateDescr) constraints.get( 1 );
        assertEquals( "$var",
                      field.getIdentifier() );
        assertEquals( "attr",
                      field.getFieldName() );
        assertEquals( " $var.equals(\"xyz\") ",
                      predicate.getContent() );
    }

    public void xxxtestPredicate2() throws Exception {
        final PatternDescr pattern = new PatternDescr();
        // predicates are also prefixed by the eval keyword
        parse( "eval( $var.equals(\"xyz\") )" ).constraints( pattern );

        assertFalse( this.parser.getErrorMessages().toString(),
                     this.parser.hasErrors() );

        final List constraints = pattern.getConstraint().getDescrs();
        assertEquals( 1,
                      constraints.size() );

        final PredicateDescr predicate = (PredicateDescr) constraints.get( 0 );
        assertEquals( " $var.equals(\"xyz\") ",
                      predicate.getContent() );
    }

    public void xxxtestEscapedStrings() throws Exception {
        final RuleDescr rule = parseResource( "escaped-string.drl" ).rule();

        assertNotNull( rule );

        assertEquals( "test_Quotes",
                      rule.getName() );

        final String expected = "String s = \"\\\"\\n\\t\\\\\";";

        assertEqualsIgnoreWhitespace( expected,
                                      (String) rule.getConsequence() );

        assertFalse( this.parser.getErrorMessages().toString(),
                     this.parser.hasErrors() );
    }

    public void xxxtestNestedCEs() throws Exception {
        final RuleDescr rule = parseResource( "nested_conditional_elements.drl" ).rule();

        assertFalse( this.parser.getErrorMessages().toString(),
                     this.parser.hasErrors() );

        assertNotNull( rule );

        final AndDescr root = rule.getLhs();
        final NotDescr not1 = (NotDescr) root.getDescrs().get( 0 );
        final AndDescr and1 = (AndDescr) not1.getDescrs().get( 0 );

        final PatternDescr state = (PatternDescr) and1.getDescrs().get( 0 );
        final NotDescr not2 = (NotDescr) and1.getDescrs().get( 1 );
        final AndDescr and2 = (AndDescr) not2.getDescrs().get( 0 );
        final PatternDescr person = (PatternDescr) and2.getDescrs().get( 0 );
        final PatternDescr cheese = (PatternDescr) and2.getDescrs().get( 1 );

        final PatternDescr person2 = (PatternDescr) root.getDescrs().get( 1 );
        final OrDescr or = (OrDescr) root.getDescrs().get( 2 );
        final PatternDescr cheese2 = (PatternDescr) or.getDescrs().get( 0 );
        final PatternDescr cheese3 = (PatternDescr) or.getDescrs().get( 1 );

        assertEquals( state.getObjectType(),
                      "State" );
        assertEquals( person.getObjectType(),
                      "Person" );
        assertEquals( cheese.getObjectType(),
                      "Cheese" );
        assertEquals( person2.getObjectType(),
                      "Person" );
        assertEquals( cheese2.getObjectType(),
                      "Cheese" );
        assertEquals( cheese3.getObjectType(),
                      "Cheese" );
    }

    public void xxxtestForall() throws Exception {
        final DRLParser parser = parseResource( "forall.drl" );
        parser.compilation_unit();

        assertFalse( parser.getErrorMessages().toString(),
                     parser.hasErrors() );

        final PackageDescr pack = parser.getPackageDescr();
        assertEquals( 1,
                      pack.getRules().size() );
        final RuleDescr rule = (RuleDescr) pack.getRules().get( 0 );
        assertEquals( 1,
                      rule.getLhs().getDescrs().size() );

        final ForallDescr forall = (ForallDescr) rule.getLhs().getDescrs().get( 0 );

        assertEquals( 2,
                      forall.getDescrs().size() );
        final PatternDescr pattern = forall.getBasePattern();
        assertEquals( "Person",
                      pattern.getObjectType() );
        final List remaining = forall.getRemainingPatterns();
        assertEquals( 1,
                      remaining.size() );
        final PatternDescr cheese = (PatternDescr) remaining.get( 0 );
        assertEquals( "Cheese",
                      cheese.getObjectType() );
    }

    public void xxxtestMemberof() throws Exception {
        final String text = "Country( $cities : city )\nPerson( city memberOf $cities )\n";
        final AndDescr descrs = new AndDescr();
        final CharStream charStream = new ANTLRStringStream( text );
        final DRLLexer lexer = new DRLLexer( charStream );
        final TokenStream tokenStream = new CommonTokenStream( lexer );
        final DRLParser parser = new DRLParser( tokenStream );
        parser.setLineOffset( descrs.getLine() );
        parser.normal_lhs_block( descrs );
        if ( parser.hasErrors() ) {
            System.err.println( parser.getErrorMessages() );
        }
        assertFalse( parser.hasErrors() );

        assertEquals( 2,
                      descrs.getDescrs().size() );
        PatternDescr pat = (PatternDescr) descrs.getDescrs().get( 1 );
        FieldConstraintDescr fieldConstr = (FieldConstraintDescr) pat.getConstraint().getDescrs().get( 0 );
        VariableRestrictionDescr restr = (VariableRestrictionDescr) fieldConstr.getRestrictions().get( 0 );

        assertEquals( "memberOf",
                      restr.getEvaluator() );
        assertFalse( restr.isNegated() );
        assertEquals( "$cities",
                      restr.getIdentifier() );
    }

    public void xxxtestNotMemberof() throws Exception {
        final String text = "Country( $cities : city )\nPerson( city not memberOf $cities )\n";
        final AndDescr descrs = new AndDescr();
        final CharStream charStream = new ANTLRStringStream( text );
        final DRLLexer lexer = new DRLLexer( charStream );
        final TokenStream tokenStream = new CommonTokenStream( lexer );
        final DRLParser parser = new DRLParser( tokenStream );
        parser.setLineOffset( descrs.getLine() );
        parser.normal_lhs_block( descrs );
        if ( parser.hasErrors() ) {
            System.err.println( parser.getErrorMessages() );
        }
        assertFalse( parser.hasErrors() );

        assertEquals( 2,
                      descrs.getDescrs().size() );
        PatternDescr pat = (PatternDescr) descrs.getDescrs().get( 1 );
        FieldConstraintDescr fieldConstr = (FieldConstraintDescr) pat.getConstraint().getDescrs().get( 0 );
        VariableRestrictionDescr restr = (VariableRestrictionDescr) fieldConstr.getRestrictions().get( 0 );

        assertEquals( "memberOf",
                      restr.getEvaluator() );
        assertTrue( restr.isNegated() );
        assertEquals( "$cities",
                      restr.getIdentifier() );
    }

    public void xxxtestInOperator() throws Exception {
        final RuleDescr rule = parseResource( "in_operator_test.drl" ).rule();

        assertFalse( this.parser.getErrors().toString(),
                     this.parser.hasErrors() );
        assertNotNull( rule );

        assertEqualsIgnoreWhitespace( "consequence();",
                                      (String) rule.getConsequence() );
        assertEquals( "simple_rule",
                      rule.getName() );
        assertEquals( 2,
                      rule.getLhs().getDescrs().size() );

        //The first pattern, with 2 restrictions on a single field (plus a connective)
        PatternDescr pattern = (PatternDescr) rule.getLhs().getDescrs().get( 0 );
        assertEquals( "Person",
                      pattern.getObjectType() );
        assertEquals( 1,
                      pattern.getConstraint().getDescrs().size() );

        FieldConstraintDescr fld = (FieldConstraintDescr) pattern.getConstraint().getDescrs().get( 0 );
        assertEquals( RestrictionConnectiveDescr.AND,
                      fld.getRestriction().getConnective() );
        assertEquals( 2,
                      fld.getRestrictions().size() );
        assertEquals( "age",
                      fld.getFieldName() );

        LiteralRestrictionDescr lit = (LiteralRestrictionDescr) fld.getRestrictions().get( 0 );
        assertEquals( ">",
                      lit.getEvaluator() );
        assertEquals( "30",
                      lit.getText() );

        lit = (LiteralRestrictionDescr) fld.getRestrictions().get( 1 );
        assertEquals( "<",
                      lit.getEvaluator() );
        assertEquals( "40",
                      lit.getText() );

        //the second col, with 2 fields, the first with 2 restrictions, the second field with one
        pattern = (PatternDescr) rule.getLhs().getDescrs().get( 1 );
        assertEquals( "Vehicle",
                      pattern.getObjectType() );
        assertEquals( 2,
                      pattern.getConstraint().getDescrs().size() );

        fld = (FieldConstraintDescr) pattern.getConstraint().getDescrs().get( 0 );
        assertEquals( "type",
                      fld.getFieldName() );
        assertEquals( 1,
                      fld.getRestrictions().size() );

        RestrictionConnectiveDescr or = (RestrictionConnectiveDescr) fld.getRestrictions().get( 0 );
        assertEquals( RestrictionConnectiveDescr.OR,
                      or.getConnective() );
        assertEquals( 2,
                      or.getRestrictions().size() );

        lit = (LiteralRestrictionDescr) or.getRestrictions().get( 0 );
        assertEquals( "==",
                      lit.getEvaluator() );
        assertEquals( "sedan",
                      lit.getText() );

        lit = (LiteralRestrictionDescr) or.getRestrictions().get( 1 );
        assertEquals( "==",
                      lit.getEvaluator() );
        assertEquals( "wagon",
                      lit.getText() );

        //now the second field
        fld = (FieldConstraintDescr) pattern.getConstraint().getDescrs().get( 1 );
        assertEquals( 1,
                      fld.getRestrictions().size() );
        lit = (LiteralRestrictionDescr) fld.getRestrictions().get( 0 );
        assertEquals( "<",
                      lit.getEvaluator() );
        assertEquals( "3",
                      lit.getText() );

    }

    public void xxxtestNotInOperator() throws Exception {
        final RuleDescr rule = parseResource( "notin_operator_test.drl" ).rule();

        assertFalse( this.parser.getErrors().toString(),
                     this.parser.hasErrors() );
        assertNotNull( rule );

        assertEqualsIgnoreWhitespace( "consequence();",
                                      (String) rule.getConsequence() );
        assertEquals( "simple_rule",
                      rule.getName() );
        assertEquals( 2,
                      rule.getLhs().getDescrs().size() );

        //The first pattern, with 2 restrictions on a single field (plus a connective)
        PatternDescr pattern = (PatternDescr) rule.getLhs().getDescrs().get( 0 );
        assertEquals( "Person",
                      pattern.getObjectType() );
        assertEquals( 1,
                      pattern.getConstraint().getDescrs().size() );

        FieldConstraintDescr fld = (FieldConstraintDescr) pattern.getConstraint().getDescrs().get( 0 );
        assertEquals( 2,
                      fld.getRestrictions().size() );
        assertEquals( "age",
                      fld.getFieldName() );

        LiteralRestrictionDescr lit = (LiteralRestrictionDescr) fld.getRestrictions().get( 0 );
        assertEquals( ">",
                      lit.getEvaluator() );
        assertEquals( "30",
                      lit.getText() );

        lit = (LiteralRestrictionDescr) fld.getRestrictions().get( 1 );
        assertEquals( "<",
                      lit.getEvaluator() );
        assertEquals( "40",
                      lit.getText() );

        //the second col, with 2 fields, the first with 2 restrictions, the second field with one
        pattern = (PatternDescr) rule.getLhs().getDescrs().get( 1 );
        assertEquals( "Vehicle",
                      pattern.getObjectType() );
        assertEquals( 2,
                      pattern.getConstraint().getDescrs().size() );

        fld = (FieldConstraintDescr) pattern.getConstraint().getDescrs().get( 0 );
        assertEquals( 2,
                      fld.getRestrictions().size() );
        lit = (LiteralRestrictionDescr) fld.getRestrictions().get( 0 );
        assertEquals( "type",
                      fld.getFieldName() );
        assertEquals( "!=",
                      lit.getEvaluator() );
        assertEquals( "sedan",
                      lit.getText() );

        lit = (LiteralRestrictionDescr) fld.getRestrictions().get( 1 );
        assertEquals( "!=",
                      lit.getEvaluator() );
        assertEquals( "wagon",
                      lit.getText() );

        //now the second field
        fld = (FieldConstraintDescr) pattern.getConstraint().getDescrs().get( 1 );
        assertEquals( 1,
                      fld.getRestrictions().size() );
        lit = (LiteralRestrictionDescr) fld.getRestrictions().get( 0 );
        assertEquals( "<",
                      lit.getEvaluator() );
        assertEquals( "3",
                      lit.getText() );

    }

    public void xxxtestConstraintAndConnective() throws Exception {
        final String text = "Person( age < 42 && location==\"atlanta\")";
        final CharStream charStream = new ANTLRStringStream( text );
        final DRLLexer lexer = new DRLLexer( charStream );
        final TokenStream tokenStream = new CommonTokenStream( lexer );
        final DRLParser parser = new DRLParser( tokenStream );

        PatternDescr pattern = (PatternDescr) parser.fact( null );
        assertFalse( parser.getErrorMessages().toString(),
                     parser.hasErrors() );

        assertEquals( 2,
                      pattern.getDescrs().size() );
        FieldConstraintDescr fcd = (FieldConstraintDescr) pattern.getDescrs().get( 0 );
        assertEquals( "age",
                      fcd.getFieldName() );
        fcd = (FieldConstraintDescr) pattern.getDescrs().get( 1 );
        assertEquals( "location",
                      fcd.getFieldName() );
    }

    public void xxxtestConstraintOrConnective() throws Exception {
        final String text = "Person( age < 42 || location==\"atlanta\")";
        final CharStream charStream = new ANTLRStringStream( text );
        final DRLLexer lexer = new DRLLexer( charStream );
        final TokenStream tokenStream = new CommonTokenStream( lexer );
        final DRLParser parser = new DRLParser( tokenStream );

        PatternDescr pattern = (PatternDescr) parser.fact( null );
        assertFalse( parser.getErrorMessages().toString(),
                     parser.hasErrors() );

        assertEquals( 1,
                      pattern.getDescrs().size() );
        OrDescr or = (OrDescr) pattern.getDescrs().get( 0 );
        assertEquals( 2,
                      or.getDescrs().size() );
        FieldConstraintDescr fcd = (FieldConstraintDescr) or.getDescrs().get( 0 );
        assertEquals( "age",
                      fcd.getFieldName() );
        fcd = (FieldConstraintDescr) or.getDescrs().get( 1 );
        assertEquals( "location",
                      fcd.getFieldName() );
    }

    public void xxxtestConstraintConnectivesPrecedence() throws Exception {
        final String text = "Person( age < 42 && location==\"atlanta\" || age > 20 && location==\"Seatle\" || location == \"Chicago\")";
        final CharStream charStream = new ANTLRStringStream( text );
        final DRLLexer lexer = new DRLLexer( charStream );
        final TokenStream tokenStream = new CommonTokenStream( lexer );
        final DRLParser parser = new DRLParser( tokenStream );

        PatternDescr pattern = (PatternDescr) parser.fact( null );
        assertFalse( parser.getErrorMessages().toString(),
                     parser.hasErrors() );

        assertEquals( 1,
                      pattern.getDescrs().size() );
        OrDescr or = (OrDescr) pattern.getDescrs().get( 0 );
        assertEquals( 3,
                      or.getDescrs().size() );

        AndDescr and = (AndDescr) or.getDescrs().get( 0 );
        assertEquals( 2,
                      and.getDescrs().size() );
        FieldConstraintDescr fcd = (FieldConstraintDescr) and.getDescrs().get( 0 );
        assertEquals( "age",
                      fcd.getFieldName() );
        assertEquals( "<",
                      ((LiteralRestrictionDescr) fcd.getRestrictions().get( 0 )).getEvaluator() );
        assertEquals( "42",
                      ((LiteralRestrictionDescr) fcd.getRestrictions().get( 0 )).getText() );
        fcd = (FieldConstraintDescr) and.getDescrs().get( 1 );
        assertEquals( "location",
                      fcd.getFieldName() );
        assertEquals( "==",
                      ((LiteralRestrictionDescr) fcd.getRestrictions().get( 0 )).getEvaluator() );
        assertEquals( "atlanta",
                      ((LiteralRestrictionDescr) fcd.getRestrictions().get( 0 )).getText() );

        and = (AndDescr) or.getDescrs().get( 1 );
        assertEquals( 2,
                      and.getDescrs().size() );
        fcd = (FieldConstraintDescr) and.getDescrs().get( 0 );
        assertEquals( "age",
                      fcd.getFieldName() );
        assertEquals( ">",
                      ((LiteralRestrictionDescr) fcd.getRestrictions().get( 0 )).getEvaluator() );
        assertEquals( "20",
                      ((LiteralRestrictionDescr) fcd.getRestrictions().get( 0 )).getText() );
        fcd = (FieldConstraintDescr) and.getDescrs().get( 1 );
        assertEquals( "location",
                      fcd.getFieldName() );
        assertEquals( "==",
                      ((LiteralRestrictionDescr) fcd.getRestrictions().get( 0 )).getEvaluator() );
        assertEquals( "Seatle",
                      ((LiteralRestrictionDescr) fcd.getRestrictions().get( 0 )).getText() );

        fcd = (FieldConstraintDescr) or.getDescrs().get( 2 );
        assertEquals( "location",
                      fcd.getFieldName() );
        assertEquals( "==",
                      ((LiteralRestrictionDescr) fcd.getRestrictions().get( 0 )).getEvaluator() );
        assertEquals( "Chicago",
                      ((LiteralRestrictionDescr) fcd.getRestrictions().get( 0 )).getText() );

    }

    public void xxxtestConstraintConnectivesPrecedenceWithBracks() throws Exception {
        final String text = "Person( age < 42 && ( location==\"atlanta\" || age > 20 && location==\"Seatle\") || location == \"Chicago\")";
        final CharStream charStream = new ANTLRStringStream( text );
        final DRLLexer lexer = new DRLLexer( charStream );
        final TokenStream tokenStream = new CommonTokenStream( lexer );
        final DRLParser parser = new DRLParser( tokenStream );

        PatternDescr pattern = (PatternDescr) parser.fact( null );
        assertFalse( parser.getErrorMessages().toString(),
                     parser.hasErrors() );

        assertEquals( 1,
                      pattern.getDescrs().size() );
        OrDescr or1 = (OrDescr) pattern.getDescrs().get( 0 );
        assertEquals( 2,
                      or1.getDescrs().size() );

        AndDescr and1 = (AndDescr) or1.getDescrs().get( 0 );
        assertEquals( 2,
                      and1.getDescrs().size() );
        FieldConstraintDescr fcd = (FieldConstraintDescr) and1.getDescrs().get( 0 );
        assertEquals( "age",
                      fcd.getFieldName() );
        assertEquals( "<",
                      ((LiteralRestrictionDescr) fcd.getRestrictions().get( 0 )).getEvaluator() );
        assertEquals( "42",
                      ((LiteralRestrictionDescr) fcd.getRestrictions().get( 0 )).getText() );

        OrDescr or2 = (OrDescr) and1.getDescrs().get( 1 );
        fcd = (FieldConstraintDescr) or2.getDescrs().get( 0 );
        assertEquals( "location",
                      fcd.getFieldName() );
        assertEquals( "==",
                      ((LiteralRestrictionDescr) fcd.getRestrictions().get( 0 )).getEvaluator() );
        assertEquals( "atlanta",
                      ((LiteralRestrictionDescr) fcd.getRestrictions().get( 0 )).getText() );

        AndDescr and2 = (AndDescr) or2.getDescrs().get( 1 );
        assertEquals( 2,
                      and2.getDescrs().size() );
        fcd = (FieldConstraintDescr) and2.getDescrs().get( 0 );
        assertEquals( "age",
                      fcd.getFieldName() );
        assertEquals( ">",
                      ((LiteralRestrictionDescr) fcd.getRestrictions().get( 0 )).getEvaluator() );
        assertEquals( "20",
                      ((LiteralRestrictionDescr) fcd.getRestrictions().get( 0 )).getText() );
        fcd = (FieldConstraintDescr) and2.getDescrs().get( 1 );
        assertEquals( "location",
                      fcd.getFieldName() );
        assertEquals( "==",
                      ((LiteralRestrictionDescr) fcd.getRestrictions().get( 0 )).getEvaluator() );
        assertEquals( "Seatle",
                      ((LiteralRestrictionDescr) fcd.getRestrictions().get( 0 )).getText() );

        fcd = (FieldConstraintDescr) or1.getDescrs().get( 1 );
        assertEquals( "location",
                      fcd.getFieldName() );
        assertEquals( "==",
                      ((LiteralRestrictionDescr) fcd.getRestrictions().get( 0 )).getEvaluator() );
        assertEquals( "Chicago",
                      ((LiteralRestrictionDescr) fcd.getRestrictions().get( 0 )).getText() );
    }

    public void xxxtestConstraintConnectivesPrecedenceWithBracks2() throws Exception {
        final String text = "Person( ( age == 70 && hair == \"black\" ) || ( age == 40 && hair == \"pink\" ) || ( age == 12 && ( hair == \"yellow\" || hair == \"blue\" ) ) )";
        final CharStream charStream = new ANTLRStringStream( text );
        final DRLLexer lexer = new DRLLexer( charStream );
        final TokenStream tokenStream = new CommonTokenStream( lexer );
        final DRLParser parser = new DRLParser( tokenStream );

        PatternDescr pattern = (PatternDescr) parser.fact( null );
        assertFalse( parser.getErrorMessages().toString(),
                     parser.hasErrors() );

        assertEquals( 1,
                      pattern.getDescrs().size() );
        OrDescr or1 = (OrDescr) pattern.getDescrs().get( 0 );
        assertEquals( 3,
                      or1.getDescrs().size() );

        AndDescr and1 = (AndDescr) or1.getDescrs().get( 0 );
        AndDescr and2 = (AndDescr) or1.getDescrs().get( 1 );
        AndDescr and3 = (AndDescr) or1.getDescrs().get( 2 );

        assertEquals( 2,
                      and1.getDescrs().size() );
        FieldConstraintDescr fcd = (FieldConstraintDescr) and1.getDescrs().get( 0 );
        assertEquals( "age",
                      fcd.getFieldName() );
        assertEquals( "==",
                      ((LiteralRestrictionDescr) fcd.getRestrictions().get( 0 )).getEvaluator() );
        assertEquals( "70",
                      ((LiteralRestrictionDescr) fcd.getRestrictions().get( 0 )).getText() );
        fcd = (FieldConstraintDescr) and1.getDescrs().get( 1 );
        assertEquals( "hair",
                      fcd.getFieldName() );
        assertEquals( "==",
                      ((LiteralRestrictionDescr) fcd.getRestrictions().get( 0 )).getEvaluator() );
        assertEquals( "black",
                      ((LiteralRestrictionDescr) fcd.getRestrictions().get( 0 )).getText() );

        assertEquals( 2,
                      and2.getDescrs().size() );
        fcd = (FieldConstraintDescr) and2.getDescrs().get( 0 );
        assertEquals( "age",
                      fcd.getFieldName() );
        assertEquals( "==",
                      ((LiteralRestrictionDescr) fcd.getRestrictions().get( 0 )).getEvaluator() );
        assertEquals( "40",
                      ((LiteralRestrictionDescr) fcd.getRestrictions().get( 0 )).getText() );
        fcd = (FieldConstraintDescr) and2.getDescrs().get( 1 );
        assertEquals( "hair",
                      fcd.getFieldName() );
        assertEquals( "==",
                      ((LiteralRestrictionDescr) fcd.getRestrictions().get( 0 )).getEvaluator() );
        assertEquals( "pink",
                      ((LiteralRestrictionDescr) fcd.getRestrictions().get( 0 )).getText() );

        assertEquals( 2,
                      and3.getDescrs().size() );
        fcd = (FieldConstraintDescr) and3.getDescrs().get( 0 );
        assertEquals( "age",
                      fcd.getFieldName() );
        assertEquals( "==",
                      ((LiteralRestrictionDescr) fcd.getRestrictions().get( 0 )).getEvaluator() );
        assertEquals( "12",
                      ((LiteralRestrictionDescr) fcd.getRestrictions().get( 0 )).getText() );
        OrDescr or2 = (OrDescr) and3.getDescrs().get( 1 );
        fcd = (FieldConstraintDescr) or2.getDescrs().get( 0 );
        assertEquals( "hair",
                      fcd.getFieldName() );
        assertEquals( "==",
                      ((LiteralRestrictionDescr) fcd.getRestrictions().get( 0 )).getEvaluator() );
        assertEquals( "yellow",
                      ((LiteralRestrictionDescr) fcd.getRestrictions().get( 0 )).getText() );
        fcd = (FieldConstraintDescr) or2.getDescrs().get( 1 );
        assertEquals( "hair",
                      fcd.getFieldName() );
        assertEquals( "==",
                      ((LiteralRestrictionDescr) fcd.getRestrictions().get( 0 )).getEvaluator() );
        assertEquals( "blue",
                      ((LiteralRestrictionDescr) fcd.getRestrictions().get( 0 )).getText() );
    }

    public void xxxtestRestrictionConnectives() throws Exception {

        // the bellow expression must generate the following tree:
        //
        //                       AND
        //                        |
        //                       OR
        //        /---------------+-------------------\
        //       AND             AND                 AND
        //    /---+---\       /---+---\           /---+---\
        //   FC       FC     FC       FC         FC       OR
        //                                             /---+---\
        //                                            FC       FC
        //
        final String text = "Person( ( age ( > 60 && < 70 ) || ( > 50 && < 55 ) && hair == \"black\" ) || ( age == 40 && hair == \"pink\" ) || ( age == 12 && ( hair == \"yellow\" || hair == \"blue\" ) ))";
        final CharStream charStream = new ANTLRStringStream( text );
        final DRLLexer lexer = new DRLLexer( charStream );
        final TokenStream tokenStream = new CommonTokenStream( lexer );
        final DRLParser parser = new DRLParser( tokenStream );

        PatternDescr pattern = (PatternDescr) parser.fact( null );
        assertFalse( parser.getErrorMessages().toString(),
                     parser.hasErrors() );

        assertEquals( 1,
                      pattern.getDescrs().size() );

        OrDescr orConstr = (OrDescr) pattern.getDescrs().get( 0 );

        assertEquals( 3,
                      orConstr.getDescrs().size() );

        AndDescr andConstr1 = (AndDescr) orConstr.getDescrs().get( 0 );

        FieldConstraintDescr fcd = (FieldConstraintDescr) andConstr1.getDescrs().get( 0 );
        assertEquals( "age",
                      fcd.getFieldName() );
        RestrictionConnectiveDescr or = (RestrictionConnectiveDescr) fcd.getRestriction().getRestrictions().get( 0 );
        RestrictionConnectiveDescr and1 = (RestrictionConnectiveDescr) or.getRestrictions().get( 0 );
        RestrictionConnectiveDescr and2 = (RestrictionConnectiveDescr) or.getRestrictions().get( 1 );

        assertEquals( ">",
                      ((LiteralRestrictionDescr) and1.getRestrictions().get( 0 )).getEvaluator() );
        assertEquals( "60",
                      ((LiteralRestrictionDescr) and1.getRestrictions().get( 0 )).getText() );

        assertEquals( "<",
                      ((LiteralRestrictionDescr) and1.getRestrictions().get( 1 )).getEvaluator() );
        assertEquals( "70",
                      ((LiteralRestrictionDescr) and1.getRestrictions().get( 1 )).getText() );

        assertEquals( ">",
                      ((LiteralRestrictionDescr) and2.getRestrictions().get( 0 )).getEvaluator() );
        assertEquals( "50",
                      ((LiteralRestrictionDescr) and2.getRestrictions().get( 0 )).getText() );

        assertEquals( "<",
                      ((LiteralRestrictionDescr) and2.getRestrictions().get( 1 )).getEvaluator() );
        assertEquals( "55",
                      ((LiteralRestrictionDescr) and2.getRestrictions().get( 1 )).getText() );

        fcd = (FieldConstraintDescr) andConstr1.getDescrs().get( 1 );
        assertEquals( "hair",
                      fcd.getFieldName() );

        assertEquals( "==",
                      ((LiteralRestrictionDescr) fcd.getRestrictions().get( 0 )).getEvaluator() );
        assertEquals( "black",
                      ((LiteralRestrictionDescr) fcd.getRestrictions().get( 0 )).getText() );

        AndDescr andConstr2 = (AndDescr) orConstr.getDescrs().get( 1 );
        assertEquals( 2,
                      andConstr2.getDescrs().size() );
        fcd = (FieldConstraintDescr) andConstr2.getDescrs().get( 0 );
        assertEquals( "age",
                      fcd.getFieldName() );

        assertEquals( "==",
                      ((LiteralRestrictionDescr) fcd.getRestrictions().get( 0 )).getEvaluator() );
        assertEquals( "40",
                      ((LiteralRestrictionDescr) fcd.getRestrictions().get( 0 )).getText() );

        fcd = (FieldConstraintDescr) andConstr2.getDescrs().get( 1 );
        assertEquals( "hair",
                      fcd.getFieldName() );

        assertEquals( "==",
                      ((LiteralRestrictionDescr) fcd.getRestrictions().get( 0 )).getEvaluator() );
        assertEquals( "pink",
                      ((LiteralRestrictionDescr) fcd.getRestrictions().get( 0 )).getText() );

        AndDescr andConstr3 = (AndDescr) orConstr.getDescrs().get( 2 );
        assertEquals( 2,
                      andConstr3.getDescrs().size() );
        fcd = (FieldConstraintDescr) andConstr3.getDescrs().get( 0 );
        assertEquals( "age",
                      fcd.getFieldName() );

        assertEquals( "==",
                      ((LiteralRestrictionDescr) fcd.getRestrictions().get( 0 )).getEvaluator() );
        assertEquals( "12",
                      ((LiteralRestrictionDescr) fcd.getRestrictions().get( 0 )).getText() );

        OrDescr orConstr2 = (OrDescr) andConstr3.getDescrs().get( 1 );

        fcd = (FieldConstraintDescr) orConstr2.getDescrs().get( 0 );
        assertEquals( "hair",
                      fcd.getFieldName() );

        assertEquals( "==",
                      ((LiteralRestrictionDescr) fcd.getRestrictions().get( 0 )).getEvaluator() );
        assertEquals( "yellow",
                      ((LiteralRestrictionDescr) fcd.getRestrictions().get( 0 )).getText() );

        fcd = (FieldConstraintDescr) orConstr2.getDescrs().get( 1 );
        assertEquals( "hair",
                      fcd.getFieldName() );

        assertEquals( "==",
                      ((LiteralRestrictionDescr) fcd.getRestrictions().get( 0 )).getEvaluator() );
        assertEquals( "blue",
                      ((LiteralRestrictionDescr) fcd.getRestrictions().get( 0 )).getText() );

    }

    public void xxxtestNotContains() throws Exception {
        final String text = "City( $city : city )\nCountry( cities not contains $city )\n";
        final AndDescr descrs = new AndDescr();
        final CharStream charStream = new ANTLRStringStream( text );
        final DRLLexer lexer = new DRLLexer( charStream );
        final TokenStream tokenStream = new CommonTokenStream( lexer );
        final DRLParser parser = new DRLParser( tokenStream );
        parser.setLineOffset( descrs.getLine() );
        parser.normal_lhs_block( descrs );
        assertFalse( parser.getErrorMessages().toString(),
                     parser.hasErrors() );

        assertEquals( 2,
                      descrs.getDescrs().size() );
        PatternDescr pat = (PatternDescr) descrs.getDescrs().get( 1 );
        FieldConstraintDescr fieldConstr = (FieldConstraintDescr) pat.getConstraint().getDescrs().get( 0 );
        VariableRestrictionDescr restr = (VariableRestrictionDescr) fieldConstr.getRestrictions().get( 0 );

        assertEquals( "contains",
                      restr.getEvaluator() );
        assertTrue( restr.isNegated() );
        assertEquals( "$city",
                      restr.getIdentifier() );
    }

    public void xxxtestNotMatches() throws Exception {
        final String text = "Message( text not matches '[abc]*' )\n";
        final AndDescr descrs = new AndDescr();
        final CharStream charStream = new ANTLRStringStream( text );
        final DRLLexer lexer = new DRLLexer( charStream );
        final TokenStream tokenStream = new CommonTokenStream( lexer );
        final DRLParser parser = new DRLParser( tokenStream );
        parser.setLineOffset( descrs.getLine() );
        parser.normal_lhs_block( descrs );
        assertFalse( parser.getErrorMessages().toString(),
                     parser.hasErrors() );

        assertEquals( 1,
                      descrs.getDescrs().size() );
        PatternDescr pat = (PatternDescr) descrs.getDescrs().get( 0 );
        FieldConstraintDescr fieldConstr = (FieldConstraintDescr) pat.getConstraint().getDescrs().get( 0 );
        LiteralRestrictionDescr restr = (LiteralRestrictionDescr) fieldConstr.getRestrictions().get( 0 );

        assertEquals( "matches",
                      restr.getEvaluator() );
        assertTrue( restr.isNegated() );
        assertEquals( "[abc]*",
                      restr.getText() );
    }

    public void testRestrictions() throws Exception {
        final String text = "Foo( bar > 1 || == 1 )\n";
        final AndDescr descrs = new AndDescr();
        final CharStream charStream = new ANTLRStringStream( text );
        final DRLLexer lexer = new DRLLexer( charStream );
        final TokenStream tokenStream = new CommonTokenStream( lexer );
        final DRLParser parser = new DRLParser( tokenStream );
        parser.setLineOffset( descrs.getLine() );
        parser.normal_lhs_block( descrs );
        assertFalse( parser.getErrorMessages().toString(),
                     parser.hasErrors() );

        assertEquals( 1,
                      descrs.getDescrs().size() );
        PatternDescr pat = (PatternDescr) descrs.getDescrs().get( 0 );
        FieldConstraintDescr fieldConstr = (FieldConstraintDescr) pat.getConstraint().getDescrs().get( 0 );
        RestrictionConnectiveDescr or = (RestrictionConnectiveDescr) fieldConstr.getRestrictions().get( 0 );
        LiteralRestrictionDescr gt1 = (LiteralRestrictionDescr) or.getRestrictions().get( 0 );
        LiteralRestrictionDescr eq1 = (LiteralRestrictionDescr) or.getRestrictions().get( 1 );
        
        assertEquals( ">", gt1.getEvaluator() );
        assertEquals( false, gt1.isNegated() );
        assertEquals( 1, ((Number) eq1.getValue()).intValue() );
        assertEquals( "==", eq1.getEvaluator() );
        assertEquals( false, eq1.isNegated() );
        assertEquals( 1, ((Number) eq1.getValue()).intValue() );
        
    }

    public void xxxtestSemicolon() throws Exception {
        parseResource( "semicolon.drl" );

        this.parser.compilation_unit();

        assertFalse( this.parser.getErrorMessages().toString(),
                     this.parser.hasErrors() );

        final PackageDescr pkg = this.parser.getPackageDescr();
        assertEquals( "org.drools",
                      pkg.getName() );
        assertEquals( 1,
                      pkg.getGlobals().size() );
        assertEquals( 3,
                      pkg.getRules().size() );

        final RuleDescr rule1 = (RuleDescr) pkg.getRules().get( 0 );
        assertEquals( 2,
                      rule1.getLhs().getDescrs().size() );

        final RuleDescr query1 = (RuleDescr) pkg.getRules().get( 1 );
        assertEquals( 3,
                      query1.getLhs().getDescrs().size() );

        final RuleDescr rule2 = (RuleDescr) pkg.getRules().get( 2 );
        assertEquals( 2,
                      rule2.getLhs().getDescrs().size() );
    }

    public void xxxtestEval() throws Exception {
        parseResource( "eval_parsing.drl" );

        this.parser.compilation_unit();

        assertFalse( this.parser.getErrorMessages().toString(),
                     this.parser.hasErrors() );

        final PackageDescr pkg = this.parser.getPackageDescr();
        assertEquals( "org.drools",
                      pkg.getName() );
        assertEquals( 1,
                      pkg.getRules().size() );

        final RuleDescr rule1 = (RuleDescr) pkg.getRules().get( 0 );
        assertEquals( 1,
                      rule1.getLhs().getDescrs().size() );
    }

    public void xxxtestAccumulateReverse() throws Exception {
        final DRLParser parser = parseResource( "accumulateReverse.drl" );
        parser.compilation_unit();

        if ( parser.hasErrors() ) {
            System.err.println( parser.getErrorMessages() );
        }

        assertFalse( parser.getErrorMessages().toString(),
                     parser.hasErrors() );

        final PackageDescr pack = parser.getPackageDescr();
        assertEquals( 1,
                      pack.getRules().size() );
        final RuleDescr rule = (RuleDescr) pack.getRules().get( 0 );
        assertEquals( 1,
                      rule.getLhs().getDescrs().size() );

        final PatternDescr out = (PatternDescr) rule.getLhs().getDescrs().get( 0 );
        final AccumulateDescr accum = (AccumulateDescr) out.getSource();
        assertEqualsIgnoreWhitespace( "int x = 0 ;",
                                      accum.getInitCode() );
        assertEqualsIgnoreWhitespace( "x++;",
                                      accum.getActionCode() );
        assertEqualsIgnoreWhitespace( "x--;",
                                      accum.getReverseCode() );
        assertEqualsIgnoreWhitespace( "new Integer(x)",
                                      accum.getResultCode() );
        assertFalse( accum.isExternalFunction() );

        final PatternDescr pattern = (PatternDescr) accum.getInputPattern();
        assertEquals( "Person",
                      pattern.getObjectType() );
    }

    public void xxxtestAccumulateExternalFunction() throws Exception {
        final DRLParser parser = parseResource( "accumulateExternalFunction.drl" );
        parser.compilation_unit();

        if ( parser.hasErrors() ) {
            System.err.println( parser.getErrorMessages() );
        }

        assertFalse( parser.getErrorMessages().toString(),
                     parser.hasErrors() );

        final PackageDescr pack = parser.getPackageDescr();
        assertEquals( 1,
                      pack.getRules().size() );
        final RuleDescr rule = (RuleDescr) pack.getRules().get( 0 );
        assertEquals( 1,
                      rule.getLhs().getDescrs().size() );

        final PatternDescr out = (PatternDescr) rule.getLhs().getDescrs().get( 0 );
        final AccumulateDescr accum = (AccumulateDescr) out.getSource();
        assertEqualsIgnoreWhitespace( "$age",
                                      accum.getExpression() );
        assertEqualsIgnoreWhitespace( "average",
                                      accum.getFunctionIdentifier() );
        assertTrue( accum.isExternalFunction() );

        final PatternDescr pattern = (PatternDescr) accum.getInputPattern();
        assertEquals( "Person",
                      pattern.getObjectType() );
    }

    public void xxxtestCollectWithNestedFrom() throws Exception {
        final DRLParser parser = parseResource( "collect_with_nested_from.drl" );
        parser.compilation_unit();

        if ( parser.hasErrors() ) {
            System.err.println( parser.getErrorMessages() );
        }

        assertFalse( parser.hasErrors() );

        final PackageDescr pack = parser.getPackageDescr();
        assertEquals( 1,
                      pack.getRules().size() );
        final RuleDescr rule = (RuleDescr) pack.getRules().get( 0 );
        assertEquals( 1,
                      rule.getLhs().getDescrs().size() );

        final PatternDescr out = (PatternDescr) rule.getLhs().getDescrs().get( 0 );
        final CollectDescr collect = (CollectDescr) out.getSource();

        PatternDescr person = (PatternDescr) collect.getInputPattern();
        assertEquals( "Person",
                      person.getObjectType() );

        final CollectDescr collect2 = (CollectDescr) person.getSource();

        final PatternDescr people = collect2.getInputPattern();
        assertEquals( "People",
                      people.getObjectType() );
    }

    public void xxxtestAccumulateWithNestedFrom() throws Exception {
        final DRLParser parser = parseResource( "accumulate_with_nested_from.drl" );
        parser.compilation_unit();

        if ( parser.hasErrors() ) {
            System.err.println( parser.getErrorMessages() );
        }

        assertFalse( parser.hasErrors() );

        final PackageDescr pack = parser.getPackageDescr();
        assertEquals( 1,
                      pack.getRules().size() );
        final RuleDescr rule = (RuleDescr) pack.getRules().get( 0 );
        assertEquals( 1,
                      rule.getLhs().getDescrs().size() );

        final PatternDescr out = (PatternDescr) rule.getLhs().getDescrs().get( 0 );
        final AccumulateDescr accumulate = (AccumulateDescr) out.getSource();

        PatternDescr person = (PatternDescr) accumulate.getInputPattern();
        assertEquals( "Person",
                      person.getObjectType() );

        final CollectDescr collect2 = (CollectDescr) person.getSource();

        final PatternDescr people = collect2.getInputPattern();
        assertEquals( "People",
                      people.getObjectType() );
    }

    public void xxxtestAccessorPaths() throws Exception {
        final String text = "org   .   drools/*comment*/\t  .Message( text not matches $c#comment\n. property )\n";
        final AndDescr descrs = new AndDescr();
        final CharStream charStream = new ANTLRStringStream( text );
        final DRLLexer lexer = new DRLLexer( charStream );
        final TokenStream tokenStream = new CommonTokenStream( lexer );
        final DRLParser parser = new DRLParser( tokenStream );
        parser.setLineOffset( descrs.getLine() );
        parser.normal_lhs_block( descrs );
        assertFalse( parser.getErrorMessages().toString(),
                     parser.hasErrors() );

        assertEquals( 1,
                      descrs.getDescrs().size() );
        PatternDescr pat = (PatternDescr) descrs.getDescrs().get( 0 );
        assertEquals( "org.drools.Message",
                      pat.getObjectType() );

        FieldConstraintDescr fieldConstr = (FieldConstraintDescr) pat.getConstraint().getDescrs().get( 0 );
        QualifiedIdentifierRestrictionDescr restr = (QualifiedIdentifierRestrictionDescr) fieldConstr.getRestrictions().get( 0 );

        assertEquals( "matches",
                      restr.getEvaluator() );
        assertTrue( restr.isNegated() );
        assertEquals( "$c.property",
                      restr.getText() );
    }

    public void xxxtestOrCE() throws Exception {
        final DRLParser parser = parseResource( "or_ce.drl" );
        parser.compilation_unit();

        assertFalse( parser.getErrorMessages().toString(),
                     parser.hasErrors() );

        final PackageDescr pack = parser.getPackageDescr();
        assertEquals( 1,
                      pack.getRules().size() );
        final RuleDescr rule = (RuleDescr) pack.getRules().get( 0 );
        assertEquals( 2,
                      rule.getLhs().getDescrs().size() );

        final PatternDescr person = (PatternDescr) rule.getLhs().getDescrs().get( 0 );
        assertEquals( "Person",
                      person.getObjectType() );
        assertEquals( "$p",
                      person.getIdentifier() );

        final OrDescr or = (OrDescr) rule.getLhs().getDescrs().get( 1 );
        assertEquals( 2,
                      or.getDescrs().size() );

        final PatternDescr cheese1 = (PatternDescr) or.getDescrs().get( 0 );
        assertEquals( "Cheese",
                      cheese1.getObjectType() );
        assertEquals( "$c",
                      cheese1.getIdentifier() );
        final PatternDescr cheese2 = (PatternDescr) or.getDescrs().get( 1 );
        assertEquals( "Cheese",
                      cheese2.getObjectType() );
        assertNull( cheese2.getIdentifier() );
    }

    public void xxxtestRuleParseLhs2() throws Exception {
        final String text = "Message( Message.HELLO )\n";
        final AndDescr descrs = new AndDescr();
        final CharStream charStream = new ANTLRStringStream( text );
        final DRLLexer lexer = new DRLLexer( charStream );
        final TokenStream tokenStream = new CommonTokenStream( lexer );
        final DRLParser parser = new DRLParser( tokenStream );
        parser.setLineOffset( descrs.getLine() );
        parser.normal_lhs_block( descrs );
        assertTrue( parser.hasErrors() );
    }
    
    public void xxxtestRuleSingleLine() throws Exception {
        final String text = "rule \"another test\" salience 10 when eval( true ) then System.out.println(1); end";
        final CharStream charStream = new ANTLRStringStream( text );
        final DRLLexer lexer = new DRLLexer( charStream );
        final TokenStream tokenStream = new CommonTokenStream( lexer );
        final DRLParser parser = new DRLParser( tokenStream );
        parser.setLineOffset( 10 );
        RuleDescr rule = parser.rule();
        
        assertFalse( parser.hasErrors() );
        assertEquals( "another test", rule.getName() );
        assertEquals( "System.out.println(1); ", rule.getConsequence());
    }
    
    public void xxxtestRuleTwoLines() throws Exception {
        final String text = "rule \"another test\" salience 10 when eval( true ) then System.out.println(1);\n end";
        final CharStream charStream = new ANTLRStringStream( text );
        final DRLLexer lexer = new DRLLexer( charStream );
        final TokenStream tokenStream = new CommonTokenStream( lexer );
        final DRLParser parser = new DRLParser( tokenStream );
        parser.setLineOffset( 10 );
        RuleDescr rule = parser.rule();
        
        assertFalse( parser.hasErrors() );
        assertEquals( "another test", rule.getName() );
        assertEquals( "System.out.println(1);\n ", rule.getConsequence());
    }

    public void xxxtestRuleParseLhs3() throws Exception {
        final String text = "(or\nnot Person()\n(and Cheese()\nMeat()\nWine()))";
        final AndDescr descrs = new AndDescr();
        final CharStream charStream = new ANTLRStringStream( text );
        final DRLLexer lexer = new DRLLexer( charStream );
        final TokenStream tokenStream = new CommonTokenStream( lexer );
        final DRLParser parser = new DRLParser( tokenStream );
        parser.setLineOffset( descrs.getLine() );
        parser.normal_lhs_block( descrs );
        if ( parser.hasErrors() ) {
            System.err.println( parser.getErrorMessages() );
        }
        assertFalse( parser.hasErrors() );
        assertEquals( 1,
                      descrs.getDescrs().size() );
        OrDescr or = (OrDescr) descrs.getDescrs().get( 0 );
        assertEquals( 2,
                      or.getDescrs().size() );
        NotDescr not = (NotDescr) or.getDescrs().get( 0 );
        AndDescr and = (AndDescr) or.getDescrs().get( 1 );
        assertEquals( 1,
                      not.getDescrs().size() );
        PatternDescr person = (PatternDescr) not.getDescrs().get( 0 );
        assertEquals( "Person",
                      person.getObjectType() );
        assertEquals( 3,
                      and.getDescrs().size() );
        PatternDescr cheese = (PatternDescr) and.getDescrs().get( 0 );
        assertEquals( "Cheese",
                      cheese.getObjectType() );
        PatternDescr meat = (PatternDescr) and.getDescrs().get( 1 );
        assertEquals( "Meat",
                      meat.getObjectType() );
        PatternDescr wine = (PatternDescr) and.getDescrs().get( 2 );
        assertEquals( "Wine",
                      wine.getObjectType() );

    }

    public void xxxtestAccumulateMultiPattern() throws Exception {
        final DRLParser parser = parseResource( "accumulate_multi_pattern.drl" );
        parser.compilation_unit();

        assertFalse( parser.getErrorMessages().toString(),
                     parser.hasErrors() );

        final PackageDescr pack = parser.getPackageDescr();
        assertEquals( 1,
                      pack.getRules().size() );
        final RuleDescr rule = (RuleDescr) pack.getRules().get( 0 );
        assertEquals( 1,
                      rule.getLhs().getDescrs().size() );

        final PatternDescr outPattern = (PatternDescr) rule.getLhs().getDescrs().get( 0 );
        final AccumulateDescr accum = (AccumulateDescr) outPattern.getSource();
        assertEqualsIgnoreWhitespace( "$counter",
                                      outPattern.getIdentifier() );
        assertEqualsIgnoreWhitespace( "int x = 0 ;",
                                      accum.getInitCode() );
        assertEqualsIgnoreWhitespace( "x++;",
                                      accum.getActionCode() );
        assertEqualsIgnoreWhitespace( "new Integer(x)",
                                      accum.getResultCode() );

        final AndDescr and = (AndDescr) accum.getInput();
        assertEquals( 2,
                      and.getDescrs().size() );
        final PatternDescr person = (PatternDescr) and.getDescrs().get( 0 );
        final PatternDescr cheese = (PatternDescr) and.getDescrs().get( 1 );
        assertEquals( "Person",
                      person.getObjectType() );
        assertEquals( "Cheese",
                      cheese.getObjectType() );
    }

    public void xxxtestErrorMessageForMisplacedParenthesis() throws Exception {
        final DRLParser parser = parseResource( "misplaced_parenthesis.drl" );
        parser.compilation_unit();

        assertTrue( "Parser should have raised errors",
                    parser.hasErrors() );

        for ( Iterator it = parser.getErrors().iterator(); it.hasNext(); ) {
            Object error = it.next();
            if ( !(error instanceof MismatchedTokenException) ) {
                fail( "Error should be an instance of MismatchedTokenException" );
            }
        }

    }

    public void xxxtestNPEOnParser() throws Exception {
        final DRLParser parser = parseResource( "npe_on_parser.drl" );
        parser.compilation_unit();

        assertTrue( "Parser should have raised errors",
                    parser.hasErrors() );
        
        List errors = parser.getErrors();
        assertEquals( 2, errors.size() );
        
        assertTrue( errors.get( 0 ) instanceof MismatchedTokenException ); // "action" is a reserved word
        assertTrue( errors.get( 1 ) instanceof NoViableAltException ); // no title in the rule
        
    }

    public void xxxtestCommaMisuse() throws Exception {
        final DRLParser parser = parseResource( "comma_misuse.drl" );
        try {
            parser.compilation_unit();

            assertTrue( "Parser should have raised errors",
                        parser.hasErrors() );
            assertEquals( 3, parser.getErrors().size() );
            
        } catch( NullPointerException npe ) {
            fail("Should not raise NPE");
        }
    }

    public void xxxtestPluggableOperators() throws Exception {
        final DRLParser parser = parseResource( "pluggable_operators.drl" );
        parser.compilation_unit();

        assertFalse( "Parser should not have raised errors: " + parser.getErrorMessages().toString(),
                     parser.hasErrors() );

        final PackageDescr pack = parser.getPackageDescr();
        assertEquals( 1,
                      pack.getRules().size() );
        final RuleDescr rule = (RuleDescr) pack.getRules().get( 0 );
        assertEquals( 5,
                      rule.getLhs().getDescrs().size() );

        final PatternDescr eventA = (PatternDescr) rule.getLhs().getDescrs().get( 0 );
        assertEquals( "$a",
                      eventA.getIdentifier() );
        assertEquals( "EventA",
                      eventA.getObjectType() );

        final PatternDescr eventB = (PatternDescr) rule.getLhs().getDescrs().get( 1 );
        assertEquals( "$b",
                      eventB.getIdentifier() );
        assertEquals( "EventB",
                      eventB.getObjectType() );
        assertEquals( 1,
                      eventB.getConstraint().getDescrs().size() );
        final FieldConstraintDescr fcdB = (FieldConstraintDescr) eventB.getConstraint().getDescrs().get( 0 );
        assertEquals( 1,
                      fcdB.getRestrictions().size() );
        assertTrue( fcdB.getRestrictions().get( 0 ) instanceof VariableRestrictionDescr );
        final VariableRestrictionDescr rb = (VariableRestrictionDescr) fcdB.getRestrictions().get( 0 );
        assertEquals( "after",
                      rb.getEvaluator() );
        assertEquals( "$a",
                      rb.getText() );
        assertEquals( "1,10",
                      rb.getParameterText() );
        assertFalse( rb.isNegated() );

        final PatternDescr eventC = (PatternDescr) rule.getLhs().getDescrs().get( 2 );
        assertEquals( "$c",
                      eventC.getIdentifier() );
        assertEquals( "EventC",
                      eventC.getObjectType() );
        assertEquals( 1,
                      eventC.getConstraint().getDescrs().size() );
        final FieldConstraintDescr fcdC = (FieldConstraintDescr) eventC.getConstraint().getDescrs().get( 0 );
        assertEquals( 1,
                      fcdC.getRestrictions().size() );
        assertTrue( fcdC.getRestrictions().get( 0 ) instanceof VariableRestrictionDescr );
        final VariableRestrictionDescr rc = (VariableRestrictionDescr) fcdC.getRestrictions().get( 0 );
        assertEquals( "finishes",
                      rc.getEvaluator() );
        assertEquals( "$b",
                      rc.getText() );
        assertNull( rc.getParameterText() );
        assertFalse( rc.isNegated() );

        final PatternDescr eventD = (PatternDescr) rule.getLhs().getDescrs().get( 3 );
        assertEquals( "$d",
                      eventD.getIdentifier() );
        assertEquals( "EventD",
                      eventD.getObjectType() );
        assertEquals( 1,
                      eventD.getConstraint().getDescrs().size() );
        final FieldConstraintDescr fcdD = (FieldConstraintDescr) eventD.getConstraint().getDescrs().get( 0 );
        assertEquals( 1,
                      fcdD.getRestrictions().size() );
        assertTrue( fcdD.getRestrictions().get( 0 ) instanceof VariableRestrictionDescr );
        final VariableRestrictionDescr rd = (VariableRestrictionDescr) fcdD.getRestrictions().get( 0 );
        assertEquals( "starts",
                      rd.getEvaluator() );
        assertEquals( "$a",
                      rd.getText() );
        assertNull( rd.getParameterText() );
        assertTrue( rd.isNegated() );

        final PatternDescr eventE = (PatternDescr) rule.getLhs().getDescrs().get( 4 );
        assertEquals( "$e",
                      eventE.getIdentifier() );
        assertEquals( "EventE",
                      eventE.getObjectType() );
        assertEquals( 1,
                      eventE.getConstraint().getDescrs().size() );
        final FieldConstraintDescr fcdE = (FieldConstraintDescr) eventE.getConstraint().getDescrs().get( 0 );
        assertEquals( 1,
                      fcdE.getRestrictions().size() );
        assertTrue( fcdE.getRestrictions().get( 0 ) instanceof VariableRestrictionDescr );
        final VariableRestrictionDescr re = (VariableRestrictionDescr) fcdE.getRestrictions().get( 0 );
        assertEquals( "before",
                      re.getEvaluator() );
        assertEquals( "$b",
                      re.getText() );
        assertEquals( "1, 10",
                      re.getParameterText() );
        assertTrue( re.isNegated() );
    }

    public void xxxtestEventImport() throws Exception {
        final DRLParser parser = parseResource( "import_event.drl" );
        parser.compilation_unit();

        assertFalse( "Parser should not raise errors: " + parser.getErrorMessages().toString(),
                     parser.hasErrors() );

        final PackageDescr pack = parser.getPackageDescr();

        final List imports = pack.getImports();

        assertEquals( 1,
                      imports.size() );

        final ImportDescr descr = (ImportDescr) imports.get( 0 );

        assertTrue( descr.isEvent() );

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

    private Reader getReader(final String name) throws Exception {
        final InputStream in = getClass().getResourceAsStream( name );

        return new InputStreamReader( in );
    }

    private DRLParser parseResource(final String name) throws Exception {

        //        System.err.println( getClass().getResource( name ) );
        final Reader reader = getReader( name );

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
        return new CommonTokenStream( lexer );
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
        final List msgs = this.parser.getErrorMessages();
        for ( final Iterator iter = msgs.iterator(); iter.hasNext(); ) {
            final String err = (String) iter.next();
            System.out.println( err );

        }
    }

}