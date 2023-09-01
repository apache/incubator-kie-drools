package org.drools.verifier.visitor;

import org.drools.drl.ast.descr.AnnotationDescr;
import org.drools.drl.ast.descr.TypeDeclarationDescr;
import org.drools.verifier.components.Field;
import org.drools.verifier.components.Import;
import org.drools.verifier.components.ObjectType;
import org.drools.verifier.data.VerifierData;

import java.util.List;
import java.util.Map;

public class TypeDeclarationDescrVisitor {

    private final VerifierData data;

    public TypeDeclarationDescrVisitor(VerifierData data) {
        this.data = data;
    }

    public void visit(List<TypeDeclarationDescr> typeDeclarationDescrs) {
        for (TypeDeclarationDescr typeDeclaration : typeDeclarationDescrs) {
            Import objectImport = data.getImportByName(typeDeclaration.getTypeName());
            String objectTypeName;
            if (objectImport == null) {
                objectTypeName = typeDeclaration.getTypeName();
            } else {
                objectTypeName = objectImport.getName();
            }

            ObjectType objectType = this.data.getObjectTypeByFullName(objectTypeName);

            if (objectType == null) {
                objectType = new ObjectType(typeDeclaration);
                objectType.setName(typeDeclaration.getTypeName());
                objectType.setFullName(typeDeclaration.getTypeName());
                data.add(objectType);
            }

            for (String fieldName : typeDeclaration.getFields().keySet()) {

                Field field = data.getFieldByObjectTypeAndFieldName(objectType.getFullName(),
                        fieldName);
                if (field == null) {
                    field = ObjectTypeFactory.createField(typeDeclaration.getFields().get(fieldName),fieldName,
                            objectType);
                    field.setFieldType(typeDeclaration.getFields().get(fieldName).getPattern().getObjectType());
                    data.add(field);
                }
            }

            for (AnnotationDescr annDescr : typeDeclaration.getAnnotations()) {
                Map<String, Object> values = typeDeclaration.getAnnotation(annDescr.getName()).getValueMap();
                for (String value : values.keySet()) {
                    objectType.getMetadata().put(annDescr.getName(), value);
                }
            }
        }
    }


}
