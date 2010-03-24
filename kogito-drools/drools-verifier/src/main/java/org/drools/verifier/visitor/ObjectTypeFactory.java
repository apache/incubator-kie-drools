package org.drools.verifier.visitor;

import org.drools.verifier.components.Field;
import org.drools.verifier.components.Import;
import org.drools.verifier.components.ObjectType;

class ObjectTypeFactory {

    static Field createField(String fieldName,
                             ObjectType objectType) {
        Field field = new Field();

        field.setObjectTypePath( objectType.getPath() );
        field.setObjectTypeName( objectType.getFullName() );
        field.setName( fieldName );

        objectType.getFields().add( field );

        return field;
    }

    static ObjectType createObjectType(Import objectImport) {
        ObjectType objectType = new ObjectType();

        objectType.setName( objectImport.getShortName() );
        objectType.setFullName( objectImport.getName() );

        return objectType;
    }

    static ObjectType createObjectType(String shortName) {
        ObjectType objectType = new ObjectType();

        objectType.setName( shortName );
        objectType.setFullName( shortName );

        return objectType;
    }
}
