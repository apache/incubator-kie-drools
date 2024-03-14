/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.drools.mvel.compiler.lang;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import org.antlr.runtime.ANTLRStringStream;
import org.antlr.runtime.CharStream;
import org.drools.compiler.builder.impl.EvaluatorRegistry;
import org.drools.drl.ast.descr.AccumulateDescr;
import org.drools.drl.ast.descr.AccumulateImportDescr;
import org.drools.drl.ast.descr.AndDescr;
import org.drools.drl.ast.descr.AnnotationDescr;
import org.drools.drl.ast.descr.AttributeDescr;
import org.drools.drl.ast.descr.BaseDescr;
import org.drools.drl.ast.descr.BehaviorDescr;
import org.drools.drl.ast.descr.CollectDescr;
import org.drools.drl.ast.descr.EntryPointDeclarationDescr;
import org.drools.drl.ast.descr.EntryPointDescr;
import org.drools.drl.ast.descr.EvalDescr;
import org.drools.drl.ast.descr.ExistsDescr;
import org.drools.drl.ast.descr.ExprConstraintDescr;
import org.drools.drl.ast.descr.ForallDescr;
import org.drools.drl.ast.descr.FromDescr;
import org.drools.drl.ast.descr.FunctionDescr;
import org.drools.drl.ast.descr.GlobalDescr;
import org.drools.drl.ast.descr.GroupByDescr;
import org.drools.drl.ast.descr.ImportDescr;
import org.drools.drl.ast.descr.MVELExprDescr;
import org.drools.drl.ast.descr.NotDescr;
import org.drools.drl.ast.descr.OrDescr;
import org.drools.drl.ast.descr.PackageDescr;
import org.drools.drl.ast.descr.PatternDescr;
import org.drools.drl.ast.descr.QueryDescr;
import org.drools.drl.ast.descr.RuleDescr;
import org.drools.drl.ast.descr.TypeDeclarationDescr;
import org.drools.drl.ast.descr.TypeFieldDescr;
import org.drools.drl.ast.descr.WindowDeclarationDescr;
import org.drools.drl.parser.DrlParser;
import org.drools.drl.parser.lang.DRL6Lexer;
import org.drools.drl.parser.lang.DRL6Parser;
import org.drools.drl.parser.lang.DRLParser;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.kie.internal.builder.conf.LanguageLevelOption;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;
import static org.drools.drl.parser.DRLFactory.buildParser;

public class RuleParserTest {


    private DRLParser parser;

    @Before
    public void setUp() throws Exception {
        // initializes pluggable operators
        new EvaluatorRegistry();
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void testPackage_OneSegment() throws Exception {
        final String packageName = (String) parse( "packageStatement",
                                                   "package foo" );
        assertThat(packageName).isEqualTo("foo");
    }

    @Test
    public void testPackage_MultipleSegments() throws Exception {
        final String packageName = (String) parse( "packageStatement",
                                                   "package foo.bar.baz;" );
        assertThat(packageName).isEqualTo("foo.bar.baz");
    }

    @Test
    public void testPackage() throws Exception {
        final String source = "package foo.bar.baz";
        final DrlParser parser = new DrlParser(LanguageLevelOption.DRL6);
        final PackageDescr pkg = parser.parse( new StringReader( source ) );
        assertThat(parser.hasErrors()).isFalse();
        assertThat(pkg.getName()).isEqualTo("foo.bar.baz");
    }

    @Test
    public void testPackageWithError() throws Exception {
        final String source = "package 12 foo.bar.baz";
        final DrlParser parser = new DrlParser(LanguageLevelOption.DRL6);
        final PackageDescr pkg = parser.parse( true,
                                               new StringReader( source ) );
        assertThat(parser.hasErrors()).isTrue();
        assertThat(pkg.getName()).isEqualTo("foo.bar.baz");
    }

    @Test
    public void testPackageWithError2() throws Exception {
        final String source = "package 12 12312 231";
        final DrlParser parser = new DrlParser(LanguageLevelOption.DRL6);
        final PackageDescr pkg = parser.parse( true,
                                               new StringReader( source ) );
        assertThat(parser.hasErrors()).isTrue();
        assertThat(pkg.getName()).isEqualTo("");
    }

    @Test
    public void testCompilationUnit() throws Exception {
        final String source = "package foo; import com.foo.Bar; import com.foo.Baz;";
        PackageDescr pkg = (PackageDescr) parse( "compilationUnit",
                                                 source );
        assertThat(parser.hasErrors()).as(parser.getErrors().toString()).isFalse();
        assertThat(pkg.getName()).isEqualTo("foo");
        assertThat(pkg.getImports().size()).isEqualTo(2);
        ImportDescr impdescr = pkg.getImports().get( 0 );
        assertThat(impdescr.getTarget()).isEqualTo("com.foo.Bar");
        assertThat(impdescr.getStartCharacter()).isEqualTo(source.indexOf("import " + impdescr.getTarget()));
        assertThat(impdescr.getEndCharacter()).isEqualTo(source.indexOf("import " + impdescr.getTarget()) + ("import " + impdescr.getTarget()).length());

        impdescr = pkg.getImports().get( 1 );
        assertThat(impdescr.getTarget()).isEqualTo("com.foo.Baz");
        assertThat(impdescr.getStartCharacter()).isEqualTo(source.indexOf("import " + impdescr.getTarget()));
        assertThat(impdescr.getEndCharacter()).isEqualTo(source.indexOf("import " + impdescr.getTarget()) + ("import " + impdescr.getTarget()).length());
    }

    @Test
    public void testFunctionImport() throws Exception {
        final String source = "package foo\n" +
                              "import function java.lang.Math.max\n" +
                              "import function java.lang.Math.min;\n" +
                              "import foo.bar.*\n" +
                              "import baz.Baz";
        PackageDescr pkg = (PackageDescr) parse( "compilationUnit",
                                                 source );
        assertThat(parser.hasErrors()).as(parser.getErrors().toString()).isFalse();
        assertThat(pkg.getName()).isEqualTo("foo");
        assertThat(pkg.getImports().size()).isEqualTo(2);
        ImportDescr impdescr = pkg.getImports().get( 0 );
        assertThat(impdescr.getTarget()).isEqualTo("foo.bar.*");
        assertThat(impdescr.getStartCharacter()).isEqualTo(source.indexOf("import " + impdescr.getTarget()));
        assertThat(impdescr.getEndCharacter()).isEqualTo(source.indexOf("import " + impdescr.getTarget()) + ("import " + impdescr.getTarget()).length());

        impdescr = pkg.getImports().get( 1 );
        assertThat(impdescr.getTarget()).isEqualTo("baz.Baz");
        assertThat(impdescr.getStartCharacter()).isEqualTo(source.indexOf("import " + impdescr.getTarget()));
        assertThat(impdescr.getEndCharacter()).isEqualTo(source.indexOf("import " + impdescr.getTarget()) + ("import " + impdescr.getTarget()).length());

        assertThat(pkg.getFunctionImports().size()).isEqualTo(2);
        impdescr = pkg.getFunctionImports().get( 0 );
        assertThat(impdescr.getTarget()).isEqualTo("java.lang.Math.max");
        assertThat(impdescr.getStartCharacter()).isEqualTo(source.indexOf("import function " + impdescr.getTarget()));
        assertThat(impdescr.getEndCharacter()).isEqualTo(source.indexOf("import function " + impdescr.getTarget()) + ("import function " + impdescr.getTarget()).length());

        impdescr = pkg.getFunctionImports().get( 1 );
        assertThat(impdescr.getTarget()).isEqualTo("java.lang.Math.min");
        assertThat(impdescr.getStartCharacter()).isEqualTo(source.indexOf("import function " + impdescr.getTarget()));
        assertThat(impdescr.getEndCharacter()).isEqualTo(source.indexOf("import function " + impdescr.getTarget()) + ("import function " + impdescr.getTarget()).length());

    }

    @Test
    public void testGlobal1() throws Exception {
        final String source = "package foo.bar.baz\n" +
                              "import com.foo.Bar\n" +
                              "global java.util.List<java.util.Map<String,Integer>> aList;\n" +
                              "global Integer aNumber";
        PackageDescr pkg = (PackageDescr) parse( "compilationUnit",
                                                 source );
        assertThat(parser.hasErrors()).as(parser.getErrors().toString()).isFalse();
        assertThat(pkg.getName()).isEqualTo("foo.bar.baz");
        assertThat(pkg.getImports().size()).isEqualTo(1);

        ImportDescr impdescr = pkg.getImports().get( 0 );
        assertThat(impdescr.getTarget()).isEqualTo("com.foo.Bar");
        assertThat(impdescr.getStartCharacter()).isEqualTo(source.indexOf("import " + impdescr.getTarget()));
        assertThat(impdescr.getEndCharacter()).isEqualTo(source.indexOf("import " + impdescr.getTarget()) + ("import " + impdescr.getTarget()).length());

        assertThat(pkg.getGlobals().size()).isEqualTo(2);

        GlobalDescr global = pkg.getGlobals().get( 0 );
        assertThat(global.getType()).isEqualTo("java.util.List<java.util.Map<String,Integer>>");
        assertThat(global.getIdentifier()).isEqualTo("aList");
        assertThat(global.getStartCharacter()).isEqualTo(source.indexOf("global " + global.getType()));
        assertThat(global.getEndCharacter()).isEqualTo(source.indexOf("global " + global.getType() + " " + global.getIdentifier()) +
                ("global " + global.getType() + " " + global.getIdentifier()).length());

        global = pkg.getGlobals().get( 1 );
        assertThat(global.getType()).isEqualTo("Integer");
        assertThat(global.getIdentifier()).isEqualTo("aNumber");
        assertThat(global.getStartCharacter()).isEqualTo(source.indexOf("global " + global.getType()));
        assertThat(global.getEndCharacter()).isEqualTo(source.indexOf("global " + global.getType() + " " + global.getIdentifier()) +
                ("global " + global.getType() + " " + global.getIdentifier()).length());
    }

    @Test
    public void testGlobal() throws Exception {
        PackageDescr pack = (PackageDescr) parseResource( "compilationUnit",
                                                          "globals.drl" );

        assertThat(pack.getRules().size()).isEqualTo(1);

        final RuleDescr rule = pack.getRules().get(0);
        assertThat(rule.getLhs().getDescrs().size()).isEqualTo(1);

        assertThat(pack.getImports().size()).isEqualTo(1);
        assertThat(pack.getGlobals().size()).isEqualTo(2);

        final GlobalDescr foo = pack.getGlobals().get(0);
        assertThat(foo.getType()).isEqualTo("java.lang.String");
        assertThat(foo.getIdentifier()).isEqualTo("foo");
        final GlobalDescr bar = pack.getGlobals().get(1);
        assertThat(bar.getType()).isEqualTo("java.lang.Integer");
        assertThat(bar.getIdentifier()).isEqualTo("bar");
    }

    @Test
    public void testFunctionImport2() throws Exception {
        PackageDescr pkg = (PackageDescr) parseResource( "compilationUnit",
                                                         "test_FunctionImport.drl" );

        assertThat(pkg.getFunctionImports().size()).isEqualTo(2);

        assertThat(pkg.getFunctionImports().get(0).getTarget()).isEqualTo("abd.def.x");
        assertThat(pkg.getFunctionImports().get(0).getStartCharacter() == -1).isFalse();
        assertThat(pkg.getFunctionImports().get(0).getEndCharacter() == -1).isFalse();
        assertThat(pkg.getFunctionImports().get(1).getTarget()).isEqualTo("qed.wah.*");
        assertThat(pkg.getFunctionImports().get(1).getStartCharacter() == -1).isFalse();
        assertThat(pkg.getFunctionImports().get(1).getEndCharacter() == -1).isFalse();
    }

    @Test
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

        assertThat(parser.hasErrors()).as(parser.getErrorMessages().toString()).isFalse();

        RuleDescr rule = pkg.getRules().get(0);
        assertThat(rule.getName()).isEqualTo("Invalid customer id");

        assertThat(rule.getLhs().getDescrs().size()).isEqualTo(2);

        NotDescr not = (NotDescr) rule.getLhs().getDescrs().get( 1 );
        PatternDescr customer = (PatternDescr) not.getDescrs().get( 0 );

        assertThat(customer.getObjectType()).isEqualTo("Customer");
        assertThat(((FromDescr) customer.getSource()).getDataSource().getText()).isEqualTo("customerService.getCustomer(o.getCustomerId())");

    }

    @Test
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
        assertThat(parser.hasErrors()).as(parser.getErrors().toString()).isFalse();

        RuleDescr rule = pkg.getRules().get(0);
        assertThat(rule.getName()).isEqualTo("XYZ");

