package org.kie.dmn.feel.runtime.impl;

import java.util.function.BiPredicate;

import org.kie.dmn.feel.runtime.Range;
import org.kie.dmn.feel.util.EvalHelper;

public class RangeImpl
        implements Range {

    private RangeBoundary lowBoundary;
    private RangeBoundary highBoundary;
    private Comparable    lowEndPoint;
    private Comparable    highEndPoint;


    public RangeImpl() {
    }

    public RangeImpl(RangeBoundary lowBoundary, Comparable lowEndPoint, Comparable highEndPoint, RangeBoundary highBoundary) {
        this.lowBoundary = lowBoundary;
        this.highBoundary = highBoundary;
        this.lowEndPoint = lowEndPoint;
        this.highEndPoint = highEndPoint;
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
    public Boolean includes(Object param) {
        if (param == null) {
            return null;
        }
        if (lowEndPoint == null) {
            if (highEndPoint == null) {
                return null;
            } else {
                return negInfRangeIncludes(param);
            }
        } else {
            if (highEndPoint == null) {
                return posInfRangeIncludes(param);
            } else {
                return finiteRangeIncludes(param);
            }
        }
    }

    private Boolean finiteRangeIncludes(Object param) {
        if (lowBoundary == RangeBoundary.OPEN && highBoundary == RangeBoundary.OPEN) {
            return bothOrThrow(compare(lowEndPoint, param, (l, r) -> l.compareTo(r) < 0) , compare(highEndPoint, param,  (l, r) -> l.compareTo(r) > 0), param);
        } else if (lowBoundary == RangeBoundary.OPEN && highBoundary == RangeBoundary.CLOSED) {
            return bothOrThrow(compare(lowEndPoint, param, (l, r) -> l.compareTo(r) < 0) , compare(highEndPoint, param,  (l, r) -> l.compareTo(r) >= 0), param);
        } else if (lowBoundary == RangeBoundary.CLOSED && highBoundary == RangeBoundary.OPEN) {
            return bothOrThrow(compare(lowEndPoint, param, (l, r) -> l.compareTo(r) <= 0) , compare(highEndPoint, param,  (l, r) -> l.compareTo(r) > 0), param);
        } else if (lowBoundary == RangeBoundary.CLOSED && highBoundary == RangeBoundary.CLOSED) {
            return bothOrThrow(compare(lowEndPoint, param, (l, r) -> l.compareTo(r) <= 0) , compare(highEndPoint, param,  (l, r) -> l.compareTo(r) >= 0), param);
        }
        throw new RuntimeException("unknown boundary combination");
    }

    private Boolean posInfRangeIncludes(Object param) {
        if (lowBoundary == RangeBoundary.OPEN) {
            return compare(lowEndPoint, param, (l, r) -> l.compareTo(r) < 0);
        } else {
            return compare(lowEndPoint, param, (l, r) -> l.compareTo(r) <= 0);
        }
    }

    private Boolean negInfRangeIncludes(Object param) {
        if (highBoundary == RangeBoundary.OPEN) {
            return compare(highEndPoint, param,  (l, r) -> l.compareTo(r) > 0);
        } else {
            return compare(highEndPoint, param,  (l, r) -> l.compareTo(r) >= 0);
        }
    }
    
    private Boolean bothOrThrow(Boolean left, Boolean right, Object param) {
        if (left == null || right == null) {
            throw new IllegalArgumentException("Range.include("+classOf(param)+") not comparable with "+classOf(lowEndPoint)+", "+classOf(highEndPoint));
        }
        return left && right;
    }
    
    private static String classOf(Object p) {
        return p != null ? p.getClass().toString() : "null";
    }
    
    private static Boolean compare(Comparable left, Object right, BiPredicate<Comparable, Comparable> op) {
        if (left.getClass().isAssignableFrom(right.getClass())) { // short path
                return op.test(left, (Comparable) right);
        }
        return EvalHelper.compare(left, right, null, op); // defer to full DMN/FEEL semantic
    }

    @Override
    public boolean equals(Object o) {
        if ( this == o ) return true;
        if ( !(o instanceof RangeImpl) ) return false;

        RangeImpl range = (RangeImpl) o;

        if ( lowBoundary != range.lowBoundary ) return false;
        if ( highBoundary != range.highBoundary ) return false;
        if ( lowEndPoint != null ? !lowEndPoint.equals( range.lowEndPoint ) : range.lowEndPoint != null ) return false;
        return highEndPoint != null ? highEndPoint.equals( range.highEndPoint ) : range.highEndPoint == null;

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
        return (lowBoundary == RangeBoundary.OPEN ? "(" : "[") +
               " " + lowEndPoint +
               " .. " + highEndPoint +
               " " + ( highBoundary == RangeBoundary.OPEN ? ")" : "]" );
    }
}
