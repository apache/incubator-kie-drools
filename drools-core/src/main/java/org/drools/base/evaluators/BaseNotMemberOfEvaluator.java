/**
 * 
 */
package org.drools.base.evaluators;

import java.util.Collection;

import org.drools.base.BaseEvaluator;
import org.drools.base.ValueType;
import org.drools.rule.VariableRestriction.ObjectVariableContextEntry;
import org.drools.rule.VariableRestriction.VariableContextEntry;
import org.drools.spi.Extractor;
import org.drools.spi.FieldValue;

/**
 * This is a base class for Not MemberOf Evaluators
 * 
 * @author etirelli
 *
 */
public abstract class BaseNotMemberOfEvaluator extends BaseEvaluator {

    public BaseNotMemberOfEvaluator(ValueType type,
                                             Operator operator) {
        super( type,
               operator );
    }

    public boolean evaluate(final Extractor extractor,
                            final Object object1,
                            final FieldValue object2) {
        if( object2.isNull() ) {
            return false;
        } else if( ! object2.isCollectionField() ) {
            throw new ClassCastException("Can't check if an attribute is not member of an object of class "+object2.getValue().getClass() );
        }
        final Collection col = (Collection) object2.getValue();
        final Object value = extractor.getValue( object1 ); 
        return ! col.contains( value );
    }

    public boolean evaluateCachedRight(final VariableContextEntry context,
                                       final Object left) {
        final Object object = context.declaration.getExtractor().getValue( left );
        if( object == null ) {
            return false;  
        } else if( ! ( object instanceof Collection ) ) {
            throw new ClassCastException("Can't check if an attribute is not member of an object of class "+object.getClass() );
        }
        final Collection col = (Collection) object;
        final Object value = ((ObjectVariableContextEntry) context).right;
        return ! col.contains( value );
    }

    public boolean evaluateCachedLeft(final VariableContextEntry context,
                                      final Object right) {
        final Object object = ((ObjectVariableContextEntry) context).left;
        if( object == null ) {
            return false;  
        } else if( ! ( object instanceof Collection ) ) {
            throw new ClassCastException("Can't check if an attribute is not member of an object of class "+object.getClass() );
        }
        final Collection col = (Collection) object;
        final Object value = context.extractor.getValue( right ); 
        return ! col.contains( value );
    }

    public boolean evaluate(final Extractor extractor1,
                            final Object object1,
                            final Extractor extractor2,
                            final Object object2) {
        final Object object = extractor2.getValue( object2 );
        if( object == null ) {
            return false;  
        } else if( ! ( object instanceof Collection ) ) {
            throw new ClassCastException("Can't check if an attribute is not member of an object of class "+object.getClass() );
        }
        final Collection col = (Collection) object;
        final Object value = extractor1.getValue( object1 );
        return ! col.contains( value );
    }

    public abstract String toString();


}
