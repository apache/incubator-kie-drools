/*
 * Copyright 2010 Red Hat, Inc. and/or its affiliates.
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

package org.drools.verifier.visitor;

import org.drools.compiler.lang.descr.BaseDescr;
import org.drools.verifier.components.Field;
import org.drools.verifier.components.Import;
import org.drools.verifier.components.ObjectType;

class ObjectTypeFactory {

    static Field createField(BaseDescr descr, String fieldName,
                             ObjectType objectType) {
        Field field = new Field(descr);

        field.setObjectTypePath( objectType.getPath() );
        field.setObjectTypeName( objectType.getFullName() );
        field.setName( fieldName );

        objectType.getFields().add( field );

        return field;
    }

    static ObjectType createObjectType(BaseDescr descr, Import objectImport) {
        ObjectType objectType = new ObjectType(descr);

        objectType.setName( objectImport.getShortName() );
        objectType.setFullName( objectImport.getName() );

        return objectType;
    }

    static ObjectType createObjectType(BaseDescr descr, String shortName) {
        ObjectType objectType = new ObjectType(descr);

        objectType.setName( shortName );
        objectType.setFullName( shortName );

        return objectType;
    }
}
