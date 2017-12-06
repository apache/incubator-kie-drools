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

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.kie.dmn.feel.util.DynamicTypeUtils.entry;
import static org.kie.dmn.feel.util.DynamicTypeUtils.mapOf;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.Map;

import com.github.javaparser.ast.expr.Expression;
import org.antlr.v4.runtime.tree.ParseTree;
import org.junit.Test;
import org.kie.dmn.feel.lang.EvaluationContext;
import org.kie.dmn.feel.lang.FEELProperty;
import org.kie.dmn.feel.lang.Type;
import org.kie.dmn.feel.lang.impl.EvaluationContextImpl;
import org.kie.dmn.feel.lang.impl.JavaBackedType;
import org.kie.dmn.feel.lang.impl.MapBackedType;
import org.kie.dmn.feel.lang.types.BuiltInType;
import org.kie.dmn.feel.parser.feel11.FEELParser;
import org.kie.dmn.feel.parser.feel11.FEELParserTest;
import org.kie.dmn.feel.parser.feel11.FEEL_1_1Parser;
import org.kie.dmn.feel.runtime.FEELConditionsAndLoopsTest;
import org.kie.dmn.feel.runtime.FEELTernaryLogicTest;

public class DirectCompilerTest {
    
    private Object parseCompileEvaluate(String feelLiteralExpression) {
        CompiledFEELExpression compiledExpression = parse( feelLiteralExpression );
        System.out.println(compiledExpression);
        
        EvaluationContext emptyContext = new EvaluationContextImpl(null);
        Object result = compiledExpression.apply(emptyContext);
        System.out.println(result);
        return result;
    }

    @Test
    public void test_FEEL_number() {
        assertThat(parseCompileEvaluate("10"), is( BigDecimal.valueOf(10) ));
    }
    
    @Test
    public void test_FEEL_negative_number() {
        assertThat(parseCompileEvaluate("-10"), is( BigDecimal.valueOf(-10) ));
    }
    
    @Test
    public void test_FEEL_DROOLS_2143() {
        // DROOLS-2143: Allow ''--1' expression as per FEEL grammar rule 26 
        assertThat(parseCompileEvaluate("--10"), is(BigDecimal.valueOf(10)));
        assertThat(parseCompileEvaluate("---10"), is(BigDecimal.valueOf(-10)));
        assertThat(parseCompileEvaluate("+10"), is(BigDecimal.valueOf(10)));
    }

    @Test
    public void test_FEEL_boolean() {
        assertThat(parseCompileEvaluate("false"), is( false ));
        assertThat(parseCompileEvaluate("true"), is( true ));
        assertThat(parseCompileEvaluate("null"), nullValue());
    }
    
    @Test
    public void test_FEEL_null() {
        assertThat(parseCompileEvaluate("null"), nullValue());
    }
    
    @Test
    public void test_FEEL_string() {
        assertThat(parseCompileEvaluate("\"some string\""), is( "some string" ));
    }
    
    @Test
    public void test_primary_parens() {
        assertThat(parseCompileEvaluate("(\"some string\")"), is( "some string" ));
        assertThat(parseCompileEvaluate("(123)"), is( BigDecimal.valueOf(123) ));
        assertThat(parseCompileEvaluate("(-123)"), is( BigDecimal.valueOf(-123) ));
        assertThat(parseCompileEvaluate("-(123)"), is( BigDecimal.valueOf(-123) ));
        assertThat(parseCompileEvaluate("(false)"), is( false ));
        assertThat(parseCompileEvaluate("(true)"), is( true ));
    }
    
