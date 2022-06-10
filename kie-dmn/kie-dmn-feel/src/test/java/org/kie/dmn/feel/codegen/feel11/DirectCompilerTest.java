/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.dmn.feel.codegen.feel11;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collections;
import java.util.Map;

import com.github.javaparser.ast.expr.Expression;
import org.antlr.v4.runtime.tree.ParseTree;
import org.junit.Test;
import org.kie.dmn.feel.lang.EvaluationContext;
import org.kie.dmn.feel.lang.FEELProperty;
import org.kie.dmn.feel.lang.Type;
import org.kie.dmn.feel.lang.ast.BaseNode;
import org.kie.dmn.feel.lang.impl.JavaBackedType;
import org.kie.dmn.feel.lang.impl.MapBackedType;
import org.kie.dmn.feel.lang.types.BuiltInType;
import org.kie.dmn.feel.parser.feel11.ASTBuilderVisitor;
import org.kie.dmn.feel.parser.feel11.FEELParser;
import org.kie.dmn.feel.parser.feel11.FEELParserTest;
import org.kie.dmn.feel.parser.feel11.FEEL_1_1Parser;
import org.kie.dmn.feel.runtime.FEELConditionsAndLoopsTest;
import org.kie.dmn.feel.runtime.FEELTernaryLogicTest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.assertj.core.api.Assertions.assertThat;
import static org.kie.dmn.feel.util.DynamicTypeUtils.entry;
import static org.kie.dmn.feel.util.DynamicTypeUtils.mapOf;

public class DirectCompilerTest {

    public static final Logger LOG = LoggerFactory.getLogger(DirectCompilerTest.class);
    
    private Object parseCompileEvaluate(String feelLiteralExpression) {
        CompiledFEELExpression compiledExpression = parse( feelLiteralExpression );
        LOG.debug("{}", compiledExpression);
        
        EvaluationContext emptyContext = CodegenTestUtil.newEmptyEvaluationContext();
        Object result = compiledExpression.apply(emptyContext);
        LOG.debug("{}", result);
        return result;
    }

    @Test
    public void test_FEEL_number() {
        assertThat(parseCompileEvaluate("10")).isEqualTo(BigDecimal.valueOf(10));
    }
    
    @Test
    public void test_FEEL_negative_number() {
        assertThat(parseCompileEvaluate("-10")).isEqualTo(BigDecimal.valueOf(-10));
    }
    
    @Test
    public void test_FEEL_DROOLS_2143() {
        // DROOLS-2143: Allow ''--1' expression as per FEEL grammar rule 26 
        assertThat(parseCompileEvaluate("--10")).isEqualTo(BigDecimal.valueOf(10));
        assertThat(parseCompileEvaluate("---10")).isEqualTo(BigDecimal.valueOf(-10));
        assertThat(parseCompileEvaluate("+10")).isEqualTo(BigDecimal.valueOf(10));
    }

    @Test
    public void test_FEEL_boolean() {
        assertThat(parseCompileEvaluate("false")).isEqualTo(Boolean.FALSE);
        assertThat(parseCompileEvaluate("true")).isEqualTo(Boolean.TRUE);
        assertThat(parseCompileEvaluate("null")).isNull();
    }
    
    @Test
    public void test_FEEL_null() {
        assertThat(parseCompileEvaluate("null")).isNull();
    }
    
    @Test
    public void test_FEEL_string() {
        assertThat(parseCompileEvaluate("\"some string\"")).isEqualTo("some string" );
    }
    
    @Test
    public void test_primary_parens() {
        assertThat(parseCompileEvaluate("(\"some string\")")).isEqualTo("some string" );
        assertThat(parseCompileEvaluate("(123)")).isEqualTo(BigDecimal.valueOf(123));
        assertThat(parseCompileEvaluate("(-123)")).isEqualTo(BigDecimal.valueOf(-123));
        assertThat(parseCompileEvaluate("-(123)")).isEqualTo(BigDecimal.valueOf(-123));
        assertThat(parseCompileEvaluate("(false)")).isEqualTo(Boolean.FALSE);
        assertThat(parseCompileEvaluate("(true)")).isEqualTo(Boolean.TRUE);
    }
    
