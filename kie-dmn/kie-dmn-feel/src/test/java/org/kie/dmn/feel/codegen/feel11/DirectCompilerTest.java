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
package org.kie.dmn.feel.codegen.feel11;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.kie.dmn.feel.lang.EvaluationContext;
import org.kie.dmn.feel.lang.FEELProperty;
import org.kie.dmn.feel.lang.Type;
import org.kie.dmn.feel.lang.impl.JavaBackedType;
import org.kie.dmn.feel.lang.impl.MapBackedType;
import org.kie.dmn.feel.lang.types.BuiltInType;
import org.kie.dmn.feel.parser.feel11.FEELParserTest;
import org.kie.dmn.feel.runtime.FEELConditionsAndLoopsTest;
import org.kie.dmn.feel.runtime.FEELTernaryLogicTest;
import org.kie.dmn.feel.runtime.functions.CustomFEELFunction;
import org.kie.dmn.feel.util.CompilerUtils;
import org.kie.dmn.feel.util.EvaluationContextTestUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.assertj.core.api.Assertions.assertThat;
import static org.kie.dmn.feel.util.CompilerUtils.parseCodegen;
import static org.kie.dmn.feel.util.CompilerUtils.parseCodegenCompileEvaluate;
import static org.kie.dmn.feel.util.DynamicTypeUtils.entry;
import static org.kie.dmn.feel.util.DynamicTypeUtils.mapOf;

public class DirectCompilerTest {

    public static final Logger LOG = LoggerFactory.getLogger(DirectCompilerTest.class);


    @Test
    void feel_number() {
        assertThat(parseCodegenCompileEvaluate("10")).isEqualTo(BigDecimal.valueOf(10));
    }

    @Test
    void feel_negative_number() {
        assertThat(parseCodegenCompileEvaluate("-10")).isEqualTo(BigDecimal.valueOf(-10));
    }

    @Test
    void feel_drools_2143() {
        // DROOLS-2143: Allow ''--1' expression as per FEEL grammar rule 26 
        assertThat(parseCodegenCompileEvaluate("--10")).isEqualTo(BigDecimal.valueOf(10));
        assertThat(parseCodegenCompileEvaluate("---10")).isEqualTo(BigDecimal.valueOf(-10));
        assertThat(parseCodegenCompileEvaluate("+10")).isEqualTo(BigDecimal.valueOf(10));
    }

    @Test
    void feel_boolean() {
        assertThat(parseCodegenCompileEvaluate("false")).isEqualTo(Boolean.FALSE);
        assertThat(parseCodegenCompileEvaluate("true")).isEqualTo(Boolean.TRUE);
        assertThat(parseCodegenCompileEvaluate("null")).isNull();
    }

    @Test
    void feel_null() {
        assertThat(parseCodegenCompileEvaluate("null")).isNull();
    }

    @Test
    void feel_string() {
        assertThat(parseCodegenCompileEvaluate("\"some string\"")).isEqualTo("some string" );
    }

    @Test
    void primary_parens() {
        assertThat(parseCodegenCompileEvaluate("(\"some string\")")).isEqualTo("some string" );
        assertThat(parseCodegenCompileEvaluate("(123)")).isEqualTo(BigDecimal.valueOf(123));
        assertThat(parseCodegenCompileEvaluate("(-123)")).isEqualTo(BigDecimal.valueOf(-123));
        assertThat(parseCodegenCompileEvaluate("-(123)")).isEqualTo(BigDecimal.valueOf(-123));
        assertThat(parseCodegenCompileEvaluate("(false)")).isEqualTo(Boolean.FALSE);
        assertThat(parseCodegenCompileEvaluate("(true)")).isEqualTo(Boolean.TRUE);
    }