    /**
     * See {@link FEELTernaryLogicTest}
     */
    @Test
    public void test_ternary_logic() {
        assertThat(parseCompileEvaluate( "true and true"), is( Boolean.TRUE ));
        assertThat(parseCompileEvaluate( "true and false"), is( Boolean.FALSE ));
        assertThat(parseCompileEvaluate( "true and null"), nullValue());
        assertThat(parseCompileEvaluate( "false and true"), is( Boolean.FALSE ));
        assertThat(parseCompileEvaluate( "false and false"), is( Boolean.FALSE ));
        assertThat(parseCompileEvaluate( "false and null"), is( Boolean.FALSE ));
        assertThat(parseCompileEvaluate( "null and true"), nullValue());
        assertThat(parseCompileEvaluate( "null and false"), is( Boolean.FALSE ));
        assertThat(parseCompileEvaluate( "null and null"), nullValue());
        assertThat(parseCompileEvaluate( "true or true"), is( Boolean.TRUE ));
        assertThat(parseCompileEvaluate( "true or false"), is( Boolean.TRUE ));
        assertThat(parseCompileEvaluate( "true or null"), is(  Boolean.TRUE ));
        assertThat(parseCompileEvaluate( "false or true"), is( Boolean.TRUE ));
        assertThat(parseCompileEvaluate( "false or false"), is( Boolean.FALSE ));
        assertThat(parseCompileEvaluate( "false or null"), nullValue());
        assertThat(parseCompileEvaluate( "null or true"), is( Boolean.TRUE ));
        assertThat(parseCompileEvaluate( "null or false"), nullValue());
        assertThat(parseCompileEvaluate( "null or null"), nullValue());
        // logical operator priority
        assertThat(parseCompileEvaluate( "false and false or true"), is( Boolean.TRUE ));
        assertThat(parseCompileEvaluate( "false and (false or true)"), is( Boolean.FALSE ));
        assertThat(parseCompileEvaluate( "true or false and false"), is( Boolean.TRUE ));
        assertThat(parseCompileEvaluate( "(true or false) and false"), is( Boolean.FALSE  ));
    }
    
    /**
     * Partially from {@link FEELConditionsAndLoopsTest}
     */
    @Test
    public void test_if() {
        assertThat(parseCompileEvaluate( "if true then 15 else 5"), is(BigDecimal.valueOf( 15 )));
        assertThat(parseCompileEvaluate( "if false then 15 else 5"), is(BigDecimal.valueOf( 5 )));
        assertThat(parseCompileEvaluate( "if null then 15 else 5"), nullValue());
        assertThat(parseCompileEvaluate( "if \"hello\" then 15 else 5"), nullValue());
    }
    
    @Test
    public void test_additiveExpression() {
        assertThat(parseCompileEvaluate( "1 + 2"), is(BigDecimal.valueOf( 3 )));
        assertThat(parseCompileEvaluate( "1 + null"), nullValue());
        assertThat(parseCompileEvaluate( "1 - 2"), is(BigDecimal.valueOf( -1 )));
        assertThat(parseCompileEvaluate( "1 - null"), nullValue());
        assertThat(parseCompileEvaluate( "\"Hello, \" + \"World\""), is("Hello, World"));
    }
    
    @Test
    public void test_multiplicativeExpression() {
        assertThat(parseCompileEvaluate("3 * 5"), is(BigDecimal.valueOf(15)));
        assertThat(parseCompileEvaluate("3 * null"), nullValue());
        assertThat(parseCompileEvaluate("10 / 2"), is(BigDecimal.valueOf(5)));
        assertThat(parseCompileEvaluate("10 / null"), nullValue());
    }

    @Test
    public void test_exponentiationExpression() {
        assertThat(parseCompileEvaluate("3 ** 3"), is(BigDecimal.valueOf(27)));
        assertThat(parseCompileEvaluate("3 ** null"), nullValue());
    }

    @Test
    public void test_logicalNegationExpression() {
        assertThat(parseCompileEvaluate("not true"), is(false));
        assertThat(parseCompileEvaluate("not false"), is(true));
        assertThat(parseCompileEvaluate("not null"), nullValue());
        assertThat(parseCompileEvaluate("not 3"), nullValue());
    }

    @Test
    public void test_listExpression() {
        assertThat(parseCompileEvaluate("[]"), is(Collections.emptyList()));
        assertThat(parseCompileEvaluate("[ ]"), is(Collections.emptyList()));
        assertThat(parseCompileEvaluate("[1]"), is(Arrays.asList(BigDecimal.valueOf(1))));
        assertThat(parseCompileEvaluate("[1, 2,3]"), is(Arrays.asList(BigDecimal.valueOf(1), BigDecimal.valueOf(2), BigDecimal.valueOf(3))));
    }

