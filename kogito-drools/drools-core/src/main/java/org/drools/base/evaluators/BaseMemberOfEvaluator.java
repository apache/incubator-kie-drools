/**
 * 
 */
package org.drools.base.evaluators;

import java.util.Collection;

import org.drools.base.BaseEvaluator;
import org.drools.base.ValueType;
import org.drools.common.InternalWorkingMemory;
import org.drools.rule.VariableRestriction.ObjectVariableContextEntry;
import org.drools.rule.VariableRestriction.VariableContextEntry;
import org.drools.spi.Extractor;
import org.drools.spi.FieldValue;
import org.drools.util.ShadowProxyUtils;

/**
 * This is a base class for MemberOf Evaluators
 * 
 * @author etirelli
 */
public abstract class BaseMemberOfEvaluator extends BaseEvaluator {

    public BaseMemberOfEvaluator(ValueType type,
                                          Operator operator) {
        super( type,
               operator );
    }

    public boolean evaluate(InternalWorkingMemory workingMemory,
                            final Extractor extractor,
                            final Object object1, final FieldValue object2) {
        if( object2.isNull() ) {
            return false;
        } else if( ! object2.isCollectionField() ) {
            throw new ClassCastException("Can't check if an attribute is member of an object of class "+object2.getValue().getClass() );
        }
        final Collection col = (Collection) object2.getValue();
        final Object value = extractor.getValue( workingMemory, object1 ); 
        return ShadowProxyUtils.contains( col, value );
    }

    public boolean evaluateCachedRight(InternalWorkingMemory workingMemory,
                                       final VariableContextEntry context, final Object left) {
        final Object object = context.declaration.getExtractor().getValue( workingMemory, left );
        if( object == null ) {
            return false;  
        } else if( ! ( object instanceof Collection ) ) {
            throw new ClassCastException("Can't check if an attribute is member of an object of class "+object.getClass() );
        }
        final Collection col = (Collection) object;
        final Object value = ((ObjectVariableContextEntry) context).right;
        return ShadowProxyUtils.contains( col, value );
    }

    public boolean evaluateCachedLeft(InternalWorkingMemory workingMemory,
                                      final VariableContextEntry context, final Object right) {
        final Object object = ((ObjectVariableContextEntry) context).left;
        if( object == null ) {
            return false;  
        } else if( ! ( object instanceof Collection ) ) {
            throw new ClassCastException("Can't check if an attribute is member of an object of class "+object.getClass() );
        }
        final Collection col = (Collection) object;
        final Object value = context.extractor.getValue( workingMemory, right ); 
        return ShadowProxyUtils.contains( col, value );
    }

    public boolean evaluate(InternalWorkingMemory workingMemory,
                            final Extractor extractor1,
                            final Object object1,
                            final Extractor extractor2, final Object object2) {
        final Object object = extractor2.getValue( workingMemory, object2 );
        if( object == null ) {
            return false;  
        } else if( ! ( object instanceof Collection ) ) {
            throw new ClassCastException("Can't check if an attribute is member of an object of class "+object.getClass() );
        }
        final Collection col = (Collection) object;
        final Object value = extractor1.getValue( workingMemory, object1 );
        return ShadowProxyUtils.contains( col, value );
    }
    
    public abstract String toString();
    
}
