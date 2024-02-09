/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.kie.dmn.validation.dtanalysis.utils;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collection;

import com.github.javaparser.ast.expr.BooleanLiteralExpr;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.IntegerLiteralExpr;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.NullLiteralExpr;
import com.github.javaparser.ast.expr.ObjectCreationExpr;
import com.github.javaparser.ast.expr.StringLiteralExpr;
import org.kie.dmn.feel.lang.types.impl.ComparablePeriod;
import org.kie.dmn.feel.runtime.Range.RangeBoundary;
import org.kie.dmn.validation.dtanalysis.model.Bound;
import org.kie.dmn.validation.dtanalysis.model.DTAnalysis;
import org.kie.dmn.validation.dtanalysis.model.Hyperrectangle;
import org.kie.dmn.validation.dtanalysis.model.Interval;
import org.kie.dmn.validation.dtanalysis.model.Overlap;

import static com.github.javaparser.StaticJavaParser.parseExpression;

public class DTAnalysisMeta {

    public static Expression printGaps(DTAnalysis analysis) {
        Collection<Hyperrectangle> gaps = analysis.getGaps();
        MethodCallExpr parseExpression = parseExpression("Arrays.asList()");
        for (Hyperrectangle gap : gaps) {
            Expression gapAsExpression = hrAsExpression(gap);
            parseExpression.addArgument(gapAsExpression);
        }
        return parseExpression;
    }

    public static Expression printOverlaps(DTAnalysis analysis) {
        Collection<Overlap> overlaps = analysis.getOverlaps();
        MethodCallExpr parseExpression = parseExpression("Arrays.asList()");
        for (Overlap overlap : overlaps) {
            Expression overlapAsExpression = overlapAsExpression(overlap);
            parseExpression.addArgument(overlapAsExpression);
        }
        return parseExpression;
    }

    private static Expression overlapAsExpression(Overlap overlap) {
        MethodCallExpr edgesExpression = parseExpression("Arrays.asList()");
        for (Number edge : overlap.getRules()) {
            edgesExpression.addArgument(new IntegerLiteralExpr(edge.intValue()));
        }
        ObjectCreationExpr newExpression = parseExpression("new Overlap()");
        newExpression.addArgument(edgesExpression);
        newExpression.addArgument(hrAsExpression(overlap.getOverlap()));
        return newExpression;
    }

    private static Expression hrAsExpression(Hyperrectangle gap) {
        int dimensions = gap.getDimensions();
        MethodCallExpr edgesExpression = parseExpression("Arrays.asList()");
        for (Interval edge : gap.getEdges()) {
            Expression intervalAsExpression = intervalAsExpression(edge);
            edgesExpression.addArgument(intervalAsExpression);
        }
        ObjectCreationExpr newExpression = parseExpression("new Hyperrectangle()");
        newExpression.addArgument(new IntegerLiteralExpr(dimensions));
        newExpression.addArgument(edgesExpression);
        return newExpression;
    }

    private static Expression intervalAsExpression(Interval edge) {
        MethodCallExpr newExpression = parseExpression("Interval.newFromBounds()");
        Expression lowerAsExpression = boundAsExpression(edge.getLowerBound());
        newExpression.addArgument(lowerAsExpression);
        Expression upperAsExpression = boundAsExpression(edge.getUpperBound());
        newExpression.addArgument(upperAsExpression);
        return newExpression;
    }

    private static Expression boundAsExpression(Bound<?> bound) {
        Comparable<?> value = bound.getValue();
        Expression valueExpr;
        if (value == Interval.NEG_INF) {
            valueExpr = parseExpression("Interval.NEG_INF");
        } else if (value == Interval.POS_INF) {
            valueExpr = parseExpression("Interval.POS_INF");
        } else if (value instanceof BigDecimal) {
            BigDecimal bigDecimal = (BigDecimal) value;
            ObjectCreationExpr newExpression = parseExpression("new BigDecimal()");
            StringLiteralExpr stringRep = new StringLiteralExpr(bigDecimal.toString());
            newExpression.addArgument(stringRep);
            valueExpr = newExpression;
        } else if (value instanceof String) {
            String string = (String) value;
            StringLiteralExpr newExpression = new StringLiteralExpr();
            newExpression.setString(string);
            valueExpr = newExpression;
        } else if (value instanceof Boolean) {
            Boolean b = (Boolean) value;
            valueExpr = new BooleanLiteralExpr(b);
        } else if (value instanceof LocalDate) {
            LocalDate localDateTime = (LocalDate) value;
            MethodCallExpr newExpression = parseExpression("java.time.LocalDate.parse()");
            StringLiteralExpr stringRep = new StringLiteralExpr(localDateTime.toString());
            newExpression.addArgument(stringRep);
            valueExpr = newExpression;
        } else if (value instanceof ComparablePeriod) {
            ComparablePeriod comparablePeriod = (ComparablePeriod) value;
            MethodCallExpr newExpression = parseExpression("org.kie.dmn.feel.lang.types.impl.ComparablePeriod.parse()");
            StringLiteralExpr stringRep = new StringLiteralExpr(comparablePeriod.asPeriod().toString());
            newExpression.addArgument(stringRep);
            valueExpr = newExpression;
        } else {
            throw new UnsupportedOperationException("boundAsExpression value " + value + " not supported.");
        }
        Expression typeExpr;
        if (bound.getBoundaryType() == RangeBoundary.OPEN) {
            typeExpr = parseExpression("RangeBoundary.OPEN");
        } else if (bound.getBoundaryType() == RangeBoundary.CLOSED) {
            typeExpr = parseExpression("RangeBoundary.CLOSED");
        } else {
            throw new IllegalStateException("illegal getBoundaryType");
        }
        ObjectCreationExpr newExpression = parseExpression("new Bound()");
        newExpression.addArgument(valueExpr);
        newExpression.addArgument(typeExpr);
        newExpression.addArgument(new NullLiteralExpr());
        return newExpression;
    }


}
