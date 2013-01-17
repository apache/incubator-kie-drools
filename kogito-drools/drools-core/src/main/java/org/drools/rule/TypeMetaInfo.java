package org.drools.rule;

import java.util.HashMap;
import java.util.Map;

public class TypeMetaInfo {
    private TypeDeclaration.Kind kind;
    private TypeDeclaration.Role role;

    public static TypeMetaInfo DEFAULT_TYPE_META_INFO = new TypeMetaInfo();

    private TypeMetaInfo() { }

    public TypeMetaInfo(TypeDeclaration typeDeclaration) {
        this.kind = typeDeclaration.getKind();
        this.role = typeDeclaration.getRole();
    }

    public boolean isEvent() {
        return role == TypeDeclaration.Role.EVENT;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("kind=").append(kind.toString().toLowerCase());
        sb.append(",");
        sb.append("role=").append(role.toString().toLowerCase());
        return sb.toString();
    }

    public static TypeMetaInfo fromString(String s) {
        TypeMetaInfo typeMetaInfo = new TypeMetaInfo();
        String[] split = s.split(",");
        String kind = split[0].substring("kind=".length());
        typeMetaInfo.kind = TypeDeclaration.Kind.parseKind(kind);
        String role = split[1].substring("role=".length());
        typeMetaInfo.role = TypeDeclaration.Role.parseRole(role);
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