    /**
     * See {@link FEELTernaryLogicTest}
     */
    @Test
    void ternary_logic() {
        assertThat(parseCodegenCompileEvaluate("true and true")).isEqualTo(Boolean.TRUE);
        assertThat(parseCodegenCompileEvaluate("true and false")).isEqualTo(Boolean.FALSE);
        assertThat(parseCodegenCompileEvaluate("true and null")).isNull();
        assertThat(parseCodegenCompileEvaluate("false and true")).isEqualTo(Boolean.FALSE);
        assertThat(parseCodegenCompileEvaluate("false and false")).isEqualTo(Boolean.FALSE);
        assertThat(parseCodegenCompileEvaluate("false and null")).isEqualTo(Boolean.FALSE);
        assertThat(parseCodegenCompileEvaluate("null and true")).isNull();
        assertThat(parseCodegenCompileEvaluate("null and false")).isEqualTo(Boolean.FALSE);
        assertThat(parseCodegenCompileEvaluate("null and null")).isNull();
        assertThat(parseCodegenCompileEvaluate("true or true")).isEqualTo(Boolean.TRUE);
        assertThat(parseCodegenCompileEvaluate("true or false")).isEqualTo(Boolean.TRUE);
        assertThat(parseCodegenCompileEvaluate("true or null")).isEqualTo(Boolean.TRUE);
        assertThat(parseCodegenCompileEvaluate("false or true")).isEqualTo(Boolean.TRUE);
        assertThat(parseCodegenCompileEvaluate("false or false")).isEqualTo(Boolean.FALSE);
        assertThat(parseCodegenCompileEvaluate("false or null")).isNull();
        assertThat(parseCodegenCompileEvaluate("null or true")).isEqualTo(Boolean.TRUE);
        assertThat(parseCodegenCompileEvaluate("null or false")).isNull();
        assertThat(parseCodegenCompileEvaluate("null or null")).isNull();
        // logical operator priority
        assertThat(parseCodegenCompileEvaluate("false and false or true")).isEqualTo(Boolean.TRUE);
        assertThat(parseCodegenCompileEvaluate("false and (false or true)")).isEqualTo(Boolean.FALSE);
        assertThat(parseCodegenCompileEvaluate("true or false and false")).isEqualTo(Boolean.TRUE);
        assertThat(parseCodegenCompileEvaluate("(true or false) and false")).isEqualTo(Boolean.FALSE);
    }

    /**
     * Partially from {@link FEELConditionsAndLoopsTest}
     */
    @Test
    void test_if() {
        assertThat(parseCodegenCompileEvaluate("if true then 15 else 5")).isEqualTo(BigDecimal.valueOf(15 ));
        assertThat(parseCodegenCompileEvaluate("if false then 15 else 5")).isEqualTo(BigDecimal.valueOf(5 ));
        assertThat(parseCodegenCompileEvaluate("if null then 15 else 5")).isEqualTo(BigDecimal.valueOf(5));
        assertThat(parseCodegenCompileEvaluate("if \"hello\" then 15 else 5")).isEqualTo(BigDecimal.valueOf(5));
    }

    @Test
    void additive_expression() {
        assertThat(parseCodegenCompileEvaluate("1 + 2")).isEqualTo(BigDecimal.valueOf(3 ));
        assertThat(parseCodegenCompileEvaluate("1 + null")).isNull();
        assertThat(parseCodegenCompileEvaluate("1 - 2")).isEqualTo(BigDecimal.valueOf(-1 ));
        assertThat(parseCodegenCompileEvaluate("1 - null")).isNull();
        assertThat(parseCodegenCompileEvaluate("\"Hello, \" + \"World\"")).isEqualTo("Hello, World");
    }

    @Test
    void multiplicative_expression() {
        assertThat(parseCodegenCompileEvaluate("3 * 5")).isEqualTo(BigDecimal.valueOf(15));
        assertThat(parseCodegenCompileEvaluate("3 * null")).isNull();
        assertThat(parseCodegenCompileEvaluate("10 / 2")).isEqualTo(BigDecimal.valueOf(5));
        assertThat(parseCodegenCompileEvaluate("10 / null")).isNull();
    }

    @Test
    void exponentiation_expression() {
        assertThat(parseCodegenCompileEvaluate("3 ** 3")).isEqualTo(BigDecimal.valueOf(27));
        assertThat(parseCodegenCompileEvaluate("3 ** null")).isNull();
    }

    @Test
    void logical_negation_expression() {
        // this is all invalid syntax
        assertThat(parseCodegenCompileEvaluate("not true")).isNull();
        assertThat(parseCodegenCompileEvaluate("not false")).isNull();
        assertThat(parseCodegenCompileEvaluate("not null")).isNull();
        assertThat(parseCodegenCompileEvaluate("not 3")).isNull();
    }

    @Test
    void list_expression() {
        assertThat(parseCodegenCompileEvaluate("[]")).asList().isEmpty();
        assertThat(parseCodegenCompileEvaluate("[ ]")).asList().isEmpty();
        assertThat(parseCodegenCompileEvaluate("[1]")).asList().containsExactly(BigDecimal.valueOf(1));
        assertThat(parseCodegenCompileEvaluate("[1, 2,3]")).asList().containsExactly(BigDecimal.valueOf(1), BigDecimal.valueOf(2), BigDecimal.valueOf(3));
    }

