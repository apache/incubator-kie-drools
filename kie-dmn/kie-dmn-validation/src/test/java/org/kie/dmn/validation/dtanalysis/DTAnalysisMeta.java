package org.kie.dmn.validation.dtanalysis;

import java.math.BigDecimal;
import java.util.Collection;

import org.drools.javaparser.JavaParser;
import org.drools.javaparser.ast.expr.Expression;
import org.drools.javaparser.ast.expr.IntegerLiteralExpr;
import org.drools.javaparser.ast.expr.MethodCallExpr;
import org.drools.javaparser.ast.expr.NullLiteralExpr;
import org.drools.javaparser.ast.expr.ObjectCreationExpr;
import org.drools.javaparser.ast.expr.StringLiteralExpr;
import org.kie.dmn.feel.runtime.Range.RangeBoundary;
import org.kie.dmn.validation.dtanalysis.model.Bound;
import org.kie.dmn.validation.dtanalysis.model.DTAnalysis;
import org.kie.dmn.validation.dtanalysis.model.Hyperrectangle;
import org.kie.dmn.validation.dtanalysis.model.Interval;

public class DTAnalysisMeta {

    public static Expression printGaps(DTAnalysis analysis) {
        Collection<Hyperrectangle> gaps = analysis.getGaps();
        MethodCallExpr parseExpression = JavaParser.parseExpression("Arrays.asList()");
        for (Hyperrectangle gap : gaps) {
            Expression gapAsExpression = gapAsExpression(gap);
            parseExpression.addArgument(gapAsExpression);
        }
        return parseExpression;
    }

    private static Expression gapAsExpression(Hyperrectangle gap) {
        int dimensions = gap.getDimensions();
        MethodCallExpr edgesExpression = JavaParser.parseExpression("Arrays.asList()");
        for (Interval edge : gap.getEdges()) {
            Expression intervalAsExpression = intervalAsExpression(edge);
            edgesExpression.addArgument(intervalAsExpression);
        }
        ObjectCreationExpr newExpression = JavaParser.parseExpression("new Hyperrectangle()");
        newExpression.addArgument(new IntegerLiteralExpr(dimensions));
        newExpression.addArgument(edgesExpression);
        return newExpression;
    }

    private static Expression intervalAsExpression(Interval edge) {
        MethodCallExpr newExpression = JavaParser.parseExpression("Interval.newFromBounds()");
        Expression lowerAsExpression = boundAsExpression(edge.getLowerBound());
        newExpression.addArgument(lowerAsExpression);
        Expression upperAsExpression = boundAsExpression(edge.getUpperBound());
        newExpression.addArgument(upperAsExpression);
        return newExpression;
    }

    private static Expression boundAsExpression(Bound<?> bound) {
        Comparable<?> value = bound.getValue();
        Expression valueExpr = null;
        if (value == Interval.NEG_INF) {
            valueExpr = JavaParser.parseExpression("Interval.NEG_INF");
        } else if (value == Interval.POS_INF) {
            valueExpr = JavaParser.parseExpression("Interval.POS_INF");
        } else if (value instanceof BigDecimal) {
            BigDecimal bigDecimal = (BigDecimal) value;
            ObjectCreationExpr newExpression = JavaParser.parseExpression("new BigDecimal()");
            StringLiteralExpr stringRep = new StringLiteralExpr(bigDecimal.toString());
            newExpression.addArgument(stringRep);
            valueExpr = newExpression;
        } else {
            throw new UnsupportedOperationException("TODO");
        }
        Expression typeExpr = null;
        if (bound.getBoundaryType() == RangeBoundary.OPEN) {
            typeExpr = JavaParser.parseExpression("RangeBoundary.OPEN");
        } else if (bound.getBoundaryType() == RangeBoundary.CLOSED) {
            typeExpr = JavaParser.parseExpression("RangeBoundary.CLOSED");
        } else {
            throw new IllegalStateException("??");
        }
        ObjectCreationExpr newExpression = JavaParser.parseExpression("new Bound()");
        newExpression.addArgument(valueExpr);
        newExpression.addArgument(typeExpr);
        newExpression.addArgument(new NullLiteralExpr());
        return newExpression;
    }

}
