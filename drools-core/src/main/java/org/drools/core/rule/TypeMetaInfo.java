package org.drools.core.rule;

import org.kie.api.definition.type.Role;

public class TypeMetaInfo {
    private TypeDeclaration.Kind kind;
    private TypeDeclaration.Role role;
    private boolean isDeclaredType;

    public TypeMetaInfo(TypeDeclaration typeDeclaration) {
        this.kind = typeDeclaration.getKind();
        this.role = typeDeclaration.getRole();
        this.isDeclaredType = !typeDeclaration.isJavaBased();
    }

    public TypeMetaInfo(Class<?> clazz) {
        this.kind = TypeDeclaration.Kind.CLASS;
        Role role = clazz.getAnnotation(Role.class);
        this.role = role == null || role.value() == Role.Type.FACT ? TypeDeclaration.Role.FACT : TypeDeclaration.Role.EVENT;
        this.isDeclaredType = false;
    }

    public boolean isEvent() {
        return role == TypeDeclaration.Role.EVENT;
    }

    public boolean isDeclaredType() {
        return isDeclaredType;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("kind=").append(kind.toString().toLowerCase());
        sb.append(",");
        sb.append("role=").append(role.toString().toLowerCase());
        sb.append(",");
        sb.append("isDeclaredType=").append(isDeclaredType);
        return sb.toString();
    }
}
