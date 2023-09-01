package org.drools.base.rule;

import org.kie.api.definition.type.Role;

import java.io.Serializable;

public class TypeMetaInfo implements Serializable{
    private TypeDeclaration.Kind kind;
    private Role.Type role;
    private boolean isDeclaredType;

    public TypeMetaInfo() { }

    public TypeMetaInfo(TypeDeclaration typeDeclaration) {
        this.kind = typeDeclaration.getKind();
        this.role = typeDeclaration.getRole();
        this.isDeclaredType = !typeDeclaration.isJavaBased();
    }

    public TypeMetaInfo(Class<?> clazz) {
        this.kind = TypeDeclaration.Kind.CLASS;
        Role role = clazz.getAnnotation(Role.class);
        this.role = role == null ? Role.Type.FACT : role.value();
        this.isDeclaredType = false;
    }

    public boolean isEvent() {
        return role == Role.Type.EVENT;
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