    /**
     * See {@link FEELTernaryLogicTest}
     */
    @Test
    public void test_ternary_logic() {
        assertThat(parseCompileEvaluate( "true and true")).isEqualTo(Boolean.TRUE);
        assertThat(parseCompileEvaluate( "true and false")).isEqualTo(Boolean.FALSE);
        assertThat(parseCompileEvaluate( "true and null")).isNull();
        assertThat(parseCompileEvaluate( "false and true")).isEqualTo(Boolean.FALSE);
        assertThat(parseCompileEvaluate( "false and false")).isEqualTo(Boolean.FALSE);
        assertThat(parseCompileEvaluate( "false and null")).isEqualTo(Boolean.FALSE);
        assertThat(parseCompileEvaluate( "null and true")).isNull();
        assertThat(parseCompileEvaluate( "null and false")).isEqualTo(Boolean.FALSE);
        assertThat(parseCompileEvaluate( "null and null")).isNull();
        assertThat(parseCompileEvaluate( "true or true")).isEqualTo(Boolean.TRUE);
        assertThat(parseCompileEvaluate( "true or false")).isEqualTo(Boolean.TRUE);
        assertThat(parseCompileEvaluate( "true or null")).isEqualTo(Boolean.TRUE);
        assertThat(parseCompileEvaluate( "false or true")).isEqualTo(Boolean.TRUE);
        assertThat(parseCompileEvaluate( "false or false")).isEqualTo(Boolean.FALSE);
        assertThat(parseCompileEvaluate( "false or null")).isNull();
        assertThat(parseCompileEvaluate( "null or true")).isEqualTo(Boolean.TRUE);
        assertThat(parseCompileEvaluate( "null or false")).isNull();
        assertThat(parseCompileEvaluate( "null or null")).isNull();
        // logical operator priority
        assertThat(parseCompileEvaluate( "false and false or true")).isEqualTo(Boolean.TRUE);
        assertThat(parseCompileEvaluate( "false and (false or true)")).isEqualTo(Boolean.FALSE);
        assertThat(parseCompileEvaluate( "true or false and false")).isEqualTo(Boolean.TRUE);
        assertThat(parseCompileEvaluate( "(true or false) and false")).isEqualTo(Boolean.FALSE);
    }
    
    /**
     * Partially from {@link FEELConditionsAndLoopsTest}
     */
    @Test
    public void test_if() {
        assertThat(parseCompileEvaluate( "if true then 15 else 5")).isEqualTo(BigDecimal.valueOf( 15 ));
        assertThat(parseCompileEvaluate( "if false then 15 else 5")).isEqualTo(BigDecimal.valueOf( 5 ));
        assertThat(parseCompileEvaluate("if null then 15 else 5")).isEqualTo(BigDecimal.valueOf(5));
        assertThat(parseCompileEvaluate("if \"hello\" then 15 else 5")).isEqualTo(BigDecimal.valueOf(5));
    }
    
    @Test
    public void test_additiveExpression() {
        assertThat(parseCompileEvaluate( "1 + 2")).isEqualTo(BigDecimal.valueOf( 3 ));
        assertThat(parseCompileEvaluate( "1 + null")).isNull();
        assertThat(parseCompileEvaluate( "1 - 2")).isEqualTo(BigDecimal.valueOf( -1 ));
        assertThat(parseCompileEvaluate( "1 - null")).isNull();
        assertThat(parseCompileEvaluate( "\"Hello, \" + \"World\"")).isEqualTo("Hello, World");
    }
    
