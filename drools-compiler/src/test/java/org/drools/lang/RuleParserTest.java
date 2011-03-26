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

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import junit.framework.TestCase;

import org.antlr.runtime.ANTLRStringStream;
import org.antlr.runtime.CharStream;
import org.antlr.runtime.CommonTokenStream;
import org.drools.base.evaluators.EvaluatorRegistry;
import org.drools.compiler.DrlParser;
import org.drools.lang.descr.AccumulateDescr;
import org.drools.lang.descr.AndDescr;
import org.drools.lang.descr.AttributeDescr;
import org.drools.lang.descr.BaseDescr;
import org.drools.lang.descr.BehaviorDescr;
import org.drools.lang.descr.BindingDescr;
import org.drools.lang.descr.CollectDescr;
import org.drools.lang.descr.EntryPointDescr;
import org.drools.lang.descr.EvalDescr;
import org.drools.lang.descr.ExistsDescr;
import org.drools.lang.descr.ExprConstraintDescr;
import org.drools.lang.descr.ForallDescr;
import org.drools.lang.descr.FromDescr;
import org.drools.lang.descr.FunctionDescr;
import org.drools.lang.descr.FunctionImportDescr;
import org.drools.lang.descr.GlobalDescr;
import org.drools.lang.descr.ImportDescr;
import org.drools.lang.descr.MVELExprDescr;
import org.drools.lang.descr.NotDescr;
import org.drools.lang.descr.OrDescr;
import org.drools.lang.descr.PackageDescr;
import org.drools.lang.descr.PatternDescr;
import org.drools.lang.descr.QueryDescr;
import org.drools.lang.descr.RuleDescr;
import org.drools.lang.descr.TypeDeclarationDescr;
import org.drools.lang.descr.TypeFieldDescr;

public class RuleParserTest extends TestCase {

    private DRLParser parser;

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

    public void testFromComplexAcessor() throws Exception {
        String source = "rule \"Invalid customer id\" ruleflow-group \"validate\" lock-on-active true \n" +
                            " when \n" +
                            "     o: Order( ) \n" +
                            "     not( Customer( ) from customerService.getCustomer(o.getCustomerId()) ) \n" +
                            " then \n" +
                            "     System.err.println(\"Invalid customer id found!\"); \n" +
                            "     o.addError(\"Invalid customer id\"); \n" +
                            "end \n";
        PackageDescr pkg = (PackageDescr) parse( "compilationUnit",
                                                 source );

        assertFalse( parser.getErrorMessages().toString(),
                     parser.hasErrors() );

        RuleDescr rule = (RuleDescr) pkg.getRules().get( 0 );
        assertEquals( "Invalid customer id",
                      rule.getName() );

        assertEquals( 2,
                      rule.getLhs().getDescrs().size() );

        NotDescr not = (NotDescr) rule.getLhs().getDescrs().get( 1 );
        PatternDescr customer = (PatternDescr) not.getDescrs().get( 0 );

        assertEquals( "Customer",
                      customer.getObjectType() );
        assertEquals( "customerService.getCustomer(o.getCustomerId())",
                      ((FromDescr) customer.getSource()).getDataSource().getText() );

    }

    public void testFromWithInlineList() throws Exception {
        String source = "rule XYZ \n" +
                            " when \n" +
                            " o: Order( ) \n" +
                            " not( Number( ) from [1, 2, 3] ) \n" +
                            " then \n" +
                            " System.err.println(\"Invalid customer id found!\"); \n" +
                            " o.addError(\"Invalid customer id\"); \n" +
                            "end \n";
        PackageDescr pkg = (PackageDescr) parse( "compilationUnit",
                                                 source );
        assertFalse( parser.getErrors().toString(),
                     parser.hasErrors() );

        RuleDescr rule = (RuleDescr) pkg.getRules().get( 0 );
        assertEquals( "XYZ",
                      rule.getName() );

        PatternDescr number = (PatternDescr) ((NotDescr) rule.getLhs().getDescrs().get( 1 )).getDescrs().get( 0 );
        assertEquals( "[1, 2, 3]",
                      ((FromDescr) number.getSource()).getDataSource().toString() );

    }

    public void testFromWithInlineListMethod() throws Exception {
        String source = "rule XYZ \n" +
                        " when \n" +
                        " o: Order( ) \n" +
                        " Number( ) from [1, 2, 3].sublist(1, 2) \n" +
                        " then \n" +
                        " System.err.println(\"Invalid customer id found!\"); \n" +
                        " o.addError(\"Invalid customer id\"); \n" +
                        "end \n";
        PackageDescr pkg = (PackageDescr) parse( "compilationUnit",
                                                 source );
        assertFalse( parser.getErrors().toString(),
                     parser.hasErrors() );

        RuleDescr rule = (RuleDescr) pkg.getRules().get( 0 );
        assertEquals( "XYZ",
                      rule.getName() );

        assertFalse( parser.hasErrors() );
        PatternDescr number = (PatternDescr) rule.getLhs().getDescrs().get( 1 );
        assertEquals( "[1, 2, 3].sublist(1, 2)",
                      ((FromDescr) number.getSource()).getDataSource().toString() );

    }

    public void testFromWithInlineListIndex() throws Exception {
        String source = "rule XYZ \n" +
                        " when \n" +
                        " o: Order( ) \n" +
                        " Number( ) from [1, 2, 3][1] \n" +
                        " then \n" +
                        " System.err.println(\"Invalid customer id found!\"); \n" +
                        " o.addError(\"Invalid customer id\"); \n" +
                        "end \n";
        PackageDescr pkg = (PackageDescr) parse( "compilationUnit",
                                                 source );
        assertFalse( parser.getErrors().toString(),
                     parser.hasErrors() );

        RuleDescr rule = (RuleDescr) pkg.getRules().get( 0 );
        assertEquals( "XYZ",
                      rule.getName() );

        assertFalse( parser.hasErrors() );
        PatternDescr number = (PatternDescr) rule.getLhs().getDescrs().get( 1 );
        assertEquals( "[1, 2, 3][1]",
                      ((FromDescr) number.getSource()).getDataSource().toString() );
    }

    public void testRuleWithoutEnd() throws Exception {
        String source = "rule \"Invalid customer id\" \n" +
                        " when \n" +
                        " o: Order( ) \n" +
                        " then \n" +
                        " System.err.println(\"Invalid customer id found!\"); \n";
        parse( "compilationUnit",
               source );
        assertTrue( parser.hasErrors() );

    }

