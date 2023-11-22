package org.drools.mvelcompiler.ast;

import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.NameExpr;
import org.drools.mvel.parser.ast.expr.ListCreationLiteralExpressionElement;
import org.drools.mvel.parser.ast.expr.MapCreationLiteralExpression;
import org.drools.mvel.parser.ast.expr.MapCreationLiteralExpressionKeyValuePair;

import java.lang.reflect.Type;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static com.github.javaparser.ast.NodeList.toNodeList;

public class MapExprT implements TypedExpression {

    private final MapCreationLiteralExpression mapExpression;

    public MapExprT(final MapCreationLiteralExpression mapExpression) {
        this.mapExpression = mapExpression;
    }

    @Override
    public Optional<Type> getType() {
        return Optional.of(Map.class);
    }

    @Override
    public Node toJavaExpression() {
        if (mapExpression.getExpressions() == null || mapExpression.getExpressions().isEmpty()) {
            return new MethodCallExpr(new NameExpr(Collections.class.getCanonicalName()), "emptyMap");
        } else {
            return new MethodCallExpr(new NameExpr(Map.class.getCanonicalName()), "ofEntries", getMapEntryExpressions(mapExpression.getExpressions()));
        }
    }

    private NodeList<Expression> getMapEntryExpressions(final NodeList<Expression> mapEntries) {
        return mapEntries.stream()
                .filter(mapEntry -> mapEntry instanceof MapCreationLiteralExpressionKeyValuePair)
                .map(mapEntry ->
                    new MethodCallExpr(
                            new NameExpr(Map.class.getCanonicalName()),
                            "entry",
                            NodeList.nodeList(((MapCreationLiteralExpressionKeyValuePair) mapEntry).getKey(),
                                    ((MapCreationLiteralExpressionKeyValuePair) mapEntry).getValue()))
                ).collect(toNodeList());
    }
}