    @Test
    void instance_of_expression() {
        assertThat(parseCodegenCompileEvaluate("123 instance of number")).isEqualTo(Boolean.TRUE);
        assertThat(parseCodegenCompileEvaluate("\"ciao\" instance of number")).isEqualTo(Boolean.FALSE);
        assertThat(parseCodegenCompileEvaluate("123 instance of string")).isEqualTo(Boolean.FALSE);
        assertThat(parseCodegenCompileEvaluate("\"ciao\" instance of string")).isEqualTo(Boolean.TRUE);
    }

    @Test
    void between() {
        assertThat(parseCodegenCompileEvaluate("10 between 5 and 12")).isEqualTo(Boolean.TRUE);
        assertThat(parseCodegenCompileEvaluate("10 between 20 and 30")).isEqualTo(Boolean.FALSE);
        assertThat(parseCodegenCompileEvaluate("10 between 5 and \"foo\"")).isNull();
        assertThat(parseCodegenCompileEvaluate("\"foo\" between 5 and 12")).isNull();
        assertThat(parseCodegenCompileEvaluate("\"foo\" between \"bar\" and \"zap\"")).isEqualTo(Boolean.TRUE);
        assertThat(parseCodegenCompileEvaluate("\"foo\" between null and \"zap\"")).isNull();
    }

    @Test
    void filter_path() {
        // Filtering by index
        assertThat(parseCodegenCompileEvaluate("[\"a\", \"b\", \"c\"][1]")).isEqualTo("a");
        assertThat(parseCodegenCompileEvaluate("[\"a\", \"b\", \"c\"][2]")).isEqualTo("b");
        assertThat(parseCodegenCompileEvaluate("[\"a\", \"b\", \"c\"][3]")).isEqualTo("c");
        assertThat(parseCodegenCompileEvaluate("[\"a\", \"b\", \"c\"][-1]")).isEqualTo("c");
        assertThat(parseCodegenCompileEvaluate("[\"a\", \"b\", \"c\"][-2]")).isEqualTo("b");
        assertThat(parseCodegenCompileEvaluate("[\"a\", \"b\", \"c\"][-3]")).isEqualTo("a");
        assertThat(parseCodegenCompileEvaluate("[\"a\", \"b\", \"c\"][4]")).isNull();
        assertThat(parseCodegenCompileEvaluate("[\"a\", \"b\", \"c\"][984]")).isNull();
        assertThat(parseCodegenCompileEvaluate("[\"a\", \"b\", \"c\"][-4]")).isNull();
        assertThat(parseCodegenCompileEvaluate("[\"a\", \"b\", \"c\"][-984]")).isNull();
        assertThat(parseCodegenCompileEvaluate("\"a\"[1]")).isEqualTo("a");
        assertThat(parseCodegenCompileEvaluate("\"a\"[2]")).isNull();
        assertThat(parseCodegenCompileEvaluate("\"a\"[-1]")).isEqualTo("a");
        assertThat(parseCodegenCompileEvaluate("\"a\"[-2]")).isNull();

        // Filtering by boolean expression
        assertThat(parseCodegenCompileEvaluate("[1, 2, 3, 4][item = 4]")).asList().containsExactly(BigDecimal.valueOf(4));
        assertThat(parseCodegenCompileEvaluate("[1, 2, 3, 4][item > 2]")).asList().containsExactly(BigDecimal.valueOf(3), BigDecimal.valueOf(4));
        assertThat(parseCodegenCompileEvaluate("[1, 2, 3, 4][item > 5]")).asList().isEmpty();
        assertThat(parseCodegenCompileEvaluate("[ {x:1, y:2}, {x:2, y:3} ][x = 1]")).asList().containsExactly(mapOf(entry("x", new BigDecimal(1)), entry("y", new BigDecimal(2))));
        assertThat(parseCodegenCompileEvaluate("[ {x:1, y:2}, {x:2, y:3} ][x > 1]")).asList().containsExactly(mapOf(entry("x", new BigDecimal(2)), entry("y", new BigDecimal(3))));
        assertThat(parseCodegenCompileEvaluate("[ {x:1, y:2}, {x:2, y:3} ][x = 0]")).asList().isEmpty();
    }