    @Test
    public void test_multiplicativeExpression() {
        assertThat(parseCompileEvaluate("3 * 5")).isEqualTo(BigDecimal.valueOf(15));
        assertThat(parseCompileEvaluate("3 * null")).isNull();
        assertThat(parseCompileEvaluate("10 / 2")).isEqualTo(BigDecimal.valueOf(5));
        assertThat(parseCompileEvaluate("10 / null")).isNull();
    }

    @Test
    public void test_exponentiationExpression() {
        assertThat(parseCompileEvaluate("3 ** 3")).isEqualTo(BigDecimal.valueOf(27));
        assertThat(parseCompileEvaluate("3 ** null")).isNull();
    }

    @Test
    public void test_logicalNegationExpression() {
        // this is all invalid syntax
        assertThat(parseCompileEvaluate("not true")).isNull();
        assertThat(parseCompileEvaluate("not false")).isNull();
        assertThat(parseCompileEvaluate("not null")).isNull();
        assertThat(parseCompileEvaluate("not 3")).isNull();
    }

    @Test
    public void test_listExpression() {
        assertThat(parseCompileEvaluate("[]")).asList().isEmpty();
        assertThat(parseCompileEvaluate("[ ]")).asList().isEmpty();
        assertThat(parseCompileEvaluate("[1]")).asList().containsExactly(BigDecimal.valueOf(1));
        assertThat(parseCompileEvaluate("[1, 2,3]")).asList().containsExactly(BigDecimal.valueOf(1), BigDecimal.valueOf(2), BigDecimal.valueOf(3));
    }

    @Test
    public void test_instanceOfExpression() {
        assertThat(parseCompileEvaluate("123 instance of number")).isEqualTo(Boolean.TRUE);
        assertThat(parseCompileEvaluate("\"ciao\" instance of number")).isEqualTo(Boolean.FALSE);
        assertThat(parseCompileEvaluate("123 instance of string")).isEqualTo(Boolean.FALSE);
        assertThat(parseCompileEvaluate("\"ciao\" instance of string")).isEqualTo(Boolean.TRUE);
    }

    @Test
    public void test_between() {
        assertThat(parseCompileEvaluate("10 between 5 and 12")).isEqualTo(Boolean.TRUE);
        assertThat(parseCompileEvaluate("10 between 20 and 30")).isEqualTo(Boolean.FALSE);
        assertThat(parseCompileEvaluate("10 between 5 and \"foo\"")).isNull();
        assertThat(parseCompileEvaluate("\"foo\" between 5 and 12")).isNull();
        assertThat(parseCompileEvaluate("\"foo\" between \"bar\" and \"zap\"")).isEqualTo(Boolean.TRUE);
        assertThat(parseCompileEvaluate("\"foo\" between null and \"zap\"")).isNull();
    }