    public void testOrWithSpecialBind() throws Exception {
        String source = "rule \"A and (B or C or D)\" \n" +
                        "    when \n" +
                        "        pdo1 : ParametricDataObject( paramID == 101, stringValue == \"1000\" ) and \n" +
                        "        pdo2 :(ParametricDataObject( paramID == 101, stringValue == \"1001\" ) or \n" +
                        "               ParametricDataObject( paramID == 101, stringValue == \"1002\" ) or \n" +
                        "               ParametricDataObject( paramID == 101, stringValue == \"1003\" )) \n" +
                        "    then \n" +
                        "        System.out.println( \"Rule: A and (B or C or D) Fired. pdo1: \" + pdo1 +  \" pdo2: \"+ pdo2); \n" +
                        "end\n";
        PackageDescr pkg = (PackageDescr) parse( "compilationUnit",
                                                 source );
        assertFalse( parser.getErrors().toString(),
                     parser.hasErrors() );

        RuleDescr rule = pkg.getRules().get( 0 );
        AndDescr lhs = rule.getLhs();
        assertEquals( 2,
                      lhs.getDescrs().size() );

        PatternDescr pdo1 = (PatternDescr) lhs.getDescrs().get( 0 );
        assertEquals( "pdo1",
                      pdo1.getIdentifier() );

        OrDescr or = (OrDescr) rule.getLhs().getDescrs().get( 1 );
        assertEquals( 3,
                      or.getDescrs().size() );
        for ( BaseDescr pdo2 : or.getDescrs() ) {
            assertEquals( "pdo2",
                          ((PatternDescr) pdo2).getIdentifier() );
        }

    }

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
        assertEquals( "type == \"stilton\"",
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

    public void testAlmostEmptyRule() throws Exception {
        PackageDescr pkg = (PackageDescr) parseResource( "compilationUnit",
                                                         "almost_empty_rule.drl" );

        assertFalse( parser.getErrors().toString(),
                     parser.hasErrors() );
        assertNotNull( pkg );

        RuleDescr rule = pkg.getRules().get( 0 );

        assertEquals( "almost_empty",
                      rule.getName() );
        assertNotNull( rule.getLhs() );
        assertEquals( "",
                      ((String) rule.getConsequence()).trim() );
    }

    public void testQuotedStringNameRule() throws Exception {
        final RuleDescr rule = (RuleDescr) parseResource( "rule",
                                                          "quoted_string_name_rule.drl" );

        assertFalse( parser.getErrors().toString(),
                         parser.hasErrors() );
        assertNotNull( rule );

        assertEquals( "quoted string name",
                          rule.getName() );
        assertNotNull( rule.getLhs() );
        assertEquals( "",
                          ((String) rule.getConsequence()).trim() );
    }

    public void testNoLoop() throws Exception {
        final RuleDescr rule = (RuleDescr) parseResource( "rule",
                                                          "no-loop.drl" );

        assertFalse( parser.getErrors().toString(),
                     parser.hasErrors() );
        assertNotNull( rule );

        assertEquals( "rule1",
                      rule.getName() );
        final AttributeDescr att = (AttributeDescr) rule.getAttributes().get( "no-loop" );
        assertEquals( "false",
                      att.getValue() );
        assertEquals( "no-loop",
                      att.getName() );
    }

    public void testAutofocus() throws Exception {
        final RuleDescr rule = (RuleDescr) parseResource( "rule",
                                                          "autofocus.drl" );

        assertFalse( parser.getErrors().toString(),
                     parser.hasErrors() );

        assertNotNull( rule );

        assertEquals( "rule1",
                      rule.getName() );
        final AttributeDescr att = (AttributeDescr) rule.getAttributes().get( "auto-focus" );
        assertEquals( "true",
                      att.getValue() );
        assertEquals( "auto-focus",
                      att.getName() );
    }

    public void testRuleFlowGroup() throws Exception {
        final RuleDescr rule = (RuleDescr) parseResource( "rule",
                                                          "ruleflowgroup.drl" );

        assertFalse( parser.getErrors().toString(),
                     parser.hasErrors() );

        assertNotNull( rule );

        assertEquals( "rule1",
                      rule.getName() );
        final AttributeDescr att = (AttributeDescr) rule.getAttributes().get( "ruleflow-group" );
        assertEquals( "a group",
                      att.getValue() );
        assertEquals( "ruleflow-group",
                      att.getName() );
    }

    public void testConsequenceWithDeclaration() throws Exception {
        final RuleDescr rule = (RuleDescr) parseResource( "rule",
                                                          "declaration-in-consequence.drl" );

        assertFalse( parser.getErrors().toString(),
                     parser.hasErrors() );

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

        // System.out.println(( String ) rule.getConsequence());
        // note, need to assert that "i++" is preserved as is, no extra spaces.
    }

    public void testRuleParseLhs() throws Exception {
        final String text = "rule X when Person(age < 42, location==\"atlanta\") \nor\nPerson(name==\"bob\") then end";
        RuleDescr rule = (RuleDescr) parse( "rule",
                                            text );
        assertFalse( parser.getErrors().toString(),
                     parser.hasErrors() );

        assertNotNull( rule );

        AndDescr lhs = rule.getLhs();
        assertEquals( 1,
                      lhs.getDescrs().size() );
        assertEquals( 2,
                      ((OrDescr) lhs.getDescrs().get( 0 )).getDescrs().size() );
    }

    public void testRuleParseLhsWithStringQuotes() throws Exception {
        final String text = "rule X when Person( location==\"atlanta\\\"\") then end\n";
        RuleDescr rule = (RuleDescr) parse( "rule",
                                                text );
        assertFalse( parser.getErrors().toString(),
                         parser.hasErrors() );

        assertNotNull( rule );

        AndDescr lhs = rule.getLhs();
        ExprConstraintDescr constr = (ExprConstraintDescr) ((PatternDescr) lhs.getDescrs().get( 0 )).getDescrs().get( 0 );

        assertEquals( "location==\"atlanta\\\"\"",
                      constr.getText() );
    }

    public void testRuleParseLhsWithStringQuotes2() throws Exception {
        final String text = "rule X when Cheese( $x: type, type == \"s\\tti\\\"lto\\nn\" ) then end\n";
        RuleDescr rule = (RuleDescr) parse( "rule",
                                             text );
        assertFalse( parser.getErrors().toString(),
                     parser.hasErrors() );

        assertNotNull( rule );

        AndDescr lhs = rule.getLhs();
        ExprConstraintDescr constr = (ExprConstraintDescr) ((PatternDescr) lhs.getDescrs().get( 0 )).getDescrs().get( 0 );

        assertEquals( "type == \"s\\tti\\\"lto\\nn\"",
                      constr.getText() );
    }

    public void testLiteralBoolAndNegativeNumbersRule() throws Exception {
        final RuleDescr rule = (RuleDescr) parseResource( "rule",
                                                              "literal_bool_and_negative.drl" );

        assertFalse( parser.getErrors().toString(),
                         parser.hasErrors() );

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
        ExprConstraintDescr fld = (ExprConstraintDescr) fieldAnd.getDescrs().get( 0 );
        assertEquals( "bar == false",
                          fld.getExpression() );

        pattern = (PatternDescr) lhs.getDescrs().get( 1 );
        assertEquals( 1,
                          pattern.getConstraint().getDescrs().size() );

        fieldAnd = (AndDescr) pattern.getConstraint();
        fld = (ExprConstraintDescr) fieldAnd.getDescrs().get( 0 );

        assertEquals( "boo > -42",
                          fld.getText() );

        pattern = (PatternDescr) lhs.getDescrs().get( 2 );
        assertEquals( 1,
                          pattern.getConstraint().getDescrs().size() );

        fieldAnd = (AndDescr) pattern.getConstraint();
        fld = (ExprConstraintDescr) fieldAnd.getDescrs().get( 0 );

        assertEquals( "boo > -42.42",
                          fld.getText() );
    }

    public void testChunkWithoutParens() throws Exception {
        String input = "( foo )";
        createParser( new ANTLRStringStream( input ) );
        String returnData = parser.chunk( DRLLexer.LEFT_PAREN,
                                          DRLLexer.RIGHT_PAREN,
                                          -1 );

        assertEquals( "foo",
                          returnData );
    }

    public void testChunkWithParens() throws Exception {
        String input = "(fnord())";
        createParser( new ANTLRStringStream( input ) );
        String returnData = parser.chunk( DRLLexer.LEFT_PAREN,
                                          DRLLexer.RIGHT_PAREN,
                                          -1 );

        assertEquals( "fnord()",
                          returnData );
    }

    public void testChunkWithParensAndQuotedString() throws Exception {
        String input = "( fnord( \"cheese\" ) )";
        createParser( new ANTLRStringStream( input ) );
        String returnData = parser.chunk( DRLLexer.LEFT_PAREN,
                                          DRLLexer.RIGHT_PAREN,
                                          -1 );

        assertEquals( "fnord( \"cheese\" )",
                          returnData );
    }

    public void testChunkWithRandomCharac5ters() throws Exception {
        String input = "( %*9dkj)";
        createParser( new ANTLRStringStream( input ) );
        String returnData = parser.chunk( DRLLexer.LEFT_PAREN,
                                          DRLLexer.RIGHT_PAREN,
                                          -1 );

        assertEquals( "%*9dkj",
                          returnData );
    }

    public void testEmptyPattern() throws Exception {
        PackageDescr pkg = (PackageDescr) parseResource( "compilationUnit",
                                                         "test_EmptyPattern.drl" );

        assertFalse( parser.getErrors().toString(),
                     parser.hasErrors() );

        assertEquals( 1,
                      pkg.getRules().size() );
        final RuleDescr ruleDescr = (RuleDescr) pkg.getRules().get( 0 );
        assertEquals( "simple rule",
                      ruleDescr.getName() );
        assertNotNull( ruleDescr.getLhs() );
        assertEquals( 1,
                      ruleDescr.getLhs().getDescrs().size() );
        final PatternDescr patternDescr = (PatternDescr) ruleDescr.getLhs().getDescrs().get( 0 );
        assertEquals( 0,
                      patternDescr.getConstraint().getDescrs().size() ); // this
        assertEquals( "Cheese",
                      patternDescr.getObjectType() );

    }

    public void testSimpleMethodCallWithFrom() throws Exception {
        final RuleDescr rule = (RuleDescr) parseResource( "rule",
                                                              "test_SimpleMethodCallWithFrom.drl" );
        assertFalse( parser.getErrors().toString(),
                     parser.hasErrors() );
        final PatternDescr pattern = (PatternDescr) rule.getLhs().getDescrs().get( 0 );
        final FromDescr from = (FromDescr) pattern.getSource();
        final MVELExprDescr method = (MVELExprDescr) from.getDataSource();

        assertEquals( "something.doIt( foo,bar,42,\"hello\",[ a : \"b\", \"something\" : 42, \"a\" : foo, x : [x:y]],\"end\", [a, \"b\", 42] )",
                      method.getExpression() );
    }

    public void testSimpleFunctionCallWithFrom() throws Exception {
        final RuleDescr rule = (RuleDescr) parseResource( "rule",
                                                          "test_SimpleFunctionCallWithFrom.drl" );
        assertFalse( parser.getErrors().toString(),
                     parser.hasErrors() );
        final PatternDescr pattern = (PatternDescr) rule.getLhs().getDescrs().get( 0 );
        final FromDescr from = (FromDescr) pattern.getSource();
        final MVELExprDescr func = (MVELExprDescr) from.getDataSource();

        assertEquals( "doIt( foo,bar,42,\"hello\",[ a : \"b\", \"something\" : 42, \"a\" : foo, x : [x:y]],\"end\", [a, \"b\", 42] )",
                      func.getExpression() );
    }

    public void testSimpleAccessorWithFrom() throws Exception {
        final RuleDescr rule = (RuleDescr) parseResource( "rule",
                                                          "test_SimpleAccessorWithFrom.drl" );
        assertFalse( parser.getErrors().toString(),
                     parser.hasErrors() );
        final PatternDescr pattern = (PatternDescr) rule.getLhs().getDescrs().get( 0 );
        final FromDescr from = (FromDescr) pattern.getSource();
        final MVELExprDescr accessor = (MVELExprDescr) from.getDataSource();

        assertEquals( "something.doIt",
                      accessor.getExpression() );
    }

    public void testSimpleAccessorAndArgWithFrom() throws Exception {
        final RuleDescr rule = (RuleDescr) parseResource( "rule",
                                                          "test_SimpleAccessorArgWithFrom.drl" );
        assertFalse( parser.getErrors().toString(),
                     parser.hasErrors() );
        final PatternDescr pattern = (PatternDescr) rule.getLhs().getDescrs().get( 0 );
        final FromDescr from = (FromDescr) pattern.getSource();
        final MVELExprDescr accessor = (MVELExprDescr) from.getDataSource();

        assertEquals( "something.doIt[\"key\"]",
                      accessor.getExpression() );
    }

    public void testComplexChainedAcessor() throws Exception {
        final RuleDescr rule = (RuleDescr) parseResource( "rule",
                                                          "test_ComplexChainedCallWithFrom.drl" );
        assertFalse( parser.getErrors().toString(),
                     parser.hasErrors() );
        final PatternDescr pattern = (PatternDescr) rule.getLhs().getDescrs().get( 0 );
        final FromDescr from = (FromDescr) pattern.getSource();
        final MVELExprDescr accessor = (MVELExprDescr) from.getDataSource();

        assertEquals( "doIt1( foo,bar,42,\"hello\",[ a : \"b\"], [a, \"b\", 42] ).doIt2(bar, [a, \"b\", 42]).field[\"key\"]",
                          accessor.getExpression() );
    }

    public void testFrom() throws Exception {
        final RuleDescr rule = (RuleDescr) parseResource( "rule",
                                                          "from.drl" );

        assertFalse( parser.getErrorMessages().toString(),
                     parser.hasErrors() );
        assertNotNull( rule );

        assertEquals( "using_from",
                      rule.getName() );

        assertEquals( 9,
                      rule.getLhs().getDescrs().size() );
    }

    public void testSimpleRule() throws Exception {
        final RuleDescr rule = (RuleDescr) parseResource( "rule",
                                                          "simple_rule.drl" );
        assertFalse( parser.getErrors().toString(),
                     parser.hasErrors() );

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

        AndDescr fieldAnd = (AndDescr) first.getConstraint();
        ExprConstraintDescr constraint = (ExprConstraintDescr) fieldAnd.getDescrs().get( 0 );
        assertNotNull( constraint );

        assertEquals( "a==3",
                      constraint.getExpression() );

        // Check second pattern
        final PatternDescr second = (PatternDescr) lhs.getDescrs().get( 1 );
        assertEquals( "foo4",
                      second.getIdentifier() );
        assertEquals( "Bar",
                      second.getObjectType() );

        // no constraints, only a binding
        fieldAnd = (AndDescr) second.getConstraint();
        assertEquals( 0,
                      fieldAnd.getDescrs().size() );

        final BindingDescr binding = second.getBindings().get( 0 );
        assertEquals( "a4",
                      binding.getVariable() );
        assertEquals( "a==4",
                      binding.getExpression() );

        // Check third pattern
        final PatternDescr third = (PatternDescr) lhs.getDescrs().get( 2 );
        assertNull( third.getIdentifier() );
        assertEquals( "Baz",
                          third.getObjectType() );

        assertEqualsIgnoreWhitespace( "if ( a == b ) { " + "  assert( foo3 );" + "} else {" + "  retract( foo4 );" + "}" + "  System.out.println( a4 );",
                                          (String) rule.getConsequence() );
    }

    public void testRestrictionsMultiple() throws Exception {
        final RuleDescr rule = (RuleDescr) parseResource( "rule",
                                                          "restrictions_test.drl" );
        assertFalse( parser.getErrors().toString(),
                         parser.hasErrors() );

        assertNotNull( rule );

        assertEqualsIgnoreWhitespace( "consequence();",
                                          (String) rule.getConsequence() );
        assertEquals( "simple_rule",
                          rule.getName() );
        assertEquals( 2,
                          rule.getLhs().getDescrs().size() );

        // The first pattern, with 2 restrictions on a single field (plus a
        // connective)
        PatternDescr pattern = (PatternDescr) rule.getLhs().getDescrs().get( 0 );
        assertEquals( "Person",
                          pattern.getObjectType() );
        assertEquals( 1,
                          pattern.getConstraint().getDescrs().size() );

        AndDescr and = (AndDescr) pattern.getConstraint();
        ExprConstraintDescr fld = (ExprConstraintDescr) and.getDescrs().get( 0 );
        assertEquals( "age > 30 && < 40",
                          fld.getExpression() );

        // the second col, with 2 fields, the first with 2 restrictions, the
        // second field with one
        pattern = (PatternDescr) rule.getLhs().getDescrs().get( 1 );
        assertEquals( "Vehicle",
                          pattern.getObjectType() );
        assertEquals( 2,
                          pattern.getConstraint().getDescrs().size() );

        and = (AndDescr) pattern.getConstraint();
        fld = (ExprConstraintDescr) and.getDescrs().get( 0 );
        assertEquals( "type == \"sedan\" || == \"wagon\"",
                          fld.getExpression() );

        // now the second field
        fld = (ExprConstraintDescr) and.getDescrs().get( 1 );
        assertEquals( "age < 3",
                          fld.getExpression() );
    }

    public void testLineNumberInAST() throws Exception {
        // also see testSimpleExpander to see how this works with an expander
        // (should be the same).

        final RuleDescr rule = (RuleDescr) parseResource( "rule",
                                                          "simple_rule.drl" );

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
    }

    public void testLineNumberIncludingCommentsInRHS() throws Exception {
        PackageDescr pkg = (PackageDescr) parseResource( "compilationUnit",
                                                         "test_CommentLineNumbersInConsequence.drl" );

        assertFalse( parser.getErrors().toString(),
                     parser.hasErrors() );

        final String rhs = (String) ((RuleDescr) pkg.getRules().get( 0 )).getConsequence();
        String expected = "\\s*//woot$\\s*first$\\s*$\\s*//$\\s*$\\s*/\\* lala$\\s*$\\s*\\*/$\\s*second$\\s*";
        assertTrue( Pattern.compile( expected,
                                     Pattern.DOTALL | Pattern.MULTILINE ).matcher( rhs ).matches() );
    }

    public void testLhsSemicolonDelim() throws Exception {
        final RuleDescr rule = (RuleDescr) parseResource( "rule",
                                                          "lhs_semicolon_delim.drl" );

        assertFalse( parser.getErrors().toString(),
                     parser.hasErrors() );

        assertNotNull( rule );

        assertEquals( "simple_rule",
                      rule.getName() );

        final AndDescr lhs = rule.getLhs();

        assertNotNull( lhs );

        assertEquals( 3,
                      lhs.getDescrs().size() );

        // System.err.println( lhs.getDescrs() );

        // Check first pattern
        final PatternDescr first = (PatternDescr) lhs.getDescrs().get( 0 );
        assertEquals( "foo3",
                      first.getIdentifier() );
        assertEquals( "Bar",
                      first.getObjectType() );

        assertEquals( 1,
                      first.getConstraint().getDescrs().size() );

        // LiteralDescr constraint = (LiteralDescr) first.getDescrs().get( 0 );
        AndDescr and = (AndDescr) first.getConstraint();
        ExprConstraintDescr fld = (ExprConstraintDescr) and.getDescrs().get( 0 );
        assertNotNull( fld );

        assertEquals( "a==3",
                      fld.getExpression() );

        // Check second pattern
        final PatternDescr second = (PatternDescr) lhs.getDescrs().get( 1 );
        assertEquals( "foo4",
                      second.getIdentifier() );
        assertEquals( "Bar",
                      second.getObjectType() );

        assertEquals( 1,
                      second.getBindings().size() );

        final BindingDescr fieldBindingDescr = second.getBindings().get( 0 );
        assertEquals( "a==4",
                      fieldBindingDescr.getExpression() );
        assertEquals( "a4",
                      fieldBindingDescr.getVariable() );

        // Check third pattern
        final PatternDescr third = (PatternDescr) lhs.getDescrs().get( 2 );
        assertNull( third.getIdentifier() );
        assertEquals( "Baz",
                      third.getObjectType() );

        assertEqualsIgnoreWhitespace( "if ( a == b ) { " + "  assert( foo3 );" + "} else {" + "  retract( foo4 );" + "}" + "  System.out.println( a4 );",
                                      (String) rule.getConsequence() );
    }

    public void testNotNode() throws Exception {
        final RuleDescr rule = (RuleDescr) parseResource( "rule",
                                                          "rule_not.drl" );

        assertFalse( parser.getErrors().toString(),
                     parser.hasErrors() );

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
        final ExprConstraintDescr fld = (ExprConstraintDescr) and.getDescrs().get( 0 );

        assertEquals( "type == \"stilton\"",
                      fld.getExpression() );
    }

    public void testNotExistWithBrackets() throws Exception {

        final PackageDescr pkg = (PackageDescr) parseResource( "compilationUnit",
                                                               "not_exist_with_brackets.drl" );

        assertFalse( parser.getErrors().toString(),
                     parser.hasErrors() );

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
    }

    public void testSimpleQuery() throws Exception {
        final QueryDescr query = (QueryDescr) parseResource( "query",
                                                             "simple_query.drl" );

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
        ExprConstraintDescr fld = (ExprConstraintDescr) and.getDescrs().get( 0 );
        assertNotNull( fld );

        assertEquals( "a==3",
                      fld.getExpression() );

        // Check second pattern
        final PatternDescr second = (PatternDescr) lhs.getDescrs().get( 1 );
        assertEquals( "foo4",
                      second.getIdentifier() );
        assertEquals( "Bar",
                      second.getObjectType() );

        assertEquals( 1,
                      second.getBindings().size() );
        // check it has field bindings.
        final BindingDescr bindingDescr = second.getBindings().get( 0 );
        assertEquals( "a==4",
                      bindingDescr.getExpression() );
        assertEquals( "a4",
                      bindingDescr.getVariable() );
    }

    public void testQueryRuleMixed() throws Exception {
        final PackageDescr pkg = (PackageDescr) parseResource( "compilationUnit",
                                                               "query_and_rule.drl" );

        assertEquals( 4,
                      pkg.getRules().size() ); // as queries are rules
        RuleDescr rule = (RuleDescr) pkg.getRules().get( 0 );
        assertEquals( "bar",
                      rule.getName() );

        QueryDescr query = (QueryDescr) pkg.getRules().get( 1 );
        assertEquals( "simple_query",
                      query.getName() );

        rule = (RuleDescr) pkg.getRules().get( 2 );
        assertEquals( "bar2",
                      rule.getName() );

        query = (QueryDescr) pkg.getRules().get( 3 );
        assertEquals( "simple_query2",
                      query.getName() );
    }

    public void testMultipleRules() throws Exception {
        final PackageDescr pkg = (PackageDescr) parseResource( "compilationUnit",
                                                               "multiple_rules.drl" );

        final List rules = pkg.getRules();

        assertEquals( 2,
                      rules.size() );

        final RuleDescr rule0 = (RuleDescr) rules.get( 0 );
        assertEquals( "Like Stilton",
                      rule0.getName() );

        final RuleDescr rule1 = (RuleDescr) rules.get( 1 );
        assertEquals( "Like Cheddar",
                      rule1.getName() );

        // checkout the first rule
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

        // checkout the second rule
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
    }

    public void testExpanderLineSpread() throws Exception {
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

    public void testExpanderMultipleConstraints() throws Exception {
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
        assertEquals( "age < 42",
                      ((ExprConstraintDescr) pattern.getConstraint().getDescrs().get( 0 )).getExpression() );
        assertEquals( "location==atlanta",
                      ((ExprConstraintDescr) pattern.getConstraint().getDescrs().get( 1 )).getExpression() );

        pattern = (PatternDescr) rule.getLhs().getDescrs().get( 1 );
        assertEquals( "Bar",
                      pattern.getObjectType() );

        assertNotNull( (String) rule.getConsequence() );

    }

    public void testExpanderMultipleConstraintsFlush() throws Exception {
        final DrlParser parser = new DrlParser();
        // this is similar to the other test, but it requires a flush to add the
        // constraints
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
        assertEquals( "age < 42",
                      ((ExprConstraintDescr) pattern.getConstraint().getDescrs().get( 0 )).getExpression() );
        assertEquals( "location==atlanta",
                      ((ExprConstraintDescr) pattern.getConstraint().getDescrs().get( 1 )).getExpression() );

        assertNotNull( (String) rule.getConsequence() );

    }

    // public void testExpanderUnExpandableErrorLines() throws Exception {
    //
    // //stubb expander
    // final ExpanderResolver res = new ExpanderResolver() {
    // public Expander get(String name,
    // String config) {
    // return new Expander() {
    // public String expand(String scope,
    // String pattern) {
    // if ( pattern.startsWith( "Good" ) ) {
    // return pattern;
    // } else {
    // throw new IllegalArgumentException( "whoops" );
    // }
    //
    // }
    // };
    // }
    // };
    //
    // final DRLParser parser = parseResource( "expander_line_errors.dslr" );
    // parser.setExpanderResolver( res );
    // parser.compilationUnit();
    // assertTrue( parser.hasErrors() );
    //
    // final List messages = parser.getErrorMessages();
    // assertEquals( messages.size(),
    // parser.getErrors().size() );
    //
    // assertEquals( 4,
    // parser.getErrors().size() );
    // assertEquals( ExpanderException.class,
    // parser.getErrors().get( 0 ).getClass() );
    // assertEquals( 8,
    // ((RecognitionException) parser.getErrors().get( 0 )).line );
    // assertEquals( 10,
    // ((RecognitionException) parser.getErrors().get( 1 )).line );
    // assertEquals( 12,
    // ((RecognitionException) parser.getErrors().get( 2 )).line );
    // assertEquals( 13,
    // ((RecognitionException) parser.getErrors().get( 3 )).line );
    //
    // final PackageDescr pack = parser.getPackageDescr();
    // assertNotNull( pack );
    //
    // final ExpanderException ex = (ExpanderException) parser.getErrors().get(
    // 0 );
    // assertTrue( ex.getMessage().indexOf( "whoops" ) > -1 );
    //
    // }

    public void testBasicBinding() throws Exception {
        final PackageDescr pkg = (PackageDescr) parseResource( "compilationUnit",
                                                               "basic_binding.drl" );

        final RuleDescr ruleDescr = (RuleDescr) pkg.getRules().get( 0 );

        final AndDescr lhs = ruleDescr.getLhs();
        assertEquals( 1,
                      lhs.getDescrs().size() );
        final PatternDescr cheese = (PatternDescr) lhs.getDescrs().get( 0 );
        assertEquals( "Cheese",
                      cheese.getObjectType() );
        assertEquals( 0,
                      cheese.getConstraint().getDescrs().size() );
        assertEquals( 1,
                      cheese.getBindings().size() );
        final BindingDescr fieldBinding = (BindingDescr) cheese.getBindings().get( 0 );
        assertEquals( "type",
                      fieldBinding.getExpression() );
    }

    public void testBoundVariables() throws Exception {
        final PackageDescr pkg = (PackageDescr) parseResource( "compilationUnit",
                                                               "bindings.drl" );

        final RuleDescr ruleDescr = (RuleDescr) pkg.getRules().get( 0 );

        final AndDescr lhs = ruleDescr.getLhs();
        assertEquals( 2,
                      lhs.getDescrs().size() );
        final PatternDescr cheese = (PatternDescr) lhs.getDescrs().get( 0 );
        assertEquals( "Cheese",
                      cheese.getObjectType() );
        assertEquals( 2,
                      lhs.getDescrs().size() );
        BindingDescr fieldBinding = (BindingDescr) cheese.getBindings().get( 0 );
        assertEquals( "type == \"stilton\"",
                      fieldBinding.getExpression() );

        final PatternDescr person = (PatternDescr) lhs.getDescrs().get( 1 );
        fieldBinding = (BindingDescr) person.getBindings().get( 0 );
        assertEquals( "name == \"bob\"",
                      fieldBinding.getExpression() );

        ExprConstraintDescr fld = (ExprConstraintDescr) person.getConstraint().getDescrs().get( 0 );
        assertEquals( "likes == $type",
                      fld.getExpression() );
    }

    public void testOrNesting() throws Exception {
        final PackageDescr pkg = (PackageDescr) parseResource( "compilationUnit",
                                                               "or_nesting.drl" );

        assertNotNull( pkg );
        assertEquals( 1,
                      pkg.getRules().size() );
        final RuleDescr rule = (RuleDescr) pkg.getRules().get( 0 );
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
    public void testAndOrRules() throws Exception {
        final PackageDescr pkg = (PackageDescr) parseResource( "compilationUnit",
                                                               "and_or_rule.drl" );

        assertNotNull( pkg );
        assertEquals( 1,
                      pkg.getRules().size() );
        final RuleDescr rule = (RuleDescr) pkg.getRules().get( 0 );
        assertEquals( "simple_rule",
                      rule.getName() );

        // we will have 3 children under the main And node
        final AndDescr and = rule.getLhs();
        assertEquals( 3,
                      and.getDescrs().size() );

        PatternDescr left = (PatternDescr) and.getDescrs().get( 0 );
        PatternDescr right = (PatternDescr) and.getDescrs().get( 1 );
        assertEquals( "Person",
                      left.getObjectType() );
        assertEquals( "Cheese",
                      right.getObjectType() );

        assertEquals( 1,
                      left.getConstraint().getDescrs().size() );

        ExprConstraintDescr fld = (ExprConstraintDescr) left.getConstraint().getDescrs().get( 0 );

        assertEquals( "name == \"mark\"",
                      fld.getExpression() );

        assertEquals( 1,
                      right.getConstraint().getDescrs().size() );

        fld = (ExprConstraintDescr) right.getConstraint().getDescrs().get( 0 );

        assertEquals( "type == \"stilton\"",
                      fld.getExpression() );

        // now the "||" part
        final OrDescr or = (OrDescr) and.getDescrs().get( 2 );
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

        fld = (ExprConstraintDescr) left.getConstraint().getDescrs().get( 0 );

        assertEquals( "name == \"mark\"",
                      fld.getExpression() );

        assertEquals( 1,
                      right.getConstraint().getDescrs().size() );

        fld = (ExprConstraintDescr) right.getConstraint().getDescrs().get( 0 );

        assertEquals( "type == \"stilton\"",
                      fld.getExpression() );

        assertEqualsIgnoreWhitespace( "System.out.println( \"Mark and Michael\" );",
                                      (String) rule.getConsequence() );
    }

    /** test basic foo : Fact() || Fact() stuff */
    public void testOrWithBinding() throws Exception {
        final PackageDescr pkg = (PackageDescr) parseResource( "compilationUnit",
                                                               "or_binding.drl" );

        assertEquals( 1,
                      pkg.getRules().size() );
        final RuleDescr rule = (RuleDescr) pkg.getRules().get( 0 );
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
    }

    /** test basic foo : Fact() || Fact() stuff binding to an "or" */
    public void testOrBindingComplex() throws Exception {
        final PackageDescr pkg = (PackageDescr) parseResource( "compilationUnit",
                                                               "or_binding_complex.drl" );

        assertEquals( 1,
                      pkg.getRules().size() );
        final RuleDescr rule = (RuleDescr) pkg.getRules().get( 0 );
        assertEquals( 1,
                      rule.getLhs().getDescrs().size() );

        assertEquals( 1,
                      rule.getLhs().getDescrs().size() );

        final OrDescr or = (OrDescr) rule.getLhs().getDescrs().get( 0 );
        assertEquals( 2,
                      or.getDescrs().size() );

        // first fact
        final PatternDescr firstFact = (PatternDescr) or.getDescrs().get( 0 );
        assertEquals( "Person",
                      firstFact.getObjectType() );
        assertEquals( "foo",
                      firstFact.getIdentifier() );

        // second "option"
        final PatternDescr secondFact = (PatternDescr) or.getDescrs().get( 1 );
        assertEquals( "Person",
                      secondFact.getObjectType() );
        assertEquals( 1,
                      secondFact.getConstraint().getDescrs().size() );
        assertEquals( "foo",
                      secondFact.getIdentifier() );

        assertEqualsIgnoreWhitespace( "System.out.println( \"Mark and Michael\" + bar );",
                                      (String) rule.getConsequence() );
    }

    public void testOrBindingWithBrackets() throws Exception {
        final PackageDescr pkg = (PackageDescr) parseResource( "compilationUnit",
                                                               "or_binding_with_brackets.drl" );

        assertEquals( 1,
                      pkg.getRules().size() );
        final RuleDescr rule = (RuleDescr) pkg.getRules().get( 0 );
        assertEquals( 1,
                      rule.getLhs().getDescrs().size() );

        assertEquals( 1,
                      rule.getLhs().getDescrs().size() );

        final OrDescr or = (OrDescr) rule.getLhs().getDescrs().get( 0 );
        assertEquals( 2,
                      or.getDescrs().size() );

        // first fact
        final PatternDescr firstFact = (PatternDescr) or.getDescrs().get( 0 );
        assertEquals( "Person",
                      firstFact.getObjectType() );
        assertEquals( "foo",
                      firstFact.getIdentifier() );

        // second "option"
        final PatternDescr secondFact = (PatternDescr) or.getDescrs().get( 0 );
        assertEquals( "Person",
                      secondFact.getObjectType() );
        assertEquals( "foo",
                      secondFact.getIdentifier() );

        assertEqualsIgnoreWhitespace( "System.out.println( \"Mark and Michael\" + bar );",
                                      (String) rule.getConsequence() );
    }

    /** */
    public void testBracketsPrecedence() throws Exception {
        final PackageDescr pkg = (PackageDescr) parseResource( "compilationUnit",
                                                               "brackets_precedence.drl" );

        assertEquals( 1,
                      pkg.getRules().size() );
        final RuleDescr rule = (RuleDescr) pkg.getRules().get( 0 );

        final AndDescr rootAnd = (AndDescr) rule.getLhs();

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
    }

    public void testEvalMultiple() throws Exception {
        final PackageDescr pkg = (PackageDescr) parseResource( "compilationUnit",
                                                               "eval_multiple.drl" );

        assertEquals( 1,
                      pkg.getRules().size() );
        final RuleDescr rule = (RuleDescr) pkg.getRules().get( 0 );
        assertEquals( 4,
                      rule.getLhs().getDescrs().size() );

        final EvalDescr eval = (EvalDescr) rule.getLhs().getDescrs().get( 0 );
        assertEqualsIgnoreWhitespace( "abc(\"foo\") + 5",
                                      (String) eval.getContent() );

        final PatternDescr pattern = (PatternDescr) rule.getLhs().getDescrs().get( 1 );
        assertEquals( "Foo",
                      pattern.getObjectType() );

    }

    public void testWithEval() throws Exception {
        final PackageDescr pkg = (PackageDescr) parseResource( "compilationUnit",
                                                               "with_eval.drl" );

        assertEquals( 1,
                      pkg.getRules().size() );
        final RuleDescr rule = (RuleDescr) pkg.getRules().get( 0 );
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
    }

    public void testWithRetval() throws Exception {
        final PackageDescr pkg = (PackageDescr) parseResource( "compilationUnit",
                                                               "with_retval.drl" );

        assertEquals( 1,
                      pkg.getRules().size() );

        final RuleDescr rule = (RuleDescr) pkg.getRules().get( 0 );
        assertEquals( 1,
                      rule.getLhs().getDescrs().size() );
        final PatternDescr col = (PatternDescr) rule.getLhs().getDescrs().get( 0 );
        assertEquals( 1,
                      col.getConstraint().getDescrs().size() );
        assertEquals( "Foo",
                      col.getObjectType() );
        final ExprConstraintDescr fld = (ExprConstraintDescr) col.getConstraint().getDescrs().get( 0 );

        assertEquals( "name== (a + b)",
                      fld.getExpression() );
    }

    public void testWithPredicate() throws Exception {
        final PackageDescr pkg = (PackageDescr) parseResource( "compilationUnit",
                                                               "with_predicate.drl" );

        assertEquals( 1,
                      pkg.getRules().size() );

        final RuleDescr rule = (RuleDescr) pkg.getRules().get( 0 );
        assertEquals( 1,
                      rule.getLhs().getDescrs().size() );
        final PatternDescr col = (PatternDescr) rule.getLhs().getDescrs().get( 0 );
        AndDescr and = (AndDescr) col.getConstraint();
        assertEquals( 1,
                      and.getDescrs().size() );

        final BindingDescr field = (BindingDescr) col.getBindings().get( 0 );
        final ExprConstraintDescr pred = (ExprConstraintDescr) and.getDescrs().get( 0 );
        assertEquals( "age",
                      field.getExpression() );
        assertEquals( "$age2",
                      field.getVariable() );
        assertEqualsIgnoreWhitespace( "$age2 == $age1+2",
                                      pred.getExpression() );
    }

    public void testNotWithConstraint() throws Exception {
        final PackageDescr pkg = (PackageDescr) parseResource( "compilationUnit",
                                                               "not_with_constraint.drl" );

        assertEquals( 1,
                      pkg.getRules().size() );

        final RuleDescr rule = (RuleDescr) pkg.getRules().get( 0 );
        assertEquals( 2,
                      rule.getLhs().getDescrs().size() );

        PatternDescr pattern = (PatternDescr) rule.getLhs().getDescrs().get( 0 );
        final BindingDescr fieldBinding = (BindingDescr) pattern.getBindings().get( 0 );
        assertEquals( "$likes",
                      fieldBinding.getVariable() );

        final NotDescr not = (NotDescr) rule.getLhs().getDescrs().get( 1 );
        pattern = (PatternDescr) not.getDescrs().get( 0 );

        final ExprConstraintDescr fld = (ExprConstraintDescr) pattern.getConstraint().getDescrs().get( 0 );

        assertEquals( "type == $likes",
                      fld.getExpression() );
    }

    public void testFunctions() throws Exception {
        final PackageDescr pkg = (PackageDescr) parseResource( "compilationUnit",
                                                               "functions.drl" );

        assertEquals( 2,
                      pkg.getRules().size() );

        final List functions = pkg.getFunctions();
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
                                      func.getBody() );

        func = (FunctionDescr) functions.get( 1 );
        assertEquals( "functionB",
                      func.getName() );
        assertEqualsIgnoreWhitespace( "bar();",
                                      func.getText() );
    }

    public void testComment() throws Exception {
        final PackageDescr pkg = (PackageDescr) parseResource( "compilationUnit",
                                                               "comment.drl" );

        assertNotNull( pkg );

        assertEquals( "foo.bar",
                      pkg.getName() );
    }

    public void testAttributes() throws Exception {
        final RuleDescr rule = (RuleDescr) parseResource( "rule",
                                                          "rule_attributes.drl" );
        assertEquals( "simple_rule",
                      rule.getName() );
        assertEqualsIgnoreWhitespace( "bar();",
                                      (String) rule.getConsequence() );

        final Map<String, AttributeDescr> attrs = rule.getAttributes();
        assertEquals( 6,
                      attrs.size() );

        AttributeDescr at = (AttributeDescr) attrs.get( "salience" );
        assertEquals( "salience",
                      at.getName() );
        assertEquals( "42",
                      at.getValue() );

        at = (AttributeDescr) attrs.get( "agenda-group" );
        assertEquals( "agenda-group",
                      at.getName() );
        assertEquals( "my_group",
                      at.getValue() );

        at = (AttributeDescr) attrs.get( "no-loop" );
        assertEquals( "no-loop",
                      at.getName() );
        assertEquals( "true",
                      at.getValue() );

        at = (AttributeDescr) attrs.get( "duration" );
        assertEquals( "duration",
                      at.getName() );
        assertEquals( "42",
                      at.getValue() );

        at = (AttributeDescr) attrs.get( "activation-group" );
        assertEquals( "activation-group",
                      at.getName() );
        assertEquals( "my_activation_group",
                      at.getValue() );

        at = (AttributeDescr) attrs.get( "lock-on-active" );
        assertEquals( "lock-on-active",
                      at.getName() );
        assertEquals( "true",
                      at.getValue() );
    }

    public void testEnabledExpression() throws Exception {
        final RuleDescr rule = (RuleDescr) parseResource( "rule",
                                                          "rule_enabled_expression.drl" );
        assertEquals( "simple_rule",
                      rule.getName() );
        assertEqualsIgnoreWhitespace( "bar();",
                                      (String) rule.getConsequence() );

        final Map<String, AttributeDescr> attrs = rule.getAttributes();
        assertEquals( 3,
                      attrs.size() );

        AttributeDescr at = (AttributeDescr) attrs.get( "enabled" );
        assertEquals( "enabled",
                      at.getName() );
        assertEquals( "( 1 + 1 == 2 )",
                      at.getValue() );

        at = (AttributeDescr) attrs.get( "salience" );
        assertEquals( "salience",
                      at.getName() );
        assertEquals( "( 1+2 )",
                      at.getValue() );

        at = (AttributeDescr) attrs.get( "lock-on-active" );
        assertEquals( "lock-on-active",
                      at.getName() );
        assertEquals( "true",
                      at.getValue() );
    }

    public void testDurationExpression() throws Exception {
        final RuleDescr rule = (RuleDescr) parseResource( "rule",
                                                          "rule_duration_expression.drl" );
        assertEquals( "simple_rule",
                      rule.getName() );
        assertEqualsIgnoreWhitespace( "bar();",
                                      (String) rule.getConsequence() );

        final Map<String, AttributeDescr> attrs = rule.getAttributes();
        assertEquals( 2,
                      attrs.size() );

        AttributeDescr at = (AttributeDescr) attrs.get( "duration" );
        assertEquals( "duration",
                      at.getName() );
        assertEquals( "1h30m",
                      at.getValue() );

        at = (AttributeDescr) attrs.get( "lock-on-active" );
        assertEquals( "lock-on-active",
                      at.getName() );
        assertEquals( "true",
                      at.getValue() );
    }

    public void testCalendars() throws Exception {
        final RuleDescr rule = (RuleDescr) parseResource( "rule",
                                                          "rule_calendars_attribute.drl" );
        assertEquals( "simple_rule",
                      rule.getName() );
        assertEqualsIgnoreWhitespace( "bar();",
                                      (String) rule.getConsequence() );

        final Map<String, AttributeDescr> attrs = rule.getAttributes();
        assertEquals( 2,
                      attrs.size() );

        AttributeDescr at = (AttributeDescr) attrs.get( "calendars" );
        assertEquals( "calendars",
                      at.getName() );
        assertEquals( "[ \"cal1\" ]",
                      at.getValue() );

        at = (AttributeDescr) attrs.get( "lock-on-active" );
        assertEquals( "lock-on-active",
                      at.getName() );
        assertEquals( "true",
                      at.getValue() );
    }

    public void testCalendars2() throws Exception {
        final RuleDescr rule = (RuleDescr) parseResource( "rule",
                                                          "rule_calendars_attribute2.drl" );
        assertFalse( parser.getErrors().toString(),
                     parser.hasErrors() );
        assertEquals( "simple_rule",
                      rule.getName() );
        assertEqualsIgnoreWhitespace( "bar();",
                                      (String) rule.getConsequence() );

        final Map<String, AttributeDescr> attrs = rule.getAttributes();
        assertEquals( 2,
                      attrs.size() );

        AttributeDescr at = (AttributeDescr) attrs.get( "calendars" );
        assertEquals( "calendars",
                      at.getName() );
        assertEquals( "[ \"cal 1\", \"cal 2\", \"cal 3\" ]",
                      at.getValue() );

        at = (AttributeDescr) attrs.get( "lock-on-active" );
        assertEquals( "lock-on-active",
                      at.getName() );
        assertEquals( "true",
                      at.getValue() );
    }

    public void testAttributes_alternateSyntax() throws Exception {
        final RuleDescr rule = (RuleDescr) parseResource( "rule",
                                                          "rule_attributes_alt.drl" );
        assertEquals( "simple_rule",
                      rule.getName() );
        assertEqualsIgnoreWhitespace( "bar();",
                                      (String) rule.getConsequence() );

        final Map<String, AttributeDescr> attrs = rule.getAttributes();
        assertEquals( 6,
                      attrs.size() );

        AttributeDescr at = (AttributeDescr) attrs.get( "salience" );
        assertEquals( "salience",
                      at.getName() );
        assertEquals( "42",
                      at.getValue() );

        at = (AttributeDescr) attrs.get( "agenda-group" );
        assertEquals( "agenda-group",
                      at.getName() );
        assertEquals( "my_group",
                      at.getValue() );

        at = (AttributeDescr) attrs.get( "no-loop" );
        assertEquals( "no-loop",
                      at.getName() );
        assertEquals( "true",
                      at.getValue() );

        at = (AttributeDescr) attrs.get( "lock-on-active" );
        assertEquals( "lock-on-active",
                      at.getName() );
        assertEquals( "true",
                      at.getValue() );

        at = (AttributeDescr) attrs.get( "duration" );
        assertEquals( "duration",
                      at.getName() );
        assertEquals( "42",
                      at.getValue() );

        at = (AttributeDescr) attrs.get( "activation-group" );
        assertEquals( "activation-group",
                      at.getName() );
        assertEquals( "my_activation_group",
                      at.getValue() );
    }

    public void testEnumeration() throws Exception {
        final RuleDescr rule = (RuleDescr) parseResource( "rule",
                                                          "enumeration.drl" );
        assertEquals( "simple_rule",
                      rule.getName() );
        assertEquals( 1,
                      rule.getLhs().getDescrs().size() );
        final PatternDescr col = (PatternDescr) rule.getLhs().getDescrs().get( 0 );
        assertEquals( "Foo",
                      col.getObjectType() );
        assertEquals( 1,
                      col.getConstraint().getDescrs().size() );
        final ExprConstraintDescr fld = (ExprConstraintDescr) col.getConstraint().getDescrs().get( 0 );

        assertEquals( "bar == Foo.BAR",
                      fld.getExpression() );
    }

    public void testExtraLhsNewline() throws Exception {
        parseResource( "compilationUnit",
                       "extra_lhs_newline.drl" );
    }

    public void testSoundsLike() throws Exception {
        final PackageDescr pkg = (PackageDescr) parseResource( "compilationUnit",
                                                               "soundslike_operator.drl" );

        RuleDescr rule = (RuleDescr) pkg.getRules().get( 0 );
        PatternDescr pat = (PatternDescr) rule.getLhs().getDescrs().get( 0 );

        pat.getConstraint();
    }

    public void testPackageAttributes() throws Exception {
        final PackageDescr pkg = (PackageDescr) parseResource( "compilationUnit",
                                                               "package_attributes.drl" );

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
        at = (AttributeDescr) rule.getAttributes().get( "agenda-group" );
        assertEquals( "agenda-group",
                      at.getName() );
        assertEquals( "x",
                      at.getValue() );
        at = (AttributeDescr) rule.getAttributes().get( "dialect" );
        assertEquals( "dialect",
                      at.getName() );
        assertEquals( "java",
                      at.getValue() );

        rule = (RuleDescr) pkg.getRules().get( 1 );
        assertEquals( "baz",
                      rule.getName() );
        at = (AttributeDescr) rule.getAttributes().get( "dialect" );
        assertEquals( "dialect",
                      at.getName() );
        assertEquals( "mvel",
                      at.getValue() );
        at = (AttributeDescr) rule.getAttributes().get( "agenda-group" );
        assertEquals( "agenda-group",
                      at.getName() );
        assertEquals( "x",
                      at.getValue() );

    }

    public void testStatementOrdering1() throws Exception {
        final PackageDescr pkg = (PackageDescr) parseResource( "compilationUnit",
                                                               "statement_ordering_1.drl" );

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
    }

    public void testRuleNamesStartingWithNumbers() throws Exception {
        final PackageDescr pkg = (PackageDescr) parseResource( "compilationUnit",
                                                               "rule_names_number_prefix.drl" );

        assertEquals( 2,
                      pkg.getRules().size() );

        assertEquals( "1. Do Stuff!",
                      ((RuleDescr) pkg.getRules().get( 0 )).getName() );
        assertEquals( "2. Do More Stuff!",
                      ((RuleDescr) pkg.getRules().get( 1 )).getName() );
    }

    public void testEvalWithNewline() throws Exception {
        parseResource( "compilationUnit",
                       "eval_with_newline.drl" );
    }

    public void testEndPosition() throws Exception {
        final PackageDescr pkg = (PackageDescr) parseResource( "compilationUnit",
                                                               "test_EndPosition.drl" );
        final RuleDescr rule = (RuleDescr) pkg.getRules().get( 0 );
        final PatternDescr col = (PatternDescr) rule.getLhs().getDescrs().get( 0 );
        // assertEquals( 6,
        // col.getLine() );
        //
        // assertEquals( 8,
        // col.getEndLine() );
    }

    public void testQualifiedClassname() throws Exception {
        final PackageDescr pkg = (PackageDescr) parseResource( "compilationUnit",
                                                               "qualified_classname.drl" );

        final RuleDescr rule = (RuleDescr) pkg.getRules().get( 0 );

        final PatternDescr p = (PatternDescr) rule.getLhs().getDescrs().get( 0 );

        assertEquals( "com.cheeseco.Cheese",
                      p.getObjectType() );
    }

    public void testAccumulate() throws Exception {
        final PackageDescr pkg = (PackageDescr) parseResource( "compilationUnit",
                                                               "accumulate.drl" );

        assertEquals( 1,
                      pkg.getRules().size() );
        final RuleDescr rule = (RuleDescr) pkg.getRules().get( 0 );
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

    public void testAccumulateWithBindings() throws Exception {
        final PackageDescr pkg = (PackageDescr) parseResource( "compilationUnit",
                                                               "accumulate_with_bindings.drl" );

        assertEquals( 1,
                      pkg.getRules().size() );
        final RuleDescr rule = (RuleDescr) pkg.getRules().get( 0 );
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

    public void testCollect() throws Exception {
        final PackageDescr pkg = (PackageDescr) parseResource( "compilationUnit",
                                                               "collect.drl" );

        assertEquals( 1,
                      pkg.getRules().size() );
        final RuleDescr rule = (RuleDescr) pkg.getRules().get( 0 );
        assertEquals( 1,
                      rule.getLhs().getDescrs().size() );

        final PatternDescr outPattern = (PatternDescr) rule.getLhs().getDescrs().get( 0 );
        final CollectDescr collect = (CollectDescr) outPattern.getSource();

        final PatternDescr pattern = (PatternDescr) collect.getInputPattern();
        assertEquals( "Person",
                      pattern.getObjectType() );
    }

    public void testPredicate2() throws Exception {
        // predicates are also prefixed by the eval keyword
        final RuleDescr rule = (RuleDescr) parse( "rule",
                                                  "rule X when Foo(eval( $var.equals(\"xyz\") )) then end" );

        final PatternDescr pattern = (PatternDescr) rule.getLhs().getDescrs().get( 0 );
        final List constraints = pattern.getConstraint().getDescrs();
        assertEquals( 1,
                      constraints.size() );

        final ExprConstraintDescr predicate = (ExprConstraintDescr) constraints.get( 0 );
        assertEquals( "eval( $var.equals(\"xyz\") )",
                        predicate.getExpression() );
    }

    public void testEscapedStrings() throws Exception {
        final RuleDescr rule = (RuleDescr) parseResource( "rule",
                                                          "escaped-string.drl" );

        assertNotNull( rule );

        assertEquals( "test_Quotes",
                      rule.getName() );

        final String expected = "String s = \"\\\"\\n\\t\\\\\";";

        assertEqualsIgnoreWhitespace( expected,
                                      (String) rule.getConsequence() );
    }

    public void testNestedCEs() throws Exception {
        final RuleDescr rule = (RuleDescr) parseResource( "rule",
                                                          "nested_conditional_elements.drl" );

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

    public void testForall() throws Exception {
        final PackageDescr pkg = (PackageDescr) parseResource( "compilationUnit",
                                                               "forall.drl" );

        assertEquals( 1,
                      pkg.getRules().size() );
        final RuleDescr rule = (RuleDescr) pkg.getRules().get( 0 );
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

    public void testForCE() throws Exception {
        final PackageDescr pkg = (PackageDescr) parseResource( "compilationUnit",
                                                               "forCE.drl" );
        //
        //        final PackageDescr pkg = walker.getPackageDescr();
        //        assertEquals( 1,
        //                      pkg.getRules().size() );
        //        final RuleDescr rule = (RuleDescr) pkg.getRules().get( 0 );
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
    }

    public void testForallWithFrom() throws Exception {
        final PackageDescr pkg = (PackageDescr) parseResource( "compilationUnit",
                                                               "forallwithfrom.drl" );

        assertEquals( 1,
                      pkg.getRules().size() );
        final RuleDescr rule = (RuleDescr) pkg.getRules().get( 0 );
        assertEquals( 1,
                      rule.getLhs().getDescrs().size() );

        final ForallDescr forall = (ForallDescr) rule.getLhs().getDescrs().get( 0 );

        assertEquals( 2,
                      forall.getDescrs().size() );
        final PatternDescr pattern = forall.getBasePattern();
        assertEquals( "Person",
                      pattern.getObjectType() );
        assertEquals( "$village",
                      ((FromDescr) pattern.getSource()).getDataSource().toString() );
        final List remaining = forall.getRemainingPatterns();
        assertEquals( 1,
                      remaining.size() );
        final PatternDescr cheese = (PatternDescr) remaining.get( 0 );
        assertEquals( "Cheese",
                      cheese.getObjectType() );
        assertEquals( "$cheesery",
                      ((FromDescr) cheese.getSource()).getDataSource().toString() );
    }

    public void testMemberof() throws Exception {
        final String text = "rule X when Country( $cities : city )\nPerson( city memberOf $cities )\n then end";
        AndDescr descrs = ((RuleDescr) parse( "rule",
                                              text )).getLhs();

        assertEquals( 2,
                      descrs.getDescrs().size() );
        PatternDescr pat = (PatternDescr) descrs.getDescrs().get( 1 );
        ExprConstraintDescr fieldConstr = (ExprConstraintDescr) pat.getConstraint().getDescrs().get( 0 );

        assertEquals( "city memberOf $cities",
                      fieldConstr.getExpression() );
    }

    public void testNotMemberof() throws Exception {
        final String text = "rule X when Country( $cities : city )\nPerson( city not memberOf $cities ) then end\n";
        AndDescr descrs = ((RuleDescr) parse( "rule",
                                              text )).getLhs();

        assertEquals( 2,
                      descrs.getDescrs().size() );
        PatternDescr pat = (PatternDescr) descrs.getDescrs().get( 1 );
        ExprConstraintDescr fieldConstr = (ExprConstraintDescr) pat.getConstraint().getDescrs().get( 0 );

        assertEquals( "city not memberOf $cities",
                      fieldConstr.getExpression() );
    }

    public void testInOperator() throws Exception {
        final RuleDescr rule = (RuleDescr) parseResource( "rule",
                                                          "in_operator_test.drl" );

        assertNotNull( rule );

        assertEqualsIgnoreWhitespace( "consequence();",
                                      (String) rule.getConsequence() );
        assertEquals( "simple_rule",
                      rule.getName() );
        assertEquals( 2,
                      rule.getLhs().getDescrs().size() );

        // The first pattern, with 2 restrictions on a single field (plus a
        // connective)
        PatternDescr pattern = (PatternDescr) rule.getLhs().getDescrs().get( 0 );
        assertEquals( "Person",
                      pattern.getObjectType() );
        assertEquals( 1,
                      pattern.getConstraint().getDescrs().size() );

        ExprConstraintDescr fld = (ExprConstraintDescr) pattern.getConstraint().getDescrs().get( 0 );
        assertEquals( "age > 30 && < 40",
                      fld.getExpression() );

        // the second col, with 2 fields, the first with 2 restrictions, the
        // second field with one
        pattern = (PatternDescr) rule.getLhs().getDescrs().get( 1 );
        assertEquals( "Vehicle",
                      pattern.getObjectType() );
        assertEquals( 2,
                      pattern.getConstraint().getDescrs().size() );

        fld = (ExprConstraintDescr) pattern.getConstraint().getDescrs().get( 0 );
        assertEquals( "type in ( \"sedan\", \"wagon\" )",
                      fld.getExpression() );

        // now the second field
        fld = (ExprConstraintDescr) pattern.getConstraint().getDescrs().get( 1 );
        assertEquals( "age < 3",
                      fld.getExpression() );

    }

    public void testNotInOperator() throws Exception {
        final RuleDescr rule = (RuleDescr) parseResource( "rule",
                                                          "notin_operator_test.drl" );

        assertNotNull( rule );

        assertEqualsIgnoreWhitespace( "consequence();",
                                      (String) rule.getConsequence() );
        assertEquals( "simple_rule",
                      rule.getName() );
        assertEquals( 2,
                      rule.getLhs().getDescrs().size() );

        // The first pattern, with 2 restrictions on a single field (plus a
        // connective)
        PatternDescr pattern = (PatternDescr) rule.getLhs().getDescrs().get( 0 );
        assertEquals( "Person",
                      pattern.getObjectType() );
        assertEquals( 1,
                      pattern.getConstraint().getDescrs().size() );

        ExprConstraintDescr fld = (ExprConstraintDescr) pattern.getConstraint().getDescrs().get( 0 );
        assertEquals( "age > 30 && < 40",
                      fld.getExpression() );

        // the second col, with 2 fields, the first with 2 restrictions, the
        // second field with one
        pattern = (PatternDescr) rule.getLhs().getDescrs().get( 1 );
        assertEquals( "Vehicle",
                      pattern.getObjectType() );
        assertEquals( 2,
                      pattern.getConstraint().getDescrs().size() );

        fld = (ExprConstraintDescr) pattern.getConstraint().getDescrs().get( 0 );
        assertEquals( "type not in ( \"sedan\", \"wagon\" )",
                      fld.getExpression() );

        // now the second field
        fld = (ExprConstraintDescr) pattern.getConstraint().getDescrs().get( 1 );
        assertEquals( "age < 3",
                      fld.getExpression() );

    }

    public void testCheckOrDescr() throws Exception {
        final String text = "rule X when Person( eval( age == 25 ) || ( eval( name.equals( \"bob\" ) ) && eval( age == 30 ) ) ) then end";
        PatternDescr pattern = (PatternDescr) ((RuleDescr) parse( "rule",
                                                                  text )).getLhs().getDescrs().get( 0 );

        assertEquals( 1,
                      pattern.getDescrs().size() );
        assertEquals( pattern.getConstraint().getClass(),
                      org.drools.lang.descr.AndDescr.class );

        assertEquals( org.drools.lang.descr.ExprConstraintDescr.class,
                      pattern.getConstraint().getDescrs().get( 0 ).getClass() );

    }

    public void testConstraintAndConnective() throws Exception {
        final String text = "rule X when Person( age < 42 && location==\"atlanta\") then end";
        PatternDescr pattern = (PatternDescr) ((RuleDescr) parse( "rule",
                                                                  text )).getLhs().getDescrs().get( 0 );

        assertEquals( 1,
                      pattern.getDescrs().size() );
        ExprConstraintDescr fcd = (ExprConstraintDescr) pattern.getDescrs().get( 0 );
        assertEquals( "age < 42 && location==\"atlanta\"",
                      fcd.getExpression() );
    }

    public void testConstraintOrConnective() throws Exception {
        final String text = "rule X when Person( age < 42 || location==\"atlanta\") then end";
        PatternDescr pattern = (PatternDescr) ((RuleDescr) parse( "rule",
                                                                  text )).getLhs().getDescrs().get( 0 );

        assertEquals( 1,
                      pattern.getDescrs().size() );
        ExprConstraintDescr fcd = (ExprConstraintDescr) pattern.getDescrs().get( 0 );
        assertEquals( "age < 42 || location==\"atlanta\"",
                      fcd.getExpression() );
    }

    public void testRestrictions() throws Exception {
        final String text = "rule X when Foo( bar > 1 || == 1 ) then end\n";

        AndDescr descrs = (AndDescr) ((RuleDescr) parse( "rule",
                                                         text )).getLhs();

        assertEquals( 1,
                      descrs.getDescrs().size() );
        PatternDescr pat = (PatternDescr) descrs.getDescrs().get( 0 );
        ExprConstraintDescr fieldConstr = (ExprConstraintDescr) pat.getConstraint().getDescrs().get( 0 );

        assertEquals( "bar > 1 || == 1",
                      fieldConstr.getExpression() );
    }

    public void testSemicolon() throws Exception {
        final PackageDescr pkg = (PackageDescr) parseResource( "compilationUnit",
                                                               "semicolon.drl" );

        assertFalse( parser.getErrors().toString(),
                     parser.hasErrors() );

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

    public void testEval() throws Exception {
        final PackageDescr pkg = (PackageDescr) parseResource( "compilationUnit",
                                                               "eval_parsing.drl" );

        assertEquals( "org.drools",
                      pkg.getName() );
        assertEquals( 1,
                      pkg.getRules().size() );

        final RuleDescr rule1 = (RuleDescr) pkg.getRules().get( 0 );
        assertEquals( 1,
                      rule1.getLhs().getDescrs().size() );
    }

    public void testAccumulateReverse() throws Exception {
        final PackageDescr pkg = (PackageDescr) parseResource( "compilationUnit",
                                                               "accumulateReverse.drl" );

        assertEquals( 1,
                      pkg.getRules().size() );
        final RuleDescr rule = (RuleDescr) pkg.getRules().get( 0 );
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

    public void testAccumulateExternalFunction() throws Exception {
        final PackageDescr pkg = (PackageDescr) parseResource( "compilationUnit",
                                                               "accumulateExternalFunction.drl" );

        assertEquals( 1,
                      pkg.getRules().size() );
        final RuleDescr rule = (RuleDescr) pkg.getRules().get( 0 );
        assertEquals( 1,
                      rule.getLhs().getDescrs().size() );

        final PatternDescr out = (PatternDescr) rule.getLhs().getDescrs().get( 0 );
        final AccumulateDescr accum = (AccumulateDescr) out.getSource();
        assertEqualsIgnoreWhitespace( "$age",
                                      accum.getFunctions().get( 0 ).getParams()[0] );
        assertEqualsIgnoreWhitespace( "average",
                                      accum.getFunctions().get( 0 ).getFunction() );
        assertTrue( accum.isExternalFunction() );

        final PatternDescr pattern = (PatternDescr) accum.getInputPattern();
        assertEquals( "Person",
                      pattern.getObjectType() );
    }

    public void testCollectWithNestedFrom() throws Exception {
        final PackageDescr pkg = (PackageDescr) parseResource( "compilationUnit",
                                                               "collect_with_nested_from.drl" );

        assertEquals( 1,
                      pkg.getRules().size() );
        final RuleDescr rule = (RuleDescr) pkg.getRules().get( 0 );
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

    public void testAccumulateWithNestedFrom() throws Exception {
        final PackageDescr pkg = (PackageDescr) parseResource( "compilationUnit",
                                                               "accumulate_with_nested_from.drl" );

        assertEquals( 1,
                      pkg.getRules().size() );
        final RuleDescr rule = (RuleDescr) pkg.getRules().get( 0 );
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

    public void testOrCE() throws Exception {
        final PackageDescr pkg = (PackageDescr) parseResource( "compilationUnit",
                                                               "or_ce.drl" );

        assertEquals( 1,
                      pkg.getRules().size() );
        final RuleDescr rule = (RuleDescr) pkg.getRules().get( 0 );
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

    public void testRuleSingleLine() throws Exception {
        final String text = "rule \"another test\" salience 10 when eval( true ) then System.out.println(1); end";
        RuleDescr rule = (RuleDescr) parse( "rule",
                                            text );

        assertEquals( "another test",
                      rule.getName() );
        assertEquals( "System.out.println(1); ",
                      rule.getConsequence() );
    }

    public void testRuleTwoLines() throws Exception {
        final String text = "rule \"another test\" salience 10 when eval( true ) then System.out.println(1);\n end";
        RuleDescr rule = (RuleDescr) parse( "rule",
                                            text );

        assertEquals( "another test",
                      rule.getName() );
        assertEquals( "System.out.println(1);\n ",
                      rule.getConsequence() );
    }

    public void testRuleParseLhs3() throws Exception {
        final String text = "rule X when (or\nnot Person()\n(and Cheese()\nMeat()\nWine())) then end";
        AndDescr pattern = ((RuleDescr) parse( "rule",
                                               text )).getLhs();

        assertEquals( 1,
                      pattern.getDescrs().size() );
        OrDescr or = (OrDescr) pattern.getDescrs().get( 0 );
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

    public void testAccumulateMultiPattern() throws Exception {
        final PackageDescr pkg = (PackageDescr) parseResource( "compilationUnit",
                                                               "accumulate_multi_pattern.drl" );

        assertEquals( 1,
                      pkg.getRules().size() );
        final RuleDescr rule = (RuleDescr) pkg.getRules().get( 0 );
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

    public void testPluggableOperators() throws Exception {

        final PackageDescr pkg = (PackageDescr) parseResource( "compilationUnit",
                                                               "pluggable_operators.drl" );

        assertFalse( parser.getErrors().toString(),
                     parser.hasErrors() );

        assertEquals( 1,
                      pkg.getRules().size() );
        final RuleDescr rule = (RuleDescr) pkg.getRules().get( 0 );
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
        assertEquals( 1,
                      eventB.getConstraint().getDescrs().size() );

        final ExprConstraintDescr fcdB = (ExprConstraintDescr) eventB.getConstraint().getDescrs().get( 0 );
        assertEquals( "this after[1,10] $a || this not after[15,20] $a",
                      fcdB.getExpression() );

        final PatternDescr eventC = (PatternDescr) rule.getLhs().getDescrs().get( 2 );
        assertEquals( "$c",
                      eventC.getIdentifier() );
        assertEquals( "EventC",
                      eventC.getObjectType() );
        assertEquals( 1,
                      eventC.getConstraint().getDescrs().size() );
        final ExprConstraintDescr fcdC = (ExprConstraintDescr) eventC.getConstraint().getDescrs().get( 0 );
        assertEquals( "this finishes $b",
                      fcdC.getExpression() );

        final PatternDescr eventD = (PatternDescr) rule.getLhs().getDescrs().get( 3 );
        assertEquals( "$d",
                      eventD.getIdentifier() );
        assertEquals( "EventD",
                      eventD.getObjectType() );
        assertEquals( 1,
                      eventD.getConstraint().getDescrs().size() );
        final ExprConstraintDescr fcdD = (ExprConstraintDescr) eventD.getConstraint().getDescrs().get( 0 );
        assertEquals( "this not starts $a",
                      fcdD.getExpression() );

        final PatternDescr eventE = (PatternDescr) rule.getLhs().getDescrs().get( 4 );
        assertEquals( "$e",
                      eventE.getIdentifier() );
        assertEquals( "EventE",
                      eventE.getObjectType() );
        assertEquals( 1,
                      eventE.getConstraint().getDescrs().size() );

        ExprConstraintDescr fcdE = (ExprConstraintDescr) eventE.getConstraint().getDescrs().get( 0 );
        assertEquals( "this not before[1, 10] $b || after[1, 10] $c && this after[1, 5] $d",
                      fcdE.getExpression() );
    }

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
        assertEquals( "\"value\"",
                          descr.getAnnotation( "name2" ).getValue() );
        assertEquals( "10",
                          descr.getAnnotation( "name3" ).getValue( "k1" ) );
        assertEquals( "\"a\"",
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
        assertEquals( "\"Models a person\"",
                      type.getAnnotation( "doc" ).getValue( "descr" ) );
        assertEquals( "\"Bob\"",
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

    public void testRuleWithLHSNesting() throws Exception {
        final PackageDescr pkg = (PackageDescr) parseResource( "compilationUnit",
                                                               "Rule_with_nested_LHS.drl" );

        assertFalse( parser.getErrors().toString(),
                     parser.hasErrors() );

        RuleDescr rule = pkg.getRules().get( 0 );
        assertEquals( "test",
                      rule.getName() );

        AndDescr lhs = rule.getLhs();
        assertNotNull( lhs );
        assertEquals( 2,
                      lhs.getDescrs().size() );

        PatternDescr a = (PatternDescr) lhs.getDescrs().get( 0 );
        assertEquals( "A",
                      a.getObjectType() );

        OrDescr or = (OrDescr) lhs.getDescrs().get( 1 );
        assertEquals( 3,
                      or.getDescrs().size() );

        AndDescr and1 = (AndDescr) or.getDescrs().get( 0 );
        assertEquals( 2,
                      and1.getDescrs().size() );
        PatternDescr b = (PatternDescr) and1.getDescrs().get( 0 );
        PatternDescr c = (PatternDescr) and1.getDescrs().get( 1 );
        assertEquals( "B",
                      b.getObjectType() );
        assertEquals( "C",
                      c.getObjectType() );

        AndDescr and2 = (AndDescr) or.getDescrs().get( 1 );
        assertEquals( 2,
                      and2.getDescrs().size() );
        PatternDescr d = (PatternDescr) and2.getDescrs().get( 0 );
        PatternDescr e = (PatternDescr) and2.getDescrs().get( 1 );
        assertEquals( "D",
                      d.getObjectType() );
        assertEquals( "E",
                      e.getObjectType() );

        AndDescr and3 = (AndDescr) or.getDescrs().get( 2 );
        assertEquals( 2,
                      and3.getDescrs().size() );
        PatternDescr f = (PatternDescr) and3.getDescrs().get( 0 );
        PatternDescr g = (PatternDescr) and3.getDescrs().get( 1 );
        assertEquals( "F",
                      f.getObjectType() );
        assertEquals( "G",
                      g.getObjectType() );
    }

    public void testEntryPoint() throws Exception {
        final String text = "rule X when StockTick( symbol==\"ACME\") from entry-point StreamA then end";

        PackageDescr pkg = (PackageDescr) parse( "compilationUnit",
                                                     text );
        assertFalse( parser.getErrors().toString(),
                     parser.hasErrors() );

        RuleDescr rule = pkg.getRules().get( 0 );
        PatternDescr pattern = (PatternDescr) rule.getLhs().getDescrs().get( 0 );

        assertEquals( 1,
                      pattern.getDescrs().size() );
        ExprConstraintDescr fcd = (ExprConstraintDescr) pattern.getDescrs().get( 0 );
        assertEquals( "symbol==\"ACME\"",
                      fcd.getExpression() );

        assertNotNull( pattern.getSource() );
        EntryPointDescr entry = (EntryPointDescr) pattern.getSource();
        assertEquals( "StreamA",
                      entry.getEntryId() );
    }

    public void testEntryPoint2() throws Exception {
        final String text = "rule X when StockTick( symbol==\"ACME\") from entry-point \"StreamA\" then end";

        PackageDescr pkg = (PackageDescr) parse( "compilationUnit",
                                                     text );
        assertFalse( parser.getErrors().toString(),
                     parser.hasErrors() );

        RuleDescr rule = pkg.getRules().get( 0 );
        PatternDescr pattern = (PatternDescr) rule.getLhs().getDescrs().get( 0 );

        assertEquals( 1,
                      pattern.getDescrs().size() );
        ExprConstraintDescr fcd = (ExprConstraintDescr) pattern.getDescrs().get( 0 );
        assertEquals( "symbol==\"ACME\"",
                      fcd.getExpression() );

        assertNotNull( pattern.getSource() );
        EntryPointDescr entry = (EntryPointDescr) pattern.getSource();
        assertEquals( "StreamA",
                      entry.getEntryId() );
    }

    public void testSlidingWindow() throws Exception {
        final String text = "rule X when StockTick( symbol==\"ACME\") over window:length(10) then end";

        PackageDescr pkg = (PackageDescr) parse( "compilationUnit",
                                                 text );
        assertFalse( parser.getErrors().toString(),
                     parser.hasErrors() );

        RuleDescr rule = pkg.getRules().get( 0 );
        PatternDescr pattern = (PatternDescr) rule.getLhs().getDescrs().get( 0 );

        assertEquals( 1,
                      pattern.getDescrs().size() );
        ExprConstraintDescr fcd = (ExprConstraintDescr) pattern.getDescrs().get( 0 );
        assertEquals( "symbol==\"ACME\"",
                      fcd.getExpression() );

        List<BehaviorDescr> behaviors = pattern.getBehaviors();
        assertNotNull( behaviors );
        assertEquals( 1,
                      behaviors.size() );
        BehaviorDescr descr = behaviors.get( 0 );
        assertEquals( "window",
                      descr.getType() );
        assertEquals( "length",
                      descr.getSubType() );
        assertEquals( "10",
                      descr.getParameters().get( 0 ) );
    }

    public void testRuleOldSyntax1() throws Exception {
        final String source = "rule \"Test\" when ( not $r :LiteralRestriction( operator == Operator.EQUAL ) ) then end";

        PackageDescr pkg = (PackageDescr) parse( "compilationUnit",
                                                 source );

        assertFalse( parser.getErrors().toString(),
                     parser.hasErrors() );
        RuleDescr rule = (RuleDescr) pkg.getRules().get( 0 );

        assertEquals( "Test",
                      rule.getName() );
        assertEquals( 1,
                      rule.getLhs().getDescrs().size() );
        assertEquals( 1,
                      ((NotDescr) rule.getLhs().getDescrs().get( 0 )).getDescrs().size() );
        NotDescr notDescr = (NotDescr) rule.getLhs().getDescrs().get( 0 );
        PatternDescr patternDescr = (PatternDescr) notDescr.getDescrs().get( 0 );
        assertEquals( "$r",
                      patternDescr.getIdentifier() );
        assertEquals( 1,
                      patternDescr.getDescrs().size() );
        ExprConstraintDescr fieldConstraintDescr = (ExprConstraintDescr) patternDescr.getDescrs().get( 0 );
        assertEquals( "operator == Operator.EQUAL",
                      fieldConstraintDescr.getExpression() );
    }

    public void testRuleOldSyntax2() throws Exception {
        final String source = "rule \"Test\" when ( $r :LiteralRestriction( operator == Operator.EQUAL ) ) then end";

        PackageDescr pkg = (PackageDescr) parse( "compilationUnit",
                                                 source );

        assertFalse( parser.getErrors().toString(),
                     parser.hasErrors() );
        RuleDescr rule = (RuleDescr) pkg.getRules().get( 0 );

        assertEquals( "Test",
                      rule.getName() );
        assertEquals( 1,
                      rule.getLhs().getDescrs().size() );
        PatternDescr patternDescr = (PatternDescr) rule.getLhs().getDescrs().get( 0 );
        assertEquals( "$r",
                      patternDescr.getIdentifier() );
        assertEquals( 1,
                      patternDescr.getDescrs().size() );
        ExprConstraintDescr fieldConstraintDescr = (ExprConstraintDescr) patternDescr.getDescrs().get( 0 );
        assertEquals( "operator == Operator.EQUAL",
                      fieldConstraintDescr.getExpression() );
    }

    public void testTypeWithMetaData() throws Exception {

        PackageDescr pkg = (PackageDescr) parseResource( "compilationUnit",
                                                         "type_with_meta.drl" );

        assertFalse( parser.getErrors().toString(),
                     parser.hasErrors() );

        final List<TypeDeclarationDescr> declarations = pkg.getTypeDeclarations();

        assertEquals( 3,
                      declarations.size() );
    }

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
            createParser( charStream );
            /** Use Reflection to get rule method from parser */
            Method ruleName = DRLParser.class.getMethod( testRuleName );

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

    private void createParser( CharStream charStream ) {
        DRLLexer lexer = new DRLLexer( charStream );
        CommonTokenStream tokens = new CommonTokenStream( lexer );
        parser = new DRLParser( tokens );
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

    private Reader getReader( final String name ) throws Exception {
        final InputStream in = getClass().getResourceAsStream( name );
        return new InputStreamReader( in );
    }

}
