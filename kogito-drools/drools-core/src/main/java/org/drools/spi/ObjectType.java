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

package org.drools.spi;

import java.io.Externalizable;

import org.drools.base.ValueType;

/**
 * Semantic object type differentiator.
 *
 * @see org.drools.rule.Declaration
 *
 */
public interface ObjectType
    extends
    Externalizable {
//    /**
//     * Determine if the passed <code>Object</code> belongs to the object type
//     * defined by this <code>objectType</code> instance.
//     *
//     * @param object
//     *            The <code>Object</code> to test.
//     *
//     * @return <code>true</code> if the <code>Object</code> matches this
//     *         object type, else <code>false</code>.
//     */
//    boolean matches(Object object);

//    boolean isAssignableFrom(Object object);

    boolean isAssignableFrom(ObjectType objectType);

    /**
     * Returns true if the object type represented by this object
     * is an event object type. False otherwise.
     * @return
     */
    boolean isEvent();

    ValueType getValueType();
}