    @Test
    public void test_filterPath() {
        // Filtering by index
        assertThat(parseCompileEvaluate("[\"a\", \"b\", \"c\"][1]")).isEqualTo("a");
        assertThat(parseCompileEvaluate("[\"a\", \"b\", \"c\"][2]")).isEqualTo("b");
        assertThat(parseCompileEvaluate("[\"a\", \"b\", \"c\"][3]")).isEqualTo("c");
        assertThat(parseCompileEvaluate("[\"a\", \"b\", \"c\"][-1]")).isEqualTo("c");
        assertThat(parseCompileEvaluate("[\"a\", \"b\", \"c\"][-2]")).isEqualTo("b");
        assertThat(parseCompileEvaluate("[\"a\", \"b\", \"c\"][-3]")).isEqualTo("a");
        assertThat(parseCompileEvaluate("[\"a\", \"b\", \"c\"][4]")).isNull();
        assertThat(parseCompileEvaluate("[\"a\", \"b\", \"c\"][984]")).isNull();
        assertThat(parseCompileEvaluate("[\"a\", \"b\", \"c\"][-4]")).isNull();
        assertThat(parseCompileEvaluate("[\"a\", \"b\", \"c\"][-984]")).isNull();
        assertThat(parseCompileEvaluate("\"a\"[1]")).isEqualTo("a");
        assertThat(parseCompileEvaluate("\"a\"[2]")).isNull();
        assertThat(parseCompileEvaluate("\"a\"[-1]")).isEqualTo("a");
        assertThat(parseCompileEvaluate("\"a\"[-2]")).isNull();

        // Filtering by boolean expression
        assertThat(parseCompileEvaluate("[1, 2, 3, 4][item = 4]")).asList().containsExactly(BigDecimal.valueOf(4));
        assertThat(parseCompileEvaluate("[1, 2, 3, 4][item > 2]")).asList().containsExactly(BigDecimal.valueOf(3), BigDecimal.valueOf(4));
        assertThat(parseCompileEvaluate("[1, 2, 3, 4][item > 5]")).asList().isEmpty();
        assertThat(parseCompileEvaluate("[ {x:1, y:2}, {x:2, y:3} ][x = 1]")).asList().containsExactly(mapOf(entry("x", new BigDecimal(1)), entry("y", new BigDecimal(2))));
        assertThat(parseCompileEvaluate("[ {x:1, y:2}, {x:2, y:3} ][x > 1]")).asList().containsExactly(mapOf(entry("x", new BigDecimal(2)), entry("y", new BigDecimal(3))));
        assertThat(parseCompileEvaluate("[ {x:1, y:2}, {x:2, y:3} ][x = 0]")).asList().isEmpty();
    }

    @Test
    public void test_filterPath_tricky1() {
        CompiledFEELExpression nameRef = parse( "[ {x:1, y:2}, {x:2, y:3} ][x]");
        LOG.debug("{}", nameRef);
        
        EvaluationContext context = CodegenTestUtil.newEmptyEvaluationContext();
        context.setValue("x", 2);
        Object result = nameRef.apply(context);
        LOG.debug("{}", result);
        
        assertThat(result).isEqualTo(mapOf(entry("x", new BigDecimal(2)), entry("y", new BigDecimal(3))));
    }

    @Test
    public void test_filterPath_tricky2() {
        CompiledFEELExpression nameRef = parse("[ {x:1, y:2}, {x:2, y:3} ][x]");
        LOG.debug("{}", nameRef);

        EvaluationContext context = CodegenTestUtil.newEmptyEvaluationContext();
        context.setValue("x", false);
        Object result = nameRef.apply(context);
        LOG.debug("{}", result);

        assertThat(result).asList().isEmpty();
    }

    @Test
    public void test_filterPathSelection() {
        // Selection
        assertThat(parseCompileEvaluate("[ {x:1, y:2}, {x:2, y:3} ].y")).asList().containsExactly(BigDecimal.valueOf(2), BigDecimal.valueOf(3));
        assertThat(parseCompileEvaluate("[ {x:1, y:2}, {x:2} ].y")).asList().containsExactly(BigDecimal.valueOf(2));
        assertThat(parseCompileEvaluate("[ {x:1, y:2}, {x:2, y:3} ].z")).asList().isEmpty();
    }

    @Test
    public void test_for() {
        // for
        Object parseCompileEvaluate = parseCompileEvaluate("for x in [ 10, 20, 30 ], y in [ 1, 2, 3 ] return x * y");
		assertThat(parseCompileEvaluate).asList().
		containsExactly(BigDecimal.valueOf(10), BigDecimal.valueOf(20), BigDecimal.valueOf(30), BigDecimal.valueOf(20), BigDecimal.valueOf(40), BigDecimal.valueOf(60), BigDecimal.valueOf(30), BigDecimal.valueOf(60), BigDecimal.valueOf(90));

        // normal:
        assertThat(parseCompileEvaluate("for x in [1, 2, 3] return x+1")).asList().
            	containsExactly(BigDecimal.valueOf(2), BigDecimal.valueOf(3), BigDecimal.valueOf(4));
        
        // TODO in order to parse correctly the enhanced for loop it is required to configure the FEEL Profiles
    }

