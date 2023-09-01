package org.drools.traits.core.reteoo;

import org.drools.base.base.ClassObjectType;
import org.drools.base.base.ValueType;
import org.drools.traits.core.factmodel.TraitProxy;
import org.drools.core.reteoo.AlphaNode;
import org.drools.core.reteoo.EntryPointNode;
import org.drools.core.reteoo.ObjectSource;
import org.drools.core.reteoo.ObjectTypeNode;
import org.drools.core.reteoo.builder.BuildContext;
import org.drools.core.reteoo.builder.NodeFactory;
import org.drools.core.reteoo.builder.PhreakNodeFactory;
import org.drools.base.rule.constraint.AlphaNodeFieldConstraint;
import org.drools.base.base.ObjectType;

public class TraitPhreakNodeFactory extends PhreakNodeFactory {

    public TraitPhreakNodeFactory() {
    }

    private static final NodeFactory INSTANCE = new TraitPhreakNodeFactory();

    public static NodeFactory getInstance() {
        return INSTANCE;
    }

    @Override
    public AlphaNode buildAlphaNode(int id, AlphaNodeFieldConstraint constraint, ObjectSource objectSource, BuildContext context ) {
        return new TraitAlphaNode(id, constraint, objectSource, context );
    }

    @Override
    public ObjectTypeNode buildObjectTypeNode(int id, EntryPointNode objectSource, ObjectType objectType, BuildContext context) {
        if ( objectType.getValueType().equals( ValueType.TRAIT_TYPE ) ) {
            if ( TraitProxy.class.isAssignableFrom( ( (ClassObjectType) objectType ).getClassType() ) ) {
                return new TraitProxyObjectTypeNode( id, objectSource, objectType, context );
            } else {
                return new TraitObjectTypeNode(id, objectSource, objectType, context );
            }
        } else {
            return new ObjectTypeNode( id, objectSource, objectType, context );
        }
    }
}
