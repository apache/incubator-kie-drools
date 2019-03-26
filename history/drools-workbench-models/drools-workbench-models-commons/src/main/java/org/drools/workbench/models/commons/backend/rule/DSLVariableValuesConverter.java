/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.drools.workbench.models.commons.backend.rule;

import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.converters.collections.CollectionConverter;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.mapper.Mapper;
import org.drools.workbench.models.datamodel.rule.DSLVariableValue;

import java.util.Collection;

/**
 * This XStream converter converts legacy String DSL values into
 * DSLVariableValue objects. XStream blindly unmarshalls members of a Collection
 * according to their persisted XML type. For legacy DSLSentences this is a
 * String whereas for newer (and correctly) it is DSLVariableValue.
 */
public class DSLVariableValuesConverter extends CollectionConverter {

    public DSLVariableValuesConverter( Mapper mapper ) {
        super( mapper );
    }

    @Override
    @SuppressWarnings({ "rawtypes", "unchecked" })
    protected void addCurrentElementToCollection( HierarchicalStreamReader reader,
                                                  UnmarshallingContext context,
                                                  Collection collection,
                                                  Collection target ) {
        Object item = readItem( reader,
                                context,
                                collection );
        if ( item instanceof DSLVariableValue) {
            target.add( item );
        } else if ( item instanceof String ) {
            //The only other possible legacy type is a String, so using toString() should be OK
            DSLVariableValue value = new DSLVariableValue( item.toString() );
            target.add( value );
        }
    }

}