    @Test
    public void test_quantifiedExpressions() {
        // quantified expressions
        assertThat(parseCompileEvaluate("some price in [ 80, 11, 110 ] satisfies price > 100")).isEqualTo(Boolean.TRUE);
        assertThat(parseCompileEvaluate("some price in [ 80, 11, 90 ] satisfies price > 100")).isEqualTo(Boolean.FALSE);
        assertThat(parseCompileEvaluate("some x in [ 5, 6, 7 ], y in [ 10, 11, 6 ] satisfies x > y")).isEqualTo(Boolean.TRUE);
        assertThat(parseCompileEvaluate("every price in [ 80, 11, 90 ] satisfies price > 10")).isEqualTo(Boolean.TRUE);
        assertThat(parseCompileEvaluate("every price in [ 80, 11, 90 ] satisfies price > 70")).isEqualTo(Boolean.FALSE);
        assertThat(parseCompileEvaluate("some x in [ 5, 6, 7 ], y in [ 10, 11, 12 ] satisfies x < y")).isEqualTo(Boolean.TRUE);
        assertThat(parseCompileEvaluate("some price in [ 80, 11, 110 ] satisfies price > max(100, 50, 10)")).isEqualTo(Boolean.TRUE);
    }

    @Test
    public void test_basicFunctionInvocation() {
        assertThat(parseCompileEvaluate("max(1, 2, 3)")).isEqualTo(new BigDecimal(3));
    }

    @Test
    public void test_basicFunctionDefinition() {
        assertThat(parseCompileEvaluate("function (a, b) a + b")).isInstanceOf(CompiledCustomFEELFunction.class);
        assertThat(parseCompileEvaluate("{ s : function (a, b) a + b, x : 1, y : 2, r : s(x,y) }.r")).isEqualTo(new BigDecimal(3));
    }

    @Test
    public void test_namedFunctionInvocation() {
        assertThat(parseCompileEvaluate("substring(start position: 2, string: \"FOOBAR\")")).isEqualTo("OOBAR");
        assertThat(parseCompileEvaluate("ceiling( n : 1.5 )")).isEqualTo(new BigDecimal("2"));
    }

    @Test
    public void test_Misc_fromOriginalFEELInterpretedTestSuite() {
        assertThat(parseCompileEvaluate("if null then \"foo\" else \"bar\"")).isEqualTo("bar");
        assertThat(parseCompileEvaluate("{ hello world : function() \"Hello World!\", message : hello world() }.message")).isEqualTo("Hello World!");
        assertThat(parseCompileEvaluate("1 + if true then 1 else 2")).isEqualTo(new BigDecimal("2"));
        assertThat(parseCompileEvaluate("\"string with \\\"quotes\\\"\"")).isEqualTo("string with \"quotes\"");
        assertThat(parseCompileEvaluate("date( -0105, 8, 2 )")).isEqualTo(LocalDate.of(-105, 8, 2));
        assertThat(parseCompileEvaluate("string(null)")).isNull();
        assertThat(parseCompileEvaluate("[ null ]")).asList().containsExactly(new Object[]{null});
        assertThat(parseCompileEvaluate("[ null, null ]")).asList().containsExactly(new Object[]{null, null});
        assertThat(parseCompileEvaluate("[ null, 47, null ]")).asList().containsExactly(new Object[]{null, BigDecimal.valueOf(47), null});
    }

    @Test
    public void test_Benchmark_feelExpressions() {
        assertThat(parseCompileEvaluate("{ full name: { first name: \"John\", last name: \"Doe\" } }.full name.last name")).isEqualTo("Doe");
        assertThat(parseCompileEvaluate("some price in [ 80, 11, 110 ] satisfies price > 100")).isEqualTo(Boolean.TRUE);
        assertThat(parseCompileEvaluate("every price in [ 80, 11, 90 ] satisfies price > 10")).isEqualTo(Boolean.TRUE);
    }

