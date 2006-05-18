/*
 * Copyright 2005 JBoss Inc
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.reteoo;

import org.drools.RuleBaseConfiguration;

/**
 * ObjectSinkListFactory
 * A factory for ObjectSinkLists
 *
 * @author <a href="mailto:tirelli@post.com">Edson Tirelli</a>
 *
 * Created: 06/march/2006
 */
public class ObjectSinkListFactory {
    private final RuleBaseConfiguration config;
    
    public ObjectSinkListFactory(RuleBaseConfiguration config) {
        this.config = config;
    }
    
    public final ObjectSinkList newObjectSinkList(Class owner) {
        if ( config.getBooleanProperty( RuleBaseConfiguration.HASH_OBJECT_TYPE_NODES) && 
             (ObjectTypeNode.class.isAssignableFrom( owner )) ) {
            return new HashedObjectSinkList();
        } else if ( config.getBooleanProperty( RuleBaseConfiguration.HASH_ALPHA_NODES ) && 
                   (AlphaNode.class.isAssignableFrom( owner )) ) {
            return new HashedObjectSinkList();
        }
        return new DefaultObjectSinkList( 1 );
    }
    
    public static final ObjectSinkList newDefaultObjectSinkList() {
        return new DefaultObjectSinkList( 1 );
    }

}
