package org.drools.mvelcompiler.phase4;

import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.expr.SimpleName;
import org.drools.constraint.parser.ast.visitor.DrlGenericVisitor;
import org.drools.mvelcompiler.context.MvelCompilerContext;
import org.drools.mvelcompiler.phase2.FirstChildProcessResult;

import static org.drools.modelcompiler.builder.generator.DrlxParseUtil.getAccessor;
import static org.drools.modelcompiler.util.ClassUtil.toRawClass;

public class ExpressionWithTypePhase implements DrlGenericVisitor<TypedExpression, ExpressionWithTypePhase.PreviousNode> {
    private final MvelCompilerContext mvelCompilerContext;

    public ExpressionWithTypePhase(MvelCompilerContext mvelCompilerContext) {
        this.mvelCompilerContext = mvelCompilerContext;
    }

    public TypedExpressions invoke(FirstChildProcessResult firstChildProcessResult) {
        TypedExpression firstNode = firstChildProcessResult.getFirstNode();
        List<TypedExpression> typedExpressions = new ArrayList<>();

        PreviousNode previousNode = new PreviousNode(firstNode.getType(), firstNode);
        firstChildProcessResult.getOtherNodes()
                .stream()
                .reduce(previousNode, (PreviousNode t, Node e) -> {
                    TypedExpression te = e.accept(this, t);
                    typedExpressions.add(te);
                    return new PreviousNode(te.getType(), te);
                }, (t1, t2) -> t1);

        return new TypedExpressions(typedExpressions);
    }

    @Override
    public TypedExpression visit(SimpleName name, PreviousNode previousNode) {
        Class<?> clazz = toRawClass(previousNode.type);
        Method accessor = getAccessor(clazz, name.asString());
        if (accessor != null) {
            return new MethodTypedExpression(name, previousNode.expression, accessor.getGenericReturnType(), accessor);
        }
        return null;
    }

    static class PreviousNode {

        final Type type;
        final TypedExpression expression;

        PreviousNode(Type type, TypedExpression previous) {
            this.type = type;
            this.expression = previous;
        }
    }
}