    @Test
    public void test_contextExpression() {
        assertThat(parseCompileEvaluate("{}")).isEqualTo(Collections.emptyMap());
        assertThat(parseCompileEvaluate("{ }")).isEqualTo(Collections.emptyMap());
        assertThat(parseCompileEvaluate("{ a : 1 }")).isEqualTo(mapOf(entry("a", new BigDecimal(1))));
        assertThat(parseCompileEvaluate("{ \"a\" : 1 }")).isEqualTo(mapOf(entry("a", new BigDecimal(1))));
        assertThat(parseCompileEvaluate("{ \" a\" : 1 }")).isEqualTo(mapOf(entry(" a", new BigDecimal(1)))); // Demonstrating a bad practice.
        assertThat(parseCompileEvaluate("{ a : 1, b : 2, c : 3 }")).isEqualTo(mapOf(entry("a", new BigDecimal(1)), entry("b", new BigDecimal(2)), entry("c", new BigDecimal(3))));
        assertThat(parseCompileEvaluate("{ a : 1, a name : \"John Doe\" }")).isEqualTo(mapOf(entry("a", new BigDecimal(1)), entry("a name", "John Doe")));

        assertThat(parseCompileEvaluate("{ a : 1, b : a }")).isEqualTo(mapOf(entry("a", new BigDecimal(1)), entry("b", new BigDecimal(1))));
    }
    
    /**
     * See {@link FEELParserTest}
     */
    @Test
    public void testContextWithMultipleEntries() {
        String inputExpression = "{ \"a string key\" : 10," + "\n"
                               + " a non-string key : 11," + "\n"
                               + " a key.with + /' odd chars : 12 }";
        assertThat(parseCompileEvaluate(inputExpression)).isEqualTo(mapOf(entry("a string key", new BigDecimal(10)), entry("a non-string key", new BigDecimal(11)), entry("a key.with + /' odd chars", new BigDecimal(12))));
    }
    
    /**
     * See {@link FEELParserTest}
     */
    @Test
    public void testNestedContexts() {
        String inputExpression = "{ a value : 10," + "\n"
                       + " an applicant : { " + "\n"
                       + "    first name : \"Edson\", " + "\n"
                       + "    last + name : \"Tirelli\", " + "\n"
                       + "    full name : first name + last + name, " + "\n"
                       + "    address : {" + "\n"
                       + "        street : \"55 broadway st\"," + "\n"
                       + "        city : \"New York\" " + "\n"
                       + "    }, " + "\n"
                       + "    xxx: last + name" + "\n"
                       + " } " + "\n"
                       + "}";
        assertThat(parseCompileEvaluate(inputExpression)).isEqualTo(mapOf(entry("a value", new BigDecimal(10)), 
                                                                   entry("an applicant", mapOf(entry("first name", "Edson"),
                                                                                               entry("last + name", "Tirelli"),
                                                                                               entry("full name", "EdsonTirelli"),
                                                                                               entry("address", mapOf(entry("street", "55 broadway st"),
                                                                                                                      entry("city", "New York"))),
                                                                                               entry("xxx", "Tirelli")))));
    }

    /**
     * See {@link FEELParserTest}
     */
    @Test
    public void testNestedContexts2() {
        String complexContext = "{ an applicant : {                                \n" +
                                "    home address : {                              \n" +
                                "        street name: \"broadway st\",             \n" +
                                "        city : \"New York\"                       \n" +
                                "    }                                             \n" +
                                "   },                                             \n" +
                                "   street : an applicant.home address.street name \n" +
                                "}                                                 ";
        assertThat(parseCompileEvaluate(complexContext)).isEqualTo(mapOf(entry("an applicant", mapOf(entry("home address", mapOf(entry("street name", "broadway st"),
                                                                                                                          entry("city", "New York"))))),
                                                                  entry("street", "broadway st")));
    }

