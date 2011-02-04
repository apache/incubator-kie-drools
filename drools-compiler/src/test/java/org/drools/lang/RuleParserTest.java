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

package org.drools.lang;

import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.lang.reflect.Method;
import java.util.List;

import junit.framework.TestCase;

import org.antlr.runtime.ANTLRStringStream;
import org.antlr.runtime.CharStream;
import org.antlr.runtime.CommonTokenStream;
import org.drools.base.evaluators.EvaluatorRegistry;
import org.drools.compiler.DrlParser;
import org.drools.lang.descr.AndDescr;
import org.drools.lang.descr.AttributeDescr;
import org.drools.lang.descr.ExprConstraintDescr;
import org.drools.lang.descr.FunctionDescr;
import org.drools.lang.descr.FunctionImportDescr;
import org.drools.lang.descr.GlobalDescr;
import org.drools.lang.descr.ImportDescr;
import org.drools.lang.descr.PackageDescr;
import org.drools.lang.descr.PatternDescr;
import org.drools.lang.descr.RuleDescr;
import org.drools.lang.descr.TypeDeclarationDescr;
import org.drools.lang.descr.TypeFieldDescr;

public class RuleParserTest extends TestCase {

    private DRLXParser parser;

    protected void setUp() throws Exception {
        super.setUp();
        // initializes pluggable operators
        new EvaluatorRegistry();
    }

    protected void tearDown() throws Exception {
        super.tearDown();
    }

    public void testPackage_OneSegment() throws Exception {
        final String packageName = (String) parse( "packageStatement",
                                                   "package foo" );
        assertEquals( "foo",
                      packageName );
    }

    public void testPackage_MultipleSegments() throws Exception {
        final String packageName = (String) parse( "packageStatement",
                                                   "package foo.bar.baz;" );
        assertEquals( "foo.bar.baz",
                      packageName );
    }

    public void testPackage() throws Exception {
        final String source = "package foo.bar.baz";
        final DrlParser parser = new DrlParser();
        final PackageDescr pkg = parser.parse( new StringReader( source ) );
        assertFalse( parser.hasErrors() );
        assertEquals( "foo.bar.baz",
                      pkg.getName() );
    }

    public void testPackageWithError() throws Exception {
        final String source = "package 12 foo.bar.baz";
        final DrlParser parser = new DrlParser();
        final PackageDescr pkg = parser.parse( true,
                                               new StringReader( source ) );
        assertTrue( parser.hasErrors() );
        assertEquals( "foo.bar.baz",
                      pkg.getName() );
    }

    public void testPackageWithError2() throws Exception {
        final String source = "package 12 12312 231";
        final DrlParser parser = new DrlParser();
        final PackageDescr pkg = parser.parse( true,
                                               new StringReader( source ) );
        assertTrue( parser.hasErrors() );
        assertEquals( "",
                      pkg.getName() );
    }

    public void testCompilationUnit() throws Exception {
        final String source = "package foo; import com.foo.Bar; import com.foo.Baz;";
        PackageDescr pkg = (PackageDescr) parse( "compilationUnit",
                                                 source );
        assertFalse( parser.getErrors().toString(),
                     parser.hasErrors() );
        assertEquals( "foo",
                      pkg.getName() );
        assertEquals( 2,
                      pkg.getImports().size() );
        ImportDescr impdescr = pkg.getImports().get( 0 );
        assertEquals( "com.foo.Bar",
                      impdescr.getTarget() );
        assertEquals( source.indexOf( "import " + impdescr.getTarget() ),
                      impdescr.getStartCharacter() );
        assertEquals( source.indexOf( "import " + impdescr.getTarget() ) + ("import " + impdescr.getTarget()).length() + 1,
                      impdescr.getEndCharacter() );

        impdescr = pkg.getImports().get( 1 );
        assertEquals( "com.foo.Baz",
                      impdescr.getTarget() );
        assertEquals( source.indexOf( "import " + impdescr.getTarget() ),
                      impdescr.getStartCharacter() );
        assertEquals( source.indexOf( "import " + impdescr.getTarget() ) + ("import " + impdescr.getTarget()).length() + 1,
                      impdescr.getEndCharacter() );
    }

    public void testFunctionImport() throws Exception {
        final String source = "package foo\n" +
                              "import function java.lang.Math.max\n" +
                              "import function java.lang.Math.min;\n" +
                              "import foo.bar.*\n" +
                              "import baz.Baz";
        PackageDescr pkg = (PackageDescr) parse( "compilationUnit",
                                                 source );
        assertFalse( parser.getErrors().toString(),
                     parser.hasErrors() );
        assertEquals( "foo",
                      pkg.getName() );
        assertEquals( 2,
                      pkg.getImports().size() );
        ImportDescr impdescr = pkg.getImports().get( 0 );
        assertEquals( "foo.bar.*",
                      impdescr.getTarget() );
        assertEquals( source.indexOf( "import " + impdescr.getTarget() ),
                      impdescr.getStartCharacter() );
        assertEquals( source.indexOf( "import " + impdescr.getTarget() ) + ("import " + impdescr.getTarget()).length(),
                      impdescr.getEndCharacter() );

        impdescr = pkg.getImports().get( 1 );
        assertEquals( "baz.Baz",
                      impdescr.getTarget() );
        assertEquals( source.indexOf( "import " + impdescr.getTarget() ),
                      impdescr.getStartCharacter() );
        assertEquals( source.indexOf( "import " + impdescr.getTarget() ) + ("import " + impdescr.getTarget()).length(),
                      impdescr.getEndCharacter() );

        assertEquals( 2,
                      pkg.getFunctionImports().size() );
        impdescr = pkg.getFunctionImports().get( 0 );
        assertEquals( "java.lang.Math.max",
                      impdescr.getTarget() );
        assertEquals( source.indexOf( "import function " + impdescr.getTarget() ),
                      impdescr.getStartCharacter() );
        assertEquals( source.indexOf( "import function " + impdescr.getTarget() ) + ("import function " + impdescr.getTarget()).length(),
                      impdescr.getEndCharacter() );

        impdescr = pkg.getFunctionImports().get( 1 );
        assertEquals( "java.lang.Math.min",
                      impdescr.getTarget() );
        assertEquals( source.indexOf( "import function " + impdescr.getTarget() ),
                      impdescr.getStartCharacter() );
        assertEquals( source.indexOf( "import function " + impdescr.getTarget() ) + ("import function " + impdescr.getTarget()).length() + 1,
                      impdescr.getEndCharacter() );

    }

    public void testGlobal1() throws Exception {
        final String source = "package foo.bar.baz\n" +
                              "import com.foo.Bar\n" +
                              "global java.util.List<java.util.Map<String,Integer>> aList;\n" +
                              "global Integer aNumber";
        PackageDescr pkg = (PackageDescr) parse( "compilationUnit",
                                                 source );
        assertFalse( parser.getErrors().toString(),
                     parser.hasErrors() );
        assertEquals( "foo.bar.baz",
                      pkg.getName() );
        assertEquals( 1,
                      pkg.getImports().size() );

        ImportDescr impdescr = pkg.getImports().get( 0 );
        assertEquals( "com.foo.Bar",
                      impdescr.getTarget() );
        assertEquals( source.indexOf( "import " + impdescr.getTarget() ),
                      impdescr.getStartCharacter() );
        assertEquals( source.indexOf( "import " + impdescr.getTarget() ) + ("import " + impdescr.getTarget()).length(),
                      impdescr.getEndCharacter() );

        assertEquals( 2,
                      pkg.getGlobals().size() );

        GlobalDescr global = pkg.getGlobals().get( 0 );
        assertEquals( "java.util.List<java.util.Map<String,Integer>>",
                      global.getType() );
        assertEquals( "aList",
                      global.getIdentifier() );
        assertEquals( source.indexOf( "global " + global.getType() ),
                      global.getStartCharacter() );
        assertEquals( source.indexOf( "global " + global.getType() + " " + global.getIdentifier() ) +
                              ("global " + global.getType() + " " + global.getIdentifier()).length() + 1,
                      global.getEndCharacter() );

        global = pkg.getGlobals().get( 1 );
        assertEquals( "Integer",
                      global.getType() );
        assertEquals( "aNumber",
                      global.getIdentifier() );
        assertEquals( source.indexOf( "global " + global.getType() ),
                      global.getStartCharacter() );
        assertEquals( source.indexOf( "global " + global.getType() + " " + global.getIdentifier() ) +
                              ("global " + global.getType() + " " + global.getIdentifier()).length(),
                      global.getEndCharacter() );
    }

    public void testGlobal() throws Exception {
        PackageDescr pack = (PackageDescr) parseResource( "compilationUnit",
                                                          "globals.drl" );

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
    }

