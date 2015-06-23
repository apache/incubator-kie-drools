/*
 * Copyright 2015 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/

package org.drools.compiler.lang.api;

import org.drools.compiler.lang.descr.AttributeDescr;

/**
 * An interface for all builders of statements that support attributes
 */
public interface AttributeSupportBuilder<P extends DescrBuilder< ? , ? >> {

    /**
     * Adds a new attribute to the statement
     * 
     * @param name the attribute name
     * @return the AttributeDescrBuilder to set the attribute value
     */
    public AttributeDescrBuilder<P> attribute( String name );

    /**
     * Adds a new attribute with the given name and value
     * 
     * @param name the name of the attribute to be added
     * @param value the value of the attribute to be added
     * @return the container builder
     */
    public P attribute( String name,
                        String value );

    /**
     * Adds a new attribute with the given name and value
     * 
     * @param name the name of the attribute to be added
     * @param value the value of the attribute to be added
     * @param type the type of the value of the attribute. See {@link AttributeDescr.Type}
     * @return the container builder
     */
    public P attribute( String name,
                        String value,
                        AttributeDescr.Type type );

}
