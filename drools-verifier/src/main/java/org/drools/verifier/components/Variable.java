package org.drools.verifier.components;

/**
 * 
 * @author Toni Rikkola
 */
public class Variable extends RuleComponent {

    private String objectTypePath;
    private String objectTypeType;
    private String objectTypeName;

    public Variable(VerifierRule rule) {
        super( rule );
    }

    public String getObjectTypeName() {
        return objectTypeName;
    }

    public void setObjectTypeName(String objectTypeName) {
        this.objectTypeName = objectTypeName;
    }

    private String name;

    public String getObjectTypePath() {
        return objectTypePath;
    }

    public void setObjectTypePath(String path) {
        this.objectTypePath = path;
    }

    public void setObjectTypeType(String type) {
        // VerifierComponentType.OBJECT_TYPE dominates VerifierComponentType.FIELD.
        if ( this.objectTypeType == null || !VerifierComponentType.OBJECT_TYPE.getType().equals( this.objectTypeType ) ) {
            this.objectTypeType = type;
        }
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getObjectTypeType() {
        return objectTypeType;
    }

    @Override
    public String toString() {
        return "Variable name: " + name;
    }

    public VerifierComponentType getVerifierComponentType() {
        return VerifierComponentType.VARIABLE;
    }
}
