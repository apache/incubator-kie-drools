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
import static org.junit.Assert.assertThat;
import static org.kie.dmn.feel.util.DynamicTypeUtils.entry;
import static org.kie.dmn.feel.util.DynamicTypeUtils.mapOf;

import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.expr.CastExpr;
import com.github.javaparser.ast.expr.EnclosedExpr;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.expr.ObjectCreationExpr;
import com.github.javaparser.ast.expr.StringLiteralExpr;
import org.antlr.v4.runtime.tree.ParseTree;
import org.junit.Test;
import org.kie.dmn.feel.lang.CompositeType;
import org.kie.dmn.feel.lang.EvaluationContext;
import org.kie.dmn.feel.lang.FEELProperty;
import org.kie.dmn.feel.lang.Type;
import org.kie.dmn.feel.lang.ast.BaseNode;
import org.kie.dmn.feel.lang.ast.NameRefNode;
import org.kie.dmn.feel.lang.ast.NumberNode;
import org.kie.dmn.feel.lang.ast.QualifiedNameNode;
import org.kie.dmn.feel.lang.ast.StringNode;
import org.kie.dmn.feel.lang.impl.EvaluationContextImpl;
import org.kie.dmn.feel.lang.impl.JavaBackedType;
import org.kie.dmn.feel.lang.impl.MapBackedType;
import org.kie.dmn.feel.lang.types.BuiltInType;
import org.kie.dmn.feel.parser.feel11.ASTBuilderVisitor;
import org.kie.dmn.feel.parser.feel11.FEELParser;
import org.kie.dmn.feel.parser.feel11.FEEL_1_1Parser;
import org.kie.dmn.feel.util.EvalHelper;

// TODO Current trying to bypass FEEL AST and go straight to codegen
@Deprecated()
public class CompiledFEELParserTest {

    @Test
    public void testIntegerLiteral() {
        String inputExpression = "10";
        CompiledFEELExpression number = parse( inputExpression );
        System.out.println(number);
        
        EvaluationContext context = new EvaluationContextImpl(null);
        Object result = number.apply(context);
        System.out.println(result);
        
        assertThat(result, is( BigDecimal.valueOf(10) ));
    }
    
    @Test
    public void testStringLiteral() {
        String inputExpression = "\"some string\"";
        CompiledFEELExpression stringLit = parse( inputExpression );
        System.out.println(stringLit);
        
        EvaluationContext context = new EvaluationContextImpl(null);
        Object result = stringLit.apply(context);
        System.out.println(result);
        
        assertThat(result, is( "some string" ));
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
        FEEL_1_1Parser parser = FEELParser.parse( null, input, inputTypes, Collections.emptyMap() );

        ParseTree tree = parser.expression();

        ASTBuilderVisitor v = new ASTBuilderVisitor(inputTypes);
        BaseNode baseNodeExpr = v.visit( tree );
        
        BaseNodeVisitor baseNodeVisitor = new BaseNodeVisitor(inputTypes);
        Expression parse = baseNodeVisitor.visit(baseNodeExpr);
        CompiledFEELExpression cu = new CompilerBytecodeLoader().makeFromJPExpression(parse);

        return cu;
    }

    public static class BaseNodeVisitor {
        
        private Map<String, Type> inputTypes;

        public BaseNodeVisitor(Map<String, Type> inputTypes) {
            this.inputTypes = inputTypes;
        }

        public Expression visit(BaseNode node) {
            // poor man visitor as undecided if to go full OOP or FP
            if ( node instanceof NumberNode ) {
                return visit( (NumberNode) node );
            } else if ( node instanceof StringNode ) {
                return visit( (StringNode) node );
            } else if ( node instanceof NameRefNode ) {
                return visit( (NameRefNode) node );
            } else if ( node instanceof QualifiedNameNode ) {
                return visit( (QualifiedNameNode) node);
            }
            throw new UnsupportedOperationException("The visitor pattern matcher missed for "+node.getClass());
        }
        
        public Expression visit(NumberNode node) {
            ObjectCreationExpr result = new ObjectCreationExpr();
            result.setType( JavaParser.parseClassOrInterfaceType(BigDecimal.class.getCanonicalName()) );
            result.addArgument( node.getText() );
            result.addArgument( JavaParser.parseExpression( "java.math.MathContext.DECIMAL128" ) );
            return result;
        }
        
        public Expression visit(StringNode node) {
            return new StringLiteralExpr( EvalHelper.unescapeString( node.getText() ) );
        }
        
        public Expression visit(NameRefNode node) {
//            return new NameExpr( node.getText() );
            NameExpr scope = new NameExpr( "feelExprCtx" );
            MethodCallExpr getFromScope = new MethodCallExpr(scope, "getValue" );
            getFromScope.addArgument( new StringLiteralExpr( node.getText() ) );
            return getFromScope;
        }
        
        public Expression visit(QualifiedNameNode node) {
            List<NameRefNode> parts = node.getParts();
            Type typeCursor = inputTypes.get( parts.get(0).getText() );
            Expression exprCursor = visit( parts.get(0) );
            for ( NameRefNode acc : parts.subList(1, parts.size()) ) {
                if ( typeCursor instanceof CompositeType ) {
                    CompositeType compositeType = (CompositeType) typeCursor;
                    
                    // setting next typeCursor
                    typeCursor = compositeType.getFields().get(acc.getText());
                    
                    // setting next exprCursor
                    if ( compositeType instanceof MapBackedType ) {
                        CastExpr castExpr = new CastExpr( JavaParser.parseType(Map.class.getCanonicalName()), exprCursor);
                        EnclosedExpr enclosedExpr = new EnclosedExpr(castExpr);
                        MethodCallExpr getExpr = new MethodCallExpr(enclosedExpr, "get");
                        getExpr.addArgument( new StringLiteralExpr( acc.getText() ) );
                        exprCursor = getExpr;
                    } else if ( compositeType instanceof JavaBackedType ) {
                        JavaBackedType javaBackedType = (JavaBackedType) compositeType;
                        Method accessor = EvalHelper.getGenericAccessor(javaBackedType.getWrapped(), acc.getText());
                        CastExpr castExpr = new CastExpr( JavaParser.parseType(javaBackedType.getWrapped().getCanonicalName()), exprCursor);
                        EnclosedExpr enclosedExpr = new EnclosedExpr(castExpr);
                        exprCursor = new MethodCallExpr(enclosedExpr, accessor.getName());
                    } else {
                        throw new UnsupportedOperationException("A Composite type is either MapBacked or JavaBAcked");
                    }
                } else {
                    throw new UnsupportedOperationException("Trying to access" + node + " but typeCursor not a CompositeType "+typeCursor);
                }
            }
            return exprCursor;
        }
    }
}
