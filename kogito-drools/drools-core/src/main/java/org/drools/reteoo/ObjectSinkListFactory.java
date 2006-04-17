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

/**
 * ObjectSinkListFactory
 * A factory for ObjectSinkLists
 *
 * @author <a href="mailto:tirelli@post.com">Edson Tirelli</a>
 *
 * Created: 06/march/2006
 */
public class ObjectSinkListFactory {
    public static final String  TYPE_NODE_ALPHA_HASHING  = "org.drools.alpha-hash.type-node";
    public static final String  ALPHA_NODE_ALPHA_HASHING = "org.drools.alpha-hash.alpha-node";

    private static final String DISABLED                 = "false";
    private static final String ENABLED                  = "true";

    public static final ObjectSinkList newObjectSinkList(Object owner) {
        if ( (!DISABLED.equalsIgnoreCase( System.getProperty( TYPE_NODE_ALPHA_HASHING ) )) && (owner instanceof ObjectTypeNode) ) {
            return new HashedObjectSinkList();
        } else if ( ENABLED.equalsIgnoreCase( System.getProperty( ALPHA_NODE_ALPHA_HASHING ) ) && (owner instanceof AlphaNode) ) {
            return new HashedObjectSinkList();
        }
        return new DefaultObjectSinkList( 1 );
    }

}