    @Test
    void filter_path_tricky1() {
        CompiledFEELExpression nameRef = CompilerUtils.parseCodegen("[ {x:1, y:2}, {x:2, y:3} ][x]");
        LOG.debug("{}", nameRef);
        
        EvaluationContext context = EvaluationContextTestUtil.newEmptyEvaluationContext();
        context.setValue("x", 2);
        Object result = nameRef.apply(context);
        LOG.debug("{}", result);
        
        assertThat(result).isEqualTo(mapOf(entry("x", new BigDecimal(2)), entry("y", new BigDecimal(3))));
    }

    @Test
    void filter_path_tricky2() {
        CompiledFEELExpression nameRef = CompilerUtils.parseCodegen("[ {x:1, y:2}, {x:2, y:3} ][x]");
        LOG.debug("{}", nameRef);

        EvaluationContext context = EvaluationContextTestUtil.newEmptyEvaluationContext();
        context.setValue("x", false);
        Object result = nameRef.apply(context);
        LOG.debug("{}", result);

        assertThat(result).asList().isEmpty();
    }

    @Test
    void filter_path_selection() {
        // Selection
        assertThat(parseCodegenCompileEvaluate("[ {x:1, y:2}, {x:2, y:3} ].y")).asList().containsExactly(BigDecimal.valueOf(2), BigDecimal.valueOf(3));
        assertThat(parseCodegenCompileEvaluate("[ {x:1, y:2}, {x:2} ].y")).asList().containsExactly(BigDecimal.valueOf(2), null);
        assertThat(parseCodegenCompileEvaluate("[ {x:1, y:2}, {x:2, y:3} ].z")).asList().containsExactly(null, null);
    }

    @Test
    void test_for() {
        // for
        Object parseCompileEvaluate = parseCodegenCompileEvaluate("for x in [ 10, 20, 30 ], y in [ 1, 2, 3 ] return x * y");
		assertThat(parseCompileEvaluate).asList().
		containsExactly(BigDecimal.valueOf(10), BigDecimal.valueOf(20), BigDecimal.valueOf(30), BigDecimal.valueOf(20), BigDecimal.valueOf(40), BigDecimal.valueOf(60), BigDecimal.valueOf(30), BigDecimal.valueOf(60), BigDecimal.valueOf(90));

        // normal:
        assertThat(parseCodegenCompileEvaluate("for x in [1, 2, 3] return x+1")).asList().
            	containsExactly(BigDecimal.valueOf(2), BigDecimal.valueOf(3), BigDecimal.valueOf(4));
        
        // TODO in order to parse correctly the enhanced for loop it is required to configure the FEEL Profiles
    }

    @Test
    void test_nested_for() {
        List<BigDecimal> firstExpected = Arrays.asList(BigDecimal.ONE, BigDecimal.valueOf(2));
        List<BigDecimal> secondExpected = Arrays.asList(BigDecimal.valueOf(3), BigDecimal.valueOf(4));
        Object parseCompileEvaluate = parseCodegenCompileEvaluate("for x in [ [1, 2], [3, 4] ] return x");
        assertThat(parseCompileEvaluate).asList().
                containsExactly(firstExpected, secondExpected);
        parseCompileEvaluate = parseCodegenCompileEvaluate("for x in [ [1,2], [3,4] ] return for y in x return y");
        assertThat(parseCompileEvaluate).asList().
                containsExactly(firstExpected, secondExpected);
        parseCompileEvaluate = CompilerUtils.parseCodegenCompileEvaluate("for x in [ 1, 2, 3, 4 ], y in x return y");
        assertThat(parseCompileEvaluate).asList().
                containsExactly(BigDecimal.ONE, BigDecimal.valueOf(2), BigDecimal.valueOf(3), BigDecimal.valueOf(4));
        parseCompileEvaluate = parseCodegenCompileEvaluate("for x in [ [1,2], [3,4] ], y in x return y");
        assertThat(parseCompileEvaluate).asList().
                containsExactly(BigDecimal.ONE, BigDecimal.valueOf(2), BigDecimal.valueOf(3), BigDecimal.valueOf(4));
    }

