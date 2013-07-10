package org.drools.core.rule;

import org.kie.api.definition.type.Role;

import java.util.HashMap;
import java.util.Map;

public class TypeMetaInfo {
    private TypeDeclaration.Kind kind;
    private TypeDeclaration.Role role;
    private boolean isDeclaredType;

    private TypeMetaInfo() { }

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

    public static TypeMetaInfo fromString(String s) {
        TypeMetaInfo typeMetaInfo = new TypeMetaInfo();
        String[] split = s.split(",");
        String kind = split[0].substring("kind=".length());
        typeMetaInfo.kind = TypeDeclaration.Kind.parseKind(kind);
        String role = split[1].substring("role=".length());
        typeMetaInfo.role = TypeDeclaration.Role.parseRole(role);
        String isDeclaredType = split[2].substring("isDeclaredType=".length());
        typeMetaInfo.isDeclaredType = Boolean.valueOf(isDeclaredType);
        return typeMetaInfo;
    }

    public static String marshallMetaInfos(Map<String, TypeDeclaration> typeDeclarations) {
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, TypeDeclaration> entry : typeDeclarations.entrySet()) {
            sb.append(entry.getKey()).append("={").append(new TypeMetaInfo(entry.getValue()).toString()).append("}\n");
        }
        return sb.toString();
    }

    public static Map<String, TypeMetaInfo> unmarshallMetaInfos(String s) {
        Map<String, TypeMetaInfo> typeMetaInfos = new HashMap<String, TypeMetaInfo>();
        for (String line : s.split("\\n")) {
            line = line.trim();
            if (line.length() == 0) {
                continue;
            }
            int eqPos = line.indexOf('=');
            String className = line.substring(0, eqPos);
            TypeMetaInfo typeMetaInfo = fromString(line.substring(eqPos+2, line.length()-1));
            typeMetaInfos.put(className, typeMetaInfo);
        }
        return typeMetaInfos;
    }
}
