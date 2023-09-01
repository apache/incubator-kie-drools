package org.kie.dmn.feel.codegen.feel11;

import java.lang.reflect.Method;
import java.util.Map;

import com.github.javaparser.ast.expr.EnclosedExpr;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.StringLiteralExpr;
import com.github.javaparser.ast.type.Type;
import org.kie.dmn.feel.lang.CompositeType;
import org.kie.dmn.feel.lang.impl.JavaBackedType;
import org.kie.dmn.feel.lang.impl.MapBackedType;
import org.kie.dmn.feel.util.EvalHelper;

import static com.github.javaparser.StaticJavaParser.parseType;

public class Contexts {

    public static final Type MapT = parseType(Map.class.getCanonicalName());

    public static Expression getKey(Expression currentContext, CompositeType contextType, String key) {
        if (contextType instanceof MapBackedType) {
            EnclosedExpr enclosedExpr = Expressions.castTo(MapT, currentContext);
            return new MethodCallExpr(enclosedExpr, "get")
                    .addArgument(new StringLiteralExpr(key));
        } else if (contextType instanceof JavaBackedType) {
            JavaBackedType javaBackedType = (JavaBackedType) contextType;
            Class<?> wrappedType = javaBackedType.getWrapped();
            Method accessor = EvalHelper.getGenericAccessor(wrappedType, key);
            Type type = parseType(wrappedType.getCanonicalName());
            return new MethodCallExpr(Expressions.castTo(type, currentContext), accessor.getName());
        } else {
            throw new UnsupportedOperationException("A Composite type is either MapBacked or JavaBAcked");
        }
    }
}