    @Test
    void quantified_expressions() {
        // quantified expressions
        assertThat(parseCodegenCompileEvaluate("some price in [ 80, 11, 110 ] satisfies price > 100")).isEqualTo(Boolean.TRUE);
        assertThat(parseCodegenCompileEvaluate("some price in [ 80, 11, 90 ] satisfies price > 100")).isEqualTo(Boolean.FALSE);
        assertThat(parseCodegenCompileEvaluate("some x in [ 5, 6, 7 ], y in [ 10, 11, 6 ] satisfies x > y")).isEqualTo(Boolean.TRUE);
        assertThat(parseCodegenCompileEvaluate("every price in [ 80, 11, 90 ] satisfies price > 10")).isEqualTo(Boolean.TRUE);
        assertThat(parseCodegenCompileEvaluate("every price in [ 80, 11, 90 ] satisfies price > 70")).isEqualTo(Boolean.FALSE);
        assertThat(parseCodegenCompileEvaluate("some x in [ 5, 6, 7 ], y in [ 10, 11, 12 ] satisfies x < y")).isEqualTo(Boolean.TRUE);
        assertThat(parseCodegenCompileEvaluate("some price in [ 80, 11, 110 ] satisfies price > max(100, 50, 10)")).isEqualTo(Boolean.TRUE);
    }

    @Test
    void basic_function_invocation() {
        assertThat(parseCodegenCompileEvaluate("max(1, 2, 3)")).isEqualTo(new BigDecimal(3));
    }

    @Test
    void basic_function_definition() {
        assertThat(parseCodegenCompileEvaluate("function (a, b) a + b")).isInstanceOf(CustomFEELFunction.class);
        assertThat(parseCodegenCompileEvaluate("{ s : function (a, b) a + b, x : 1, y : 2, r : s(x,y) }.r")).isEqualTo(new BigDecimal(3));
    }

    @Test
    void named_function_invocation() {
        assertThat(parseCodegenCompileEvaluate("substring(start position: 2, string: \"FOOBAR\")")).isEqualTo("OOBAR");
        assertThat(parseCodegenCompileEvaluate("ceiling( n : 1.5 )")).isEqualTo(new BigDecimal("2"));
    }

    @Test
    void misc_from_original_feelinterpreted_test_suite() {
        assertThat(parseCodegenCompileEvaluate("if null then \"foo\" else \"bar\"")).isEqualTo("bar");
        assertThat(parseCodegenCompileEvaluate("{ hello world : function() \"Hello World!\", message : hello world() }.message")).isEqualTo("Hello World!");
        assertThat(parseCodegenCompileEvaluate("1 + if true then 1 else 2")).isEqualTo(new BigDecimal("2"));
        assertThat(parseCodegenCompileEvaluate("\"string with \\\"quotes\\\"\"")).isEqualTo("string with \"quotes\"");
        assertThat(parseCodegenCompileEvaluate("date( -0105, 8, 2 )")).isEqualTo(LocalDate.of(-105, 8, 2));
        assertThat(parseCodegenCompileEvaluate("string(null)")).isNull();
        assertThat(parseCodegenCompileEvaluate("[ null ]")).asList().containsExactly(new Object[]{null});
        assertThat(parseCodegenCompileEvaluate("[ null, null ]")).asList().containsExactly(null, null);
        assertThat(parseCodegenCompileEvaluate("[ null, 47, null ]")).asList().containsExactly(null, BigDecimal.valueOf(47), null);
    }

    @Test
    void benchmark_feel_expressions() {
        assertThat(parseCodegenCompileEvaluate("{ full name: { first name: \"John\", last name: \"Doe\" } }.full name.last name")).isEqualTo("Doe");
        assertThat(parseCodegenCompileEvaluate("some price in [ 80, 11, 110 ] satisfies price > 100")).isEqualTo(Boolean.TRUE);
        assertThat(parseCodegenCompileEvaluate("every price in [ 80, 11, 90 ] satisfies price > 10")).isEqualTo(Boolean.TRUE);
    }

    @Test
    void context_expression() {
        assertThat(parseCodegenCompileEvaluate("{}")).isEqualTo(Collections.emptyMap());
        assertThat(parseCodegenCompileEvaluate("{ }")).isEqualTo(Collections.emptyMap());
        assertThat(parseCodegenCompileEvaluate("{ a : 1 }")).isEqualTo(mapOf(entry("a", new BigDecimal(1))));
        assertThat(parseCodegenCompileEvaluate("{ \"a\" : 1 }")).isEqualTo(mapOf(entry("a", new BigDecimal(1))));
        assertThat(parseCodegenCompileEvaluate("{ \" a\" : 1 }")).isEqualTo(mapOf(entry("a", new BigDecimal(1)))); // Demonstrating a bad practice.
        assertThat(parseCodegenCompileEvaluate("{ a : 1, b : 2, c : 3 }")).isEqualTo(mapOf(entry("a", new BigDecimal(1)), entry("b", new BigDecimal(2)), entry("c", new BigDecimal(3))));
        assertThat(parseCodegenCompileEvaluate("{ a : 1, a name : \"John Doe\" }")).isEqualTo(mapOf(entry("a", new BigDecimal(1)), entry("a name", "John Doe")));

        assertThat(parseCodegenCompileEvaluate("{ a : 1, b : a }")).isEqualTo(mapOf(entry("a", new BigDecimal(1)), entry("b", new BigDecimal(1))));
    }