    public void testFunctionImport2() throws Exception {
        PackageDescr pkg = (PackageDescr) parseResource( "compilationUnit",
                                                         "test_FunctionImport.drl" );

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

    //    public void testFromComplexAcessor() throws Exception {
    //        String source = "rule \"Invalid customer id\" ruleflow-group \"validate\" lock-on-active true \n" +
    //                        " when \n" +
    //                        "     o: Order( ) \n" +
    //                        "     not( Customer( ) from customerService.getCustomer(o.getCustomerId()) ) \n" +
    //                        " then \n" +
    //                        "     System.err.println(\"Invalid customer id found!\"); \n" +
    //                        "     o.addError(\"Invalid customer id\"); \n" +
    //                        "end \n";
    //        parse( "compilation_unit",
    //               "compilation_unit",
    //               source );
    //
    //        assertFalse( parser.getErrorMessages().toString(), parser.hasErrors() );
    //
    //        RuleDescr rule = (RuleDescr) this.walker.getPackageDescr().getRules().get( 0 );
    //        assertEquals( "Invalid customer id",
    //                      rule.getName() );
    //
    //    }
    //
    //    public void testFromWithInlineList() throws Exception {
    //        String source = "rule XYZ \n" +
    //                        " when \n" +
    //                        " o: Order( ) \n" +
    //                        " not( Number( ) from [1, 2, 3] ) \n" +
    //                        " then \n" +
    //                        " System.err.println(\"Invalid customer id found!\"); \n" +
    //                        " o.addError(\"Invalid customer id\"); \n" +
    //                        "end \n";
    //        parse( "compilation_unit",
    //               "compilation_unit",
    //               source );
    //
    //        RuleDescr rule = (RuleDescr) this.walker.getPackageDescr().getRules().get( 0 );
    //        assertEquals( "XYZ",
    //                      rule.getName() );
    //
    //        assertFalse( parser.hasErrors() );
    //        PatternDescr number = (PatternDescr) ((NotDescr) rule.getLhs().getDescrs().get( 1 )).getDescrs().get( 0 );
    //        assertEquals( "[1, 2, 3]",
    //                      ((FromDescr) number.getSource()).getDataSource().toString() );
    //
    //    }
    //
    //    public void testFromWithInlineListMethod() throws Exception {
    //        String source = "rule XYZ \n" +
    //                        " when \n" +
    //                        " o: Order( ) \n" +
    //                        " Number( ) from [1, 2, 3].sublist(1, 2) \n" +
    //                        " then \n" +
    //                        " System.err.println(\"Invalid customer id found!\"); \n" +
    //                        " o.addError(\"Invalid customer id\"); \n" +
    //                        "end \n";
    //        parse( "compilation_unit",
    //               "compilation_unit",
    //               source );
    //
    //        RuleDescr rule = (RuleDescr) this.walker.getPackageDescr().getRules().get( 0 );
    //        assertEquals( "XYZ",
    //                      rule.getName() );
    //
    //        assertFalse( parser.hasErrors() );
    //        PatternDescr number = (PatternDescr) rule.getLhs().getDescrs().get( 1 );
    //        assertEquals( "[1, 2, 3].sublist(1, 2)",
    //                      ((FromDescr) number.getSource()).getDataSource().toString() );
    //
    //    }
    //
    //    public void testFromWithInlineListIndex() throws Exception {
    //        String source = "rule XYZ \n" +
    //                        " when \n" +
    //                        " o: Order( ) \n" +
    //                        " Number( ) from [1, 2, 3][1] \n" +
    //                        " then \n" +
    //                        " System.err.println(\"Invalid customer id found!\"); \n" +
    //                        " o.addError(\"Invalid customer id\"); \n" +
    //                        "end \n";
    //        parse( "compilation_unit",
    //               "compilation_unit",
    //               source );
    //
    //        RuleDescr rule = (RuleDescr) this.walker.getPackageDescr().getRules().get( 0 );
    //        assertEquals( "XYZ",
    //                      rule.getName() );
    //
    //        assertFalse( parser.hasErrors() );
    //        PatternDescr number = (PatternDescr) rule.getLhs().getDescrs().get( 1 );
    //        assertEquals( "[1, 2, 3][1]",
    //                      ((FromDescr) number.getSource()).getDataSource().toString() );
    //    }
    //
    //    public void testRuleWithoutEnd() throws Exception {
    //        String source = "rule \"Invalid customer id\" \n" + " when \n" + " o: Order( ) \n" + " then \n" + " System.err.println(\"Invalid customer id found!\"); \n";
    //        parse( "compilation_unit",
    //               "compilation_unit",
    //               source );
    //        assertTrue( parser.hasErrors() );
    //    }
    //
    //    public void testOrWithSpecialBind() throws Exception {
    //        String source = "rule \"A and (B or C or D)\" \n" + "    when \n" + "        pdo1 : ParametricDataObject( paramID == 101, stringValue == \"1000\" ) and \n"
    //                        + "        pdo2 :(ParametricDataObject( paramID == 101, stringValue == \"1001\" ) or \n" + "               ParametricDataObject( paramID == 101, stringValue == \"1002\" ) or \n"
    //                        + "               ParametricDataObject( paramID == 101, stringValue == \"1003\" )) \n" + "    then \n" + "        System.out.println( \"Rule: A and (B or C or D) Fired. pdo1: \" + pdo1 +  \" pdo2: \"+ pdo2); \n" + "end\n";
    //        parse( "compilation_unit",
    //               "compilation_unit",
    //               source );
    //
    //        assertFalse( parser.hasErrors() );
    //
    //    }
    //
    public void testCompatibleRestriction() throws Exception {
        String source = "package com.sample  rule test  when  Test( ( text == null || text2 matches \"\" ) )  then  end";
        PackageDescr pkg = (PackageDescr) parse( "compilationUnit",
                                                 source );
        assertEquals( "com.sample",
                          pkg.getName() );
        RuleDescr rule = (RuleDescr) pkg.getRules().get( 0 );
        assertEquals( "test",
                          rule.getName() );
        ExprConstraintDescr expr = (ExprConstraintDescr) ((PatternDescr) rule.getLhs().getDescrs().get( 0 )).getDescrs().get( 0 );
        assertEquals( "( text == null || text2 matches \"\" )",
                          expr.getText() );
    }

    public void testSimpleConstraint() throws Exception {
        String source = "package com.sample  rule test  when  Cheese( type == 'stilton', price > 10 )  then  end";
        PackageDescr pkg = (PackageDescr) parse( "compilationUnit",
                                                 source );
        assertEquals( "com.sample",
                          pkg.getName() );
        RuleDescr rule = (RuleDescr) pkg.getRules().get( 0 );
        assertEquals( "test",
                          rule.getName() );

        assertEquals( 1,
                      rule.getLhs().getDescrs().size() );
        PatternDescr pattern = (PatternDescr) rule.getLhs().getDescrs().get( 0 );

        AndDescr constraint = (AndDescr) pattern.getConstraint();
        assertEquals( 2,
                      constraint.getDescrs().size() );
        assertEquals( "type == 'stilton'",
                      constraint.getDescrs().get( 0 ).toString() );
        assertEquals( "price > 10",
                      constraint.getDescrs().get( 1 ).toString() );
    }

    public void testDialect() throws Exception {
        final String source = "dialect 'mvel'";
        PackageDescr pkg = (PackageDescr) parse( "compilationUnit",
                                                 source );
        AttributeDescr attr = (AttributeDescr) pkg.getAttributes().get( 0 );
        assertEquals( "dialect",
                      attr.getName() );
        assertEquals( "mvel",
                      attr.getValue() );
    }

    public void testDialect2() throws Exception {
        final String source = "dialect \"mvel\"";
        PackageDescr pkg = (PackageDescr) parse( "compilationUnit",
                                                     source );
        AttributeDescr attr = pkg.getAttributes().get( 0 );
        assertEquals( "dialect",
                          attr.getName() );
        assertEquals( "mvel",
                          attr.getValue() );
    }

    public void testEmptyRule() throws Exception {
        final RuleDescr rule = (RuleDescr) parseResource( "rule",
                                                              "empty_rule.drl" );

        assertNotNull( rule );

        assertEquals( "empty",
                          rule.getName() );
        assertNotNull( rule.getLhs() );
        assertNotNull( rule.getConsequence() );
    }

    public void testKeywordCollisions() throws Exception {
        PackageDescr pkg = (PackageDescr) parseResource( "compilationUnit",
                                                         "eol_funny_business.drl" );
        assertFalse( parser.getErrors().toString(),
                     parser.hasErrors() );

        assertEquals( 1,
                      pkg.getRules().size() );
    }

    public void testTernaryExpression() throws Exception {
        PackageDescr pkg = (PackageDescr) parseResource( "compilationUnit",
                                                         "ternary_expression.drl" );

        final RuleDescr rule = (RuleDescr) pkg.getRules().get( 0 );
        assertEquals( 1,
                      pkg.getRules().size() );

        assertEqualsIgnoreWhitespace( "if (speed > speedLimit ? true : false;) pullEmOver();",
                                          (String) rule.getConsequence() );
    }

    //    public void FIXME_testLatinChars() throws Exception {
    //        final DrlParser parser = new DrlParser();
    //        final Reader drl = new InputStreamReader( this.getClass().getResourceAsStream( "latin-sample.dslr" ) );
    //        final Reader dsl = new InputStreamReader( this.getClass().getResourceAsStream( "latin.dsl" ) );
    //
    //        final PackageDescr pkg = parser.parse( drl,
    //                                               dsl );
    //
    //        // MN: will get some errors due to the char encoding on my FC5 install
    //        // others who use the right encoding may not see this, feel free to
    //        // uncomment
    //        // the following assertion.
    //        assertFalse( parser.hasErrors() );
    //
    //        assertEquals( "br.com.auster.drools.sample",
    //                      pkg.getName() );
    //        assertEquals( 1,
    //                      pkg.getRules().size() );
    //
    //    }
    //
    public void testFunctionWithArrays() throws Exception {
        PackageDescr pkg = (PackageDescr) parseResource( "compilationUnit",
                                                         "function_arrays.drl" );

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

    //    public void testAlmostEmptyRule() throws Exception {
    //        final RuleDescr rule = (RuleDescr) parseResource( "rule",
    //                                                          "rule",
    //                                                          "almost_empty_rule.drl" );
    //
    //        assertNotNull( rule );
    //
    //        assertEquals( "almost_empty",
    //                      rule.getName() );
    //        assertNotNull( rule.getLhs() );
    //        assertEquals( "",
    //                      ((String) rule.getConsequence()).trim() );
    //    }
    //
    //    public void testQuotedStringNameRule() throws Exception {
    //        final RuleDescr rule = (RuleDescr) parseResource( "rule",
    //                                                          "rule",
    //                                                          "quoted_string_name_rule.drl" );
    //
    //        assertNotNull( rule );
    //
    //        assertEquals( "quoted string name",
    //                      rule.getName() );
    //        assertNotNull( rule.getLhs() );
    //        assertEquals( "",
    //                      ((String) rule.getConsequence()).trim() );
    //    }
    //
    //    public void testNoLoop() throws Exception {
    //        final RuleDescr rule = (RuleDescr) parseResource( "rule",
    //                                                          "rule",
    //                                                          "no-loop.drl" );
    //
    //        assertNotNull( rule );
    //
    //        assertEquals( "rule1",
    //                      rule.getName() );
    //        final AttributeDescr att = (AttributeDescr) rule.getAttributes().get( "no-loop" );
    //        assertEquals( "false",
    //                      att.getValue() );
    //        assertEquals( "no-loop",
    //                      att.getName() );
    //    }
    //
    //    public void testAutofocus() throws Exception {
    //        final RuleDescr rule = (RuleDescr) parseResource( "rule",
    //                                                          "rule",
    //                                                          "autofocus.drl" );
    //
    //        assertNotNull( rule );
    //
    //        assertEquals( "rule1",
    //                      rule.getName() );
    //        final AttributeDescr att = (AttributeDescr) rule.getAttributes().get( "auto-focus" );
    //        assertEquals( "true",
    //                      att.getValue() );
    //        assertEquals( "auto-focus",
    //                      att.getName() );
    //    }
    //
    //    public void testRuleFlowGroup() throws Exception {
    //        final RuleDescr rule = (RuleDescr) parseResource( "rule",
    //                                                          "rule",
    //                                                          "ruleflowgroup.drl" );
    //
    //        assertNotNull( rule );
    //
    //        assertEquals( "rule1",
    //                      rule.getName() );
    //        final AttributeDescr att = (AttributeDescr) rule.getAttributes().get( "ruleflow-group" );
    //        assertEquals( "a group",
    //                      att.getValue() );
    //        assertEquals( "ruleflow-group",
    //                      att.getName() );
    //    }
    //
    //    public void testConsequenceWithDeclaration() throws Exception {
    //        final RuleDescr rule = (RuleDescr) parseResource( "rule",
    //                                                          "rule",
    //                                                          "declaration-in-consequence.drl" );
    //
    //        assertNotNull( rule );
    //
    //        assertEquals( "myrule",
    //                      rule.getName() );
    //
    //        final String expected = "int i = 0; i = 1; i / 1; i == 1; i(i); i = 'i'; i.i.i; i\\i; i<i; i>i; i=\"i\";  ++i;" + "i++; --i; i--; i += i; i -= i; i *= i; i /= i;" + "int i = 5;" + "for(int j; j<i; ++j) {" + "System.out.println(j);}"
    //                                + "Object o = new String(\"Hello\");" + "String s = (String) o;";
    //
    //        assertEqualsIgnoreWhitespace( expected,
    //                                      (String) rule.getConsequence() );
    //        assertTrue( ((String) rule.getConsequence()).indexOf( "++" ) > 0 );
    //        assertTrue( ((String) rule.getConsequence()).indexOf( "--" ) > 0 );
    //        assertTrue( ((String) rule.getConsequence()).indexOf( "+=" ) > 0 );
    //        assertTrue( ((String) rule.getConsequence()).indexOf( "==" ) > 0 );
    //
    //        // System.out.println(( String ) rule.getConsequence());
    //        // note, need to assert that "i++" is preserved as is, no extra spaces.
    //    }
    //
    //    public void testRuleParseLhs() throws Exception {
    //        final String text = "Person(age < 42, location==\"atlanta\") \nor\nPerson(name==\"bob\") \n";
    //        PatternDescr pattern = (PatternDescr) parse( "lhs_pattern",
    //                                                     "lhs_pattern",
    //                                                     text );
    //        assertNotNull( pattern );
    //    }
    //
    //    public void testRuleParseLhsWithStringQuotes() throws Exception {
    //        final String text = "Person( location==\"atlanta\\\"\")\n";
    //        PatternDescr pattern = (PatternDescr) parse( "lhs_pattern",
    //                                                     "lhs_pattern",
    //                                                     text );
    //        assertNotNull( pattern );
    //
    //        FieldConstraintDescr field = (FieldConstraintDescr) pattern.getDescrs().get( 0 );
    //        assertEquals( "location",
    //                      field.getFieldName() );
    //        // System.out.println(field.getRestriction().getRestrictions().get( 0
    //        // ).getText());
    //        assertEquals( "atlanta\\\"",
    //                      field.getRestriction().getRestrictions().get( 0 ).getText() );
    //    }
    //
    //    public void testLiteralBoolAndNegativeNumbersRule() throws Exception {
    //        final RuleDescr rule = (RuleDescr) parseResource( "rule",
    //                                                          "rule",
    //                                                          "literal_bool_and_negative.drl" );
    //
    //        assertNotNull( rule );
    //
    //        assertEquals( "simple_rule",
    //                      rule.getName() );
    //        assertNotNull( rule.getLhs() );
    //        assertEqualsIgnoreWhitespace( "cons();",
    //                                      (String) rule.getConsequence() );
    //
    //        final AndDescr lhs = rule.getLhs();
    //        assertEquals( 3,
    //                      lhs.getDescrs().size() );
    //
    //        PatternDescr pattern = (PatternDescr) lhs.getDescrs().get( 0 );
    //        assertEquals( 1,
    //                      pattern.getConstraint().getDescrs().size() );
    //        AndDescr fieldAnd = (AndDescr) pattern.getConstraint();
    //        FieldConstraintDescr fld = (FieldConstraintDescr) fieldAnd.getDescrs().get( 0 );
    //        LiteralRestrictionDescr lit = (LiteralRestrictionDescr) fld.getRestrictions().get( 0 );
    //
    //        assertEquals( "==",
    //                      lit.getEvaluator() );
    //        assertEquals( "false",
    //                      lit.getText() );
    //        assertEquals( "bar",
    //                      fld.getFieldName() );
    //
    //        pattern = (PatternDescr) lhs.getDescrs().get( 1 );
    //        assertEquals( 1,
    //                      pattern.getConstraint().getDescrs().size() );
    //
    //        fieldAnd = (AndDescr) pattern.getConstraint();
    //        fld = (FieldConstraintDescr) fieldAnd.getDescrs().get( 0 );
    //        lit = (LiteralRestrictionDescr) fld.getRestrictions().get( 0 );
    //
    //        assertEquals( ">",
    //                      lit.getEvaluator() );
    //        assertEquals( "-42",
    //                      lit.getText() );
    //        assertEquals( "boo",
    //                      fld.getFieldName() );
    //
    //        pattern = (PatternDescr) lhs.getDescrs().get( 2 );
    //        assertEquals( 1,
    //                      pattern.getConstraint().getDescrs().size() );
    //
    //        // lit = (LiteralDescr) col.getDescrs().get( 0 );
    //
    //        fieldAnd = (AndDescr) pattern.getConstraint();
    //        fld = (FieldConstraintDescr) fieldAnd.getDescrs().get( 0 );
    //        lit = (LiteralRestrictionDescr) fld.getRestrictions().get( 0 );
    //        assertEquals( ">",
    //                      lit.getEvaluator() );
    //        assertEquals( "-42.42",
    //                      lit.getText() );
    //        assertEquals( "boo",
    //                      fld.getFieldName() );
    //    }
    //
    //    public void testChunkWithoutParens() throws Exception {
    //        String input = "( foo )";
    //        ReturnValueRestrictionDescr returnData = (ReturnValueRestrictionDescr) parse( "paren_chunk",
    //                                                                                      "fact_expression",
    //                                                                                      input );
    //
    //        assertEquals( "( foo )",
    //                      input.substring( returnData.getStartCharacter(),
    //                                       returnData.getEndCharacter() ) );
    //    }
    //
    //    public void testChunkWithParens() throws Exception {
    //        String input = "(fnord())";
    //        ReturnValueRestrictionDescr returnData = (ReturnValueRestrictionDescr) parse( "paren_chunk",
    //                                                                                      "fact_expression",
    //                                                                                      input );
    //
    //        assertEquals( "(fnord())",
    //                      input.substring( returnData.getStartCharacter(),
    //                                       returnData.getEndCharacter() ) );
    //    }
    //
    //    public void testChunkWithParensAndQuotedString() throws Exception {
    //        String input = "( fnord( \"cheese\" ) )";
    //        ReturnValueRestrictionDescr returnData = (ReturnValueRestrictionDescr) parse( "paren_chunk",
    //                                                                                      "fact_expression",
    //                                                                                      input );
    //
    //        assertEquals( "( fnord( \"cheese\" ) )",
    //                      input.substring( returnData.getStartCharacter(),
    //                                       returnData.getEndCharacter() ) );
    //    }
    //
    //    public void testChunkWithRandomCharac5ters() throws Exception {
    //        String input = "( %*9dkj)";
    //        ReturnValueRestrictionDescr returnData = (ReturnValueRestrictionDescr) parse( "paren_chunk",
    //                                                                                      "fact_expression",
    //                                                                                      input );
    //
    //        assertEquals( "( %*9dkj)",
    //                      input.substring( returnData.getStartCharacter(),
    //                                       returnData.getEndCharacter() ) );
    //    }
    //
    //    public void testEmptyPattern() throws Exception {
    //        parseResource( "compilation_unit",
    //                       "compilation_unit",
    //                       "test_EmptyPattern.drl" );
    //
    //        final PackageDescr packageDescr = this.walker.getPackageDescr();
    //        assertEquals( 1,
    //                      packageDescr.getRules().size() );
    //        final RuleDescr ruleDescr = (RuleDescr) packageDescr.getRules().get( 0 );
    //        assertEquals( "simple rule",
    //                      ruleDescr.getName() );
    //        assertNotNull( ruleDescr.getLhs() );
    //        assertEquals( 1,
    //                      ruleDescr.getLhs().getDescrs().size() );
    //        final PatternDescr patternDescr = (PatternDescr) ruleDescr.getLhs().getDescrs().get( 0 );
    //        assertEquals( 0,
    //                      patternDescr.getConstraint().getDescrs().size() ); // this
    //        // may
    //        // be
    //        // null,
    //        // not
    //        // sure
    //        // as
    //        // the
    //        // test
    //        // doesn't
    //        // get
    //        // this
    //        // far...
    //        assertEquals( "Cheese",
    //                      patternDescr.getObjectType() );
    //
    //    }
    //
    //    public void testSimpleMethodCallWithFrom() throws Exception {
    //
    //        final RuleDescr rule = (RuleDescr) parseResource( "rule",
    //                                                          "rule",
    //                                                          "test_SimpleMethodCallWithFrom.drl" );
    //        final PatternDescr pattern = (PatternDescr) rule.getLhs().getDescrs().get( 0 );
    //        final FromDescr from = (FromDescr) pattern.getSource();
    //        final AccessorDescr method = (AccessorDescr) from.getDataSource();
    //
    //        assertEquals( "something.doIt( foo,bar,42,\"hello\",[ a : \"b\", \"something\" : 42, \"a\" : foo, x : [x:y]],\"end\", [a, \"b\", 42] )",
    //                      method.toString() );
    //    }
    //
    //    public void testSimpleFunctionCallWithFrom() throws Exception {
    //
    //        final RuleDescr rule = (RuleDescr) parseResource( "rule",
    //                                                          "rule",
    //                                                          "test_SimpleFunctionCallWithFrom.drl" );
    //        final PatternDescr pattern = (PatternDescr) rule.getLhs().getDescrs().get( 0 );
    //        final FromDescr from = (FromDescr) pattern.getSource();
    //        final AccessorDescr func = (AccessorDescr) from.getDataSource();
    //
    //        assertEquals( "doIt( foo,bar,42,\"hello\",[ a : \"b\", \"something\" : 42, \"a\" : foo, x : [x:y]],\"end\", [a, \"b\", 42] )",
    //                      func.toString() );
    //    }
    //
    //    public void testSimpleAccessorWithFrom() throws Exception {
    //
    //        final RuleDescr rule = (RuleDescr) parseResource( "rule",
    //                                                          "rule",
    //                                                          "test_SimpleAccessorWithFrom.drl" );
    //        final PatternDescr pattern = (PatternDescr) rule.getLhs().getDescrs().get( 0 );
    //        final FromDescr from = (FromDescr) pattern.getSource();
    //        final AccessorDescr accessor = (AccessorDescr) from.getDataSource();
    //
    //        assertEquals( "something.doIt",
    //                      accessor.toString() );
    //    }
    //
    //    public void testSimpleAccessorAndArgWithFrom() throws Exception {
    //
    //        final RuleDescr rule = (RuleDescr) parseResource( "rule",
    //                                                          "rule",
    //                                                          "test_SimpleAccessorArgWithFrom.drl" );
    //        final PatternDescr pattern = (PatternDescr) rule.getLhs().getDescrs().get( 0 );
    //        final FromDescr from = (FromDescr) pattern.getSource();
    //        final AccessorDescr accessor = (AccessorDescr) from.getDataSource();
    //
    //        assertEquals( "something.doIt[\"key\"]",
    //                      accessor.toString() );
    //    }
    //
    //    public void testComplexChainedAcessor() throws Exception {
    //        final RuleDescr rule = (RuleDescr) parseResource( "rule",
    //                                                          "rule",
    //                                                          "test_ComplexChainedCallWithFrom.drl" );
    //        final PatternDescr pattern = (PatternDescr) rule.getLhs().getDescrs().get( 0 );
    //        final FromDescr from = (FromDescr) pattern.getSource();
    //        final AccessorDescr accessor = (AccessorDescr) from.getDataSource();
    //
    //        assertEquals( "doIt1( foo,bar,42,\"hello\",[ a : \"b\"], [a, \"b\", 42] ).doIt2(bar, [a, \"b\", 42]).field[\"key\"]",
    //                      accessor.toString() );
    //    }
    //
    //    // public void testFrom() throws Exception {
    //    // final RuleDescr rule = parseResource( "from.drl" ).rule();
    //    //
    //    // if(parser.hasErrors()) {
    //    // System.err.println(parser.getErrorMessages());
    //    // }
    //    // assertFalse(parser.hasErrors());
    //    //
    //    // assertNotNull( rule );
    //    //
    //    // assertEquals( "using_from",
    //    // rule.getName() );
    //    //
    //    // assertEquals(9, rule.getLhs().getDescrs().size());
    //    //
    //    // FromDescr from = (FromDescr) rule.getLhs().getDescrs().get(0);
    //    //
    //    // assertEquals(3, from.getLine());
    //    //
    //    // assertEquals("Foo", from.getReturnedPattern().getObjectType());
    //    // assertTrue(from.getDataSource() instanceof FieldAccessDescr);
    //    // assertEquals("baz", ((FieldAccessDescr)
    //    // from.getDataSource()).getFieldName());
    //    // assertEquals("bar", ((FieldAccessDescr)
    //    // from.getDataSource()).getVariableName());
    //    //
    //    //
    //    // ArgumentValueDescr arg = null;
    //    //
    //    // from = (FromDescr) rule.getLhs().getDescrs().get(1);
    //    // assertEquals("Foo", from.getReturnedPattern().getObjectType());
    //    // assertEquals(0, from.getReturnedPattern().getDescrs().size());
    //    // FieldAccessDescr fieldAccess = ( FieldAccessDescr ) from.getDataSource();
    //    // arg = ( ArgumentValueDescr ) fieldAccess.getArgument();
    //    // assertEquals(ArgumentValueDescr.STRING, arg.getType() );
    //    //
    //    // from = (FromDescr) rule.getLhs().getDescrs().get(2);
    //    // fieldAccess = ( FieldAccessDescr ) from.getDataSource();
    //    // arg = ( ArgumentValueDescr ) fieldAccess.getArgument();
    //    // assertEquals(ArgumentValueDescr.VARIABLE, arg.getType() );
    //    //
    //    // from = (FromDescr) rule.getLhs().getDescrs().get(3);
    //    // fieldAccess = ( FieldAccessDescr ) from.getDataSource();
    //    // arg = ( ArgumentValueDescr ) fieldAccess.getArgument();
    //    // assertEquals(ArgumentValueDescr.INTEGRAL, arg.getType() );
    //    //
    //    // from = (FromDescr) rule.getLhs().getDescrs().get(4);
    //    // assertEquals("Whee", from.getReturnedColumn().getObjectType());
    //    // assertEquals(1, from.getReturnedColumn().getDescrs().size());
    //    // assertTrue(from.getDataSource() instanceof FunctionCallDescr);
    //    // assertEquals("whee", ((FunctionCallDescr)
    //    // from.getDataSource()).getName());
    //    // assertEquals(1, ((FunctionCallDescr)
    //    // from.getDataSource()).getArguments().size());
    //    // arg = ( (ArgumentValueDescr )((FunctionCallDescr)
    //    // from.getDataSource()).getArguments().get(0));
    //    // assertEquals("y", arg.getValue());
    //    // assertEquals(ArgumentValueDescr.STRING, arg.getType());
    //    //
    //    // assertEquals(7, from.getLine());
    //    // assertEquals(7, from.getReturnedColumn().getLine());
    //    //
    //    // from = (FromDescr) rule.getLhs().getDescrs().get(5);
    //    // assertEquals("Foo", from.getReturnedColumn().getObjectType());
    //    // assertEquals(1, from.getReturnedColumn().getDescrs().size());
    //    // assertEquals("f", from.getReturnedColumn().getIdentifier());
    //    // assertTrue(from.getDataSource() instanceof MethodAccessDescr);
    //    // assertEquals("bar", ((MethodAccessDescr)
    //    // from.getDataSource()).getVariableName());
    //    // assertEquals("la", ((MethodAccessDescr)
    //    // from.getDataSource()).getMethodName());
    //    // assertEquals(1, ((MethodAccessDescr)
    //    // from.getDataSource()).getArguments().size());
    //    // arg = (ArgumentValueDescr) ((MethodAccessDescr)
    //    // from.getDataSource()).getArguments().get(0);
    //    //
    //    //
    //    // assertEquals("x", arg.getValue());
    //    // assertEquals(ArgumentValueDescr.VARIABLE, arg.getType());
    //    //
    //    // assertEqualsIgnoreWhitespace("whee();", ( String )
    //    // rule.getConsequence());
    //    //
    //    // from = (FromDescr) rule.getLhs().getDescrs().get(6);
    //    // assertEquals("wa", ((FunctionCallDescr)from.getDataSource()).getName());
    //    //
    //    // from = (FromDescr) rule.getLhs().getDescrs().get(7);
    //    // MethodAccessDescr meth = (MethodAccessDescr)from.getDataSource();
    //    // assertEquals("wa", meth.getMethodName());
    //    // assertEquals("la", meth.getVariableName());
    //    //
    //    // arg = (ArgumentValueDescr) meth.getArguments().get(0);
    //    // assertEquals("42", arg.getValue());
    //    // assertEquals(ArgumentValueDescr.INTEGRAL, arg.getType());
    //    //
    //    // arg = (ArgumentValueDescr) meth.getArguments().get(1);
    //    // assertEquals("42.42", arg.getValue());
    //    // assertEquals(ArgumentValueDescr.DECIMAL, arg.getType());
    //    //
    //    // arg = (ArgumentValueDescr) meth.getArguments().get(2);
    //    // assertEquals("false", arg.getValue());
    //    // assertEquals(ArgumentValueDescr.BOOLEAN, arg.getType());
    //    //
    //    // arg = (ArgumentValueDescr) meth.getArguments().get(3);
    //    // assertEquals("null", arg.getValue());
    //    // assertEquals(ArgumentValueDescr.NULL, arg.getType());
    //    //
    //    // assertEquals("Bam",
    //    // ((PatternDescr)rule.getLhs().getDescrs().get(8)).getObjectType());
    //    // }
    //
    //    public void testSimpleRule() throws Exception {
    //        final RuleDescr rule = (RuleDescr) parseResource( "rule",
    //                                                          "rule",
    //                                                          "simple_rule.drl" );
    //
    //        assertNotNull( rule );
    //
    //        assertEquals( "simple_rule",
    //                      rule.getName() );
    //
    //        assertEquals( 7,
    //                      rule.getConsequenceLine() );
    //        assertEquals( 2,
    //                      rule.getConsequencePattern() );
    //
    //        final AndDescr lhs = rule.getLhs();
    //
    //        assertNotNull( lhs );
    //
    //        assertEquals( 3,
    //                      lhs.getDescrs().size() );
    //
    //        // System.err.println( lhs.getDescrs() );
    //
    //        // Check first pattern
    //        final PatternDescr first = (PatternDescr) lhs.getDescrs().get( 0 );
    //        assertEquals( "foo3",
    //                      first.getIdentifier() );
    //        assertEquals( "Bar",
    //                      first.getObjectType() );
    //
    //        assertEquals( 1,
    //                      first.getConstraint().getDescrs().size() );
    //
    //        AndDescr fieldAnd = (AndDescr) first.getConstraint();
    //        FieldConstraintDescr fld = (FieldConstraintDescr) fieldAnd.getDescrs().get( 0 );
    //        LiteralRestrictionDescr constraint = (LiteralRestrictionDescr) fld.getRestrictions().get( 0 );
    //
    //        assertNotNull( constraint );
    //
    //        assertEquals( "a",
    //                      fld.getFieldName() );
    //        assertEquals( "==",
    //                      constraint.getEvaluator() );
    //        assertEquals( "3",
    //                      constraint.getText() );
    //
    //        // Check second pattern
    //        final PatternDescr second = (PatternDescr) lhs.getDescrs().get( 1 );
    //        assertEquals( "foo4",
    //                      second.getIdentifier() );
    //        assertEquals( "Bar",
    //                      second.getObjectType() );
    //
    //        // System.err.println( second.getDescrs() );
    //
    //        fieldAnd = (AndDescr) second.getConstraint();
    //        assertEquals( 2,
    //                      fieldAnd.getDescrs().size() );
    //
    //        final FieldBindingDescr fieldBindingDescr = (FieldBindingDescr) fieldAnd.getDescrs().get( 0 );
    //        assertEquals( "a",
    //                      fieldBindingDescr.getFieldName() );
    //        assertEquals( "a4",
    //                      fieldBindingDescr.getIdentifier() );
    //
    //        fld = (FieldConstraintDescr) fieldAnd.getDescrs().get( 1 );
    //        constraint = (LiteralRestrictionDescr) fld.getRestrictions().get( 0 );
    //
    //        assertNotNull( constraint );
    //
    //        assertEquals( "a",
    //                      fld.getFieldName() );
    //        assertEquals( "==",
    //                      constraint.getEvaluator() );
    //        assertEquals( "4",
    //                      constraint.getText() );
    //
    //        // Check third pattern
    //        final PatternDescr third = (PatternDescr) lhs.getDescrs().get( 2 );
    //        assertNull( third.getIdentifier() );
    //        assertEquals( "Baz",
    //                      third.getObjectType() );
    //
    //        assertEqualsIgnoreWhitespace( "if ( a == b ) { " + "  assert( foo3 );" + "} else {" + "  retract( foo4 );" + "}" + "  System.out.println( a4 );",
    //                                      (String) rule.getConsequence() );
    //    }
    //
    //    public void testRestrictionsMultiple() throws Exception {
    //        final RuleDescr rule = (RuleDescr) parseResource( "rule",
    //                                                          "rule",
    //                                                          "restrictions_test.drl" );
    //
    //        assertNotNull( rule );
    //
    //        assertEqualsIgnoreWhitespace( "consequence();",
    //                                      (String) rule.getConsequence() );
    //        assertEquals( "simple_rule",
    //                      rule.getName() );
    //        assertEquals( 2,
    //                      rule.getLhs().getDescrs().size() );
    //
    //        // The first pattern, with 2 restrictions on a single field (plus a
    //        // connective)
    //        PatternDescr pattern = (PatternDescr) rule.getLhs().getDescrs().get( 0 );
    //        assertEquals( "Person",
    //                      pattern.getObjectType() );
    //        assertEquals( 1,
    //                      pattern.getConstraint().getDescrs().size() );
    //
    //        AndDescr and = (AndDescr) pattern.getConstraint();
    //        FieldConstraintDescr fld = (FieldConstraintDescr) and.getDescrs().get( 0 );
    //        assertEquals( RestrictionConnectiveDescr.AND,
    //                      ((RestrictionConnectiveDescr) fld.getRestriction()).getConnective() );
    //        assertEquals( 2,
    //                      fld.getRestrictions().size() );
    //        assertEquals( "age",
    //                      fld.getFieldName() );
    //
    //        LiteralRestrictionDescr lit = (LiteralRestrictionDescr) fld.getRestrictions().get( 0 );
    //        assertEquals( ">",
    //                      lit.getEvaluator() );
    //        assertEquals( "30",
    //                      lit.getText() );
    //
    //        lit = (LiteralRestrictionDescr) fld.getRestrictions().get( 1 );
    //        assertEquals( "<",
    //                      lit.getEvaluator() );
    //        assertEquals( "40",
    //                      lit.getText() );
    //
    //        // the second col, with 2 fields, the first with 2 restrictions, the
    //        // second field with one
    //        pattern = (PatternDescr) rule.getLhs().getDescrs().get( 1 );
    //        assertEquals( "Vehicle",
    //                      pattern.getObjectType() );
    //        assertEquals( 2,
    //                      pattern.getConstraint().getDescrs().size() );
    //
    //        and = (AndDescr) pattern.getConstraint();
    //        fld = (FieldConstraintDescr) and.getDescrs().get( 0 );
    //        assertEquals( "type",
    //                      fld.getFieldName() );
    //        assertEquals( 1,
    //                      fld.getRestrictions().size() );
    //        RestrictionConnectiveDescr or = (RestrictionConnectiveDescr) fld.getRestrictions().get( 0 );
    //        assertEquals( RestrictionConnectiveDescr.OR,
    //                      or.getConnective() );
    //        assertEquals( 2,
    //                      or.getRestrictions().size() );
    //        lit = (LiteralRestrictionDescr) or.getRestrictions().get( 0 );
    //        assertEquals( "==",
    //                      lit.getEvaluator() );
    //        assertEquals( "sedan",
    //                      lit.getText() );
    //
    //        lit = (LiteralRestrictionDescr) or.getRestrictions().get( 1 );
    //        assertEquals( "==",
    //                      lit.getEvaluator() );
    //        assertEquals( "wagon",
    //                      lit.getText() );
    //
    //        // now the second field
    //        fld = (FieldConstraintDescr) and.getDescrs().get( 1 );
    //        assertEquals( 1,
    //                      fld.getRestrictions().size() );
    //        lit = (LiteralRestrictionDescr) fld.getRestrictions().get( 0 );
    //        assertEquals( "<",
    //                      lit.getEvaluator() );
    //        assertEquals( "3",
    //                      lit.getText() );
    //
    //    }
    //
    //    public void testLineNumberInAST() throws Exception {
    //        // also see testSimpleExpander to see how this works with an expander
    //        // (should be the same).
    //
    //        final RuleDescr rule = (RuleDescr) parseResource( "rule",
    //                                                          "rule",
    //                                                          "simple_rule.drl" );
    //
    //        assertNotNull( rule );
    //
    //        assertEquals( "simple_rule",
    //                      rule.getName() );
    //
    //        assertEquals( 7,
    //                      rule.getConsequenceLine() );
    //        assertEquals( 2,
    //                      rule.getConsequencePattern() );
    //
    //        final AndDescr lhs = rule.getLhs();
    //
    //        assertNotNull( lhs );
    //
    //        assertEquals( 3,
    //                      lhs.getDescrs().size() );
    //
    //        // Check first pattern
    //        final PatternDescr first = (PatternDescr) lhs.getDescrs().get( 0 );
    //        assertEquals( "foo3",
    //                      first.getIdentifier() );
    //        assertEquals( "Bar",
    //                      first.getObjectType() );
    //        assertEquals( 1,
    //                      first.getConstraint().getDescrs().size() );
    //
    //        // Check second pattern
    //        final PatternDescr second = (PatternDescr) lhs.getDescrs().get( 1 );
    //        assertEquals( "foo4",
    //                      second.getIdentifier() );
    //        assertEquals( "Bar",
    //                      second.getObjectType() );
    //
    //        final PatternDescr third = (PatternDescr) lhs.getDescrs().get( 2 );
    //        assertEquals( "Baz",
    //                      third.getObjectType() );
    //
    //        assertEquals( 4,
    //                      first.getLine() );
    //        assertEquals( 5,
    //                      second.getLine() );
    //        assertEquals( 6,
    //                      third.getLine() );
    //    }
    //
    //    public void testLineNumberIncludingCommentsInRHS() throws Exception {
    //        parseResource( "compilation_unit",
    //                       "compilation_unit",
    //                       "test_CommentLineNumbersInConsequence.drl" );
    //
    //        final String rhs = (String) ((RuleDescr) this.walker.getPackageDescr().getRules().get( 0 )).getConsequence();
    //        String expected = "  \t//woot\n  \tfirst\n  \t\n  \t//\n  \t\n  \t/* lala\n  \t\n  \t*/\n  \tsecond  \n";
    //        assertEquals( expected,
    //                      rhs );
    //    }
    //
    //    public void testLhsSemicolonDelim() throws Exception {
    //        final RuleDescr rule = (RuleDescr) parseResource( "rule",
    //                                                          "rule",
    //                                                          "lhs_semicolon_delim.drl" );
    //
    //        assertNotNull( rule );
    //
    //        assertEquals( "simple_rule",
    //                      rule.getName() );
    //
    //        final AndDescr lhs = rule.getLhs();
    //
    //        assertNotNull( lhs );
    //
    //        assertEquals( 3,
    //                      lhs.getDescrs().size() );
    //
    //        // System.err.println( lhs.getDescrs() );
    //
    //        // Check first pattern
    //        final PatternDescr first = (PatternDescr) lhs.getDescrs().get( 0 );
    //        assertEquals( "foo3",
    //                      first.getIdentifier() );
    //        assertEquals( "Bar",
    //                      first.getObjectType() );
    //
    //        assertEquals( 1,
    //                      first.getConstraint().getDescrs().size() );
    //
    //        // LiteralDescr constraint = (LiteralDescr) first.getDescrs().get( 0 );
    //        AndDescr and = (AndDescr) first.getConstraint();
    //        FieldConstraintDescr fld = (FieldConstraintDescr) and.getDescrs().get( 0 );
    //        LiteralRestrictionDescr constraint = (LiteralRestrictionDescr) fld.getRestrictions().get( 0 );
    //
    //        assertNotNull( constraint );
    //
    //        assertEquals( "a",
    //                      fld.getFieldName() );
    //        assertEquals( "==",
    //                      constraint.getEvaluator() );
    //        assertEquals( "3",
    //                      constraint.getText() );
    //
    //        // Check second pattern
    //        final PatternDescr second = (PatternDescr) lhs.getDescrs().get( 1 );
    //        assertEquals( "foo4",
    //                      second.getIdentifier() );
    //        assertEquals( "Bar",
    //                      second.getObjectType() );
    //
    //        and = (AndDescr) second.getConstraint();
    //        assertEquals( 2,
    //                      and.getDescrs().size() );
    //
    //        // System.err.println( second.getDescrs() );
    //
    //        final FieldBindingDescr fieldBindingDescr = (FieldBindingDescr) and.getDescrs().get( 0 );
    //        assertEquals( "a",
    //                      fieldBindingDescr.getFieldName() );
    //        assertEquals( "a4",
    //                      fieldBindingDescr.getIdentifier() );
    //
    //        fld = (FieldConstraintDescr) and.getDescrs().get( 1 );
    //
    //        constraint = (LiteralRestrictionDescr) fld.getRestrictions().get( 0 );
    //
    //        assertNotNull( constraint );
    //
    //        assertEquals( "a",
    //                      fld.getFieldName() );
    //        assertEquals( "==",
    //                      constraint.getEvaluator() );
    //        assertEquals( "4",
    //                      constraint.getText() );
    //
    //        // Check third pattern
    //        final PatternDescr third = (PatternDescr) lhs.getDescrs().get( 2 );
    //        assertNull( third.getIdentifier() );
    //        assertEquals( "Baz",
    //                      third.getObjectType() );
    //
    //        assertEqualsIgnoreWhitespace( "if ( a == b ) { " + "  assert( foo3 );" + "} else {" + "  retract( foo4 );" + "}" + "  System.out.println( a4 );",
    //                                      (String) rule.getConsequence() );
    //    }
    //
    //    public void testNotNode() throws Exception {
    //        final RuleDescr rule = (RuleDescr) parseResource( "rule",
    //                                                          "rule",
    //                                                          "rule_not.drl" );
    //
    //        assertNotNull( rule );
    //        assertEquals( "simple_rule",
    //                      rule.getName() );
    //
    //        final AndDescr lhs = rule.getLhs();
    //        assertEquals( 1,
    //                      lhs.getDescrs().size() );
    //        final NotDescr not = (NotDescr) lhs.getDescrs().get( 0 );
    //        assertEquals( 1,
    //                      not.getDescrs().size() );
    //        final PatternDescr pattern = (PatternDescr) not.getDescrs().get( 0 );
    //
    //        assertEquals( "Cheese",
    //                      pattern.getObjectType() );
    //        assertEquals( 1,
    //                      pattern.getConstraint().getDescrs().size() );
    //
    //        final AndDescr and = (AndDescr) pattern.getConstraint();
    //        final FieldConstraintDescr fld = (FieldConstraintDescr) and.getDescrs().get( 0 );
    //        final LiteralRestrictionDescr lit = (LiteralRestrictionDescr) fld.getRestrictions().get( 0 );
    //
    //        assertEquals( "==",
    //                      lit.getEvaluator() );
    //        assertEquals( "stilton",
    //                      lit.getText() );
    //        assertEquals( "type",
    //                      fld.getFieldName() );
    //    }
    //
    //    public void testNotExistWithBrackets() throws Exception {
    //
    //        parseResource( "compilation_unit",
    //                       "compilation_unit",
    //                       "not_exist_with_brackets.drl" );
    //
    //        final PackageDescr pkg = walker.getPackageDescr();
    //
    //        final RuleDescr rule = (RuleDescr) pkg.getRules().get( 0 );
    //
    //        assertNotNull( rule );
    //        assertEquals( "simple_rule",
    //                      rule.getName() );
    //
    //        final AndDescr lhs = rule.getLhs();
    //        assertEquals( 2,
    //                      lhs.getDescrs().size() );
    //        final NotDescr not = (NotDescr) lhs.getDescrs().get( 0 );
    //        assertEquals( 1,
    //                      not.getDescrs().size() );
    //        final PatternDescr pattern = (PatternDescr) not.getDescrs().get( 0 );
    //
    //        assertEquals( "Cheese",
    //                      pattern.getObjectType() );
    //
    //        final ExistsDescr ex = (ExistsDescr) lhs.getDescrs().get( 1 );
    //        assertEquals( 1,
    //                      ex.getDescrs().size() );
    //        final PatternDescr exPattern = (PatternDescr) ex.getDescrs().get( 0 );
    //        assertEquals( "Foo",
    //                      exPattern.getObjectType() );
    //    }
    //
    //    public void testSimpleQuery() throws Exception {
    //        final QueryDescr query = (QueryDescr) parseResource( "query",
    //                                                             "query",
    //                                                             "simple_query.drl" );
    //
    //        assertNotNull( query );
    //
    //        assertEquals( "simple_query",
    //                      query.getName() );
    //
    //        final AndDescr lhs = query.getLhs();
    //
    //        assertNotNull( lhs );
    //
    //        assertEquals( 3,
    //                      lhs.getDescrs().size() );
    //
    //        // Check first pattern
    //        final PatternDescr first = (PatternDescr) lhs.getDescrs().get( 0 );
    //        assertEquals( "foo3",
    //                      first.getIdentifier() );
    //        assertEquals( "Bar",
    //                      first.getObjectType() );
    //
    //        assertEquals( 1,
    //                      first.getConstraint().getDescrs().size() );
    //
    //        AndDescr and = (AndDescr) first.getConstraint();
    //        FieldConstraintDescr fld = (FieldConstraintDescr) and.getDescrs().get( 0 );
    //        LiteralRestrictionDescr constraint = (LiteralRestrictionDescr) fld.getRestrictions().get( 0 );
    //        // LiteralDescr constraint = (LiteralDescr) first.getDescrs().get( 0 );
    //
    //        assertNotNull( constraint );
    //
    //        assertEquals( "a",
    //                      fld.getFieldName() );
    //        assertEquals( "==",
    //                      constraint.getEvaluator() );
    //        assertEquals( "3",
    //                      constraint.getText() );
    //
    //        // Check second pattern
    //        final PatternDescr second = (PatternDescr) lhs.getDescrs().get( 1 );
    //        assertEquals( "foo4",
    //                      second.getIdentifier() );
    //        assertEquals( "Bar",
    //                      second.getObjectType() );
    //
    //        and = (AndDescr) second.getConstraint();
    //        assertEquals( 2,
    //                      and.getDescrs().size() );
    //        // check it has field bindings.
    //        final FieldBindingDescr fieldBindingDescr = (FieldBindingDescr) and.getDescrs().get( 0 );
    //        assertEquals( "a",
    //                      fieldBindingDescr.getFieldName() );
    //        assertEquals( "a4",
    //                      fieldBindingDescr.getIdentifier() );
    //
    //        fld = (FieldConstraintDescr) and.getDescrs().get( 1 );
    //
    //        constraint = (LiteralRestrictionDescr) fld.getRestrictions().get( 0 );
    //
    //        assertNotNull( constraint );
    //
    //        assertEquals( "a",
    //                      fld.getFieldName() );
    //        assertEquals( "==",
    //                      constraint.getEvaluator() );
    //        assertEquals( "4",
    //                      constraint.getText() );
    //    }
    //
    //    public void testQueryRuleMixed() throws Exception {
    //        parseResource( "compilation_unit",
    //                       "compilation_unit",
    //                       "query_and_rule.drl" );
    //
    //        final PackageDescr pack = walker.getPackageDescr();
    //        assertEquals( 4,
    //                      pack.getRules().size() ); // as queries are rules
    //        RuleDescr rule = (RuleDescr) pack.getRules().get( 0 );
    //        assertEquals( "bar",
    //                      rule.getName() );
    //
    //        QueryDescr query = (QueryDescr) pack.getRules().get( 1 );
    //        assertEquals( "simple_query",
    //                      query.getName() );
    //
    //        rule = (RuleDescr) pack.getRules().get( 2 );
    //        assertEquals( "bar2",
    //                      rule.getName() );
    //
    //        query = (QueryDescr) pack.getRules().get( 3 );
    //        assertEquals( "simple_query2",
    //                      query.getName() );
    //    }
    //
    //    public void testMultipleRules() throws Exception {
    //        parseResource( "compilation_unit",
    //                       "compilation_unit",
    //                       "multiple_rules.drl" );
    //
    //        final PackageDescr pkg = walker.getPackageDescr();
    //        final List rules = pkg.getRules();
    //
    //        assertEquals( 2,
    //                      rules.size() );
    //
    //        final RuleDescr rule0 = (RuleDescr) rules.get( 0 );
    //        assertEquals( "Like Stilton",
    //                      rule0.getName() );
    //
    //        final RuleDescr rule1 = (RuleDescr) rules.get( 1 );
    //        assertEquals( "Like Cheddar",
    //                      rule1.getName() );
    //
    //        // checkout the first rule
    //        AndDescr lhs = rule1.getLhs();
    //        assertNotNull( lhs );
    //        assertEquals( 1,
    //                      lhs.getDescrs().size() );
    //        assertEqualsIgnoreWhitespace( "System.out.println(\"I like \" + t);",
    //                                      (String) rule0.getConsequence() );
    //
    //        // Check first pattern
    //        PatternDescr first = (PatternDescr) lhs.getDescrs().get( 0 );
    //        assertEquals( "Cheese",
    //                      first.getObjectType() );
    //
    //        // checkout the second rule
    //        lhs = rule1.getLhs();
    //        assertNotNull( lhs );
    //        assertEquals( 1,
    //                      lhs.getDescrs().size() );
    //        assertEqualsIgnoreWhitespace( "System.out.println(\"I like \" + t);",
    //                                      (String) rule1.getConsequence() );
    //
    //        // Check first pattern
    //        first = (PatternDescr) lhs.getDescrs().get( 0 );
    //        assertEquals( "Cheese",
    //                      first.getObjectType() );
    //    }
    //
    //    public void testExpanderLineSpread() throws Exception {
    //        final DrlParser parser = new DrlParser();
    //        final PackageDescr pkg = parser.parse( this.getReader( "expander_spread_lines.dslr" ),
    //                                               this.getReader( "complex.dsl" ) );
    //
    //        assertFalse( parser.getErrors().toString(),
    //                     parser.hasErrors() );
    //
    //        final RuleDescr rule = (RuleDescr) pkg.getRules().get( 0 );
    //        assertEquals( 1,
    //                      rule.getLhs().getDescrs().size() );
    //
    //        final OrDescr or = (OrDescr) rule.getLhs().getDescrs().get( 0 );
    //        assertEquals( 2,
    //                      or.getDescrs().size() );
    //        assertNotNull( (String) rule.getConsequence() );
    //
    //    }
    //
    //    public void testExpanderMultipleConstraints() throws Exception {
    //        final DrlParser parser = new DrlParser();
    //        final PackageDescr pkg = parser.parse( this.getReader( "expander_multiple_constraints.dslr" ),
    //                                               this.getReader( "multiple_constraints.dsl" ) );
    //
    //        assertFalse( parser.getErrors().toString(),
    //                     parser.hasErrors() );
    //
    //        final RuleDescr rule = (RuleDescr) pkg.getRules().get( 0 );
    //        assertEquals( 2,
    //                      rule.getLhs().getDescrs().size() );
    //
    //        PatternDescr pattern = (PatternDescr) rule.getLhs().getDescrs().get( 0 );
    //        assertEquals( "Person",
    //                      pattern.getObjectType() );
    //
    //        assertEquals( 2,
    //                      pattern.getConstraint().getDescrs().size() );
    //        assertEquals( "age",
    //                      ((FieldConstraintDescr) pattern.getConstraint().getDescrs().get( 0 )).getFieldName() );
    //        assertEquals( "location",
    //                      ((FieldConstraintDescr) pattern.getConstraint().getDescrs().get( 1 )).getFieldName() );
    //
    //        pattern = (PatternDescr) rule.getLhs().getDescrs().get( 1 );
    //        assertEquals( "Bar",
    //                      pattern.getObjectType() );
    //
    //        assertNotNull( (String) rule.getConsequence() );
    //
    //    }
    //
    //    public void testExpanderMultipleConstraintsFlush() throws Exception {
    //        final DrlParser parser = new DrlParser();
    //        // this is similar to the other test, but it requires a flush to add the
    //        // constraints
    //        final PackageDescr pkg = parser.parse( this.getReader( "expander_multiple_constraints_flush.dslr" ),
    //                                               this.getReader( "multiple_constraints.dsl" ) );
    //
    //        assertFalse( parser.getErrors().toString(),
    //                     parser.hasErrors() );
    //
    //        final RuleDescr rule = (RuleDescr) pkg.getRules().get( 0 );
    //        assertEquals( 1,
    //                      rule.getLhs().getDescrs().size() );
    //
    //        final PatternDescr pattern = (PatternDescr) rule.getLhs().getDescrs().get( 0 );
    //        assertEquals( "Person",
    //                      pattern.getObjectType() );
    //
    //        assertEquals( 2,
    //                      pattern.getConstraint().getDescrs().size() );
    //        assertEquals( "age",
    //                      ((FieldConstraintDescr) pattern.getConstraint().getDescrs().get( 0 )).getFieldName() );
    //        assertEquals( "location",
    //                      ((FieldConstraintDescr) pattern.getConstraint().getDescrs().get( 1 )).getFieldName() );
    //
    //        assertNotNull( (String) rule.getConsequence() );
    //
    //    }
    //
    //    // public void testExpanderUnExpandableErrorLines() throws Exception {
    //    //
    //    // //stubb expander
    //    // final ExpanderResolver res = new ExpanderResolver() {
    //    // public Expander get(String name,
    //    // String config) {
    //    // return new Expander() {
    //    // public String expand(String scope,
    //    // String pattern) {
    //    // if ( pattern.startsWith( "Good" ) ) {
    //    // return pattern;
    //    // } else {
    //    // throw new IllegalArgumentException( "whoops" );
    //    // }
    //    //
    //    // }
    //    // };
    //    // }
    //    // };
    //    //
    //    // final DRLParser parser = parseResource( "expander_line_errors.dslr" );
    //    // parser.setExpanderResolver( res );
    //    // parser.compilation_unit();
    //    // assertTrue( parser.hasErrors() );
    //    //
    //    // final List messages = parser.getErrorMessages();
    //    // assertEquals( messages.size(),
    //    // parser.getErrors().size() );
    //    //
    //    // assertEquals( 4,
    //    // parser.getErrors().size() );
    //    // assertEquals( ExpanderException.class,
    //    // parser.getErrors().get( 0 ).getClass() );
    //    // assertEquals( 8,
    //    // ((RecognitionException) parser.getErrors().get( 0 )).line );
    //    // assertEquals( 10,
    //    // ((RecognitionException) parser.getErrors().get( 1 )).line );
    //    // assertEquals( 12,
    //    // ((RecognitionException) parser.getErrors().get( 2 )).line );
    //    // assertEquals( 13,
    //    // ((RecognitionException) parser.getErrors().get( 3 )).line );
    //    //
    //    // final PackageDescr pack = parser.getPackageDescr();
    //    // assertNotNull( pack );
    //    //
    //    // final ExpanderException ex = (ExpanderException) parser.getErrors().get(
    //    // 0 );
    //    // assertTrue( ex.getMessage().indexOf( "whoops" ) > -1 );
    //    //
    //    // }
    //
    //    public void testBasicBinding() throws Exception {
    //        parseResource( "compilation_unit",
    //                       "compilation_unit",
    //                       "basic_binding.drl" );
    //
    //        final PackageDescr pkg = walker.getPackageDescr();
    //        final RuleDescr ruleDescr = (RuleDescr) pkg.getRules().get( 0 );
    //
    //        final AndDescr lhs = ruleDescr.getLhs();
    //        assertEquals( 1,
    //                      lhs.getDescrs().size() );
    //        final PatternDescr cheese = (PatternDescr) lhs.getDescrs().get( 0 );
    //        assertEquals( 1,
    //                      cheese.getConstraint().getDescrs().size() );
    //        assertEquals( "Cheese",
    //                      cheese.getObjectType() );
    //        assertEquals( 1,
    //                      lhs.getDescrs().size() );
    //        final FieldBindingDescr fieldBinding = (FieldBindingDescr) cheese.getConstraint().getDescrs().get( 0 );
    //        assertEquals( "type",
    //                      fieldBinding.getFieldName() );
    //    }
    //
    //    public void testBoundVariables() throws Exception {
    //        parseResource( "compilation_unit",
    //                       "compilation_unit",
    //                       "bindings.drl" );
    //
    //        final PackageDescr pkg = walker.getPackageDescr();
    //        final RuleDescr ruleDescr = (RuleDescr) pkg.getRules().get( 0 );
    //
    //        final AndDescr lhs = ruleDescr.getLhs();
    //        assertEquals( 2,
    //                      lhs.getDescrs().size() );
    //        final PatternDescr cheese = (PatternDescr) lhs.getDescrs().get( 0 );
    //        assertEquals( "Cheese",
    //                      cheese.getObjectType() );
    //        assertEquals( 2,
    //                      lhs.getDescrs().size() );
    //        FieldBindingDescr fieldBinding = (FieldBindingDescr) cheese.getConstraint().getDescrs().get( 0 );
    //        assertEquals( "type",
    //                      fieldBinding.getFieldName() );
    //
    //        FieldConstraintDescr fld = (FieldConstraintDescr) cheese.getConstraint().getDescrs().get( 1 );
    //        LiteralRestrictionDescr literalDescr = (LiteralRestrictionDescr) fld.getRestrictions().get( 0 );
    //        // LiteralDescr literalDescr = (LiteralDescr) cheese.getDescrs().get( 1
    //        // );
    //        assertEquals( "type",
    //                      fld.getFieldName() );
    //        assertEquals( "==",
    //                      literalDescr.getEvaluator() );
    //        assertEquals( "stilton",
    //                      literalDescr.getText() );
    //
    //        final PatternDescr person = (PatternDescr) lhs.getDescrs().get( 1 );
    //        fieldBinding = (FieldBindingDescr) person.getConstraint().getDescrs().get( 0 );
    //        assertEquals( "name",
    //                      fieldBinding.getFieldName() );
    //
    //        fld = (FieldConstraintDescr) person.getConstraint().getDescrs().get( 1 );
    //        literalDescr = (LiteralRestrictionDescr) fld.getRestrictions().get( 0 );
    //
    //        assertEquals( "name",
    //                      fld.getFieldName() );
    //        assertEquals( "==",
    //                      literalDescr.getEvaluator() );
    //        assertEquals( "bob",
    //                      literalDescr.getText() );
    //
    //        fld = (FieldConstraintDescr) person.getConstraint().getDescrs().get( 2 );
    //        final VariableRestrictionDescr variableDescr = (VariableRestrictionDescr) fld.getRestrictions().get( 0 );
    //
    //        assertEquals( "likes",
    //                      fld.getFieldName() );
    //        assertEquals( "==",
    //                      variableDescr.getEvaluator() );
    //        assertEquals( "$type",
    //                      variableDescr.getIdentifier() );
    //    }
    //
    //    public void testOrNesting() throws Exception {
    //        parseResource( "compilation_unit",
    //                       "compilation_unit",
    //                       "or_nesting.drl" );
    //
    //        final PackageDescr pack = walker.getPackageDescr();
    //        assertNotNull( pack );
    //        assertEquals( 1,
    //                      pack.getRules().size() );
    //        final RuleDescr rule = (RuleDescr) pack.getRules().get( 0 );
    //        assertEquals( "simple_rule",
    //                      rule.getName() );
    //
    //        assertEquals( 1,
    //                      rule.getLhs().getDescrs().size() );
    //
    //        final OrDescr or = (OrDescr) rule.getLhs().getDescrs().get( 0 );
    //        assertEquals( 2,
    //                      or.getDescrs().size() );
    //
    //        final PatternDescr first = (PatternDescr) or.getDescrs().get( 0 );
    //        assertEquals( "Person",
    //                      first.getObjectType() );
    //
    //        final AndDescr and = (AndDescr) or.getDescrs().get( 1 );
    //        assertEquals( 2,
    //                      and.getDescrs().size() );
    //
    //        final PatternDescr left = (PatternDescr) and.getDescrs().get( 0 );
    //        assertEquals( "Person",
    //                      left.getObjectType() );
    //
    //        final PatternDescr right = (PatternDescr) and.getDescrs().get( 1 );
    //        assertEquals( "Cheese",
    //                      right.getObjectType() );
    //    }
    //
    //    /** Test that explicit "&&", "||" works as expected */
    //    public void testAndOrRules() throws Exception {
    //        parseResource( "compilation_unit",
    //                       "compilation_unit",
    //                       "and_or_rule.drl" );
    //
    //        final PackageDescr pack = walker.getPackageDescr();
    //        assertNotNull( pack );
    //        assertEquals( 1,
    //                      pack.getRules().size() );
    //        final RuleDescr rule = (RuleDescr) pack.getRules().get( 0 );
    //        assertEquals( "simple_rule",
    //                      rule.getName() );
    //
    //        // we will have 2 children under the main And node
    //        final AndDescr and = rule.getLhs();
    //        assertEquals( 2,
    //                      and.getDescrs().size() );
    //
    //        // check the "&&" part
    //        final AndDescr join = (AndDescr) and.getDescrs().get( 0 );
    //        assertEquals( 2,
    //                      join.getDescrs().size() );
    //
    //        PatternDescr left = (PatternDescr) join.getDescrs().get( 0 );
    //        PatternDescr right = (PatternDescr) join.getDescrs().get( 1 );
    //        assertEquals( "Person",
    //                      left.getObjectType() );
    //        assertEquals( "Cheese",
    //                      right.getObjectType() );
    //
    //        assertEquals( 1,
    //                      left.getConstraint().getDescrs().size() );
    //
    //        FieldConstraintDescr fld = (FieldConstraintDescr) left.getConstraint().getDescrs().get( 0 );
    //        LiteralRestrictionDescr literal = (LiteralRestrictionDescr) fld.getRestrictions().get( 0 );
    //
    //        assertEquals( "==",
    //                      literal.getEvaluator() );
    //        assertEquals( "name",
    //                      fld.getFieldName() );
    //        assertEquals( "mark",
    //                      literal.getText() );
    //
    //        assertEquals( 1,
    //                      right.getConstraint().getDescrs().size() );
    //
    //        fld = (FieldConstraintDescr) right.getConstraint().getDescrs().get( 0 );
    //        literal = (LiteralRestrictionDescr) fld.getRestrictions().get( 0 );
    //
    //        assertEquals( "==",
    //                      literal.getEvaluator() );
    //        assertEquals( "type",
    //                      fld.getFieldName() );
    //        assertEquals( "stilton",
    //                      literal.getText() );
    //
    //        // now the "||" part
    //        final OrDescr or = (OrDescr) and.getDescrs().get( 1 );
    //        assertEquals( 2,
    //                      or.getDescrs().size() );
    //        left = (PatternDescr) or.getDescrs().get( 0 );
    //        right = (PatternDescr) or.getDescrs().get( 1 );
    //        assertEquals( "Person",
    //                      left.getObjectType() );
    //        assertEquals( "Cheese",
    //                      right.getObjectType() );
    //        assertEquals( 1,
    //                      left.getConstraint().getDescrs().size() );
    //
    //        fld = (FieldConstraintDescr) left.getConstraint().getDescrs().get( 0 );
    //        literal = (LiteralRestrictionDescr) fld.getRestrictions().get( 0 );
    //
    //        assertEquals( "==",
    //                      literal.getEvaluator() );
    //        assertEquals( "name",
    //                      fld.getFieldName() );
    //        assertEquals( "mark",
    //                      literal.getText() );
    //
    //        assertEquals( 1,
    //                      right.getConstraint().getDescrs().size() );
    //
    //        fld = (FieldConstraintDescr) right.getConstraint().getDescrs().get( 0 );
    //        literal = (LiteralRestrictionDescr) fld.getRestrictions().get( 0 );
    //
    //        assertEquals( "==",
    //                      literal.getEvaluator() );
    //        assertEquals( "type",
    //                      fld.getFieldName() );
    //        assertEquals( "stilton",
    //                      literal.getText() );
    //
    //        assertEqualsIgnoreWhitespace( "System.out.println( \"Mark and Michael\" );",
    //                                      (String) rule.getConsequence() );
    //    }
    //
    //    /** test basic foo : Fact() || Fact() stuff */
    //    public void testOrWithBinding() throws Exception {
    //        parseResource( "compilation_unit",
    //                       "compilation_unit",
    //                       "or_binding.drl" );
    //
    //        final PackageDescr pack = walker.getPackageDescr();
    //        assertEquals( 1,
    //                      pack.getRules().size() );
    //        final RuleDescr rule = (RuleDescr) pack.getRules().get( 0 );
    //        assertEquals( 2,
    //                      rule.getLhs().getDescrs().size() );
    //
    //        final OrDescr or = (OrDescr) rule.getLhs().getDescrs().get( 0 );
    //        assertEquals( 2,
    //                      or.getDescrs().size() );
    //
    //        final PatternDescr leftPattern = (PatternDescr) or.getDescrs().get( 0 );
    //        assertEquals( "Person",
    //                      leftPattern.getObjectType() );
    //        assertEquals( "foo",
    //                      leftPattern.getIdentifier() );
    //
    //        final PatternDescr rightPattern = (PatternDescr) or.getDescrs().get( 1 );
    //        assertEquals( "Person",
    //                      rightPattern.getObjectType() );
    //        assertEquals( "foo",
    //                      rightPattern.getIdentifier() );
    //
    //        final PatternDescr cheeseDescr = (PatternDescr) rule.getLhs().getDescrs().get( 1 );
    //        assertEquals( "Cheese",
    //                      cheeseDescr.getObjectType() );
    //        assertEquals( null,
    //                      cheeseDescr.getIdentifier() );
    //
    //        assertEqualsIgnoreWhitespace( "System.out.println( \"Mark and Michael\" + bar );",
    //                                      (String) rule.getConsequence() );
    //    }
    //
    //    /** test basic foo : Fact() || Fact() stuff binding to an "or" */
    //    public void testOrBindingComplex() throws Exception {
    //        parseResource( "compilation_unit",
    //                       "compilation_unit",
    //                       "or_binding_complex.drl" );
    //
    //        final PackageDescr pack = walker.getPackageDescr();
    //        assertEquals( 1,
    //                      pack.getRules().size() );
    //        final RuleDescr rule = (RuleDescr) pack.getRules().get( 0 );
    //        assertEquals( 1,
    //                      rule.getLhs().getDescrs().size() );
    //
    //        assertEquals( 1,
    //                      rule.getLhs().getDescrs().size() );
    //
    //        final OrDescr or = (OrDescr) rule.getLhs().getDescrs().get( 0 );
    //        assertEquals( 2,
    //                      or.getDescrs().size() );
    //
    //        // first fact
    //        final PatternDescr firstFact = (PatternDescr) or.getDescrs().get( 0 );
    //        assertEquals( "Person",
    //                      firstFact.getObjectType() );
    //        assertEquals( "foo",
    //                      firstFact.getIdentifier() );
    //
    //        // second "option"
    //        final PatternDescr secondFact = (PatternDescr) or.getDescrs().get( 1 );
    //        assertEquals( "Person",
    //                      secondFact.getObjectType() );
    //        assertEquals( 1,
    //                      secondFact.getConstraint().getDescrs().size() );
    //        assertEquals( "foo",
    //                      secondFact.getIdentifier() );
    //
    //        assertEqualsIgnoreWhitespace( "System.out.println( \"Mark and Michael\" + bar );",
    //                                      (String) rule.getConsequence() );
    //    }
    //
    //    public void testOrBindingWithBrackets() throws Exception {
    //        parseResource( "compilation_unit",
    //                       "compilation_unit",
    //                       "or_binding_with_brackets.drl" );
    //
    //        final PackageDescr pack = walker.getPackageDescr();
    //        assertEquals( 1,
    //                      pack.getRules().size() );
    //        final RuleDescr rule = (RuleDescr) pack.getRules().get( 0 );
    //        assertEquals( 1,
    //                      rule.getLhs().getDescrs().size() );
    //
    //        assertEquals( 1,
    //                      rule.getLhs().getDescrs().size() );
    //
    //        final OrDescr or = (OrDescr) rule.getLhs().getDescrs().get( 0 );
    //        assertEquals( 2,
    //                      or.getDescrs().size() );
    //
    //        // first fact
    //        final PatternDescr firstFact = (PatternDescr) or.getDescrs().get( 0 );
    //        assertEquals( "Person",
    //                      firstFact.getObjectType() );
    //        assertEquals( "foo",
    //                      firstFact.getIdentifier() );
    //
    //        // second "option"
    //        final PatternDescr secondFact = (PatternDescr) or.getDescrs().get( 0 );
    //        assertEquals( "Person",
    //                      secondFact.getObjectType() );
    //        assertEquals( "foo",
    //                      secondFact.getIdentifier() );
    //
    //        assertEqualsIgnoreWhitespace( "System.out.println( \"Mark and Michael\" + bar );",
    //                                      (String) rule.getConsequence() );
    //    }
    //
    //    /** */
    //    public void testBracketsPrecedence() throws Exception {
    //        parseResource( "compilation_unit",
    //                       "compilation_unit",
    //                       "brackets_precedence.drl" );
    //
    //        final PackageDescr pack = walker.getPackageDescr();
    //        assertEquals( 1,
    //                      pack.getRules().size() );
    //        final RuleDescr rule = (RuleDescr) pack.getRules().get( 0 );
    //
    //        assertEquals( 1,
    //                      rule.getLhs().getDescrs().size() );
    //
    //        final AndDescr rootAnd = (AndDescr) rule.getLhs().getDescrs().get( 0 );
    //
    //        assertEquals( 2,
    //                      rootAnd.getDescrs().size() );
    //
    //        final OrDescr leftOr = (OrDescr) rootAnd.getDescrs().get( 0 );
    //
    //        assertEquals( 2,
    //                      leftOr.getDescrs().size() );
    //        final NotDescr not = (NotDescr) leftOr.getDescrs().get( 0 );
    //        final PatternDescr foo1 = (PatternDescr) not.getDescrs().get( 0 );
    //        assertEquals( "Foo",
    //                      foo1.getObjectType() );
    //        final PatternDescr foo2 = (PatternDescr) leftOr.getDescrs().get( 1 );
    //        assertEquals( "Foo",
    //                      foo2.getObjectType() );
    //
    //        final OrDescr rightOr = (OrDescr) rootAnd.getDescrs().get( 1 );
    //
    //        assertEquals( 2,
    //                      rightOr.getDescrs().size() );
    //        final PatternDescr shoes = (PatternDescr) rightOr.getDescrs().get( 0 );
    //        assertEquals( "Shoes",
    //                      shoes.getObjectType() );
    //        final PatternDescr butt = (PatternDescr) rightOr.getDescrs().get( 1 );
    //        assertEquals( "Butt",
    //                      butt.getObjectType() );
    //    }
    //
    //    public void testEvalMultiple() throws Exception {
    //        parseResource( "compilation_unit",
    //                       "compilation_unit",
    //                       "eval_multiple.drl" );
    //
    //        final PackageDescr pack = walker.getPackageDescr();
    //        assertEquals( 1,
    //                      pack.getRules().size() );
    //        final RuleDescr rule = (RuleDescr) pack.getRules().get( 0 );
    //        assertEquals( 4,
    //                      rule.getLhs().getDescrs().size() );
    //
    //        final EvalDescr eval = (EvalDescr) rule.getLhs().getDescrs().get( 0 );
    //        assertEqualsIgnoreWhitespace( "abc(\"foo\") + 5",
    //                                      (String) eval.getContent() );
    //
    //        final PatternDescr pattern = (PatternDescr) rule.getLhs().getDescrs().get( 1 );
    //        assertEquals( "Foo",
    //                      pattern.getObjectType() );
    //
    //    }
    //
    //    public void testWithEval() throws Exception {
    //        parseResource( "compilation_unit",
    //                       "compilation_unit",
    //                       "with_eval.drl" );
    //
    //        final PackageDescr pack = walker.getPackageDescr();
    //        assertEquals( 1,
    //                      pack.getRules().size() );
    //        final RuleDescr rule = (RuleDescr) pack.getRules().get( 0 );
    //        assertEquals( 3,
    //                      rule.getLhs().getDescrs().size() );
    //        PatternDescr pattern = (PatternDescr) rule.getLhs().getDescrs().get( 0 );
    //        assertEquals( "Foo",
    //                      pattern.getObjectType() );
    //        pattern = (PatternDescr) rule.getLhs().getDescrs().get( 1 );
    //        assertEquals( "Bar",
    //                      pattern.getObjectType() );
    //
    //        final EvalDescr eval = (EvalDescr) rule.getLhs().getDescrs().get( 2 );
    //        assertEqualsIgnoreWhitespace( "abc(\"foo\")",
    //                                      (String) eval.getContent() );
    //        assertEqualsIgnoreWhitespace( "Kapow",
    //                                      (String) rule.getConsequence() );
    //    }
    //
    //    public void testWithRetval() throws Exception {
    //        parseResource( "compilation_unit",
    //                       "compilation_unit",
    //                       "with_retval.drl" );
    //
    //        final PackageDescr pack = walker.getPackageDescr();
    //        assertEquals( 1,
    //                      pack.getRules().size() );
    //
    //        final RuleDescr rule = (RuleDescr) pack.getRules().get( 0 );
    //        assertEquals( 1,
    //                      rule.getLhs().getDescrs().size() );
    //        final PatternDescr col = (PatternDescr) rule.getLhs().getDescrs().get( 0 );
    //        assertEquals( 1,
    //                      col.getConstraint().getDescrs().size() );
    //        assertEquals( "Foo",
    //                      col.getObjectType() );
    //        final FieldConstraintDescr fld = (FieldConstraintDescr) col.getConstraint().getDescrs().get( 0 );
    //        final ReturnValueRestrictionDescr retval = (ReturnValueRestrictionDescr) fld.getRestrictions().get( 0 );
    //
    //        assertEquals( "a + b",
    //                      retval.getContent() );
    //        assertEquals( "name",
    //                      fld.getFieldName() );
    //        assertEquals( "==",
    //                      retval.getEvaluator() );
    //    }
    //
    //    public void testWithPredicate() throws Exception {
    //        parseResource( "compilation_unit",
    //                       "compilation_unit",
    //                       "with_predicate.drl" );
    //
    //        final PackageDescr pack = walker.getPackageDescr();
    //        assertEquals( 1,
    //                      pack.getRules().size() );
    //
    //        final RuleDescr rule = (RuleDescr) pack.getRules().get( 0 );
    //        assertEquals( 1,
    //                      rule.getLhs().getDescrs().size() );
    //        final PatternDescr col = (PatternDescr) rule.getLhs().getDescrs().get( 0 );
    //        AndDescr and = (AndDescr) col.getConstraint();
    //        assertEquals( 2,
    //                      and.getDescrs().size() );
    //
    //        final FieldBindingDescr field = (FieldBindingDescr) and.getDescrs().get( 0 );
    //        final PredicateDescr pred = (PredicateDescr) and.getDescrs().get( 1 );
    //        assertEquals( "age",
    //                      field.getFieldName() );
    //        assertEquals( "$age2",
    //                      field.getIdentifier() );
    //        assertEqualsIgnoreWhitespace( "$age2 == $age1+2",
    //                                      (String) pred.getContent() );
    //    }
    //
    //    public void testNotWithConstraint() throws Exception {
    //        parseResource( "compilation_unit",
    //                       "compilation_unit",
    //                       "not_with_constraint.drl" );
    //
    //        final PackageDescr pack = walker.getPackageDescr();
    //        assertEquals( 1,
    //                      pack.getRules().size() );
    //
    //        final RuleDescr rule = (RuleDescr) pack.getRules().get( 0 );
    //        assertEquals( 2,
    //                      rule.getLhs().getDescrs().size() );
    //
    //        PatternDescr pattern = (PatternDescr) rule.getLhs().getDescrs().get( 0 );
    //        final FieldBindingDescr fieldBinding = (FieldBindingDescr) pattern.getConstraint().getDescrs().get( 0 );
    //        assertEquals( "$likes",
    //                      fieldBinding.getIdentifier() );
    //
    //        final NotDescr not = (NotDescr) rule.getLhs().getDescrs().get( 1 );
    //        pattern = (PatternDescr) not.getDescrs().get( 0 );
    //
    //        final FieldConstraintDescr fld = (FieldConstraintDescr) pattern.getConstraint().getDescrs().get( 0 );
    //        final VariableRestrictionDescr boundVariable = (VariableRestrictionDescr) fld.getRestrictions().get( 0 );
    //
    //        assertEquals( "type",
    //                      fld.getFieldName() );
    //        assertEquals( "==",
    //                      boundVariable.getEvaluator() );
    //        assertEquals( "$likes",
    //                      boundVariable.getIdentifier() );
    //    }
    //
    //    public void testFunctions() throws Exception {
    //        parseResource( "compilation_unit",
    //                       "compilation_unit",
    //                       "functions.drl" );
    //
    //        final PackageDescr pack = walker.getPackageDescr();
    //        assertEquals( 2,
    //                      pack.getRules().size() );
    //
    //        final List functions = pack.getFunctions();
    //        assertEquals( 2,
    //                      functions.size() );
    //
    //        FunctionDescr func = (FunctionDescr) functions.get( 0 );
    //        assertEquals( "functionA",
    //                      func.getName() );
    //        assertEquals( "String",
    //                      func.getReturnType() );
    //        assertEquals( 2,
    //                      func.getParameterNames().size() );
    //        assertEquals( 2,
    //                      func.getParameterTypes().size() );
    //        assertEquals( 4,
    //                      func.getLine() );
    //        assertEquals( 0,
    //                      func.getColumn() );
    //
    //        assertEquals( "String",
    //                      func.getParameterTypes().get( 0 ) );
    //        assertEquals( "s",
    //                      func.getParameterNames().get( 0 ) );
    //
    //        assertEquals( "Integer",
    //                      func.getParameterTypes().get( 1 ) );
    //        assertEquals( "i",
    //                      func.getParameterNames().get( 1 ) );
    //
    //        assertEqualsIgnoreWhitespace( "foo();",
    //                                      func.getText() );
    //
    //        func = (FunctionDescr) functions.get( 1 );
    //        assertEquals( "functionB",
    //                      func.getName() );
    //        assertEqualsIgnoreWhitespace( "bar();",
    //                                      func.getText() );
    //    }
    //
    //    public void testComment() throws Exception {
    //        parseResource( "compilation_unit",
    //                       "compilation_unit",
    //                       "comment.drl" );
    //
    //        final PackageDescr pack = walker.getPackageDescr();
    //        assertNotNull( pack );
    //
    //        assertEquals( "foo.bar",
    //                      pack.getName() );
    //    }
    //
    //    public void testAttributes() throws Exception {
    //        final RuleDescr rule = (RuleDescr) parseResource( "rule",
    //                                                          "rule",
    //                                                          "rule_attributes.drl" );
    //        assertEquals( "simple_rule",
    //                      rule.getName() );
    //        assertEqualsIgnoreWhitespace( "bar();",
    //                                      (String) rule.getConsequence() );
    //
    //        final Map<String, AttributeDescr> attrs = rule.getAttributes();
    //        assertEquals( 6,
    //                      attrs.size() );
    //
    //        AttributeDescr at = (AttributeDescr) attrs.get( "salience" );
    //        assertEquals( "salience",
    //                      at.getName() );
    //        assertEquals( "42",
    //                      at.getValue() );
    //
    //        at = (AttributeDescr) attrs.get( "agenda-group" );
    //        assertEquals( "agenda-group",
    //                      at.getName() );
    //        assertEquals( "my_group",
    //                      at.getValue() );
    //
    //        at = (AttributeDescr) attrs.get( "no-loop" );
    //        assertEquals( "no-loop",
    //                      at.getName() );
    //        assertEquals( "true",
    //                      at.getValue() );
    //
    //        at = (AttributeDescr) attrs.get( "duration" );
    //        assertEquals( "duration",
    //                      at.getName() );
    //        assertEquals( "42",
    //                      at.getValue() );
    //
    //        at = (AttributeDescr) attrs.get( "activation-group" );
    //        assertEquals( "activation-group",
    //                      at.getName() );
    //        assertEquals( "my_activation_group",
    //                      at.getValue() );
    //
    //        at = (AttributeDescr) attrs.get( "lock-on-active" );
    //        assertEquals( "lock-on-active",
    //                      at.getName() );
    //        assertEquals( "true",
    //                      at.getValue() );
    //    }
    //
    //    public void testEnabledExpression() throws Exception {
    //        final RuleDescr rule = (RuleDescr) parseResource( "rule",
    //                                                          "rule",
    //                                                          "rule_enabled_expression.drl" );
    //        assertEquals( "simple_rule",
    //                      rule.getName() );
    //        assertEqualsIgnoreWhitespace( "bar();",
    //                                      (String) rule.getConsequence() );
    //
    //        final Map<String, AttributeDescr> attrs = rule.getAttributes();
    //        assertEquals( 3,
    //                      attrs.size() );
    //
    //        AttributeDescr at = (AttributeDescr) attrs.get( "enabled" );
    //        assertEquals( "enabled",
    //                      at.getName() );
    //        assertEquals( "( 1 + 1 == 2 )",
    //                      at.getValue() );
    //
    //        at = (AttributeDescr) attrs.get( "salience" );
    //        assertEquals( "salience",
    //                      at.getName() );
    //        assertEquals( "( 1+2 )",
    //                      at.getValue() );
    //
    //        at = (AttributeDescr) attrs.get( "lock-on-active" );
    //        assertEquals( "lock-on-active",
    //                      at.getName() );
    //        assertEquals( "true",
    //                      at.getValue() );
    //    }
    //
    //    public void testDurationExpression() throws Exception {
    //        final RuleDescr rule = (RuleDescr) parseResource( "rule",
    //                                                          "rule",
    //                                                          "rule_duration_expression.drl" );
    //        assertEquals( "simple_rule",
    //                      rule.getName() );
    //        assertEqualsIgnoreWhitespace( "bar();",
    //                                      (String) rule.getConsequence() );
    //
    //        final Map<String, AttributeDescr> attrs = rule.getAttributes();
    //        assertEquals( 2,
    //                      attrs.size() );
    //
    //        AttributeDescr at = (AttributeDescr) attrs.get( "duration" );
    //        assertEquals( "duration",
    //                      at.getName() );
    //        assertEquals( "( 1h30m )",
    //                      at.getValue() );
    //
    //        at = (AttributeDescr) attrs.get( "lock-on-active" );
    //        assertEquals( "lock-on-active",
    //                      at.getName() );
    //        assertEquals( "true",
    //                      at.getValue() );
    //    }
    //
    //    public void testCalendars() throws Exception {
    //        final RuleDescr rule = (RuleDescr) parseResource( "rule",
    //                                                          "rule",
    //                                                          "rule_calendars_attribute.drl" );
    //        assertEquals( "simple_rule",
    //                      rule.getName() );
    //        assertEqualsIgnoreWhitespace( "bar();",
    //                                      (String) rule.getConsequence() );
    //
    //        final Map<String, AttributeDescr> attrs = rule.getAttributes();
    //        assertEquals( 2,
    //                      attrs.size() );
    //
    //        AttributeDescr at = (AttributeDescr) attrs.get( "calendars" );
    //        assertEquals( "calendars",
    //                      at.getName() );
    //        assertEquals( "[ \"cal1\" ]",
    //                      at.getValue() );
    //
    //        at = (AttributeDescr) attrs.get( "lock-on-active" );
    //        assertEquals( "lock-on-active",
    //                      at.getName() );
    //        assertEquals( "true",
    //                      at.getValue() );
    //    }
    //
    //    public void testCalendars2() throws Exception {
    //        final RuleDescr rule = (RuleDescr) parseResource( "rule",
    //                                                          "rule",
    //                                                          "rule_calendars_attribute2.drl" );
    //        assertEquals( "simple_rule",
    //                      rule.getName() );
    //        assertEqualsIgnoreWhitespace( "bar();",
    //                                      (String) rule.getConsequence() );
    //
    //        final Map<String, AttributeDescr> attrs = rule.getAttributes();
    //        assertEquals( 2,
    //                      attrs.size() );
    //
    //        AttributeDescr at = (AttributeDescr) attrs.get( "calendars" );
    //        assertEquals( "calendars",
    //                      at.getName() );
    //        assertEquals( "[ \"cal 1\", \"cal 2\", \"cal 3\" ]",
    //                      at.getValue() );
    //
    //        at = (AttributeDescr) attrs.get( "lock-on-active" );
    //        assertEquals( "lock-on-active",
    //                      at.getName() );
    //        assertEquals( "true",
    //                      at.getValue() );
    //    }
    //
    //    public void testAttributes_alternateSyntax() throws Exception {
    //        final RuleDescr rule = (RuleDescr) parseResource( "rule",
    //                                                          "rule",
    //                                                          "rule_attributes_alt.drl" );
    //        assertEquals( "simple_rule",
    //                      rule.getName() );
    //        assertEqualsIgnoreWhitespace( "bar();",
    //                                      (String) rule.getConsequence() );
    //
    //        final Map<String, AttributeDescr> attrs = rule.getAttributes();
    //        assertEquals( 6,
    //                      attrs.size() );
    //
    //        AttributeDescr at = (AttributeDescr) attrs.get( "salience" );
    //        assertEquals( "salience",
    //                      at.getName() );
    //        assertEquals( "42",
    //                      at.getValue() );
    //
    //        at = (AttributeDescr) attrs.get( "agenda-group" );
    //        assertEquals( "agenda-group",
    //                      at.getName() );
    //        assertEquals( "my_group",
    //                      at.getValue() );
    //
    //        at = (AttributeDescr) attrs.get( "no-loop" );
    //        assertEquals( "no-loop",
    //                      at.getName() );
    //        assertEquals( "true",
    //                      at.getValue() );
    //
    //        at = (AttributeDescr) attrs.get( "lock-on-active" );
    //        assertEquals( "lock-on-active",
    //                      at.getName() );
    //        assertEquals( "true",
    //                      at.getValue() );
    //
    //        at = (AttributeDescr) attrs.get( "duration" );
    //        assertEquals( "duration",
    //                      at.getName() );
    //        assertEquals( "42",
    //                      at.getValue() );
    //
    //        at = (AttributeDescr) attrs.get( "activation-group" );
    //        assertEquals( "activation-group",
    //                      at.getName() );
    //        assertEquals( "my_activation_group",
    //                      at.getValue() );
    //    }
    //
    //    public void testEnumeration() throws Exception {
    //        final RuleDescr rule = (RuleDescr) parseResource( "rule",
    //                                                          "rule",
    //                                                          "enumeration.drl" );
    //        assertEquals( "simple_rule",
    //                      rule.getName() );
    //        assertEquals( 1,
    //                      rule.getLhs().getDescrs().size() );
    //        final PatternDescr col = (PatternDescr) rule.getLhs().getDescrs().get( 0 );
    //        assertEquals( "Foo",
    //                      col.getObjectType() );
    //        assertEquals( 1,
    //                      col.getConstraint().getDescrs().size() );
    //        final FieldConstraintDescr fld = (FieldConstraintDescr) col.getConstraint().getDescrs().get( 0 );
    //        final QualifiedIdentifierRestrictionDescr lit = (QualifiedIdentifierRestrictionDescr) fld.getRestrictions().get( 0 );
    //
    //        assertEquals( "bar",
    //                      fld.getFieldName() );
    //        assertEquals( "==",
    //                      lit.getEvaluator() );
    //        assertEquals( "Foo.BAR",
    //                      lit.getText() );
    //    }
    //
    //    public void testExtraLhsNewline() throws Exception {
    //        parseResource( "compilation_unit",
    //                       "compilation_unit",
    //                       "extra_lhs_newline.drl" );
    //    }
    //
    //    public void testSoundsLike() throws Exception {
    //        parseResource( "compilation_unit",
    //                       "compilation_unit",
    //                       "soundslike_operator.drl" );
    //
    //        RuleDescr rule = (RuleDescr) this.walker.getPackageDescr().getRules().get( 0 );
    //        PatternDescr pat = (PatternDescr) rule.getLhs().getDescrs().get( 0 );
    //
    //        pat.getConstraint();
    //    }
    //
    //    public void testPackageAttributes() throws Exception {
    //        parseResource( "compilation_unit",
    //                       "compilation_unit",
    //                       "package_attributes.drl" );
    //
    //        PackageDescr pkg = this.walker.getPackageDescr();
    //        AttributeDescr at = (AttributeDescr) pkg.getAttributes().get( 0 );
    //        assertEquals( "agenda-group",
    //                      at.getName() );
    //        assertEquals( "x",
    //                      at.getValue() );
    //        at = (AttributeDescr) pkg.getAttributes().get( 1 );
    //        assertEquals( "dialect",
    //                      at.getName() );
    //        assertEquals( "java",
    //                      at.getValue() );
    //
    //        assertEquals( 2,
    //                      pkg.getRules().size() );
    //
    //        assertEquals( 2,
    //                      pkg.getImports().size() );
    //
    //        RuleDescr rule = (RuleDescr) pkg.getRules().get( 0 );
    //        assertEquals( "bar",
    //                      rule.getName() );
    //        at = (AttributeDescr) rule.getAttributes().get( "agenda-group" );
    //        assertEquals( "agenda-group",
    //                      at.getName() );
    //        assertEquals( "x",
    //                      at.getValue() );
    //        at = (AttributeDescr) rule.getAttributes().get( "dialect" );
    //        assertEquals( "dialect",
    //                      at.getName() );
    //        assertEquals( "java",
    //                      at.getValue() );
    //
    //        rule = (RuleDescr) pkg.getRules().get( 1 );
    //        assertEquals( "baz",
    //                      rule.getName() );
    //        at = (AttributeDescr) rule.getAttributes().get( "dialect" );
    //        assertEquals( "dialect",
    //                      at.getName() );
    //        assertEquals( "mvel",
    //                      at.getValue() );
    //        at = (AttributeDescr) rule.getAttributes().get( "agenda-group" );
    //        assertEquals( "agenda-group",
    //                      at.getName() );
    //        assertEquals( "x",
    //                      at.getValue() );
    //
    //    }
    //
    //    public void testStatementOrdering1() throws Exception {
    //        parseResource( "compilation_unit",
    //                       "compilation_unit",
    //                       "statement_ordering_1.drl" );
    //
    //        final PackageDescr pkg = this.walker.getPackageDescr();
    //
    //        assertEquals( 2,
    //                      pkg.getRules().size() );
    //
    //        assertEquals( "foo",
    //                      ((RuleDescr) pkg.getRules().get( 0 )).getName() );
    //        assertEquals( "bar",
    //                      ((RuleDescr) pkg.getRules().get( 1 )).getName() );
    //
    //        assertEquals( 2,
    //                      pkg.getFunctions().size() );
    //
    //        assertEquals( "cheeseIt",
    //                      ((FunctionDescr) pkg.getFunctions().get( 0 )).getName() );
    //        assertEquals( "uncheeseIt",
    //                      ((FunctionDescr) pkg.getFunctions().get( 1 )).getName() );
    //
    //        assertEquals( 4,
    //                      pkg.getImports().size() );
    //        assertEquals( "im.one",
    //                      ((ImportDescr) pkg.getImports().get( 0 )).getTarget() );
    //        assertEquals( "im.two",
    //                      ((ImportDescr) pkg.getImports().get( 1 )).getTarget() );
    //        assertEquals( "im.three",
    //                      ((ImportDescr) pkg.getImports().get( 2 )).getTarget() );
    //        assertEquals( "im.four",
    //                      ((ImportDescr) pkg.getImports().get( 3 )).getTarget() );
    //    }
    //
    //    public void testRuleNamesStartingWithNumbers() throws Exception {
    //        parseResource( "compilation_unit",
    //                       "compilation_unit",
    //                       "rule_names_number_prefix.drl" );
    //
    //        final PackageDescr pkg = this.walker.getPackageDescr();
    //
    //        assertEquals( 2,
    //                      pkg.getRules().size() );
    //
    //        assertEquals( "1. Do Stuff!",
    //                      ((RuleDescr) pkg.getRules().get( 0 )).getName() );
    //        assertEquals( "2. Do More Stuff!",
    //                      ((RuleDescr) pkg.getRules().get( 1 )).getName() );
    //    }
    //
    //    public void testEvalWithNewline() throws Exception {
    //        parseResource( "compilation_unit",
    //                       "compilation_unit",
    //                       "eval_with_newline.drl" );
    //    }
    //
    //    public void testEndPosition() throws Exception {
    //        parseResource( "compilation_unit",
    //                       "compilation_unit",
    //                       "test_EndPosition.drl" );
    //        final RuleDescr rule = (RuleDescr) this.walker.getPackageDescr().getRules().get( 0 );
    //        final PatternDescr col = (PatternDescr) rule.getLhs().getDescrs().get( 0 );
    //        // assertEquals( 6,
    //        // col.getLine() );
    //        //
    //        // assertEquals( 8,
    //        // col.getEndLine() );
    //    }
    //
    //    public void testQualifiedClassname() throws Exception {
    //        parseResource( "compilation_unit",
    //                       "compilation_unit",
    //                       "qualified_classname.drl" );
    //
    //        final PackageDescr pkg = this.walker.getPackageDescr();
    //        final RuleDescr rule = (RuleDescr) pkg.getRules().get( 0 );
    //
    //        final PatternDescr p = (PatternDescr) rule.getLhs().getDescrs().get( 0 );
    //
    //        assertEquals( "com.cheeseco.Cheese",
    //                      p.getObjectType() );
    //    }
    //
    //    public void testAccumulate() throws Exception {
    //        parseResource( "compilation_unit",
    //                       "compilation_unit",
    //                       "accumulate.drl" );
    //
    //        final PackageDescr pack = walker.getPackageDescr();
    //        assertEquals( 1,
    //                      pack.getRules().size() );
    //        final RuleDescr rule = (RuleDescr) pack.getRules().get( 0 );
    //        assertEquals( 1,
    //                      rule.getLhs().getDescrs().size() );
    //
    //        final PatternDescr outPattern = (PatternDescr) rule.getLhs().getDescrs().get( 0 );
    //        final AccumulateDescr accum = (AccumulateDescr) outPattern.getSource();
    //        assertEqualsIgnoreWhitespace( "int x = 0 ;",
    //                                      accum.getInitCode() );
    //        assertEqualsIgnoreWhitespace( "x++;",
    //                                      accum.getActionCode() );
    //        assertNull( accum.getReverseCode() );
    //        assertEqualsIgnoreWhitespace( "new Integer(x)",
    //                                      accum.getResultCode() );
    //
    //        assertFalse( accum.isExternalFunction() );
    //
    //        final PatternDescr pattern = (PatternDescr) accum.getInputPattern();
    //        assertEquals( "Person",
    //                      pattern.getObjectType() );
    //    }
    //
    //    public void testAccumulateWithBindings() throws Exception {
    //        parseResource( "compilation_unit",
    //                       "compilation_unit",
    //                       "accumulate_with_bindings.drl" );
    //
    //        final PackageDescr pack = walker.getPackageDescr();
    //        assertEquals( 1,
    //                      pack.getRules().size() );
    //        final RuleDescr rule = (RuleDescr) pack.getRules().get( 0 );
    //        assertEquals( 1,
    //                      rule.getLhs().getDescrs().size() );
    //
    //        final PatternDescr outPattern = (PatternDescr) rule.getLhs().getDescrs().get( 0 );
    //        final AccumulateDescr accum = (AccumulateDescr) outPattern.getSource();
    //        assertEqualsIgnoreWhitespace( "$counter",
    //                                      outPattern.getIdentifier() );
    //        assertEqualsIgnoreWhitespace( "int x = 0 ;",
    //                                      accum.getInitCode() );
    //        assertEqualsIgnoreWhitespace( "x++;",
    //                                      accum.getActionCode() );
    //        assertEqualsIgnoreWhitespace( "new Integer(x)",
    //                                      accum.getResultCode() );
    //
    //        final PatternDescr pattern = (PatternDescr) accum.getInputPattern();
    //        assertEquals( "Person",
    //                      pattern.getObjectType() );
    //    }
    //
    //    public void testCollect() throws Exception {
    //        parseResource( "compilation_unit",
    //                       "compilation_unit",
    //                       "collect.drl" );
    //
    //        final PackageDescr pack = walker.getPackageDescr();
    //        assertEquals( 1,
    //                      pack.getRules().size() );
    //        final RuleDescr rule = (RuleDescr) pack.getRules().get( 0 );
    //        assertEquals( 1,
    //                      rule.getLhs().getDescrs().size() );
    //
    //        final PatternDescr outPattern = (PatternDescr) rule.getLhs().getDescrs().get( 0 );
    //        final CollectDescr collect = (CollectDescr) outPattern.getSource();
    //
    //        final PatternDescr pattern = (PatternDescr) collect.getInputPattern();
    //        assertEquals( "Person",
    //                      pattern.getObjectType() );
    //    }
    //
    //    public void testPredicate() throws Exception {
    //        final PatternDescr pattern = (PatternDescr) parse( "lhs_pattern",
    //                                                           "lhs_pattern",
    //                                                           "Foo ($var : attr -> ( $var.equals(\"xyz\") ))" );
    //
    //        final List constraints = pattern.getConstraint().getDescrs();
    //        assertEquals( 2,
    //                      constraints.size() );
    //
    //        final FieldBindingDescr field = (FieldBindingDescr) constraints.get( 0 );
    //        final PredicateDescr predicate = (PredicateDescr) constraints.get( 1 );
    //        assertEquals( "$var",
    //                      field.getIdentifier() );
    //        assertEquals( "attr",
    //                      field.getFieldName() );
    //        assertEquals( " $var.equals(\"xyz\") ",
    //                      predicate.getContent() );
    //    }
    //
    //    public void testPredicate2() throws Exception {
    //        // predicates are also prefixed by the eval keyword
    //        final PatternDescr pattern = (PatternDescr) parse( "lhs_pattern",
    //                                                           "lhs_pattern",
    //                                                           "Foo(eval( $var.equals(\"xyz\") ))" );
    //
    //        final List constraints = pattern.getConstraint().getDescrs();
    //        assertEquals( 1,
    //                      constraints.size() );
    //
    //        final PredicateDescr predicate = (PredicateDescr) constraints.get( 0 );
    //        assertEquals( " $var.equals(\"xyz\") ",
    //                      predicate.getContent() );
    //    }
    //
    //    public void testEscapedStrings() throws Exception {
    //        final RuleDescr rule = (RuleDescr) parseResource( "rule",
    //                                                          "rule",
    //                                                          "escaped-string.drl" );
    //
    //        assertNotNull( rule );
    //
    //        assertEquals( "test_Quotes",
    //                      rule.getName() );
    //
    //        final String expected = "String s = \"\\\"\\n\\t\\\\\";";
    //
    //        assertEqualsIgnoreWhitespace( expected,
    //                                      (String) rule.getConsequence() );
    //    }
    //
    //    public void testNestedCEs() throws Exception {
    //        final RuleDescr rule = (RuleDescr) parseResource( "rule",
    //                                                          "rule",
    //                                                          "nested_conditional_elements.drl" );
    //
    //        assertNotNull( rule );
    //
    //        final AndDescr root = rule.getLhs();
    //        final NotDescr not1 = (NotDescr) root.getDescrs().get( 0 );
    //        final AndDescr and1 = (AndDescr) not1.getDescrs().get( 0 );
    //
    //        final PatternDescr state = (PatternDescr) and1.getDescrs().get( 0 );
    //        final NotDescr not2 = (NotDescr) and1.getDescrs().get( 1 );
    //        final AndDescr and2 = (AndDescr) not2.getDescrs().get( 0 );
    //        final PatternDescr person = (PatternDescr) and2.getDescrs().get( 0 );
    //        final PatternDescr cheese = (PatternDescr) and2.getDescrs().get( 1 );
    //
    //        final PatternDescr person2 = (PatternDescr) root.getDescrs().get( 1 );
    //        final OrDescr or = (OrDescr) root.getDescrs().get( 2 );
    //        final PatternDescr cheese2 = (PatternDescr) or.getDescrs().get( 0 );
    //        final PatternDescr cheese3 = (PatternDescr) or.getDescrs().get( 1 );
    //
    //        assertEquals( state.getObjectType(),
    //                      "State" );
    //        assertEquals( person.getObjectType(),
    //                      "Person" );
    //        assertEquals( cheese.getObjectType(),
    //                      "Cheese" );
    //        assertEquals( person2.getObjectType(),
    //                      "Person" );
    //        assertEquals( cheese2.getObjectType(),
    //                      "Cheese" );
    //        assertEquals( cheese3.getObjectType(),
    //                      "Cheese" );
    //    }
    //
    //    public void testForall() throws Exception {
    //        parseResource( "compilation_unit",
    //                       "compilation_unit",
    //                       "forall.drl" );
    //
    //        final PackageDescr pack = walker.getPackageDescr();
    //        assertEquals( 1,
    //                      pack.getRules().size() );
    //        final RuleDescr rule = (RuleDescr) pack.getRules().get( 0 );
    //        assertEquals( 1,
    //                      rule.getLhs().getDescrs().size() );
    //
    //        final ForallDescr forall = (ForallDescr) rule.getLhs().getDescrs().get( 0 );
    //
    //        assertEquals( 2,
    //                      forall.getDescrs().size() );
    //        final PatternDescr pattern = forall.getBasePattern();
    //        assertEquals( "Person",
    //                      pattern.getObjectType() );
    //        final List remaining = forall.getRemainingPatterns();
    //        assertEquals( 1,
    //                      remaining.size() );
    //        final PatternDescr cheese = (PatternDescr) remaining.get( 0 );
    //        assertEquals( "Cheese",
    //                      cheese.getObjectType() );
    //    }
    //
    //    public void testForCE() throws Exception {
    //        parseResource( "compilation_unit",
    //                       "compilation_unit",
    //                       "forCE.drl" );
    ////
    ////        final PackageDescr pkg = walker.getPackageDescr();
    ////        assertEquals( 1,
    ////                      pkg.getRules().size() );
    ////        final RuleDescr rule = (RuleDescr) pkg.getRules().get( 0 );
    ////        assertEquals( 1,
    ////                      rule.getLhs().getDescrs().size() );
    ////
    ////        final ForallDescr forall = (ForallDescr) rule.getLhs().getDescrs().get( 0 );
    ////
    ////        assertEquals( 2,
    ////                      forall.getDescrs().size() );
    ////        final PatternDescr pattern = forall.getBasePattern();
    ////        assertEquals( "Person",
    ////                      pattern.getObjectType() );
    ////        final List remaining = forall.getRemainingPatterns();
    ////        assertEquals( 1,
    ////                      remaining.size() );
    ////        final PatternDescr cheese = (PatternDescr) remaining.get( 0 );
    ////        assertEquals( "Cheese",
    ////                      cheese.getObjectType() );
    //    }
    //
    //    public void testForallWithFrom() throws Exception {
    //        parseResource( "compilation_unit",
    //                       "compilation_unit",
    //                       "forallwithfrom.drl" );
    //
    //        final PackageDescr pack = walker.getPackageDescr();
    //        assertEquals( 1,
    //                      pack.getRules().size() );
    //        final RuleDescr rule = (RuleDescr) pack.getRules().get( 0 );
    //        assertEquals( 1,
    //                      rule.getLhs().getDescrs().size() );
    //
    //        final ForallDescr forall = (ForallDescr) rule.getLhs().getDescrs().get( 0 );
    //
    //        assertEquals( 2,
    //                      forall.getDescrs().size() );
    //        final PatternDescr pattern = forall.getBasePattern();
    //        assertEquals( "Person",
    //                      pattern.getObjectType() );
    //        assertEquals( "$village",
    //                      ((FromDescr) pattern.getSource()).getDataSource().toString() );
    //        final List remaining = forall.getRemainingPatterns();
    //        assertEquals( 1,
    //                      remaining.size() );
    //        final PatternDescr cheese = (PatternDescr) remaining.get( 0 );
    //        assertEquals( "Cheese",
    //                      cheese.getObjectType() );
    //        assertEquals( "$cheesery",
    //                      ((FromDescr) cheese.getSource()).getDataSource().toString() );
    //    }
    //
    //    public void testMemberof() throws Exception {
    //        final String text = "Country( $cities : city )\nPerson( city memberOf $cities )\n";
    //        AndDescr descrs = (AndDescr) parse( "normal_lhs_block",
    //                                            "lhs_block",
    //                                            text );
    //
    //        assertEquals( 2,
    //                      descrs.getDescrs().size() );
    //        PatternDescr pat = (PatternDescr) descrs.getDescrs().get( 1 );
    //        FieldConstraintDescr fieldConstr = (FieldConstraintDescr) pat.getConstraint().getDescrs().get( 0 );
    //        VariableRestrictionDescr restr = (VariableRestrictionDescr) fieldConstr.getRestrictions().get( 0 );
    //
    //        assertEquals( "memberOf",
    //                      restr.getEvaluator() );
    //        assertFalse( restr.isNegated() );
    //        assertEquals( "$cities",
    //                      restr.getIdentifier() );
    //    }
    //
    //    public void testNotMemberof() throws Exception {
    //        final String text = "Country( $cities : city )\nPerson( city not memberOf $cities )\n";
    //        AndDescr descrs = (AndDescr) parse( "normal_lhs_block",
    //                                            "lhs_block",
    //                                            text );
    //
    //        assertEquals( 2,
    //                      descrs.getDescrs().size() );
    //        PatternDescr pat = (PatternDescr) descrs.getDescrs().get( 1 );
    //        FieldConstraintDescr fieldConstr = (FieldConstraintDescr) pat.getConstraint().getDescrs().get( 0 );
    //        VariableRestrictionDescr restr = (VariableRestrictionDescr) fieldConstr.getRestrictions().get( 0 );
    //
    //        assertEquals( "memberOf",
    //                      restr.getEvaluator() );
    //        assertTrue( restr.isNegated() );
    //        assertEquals( "$cities",
    //                      restr.getIdentifier() );
    //    }
    //
    //    public void testInOperator() throws Exception {
    //        final RuleDescr rule = (RuleDescr) parseResource( "rule",
    //                                                          "rule",
    //                                                          "in_operator_test.drl" );
    //
    //        assertNotNull( rule );
    //
    //        assertEqualsIgnoreWhitespace( "consequence();",
    //                                      (String) rule.getConsequence() );
    //        assertEquals( "simple_rule",
    //                      rule.getName() );
    //        assertEquals( 2,
    //                      rule.getLhs().getDescrs().size() );
    //
    //        // The first pattern, with 2 restrictions on a single field (plus a
    //        // connective)
    //        PatternDescr pattern = (PatternDescr) rule.getLhs().getDescrs().get( 0 );
    //        assertEquals( "Person",
    //                      pattern.getObjectType() );
    //        assertEquals( 1,
    //                      pattern.getConstraint().getDescrs().size() );
    //
    //        FieldConstraintDescr fld = (FieldConstraintDescr) pattern.getConstraint().getDescrs().get( 0 );
    //        assertEquals( RestrictionConnectiveDescr.AND,
    //                      fld.getRestriction().getConnective() );
    //        assertEquals( 2,
    //                      fld.getRestrictions().size() );
    //        assertEquals( "age",
    //                      fld.getFieldName() );
    //
    //        LiteralRestrictionDescr lit = (LiteralRestrictionDescr) fld.getRestrictions().get( 0 );
    //        assertEquals( ">",
    //                      lit.getEvaluator() );
    //        assertEquals( "30",
    //                      lit.getText() );
    //
    //        lit = (LiteralRestrictionDescr) fld.getRestrictions().get( 1 );
    //        assertEquals( "<",
    //                      lit.getEvaluator() );
    //        assertEquals( "40",
    //                      lit.getText() );
    //
    //        // the second col, with 2 fields, the first with 2 restrictions, the
    //        // second field with one
    //        pattern = (PatternDescr) rule.getLhs().getDescrs().get( 1 );
    //        assertEquals( "Vehicle",
    //                      pattern.getObjectType() );
    //        assertEquals( 2,
    //                      pattern.getConstraint().getDescrs().size() );
    //
    //        fld = (FieldConstraintDescr) pattern.getConstraint().getDescrs().get( 0 );
    //        assertEquals( "type",
    //                      fld.getFieldName() );
    //        assertEquals( 1,
    //                      fld.getRestrictions().size() );
    //
    //        RestrictionConnectiveDescr or = (RestrictionConnectiveDescr) fld.getRestrictions().get( 0 );
    //        assertEquals( RestrictionConnectiveDescr.OR,
    //                      or.getConnective() );
    //        assertEquals( 2,
    //                      or.getRestrictions().size() );
    //
    //        lit = (LiteralRestrictionDescr) or.getRestrictions().get( 0 );
    //        assertEquals( "==",
    //                      lit.getEvaluator() );
    //        assertEquals( "sedan",
    //                      lit.getText() );
    //
    //        lit = (LiteralRestrictionDescr) or.getRestrictions().get( 1 );
    //        assertEquals( "==",
    //                      lit.getEvaluator() );
    //        assertEquals( "wagon",
    //                      lit.getText() );
    //
    //        // now the second field
    //        fld = (FieldConstraintDescr) pattern.getConstraint().getDescrs().get( 1 );
    //        assertEquals( 1,
    //                      fld.getRestrictions().size() );
    //        lit = (LiteralRestrictionDescr) fld.getRestrictions().get( 0 );
    //        assertEquals( "<",
    //                      lit.getEvaluator() );
    //        assertEquals( "3",
    //                      lit.getText() );
    //
    //    }
    //
    //    public void testNotInOperator() throws Exception {
    //        final RuleDescr rule = (RuleDescr) parseResource( "rule",
    //                                                          "rule",
    //                                                          "notin_operator_test.drl" );
    //
    //        assertNotNull( rule );
    //
    //        assertEqualsIgnoreWhitespace( "consequence();",
    //                                      (String) rule.getConsequence() );
    //        assertEquals( "simple_rule",
    //                      rule.getName() );
    //        assertEquals( 2,
    //                      rule.getLhs().getDescrs().size() );
    //
    //        // The first pattern, with 2 restrictions on a single field (plus a
    //        // connective)
    //        PatternDescr pattern = (PatternDescr) rule.getLhs().getDescrs().get( 0 );
    //        assertEquals( "Person",
    //                      pattern.getObjectType() );
    //        assertEquals( 1,
    //                      pattern.getConstraint().getDescrs().size() );
    //
    //        FieldConstraintDescr fld = (FieldConstraintDescr) pattern.getConstraint().getDescrs().get( 0 );
    //        assertEquals( 2,
    //                      fld.getRestrictions().size() );
    //        assertEquals( "age",
    //                      fld.getFieldName() );
    //
    //        LiteralRestrictionDescr lit = (LiteralRestrictionDescr) fld.getRestrictions().get( 0 );
    //        assertEquals( ">",
    //                      lit.getEvaluator() );
    //        assertEquals( "30",
    //                      lit.getText() );
    //
    //        lit = (LiteralRestrictionDescr) fld.getRestrictions().get( 1 );
    //        assertEquals( "<",
    //                      lit.getEvaluator() );
    //        assertEquals( "40",
    //                      lit.getText() );
    //
    //        // the second col, with 2 fields, the first with 2 restrictions, the
    //        // second field with one
    //        pattern = (PatternDescr) rule.getLhs().getDescrs().get( 1 );
    //        assertEquals( "Vehicle",
    //                      pattern.getObjectType() );
    //        assertEquals( 2,
    //                      pattern.getConstraint().getDescrs().size() );
    //
    //        fld = (FieldConstraintDescr) pattern.getConstraint().getDescrs().get( 0 );
    //        assertEquals( 2,
    //                      fld.getRestrictions().size() );
    //        lit = (LiteralRestrictionDescr) fld.getRestrictions().get( 0 );
    //        assertEquals( "type",
    //                      fld.getFieldName() );
    //        assertEquals( "!=",
    //                      lit.getEvaluator() );
    //        assertEquals( "sedan",
    //                      lit.getText() );
    //
    //        lit = (LiteralRestrictionDescr) fld.getRestrictions().get( 1 );
    //        assertEquals( "!=",
    //                      lit.getEvaluator() );
    //        assertEquals( "wagon",
    //                      lit.getText() );
    //
    //        // now the second field
    //        fld = (FieldConstraintDescr) pattern.getConstraint().getDescrs().get( 1 );
    //        assertEquals( 1,
    //                      fld.getRestrictions().size() );
    //        lit = (LiteralRestrictionDescr) fld.getRestrictions().get( 0 );
    //        assertEquals( "<",
    //                      lit.getEvaluator() );
    //        assertEquals( "3",
    //                      lit.getText() );
    //
    //    }
    //
    //    public void testCheckOrDescr() throws Exception {
    //        final String text = "Person( eval( age == 25 ) || ( eval( name.equals( \"bob\" ) ) && eval( age == 30 ) ) )";
    //        PatternDescr pattern = (PatternDescr) parse( "lhs_pattern",
    //                                                     "lhs_pattern",
    //                                                     text );
    //
    //        assertEquals( 1,
    //                      pattern.getDescrs().size() );
    //        assertEquals( pattern.getConstraint().getClass(),
    //                      org.drools.lang.descr.AndDescr.class );
    //
    //        assertEquals( pattern.getConstraint().getDescrs().get( 0 ).getClass(),
    //                      org.drools.lang.descr.OrDescr.class );
    //
    //        OrDescr orDescr = (OrDescr) pattern.getConstraint().getDescrs().get( 0 );
    //        assertEquals( orDescr.getDescrs().get( 0 ).getClass(),
    //                      org.drools.lang.descr.PredicateDescr.class );
    //    }
    //
    //    public void testConstraintAndConnective() throws Exception {
    //        final String text = "Person( age < 42 && location==\"atlanta\")";
    //        PatternDescr pattern = (PatternDescr) parse( "lhs_pattern",
    //                                                     "lhs_pattern",
    //                                                     text );
    //
    //        assertEquals( 2,
    //                      pattern.getDescrs().size() );
    //        FieldConstraintDescr fcd = (FieldConstraintDescr) pattern.getDescrs().get( 0 );
    //        assertEquals( "age",
    //                      fcd.getFieldName() );
    //        fcd = (FieldConstraintDescr) pattern.getDescrs().get( 1 );
    //        assertEquals( "location",
    //                      fcd.getFieldName() );
    //    }
    //
    //    public void testConstraintOrConnective() throws Exception {
    //        final String text = "Person( age < 42 || location==\"atlanta\")";
    //        PatternDescr pattern = (PatternDescr) parse( "lhs_pattern",
    //                                                     "lhs_pattern",
    //                                                     text );
    //
    //        assertEquals( 1,
    //                      pattern.getDescrs().size() );
    //        OrDescr or = (OrDescr) pattern.getDescrs().get( 0 );
    //        assertEquals( 2,
    //                      or.getDescrs().size() );
    //        FieldConstraintDescr fcd = (FieldConstraintDescr) or.getDescrs().get( 0 );
    //        assertEquals( "age",
    //                      fcd.getFieldName() );
    //        fcd = (FieldConstraintDescr) or.getDescrs().get( 1 );
    //        assertEquals( "location",
    //                      fcd.getFieldName() );
    //    }
    //
    //    public void testConstraintConnectivesPrecedence() throws Exception {
    //        final String text = "Person( age < 42 && location==\"atlanta\" || age > 20 && location==\"Seatle\" || location == \"Chicago\")";
    //
    //        PatternDescr pattern = (PatternDescr) parse( "lhs_pattern",
    //                                                     "lhs_pattern",
    //                                                     text );
    //
    //        assertEquals( 1,
    //                      pattern.getDescrs().size() );
    //        OrDescr or = (OrDescr) pattern.getDescrs().get( 0 );
    //        assertEquals( 3,
    //                      or.getDescrs().size() );
    //
    //        AndDescr and = (AndDescr) or.getDescrs().get( 0 );
    //        assertEquals( 2,
    //                      and.getDescrs().size() );
    //        FieldConstraintDescr fcd = (FieldConstraintDescr) and.getDescrs().get( 0 );
    //        assertEquals( "age",
    //                      fcd.getFieldName() );
    //        assertEquals( "<",
    //                      ((LiteralRestrictionDescr) fcd.getRestrictions().get( 0 )).getEvaluator() );
    //        assertEquals( "42",
    //                      ((LiteralRestrictionDescr) fcd.getRestrictions().get( 0 )).getText() );
    //        fcd = (FieldConstraintDescr) and.getDescrs().get( 1 );
    //        assertEquals( "location",
    //                      fcd.getFieldName() );
    //        assertEquals( "==",
    //                      ((LiteralRestrictionDescr) fcd.getRestrictions().get( 0 )).getEvaluator() );
    //        assertEquals( "atlanta",
    //                      ((LiteralRestrictionDescr) fcd.getRestrictions().get( 0 )).getText() );
    //
    //        and = (AndDescr) or.getDescrs().get( 1 );
    //        assertEquals( 2,
    //                      and.getDescrs().size() );
    //        fcd = (FieldConstraintDescr) and.getDescrs().get( 0 );
    //        assertEquals( "age",
    //                      fcd.getFieldName() );
    //        assertEquals( ">",
    //                      ((LiteralRestrictionDescr) fcd.getRestrictions().get( 0 )).getEvaluator() );
    //        assertEquals( "20",
    //                      ((LiteralRestrictionDescr) fcd.getRestrictions().get( 0 )).getText() );
    //        fcd = (FieldConstraintDescr) and.getDescrs().get( 1 );
    //        assertEquals( "location",
    //                      fcd.getFieldName() );
    //        assertEquals( "==",
    //                      ((LiteralRestrictionDescr) fcd.getRestrictions().get( 0 )).getEvaluator() );
    //        assertEquals( "Seatle",
    //                      ((LiteralRestrictionDescr) fcd.getRestrictions().get( 0 )).getText() );
    //
    //        fcd = (FieldConstraintDescr) or.getDescrs().get( 2 );
    //        assertEquals( "location",
    //                      fcd.getFieldName() );
    //        assertEquals( "==",
    //                      ((LiteralRestrictionDescr) fcd.getRestrictions().get( 0 )).getEvaluator() );
    //        assertEquals( "Chicago",
    //                      ((LiteralRestrictionDescr) fcd.getRestrictions().get( 0 )).getText() );
    //
    //    }
    //
    //    public void testConstraintConnectivesPrecedenceWithBracks() throws Exception {
    //        final String text = "Person( age < 42 && ( location==\"atlanta\" || age > 20 && location==\"Seatle\") || location == \"Chicago\")";
    //
    //        PatternDescr pattern = (PatternDescr) parse( "lhs_pattern",
    //                                                     "lhs_pattern",
    //                                                     text );
    //
    //        assertEquals( 1,
    //                      pattern.getDescrs().size() );
    //        OrDescr or1 = (OrDescr) pattern.getDescrs().get( 0 );
    //        assertEquals( 2,
    //                      or1.getDescrs().size() );
    //
    //        AndDescr and1 = (AndDescr) or1.getDescrs().get( 0 );
    //        assertEquals( 2,
    //                      and1.getDescrs().size() );
    //        FieldConstraintDescr fcd = (FieldConstraintDescr) and1.getDescrs().get( 0 );
    //        assertEquals( "age",
    //                      fcd.getFieldName() );
    //        assertEquals( "<",
    //                      ((LiteralRestrictionDescr) fcd.getRestrictions().get( 0 )).getEvaluator() );
    //        assertEquals( "42",
    //                      ((LiteralRestrictionDescr) fcd.getRestrictions().get( 0 )).getText() );
    //
    //        OrDescr or2 = (OrDescr) and1.getDescrs().get( 1 );
    //        fcd = (FieldConstraintDescr) or2.getDescrs().get( 0 );
    //        assertEquals( "location",
    //                      fcd.getFieldName() );
    //        assertEquals( "==",
    //                      ((LiteralRestrictionDescr) fcd.getRestrictions().get( 0 )).getEvaluator() );
    //        assertEquals( "atlanta",
    //                      ((LiteralRestrictionDescr) fcd.getRestrictions().get( 0 )).getText() );
    //
    //        AndDescr and2 = (AndDescr) or2.getDescrs().get( 1 );
    //        assertEquals( 2,
    //                      and2.getDescrs().size() );
    //        fcd = (FieldConstraintDescr) and2.getDescrs().get( 0 );
    //        assertEquals( "age",
    //                      fcd.getFieldName() );
    //        assertEquals( ">",
    //                      ((LiteralRestrictionDescr) fcd.getRestrictions().get( 0 )).getEvaluator() );
    //        assertEquals( "20",
    //                      ((LiteralRestrictionDescr) fcd.getRestrictions().get( 0 )).getText() );
    //        fcd = (FieldConstraintDescr) and2.getDescrs().get( 1 );
    //        assertEquals( "location",
    //                      fcd.getFieldName() );
    //        assertEquals( "==",
    //                      ((LiteralRestrictionDescr) fcd.getRestrictions().get( 0 )).getEvaluator() );
    //        assertEquals( "Seatle",
    //                      ((LiteralRestrictionDescr) fcd.getRestrictions().get( 0 )).getText() );
    //
    //        fcd = (FieldConstraintDescr) or1.getDescrs().get( 1 );
    //        assertEquals( "location",
    //                      fcd.getFieldName() );
    //        assertEquals( "==",
    //                      ((LiteralRestrictionDescr) fcd.getRestrictions().get( 0 )).getEvaluator() );
    //        assertEquals( "Chicago",
    //                      ((LiteralRestrictionDescr) fcd.getRestrictions().get( 0 )).getText() );
    //    }
    //
    //    public void testConstraintConnectivesPrecedenceWithBracks2() throws Exception {
    //        final String text = "Person( ( age == 70 && hair == \"black\" ) || ( age == 40 && hair == \"pink\" ) || ( age == 12 && ( hair == \"yellow\" || hair == \"blue\" ) ) )";
    //
    //        PatternDescr pattern = (PatternDescr) parse( "lhs_pattern",
    //                                                     "lhs_pattern",
    //                                                     text );
    //
    //        assertEquals( 1,
    //                      pattern.getDescrs().size() );
    //        OrDescr or1 = (OrDescr) pattern.getDescrs().get( 0 );
    //        assertEquals( 3,
    //                      or1.getDescrs().size() );
    //
    //        AndDescr and1 = (AndDescr) or1.getDescrs().get( 0 );
    //        AndDescr and2 = (AndDescr) or1.getDescrs().get( 1 );
    //        AndDescr and3 = (AndDescr) or1.getDescrs().get( 2 );
    //
    //        assertEquals( 2,
    //                      and1.getDescrs().size() );
    //        FieldConstraintDescr fcd = (FieldConstraintDescr) and1.getDescrs().get( 0 );
    //        assertEquals( "age",
    //                      fcd.getFieldName() );
    //        assertEquals( "==",
    //                      ((LiteralRestrictionDescr) fcd.getRestrictions().get( 0 )).getEvaluator() );
    //        assertEquals( "70",
    //                      ((LiteralRestrictionDescr) fcd.getRestrictions().get( 0 )).getText() );
    //        fcd = (FieldConstraintDescr) and1.getDescrs().get( 1 );
    //        assertEquals( "hair",
    //                      fcd.getFieldName() );
    //        assertEquals( "==",
    //                      ((LiteralRestrictionDescr) fcd.getRestrictions().get( 0 )).getEvaluator() );
    //        assertEquals( "black",
    //                      ((LiteralRestrictionDescr) fcd.getRestrictions().get( 0 )).getText() );
    //
    //        assertEquals( 2,
    //                      and2.getDescrs().size() );
    //        fcd = (FieldConstraintDescr) and2.getDescrs().get( 0 );
    //        assertEquals( "age",
    //                      fcd.getFieldName() );
    //        assertEquals( "==",
    //                      ((LiteralRestrictionDescr) fcd.getRestrictions().get( 0 )).getEvaluator() );
    //        assertEquals( "40",
    //                      ((LiteralRestrictionDescr) fcd.getRestrictions().get( 0 )).getText() );
    //        fcd = (FieldConstraintDescr) and2.getDescrs().get( 1 );
    //        assertEquals( "hair",
    //                      fcd.getFieldName() );
    //        assertEquals( "==",
    //                      ((LiteralRestrictionDescr) fcd.getRestrictions().get( 0 )).getEvaluator() );
    //        assertEquals( "pink",
    //                      ((LiteralRestrictionDescr) fcd.getRestrictions().get( 0 )).getText() );
    //
    //        assertEquals( 2,
    //                      and3.getDescrs().size() );
    //        fcd = (FieldConstraintDescr) and3.getDescrs().get( 0 );
    //        assertEquals( "age",
    //                      fcd.getFieldName() );
    //        assertEquals( "==",
    //                      ((LiteralRestrictionDescr) fcd.getRestrictions().get( 0 )).getEvaluator() );
    //        assertEquals( "12",
    //                      ((LiteralRestrictionDescr) fcd.getRestrictions().get( 0 )).getText() );
    //        OrDescr or2 = (OrDescr) and3.getDescrs().get( 1 );
    //        fcd = (FieldConstraintDescr) or2.getDescrs().get( 0 );
    //        assertEquals( "hair",
    //                      fcd.getFieldName() );
    //        assertEquals( "==",
    //                      ((LiteralRestrictionDescr) fcd.getRestrictions().get( 0 )).getEvaluator() );
    //        assertEquals( "yellow",
    //                      ((LiteralRestrictionDescr) fcd.getRestrictions().get( 0 )).getText() );
    //        fcd = (FieldConstraintDescr) or2.getDescrs().get( 1 );
    //        assertEquals( "hair",
    //                      fcd.getFieldName() );
    //        assertEquals( "==",
    //                      ((LiteralRestrictionDescr) fcd.getRestrictions().get( 0 )).getEvaluator() );
    //        assertEquals( "blue",
    //                      ((LiteralRestrictionDescr) fcd.getRestrictions().get( 0 )).getText() );
    //    }
    //
    //    public void testSimpleRestrictionConnective() throws Exception {
    //
    //        final String text = "Person( age == 12 || ( test == 222 ))";
    //
    //        PatternDescr pattern = (PatternDescr) parse( "lhs_pattern",
    //                                                     "lhs_pattern",
    //                                                     text );
    //
    //        assertEquals( 1,
    //                      pattern.getDescrs().size() );
    //    }
    //
    //    public void testRestrictionConnectives() throws Exception {
    //
    //        // the expression bellow must generate the following tree:
    //        //
    //        // AND
    //        // |
    //        // OR
    //        // /---------------+-------------------\
    //        // AND AND AND
    //        // /---+---\ /---+---\ /---+---\
    //        // FC FC FC FC FC OR
    //        // /---+---\
    //        // FC FC
    //        //
    //        final String text = "Person( ( age ( > 60 && < 70 ) || ( > 50 && < 55 ) && hair == \"black\" ) || ( age == 40 && hair == \"pink\" ) || ( age == 12 && ( hair == \"yellow\" || hair == \"blue\" ) ))";
    //
    //        PatternDescr pattern = (PatternDescr) parse( "lhs_pattern",
    //                                                     "lhs_pattern",
    //                                                     text );
    //
    //        assertEquals( 1,
    //                      pattern.getDescrs().size() );
    //
    //        OrDescr orConstr = (OrDescr) pattern.getDescrs().get( 0 );
    //
    //        assertEquals( 3,
    //                      orConstr.getDescrs().size() );
    //
    //        AndDescr andConstr1 = (AndDescr) orConstr.getDescrs().get( 0 );
    //
    //        FieldConstraintDescr fcd = (FieldConstraintDescr) andConstr1.getDescrs().get( 0 );
    //        assertEquals( "age",
    //                      fcd.getFieldName() );
    //        RestrictionConnectiveDescr or = (RestrictionConnectiveDescr) fcd.getRestriction().getRestrictions().get( 0 );
    //        RestrictionConnectiveDescr and1 = (RestrictionConnectiveDescr) or.getRestrictions().get( 0 );
    //        RestrictionConnectiveDescr and2 = (RestrictionConnectiveDescr) or.getRestrictions().get( 1 );
    //
    //        assertEquals( ">",
    //                      ((LiteralRestrictionDescr) and1.getRestrictions().get( 0 )).getEvaluator() );
    //        assertEquals( "60",
    //                      ((LiteralRestrictionDescr) and1.getRestrictions().get( 0 )).getText() );
    //
    //        assertEquals( "<",
    //                      ((LiteralRestrictionDescr) and1.getRestrictions().get( 1 )).getEvaluator() );
    //        assertEquals( "70",
    //                      ((LiteralRestrictionDescr) and1.getRestrictions().get( 1 )).getText() );
    //
    //        assertEquals( ">",
    //                      ((LiteralRestrictionDescr) and2.getRestrictions().get( 0 )).getEvaluator() );
    //        assertEquals( "50",
    //                      ((LiteralRestrictionDescr) and2.getRestrictions().get( 0 )).getText() );
    //
    //        assertEquals( "<",
    //                      ((LiteralRestrictionDescr) and2.getRestrictions().get( 1 )).getEvaluator() );
    //        assertEquals( "55",
    //                      ((LiteralRestrictionDescr) and2.getRestrictions().get( 1 )).getText() );
    //
    //        fcd = (FieldConstraintDescr) andConstr1.getDescrs().get( 1 );
    //        assertEquals( "hair",
    //                      fcd.getFieldName() );
    //
    //        assertEquals( "==",
    //                      ((LiteralRestrictionDescr) fcd.getRestrictions().get( 0 )).getEvaluator() );
    //        assertEquals( "black",
    //                      ((LiteralRestrictionDescr) fcd.getRestrictions().get( 0 )).getText() );
    //
    //        AndDescr andConstr2 = (AndDescr) orConstr.getDescrs().get( 1 );
    //        assertEquals( 2,
    //                      andConstr2.getDescrs().size() );
    //        fcd = (FieldConstraintDescr) andConstr2.getDescrs().get( 0 );
    //        assertEquals( "age",
    //                      fcd.getFieldName() );
    //
    //        assertEquals( "==",
    //                      ((LiteralRestrictionDescr) fcd.getRestrictions().get( 0 )).getEvaluator() );
    //        assertEquals( "40",
    //                      ((LiteralRestrictionDescr) fcd.getRestrictions().get( 0 )).getText() );
    //
    //        fcd = (FieldConstraintDescr) andConstr2.getDescrs().get( 1 );
    //        assertEquals( "hair",
    //                      fcd.getFieldName() );
    //
    //        assertEquals( "==",
    //                      ((LiteralRestrictionDescr) fcd.getRestrictions().get( 0 )).getEvaluator() );
    //        assertEquals( "pink",
    //                      ((LiteralRestrictionDescr) fcd.getRestrictions().get( 0 )).getText() );
    //
    //        AndDescr andConstr3 = (AndDescr) orConstr.getDescrs().get( 2 );
    //        assertEquals( 2,
    //                      andConstr3.getDescrs().size() );
    //        fcd = (FieldConstraintDescr) andConstr3.getDescrs().get( 0 );
    //        assertEquals( "age",
    //                      fcd.getFieldName() );
    //
    //        assertEquals( "==",
    //                      ((LiteralRestrictionDescr) fcd.getRestrictions().get( 0 )).getEvaluator() );
    //        assertEquals( "12",
    //                      ((LiteralRestrictionDescr) fcd.getRestrictions().get( 0 )).getText() );
    //
    //        OrDescr orConstr2 = (OrDescr) andConstr3.getDescrs().get( 1 );
    //
    //        fcd = (FieldConstraintDescr) orConstr2.getDescrs().get( 0 );
    //        assertEquals( "hair",
    //                      fcd.getFieldName() );
    //
    //        assertEquals( "==",
    //                      ((LiteralRestrictionDescr) fcd.getRestrictions().get( 0 )).getEvaluator() );
    //        assertEquals( "yellow",
    //                      ((LiteralRestrictionDescr) fcd.getRestrictions().get( 0 )).getText() );
    //
    //        fcd = (FieldConstraintDescr) orConstr2.getDescrs().get( 1 );
    //        assertEquals( "hair",
    //                      fcd.getFieldName() );
    //
    //        assertEquals( "==",
    //                      ((LiteralRestrictionDescr) fcd.getRestrictions().get( 0 )).getEvaluator() );
    //        assertEquals( "blue",
    //                      ((LiteralRestrictionDescr) fcd.getRestrictions().get( 0 )).getText() );
    //
    //    }
    //
    //    public void testConstraintConnectivesMatches() throws Exception {
    //        final String text = "Person( name matches \"mark\" || matches \"bob\" )";
    //        PatternDescr pattern = (PatternDescr) parse( "lhs_pattern",
    //                                                     "lhs_pattern",
    //                                                     text );
    //
    //        assertEquals( 1,
    //                      pattern.getDescrs().size() );
    //        FieldConstraintDescr fcd = (FieldConstraintDescr) pattern.getDescrs().get( 0 );
    //        assertEquals( "name",
    //                      fcd.getFieldName() );
    //
    //        RestrictionConnectiveDescr or = (RestrictionConnectiveDescr) fcd.getRestrictions().get( 0 );
    //
    //        assertEquals( 2,
    //                      or.getRestrictions().size() );
    //
    //        assertEquals( "matches",
    //                      ((LiteralRestrictionDescr) or.getRestrictions().get( 0 )).getEvaluator() );
    //        assertEquals( "mark",
    //                      ((LiteralRestrictionDescr) or.getRestrictions().get( 0 )).getText() );
    //
    //        assertEquals( "matches",
    //                      ((LiteralRestrictionDescr) or.getRestrictions().get( 1 )).getEvaluator() );
    //        assertEquals( "bob",
    //                      ((LiteralRestrictionDescr) or.getRestrictions().get( 1 )).getText() );
    //
    //    }
    //
    //    public void testNotContains() throws Exception {
    //        final String text = "City( $city : city )\nCountry( cities not contains $city )\n";
    //        AndDescr descrs = (AndDescr) parse( "normal_lhs_block",
    //                                            "lhs_block",
    //                                            text );
    //
    //        assertEquals( 2,
    //                      descrs.getDescrs().size() );
    //        PatternDescr pat = (PatternDescr) descrs.getDescrs().get( 1 );
    //        FieldConstraintDescr fieldConstr = (FieldConstraintDescr) pat.getConstraint().getDescrs().get( 0 );
    //        VariableRestrictionDescr restr = (VariableRestrictionDescr) fieldConstr.getRestrictions().get( 0 );
    //
    //        assertEquals( "contains",
    //                      restr.getEvaluator() );
    //        assertTrue( restr.isNegated() );
    //        assertEquals( "$city",
    //                      restr.getIdentifier() );
    //    }
    //
    //    public void testNotMatches() throws Exception {
    //        final String text = "Message( text not matches '[abc]*' )\n";
    //        AndDescr descrs = (AndDescr) parse( "normal_lhs_block",
    //                                            "lhs_block",
    //                                            text );
    //
    //        assertEquals( 1,
    //                      descrs.getDescrs().size() );
    //        PatternDescr pat = (PatternDescr) descrs.getDescrs().get( 0 );
    //        FieldConstraintDescr fieldConstr = (FieldConstraintDescr) pat.getConstraint().getDescrs().get( 0 );
    //        LiteralRestrictionDescr restr = (LiteralRestrictionDescr) fieldConstr.getRestrictions().get( 0 );
    //
    //        assertEquals( "matches",
    //                      restr.getEvaluator() );
    //        assertTrue( restr.isNegated() );
    //        assertEquals( "[abc]*",
    //                      restr.getText() );
    //    }
    //
    //    public void testRestrictions() throws Exception {
    //        final String text = "Foo( bar > 1 || == 1 )\n";
    //
    //        AndDescr descrs = (AndDescr) parse( "normal_lhs_block",
    //                                            "lhs_block",
    //                                            text );
    //
    //        assertEquals( 1,
    //                      descrs.getDescrs().size() );
    //        PatternDescr pat = (PatternDescr) descrs.getDescrs().get( 0 );
    //        FieldConstraintDescr fieldConstr = (FieldConstraintDescr) pat.getConstraint().getDescrs().get( 0 );
    //        RestrictionConnectiveDescr or = (RestrictionConnectiveDescr) fieldConstr.getRestrictions().get( 0 );
    //        LiteralRestrictionDescr gt1 = (LiteralRestrictionDescr) or.getRestrictions().get( 0 );
    //        LiteralRestrictionDescr eq1 = (LiteralRestrictionDescr) or.getRestrictions().get( 1 );
    //
    //        assertEquals( ">",
    //                      gt1.getEvaluator() );
    //        assertEquals( false,
    //                      gt1.isNegated() );
    //        assertEquals( 1,
    //                      ((Number) eq1.getValue()).intValue() );
    //        assertEquals( "==",
    //                      eq1.getEvaluator() );
    //        assertEquals( false,
    //                      eq1.isNegated() );
    //        assertEquals( 1,
    //                      ((Number) eq1.getValue()).intValue() );
    //
    //    }
    //
    //    public void testSemicolon() throws Exception {
    //        parseResource( "compilation_unit",
    //                       "compilation_unit",
    //                       "semicolon.drl" );
    //
    //        final PackageDescr pkg = this.walker.getPackageDescr();
    //        assertEquals( "org.drools",
    //                      pkg.getName() );
    //        assertEquals( 1,
    //                      pkg.getGlobals().size() );
    //        assertEquals( 3,
    //                      pkg.getRules().size() );
    //
    //        final RuleDescr rule1 = (RuleDescr) pkg.getRules().get( 0 );
    //        assertEquals( 2,
    //                      rule1.getLhs().getDescrs().size() );
    //
    //        final RuleDescr query1 = (RuleDescr) pkg.getRules().get( 1 );
    //        assertEquals( 3,
    //                      query1.getLhs().getDescrs().size() );
    //
    //        final RuleDescr rule2 = (RuleDescr) pkg.getRules().get( 2 );
    //        assertEquals( 2,
    //                      rule2.getLhs().getDescrs().size() );
    //    }
    //
    //    public void testEval() throws Exception {
    //        parseResource( "compilation_unit",
    //                       "compilation_unit",
    //                       "eval_parsing.drl" );
    //
    //        final PackageDescr pkg = this.walker.getPackageDescr();
    //        assertEquals( "org.drools",
    //                      pkg.getName() );
    //        assertEquals( 1,
    //                      pkg.getRules().size() );
    //
    //        final RuleDescr rule1 = (RuleDescr) pkg.getRules().get( 0 );
    //        assertEquals( 1,
    //                      rule1.getLhs().getDescrs().size() );
    //    }
    //
    //    public void testAccumulateReverse() throws Exception {
    //        parseResource( "compilation_unit",
    //                       "compilation_unit",
    //                       "accumulateReverse.drl" );
    //
    //        final PackageDescr pack = walker.getPackageDescr();
    //        assertEquals( 1,
    //                      pack.getRules().size() );
    //        final RuleDescr rule = (RuleDescr) pack.getRules().get( 0 );
    //        assertEquals( 1,
    //                      rule.getLhs().getDescrs().size() );
    //
    //        final PatternDescr out = (PatternDescr) rule.getLhs().getDescrs().get( 0 );
    //        final AccumulateDescr accum = (AccumulateDescr) out.getSource();
    //        assertEqualsIgnoreWhitespace( "int x = 0 ;",
    //                                      accum.getInitCode() );
    //        assertEqualsIgnoreWhitespace( "x++;",
    //                                      accum.getActionCode() );
    //        assertEqualsIgnoreWhitespace( "x--;",
    //                                      accum.getReverseCode() );
    //        assertEqualsIgnoreWhitespace( "new Integer(x)",
    //                                      accum.getResultCode() );
    //        assertFalse( accum.isExternalFunction() );
    //
    //        final PatternDescr pattern = (PatternDescr) accum.getInputPattern();
    //        assertEquals( "Person",
    //                      pattern.getObjectType() );
    //    }
    //
    //    public void testAccumulateExternalFunction() throws Exception {
    //        parseResource( "compilation_unit",
    //                       "compilation_unit",
    //                       "accumulateExternalFunction.drl" );
    //
    //        final PackageDescr pack = walker.getPackageDescr();
    //        assertEquals( 1,
    //                      pack.getRules().size() );
    //        final RuleDescr rule = (RuleDescr) pack.getRules().get( 0 );
    //        assertEquals( 1,
    //                      rule.getLhs().getDescrs().size() );
    //
    //        final PatternDescr out = (PatternDescr) rule.getLhs().getDescrs().get( 0 );
    //        final AccumulateDescr accum = (AccumulateDescr) out.getSource();
    //        assertEqualsIgnoreWhitespace( "$age",
    //                                      accum.getExpression() );
    //        assertEqualsIgnoreWhitespace( "average",
    //                                      accum.getFunctionIdentifier() );
    //        assertTrue( accum.isExternalFunction() );
    //
    //        final PatternDescr pattern = (PatternDescr) accum.getInputPattern();
    //        assertEquals( "Person",
    //                      pattern.getObjectType() );
    //    }
    //
    //    public void testCollectWithNestedFrom() throws Exception {
    //        parseResource( "compilation_unit",
    //                       "compilation_unit",
    //                       "collect_with_nested_from.drl" );
    //
    //        final PackageDescr pack = walker.getPackageDescr();
    //        assertEquals( 1,
    //                      pack.getRules().size() );
    //        final RuleDescr rule = (RuleDescr) pack.getRules().get( 0 );
    //        assertEquals( 1,
    //                      rule.getLhs().getDescrs().size() );
    //
    //        final PatternDescr out = (PatternDescr) rule.getLhs().getDescrs().get( 0 );
    //        final CollectDescr collect = (CollectDescr) out.getSource();
    //
    //        PatternDescr person = (PatternDescr) collect.getInputPattern();
    //        assertEquals( "Person",
    //                      person.getObjectType() );
    //
    //        final CollectDescr collect2 = (CollectDescr) person.getSource();
    //
    //        final PatternDescr people = collect2.getInputPattern();
    //        assertEquals( "People",
    //                      people.getObjectType() );
    //    }
    //
    //    public void testAccumulateWithNestedFrom() throws Exception {
    //        parseResource( "compilation_unit",
    //                       "compilation_unit",
    //                       "accumulate_with_nested_from.drl" );
    //
    //        final PackageDescr pack = walker.getPackageDescr();
    //        assertEquals( 1,
    //                      pack.getRules().size() );
    //        final RuleDescr rule = (RuleDescr) pack.getRules().get( 0 );
    //        assertEquals( 1,
    //                      rule.getLhs().getDescrs().size() );
    //
    //        final PatternDescr out = (PatternDescr) rule.getLhs().getDescrs().get( 0 );
    //        final AccumulateDescr accumulate = (AccumulateDescr) out.getSource();
    //
    //        PatternDescr person = (PatternDescr) accumulate.getInputPattern();
    //        assertEquals( "Person",
    //                      person.getObjectType() );
    //
    //        final CollectDescr collect2 = (CollectDescr) person.getSource();
    //
    //        final PatternDescr people = collect2.getInputPattern();
    //        assertEquals( "People",
    //                      people.getObjectType() );
    //    }
    //
    //    public void testAccessorPaths() throws Exception {
    //        final String text = "org   .   drools/*comment*/\t  .Message( text not matches $c#comment\n. property )\n";
    //
    //        PatternDescr pattern = (PatternDescr) parse( "lhs_pattern",
    //                                                     "lhs_pattern",
    //                                                     text );
    //
    //        assertEquals( "org.drools.Message",
    //                      pattern.getObjectType() );
    //
    //        FieldConstraintDescr fieldConstr = (FieldConstraintDescr) pattern.getConstraint().getDescrs().get( 0 );
    //        QualifiedIdentifierRestrictionDescr restr = (QualifiedIdentifierRestrictionDescr) fieldConstr.getRestrictions().get( 0 );
    //
    //        assertEquals( "matches",
    //                      restr.getEvaluator() );
    //        assertTrue( restr.isNegated() );
    //        assertEquals( "$c.property",
    //                      restr.getText() );
    //    }
    //
    //    public void testOrCE() throws Exception {
    //        parseResource( "compilation_unit",
    //                       "compilation_unit",
    //                       "or_ce.drl" );
    //
    //        final PackageDescr pack = walker.getPackageDescr();
    //        assertEquals( 1,
    //                      pack.getRules().size() );
    //        final RuleDescr rule = (RuleDescr) pack.getRules().get( 0 );
    //        assertEquals( 2,
    //                      rule.getLhs().getDescrs().size() );
    //
    //        final PatternDescr person = (PatternDescr) rule.getLhs().getDescrs().get( 0 );
    //        assertEquals( "Person",
    //                      person.getObjectType() );
    //        assertEquals( "$p",
    //                      person.getIdentifier() );
    //
    //        final OrDescr or = (OrDescr) rule.getLhs().getDescrs().get( 1 );
    //        assertEquals( 2,
    //                      or.getDescrs().size() );
    //
    //        final PatternDescr cheese1 = (PatternDescr) or.getDescrs().get( 0 );
    //        assertEquals( "Cheese",
    //                      cheese1.getObjectType() );
    //        assertEquals( "$c",
    //                      cheese1.getIdentifier() );
    //        final PatternDescr cheese2 = (PatternDescr) or.getDescrs().get( 1 );
    //        assertEquals( "Cheese",
    //                      cheese2.getObjectType() );
    //        assertNull( cheese2.getIdentifier() );
    //    }
    //
    //    public void testRuleSingleLine() throws Exception {
    //        final String text = "rule \"another test\" salience 10 when eval( true ) then System.out.println(1); end";
    //        RuleDescr rule = (RuleDescr) parse( "rule",
    //                                            "rule",
    //                                            text );
    //
    //        assertEquals( "another test",
    //                      rule.getName() );
    //        assertEquals( "System.out.println(1); ",
    //                      rule.getConsequence() );
    //    }
    //
    //    public void testRuleTwoLines() throws Exception {
    //        final String text = "rule \"another test\" salience 10 when eval( true ) then System.out.println(1);\n end";
    //        RuleDescr rule = (RuleDescr) parse( "rule",
    //                                            "rule",
    //                                            text );
    //
    //        assertEquals( "another test",
    //                      rule.getName() );
    //        assertEquals( "System.out.println(1);\n ",
    //                      rule.getConsequence() );
    //    }
    //
    //    public void testRuleParseLhs3() throws Exception {
    //        final String text = "(or\nnot Person()\n(and Cheese()\nMeat()\nWine()))";
    //        AndDescr pattern = (AndDescr) parse( "normal_lhs_block",
    //                                             "lhs_block",
    //                                             text );
    //
    //        assertEquals( 1,
    //                      pattern.getDescrs().size() );
    //        OrDescr or = (OrDescr) pattern.getDescrs().get( 0 );
    //        assertEquals( 2,
    //                      or.getDescrs().size() );
    //        NotDescr not = (NotDescr) or.getDescrs().get( 0 );
    //        AndDescr and = (AndDescr) or.getDescrs().get( 1 );
    //        assertEquals( 1,
    //                      not.getDescrs().size() );
    //        PatternDescr person = (PatternDescr) not.getDescrs().get( 0 );
    //        assertEquals( "Person",
    //                      person.getObjectType() );
    //        assertEquals( 3,
    //                      and.getDescrs().size() );
    //        PatternDescr cheese = (PatternDescr) and.getDescrs().get( 0 );
    //        assertEquals( "Cheese",
    //                      cheese.getObjectType() );
    //        PatternDescr meat = (PatternDescr) and.getDescrs().get( 1 );
    //        assertEquals( "Meat",
    //                      meat.getObjectType() );
    //        PatternDescr wine = (PatternDescr) and.getDescrs().get( 2 );
    //        assertEquals( "Wine",
    //                      wine.getObjectType() );
    //
    //    }
    //
    //    public void testAccumulateMultiPattern() throws Exception {
    //        parseResource( "compilation_unit",
    //                       "compilation_unit",
    //                       "accumulate_multi_pattern.drl" );
    //
    //        final PackageDescr pack = walker.getPackageDescr();
    //        assertEquals( 1,
    //                      pack.getRules().size() );
    //        final RuleDescr rule = (RuleDescr) pack.getRules().get( 0 );
    //        assertEquals( 1,
    //                      rule.getLhs().getDescrs().size() );
    //
    //        final PatternDescr outPattern = (PatternDescr) rule.getLhs().getDescrs().get( 0 );
    //        final AccumulateDescr accum = (AccumulateDescr) outPattern.getSource();
    //        assertEqualsIgnoreWhitespace( "$counter",
    //                                      outPattern.getIdentifier() );
    //        assertEqualsIgnoreWhitespace( "int x = 0 ;",
    //                                      accum.getInitCode() );
    //        assertEqualsIgnoreWhitespace( "x++;",
    //                                      accum.getActionCode() );
    //        assertEqualsIgnoreWhitespace( "new Integer(x)",
    //                                      accum.getResultCode() );
    //
    //        final AndDescr and = (AndDescr) accum.getInput();
    //        assertEquals( 2,
    //                      and.getDescrs().size() );
    //        final PatternDescr person = (PatternDescr) and.getDescrs().get( 0 );
    //        final PatternDescr cheese = (PatternDescr) and.getDescrs().get( 1 );
    //        assertEquals( "Person",
    //                      person.getObjectType() );
    //        assertEquals( "Cheese",
    //                      cheese.getObjectType() );
    //    }
    //
    //    public void testPluggableOperators() throws Exception {
    //
    //        parseResource( "compilation_unit",
    //                       "compilation_unit",
    //                       "pluggable_operators.drl" );
    //
    //        final PackageDescr pack = walker.getPackageDescr();
    //        assertEquals( 1,
    //                      pack.getRules().size() );
    //        final RuleDescr rule = (RuleDescr) pack.getRules().get( 0 );
    //        assertEquals( 5,
    //                      rule.getLhs().getDescrs().size() );
    //
    //        final PatternDescr eventA = (PatternDescr) rule.getLhs().getDescrs().get( 0 );
    //        assertEquals( "$a",
    //                      eventA.getIdentifier() );
    //        assertEquals( "EventA",
    //                      eventA.getObjectType() );
    //
    //        final PatternDescr eventB = (PatternDescr) rule.getLhs().getDescrs().get( 1 );
    //        assertEquals( "$b",
    //                      eventB.getIdentifier() );
    //        assertEquals( "EventB",
    //                      eventB.getObjectType() );
    //        assertEquals( 1,
    //                      eventB.getConstraint().getDescrs().size() );
    //        final OrDescr or = (OrDescr) eventB.getConstraint().getDescrs().get( 0 );
    //        assertEquals( 2,
    //                      or.getDescrs().size() );
    //
    //        final FieldConstraintDescr fcdB = (FieldConstraintDescr) or.getDescrs().get( 0 );
    //        assertEquals( 1,
    //                      fcdB.getRestrictions().size() );
    //        assertTrue( fcdB.getRestrictions().get( 0 ) instanceof VariableRestrictionDescr );
    //        final VariableRestrictionDescr rb = (VariableRestrictionDescr) fcdB.getRestrictions().get( 0 );
    //        assertEquals( "after",
    //                      rb.getEvaluator() );
    //        assertEquals( "$a",
    //                      rb.getText() );
    //        assertEquals( "1,10",
    //                      rb.getParameterText() );
    //        assertFalse( rb.isNegated() );
    //
    //        final FieldConstraintDescr fcdB2 = (FieldConstraintDescr) or.getDescrs().get( 1 );
    //        assertEquals( 1,
    //                      fcdB2.getRestrictions().size() );
    //        assertTrue( fcdB2.getRestrictions().get( 0 ) instanceof VariableRestrictionDescr );
    //        final VariableRestrictionDescr rb2 = (VariableRestrictionDescr) fcdB2.getRestrictions().get( 0 );
    //        assertEquals( "after",
    //                      rb2.getEvaluator() );
    //        assertEquals( "$a",
    //                      rb2.getText() );
    //        assertEquals( "15,20",
    //                      rb2.getParameterText() );
    //        assertTrue( rb2.isNegated() );
    //
    //        final PatternDescr eventC = (PatternDescr) rule.getLhs().getDescrs().get( 2 );
    //        assertEquals( "$c",
    //                      eventC.getIdentifier() );
    //        assertEquals( "EventC",
    //                      eventC.getObjectType() );
    //        assertEquals( 1,
    //                      eventC.getConstraint().getDescrs().size() );
    //        final FieldConstraintDescr fcdC = (FieldConstraintDescr) eventC.getConstraint().getDescrs().get( 0 );
    //        assertEquals( 1,
    //                      fcdC.getRestrictions().size() );
    //        assertTrue( fcdC.getRestrictions().get( 0 ) instanceof VariableRestrictionDescr );
    //        final VariableRestrictionDescr rc = (VariableRestrictionDescr) fcdC.getRestrictions().get( 0 );
    //        assertEquals( "finishes",
    //                      rc.getEvaluator() );
    //        assertEquals( "$b",
    //                      rc.getText() );
    //        assertNull( rc.getParameterText() );
    //        assertFalse( rc.isNegated() );
    //
    //        final PatternDescr eventD = (PatternDescr) rule.getLhs().getDescrs().get( 3 );
    //        assertEquals( "$d",
    //                      eventD.getIdentifier() );
    //        assertEquals( "EventD",
    //                      eventD.getObjectType() );
    //        assertEquals( 1,
    //                      eventD.getConstraint().getDescrs().size() );
    //        final FieldConstraintDescr fcdD = (FieldConstraintDescr) eventD.getConstraint().getDescrs().get( 0 );
    //        assertEquals( 1,
    //                      fcdD.getRestrictions().size() );
    //        assertTrue( fcdD.getRestrictions().get( 0 ) instanceof VariableRestrictionDescr );
    //        final VariableRestrictionDescr rd = (VariableRestrictionDescr) fcdD.getRestrictions().get( 0 );
    //        assertEquals( "starts",
    //                      rd.getEvaluator() );
    //        assertEquals( "$a",
    //                      rd.getText() );
    //        assertNull( rd.getParameterText() );
    //        assertTrue( rd.isNegated() );
    //
    //        final PatternDescr eventE = (PatternDescr) rule.getLhs().getDescrs().get( 4 );
    //        assertEquals( "$e",
    //                      eventE.getIdentifier() );
    //        assertEquals( "EventE",
    //                      eventE.getObjectType() );
    //        assertEquals( 2,
    //                      eventE.getConstraint().getDescrs().size() );
    //
    //        final AndDescr and = (AndDescr) eventE.getConstraint();
    //
    //        FieldConstraintDescr fcdE = (FieldConstraintDescr) and.getDescrs().get( 0 );
    //        assertEquals( 1,
    //                      fcdE.getRestrictions().size() );
    //        final RestrictionConnectiveDescr orrestr = (RestrictionConnectiveDescr) fcdE.getRestrictions().get( 0 );
    //        assertEquals( RestrictionConnectiveDescr.OR,
    //                      orrestr.getConnective() );
    //        assertEquals( 2,
    //                      orrestr.getRestrictions().size() );
    //        assertTrue( orrestr.getRestrictions().get( 0 ) instanceof VariableRestrictionDescr );
    //        VariableRestrictionDescr re = (VariableRestrictionDescr) orrestr.getRestrictions().get( 0 );
    //        assertEquals( "before",
    //                      re.getEvaluator() );
    //        assertEquals( "$b",
    //                      re.getText() );
    //        assertEquals( "1, 10",
    //                      re.getParameterText() );
    //        assertTrue( re.isNegated() );
    //        re = (VariableRestrictionDescr) orrestr.getRestrictions().get( 1 );
    //        assertEquals( "after",
    //                      re.getEvaluator() );
    //        assertEquals( "$c",
    //                      re.getText() );
    //        assertEquals( "1, 10",
    //                      re.getParameterText() );
    //        assertFalse( re.isNegated() );
    //
    //        fcdE = (FieldConstraintDescr) and.getDescrs().get( 1 );
    //        assertEquals( 1,
    //                      fcdE.getRestrictions().size() );
    //        assertTrue( fcdE.getRestrictions().get( 0 ) instanceof VariableRestrictionDescr );
    //        re = (VariableRestrictionDescr) fcdE.getRestrictions().get( 0 );
    //        assertEquals( "after",
    //                      re.getEvaluator() );
    //        assertEquals( "$d",
    //                      re.getText() );
    //        assertEquals( "1, 5",
    //                      re.getParameterText() );
    //        assertFalse( re.isNegated() );
    //    }
    //

    public void testTypeDeclaration() throws Exception {
        final PackageDescr pkg = (PackageDescr) parseResource( "compilationUnit",
                                                               "declare_type.drl" );
        assertFalse( parser.getErrors().toString(),
                     parser.hasErrors() );

        final List<TypeDeclarationDescr> declarations = pkg.getTypeDeclarations();

        assertEquals( 2,
                      declarations.size() );

        TypeDeclarationDescr descr = declarations.get( 0 );
        assertEquals( "CallEvent",
                      descr.getTypeName() );
        assertEquals( 4,
                      descr.getAnnotationNames().size() );
        assertEquals( "event",
                          descr.getAnnotation( "role" ).getValue() );
        assertEquals( "org.drools.events.Call",
                          descr.getAnnotation( "class" ).getValue() );
        assertEquals( "duration",
                          descr.getAnnotation( "duration" ).getValue() );
        assertEquals( "timestamp",
                          descr.getAnnotation( "timestamp" ).getValue() );
        assertNull( descr.getAnnotation( "FOO" ) );

        descr = declarations.get( 1 );
        assertEquals( "some.pkg.Type",
                      descr.getTypeName() );
        assertEquals( 5,
                      descr.getAnnotationNames().size() );
        assertNotNull( descr.getAnnotation( "name1" ) );
        assertEquals( "value",
                          descr.getAnnotation( "name2" ).getValue() );
        assertEquals( "10",
                          descr.getAnnotation( "name3" ).getValue( "k1" ) );
        assertEquals( "a",
                      descr.getAnnotation( "name4" ).getValue( "k1" ) );
        assertEquals( "Math.max( 10 + 25, 22 ) % 2 + someVariable",
                      descr.getAnnotation( "name4" ).getValue( "formula" ) );
        assertEquals( "{ a, b, c }",
                      descr.getAnnotation( "name4" ).getValue( "array" ) );
        assertEquals( "backward compatible value",
                      descr.getAnnotation( "name5" ).getValue() );
    }

    public void testRuleMetadata() throws Exception {
        final PackageDescr pkg = (PackageDescr) parseResource( "compilationUnit",
                                                               "Rule_with_Metadata.drl" );
        assertFalse( parser.getErrors().toString(),
                     parser.hasErrors() );

        // @fooAttribute(barValue)
        // @fooAtt2(barVal2)
        RuleDescr rule = pkg.getRules().get( 0 );
        assertTrue( rule.getAnnotationNames().contains( "fooMeta1" ) );
        assertEquals( "barVal1",
                      rule.getAnnotation( "fooMeta1" ).getValue() );
        assertTrue( rule.getAnnotationNames().contains( "fooMeta2" ) );
        assertEquals( "barVal2",
                      rule.getAnnotation( "fooMeta2" ).getValue() );
        assertEqualsIgnoreWhitespace( "System.out.println(\"Consequence\");",
                                      (String) rule.getConsequence() );
    }

    public void testRuleExtends() throws Exception {
        final PackageDescr pkg = (PackageDescr) parseResource( "compilationUnit",
                                                               "Rule_with_Extends.drl" );

        assertFalse( parser.getErrors().toString(),
                     parser.hasErrors() );

        RuleDescr rule = pkg.getRules().get( 0 );
        assertTrue( rule.getParentName() != null );
        assertEquals( "rule1",
                      rule.getParentName() );

        AndDescr lhs = rule.getLhs();
        assertNotNull( lhs );
        assertEquals( 1,
                      lhs.getDescrs().size() );

        PatternDescr pattern = (PatternDescr) lhs.getDescrs().get( 0 );
        assertEquals( "foo",
                      pattern.getObjectType() );
        assertEquals( "$foo",
                      pattern.getIdentifier() );

    }

    public void testTypeDeclarationWithFields() throws Exception {
        final PackageDescr pkg = (PackageDescr) parseResource( "compilationUnit",
                                                               "declare_type_with_fields.drl" );
        assertFalse( parser.getErrors().toString(),
                     parser.hasErrors() );

        List<TypeDeclarationDescr> td = pkg.getTypeDeclarations();
        assertEquals( 3,
                      td.size() );

        TypeDeclarationDescr d = td.get( 0 );
        assertEquals( "SomeFact",
                          d.getTypeName() );
        assertEquals( 2,
                          d.getFields().size() );
        assertTrue( d.getFields().containsKey( "name" ) );
        assertTrue( d.getFields().containsKey( "age" ) );

        TypeFieldDescr f = d.getFields().get( "name" );
        assertEquals( "String",
                          f.getPattern().getObjectType() );

        f = d.getFields().get( "age" );
        assertEquals( "Integer",
                          f.getPattern().getObjectType() );

        d = td.get( 1 );
        assertEquals( "AnotherFact",
                          d.getTypeName() );

        TypeDeclarationDescr type = td.get( 2 );
        assertEquals( "Person",
                       type.getTypeName() );

        assertEquals( "fact",
                      type.getAnnotation( "role" ).getValue() );
        assertEquals( "Models a person",
                      type.getAnnotation( "doc" ).getValue( "descr" ) );
        assertEquals( "Bob",
                      type.getAnnotation( "doc" ).getValue( "author" ) );
        assertEquals( "Calendar.getInstance().getDate()",
                      type.getAnnotation( "doc" ).getValue( "date" ) );

        assertEquals( 2,
                      type.getFields().size() );
        TypeFieldDescr field = type.getFields().get( "name" );
        assertEquals( "name",
                      field.getFieldName() );
        assertEquals( "String",
                      field.getPattern().getObjectType() );
        assertEquals( "\"John Doe\"",
                      field.getInitExpr() );
        assertEquals( "50",
                      field.getAnnotation( "length" ).getValue( "max" ) );
        assertNotNull( field.getAnnotation( "key" ) );

        field = type.getFields().get( "age" );
        assertEquals( "age",
                      field.getFieldName() );
        assertEquals( "int",
                      field.getPattern().getObjectType() );
        assertEquals( "-1",
                      field.getInitExpr() );
        assertEquals( "0",
                      field.getAnnotation( "ranged" ).getValue( "min" ) );
        assertEquals( "150",
                      field.getAnnotation( "ranged" ).getValue( "max" ) );
        assertEquals( "-1",
                      field.getAnnotation( "ranged" ).getValue( "unknown" ) );

    }

    //
    //    public void testEntryPoint() throws Exception {
    //        final String text = "StockTick( symbol==\"ACME\") from entry-point StreamA";
    //
    //        PatternDescr pattern = (PatternDescr) parse( "pattern_source",
    //                                                     "lhs",
    //                                                     text );
    //
    //        assertEquals( 1,
    //                      pattern.getDescrs().size() );
    //        FieldConstraintDescr fcd = (FieldConstraintDescr) pattern.getDescrs().get( 0 );
    //        assertEquals( "symbol",
    //                      fcd.getFieldName() );
    //
    //        assertNotNull( pattern.getSource() );
    //        EntryPointDescr entry = (EntryPointDescr) pattern.getSource();
    //        assertEquals( "StreamA",
    //                      entry.getEntryId() );
    //    }
    //
    //    public void testEntryPoint2() throws Exception {
    //        final String text = "StockTick( symbol==\"ACME\") from entry-point \"StreamA\"";
    //
    //        PatternDescr pattern = (PatternDescr) parse( "pattern_source",
    //                                                     "lhs",
    //                                                     text );
    //
    //        assertEquals( 1,
    //                      pattern.getDescrs().size() );
    //        FieldConstraintDescr fcd = (FieldConstraintDescr) pattern.getDescrs().get( 0 );
    //        assertEquals( "symbol",
    //                      fcd.getFieldName() );
    //
    //        assertNotNull( pattern.getSource() );
    //        EntryPointDescr entry = (EntryPointDescr) pattern.getSource();
    //        assertEquals( "StreamA",
    //                      entry.getEntryId() );
    //    }
    //
    //    public void testSlidingWindow() throws Exception {
    //        final String text = "StockTick( symbol==\"ACME\") over window:length(10)";
    //
    //        PatternDescr pattern = (PatternDescr) parse( "pattern_source",
    //                                                     "lhs",
    //                                                     text );
    //
    //        assertEquals( 1,
    //                      pattern.getDescrs().size() );
    //        FieldConstraintDescr fcd = (FieldConstraintDescr) pattern.getDescrs().get( 0 );
    //        assertEquals( "symbol",
    //                      fcd.getFieldName() );
    //
    //        List<BehaviorDescr> behaviors = pattern.getBehaviors();
    //        assertNotNull( behaviors );
    //        assertEquals( 1,
    //                      behaviors.size() );
    //        SlidingWindowDescr descr = (SlidingWindowDescr) behaviors.get( 0 );
    //        assertEquals( "length",
    //                      descr.getText() );
    //        assertEquals( "length",
    //                      descr.getType() );
    //        assertEquals( "10",
    //                      descr.getParameters() );
    //    }
    //
    //    public void testNesting() throws Exception {
    //        parseResource( "compilation_unit",
    //                       "compilation_unit",
    //                       "not_pluggable_operator.drl" );
    //
    //        assertNotNull( walker );
    //    }
    //
    //    public void testNoEOLOnCommentInTheLastLine() throws Exception {
    //        final String fileName = "no_eol_on_comment.drl";
    //
    //        // forcing ANTLR to use an input stream as a source
    //        final URL url = getClass().getResource( fileName );
    //        final Resource resource = ResourceFactory.newUrlResource( url );
    //
    //        final DrlParser parser = new DrlParser();
    //        final PackageDescr pkg = parser.parse( resource.getInputStream() );
    //
    //        assertFalse( parser.hasErrors() );
    //        assertNotNull( pkg );
    //    }
    //
    //    public void testRuleOldSyntax1() throws Exception {
    //        final String source = "rule \"Test\" when ( not $r :LiteralRestriction( operator == Operator.EQUAL ) ) then end";
    //
    //        parse( "compilation_unit",
    //               "compilation_unit",
    //               source );
    //
    //        RuleDescr rule = (RuleDescr) this.walker.getPackageDescr().getRules().get( 0 );
    //        assertFalse( parser.hasErrors() );
    //
    //        assertEquals( "Test",
    //                      rule.getName() );
    //        assertEquals( 1,
    //                      rule.getLhs().getDescrs().size() );
    //        assertEquals( 1,
    //                      ((NotDescr) rule.getLhs().getDescrs().get( 0 )).getDescrs().size() );
    //        NotDescr notDescr = (NotDescr) rule.getLhs().getDescrs().get( 0 );
    //        PatternDescr patternDescr = (PatternDescr) notDescr.getDescrs().get( 0 );
    //        assertEquals( "$r",
    //                      patternDescr.getIdentifier() );
    //        assertEquals( 1,
    //                      patternDescr.getDescrs().size() );
    //        FieldConstraintDescr fieldConstraintDescr = (FieldConstraintDescr) patternDescr.getDescrs().get( 0 );
    //        assertEquals( "operator",
    //                      fieldConstraintDescr.getFieldName() );
    //        assertEquals( 1,
    //                      fieldConstraintDescr.getRestriction().getRestrictions().size() );
    //        QualifiedIdentifierRestrictionDescr qualifiedIdentifierRestrictionDescr = (QualifiedIdentifierRestrictionDescr) fieldConstraintDescr.getRestriction().getRestrictions().get( 0 );
    //        assertEquals( "==",
    //                      qualifiedIdentifierRestrictionDescr.getEvaluator() );
    //        assertEquals( "Operator.EQUAL",
    //                      qualifiedIdentifierRestrictionDescr.getText() );
    //    }
    //
    //    public void testRuleOldSyntax2() throws Exception {
    //        final String source = "rule \"Test\" when ( $r :LiteralRestriction( operator == Operator.EQUAL ) ) then end";
    //
    //        parse( "compilation_unit",
    //               "compilation_unit",
    //               source );
    //
    //        RuleDescr rule = (RuleDescr) this.walker.getPackageDescr().getRules().get( 0 );
    //        assertFalse( parser.hasErrors() );
    //
    //        assertEquals( "Test",
    //                      rule.getName() );
    //        assertEquals( 1,
    //                      rule.getLhs().getDescrs().size() );
    //        PatternDescr patternDescr = (PatternDescr) rule.getLhs().getDescrs().get( 0 );
    //        assertEquals( "$r",
    //                      patternDescr.getIdentifier() );
    //        assertEquals( 1,
    //                      patternDescr.getDescrs().size() );
    //        FieldConstraintDescr fieldConstraintDescr = (FieldConstraintDescr) patternDescr.getDescrs().get( 0 );
    //        assertEquals( "operator",
    //                      fieldConstraintDescr.getFieldName() );
    //        assertEquals( 1,
    //                      fieldConstraintDescr.getRestriction().getRestrictions().size() );
    //        QualifiedIdentifierRestrictionDescr qualifiedIdentifierRestrictionDescr = (QualifiedIdentifierRestrictionDescr) fieldConstraintDescr.getRestriction().getRestrictions().get( 0 );
    //        assertEquals( "==",
    //                      qualifiedIdentifierRestrictionDescr.getEvaluator() );
    //        assertEquals( "Operator.EQUAL",
    //                      qualifiedIdentifierRestrictionDescr.getText() );
    //    }
    //
    //    public void testAndRestrictionConnective() throws Exception {
    //        final String source = "rule \"Test\" when ( $r :Person( $n : name == 'Bob' && $a : age == 20) ) then end";
    //
    //        parse( "compilation_unit",
    //               "compilation_unit",
    //               source );
    //
    //        RuleDescr rule = (RuleDescr) this.walker.getPackageDescr().getRules().get( 0 );
    //        assertFalse( parser.hasErrors() );
    //
    //        assertEquals( "Test",
    //                      rule.getName() );
    //        assertEquals( 1,
    //                      rule.getLhs().getDescrs().size() );
    //        PatternDescr patternDescr = (PatternDescr) rule.getLhs().getDescrs().get( 0 );
    //        assertEquals( "$r",
    //                      patternDescr.getIdentifier() );
    //        assertEquals( 4,
    //                      patternDescr.getDescrs().size() );
    //        FieldBindingDescr nameBind = (FieldBindingDescr) patternDescr.getDescrs().get( 0 );
    //        assertEquals( "$n",
    //                      nameBind.getIdentifier() );
    //        assertEquals( "name",
    //                      nameBind.getFieldName() );
    //        FieldConstraintDescr fieldConstraintDescr = (FieldConstraintDescr) patternDescr.getDescrs().get( 1 );
    //        assertEquals( "name",
    //                      fieldConstraintDescr.getFieldName() );
    //        assertEquals( 1,
    //                      fieldConstraintDescr.getRestriction().getRestrictions().size() );
    //        LiteralRestrictionDescr literalRestrictionDescr = (LiteralRestrictionDescr) fieldConstraintDescr.getRestriction().getRestrictions().get( 0 );
    //        assertEquals( "==",
    //                      literalRestrictionDescr.getEvaluator() );
    //        assertEquals( "Bob",
    //                      literalRestrictionDescr.getText() );
    //
    //        FieldBindingDescr ageBind = (FieldBindingDescr) patternDescr.getDescrs().get( 2 );
    //        assertEquals( "$a",
    //                      ageBind.getIdentifier() );
    //        assertEquals( "age",
    //                      ageBind.getFieldName() );
    //        fieldConstraintDescr = (FieldConstraintDescr) patternDescr.getDescrs().get( 3 );
    //        assertEquals( "age",
    //                      fieldConstraintDescr.getFieldName() );
    //        assertEquals( 1,
    //                      fieldConstraintDescr.getRestriction().getRestrictions().size() );
    //        literalRestrictionDescr = (LiteralRestrictionDescr) fieldConstraintDescr.getRestriction().getRestrictions().get( 0 );
    //        assertEquals( "==",
    //                      literalRestrictionDescr.getEvaluator() );
    //        assertEquals( "20",
    //                      literalRestrictionDescr.getText() );
    //    }
    //
    //    public void testTypeWithMetaData() throws Exception {
    //
    //        parseResource( "compilation_unit",
    //                       "compilation_unit",
    //                       "type_with_meta.drl" );
    //
    //        final PackageDescr pack = walker.getPackageDescr();
    //
    //        final List<TypeDeclarationDescr> declarations = pack.getTypeDeclarations();
    //
    //        assertEquals( 3,
    //                      declarations.size() );
    //    }

    private Object parse( final String parserRuleName,
                          final String text ) throws Exception {
        return execParser( parserRuleName,
                           new ANTLRStringStream( text ) );
    }

    private Object parseResource( final String parserRuleName,
                                  final String name ) throws Exception {
        final Reader reader = new InputStreamReader( getClass().getResourceAsStream( name ) );
        final StringBuilder text = new StringBuilder();

        final char[] buf = new char[1024];
        int len = 0;
        while ( (len = reader.read( buf )) >= 0 ) {
            text.append( buf,
                         0,
                         len );
        }
        return parse( parserRuleName,
                      text.toString() );
    }

    public Object execParser( String testRuleName,
                              CharStream charStream ) {
        try {
            DRLLexer lexer = new DRLLexer( charStream );
            CommonTokenStream tokens = new CommonTokenStream( lexer );
            parser = new DRLXParser( tokens );
            /** Use Reflection to get rule method from parser */
            Method ruleName = DRLXParser.class.getMethod( testRuleName );

            /** Invoke grammar rule, and get the return value */
            Object ruleReturn = ruleName.invoke( parser );

            if ( parser.hasErrors() ) {
                System.out.println( parser.getErrorMessages() );
            }

            return ruleReturn;
        } catch ( Exception e ) {
            e.printStackTrace();
            fail( e.getMessage() );
        }
        return null;
    }

    private void assertEqualsIgnoreWhitespace( final String expected,
                                               final String actual ) {
        final String cleanExpected = expected.replaceAll( "\\s+",
                                                          "" );
        final String cleanActual = actual.replaceAll( "\\s+",
                                                      "" );

        assertEquals( cleanExpected,
                      cleanActual );
    }

}
