package org.drools.core.reteoo.builder;

import org.drools.core.base.ClassObjectType;
import org.drools.core.base.ValueType;
import org.drools.core.factmodel.traits.TraitProxy;
import org.drools.core.reteoo.*;
import org.drools.core.spi.AlphaNodeFieldConstraint;
import org.drools.core.spi.ObjectType;

public class MethodCountingNodeFactory extends PhreakNodeFactory {

    private static final NodeFactory INSTANCE = new MethodCountingNodeFactory();

    public static NodeFactory getInstance() {
        return INSTANCE;
    }


    public AlphaNode buildAlphaNode(int id, AlphaNodeFieldConstraint constraint, ObjectSource objectSource, BuildContext context ) {
        return new MethodCountingAlphaNode( id, constraint, objectSource, context );
    }

    public ObjectTypeNode buildObjectTypeNode(int id, EntryPointNode objectSource, ObjectType objectType, BuildContext context ) {
        if ( objectType.getValueType().equals( ValueType.TRAIT_TYPE ) ) {
            if ( TraitProxy.class.isAssignableFrom( ( (ClassObjectType) objectType ).getClassType() ) ) {
                return new TraitProxyObjectTypeNode( id, objectSource, objectType, context );
            } else {
                return new TraitObjectTypeNode( id, objectSource, objectType, context );
            }
        } else {
            return new MethodCountingObjectTypeNode( id, objectSource, objectType, context );
        }
    }

    public LeftInputAdapterNode buildLeftInputAdapterNode( int id, ObjectSource objectSource, BuildContext context ) {
        return new MethodCountingLeftInputAdapterNode( id, objectSource, context );
    }
}
