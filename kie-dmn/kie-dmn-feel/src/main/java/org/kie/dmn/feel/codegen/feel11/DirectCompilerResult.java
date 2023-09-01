package org.kie.dmn.feel.codegen.feel11;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.expr.Expression;
import org.kie.dmn.feel.lang.Type;

public class DirectCompilerResult {

    private final Expression expression;
    public final Type resultType;
    
    private final Set<FieldDeclaration> fieldDeclarations = new HashSet<>();

    public DirectCompilerResult(Expression expression,
                                Type resultType,
                                Set<FieldDeclaration> fieldDeclarations) {
        this.expression = expression;
        this.resultType = resultType;
        this.fieldDeclarations.addAll(fieldDeclarations);
    }

    public static DirectCompilerResult of(Expression expression, Type resultType) {
        return new DirectCompilerResult(expression, resultType, Collections.emptySet());
    }
    
    public static DirectCompilerResult of(Expression expression, Type resultType, Set<FieldDeclaration> fieldDeclarations) {
        return new DirectCompilerResult(expression, resultType, fieldDeclarations);
    }
    
    public static DirectCompilerResult of(Expression expression, Type resultType, FieldDeclaration fieldDeclaration) {
        Set<FieldDeclaration> singleton = new HashSet<>();
        singleton.add(fieldDeclaration);
        return new DirectCompilerResult(expression, resultType, singleton);
    }
    
    public DirectCompilerResult withFD(DirectCompilerResult from) {
        this.fieldDeclarations.addAll(from.getFieldDeclarations());
        return this;
    }

    public DirectCompilerResult withFD(Set<FieldDeclaration> from) {
        this.fieldDeclarations.addAll(from);
        return this;
    }

    public Set<FieldDeclaration> getFieldDeclarations() {
        return Collections.unmodifiableSet(fieldDeclarations);
    }
    
    public boolean addFieldDeclaration(FieldDeclaration d) {
        return fieldDeclarations.add(d);
    }

    public static Set<FieldDeclaration> mergeFDs( List<DirectCompilerResult> sets ) {
        Set<FieldDeclaration> result = new HashSet<>();
        for ( DirectCompilerResult fs : sets ) {
            result.addAll(fs.getFieldDeclarations());
        }
        return result;
    }
    
    public static Set<FieldDeclaration> mergeFDs( DirectCompilerResult... sets ) {
        return mergeFDs(Arrays.asList(sets));
    }

    public Expression getExpression() {
        return expression;
    }
}