    @Test
    public void testNameReference() {
        String inputExpression = "someSimpleName";
        CompiledFEELExpression nameRef = parse( inputExpression, mapOf( entry("someSimpleName", BuiltInType.STRING) ) );
        LOG.debug("{}", nameRef);
        
        EvaluationContext context = CodegenTestUtil.newEmptyEvaluationContext();
        context.setValue("someSimpleName", 123L);
        Object result = nameRef.apply(context);
        LOG.debug("{}", result);
        
        assertThat(result).isEqualTo(BigDecimal.valueOf(123));
    }
    
    @Test
    public void testQualifiedName() {
        String inputExpression = "My Person.Full Name";
        Type personType = new MapBackedType("Person", mapOf( entry("Full Name", BuiltInType.STRING), entry("Age", BuiltInType.NUMBER) ) );
        CompiledFEELExpression qualRef = parse( inputExpression, mapOf( entry("My Person", personType) ) );
        LOG.debug("{}", qualRef);
        
        EvaluationContext context = CodegenTestUtil.newEmptyEvaluationContext();
        context.setValue("My Person", mapOf( entry("Full Name", "John Doe"), entry("Age", 47) ));
        Object result = qualRef.apply(context);
        LOG.debug("{}", result);
        
        assertThat(result).isEqualTo("John Doe" );

        // check number coercion for qualified name
        CompiledFEELExpression personAgeExpression = parse("My Person.Age", mapOf(entry("My Person", personType)));
        LOG.debug("{}", personAgeExpression);

        Object resultPersonAge = personAgeExpression.apply(context); // Please notice input variable in context is a Map containing and entry value for int 47.
        LOG.debug("{}", resultPersonAge);

        assertThat(resultPersonAge).isEqualTo(BigDecimal.valueOf(47));
    }
    
    public static class MyPerson {
        @FEELProperty("Full Name")
        public String getFullName() {
            return "John Doe";
        }
    }
    @Test
    public void testQualifiedName2() {
        String inputExpression = "My Person.Full Name";
        Type personType = JavaBackedType.of(MyPerson.class);
        CompiledFEELExpression qualRef = parse( inputExpression, mapOf( entry("My Person", personType) ) );
        LOG.debug("{}", qualRef);
        
        EvaluationContext context = CodegenTestUtil.newEmptyEvaluationContext();
        context.setValue("My Person", new MyPerson());
        Object result = qualRef.apply(context);
        LOG.debug("{}", result);
        
        assertThat(result).isEqualTo("John Doe" );
    }

    @Test
    public void testQualifiedName3() {
        String inputExpression = "a date.year";
        Type dateType = BuiltInType.DATE;
        CompiledFEELExpression qualRef = parse(inputExpression, mapOf(entry("a date", dateType)));
        LOG.debug("{}", qualRef);
        
        EvaluationContext context = CodegenTestUtil.newEmptyEvaluationContext();
        context.setValue("a date", LocalDate.of(2016, 8, 2));
        Object result = qualRef.apply(context);
        LOG.debug("{}", result);
        
        assertThat(result).isEqualTo(BigDecimal.valueOf(2016));
    }

    private CompiledFEELExpression parse(String input) {
        return parse( input, Collections.emptyMap() );
    }

    private CompiledFEELExpression parse(String input, Map<String, Type> inputTypes) {
        FEEL_1_1Parser parser = FEELParser.parse(null, input, inputTypes, Collections.emptyMap(), Collections.emptyList(), Collections.emptyList(), null);

        ParseTree tree = parser.compilation_unit();

        ASTBuilderVisitor v = new ASTBuilderVisitor(inputTypes, null);
        BaseNode node = v.visit(tree);
        DirectCompilerResult directResult = node.accept(new ASTCompilerVisitor());
        
        Expression expr = directResult.getExpression();
        CompiledFEELExpression cu = new CompilerBytecodeLoader().makeFromJPExpression(input, expr, directResult.getFieldDeclarations());

        return cu;
    }
    

}