        PatternDescr number = (PatternDescr) ((NotDescr) rule.getLhs().getDescrs().get( 1 )).getDescrs().get( 0 );
        assertThat(((FromDescr) number.getSource()).getDataSource().toString()).isEqualTo("[1, 2, 3]");

    }

    @Test
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
        assertThat(parser.hasErrors()).as(parser.getErrors().toString()).isFalse();

        RuleDescr rule = pkg.getRules().get(0);
        assertThat(rule.getName()).isEqualTo("XYZ");

        assertThat(parser.hasErrors()).isFalse();
        PatternDescr number = (PatternDescr) rule.getLhs().getDescrs().get( 1 );
        assertThat(((FromDescr) number.getSource()).getDataSource().toString()).isEqualTo("[1, 2, 3].sublist(1, 2)");

    }

    @Test
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
        assertThat(parser.hasErrors()).as(parser.getErrors().toString()).isFalse();

        RuleDescr rule = pkg.getRules().get(0);
        assertThat(rule.getName()).isEqualTo("XYZ");

        assertThat(parser.hasErrors()).isFalse();
        PatternDescr number = (PatternDescr) rule.getLhs().getDescrs().get( 1 );
        assertThat(((FromDescr) number.getSource()).getDataSource().toString()).isEqualTo("[1, 2, 3][1]");
    }

    @Test
    public void testRuleWithoutEnd() throws Exception {
        String source = "rule \"Invalid customer id\" \n" +
                        " when \n" +
                        " o: Order( ) \n" +
                        " then \n" +
                        " System.err.println(\"Invalid customer id found!\"); \n";
        parse( "compilationUnit",
               source );
        assertThat(parser.hasErrors()).isTrue();

    }

    @Test
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
        assertThat(parser.hasErrors()).as(parser.getErrors().toString()).isFalse();

        RuleDescr rule = pkg.getRules().get( 0 );
        AndDescr lhs = rule.getLhs();
        assertThat(lhs.getDescrs().size()).isEqualTo(2);

        PatternDescr pdo1 = (PatternDescr) lhs.getDescrs().get( 0 );
        assertThat(pdo1.getIdentifier()).isEqualTo("pdo1");

        OrDescr or = (OrDescr) rule.getLhs().getDescrs().get( 1 );
        assertThat(or.getDescrs().size()).isEqualTo(3);
        for ( BaseDescr pdo2 : or.getDescrs() ) {
            assertThat(((PatternDescr) pdo2).getIdentifier()).isEqualTo("pdo2");
        }

    }

    @Test
    public void testCompatibleRestriction() throws Exception {
        String source = "package com.sample  rule test  when  Test( ( text == null || text2 matches \"\" ) )  then  end";
        PackageDescr pkg = (PackageDescr) parse( "compilationUnit",
                                                 source );
        assertThat(pkg.getName()).isEqualTo("com.sample");
        RuleDescr rule = pkg.getRules().get(0);
        assertThat(rule.getName()).isEqualTo("test");
        ExprConstraintDescr expr = (ExprConstraintDescr) ((PatternDescr) rule.getLhs().getDescrs().get( 0 )).getDescrs().get( 0 );
        assertThat(expr.getText()).isEqualTo("( text == null || text2 matches \"\" )");
    }

    @Test
    public void testSimpleConstraint() throws Exception {
        String source = "package com.sample  rule test  when  Cheese( type == 'stilton', price > 10 )  then  end";
        PackageDescr pkg = (PackageDescr) parse( "compilationUnit",
                                                 source );
        assertThat(pkg.getName()).isEqualTo("com.sample");
        RuleDescr rule = pkg.getRules().get(0);
        assertThat(rule.getName()).isEqualTo("test");

        assertThat(rule.getLhs().getDescrs().size()).isEqualTo(1);
        PatternDescr pattern = (PatternDescr) rule.getLhs().getDescrs().get( 0 );

        AndDescr constraint = (AndDescr) pattern.getConstraint();
        assertThat(constraint.getDescrs().size()).isEqualTo(2);
        assertThat(constraint.getDescrs().get(0).toString()).isEqualTo("type == \"stilton\"");
        assertThat(constraint.getDescrs().get(1).toString()).isEqualTo("price > 10");
    }

    @Test
    public void testStringEscapes() throws Exception {
        String source = "package com.sample  rule test  when  Cheese( type matches \"\\..*\\\\.\" )  then  end";
        PackageDescr pkg = (PackageDescr) parse( "compilationUnit",
                                                 source );
        assertThat(pkg.getName()).isEqualTo("com.sample");
        RuleDescr rule = pkg.getRules().get(0);
        assertThat(rule.getName()).isEqualTo("test");

        assertThat(rule.getLhs().getDescrs().size()).isEqualTo(1);
        PatternDescr pattern = (PatternDescr) rule.getLhs().getDescrs().get( 0 );

        AndDescr constraint = (AndDescr) pattern.getConstraint();
        assertThat(constraint.getDescrs().size()).isEqualTo(1);
        assertThat(constraint.getDescrs().get(0).toString()).isEqualTo("type matches \"\\..*\\\\.\"");
    }

    @Test
    public void testDialect() throws Exception {
        final String source = "dialect 'mvel'";
        PackageDescr pkg = (PackageDescr) parse( "compilationUnit",
                                                 source );
        AttributeDescr attr = pkg.getAttributes().get(0);
        assertThat(attr.getName()).isEqualTo("dialect");
        assertThat(attr.getValue()).isEqualTo("mvel");
    }

    @Test
    public void testDialect2() throws Exception {
        final String source = "dialect \"mvel\"";
        PackageDescr pkg = (PackageDescr) parse( "compilationUnit",
                                                     source );
        AttributeDescr attr = pkg.getAttributes().get( 0 );
        assertThat(attr.getName()).isEqualTo("dialect");
        assertThat(attr.getValue()).isEqualTo("mvel");
    }

    @Test
    public void testEmptyRule() throws Exception {
        final RuleDescr rule = (RuleDescr) parseResource( "rule",
                                                              "empty_rule.drl" );

        assertThat(rule).isNotNull();

        assertThat(rule.getName()).isEqualTo("empty");
        assertThat(rule.getLhs()).isNotNull();
        assertThat(rule.getConsequence()).isNotNull();
    }

    @Test
    public void testKeywordCollisions() throws Exception {
        PackageDescr pkg = (PackageDescr) parseResource( "compilationUnit",
                                                         "eol_funny_business.drl" );
        assertThat(parser.hasErrors()).as(parser.getErrors().toString()).isFalse();

        assertThat(pkg.getRules().size()).isEqualTo(1);
    }

    @Test
    public void testTernaryExpression() throws Exception {
        PackageDescr pkg = (PackageDescr) parseResource( "compilationUnit",
                                                         "ternary_expression.drl" );

        final RuleDescr rule = pkg.getRules().get(0);
        assertThat(pkg.getRules().size()).isEqualTo(1);

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
    @Test
    public void testFunctionWithArrays() throws Exception {
        PackageDescr pkg = (PackageDescr) parseResource( "compilationUnit",
                                                         "function_arrays.drl" );

        assertThat(pkg.getName()).isEqualTo("foo");
        assertThat(pkg.getRules().size()).isEqualTo(1);

        final RuleDescr rule = pkg.getRules().get(0);

        assertEqualsIgnoreWhitespace( "yourFunction(new String[3] {\"a\",\"b\",\"c\"});",
                                      (String) rule.getConsequence() );

        final FunctionDescr func = pkg.getFunctions().get(0);

        assertThat(func.getReturnType()).isEqualTo("String[]");
        assertThat(func.getParameterNames().get(0)).isEqualTo("args[]");
        assertThat(func.getParameterTypes().get(0)).isEqualTo("String");
    }

    @Test
    public void testAlmostEmptyRule() throws Exception {
        PackageDescr pkg = (PackageDescr) parseResource( "compilationUnit",
                                                         "almost_empty_rule.drl" );

        assertThat(parser.hasErrors()).as(parser.getErrors().toString()).isFalse();
        assertThat(pkg).isNotNull();

        RuleDescr rule = pkg.getRules().get( 0 );

        assertThat(rule.getName()).isEqualTo("almost_empty");
        assertThat(rule.getLhs()).isNotNull();
        assertThat(((String) rule.getConsequence()).trim()).isEqualTo("");
    }

    @Test
    public void testQuotedStringNameRule() throws Exception {
        final RuleDescr rule = (RuleDescr) parseResource( "rule",
                                                          "quoted_string_name_rule.drl" );

        assertThat(parser.hasErrors()).as(parser.getErrors().toString()).isFalse();
        assertThat(rule).isNotNull();

        assertThat(rule.getName()).isEqualTo("quoted string name");
        assertThat(rule.getLhs()).isNotNull();
        assertThat(((String) rule.getConsequence()).trim()).isEqualTo("");
    }

    @Test
    public void testNoLoop() throws Exception {
        final RuleDescr rule = (RuleDescr) parseResource( "rule",
                                                          "no-loop.drl" );

        assertThat(parser.hasErrors()).as(parser.getErrors().toString()).isFalse();
        assertThat(rule).isNotNull();

        assertThat(rule.getName()).isEqualTo("rule1");
        final AttributeDescr att = rule.getAttributes().get("no-loop");
        assertThat(att.getValue()).isEqualTo("false");
        assertThat(att.getName()).isEqualTo("no-loop");
    }

    @Test
    public void testAutofocus() throws Exception {
        final RuleDescr rule = (RuleDescr) parseResource( "rule",
                                                          "autofocus.drl" );

        assertThat(parser.hasErrors()).as(parser.getErrors().toString()).isFalse();

        assertThat(rule).isNotNull();

        assertThat(rule.getName()).isEqualTo("rule1");
        final AttributeDescr att = rule.getAttributes().get("auto-focus");
        assertThat(att.getValue()).isEqualTo("true");
        assertThat(att.getName()).isEqualTo("auto-focus");
    }

    @Test
    public void testRuleFlowGroup() throws Exception {
        final RuleDescr rule = (RuleDescr) parseResource( "rule",
                                                          "ruleflowgroup.drl" );

        assertThat(parser.hasErrors()).as(parser.getErrors().toString()).isFalse();

        assertThat(rule).isNotNull();

        assertThat(rule.getName()).isEqualTo("rule1");
        final AttributeDescr att = rule.getAttributes().get("ruleflow-group");
        assertThat(att.getValue()).isEqualTo("a group");
        assertThat(att.getName()).isEqualTo("ruleflow-group");
    }

    @Test
    public void testConsequenceWithDeclaration() throws Exception {
        final RuleDescr rule = (RuleDescr) parseResource( "rule",
                                                          "declaration-in-consequence.drl" );

        assertThat(parser.hasErrors()).as(parser.getErrors().toString()).isFalse();

        assertThat(rule).isNotNull();

        assertThat(rule.getName()).isEqualTo("myrule");

        final String expected = "int i = 0; i = 1; i / 1; i == 1; i(i); i = 'i'; i.i.i; i\\i; i<i; i>i; i=\"i\";  ++i;" + "i++; --i; i--; i += i; i -= i; i *= i; i /= i;" + "int i = 5;" + "for(int j; j<i; ++j) {" + "System.out.println(j);}"
                                    + "Object o = new String(\"Hello\");" + "String s = (String) o;";

        assertEqualsIgnoreWhitespace( expected,
                                          (String) rule.getConsequence() );
        assertThat(((String) rule.getConsequence()).indexOf("++") > 0).isTrue();
        assertThat(((String) rule.getConsequence()).indexOf("--") > 0).isTrue();
        assertThat(((String) rule.getConsequence()).indexOf("+=") > 0).isTrue();
        assertThat(((String) rule.getConsequence()).indexOf("==") > 0).isTrue();

        // System.out.println(( String ) rule.getConsequence());
        // note, need to assert that "i++" is preserved as is, no extra spaces.
    }

    @Test
    public void testRuleParseLhs() throws Exception {
        final String text = "rule X when Person(age < 42, location==\"atlanta\") \nor\nPerson(name==\"bob\") then end";
        RuleDescr rule = (RuleDescr) parse( "rule",
                                            text );
        assertThat(parser.hasErrors()).as(parser.getErrors().toString()).isFalse();

        assertThat(rule).isNotNull();

        AndDescr lhs = rule.getLhs();
        assertThat(lhs.getDescrs().size()).isEqualTo(1);
        assertThat(((OrDescr) lhs.getDescrs().get(0)).getDescrs().size()).isEqualTo(2);
    }

    @Test
    public void testRuleParseLhsWithStringQuotes() throws Exception {
        final String text = "rule X when Person( location==\"atlanta\\\"\") then end\n";
        RuleDescr rule = (RuleDescr) parse( "rule",
                                                text );
        assertThat(parser.hasErrors()).as(parser.getErrors().toString()).isFalse();

        assertThat(rule).isNotNull();

        AndDescr lhs = rule.getLhs();
        ExprConstraintDescr constr = (ExprConstraintDescr) ((PatternDescr) lhs.getDescrs().get( 0 )).getDescrs().get( 0 );

        assertThat(constr.getText()).isEqualTo("location==\"atlanta\\\"\"");
    }

    @Test
    public void testRuleParseLhsWithStringQuotes2() throws Exception {
        final String text = "rule X when Cheese( $x: type, type == \"s\\tti\\\"lto\\nn\" ) then end\n";
        RuleDescr rule = (RuleDescr) parse( "rule",
                                             text );
        assertThat(parser.hasErrors()).as(parser.getErrors().toString()).isFalse();

        assertThat(rule).isNotNull();

        AndDescr lhs = rule.getLhs();
        ExprConstraintDescr constr = (ExprConstraintDescr) ((PatternDescr) lhs.getDescrs().get( 0 )).getDescrs().get( 1 );

        assertThat(constr.getText()).isEqualTo("type == \"s\\tti\\\"lto\\nn\"");
    }

    @Test
    public void testLiteralBoolAndNegativeNumbersRule() throws Exception {
        final RuleDescr rule = (RuleDescr) parseResource( "rule",
                                                              "literal_bool_and_negative.drl" );

        assertThat(parser.hasErrors()).as(parser.getErrors().toString()).isFalse();

        assertThat(rule).isNotNull();

        assertThat(rule.getName()).isEqualTo("simple_rule");
        assertThat(rule.getLhs()).isNotNull();
        assertEqualsIgnoreWhitespace( "cons();",
                                          (String) rule.getConsequence() );

        final AndDescr lhs = rule.getLhs();
        assertThat(lhs.getDescrs().size()).isEqualTo(3);

        PatternDescr pattern = (PatternDescr) lhs.getDescrs().get( 0 );
        assertThat(pattern.getConstraint().getDescrs().size()).isEqualTo(1);
        AndDescr fieldAnd = (AndDescr) pattern.getConstraint();
        ExprConstraintDescr fld = (ExprConstraintDescr) fieldAnd.getDescrs().get( 0 );
        assertThat(fld.getExpression()).isEqualTo("bar == false");

        pattern = (PatternDescr) lhs.getDescrs().get( 1 );
        assertThat(pattern.getConstraint().getDescrs().size()).isEqualTo(1);

        fieldAnd = (AndDescr) pattern.getConstraint();
        fld = (ExprConstraintDescr) fieldAnd.getDescrs().get( 0 );

        assertThat(fld.getText()).isEqualTo("boo > -42");

        pattern = (PatternDescr) lhs.getDescrs().get( 2 );
        assertThat(pattern.getConstraint().getDescrs().size()).isEqualTo(1);

        fieldAnd = (AndDescr) pattern.getConstraint();
        fld = (ExprConstraintDescr) fieldAnd.getDescrs().get( 0 );

        assertThat(fld.getText()).isEqualTo("boo > -42.42");
    }

    @Test
    public void testChunkWithoutParens() throws Exception {
        String input = "( foo )";
        createParser( new ANTLRStringStream( input ) );
        String returnData = parser.chunk( DRL6Lexer.LEFT_PAREN,
                                          DRL6Lexer.RIGHT_PAREN,
                                          -1 );

        assertThat(returnData).isEqualTo("foo");
    }

    @Test
    public void testChunkWithParens() throws Exception {
        String input = "(fnord())";
        createParser( new ANTLRStringStream( input ) );
        String returnData = parser.chunk( DRL6Lexer.LEFT_PAREN,
                                          DRL6Lexer.RIGHT_PAREN,
                                          -1 );

        assertThat(returnData).isEqualTo("fnord()");
    }

    @Test
    public void testChunkWithParensAndQuotedString() throws Exception {
        String input = "( fnord( \"cheese\" ) )";
        createParser( new ANTLRStringStream( input ) );
        String returnData = parser.chunk( DRL6Lexer.LEFT_PAREN,
                                          DRL6Lexer.RIGHT_PAREN,
                                          -1 );

        assertThat(returnData).isEqualTo("fnord( \"cheese\" )");
    }

    @Test
    public void testChunkWithRandomCharac5ters() throws Exception {
        String input = "( %*9dkj)";
        createParser( new ANTLRStringStream( input ) );
        String returnData = parser.chunk( DRL6Lexer.LEFT_PAREN,
                                          DRL6Lexer.RIGHT_PAREN,
                                          -1 );

        assertThat(returnData).isEqualTo("%*9dkj");
    }

    @Test
    public void testEmptyPattern() throws Exception {
        PackageDescr pkg = (PackageDescr) parseResource( "compilationUnit",
                                                         "test_EmptyPattern.drl" );

        assertThat(parser.hasErrors()).as(parser.getErrors().toString()).isFalse();

        assertThat(pkg.getRules().size()).isEqualTo(1);
        final RuleDescr ruleDescr = pkg.getRules().get(0);
        assertThat(ruleDescr.getName()).isEqualTo("simple rule");
        assertThat(ruleDescr.getLhs()).isNotNull();
        assertThat(ruleDescr.getLhs().getDescrs().size()).isEqualTo(1);
        final PatternDescr patternDescr = (PatternDescr) ruleDescr.getLhs().getDescrs().get( 0 );
        assertThat(patternDescr.getConstraint().getDescrs().size()).isEqualTo(0); // this
        assertThat(patternDescr.getObjectType()).isEqualTo("Cheese");

    }

    @Test
    public void testSimpleMethodCallWithFrom() throws Exception {
        final RuleDescr rule = (RuleDescr) parseResource( "rule",
                                                          "test_SimpleMethodCallWithFrom.drl" );
        assertThat(parser.hasErrors()).as(parser.getErrors().toString()).isFalse();
        final PatternDescr pattern = (PatternDescr) rule.getLhs().getDescrs().get( 0 );
        final FromDescr from = (FromDescr) pattern.getSource();
        final MVELExprDescr method = (MVELExprDescr) from.getDataSource();

        assertThat(method.getExpression()).isEqualTo("something.doIt( foo,bar,42,\"hello\",[ a : \"b\", \"something\" : 42, \"a\" : foo, x : [x:y]],\"end\", [a, \"b\", 42] )");
    }

    @Test
    public void testSimpleFunctionCallWithFrom() throws Exception {
        final RuleDescr rule = (RuleDescr) parseResource( "rule",
                                                          "test_SimpleFunctionCallWithFrom.drl" );
        assertThat(parser.hasErrors()).as(parser.getErrors().toString()).isFalse();
        final PatternDescr pattern = (PatternDescr) rule.getLhs().getDescrs().get( 0 );
        final FromDescr from = (FromDescr) pattern.getSource();
        final MVELExprDescr func = (MVELExprDescr) from.getDataSource();

        assertThat(func.getExpression()).isEqualTo("doIt( foo,bar,42,\"hello\",[ a : \"b\", \"something\" : 42, \"a\" : foo, x : [x:y]],\"end\", [a, \"b\", 42] )");
    }

    @Test
    public void testSimpleAccessorWithFrom() throws Exception {
        final RuleDescr rule = (RuleDescr) parseResource( "rule",
                                                          "test_SimpleAccessorWithFrom.drl" );
        assertThat(parser.hasErrors()).as(parser.getErrors().toString()).isFalse();
        final PatternDescr pattern = (PatternDescr) rule.getLhs().getDescrs().get( 0 );
        final FromDescr from = (FromDescr) pattern.getSource();
        final MVELExprDescr accessor = (MVELExprDescr) from.getDataSource();

        assertThat(accessor.getExpression()).isEqualTo("something.doIt");
    }

    @Test
    public void testSimpleAccessorAndArgWithFrom() throws Exception {
        final RuleDescr rule = (RuleDescr) parseResource( "rule",
                                                          "test_SimpleAccessorArgWithFrom.drl" );
        assertThat(parser.hasErrors()).as(parser.getErrors().toString()).isFalse();
        final PatternDescr pattern = (PatternDescr) rule.getLhs().getDescrs().get( 0 );
        final FromDescr from = (FromDescr) pattern.getSource();
        final MVELExprDescr accessor = (MVELExprDescr) from.getDataSource();

        assertThat(accessor.getExpression()).isEqualTo("something.doIt[\"key\"]");
    }

    @Test
    public void testComplexChainedAcessor() throws Exception {
        final RuleDescr rule = (RuleDescr) parseResource( "rule",
                                                          "test_ComplexChainedCallWithFrom.drl" );
        assertThat(parser.hasErrors()).as(parser.getErrors().toString()).isFalse();
        final PatternDescr pattern = (PatternDescr) rule.getLhs().getDescrs().get( 0 );
        final FromDescr from = (FromDescr) pattern.getSource();
        final MVELExprDescr accessor = (MVELExprDescr) from.getDataSource();

        assertThat(accessor.getExpression()).isEqualTo("doIt1( foo,bar,42,\"hello\",[ a : \"b\"], [a, \"b\", 42] ).doIt2(bar, [a, \"b\", 42]).field[\"key\"]");
    }

    @Test
    public void testFrom() throws Exception {
        final RuleDescr rule = (RuleDescr) parseResource( "rule",
                                                          "from.drl" );

        assertThat(parser.hasErrors()).as(parser.getErrorMessages().toString()).isFalse();
        assertThat(rule).isNotNull();

        assertThat(rule.getName()).isEqualTo("using_from");

        assertThat(rule.getLhs().getDescrs().size()).isEqualTo(9);
    }

    @Test
    public void testSimpleRule() throws Exception {
        final RuleDescr rule = (RuleDescr) parseResource( "rule",
                                                          "simple_rule.drl" );
        assertThat(parser.hasErrors()).as(parser.getErrors().toString()).isFalse();

        assertThat(rule).isNotNull();

        assertThat(rule.getName()).isEqualTo("simple_rule");

        assertThat(rule.getConsequenceLine()).isEqualTo(26);
        assertThat(rule.getConsequencePattern()).isEqualTo(2);

        final AndDescr lhs = rule.getLhs();

        assertThat(lhs).isNotNull();

        assertThat(lhs.getDescrs().size()).isEqualTo(3);

        // Check first pattern
        final PatternDescr first = (PatternDescr) lhs.getDescrs().get( 0 );
        assertThat(first.getIdentifier()).isEqualTo("foo3");
        assertThat(first.getObjectType()).isEqualTo("Bar");

        assertThat(first.getConstraint().getDescrs().size()).isEqualTo(1);

        AndDescr fieldAnd = (AndDescr) first.getConstraint();
        ExprConstraintDescr constraint = (ExprConstraintDescr) fieldAnd.getDescrs().get( 0 );
        assertThat(constraint).isNotNull();

        assertThat(constraint.getExpression()).isEqualTo("a==3");

        // Check second pattern
        final PatternDescr second = (PatternDescr) lhs.getDescrs().get( 1 );
        assertThat(second.getIdentifier()).isEqualTo("foo4");
        assertThat(second.getObjectType()).isEqualTo("Bar");

        // no constraints, only a binding
        fieldAnd = (AndDescr) second.getConstraint();
        assertThat(fieldAnd.getDescrs().size()).isEqualTo(1);

        final ExprConstraintDescr binding = (ExprConstraintDescr) second.getConstraint().getDescrs().get( 0 );
        assertThat(binding.getExpression()).isEqualTo("a4:a==4");

        // Check third pattern
        final PatternDescr third = (PatternDescr) lhs.getDescrs().get( 2 );
        assertThat(third.getIdentifier()).isNull();
        assertThat(third.getObjectType()).isEqualTo("Baz");

        assertEqualsIgnoreWhitespace( "if ( a == b ) { " + "  assert( foo3 );" + "} else {" + "  retract( foo4 );" + "}" + "  System.out.println( a4 );",
                                          (String) rule.getConsequence() );
    }

    @Test
    public void testRestrictionsMultiple() throws Exception {
        final RuleDescr rule = (RuleDescr) parseResource( "rule",
                                                          "restrictions_test.drl" );
        assertThat(parser.hasErrors()).as(parser.getErrors().toString()).isFalse();

        assertThat(rule).isNotNull();

        assertEqualsIgnoreWhitespace( "consequence();",
                                          (String) rule.getConsequence() );
        assertThat(rule.getName()).isEqualTo("simple_rule");
        assertThat(rule.getLhs().getDescrs().size()).isEqualTo(2);

        // The first pattern, with 2 restrictions on a single field (plus a
        // connective)
        PatternDescr pattern = (PatternDescr) rule.getLhs().getDescrs().get( 0 );
        assertThat(pattern.getObjectType()).isEqualTo("Person");
        assertThat(pattern.getConstraint().getDescrs().size()).isEqualTo(1);

        AndDescr and = (AndDescr) pattern.getConstraint();
        ExprConstraintDescr fld = (ExprConstraintDescr) and.getDescrs().get( 0 );
        assertThat(fld.getExpression()).isEqualTo("age > 30 && < 40");

        // the second col, with 2 fields, the first with 2 restrictions, the
        // second field with one
        pattern = (PatternDescr) rule.getLhs().getDescrs().get( 1 );
        assertThat(pattern.getObjectType()).isEqualTo("Vehicle");
        assertThat(pattern.getConstraint().getDescrs().size()).isEqualTo(2);

        and = (AndDescr) pattern.getConstraint();
        fld = (ExprConstraintDescr) and.getDescrs().get( 0 );
        assertThat(fld.getExpression()).isEqualTo("type == \"sedan\" || == \"wagon\"");

        // now the second field
        fld = (ExprConstraintDescr) and.getDescrs().get( 1 );
        assertThat(fld.getExpression()).isEqualTo("age < 3");
    }

    @Test
    public void testLineNumberInAST() throws Exception {
        // also see testSimpleExpander to see how this works with an expander
        // (should be the same).

        final RuleDescr rule = (RuleDescr) parseResource( "rule",
                                                          "simple_rule.drl" );

        assertThat(rule).isNotNull();

        assertThat(rule.getName()).isEqualTo("simple_rule");

        assertThat(rule.getConsequenceLine()).isEqualTo(26);
        assertThat(rule.getConsequencePattern()).isEqualTo(2);

        final AndDescr lhs = rule.getLhs();

        assertThat(lhs).isNotNull();

        assertThat(lhs.getDescrs().size()).isEqualTo(3);

        // Check first pattern
        final PatternDescr first = (PatternDescr) lhs.getDescrs().get( 0 );
        assertThat(first.getIdentifier()).isEqualTo("foo3");
        assertThat(first.getObjectType()).isEqualTo("Bar");
        assertThat(first.getConstraint().getDescrs().size()).isEqualTo(1);

        // Check second pattern
        final PatternDescr second = (PatternDescr) lhs.getDescrs().get( 1 );
        assertThat(second.getIdentifier()).isEqualTo("foo4");
        assertThat(second.getObjectType()).isEqualTo("Bar");

        final PatternDescr third = (PatternDescr) lhs.getDescrs().get( 2 );
        assertThat(third.getObjectType()).isEqualTo("Baz");

        assertThat(first.getLine()).isEqualTo(23);
        assertThat(second.getLine()).isEqualTo(24);
        assertThat(third.getLine()).isEqualTo(25);
    }

    @Test
    public void testLineNumberIncludingCommentsInRHS() throws Exception {
        PackageDescr pkg = (PackageDescr) parseResource( "compilationUnit",
                                                         "test_CommentLineNumbersInConsequence.drl" );

        assertThat(parser.hasErrors()).as(parser.getErrors().toString()).isFalse();

        final String rhs = (String) pkg.getRules().get( 0 ).getConsequence();
        String expected = "\\s*//woot$\\s*first$\\s*$\\s*//$\\s*$\\s*/\\* lala$\\s*$\\s*\\*/$\\s*second$\\s*";
        assertThat(Pattern.compile(expected,
                Pattern.DOTALL | Pattern.MULTILINE).matcher(rhs).matches()).isTrue();
    }

    @Test
    public void testLhsSemicolonDelim() throws Exception {
        final RuleDescr rule = (RuleDescr) parseResource( "rule",
                                                          "lhs_semicolon_delim.drl" );

        assertThat(parser.hasErrors()).as(parser.getErrors().toString()).isFalse();

        assertThat(rule).isNotNull();

        assertThat(rule.getName()).isEqualTo("simple_rule");

        final AndDescr lhs = rule.getLhs();

        assertThat(lhs).isNotNull();

        assertThat(lhs.getDescrs().size()).isEqualTo(3);

        // System.err.println( lhs.getDescrs() );

        // Check first pattern
        final PatternDescr first = (PatternDescr) lhs.getDescrs().get( 0 );
        assertThat(first.getIdentifier()).isEqualTo("foo3");
        assertThat(first.getObjectType()).isEqualTo("Bar");

        assertThat(first.getConstraint().getDescrs().size()).isEqualTo(1);

        // LiteralDescr constraint = (LiteralDescr) first.getDescrs().get( 0 );
        AndDescr and = (AndDescr) first.getConstraint();
        ExprConstraintDescr fld = (ExprConstraintDescr) and.getDescrs().get( 0 );
        assertThat(fld).isNotNull();

        assertThat(fld.getExpression()).isEqualTo("a==3");

        // Check second pattern
        final PatternDescr second = (PatternDescr) lhs.getDescrs().get( 1 );
        assertThat(second.getIdentifier()).isEqualTo("foo4");
        assertThat(second.getObjectType()).isEqualTo("Bar");

        assertThat(second.getDescrs().size()).isEqualTo(1);

        final ExprConstraintDescr fieldBindingDescr = (ExprConstraintDescr) second.getDescrs().get( 0 );
        assertThat(fieldBindingDescr.getExpression()).isEqualTo("a4:a==4");

        // Check third pattern
        final PatternDescr third = (PatternDescr) lhs.getDescrs().get( 2 );
        assertThat(third.getIdentifier()).isNull();
        assertThat(third.getObjectType()).isEqualTo("Baz");

        assertEqualsIgnoreWhitespace( "if ( a == b ) { " + "  assert( foo3 );" + "} else {" + "  retract( foo4 );" + "}" + "  System.out.println( a4 );",
                                      (String) rule.getConsequence() );
    }

    @Test
    public void testNotNode() throws Exception {
        final RuleDescr rule = (RuleDescr) parseResource( "rule",
                                                          "rule_not.drl" );

        assertThat(parser.hasErrors()).as(parser.getErrors().toString()).isFalse();

        assertThat(rule).isNotNull();
        assertThat(rule.getName()).isEqualTo("simple_rule");

        final AndDescr lhs = rule.getLhs();
        assertThat(lhs.getDescrs().size()).isEqualTo(1);
        final NotDescr not = (NotDescr) lhs.getDescrs().get( 0 );
        assertThat(not.getDescrs().size()).isEqualTo(1);
        final PatternDescr pattern = (PatternDescr) not.getDescrs().get( 0 );

        assertThat(pattern.getObjectType()).isEqualTo("Cheese");
        assertThat(pattern.getConstraint().getDescrs().size()).isEqualTo(1);

        final AndDescr and = (AndDescr) pattern.getConstraint();
        final ExprConstraintDescr fld = (ExprConstraintDescr) and.getDescrs().get( 0 );

        assertThat(fld.getExpression()).isEqualTo("type == \"stilton\"");
    }

    @Test
    public void testNotExistWithBrackets() throws Exception {

        final PackageDescr pkg = (PackageDescr) parseResource( "compilationUnit",
                                                               "not_exist_with_brackets.drl" );

        assertThat(parser.hasErrors()).as(parser.getErrors().toString()).isFalse();

        final RuleDescr rule = pkg.getRules().get(0);

        assertThat(rule).isNotNull();
        assertThat(rule.getName()).isEqualTo("simple_rule");

        final AndDescr lhs = rule.getLhs();
        assertThat(lhs.getDescrs().size()).isEqualTo(2);
        final NotDescr not = (NotDescr) lhs.getDescrs().get( 0 );
        assertThat(not.getDescrs().size()).isEqualTo(1);
        final PatternDescr pattern = (PatternDescr) not.getDescrs().get( 0 );

        assertThat(pattern.getObjectType()).isEqualTo("Cheese");

        final ExistsDescr ex = (ExistsDescr) lhs.getDescrs().get( 1 );
        assertThat(ex.getDescrs().size()).isEqualTo(1);
        final PatternDescr exPattern = (PatternDescr) ex.getDescrs().get( 0 );
        assertThat(exPattern.getObjectType()).isEqualTo("Foo");
    }

    @Test
    public void testSimpleQuery() throws Exception {
        final QueryDescr query = (QueryDescr) parseResource( "query",
                                                             "simple_query.drl" );

        assertThat(query).isNotNull();

        assertThat(query.getName()).isEqualTo("simple_query");

        final AndDescr lhs = query.getLhs();

        assertThat(lhs).isNotNull();

        assertThat(lhs.getDescrs().size()).isEqualTo(3);

        // Check first pattern
        final PatternDescr first = (PatternDescr) lhs.getDescrs().get( 0 );
        assertThat(first.getIdentifier()).isEqualTo("foo3");
        assertThat(first.getObjectType()).isEqualTo("Bar");

        assertThat(first.getConstraint().getDescrs().size()).isEqualTo(1);

        AndDescr and = (AndDescr) first.getConstraint();
        ExprConstraintDescr fld = (ExprConstraintDescr) and.getDescrs().get( 0 );
        assertThat(fld).isNotNull();

        assertThat(fld.getExpression()).isEqualTo("a==3");

        // Check second pattern
        final PatternDescr second = (PatternDescr) lhs.getDescrs().get( 1 );
        assertThat(second.getIdentifier()).isEqualTo("foo4");
        assertThat(second.getObjectType()).isEqualTo("Bar");

        assertThat(second.getDescrs().size()).isEqualTo(1);
        // check it has field bindings.
        final ExprConstraintDescr bindingDescr = (ExprConstraintDescr) second.getDescrs().get( 0 );
        assertThat(bindingDescr.getExpression()).isEqualTo("a4:a==4");
    }

    @Test
    public void testQueryRuleMixed() throws Exception {
        final PackageDescr pkg = (PackageDescr) parseResource( "compilationUnit",
                                                               "query_and_rule.drl" );

        assertThat(pkg.getRules().size()).isEqualTo(4); // as queries are rules
        RuleDescr rule = pkg.getRules().get(0);
        assertThat(rule.getName()).isEqualTo("bar");

        QueryDescr query = (QueryDescr) pkg.getRules().get( 1 );
        assertThat(query.getName()).isEqualTo("simple_query");

        rule = pkg.getRules().get(2);
        assertThat(rule.getName()).isEqualTo("bar2");

        query = (QueryDescr) pkg.getRules().get( 3 );
        assertThat(query.getName()).isEqualTo("simple_query2");
    }

    @Test
    public void testMultipleRules() throws Exception {
        final PackageDescr pkg = (PackageDescr) parseResource( "compilationUnit",
                                                               "multiple_rules.drl" );

        final List<RuleDescr> rules = pkg.getRules();

        assertThat(rules.size()).isEqualTo(2);

        final RuleDescr rule0 = rules.get( 0 );
        assertThat(rule0.getName()).isEqualTo("Like Stilton");

        final RuleDescr rule1 = rules.get( 1 );
        assertThat(rule1.getName()).isEqualTo("Like Cheddar");

        // checkout the first rule
        AndDescr lhs = rule1.getLhs();
        assertThat(lhs).isNotNull();
        assertThat(lhs.getDescrs().size()).isEqualTo(1);
        assertEqualsIgnoreWhitespace( "System.out.println(\"I like \" + t);",
                                      (String) rule0.getConsequence() );

        // Check first pattern
        PatternDescr first = (PatternDescr) lhs.getDescrs().get( 0 );
        assertThat(first.getObjectType()).isEqualTo("Cheese");

        // checkout the second rule
        lhs = rule1.getLhs();
        assertThat(lhs).isNotNull();
        assertThat(lhs.getDescrs().size()).isEqualTo(1);
        assertEqualsIgnoreWhitespace( "System.out.println(\"I like \" + t);",
                                      (String) rule1.getConsequence() );

        // Check first pattern
        first = (PatternDescr) lhs.getDescrs().get( 0 );
        assertThat(first.getObjectType()).isEqualTo("Cheese");
    }

    @Test
    public void testExpanderLineSpread() throws Exception {
        final DrlParser parser = new DrlParser(LanguageLevelOption.DRL6);
        final PackageDescr pkg = parser.parse( this.getReader( "expander_spread_lines.dslr" ),
                                               this.getReader( "complex.dsl" ) );

        assertThat(parser.hasErrors()).as(parser.getErrors().toString()).isFalse();

        final RuleDescr rule = pkg.getRules().get(0);
        assertThat(rule.getLhs().getDescrs().size()).isEqualTo(1);

        final OrDescr or = (OrDescr) rule.getLhs().getDescrs().get( 0 );
        assertThat(or.getDescrs().size()).isEqualTo(2);
        assertThat( (String) rule.getConsequence() ).isNotNull();

    }

    @Test
    public void testExpanderMultipleConstraints() throws Exception {
        final DrlParser parser = new DrlParser(LanguageLevelOption.DRL6);
        final PackageDescr pkg = parser.parse( this.getReader( "expander_multiple_constraints.dslr" ),
                                               this.getReader( "multiple_constraints.dsl" ) );

        assertThat(parser.hasErrors()).as(parser.getErrors().toString()).isFalse();

        final RuleDescr rule = pkg.getRules().get(0);
        assertThat(rule.getLhs().getDescrs().size()).isEqualTo(2);

        PatternDescr pattern = (PatternDescr) rule.getLhs().getDescrs().get( 0 );
        assertThat(pattern.getObjectType()).isEqualTo("Person");

        assertThat(pattern.getConstraint().getDescrs().size()).isEqualTo(2);
        assertThat(((ExprConstraintDescr) pattern.getConstraint().getDescrs().get(0)).getExpression()).isEqualTo("age < 42");
        assertThat(((ExprConstraintDescr) pattern.getConstraint().getDescrs().get(1)).getExpression()).isEqualTo("location==atlanta");

        pattern = (PatternDescr) rule.getLhs().getDescrs().get( 1 );
        assertThat(pattern.getObjectType()).isEqualTo("Bar");

        assertThat( (String) rule.getConsequence() ).isNotNull();

    }

    @Test
    public void testExpanderMultipleConstraintsFlush() throws Exception {
        final DrlParser parser = new DrlParser(LanguageLevelOption.DRL6);
        // this is similar to the other test, but it requires a flush to add the
        // constraints
        final PackageDescr pkg = parser.parse( this.getReader( "expander_multiple_constraints_flush.dslr" ),
                                               this.getReader( "multiple_constraints.dsl" ) );

        assertThat(parser.hasErrors()).as(parser.getErrors().toString()).isFalse();

        final RuleDescr rule = pkg.getRules().get(0);
        assertThat(rule.getLhs().getDescrs().size()).isEqualTo(1);

        final PatternDescr pattern = (PatternDescr) rule.getLhs().getDescrs().get( 0 );
        assertThat(pattern.getObjectType()).isEqualTo("Person");

        assertThat(pattern.getConstraint().getDescrs().size()).isEqualTo(2);
        assertThat(((ExprConstraintDescr) pattern.getConstraint().getDescrs().get(0)).getExpression()).isEqualTo("age < 42");
        assertThat(((ExprConstraintDescr) pattern.getConstraint().getDescrs().get(1)).getExpression()).isEqualTo("location==atlanta");

        assertThat( (String) rule.getConsequence() ).isNotNull();

    }

    // @Test public void testExpanderUnExpandableErrorLines() throws Exception {
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
    // assertThat(pack).isNotNull();
    //
    // final ExpanderException ex = (ExpanderException) parser.getErrors().get(
    // 0 );
    // assertTrue( ex.getMessage().indexOf( "whoops" ) > -1 );
    //
    // }

    @Test
    public void testBasicBinding() throws Exception {
        final PackageDescr pkg = (PackageDescr) parseResource( "compilationUnit",
                                                               "basic_binding.drl" );

        final RuleDescr ruleDescr = pkg.getRules().get(0);

        final AndDescr lhs = ruleDescr.getLhs();
        assertThat(lhs.getDescrs().size()).isEqualTo(1);
        final PatternDescr cheese = (PatternDescr) lhs.getDescrs().get( 0 );
        assertThat(cheese.getObjectType()).isEqualTo("Cheese");
        assertThat(cheese.getConstraint().getDescrs().size()).isEqualTo(1);
        final ExprConstraintDescr fieldBinding = (ExprConstraintDescr) cheese.getDescrs().get( 0 );
        assertThat(fieldBinding.getExpression()).isEqualTo("$type:type");
    }

    @Test
    public void testBoundVariables() throws Exception {
        final PackageDescr pkg = (PackageDescr) parseResource( "compilationUnit",
                                                               "bindings.drl" );

        final RuleDescr ruleDescr = pkg.getRules().get(0);

        final AndDescr lhs = ruleDescr.getLhs();
        assertThat(lhs.getDescrs().size()).isEqualTo(2);
        final PatternDescr cheese = (PatternDescr) lhs.getDescrs().get( 0 );
        assertThat(cheese.getObjectType()).isEqualTo("Cheese");
        assertThat(cheese.getDescrs().size()).isEqualTo(1);
        ExprConstraintDescr fieldBinding = (ExprConstraintDescr) cheese.getDescrs().get( 0 );
        assertThat(fieldBinding.getExpression()).isEqualTo("$type : type == \"stilton\"");

        final PatternDescr person = (PatternDescr) lhs.getDescrs().get( 1 );
        assertThat(person.getDescrs().size()).isEqualTo(2);
        fieldBinding = (ExprConstraintDescr) person.getDescrs().get( 0 );
        assertThat(fieldBinding.getExpression()).isEqualTo("$name : name == \"bob\"");

        ExprConstraintDescr fld = (ExprConstraintDescr) person.getDescrs().get( 1 );
        assertThat(fld.getExpression()).isEqualTo("likes == $type");
    }

    @Test
    public void testOrNesting() throws Exception {
        final PackageDescr pkg = (PackageDescr) parseResource( "compilationUnit",
                                                               "or_nesting.drl" );

        assertThat(pkg).isNotNull();
        assertThat(pkg.getRules().size()).isEqualTo(1);
        final RuleDescr rule = pkg.getRules().get(0);
        assertThat(rule.getName()).isEqualTo("simple_rule");

        assertThat(rule.getLhs().getDescrs().size()).isEqualTo(1);

        final OrDescr or = (OrDescr) rule.getLhs().getDescrs().get( 0 );
        assertThat(or.getDescrs().size()).isEqualTo(2);

        final PatternDescr first = (PatternDescr) or.getDescrs().get( 0 );
        assertThat(first.getObjectType()).isEqualTo("Person");

        final AndDescr and = (AndDescr) or.getDescrs().get( 1 );
        assertThat(and.getDescrs().size()).isEqualTo(2);

        final PatternDescr left = (PatternDescr) and.getDescrs().get( 0 );
        assertThat(left.getObjectType()).isEqualTo("Person");

        final PatternDescr right = (PatternDescr) and.getDescrs().get( 1 );
        assertThat(right.getObjectType()).isEqualTo("Cheese");
    }

    /** Test that explicit "&&", "||" works as expected */
    @Test
    public void testAndOrRules() throws Exception {
        final PackageDescr pkg = (PackageDescr) parseResource( "compilationUnit",
                                                               "and_or_rule.drl" );

        assertThat(pkg).isNotNull();
        assertThat(pkg.getRules().size()).isEqualTo(1);
        final RuleDescr rule = pkg.getRules().get(0);
        assertThat(rule.getName()).isEqualTo("simple_rule");

        // we will have 3 children under the main And node
        final AndDescr and = rule.getLhs();
        assertThat(and.getDescrs().size()).isEqualTo(3);

        PatternDescr left = (PatternDescr) and.getDescrs().get( 0 );
        PatternDescr right = (PatternDescr) and.getDescrs().get( 1 );
        assertThat(left.getObjectType()).isEqualTo("Person");
        assertThat(right.getObjectType()).isEqualTo("Cheese");

        assertThat(left.getConstraint().getDescrs().size()).isEqualTo(1);

        ExprConstraintDescr fld = (ExprConstraintDescr) left.getConstraint().getDescrs().get( 0 );

        assertThat(fld.getExpression()).isEqualTo("name == \"mark\"");

        assertThat(right.getConstraint().getDescrs().size()).isEqualTo(1);

        fld = (ExprConstraintDescr) right.getConstraint().getDescrs().get( 0 );

        assertThat(fld.getExpression()).isEqualTo("type == \"stilton\"");

        // now the "||" part
        final OrDescr or = (OrDescr) and.getDescrs().get( 2 );
        assertThat(or.getDescrs().size()).isEqualTo(2);
        left = (PatternDescr) or.getDescrs().get( 0 );
        right = (PatternDescr) or.getDescrs().get( 1 );
        assertThat(left.getObjectType()).isEqualTo("Person");
        assertThat(right.getObjectType()).isEqualTo("Cheese");
        assertThat(left.getConstraint().getDescrs().size()).isEqualTo(1);

        fld = (ExprConstraintDescr) left.getConstraint().getDescrs().get( 0 );

        assertThat(fld.getExpression()).isEqualTo("name == \"mark\"");

        assertThat(right.getConstraint().getDescrs().size()).isEqualTo(1);

        fld = (ExprConstraintDescr) right.getConstraint().getDescrs().get( 0 );

        assertThat(fld.getExpression()).isEqualTo("type == \"stilton\"");

        assertEqualsIgnoreWhitespace( "System.out.println( \"Mark and Michael\" );",
                                      (String) rule.getConsequence() );
    }

    /** test basic foo : Fact() || Fact() stuff */
    @Test
    public void testOrWithBinding() throws Exception {
        final PackageDescr pkg = (PackageDescr) parseResource( "compilationUnit",
                                                               "or_binding.drl" );

        assertThat(pkg.getRules().size()).isEqualTo(1);
        final RuleDescr rule = pkg.getRules().get(0);
        assertThat(rule.getLhs().getDescrs().size()).isEqualTo(2);

        final OrDescr or = (OrDescr) rule.getLhs().getDescrs().get( 0 );
        assertThat(or.getDescrs().size()).isEqualTo(2);

        final PatternDescr leftPattern = (PatternDescr) or.getDescrs().get( 0 );
        assertThat(leftPattern.getObjectType()).isEqualTo("Person");
        assertThat(leftPattern.getIdentifier()).isEqualTo("foo");

        final PatternDescr rightPattern = (PatternDescr) or.getDescrs().get( 1 );
        assertThat(rightPattern.getObjectType()).isEqualTo("Person");
        assertThat(rightPattern.getIdentifier()).isEqualTo("foo");

        final PatternDescr cheeseDescr = (PatternDescr) rule.getLhs().getDescrs().get( 1 );
        assertThat(cheeseDescr.getObjectType()).isEqualTo("Cheese");
        assertThat(cheeseDescr.getIdentifier()).isEqualTo(null);

        assertEqualsIgnoreWhitespace( "System.out.println( \"Mark and Michael\" + bar );",
                                      (String) rule.getConsequence() );
    }

    /** test basic foo : Fact() || Fact() stuff binding to an "or" */
    @Test
    public void testOrBindingComplex() throws Exception {
        final PackageDescr pkg = (PackageDescr) parseResource( "compilationUnit",
                                                               "or_binding_complex.drl" );

        assertThat(pkg.getRules().size()).isEqualTo(1);
        final RuleDescr rule = pkg.getRules().get(0);
        assertThat(rule.getLhs().getDescrs().size()).isEqualTo(1);

        assertThat(rule.getLhs().getDescrs().size()).isEqualTo(1);

        final OrDescr or = (OrDescr) rule.getLhs().getDescrs().get( 0 );
        assertThat(or.getDescrs().size()).isEqualTo(2);

        // first fact
        final PatternDescr firstFact = (PatternDescr) or.getDescrs().get( 0 );
        assertThat(firstFact.getObjectType()).isEqualTo("Person");
        assertThat(firstFact.getIdentifier()).isEqualTo("foo");

        // second "option"
        final PatternDescr secondFact = (PatternDescr) or.getDescrs().get( 1 );
        assertThat(secondFact.getObjectType()).isEqualTo("Person");
        assertThat(secondFact.getConstraint().getDescrs().size()).isEqualTo(1);
        assertThat(secondFact.getIdentifier()).isEqualTo("foo");

        assertEqualsIgnoreWhitespace( "System.out.println( \"Mark and Michael\" + bar );",
                                      (String) rule.getConsequence() );
    }

    @Test
    public void testOrBindingWithBrackets() throws Exception {
        final PackageDescr pkg = (PackageDescr) parseResource( "compilationUnit",
                                                               "or_binding_with_brackets.drl" );

        assertThat(pkg.getRules().size()).isEqualTo(1);
        final RuleDescr rule = pkg.getRules().get(0);
        assertThat(rule.getLhs().getDescrs().size()).isEqualTo(1);

        assertThat(rule.getLhs().getDescrs().size()).isEqualTo(1);

        final OrDescr or = (OrDescr) rule.getLhs().getDescrs().get( 0 );
        assertThat(or.getDescrs().size()).isEqualTo(2);

        // first fact
        final PatternDescr firstFact = (PatternDescr) or.getDescrs().get( 0 );
        assertThat(firstFact.getObjectType()).isEqualTo("Person");
        assertThat(firstFact.getIdentifier()).isEqualTo("foo");

        // second "option"
        final PatternDescr secondFact = (PatternDescr) or.getDescrs().get( 0 );
        assertThat(secondFact.getObjectType()).isEqualTo("Person");
        assertThat(secondFact.getIdentifier()).isEqualTo("foo");

        assertEqualsIgnoreWhitespace( "System.out.println( \"Mark and Michael\" + bar );",
                                      (String) rule.getConsequence() );
    }

    /** */
    @Test
    public void testBracketsPrecedence() throws Exception {
        final PackageDescr pkg = (PackageDescr) parseResource( "compilationUnit",
                                                               "brackets_precedence.drl" );

        assertThat(pkg.getRules().size()).isEqualTo(1);
        final RuleDescr rule = pkg.getRules().get(0);

        final AndDescr rootAnd = rule.getLhs();

        assertThat(rootAnd.getDescrs().size()).isEqualTo(2);

        final OrDescr leftOr = (OrDescr) rootAnd.getDescrs().get( 0 );

        assertThat(leftOr.getDescrs().size()).isEqualTo(2);
        final NotDescr not = (NotDescr) leftOr.getDescrs().get( 0 );
        final PatternDescr foo1 = (PatternDescr) not.getDescrs().get( 0 );
        assertThat(foo1.getObjectType()).isEqualTo("Foo");
        final PatternDescr foo2 = (PatternDescr) leftOr.getDescrs().get( 1 );
        assertThat(foo2.getObjectType()).isEqualTo("Foo");

        final OrDescr rightOr = (OrDescr) rootAnd.getDescrs().get( 1 );

        assertThat(rightOr.getDescrs().size()).isEqualTo(2);
        final PatternDescr shoes = (PatternDescr) rightOr.getDescrs().get( 0 );
        assertThat(shoes.getObjectType()).isEqualTo("Shoes");
        final PatternDescr butt = (PatternDescr) rightOr.getDescrs().get( 1 );
        assertThat(butt.getObjectType()).isEqualTo("Butt");
    }

    @Test
    public void testEvalMultiple() throws Exception {
        final PackageDescr pkg = (PackageDescr) parseResource( "compilationUnit",
                                                               "eval_multiple.drl" );

        assertThat(pkg.getRules().size()).isEqualTo(1);
        final RuleDescr rule = pkg.getRules().get(0);
        assertThat(rule.getLhs().getDescrs().size()).isEqualTo(4);

        final EvalDescr eval = (EvalDescr) rule.getLhs().getDescrs().get( 0 );
        assertEqualsIgnoreWhitespace( "abc(\"foo\") + 5",
                                      (String) eval.getContent() );

        final PatternDescr pattern = (PatternDescr) rule.getLhs().getDescrs().get( 1 );
        assertThat(pattern.getObjectType()).isEqualTo("Foo");

    }

    @Test
    public void testWithEval() throws Exception {
        final PackageDescr pkg = (PackageDescr) parseResource( "compilationUnit",
                                                               "with_eval.drl" );

        assertThat(pkg.getRules().size()).isEqualTo(1);
        final RuleDescr rule = pkg.getRules().get(0);
        assertThat(rule.getLhs().getDescrs().size()).isEqualTo(3);
        PatternDescr pattern = (PatternDescr) rule.getLhs().getDescrs().get( 0 );
        assertThat(pattern.getObjectType()).isEqualTo("Foo");
        pattern = (PatternDescr) rule.getLhs().getDescrs().get( 1 );
        assertThat(pattern.getObjectType()).isEqualTo("Bar");

        final EvalDescr eval = (EvalDescr) rule.getLhs().getDescrs().get( 2 );
        assertEqualsIgnoreWhitespace( "abc(\"foo\")",
                                      (String) eval.getContent() );
        assertEqualsIgnoreWhitespace( "Kapow",
                                      (String) rule.getConsequence() );
    }

    @Test
    public void testWithRetval() throws Exception {
        final PackageDescr pkg = (PackageDescr) parseResource( "compilationUnit",
                                                               "with_retval.drl" );

        assertThat(pkg.getRules().size()).isEqualTo(1);

        final RuleDescr rule = pkg.getRules().get(0);
        assertThat(rule.getLhs().getDescrs().size()).isEqualTo(1);
        final PatternDescr col = (PatternDescr) rule.getLhs().getDescrs().get( 0 );
        assertThat(col.getConstraint().getDescrs().size()).isEqualTo(1);
        assertThat(col.getObjectType()).isEqualTo("Foo");
        final ExprConstraintDescr fld = (ExprConstraintDescr) col.getConstraint().getDescrs().get( 0 );

        assertThat(fld.getExpression()).isEqualTo("name== (a + b)");
    }

    @Test
    public void testWithPredicate() throws Exception {
        final PackageDescr pkg = (PackageDescr) parseResource( "compilationUnit",
                                                               "with_predicate.drl" );

        assertThat(pkg.getRules().size()).isEqualTo(1);

        final RuleDescr rule = pkg.getRules().get(0);
        assertThat(rule.getLhs().getDescrs().size()).isEqualTo(1);
        final PatternDescr col = (PatternDescr) rule.getLhs().getDescrs().get( 0 );
        AndDescr and = (AndDescr) col.getConstraint();
        assertThat(and.getDescrs().size()).isEqualTo(2);

        final ExprConstraintDescr field = (ExprConstraintDescr) col.getDescrs().get( 0 );
        final ExprConstraintDescr pred = (ExprConstraintDescr) and.getDescrs().get( 1 );
        assertThat(field.getExpression()).isEqualTo("$age2:age");
        assertEqualsIgnoreWhitespace( "$age2 == $age1+2",
                                      pred.getExpression() );
    }

    @Test
    public void testNotWithConstraint() throws Exception {
        final PackageDescr pkg = (PackageDescr) parseResource( "compilationUnit",
                                                               "not_with_constraint.drl" );

        assertThat(pkg.getRules().size()).isEqualTo(1);

        final RuleDescr rule = pkg.getRules().get(0);
        assertThat(rule.getLhs().getDescrs().size()).isEqualTo(2);

        PatternDescr pattern = (PatternDescr) rule.getLhs().getDescrs().get( 0 );
        final ExprConstraintDescr fieldBinding = (ExprConstraintDescr) pattern.getDescrs().get( 0 );
        assertThat(fieldBinding.getExpression()).isEqualTo("$likes:like");

        final NotDescr not = (NotDescr) rule.getLhs().getDescrs().get( 1 );
        pattern = (PatternDescr) not.getDescrs().get( 0 );

        final ExprConstraintDescr fld = (ExprConstraintDescr) pattern.getConstraint().getDescrs().get( 0 );

        assertThat(fld.getExpression()).isEqualTo("type == $likes");
    }

    @Test
    public void testFunctions() throws Exception {
        final PackageDescr pkg = (PackageDescr) parseResource( "compilationUnit",
                                                               "functions.drl" );

        assertThat(pkg.getRules().size()).isEqualTo(2);

        final List<FunctionDescr> functions = pkg.getFunctions();
        assertThat(functions.size()).isEqualTo(2);

        FunctionDescr func = functions.get( 0 );
        assertThat(func.getName()).isEqualTo("functionA");
        assertThat(func.getReturnType()).isEqualTo("String");
        assertThat(func.getParameterNames().size()).isEqualTo(2);
        assertThat(func.getParameterTypes().size()).isEqualTo(2);
        assertThat(func.getLine()).isEqualTo(23);
        assertThat(func.getColumn()).isEqualTo(0);

        assertThat(func.getParameterTypes().get(0)).isEqualTo("String");
        assertThat(func.getParameterNames().get(0)).isEqualTo("s");

        assertThat(func.getParameterTypes().get(1)).isEqualTo("Integer");
        assertThat(func.getParameterNames().get(1)).isEqualTo("i");

        assertEqualsIgnoreWhitespace( "foo();",
                                      func.getBody() );

        func = functions.get( 1 );
        assertThat(func.getName()).isEqualTo("functionB");
        assertEqualsIgnoreWhitespace( "bar();",
                                      func.getText() );
    }

    @Test
    public void testComment() throws Exception {
        final PackageDescr pkg = (PackageDescr) parseResource( "compilationUnit",
                                                               "comment.drl" );

        assertThat(pkg).isNotNull();

        assertThat(pkg.getName()).isEqualTo("foo.bar");
    }

    @Test
    public void testAttributes() throws Exception {
        final RuleDescr rule = (RuleDescr) parseResource( "rule",
                                                          "rule_attributes.drl" );
        assertThat(rule.getName()).isEqualTo("simple_rule");
        assertEqualsIgnoreWhitespace( "bar();",
                                      (String) rule.getConsequence() );

        final Map<String, AttributeDescr> attrs = rule.getAttributes();
        assertThat(attrs.size()).isEqualTo(6);

        AttributeDescr at = attrs.get("salience");
        assertThat(at.getName()).isEqualTo("salience");
        assertThat(at.getValue()).isEqualTo("42");

        at = attrs.get("agenda-group");
        assertThat(at.getName()).isEqualTo("agenda-group");
        assertThat(at.getValue()).isEqualTo("my_group");

        at = attrs.get("no-loop");
        assertThat(at.getName()).isEqualTo("no-loop");
        assertThat(at.getValue()).isEqualTo("true");

        at = attrs.get("duration");
        assertThat(at.getName()).isEqualTo("duration");
        assertThat(at.getValue()).isEqualTo("42");

        at = attrs.get("activation-group");
        assertThat(at.getName()).isEqualTo("activation-group");
        assertThat(at.getValue()).isEqualTo("my_activation_group");

        at = attrs.get("lock-on-active");
        assertThat(at.getName()).isEqualTo("lock-on-active");
        assertThat(at.getValue()).isEqualTo("true");
    }

    @Test
    public void testAttributes2() throws Exception {
        final PackageDescr pkg = (PackageDescr) parseResource( "compilationUnit",
                                                               "rule_attributes2.drl" );
        assertThat(parser.hasErrors()).as(parser.getErrors().toString()).isFalse();

        List<RuleDescr> rules = pkg.getRules();
        assertThat(rules.size()).isEqualTo(3);

        RuleDescr rule = rules.get( 0 );
        assertThat(rule.getName()).isEqualTo("rule1");
        Map<String, AttributeDescr> attrs = rule.getAttributes();
        assertThat(attrs.size()).isEqualTo(2);
        AttributeDescr at = attrs.get("salience");
        assertThat(at.getName()).isEqualTo("salience");
        assertThat(at.getValue()).isEqualTo("(42)");
        at = attrs.get("agenda-group");
        assertThat(at.getName()).isEqualTo("agenda-group");
        assertThat(at.getValue()).isEqualTo("my_group");

        rule = rules.get( 1 );
        assertThat(rule.getName()).isEqualTo("rule2");
        attrs = rule.getAttributes();
        assertThat(attrs.size()).isEqualTo(2);
        at = attrs.get("salience");
        assertThat(at.getName()).isEqualTo("salience");
        assertThat(at.getValue()).isEqualTo("(Integer.MIN_VALUE)");
        at = attrs.get("no-loop");
        assertThat(at.getName()).isEqualTo("no-loop");

        rule = rules.get( 2 );
        assertThat(rule.getName()).isEqualTo("rule3");
        attrs = rule.getAttributes();
        assertThat(attrs.size()).isEqualTo(2);
        at = attrs.get("enabled");
        assertThat(at.getName()).isEqualTo("enabled");
        assertThat(at.getValue()).isEqualTo("(Boolean.TRUE)");
        at = attrs.get("activation-group");
        assertThat(at.getName()).isEqualTo("activation-group");
        assertThat(at.getValue()).isEqualTo("my_activation_group");

    }

    @Test
    public void testAttributeRefract() throws Exception {
        final String source = "rule Test refract when Person() then end";

        PackageDescr pkg = (PackageDescr) parse( "compilationUnit",
                                                 source );

        assertThat(parser.hasErrors()).as(parser.getErrors().toString()).isFalse();
        RuleDescr rule = pkg.getRules().get(0);

        assertThat(rule.getName()).isEqualTo("Test");
        Map<String, AttributeDescr> attributes = rule.getAttributes();
        assertThat(attributes.size()).isEqualTo(1);
        AttributeDescr refract = attributes.get( "refract" );
        assertThat(refract).isNotNull();
        assertThat(refract.getValue()).isEqualTo("true");

    }

    @Test
    public void testEnabledExpression() throws Exception {
        final RuleDescr rule = (RuleDescr) parseResource( "rule",
                                                          "rule_enabled_expression.drl" );
        assertThat(rule.getName()).isEqualTo("simple_rule");
        assertEqualsIgnoreWhitespace( "bar();",
                                      (String) rule.getConsequence() );

        final Map<String, AttributeDescr> attrs = rule.getAttributes();
        assertThat(attrs.size()).isEqualTo(3);

        AttributeDescr at = attrs.get("enabled");
        assertThat(at.getName()).isEqualTo("enabled");
        assertThat(at.getValue()).isEqualTo("( 1 + 1 == 2 )");

        at = attrs.get("salience");
        assertThat(at.getName()).isEqualTo("salience");
        assertThat(at.getValue()).isEqualTo("( 1+2 )");

        at = attrs.get("lock-on-active");
        assertThat(at.getName()).isEqualTo("lock-on-active");
        assertThat(at.getValue()).isEqualTo("true");
    }

    @Test
    public void testDurationExpression() throws Exception {
        final RuleDescr rule = (RuleDescr) parseResource( "rule",
                                                          "rule_duration_expression.drl" );
        assertThat(rule.getName()).isEqualTo("simple_rule");
        assertEqualsIgnoreWhitespace( "bar();",
                                      (String) rule.getConsequence() );

        final Map<String, AttributeDescr> attrs = rule.getAttributes();
        assertThat(attrs.size()).isEqualTo(2);

        AttributeDescr at = attrs.get("duration");
        assertThat(at.getName()).isEqualTo("duration");
        assertThat(at.getValue()).isEqualTo("1h30m");

        at = attrs.get("lock-on-active");
        assertThat(at.getName()).isEqualTo("lock-on-active");
        assertThat(at.getValue()).isEqualTo("true");
    }

    @Test
    public void testCalendars() throws Exception {
        final RuleDescr rule = (RuleDescr) parseResource( "rule",
                                                          "rule_calendars_attribute.drl" );
        assertThat(rule.getName()).isEqualTo("simple_rule");
        assertEqualsIgnoreWhitespace( "bar();",
                                      (String) rule.getConsequence() );

        final Map<String, AttributeDescr> attrs = rule.getAttributes();
        assertThat(attrs.size()).isEqualTo(2);

        AttributeDescr at = attrs.get("calendars");
        assertThat(at.getName()).isEqualTo("calendars");
        assertThat(at.getValue()).isEqualTo("[ \"cal1\" ]");

        at = attrs.get("lock-on-active");
        assertThat(at.getName()).isEqualTo("lock-on-active");
        assertThat(at.getValue()).isEqualTo("true");
    }

    @Test
    public void testCalendars2() throws Exception {
        final RuleDescr rule = (RuleDescr) parseResource( "rule",
                                                          "rule_calendars_attribute2.drl" );
        assertThat(parser.hasErrors()).as(parser.getErrors().toString()).isFalse();
        assertThat(rule.getName()).isEqualTo("simple_rule");
        assertEqualsIgnoreWhitespace( "bar();",
                                      (String) rule.getConsequence() );

        final Map<String, AttributeDescr> attrs = rule.getAttributes();
        assertThat(attrs.size()).isEqualTo(2);

        AttributeDescr at = attrs.get("calendars");
        assertThat(at.getName()).isEqualTo("calendars");
        assertThat(at.getValue()).isEqualTo("[ \"cal 1\", \"cal 2\", \"cal 3\" ]");

        at = attrs.get("lock-on-active");
        assertThat(at.getName()).isEqualTo("lock-on-active");
        assertThat(at.getValue()).isEqualTo("true");
    }

    @Test
    public void testAttributes_alternateSyntax() throws Exception {
        final RuleDescr rule = (RuleDescr) parseResource( "rule",
                                                          "rule_attributes_alt.drl" );
        assertThat(rule.getName()).isEqualTo("simple_rule");
        assertEqualsIgnoreWhitespace( "bar();",
                                      (String) rule.getConsequence() );

        final Map<String, AttributeDescr> attrs = rule.getAttributes();
        assertThat(attrs.size()).isEqualTo(6);

        AttributeDescr at = attrs.get("salience");
        assertThat(at.getName()).isEqualTo("salience");
        assertThat(at.getValue()).isEqualTo("42");

        at = attrs.get("agenda-group");
        assertThat(at.getName()).isEqualTo("agenda-group");
        assertThat(at.getValue()).isEqualTo("my_group");

        at = attrs.get("no-loop");
        assertThat(at.getName()).isEqualTo("no-loop");
        assertThat(at.getValue()).isEqualTo("true");

        at = attrs.get("lock-on-active");
        assertThat(at.getName()).isEqualTo("lock-on-active");
        assertThat(at.getValue()).isEqualTo("true");

        at = attrs.get("duration");
        assertThat(at.getName()).isEqualTo("duration");
        assertThat(at.getValue()).isEqualTo("42");

        at = attrs.get("activation-group");
        assertThat(at.getName()).isEqualTo("activation-group");
        assertThat(at.getValue()).isEqualTo("my_activation_group");
    }

    @Test
    public void testEnumeration() throws Exception {
        final RuleDescr rule = (RuleDescr) parseResource( "rule",
                                                          "enumeration.drl" );
        assertThat(rule.getName()).isEqualTo("simple_rule");
        assertThat(rule.getLhs().getDescrs().size()).isEqualTo(1);
        final PatternDescr col = (PatternDescr) rule.getLhs().getDescrs().get( 0 );
        assertThat(col.getObjectType()).isEqualTo("Foo");
        assertThat(col.getConstraint().getDescrs().size()).isEqualTo(1);
        final ExprConstraintDescr fld = (ExprConstraintDescr) col.getConstraint().getDescrs().get( 0 );

        assertThat(fld.getExpression()).isEqualTo("bar == Foo.BAR");
    }

    @Test
    public void testExtraLhsNewline() throws Exception {
        parseResource( "compilationUnit",
                       "extra_lhs_newline.drl" );
    }

    @Test
    public void testSoundsLike() throws Exception {
        final PackageDescr pkg = (PackageDescr) parseResource( "compilationUnit",
                                                               "soundslike_operator.drl" );

        RuleDescr rule = pkg.getRules().get(0);
        PatternDescr pat = (PatternDescr) rule.getLhs().getDescrs().get( 0 );

        pat.getConstraint();
    }

    @Test
    public void testPackageAttributes() throws Exception {
        final PackageDescr pkg = (PackageDescr) parseResource( "compilationUnit",
                                                               "package_attributes.drl" );

        AttributeDescr at = pkg.getAttributes().get(0);
        assertThat(at.getName()).isEqualTo("agenda-group");
        assertThat(at.getValue()).isEqualTo("x");
        at = pkg.getAttributes().get(1);
        assertThat(at.getName()).isEqualTo("dialect");
        assertThat(at.getValue()).isEqualTo("java");

        assertThat(pkg.getRules().size()).isEqualTo(2);

        assertThat(pkg.getImports().size()).isEqualTo(2);

        RuleDescr rule = pkg.getRules().get(0);
        assertThat(rule.getName()).isEqualTo("bar");
        at = rule.getAttributes().get("agenda-group");
        assertThat(at.getName()).isEqualTo("agenda-group");
        assertThat(at.getValue()).isEqualTo("x");
        at = rule.getAttributes().get("dialect");
        assertThat(at.getName()).isEqualTo("dialect");
        assertThat(at.getValue()).isEqualTo("java");

        rule = pkg.getRules().get(1);
        assertThat(rule.getName()).isEqualTo("baz");
        at = rule.getAttributes().get("dialect");
        assertThat(at.getName()).isEqualTo("dialect");
        assertThat(at.getValue()).isEqualTo("mvel");
        at = rule.getAttributes().get("agenda-group");
        assertThat(at.getName()).isEqualTo("agenda-group");
        assertThat(at.getValue()).isEqualTo("x");

    }

    @Test
    public void testStatementOrdering1() throws Exception {
        final PackageDescr pkg = (PackageDescr) parseResource( "compilationUnit",
                                                               "statement_ordering_1.drl" );

        assertThat(pkg.getRules().size()).isEqualTo(2);

        assertThat(pkg.getRules().get(0).getName()).isEqualTo("foo");
        assertThat(pkg.getRules().get(1).getName()).isEqualTo("bar");

        assertThat(pkg.getFunctions().size()).isEqualTo(2);

        assertThat(pkg.getFunctions().get(0).getName()).isEqualTo("cheeseIt");
        assertThat(pkg.getFunctions().get(1).getName()).isEqualTo("uncheeseIt");

        assertThat(pkg.getImports().size()).isEqualTo(4);
        assertThat(pkg.getImports().get(0).getTarget()).isEqualTo("im.one");
        assertThat(pkg.getImports().get(1).getTarget()).isEqualTo("im.two");
        assertThat(pkg.getImports().get(2).getTarget()).isEqualTo("im.three");
        assertThat(pkg.getImports().get(3).getTarget()).isEqualTo("im.four");
    }

    @Test
    public void testRuleNamesStartingWithNumbers() throws Exception {
        final PackageDescr pkg = (PackageDescr) parseResource( "compilationUnit",
                                                               "rule_names_number_prefix.drl" );

        assertThat(pkg.getRules().size()).isEqualTo(2);

        assertThat(pkg.getRules().get(0).getName()).isEqualTo("1. Do Stuff!");
        assertThat(pkg.getRules().get(1).getName()).isEqualTo("2. Do More Stuff!");
    }

    @Test
    public void testEvalWithNewline() throws Exception {
        parseResource( "compilationUnit",
                       "eval_with_newline.drl" );
    }

    @Test
    public void testEndPosition() throws Exception {
        final PackageDescr pkg = (PackageDescr) parseResource( "compilationUnit",
                                                               "test_EndPosition.drl" );
        final RuleDescr rule = pkg.getRules().get(0);
        final PatternDescr col = (PatternDescr) rule.getLhs().getDescrs().get( 0 );
        assertThat(col.getLine()).isEqualTo(25);
        assertThat(col.getEndLine()).isEqualTo(27);
    }

    @Test
    public void testQualifiedClassname() throws Exception {
        final PackageDescr pkg = (PackageDescr) parseResource( "compilationUnit",
                                                               "qualified_classname.drl" );

        final RuleDescr rule = pkg.getRules().get(0);

        final PatternDescr p = (PatternDescr) rule.getLhs().getDescrs().get( 0 );

        assertThat(p.getObjectType()).isEqualTo("com.cheeseco.Cheese");
    }

    @Test
    public void testGroupBy() throws Exception {
        final PackageDescr pkg = (PackageDescr) parseResource( "compilationUnit",
                "groupBy.drl" );

        assertThat(pkg.getRules().size()).isEqualTo(1);
        final RuleDescr rule = pkg.getRules().get(0);
        assertThat(rule.getLhs().getDescrs().size()).isEqualTo(1);

        final PatternDescr outPattern = (PatternDescr) rule.getLhs().getDescrs().get( 0 );
        final GroupByDescr groupBy = (GroupByDescr) outPattern.getSource();
        assertEqualsIgnoreWhitespace( "$initial",
                groupBy.getGroupingKey() );
        assertEqualsIgnoreWhitespace( "$p.getName().substring(0, 1)",
                groupBy.getGroupingFunction() );
        assertThat(groupBy.getActionCode()).isNull();
        assertThat(groupBy.getReverseCode()).isNull();
        assertThat(groupBy.getFunctions()).hasSize(2);
        assertEqualsIgnoreWhitespace( "sum",
                groupBy.getFunctions().get(0).getFunction() );
        assertEqualsIgnoreWhitespace( "count",
                groupBy.getFunctions().get(1).getFunction() );

        assertThat(groupBy.getFunctions().get(0).getParams()).hasSize(1);
        assertEqualsIgnoreWhitespace("$age",
                groupBy.getFunctions().get(0).getParams()[0]);

        assertThat(groupBy.getFunctions().get(1).getParams()).hasSize(0);

        assertThat(groupBy.isExternalFunction()).isTrue();

        final PatternDescr pattern = groupBy.getInputPattern();
        assertThat(pattern.getObjectType()).isEqualTo("Person");
        assertThat(pattern.getConstraint().getDescrs()).hasSize(1);
        assertEqualsIgnoreWhitespace("$age : age < 30",
                pattern.getConstraint().getDescrs().get(0).getText());

        assertThat(pattern.getConstraint().getDescrs()).hasSize(1);
        assertEqualsIgnoreWhitespace("$age : age < 30",
                pattern.getConstraint().getDescrs().get(0).getText());

        assertThat(outPattern.getConstraint().getDescrs()).hasSize(1);
        assertEqualsIgnoreWhitespace("$sumOfAges > 10",
                outPattern.getConstraint().getDescrs().get(0).getText());
    }

    @Test
    public void testAccumulate() throws Exception {
        final PackageDescr pkg = (PackageDescr) parseResource( "compilationUnit",
                                                               "accumulate.drl" );

        assertThat(pkg.getRules().size()).isEqualTo(1);
        final RuleDescr rule = pkg.getRules().get(0);
        assertThat(rule.getLhs().getDescrs().size()).isEqualTo(1);

        final PatternDescr outPattern = (PatternDescr) rule.getLhs().getDescrs().get( 0 );
        final AccumulateDescr accum = (AccumulateDescr) outPattern.getSource();
        assertEqualsIgnoreWhitespace( "int x = 0 ;",
                                      accum.getInitCode() );
        assertEqualsIgnoreWhitespace( "x++;",
                                      accum.getActionCode() );
        assertThat(accum.getReverseCode()).isNull();
        assertEqualsIgnoreWhitespace( "new Integer(x)",
                                      accum.getResultCode() );

        assertThat(accum.isExternalFunction()).isFalse();

        final PatternDescr pattern = accum.getInputPattern();
        assertThat(pattern.getObjectType()).isEqualTo("Person");
    }

    @Test
    public void testAccumulateWithBindings() throws Exception {
        final PackageDescr pkg = (PackageDescr) parseResource( "compilationUnit",
                                                               "accumulate_with_bindings.drl" );

        assertThat(pkg.getRules().size()).isEqualTo(1);
        final RuleDescr rule = pkg.getRules().get(0);
        assertThat(rule.getLhs().getDescrs().size()).isEqualTo(1);

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

        final PatternDescr pattern = accum.getInputPattern();
        assertThat(pattern.getObjectType()).isEqualTo("Person");
    }

    @Test
    public void testCollect() throws Exception {
        final PackageDescr pkg = (PackageDescr) parseResource( "compilationUnit",
                                                               "collect.drl" );

        assertThat(pkg.getRules().size()).isEqualTo(1);
        final RuleDescr rule = pkg.getRules().get(0);
        assertThat(rule.getLhs().getDescrs().size()).isEqualTo(1);

        final PatternDescr outPattern = (PatternDescr) rule.getLhs().getDescrs().get( 0 );
        final CollectDescr collect = (CollectDescr) outPattern.getSource();

        final PatternDescr pattern = collect.getInputPattern();
        assertThat(pattern.getObjectType()).isEqualTo("Person");
    }

    @Test
    public void testPredicate2() throws Exception {
        // predicates are also prefixed by the eval keyword
        final RuleDescr rule = (RuleDescr) parse( "rule",
                                                  "rule X when Foo(eval( $var.equals(\"xyz\") )) then end" );

        final PatternDescr pattern = (PatternDescr) rule.getLhs().getDescrs().get( 0 );
        final List< ? > constraints = pattern.getConstraint().getDescrs();
        assertThat(constraints.size()).isEqualTo(1);

        final ExprConstraintDescr predicate = (ExprConstraintDescr) constraints.get( 0 );
        assertThat(predicate.getExpression()).isEqualTo("eval( $var.equals(\"xyz\") )");
    }

    @Test
    public void testEscapedStrings() throws Exception {
        final RuleDescr rule = (RuleDescr) parseResource( "rule",
                                                          "escaped-string.drl" );

        assertThat(rule).isNotNull();

        assertThat(rule.getName()).isEqualTo("test_Quotes");

        final String expected = "String s = \"\\\"\\n\\t\\\\\";";

        assertEqualsIgnoreWhitespace( expected,
                                      (String) rule.getConsequence() );
    }

    @Test
    public void testNestedCEs() throws Exception {
        final RuleDescr rule = (RuleDescr) parseResource( "rule",
                                                          "nested_conditional_elements.drl" );

        assertThat(rule).isNotNull();

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

        assertThat("State").isEqualTo(state.getObjectType());
        assertThat("Person").isEqualTo(person.getObjectType());
        assertThat("Cheese").isEqualTo(cheese.getObjectType());
        assertThat("Person").isEqualTo(person2.getObjectType());
        assertThat("Cheese").isEqualTo(cheese2.getObjectType());
        assertThat("Cheese").isEqualTo(cheese3.getObjectType());
    }

    @Test
    public void testForall() throws Exception {
        final PackageDescr pkg = (PackageDescr) parseResource( "compilationUnit",
                                                               "forall.drl" );

        assertThat(pkg.getRules().size()).isEqualTo(1);
        final RuleDescr rule = pkg.getRules().get(0);
        assertThat(rule.getLhs().getDescrs().size()).isEqualTo(1);

        final ForallDescr forall = (ForallDescr) rule.getLhs().getDescrs().get( 0 );

        assertThat(forall.getDescrs().size()).isEqualTo(2);
        final PatternDescr pattern = forall.getBasePattern();
        assertThat(pattern.getObjectType()).isEqualTo("Person");
        final List<BaseDescr> remaining = forall.getRemainingPatterns();
        assertThat(remaining.size()).isEqualTo(1);
        final PatternDescr cheese = (PatternDescr) remaining.get( 0 );
        assertThat(cheese.getObjectType()).isEqualTo("Cheese");
    }

    @Test
    public void testForallWithFrom() throws Exception {
        final PackageDescr pkg = (PackageDescr) parseResource( "compilationUnit",
                                                               "forallwithfrom.drl" );

        assertThat(pkg.getRules().size()).isEqualTo(1);
        final RuleDescr rule = pkg.getRules().get(0);
        assertThat(rule.getLhs().getDescrs().size()).isEqualTo(1);

        final ForallDescr forall = (ForallDescr) rule.getLhs().getDescrs().get( 0 );

        assertThat(forall.getDescrs().size()).isEqualTo(2);
        final PatternDescr pattern = forall.getBasePattern();
        assertThat(pattern.getObjectType()).isEqualTo("Person");
        assertThat(((FromDescr) pattern.getSource()).getDataSource().toString()).isEqualTo("$village");
        final List<BaseDescr> remaining = forall.getRemainingPatterns();
        assertThat(remaining.size()).isEqualTo(1);
        final PatternDescr cheese = (PatternDescr) remaining.get( 0 );
        assertThat(cheese.getObjectType()).isEqualTo("Cheese");
        assertThat(((FromDescr) cheese.getSource()).getDataSource().toString()).isEqualTo("$cheesery");
    }

    @Test
    public void testMemberof() throws Exception {
        final String text = "rule X when Country( $cities : city )\nPerson( city memberOf $cities )\n then end";
        AndDescr descrs = ((RuleDescr) parse( "rule",
                                              text )).getLhs();

        assertThat(descrs.getDescrs().size()).isEqualTo(2);
        PatternDescr pat = (PatternDescr) descrs.getDescrs().get( 1 );
        ExprConstraintDescr fieldConstr = (ExprConstraintDescr) pat.getConstraint().getDescrs().get( 0 );

        assertThat(fieldConstr.getExpression()).isEqualTo("city memberOf $cities");
    }

    @Test
    public void testNotMemberof() throws Exception {
        final String text = "rule X when Country( $cities : city )\nPerson( city not memberOf $cities ) then end\n";
        AndDescr descrs = ((RuleDescr) parse( "rule",
                                              text )).getLhs();

        assertThat(descrs.getDescrs().size()).isEqualTo(2);
        PatternDescr pat = (PatternDescr) descrs.getDescrs().get( 1 );
        ExprConstraintDescr fieldConstr = (ExprConstraintDescr) pat.getConstraint().getDescrs().get( 0 );

        assertThat(fieldConstr.getExpression()).isEqualTo("city not memberOf $cities");
    }

    @Test
    public void testInOperator() throws Exception {
        final RuleDescr rule = (RuleDescr) parseResource( "rule",
                                                          "in_operator_test.drl" );

        assertThat(rule).isNotNull();

        assertEqualsIgnoreWhitespace( "consequence();",
                                      (String) rule.getConsequence() );
        assertThat(rule.getName()).isEqualTo("simple_rule");
        assertThat(rule.getLhs().getDescrs().size()).isEqualTo(2);

        // The first pattern, with 2 restrictions on a single field (plus a
        // connective)
        PatternDescr pattern = (PatternDescr) rule.getLhs().getDescrs().get( 0 );
        assertThat(pattern.getObjectType()).isEqualTo("Person");
        assertThat(pattern.getConstraint().getDescrs().size()).isEqualTo(1);

        ExprConstraintDescr fld = (ExprConstraintDescr) pattern.getConstraint().getDescrs().get( 0 );
        assertThat(fld.getExpression()).isEqualTo("age > 30 && < 40");

        // the second col, with 2 fields, the first with 2 restrictions, the
        // second field with one
        pattern = (PatternDescr) rule.getLhs().getDescrs().get( 1 );
        assertThat(pattern.getObjectType()).isEqualTo("Vehicle");
        assertThat(pattern.getConstraint().getDescrs().size()).isEqualTo(2);

        fld = (ExprConstraintDescr) pattern.getConstraint().getDescrs().get( 0 );
        assertThat(fld.getExpression()).isEqualTo("type in ( \"sedan\", \"wagon\" )");

        // now the second field
        fld = (ExprConstraintDescr) pattern.getConstraint().getDescrs().get( 1 );
        assertThat(fld.getExpression()).isEqualTo("age < 3");

    }

    @Test
    public void testNotInOperator() throws Exception {
        final RuleDescr rule = (RuleDescr) parseResource( "rule",
                                                          "notin_operator_test.drl" );

        assertThat(rule).isNotNull();

        assertEqualsIgnoreWhitespace( "consequence();",
                                      (String) rule.getConsequence() );
        assertThat(rule.getName()).isEqualTo("simple_rule");
        assertThat(rule.getLhs().getDescrs().size()).isEqualTo(2);

        // The first pattern, with 2 restrictions on a single field (plus a
        // connective)
        PatternDescr pattern = (PatternDescr) rule.getLhs().getDescrs().get( 0 );
        assertThat(pattern.getObjectType()).isEqualTo("Person");
        assertThat(pattern.getConstraint().getDescrs().size()).isEqualTo(1);

        ExprConstraintDescr fld = (ExprConstraintDescr) pattern.getConstraint().getDescrs().get( 0 );
        assertThat(fld.getExpression()).isEqualTo("age > 30 && < 40");

        // the second col, with 2 fields, the first with 2 restrictions, the
        // second field with one
        pattern = (PatternDescr) rule.getLhs().getDescrs().get( 1 );
        assertThat(pattern.getObjectType()).isEqualTo("Vehicle");
        assertThat(pattern.getConstraint().getDescrs().size()).isEqualTo(2);

        fld = (ExprConstraintDescr) pattern.getConstraint().getDescrs().get( 0 );
        assertThat(fld.getExpression()).isEqualTo("type not in ( \"sedan\", \"wagon\" )");

        // now the second field
        fld = (ExprConstraintDescr) pattern.getConstraint().getDescrs().get( 1 );
        assertThat(fld.getExpression()).isEqualTo("age < 3");

    }

    @Test
    public void testCheckOrDescr() throws Exception {
        final String text = "rule X when Person( eval( age == 25 ) || ( eval( name.equals( \"bob\" ) ) && eval( age == 30 ) ) ) then end";
        PatternDescr pattern = (PatternDescr) ((RuleDescr) parse( "rule",
                                                                  text )).getLhs().getDescrs().get( 0 );

        assertThat(pattern.getDescrs().size()).isEqualTo(1);
        assertThat(AndDescr.class).isEqualTo(pattern.getConstraint().getClass());

        assertThat(pattern.getConstraint().getDescrs().get(0).getClass()).isEqualTo(ExprConstraintDescr.class);

    }

    @Test
    public void testConstraintAndConnective() throws Exception {
        final String text = "rule X when Person( age < 42 && location==\"atlanta\") then end";
        PatternDescr pattern = (PatternDescr) ((RuleDescr) parse( "rule",
                                                                  text )).getLhs().getDescrs().get( 0 );

        assertThat(pattern.getDescrs().size()).isEqualTo(1);
        ExprConstraintDescr fcd = (ExprConstraintDescr) pattern.getDescrs().get( 0 );
        assertThat(fcd.getExpression()).isEqualTo("age < 42 && location==\"atlanta\"");
    }

    @Test
    public void testConstraintOrConnective() throws Exception {
        final String text = "rule X when Person( age < 42 || location==\"atlanta\") then end";
        PatternDescr pattern = (PatternDescr) ((RuleDescr) parse( "rule",
                                                                  text )).getLhs().getDescrs().get( 0 );

        assertThat(pattern.getDescrs().size()).isEqualTo(1);
        ExprConstraintDescr fcd = (ExprConstraintDescr) pattern.getDescrs().get( 0 );
        assertThat(fcd.getExpression()).isEqualTo("age < 42 || location==\"atlanta\"");
    }

    @Test
    public void testRestrictions() throws Exception {
        final String text = "rule X when Foo( bar > 1 || == 1 ) then end\n";

        AndDescr descrs = ((RuleDescr) parse("rule",
                                             text)).getLhs();

        assertThat(descrs.getDescrs().size()).isEqualTo(1);
        PatternDescr pat = (PatternDescr) descrs.getDescrs().get( 0 );
        ExprConstraintDescr fieldConstr = (ExprConstraintDescr) pat.getConstraint().getDescrs().get( 0 );

        assertThat(fieldConstr.getExpression()).isEqualTo("bar > 1 || == 1");
    }

    @Test
    public void testSemicolon() throws Exception {
        final PackageDescr pkg = (PackageDescr) parseResource( "compilationUnit",
                                                               "semicolon.drl" );

        assertThat(parser.hasErrors()).as(parser.getErrors().toString()).isFalse();

        assertThat(pkg.getName()).isEqualTo("org.drools.mvel.compiler");
        assertThat(pkg.getGlobals().size()).isEqualTo(1);
        assertThat(pkg.getRules().size()).isEqualTo(3);

        final RuleDescr rule1 = pkg.getRules().get(0);
        assertThat(rule1.getLhs().getDescrs().size()).isEqualTo(2);

        final RuleDescr query1 = pkg.getRules().get(1);
        assertThat(query1.getLhs().getDescrs().size()).isEqualTo(3);

        final RuleDescr rule2 = pkg.getRules().get(2);
        assertThat(rule2.getLhs().getDescrs().size()).isEqualTo(2);
    }

    @Test
    public void testEval() throws Exception {
        final PackageDescr pkg = (PackageDescr) parseResource( "compilationUnit",
                                                               "eval_parsing.drl" );

        assertThat(pkg.getName()).isEqualTo("org.drools.mvel.compiler");
        assertThat(pkg.getRules().size()).isEqualTo(1);

        final RuleDescr rule1 = pkg.getRules().get(0);
        assertThat(rule1.getLhs().getDescrs().size()).isEqualTo(1);
    }

    @Test
    public void testAccumulateReverse() throws Exception {
        final PackageDescr pkg = (PackageDescr) parseResource( "compilationUnit",
                                                               "accumulateReverse.drl" );

        assertThat(pkg.getRules().size()).isEqualTo(1);
        final RuleDescr rule = pkg.getRules().get(0);
        assertThat(rule.getLhs().getDescrs().size()).isEqualTo(1);

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
        assertThat(accum.isExternalFunction()).isFalse();

        final PatternDescr pattern = accum.getInputPattern();
        assertThat(pattern.getObjectType()).isEqualTo("Person");
    }

    @Test
    public void testAccumulateExternalFunction() throws Exception {
        final PackageDescr pkg = (PackageDescr) parseResource( "compilationUnit",
                                                               "accumulateExternalFunction.drl" );

        assertThat(pkg.getRules().size()).isEqualTo(1);
        final RuleDescr rule = pkg.getRules().get(0);
        assertThat(rule.getLhs().getDescrs().size()).isEqualTo(1);

        final PatternDescr out = (PatternDescr) rule.getLhs().getDescrs().get( 0 );
        final AccumulateDescr accum = (AccumulateDescr) out.getSource();
        assertEqualsIgnoreWhitespace( "$age",
                                      accum.getFunctions().get( 0 ).getParams()[0] );
        assertEqualsIgnoreWhitespace( "average",
                                      accum.getFunctions().get( 0 ).getFunction() );
        assertThat(accum.isExternalFunction()).isTrue();

        final PatternDescr pattern = accum.getInputPattern();
        assertThat(pattern.getObjectType()).isEqualTo("Person");
    }

    @Test
    public void testCollectWithNestedFrom() throws Exception {
        final PackageDescr pkg = (PackageDescr) parseResource( "compilationUnit",
                                                               "collect_with_nested_from.drl" );

        assertThat(pkg.getRules().size()).isEqualTo(1);
        final RuleDescr rule = pkg.getRules().get(0);
        assertThat(rule.getLhs().getDescrs().size()).isEqualTo(1);

        final PatternDescr out = (PatternDescr) rule.getLhs().getDescrs().get( 0 );
        final CollectDescr collect = (CollectDescr) out.getSource();

        PatternDescr person = collect.getInputPattern();
        assertThat(person.getObjectType()).isEqualTo("Person");

        final CollectDescr collect2 = (CollectDescr) person.getSource();

        final PatternDescr people = collect2.getInputPattern();
        assertThat(people.getObjectType()).isEqualTo("People");
    }

    @Test
    public void testAccumulateWithNestedFrom() throws Exception {
        final PackageDescr pkg = (PackageDescr) parseResource( "compilationUnit",
                                                               "accumulate_with_nested_from.drl" );

        assertThat(pkg.getRules().size()).isEqualTo(1);
        final RuleDescr rule = pkg.getRules().get(0);
        assertThat(rule.getLhs().getDescrs().size()).isEqualTo(1);

        final PatternDescr out = (PatternDescr) rule.getLhs().getDescrs().get( 0 );
        final AccumulateDescr accumulate = (AccumulateDescr) out.getSource();

        PatternDescr person = accumulate.getInputPattern();
        assertThat(person.getObjectType()).isEqualTo("Person");

        final CollectDescr collect2 = (CollectDescr) person.getSource();

        final PatternDescr people = collect2.getInputPattern();
        assertThat(people.getObjectType()).isEqualTo("People");
    }

    @Test
    public void testAccumulateMultipleFunctions() throws Exception {
        final PackageDescr pkg = (PackageDescr) parseResource( "compilationUnit",
                                                               "accumulateMultipleFunctions.drl" );

        assertThat(pkg.getRules().size()).isEqualTo(1);

        RuleDescr rule = pkg.getRules().get(0);
        assertThat(rule.getLhs().getDescrs().size()).isEqualTo(1);

        PatternDescr out = (PatternDescr) rule.getLhs().getDescrs().get( 0 );
        assertThat(out.getObjectType()).isEqualTo("Object");
        AccumulateDescr accum = (AccumulateDescr) out.getSource();
        assertThat(accum.isExternalFunction()).isTrue();

        List<AccumulateDescr.AccumulateFunctionCallDescr> functions = accum.getFunctions();
        assertThat(functions.size()).isEqualTo(3);
        assertThat(functions.get(0).getFunction()).isEqualTo("average");
        assertThat(functions.get(0).getBind()).isEqualTo("$a1");
        assertThat(functions.get(0).getParams()[0]).isEqualTo("$price");

        assertThat(functions.get(1).getFunction()).isEqualTo("min");
        assertThat(functions.get(1).getBind()).isEqualTo("$m1");
        assertThat(functions.get(1).getParams()[0]).isEqualTo("$price");

        assertThat(functions.get(2).getFunction()).isEqualTo("max");
        assertThat(functions.get(2).getBind()).isEqualTo("$M1");
        assertThat(functions.get(2).getParams()[0]).isEqualTo("$price");

        final PatternDescr pattern = accum.getInputPattern();
        assertThat(pattern.getObjectType()).isEqualTo("Cheese");
    }

    @Test
    public void testAccumulateMnemonic() throws Exception {
        String drl = "package org.drools.mvel.compiler\n" +
                "rule \"Accumulate 1\"\n" + 
                "when\n" + 
                "     acc( Cheese( $price : price ),\n" + 
                "          $a1 : average( $price ) )\n" + 
                "then\n" + 
                "end\n";
        final PackageDescr pkg = (PackageDescr) parse( "compilationUnit",
                                                        drl );

        assertThat(pkg.getRules().size()).isEqualTo(1);

        RuleDescr rule = pkg.getRules().get(0);
        assertThat(rule.getLhs().getDescrs().size()).isEqualTo(1);

        PatternDescr out = (PatternDescr) rule.getLhs().getDescrs().get( 0 );
        assertThat(out.getObjectType()).isEqualTo("Object");
        AccumulateDescr accum = (AccumulateDescr) out.getSource();
        assertThat(accum.isExternalFunction()).isTrue();

        List<AccumulateDescr.AccumulateFunctionCallDescr> functions = accum.getFunctions();
        assertThat(functions.size()).isEqualTo(1);
        assertThat(functions.get(0).getFunction()).isEqualTo("average");
        assertThat(functions.get(0).getBind()).isEqualTo("$a1");
        assertThat(functions.get(0).getParams()[0]).isEqualTo("$price");

        final PatternDescr pattern = accum.getInputPattern();
        assertThat(pattern.getObjectType()).isEqualTo("Cheese");
    }
    
    @Test
    public void testAccumulateMnemonic2() throws Exception {
        String drl = "package org.drools.mvel.compiler\n" +
                "rule \"Accumulate 1\"\n" + 
                "when\n" + 
                "     Number() from acc( Cheese( $price : price ),\n" + 
                "                        average( $price ) )\n" + 
                "then\n" + 
                "end\n";
        final PackageDescr pkg = (PackageDescr) parse( "compilationUnit",
                                                        drl );

        assertThat(pkg.getRules().size()).isEqualTo(1);

        RuleDescr rule = pkg.getRules().get(0);
        assertThat(rule.getLhs().getDescrs().size()).isEqualTo(1);

        PatternDescr out = (PatternDescr) rule.getLhs().getDescrs().get( 0 );
        assertThat(out.getObjectType()).isEqualTo("Number");
        AccumulateDescr accum = (AccumulateDescr) out.getSource();
        assertThat(accum.isExternalFunction()).isTrue();

        List<AccumulateDescr.AccumulateFunctionCallDescr> functions = accum.getFunctions();
        assertThat(functions.size()).isEqualTo(1);
        assertThat(functions.get(0).getFunction()).isEqualTo("average");
        assertThat(functions.get(0).getParams()[0]).isEqualTo("$price");

        final PatternDescr pattern = accum.getInputPattern();
        assertThat(pattern.getObjectType()).isEqualTo("Cheese");
    }

    @Test
    public void testImportAccumulate() throws Exception {
        String drl = "package org.drools.mvel.compiler\n" +
                "import acc foo.Bar baz\n" +
                "import accumulate foo.Bar2 baz2\n" + 
                "rule \"Accumulate 1\"\n" + 
                "when\n" + 
                "     acc( Cheese( $price : price ),\n" + 
                "          $v1 : baz( $price ), \n" +
                "          $v2 : baz2( $price ) )\n" + 
                "then\n" + 
                "end\n";
        final PackageDescr pkg = (PackageDescr) parse( "compilationUnit",
                                                        drl );

        assertThat(pkg.getAccumulateImports().size()).isEqualTo(2);
        AccumulateImportDescr imp = pkg.getAccumulateImports().get(0);
        assertThat(imp.getTarget()).isEqualTo("foo.Bar");
        assertThat(imp.getFunctionName()).isEqualTo("baz");

        imp = pkg.getAccumulateImports().get(1);
        assertThat(imp.getTarget()).isEqualTo("foo.Bar2");
        assertThat(imp.getFunctionName()).isEqualTo("baz2");

        assertThat(pkg.getRules().size()).isEqualTo(1);

        RuleDescr rule = pkg.getRules().get(0);
        assertThat(rule.getLhs().getDescrs().size()).isEqualTo(1);

        PatternDescr out = (PatternDescr) rule.getLhs().getDescrs().get( 0 );
        assertThat(out.getObjectType()).isEqualTo("Object");
        AccumulateDescr accum = (AccumulateDescr) out.getSource();
        assertThat(accum.isExternalFunction()).isTrue();

        List<AccumulateDescr.AccumulateFunctionCallDescr> functions = accum.getFunctions();
        assertThat(functions.size()).isEqualTo(2);
        assertThat(functions.get(0).getFunction()).isEqualTo("baz");
        assertThat(functions.get(0).getBind()).isEqualTo("$v1");
        assertThat(functions.get(0).getParams()[0]).isEqualTo("$price");

        assertThat(functions.get(1).getFunction()).isEqualTo("baz2");
        assertThat(functions.get(1).getBind()).isEqualTo("$v2");
        assertThat(functions.get(1).getParams()[0]).isEqualTo("$price");

        final PatternDescr pattern = accum.getInputPattern();
        assertThat(pattern.getObjectType()).isEqualTo("Cheese");
    }

    @Test
    public void testAccumulateMultipleFunctionsConstraint() throws Exception {
        final PackageDescr pkg = (PackageDescr) parseResource( "compilationUnit",
                                                               "accumulateMultipleFunctionsConstraint.drl" );

        assertThat(pkg.getRules().size()).isEqualTo(1);

        RuleDescr rule = pkg.getRules().get(0);
        assertThat(rule.getLhs().getDescrs().size()).isEqualTo(1);

        PatternDescr out = (PatternDescr) rule.getLhs().getDescrs().get( 0 );
        assertThat(out.getObjectType()).isEqualTo("Object");
        assertThat(out.getConstraint().getDescrs().size()).isEqualTo(2);
        assertThat(out.getConstraint().getDescrs().get(0).toString()).isEqualTo("$a1 > 10 && $M1 <= 100");
        assertThat(out.getConstraint().getDescrs().get(1).toString()).isEqualTo("$m1 == 5");
        AccumulateDescr accum = (AccumulateDescr) out.getSource();
        assertThat(accum.isExternalFunction()).isTrue();

        List<AccumulateDescr.AccumulateFunctionCallDescr> functions = accum.getFunctions();
        assertThat(functions.size()).isEqualTo(3);
        assertThat(functions.get(0).getFunction()).isEqualTo("average");
        assertThat(functions.get(0).getBind()).isEqualTo("$a1");
        assertThat(functions.get(0).getParams()[0]).isEqualTo("$price");

        assertThat(functions.get(1).getFunction()).isEqualTo("min");
        assertThat(functions.get(1).getBind()).isEqualTo("$m1");
        assertThat(functions.get(1).getParams()[0]).isEqualTo("$price");

        assertThat(functions.get(2).getFunction()).isEqualTo("max");
        assertThat(functions.get(2).getBind()).isEqualTo("$M1");
        assertThat(functions.get(2).getParams()[0]).isEqualTo("$price");

        final PatternDescr pattern = accum.getInputPattern();
        assertThat(pattern.getObjectType()).isEqualTo("Cheese");
    }
    
    @Test
    public void testOrCE() throws Exception {
        final PackageDescr pkg = (PackageDescr) parseResource( "compilationUnit",
                                                               "or_ce.drl" );

        assertThat(pkg.getRules().size()).isEqualTo(1);
        final RuleDescr rule = pkg.getRules().get(0);
        assertThat(rule.getLhs().getDescrs().size()).isEqualTo(2);

        final PatternDescr person = (PatternDescr) rule.getLhs().getDescrs().get( 0 );
        assertThat(person.getObjectType()).isEqualTo("Person");
        assertThat(person.getIdentifier()).isEqualTo("$p");

        final OrDescr or = (OrDescr) rule.getLhs().getDescrs().get( 1 );
        assertThat(or.getDescrs().size()).isEqualTo(2);

        final PatternDescr cheese1 = (PatternDescr) or.getDescrs().get( 0 );
        assertThat(cheese1.getObjectType()).isEqualTo("Cheese");
        assertThat(cheese1.getIdentifier()).isEqualTo("$c");
        final PatternDescr cheese2 = (PatternDescr) or.getDescrs().get( 1 );
        assertThat(cheese2.getObjectType()).isEqualTo("Cheese");
        assertThat(cheese2.getIdentifier()).isNull();
    }

    @Test
    public void testRuleSingleLine() throws Exception {
        final String text = "rule \"another test\" salience 10 when eval( true ) then System.out.println(1); end";
        RuleDescr rule = (RuleDescr) parse( "rule",
                                            text );

        assertThat(rule.getName()).isEqualTo("another test");
        assertThat(rule.getConsequence()).isEqualTo("System.out.println(1); ");
    }

    @Test
    public void testRuleTwoLines() throws Exception {
        final String text = "rule \"another test\" salience 10 when eval( true ) then System.out.println(1);\n end";
        RuleDescr rule = (RuleDescr) parse( "rule",
                                            text );

        assertThat(rule.getName()).isEqualTo("another test");
        assertThat(rule.getConsequence()).isEqualTo("System.out.println(1);\n ");
    }

    @Test
    public void testRuleParseLhs3() throws Exception {
        final String text = "rule X when (or\nnot Person()\n(and Cheese()\nMeat()\nWine())) then end";
        AndDescr pattern = ((RuleDescr) parse( "rule",
                                               text )).getLhs();

        assertThat(pattern.getDescrs().size()).isEqualTo(1);
        OrDescr or = (OrDescr) pattern.getDescrs().get( 0 );
        assertThat(or.getDescrs().size()).isEqualTo(2);
        NotDescr not = (NotDescr) or.getDescrs().get( 0 );
        AndDescr and = (AndDescr) or.getDescrs().get( 1 );
        assertThat(not.getDescrs().size()).isEqualTo(1);
        PatternDescr person = (PatternDescr) not.getDescrs().get( 0 );
        assertThat(person.getObjectType()).isEqualTo("Person");
        assertThat(and.getDescrs().size()).isEqualTo(3);
        PatternDescr cheese = (PatternDescr) and.getDescrs().get( 0 );
        assertThat(cheese.getObjectType()).isEqualTo("Cheese");
        PatternDescr meat = (PatternDescr) and.getDescrs().get( 1 );
        assertThat(meat.getObjectType()).isEqualTo("Meat");
        PatternDescr wine = (PatternDescr) and.getDescrs().get( 2 );
        assertThat(wine.getObjectType()).isEqualTo("Wine");

    }

    @Test
    public void testAccumulateMultiPattern() throws Exception {
        final PackageDescr pkg = (PackageDescr) parseResource( "compilationUnit",
                                                               "accumulate_multi_pattern.drl" );

        assertThat(pkg.getRules().size()).isEqualTo(1);
        final RuleDescr rule = pkg.getRules().get(0);
        assertThat(rule.getLhs().getDescrs().size()).isEqualTo(1);

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
        assertThat(and.getDescrs().size()).isEqualTo(2);
        final PatternDescr person = (PatternDescr) and.getDescrs().get( 0 );
        final PatternDescr cheese = (PatternDescr) and.getDescrs().get( 1 );
        assertThat(person.getObjectType()).isEqualTo("Person");
        assertThat(cheese.getObjectType()).isEqualTo("Cheese");
    }

    @Test
    public void testPluggableOperators() throws Exception {

        final PackageDescr pkg = (PackageDescr) parseResource( "compilationUnit",
                                                               "pluggable_operators.drl" );

        assertThat(parser.hasErrors()).as(parser.getErrors().toString()).isFalse();

        assertThat(pkg.getRules().size()).isEqualTo(1);
        final RuleDescr rule = pkg.getRules().get(0);
        assertThat(rule.getLhs().getDescrs().size()).isEqualTo(5);

        final PatternDescr eventA = (PatternDescr) rule.getLhs().getDescrs().get( 0 );
        assertThat(eventA.getIdentifier()).isEqualTo("$a");
        assertThat(eventA.getObjectType()).isEqualTo("EventA");

        final PatternDescr eventB = (PatternDescr) rule.getLhs().getDescrs().get( 1 );
        assertThat(eventB.getIdentifier()).isEqualTo("$b");
        assertThat(eventB.getObjectType()).isEqualTo("EventB");
        assertThat(eventB.getConstraint().getDescrs().size()).isEqualTo(1);
        assertThat(eventB.getConstraint().getDescrs().size()).isEqualTo(1);

        final ExprConstraintDescr fcdB = (ExprConstraintDescr) eventB.getConstraint().getDescrs().get( 0 );
        assertThat(fcdB.getExpression()).isEqualTo("this after[1,10] $a || this not after[15,20] $a");

        final PatternDescr eventC = (PatternDescr) rule.getLhs().getDescrs().get( 2 );
        assertThat(eventC.getIdentifier()).isEqualTo("$c");
        assertThat(eventC.getObjectType()).isEqualTo("EventC");
        assertThat(eventC.getConstraint().getDescrs().size()).isEqualTo(1);
        final ExprConstraintDescr fcdC = (ExprConstraintDescr) eventC.getConstraint().getDescrs().get( 0 );
        assertThat(fcdC.getExpression()).isEqualTo("this finishes $b");

        final PatternDescr eventD = (PatternDescr) rule.getLhs().getDescrs().get( 3 );
        assertThat(eventD.getIdentifier()).isEqualTo("$d");
        assertThat(eventD.getObjectType()).isEqualTo("EventD");
        assertThat(eventD.getConstraint().getDescrs().size()).isEqualTo(1);
        final ExprConstraintDescr fcdD = (ExprConstraintDescr) eventD.getConstraint().getDescrs().get( 0 );
        assertThat(fcdD.getExpression()).isEqualTo("this not starts $a");

        final PatternDescr eventE = (PatternDescr) rule.getLhs().getDescrs().get( 4 );
        assertThat(eventE.getIdentifier()).isEqualTo("$e");
        assertThat(eventE.getObjectType()).isEqualTo("EventE");
        assertThat(eventE.getConstraint().getDescrs().size()).isEqualTo(1);

        ExprConstraintDescr fcdE = (ExprConstraintDescr) eventE.getConstraint().getDescrs().get( 0 );
        assertThat(fcdE.getExpression()).isEqualTo("this not before[1, 10] $b || after[1, 10] $c && this after[1, 5] $d");
    }

    @Test
    public void testRuleMetadata() throws Exception {
        final PackageDescr pkg = (PackageDescr) parseResource( "compilationUnit",
                                                               "Rule_with_Metadata.drl" );
        assertThat(parser.hasErrors()).as(parser.getErrors().toString()).isFalse();

        // @fooAttribute(barValue)
        // @fooAtt2(barVal2)
        RuleDescr rule = pkg.getRules().get( 0 );
        assertThat(rule.getAnnotationNames().contains("fooMeta1")).isTrue();
        assertThat(rule.getAnnotation("fooMeta1").getValue()).isEqualTo("barVal1");
        assertThat(rule.getAnnotationNames().contains("fooMeta2")).isTrue();
        assertThat(rule.getAnnotation("fooMeta2").getValue()).isEqualTo("barVal2");
        assertEqualsIgnoreWhitespace( "System.out.println(\"Consequence\");",
                                      (String) rule.getConsequence() );
    }

    @Test
    public void testRuleExtends() throws Exception {
        final PackageDescr pkg = (PackageDescr) parseResource( "compilationUnit",
                                                               "Rule_with_Extends.drl" );

        assertThat(parser.hasErrors()).as(parser.getErrors().toString()).isFalse();

        RuleDescr rule = pkg.getRules().get( 0 );
        assertThat(rule.getParentName() != null).isTrue();
        assertThat(rule.getParentName()).isEqualTo("rule1");

        AndDescr lhs = rule.getLhs();
        assertThat(lhs).isNotNull();
        assertThat(lhs.getDescrs().size()).isEqualTo(1);

        PatternDescr pattern = (PatternDescr) lhs.getDescrs().get( 0 );
        assertThat(pattern.getObjectType()).isEqualTo("foo");
        assertThat(pattern.getIdentifier()).isEqualTo("$foo");

    }

    @Test
    public void testTypeDeclarationWithFields() throws Exception {
        final PackageDescr pkg = (PackageDescr) parseResource( "compilationUnit",
                                                               "declare_type_with_fields.drl" );
        assertThat(parser.hasErrors()).as(parser.getErrors().toString()).isFalse();

        List<TypeDeclarationDescr> td = pkg.getTypeDeclarations();
        assertThat(td.size()).isEqualTo(3);

        TypeDeclarationDescr d = td.get( 0 );
        assertThat(d.getTypeName()).isEqualTo("SomeFact");
        assertThat(d.getFields().size()).isEqualTo(2);
        assertThat(d.getFields().containsKey("name")).isTrue();
        assertThat(d.getFields().containsKey("age")).isTrue();

        TypeFieldDescr f = d.getFields().get( "name" );
        assertThat(f.getPattern().getObjectType()).isEqualTo("String");

        f = d.getFields().get( "age" );
        assertThat(f.getPattern().getObjectType()).isEqualTo("Integer");

        d = td.get( 1 );
        assertThat(d.getTypeName()).isEqualTo("AnotherFact");

        TypeDeclarationDescr type = td.get( 2 );
        assertThat(type.getTypeName()).isEqualTo("Person");

        assertThat(type.getAnnotation("role").getValue()).isEqualTo("fact");
        assertThat(type.getAnnotation("doc").getValue("descr")).isEqualTo("\"Models a person\"");
        assertThat(type.getAnnotation("doc").getValue("author")).isEqualTo("\"Bob\"");
        assertThat(type.getAnnotation("doc").getValue("date")).isEqualTo("Calendar.getInstance().getDate()");

        assertThat(type.getFields().size()).isEqualTo(2);
        TypeFieldDescr field = type.getFields().get( "name" );
        assertThat(field.getFieldName()).isEqualTo("name");
        assertThat(field.getPattern().getObjectType()).isEqualTo("String");
        assertThat(field.getInitExpr()).isEqualTo("\"John Doe\"");
        assertThat(field.getAnnotation("length").getValue("max")).isEqualTo("50");
        assertThat( field.getAnnotation( "key" ) ).isNotNull();

        field = type.getFields().get( "age" );
        assertThat(field.getFieldName()).isEqualTo("age");
        assertThat(field.getPattern().getObjectType()).isEqualTo("int");
        assertThat(field.getInitExpr()).isEqualTo("-1");
        assertThat(field.getAnnotation("ranged").getValue("min")).isEqualTo("0");
        assertThat(field.getAnnotation("ranged").getValue("max")).isEqualTo("150");
        assertThat(field.getAnnotation("ranged").getValue("unknown")).isEqualTo("-1");

    }

    @Test
    public void testRuleWithLHSNesting() throws Exception {
        final PackageDescr pkg = (PackageDescr) parseResource( "compilationUnit",
                                                               "Rule_with_nested_LHS.drl" );

        assertThat(parser.hasErrors()).as(parser.getErrors().toString()).isFalse();

        RuleDescr rule = pkg.getRules().get( 0 );
        assertThat(rule.getName()).isEqualTo("test");

        AndDescr lhs = rule.getLhs();
        assertThat(lhs).isNotNull();
        assertThat(lhs.getDescrs().size()).isEqualTo(2);

        PatternDescr a = (PatternDescr) lhs.getDescrs().get( 0 );
        assertThat(a.getObjectType()).isEqualTo("A");

        OrDescr or = (OrDescr) lhs.getDescrs().get( 1 );
        assertThat(or.getDescrs().size()).isEqualTo(3);

        AndDescr and1 = (AndDescr) or.getDescrs().get( 0 );
        assertThat(and1.getDescrs().size()).isEqualTo(2);
        PatternDescr b = (PatternDescr) and1.getDescrs().get( 0 );
        PatternDescr c = (PatternDescr) and1.getDescrs().get( 1 );
        assertThat(b.getObjectType()).isEqualTo("B");
        assertThat(c.getObjectType()).isEqualTo("C");

        AndDescr and2 = (AndDescr) or.getDescrs().get( 1 );
        assertThat(and2.getDescrs().size()).isEqualTo(2);
        PatternDescr d = (PatternDescr) and2.getDescrs().get( 0 );
        PatternDescr e = (PatternDescr) and2.getDescrs().get( 1 );
        assertThat(d.getObjectType()).isEqualTo("D");
        assertThat(e.getObjectType()).isEqualTo("E");

        AndDescr and3 = (AndDescr) or.getDescrs().get( 2 );
        assertThat(and3.getDescrs().size()).isEqualTo(2);
        PatternDescr f = (PatternDescr) and3.getDescrs().get( 0 );
        PatternDescr g = (PatternDescr) and3.getDescrs().get( 1 );
        assertThat(f.getObjectType()).isEqualTo("F");
        assertThat(g.getObjectType()).isEqualTo("G");
    }

    @Test
    public void testEntryPoint() throws Exception {
        final String text = "rule X when StockTick( symbol==\"ACME\") from entry-point StreamA then end";

        PackageDescr pkg = (PackageDescr) parse( "compilationUnit",
                                                     text );
        assertThat(parser.hasErrors()).as(parser.getErrors().toString()).isFalse();

        RuleDescr rule = pkg.getRules().get( 0 );
        PatternDescr pattern = (PatternDescr) rule.getLhs().getDescrs().get( 0 );

        assertThat(pattern.getDescrs().size()).isEqualTo(1);
        ExprConstraintDescr fcd = (ExprConstraintDescr) pattern.getDescrs().get( 0 );
        assertThat(fcd.getExpression()).isEqualTo("symbol==\"ACME\"");

        assertThat(pattern.getSource()).isNotNull();
        EntryPointDescr entry = (EntryPointDescr) pattern.getSource();
        assertThat(entry.getEntryId()).isEqualTo("StreamA");
    }

    @Test
    public void testEntryPoint2() throws Exception {
        final String text = "rule X when StockTick( symbol==\"ACME\") from entry-point \"StreamA\" then end";

        PackageDescr pkg = (PackageDescr) parse( "compilationUnit",
                                                     text );
        assertThat(parser.hasErrors()).as(parser.getErrors().toString()).isFalse();

        RuleDescr rule = pkg.getRules().get( 0 );
        PatternDescr pattern = (PatternDescr) rule.getLhs().getDescrs().get( 0 );

        assertThat(pattern.getDescrs().size()).isEqualTo(1);
        ExprConstraintDescr fcd = (ExprConstraintDescr) pattern.getDescrs().get( 0 );
        assertThat(fcd.getExpression()).isEqualTo("symbol==\"ACME\"");

        assertThat(pattern.getSource()).isNotNull();
        EntryPointDescr entry = (EntryPointDescr) pattern.getSource();
        assertThat(entry.getEntryId()).isEqualTo("StreamA");
    }

    @Test
    public void testSlidingWindow() throws Exception {
        final String text = "rule X when StockTick( symbol==\"ACME\") over window:length(10) then end";

        PackageDescr pkg = (PackageDescr) parse( "compilationUnit",
                                                 text );
        assertThat(parser.hasErrors()).as(parser.getErrors().toString()).isFalse();

        RuleDescr rule = pkg.getRules().get( 0 );
        PatternDescr pattern = (PatternDescr) rule.getLhs().getDescrs().get( 0 );

        assertThat(pattern.getDescrs().size()).isEqualTo(1);
        ExprConstraintDescr fcd = (ExprConstraintDescr) pattern.getDescrs().get( 0 );
        assertThat(fcd.getExpression()).isEqualTo("symbol==\"ACME\"");

        List<BehaviorDescr> behaviors = pattern.getBehaviors();
        assertThat(behaviors).isNotNull();
        assertThat(behaviors.size()).isEqualTo(1);
        BehaviorDescr descr = behaviors.get( 0 );
        assertThat(descr.getType()).isEqualTo("window");
        assertThat(descr.getSubType()).isEqualTo("length");
        assertThat(descr.getParameters().get(0)).isEqualTo("10");
    }

    @Test
    public void testRuleOldSyntax1() throws Exception {
        final String source = "rule \"Test\" when ( not $r :LiteralRestriction( operator == Operator.EQUAL ) ) then end";

        PackageDescr pkg = (PackageDescr) parse( "compilationUnit",
                                                 source );

        assertThat(parser.hasErrors()).as(parser.getErrors().toString()).isFalse();
        RuleDescr rule = pkg.getRules().get(0);

        assertThat(rule.getName()).isEqualTo("Test");
        assertThat(rule.getLhs().getDescrs().size()).isEqualTo(1);
        assertThat(((NotDescr) rule.getLhs().getDescrs().get(0)).getDescrs().size()).isEqualTo(1);
        NotDescr notDescr = (NotDescr) rule.getLhs().getDescrs().get( 0 );
        PatternDescr patternDescr = (PatternDescr) notDescr.getDescrs().get( 0 );
        assertThat(patternDescr.getIdentifier()).isEqualTo("$r");
        assertThat(patternDescr.getDescrs().size()).isEqualTo(1);
        ExprConstraintDescr fieldConstraintDescr = (ExprConstraintDescr) patternDescr.getDescrs().get( 0 );
        assertThat(fieldConstraintDescr.getExpression()).isEqualTo("operator == Operator.EQUAL");
    }

    @Test
    public void testRuleOldSyntax2() throws Exception {
        final String source = "rule \"Test\" when ( $r :LiteralRestriction( operator == Operator.EQUAL ) ) then end";

        PackageDescr pkg = (PackageDescr) parse( "compilationUnit",
                                                 source );

        assertThat(parser.hasErrors()).as(parser.getErrors().toString()).isFalse();
        RuleDescr rule = pkg.getRules().get(0);

        assertThat(rule.getName()).isEqualTo("Test");
        assertThat(rule.getLhs().getDescrs().size()).isEqualTo(1);
        PatternDescr patternDescr = (PatternDescr) rule.getLhs().getDescrs().get( 0 );
        assertThat(patternDescr.getIdentifier()).isEqualTo("$r");
        assertThat(patternDescr.getDescrs().size()).isEqualTo(1);
        ExprConstraintDescr fieldConstraintDescr = (ExprConstraintDescr) patternDescr.getDescrs().get( 0 );
        assertThat(fieldConstraintDescr.getExpression()).isEqualTo("operator == Operator.EQUAL");
    }

    @Test
    public void testTypeWithMetaData() throws Exception {

        PackageDescr pkg = (PackageDescr) parseResource( "compilationUnit",
                                                         "type_with_meta.drl" );

        assertThat(parser.hasErrors()).as(parser.getErrors().toString()).isFalse();

        final List<TypeDeclarationDescr> declarations = pkg.getTypeDeclarations();

        assertThat(declarations.size()).isEqualTo(3);
    }

    @Test
    public void testNullConstraints() throws Exception {
        final String text = "rule X when Person( name == null ) then end";
        PatternDescr pattern = (PatternDescr) ((RuleDescr) parse( "rule",
                                                                  text )).getLhs().getDescrs().get( 0 );

        assertThat(pattern.getDescrs().size()).isEqualTo(1);
        ExprConstraintDescr fcd = (ExprConstraintDescr) pattern.getDescrs().get( 0 );
        assertThat(fcd.getExpression()).isEqualTo("name == null");
        assertThat(fcd.getPosition()).isEqualTo(0);
        assertThat(fcd.getType()).isEqualTo(ExprConstraintDescr.Type.NAMED);
    }

    @Test
    public void testPositionalConstraintsOnly() throws Exception {
        final String text = "rule X when Person( \"Mark\", 42; ) then end";
        PatternDescr pattern = (PatternDescr) ((RuleDescr) parse( "rule",
                                                                  text )).getLhs().getDescrs().get( 0 );

        assertThat(pattern.getDescrs().size()).isEqualTo(2);
        ExprConstraintDescr fcd = (ExprConstraintDescr) pattern.getDescrs().get( 0 );
        assertThat(fcd.getExpression()).isEqualTo("\"Mark\"");
        assertThat(fcd.getPosition()).isEqualTo(0);
        assertThat(fcd.getType()).isEqualTo(ExprConstraintDescr.Type.POSITIONAL);
        fcd = (ExprConstraintDescr) pattern.getDescrs().get( 1 );
        assertThat(fcd.getExpression()).isEqualTo("42");
        assertThat(fcd.getPosition()).isEqualTo(1);
        assertThat(fcd.getType()).isEqualTo(ExprConstraintDescr.Type.POSITIONAL);
    }

    @Test
    public void testIsQuery() throws Exception {
        final String text = "rule X when ?person( \"Mark\", 42; ) then end";
        PatternDescr pattern = (PatternDescr) ((RuleDescr) parse( "rule",
                                                                  text )).getLhs().getDescrs().get( 0 );

        assertThat(pattern.isQuery()).isTrue();

        assertThat(pattern.getDescrs().size()).isEqualTo(2);
        ExprConstraintDescr fcd = (ExprConstraintDescr) pattern.getDescrs().get( 0 );
        assertThat(fcd.getExpression()).isEqualTo("\"Mark\"");
        assertThat(fcd.getPosition()).isEqualTo(0);
        assertThat(fcd.getType()).isEqualTo(ExprConstraintDescr.Type.POSITIONAL);
        fcd = (ExprConstraintDescr) pattern.getDescrs().get( 1 );
        assertThat(fcd.getExpression()).isEqualTo("42");
        assertThat(fcd.getPosition()).isEqualTo(1);
        assertThat(fcd.getType()).isEqualTo(ExprConstraintDescr.Type.POSITIONAL);
    }

    @Test
    public void testFromFollowedByQuery() throws Exception {
        // the 'from' expression requires a ";" to disambiguate the "?" 
        // prefix for queries from the ternary operator "? :"
        final String text = "rule X when Cheese() from $cheesery ?person( \"Mark\", 42; ) then end";
        RuleDescr rule = (RuleDescr) parse( "rule",
                                             text );
        assertThat(parser.hasErrors()).as(parser.getErrors().toString()).isFalse();

        PatternDescr pattern = (PatternDescr) rule.getLhs().getDescrs().get( 0 );
        assertThat(pattern.getObjectType()).isEqualTo("Cheese");
        assertThat(pattern.getSource().getText()).isEqualTo("from $cheesery");
        assertThat(pattern.isQuery()).isFalse();

        pattern = (PatternDescr) rule.getLhs().getDescrs().get( 1 );
        assertThat(pattern.getObjectType()).isEqualTo("person");
        assertThat(pattern.isQuery()).isTrue();

    }

    @Test
    public void testFromWithTernaryFollowedByQuery() throws Exception {
        // the 'from' expression requires a ";" to disambiguate the "?" 
        // prefix for queries from the ternary operator "? :"
        final String text = "rule X when Cheese() from (isFull ? $cheesery : $market) ?person( \"Mark\", 42; ) then end";
        RuleDescr rule = (RuleDescr) parse( "rule",
                                             text );
        assertThat(parser.hasErrors()).as(parser.getErrors().toString()).isFalse();

        PatternDescr pattern = (PatternDescr) rule.getLhs().getDescrs().get( 0 );
        assertThat(pattern.getObjectType()).isEqualTo("Cheese");
        assertThat(pattern.getSource().getText()).isEqualTo("from (isFull ? $cheesery : $market)");
        assertThat(pattern.isQuery()).isFalse();

        pattern = (PatternDescr) rule.getLhs().getDescrs().get( 1 );
        assertThat(pattern.getObjectType()).isEqualTo("person");
        assertThat(pattern.isQuery()).isTrue();

    }

    @Test
    public void testMultiValueAnnotationsBackwardCompatibility() throws Exception {
        // multiple values with no keys are parsed as a single value
        final String text = "rule X @ann1( val1, val2 ) @ann2( \"val1\", \"val2\" ) when then end";
        RuleDescr rule = (RuleDescr) parse( "rule",
                                             text );

        AnnotationDescr ann = rule.getAnnotation( "ann1" );
        assertThat(ann).isNotNull();
        assertThat(ann.getValue()).isEqualTo("val1, val2");

        ann = rule.getAnnotation( "ann2" );
        assertThat(ann).isNotNull();
        assertThat(ann.getValue()).isEqualTo("\"val1\", \"val2\"");
    }

    @Test
    public void testPositionalsAndNamedConstraints() throws Exception {
        final String text = "rule X when Person( \"Mark\", 42; location == \"atlanta\" ) then end";
        PatternDescr pattern = (PatternDescr) ((RuleDescr) parse( "rule",
                                                                  text )).getLhs().getDescrs().get( 0 );

        assertThat(pattern.getDescrs().size()).isEqualTo(3);
        ExprConstraintDescr fcd = (ExprConstraintDescr) pattern.getDescrs().get( 0 );
        assertThat(fcd.getExpression()).isEqualTo("\"Mark\"");
        assertThat(fcd.getPosition()).isEqualTo(0);
        assertThat(fcd.getType()).isEqualTo(ExprConstraintDescr.Type.POSITIONAL);
        fcd = (ExprConstraintDescr) pattern.getDescrs().get( 1 );
        assertThat(fcd.getExpression()).isEqualTo("42");
        assertThat(fcd.getPosition()).isEqualTo(1);
        assertThat(fcd.getType()).isEqualTo(ExprConstraintDescr.Type.POSITIONAL);

        fcd = (ExprConstraintDescr) pattern.getDescrs().get( 2 );
        assertThat(fcd.getExpression()).isEqualTo("location == \"atlanta\"");
        assertThat(fcd.getPosition()).isEqualTo(2);
        assertThat(fcd.getType()).isEqualTo(ExprConstraintDescr.Type.NAMED);

    }

    @Test
    public void testUnificationBinding() throws Exception {
        final String text = "rule X when $p := Person( $name := name, $loc : location ) then end";
        PatternDescr pattern = (PatternDescr) ((RuleDescr) parse( "rule",
                                                                  text )).getLhs().getDescrs().get( 0 );

        assertThat(pattern.getIdentifier()).isEqualTo("$p");
        assertThat(pattern.isUnification()).isTrue();

        assertThat(pattern.getDescrs().size()).isEqualTo(2);
        ExprConstraintDescr bindingDescr = (ExprConstraintDescr) pattern.getDescrs().get( 0 );
        assertThat(bindingDescr.getExpression()).isEqualTo("$name := name");

        bindingDescr = (ExprConstraintDescr) pattern.getDescrs().get( 1 );
        assertThat(bindingDescr.getExpression()).isEqualTo("$loc : location");

    }

    @Test
    public void testBigLiterals() throws Exception {
        final String text = "rule X when Primitives( bigInteger == (10I), " +
        		            "                        bigDecimal == (10B), " +
        		            "                        bigInteger < 50I, " +
        		            "                        bigDecimal < 50B ) then end";
        PatternDescr pattern = (PatternDescr) ((RuleDescr) parse( "rule",
                                                                  text )).getLhs().getDescrs().get( 0 );

        assertThat(pattern.getDescrs().size()).isEqualTo(4);
        ExprConstraintDescr ecd = (ExprConstraintDescr) pattern.getDescrs().get( 0 );
        assertThat(ecd.getExpression()).isEqualTo("bigInteger == (10I)");

        ecd = (ExprConstraintDescr) pattern.getDescrs().get( 1 );
        assertThat(ecd.getExpression()).isEqualTo("bigDecimal == (10B)");

        ecd = (ExprConstraintDescr) pattern.getDescrs().get( 2 );
        assertThat(ecd.getExpression()).isEqualTo("bigInteger < 50I");

        ecd = (ExprConstraintDescr) pattern.getDescrs().get( 3 );
        assertThat(ecd.getExpression()).isEqualTo("bigDecimal < 50B");
    }

    @Test
    public void testBindingComposite() throws Exception {
        final String text = "rule X when Person( $name : name == \"Bob\" || $loc : location == \"Montreal\" ) then end";
        PatternDescr pattern = (PatternDescr) ((RuleDescr) parse( "rule",
                                                                  text )).getLhs().getDescrs().get( 0 );

        assertThat(pattern.getObjectType()).isEqualTo("Person");
        assertThat(pattern.isUnification()).isFalse();

        //        assertEquals( 2,
        //                      pattern.getDescrs().size() );
        //        BindingDescr bindingDescr = pattern.getDescrs().get( 0 );
        //        assertEquals( "$name",
        //                      bindingDescr.getVariable() );
        //        assertEquals( "name",
        //                      bindingDescr.getExpression() );
        //        assertFalse( bindingDescr.isUnification() );
        //
        //        bindingDescr = pattern.getDescrs().get( 1 );
        //        assertEquals( "$loc",
        //                      bindingDescr.getVariable() );
        //        assertEquals( "location",
        //                      bindingDescr.getExpression() );
        //        assertFalse( bindingDescr.isUnification() );

        // embedded bindings are extracted at compile time
        List< ? > constraints = pattern.getDescrs();
        assertThat(constraints.size()).isEqualTo(1);
        assertThat(((ExprConstraintDescr) constraints.get(0)).getExpression()).isEqualTo("$name : name == \"Bob\" || $loc : location == \"Montreal\"");
    }

    @Test
    public void testBindingCompositeWithMethods() throws Exception {
        final String text = "rule X when Person( $name : name.toUpperCase() == \"Bob\" || $loc : location[0].city == \"Montreal\" ) then end";
        PatternDescr pattern = (PatternDescr) ((RuleDescr) parse( "rule",
                                                                  text )).getLhs().getDescrs().get( 0 );

        assertThat(pattern.getObjectType()).isEqualTo("Person");
        assertThat(pattern.isUnification()).isFalse();

        //        assertEquals( 2,
        //                      pattern.getDescrs().size() );
        //        BindingDescr bindingDescr = pattern.getDescrs().get( 0 );
        //        assertEquals( "$name",
        //                      bindingDescr.getVariable() );
        //        assertEquals( "name.toUpperCase()",
        //                      bindingDescr.getExpression() );
        //        assertFalse( bindingDescr.isUnification() );
        //
        //        bindingDescr = pattern.getDescrs().get( 1 );
        //        assertEquals( "$loc",
        //                      bindingDescr.getVariable() );
        //        assertEquals( "location[0].city",
        //                      bindingDescr.getExpression() );
        //        assertFalse( bindingDescr.isUnification() );

        // embedded bindings are extracted at compile time
        List< ? > constraints = pattern.getDescrs();
        assertThat(constraints.size()).isEqualTo(1);
        assertThat(((ExprConstraintDescr) constraints.get(0)).getExpression()).isEqualTo("$name : name.toUpperCase() == \"Bob\" || $loc : location[0].city == \"Montreal\"");
    }

    @Test
    public void testPluggableOperators2() throws Exception {
        final String text = "rule \"tt\"\n" +
                            "    dialect \"mvel\"\n" +
                            "when\n" +
                            "    exists (TelephoneCall( this finishes [1m] \"25-May-2011\" ))\n" +
                            "then\n" +
                            "end";
        PatternDescr pattern = (PatternDescr) ((ExistsDescr) ((RuleDescr) parse( "rule",
                                                                                 text )).getLhs().getDescrs().get( 0 )).getDescrs().get( 0 );

        assertThat(pattern.getObjectType()).isEqualTo("TelephoneCall");
        ExprConstraintDescr constr = (ExprConstraintDescr) pattern.getConstraint().getDescrs().get( 0 );
        assertThat(constr.getText()).isEqualTo("this finishes [1m] \"25-May-2011\"");

    }

    @Test
    public void testInlineEval() throws Exception {
        final String text = "rule \"inline eval\"\n" +
                            "when\n" +
                            "    Person( eval( name.startsWith(\"b\") && name.finishesWith(\"b\")) )\n" +
                            "then\n" +
                            "end";
        PatternDescr pattern = (PatternDescr) ((RuleDescr) parse( "rule",
                                                                  text )).getLhs().getDescrs().get( 0 );

        assertThat(pattern.getObjectType()).isEqualTo("Person");
        ExprConstraintDescr constr = (ExprConstraintDescr) pattern.getConstraint().getDescrs().get( 0 );
        assertThat(constr.getText()).isEqualTo("eval( name.startsWith(\"b\") && name.finishesWith(\"b\"))");

    }

    @Test
    public void testInfinityLiteral() throws Exception {
        final String text = "rule \"infinity\"\n" +
                            "when\n" +
                            "    StockTick( this after[-*,*] $another )\n" +
                            "then\n" +
                            "end";
        PatternDescr pattern = (PatternDescr) ((RuleDescr) parse( "rule",
                                                                  text )).getLhs().getDescrs().get( 0 );

        assertThat(pattern.getObjectType()).isEqualTo("StockTick");
        ExprConstraintDescr constr = (ExprConstraintDescr) pattern.getConstraint().getDescrs().get( 0 );
        assertThat(constr.getText()).isEqualTo("this after[-*,*] $another");

    }

    @Test
    public void testEntryPointDeclaration() throws Exception {
        final String text = "package org.drools\n" +
                            "declare entry-point eventStream\n" +
                            "    @source(\"jndi://queues/events\")\n" +
                            "    @foo( true )\n" +
                            "end";
        PackageDescr pkg = (PackageDescr) parse( "compilationUnit",
                                                 text );

        assertThat(pkg.getName()).isEqualTo("org.drools");
        assertThat(pkg.getEntryPointDeclarations().size()).isEqualTo(1);

        EntryPointDeclarationDescr epd = pkg.getEntryPointDeclarations().iterator().next();

        assertThat(epd.getEntryPointId()).isEqualTo("eventStream");
        assertThat(epd.getAnnotations().size()).isEqualTo(2);
        assertThat(epd.getAnnotation("source").getValue()).isEqualTo("\"jndi://queues/events\"");
        assertThat(epd.getAnnotation("foo").getValue()).isEqualTo("true");
    }

    @Test
    public void testWindowDeclaration() throws Exception {
        final String text = "package org.drools\n" +
                            "declare window Ticks\n" +
                            "    @doc(\"last 10 stock ticks\")\n" +
                            "    $s : StockTick( source == \"NYSE\" )\n" +
                            "        over window:length( 10, $s.symbol )\n" +
                            "        from entry-point stStream\n" +
                            "end";
        PackageDescr pkg = (PackageDescr) parse( "compilationUnit",
                                                 text );

        assertThat(pkg.getName()).isEqualTo("org.drools");
        assertThat(pkg.getWindowDeclarations().size()).isEqualTo(1);

        WindowDeclarationDescr wdd = pkg.getWindowDeclarations().iterator().next();

        assertThat(wdd.getName()).isEqualTo("Ticks");
        assertThat(wdd.getAnnotations().size()).isEqualTo(1);
        assertThat(wdd.getAnnotation("doc").getValue()).isEqualTo("\"last 10 stock ticks\"");

        PatternDescr pd = wdd.getPattern();
        assertThat(pd).isNotNull();
        assertThat(pd.getIdentifier()).isEqualTo("$s");
        assertThat(pd.getObjectType()).isEqualTo("StockTick");
        assertThat(pd.getSource().getText()).isEqualTo("stStream");

        assertThat(pd.getBehaviors().size()).isEqualTo(1);
        BehaviorDescr bd = pd.getBehaviors().get( 0 );
        assertThat(bd.getType()).isEqualTo("window");
        assertThat(bd.getSubType()).isEqualTo("length");
        assertThat(bd.getParameters().size()).isEqualTo(2);
        assertThat(bd.getParameters().get(0)).isEqualTo("10");
        assertThat(bd.getParameters().get(1)).isEqualTo("$s.symbol");
    }

    @Test
    public void testWindowUsage() throws Exception {
        final String text = "package org.drools\n" +
                            "rule X\n" +
                            "when\n" +
                            "    StockTick() from window Y\n" +
                            "then\n" +
                            "end\n";
        PackageDescr pkg = (PackageDescr) parse( "compilationUnit",
                                                 text );

        assertThat(pkg.getName()).isEqualTo("org.drools");
        assertThat(pkg.getRules().size()).isEqualTo(1);

        RuleDescr rd = pkg.getRules().get(0);

        assertThat(rd.getName()).isEqualTo("X");
        assertThat(rd.getLhs().getDescrs().size()).isEqualTo(1);

        PatternDescr pd = (PatternDescr) rd.getLhs().getDescrs().get(0);
        assertThat(pd).isNotNull();
        assertThat(pd.getObjectType()).isEqualTo("StockTick");
        assertThat(pd.getSource().getText()).isEqualTo("Y");
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
            Method ruleName = null;
            Object[] params = null;
            for ( Method method : DRL6Parser.class.getMethods() ) {
                if ( method.getName().equals( testRuleName ) ) {
                    ruleName = method;
                    Class< ? >[] parameterTypes = method.getParameterTypes();
                    params = new Object[parameterTypes.length];
                }
            }

            /** Invoke grammar rule, and get the return value */
            Object ruleReturn = ruleName.invoke( parser,
                                                 params );

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
        parser = buildParser(charStream, LanguageLevelOption.DRL6);
    }

    private void assertEqualsIgnoreWhitespace( final String expected,
                                               final String actual ) {
        assertThat(expected).isEqualToIgnoringWhitespace(actual);
    }

    private Reader getReader( final String name ) throws Exception {
        final InputStream in = getClass().getResourceAsStream( name );
        return new InputStreamReader( in );
    }

}
