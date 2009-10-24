package org.drools.verifier.components;

import org.drools.verifier.data.VerifierComponent;

/**
 * 
 * @author Toni Rikkola
 */
public class FieldObjectTypeLink extends VerifierComponent {

    private int fieldId;
    private int objectTypeId;

    public int getObjectTypeId() {
        return objectTypeId;
    }

    public void setClassId(int classId) {
        this.objectTypeId = classId;
    }

    public int getFieldId() {
        return fieldId;
    }

    public void setFieldId(int fieldId) {
        this.fieldId = fieldId;
    }

    public VerifierComponentType getVerifierComponentType() {
        return VerifierComponentType.FIELD_OBJECT_TYPE_LINK;
    }
}
