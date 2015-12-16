/*
 * Copyright 2011 Red Hat, Inc. and/or its affiliates.
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

package org.drools.workbench.models.testscenarios.backend.populators;

import java.io.Serializable;
import java.util.Map;

import org.drools.core.base.TypeResolver;
import org.drools.core.util.MVELSafeHelper;
import org.mvel2.MVEL;
import org.mvel2.ParserConfiguration;
import org.mvel2.ParserContext;

public class EnumFieldPopulator extends FieldPopulator {

    private final String              fieldValue;
    private final TypeResolver        typeResolver;

    private final ParserConfiguration pconf;
    private final ParserContext       pctx;

    public EnumFieldPopulator(Object factObject,
                              String fieldName,
                              String fieldValue,
                              TypeResolver typeResolver) {
        super( factObject,
               fieldName );
        this.typeResolver = typeResolver;
        this.fieldValue = fieldValue;

        this.pconf = new ParserConfiguration();
        pconf.setClassLoader( typeResolver.getClassLoader() ); // classLoader );
        this.pctx = new ParserContext( pconf );
        pctx.setStrongTyping( true );
    }

    @Override
    public void populate(Map<String, Object> populatedData) {
        Object value;
        String valueOfEnum = "";
        if ( fieldValue.indexOf( "." ) != -1 ) {
            String classNameOfEnum = fieldValue.substring( 0,
                                                           fieldValue.lastIndexOf( "." ) );
            valueOfEnum = fieldValue.substring( fieldValue.lastIndexOf( "." ) + 1 );
            try {
                //This is a Java enum type if the type can be resolved by ClassTypeResolver
                //Revisit: Better way to determine java enum type or Guvnor enum type.
                String fullName = typeResolver.getFullTypeName( classNameOfEnum );
                if ( fullName != null && !"".equals( fullName ) ) {
                    valueOfEnum = fullName + "." + valueOfEnum;
                }

                Serializable compiled = MVEL.compileExpression( valueOfEnum,
                                                                pctx );
                value = MVELSafeHelper.getEvaluator().executeExpression( compiled );

            } catch ( ClassNotFoundException e ) {
                // This is a Guvnor enum type
                String fullName = classNameOfEnum;
                if ( fullName != null && !"".equals( fullName ) ) {
                    valueOfEnum = fullName + "." + valueOfEnum;
                }
                value = valueOfEnum;
            }
        } else {
            value = this.fieldValue;
        }

        populateField( value,
                       populatedData );
    }
}
