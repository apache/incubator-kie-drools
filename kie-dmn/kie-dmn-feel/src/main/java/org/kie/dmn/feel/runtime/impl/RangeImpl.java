/*
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
package org.kie.dmn.feel.runtime.impl;

import java.math.BigDecimal;
import java.time.LocalDate;

import org.kie.dmn.feel.lang.EvaluationContext;
import org.kie.dmn.feel.lang.ast.dialectHandlers.DialectHandler;
import org.kie.dmn.feel.lang.ast.dialectHandlers.DialectHandlerFactory;
import org.kie.dmn.feel.runtime.Range;

import static org.kie.dmn.feel.lang.ast.UnaryTestNode.UnaryOperator.GT;
import static org.kie.dmn.feel.lang.ast.UnaryTestNode.UnaryOperator.GTE;
import static org.kie.dmn.feel.lang.ast.UnaryTestNode.UnaryOperator.LT;
import static org.kie.dmn.feel.lang.ast.UnaryTestNode.UnaryOperator.LTE;

public class RangeImpl
        implements Range {

    private RangeBoundary lowBoundary;
    private RangeBoundary highBoundary;
    private Comparable lowEndPoint;
    private Comparable highEndPoint;
    private boolean withUndefined = false;

    public RangeImpl() {
    }

    public RangeImpl(RangeBoundary lowBoundary, Comparable lowEndPoint, Comparable highEndPoint, RangeBoundary highBoundary) {
        this.lowBoundary = lowBoundary;
        this.highBoundary = highBoundary;
        this.lowEndPoint = lowEndPoint;
        this.highEndPoint = highEndPoint;
        withUndefined = lowEndPoint instanceof UndefinedValueComparable || highEndPoint instanceof UndefinedValueComparable;
    }

    @Override
    public RangeBoundary getLowBoundary() {
        return lowBoundary;
    }

    @Override
    public Comparable getLowEndPoint() {
        return lowEndPoint;
    }

    @Override
    public Comparable getHighEndPoint() {
        return highEndPoint;
    }

    @Override
    public RangeBoundary getHighBoundary() {
        return highBoundary;
    }

    @Override
    public Boolean includes(EvaluationContext ctx, Object param) {
        if (param == null) {
            return null;
        }
        if (lowEndPoint == null || lowEndPoint instanceof UndefinedValueComparable) {
            if (highEndPoint == null || highEndPoint instanceof UndefinedValueComparable) {
                return null;
            } else if (lowEndPoint != null) { // it means it is UndefinedValueComparable
                return negInfRangeIncludes(ctx, param);
            } else {
                return false;
            }
        } else {
            if (highEndPoint instanceof UndefinedValueComparable) {
                return posInfRangeIncludes(ctx, param);
            } else if (highEndPoint != null) {
                return finiteRangeIncludes(ctx, param);
            } else {
                return false;
            }
        }
    }

    @Override
    public boolean isWithUndefined() {
        return withUndefined;
    }

    @Override
    public Comparable getStart() {
        if (lowEndPoint instanceof BigDecimal) {
            BigDecimal start = (BigDecimal) lowEndPoint;
            start = lowBoundary == Range.RangeBoundary.OPEN ? start.add(BigDecimal.ONE) : start;
            return start;
        } else if (lowEndPoint instanceof LocalDate) {
            LocalDate start = (LocalDate) lowEndPoint;
            start = lowBoundary == Range.RangeBoundary.OPEN ? start.plusDays(1) : start;
            return start;
        }
        return lowEndPoint;
    }

    @Override
    public Comparable getEnd() {
        if (highEndPoint instanceof BigDecimal) {
            BigDecimal end = (BigDecimal) highEndPoint;
            end = highBoundary == Range.RangeBoundary.OPEN ? end.subtract(BigDecimal.ONE) : end;
            return end;
        } else if (highEndPoint instanceof LocalDate) {
            LocalDate end = (LocalDate) highEndPoint;
            end = highBoundary == Range.RangeBoundary.OPEN ? end.minusDays(1) : end;
            return end;
        }
        return highEndPoint;
    }

    private Boolean finiteRangeIncludes(EvaluationContext ctx, Object param) {
        DialectHandler handler = DialectHandlerFactory.getHandler(ctx);
        if (lowBoundary == RangeBoundary.OPEN && highBoundary == RangeBoundary.OPEN) {
            //return bothOrThrow(compare(feelDialect, lowEndPoint, param, (l, r) -> l.compareTo(r) < 0), compare(feelDialect, highEndPoint, param, (l, r) -> l.compareTo(r) > 0), param);
            return bothOrThrow((Boolean) handler.executeLt(lowEndPoint, param, ctx), (Boolean) handler.executeGt(highEndPoint, param, ctx), param);
        } else if (lowBoundary == RangeBoundary.OPEN && highBoundary == RangeBoundary.CLOSED) {
            //return bothOrThrow(compare(feelDialect, lowEndPoint, param, (l, r) -> l.compareTo(r) < 0), compare(feelDialect, highEndPoint, param, (l, r) -> l.compareTo(r) >= 0), param);
            return bothOrThrow((Boolean) handler.executeLt(lowEndPoint, param, ctx), (Boolean) handler.executeGte(highEndPoint, param, ctx), param);
        } else if (lowBoundary == RangeBoundary.CLOSED && highBoundary == RangeBoundary.OPEN) {
            //return bothOrThrow(compare(feelDialect, lowEndPoint, param, (l, r) -> l.compareTo(r) <= 0), compare(feelDialect, highEndPoint, param, (l, r) -> l.compareTo(r) > 0), param);
            return bothOrThrow((Boolean) handler.executeLte(lowEndPoint, param, ctx), (Boolean) handler.executeGt(highEndPoint, param, ctx), param);
        } else if (lowBoundary == RangeBoundary.CLOSED && highBoundary == RangeBoundary.CLOSED) {
            //return bothOrThrow(compare(feelDialect, lowEndPoint, param, (l, r) -> l.compareTo(r) <= 0), compare(feelDialect, highEndPoint, param, (l, r) -> l.compareTo(r) >= 0), param);
            return bothOrThrow((Boolean) handler.executeLte(lowEndPoint, param, ctx), (Boolean) handler.executeGte(highEndPoint, param, ctx), param);
        }
        throw new RuntimeException("unknown boundary combination");
    }

    private Boolean posInfRangeIncludes(EvaluationContext ctx, Object param) {
        DialectHandler handler = DialectHandlerFactory.getHandler(ctx);
        if (lowBoundary == RangeBoundary.OPEN) {
            //return compare(feelDialect, lowEndPoint, param, (l, r) -> l.compareTo(r) < 0);
            return (Boolean) handler.executeLt(lowEndPoint, param, ctx);
        } else {
            //return compare(feelDialect, lowEndPoint, param, (l, r) -> l.compareTo(r) <= 0);
            return (Boolean) handler.executeLte(lowEndPoint, param, ctx);
        }
    }

    private Boolean negInfRangeIncludes(EvaluationContext ctx, Object param) {
        DialectHandler handler = DialectHandlerFactory.getHandler(ctx);
        if (highBoundary == RangeBoundary.OPEN) {
            //return compare(ctx, highEndPoint, param, (l, r) -> l.compareTo(r) > 0);
            return (Boolean) handler.executeGt(highEndPoint, param, ctx);
        } else {
            //return compare(ctx, highEndPoint, param, (l, r) -> l.compareTo(r) >= 0);
            return (Boolean) handler.executeGte(highEndPoint, param, ctx);
        }
    }

    private Boolean bothOrThrow(Boolean left, Boolean right, Object param) {
        if (left == null || right == null) {
            throw new IllegalArgumentException("Range.include(" + classOf(param) + ") not comparable with " + classOf(lowEndPoint) + ", " + classOf(highEndPoint));
        }
        return left && right;
    }

    private static String classOf(Object p) {
        return p != null ? p.getClass().toString() : "null";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (!(o instanceof RangeImpl))
            return false;

        RangeImpl range = (RangeImpl) o;

        if (lowBoundary != range.lowBoundary)
            return false;
        if (highBoundary != range.highBoundary)
            return false;
        if (lowEndPoint != null ? !lowEndPoint.equals(range.lowEndPoint) : range.lowEndPoint != null)
            return false;
        return highEndPoint != null ? highEndPoint.equals(range.highEndPoint) : range.highEndPoint == null;

    }

    @Override
    public int hashCode() {
        int result = lowBoundary != null ? lowBoundary.hashCode() : 0;
        result = 31 * result + (highBoundary != null ? highBoundary.hashCode() : 0);
        result = 31 * result + (lowEndPoint != null ? lowEndPoint.hashCode() : 0);
        result = 31 * result + (highEndPoint != null ? highEndPoint.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return withUndefined ? withUndefinedtoString() : withoutUndefinedtoString();
    }

    private String withoutUndefinedtoString() {
        return (lowBoundary == RangeBoundary.OPEN ? "(" : "[") +
                " " + lowEndPoint +
                " .. " + highEndPoint +
                " " + (highBoundary == RangeBoundary.OPEN ? ")" : "]");
    }

    private String withUndefinedtoString() {
        StringBuilder sb = new StringBuilder("( ");
        if (lowEndPoint instanceof UndefinedValueComparable) {
            if (highBoundary == RangeBoundary.OPEN) {
                sb.append(LT.symbol);
            } else {
                sb.append(LTE.symbol);
            }
            sb.append(" ");
            sb.append(highEndPoint);
        } else if (highEndPoint instanceof UndefinedValueComparable) {
            if (lowBoundary == RangeBoundary.OPEN) {
                sb.append(GT.symbol);
            } else {
                sb.append(GTE.symbol);
            }
            sb.append(" ");
            sb.append(lowEndPoint);
        }
        sb.append(" )");
        return sb.toString();
    }

}
