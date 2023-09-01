package org.drools.verifier.visitor;

import org.drools.drl.ast.descr.BaseDescr;
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
