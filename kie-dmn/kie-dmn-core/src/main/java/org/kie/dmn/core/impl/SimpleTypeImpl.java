package org.kie.dmn.core.impl;

import java.util.List;

import org.kie.dmn.api.core.DMNType;
import org.kie.dmn.feel.lang.Type;
import org.kie.dmn.feel.runtime.UnaryTest;

/**
 * @see DMNType
 */
public class SimpleTypeImpl
        extends BaseDMNTypeImpl {

    public SimpleTypeImpl() {
        this( null, null, null, false, null, null, null );
    }

    public SimpleTypeImpl(String namespace, String name, String id) {
        this( namespace, name, id, false, null, null, null );
    }

    public SimpleTypeImpl(String namespace, String name, String id, boolean isCollection, List<UnaryTest> allowedValues, DMNType baseType, Type feelType) {
        super(namespace, name, id, isCollection, baseType, feelType);
        setAllowedValues( allowedValues );
    }

    @Override
    public boolean isComposite() {
        return false;
    }

    public BaseDMNTypeImpl clone() {
        return new SimpleTypeImpl( getNamespace(), getName(), getId(), isCollection(), getAllowedValuesFEEL(), getBaseType(), getFeelType() );
    }

    @Override
    protected boolean internalIsInstanceOf(Object o) {
        return getBaseType() != null ? getBaseType().isInstanceOf(o) : getFeelType().isInstanceOf(o);
    }

    @Override
    protected boolean internalIsAssignableValue(Object o) {
        return getBaseType() != null ? getBaseType().isAssignableValue(o) : getFeelType().isAssignableValue (o);
    }
}