    @Test
    public void test_contextExpression() {
        assertThat(parseCompileEvaluate("{}"), is(Collections.emptyMap()));
        assertThat(parseCompileEvaluate("{ }"), is(Collections.emptyMap()));
        assertThat(parseCompileEvaluate("{ a : 1 }"), is(mapOf(entry("a", new BigDecimal(1)))));
        assertThat(parseCompileEvaluate("{ \"a\" : 1 }"), is(mapOf(entry("a", new BigDecimal(1)))));
        assertThat(parseCompileEvaluate("{ \" a\" : 1 }"), is(mapOf(entry(" a", new BigDecimal(1))))); // Demonstrating a bad practice.
        assertThat(parseCompileEvaluate("{ a : 1, b : 2, c : 3 }"), is(mapOf(entry("a", new BigDecimal(1)), entry("b", new BigDecimal(2)), entry("c", new BigDecimal(3)))));
        assertThat(parseCompileEvaluate("{ a : 1, a name : \"John Doe\" }"), is(mapOf(entry("a", new BigDecimal(1)), entry("a name", "John Doe"))));

        assertThat(parseCompileEvaluate("{ a : 1, b : a }"), is(mapOf(entry("a", new BigDecimal(1)), entry("b", new BigDecimal(1)))));
    }
    
    /**
     * See {@link FEELParserTest}
     */
    @Test
    public void testContextWithMultipleEntries() {
        String inputExpression = "{ \"a string key\" : 10," + "\n"
                               + " a non-string key : 11," + "\n"
                               + " a key.with + /' odd chars : 12 }";
        assertThat(parseCompileEvaluate(inputExpression), is(mapOf(entry("a string key", new BigDecimal(10)), entry("a non-string key", new BigDecimal(11)), entry("a key.with + /' odd chars", new BigDecimal(12)))));
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
        assertThat(parseCompileEvaluate(inputExpression), is(mapOf(entry("a value", new BigDecimal(10)), 
                                                                   entry("an applicant", mapOf(entry("first name", "Edson"),
                                                                                               entry("last + name", "Tirelli"),
                                                                                               entry("full name", "EdsonTirelli"),
                                                                                               entry("address", mapOf(entry("street", "55 broadway st"),
                                                                                                                      entry("city", "New York"))),
                                                                                               entry("xxx", "Tirelli"))))));
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
        assertThat(parseCompileEvaluate(complexContext), is(mapOf(entry("an applicant", mapOf(entry("home address", mapOf(entry("street name", "broadway st"),
                                                                                                                          entry("city", "New York"))))),
                                                                  entry("street", "broadway st"))));
    }

    @Test
    public void testNameReference() {
        String inputExpression = "someSimpleName";
        CompiledFEELExpression nameRef = parse( inputExpression, mapOf( entry("someSimpleName", BuiltInType.STRING) ) );
        System.out.println(nameRef);
        
        EvaluationContext context = new EvaluationContextImpl(null);
        context.setValue("someSimpleName", 123L);
        Object result = nameRef.apply(context);
        System.out.println(result);
        
        assertThat(result, is( BigDecimal.valueOf(123) ));
    }
    
    @Test
    public void testQualifiedName() {
        String inputExpression = "My Person.Full Name";
        Type personType = new MapBackedType("Person", mapOf( entry("Full Name", BuiltInType.STRING), entry("Age", BuiltInType.NUMBER) ) );
        CompiledFEELExpression qualRef = parse( inputExpression, mapOf( entry("My Person", personType) ) );
        System.out.println(qualRef);
        
        EvaluationContext context = new EvaluationContextImpl(null);
        context.setValue("My Person", mapOf( entry("Full Name", "John Doe"), entry("Age", 47) ));
        Object result = qualRef.apply(context);
        System.out.println(result);
        
        assertThat(result, is( "John Doe" ));
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
        System.out.println(qualRef);
        
        EvaluationContext context = new EvaluationContextImpl(null);
        context.setValue("My Person", new MyPerson());
        Object result = qualRef.apply(context);
        System.out.println(result);
        
        assertThat(result, is( "John Doe" ));
    }

    private CompiledFEELExpression parse(String input) {
        return parse( input, Collections.emptyMap() );
    }

    private CompiledFEELExpression parse(String input, Map<String, Type> inputTypes) {
        FEEL_1_1Parser parser = FEELParser.parse(null, input, inputTypes, Collections.emptyMap(), Collections.emptyList());

        ParseTree tree = parser.compilation_unit();

        DirectCompilerVisitor v = new DirectCompilerVisitor(inputTypes);
        DirectCompilerResult directResult = v.visit(tree);
        
        Expression expr = directResult.getExpression();
        CompiledFEELExpression cu = new CompilerBytecodeLoader().makeFromJPExpression(input, expr, directResult.getFieldDeclarations());

        return cu;
    }
    

}
