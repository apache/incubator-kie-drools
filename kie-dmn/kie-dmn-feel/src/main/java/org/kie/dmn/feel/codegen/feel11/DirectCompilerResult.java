package org.kie.dmn.feel.codegen.feel11;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.expr.Expression;
import org.kie.dmn.feel.lang.Type;

public class DirectCompilerResult {

    public final Expression expression;
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
    
    public Set<FieldDeclaration> getFieldDeclarations() {
        return Collections.unmodifiableSet(fieldDeclarations);
    }
    
    public boolean addFieldDesclaration(FieldDeclaration d) {
        return fieldDeclarations.add(d);
    }
    
    public static Set<FieldDeclaration> unifyFDs( DirectCompilerResult... sets ) {
        Set<FieldDeclaration> result = new HashSet<>();
        for ( DirectCompilerResult fs : sets ) {
            result.addAll(fs.getFieldDeclarations());
        }
        return result;
    }
}
