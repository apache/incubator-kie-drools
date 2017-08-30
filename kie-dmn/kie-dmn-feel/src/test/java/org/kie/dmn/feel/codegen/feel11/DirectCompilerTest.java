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
import java.util.Collections;
import java.util.Map;

import com.github.javaparser.ast.expr.Expression;
import org.antlr.v4.runtime.tree.ParseTree;
import org.junit.Test;
import org.kie.dmn.feel.lang.FEELProperty;
import org.kie.dmn.feel.lang.Type;
import org.kie.dmn.feel.lang.impl.JavaBackedType;
import org.kie.dmn.feel.lang.impl.MapBackedType;
import org.kie.dmn.feel.lang.types.BuiltInType;
import org.kie.dmn.feel.parser.feel11.FEELParser;
import org.kie.dmn.feel.parser.feel11.FEEL_1_1Parser;
import org.kie.dmn.feel.runtime.FEELTernaryLogicTest;

public class DirectCompilerTest {
    
    private Object parseCompileEvaluate(String feelLiteralExpression) {
        CompiledFEELExpression compiledExpression = parse( feelLiteralExpression );
        System.out.println(compiledExpression);
        
        CompiledContextImpl emptyContext = new CompiledContextImpl();
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
    
    @Test
    public void testNameReference() {
        String inputExpression = "someSimpleName";
        CompiledFEELExpression nameRef = parse( inputExpression, mapOf( entry("someSimpleName", BuiltInType.STRING) ) );
        System.out.println(nameRef);
        
        CompiledContextImpl context = new CompiledContextImpl();
        context.set("someSimpleName", 123L);
        Object result = context.accept(nameRef);
        System.out.println(result);
        
        assertThat(result, is( 123L ));
    }
    
    @Test
    public void testQualifiedName() {
        String inputExpression = "My Person.Full Name";
        Type personType = new MapBackedType("Person", mapOf( entry("Full Name", BuiltInType.STRING), entry("Age", BuiltInType.NUMBER) ) );
        CompiledFEELExpression qualRef = parse( inputExpression, mapOf( entry("My Person", personType) ) );
        System.out.println(qualRef);
        
        CompiledContextImpl context = new CompiledContextImpl();
        context.set("My Person", mapOf( entry("Full Name", "John Doe"), entry("Age", 47) ));
        Object result = context.accept(qualRef);
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
        
        CompiledContextImpl context = new CompiledContextImpl();
        context.set("My Person", new MyPerson());
        Object result = context.accept(qualRef);
        System.out.println(result);
        
        assertThat(result, is( "John Doe" ));
    }

    private CompiledFEELExpression parse(String input) {
        return parse( input, Collections.emptyMap() );
    }

    private CompiledFEELExpression parse(String input, Map<String, Type> inputTypes) {
        FEEL_1_1Parser parser = FEELParser.parse( null, input, inputTypes, Collections.emptyMap() );

        ParseTree tree = parser.expression();

        DirectCompilerVisitor v = new DirectCompilerVisitor(inputTypes);
        DirectCompilerResult directResult = v.visit(tree);
        
        Expression expr = directResult.expression;
        CompiledFEELExpression cu = new CompilerBytecodeLoader().makeFromJPExpression(expr);

        return cu;
    }
    

}
