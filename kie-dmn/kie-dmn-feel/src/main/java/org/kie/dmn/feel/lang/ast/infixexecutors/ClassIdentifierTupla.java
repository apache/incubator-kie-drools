package org.kie.dmn.feel.lang.ast.infixexecutors;

import java.util.Objects;

public class ClassIdentifierTupla {

    private final Class LEFT_TYPE;
    private final Class RIGHT_TYPE;


    public ClassIdentifierTupla(Object leftObject, Object rightObject) {
        this.LEFT_TYPE = leftObject != null ? leftObject.getClass() : null;
        this.RIGHT_TYPE = rightObject != null ? rightObject.getClass() : null;
    }

    public ClassIdentifierTupla(Class leftType, Class rightType) {
        this.LEFT_TYPE = leftType;
        this.RIGHT_TYPE = rightType;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ClassIdentifierTupla that = (ClassIdentifierTupla) o;
        return isEquals(LEFT_TYPE, that.LEFT_TYPE) && isEquals(RIGHT_TYPE, that.RIGHT_TYPE);
    }

    @Override
    public int hashCode() {
        return 1;
    }

    static boolean isEquals(Class thisClass, Class thatClass) {
        return (thisClass != null && thatClass != null) && Objects.equals(thisClass, thatClass) || thatClass.isAssignableFrom(thisClass);
    }
}
