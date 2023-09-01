package org.drools.base.base.extractors;

import java.io.Externalizable;

import org.drools.base.base.ValueResolver;
import org.drools.base.base.ValueType;

/**
 * A special field extractor for the self reference "this".
 */
public class SelfReferenceClassFieldReader extends BaseObjectClassFieldReader implements Externalizable  {

    private static final long serialVersionUID = 510l;
    
    public SelfReferenceClassFieldReader() {
        
    }

    public SelfReferenceClassFieldReader(final Class<?> clazz) {
        super( 0, // index
               clazz, // fieldType
               ValueType.determineValueType( clazz ) ); // value type
    }

    public Object getValue(ValueResolver valueResolver, final Object object) {
        return object;
    }
    
    public boolean isNullValue(ValueResolver valueResolver, final Object object) {
        return getValue( valueResolver, object ) == null;
    }
    
    @Override
    public boolean isSelfReference() {
        return true;
    }
}
