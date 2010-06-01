/**
 * 
 */
package org.drools.reteoo.test.dsl;

import java.util.Arrays;
import java.util.List;

import org.drools.common.InternalFactHandle;
import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.hamcrest.Factory;
import org.hamcrest.Matcher;

public class IsTuple extends BaseMatcher<List<InternalFactHandle>> {
    private final InternalFactHandle[] expected;

    public IsTuple(List<InternalFactHandle> tupleAsList) {
        expected = tupleAsList.toArray( new InternalFactHandle[tupleAsList.size()] );
    }

    public IsTuple(InternalFactHandle[] tuple) {
        expected = tuple;
    }

    public boolean matches(Object arg) {
        if( arg == null || ! ( arg.getClass().isArray() && InternalFactHandle.class.isAssignableFrom( arg.getClass().getComponentType() ) ) ) {
            return false;
        }
        InternalFactHandle[] actual = (InternalFactHandle[]) arg;
        return Arrays.equals( expected, actual );
    }

    public void describeTo(Description description) {
        description.appendValue(expected);
    }
    
    /**
     * Is the value equal to another value, as tested by the
     * {@link java.lang.Object#equals} invokedMethod?
     */
    @Factory
    public static Matcher<List<InternalFactHandle>> isTuple(List<InternalFactHandle> operand) {
        return new IsTuple(operand);
    }

    public static Matcher<? super List<InternalFactHandle>> isTuple(InternalFactHandle... operands) {
        return new IsTuple(operands);
    }

}