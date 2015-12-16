/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
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

package org.drools.compiler.lang.api.impl;

import org.drools.compiler.lang.api.AttributeDescrBuilder;
import org.drools.compiler.lang.api.DescrBuilder;
import org.drools.compiler.lang.descr.AttributeDescr;

public class AttributeDescrBuilderImpl<P extends DescrBuilder< ? , ? >> extends BaseDescrBuilderImpl<P, AttributeDescr>
    implements
        AttributeDescrBuilder<P> {

    public AttributeDescrBuilderImpl(P parent,
                                     String name) {
        super( parent,
               new AttributeDescr( name ) );
    }

    public AttributeDescrBuilderImpl<P> value( String value ) {
        descr.setValue( value );
        return this;
    }

    public AttributeDescrBuilder<P> type( AttributeDescr.Type type ) {
        descr.setType( type );
        return this;
    }

}
