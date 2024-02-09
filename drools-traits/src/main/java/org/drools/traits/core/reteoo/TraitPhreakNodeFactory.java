/**
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
