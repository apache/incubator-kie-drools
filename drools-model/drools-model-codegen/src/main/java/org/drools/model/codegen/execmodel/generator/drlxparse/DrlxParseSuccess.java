package org.drools.model.codegen.execmodel.generator.drlxparse;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import com.github.javaparser.ast.expr.Expression;
import org.drools.mvel.parser.ast.expr.OOPathExpr;

public interface DrlxParseSuccess extends DrlxParseResult {

    boolean isPredicate();

    String getExprBinding();

    Expression getExpr();

    boolean isRequiresSplit();

    boolean isTemporal();

    DrlxParseSuccess addAllWatchedProperties(Collection<String> watchedProperties);

    Optional<Expression> getImplicitCastExpression();

    List<Expression> getNullSafeExpressions();

    default boolean isOOPath() {
        return getExpr() instanceof OOPathExpr;
    }
}