    /**
     * See {@link FEELParserTest}
     */
    @Test
    void contextWithMultipleEntries() {
        String inputExpression = "{ \"a string key\" : 10," + "\n"
                               + " a non-string key : 11," + "\n"
                               + " a key.with + /' odd chars : 12 }";
        assertThat(parseCodegenCompileEvaluate(inputExpression)).isEqualTo(mapOf(entry("a string key", new BigDecimal(10)), entry("a non-string key", new BigDecimal(11)), entry("a key.with + /' odd chars", new BigDecimal(12))));
    }

    /**
     * See {@link FEELParserTest}
     */
    @Test
    void nestedContexts() {
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
        assertThat(parseCodegenCompileEvaluate(inputExpression)).isEqualTo(mapOf(entry("a value", new BigDecimal(10)),
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
    void nestedContexts2() {
        String complexContext = "{ an applicant : {                                \n" +
                                "    home address : {                              \n" +
                                "        street name: \"broadway st\",             \n" +
                                "        city : \"New York\"                       \n" +
                                "    }                                             \n" +
                                "   },                                             \n" +
                                "   street : an applicant.home address.street name \n" +
                                "}                                                 ";
        assertThat(parseCodegenCompileEvaluate(complexContext)).isEqualTo(mapOf(entry("an applicant", mapOf(entry("home address", mapOf(entry("street name", "broadway st"),
                                                                                                                                        entry("city", "New York"))))),
                                                                                entry("street", "broadway st")));
    }

    @Test
    void nameReference() {
        String inputExpression = "someSimpleName";
        CompiledFEELExpression nameRef = parseCodegen(inputExpression, mapOf(entry("someSimpleName", BuiltInType.STRING) ) );
        LOG.debug("{}", nameRef);
        
        EvaluationContext context = EvaluationContextTestUtil.newEmptyEvaluationContext();
        context.setValue("someSimpleName", 123L);
        Object result = nameRef.apply(context);
        LOG.debug("{}", result);
        
        assertThat(result).isEqualTo(BigDecimal.valueOf(123));
    }

    @Test
    void qualifiedName() {
        String inputExpression = "My Person.Full Name";
        Type personType = new MapBackedType("Person", mapOf( entry("Full Name", BuiltInType.STRING), entry("Age", BuiltInType.NUMBER) ) );
        CompiledFEELExpression qualRef = parseCodegen(inputExpression, mapOf(entry("My Person", personType) ) );
        LOG.debug("{}", qualRef);
        
        EvaluationContext context = EvaluationContextTestUtil.newEmptyEvaluationContext();
        context.setValue("My Person", mapOf( entry("Full Name", "John Doe"), entry("Age", 47) ));
        Object result = qualRef.apply(context);
        LOG.debug("{}", result);
        
        assertThat(result).isEqualTo("John Doe" );

        // check number coercion for qualified name
        CompiledFEELExpression personAgeExpression = parseCodegen("My Person.Age", mapOf(entry("My Person", personType)));
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
    void qualifiedName2() {
        String inputExpression = "My Person.Full Name";
        Type personType = JavaBackedType.of(MyPerson.class);
        CompiledFEELExpression qualRef = parseCodegen(inputExpression, mapOf(entry("My Person", personType) ) );
        LOG.debug("{}", qualRef);
        
        EvaluationContext context = EvaluationContextTestUtil.newEmptyEvaluationContext();
        context.setValue("My Person", new MyPerson());
        Object result = qualRef.apply(context);
        LOG.debug("{}", result);
        
        assertThat(result).isEqualTo("John Doe" );
    }

    @Test
    void qualifiedName3() {
        String inputExpression = "a date.year";
        Type dateType = BuiltInType.DATE;
        CompiledFEELExpression qualRef = parseCodegen(inputExpression, mapOf(entry("a date", dateType)));
        LOG.debug("{}", qualRef);
        
        EvaluationContext context = EvaluationContextTestUtil.newEmptyEvaluationContext();
        context.setValue("a date", LocalDate.of(2016, 8, 2));
        Object result = qualRef.apply(context);
        LOG.debug("{}", result);
        
        assertThat(result).isEqualTo(BigDecimal.valueOf(2016));
    }

    

}
